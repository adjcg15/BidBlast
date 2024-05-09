package com.bidblast.api;

import android.app.Service;

public class ApiClient {
    private static final ApiClient apiClient = new ApiClient();
    public static final String API_HOST = "http://10.0.2.2:3000";

    private Service authenticationService;
    private Service auctionsService;
    private Service auctionCategoriesService;

    public static ApiClient getInstance() {
        return apiClient;
    }
    private ApiClient() {

    }

    public Service getAuthenticationService() {
        if (authenticationService == null) {
            authenticationService = IAuthenticationService.retrofit.create(Service.class);
        }

        return authenticationService;
    }

    public Service getAuctionsService() {
        if (auctionsService == null) {
            auctionsService = IAuctionsService.retrofit.create(Service.class);
        }

        return auctionsService;
    }

    public Service getAuctionCategoriesService() {
        if (auctionCategoriesService == null) {
            auctionCategoriesService = IAuctionCategoriesService.retrofit.create(Service.class);
        }

        return auctionCategoriesService;
    }
}
