package com.bidblast.usecases.consultoffersonauction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.databinding.FragmentOffersOnAuctionBinding;
import com.bidblast.global.CarouselItemAdapter;
import com.bidblast.global.CarouselViewModel;
import com.bidblast.grpc.Client;
import com.bidblast.lib.DateToolkit;
import com.bidblast.lib.ImageToolkit;
import com.bidblast.model.Auction;
import com.bidblast.model.HypermediaFile;
import com.bidblast.model.Offer;
import com.bidblast.repositories.ProcessErrorCodes;
import com.bidblast.repositories.businesserrors.SaveAuctionCategoryCodes;
import com.google.android.material.snackbar.Snackbar;

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
    private static final int TOTAL_OFFERS_TO_LOAD = 5;
    private static final String ARG_ID_AUCTION = "id_auction";
    private int idAuction;
    private CarouselItemAdapter carouselAdapter;
    private OfferDetailsAdapter offerDetailsAdapter;
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
        offerDetailsAdapter = new OfferDetailsAdapter();
        offerDetailsAdapter.setOnAuctionClickListener(this::handleBlockUserFragment);
        binding.carouselFilesList.setAdapter(carouselAdapter);
        binding.offersListRecyclerView.setAdapter(offerDetailsAdapter);
        surfaceView = binding.playerSurfaceView;
        loadAuction();
        loadOffers();
        setupGoBackButton();
        setupCarouselItemsListener();
        setupOffersListListener();
        setupSelectedCarouselItemValueListener();
        setupAuctionStatusListener();
        setupOffersListStatusListener();
        setupBlockUserStatusListener();
        setupStillOffersLeftToLoadListener();
        setupRecyclerViewScrollListener();

        return binding.getRoot();
    }

    private void loadAuction() {
        offersOnAuctionViewModel.recoverAuction(idAuction);
        binding.progressBarLinerLayout.setVisibility(View.VISIBLE);
    }

    private void loadOffers() {
        offersOnAuctionViewModel.recoverOffers(idAuction, TOTAL_OFFERS_TO_LOAD);
    }

    private void handleBlockUserFragment(int idProfile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirmación");
        builder.setMessage("¿Estás seguro de que deseas bloquear al comprador?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (offersOnAuctionViewModel.getBlockUserRequestStatus().getValue() != RequestStatus.LOADING) {
                    offersOnAuctionViewModel.blockUser(idProfile, idAuction);
                };
            }
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupBlockUserStatusListener() {
        offersOnAuctionViewModel.getBlockUserRequestStatus().observe(getViewLifecycleOwner(), requestStatus -> {
            if (requestStatus == RequestStatus.DONE) {
                String successMessage = getString(R.string.consultoffers_block_user_success_message);
                Snackbar.make(binding.getRoot(), successMessage, Snackbar.LENGTH_SHORT).show();
                offersOnAuctionViewModel.clearOffersList();
                offerDetailsAdapter = new OfferDetailsAdapter();
                loadOffers();
            }

            if (requestStatus == RequestStatus.ERROR) {
                Snackbar.make(binding.getRoot(), "No se pudo bloquear al comprador", Snackbar.LENGTH_SHORT).show();
                /*SaveAuctionCategoryCodes errorCode = offersOnAuctionViewModel.get().getValue();

                if(errorCode != null) {
                    showSaveAuctionCategoryError(errorCode);
                }*/
            }
        });
    }

    private void setupAuctionStatusListener() {
        offersOnAuctionViewModel.getAuctionRequestStatus().observe(getViewLifecycleOwner(), requestStatus -> {
            if (requestStatus == RequestStatus.DONE) {
                binding.progressBarLinerLayout.setVisibility(View.GONE);
                binding.mainViewScrollView.setVisibility(View.VISIBLE);
                auction = offersOnAuctionViewModel.getAuction().getValue();
                loadHypermediaFilesOnCarouselAndAuctionInformation();
            }
            if (requestStatus == RequestStatus.ERROR) {
                binding.progressBarLinerLayout.setVisibility(View.GONE);
                ProcessErrorCodes errorCode = offersOnAuctionViewModel.getAuctionErrorCode().getValue();

                if(errorCode != null) {
                    binding.mainViewScrollView.setVisibility(View.GONE);
                    binding.errorLoadingOffersLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadHypermediaFilesOnCarouselAndAuctionInformation() {
        for (HypermediaFile file : auction.getMediaFiles()) {
            if (file.getContent() == null || file.getContent().isEmpty()) {
                String content = ImageToolkit.convertDrawableToBase64(requireContext(), R.drawable.video);
                file.setContent(content);
            }
        }
        carouselViewModel.setFilesList(auction.getMediaFiles());
        binding.auctionTitleTextView.setText(auction.getTitle());
        String timeLeft = DateToolkit.parseToFullDateWithHour(auction.getClosesAt());
        binding.auctionClosingDateTextView.setText(String.format(
                binding.getRoot().getContext().getString(
                        R.string.consultcreatedauctions_available_date_message
                ),
                timeLeft
        ));
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
            goToPreviousWindow();
        });
    }

    private void goToPreviousWindow() {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
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

    private void setupOffersListStatusListener() {
        offersOnAuctionViewModel.getOffersListRequestStatus().observe(getViewLifecycleOwner(), requestStatus -> {
            binding.emptyOffersMessageLinearLayout.setVisibility(View.GONE);

            if(requestStatus == RequestStatus.LOADING) {
                binding.loadingOffersTextView.setVisibility(View.VISIBLE);
            } else {
                binding.loadingOffersTextView.setVisibility(View.GONE);

                if(requestStatus == RequestStatus.DONE) {
                    binding.errorLoadingOffersLinearLayout.setVisibility(View.GONE);

                    List<Offer> offers = offersOnAuctionViewModel.getOffersList().getValue();
                    if(offers != null && offers.size() != 0) {
                        binding.offersListRecyclerView.setVisibility(View.VISIBLE);
                        binding.emptyOffersMessageLinearLayout.setVisibility(View.GONE);
                    } else {
                        binding.offersListRecyclerView.setVisibility(View.GONE);
                        binding.emptyOffersMessageLinearLayout.setVisibility(View.VISIBLE);
                        binding.allOffersLoadedTextView.setVisibility(View.GONE);
                    }
                } else if (requestStatus == RequestStatus.ERROR) {
                    binding.errorLoadingOffersLinearLayout.setVisibility(View.VISIBLE);
                    binding.offersListRecyclerView.setVisibility(View.GONE);
                    binding.allOffersLoadedTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupStillOffersLeftToLoadListener() {
        offersOnAuctionViewModel.getStillOffersLeftToLoad().observe(getViewLifecycleOwner(), stillOffersLeftToLoad -> {
            if(stillOffersLeftToLoad) {
                binding.allOffersLoadedTextView.setVisibility(View.GONE);
            } else {
                binding.allOffersLoadedTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupOffersListListener() {
        offersOnAuctionViewModel.getOffersList().observe(getViewLifecycleOwner(), offersList -> {
            if (offersList != null) {
                offerDetailsAdapter.submitList(offersList);
            } else {
                String successMessage = "Ya no hay ofertas, será redirigido a la ventana anterior";
                Snackbar.make(binding.getRoot(), successMessage, Snackbar.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(this::goToPreviousWindow, 4000);
            }
        });
    }

    private void setupRecyclerViewScrollListener() {
        binding.offersListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= TOTAL_OFFERS_TO_LOAD
                            && Boolean.TRUE.equals(offersOnAuctionViewModel.getStillOffersLeftToLoad().getValue())
                            && offersOnAuctionViewModel.getOffersListRequestStatus().getValue() != RequestStatus.LOADING) {
                        loadOffers();
                    }
                }
            }
        });
    }
}