package com.bidblast.api.responses.auctions;

import com.bidblast.api.responses.auctioncategories.AuctionCategoryJSONResponse;

import java.util.Date;
import java.util.List;

public class AuctionJSONResponse {
    private int id;
    private String title;
    private String closesAt;
    private String updatedDate;

    private List<AuctionMediaFileJSONResponse> mediaFiles;

    private AuctionAuctioneerJSONResponse auctioneer;

    private AuctionLastOfferJSONResponse lastOffer;

    private AuctionCategoryJSONResponse category;

    public AuctionJSONResponse() { }

    public AuctionJSONResponse(int id, String title, String closesAt, List<AuctionMediaFileJSONResponse> mediaFiles, AuctionAuctioneerJSONResponse auctioneer, AuctionLastOfferJSONResponse lastOffer) {
        this.id = id;
        this.title = title;
        this.closesAt = closesAt;
        this.mediaFiles = mediaFiles;
        this.auctioneer = auctioneer;
        this.lastOffer = lastOffer;
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

    public String getClosesAt() {
        return closesAt;
    }

    public void setClosesAt(String closesAt) {
        this.closesAt = closesAt;
    }

    public List<AuctionMediaFileJSONResponse> getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(List<AuctionMediaFileJSONResponse> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    public AuctionAuctioneerJSONResponse getAuctioneer() {
        return auctioneer;
    }

    public void setAuctioneer(AuctionAuctioneerJSONResponse auctioneer) {
        this.auctioneer = auctioneer;
    }

    public AuctionLastOfferJSONResponse getLastOffer() {
        return lastOffer;
    }

    public void setLastOffer(AuctionLastOfferJSONResponse lastOffer) {
        this.lastOffer = lastOffer;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public AuctionCategoryJSONResponse getCategory() {
        return category;
    }

    public void setCategory(AuctionCategoryJSONResponse category) {
        this.category = category;
    }
}
