package com.bidblast.model;

public class AuctionState {
    private int id_item_condition;
    private String name;

    public AuctionState(int id_item_condition, String name) {
        this.id_item_condition = id_item_condition;
        this.name = name;
    }

    public int getId_item_condition() {
        return id_item_condition;
    }

    public void setId_item_condition(int id_item_condition) {
        this.id_item_condition = id_item_condition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}