package com.bidblast.api;

import com.bidblast.api.responses.auctioncategories.AuctionCategoryJSONResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface IAuctionCategoriesService {
    @GET("auction-categories/")
    Call<List<AuctionCategoryJSONResponse>> getAuctionCategoriesList(
        @Header("Authorization") String authHeader
    );
}
