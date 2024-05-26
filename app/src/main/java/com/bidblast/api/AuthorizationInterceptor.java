package com.bidblast.api;

import androidx.annotation.NonNull;

import com.bidblast.lib.Session;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if (response.header("Set-Authorization") != null) {
            String newAuthToken = response.header("Set-Authorization");
            Session.getInstance().setToken(newAuthToken);
            System.out.println("Token nuevo: " + Session.getInstance().getToken());
        }

        return response;
    }
}
