package com.bidblast.model;

import java.util.Objects;

public class AuctionCategory {
    private int id;

    private String title;

    private String description;
    private String keyWords;

    public AuctionCategory() {}

    public AuctionCategory(int id, String title, String description, String keyWords) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.keyWords = keyWords;
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

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuctionCategory that = (AuctionCategory) o;
        return id == that.id && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(keyWords, that.keyWords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, keyWords);
    }
}
