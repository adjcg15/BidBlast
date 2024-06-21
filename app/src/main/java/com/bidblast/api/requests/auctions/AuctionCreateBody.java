package com.bidblast.api.requests.auctions;

import com.bidblast.model.HypermediaFile;

import java.util.List;

public class AuctionCreateBody {
    private String title;
    private String description;
    private double basePrice;
    private Double minimumBid;
    private int daysAvailable;
    private int idItemCondition;
    private List<HypermediaFile> mediaFiles;

    public AuctionCreateBody(String title, String description, double basePrice, Double minimumBid, int daysAvailable, int idItemCondition, List<HypermediaFile> mediaFiles) {
        this.title = title;
        this.description = description;
        this.basePrice = basePrice;
        this.minimumBid = minimumBid;
        this.daysAvailable = daysAvailable;
        this.idItemCondition = idItemCondition;
        this.mediaFiles = mediaFiles;
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

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public Double getMinimumBid() {
        return minimumBid;
    }

    public void setMinimumBid(Double minimumBid) {
        this.minimumBid = minimumBid;
    }

    public int getDaysAvailable() {
        return daysAvailable;
    }

    public void setDaysAvailable(int daysAvailable) {
        this.daysAvailable = daysAvailable;
    }

    public int getIdItemCondition() {
        return idItemCondition;
    }

    public void setIdItemCondition(int idItemCondition) {
        this.idItemCondition = idItemCondition;
    }

    public List<HypermediaFile> getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(List<HypermediaFile> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    @Override
    public String toString() {
        return "AuctionCreateBody{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", basePrice=" + basePrice +
                ", minimumBid=" + minimumBid +
                ", daysAvailable=" + daysAvailable +
                ", idItemCondition=" + idItemCondition +
                ", mediaFiles=" + mediaFiles +
                '}';
    }
}