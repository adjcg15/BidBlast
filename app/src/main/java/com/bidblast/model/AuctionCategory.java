package com.bidblast.model;

public class AuctionCategory {
    private int id;
    private String title;
    private String description;
    private String keywords;

    public AuctionCategory() { }

    public AuctionCategory(int id, String title, String description, String keywords) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
