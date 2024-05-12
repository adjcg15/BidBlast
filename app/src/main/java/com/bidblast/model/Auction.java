package com.bidblast.model;

import java.util.Date;
import java.util.List;

public class Auction {
    private int id;
    private String title;
    private String description;
    private float basePrice;
    private float minimumBid;
    private Date approvalDate;
    private Date closesAt;
    private int daysAvailable;
    private User auctioneer;
    private List<HypermediaFile> mediaFiles;
    private Offer lastOffer;

    public Auction() { }

    public Auction(int id, String title, String description, float basePrice, float minimumBid, Date approvalDate, Date closesAt, int daysAvailable, User auctioneer, List<HypermediaFile> mediaFiles, Offer lastOffer) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.basePrice = basePrice;
        this.minimumBid = minimumBid;
        this.approvalDate = approvalDate;
        this.closesAt = closesAt;
        this.daysAvailable = daysAvailable;
        this.auctioneer = auctioneer;
        this.mediaFiles = mediaFiles;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(float basePrice) {
        this.basePrice = basePrice;
    }

    public float getMinimumBid() {
        return minimumBid;
    }

    public void setMinimumBid(float minimumBid) {
        this.minimumBid = minimumBid;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Date getClosesAt() {
        return closesAt;
    }

    public void setClosesAt(Date closesAt) {
        this.closesAt = closesAt;
    }

    public int getDaysAvailable() {
        return daysAvailable;
    }

    public void setDaysAvailable(int daysAvailable) {
        this.daysAvailable = daysAvailable;
    }

    public User getAuctioneer() {
        return auctioneer;
    }

    public void setAuctioneer(User auctioneer) {
        this.auctioneer = auctioneer;
    }

    public List<HypermediaFile> getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(List<HypermediaFile> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    public Offer getLastOffer() {
        return lastOffer;
    }

    public void setLastOffer(Offer lastOffer) {
        this.lastOffer = lastOffer;
    }
}
