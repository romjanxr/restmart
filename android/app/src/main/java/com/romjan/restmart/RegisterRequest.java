package com.romjan.restmart;

public class RegisterRequest {
    private String email;
    private String password;
    private String first_name;
    private String last_name;
    private String address;
    private String phone_number;

    public RegisterRequest(String email, String password, String first_name, String last_name, String address, String phone_number) {
        this.email = email;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.address = address;
        this.phone_number = phone_number;
    }
}