package com.bidblast.api.responses.auctions;

import java.util.Date;

public class AuctionLastOfferJSONResponse {
    private int id;
    private float amount;
    private Date creationDate;

    public AuctionLastOfferJSONResponse() { }

    public AuctionLastOfferJSONResponse(int id, float amount, Date creationDate) {
        this.id = id;
        this.amount = amount;
        this.creationDate = creationDate;
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
}
