package com.bidblast.usecases.createauction;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bidblast.model.AuctionState;

import java.util.ArrayList;
import java.util.List;

public class CreateAuctionViewModel extends ViewModel {
    private final MutableLiveData<String> auctionTitle = new MutableLiveData<>();
    private final MutableLiveData<String> itemDescription = new MutableLiveData<>();
    private final MutableLiveData<Integer> itemStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> openingDays = new MutableLiveData<>();
    private final MutableLiveData<Double> basePrice = new MutableLiveData<>();
    private final MutableLiveData<Double> minimumBid = new MutableLiveData<>();
    private final MutableLiveData<List<Uri>> selectedImages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Uri> selectedVideo = new MutableLiveData<>();
    private final MutableLiveData<List<AuctionState>> auctionStates = new MutableLiveData<>();

    public LiveData<String> getAuctionTitle() {
        return auctionTitle;
    }

    public void setAuctionTitle(String title) {
        auctionTitle.setValue(title);
    }

    public LiveData<String> getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String description) {
        itemDescription.setValue(description);
    }

    public LiveData<Integer> getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(Integer status) {
        itemStatus.setValue(status);
    }

    public LiveData<Integer> getOpeningDays() {
        return openingDays;
    }

    public void setOpeningDays(Integer days) {
        openingDays.setValue(days);
    }

    public LiveData<Double> getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double price) {
        basePrice.setValue(price);
    }

    public LiveData<Double> getMinimumBid() {
        return minimumBid;
    }

    public void setMinimumBid(Double bid) {
        minimumBid.setValue(bid);
    }

    public LiveData<List<Uri>> getSelectedImages() {
        return selectedImages;
    }

    public void addImage(Uri imageUri) {
        List<Uri> images = new ArrayList<>(selectedImages.getValue());
        images.add(imageUri);
        selectedImages.setValue(images);
    }

    public void removeImage(Uri imageUri) {
        List<Uri> images = new ArrayList<>(selectedImages.getValue());
        images.remove(imageUri);
        selectedImages.setValue(images);
    }

    public LiveData<Uri> getSelectedVideo() {
        return selectedVideo;
    }

    public void setSelectedVideo(Uri videoUri) {
        selectedVideo.setValue(videoUri);
    }

    public LiveData<List<AuctionState>> getAuctionStates() {
        return auctionStates;
    }

    public void setAuctionStates(List<AuctionState> states) {
        auctionStates.setValue(states);
    }
}
