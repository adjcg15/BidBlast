package com.bidblast.usecases.evaluateauction;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.databinding.FragmentEvaluateAuctionBinding;
import com.bidblast.global.CarouselItemAdapter;
import com.bidblast.global.CarouselViewModel;
import com.bidblast.grpc.Client;
import com.bidblast.lib.ImageToolkit;
import com.bidblast.model.Auction;
import com.bidblast.model.AuctionCategory;
import com.bidblast.model.HypermediaFile;
import com.bidblast.repositories.AuctionsRepository;
import com.bidblast.repositories.IEmptyProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;
import com.bidblast.usecases.consultaauctioncategories.ConsultAuctionCategoriesFragment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class EvaluateAuctionFragment extends Fragment {
    private FragmentEvaluateAuctionBinding binding;
    private CarouselViewModel carouselViewModel;
    private EvaluateAuctionViewModel evaluateAuctionViewModel;
    private CarouselItemAdapter carouselAdapter;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private Client client;
    private File tempFile;
    private BufferedOutputStream bufferedOutputStream;
    private Auction auction;
    private List<HypermediaFile> hypermediaFiles;

    public EvaluateAuctionFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int idAuction = getArguments().getInt("idAuction");
        }
        carouselViewModel = new ViewModelProvider(this).get(CarouselViewModel.class);
        evaluateAuctionViewModel = new ViewModelProvider(this).get(EvaluateAuctionViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEvaluateAuctionBinding.inflate(inflater, container, false);
        mediaPlayer = new MediaPlayer();

        carouselAdapter = new CarouselItemAdapter(carouselViewModel);
        binding.carouselFilesList.setAdapter(carouselAdapter);
        surfaceView = binding.playerSurfaceView;

        setupGoBackButton();
        setupCarouselItemsListener();
        setupSelectedCarouselItemValueListener();
        setupAuctionSpinnerListener();
        setupApproveButtonListener();

        loadAuctionCategories();
        loadAllAuctions();

        return binding.getRoot();
    }

    private void setupApproveButtonListener() {
        binding.aproveAuctionButton.setOnClickListener(v -> {
            approveAuction();
        });
    }

    private void approveAuction() {
        Auction selectedAuction = (Auction) binding.auctionItemsSpinner.getSelectedItem();
        AuctionCategory selectedCategory = (AuctionCategory) binding.auctionCategorySpinner.getSelectedItem();

        if (selectedAuction != null && selectedCategory != null && !selectedCategory.getTitle().equals("Seleccione una categoría")) {
            new AuctionsRepository().approveAuction(
                    selectedAuction.getId(),
                    selectedCategory.getId(),
                    new IEmptyProcessStatusListener() {
                        @Override
                        public void onSuccess() {
                            getActivity().runOnUiThread(() -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                builder.setTitle("Subasta aprobada");
                                builder.setMessage("La subasta ha sido aprobada con éxito.");
                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    navigateToFirstFragment();
                                });
                                builder.show();
                            });
                        }

                        @Override
                        public void onError(ProcessErrorCodes errorCode) {
                            getActivity().runOnUiThread(() -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                builder.setTitle("Error al aprobar subasta");
                                builder.setMessage("No se pudo aprobar la subasta. Intente de nuevo más tarde.");
                                builder.setPositiveButton("OK", null);
                                builder.show();
                            });
                        }
                    }
            );
        } else {
            getActivity().runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Advertencia");
                builder.setMessage("Por favor seleccione una categoría para aprobar la subasta.");
                builder.setPositiveButton("OK", null);
                builder.show();
            });
        }
    }

    private void loadAuctionCategories() {
        evaluateAuctionViewModel.recoverAuctionCategories();
        evaluateAuctionViewModel.getAuctionCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                AuctionCategory header = new AuctionCategory();
                header.setTitle("Seleccione una categoría");
                categories.add(0, header);

                ArrayAdapter<AuctionCategory> adapter = new ArrayAdapter<AuctionCategory>(requireContext(), android.R.layout.simple_spinner_item, categories) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            tv.setTextColor(Color.GRAY);
                        } else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }
                };

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.auctionCategorySpinner.setAdapter(adapter);
            } else {
                showError("No se pudieron cargar las categorías de subastas. Intente de nuevo más tarde.");
            }
        });
    }

    private void loadAllAuctions() {
        evaluateAuctionViewModel.recoverAllAuctions();
        evaluateAuctionViewModel.getAuctionsLiveData().observe(getViewLifecycleOwner(), auctions -> {
            if (auctions != null && !auctions.isEmpty()) {
                ArrayAdapter<Auction> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, auctions);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.auctionItemsSpinner.setAdapter(adapter);
                auction = auctions.get(0);
                fillAuctionDetails();
                loadHypermediaFilesOnCarousel();
                binding.mainContentLayout.setVisibility(View.VISIBLE);
                binding.progressBarLinerLayout.setVisibility(View.GONE);
            } else {
                showNoAuctionsMessage();
            }
        });

        evaluateAuctionViewModel.getAuctionErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error) {
                showNoAuctionsMessage();
            }
        });
    }

    private void fillAuctionDetails() {
        if (auction != null) {
            binding.auctionTitleTextView.setText(auction.getTitle());

            String basePriceFormatted = formatCurrency(auction.getBasePrice());
            binding.auctionPriceTextView.setText(basePriceFormatted);

            String minimumBidFormatted = formatCurrency(auction.getMinimumBid());
            binding.actionMiniumBidTextView.setText(minimumBidFormatted);

            binding.auctionItemStateTextView.setText(auction.getItemCondition());
            binding.auctionDescriptionTextView.setText(auction.getDescription());
            binding.auctionOpenningDaysTextView.setText(String.valueOf(auction.getDaysAvailable()));
        }
    }

    private String formatCurrency(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        String formattedAmount = format.format(amount);
        return formattedAmount + " MXN";
    }

    private void loadHypermediaFilesOnCarousel() {
        if (auction != null) {
            List<HypermediaFile> mediaFiles = auction.getMediaFiles();
            for (HypermediaFile file : mediaFiles) {
                if (file.getContent() == null || file.getContent().isEmpty()) {
                    String content = ImageToolkit.convertDrawableToBase64(requireContext(), R.drawable.video);
                    file.setContent(content);
                }
            }
            carouselViewModel.setFilesList(mediaFiles);
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
            if (selectedFile != null && selectedFile.getMimeType() != null) {
                String hypermediaType = selectedFile.getMimeType();
                if (hypermediaType.startsWith("image")) {
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

    private void setupAuctionSpinnerListener() {
        binding.auctionItemsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Auction selectedAuction = (Auction) parent.getSelectedItem();
                if (selectedAuction != null) {
                    auction = selectedAuction;
                    fillAuctionDetails();
                    loadHypermediaFilesOnCarousel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    private void loadVideoOnSurfaceView(int videoId) {
        mediaPlayer.setOnErrorListener((mp, what, extra) -> true);
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
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {}
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

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    private void showNoAuctionsMessage() {
        binding.errorLoadingAuctionLinearLayout.setVisibility(View.VISIBLE);
        binding.mainContentLayout.setVisibility(View.GONE);
        binding.progressBarLinerLayout.setVisibility(View.GONE);
        ((ImageView)binding.errorLoadingAuctionLinearLayout.findViewById(R.id.errorLoadingImageTextView)).setImageResource(R.drawable.empty_items_generic);
        ((TextView) binding.errorLoadingAuctionLinearLayout.findViewById(R.id.errorLoadingTextView)).setText("No hay subastas para aprobar");
        ((TextView) binding.errorLoadingDescriptionTextView.findViewById(R.id.errorLoadingDescriptionTextView)).setText("No se encontraron subastas para aprobar. Intente de nuevo más tarde.");
    }

    private void navigateToFirstFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }
}
