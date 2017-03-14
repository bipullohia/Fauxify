package com.example.bipul.fauxify;

/**
 * Created by Bipul Lohia on 9/27/2016.
 */

public class MyOrders {
    public String orderId, totalitems, totalprice, ordertime,
            orderconfirmed, orderdelivered, totalitemsprice, customerorder, customeraddress, restName, deliveryFee;

    public MyOrders(String orderId, String totalitems, String totalprice,
                    String ordertime, String orderconfirmed, String orderdelivered,
                    String totalitemsprice, String customerorder, String customeraddress, String restName, String deliveryFee) {
        this.orderId = orderId;
        this.totalitems = totalitems;
        this.totalprice = totalprice;
        this.ordertime = ordertime;
        this.orderconfirmed = orderconfirmed;
        this.orderdelivered = orderdelivered;
        this.customerorder = customerorder;
        this.totalitemsprice = totalitemsprice;
        this.customeraddress = customeraddress;
        this.restName = restName;
        this.deliveryFee = deliveryFee;

    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String name) {
        this.orderId = name;
    }

    public String getTotalitems() {
        return totalitems;
    }

    public void setTotalitems(String name) {
        this.totalitems = name;
    }

    public String getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(String name) {
        this.totalprice = name;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String name) {
        this.ordertime = name;
    }

    public String getOrderconfirmed() {
        return orderconfirmed;
    }

    public void setOrderconfirmed(String name) {
        this.orderconfirmed = name;
    }

    public String getOrderdelivered() {
        return orderdelivered;
    }

    public void setOrderdelivered(String name) {
        this.orderdelivered = name;
    }

    public String getCustomerorder() {
        return customerorder;
    }

    public void setCustomerorder(String name) {
        this.customerorder = name;
    }

    public String getTotalitemsprice() {
        return totalitemsprice;
    }

    public void setTotalitemsprice(String name) {
        this.totalitemsprice = name;
    }

    public String getCustomeraddress() {
        return customeraddress;
    }

    public void setCustomeraddress(String name) {
        this.customeraddress = name;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String name) {
        this.restName = name;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String name) {
        this.deliveryFee = name;
    }

}
