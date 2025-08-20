package com.romjan.restmart;

public class Review {
    private int id;
    private int rating;
    private String comment;
    private User user;
    private int product;

    public int getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public User getUser() {
        return user;
    }

    public int getProduct() {
        return product;
    }

    public static class User {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
