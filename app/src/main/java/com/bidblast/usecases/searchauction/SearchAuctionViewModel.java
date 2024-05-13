package com.bidblast.usecases.searchauction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bidblast.api.RequestStatus;
import com.bidblast.model.Auction;
import com.bidblast.model.AuctionCategory;
import com.bidblast.repositories.AuctionCategoriesRepository;
import com.bidblast.repositories.AuctionsRepository;
import com.bidblast.repositories.IProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;

import java.util.List;

public class SearchAuctionViewModel {
    private final MutableLiveData<List<Auction>> auctionsList = new MutableLiveData<>();
    private final MutableLiveData<List<AuctionCategory>> auctionCategoriesList = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> auctionsListRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> auctionCategoriesListRequestStatus = new MutableLiveData<>();

    public LiveData<List<Auction>> getAuctionsList() { return auctionsList; }

    public LiveData<List<AuctionCategory>> getAuctionCategoriesList() {
        return auctionCategoriesList;
    }

    public LiveData<RequestStatus> getAuctionsListRequestStatus() {
        return auctionsListRequestStatus;
    }

    public LiveData<RequestStatus> getAuctionCategoriesListRequestStatus() {
        return auctionCategoriesListRequestStatus;
    }

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
                }
            }
        );
    }

    public void recoverAuctionCategories() {
        auctionCategoriesListRequestStatus.setValue(RequestStatus.LOADING);

        new AuctionCategoriesRepository().getAuctionCategories(
            new IProcessStatusListener<List<AuctionCategory>>() {
                @Override
                public void onSuccess(List<AuctionCategory> categories) {
                    System.out.println("TOTAL DE CATEGORIAS RECUPERADAS: " + categories.size());
                    auctionCategoriesListRequestStatus.setValue(RequestStatus.DONE);
                    auctionCategoriesList.setValue(categories);
                }

                @Override
                public void onError(ProcessErrorCodes errorCode) {
                    auctionCategoriesListRequestStatus.setValue(RequestStatus.ERROR);
                }
            }
        );
    }
}
