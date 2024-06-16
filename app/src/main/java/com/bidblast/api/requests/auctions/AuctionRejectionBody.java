package com.bidblast.api.requests.auctions;

public class AuctionRejectionBody {
    private int idAuction;
    private String comments;

    public AuctionRejectionBody(int idAuction, String comments) {
        this.idAuction = idAuction;
        this.comments = comments;
    }

    public int getIdAuction() {
        return idAuction;
    }

    public void setIdAuction(int idAuction) {
        this.idAuction = idAuction;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}