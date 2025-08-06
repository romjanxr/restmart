package com.romjan.restmart;

import com.google.gson.annotations.SerializedName;

public class CreateOrderRequest {
    @SerializedName("cart_id")
    private String cartId;

    public CreateOrderRequest(String cartId) {
        this.cartId = cartId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }
}
