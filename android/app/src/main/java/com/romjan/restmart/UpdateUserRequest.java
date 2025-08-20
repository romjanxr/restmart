package com.romjan.restmart;

public class UpdateUserRequest {
    private String first_name;
    private String last_name;
    private String address;
    private String phone_number;

    public UpdateUserRequest(String first_name, String last_name, String address, String phone_number) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.address = address;
        this.phone_number = phone_number;
    }
}
