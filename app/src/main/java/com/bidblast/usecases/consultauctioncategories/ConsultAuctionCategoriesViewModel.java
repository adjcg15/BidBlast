package com.bidblast.usecases.consultauctioncategories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bidblast.model.AuctionCategory;
import com.bidblast.repositories.AuctionCategoriesRepository;
import com.bidblast.repositories.IProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;

import java.util.List;
public class ConsultAuctionCategoriesViewModel extends ViewModel {
    private final MutableLiveData<List<AuctionCategory>> auctionCategories = new MutableLiveData<>();
    private final MutableLiveData<ProcessErrorCodes> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEmpty = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<List<AuctionCategory>> getAuctionCategories() {
        return auctionCategories;
    }

    public LiveData<ProcessErrorCodes> getError() {
        return error;
    }

    public LiveData<Boolean> getIsEmpty() {
        return isEmpty;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadAuctionCategories() {
        isLoading.setValue(true);
        AuctionCategoriesRepository repository = new AuctionCategoriesRepository();
        repository.getAuctionCategories(new IProcessStatusListener<List<AuctionCategory>>() {
            @Override
            public void onSuccess(List<AuctionCategory> categories) {
                isLoading.setValue(false);
                auctionCategories.postValue(categories);
                isEmpty.postValue(categories.isEmpty());
                error.postValue(null);
            }

            @Override
            public void onError(ProcessErrorCodes errorCode) {
                isLoading.setValue(false);
                error.postValue(errorCode);
                isEmpty.postValue(true);
            }
        });
    }

    public void searchAuctionCategories(String query) {
        isLoading.setValue(true);
        AuctionCategoriesRepository repository = new AuctionCategoriesRepository();
        repository.searchAuctionCategories(query, new IProcessStatusListener<List<AuctionCategory>>() {
            @Override
            public void onSuccess(List<AuctionCategory> categories) {
                isLoading.setValue(false);
                auctionCategories.postValue(categories);
                isEmpty.postValue(categories.isEmpty());
                error.postValue(null);
            }

            @Override
            public void onError(ProcessErrorCodes errorCode) {
                isLoading.setValue(false);
                error.postValue(errorCode);
                isEmpty.postValue(true);
            }
        });
    }
}
