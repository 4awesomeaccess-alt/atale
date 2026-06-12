package com.example.newcardmaker;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Hybrid encryption for design JSON files.
 *
 * Local files  → Android Keystore device key (most secure, NOT portable)
 * Shared files → App fixed key (portable, opens on other devices)
 *
 * File format: 4-byte magic header + payload
 *   "ATKL" → Keystore (local)   : magic + 12-byte IV + GCM ciphertext
 *   "ATSH" → Shared (fixed key) : magic + 16-byte IV + CBC ciphertext
 *   (no magic / plain '{')      → legacy plaintext JSON (backward compatible)
 */
public class EncryptionHelper {

    private static final String TAG = "#EncHelper";
    private static final String KEYSTORE = "AndroidKeyStore";
    private static final String KS_ALIAS = "atale_local_key";

    private static final byte[] MAGIC_LOCAL  = {'A','T','K','L'};
    private static final byte[] MAGIC_SHARED = {'A','T','S','H'};

    // Fixed app key for portable/shared files (16 bytes = AES-128).
    // NOTE: basic protection only — embedded in app, can be reverse-engineered.
    private static final byte[] SHARED_KEY = {
        (byte)0x41,(byte)0x74,(byte)0x61,(byte)0x6C,(byte)0x65,(byte)0x53,(byte)0x68,(byte)0x72,
        (byte)0x4B,(byte)0x65,(byte)0x79,(byte)0x32,(byte)0x30,(byte)0x32,(byte)0x36,(byte)0x21
    };

    // ────────── Keystore (local) key ──────────
    private static SecretKey getOrCreateLocalKey() throws Exception {
        KeyStore ks = KeyStore.getInstance(KEYSTORE);
        ks.load(null);
        if (ks.containsAlias(KS_ALIAS)) {
            return ((KeyStore.SecretKeyEntry) ks.getEntry(KS_ALIAS, null)).getSecretKey();
        }
        KeyGenerator kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE);
        kg.init(new KeyGenParameterSpec.Builder(KS_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build());
        return kg.generateKey();
    }

    // ────────── Encrypt local (Keystore, GCM) ──────────
    public static byte[] encryptLocal(String json) {
        try {
            SecretKey key = getOrCreateLocalKey();
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] iv = c.getIV();                       // 12 bytes
            byte[] ct = c.doFinal(json.getBytes(StandardCharsets.UTF_8));
            byte[] out = new byte[4 + iv.length + ct.length];
            System.arraycopy(MAGIC_LOCAL, 0, out, 0, 4);
            System.arraycopy(iv, 0, out, 4, iv.length);
            System.arraycopy(ct, 0, out, 4 + iv.length, ct.length);
            return out;
        } catch (Exception e) {
            Log.e(TAG, "encryptLocal fail: " + e.getMessage());
            return json.getBytes(StandardCharsets.UTF_8); // fallback plaintext
        }
    }

    private static String decryptLocal(byte[] data) throws Exception {
        SecretKey key = getOrCreateLocalKey();
        byte[] iv = new byte[12];
        System.arraycopy(data, 4, iv, 0, 12);
        byte[] ct = new byte[data.length - 16];
        System.arraycopy(data, 16, ct, 0, ct.length);
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        return new String(c.doFinal(ct), StandardCharsets.UTF_8);
    }

    // ────────── Encrypt shared (fixed key, CBC) ──────────
    public static byte[] encryptShared(String json) {
        try {
            SecretKeySpec key = new SecretKeySpec(SHARED_KEY, "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[16];
            new java.security.SecureRandom().nextBytes(iv);
            c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] ct = c.doFinal(json.getBytes(StandardCharsets.UTF_8));
            byte[] out = new byte[4 + 16 + ct.length];
            System.arraycopy(MAGIC_SHARED, 0, out, 0, 4);
            System.arraycopy(iv, 0, out, 4, 16);
            System.arraycopy(ct, 0, out, 20, ct.length);
            return out;
        } catch (Exception e) {
            Log.e(TAG, "encryptShared fail: " + e.getMessage());
            return json.getBytes(StandardCharsets.UTF_8);
        }
    }

    private static String decryptShared(byte[] data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SHARED_KEY, "AES");
        byte[] iv = new byte[16];
        System.arraycopy(data, 4, iv, 0, 16);
        byte[] ct = new byte[data.length - 20];
        System.arraycopy(data, 20, ct, 0, ct.length);
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return new String(c.doFinal(ct), StandardCharsets.UTF_8);
    }

    // ────────── Auto-detect & decrypt ──────────
    public static String decryptAny(byte[] data) {
        if (data == null || data.length == 0) return "";
        try {
            if (startsWith(data, MAGIC_LOCAL))  return decryptLocal(data);
            if (startsWith(data, MAGIC_SHARED)) return decryptShared(data);
        } catch (Exception e) {
            Log.e(TAG, "decryptAny fail: " + e.getMessage());
        }
        // Legacy plaintext JSON
        return new String(data, StandardCharsets.UTF_8);
    }

    private static boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) if (data[i] != prefix[i]) return false;
        return true;
    }
}
