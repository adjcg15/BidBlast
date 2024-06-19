package com.bidblast.usecases.bidonauction;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.databinding.FragmentBidOnAuctionBinding;
import com.bidblast.grpc.Client;
import com.bidblast.global.CarouselViewModel;
import com.bidblast.global.CarouselItemAdapter;
import com.bidblast.lib.CurrencyToolkit;
import com.bidblast.lib.DateToolkit;
import com.bidblast.lib.ImageToolkit;
import com.bidblast.model.Auction;
import com.bidblast.model.HypermediaFile;
import com.bidblast.repositories.ProcessErrorCodes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class BidOnAuctionFragment extends Fragment {
    private static final String ARG_ID_AUCTION = "id_auction";
    private int idAuction;
    private Auction auction;
    private CarouselViewModel carouselViewModel;
    private BidOnAuctionViewModel bidOnAuctionViewModel;
    private FragmentBidOnAuctionBinding binding;
    private CarouselItemAdapter carouselAdapter;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private static final String TAG = "VideoFragment";
    private Client client;
    private File tempFile;
    private BufferedOutputStream bufferedOutputStream;

    public BidOnAuctionFragment() {

    }

    public static BidOnAuctionFragment newInstance(int idAuction) {
        BidOnAuctionFragment fragment = new BidOnAuctionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID_AUCTION, idAuction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idAuction = getArguments().getInt(ARG_ID_AUCTION);
        }
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        binding = FragmentBidOnAuctionBinding.inflate(inflater, container, false);

        bidOnAuctionViewModel = new BidOnAuctionViewModel();

        carouselViewModel = new CarouselViewModel();
        mediaPlayer = new MediaPlayer();

        carouselAdapter = new CarouselItemAdapter(carouselViewModel);
        binding.carouselFilesList.setAdapter(carouselAdapter);
        surfaceView = binding.playerSurfaceView;

        loadAuction();
        setupGoBackButton();
        setupCarouselItemsListener();
        setupSelectedCarouselItemValueListener();
        setupAuctionRequestListener();
        setupFirstDefaultOfferClick();
        setupSecondDefaultOfferClick();
        setupThirdDefaultOfferClick();
        setupCustomBidClick();

        return binding.getRoot();
    }

    private void loadAuction() {
        bidOnAuctionViewModel.recoverAuction(idAuction);
    }

    private void setupCustomBidClick() {
        binding.customBidButton.setOnClickListener(v -> {
            unselectDefaultOfferButtons();
            startCustomOffer();
        });
    }

    private void unselectDefaultOfferButtons() {
        binding.firstDefaultOfferTextView.setBackgroundResource(R.drawable.black_rounded_border);
        binding.firstDefaultOfferTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.black));
        binding.secondDefaultOfferTextView.setBackgroundResource(R.drawable.black_rounded_border);
        binding.secondDefaultOfferTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.black));
        binding.thirdDefaultOfferTextView.setBackgroundResource(R.drawable.black_rounded_border);
        binding.thirdDefaultOfferTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.black));
    }

    private void startCustomOffer() {
        bidOnAuctionViewModel.startCustomOffer();
        binding.customBidButton.setVisibility(View.GONE);
        binding.customBidEditText.setVisibility(View.VISIBLE);
    }

    private void startDefaultOffer() {
        binding.customBidEditText.setText("");
        binding.customBidEditText.setVisibility(View.GONE);
        binding.customBidButton.setVisibility(View.VISIBLE);
        bidOnAuctionViewModel.startDefaultOffer();
    }

    private void setupFirstDefaultOfferClick() {
        binding.firstDefaultOfferTextView.setOnClickListener(v -> {
            startDefaultOffer();

            binding.firstDefaultOfferTextView.setBackgroundResource(R.drawable.filled_black_rounded_border);
            binding.firstDefaultOfferTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.white));
            binding.secondDefaultOfferTextView.setBackgroundResource(R.drawable.black_rounded_border);
            binding.secondDefaultOfferTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.black));
            binding.thirdDefaultOfferTextView.setBackgroundResource(R.drawable.black_rounded_border);
            binding.thirdDefaultOfferTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.black));

            bidOnAuctionViewModel.setCurrentOffer(bidOnAuctionViewModel.getDefaultBaseOffer().getValue());
        });
    }

    private void setupSecondDefaultOfferClick() {
        binding.secondDefaultOfferTextView.setOnClickListener(v -> {
            startDefaultOffer();

            binding.firstDefaultOfferTextView.setBackgroundResource(R.drawable.black_rounded_border);
            binding.firstDefaultOfferTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.black));
            binding.secondDefaultOfferTextView.setBackgroundResource(R.drawable.filled_black_rounded_border);
            binding.secondDefaultOfferTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.white));
            binding.thirdDefaultOfferTextView.setBackgroundResource(R.drawable.black_rounded_border);
            binding.thirdDefaultOfferTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.black));

            bidOnAuctionViewModel.setCurrentOffer(bidOnAuctionViewModel.getDefaultBaseOffer().getValue() * 2);
        });
    }

    private void setupThirdDefaultOfferClick() {
        binding.thirdDefaultOfferTextView.setOnClickListener(v -> {
            startDefaultOffer();

            binding.firstDefaultOfferTextView.setBackgroundResource(R.drawable.black_rounded_border);
            binding.firstDefaultOfferTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.black));
            binding.secondDefaultOfferTextView.setBackgroundResource(R.drawable.black_rounded_border);
            binding.secondDefaultOfferTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.black));
            binding.thirdDefaultOfferTextView.setBackgroundResource(R.drawable.filled_black_rounded_border);
            binding.thirdDefaultOfferTextView.setTextColor(binding.getRoot().getContext().getColor(R.color.white));

            bidOnAuctionViewModel.setCurrentOffer(bidOnAuctionViewModel.getDefaultBaseOffer().getValue() * 3);
        });
    }

    private void setupAuctionRequestListener() {
        bidOnAuctionViewModel.getAuctionRequestStatus().observe(getViewLifecycleOwner(), requestStatus -> {
            if (requestStatus == RequestStatus.ERROR) {
                ProcessErrorCodes errorCode = bidOnAuctionViewModel.getAuctionErrorCode().getValue();
                if(errorCode == ProcessErrorCodes.AUTH_ERROR) {
                    finishUserSession();
                } else {
                    showErrorLoadingAuction();
                }
            }

            if (requestStatus == RequestStatus.DONE) {
                auction = bidOnAuctionViewModel.getAuction().getValue();
                showMainView();
                showAuctionInformation();
                showDefaultOfferButtons();
            }
        });
    }

    private void showMainView() {
        binding.mainViewScrollView.setVisibility(View.VISIBLE);
        binding.errorLoadingAuctionLinearLayout.setVisibility(View.GONE);
        binding.progressBarLinerLayout.setVisibility(View.GONE);
    }

    private void showAuctionInformation() {
        if(auction.getAuctionState() != null) {
            binding.articleStateTextView.setText(auction.getAuctionState().toUpperCase());
        }
        binding.auctionTitleTextView.setText(auction.getTitle());

        if(auction.getLastOffer() != null) {
            float lastOfferAmount = auction.getLastOffer().getAmount();
            binding.auctionPriceTextView.setText(CurrencyToolkit.parseToMXN(lastOfferAmount));
        } else {
            binding.auctionPriceLabelTextView.setText(R.string.bidonauction_base_price);
            binding.auctionPriceTextView.setText(CurrencyToolkit.parseToMXN(auction.getBasePrice()));
        }

        binding.auctionClosingDateTextView.setText(DateToolkit.parseToFullDateWithHour(auction.getClosesAt()));
        binding.auctionDescriptionTextView.setText(auction.getDescription());

        if(auction.getMinimumBid() != 0) {
            binding.auctionMinimumBidTextView.setText(CurrencyToolkit.parseToMXN(auction.getMinimumBid()));
        } else {
            binding.auctionMinimumBidMessageTextView.setText(R.string.bidonauction_not_minimum_bid);
            binding.auctionMinimumBidTextView.setVisibility(View.GONE);
        }

        showHypermediaFilesOnCarousel();
    }

    private void showHypermediaFilesOnCarousel() {
        for (HypermediaFile file : auction.getMediaFiles()) {
            if (file.getContent() == null || file.getContent().isEmpty()) {
                String content = ImageToolkit.convertDrawableToBase64(requireContext(), R.drawable.video);
                file.setContent(content);
            }
        }

        carouselViewModel.setFilesList(auction.getMediaFiles());
    }

    private void showDefaultOfferButtons() {
        if(auction != null) {
            float baseOffer = 100;
            if(auction.getMinimumBid() != 0) {
                baseOffer = auction.getMinimumBid();
            }

            binding.firstDefaultOfferTextView.setText(String.format(Locale.getDefault(), "%.1f", baseOffer));
            binding.secondDefaultOfferTextView.setText(String.format(Locale.getDefault(), "%.1f", baseOffer * 2));
            binding.thirdDefaultOfferTextView.setText(String.format(Locale.getDefault(), "%.1f", baseOffer * 3));
            bidOnAuctionViewModel.setDefaultBaseOffer(baseOffer);
        }
    }

    private void showErrorLoadingAuction() {
        binding.errorLoadingAuctionLinearLayout.setVisibility(View.VISIBLE);
        binding.progressBarLinerLayout.setVisibility(View.GONE);
        binding.mainViewScrollView.setVisibility(View.GONE);
    }

    private void finishUserSession() {

    }

    private void setupGoBackButton() {
        binding.goBackImageView.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });
    }

    private void setupCarouselItemsListener() {
        carouselViewModel.getFilesList().observe(getViewLifecycleOwner(), filesList -> {
            carouselAdapter.submitList(filesList);
        });
    }

    private void setupSelectedCarouselItemValueListener() {
        carouselViewModel.getSelectedFile().observe(getViewLifecycleOwner(), selectedFile -> {
            if(selectedFile != null) {
                String hypermediaType = selectedFile.getMimeType();

                if(hypermediaType.startsWith("image")) {
                    binding.showedFileImageView.setImageBitmap(
                        ImageToolkit.parseBitmapFromBase64(selectedFile.getContent())
                    );
                    binding.showedFileImageView.setVisibility(View.VISIBLE);
                    deleteVideoInCache();
                    binding.playerSurfaceView.setVisibility(View.GONE);
                } else if (hypermediaType.startsWith("video")) {
                    binding.showedFileImageView.setVisibility(View.GONE);
                    loadVideoOnSurfaceView(selectedFile.getId());
                    binding.progressBarVideo.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadVideoOnSurfaceView(int videoId) {
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            //TODO Manejo de mensaje cuando hay error en el stream
            return true;
        });

        mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        mediaPlayer.setOnCompletionListener(mp -> {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(tempFile.getAbsolutePath());
                mediaPlayer.prepareAsync();
                mediaPlayer.start();
            } catch (IOException e) {
                Log.e(TAG, "Error setting data source or preparing MediaPlayer", e);
            }
        });

        client = new Client(new Handler(msg -> {
            if (msg.what == 1) {
                List<byte[]> videoFragments = (List<byte[]>) msg.obj;
                for (byte[] videoChunk : videoFragments) {
                    addVideoChunk(videoChunk);
                }
            }
            return true;
        }));

        client.streamVideo(videoId);

        try {
            tempFile = File.createTempFile("video", ".mp4", requireContext().getCacheDir());
            bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(tempFile.toPath()));
        } catch (IOException e) {
            Log.e(TAG, "Error creating temp file", e);
        }

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                mediaPlayer.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            }
        });
    }

    private void addVideoChunk(byte[] videoChunk) {
        if (videoChunk != null && videoChunk.length > 0) {
            try {
                binding.progressBarVideo.setVisibility(View.GONE);
                binding.playerSurfaceView.setVisibility(View.VISIBLE);
                bufferedOutputStream.flush();
                bufferedOutputStream.write(videoChunk);
                if (!mediaPlayer.isPlaying() && mediaPlayer.getCurrentPosition() == 0) {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(tempFile.getAbsolutePath());
                    mediaPlayer.prepareAsync();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error writing video chunk to file", e);
            }
        } else {
            Log.e(TAG, "Received empty video chunk or null");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deleteVideoInCache();
    }

    private void deleteVideoInCache() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
        if (Client.getChannelStatus() && client != null) {
            client.shutdown();
            client = null;
        }
        if (bufferedOutputStream != null) {
            try {
                bufferedOutputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing BufferedOutputStream", e);
            }
        }
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }
}