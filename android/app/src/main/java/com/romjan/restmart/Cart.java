package com.romjan.restmart;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cart {
    @SerializedName("id")
    private String id;
    @SerializedName("user")
    private int user;
    @SerializedName("items")
    private List<CartItem> items;
    @SerializedName("total_price")
    private double totalPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
