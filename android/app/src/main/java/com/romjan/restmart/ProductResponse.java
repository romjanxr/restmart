package com.romjan.restmart;

import java.util.List;

public class ProductResponse {
    private int count;
    private String next;
    private String previous;
    private List<Product> results;

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public List<Product> getResults() {
        return results;
    }
}