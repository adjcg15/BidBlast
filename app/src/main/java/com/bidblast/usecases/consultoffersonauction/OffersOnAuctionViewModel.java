package com.bidblast.usecases.consultoffersonauction;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bidblast.api.RequestStatus;
import com.bidblast.model.Auction;
import com.bidblast.model.Offer;
import com.bidblast.repositories.AuctionsRepository;
import com.bidblast.repositories.IEmptyProcessStatusListener;
import com.bidblast.repositories.IProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OffersOnAuctionViewModel extends ViewModel {
    private final MutableLiveData<Auction> auction = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> auctionRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<ProcessErrorCodes> auctionErrorCode = new MutableLiveData<>();
    private MutableLiveData<List<Offer>> offersList = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Boolean> stillOffersLeftToLoad = new MutableLiveData<>(true);
    private final MutableLiveData<RequestStatus> offersListRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> blockUserRequestStatus = new MutableLiveData<>();

    public LiveData<Auction> getAuction() { return auction; }
    public LiveData<ProcessErrorCodes> getAuctionErrorCode() {
        return auctionErrorCode;
    }

    public LiveData<RequestStatus> getAuctionRequestStatus() {
        return auctionRequestStatus;
    }
    public LiveData<Boolean> getStillOffersLeftToLoad() { return stillOffersLeftToLoad; }

    public void recoverAuction(int idAuction) {
        auctionRequestStatus.setValue(RequestStatus.LOADING);

        new AuctionsRepository().getAuctionById(
                idAuction,
                new IProcessStatusListener<Auction>() {
                    @Override
                    public void onSuccess(Auction auctions) {
                        auction.setValue(auctions);
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

    public LiveData<List<Offer>> getOffersList() { return offersList; }

    public void clearOffersList() {
        stillOffersLeftToLoad.setValue(true);
        offersList.setValue(new ArrayList<>());
    }

    public LiveData<RequestStatus> getOffersListRequestStatus() {
        return offersListRequestStatus;
    }

    public void recoverOffers(int idAuction, int limit) {
        offersListRequestStatus.setValue(RequestStatus.LOADING);
        int totalAuctionsLoaded = offersList != null
                ? offersList.getValue().size()
                : 0;

        new AuctionsRepository().getUserAuctionOffersByAuctionId(
                idAuction, limit, totalAuctionsLoaded,
                new IProcessStatusListener<List<Offer>>() {

                    @Override
                    public void onSuccess(List<Offer> offers) {
                        List<Offer> currentOffers = new ArrayList<>(Objects.requireNonNull(offersList.getValue()));
                        currentOffers.addAll(offers);
                        offersList.setValue(currentOffers);

                        if (offers.size() < limit) {
                            stillOffersLeftToLoad.setValue(false);
                        }

                        offersListRequestStatus.setValue(RequestStatus.DONE);
                    }

                    @Override
                    public void onError(ProcessErrorCodes errorCode) {
                        offersListRequestStatus.setValue(RequestStatus.ERROR);
                    }
                }
        );
    }

    public LiveData<RequestStatus> getBlockUserRequestStatus() {
        return blockUserRequestStatus;
    }

    public void blockUser(int idProfile, int idAuction) {
        blockUserRequestStatus.setValue(RequestStatus.LOADING);

        new AuctionsRepository().blockUserInAnAuctionAndDeleteHisOffers(
                idAuction, idProfile,
                new IEmptyProcessStatusListener() {
                    @Override
                    public void onSuccess() {
                        blockUserRequestStatus.setValue(RequestStatus.DONE);
                    }

                    @Override
                    public void onError(ProcessErrorCodes errorCode) {
                        blockUserRequestStatus.setValue(RequestStatus.ERROR);
                    }
                }
        );
    }
}