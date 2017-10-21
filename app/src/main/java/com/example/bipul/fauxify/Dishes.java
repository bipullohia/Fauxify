package com.example.bipul.fauxify;


class Dishes {
    private String dishName, dishPrice, dishId;
    private Integer isVeg;

    Dishes(String dishName, String dishPrice, String dishId, Integer isVeg) {
        this.dishName = dishName;
        this.dishPrice = dishPrice;
        this.dishId = dishId;
        this.isVeg = isVeg;
    }

    String getDishName() {
        return dishName;
    }

    public void setDishName(String name) {
        this.dishName = name;
    }

    String getDishPrice() {
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

    Integer getIsVeg() {
        return isVeg;
    }

    public void setIsVeg(Integer name) {
        this.isVeg = name;
    }
}

