package com.example.newcardmaker.Activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newcardmaker.R;
import com.example.newcardmaker.billing.SubscriptionManager;

import java.util.List;

public class PremiumActivity extends AppCompatActivity {

    private SubscriptionManager subMgr;
    private final com.android.billingclient.api.ProductDetails[] weeklyDetails  = {null};
    private final com.android.billingclient.api.ProductDetails[] monthlyDetails = {null};
    private final String[] weeklyToken  = {""};
    private final String[] monthlyToken = {""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        TextView tvWeeklyPrice  = findViewById(R.id.tv_weekly_price);
        TextView tvMonthlyPrice = findViewById(R.id.tv_monthly_price);
        TextView btnWeekly      = findViewById(R.id.btn_weekly_subscribe);
        TextView btnMonthly     = findViewById(R.id.btn_monthly_subscribe);
        TextView btnClose       = findViewById(R.id.btn_premium_close);
        TextView btnRestore     = findViewById(R.id.btn_restore_purchase);

        subMgr = SubscriptionManager.getInstance(this);

        subMgr.setListener(new SubscriptionManager.SubscriptionListener() {
            @Override
            public void onSubscriptionStatusChanged(boolean isActive) {
                if (isActive) {
                    runOnUiThread(() -> {
                        Toast.makeText(PremiumActivity.this,
                            "✅ Premium activated!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }

            @Override
            public void onProductsLoaded(List<com.android.billingclient.api.ProductDetails> products) {
                runOnUiThread(() -> {
                    for (com.android.billingclient.api.ProductDetails pd : products) {
                        List<com.android.billingclient.api.ProductDetails.SubscriptionOfferDetails> offers =
                            pd.getSubscriptionOfferDetails();
                        if (offers == null || offers.isEmpty()) continue;
                        String price = offers.get(0).getPricingPhases()
                            .getPricingPhaseList().get(0).getFormattedPrice();
                        String token = offers.get(0).getOfferToken();
                        if (pd.getProductId().equals(SubscriptionManager.PRODUCT_WEEKLY)) {
                            tvWeeklyPrice.setText(price + " / week");
                            weeklyDetails[0] = pd;
                            weeklyToken[0]   = token;
                        } else if (pd.getProductId().equals(SubscriptionManager.PRODUCT_MONTHLY)) {
                            tvMonthlyPrice.setText(price + " / month");
                            monthlyDetails[0] = pd;
                            monthlyToken[0]   = token;
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                    Toast.makeText(PremiumActivity.this, message, Toast.LENGTH_SHORT).show());
            }
        });

        // ── Weekly subscribe
        btnWeekly.setOnClickListener(v -> {
            if (weeklyDetails[0] != null) {
                subMgr.launchPurchase(this, weeklyDetails[0], weeklyToken[0]);
            } else {
                Toast.makeText(this, "Loading... please wait", Toast.LENGTH_SHORT).show();
            }
        });

        // ── Monthly subscribe
        btnMonthly.setOnClickListener(v -> {
            if (monthlyDetails[0] != null) {
                subMgr.launchPurchase(this, monthlyDetails[0], monthlyToken[0]);
            } else {
                Toast.makeText(this, "Loading... please wait", Toast.LENGTH_SHORT).show();
            }
        });

        // ── Restore purchase
        btnRestore.setOnClickListener(v -> {
            subMgr.checkExistingSubscriptions();
            Toast.makeText(this, "Checking purchases...", Toast.LENGTH_SHORT).show();
        });

        // ── Close
        btnClose.setOnClickListener(v -> finish());

        // ── Start billing & load products
        subMgr.startConnection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
