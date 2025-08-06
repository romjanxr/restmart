package com.romjan.restmart;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    private ViewPager2 productImageSlider;
    private ProductImageAdapter productImageAdapter;
    private TextView productName, productPrice, productDescription, productStock;
    private Button btnAddToCart;
    private ProgressBar addToCartProgress;
    private ApiService apiService;
    private SharedPreferencesManager sharedPreferencesManager;
    private Product product;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        apiService = NetworkConfig.getApiService();
        sharedPreferencesManager = new SharedPreferencesManager(this);

        productImageSlider = findViewById(R.id.product_image_slider);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productDescription = findViewById(R.id.product_description);
        productStock = findViewById(R.id.product_stock);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        addToCartProgress = findViewById(R.id.add_to_cart_progress);

        product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            productName.setText(product.getName());
            productPrice.setText(String.format("$%.2f", product.getPrice()));
            productDescription.setText(product.getDescription());
            productStock.setText("In Stock: " + product.getStock());

            productImageAdapter = new ProductImageAdapter(product.getImages());
            productImageSlider.setAdapter(productImageAdapter);
        }

        btnAddToCart.setOnClickListener(v -> addToCart());

        findViewById(R.id.btn_quantity_minus).setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityView();
            }
        });

        findViewById(R.id.btn_quantity_plus).setOnClickListener(v -> {
            quantity++;
            updateQuantityView();
        });
    }

    private void updateQuantityView() {
        ((TextView) findViewById(R.id.tv_quantity)).setText(String.valueOf(quantity));
    }

    private void addToCart() {
        setLoading(true);
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        apiService.createCart(authToken).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String cartPk = response.body().getId();
                    addItemToCart(authToken, cartPk);
                } else {
                    setLoading(false);
                    Toast.makeText(ProductDetailsActivity.this, "Failed to create cart.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                setLoading(false);
                Toast.makeText(ProductDetailsActivity.this, "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addItemToCart(String authToken, String cartPk) {
        AddItemRequest addItemRequest = new AddItemRequest(product.getId(), quantity);
        apiService.addItemToCart(authToken, cartPk, addItemRequest).enqueue(new Callback<CartItem>() {
            @Override
            public void onResponse(Call<CartItem> call, Response<CartItem> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(ProductDetailsActivity.this, "Item added to cart.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Failed to add item to cart.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartItem> call, Throwable t) {
                setLoading(false);
                Toast.makeText(ProductDetailsActivity.this, "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            btnAddToCart.setEnabled(false);
            btnAddToCart.setText("ADDING...");
            btnAddToCart.setBackgroundColor(getResources().getColor(R.color.colorButtonDisabled));
            addToCartProgress.setVisibility(View.VISIBLE);
        } else {
            btnAddToCart.setEnabled(true);
            btnAddToCart.setText("Add to Cart");
            btnAddToCart.setBackgroundColor(getResources().getColor(R.color.purple_500));
            addToCartProgress.setVisibility(View.GONE);
        }
    }
}
