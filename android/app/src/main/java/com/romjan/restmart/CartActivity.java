package com.romjan.restmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnItemActionListener {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceTextView;
    private Button checkoutButton;
    private ApiService apiService;
    private SharedPreferencesManager sharedPreferencesManager;
    private String cartPk;
    private Toolbar toolbar;
    private List<CartItem> cartItems = new ArrayList<>();
    private ProgressBar progressBar, checkoutProgress;
    private LinearLayout emptyCartView;

    private static final String TAG = "CartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        checkoutButton = findViewById(R.id.checkoutButton);
        progressBar = findViewById(R.id.progressBar);
        emptyCartView = findViewById(R.id.empty_cart_view);
        checkoutProgress = findViewById(R.id.checkout_progress);

        cartAdapter = new CartAdapter(this);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);

        apiService = NetworkConfig.getApiService();
        sharedPreferencesManager = new SharedPreferencesManager(this);

        createOrGetCart();

        checkoutButton.setOnClickListener(v -> {
            createOrder();
        });
    }

    private void createOrder() {
        if (cartPk == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        CreateOrderRequest request = new CreateOrderRequest(cartPk);

        apiService.createOrder(authToken, request).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CartActivity.this, "Order created successfully!", Toast.LENGTH_SHORT).show();
                    
                    // Redirect to the OrderDetailActivity
                    Intent intent = new Intent(CartActivity.this, OrderDetailActivity.class);
                    intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, response.body().getId());
                    startActivity(intent);
                    
                    finish(); // Close the cart activity
                } else {
                    try {
                        Log.e(TAG, "Failed to create order: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                    Toast.makeText(CartActivity.this, "Failed to create order.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                setLoading(false);
                Toast.makeText(CartActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            checkoutButton.setEnabled(false);
            checkoutButton.setText("PLACING ORDER...");
            checkoutButton.setBackgroundColor(getResources().getColor(R.color.colorButtonDisabled));
            checkoutProgress.setVisibility(View.VISIBLE);
        } else {
            checkoutButton.setEnabled(true);
            checkoutButton.setText("Place Order");
            checkoutButton.setBackgroundColor(getResources().getColor(R.color.purple_500));
            checkoutProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createOrGetCart() {
        progressBar.setVisibility(View.VISIBLE);
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        apiService.createCart(authToken).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartPk = response.body().getId();
                    getCartItems();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CartActivity.this, "Failed to load cart.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CartActivity.this, "Network error loading cart.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCartItems() {
        if (cartPk == null) return;
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        apiService.getCartItems(authToken, cartPk).enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    cartItems.clear();
                    cartItems.addAll(response.body());
                    cartAdapter.setItems(cartItems);
                    updateTotalPrice();
                    updateCartView();
                } else {
                    Toast.makeText(CartActivity.this, "Failed to load cart items.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CartActivity.this, "Network error loading cart items.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        totalPriceTextView.setText(String.format(Locale.getDefault(), "Total: $%.2f", total));
    }

    private void updateCartView() {
        if (cartItems.isEmpty()) {
            cartRecyclerView.setVisibility(View.GONE);
            emptyCartView.setVisibility(View.VISIBLE);
        } else {
            cartRecyclerView.setVisibility(View.VISIBLE);
            emptyCartView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onQuantityChange(CartItem item, int newQuantity) {
        if (newQuantity <= 0) {
            deleteCartItem(item);
        } else {
            updateCartItem(item, newQuantity);
        }
    }

    @Override
    public void onDeleteItem(CartItem item) {
        deleteCartItem(item);
    }

    private void deleteCartItem(CartItem item) {
        if (cartPk == null) return;

        int position = findItemPosition(item);
        if (position == -1) return;

        item.setUpdating(true);
        cartAdapter.notifyItemChanged(position);

        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        apiService.deleteCartItem(authToken, cartPk, item.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CartActivity.this, "Item removed", Toast.LENGTH_SHORT).show();
                    getCartItems(); // Re-fetch the entire cart
                } else {
                    Toast.makeText(CartActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                    item.setUpdating(false);
                    cartAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                item.setUpdating(false);
                cartAdapter.notifyItemChanged(position);
            }
        });
    }

    private void updateCartItem(CartItem item, int newQuantity) {
        if (cartPk == null) return;

        int position = findItemPosition(item);
        if (position == -1) return;

        item.setUpdating(true);
        cartAdapter.notifyItemChanged(position);

        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        UpdateQuantityRequest request = new UpdateQuantityRequest(newQuantity);
        apiService.updateCartItemQuantity(authToken, cartPk, item.getId(), request).enqueue(new Callback<CartItem>() {
            @Override
            public void onResponse(Call<CartItem> call, Response<CartItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CartActivity.this, "Quantity updated", Toast.LENGTH_SHORT).show();
                    getCartItems(); // Re-fetch the entire cart
                } else {
                    Toast.makeText(CartActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                    item.setUpdating(false);
                    cartAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onFailure(Call<CartItem> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                item.setUpdating(false);
                cartAdapter.notifyItemChanged(position);
            }
        });
    }

    private int findItemPosition(CartItem item) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getId() == item.getId()) {
                return i;
            }
        }
        return -1;
    }
}