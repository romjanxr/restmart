package com.romjan.restmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "extra_order_id";

    private TextView orderIdTextView;
    private TextView orderStatusTextView;
    private TextView orderTotalPriceTextView;
    private TextView orderCreatedAtTextView;
    private TextView customerNameTextView;
    private TextView customerEmailTextView;
    private TextView customerPhoneTextView;
    private TextView customerAddressTextView;
    private RecyclerView orderItemsRecyclerView;
    private ProgressBar progressBar;
    private Button cancelOrderButton;
    private Button payNowButton;
    private OrderItemAdapter adapter;
    private String orderId;
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orderIdTextView = findViewById(R.id.orderIdTextView);
        orderStatusTextView = findViewById(R.id.orderStatusTextView);
        orderTotalPriceTextView = findViewById(R.id.orderTotalPriceTextView);
        orderCreatedAtTextView = findViewById(R.id.orderCreatedAtTextView);
        customerNameTextView = findViewById(R.id.customerNameTextView);
        customerEmailTextView = findViewById(R.id.customerEmailTextView);
        customerPhoneTextView = findViewById(R.id.customerPhoneTextView);
        customerAddressTextView = findViewById(R.id.customerAddressTextView);
        orderItemsRecyclerView = findViewById(R.id.orderItemsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        cancelOrderButton = findViewById(R.id.cancelOrderButton);
        payNowButton = findViewById(R.id.payNowButton);

        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);
        if (orderId == null) {
            Toast.makeText(this, "Order ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchOrderDetails(orderId);

        cancelOrderButton.setOnClickListener(v -> cancelOrder());
        payNowButton.setOnClickListener(v -> handlePayment());
    }

    private void fetchOrderDetails(String orderId) {
        progressBar.setVisibility(View.VISIBLE);
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        ApiService apiService = NetworkConfig.getApiService(this);

        apiService.getOrderDetails(authToken, orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    currentOrder = response.body();
                    populateOrderDetails(currentOrder);
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Failed to fetch order details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateOrderDetails(Order order) {
        if (order.getId() != null) {
            orderIdTextView.setText("Order #" + order.getId().substring(0, 8));
        }
        orderStatusTextView.setText(order.getStatus());
        orderTotalPriceTextView.setText(String.format(Locale.getDefault(), "$%.2f", order.getTotalPrice()));
        orderCreatedAtTextView.setText(order.getCreatedAt().substring(0, 10));

        User user = order.getUser();
        if (user != null) {
            customerNameTextView.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
            customerEmailTextView.setText(user.getEmail());
            customerPhoneTextView.setText(user.getPhoneNumber());
            customerAddressTextView.setText(user.getAddress());
        }

        adapter = new OrderItemAdapter(order.getItems());
        orderItemsRecyclerView.setAdapter(adapter);

        View buttonContainer = findViewById(R.id.buttonContainer);
        int statusBackground;
        switch (order.getStatus().toLowerCase()) {
            case "not paid":
                statusBackground = R.drawable.bg_status_not_paid;
                buttonContainer.setVisibility(View.VISIBLE);
                break;
            case "ready to ship":
                statusBackground = R.drawable.bg_status_ready_to_ship;
                buttonContainer.setVisibility(View.GONE);
                break;
            case "shipped":
                statusBackground = R.drawable.bg_status_shipped;
                buttonContainer.setVisibility(View.GONE);
                break;
            case "delivered":
                statusBackground = R.drawable.bg_status_delivered;
                buttonContainer.setVisibility(View.GONE);
                break;
            case "canceled":
                statusBackground = R.drawable.bg_status_canceled;
                buttonContainer.setVisibility(View.GONE);
                break;
            default:
                statusBackground = R.drawable.bg_status_paid; // A default fallback
                buttonContainer.setVisibility(View.GONE);
                break;
        }
        orderStatusTextView.setBackgroundResource(statusBackground);
    }

    private void handlePayment() {
        if (currentOrder == null) {
            Toast.makeText(this, "Order details not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = currentOrder.getUser();
        if (user.getFirstName() == null || user.getFirstName().isEmpty() ||
                user.getLastName() == null || user.getLastName().isEmpty() ||
                user.getAddress() == null || user.getAddress().isEmpty() ||
                user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            Toast.makeText(this, "Please update your profile with name, address, and phone number.", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        ApiService apiService = NetworkConfig.getApiService(this);

        PaymentInitiationRequest request = new PaymentInitiationRequest(
                currentOrder.getTotalPrice(),
                orderId, // Use the ID from the intent
                currentOrder.getItems().size()
        );

        apiService.initiatePayment(authToken, request).enqueue(new Callback<PaymentInitiationResponse>() {
            @Override
            public void onResponse(Call<PaymentInitiationResponse> call, Response<PaymentInitiationResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    String paymentUrl = response.body().getPaymentUrl();
                    if (paymentUrl != null && !paymentUrl.isEmpty()) {
                        Intent intent = new Intent(OrderDetailActivity.this, PaymentActivity.class);
                        intent.putExtra(PaymentActivity.EXTRA_PAYMENT_URL, paymentUrl);
                        startActivity(intent);
                    } else {
                        Toast.makeText(OrderDetailActivity.this, "Payment initiation failed.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Payment initiation failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentInitiationResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelOrder() {
        progressBar.setVisibility(View.VISIBLE);
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        ApiService apiService = NetworkConfig.getApiService(this);

        apiService.cancelOrder(authToken, orderId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(OrderDetailActivity.this, "Order cancelled successfully.", Toast.LENGTH_SHORT).show();
                    fetchOrderDetails(orderId); // Refresh details
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Failed to cancel order.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}