package com.example.bipul.fauxify;

/**
 * Created by Bipul Lohia on 8/31/2016.
 **/

public class Address {
    private String userAddress;

    public Address(String userAddress) {
        this.userAddress = userAddress;
    }


    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String name) {
        this.userAddress = name;
    }
}
