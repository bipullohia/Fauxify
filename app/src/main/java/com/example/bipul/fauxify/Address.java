package com.example.bipul.fauxify;


public class Address {
    private String userAddress;

    public Address(String userAddress) {
        this.userAddress = userAddress;
    }

    String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String name) {
        this.userAddress = name;
    }
}
