package com.bidblast.repositories;

import com.bidblast.api.ApiClient;
import com.bidblast.api.IAuctionCategoriesService;
import com.bidblast.api.requests.auctioncategory.AuctionCategoryBody;
import com.bidblast.api.responses.auctioncategories.AuctionCategoryJSONResponse;
import com.bidblast.lib.Session;
import com.bidblast.model.AuctionCategory;
import com.bidblast.repositories.businesserrors.SaveAuctionCategoryCodes;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
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
            IEmptyProcessWithBusinessErrorListener<SaveAuctionCategoryCodes> statusListener) {
        IAuctionCategoriesService categoriesService = ApiClient.getInstance().getAuctionCategoriesService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());

        categoriesService.registerAuctionCategory(authHeader, auctionCategoryBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()) {
                    statusListener.onSuccess();
                } else {
                    if(response.code() == 401 || response.code() == 403) {
                        statusListener.onError(SaveAuctionCategoryCodes.UNAUTHORIZED);
                    } else if (response.code() == 400 && response.errorBody() != null) {
                        try {
                            String errorBodyString = response.errorBody().string();
                            JsonObject jsonErrorBody = new Gson().fromJson(errorBodyString, JsonObject.class);

                            if(jsonErrorBody.has("apiErrorCode")) {
                                String apiErrorCode = jsonErrorBody.get("apiErrorCode").getAsString();

                                if (apiErrorCode.equals("CRCT-400001")) {
                                    statusListener.onError(SaveAuctionCategoryCodes.TITLE_ALREADY_EXISTS);
                                } else {
                                    statusListener.onError(SaveAuctionCategoryCodes.UNKNOWN);
                                }
                            } else {
                                statusListener.onError(SaveAuctionCategoryCodes.VALIDATION_ERROR);
                            }
                        } catch (IOException ex) {
                            statusListener.onError(SaveAuctionCategoryCodes.UNKNOWN);
                        }
                    } else {
                        statusListener.onError(SaveAuctionCategoryCodes.UNKNOWN);
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                statusListener.onError(SaveAuctionCategoryCodes.SERVER_ERROR);
            }
        });
    }

    public void updateAuctionCategory(
            int idAuctionCategory,
            AuctionCategoryBody auctionCategoryBody,
            IEmptyProcessWithBusinessErrorListener<SaveAuctionCategoryCodes> statusListener
    ) {
        IAuctionCategoriesService categoriesService = ApiClient.getInstance().getAuctionCategoriesService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());

        categoriesService.updateAuctionCategory(authHeader, idAuctionCategory, auctionCategoryBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()) {
                    statusListener.onSuccess();
                } else {
                    if(response.code() == 401 || response.code() == 403) {
                        statusListener.onError(SaveAuctionCategoryCodes.UNAUTHORIZED);
                    } else if (response.code() == 400 && response.errorBody() != null) {
                        try {
                            String errorBodyString = response.errorBody().string();
                            JsonObject jsonErrorBody = new Gson().fromJson(errorBodyString, JsonObject.class);

                            if(jsonErrorBody.has("apiErrorCode")) {
                                String apiErrorCode = jsonErrorBody.get("apiErrorCode").getAsString();

                                if (apiErrorCode.equals("MCBI-400001")) {
                                    statusListener.onError(SaveAuctionCategoryCodes.CATEGORY_NOT_FOUND);
                                } else if (apiErrorCode.equals("MCBI-400002")) {
                                    statusListener.onError(SaveAuctionCategoryCodes.TITLE_ALREADY_EXISTS);
                                } else {
                                    statusListener.onError(SaveAuctionCategoryCodes.UNKNOWN);
                                }
                            } else {
                                statusListener.onError(SaveAuctionCategoryCodes.VALIDATION_ERROR);
                            }
                        } catch (IOException ex) {
                            statusListener.onError(SaveAuctionCategoryCodes.UNKNOWN);
                        }
                    } else {
                        statusListener.onError(SaveAuctionCategoryCodes.UNKNOWN);
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                statusListener.onError(SaveAuctionCategoryCodes.SERVER_ERROR);
            }
        });
    }
}