package com.romjan.restmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.romjan.restmart.databinding.ActivityProductDetailsBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity implements ReviewAdapter.OnReviewInteractionListener {

    private ActivityProductDetailsBinding binding;
    private ApiService apiService;
    private SharedPreferencesManager sharedPreferencesManager;
    private Product product;
    private int productId;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();
    private LinearLayout reviewFormContainer;
    private RecyclerView reviewsRecyclerView;
    private TextView tvNoReviews;
    private RatingBar newReviewRatingBar;
    private EditText etNewReviewComment;
    private Button btnSubmitReview;
    private ProgressBar reviewsProgressBar;
    private boolean isEditing = false;
    private int editingReviewId;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = NetworkConfig.getApiService(this);
        sharedPreferencesManager = new SharedPreferencesManager(this);

        product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            Toast.makeText(this, "Product not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        productId = product.getId();

        reviewFormContainer = findViewById(R.id.reviewFormContainer);
        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView);
        tvNoReviews = findViewById(R.id.tvNoReviews);
        newReviewRatingBar = findViewById(R.id.newReviewRatingBar);
        etNewReviewComment = findViewById(R.id.etNewReviewComment);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        reviewsProgressBar = findViewById(R.id.reviewsProgressBar);

        populateProductDetails();
        setupRecyclerView();
        fetchReviews();
        checkUserPermission();

        binding.backButton.setOnClickListener(v -> onBackPressed());
        binding.btnQuantityMinus.setOnClickListener(v -> updateQuantity(false));
        binding.btnQuantityPlus.setOnClickListener(v -> updateQuantity(true));
        binding.btnAddToCart.setOnClickListener(v -> addToCart());

        btnSubmitReview.setOnClickListener(v -> {
            if (isEditing) {
                updateReview();
            } else {
                submitReview();
            }
        });
    }

    private void populateProductDetails() {
        binding.productName.setText(product.getName());
        binding.productTitleToolbar.setText(product.getName());
        binding.productPrice.setText("$" + product.getPrice());
        binding.productStock.setText("In Stock: " + product.getStock());
        binding.productDescription.setText(product.getDescription());

        ImageSliderAdapter adapter = new ImageSliderAdapter(this, product.getImages(), position -> {
            Intent intent = new Intent(this, FullscreenImageActivity.class);
            ArrayList<String> imageUrls = new ArrayList<>();
            for (Image image : product.getImages()) {
                imageUrls.add(image.getImage());
            }
            intent.putStringArrayListExtra("image_urls", imageUrls);
            intent.putExtra("position", position);
            startActivity(intent);
        });
        binding.productImageSlider.setAdapter(adapter);
    }

    private void setupRecyclerView() {
        // TODO: Get the real current user ID
        int currentUserId = -1; // Placeholder
        reviewAdapter = new ReviewAdapter(this, reviewList, currentUserId, this);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewsRecyclerView.setAdapter(reviewAdapter);
    }

    private void fetchReviews() {
        reviewsProgressBar.setVisibility(View.VISIBLE);
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        apiService.getReviews(authToken, productId).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                reviewsProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    reviewList.clear();
                    reviewList.addAll(response.body());
                    reviewAdapter.notifyDataSetChanged();
                    tvNoReviews.setVisibility(reviewList.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                reviewsProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void checkUserPermission() {
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        apiService.hasOrdered(authToken, productId).enqueue(new Callback<HasOrderedResponse>() {
            @Override
            public void onResponse(Call<HasOrderedResponse> call, Response<HasOrderedResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().hasOrdered()) {
                        reviewFormContainer.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<HasOrderedResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void submitReview() {
        int rating = (int) newReviewRatingBar.getRating();
        String comment = etNewReviewComment.getText().toString().trim();
        if (rating == 0 || comment.isEmpty()) {
            Toast.makeText(this, "Please provide a rating and a comment.", Toast.LENGTH_SHORT).show();
            return;
        }

        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        ReviewRequest reviewRequest = new ReviewRequest(rating, comment);
        apiService.createReview(authToken, productId, reviewRequest).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductDetailsActivity.this, "Review submitted successfully.", Toast.LENGTH_SHORT).show();
                    fetchReviews();
                    resetReviewForm();
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Failed to submit review.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateReview() {
        int rating = (int) newReviewRatingBar.getRating();
        String comment = etNewReviewComment.getText().toString().trim();
        if (rating == 0 || comment.isEmpty()) {
            Toast.makeText(this, "Please provide a rating and a comment.", Toast.LENGTH_SHORT).show();
            return;
        }

        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        ReviewRequest reviewRequest = new ReviewRequest(rating, comment);
        apiService.updateReview(authToken, productId, editingReviewId, reviewRequest).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductDetailsActivity.this, "Review updated successfully.", Toast.LENGTH_SHORT).show();
                    fetchReviews();
                    resetReviewForm();
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Failed to update review.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetReviewForm() {
        newReviewRatingBar.setRating(0);
        etNewReviewComment.setText("");
        isEditing = false;
        btnSubmitReview.setText("Submit");
    }

    @Override
    public void onEditReview(Review review) {
        isEditing = true;
        editingReviewId = review.getId();
        newReviewRatingBar.setRating(review.getRating());
        etNewReviewComment.setText(review.getComment());
        btnSubmitReview.setText("Update");
    }

    @Override
    public void onDeleteReview(Review review) {
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        apiService.deleteReview(authToken, productId, review.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductDetailsActivity.this, "Review deleted successfully.", Toast.LENGTH_SHORT).show();
                    fetchReviews();
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Failed to delete review.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateQuantity(boolean isIncrement) {
        if (isIncrement) {
            quantity++;
        } else {
            if (quantity > 1) {
                quantity--;
            }
        }
        binding.tvQuantity.setText(String.valueOf(quantity));
    }

    private void addToCart() {
        setLoadingState(true);
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        String cartPk = sharedPreferencesManager.getCartPk();

        if (cartPk == null) {
            createCartThenAddItem(authToken);
        } else {
            addItemToCart(authToken, cartPk);
        }
    }

    private void createCartThenAddItem(String authToken) {
        apiService.createCart(authToken).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String newCartPk = response.body().getId();
                    sharedPreferencesManager.saveCartPk(newCartPk);
                    addItemToCart(authToken, newCartPk);
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Could not create a cart.", Toast.LENGTH_SHORT).show();
                    setLoadingState(false);
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                setLoadingState(false);
            }
        });
    }

    private void addItemToCart(String authToken, String cartPk) {
        AddItemRequest addItemRequest = new AddItemRequest(productId, quantity);
        apiService.addItemToCart(authToken, cartPk, addItemRequest).enqueue(new Callback<CartItem>() {
            @Override
            public void onResponse(Call<CartItem> call, Response<CartItem> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductDetailsActivity.this, "Added to cart.", Toast.LENGTH_SHORT).show();
                    setLoadingState(false);
                } else {
                    sharedPreferencesManager.clearCartPk();
                    createCartThenAddItem(authToken);
                }
            }

            @Override
            public void onFailure(Call<CartItem> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                setLoadingState(false);
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            binding.addToCartProgress.setVisibility(View.VISIBLE);
            binding.btnAddToCart.setEnabled(false);
        } else {
            binding.addToCartProgress.setVisibility(View.GONE);
            binding.btnAddToCart.setEnabled(true);
        }
    }
}
