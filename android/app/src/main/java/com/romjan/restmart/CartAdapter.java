package com.romjan.restmart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems = new ArrayList<>();
    private final OnItemActionListener listener;

    public interface OnItemActionListener {
        void onQuantityChange(CartItem item, int newQuantity);
        void onDeleteItem(CartItem item);
    }

    public CartAdapter(OnItemActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView itemQuantityTextView;
        MaterialButton decreaseQuantityButton;
        MaterialButton increaseQuantityButton;
        ImageButton deleteButton;
        ProgressBar quantityProgressBar;
        LinearLayout quantityControlLayout;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            itemQuantityTextView = itemView.findViewById(R.id.itemQuantityTextView);
            decreaseQuantityButton = itemView.findViewById(R.id.decreaseQuantityButton);
            increaseQuantityButton = itemView.findViewById(R.id.increaseQuantityButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            quantityProgressBar = itemView.findViewById(R.id.quantityProgressBar);
            quantityControlLayout = itemView.findViewById(R.id.quantityControlLayout);
        }

        public void bind(CartItem item, OnItemActionListener listener) {
            productNameTextView.setText(item.getProduct().getName());
            productPriceTextView.setText(String.format(Locale.getDefault(), "Price: $%.2f", item.getProduct().getPrice()));
            itemQuantityTextView.setText(String.valueOf(item.getQuantity()));

            setLoading(item.isUpdating());

            decreaseQuantityButton.setOnClickListener(v -> listener.onQuantityChange(item, item.getQuantity() - 1));
            increaseQuantityButton.setOnClickListener(v -> listener.onQuantityChange(item, item.getQuantity() + 1));
            deleteButton.setOnClickListener(v -> listener.onDeleteItem(item));
        }

        void setLoading(boolean isLoading) {
            quantityProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            quantityControlLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            deleteButton.setEnabled(!isLoading);
            decreaseQuantityButton.setEnabled(!isLoading);
            increaseQuantityButton.setEnabled(!isLoading);
        }
    }
}