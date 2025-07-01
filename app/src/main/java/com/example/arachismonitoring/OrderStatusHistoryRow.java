package com.example.arachismonitoring;

import java.io.Serializable;

public class OrderStatusHistoryRow implements Serializable {
    private String orderNumber;
    private String orderDetails;
    private String dateTime;
    private String previousStatus;
    private String newStatus;
    private String note;
    private String user;

    public OrderStatusHistoryRow(String orderNumber, String orderDetails, String dateTime, String previousStatus, String newStatus, String note, String user) {
        this.orderNumber = orderNumber;
        this.orderDetails = orderDetails;
        this.dateTime = dateTime;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.note = note;
        this.user = user;
    }

    public String getOrderNumber() { return orderNumber; }
    public String getOrderDetails() { return orderDetails; }
    public String getDateTime() { return dateTime; }
    public String getPreviousStatus() { return previousStatus; }
    public String getNewStatus() { return newStatus; }
    public String getNote() { return note; }
    public String getUser() { return user; }
} 