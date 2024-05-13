package com.bidblast.usecases.searchauction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bidblast.api.RequestStatus;
import com.bidblast.model.Auction;
import com.bidblast.repositories.AuctionsRepository;
import com.bidblast.repositories.IProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;

import java.util.List;

public class SearchAuctionViewModel {
    private final MutableLiveData<List<Auction>> auctionsList = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> auctionsListRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<ProcessErrorCodes> auctionsListErrorCode = new MutableLiveData<>();

    public LiveData<List<Auction>> getAuctionsList() { return auctionsList; }

    public LiveData<RequestStatus> getAuctionsListRequestStatus() { return auctionsListRequestStatus; }

    public LiveData<ProcessErrorCodes> getAuctionsListErrorCode() { return auctionsListErrorCode; }

    public void recoverAuctions(String searchQuery, int limit, int offset) {
        auctionsListRequestStatus.setValue(RequestStatus.LOADING);

        new AuctionsRepository().getAuctionsList(
            searchQuery, limit, offset,
            new IProcessStatusListener<List<Auction>>() {
                @Override
                public void onSuccess(List<Auction> auctions) {
                    auctionsList.setValue(auctions);
                    auctionsListRequestStatus.setValue(RequestStatus.DONE);
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    auctionsListRequestStatus.setValue(RequestStatus.ERROR);
                    auctionsListErrorCode.setValue(errorCode);
                }
            }
        );
    }
}
