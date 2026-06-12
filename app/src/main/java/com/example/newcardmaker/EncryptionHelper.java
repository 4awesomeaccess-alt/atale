package com.example.newcardmaker;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

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

    // Separate HMAC key for shared-file integrity (32 bytes). MUST differ from SHARED_KEY.
    private static final byte[] SHARED_HMAC_KEY = {
        (byte)0x9A,(byte)0x3F,(byte)0xC1,(byte)0x07,(byte)0x52,(byte)0xEE,(byte)0x84,(byte)0x6B,
        (byte)0x1D,(byte)0xB9,(byte)0x40,(byte)0x2C,(byte)0x77,(byte)0xA5,(byte)0x6F,(byte)0x38,
        (byte)0xD4,(byte)0x0E,(byte)0x91,(byte)0xCB,(byte)0x63,(byte)0x2A,(byte)0xF8,(byte)0x15,
        (byte)0x49,(byte)0x8C,(byte)0xB2,(byte)0x70,(byte)0xE6,(byte)0x5D,(byte)0x3B,(byte)0xA7
    };
    private static final int HMAC_LEN = 32;

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

    // ────────── Encrypt shared (fixed key, CBC + HMAC) ──────────
    // Format: magic(4) + iv(16) + ciphertext + hmac(32)
    public static byte[] encryptShared(String json) {
        try {
            SecretKeySpec key = new SecretKeySpec(SHARED_KEY, "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[16];
            new java.security.SecureRandom().nextBytes(iv);
            c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            byte[] ct = c.doFinal(json.getBytes(StandardCharsets.UTF_8));

            // HMAC over (iv + ciphertext) — encrypt-then-MAC
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SHARED_HMAC_KEY, "HmacSHA256"));
            mac.update(iv);
            byte[] tag = mac.doFinal(ct);

            byte[] out = new byte[4 + 16 + ct.length + HMAC_LEN];
            System.arraycopy(MAGIC_SHARED, 0, out, 0, 4);
            System.arraycopy(iv, 0, out, 4, 16);
            System.arraycopy(ct, 0, out, 20, ct.length);
            System.arraycopy(tag, 0, out, 20 + ct.length, HMAC_LEN);
            return out;
        } catch (Exception e) {
            Log.e(TAG, "encryptShared fail: " + e.getMessage());
            return json.getBytes(StandardCharsets.UTF_8);
        }
    }

    private static String decryptShared(byte[] data) throws Exception {
        // Need at least magic + iv + hmac
        if (data.length < 4 + 16 + HMAC_LEN) throw new SecurityException("Too short");

        byte[] iv = new byte[16];
        System.arraycopy(data, 4, iv, 0, 16);
        int ctLen = data.length - 20 - HMAC_LEN;
        byte[] ct = new byte[ctLen];
        System.arraycopy(data, 20, ct, 0, ctLen);
        byte[] storedTag = new byte[HMAC_LEN];
        System.arraycopy(data, 20 + ctLen, storedTag, 0, HMAC_LEN);

        // Verify HMAC FIRST (constant-time) — reject if tampered
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SHARED_HMAC_KEY, "HmacSHA256"));
        mac.update(iv);
        byte[] calcTag = mac.doFinal(ct);
        if (!MessageDigest.isEqual(storedTag, calcTag)) {
            throw new SecurityException("HMAC mismatch — file tampered");
        }

        SecretKeySpec key = new SecretKeySpec(SHARED_KEY, "AES");
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return new String(c.doFinal(ct), StandardCharsets.UTF_8);
    }

    // ────────── Auto-detect & decrypt ──────────
    public static String decryptAny(byte[] data) {
        if (data == null || data.length == 0) return "";
        if (startsWith(data, MAGIC_LOCAL)) {
            try { return decryptLocal(data); }
            catch (Exception e) { Log.e(TAG, "local decrypt fail: " + e.getMessage()); return ""; }
        }
        if (startsWith(data, MAGIC_SHARED)) {
            try { return decryptShared(data); }
            catch (Exception e) { Log.e(TAG, "shared decrypt/HMAC fail: " + e.getMessage()); return ""; }
        }
        // Legacy plaintext JSON (no magic header)
        return new String(data, StandardCharsets.UTF_8);
    }

    private static boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) if (data[i] != prefix[i]) return false;
        return true;
    }
}
