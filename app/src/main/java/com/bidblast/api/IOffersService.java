package com.bidblast.api;

import com.bidblast.api.requests.offers.OfferCreationBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface IOffersService {
    @POST("offers/")
    Call<Void> createOffer(
            @Header("Authorization") String authHeader,
            @Body OfferCreationBody body
    );
}
