package com.bidblast.model.repositories;

import com.bidblast.R;
import com.bidblast.api.ApiClient;
import com.bidblast.api.IAuthenticationService;
import com.bidblast.api.responses.authentication.UserLoginJSONResponse;
import com.bidblast.model.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationRepository {
    public void login(String email, String password, IProcessStatusListener<User> statusListener) {
        IAuthenticationService authService = ApiClient.getInstance().getAuthenticationService();

        authService.login(email, password).enqueue(new Callback<UserLoginJSONResponse>() {
            @Override
            public void onResponse(Call<UserLoginJSONResponse> call, Response<UserLoginJSONResponse> response) {
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
            }

            @Override
            public void onFailure(Call<UserLoginJSONResponse> call, Throwable t) {
                statusListener.onError();
            }
        });
    }
}
