package com.example.bipul.fauxify;

/**
 * Created by bipul on 27-06-2016.
 */

public class Restaurant {
    private String resName, resType, resRating, resDeliveryTime, resMinOrder, resId, deliveryFee, freeDeliveryAmount, restStatus;

    public Restaurant(String resName, String resType, String resRating, String resDeliveryTime,
                      String resMinOrder, String resId, String deliveryFee, String freeDeliveryAmount, String restStatus) {

        this.resName = resName;
        this.resType = resType;
        this.resId = resId;
        this.resRating = resRating;
        this.resDeliveryTime = resDeliveryTime;
        this.resMinOrder = resMinOrder;
        this.deliveryFee = deliveryFee;
        this.freeDeliveryAmount = freeDeliveryAmount;
        this.restStatus = restStatus;
    }

    String getResName() {
        return resName;
    }

    public void setResName(String name) {
        this.resName = name;
    }

    String getResId() {
        return resId;
    }

    public void setResId(String name) {
        this.resId = name;
    }

    String getResType() {
        return resType;
    }

    public void setResType(String year) {
        this.resType = year;
    }

    String getResRating() {
        return resRating;
    }

    public void setResRating(String genre) {
        this.resRating = genre;
    }

    String getResDeliveryTime() {
        return resDeliveryTime;
    }

    public void setResDeliveryTime(String genre) {
        this.resDeliveryTime = genre;
    }

    String getResMinOrder() {
        return resMinOrder;
    }

    public void setResMinOrder(String genre) {
        this.resMinOrder = genre;
    }

    String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String genre) {
        this.deliveryFee = genre;
    }

    String getFreeDeliveryAmount() {
        return freeDeliveryAmount;
    }

    public void setFreeDeliveryAmount(String genre) {
        this.freeDeliveryAmount = genre;
    }

    String getRestStatus() {
        return restStatus;
    }

    public void setRestStatus(String genre) {
        this.restStatus = genre;
    }
}
