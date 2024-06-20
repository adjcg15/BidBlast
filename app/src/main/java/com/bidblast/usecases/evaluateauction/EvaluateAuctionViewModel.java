package com.bidblast.usecases.evaluateauction;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bidblast.api.RequestStatus;
import com.bidblast.model.Auction;
import com.bidblast.model.AuctionCategory;
import com.bidblast.model.HypermediaFile;
import com.bidblast.repositories.AuctionCategoriesRepository;
import com.bidblast.repositories.AuctionsRepository;
import com.bidblast.repositories.IProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;

import java.util.ArrayList;
import java.util.List;

public class EvaluateAuctionViewModel extends ViewModel {
    private final MutableLiveData<List<Auction>> auctionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> auctionRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<Auction> auctionLiveData = new MutableLiveData<>();
    private final MutableLiveData<ProcessErrorCodes> auctionErrorCode = new MutableLiveData<>();
    private final MutableLiveData<List<AuctionCategory>> auctionCategoriesLiveData = new MutableLiveData<>();
    private final AuctionsRepository auctionsRepository = new AuctionsRepository();
    private final AuctionCategoriesRepository auctionCategoriesRepository = new AuctionCategoriesRepository();
    private final MutableLiveData<Boolean> auctionErrorLiveData = new MutableLiveData<>();
    private static final String TAG = "EvaluateAuctionViewModel";

    public LiveData<RequestStatus> getAuctionRequestStatus() {
        return auctionRequestStatus;
    }

    public LiveData<Auction> getAuction() {
        return auctionLiveData;
    }

    public LiveData<ProcessErrorCodes> getAuctionErrorCode() {
        return auctionErrorCode;
    }

    public LiveData<List<AuctionCategory>> getAuctionCategories() {
        return auctionCategoriesLiveData;
    }
    public LiveData<List<Auction>> getAuctionsLiveData() {
        return auctionsLiveData;
    }

    public LiveData<Boolean> getAuctionErrorLiveData() {
        return auctionErrorLiveData;
    }
    public void recoverAuctionCategories() {
        Log.d(TAG, "Recovering auction categories");
        auctionCategoriesRepository.getAuctionCategories(new IProcessStatusListener<List<AuctionCategory>>() {
            @Override
            public void onSuccess(List<AuctionCategory> categories) {
                auctionCategoriesLiveData.postValue(categories);
                Log.d(TAG, "Auction categories recovered: " + categories.toString());
            }

            @Override
            public void onError(ProcessErrorCodes errorCode) {
                Log.d(TAG, "Error recovering auction categories: " + errorCode.toString());
            }
        });
    }

    public void recoverAllAuctions() {
        auctionsRepository.getPublishedAuctions(new IProcessStatusListener<List<Auction>>() {
            @Override
            public void onSuccess(List<Auction> auctions) {
                auctionsLiveData.setValue(auctions);
            }

            @Override
            public void onError(ProcessErrorCodes errorCode) {
                auctionErrorLiveData.setValue(true);
            }
        });
    }
}
