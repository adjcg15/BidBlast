package com.bidblast.model;

import java.util.Date;
import java.util.Objects;

public class AuctionReview {
    private int id;
    private Date creationDate;
    private String comments;

    public AuctionReview() {
    }

    public AuctionReview(int id, Date creationDate, String comments) {
        this.id = id;
        this.creationDate = creationDate;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuctionReview that = (AuctionReview) o;
        return id == that.id && Objects.equals(creationDate, that.creationDate) && Objects.equals(comments, that.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationDate, comments);
    }
}
