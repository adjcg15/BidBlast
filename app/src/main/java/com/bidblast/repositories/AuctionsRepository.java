package com.bidblast.repositories;

import android.util.Log;

import com.bidblast.api.ApiClient;
import com.bidblast.api.IAuctionsService;
import com.bidblast.api.requests.auctions.AuctionApprovalBody;
import com.bidblast.api.requests.auctions.AuctionCreateBody;
import com.bidblast.api.requests.auctions.AuctionRejectionBody;
import com.bidblast.api.requests.auctions.BlockedProfileBody;
import com.bidblast.api.responses.auctioncategories.AuctionCategoryJSONResponse;
import com.bidblast.api.responses.auctions.AuctionAuctioneerJSONResponse;
import com.bidblast.api.responses.auctions.AuctionCustomerJSONResponse;
import com.bidblast.api.responses.auctions.AuctionJSONResponse;
import com.bidblast.api.responses.auctions.AuctionLastOfferJSONResponse;
import com.bidblast.api.responses.auctions.AuctionMediaFileJSONResponse;
import com.bidblast.api.responses.auctions.AuctionReviewJSONResponse;
import com.bidblast.lib.DateToolkit;
import com.bidblast.lib.Session;
import com.bidblast.model.Auction;
import com.bidblast.model.AuctionCategory;
import com.bidblast.model.AuctionReview;
import com.bidblast.model.AuctionState;
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
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }

    public void getCreatedAuctionsList(
            String searchQuery,
            int limit,
            int offset,
            IProcessStatusListener<List<Auction>> statusListener) {
        IAuctionsService auctionsService = ApiClient.getInstance().getAuctionsService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());

        auctionsService.getCreatedAuctionsList(authHeader, searchQuery, limit, offset).enqueue(new Callback<List<AuctionJSONResponse>>() {
            @Override
            public void onResponse(Call<List<AuctionJSONResponse>> call, Response<List<AuctionJSONResponse>> response) {
                if(response.isSuccessful()) {
                    List<AuctionJSONResponse> body = response.body();

                    if(body != null) {
                        List<Auction> auctionsList = new ArrayList<>();

                        for(AuctionJSONResponse auctionRes : body) {
                            Auction auction = new Auction();

                            auction.setId(auctionRes.getId());
                            auction.setTitle(auctionRes.getTitle());
                            auction.setUpdatedDate(DateToolkit.parseDateFromIS8601(auctionRes.getUpdatedDate()));
                            auction.setAuctionState(auctionRes.getAuctionState());
                            auction.setBasePrice(auctionRes.getBasePrice());
                            auction.setMinimumBid(auctionRes.getMinimumBid());
                            auction.setDaysAvailable(auctionRes.getDaysAvailable());

                            if (auctionRes.getClosesAt() != null) {
                                auction.setClosesAt(DateToolkit.parseDateFromIS8601(auctionRes.getClosesAt()));
                            }

                            AuctionLastOfferJSONResponse lastOfferRes = auctionRes.getLastOffer();
                            if(lastOfferRes != null) {
                                Offer lastOffer = new Offer();

                                lastOffer.setId(lastOfferRes.getId());
                                lastOffer.setAmount(lastOfferRes.getAmount());
                                lastOffer.setCreationDate(DateToolkit.parseDateFromIS8601(lastOfferRes.getCreationDate()));

                                AuctionCustomerJSONResponse customerRes = auctionRes.getLastOffer().getCustomer();
                                if(customerRes != null) {
                                    User customer = new User();
                                    customer.setId(customerRes.getId());
                                    customer.setFullName(customerRes.getFullName());
                                    customer.setPhoneNumber(customerRes.getPhoneNumber());
                                    customer.setEmail(customerRes.getEmail());
                                    customer.setAvatar(customerRes.getAvatar());

                                    lastOffer.setCustomer(customer);
                                }

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

                            AuctionReviewJSONResponse reviewRes = auctionRes.getReview();
                            if(reviewRes != null) {
                                AuctionReview review = new AuctionReview();

                                review.setId(reviewRes.getId());
                                review.setCreationDate(DateToolkit.parseDateFromIS8601(reviewRes.getCreationDate()));
                                review.setComments(reviewRes.getComments());

                                auction.setReview(review);
                            }

                            auctionsList.add(auction);
                        }

                        statusListener.onSuccess(auctionsList);
                    } else {
                        statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
                    }
                } else {
                    statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
                }
            }

            @Override
            public void onFailure(Call<List<AuctionJSONResponse>> call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }

    public void getCompletedAuctionsList(
            String searchQuery,
            int limit,
            int offset,
            IProcessStatusListener<List<Auction>> statusListener) {
        IAuctionsService auctionsService = ApiClient.getInstance().getAuctionsService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());

        auctionsService.getCompletedAuctionsList(authHeader, searchQuery, limit, offset).enqueue(new Callback<List<AuctionJSONResponse>>() {
            @Override
            public void onResponse(Call<List<AuctionJSONResponse>> call, Response<List<AuctionJSONResponse>> response) {
                if(response.isSuccessful()) {
                    List<AuctionJSONResponse> body = response.body();

                    if(body != null) {
                        List<Auction> auctionsList = new ArrayList<>();

                        for(AuctionJSONResponse auctionRes : body) {
                            Auction auction = new Auction();

                            auction.setId(auctionRes.getId());
                            auction.setTitle(auctionRes.getTitle());
                            auction.setUpdatedDate(DateToolkit.parseDateFromIS8601(auctionRes.getUpdatedDate()));

                            AuctionAuctioneerJSONResponse auctioneerRes = auctionRes.getAuctioneer();
                            if(auctioneerRes != null) {
                                User auctioneer = new User();

                                auctioneer.setId(auctionRes.getId());
                                auctioneer.setFullName(auctioneerRes.getFullName());
                                auctioneer.setPhoneNumber(auctioneerRes.getPhoneNumber());
                                auctioneer.setEmail(auctioneerRes.getEmail());
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
                    statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
                }
            }

            @Override
            public void onFailure(Call<List<AuctionJSONResponse>> call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }

    public void getUserAuctionOffersByAuctionId(
            int idAuction,
            int limit,
            int offset,
            IProcessStatusListener<List<Offer>> statusListener) {
        IAuctionsService auctionsService = ApiClient.getInstance().getAuctionsService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());
        auctionsService.getUserAuctionOffersByAuctionId(authHeader, idAuction, limit, offset).enqueue(new Callback<List<AuctionLastOfferJSONResponse>> () {
            @Override
            public void onResponse(Call<List<AuctionLastOfferJSONResponse>> call, Response<List<AuctionLastOfferJSONResponse>> response) {
                if (response.isSuccessful()) {
                    List<AuctionLastOfferJSONResponse> body = response.body();

                    if (body != null) {
                        List<Offer> offersList = new ArrayList<>();

                        for (AuctionLastOfferJSONResponse offerRes : body) {
                            Offer offer = new Offer();

                            offer.setId(offerRes.getId());
                            offer.setAmount(offerRes.getAmount());
                            offer.setCreationDate(DateToolkit.parseDateFromIS8601(offerRes.getCreationDate()));

                            if (offerRes.getCustomer() != null) {
                                User customer = new User();

                                customer.setId(offerRes.getCustomer().getId());
                                customer.setFullName(offerRes.getCustomer().getFullName());
                                customer.setAvatar(offerRes.getCustomer().getAvatar());

                                offer.setCustomer(customer);
                            }

                            offersList.add(offer);
                        }

                        statusListener.onSuccess(offersList);
                    } else {
                        statusListener.onError(ProcessErrorCodes.AUTH_ERROR);
                    }
                } else {
                    statusListener.onError(ProcessErrorCodes.AUTH_ERROR);
                }
            }

            @Override
            public void onFailure(Call<List<AuctionLastOfferJSONResponse>> call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }

    public void blockUserInAnAuctionAndDeleteHisOffers(
            int idAuction,
            int idProfile,
            IEmptyProcessStatusListener statusListener) {
        IAuctionsService auctionsService = ApiClient.getInstance().getAuctionsService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());
        auctionsService.blockUserInAnAuctionAndDeleteHisOffers(authHeader, idAuction, new BlockedProfileBody(idProfile)).enqueue(new Callback<Void> () {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    statusListener.onSuccess();
                } else {
                    statusListener.onError(ProcessErrorCodes.AUTH_ERROR);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }

    public void getAuctionById(int idAuction, IProcessStatusListener<Auction> statusListener) {
        IAuctionsService auctionsService = ApiClient.getInstance().getAuctionsService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());
        auctionsService.getAuctionById(authHeader, idAuction).enqueue(new Callback<AuctionJSONResponse> () {
            @Override
            public void onResponse(Call<AuctionJSONResponse> call, Response<AuctionJSONResponse> response) {
                if (response.isSuccessful()) {
                    AuctionJSONResponse body = response.body();

                    if (body != null) {
                        Auction auction = new Auction();

                        auction.setId(body.getId());
                        auction.setTitle(body.getTitle());
                        auction.setClosesAt(DateToolkit.parseDateFromIS8601(body.getClosesAt()));
                        auction.setDescription(body.getDescription());
                        auction.setBasePrice(body.getBasePrice());
                        auction.setMinimumBid(body.getMinimumBid());
                        auction.setItemCondition(body.getItemCondition());

                        List<AuctionMediaFileJSONResponse> mediaFilesRes = body.getMediaFiles();
                        if(mediaFilesRes != null) {
                            List<HypermediaFile> mediaFiles = new ArrayList<>();

                            for(AuctionMediaFileJSONResponse fileRes : mediaFilesRes) {
                                HypermediaFile file = new HypermediaFile();

                                file.setId(fileRes.getId());
                                file.setName(fileRes.getName());
                                file.setContent(fileRes.getContent());
                                file.setMimeType(fileRes.getMimeType());

                                mediaFiles.add(file);
                            }

                            auction.setMediaFiles(mediaFiles);
                        }

                        AuctionLastOfferJSONResponse lastOffer = body.getLastOffer();
                        if(lastOffer != null) {
                            Offer offer = new Offer();
                            offer.setId(lastOffer.getId());
                            offer.setAmount(lastOffer.getAmount());
                            offer.setCreationDate(DateToolkit.parseDateFromIS8601(lastOffer.getCreationDate()));

                            auction.setLastOffer(offer);
                        }

                        statusListener.onSuccess(auction);
                    } else {
                        statusListener.onError(ProcessErrorCodes.AUTH_ERROR);
                    }
                } else {
                    statusListener.onError(ProcessErrorCodes.AUTH_ERROR);
                }
            }

            @Override
            public void onFailure(Call<AuctionJSONResponse> call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }

    public void getUserSalesAuctionsList(
            String startDate,
            String endDate,
            IProcessStatusListener<List<Auction>> statusListener){
        IAuctionsService auctionsService = ApiClient.getInstance().getAuctionsService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());
        auctionsService.getUserSalesAuctionsList(authHeader, startDate, endDate).enqueue(new Callback<List<AuctionJSONResponse>>() {
            @Override
            public void onResponse(Call<List<AuctionJSONResponse>> call, Response<List<AuctionJSONResponse>> response) {
                if (response.isSuccessful()) {
                    List<AuctionJSONResponse> body = response.body();

                    if (body != null) {
                        List<Auction> auctionsList = new ArrayList<>();

                        for(AuctionJSONResponse auctionRes : body) {
                            Auction auction = new Auction();

                            auction.setId(auctionRes.getId());
                            auction.setTitle(auctionRes.getTitle());
                            auction.setUpdatedDate(DateToolkit.parseDateFromIS8601(auctionRes.getUpdatedDate()));

                            AuctionCategoryJSONResponse categoryRes = auctionRes.getCategory();
                            if (categoryRes != null) {
                                AuctionCategory category = new AuctionCategory();

                                category.setId(categoryRes.getId());
                                category.setTitle(categoryRes.getTitle());

                                auction.setCategory(category);
                            }

                            AuctionLastOfferJSONResponse lastOfferRes = auctionRes.getLastOffer();
                            if(lastOfferRes != null) {
                                Offer lastOffer = new Offer();

                                lastOffer.setId(lastOfferRes.getId());
                                lastOffer.setAmount(lastOfferRes.getAmount());
                                lastOffer.setCreationDate(DateToolkit.parseDateFromIS8601(lastOfferRes.getCreationDate()));

                                auction.setLastOffer(lastOffer);
                            }
                            auctionsList.add(auction);
                        }
                        statusListener.onSuccess(auctionsList);
                    } else {
                        statusListener.onError(ProcessErrorCodes.AUTH_ERROR);
                    }
                } else {
                    statusListener.onError(ProcessErrorCodes.AUTH_ERROR);
                }
            }

            @Override
            public void onFailure(Call<List<AuctionJSONResponse>> call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }
    public void getPublishedAuctions(IProcessStatusListener<List<Auction>> statusListener) {
        IAuctionsService auctionsService = ApiClient.getInstance().getAuctionsService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());

        auctionsService.getPublishedAuctions(authHeader).enqueue(new Callback<List<AuctionJSONResponse>>() {
            @Override
            public void onResponse(Call<List<AuctionJSONResponse>> call, Response<List<AuctionJSONResponse>> response) {
                if (response.isSuccessful()) {
                    List<AuctionJSONResponse> body = response.body();

                    if (body != null) {
                        List<Auction> auctionsList = new ArrayList<>();

                        for (AuctionJSONResponse auctionRes : body) {
                            Auction auction = new Auction();

                            auction.setId(auctionRes.getId());
                            auction.setTitle(auctionRes.getTitle());
                            auction.setClosesAt(auctionRes.getClosesAt() != null ? DateToolkit.parseDateFromIS8601(auctionRes.getClosesAt()) : null);
                            auction.setDescription(auctionRes.getDescription());
                            auction.setBasePrice(auctionRes.getBasePrice());
                            auction.setMinimumBid(auctionRes.getMinimumBid());
                            auction.setItemCondition(auctionRes.getItemCondition());

                            List<AuctionMediaFileJSONResponse> mediaFilesRes = auctionRes.getMediaFiles();
                            if (mediaFilesRes != null) {
                                List<HypermediaFile> mediaFiles = new ArrayList<>();

                                for (AuctionMediaFileJSONResponse fileRes : mediaFilesRes) {
                                    HypermediaFile file = new HypermediaFile();

                                    file.setId(fileRes.getId());
                                    file.setName(fileRes.getName());
                                    file.setContent(fileRes.getContent());
                                    file.setMimeType(fileRes.getMimeType());
                                    mediaFiles.add(file);
                                }

                                auction.setMediaFiles(mediaFiles);
                            }

                            AuctionLastOfferJSONResponse lastOffer = auctionRes.getLastOffer();
                            if (lastOffer != null) {
                                Offer offer = new Offer();
                                offer.setId(lastOffer.getId());
                                offer.setAmount(lastOffer.getAmount());
                                offer.setCreationDate(DateToolkit.parseDateFromIS8601(lastOffer.getCreationDate()));

                                auction.setLastOffer(offer);
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
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }

    public void approveAuction(int idAuction, int idAuctionCategory, IEmptyProcessStatusListener statusListener) {
        IAuctionsService reviewService = ApiClient.getInstance().getAuctionsService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());
        AuctionApprovalBody approvalBody = new AuctionApprovalBody(idAuction, idAuctionCategory);

        reviewService.approveAuction(authHeader, approvalBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    statusListener.onSuccess();
                } else {
                    System.err.println("Error al aprobar la subasta: " + response.message());
                    statusListener.onError(ProcessErrorCodes.AUTH_ERROR);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.err.println("Error al conectar con el servidor: " + t.getMessage());
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }
    public void rejectAuction(int idAuction, String comments, IEmptyProcessStatusListener statusListener) {
        IAuctionsService auctionsService = ApiClient.getInstance().getAuctionsService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());
        AuctionRejectionBody rejectionBody = new AuctionRejectionBody(idAuction, comments);

        auctionsService.rejectAuction(authHeader, rejectionBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    statusListener.onSuccess();
                } else {
                    statusListener.onError(ProcessErrorCodes.AUTH_ERROR);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }
    public void getAuctionStates(IProcessStatusListener<List<AuctionState>> statusListener) {
        IAuctionsService auctionsService = ApiClient.getInstance().getAuctionsService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());

        auctionsService.getAuctionStates(authHeader).enqueue(new Callback<List<AuctionState>>() {
            @Override
            public void onResponse(Call<List<AuctionState>> call, Response<List<AuctionState>> response) {
                if (response.isSuccessful()) {
                    List<AuctionState> body = response.body();

                    if (body != null) {
                        statusListener.onSuccess(body);
                    } else {
                        statusListener.onError(ProcessErrorCodes.AUTH_ERROR);
                    }
                } else {
                    statusListener.onError(ProcessErrorCodes.AUTH_ERROR);
                }
            }

            @Override
            public void onFailure(Call<List<AuctionState>> call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }

    public void createAuction(
            AuctionCreateBody auctionBody,
            IProcessStatusListener<Auction> statusListener) {
        IAuctionsService auctionsService = ApiClient.getInstance().getAuctionsService();
        String authHeader = String.format("Bearer %s", Session.getInstance().getToken());
        auctionsService.createAuction(authHeader, auctionBody).enqueue(new Callback<AuctionJSONResponse>() {
            @Override
            public void onResponse(Call<AuctionJSONResponse> call, Response<AuctionJSONResponse> response) {
                if (response.isSuccessful()) {
                    AuctionJSONResponse body = response.body();
                    if (body != null) {
                        Auction auction = new Auction();

                        auction.setId(body.getId());
                        statusListener.onSuccess(auction);
                    } else {
                        statusListener.onError(ProcessErrorCodes.AUTH_ERROR);
                    }
                } else {
                    statusListener.onError(ProcessErrorCodes.AUTH_ERROR);
                }
            }
            @Override
            public void onFailure(Call<AuctionJSONResponse> call, Throwable t) {
                statusListener.onError(ProcessErrorCodes.FATAL_ERROR);
            }
        });
    }
}