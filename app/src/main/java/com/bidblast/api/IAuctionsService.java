package com.bidblast.api;

import com.bidblast.api.responses.auctions.AuctionJSONResponse;
import com.bidblast.lib.Session;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface IAuctionsService {
    @GET("auctions/")
    Call<List<AuctionJSONResponse>> getAuctionsList(
        @Header("Authorization") String authHeader,
        @Query("query") String searchQuery,
        @Query("limit") int limit,
        @Query("offset") int offset
    );
}
