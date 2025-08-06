package com.romjan.restmart;

public class CartItem {
    private int id;
    private ProductInCart product;
    private int quantity;
    private double total_price;
    private boolean isUpdating = false;

    // Copy constructor
    public CartItem(CartItem original) {
        this.id = original.id;
        this.product = original.product;
        this.quantity = original.quantity;
        this.total_price = original.total_price;
        this.isUpdating = original.isUpdating;
    }

    public int getId() {
        return id;
    }

    public ProductInCart getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return total_price;
    }

    public boolean isUpdating() {
        return isUpdating;
    }

    public void setUpdating(boolean updating) {
        isUpdating = updating;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTotalPrice(double total_price) {
        this.total_price = total_price;
    }

    public void setProduct(ProductInCart product) {
        this.product = product;
    }
}
