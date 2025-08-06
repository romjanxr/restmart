package com.romjan.restmart;

import com.google.gson.annotations.SerializedName;

public class PaymentInitiationResponse {
    @SerializedName("payment_url")
    private String paymentUrl;

    public String getPaymentUrl() {
        return paymentUrl;
    }
}
