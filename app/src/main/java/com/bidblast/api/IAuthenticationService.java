package com.bidblast.api;

import com.bidblast.api.responses.authentication.UserLoginJSONResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IAuthenticationService {
     final Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(ApiClient.API_HOST + "/sessions")
          .addConverterFactory(MoshiConverterFactory.create())
          .build();

     @POST
     Call<UserLoginJSONResponse> login(@Body String email, @Body String password);
}
