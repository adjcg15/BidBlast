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

import com.bidblast.databinding.FragmentBidOnAuctionBinding;
import com.bidblast.gRPC.Client;
import com.bidblast.global.CarouselViewModel;
import com.bidblast.global.CarouselItemAdapter;
import com.bidblast.lib.ImageToolkit;
import com.bidblast.model.HypermediaFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class BidOnAuctionFragment extends Fragment {
    private static final String ARG_ID_AUCTION = "id_auction";
    private int idAuction;
    private CarouselViewModel carouselViewModel;
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

        carouselViewModel = new CarouselViewModel();
        mediaPlayer = new MediaPlayer();

        carouselAdapter = new CarouselItemAdapter(carouselViewModel);
        binding.carouselFilesList.setAdapter(carouselAdapter);
        surfaceView = binding.playerSurfaceView;

        setupGoBackButton();
        setupCarouselItemsListener();
        setupSelectedCarouselItemValueListener();
        recoverAuctionHypermediaFiles();

        return binding.getRoot();
    }

    private void loadVideoOnSurfaceView(int videoId) {
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            //TODO Manejo de mensaje cuando hay error en el stream
            return true;
        });

        mediaPlayer.setOnPreparedListener(MediaPlayer::start);

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
            tempFile = File.createTempFile("video", ".avi", requireContext().getCacheDir());
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

    private void recoverAuctionHypermediaFiles() {
        //La lógica de recuperación de los archivos la desacoplé de la lógica de carga de esta información
        //en el ViewModel del carrusel, para que así cada uno pueda cargar esa información como sea más
        //conveniente para su CU. En este caso, simulo esta carga con objetos estáticos
        List<HypermediaFile> recoveredFiles = new ArrayList<>();
        recoveredFiles.add(new HypermediaFile(
            1,
            "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",
            "image-1-test",
            "image/jpeg"
        ));
        recoveredFiles.add(new HypermediaFile(
            2,
            "",
            "video-1-test",
            "video/mp3"
        ));
        recoveredFiles.add(new HypermediaFile(
            3,
            "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",
            "image-2-test",
            "image/jpeg"
        ));

        //Lo que sí es importante es avisarle al ViewModel sobre la carga de los archivos
        carouselViewModel.setFilesList(recoveredFiles);
    }

    private void setupSelectedCarouselItemValueListener() {
        //Cada vez que una imagen o video se selecciona en el carrusel, el view model es avisado,
        //por lo que aquí directamente se maneja lo que se desee hacer con ese nuevo elemento seleccionado.
        //En este caso, solo mostrarlo en la vista principal
        carouselViewModel.getSelectedFile().observe(getViewLifecycleOwner(), selectedFile -> {
            if(selectedFile != null) {
                String hypermediaType = selectedFile.getMimeType();

                if(hypermediaType.startsWith("image")) {
                    binding.showedFileImageView.setImageBitmap(
                        ImageToolkit.parseBitmapFromBase64(selectedFile.getContent())
                    );
                    binding.showedFileImageView.setVisibility(View.VISIBLE);
                    // Siempre que se seleccione una imagen, se debe eliminar el video en caso de haberse
                    // solicitado antes
                    deleteVideoInCache();
                    binding.playerSurfaceView.setVisibility(View.GONE);
                } else if (hypermediaType.startsWith("video")) {
                    binding.showedFileImageView.setVisibility(View.GONE);
                    binding.playerSurfaceView.setVisibility(View.VISIBLE);
                    loadVideoOnSurfaceView(2);
                }
            }
        });
    }
}