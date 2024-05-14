package com.bidblast.usecases.modifycategory;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;
import com.bidblast.databinding.FragmentModifyAuctionCategoryBinding;
import com.bidblast.usecases.login.LoginViewModel;

public class ModifyAuctionCategoryFragment extends Fragment {

    private FragmentModifyAuctionCategoryBinding binding;
    private ModifyAuctionCategoryViewModel viewModel;

    private static final String ID_KEY = "id";
    private static final String TITLE_KEY = "title";
    private static final String DESCRIPTION_KEY = "description";
    private static final String KEYWORDS_KEY = "keywords";

    private int id;
    private String title;
    private String description;
    private String keywords;

    public ModifyAuctionCategoryFragment() {}

    public static ModifyAuctionCategoryFragment newInstance(int id, String title, String description, String keywords) {
        ModifyAuctionCategoryFragment fragment = new ModifyAuctionCategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ID_KEY, id);
        args.putString(TITLE_KEY, title);
        args.putString(DESCRIPTION_KEY, description);
        args.putString(KEYWORDS_KEY, keywords);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ID_KEY);
            title = getArguments().getString(TITLE_KEY);
            description = getArguments().getString(DESCRIPTION_KEY);
            keywords = getArguments().getString(KEYWORDS_KEY);
            // Aquí podrías hacer cualquier cosa que necesites con los datos, como pasárselos al ViewModel
            // viewModel.setData(id, title, description, keywords);
        }
        setupFieldsValidations();
        viewModel = new ViewModelProvider(this).get(ModifyAuctionCategoryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentModifyAuctionCategoryBinding.inflate(inflater, container, false);

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
}

