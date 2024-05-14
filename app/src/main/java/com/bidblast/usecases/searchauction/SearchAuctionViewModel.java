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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchAuctionViewModel {
    private final MutableLiveData<List<Auction>> auctionsList = new MutableLiveData<>();
    private final MutableLiveData<List<AuctionCategory>> auctionCategoriesList =
        new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<RequestStatus> auctionsListRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> auctionCategoriesListRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<List<Integer>> categoryFiltersSelected =
        new MutableLiveData<>(new ArrayList<>());

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

    public LiveData<List<Integer>> getCategoryFiltersSelected() {
        return categoryFiltersSelected;
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

    public void toggleCategoryFilter(AuctionCategory category) {
        Integer categoryId = category.getId();

        List<Integer> currentFilters = new ArrayList<>(Objects.requireNonNull(categoryFiltersSelected.getValue()));
        if(Objects.requireNonNull(categoryFiltersSelected.getValue()).contains(categoryId)) {
            currentFilters.remove(categoryId);
        } else {
            currentFilters.add(categoryId);
        }

        categoryFiltersSelected.setValue(currentFilters);
    }
}
