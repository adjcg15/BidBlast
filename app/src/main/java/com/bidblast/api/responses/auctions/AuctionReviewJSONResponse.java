package com.bidblast.api.responses.auctions;

public class AuctionReviewJSONResponse {
    private int id;
    private String creationDate;
    private String comments;

    public AuctionReviewJSONResponse() {
    }

    public AuctionReviewJSONResponse(int id, String creationDate, String comments) {
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

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
