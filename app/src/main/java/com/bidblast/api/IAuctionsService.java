package com.bidblast.api;

import com.bidblast.api.responses.auctions.AuctionJSONResponse;
import com.bidblast.lib.Session;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IAuctionsService {
    @GET("auctions/")
    Call<List<AuctionJSONResponse>> getAuctionsList(
        @Header("Authorization") String authHeader,
        @Query("query") String searchQuery,
        @Query("limit") int limit,
        @Query("offset") int offset,
        @Query("categories") String categories,
        @Query("minimumPrice") int minimumPrice,
        @Query("maximumPrice") int maximumPrice
    );

    @GET("users/{usid}/completed-auctions")
    Call<List<AuctionJSONResponse>> getCompletedAuctionsList(
        @Header("Authorization") String authHeader,
        @Path("usid") int customerId,
        @Query("query") String searchQuery,
        @Query("limit") int limit,
        @Query("offset") int offset
    );

    @GET("users/{usid}/sales-auctions")
    Call<List<AuctionJSONResponse>> getUserSalesAuctionsList(
        @Header("Authorization") String authHeader,
        @Path("usid") int auctioneerId,
        @Query("startDate") String startDate,
        @Query("endDate") String endDate
    );
}
