package com.bidblast.usecases.postitemforauction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PostItemForAuctionViewModel extends ViewModel {
    private final MutableLiveData<String> auctionTitle = new MutableLiveData<>();
    private final MutableLiveData<String> itemDescription = new MutableLiveData<>();
    private final MutableLiveData<Integer> itemStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> openingDays = new MutableLiveData<>();
    private final MutableLiveData<Integer> minimumBid = new MutableLiveData<>();

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

    public LiveData<Integer> getMinimumBid() {
        return minimumBid;
    }

    public void setMinimumBid(Integer bid) {
        minimumBid.setValue(bid);
    }
}

