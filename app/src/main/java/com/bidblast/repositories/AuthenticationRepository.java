package com.bidblast.repositories;

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
                        statusListener.onError();
                    }
                } else {
                    //TODO: handle status different to 200
                    statusListener.onError();
                }
            }

            @Override
            public void onFailure(Call<UserLoginJSONResponse> call, Throwable t) {
                statusListener.onError();
            }
        });
    }
}
