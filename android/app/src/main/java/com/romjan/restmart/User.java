package com.romjan.restmart;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String email;
    private String first_name;
    private String last_name;
    private String address;
    private String phone_number;
    private boolean is_staff;

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public boolean isStaff() {
        return is_staff;
    }
}