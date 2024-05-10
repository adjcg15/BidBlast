package com.bidblast.repositories;

import android.content.Context;

import com.bidblast.R;
import com.bidblast.api.ApiClient;
import com.bidblast.api.IAuthenticationService;
import com.bidblast.api.requests.authentication.UserCredentialsBody;
import com.bidblast.api.responses.authentication.UserLoginJSONResponse;
import com.bidblast.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationRepository {
    public void login(UserCredentialsBody credentials, IProcessStatusListener<User> statusListener) {
        IAuthenticationService authService = ApiClient.getInstance().getAuthenticationService();

        authService.login(credentials).enqueue(new Callback<UserLoginJSONResponse>() {
            @Override
            public void onResponse(Call<UserLoginJSONResponse> call, Response<UserLoginJSONResponse> response) {
                if(response.isSuccessful()) {
                    UserLoginJSONResponse body = response.body();

                    if(body != null) {
                        //TODO handle token saving
                        statusListener.onSuccess(new User(
                            body.getId(),
                            body.getFullName(),
                            body.getPhoneNumber(),
                            body.getAvatar(),
                            body.getEmail(),
                            body.getRoles()
                        ));
                    } else {
                        statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
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
}
