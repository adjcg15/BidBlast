package com.bidblast.usecases.registerandmodifycategory;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.databinding.FragmentAuctionCategoryFormBinding;
import com.bidblast.model.AuctionCategory;
import com.bidblast.repositories.businesserrors.SaveAuctionCategoryCodes;
import com.google.android.material.snackbar.Snackbar;

public class AuctionCategoryFormFragment extends Fragment {

    private FragmentAuctionCategoryFormBinding binding;
    private AuctionCategoryFormViewModel viewModel;

    private static final String AUCTIONCATEGORY_KEY = "auction_category";

    private AuctionCategory auctionCategory;

    public AuctionCategoryFormFragment() {}

    public static AuctionCategoryFormFragment newInstance(AuctionCategory auctionCategory) {
        AuctionCategoryFormFragment fragment = new AuctionCategoryFormFragment();
        if (auctionCategory != null) {
            Bundle args = new Bundle();
            args.putParcelable(AUCTIONCATEGORY_KEY, auctionCategory);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            auctionCategory = getArguments().getParcelable(AUCTIONCATEGORY_KEY);
        }
        viewModel = new ViewModelProvider(this).get(AuctionCategoryFormViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAuctionCategoryFormBinding.inflate(inflater, container, false);
        if (auctionCategory != null) {
            binding.setAuctionCategory(auctionCategory);
        } else {
            binding.saveCategoryButton.setText(R.string.savecategory_register_tittle);
            binding.titleFormTextView.setText(R.string.savecategory_register_tittle);
        }
        setupFieldsValidations();
        setupSaveAuctionCategoryStatusListener();
        setupSaveAuctionCategoryButton();
        setupCancelSaveCategoryButton();
        setupDiscardSaveCategoryButton();
        return binding.getRoot();
    }

    private void setupFieldsValidations() {
        viewModel.isValidTitle().observe(getViewLifecycleOwner(), isValidTitle -> {
            if (isValidTitle) {
                binding.categoryTitleErrorTextView.setVisibility(View.GONE);
                binding.categoryTitleEditText.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.categoryTitleErrorTextView.setVisibility(View.VISIBLE);
                binding.categoryTitleEditText.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });

        viewModel.isValidDescription().observe(getViewLifecycleOwner(), isValidDescription -> {
            if (isValidDescription) {
                binding.categoryDescriptionErrorTextView.setVisibility(View.GONE);
                binding.categoryDescriptionEditText.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.categoryDescriptionErrorTextView.setVisibility(View.VISIBLE);
                binding.categoryDescriptionEditText.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });

        viewModel.areValidKeywords().observe(getViewLifecycleOwner(), areValidKeywords -> {
            if (areValidKeywords) {
                binding.categoryKeywordsErrorTextView.setVisibility(View.GONE);
                binding.categoryKeywordsEditText.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.categoryKeywordsErrorTextView.setVisibility(View.VISIBLE);
                binding.categoryKeywordsEditText.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });
    }

    private void setupSaveAuctionCategoryButton() {
        binding.saveCategoryButton.setOnClickListener(v -> {
            if (validateFields()) {
                if (auctionCategory != null) {
                    if(viewModel.getSaveAuctionCategoryRequestStatus().getValue() != RequestStatus.LOADING) {
                        AuctionCategory auctionCategory = new AuctionCategory();
                        auctionCategory.setId(this.auctionCategory.getId());
                        auctionCategory.setTitle(binding.categoryTitleEditText.getText().toString().trim());
                        auctionCategory.setDescription(binding.categoryDescriptionEditText.getText().toString().trim());
                        auctionCategory.setKeywords(binding.categoryKeywordsEditText.getText().toString().trim());

                        viewModel.updateAuctionCategory(auctionCategory);
                    }
                } else {
                    if(viewModel.getSaveAuctionCategoryRequestStatus().getValue() != RequestStatus.LOADING) {
                        AuctionCategory auctionCategory = new AuctionCategory();
                        auctionCategory.setTitle(binding.categoryTitleEditText.getText().toString().trim());
                        auctionCategory.setDescription(binding.categoryDescriptionEditText.getText().toString().trim());
                        auctionCategory.setKeywords(binding.categoryKeywordsEditText.getText().toString().trim());

                        viewModel.registerAuctionCategory(auctionCategory);
                    }
                }
            }
        });
    }

    private void setupCancelSaveCategoryButton() {
        binding.cancelSaveCategoryButton.setOnClickListener(v -> {
            goToPreviousWindow();
        });
    }

    private void setupDiscardSaveCategoryButton() {
        binding.discardModifyAuctionCategoryButton.setOnClickListener(v -> {
            goToPreviousWindow();
        });
    }

    private void goToPreviousWindow() {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }

    private boolean validateFields(){
        String title = binding.categoryTitleEditText.getText().toString().trim();
        String description = binding.categoryDescriptionEditText.getText().toString().trim();
        String keywords = binding.categoryKeywordsEditText.getText().toString().trim();

        viewModel.validateTitle(title);
        viewModel.validateDescription(description);
        viewModel.validateKeywords(keywords);

        return Boolean.TRUE.equals(viewModel.isValidTitle().getValue()) &&
                Boolean.TRUE.equals(viewModel.isValidDescription().getValue()) &&
                Boolean.TRUE.equals(viewModel.areValidKeywords().getValue());
    }

    private void setupSaveAuctionCategoryStatusListener() {
        viewModel.getSaveAuctionCategoryRequestStatus().observe(getViewLifecycleOwner(), requestStatus -> {
            if (requestStatus == RequestStatus.DONE) {
                String successMessage = getString(R.string.savecategory_success_message);
                Snackbar.make(binding.getRoot(), successMessage, Snackbar.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(this::goToPreviousWindow, 4000);
            }

            if (requestStatus == RequestStatus.ERROR) {
                SaveAuctionCategoryCodes errorCode = viewModel.getSaveAuctionCategoryErrorCode().getValue();

                if(errorCode != null) {
                    showSaveAuctionCategoryError(errorCode);
                }
            }
        });
    }

    private void showSaveAuctionCategoryError(SaveAuctionCategoryCodes errorCode) {
        String errorMessage = "";

        switch (errorCode) {
            case TITLE_ALREADY_EXISTS:
                errorMessage = getString(R.string.savecategory_title_already_exists_toast_message);
                break;
            case CATEGORY_NOT_FOUND:
                errorMessage = getString(R.string.savecategory_not_found_toast_message);
                break;
            default:
                errorMessage = getString(R.string.savecategory_error_message);
        }

        Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_SHORT).show();
    }
}