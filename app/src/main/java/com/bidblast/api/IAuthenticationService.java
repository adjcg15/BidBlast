package com.bidblast.api;

import com.bidblast.api.requests.authentication.UserCredentialsBody;
import com.bidblast.api.requests.authentication.UserRegisterBody;
import com.bidblast.api.responses.authentication.UserLoginJSONResponse;
import com.bidblast.api.responses.authentication.UserRegisterJSONResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface IAuthenticationService {


     @POST("sessions/")
     Call<UserLoginJSONResponse> login(@Body UserCredentialsBody credentials);
     @POST("users/")
     Call<Void> createAccount(@Body UserRegisterBody body);
     @PUT("users/")
     Call<Void> updateUser(
             @Header("Authorization") String authHeader,
             @Body UserRegisterBody body
     );
}