package com.bidblast.model;

import androidx.annotation.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    private AuctionCategory category;
    private String state;
    private Date updatedDate;
    private String auctionState;
    private AuctionReview review;

    public Auction() { }

    public Auction(int id, String title, String description, float basePrice, float minimumBid, Date approvalDate, Date closesAt, int daysAvailable, User auctioneer, List<HypermediaFile> mediaFiles, Offer lastOffer, AuctionCategory category, String state, Date updatedDate) {
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
        this.category = category;
        this.state = state;
        this.updatedDate = updatedDate;
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

    public AuctionCategory getCategory() {
        return category;
    }

    public void setCategory(AuctionCategory category){
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getAuctionState() {
        return auctionState;
    }

    public void setAuctionState(String auctionState) {
        this.auctionState = auctionState;
    }

    public AuctionReview getReview() {
        return review;
    }

    public void setReview(AuctionReview review) {
        this.review = review;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Auction auction = (Auction) o;
        return id == auction.id && Float.compare(auction.basePrice, basePrice) == 0 && Float.compare(auction.minimumBid, minimumBid) == 0 && daysAvailable == auction.daysAvailable && Objects.equals(title, auction.title) && Objects.equals(description, auction.description) && Objects.equals(approvalDate, auction.approvalDate) && Objects.equals(closesAt, auction.closesAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, basePrice, minimumBid, approvalDate, closesAt, daysAvailable);
    }
}
