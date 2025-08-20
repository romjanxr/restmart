package com.romjan.restmart;

public class ChangePasswordRequest {
    private String new_password;
    private String current_password;

    public ChangePasswordRequest(String new_password, String current_password) {
        this.new_password = new_password;
        this.current_password = current_password;
    }
}
