package com.bidblast.api;

public class ApiClient {
    private static final ApiClient apiClient = new ApiClient();
    public static final String API_HOST = "http://10.0.2.2:3000";

    private IAuthenticationService authenticationService;
    private IAuctionsService auctionsService;
    private IAuctionCategoriesService auctionCategoriesService;

    public static ApiClient getInstance() {
        return apiClient;
    }
    private ApiClient() {

    }

    public IAuthenticationService getAuthenticationService() {
        if (authenticationService == null) {
            authenticationService = IAuthenticationService.retrofit.create(IAuthenticationService.class);
        }

        return authenticationService;
    }

    public IAuctionsService getAuctionsService() {
        if (auctionsService == null) {
            auctionsService = IAuctionsService.retrofit.create(IAuctionsService.class);
        }

        return auctionsService;
    }

    public IAuctionCategoriesService getAuctionCategoriesService() {
        if (auctionCategoriesService == null) {
            auctionCategoriesService = IAuctionCategoriesService.retrofit.create(IAuctionCategoriesService.class);
        }

        return auctionCategoriesService;
    }
}
