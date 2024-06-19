package com.bidblast.usecases.bidonauction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bidblast.api.RequestStatus;
import com.bidblast.model.Auction;
import com.bidblast.repositories.AuctionsRepository;
import com.bidblast.repositories.IProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;

public class BidOnAuctionViewModel extends ViewModel {
    private final MutableLiveData<Auction> auction = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> auctionRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<ProcessErrorCodes> auctionErrorCode = new MutableLiveData<>();
    private final MutableLiveData<Float> defaultBaseOffer = new MutableLiveData<>();
    private final MutableLiveData<Float> currentOffer = new MutableLiveData<>();

    public LiveData<Auction> getAuction() { return auction; }

    public LiveData<ProcessErrorCodes> getAuctionErrorCode() {
        return auctionErrorCode;
    }

    public LiveData<RequestStatus> getAuctionRequestStatus() {
        return auctionRequestStatus;
    }

    public LiveData<Float> getDefaultBaseOffer() { return defaultBaseOffer; }

    public void setDefaultBaseOffer(float defaultBaseOffer) {
        this.defaultBaseOffer.setValue(defaultBaseOffer);
    }

    public LiveData<Float> getCurrentOffer() { return currentOffer; }

    public void setCurrentOffer(float currentOffer) {
        this.currentOffer.setValue(currentOffer);
    }

    public void recoverAuction(int idAuction) {
        auctionRequestStatus.setValue(RequestStatus.LOADING);

        new AuctionsRepository().getAuctionById(
            idAuction,
            new IProcessStatusListener<Auction>() {
                @Override
                public void onSuccess(Auction recoveredAuction) {
                    auction.setValue(recoveredAuction);
                    auctionRequestStatus.setValue(RequestStatus.DONE);
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    auctionErrorCode.setValue(errorCode);
                    auctionRequestStatus.setValue(RequestStatus.ERROR);
                }
            }
        );
    }
}
