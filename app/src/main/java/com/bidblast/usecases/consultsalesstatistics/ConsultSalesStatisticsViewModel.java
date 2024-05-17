package com.bidblast.usecases.consultsalesstatistics;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bidblast.api.RequestStatus;
import com.bidblast.model.Auction;
import com.bidblast.repositories.AuctionsRepository;
import com.bidblast.repositories.IProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;

import java.util.List;

public class ConsultSalesStatisticsViewModel extends ViewModel {
    private final MutableLiveData<List<Auction>> salesAuctionsList = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> salesAuctionsListRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<ProcessErrorCodes> salesAuctionsListErrorCode = new MutableLiveData<>();

    public LiveData<List<Auction>> getSalesAuctionsList() { return salesAuctionsList; }
    public LiveData<ProcessErrorCodes> getSalesAuctionsListErrorCode() {
        return salesAuctionsListErrorCode;
    }

    public LiveData<RequestStatus> getSalesAuctionsListRequestStatus() {
        return salesAuctionsListRequestStatus;
    }
    public void recoverSalesAuctions(int auctioneerId, String startDate, String endDate) {
        salesAuctionsListRequestStatus.setValue(RequestStatus.LOADING);

        new AuctionsRepository().getUserSalesAuctionsList(
                auctioneerId, startDate, endDate,
                new IProcessStatusListener<List<Auction>>() {
                    @Override
                    public void onSuccess(List<Auction> auctions) {
                        salesAuctionsList.setValue(auctions);
                        salesAuctionsListRequestStatus.setValue(RequestStatus.DONE);
                    }

                    @Override
                    public void onError(ProcessErrorCodes errorCode) {
                        salesAuctionsListErrorCode.setValue(errorCode);
                        salesAuctionsListRequestStatus.setValue(RequestStatus.ERROR);
                    }
                }
        );
    }
}
