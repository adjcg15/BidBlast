package com.bidblast.usecases.consultaauctioncategories;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bidblast.R;
import com.bidblast.databinding.FragmentConsultCategoriesBinding;
import com.bidblast.model.AuctionCategory;
import com.bidblast.usecases.login.LoginActivity;

public class ConsultAuctionCategoriesFragment extends Fragment {

    private FragmentConsultCategoriesBinding binding;
    private AuctionCategoryAdapter adapter;
    private ConsultAuctionCategoriesViewModel viewModel;
    private EditText searchCategoryBarEditText;
    private ImageButton searchButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentConsultCategoriesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(ConsultAuctionCategoriesViewModel.class);

        binding.eqRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AuctionCategoryAdapter(getContext());
        binding.eqRecycler.setAdapter(adapter);

        searchCategoryBarEditText = binding.searchCategoryBarEditText;
        searchButton = binding.searchCategoryButton;

        observeViewModel();
        setupSignOutImageButton();

        viewModel.loadAuctionCategories();
        searchButton.setOnClickListener(v -> {
            String query = searchCategoryBarEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                viewModel.searchAuctionCategories(query);
            } else {
                viewModel.loadAuctionCategories();
            }
        });

        return view;
    }

    private void setupSignOutImageButton() {
        binding.signOutImageView.setOnClickListener(v -> {
            if (getActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.global_sign_out_confirmation_message));

                builder.setPositiveButton(getString(R.string.global_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishUserSession();
                    }
                });

                builder.setNegativeButton(getString(R.string.global_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void finishUserSession() {
        if(getActivity() != null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void observeViewModel() {
        viewModel.getAuctionCategories().observe(getViewLifecycleOwner(), auctionCategories -> {
            if (auctionCategories != null && !auctionCategories.isEmpty()) {
                adapter.submitList(auctionCategories);
                binding.eqRecycler.setVisibility(View.VISIBLE);
                binding.emptyCategoriesMessageLinearLayout.setVisibility(View.GONE);
                binding.emptySearchMessageLinearLayout.setVisibility(View.GONE);
            } else {
                binding.eqRecycler.setVisibility(View.GONE);
                if (searchCategoryBarEditText.getText().toString().trim().isEmpty()) {
                    binding.emptyCategoriesMessageLinearLayout.setVisibility(View.VISIBLE);
                    binding.emptySearchMessageLinearLayout.setVisibility(View.GONE);
                } else {
                    binding.emptyCategoriesMessageLinearLayout.setVisibility(View.GONE);
                    binding.emptySearchMessageLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.loadingCategoriesTextView.setVisibility(View.VISIBLE);
                binding.eqRecycler.setVisibility(View.GONE);
                binding.emptyCategoriesMessageLinearLayout.setVisibility(View.GONE);
                binding.emptySearchMessageLinearLayout.setVisibility(View.GONE);
                binding.errorLoadingCategoriesLinearLayout.setVisibility(View.GONE);
            } else {
                binding.loadingCategoriesTextView.setVisibility(View.GONE);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorCode -> {
            if (errorCode != null) {
                binding.eqRecycler.setVisibility(View.GONE);
                binding.loadingCategoriesTextView.setVisibility(View.GONE);
                binding.emptyCategoriesMessageLinearLayout.setVisibility(View.GONE);
                binding.emptySearchMessageLinearLayout.setVisibility(View.GONE);
                binding.errorLoadingCategoriesLinearLayout.setVisibility(View.VISIBLE);
            } else {
                binding.errorLoadingCategoriesLinearLayout.setVisibility(View.GONE);
            }
        });
    }
}