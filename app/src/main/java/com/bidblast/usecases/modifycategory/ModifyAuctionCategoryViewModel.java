package com.bidblast.usecases.modifycategory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bidblast.api.RequestStatus;
import com.bidblast.api.requests.auctioncategory.AuctionCategoryBody;
import com.bidblast.lib.ValidationToolkit;
import com.bidblast.model.AuctionCategory;
import com.bidblast.repositories.AuctionCategoriesRepository;
import com.bidblast.repositories.IEmptyProcessStatusListener;
import com.bidblast.repositories.IEmptyProcessWithBusinessErrorListener;
import com.bidblast.repositories.IProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;
import com.bidblast.repositories.businesserrors.ModifyAuctionCategoryCodes;

public class ModifyAuctionCategoryViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isValidTitle = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isValidDescription = new MutableLiveData<>();
    private final MutableLiveData<Boolean> areValidKeywords = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> modifyAuctionCategoryRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<ModifyAuctionCategoryCodes> modifyAuctionCategoryErrorCode = new MutableLiveData<>();

    public ModifyAuctionCategoryViewModel(){

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

    public LiveData<RequestStatus> getModifyAuctionCategoryRequestStatus() {
        return modifyAuctionCategoryRequestStatus;
    }

    public LiveData<ModifyAuctionCategoryCodes> getModifyAuctionCategoryErrorCode() {
        return  modifyAuctionCategoryErrorCode;
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

    public void updateAuctionCategory(AuctionCategory auctionCategory){
        modifyAuctionCategoryRequestStatus.setValue(RequestStatus.LOADING);

        new AuctionCategoriesRepository().updateAuctionCategory(
            auctionCategory.getId(),
            new AuctionCategoryBody(
                    auctionCategory.getTitle(),
                    auctionCategory.getDescription(),
                    auctionCategory.getKeywords()
            ),
            new IEmptyProcessWithBusinessErrorListener<ModifyAuctionCategoryCodes>() {

                @Override
                public void onSuccess() {
                    modifyAuctionCategoryRequestStatus.setValue(RequestStatus.DONE);
                }

                @Override
                public void onError(ModifyAuctionCategoryCodes errorCode) {
                    modifyAuctionCategoryErrorCode.setValue(errorCode);
                    modifyAuctionCategoryRequestStatus.setValue(RequestStatus.ERROR);
                }
            }
        );
    }
}
