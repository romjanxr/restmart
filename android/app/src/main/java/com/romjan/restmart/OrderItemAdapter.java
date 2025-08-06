package com.romjan.restmart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private List<OrderItem> items;
    private ApiService apiService;
    private SharedPreferencesManager sharedPreferencesManager;

    public OrderItemAdapter(List<OrderItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_product, parent, false);
        apiService = NetworkConfig.getApiService();
        sharedPreferencesManager = new SharedPreferencesManager(parent.getContext());
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = items.get(position);
        holder.productNameTextView.setText(item.getProduct().getName());
        holder.quantityTextView.setText(String.format(Locale.getDefault(), "Qty: %d", item.getQuantity()));
        holder.priceTextView.setText(String.format(Locale.getDefault(), "$%.2f", item.getPrice()));

        // Fetch full product details to get the image
        fetchProductDetails(item.getProduct().getId(), holder);
    }

    private void fetchProductDetails(int productId, @NonNull OrderItemViewHolder holder) {
        String authToken = "Bearer " + sharedPreferencesManager.getAuthToken();
        apiService.getProductDetails(authToken, productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();
                    if (product.getImages() != null && !product.getImages().isEmpty()) {
                        Glide.with(holder.itemView.getContext())
                                .load(product.getImages().get(0).getImage())
                                .placeholder(R.drawable.ic_placeholder)
                                .into(holder.productImageView);
                    } else {
                        holder.productImageView.setImageResource(R.drawable.ic_placeholder);
                    }
                } else {
                    holder.productImageView.setImageResource(R.drawable.ic_placeholder);
                    Toast.makeText(holder.itemView.getContext(), "Failed to load product image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                holder.productImageView.setImageResource(R.drawable.ic_placeholder);
                Toast.makeText(holder.itemView.getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView quantityTextView;
        TextView priceTextView;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }
}
