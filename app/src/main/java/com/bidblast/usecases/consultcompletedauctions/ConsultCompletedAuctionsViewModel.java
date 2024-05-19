package com.bidblast.usecases.consultcompletedauctions;

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

public class ConsultCompletedAuctionsViewModel extends ViewModel {
    private final MutableLiveData<List<Auction>> auctionsList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<RequestStatus> auctionsListRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> stillAuctionsLeftToLoad = new MutableLiveData<>(true);
    public LiveData<Boolean> getStillAuctionsLeftToLoad() { return stillAuctionsLeftToLoad; }


    public LiveData<List<Auction>> getAuctionsList() { return auctionsList; }
    public LiveData<RequestStatus> getAuctionsListRequestStatus() {
        return auctionsListRequestStatus;
    }
    public void cleanAuctionsList() {
        stillAuctionsLeftToLoad.setValue(true);
        auctionsList.setValue(new ArrayList<>());
    }

    public void recoverAuctions(int customerId, String searchQuery, int limit) {
        auctionsListRequestStatus.setValue(RequestStatus.LOADING);

        int totalAuctionsLoaded = auctionsList.getValue() != null
                ? auctionsList.getValue().size()
                : 0;

        new AuctionsRepository().getCompletedAuctionsList(
                customerId, searchQuery, limit, totalAuctionsLoaded,
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
                        auctionsListRequestStatus.setValue(RequestStatus.ERROR);
                    }
                }
        );
    }
}
