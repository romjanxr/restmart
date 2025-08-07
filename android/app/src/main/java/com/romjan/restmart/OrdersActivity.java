package com.romjan.restmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.romjan.restmart.User;
import com.romjan.restmart.PaymentInitiationRequest;
import com.romjan.restmart.PaymentInitiationResponse;


public class OrdersActivity extends AppCompatActivity implements OrderAdapter.OnCancelOrderClickListener, OrderAdapter.OnOrderClickListener, OrderAdapter.OnPayNowClickListener {


    private RecyclerView ordersRecyclerView;
    private OrderAdapter orderAdapter;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SharedPreferencesManager sharedPreferencesManager;
    private TextView emptyOrdersView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyOrdersView = findViewById(R.id.empty_orders_view);

        apiService = NetworkConfig.getApiService(this);
        sharedPreferencesManager = new SharedPreferencesManager(this);

        orderAdapter = new OrderAdapter(this, new ArrayList<>(), this, this, this);
        ordersRecyclerView.setAdapter(orderAdapter);

        fetchOrders();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void fetchOrders() {
        progressBar.setVisibility(View.VISIBLE);
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();

        apiService.getOrders(authToken).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body();
                    orderAdapter.setOrders(orders);
                    updateOrdersView(orders);
                } else {
                    Toast.makeText(OrdersActivity.this, "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrdersActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateOrdersView(List<Order> orders) {
        if (orders.isEmpty()) {
            ordersRecyclerView.setVisibility(View.GONE);
            emptyOrdersView.setVisibility(View.VISIBLE);
        } else {
            ordersRecyclerView.setVisibility(View.VISIBLE);
            emptyOrdersView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCancelOrderClick(String orderId) {
        progressBar.setVisibility(View.VISIBLE);
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();

        apiService.cancelOrder(authToken, orderId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(OrdersActivity.this, "Order cancelled successfully", Toast.LENGTH_SHORT).show();
                    fetchOrders(); // Refresh the list
                } else {
                    Toast.makeText(OrdersActivity.this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrdersActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onOrderClick(String orderId) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, orderId);
        startActivity(intent);
    }

    @Override
    public void onPayNowClick(Order order) {
        handlePayment(order);
    }

    private void handlePayment(Order order) {
        if (order == null) {
            Toast.makeText(this, "Order details not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = order.getUser();
        if (user.getFirstName() == null || user.getFirstName().isEmpty() ||
                user.getLastName() == null || user.getLastName().isEmpty() ||
                user.getAddress() == null || user.getAddress().isEmpty() ||
                user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            Toast.makeText(this, "Please update your profile with name, address, and phone number.", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        ApiService apiService = NetworkConfig.getApiService(this);

        PaymentInitiationRequest request = new PaymentInitiationRequest(
                order.getTotalPrice(),
                order.getId(),
                order.getItems().size()
        );

        apiService.initiatePayment(authToken, request).enqueue(new Callback<PaymentInitiationResponse>() {
            @Override
            public void onResponse(Call<PaymentInitiationResponse> call, Response<PaymentInitiationResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    String paymentUrl = response.body().getPaymentUrl();
                    if (paymentUrl != null && !paymentUrl.isEmpty()) {
                        Intent intent = new Intent(OrdersActivity.this, PaymentActivity.class);
                        intent.putExtra(PaymentActivity.EXTRA_PAYMENT_URL, paymentUrl);
                        startActivity(intent);
                    } else {
                        Toast.makeText(OrdersActivity.this, "Payment initiation failed.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OrdersActivity.this, "Payment initiation failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentInitiationResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrdersActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

