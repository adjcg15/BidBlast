package com.bidblast.model;

import java.util.Date;

public class Offer {
    private int id;
    private float amount;
    private Date creationDate;
    private User customer;

    public Offer() { }

    public Offer(int id, float amount, Date creationDate, User customer) {
        this.id = id;
        this.amount = amount;
        this.creationDate = creationDate;
        this.customer = customer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }
}
