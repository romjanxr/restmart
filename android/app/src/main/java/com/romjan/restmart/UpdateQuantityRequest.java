package com.romjan.restmart;

import com.google.gson.annotations.SerializedName;

public class UpdateQuantityRequest {
    @SerializedName("quantity")
    private int quantity;

    public UpdateQuantityRequest(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
