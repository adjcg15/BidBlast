package com.bidblast.api.requests.auctions;

public class AuctionApprovalBody {
    private int idAuction;
    private int idAuctionCategory;

    public AuctionApprovalBody(int idAuction, int idAuctionCategory) {
        this.idAuction = idAuction;
        this.idAuctionCategory = idAuctionCategory;
    }

    public int getIdAuction() {
        return idAuction;
    }

    public void setIdAuction(int idAuction) {
        this.idAuction = idAuction;
    }

    public int getIdAuctionCategory() {
        return idAuctionCategory;
    }

    public void setIdAuctionCategory(int idAuctionCategory) {
        this.idAuctionCategory = idAuctionCategory;
    }
}

