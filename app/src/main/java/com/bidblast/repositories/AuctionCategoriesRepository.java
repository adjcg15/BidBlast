package com.bidblast.repositories;

import com.bidblast.api.ApiClient;
import com.bidblast.api.IAuctionCategoriesService;
import com.bidblast.api.requests.auctioncategory.AuctionCategoryBody;
import com.bidblast.api.responses.auctioncategories.AuctionCategoryJSONResponse;
import com.bidblast.api.responses.auctioncategories.UpdatedAuctionCategoryJSONResponse;
import com.bidblast.lib.Session;
import com.bidblast.model.AuctionCategory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuctionCategoriesRepository {
    public void getAuctionCategories(IProcessStatusListener<List<AuctionCategory>> statusListener) {
        IAuctionCategoriesService categoriesService = ApiClient.getInstance().getAuctionCategoriesService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());

        categoriesService.getAuctionCategoriesList(authHeader).enqueue(new Callback<List<AuctionCategoryJSONResponse>>() {
            @Override
            public void onResponse(Call<List<AuctionCategoryJSONResponse>> call, Response<List<AuctionCategoryJSONResponse>> response) {
                List<AuctionCategoryJSONResponse> body = response.body();

                if(response.isSuccessful()) {
                    if(body != null) {
                        List<AuctionCategory> auctionCategoriesList = new ArrayList<>();

                        for(AuctionCategoryJSONResponse categoryRes: body) {
                            AuctionCategory category = new AuctionCategory(
                                    categoryRes.getId(),
                                    categoryRes.getTitle(),
                                    categoryRes.getDescription(),
                                    categoryRes.getKeywords()
                            );

                            auctionCategoriesList.add(category);
                        }

                        statusListener.onSuccess(auctionCategoriesList);
                    } else {
                        statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
                    }
                } else {
                    statusListener.onError(ProcessErrorCodes.AUTH_ERROR);
                }
            }

            @Override
            public void onFailure(Call<List<AuctionCategoryJSONResponse>> call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }

    public void registerAuctionCategory(
            AuctionCategoryBody auctionCategoryBody,
            IEmptyProcessStatusListener statusListener) {
        IAuctionCategoriesService categoriesService = ApiClient.getInstance().getAuctionCategoriesService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());

        categoriesService.registerAuctionCategory(authHeader, auctionCategoryBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    statusListener.onSuccess();
                }else{
                    if(response.code() == 400) {
                        statusListener.onError(ProcessErrorCodes.REQUEST_FORMAT_ERROR);
                    } else {
                        statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }

    public void updateAuctionCategory(
            int idAuctionCategory,
            AuctionCategoryBody auctionCategoryBody,
            IEmptyProcessStatusListener statusListener
    ) {
        IAuctionCategoriesService categoriesService = ApiClient.getInstance().getAuctionCategoriesService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());

        categoriesService.updateAuctionCategory(authHeader, idAuctionCategory, auctionCategoryBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    statusListener.onSuccess();
                }else{
                    if(response.code() == 400) {
                        statusListener.onError(ProcessErrorCodes.REQUEST_FORMAT_ERROR);
                    } else {
                        statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }
}