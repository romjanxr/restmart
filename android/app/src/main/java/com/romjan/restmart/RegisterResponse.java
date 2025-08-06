package com.romjan.restmart;

public class RegisterResponse {
    private int id;
    private String email;
    private String first_name;
    private String last_name;
    private String address;
    private String phone_number;

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone_number() {
        return phone_number;
    }
}