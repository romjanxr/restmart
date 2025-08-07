package com.romjan.restmart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private OnCancelOrderClickListener onCancelOrderClickListener;
    private OnOrderClickListener onOrderClickListener;
    private OnPayNowClickListener onPayNowClickListener;
    private Context context;

    public OrderAdapter(Context context, List<Order> orders, OnCancelOrderClickListener onCancelOrderClickListener, OnOrderClickListener onOrderClickListener, OnPayNowClickListener onPayNowClickListener) {
        this.context = context;
        this.orders = orders;
        this.onCancelOrderClickListener = onCancelOrderClickListener;
        this.onOrderClickListener = onOrderClickListener;
        this.onPayNowClickListener = onPayNowClickListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.orderIdTextView.setText("Order #" + order.getId().substring(0, 8));
        holder.orderStatusTextView.setText(order.getStatus());
        holder.orderTotalPriceTextView.setText(String.format(Locale.getDefault(), "Total: $%.2f", order.getTotalPrice()));
        holder.orderDateTextView.setText(formatDate(order.getCreatedAt()));

        int statusBackground;
        switch (order.getStatus().toLowerCase()) {
            case "not paid":
                statusBackground = R.drawable.bg_status_not_paid;
                holder.buttonContainer.setVisibility(View.VISIBLE);
                break;
            case "ready to ship":
                statusBackground = R.drawable.bg_status_ready_to_ship;
                holder.buttonContainer.setVisibility(View.GONE);
                break;
            case "shipped":
                statusBackground = R.drawable.bg_status_shipped;
                holder.buttonContainer.setVisibility(View.GONE);
                break;
            case "delivered":
                statusBackground = R.drawable.bg_status_delivered;
                holder.buttonContainer.setVisibility(View.GONE);
                break;
            case "canceled":
                statusBackground = R.drawable.bg_status_canceled;
                holder.buttonContainer.setVisibility(View.GONE);
                break;
            default:
                statusBackground = R.drawable.bg_status_paid; // A default fallback
                holder.buttonContainer.setVisibility(View.GONE);
                break;
        }
        holder.orderStatusTextView.setBackground(ContextCompat.getDrawable(context, statusBackground));

        holder.payNowButton.setOnClickListener(v -> {
            if (onPayNowClickListener != null) {
                onPayNowClickListener.onPayNowClick(order);
            }
        });

        holder.cancelOrderButton.setOnClickListener(v -> {
            if (onCancelOrderClickListener != null) {
                onCancelOrderClickListener.onCancelOrderClick(order.getId());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onOrderClickListener != null) {
                onOrderClickListener.onOrderClick(order.getId());
            } else {
                Log.w("OrderAdapter", "onOrderClickListener is null");
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString; // Return original string if parsing fails
        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView;
        TextView orderStatusTextView;
        TextView orderTotalPriceTextView;
        TextView orderDateTextView;
        Button payNowButton;
        Button cancelOrderButton;
        View buttonContainer;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            orderStatusTextView = itemView.findViewById(R.id.orderStatusTextView);
            orderTotalPriceTextView = itemView.findViewById(R.id.orderTotalPriceTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            payNowButton = itemView.findViewById(R.id.payNowButton);
            cancelOrderButton = itemView.findViewById(R.id.cancelOrderButton);
            buttonContainer = itemView.findViewById(R.id.buttonContainer);
        }
    }

    public interface OnCancelOrderClickListener {
        void onCancelOrderClick(String orderId);
    }

    public interface OnOrderClickListener {
        void onOrderClick(String orderId);
    }

    public interface OnPayNowClickListener {
        void onPayNowClick(Order order);
    }
}

