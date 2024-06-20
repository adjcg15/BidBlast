package com.bidblast.usecases.createauction;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.CursorLoader;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bidblast.R;
import com.bidblast.api.requests.auctions.AuctionCreateBody;
import com.bidblast.databinding.FragmentPostItemForAuction2Binding;
import com.bidblast.databinding.FragmentPostItemForAuctionBinding;
import com.bidblast.menus.mainmenu.MainMenuActivity;
import com.bidblast.menus.moderatormenu.ModeratorMenuActivity;
import com.bidblast.model.Auction;
import com.bidblast.model.HypermediaFile;
import com.bidblast.repositories.AuctionsRepository;
import com.bidblast.repositories.IProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;
import com.bidblast.usecases.searchauction.SearchAuctionFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.bidblast.grpc.Server;

public class CreateAuctionFragment2 extends Fragment {
    private static final int MAX_IMAGES = 7;
    private static final int MAX_IMAGE_SIZE_MB = 2;
    private static final int MAX_VIDEO_SIZE_MB = 5;
    private static final String MIME_TYPE_VIDEO = "video/mp4";

    private CreateAuctionViewModel viewModel;
    private MediaPagerAdapter mediaPagerAdapter;
    private ActivityResultLauncher<Intent> selectMediaLauncher;
    private ProgressBar progressBar;
    private int itemStatus;

    private EditText basePriceEditText;
    private TextView basePriceErrorTextView;
    private TextView selectMultimediaErrorTextView;
    private TextView selectedBidOption;

    private TextView oneHundredOfferTextView;
    private TextView oneHundredFiftyOfferTextView;
    private TextView twoHundredOfferTextView;
    private TextView twoHundredfiftyOfferTextView;
    private TextView threeHundredOfferTextView;

    private FragmentPostItemForAuction2Binding binding;
    private File selectedVideoFile;
    private static final int REQUEST_CODE = 100;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectMediaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleActivityResult
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPostItemForAuction2Binding.inflate(inflater, container, false);
        initializeViews();
        setupViewModel();
        setupMediaPagerAdapter();
        setupObservers();
        retrieveArguments();
        setupClickListeners();
        setupListeners();
        return binding.getRoot();
    }

    private void initializeViews() {
        progressBar = binding.progressBar;
        basePriceEditText = binding.basePriceEditText;
        basePriceErrorTextView = binding.basePriceErrorTextView;
        selectMultimediaErrorTextView = binding.selectMultimediaErrorTextView;

        oneHundredOfferTextView = binding.oneHundredOfferTextView;
        oneHundredFiftyOfferTextView = binding.oneHundredFiftyOfferTextView;
        twoHundredOfferTextView = binding.twoHundredOfferTextView;
        twoHundredfiftyOfferTextView = binding.twoHundredfiftyOfferTextView;
        threeHundredOfferTextView = binding.threeHundredOfferTextView;

        setTextLimiter(basePriceEditText, 5);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CreateAuctionViewModel.class);
    }

    private void setupMediaPagerAdapter() {
        List<Object> initialItems = new ArrayList<>();
        initialItems.add(R.drawable.multimedia_icon);
        mediaPagerAdapter = new MediaPagerAdapter(initialItems, requireContext());
        binding.viewPagerMedia.setAdapter(mediaPagerAdapter);
    }

    private void setupObservers() {
        viewModel.getSelectedImages().observe(getViewLifecycleOwner(), uris -> updateMediaAdapter());
        viewModel.getSelectedVideo().observe(getViewLifecycleOwner(), uri -> updateMediaAdapter());
    }

    private void retrieveArguments() {
        Bundle args = getArguments();
        if (args != null) {
            viewModel.setAuctionTitle(args.getString("auctionTitle"));
            viewModel.setItemDescription(args.getString("itemDescription"));
            viewModel.setOpeningDays(args.getInt("openingDays"));
            itemStatus = args.getInt("itemStatus");
        }
    }

    private void setupClickListeners() {
        binding.addMediaButton.setOnClickListener(v -> openGalleryForMedia());
        binding.createAuctionButton.setOnClickListener(v -> createAuction());

        oneHundredOfferTextView.setOnClickListener(v -> selectBidOption(oneHundredOfferTextView));
        oneHundredFiftyOfferTextView.setOnClickListener(v -> selectBidOption(oneHundredFiftyOfferTextView));
        twoHundredOfferTextView.setOnClickListener(v -> selectBidOption(twoHundredOfferTextView));
        twoHundredfiftyOfferTextView.setOnClickListener(v -> selectBidOption(twoHundredfiftyOfferTextView));
        threeHundredOfferTextView.setOnClickListener(v -> selectBidOption(threeHundredOfferTextView));

        binding.cancelCreateAuctionButton.setOnClickListener(v -> navigateToMainMenu());
    }

    private void handleActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            List<Uri> selectedUris = extractUrisFromIntent(result.getData());
            handleSelectedMedia(selectedUris);
        }
    }

    private List<Uri> extractUrisFromIntent(Intent data) {
        List<Uri> selectedUris = new ArrayList<>();
        if (data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                selectedUris.add(data.getClipData().getItemAt(i).getUri());
            }
        } else if (data.getData() != null) {
            selectedUris.add(data.getData());
        }
        return selectedUris;
    }

    private void setTextLimiter(EditText editText, int maxLength) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    private void selectBidOption(TextView selectedView) {
        if (selectedBidOption != null) {
            selectedBidOption.setBackgroundResource(R.drawable.black_rounded_border);
        }
        selectedBidOption = selectedView;
        selectedBidOption.setBackgroundResource(R.drawable.blue_rounded_border);
    }

    private void openGalleryForMedia() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"image/jpeg", "image/png", "video/mp4"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        selectMediaLauncher.launch(intent);
    }

    private void handleSelectedMedia(List<Uri> mediaUris) {
        int imageCount = viewModel.getSelectedImages().getValue().size();
        boolean videoSelected = viewModel.getSelectedVideo().getValue() != null;

        showProgressBar();
        for (Uri mediaUri : mediaUris) {
            handleMediaUri(mediaUri, imageCount, videoSelected);
        }
        hideProgressBar();
        updateMediaAdapter();
    }

    private void handleMediaUri(Uri mediaUri, int imageCount, boolean videoSelected) {
        String mimeType = getActivity().getContentResolver().getType(mediaUri);
        if (mimeType != null && mimeType.startsWith("image")) {
            handleImageUri(mediaUri, imageCount);
        } else if (mimeType != null && mimeType.equals("video/mp4")) {
            handleVideoUri(mediaUri, videoSelected);
        }
    }

    private void handleImageUri(Uri mediaUri, int imageCount) {
        if (imageCount < MAX_IMAGES) {
            if (isValidFileSize(mediaUri, MAX_IMAGE_SIZE_MB)) {
                viewModel.addImage(mediaUri);
            } else {
                showToast("Cada imagen debe ser menor a 2MB");
            }
        } else {
            showToast("Ya has seleccionado el máximo de 7 imágenes");
        }
    }

    private void handleVideoUri(Uri mediaUri, boolean videoSelected) {
        if (!videoSelected) {
            if (isValidFileSize(mediaUri, MAX_VIDEO_SIZE_MB)) {
                viewModel.setSelectedVideo(mediaUri);
                selectedVideoFile = getFileFromUri(mediaUri);
                if (selectedVideoFile != null) {
                    System.out.println("Video file selected: " + selectedVideoFile.getAbsolutePath());
                } else {
                    System.out.println("Failed to get video file from URI.");
                }
            } else {
                showToast("El video debe ser menor a 5MB");
            }
        } else {
            showToast("Ya has seleccionado un video");
        }
    }

    private File getFileFromUri(Uri uri) {
        File file = null;
        try {
            String filePath = getRealPathFromURI(uri);
            file = new File(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Video.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            String result = cursor.getString(column_index);
            cursor.close();
            return result;
        }
        return null;
    }

    private File copyUriToFile(Uri uri) throws IOException {
        File tempFile = File.createTempFile("temp", null, getActivity().getCacheDir());
        try (InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
        return tempFile;
    }

    private void sendVideoByGrpc(File videoFile, int auctionId) {
        Server videoService = new Server();
        try {
            System.out.println("Sending video file: " + videoFile.getName());
            System.out.println("Auction ID: " + auctionId);
            videoService.uploadVideo(videoFile, auctionId, MIME_TYPE_VIDEO);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            videoService.shutdown();
        }
    }

    private boolean isValidFileSize(Uri fileUri, int maxSizeMb) {
        try (Cursor returnCursor = getActivity().getContentResolver().query(fileUri, null, null, null, null)) {
            if (returnCursor == null) return false;
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            if (sizeIndex == -1) return false;
            returnCursor.moveToFirst();
            long size = returnCursor.getLong(sizeIndex);
            return size <= maxSizeMb * 1024 * 1024;
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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

        boolean maxMediaSelected = viewModel.getSelectedImages().getValue().size() >= MAX_IMAGES && videoUri != null;
        binding.addMediaButton.setVisibility(maxMediaSelected ? View.GONE : View.VISIBLE);
    }

    private void createAuction() {
        if (!validateInputFields()) return;
        showProgressBar();
        AuctionCreateBody auctionBody = createAuctionBody();
        AuctionsRepository auctionsRepository = new AuctionsRepository();
        auctionsRepository.createAuction(auctionBody, new IProcessStatusListener<Auction>() {
            @Override
            public void onSuccess(Auction auction) {
                handleAuctionCreationSuccess(auction);
            }

            @Override
            public void onError(ProcessErrorCodes errorStatus) {
                handleAuctionCreationError();
            }
        });
    }

    private boolean validateInputFields() {
        if (viewModel.getSelectedImages().getValue().isEmpty() && viewModel.getSelectedVideo().getValue() == null) {
            selectMultimediaErrorTextView.setVisibility(View.VISIBLE);
            return false;
        }
        selectMultimediaErrorTextView.setVisibility(View.GONE);

        String basePriceStr = basePriceEditText.getText().toString().trim();
        if (TextUtils.isEmpty(basePriceStr) || Double.parseDouble(basePriceStr) <= 0) {
            basePriceErrorTextView.setVisibility(View.VISIBLE);
            return false;
        }
        basePriceErrorTextView.setVisibility(View.GONE);
        return true;
    }

    private AuctionCreateBody createAuctionBody() {
        String title = viewModel.getAuctionTitle().getValue();
        String description = viewModel.getItemDescription().getValue();
        int openingDays = viewModel.getOpeningDays().getValue();
        double basePrice = Double.parseDouble(basePriceEditText.getText().toString().trim());
        Double minimumBid = selectedBidOption != null ? Double.parseDouble(selectedBidOption.getText().toString().replace("$", "")) : null;

        List<HypermediaFile> uploadedMediaFiles = new ArrayList<>();
        for (Uri imageUri : viewModel.getSelectedImages().getValue()) {
            uploadedMediaFiles.add(new HypermediaFile(convertFileToBase64(imageUri), getFileName(imageUri), "image/jpg"));
        }

        return new AuctionCreateBody(title, description, basePrice, minimumBid, openingDays, itemStatus, uploadedMediaFiles);
    }

    private void handleAuctionCreationSuccess(Auction auction) {
        getActivity().runOnUiThread(() -> {
            hideProgressBar();
            showAlert("Subasta creada con éxito");
            if (selectedVideoFile != null) {
                sendVideoByGrpc(selectedVideoFile, auction.getId());
            }
            navigateToMainMenu();
        });
    }

    private void handleAuctionCreationError() {
        getActivity().runOnUiThread(() -> {
            hideProgressBar();
            showAlert("Error al crear la subasta. Intenta de nuevo.");
        });
    }

    private String getFileName(Uri uri) {
        try (Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    return cursor.getString(nameIndex);
                }
            }
        }
        String path = uri.getPath();
        int cut = path != null ? path.lastIndexOf('/') : -1;
        return cut != -1 ? path.substring(cut + 1) : null;
    }

    private String convertFileToBase64(Uri uri) {
        try (InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP);
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

    private void navigateToMainMenu() {
        clearFields();
        if (getActivity() instanceof MainMenuActivity) {
            MainMenuActivity activity = (MainMenuActivity) getActivity();
            activity.showFragment(new SearchAuctionFragment());
            activity.selectSearchAuctionMenuItem();
        }
    }

    private void setupListeners() {
        binding.cancelCreateAuctionButton.setOnClickListener(v -> navigateToMainMenu());
    }

    private void clearFields() {
        viewModel.clearData();
        basePriceEditText.setText("");
        basePriceErrorTextView.setVisibility(View.GONE);
        selectMultimediaErrorTextView.setVisibility(View.GONE);
        selectedBidOption = null;
        oneHundredOfferTextView.setBackgroundResource(R.drawable.black_rounded_border);
        oneHundredFiftyOfferTextView.setBackgroundResource(R.drawable.black_rounded_border);
        twoHundredOfferTextView.setBackgroundResource(R.drawable.black_rounded_border);
        twoHundredfiftyOfferTextView.setBackgroundResource(R.drawable.black_rounded_border);
        threeHundredOfferTextView.setBackgroundResource(R.drawable.black_rounded_border);
        updateMediaAdapter(); // Esto actualizará la vista para mostrar solo el icono de multimedia.
    }
}

