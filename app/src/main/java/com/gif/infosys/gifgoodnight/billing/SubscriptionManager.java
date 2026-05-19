package com.gif.infosys.gifgoodnight.billing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.billingclient.api.*;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionManager implements PurchasesUpdatedListener {

    private static final String TAG = "SubscriptionManager";

    // ── Product IDs — Google Play Console ma same ID rakho
    public static final String PRODUCT_WEEKLY  = "card_maker_weekly";
    public static final String PRODUCT_MONTHLY = "card_maker_monthly";

    private static SubscriptionManager instance;
    private BillingClient billingClient;
    private final Context context;
    private SubscriptionListener listener;

    private boolean isSubscribed = false;

    public interface SubscriptionListener {
        void onSubscriptionStatusChanged(boolean isActive);
        void onProductsLoaded(List<ProductDetails> products);
        void onError(String message);
    }

    private SubscriptionManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static SubscriptionManager getInstance(Context context) {
        if (instance == null) instance = new SubscriptionManager(context);
        return instance;
    }

    public void setListener(SubscriptionListener listener) {
        this.listener = listener;
    }

    // ── Billing client connect
    public void startConnection() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult result) {
                if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing connected");
                    checkExistingSubscriptions();
                    loadProducts();
                } else {
                    Log.e(TAG, "Billing setup failed: " + result.getDebugMessage());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.w(TAG, "Billing disconnected — retry");
                startConnection();
            }
        });
    }

    // ── Existing subscription check
    public void checkExistingSubscriptions() {
        if (billingClient == null || !billingClient.isReady()) return;
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            (result, purchases) -> {
                if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    isSubscribed = false;
                    for (Purchase p : purchases) {
                        if (p.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                            acknowledgePurchase(p);
                            isSubscribed = true;
                        }
                    }
                    if (listener != null) listener.onSubscriptionStatusChanged(isSubscribed);
                }
            }
        );
    }

    // ── Load products from Play Store
    public void loadProducts() {
        if (billingClient == null || !billingClient.isReady()) return;

        List<QueryProductDetailsParams.Product> products = new ArrayList<>();
        products.add(QueryProductDetailsParams.Product.newBuilder()
            .setProductId(PRODUCT_WEEKLY)
            .setProductType(BillingClient.ProductType.SUBS)
            .build());
        products.add(QueryProductDetailsParams.Product.newBuilder()
            .setProductId(PRODUCT_MONTHLY)
            .setProductType(BillingClient.ProductType.SUBS)
            .build());

        billingClient.queryProductDetailsAsync(
            QueryProductDetailsParams.newBuilder().setProductList(products).build(),
            (result, productDetailsList) -> {
                if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    if (listener != null) listener.onProductsLoaded(productDetailsList);
                }
            }
        );
    }

    // ── Launch purchase flow
    public void launchPurchase(Activity activity, ProductDetails productDetails, String offerToken) {
        if (billingClient == null || !billingClient.isReady()) return;

        List<BillingFlowParams.ProductDetailsParams> detailsList = new ArrayList<>();
        detailsList.add(BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .setOfferToken(offerToken)
            .build());

        BillingFlowParams params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(detailsList)
            .build();

        billingClient.launchBillingFlow(activity, params);
    }

    // ── Purchase result
    @Override
    public void onPurchasesUpdated(@NonNull BillingResult result, List<Purchase> purchases) {
        if (result.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    acknowledgePurchase(purchase);
                    isSubscribed = true;
                    if (listener != null) listener.onSubscriptionStatusChanged(true);
                }
            }
        } else if (result.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            if (listener != null) listener.onError("Purchase cancelled");
        } else {
            if (listener != null) listener.onError("Purchase failed: " + result.getDebugMessage());
        }
    }

    // ── Acknowledge purchase
    private void acknowledgePurchase(Purchase purchase) {
        if (purchase.isAcknowledged()) return;
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.getPurchaseToken())
            .build();
        billingClient.acknowledgePurchase(params, billingResult ->
            Log.d(TAG, "Acknowledge: " + billingResult.getResponseCode()));
    }

    public boolean isSubscribed() { return isSubscribed; }

    public void destroy() {
        if (billingClient != null) billingClient.endConnection();
    }
}
