package com.romjan.restmart;

import com.google.gson.annotations.SerializedName;

public class HasOrderedResponse {
    @SerializedName("hasOrdered")
    private boolean hasOrdered;

    public boolean hasOrdered() {
        return hasOrdered;
    }
}
