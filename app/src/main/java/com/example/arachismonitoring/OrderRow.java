package com.example.arachismonitoring;

public class OrderRow {
    private String no;
    private String date;
    private String qty;
    private String name;
    private String surname;
    private String phone;
    private String product;
    private String address;
    private String status;

    public OrderRow(String no, String date, String qty, String name, String surname, String phone, String product, String address, String status) {
        this.no = no;
        this.date = date;
        this.qty = qty;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.product = product;
        this.address = address;
        this.status = status;
    }

    public String getNo() {
        return no;
    }

    public String getDate() {
        return date;
    }

    public String getQty() {
        return qty;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhone() {
        return phone;
    }

    public String getProduct() {
        return product;
    }

    public String getAddress() {
        return address;
    }

    public String getStatus() {
        return status;
    }
}
