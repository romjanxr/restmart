package com.romjan.restmart;

public class Category {
    private int id;
    private String name;
    private String description;
    private int products_count;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getProductsCount() {
        return products_count;
    }

    @Override
    public String toString() {
        if (name == null) {
            return "All Categories";
        }
        return name;
    }
}
