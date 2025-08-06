package com.romjan.restmart;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    public static final String EXTRA_PAYMENT_URL = "extra_payment_url";
    private static final String FINAL_REDIRECT_URL = "https://restmart-client.vercel.app/dashboard/orders";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        String paymentUrl = getIntent().getStringExtra(EXTRA_PAYMENT_URL);
        if (paymentUrl == null || paymentUrl.isEmpty()) {
            Toast.makeText(this, "Invalid Payment URL", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new PaymentWebViewClient());
        webView.loadUrl(paymentUrl);
    }

    private class PaymentWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();

            if (url.startsWith(FINAL_REDIRECT_URL)) {
                // Intercept the final redirect, don't load it.
                paymentSuccessful();
                return true; // Indicate we've handled this URL
            }
            // Allow all other URLs to load normally.
            return super.shouldOverrideUrlLoading(view, request);
        }
    }

    private void paymentSuccessful() {
        Toast.makeText(this, "Payment successful! Your order is being processed.", Toast.LENGTH_LONG).show();
        redirectToOrders();
    }

    private void redirectToOrders() {
        // Redirect to the native Order History screen
        Intent intent = new Intent(this, OrdersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close the PaymentActivity
    }
}
