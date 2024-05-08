package com.bidblast.API;

import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public interface IAuthenticationService {
     final Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(ApiClient.API_HOST + "/sessions")
          .addConverterFactory(MoshiConverterFactory.create())
          .build();
}
