package com.romjan.restmart;

import java.io.Serializable;

public class Image implements Serializable {
    private int id;
    private String image;

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }
}
