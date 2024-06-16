package com.bidblast.usecases.registerandmodifycategory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bidblast.api.RequestStatus;
import com.bidblast.api.requests.auctioncategory.AuctionCategoryBody;
import com.bidblast.lib.ValidationToolkit;
import com.bidblast.model.AuctionCategory;
import com.bidblast.repositories.AuctionCategoriesRepository;
import com.bidblast.repositories.IEmptyProcessWithBusinessErrorListener;
import com.bidblast.repositories.businesserrors.SaveAuctionCategoryCodes;

public class AuctionCategoryFormViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isValidTitle = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isValidDescription = new MutableLiveData<>();
    private final MutableLiveData<Boolean> areValidKeywords = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> saveAuctionCategoryRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<SaveAuctionCategoryCodes> saveAuctionCategoryErrorCode = new MutableLiveData<>();

    public AuctionCategoryFormViewModel(){

    }

    public LiveData<Boolean> isValidTitle(){
        return isValidTitle;
    }

    public LiveData<Boolean> isValidDescription(){
        return isValidDescription;
    }

    public LiveData<Boolean> areValidKeywords(){
        return areValidKeywords;
    }

    public LiveData<RequestStatus> getSaveAuctionCategoryRequestStatus() {
        return saveAuctionCategoryRequestStatus;
    }

    public LiveData<SaveAuctionCategoryCodes> getSaveAuctionCategoryErrorCode() {
        return  saveAuctionCategoryErrorCode;
    }

    public void validateTitle(String title){
        boolean validationResult = title.trim().length() != 0;

        isValidTitle.setValue(validationResult);
    }

    public void validateDescription(String description){
        boolean validationResult = description.trim().length() != 0;

        isValidDescription.setValue(validationResult);
    }

    public void validateKeywords(String keywords){
        boolean validationResult = ValidationToolkit.areValidKeywords(keywords);

        areValidKeywords.setValue(validationResult);
    }

    public void registerAuctionCategory(AuctionCategory auctionCategory){
        saveAuctionCategoryRequestStatus.setValue(RequestStatus.LOADING);

        new AuctionCategoriesRepository().registerAuctionCategory(
                new AuctionCategoryBody(
                        auctionCategory.getTitle(),
                        auctionCategory.getDescription(),
                        auctionCategory.getKeywords()
                ),
                new IEmptyProcessWithBusinessErrorListener<SaveAuctionCategoryCodes>() {

                    @Override
                    public void onSuccess() {
                        saveAuctionCategoryRequestStatus.setValue(RequestStatus.DONE);
                    }

                    @Override
                    public void onError(SaveAuctionCategoryCodes errorCode) {
                        saveAuctionCategoryErrorCode.setValue(errorCode);
                        saveAuctionCategoryRequestStatus.setValue(RequestStatus.ERROR);
                    }
                }
        );
    }

    public void updateAuctionCategory(AuctionCategory auctionCategory){
        saveAuctionCategoryRequestStatus.setValue(RequestStatus.LOADING);

        new AuctionCategoriesRepository().updateAuctionCategory(
            auctionCategory.getId(),
            new AuctionCategoryBody(
                    auctionCategory.getTitle(),
                    auctionCategory.getDescription(),
                    auctionCategory.getKeywords()
            ),
            new IEmptyProcessWithBusinessErrorListener<SaveAuctionCategoryCodes>() {

                @Override
                public void onSuccess() {
                    saveAuctionCategoryRequestStatus.setValue(RequestStatus.DONE);
                }

                @Override
                public void onError(SaveAuctionCategoryCodes errorCode) {
                    saveAuctionCategoryErrorCode.setValue(errorCode);
                    saveAuctionCategoryRequestStatus.setValue(RequestStatus.ERROR);
                }
            }
        );
    }
}
