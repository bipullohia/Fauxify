package com.example.bipul.fauxify;


class MyOrders {

    private String orderId, totalItems, totalPrice, orderTime,
            orderConfirmed, orderDelivered, totalItemPrice, customerOrder, customerAddress, restName, deliveryFee;

    MyOrders(String orderId, String totalItems, String totalPrice,
             String orderTime, String orderConfirmed, String orderDelivered,
             String totalItemPrice, String customerOrder, String customerAddress, String restName, String deliveryFee) {

        this.orderId = orderId;
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
        this.orderTime = orderTime;
        this.orderConfirmed = orderConfirmed;
        this.orderDelivered = orderDelivered;
        this.customerOrder = customerOrder;
        this.totalItemPrice = totalItemPrice;
        this.customerAddress = customerAddress;
        this.restName = restName;
        this.deliveryFee = deliveryFee;
    }

    String getOrderId() {
        return orderId;
    }

    public void setOrderId(String name) {
        this.orderId = name;
    }

    String getTotalitems() {
        return totalItems;
    }

    public void setTotalitems(String name) {
        this.totalItems = name;
    }

    String getTotalprice() {
        return totalPrice;
    }

    public void setTotalprice(String name) {
        this.totalPrice = name;
    }

    String getOrdertime() {
        return orderTime;
    }

    public void setOrdertime(String name) {
        this.orderTime = name;
    }

    String getOrderconfirmed() { return orderConfirmed; }

    public void setOrderconfirmed(String name) {
        this.orderConfirmed = name;
    }

    String getOrderdelivered() {
        return orderDelivered;
    }

    public void setOrderdelivered(String name) {
        this.orderDelivered = name;
    }

    String getCustomerorder() {
        return customerOrder;
    }

    public void setCustomerorder(String name) { this.customerOrder = name; }

    String getTotalitemsprice() {
        return totalItemPrice;
    }

    public void setTotalitemsprice(String name) {
        this.totalItemPrice = name;
    }

    String getCustomeraddress() {
        return customerAddress;
    }

    public void setCustomeraddress(String name) {
        this.customerAddress = name;
    }

    String getRestName() {
        return restName;
    }

    public void setRestName(String name) {
        this.restName = name;
    }

    String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String name) {
        this.deliveryFee = name;
    }
}
