package com.bidblast.usecases.consultoffersonauction;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.databinding.FragmentOffersOnAuctionBinding;
import com.bidblast.global.CarouselItemAdapter;
import com.bidblast.global.CarouselViewModel;
import com.bidblast.grpc.Client;
import com.bidblast.lib.ImageToolkit;
import com.bidblast.model.Auction;
import com.bidblast.model.HypermediaFile;
import com.bidblast.repositories.ProcessErrorCodes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OffersOnAuctionFragment extends Fragment {
    private FragmentOffersOnAuctionBinding binding;
    private CarouselViewModel carouselViewModel;
    private OffersOnAuctionViewModel offersOnAuctionViewModel;
    private static final String ARG_ID_AUCTION = "id_auction";
    private int idAuction;
    private CarouselItemAdapter carouselAdapter;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private static final String TAG = "VideoFragment";
    private Client client;
    private File tempFile;
    private BufferedOutputStream bufferedOutputStream;
    private Auction auction;

    public OffersOnAuctionFragment() {

    }

    public static OffersOnAuctionFragment newInstance(int idAuction) {
        OffersOnAuctionFragment fragment = new OffersOnAuctionFragment();
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
        carouselViewModel = new ViewModelProvider(this).get(CarouselViewModel.class);
        offersOnAuctionViewModel = new ViewModelProvider(this).get(OffersOnAuctionViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOffersOnAuctionBinding.inflate(inflater, container, false);
        mediaPlayer = new MediaPlayer();

        carouselAdapter = new CarouselItemAdapter(carouselViewModel);
        binding.carouselFilesList.setAdapter(carouselAdapter);
        surfaceView = binding.playerSurfaceView;
        getAuction();
        setupGoBackButton();
        setupCarouselItemsListener();
        setupSelectedCarouselItemValueListener();
        auctionStatusListener();

        return binding.getRoot();
    }

    private void getAuction() {
        if (offersOnAuctionViewModel.getAuctionRequestStatus().getValue() != RequestStatus.LOADING) {
            offersOnAuctionViewModel.recoverAuction(idAuction);
            binding.progressBarLinerLayout.setVisibility(View.VISIBLE);
        }
    }

    private void showMediaFilesSection() {
        binding.playerConstraintlayout.setVisibility(View.VISIBLE);
        binding.hypermediaFilesHorizontalScrollView.setVisibility(View.VISIBLE);
    }

    private void auctionStatusListener() {
        offersOnAuctionViewModel.getAuctionRequestStatus().observe(getViewLifecycleOwner(), requestStatus -> {
            if (requestStatus == RequestStatus.DONE) {
                binding.progressBarLinerLayout.setVisibility(View.GONE);
                showMediaFilesSection();
                auction = offersOnAuctionViewModel.getAuction().getValue();
                loadHypermediaFilesOnCarousel();
            }
            if (requestStatus == RequestStatus.ERROR) {
                binding.progressBarLinerLayout.setVisibility(View.GONE);
                ProcessErrorCodes errorCode = offersOnAuctionViewModel.getAuctionErrorCode().getValue();

                if(errorCode != null) {
                    showAuctionImageError();
                }
            }
        });
    }

    private void showAuctionImageError() {
        binding.playerConstraintlayout.setVisibility(View.GONE);
        binding.hypermediaFilesHorizontalScrollView.setVisibility(View.GONE);
        binding.errorLoadingAuctionLinearLayout.setVisibility(View.VISIBLE);
    }

    private void loadHypermediaFilesOnCarousel() {
        for (HypermediaFile file : auction.getMediaFiles()) {
            if (file.getContent() == null || file.getContent().isEmpty()) {
                String content = ImageToolkit.convertDrawableToBase64(requireContext(), R.drawable.video);
                file.setContent(content);
            }
        }
        carouselViewModel.setFilesList(auction.getMediaFiles());
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
}