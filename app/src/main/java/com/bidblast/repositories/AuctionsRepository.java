package com.bidblast.repositories;

import com.bidblast.api.ApiClient;
import com.bidblast.api.IAuctionsService;
import com.bidblast.api.responses.auctions.AuctionAuctioneerJSONResponse;
import com.bidblast.api.responses.auctions.AuctionJSONResponse;
import com.bidblast.api.responses.auctions.AuctionLastOfferJSONResponse;
import com.bidblast.api.responses.auctions.AuctionMediaFileJSONResponse;
import com.bidblast.lib.DateToolkit;
import com.bidblast.lib.Session;
import com.bidblast.model.Auction;
import com.bidblast.model.HypermediaFile;
import com.bidblast.model.Offer;
import com.bidblast.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuctionsRepository {
    public void getAuctionsList(
        String searchQuery,
        int limit,
        int offset,
        String categories,
        int minimumPrice,
        int maximumPrice,
        IProcessStatusListener<List<Auction>> statusListener
    ) {
        IAuctionsService auctionsService = ApiClient.getInstance().getAuctionsService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());

        auctionsService.getAuctionsList(authHeader, searchQuery, limit, offset, categories, minimumPrice, maximumPrice).enqueue(new Callback<List<AuctionJSONResponse>>() {
            @Override
            public void onResponse(Call<List<AuctionJSONResponse>> call, Response<List<AuctionJSONResponse>> response) {
                if(response.isSuccessful()) {
                    List<AuctionJSONResponse> body = response.body();

                    if(body != null) {
                        List<Auction> auctionsList = new ArrayList<>();

                        for(AuctionJSONResponse auctionRes : body) {
                            Auction auction = new Auction();

                            auction.setClosesAt(DateToolkit.parseDateFromIS8601(auctionRes.getClosesAt()));
                            auction.setId(auctionRes.getId());
                            auction.setTitle(auctionRes.getTitle());

                            AuctionAuctioneerJSONResponse auctioneerRes = auctionRes.getAuctioneer();
                            if(auctioneerRes != null) {
                                User auctioneer = new User();

                                auctioneer.setId(auctionRes.getId());
                                auctioneer.setFullName(auctioneerRes.getFullName());
                                auctioneer.setAvatar(auctioneerRes.getAvatar());

                                auction.setAuctioneer(auctioneer);
                            }

                            AuctionLastOfferJSONResponse lastOfferRes = auctionRes.getLastOffer();
                            if(lastOfferRes != null) {
                                Offer lastOffer = new Offer();

                                lastOffer.setId(lastOfferRes.getId());
                                lastOffer.setAmount(lastOfferRes.getAmount());
                                lastOffer.setCreationDate(DateToolkit.parseDateFromIS8601(lastOfferRes.getCreationDate()));

                                auction.setLastOffer(lastOffer);
                            }

                            List<AuctionMediaFileJSONResponse> mediaFilesRes = auctionRes.getMediaFiles();
                            if(mediaFilesRes != null) {
                                List<HypermediaFile> mediaFiles = new ArrayList<>();

                                for(AuctionMediaFileJSONResponse fileRes : mediaFilesRes) {
                                    HypermediaFile file = new HypermediaFile();

                                    file.setId(fileRes.getId());
                                    file.setName(fileRes.getName());
                                    file.setContent(fileRes.getContent());

                                    mediaFiles.add(file);
                                }

                                auction.setMediaFiles(mediaFiles);
                            }

                            auctionsList.add(auction);
                        }

                        statusListener.onSuccess(auctionsList);
                    } else {
                        statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
                    }
                } else {
                    statusListener.onError(ProcessErrorCodes.AUTH_ERROR);
                }
            }

            @Override
            public void onFailure(Call<List<AuctionJSONResponse>> call, Throwable t) {
                t.printStackTrace();
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }
}
