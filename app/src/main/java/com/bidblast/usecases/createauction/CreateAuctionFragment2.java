package com.bidblast.usecases.createauction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bidblast.R;
import com.bidblast.api.requests.auctions.AuctionCreateBody;
import com.bidblast.model.Auction;
import com.bidblast.model.HypermediaFile;
import com.bidblast.repositories.AuctionsRepository;
import com.bidblast.repositories.IProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
public class CreateAuctionFragment2 extends Fragment {
    private static final int MAX_IMAGES = 7;
    private static final int MAX_IMAGE_SIZE_MB = 2;
    private static final int MAX_VIDEO_SIZE_MB = 5;

    private CreateAuctionViewModel viewModel;
    private MediaPagerAdapter mediaPagerAdapter;
    private ActivityResultLauncher<Intent> selectMediaLauncher;
    private View rootView;
    private ProgressBar progressBar;
    private int itemStatus; // To store the passed itemStatus

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectMediaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            List<Uri> selectedUris = new ArrayList<>();
                            if (data.getClipData() != null) {
                                int count = data.getClipData().getItemCount();
                                for (int i = 0; i < count; i++) {
                                    Uri mediaUri = data.getClipData().getItemAt(i).getUri();
                                    selectedUris.add(mediaUri);
                                }
                            } else if (data.getData() != null) {
                                Uri mediaUri = data.getData();
                                selectedUris.add(mediaUri);
                            }
                            handleSelectedMedia(selectedUris);
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_post_item_for_auction2, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(CreateAuctionViewModel.class);
        progressBar = rootView.findViewById(R.id.progressBar);

        ViewPager2 viewPagerMedia = rootView.findViewById(R.id.viewPagerMedia);

        List<Object> initialItems = new ArrayList<>();
        initialItems.add(R.drawable.multimedia_icon);

        mediaPagerAdapter = new MediaPagerAdapter(initialItems, requireContext());
        viewPagerMedia.setAdapter(mediaPagerAdapter);

        rootView.findViewById(R.id.addMediaButton).setOnClickListener(v -> openGalleryForMedia());

        viewModel.getSelectedImages().observe(getViewLifecycleOwner(), uris -> updateMediaAdapter());
        viewModel.getSelectedVideo().observe(getViewLifecycleOwner(), uri -> updateMediaAdapter());

        Bundle args = getArguments();
        if (args != null) {
            viewModel.setAuctionTitle(args.getString("auctionTitle"));
            viewModel.setItemDescription(args.getString("itemDescription"));
            viewModel.setOpeningDays(args.getInt("openingDays"));
            itemStatus = args.getInt("itemStatus"); // Get the passed itemStatus
        }

        Button createAuctionButton = rootView.findViewById(R.id.createAuctionButton);
        createAuctionButton.setOnClickListener(v -> createAuction());

        return rootView;
    }

    private void openGalleryForMedia() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        selectMediaLauncher.launch(intent);
    }

    private void handleSelectedMedia(List<Uri> mediaUris) {
        int imageCount = viewModel.getSelectedImages().getValue().size();
        boolean videoSelected = viewModel.getSelectedVideo().getValue() != null;

        showProgressBar();

        for (Uri mediaUri : mediaUris) {
            String mimeType = getActivity().getContentResolver().getType(mediaUri);
            if (mimeType != null && mimeType.startsWith("image")) {
                if (imageCount < MAX_IMAGES) {
                    if (isValidImageSize(mediaUri)) {
                        viewModel.addImage(mediaUri);
                        imageCount++;
                    } else {
                        Toast.makeText(getContext(), "Cada imagen debe ser menor a 2MB", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Ya has seleccionado el máximo de 7 imágenes", Toast.LENGTH_SHORT).show();
                    break;
                }
            } else if (mimeType != null && mimeType.startsWith("video")) {
                if (!videoSelected) {
                    if (isValidVideoSize(mediaUri)) {
                        viewModel.setSelectedVideo(mediaUri);
                        videoSelected = true;
                    } else {
                        Toast.makeText(getContext(), "El video debe ser menor a 5MB", Toast.LENGTH_SHORT).show();
                        break;
                    }
                } else {
                    Toast.makeText(getContext(), "Ya has seleccionado un video", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }

        hideProgressBar();
        updateMediaAdapter();
    }

    private boolean isValidImageSize(Uri imageUri) {
        return isValidFileSize(imageUri, MAX_IMAGE_SIZE_MB);
    }

    private boolean isValidVideoSize(Uri videoUri) {
        return isValidFileSize(videoUri, MAX_VIDEO_SIZE_MB);
    }

    private boolean isValidFileSize(Uri fileUri, int maxSizeMb) {
        Cursor returnCursor = getActivity().getContentResolver().query(fileUri, null, null, null, null);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        if (sizeIndex == -1) {
            returnCursor.close();
            return false;
        }
        returnCursor.moveToFirst();
        long size = returnCursor.getLong(sizeIndex);
        returnCursor.close();
        return size <= maxSizeMb * 1024 * 1024;
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void updateMediaAdapter() {
        List<Object> items = new ArrayList<>(viewModel.getSelectedImages().getValue());
        Uri videoUri = viewModel.getSelectedVideo().getValue();
        if (videoUri != null) {
            items.add(new VideoItem(videoUri));
        }

        if (items.isEmpty()) {
            items.add(R.drawable.multimedia_icon);
        }

        mediaPagerAdapter.updateMedia(items);

        if (viewModel.getSelectedImages().getValue().size() >= MAX_IMAGES && viewModel.getSelectedVideo().getValue() != null) {
            rootView.findViewById(R.id.addMediaButton).setVisibility(View.GONE);
        } else {
            rootView.findViewById(R.id.addMediaButton).setVisibility(View.VISIBLE);
        }
    }

    private void createAuction() {
        String title = viewModel.getAuctionTitle().getValue();
        String description = viewModel.getItemDescription().getValue();
        int openingDays = viewModel.getOpeningDays().getValue();
        double basePrice = viewModel.getBasePrice().getValue() != null ? viewModel.getBasePrice().getValue() : 0.0;
        double minimumBid = viewModel.getMinimumBid().getValue() != null ? viewModel.getMinimumBid().getValue() : 0.0;
        List<Uri> images = viewModel.getSelectedImages().getValue();
        Uri video = viewModel.getSelectedVideo().getValue();

        if (title == null || title.isEmpty()) {
            showAlert("El título es obligatorio");
            return;
        }

        if (description == null || description.isEmpty()) {
            showAlert("La descripción es obligatoria");
            return;
        }

        if (openingDays <= 0) {
            showAlert("Los días disponibles deben ser mayores a cero");
            return;
        }

        if (images.isEmpty() && video == null) {
            showAlert("Debes subir al menos una imagen o un video");
            return;
        }

        showProgressBar();

        List<HypermediaFile> uploadedMediaFiles = new ArrayList<>();
        for (Uri imageUri : images) {
            uploadedMediaFiles.add(new HypermediaFile(getFileName(imageUri), convertFileToBase64(imageUri), "image/jpeg"));
        }
        if (video != null) {
            uploadedMediaFiles.add(new HypermediaFile(getFileName(video), convertFileToBase64(video), "video/mp4"));
        }

        AuctionCreateBody auctionBody = new AuctionCreateBody(
                title, description, basePrice, minimumBid, openingDays, itemStatus, uploadedMediaFiles
        );

        AuctionsRepository auctionsRepository = new AuctionsRepository();
        auctionsRepository.createAuction(auctionBody, new IProcessStatusListener<Auction>() {
            @Override
            public void onSuccess(Auction auction) {
                getActivity().runOnUiThread(() -> {
                    hideProgressBar();
                    sendVideos(auction.getId());
                    showAlert("Subasta creada con éxito");
                });
            }

            @Override
            public void onError(ProcessErrorCodes errorStatus) {
                getActivity().runOnUiThread(() -> {
                    hideProgressBar();
                    showAlert("Error al crear la subasta. Intenta de nuevo.");
                });
            }
        });
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String convertFileToBase64(Uri uri) {
        try (InputStream inputStream = getContext().getContentResolver().openInputStream(uri)) {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("Información")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void sendVideos(int auctionId) {
        // Implementa la lógica para enviar el video al servidor usando gRPC
    }
}
