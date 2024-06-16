package com.bidblast.usecases.consultaauctioncategories;

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

    public LiveData<List<AuctionCategory>> getAuctionCategories() {
        return auctionCategories;
    }

    public LiveData<ProcessErrorCodes> getError() {
        return error;
    }

    public void loadAuctionCategories() {
        AuctionCategoriesRepository repository = new AuctionCategoriesRepository();
        repository.getAuctionCategories(new IProcessStatusListener<List<AuctionCategory>>() {
            @Override
            public void onSuccess(List<AuctionCategory> categories) {
                auctionCategories.postValue(categories);
            }

            @Override
            public void onError(ProcessErrorCodes errorCode) {
                error.postValue(errorCode);
            }
        });
    }
}
