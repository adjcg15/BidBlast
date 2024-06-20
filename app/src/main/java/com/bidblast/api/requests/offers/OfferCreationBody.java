package com.bidblast.api.requests.offers;

public class OfferCreationBody {
    private int auctionId;
    private float amount;

    public OfferCreationBody(int auctionId, float amount) {
        this.auctionId = auctionId;
        this.amount = amount;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int auctionId) {
        this.auctionId = auctionId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
