package com.bidblast.api;

import com.bidblast.api.requests.auctions.AuctionApprovalBody;
import com.bidblast.api.requests.auctions.AuctionCreateBody;
import com.bidblast.api.requests.auctions.AuctionRejectionBody;
import com.bidblast.api.requests.auctions.BlockedProfileBody;
import com.bidblast.api.responses.auctions.AuctionJSONResponse;
import com.bidblast.api.responses.auctions.AuctionLastOfferJSONResponse;
import com.bidblast.lib.Session;
import com.bidblast.model.AuctionState;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
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

    @GET("auctions/published")
    Call<List<AuctionJSONResponse>> getPublishedAuctions(
            @Header("Authorization") String authHeader
    );
    @GET("auctions/states")
    Call<List<AuctionState>> getAuctionStates(
            @Header("Authorization") String authHeader
    );

    @POST("auctions/")
    Call<AuctionJSONResponse> createAuction(
            @Header("Authorization") String authHeader,
            @Body AuctionCreateBody auctionBody
    );

    @GET("auctions/{idAuction}")
    Call<AuctionJSONResponse> getAuctionById(
            @Header("Authorization") String authHeader,
            @Path("idAuction") int idAuction
    );

    @GET("auctions/{idAuction}/offers")
    Call<List<AuctionLastOfferJSONResponse>> getUserAuctionOffersByAuctionId(
            @Header("Authorization") String authHeader,
            @Path("idAuction") int idAuction,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @POST("auctions/{idAuction}/user-blocking")
    Call<Void> blockUserInAnAuctionAndDeleteHisOffers(
            @Header("Authorization") String authHeader,
            @Path("idAuction") int idAuction,
            @Body BlockedProfileBody idProfile
    );

    @GET("users/completed-auctions")
    Call<List<AuctionJSONResponse>> getCompletedAuctionsList(
            @Header("Authorization") String authHeader,
            @Query("query") String searchQuery,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("users/auctions")
    Call<List<AuctionJSONResponse>> getCreatedAuctionsList(
            @Header("Authorization") String authHeader,
            @Query("query") String searchQuery,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("users/sold-auctions")
    Call<List<AuctionJSONResponse>> getUserSalesAuctionsList(
            @Header("Authorization") String authHeader,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );
    @POST("auction-reviews/approval")
    Call<Void> approveAuction(
            @Header("Authorization") String authHeader,
            @Body AuctionApprovalBody auctionApprovalBody
    );
    @POST("auction-reviews/rejection")
    Call<Void> rejectAuction(
            @Header("Authorization") String authHeader,
            @Body AuctionRejectionBody auctionRejectionBody
    );
}