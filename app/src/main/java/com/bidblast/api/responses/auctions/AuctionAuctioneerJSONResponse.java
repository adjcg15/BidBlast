package com.bidblast.api.responses.auctions;

public class AuctionAuctioneerJSONResponse {
    private int id;
    private String fullName;
    private String avatar;

    public AuctionAuctioneerJSONResponse() { }

    public AuctionAuctioneerJSONResponse(int id, String fullName, String avatar) {
        this.id = id;
        this.fullName = fullName;
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
