package com.bidblast.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ApiClient {
    private static final ApiClient apiClient = new ApiClient();
    public static final String API_BASE_URL = "http://10.0.2.2:3000/api/";
    private final Retrofit retrofit;

    private IAuthenticationService authenticationService;
    private IAuctionsService auctionsService;
    private IAuctionCategoriesService auctionCategoriesService;
    private IOffersService offersService;

    public static ApiClient getInstance() {
        return apiClient;
    }
    private ApiClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
            .addInterceptor(new AuthorizationInterceptor())
            .build();

        retrofit = new Retrofit.Builder()
            .baseUrl(ApiClient.API_BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build();
    }

    public IAuthenticationService getAuthenticationService() {
        if (authenticationService == null) {
            authenticationService = retrofit.create(IAuthenticationService.class);
        }

        return authenticationService;
    }

    public IAuctionsService getAuctionsService() {
        if (auctionsService == null) {
            auctionsService = retrofit.create(IAuctionsService.class);
        }

        return auctionsService;
    }

    public IAuctionCategoriesService getAuctionCategoriesService() {
        if (auctionCategoriesService == null) {
            auctionCategoriesService = retrofit.create(IAuctionCategoriesService.class);
        }

        return auctionCategoriesService;
    }

    public IOffersService getOffersService() {
        if (offersService == null) {
            offersService = retrofit.create(IOffersService.class);
        }

        return offersService;
    }
}
