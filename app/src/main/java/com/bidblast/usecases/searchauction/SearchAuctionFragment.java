package com.bidblast.usecases.searchauction;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;
import com.bidblast.databinding.FragmentSearchAuctionBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class SearchAuctionFragment extends Fragment {
    private FragmentSearchAuctionBinding binding;
    private SearchAuctionViewModel viewModel;
    private AuctionDetailsAdapter auctionsListAdapter;

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
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        binding = FragmentSearchAuctionBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        this.viewModel = new SearchAuctionViewModel();

        auctionsListAdapter = new AuctionDetailsAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.auctionsListRecyclerView.setAdapter(auctionsListAdapter);
        binding.auctionsListRecyclerView.setLayoutManager(layoutManager);

        setupFiltersButton();
        setupAuctionsListStatusListener();
        setupAuctionsListListener();
        loadAuctions("", 10, 0);
        loadAuctionCategories();

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

    private void setupAuctionsListStatusListener() {
        viewModel.getAuctionsListRequestStatus().observe(getViewLifecycleOwner(), requestStatus -> {
            //TODO: handle errors
        });
    }

    private void setupAuctionsListListener() {
        viewModel.getAuctionsList().observe(getViewLifecycleOwner(), auctionsList -> {
            auctionsListAdapter.submitList(auctionsList);
        });
    }

    private void loadAuctions(String searchQuery, int limit, int offset) {
        viewModel.recoverAuctions(searchQuery, limit, offset);
    }

    private void loadAuctionCategories() {
        viewModel.recoverAuctionCategories();
    }
}