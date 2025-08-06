package com.romjan.restmart;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private int id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private int category;
    private double price_with_tax;
    private List<Image> images;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public int getCategory() {
        return category;
    }

    public double getPriceWithTax() {
        return price_with_tax;
    }

    public List<Image> getImages() {
        return images;
    }
}
