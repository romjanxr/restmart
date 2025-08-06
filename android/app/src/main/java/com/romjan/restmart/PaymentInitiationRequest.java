package com.romjan.restmart;

import com.google.gson.annotations.SerializedName;

public class PaymentInitiationRequest {
    private double amount;
    @SerializedName("orderId")
    private String orderId;
    @SerializedName("num_items")
    private int numItems;

    public PaymentInitiationRequest(double amount, String orderId, int numItems) {
        this.amount = amount;
        this.orderId = orderId;
        this.numItems = numItems;
    }
}
