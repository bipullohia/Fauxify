package com.example.bipul.fauxify;

/**
 * Created by Bipul Lohia on 9/7/2016.
 */
public class CurrentOrder {
    public String currentdishName, currentdishPrice; Integer currentdishQuantity;

    public CurrentOrder(String currentdishName, String currentdishPrice, Integer currentdishQuantity) {
        this.currentdishName = currentdishName;
        this.currentdishPrice = currentdishPrice;
        this.currentdishQuantity = currentdishQuantity;
    }

    public String getCurrentdishName() {
        return currentdishName;
    }

    public void setCurrentdishName(String name) { this.currentdishName = name; }

    public String getCurrentdishPrice() {
        return currentdishPrice;
    }

    public void setCurrentdishPrice(String name) {
        this.currentdishPrice = name;
    }

    public Integer getCurrentdishQuantity() {
        return currentdishQuantity;
    }

    public void setCurrentdishQuantity(Integer name) {
        this.currentdishQuantity = name;
    }

}
