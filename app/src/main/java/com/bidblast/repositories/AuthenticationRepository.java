package com.bidblast.repositories;

import android.content.Context;

import com.bidblast.R;
import com.bidblast.api.ApiClient;
import com.bidblast.api.IAuthenticationService;
import com.bidblast.api.requests.authentication.UserCredentialsBody;
import com.bidblast.api.requests.authentication.UserRegisterBody;
import com.bidblast.api.responses.authentication.UserLoginJSONResponse;
import com.bidblast.api.responses.authentication.UserRegisterJSONResponse;
import com.bidblast.lib.Session;
import com.bidblast.model.User;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationRepository {
    public void login(UserCredentialsBody credentials, IEmptyProcessStatusListener statusListener) {
        IAuthenticationService authService = ApiClient.getInstance().getAuthenticationService();

        authService.login(credentials).enqueue(new Callback<UserLoginJSONResponse>() {
            @Override
            public void onResponse(Call<UserLoginJSONResponse> call, Response<UserLoginJSONResponse> response) {
                if(response.isSuccessful()) {
                    UserLoginJSONResponse body = response.body();

                    if(body != null) {
                        User user = new User(
                            body.getId(),
                            body.getFullName(),
                            body.getPhoneNumber(),
                            body.getAvatar(),
                            body.getEmail(),
                            body.getRoles()
                        );

                        Session session = Session.getInstance();
                        session.setToken(body.getToken());
                        session.setUser(user);

                        statusListener.onSuccess();
                    } else {
                        statusListener.onError(ProcessErrorCodes.REQUEST_FORMAT_ERROR);
                    }
                } else {
                    statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
                }
            }

            @Override
            public void onFailure(Call<UserLoginJSONResponse> call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }
    public void createAccount(UserRegisterBody body, IEmptyProcessStatusListener creationListener) {
        IAuthenticationService authService = ApiClient.getInstance().getAuthenticationService();
        authService.createAccount(body).enqueue(new Callback<UserRegisterJSONResponse>() {
            @Override
            public void onResponse(Call<UserRegisterJSONResponse> call, Response<UserRegisterJSONResponse> response) {
                if (response.isSuccessful()) {
                    UserRegisterJSONResponse responseBody = response.body();
                    if (responseBody != null && responseBody.getAccount() != null) {
                        creationListener.onSuccess();
                    } else {
                        System.err.println("Response body or account is null");
                        creationListener.onError(ProcessErrorCodes.REQUEST_FORMAT_ERROR);
                    }
                } else {
                    creationListener.onError(ProcessErrorCodes.REQUEST_FORMAT_ERROR);
                }
            }

            @Override
            public void onFailure(Call<UserRegisterJSONResponse> call, Throwable t) {
                creationListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }
}
