package com.bidblast.api;

import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public interface IAuctionsService {
    final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiClient.API_HOST + "/auctions")
            .addConverterFactory(MoshiConverterFactory.create())
            .build();
}
