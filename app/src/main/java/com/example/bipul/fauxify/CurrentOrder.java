package com.example.bipul.fauxify;

/**
 * Created by Bipul Lohia on 9/7/2016.
 */

class CurrentOrder {
    String currentdishName, currentdishPrice; Integer currentdishQuantity;

    CurrentOrder(String currentdishName, String currentdishPrice, Integer currentdishQuantity) {
        this.currentdishName = currentdishName;
        this.currentdishPrice = currentdishPrice;
        this.currentdishQuantity = currentdishQuantity;
    }

    String getCurrentdishName() {
        return currentdishName;
    }

    public void setCurrentdishName(String name) { this.currentdishName = name; }

    String getCurrentdishPrice() {
        return currentdishPrice;
    }

    public void setCurrentdishPrice(String name) {
        this.currentdishPrice = name;
    }

    Integer getCurrentdishQuantity() {
        return currentdishQuantity;
    }

    public void setCurrentdishQuantity(Integer name) {
        this.currentdishQuantity = name;
    }
}
