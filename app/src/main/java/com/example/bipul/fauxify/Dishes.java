package com.example.bipul.fauxify;

/**
 * Created by Bipul Lohia on 9/6/2016.
 */
public class Dishes {
    private String dishName, dishPrice, dishId;
    Integer isVeg;

    public Dishes(String dishName, String dishPrice, String dishId, Integer isVeg) {
        this.dishName = dishName;
        this.dishPrice = dishPrice;
        this.dishId = dishId;
        this.isVeg = isVeg;
    }


    public String getDishName() {
        return dishName;
    }

    public void setDishName(String name) {
        this.dishName = name;
    }

    public String getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(String name) {
        this.dishPrice = name;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String name) {
        this.dishId = name;
    }

    public Integer getIsVeg() {
        return isVeg;
    }

    public void setIsVeg(Integer name) {
        this.isVeg = name;
    }


}

