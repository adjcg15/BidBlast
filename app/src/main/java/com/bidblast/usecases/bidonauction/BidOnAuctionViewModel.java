package com.bidblast.usecases.bidonauction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bidblast.api.RequestStatus;
import com.bidblast.api.requests.offers.OfferCreationBody;
import com.bidblast.model.Auction;
import com.bidblast.repositories.AuctionsRepository;
import com.bidblast.repositories.IEmptyProcessWithBusinessErrorListener;
import com.bidblast.repositories.IProcessStatusListener;
import com.bidblast.repositories.OffersRepository;
import com.bidblast.repositories.ProcessErrorCodes;
import com.bidblast.repositories.businesserrors.CreateOfferCodes;

public class BidOnAuctionViewModel extends ViewModel {
    private final MutableLiveData<Auction> auction = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> auctionRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<ProcessErrorCodes> auctionErrorCode = new MutableLiveData<>();
    private final MutableLiveData<Float> defaultBaseBid = new MutableLiveData<>(0f);
    private final MutableLiveData<Float> currentBid = new MutableLiveData<>(0f);
    private final MutableLiveData<Boolean> isCreatingCustomOffer = new MutableLiveData<>(false);
    private final MutableLiveData<RequestStatus> offerRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<CreateOfferCodes> offerRequestError = new MutableLiveData<>();

    public LiveData<Auction> getAuction() { return auction; }

    public LiveData<ProcessErrorCodes> getAuctionErrorCode() {
        return auctionErrorCode;
    }

    public LiveData<RequestStatus> getAuctionRequestStatus() {
        return auctionRequestStatus;
    }

    public LiveData<RequestStatus> getOfferRequestStatus() { return offerRequestStatus; }

    public LiveData<CreateOfferCodes> getOfferRequestError() { return offerRequestError; }

    public LiveData<Float> getDefaultBaseBid() { return defaultBaseBid; }

    public void setDefaultBaseBid(float defaultBaseBid) {
        this.defaultBaseBid.setValue(defaultBaseBid);
    }

    public LiveData<Float> getCurrentBid() { return currentBid; }

    public void setCurrentBid(float currentBid) {
        this.currentBid.setValue(currentBid);
    }

    public LiveData<Boolean> getIsCreatingCustomOffer() { return isCreatingCustomOffer; }

    public void startCustomOffer() {
        this.currentBid.setValue(0f);
        this.isCreatingCustomOffer.setValue(true);
    }

    public void startDefaultOffer() {
        this.currentBid.setValue(0f);
        this.isCreatingCustomOffer.setValue(false);
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

    public void makeOffer() {
        if(auction.getValue() != null) {
            offerRequestStatus.setValue(RequestStatus.LOADING);

            int idAuction = auction.getValue().getId();
            float lastAuctionPrice = auction.getValue().getLastOffer() != null
                ? auction.getValue().getLastOffer().getAmount()
                : auction.getValue().getBasePrice();
            float proposedBid = currentBid.getValue();
            float newOffer = lastAuctionPrice + proposedBid;

            new OffersRepository().createOffer(
                new OfferCreationBody(idAuction, newOffer),
                new IEmptyProcessWithBusinessErrorListener<CreateOfferCodes>() {
                    @Override
                    public void onSuccess() {
                        offerRequestStatus.setValue(RequestStatus.DONE);
                    }

                    @Override
                    public void onError(CreateOfferCodes errorCode) {
                        offerRequestError.setValue(errorCode);
                        offerRequestStatus.setValue(RequestStatus.ERROR);
                    }
                }
            );
        }
    }
}
