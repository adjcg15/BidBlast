package com.bidblast.usecases.searchauction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bidblast.api.RequestStatus;
import com.bidblast.lib.ApiFormatter;
import com.bidblast.model.Auction;
import com.bidblast.model.AuctionCategory;
import com.bidblast.model.PriceRange;
import com.bidblast.repositories.AuctionCategoriesRepository;
import com.bidblast.repositories.AuctionsRepository;
import com.bidblast.repositories.IProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SearchAuctionViewModel {
    private final MutableLiveData<List<Auction>> auctionsList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<AuctionCategory>> auctionCategoriesList =
        new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<RequestStatus> auctionsListRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> auctionCategoriesListRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<List<Integer>> temporaryCategoryFiltersSelected =
        new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Integer>> categoryFiltersSelected =
        new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<PriceRange> temporaryPriceFilterSelected =
        new MutableLiveData<>();
    private final MutableLiveData<PriceRange> priceFilterSelected =
        new MutableLiveData<>();
    private final MutableLiveData<Boolean> stillAuctionsLeftToLoad = new MutableLiveData<>(true);

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

    public LiveData<List<Integer>> getTemporaryCategoryFiltersSelected() {
        return temporaryCategoryFiltersSelected;
    }

    public LiveData<List<Integer>> getCategoryFiltersSelected() {
        return categoryFiltersSelected;
    }

    public LiveData<PriceRange> getTemporaryPriceFilterSelected() { return temporaryPriceFilterSelected; }

    public LiveData<PriceRange> getPriceFilterSelected() { return priceFilterSelected; }

    public LiveData<Boolean> getStillAuctionsLeftToLoad() { return stillAuctionsLeftToLoad; }

    public void cleanAuctionsList() {
        stillAuctionsLeftToLoad.setValue(true);
        auctionsList.setValue(new ArrayList<>());
    }

    public void recoverAuctions(String searchQuery, int limit) {
        auctionsListRequestStatus.setValue(RequestStatus.LOADING);

        int totalAuctionsLoaded = auctionsList.getValue() != null
            ? auctionsList.getValue().size()
            : 0;

        int minimumPrice = 0, maximumPrice = Integer.MAX_VALUE;
        PriceRange priceFilter = priceFilterSelected.getValue();
        if(priceFilter != null) {
            if(priceFilter.getMinimumAmount() != Float.NEGATIVE_INFINITY) {
                minimumPrice = (int)priceFilter.getMinimumAmount();
            }

            if(priceFilter.getMaximumAmount() != Float.POSITIVE_INFINITY) {
                maximumPrice = (int)priceFilter.getMaximumAmount();
            }
        }

        new AuctionsRepository().getAuctionsList(
            searchQuery, limit, totalAuctionsLoaded,
            ApiFormatter.parseToPlainMultiValueParam(categoryFiltersSelected.getValue()),
            minimumPrice, maximumPrice,
            new IProcessStatusListener<List<Auction>>() {
                @Override
                public void onSuccess(List<Auction> auctions) {
                    List<Auction> currentAuctions = new ArrayList<>(auctionsList.getValue());
                    currentAuctions.addAll(auctions);
                    auctionsList.setValue(currentAuctions);

                    if(auctions.size() < limit) {
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

    public List<PriceRange> getAllPriceRanges() {
        return new ArrayList<>(Arrays.asList(
            new PriceRange("Menos de $100", Float.NEGATIVE_INFINITY, 100.0f),
            new PriceRange("$100 a menos de $200", 100.0f, 200.0f),
            new PriceRange("$200 a menos de $300", 200.0f, 300.0f),
            new PriceRange("$300 a menos de $500", 300.0f, 500.0f),
            new PriceRange("$500 a menos de $750", 500.0f, 750.0f),
            new PriceRange("$750 a menos de $1000", 750.0f, 1000.0f),
            new PriceRange("$1000 o más", 1000.0f, Float.POSITIVE_INFINITY)
        ));
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
        temporaryCategoryFiltersSelected.setValue(currentFilters);
    }

    public void toggleTemporaryCategoryFilter(AuctionCategory category) {
        Integer categoryId = category.getId();

        List<Integer> currentFilters = new ArrayList<>(Objects.requireNonNull(temporaryCategoryFiltersSelected.getValue()));
        if(Objects.requireNonNull(temporaryCategoryFiltersSelected.getValue()).contains(categoryId)) {
            currentFilters.remove(categoryId);
        } else {
            currentFilters.add(categoryId);
        }

        temporaryCategoryFiltersSelected.setValue(currentFilters);
    }

    public void toggleTemporaryPriceFilter(PriceRange priceRange) {
        if(priceRange.equals(temporaryPriceFilterSelected.getValue())) {
            temporaryPriceFilterSelected.setValue(null);
        } else {
            temporaryPriceFilterSelected.setValue(priceRange);
        }
    }

    public void discardTemporaryFilters() {
        temporaryCategoryFiltersSelected.setValue(categoryFiltersSelected.getValue());
        temporaryPriceFilterSelected.setValue(priceFilterSelected.getValue());
    }

    public void saveTemporaryFilters() {
        categoryFiltersSelected.setValue(temporaryCategoryFiltersSelected.getValue());
        priceFilterSelected.setValue(temporaryPriceFilterSelected.getValue());
    }
}
