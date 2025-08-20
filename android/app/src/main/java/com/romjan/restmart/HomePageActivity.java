package com.romjan.restmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.romjan.restmart.databinding.ActivityHomePageBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePageActivity extends AppCompatActivity implements FilterBottomSheetFragment.FilterListener {

    private ActivityHomePageBinding binding;
    private SharedPreferencesManager sharedPreferencesManager;
    private ApiService apiService;
    private ProductAdapter productAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;

    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private Integer selectedCategoryId = null;
    private Integer minPrice = null;
    private Integer maxPrice = null;
    private String searchQuery = null;
    private String ordering = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Products");
        }

        binding.toolbar.findViewById(R.id.cart_button).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, CartActivity.class);
            startActivity(intent);
        });

        sharedPreferencesManager = new SharedPreferencesManager(this);
        apiService = NetworkConfig.getApiService(this);

        drawerLayout = binding.drawerLayout;
        navigationView = binding.navView;
        bottomNavigationView = binding.bottomNavigation;

        // Apply window insets to the bottom navigation view
        bottomNavigationView.setOnApplyWindowInsetsListener((v, insets) -> {
            int bottomPadding = insets.getSystemWindowInsetBottom();
            v.setPadding(0, 0, 0, bottomPadding);
            return insets;
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        bottomNavigationView.setOnItemSelectedListener(this::onBottomNavigationItemSelected);

        setupRecyclerView();
        loadCategories();
        loadProducts(false);
        updateNavHeader();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    if (isEnabled()) {
                        setEnabled(false);
                        onBackPressed();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_page_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                loadProducts(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    searchQuery = null;
                    loadProducts(true);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            FilterBottomSheetFragment bottomSheet = FilterBottomSheetFragment.newInstance();
            bottomSheet.setFilterListener(this);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            return true;
        } else if (item.getItemId() == R.id.action_clear_filters) {
            clearFilters();
            return true;
        } else if (item.getItemId() == R.id.action_orders) {
            startActivity(new Intent(this, OrdersActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearFilters() {
        selectedCategoryId = null;
        minPrice = null;
        maxPrice = null;
        searchQuery = null;
        ordering = null;
        binding.categorySpinner.setSelection(0);
        loadProducts(true);
    }

    private void loadCategories() {
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        apiService.getCategories(authToken).enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    categories.add(0, new Category()); // For "All" option
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(HomePageActivity.this, android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.categorySpinner.setAdapter(adapter);
                    binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                selectedCategoryId = null;
                            } else {
                                selectedCategoryId = ((Category) parent.getItemAtPosition(position)).getId();
                            }
                            loadProducts(true);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter();
        binding.rvProducts.setAdapter(productAdapter);
        binding.rvProducts.setLayoutManager(new GridLayoutManager(this, 2));

        binding.rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0) {
                            currentPage++;
                            loadProducts(false);
                        }
                    }
                }
            }
        });
    }

    private void loadProducts(boolean clearPrevious) {
        if (clearPrevious) {
            currentPage = 1;
            isLastPage = false;
            productAdapter.clearProducts();
        }
        isLoading = true;
        binding.progressBar.setVisibility(View.VISIBLE);

        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        apiService.getProducts(authToken, currentPage, selectedCategoryId, minPrice, maxPrice, searchQuery, ordering)
                .enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        isLoading = false;
                        binding.progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            ProductResponse productResponse = response.body();
                            if (productResponse.getResults() != null) {
                                productAdapter.addProducts(productResponse.getResults());
                            }
                            if (productResponse.getNext() == null) {
                                isLastPage = true;
                            }
                        } else {
                            Toast.makeText(HomePageActivity.this, "Failed to load products: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        isLoading = false;
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(HomePageActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logout() {
        sharedPreferencesManager.clearLoginData();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onApplyFilters(String ordering, Integer minPrice, Integer maxPrice) {
        this.ordering = ordering;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        loadProducts(true);
    }

    private void updateNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderNameTextView = headerView.findViewById(R.id.navHeaderNameTextView);
        TextView navHeaderEmailTextView = headerView.findViewById(R.id.navHeaderEmailTextView);

        // Set initial text from SharedPreferences
        String firstName = sharedPreferencesManager.getUserFirstName();
        String lastName = sharedPreferencesManager.getUserLastName();
        String email = sharedPreferencesManager.getUserEmail();
        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            navHeaderNameTextView.setText(firstName + " " + lastName);
        }
        if (!email.isEmpty()) {
            navHeaderEmailTextView.setText(email);
        }

        // Fetch latest user data from network
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        apiService.getUser(authToken).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    // Update SharedPreferences with the latest data
                    sharedPreferencesManager.updateUserDetails(user.getFirstName(), user.getLastName(), user.getEmail());
                    // Update the UI
                    navHeaderNameTextView.setText(user.getFirstName() + " " + user.getLastName());
                    navHeaderEmailTextView.setText(user.getEmail());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Could optionally show a toast message here
            }
        });
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the home action
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_logout) {
            logout();
        }

        drawerLayout.closeDrawer(navigationView);
        return true;
    }

    private boolean onBottomNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.bottom_nav_home) {
            // Already on home
            return true;
        } else if (id == R.id.bottom_nav_orders) {
            startActivity(new Intent(this, OrdersActivity.class));
            return true;
        } else if (id == R.id.bottom_nav_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        } else if (id == R.id.bottom_nav_account) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return false;
    }
}