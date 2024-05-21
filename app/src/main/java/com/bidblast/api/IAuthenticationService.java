package com.bidblast.api;

import com.bidblast.api.requests.authentication.UserCredentialsBody;
import com.bidblast.api.requests.authentication.UserRegisterBody;
import com.bidblast.api.responses.authentication.UserLoginJSONResponse;
import com.bidblast.api.responses.authentication.UserRegisterJSONResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IAuthenticationService {


     @POST("sessions/")
     Call<UserLoginJSONResponse> login(@Body UserCredentialsBody credentials);
     @POST("accounts/")
     Call<UserRegisterJSONResponse> createAccount(@Body UserRegisterBody body);
}