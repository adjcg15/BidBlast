package com.bidblast.usecases.consultcreatedauctions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bidblast.api.RequestStatus;
import com.bidblast.model.Auction;
import com.bidblast.repositories.AuctionsRepository;
import com.bidblast.repositories.IProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;

import java.util.ArrayList;
import java.util.List;

public class ConsultCreatedAuctionsViewModel extends ViewModel {
    private final MutableLiveData<List<Auction>> auctionsList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<RequestStatus> auctionsListRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> stillAuctionsLeftToLoad = new MutableLiveData<>(true);
    private final MutableLiveData<ProcessErrorCodes> consultCreatedAuctionsErrorCode = new MutableLiveData<>();
    public LiveData<Boolean> getStillAuctionsLeftToLoad() { return stillAuctionsLeftToLoad; }


    public LiveData<List<Auction>> getAuctionsList() { return auctionsList; }
    public LiveData<RequestStatus> getAuctionsListRequestStatus() {
        return auctionsListRequestStatus;
    }
    public void cleanAuctionsList() {
        stillAuctionsLeftToLoad.setValue(true);
        auctionsList.setValue(new ArrayList<>());
    }

    public LiveData<ProcessErrorCodes> getConsultCreatedAuctionsErrorCode() {
        return consultCreatedAuctionsErrorCode;
    }

    public void recoverAuctions(String searchQuery, int limit) {
        auctionsListRequestStatus.setValue(RequestStatus.LOADING);

        int totalAuctionsLoaded = auctionsList.getValue() != null
                ? auctionsList.getValue().size()
                : 0;

        new AuctionsRepository().getCreatedAuctionsList(
                searchQuery, limit, totalAuctionsLoaded,
                new IProcessStatusListener<List<Auction>>() {
                    @Override
                    public void onSuccess(List<Auction> auctions) {
                        List<Auction> currentAuctions = new ArrayList<>(auctionsList.getValue());
                        currentAuctions.addAll(auctions);
                        auctionsList.setValue(currentAuctions);

                        if (auctions.size() < limit) {
                            stillAuctionsLeftToLoad.setValue(false);
                        }

                        auctionsListRequestStatus.setValue(RequestStatus.DONE);
                    }

                    @Override
                    public void onError(ProcessErrorCodes errorCode) {
                        consultCreatedAuctionsErrorCode.setValue(errorCode);
                        auctionsListRequestStatus.setValue(RequestStatus.ERROR);
                    }
                }
        );
    }
}