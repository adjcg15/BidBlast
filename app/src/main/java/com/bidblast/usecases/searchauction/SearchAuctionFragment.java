package com.bidblast.usecases.searchauction;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;
import com.bidblast.databinding.FragmentSearchAuctionBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class SearchAuctionFragment extends Fragment {
    private FragmentSearchAuctionBinding binding;

    public SearchAuctionFragment() {

    }

    public static SearchAuctionFragment newInstance() {
        return new SearchAuctionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchAuctionBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        setupFiltersButton();

        return rootView;
    }

    private void setupFiltersButton() {
        binding.filtersButton.setOnClickListener(v -> {
            View filtersView = getLayoutInflater().inflate(R.layout.dialog_search_auction_filters, null);

            BottomSheetDialog filtersDialog = new BottomSheetDialog(requireContext());
            filtersDialog.setContentView(filtersView);
            filtersDialog.show();
        });
    }
}