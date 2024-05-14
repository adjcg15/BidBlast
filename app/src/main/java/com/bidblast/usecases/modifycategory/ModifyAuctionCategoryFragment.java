package com.bidblast.usecases.modifycategory;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.databinding.FragmentModifyAuctionCategoryBinding;
import com.bidblast.model.AuctionCategory;
import com.bidblast.repositories.ProcessErrorCodes;
import com.google.android.material.snackbar.Snackbar;

public class ModifyAuctionCategoryFragment extends Fragment {

    private FragmentModifyAuctionCategoryBinding binding;
    private ModifyAuctionCategoryViewModel viewModel;

    private static final String AUCTIONCATEGORY_KEY = "auction_category";

    private AuctionCategory auctionCategory;

    public ModifyAuctionCategoryFragment() {}

    public static ModifyAuctionCategoryFragment newInstance(AuctionCategory auctionCategory) {
        ModifyAuctionCategoryFragment fragment = new ModifyAuctionCategoryFragment();
        Bundle args = new Bundle();
        args.putParcelable(AUCTIONCATEGORY_KEY, auctionCategory);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            auctionCategory = getArguments().getParcelable(AUCTIONCATEGORY_KEY);
        }
        setupFieldsValidations();
        setupModifyAuctionCategoryStatusListener();
        viewModel = new ViewModelProvider(this).get(ModifyAuctionCategoryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentModifyAuctionCategoryBinding.inflate(inflater, container, false);
        binding.setAuctionCategory(auctionCategory);
        setupModifyAuctionCategoryButton();
        return binding.getRoot();
    }

    private void setupFieldsValidations() {
        viewModel.isValidTitle().observe(this, isValidTitle -> {
            if (isValidTitle) {
                binding.categoryTitleErrorTextView.setVisibility(View.GONE);
                binding.categoryTitleEditText.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.categoryTitleErrorTextView.setVisibility(View.VISIBLE);
                binding.categoryTitleEditText.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });

        viewModel.isValidDescription().observe(this, isValidDescription -> {
            if (isValidDescription) {
                binding.categoryDescriptionErrorTextView.setVisibility(View.GONE);
                binding.categoryDescriptionEditText.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.categoryDescriptionErrorTextView.setVisibility(View.VISIBLE);
                binding.categoryDescriptionEditText.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });

        viewModel.areValidKeywords().observe(this, areValidKeywords -> {
            if (areValidKeywords) {
                binding.categoryKeywordsErrorTextView.setVisibility(View.GONE);
                binding.categoryKeyWordsEditText.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.categoryKeywordsErrorTextView.setVisibility(View.VISIBLE);
                binding.categoryKeyWordsEditText.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });
    }

    private void setupModifyAuctionCategoryButton() {
        binding.modifyCategoryButton.setOnClickListener(v -> {
            if (validateFields()) {
                if(viewModel.getModifyAuctionCategoryRequestStatus().getValue() != RequestStatus.LOADING) {
                    String title = binding.categoryTitleEditText.getText().toString().trim();
                    String description = binding.categoryDescriptionEditText.getText().toString().trim();
                    String keywords = binding.categoryDescriptionEditText.getText().toString().trim();

                    viewModel.updateAuctionCategory(title, description, keywords);
                }
            }
        });
    }

    private boolean validateFields(){
        String title = binding.categoryTitleEditText.getText().toString().trim();
        String description = binding.categoryDescriptionEditText.getText().toString().trim();
        String keywords = binding.categoryKeyWordsEditText.getText().toString().trim();

        viewModel.validateTitle(title);
        viewModel.validateDescription(description);
        viewModel.validateKeywords(keywords);

        return Boolean.TRUE.equals(viewModel.isValidTitle().getValue()) &&
                Boolean.TRUE.equals(viewModel.isValidDescription().getValue()) &&
                Boolean.TRUE.equals(viewModel.areValidKeywords().getValue());
    }

    private void setupModifyAuctionCategoryStatusListener() {
        viewModel.getModifyAuctionCategoryRequestStatus().observe(this, requestStatus -> {
            if (requestStatus == RequestStatus.DONE) {
                String successMessage = getString(R.string.modifycategory_success_toast_message);
                Snackbar.make(binding.getRoot(), successMessage, Snackbar.LENGTH_SHORT).show();
            }

            if (requestStatus == RequestStatus.ERROR) {
                ProcessErrorCodes errorCode = viewModel.getModifyAuctionCategoryErrorCode().getValue();

                if(errorCode != null) {
                    showModifyAuctionCategoryError(errorCode);
                }
            }
        });
    }

    private void showModifyAuctionCategoryError(ProcessErrorCodes errorCode) {
        String errorMessage = "";

        switch (errorCode) {
            case REQUEST_FORMAT_ERROR:
                errorMessage = getString(R.string.modifycategory_error_toast_message);
                break;
            case FATAL_ERROR:
                errorMessage = getString(R.string.modifycategory_error_toast_message);
                break;
            default:
                errorMessage = getString(R.string.modifycategory_error_toast_message);
        }

        Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_SHORT).show();
    }
}

