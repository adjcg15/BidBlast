package com.bidblast.api;

import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public interface IAuctionCategoriesService {
    final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiClient.API_HOST + "/auction-categories")
            .addConverterFactory(MoshiConverterFactory.create())
            .build();
}
