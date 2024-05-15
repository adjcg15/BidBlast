package com.bidblast.usecases.searchauction;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;
import com.bidblast.databinding.FragmentSearchAuctionBinding;
import com.bidblast.model.AuctionCategory;
import com.bidblast.model.PriceRange;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class SearchAuctionFragment extends Fragment {
    private FragmentSearchAuctionBinding binding;
    private SearchAuctionViewModel viewModel;
    private AuctionDetailsAdapter auctionsListAdapter;
    private CategoryFilterAdapter fastCategoryFiltersListAdapter;
    private CategoryFilterAdapter categoryFiltersListAdapter;
    private PriceFilterAdapter priceFiltersListAdapter;

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
        binding.auctionsListRecyclerView.setAdapter(auctionsListAdapter);

        fastCategoryFiltersListAdapter = new CategoryFilterAdapter(viewModel);
        binding.fastCategoryFiltersListRecyclerView.setAdapter(fastCategoryFiltersListAdapter);
        fastCategoryFiltersListAdapter.setOnFilterClickListener(this::toggleCategoryFastFilter);

        categoryFiltersListAdapter = new CategoryFilterAdapter(viewModel);
        categoryFiltersListAdapter.setOnFilterClickListener(this::toggleCategoryFilter);

        priceFiltersListAdapter = new PriceFilterAdapter(viewModel);
        priceFiltersListAdapter.setOnFilterClickListener(this::togglePriceFilter);
        priceFiltersListAdapter.submitList(viewModel.getAllPriceRanges());

        setupFiltersButton();
        setupAuctionsListStatusListener();
        setupAuctionsListListener();
        setupAuctionCategoriesListListener();
        loadAuctions("", 10, 0);
        loadAuctionCategories();

        return rootView;
    }

    private void setupFiltersButton() {
        binding.filtersButton.setOnClickListener(v -> {
            View filtersView = getLayoutInflater().inflate(R.layout.dialog_search_auction_filters, null);

            RecyclerView categoryFiltersListRecyclerView = filtersView.findViewById(R.id.categoryFiltersListRecyclerView);
            FlexboxLayoutManager categoriesLayoutManager = new FlexboxLayoutManager(requireContext());
            categoryFiltersListRecyclerView.setLayoutManager(categoriesLayoutManager);
            categoryFiltersListRecyclerView.setAdapter(categoryFiltersListAdapter);

            RecyclerView priceFiltersListRecyclerView = filtersView.findViewById(R.id.priceFiltersListRecyclerView);
            FlexboxLayoutManager pricesLayoutManager = new FlexboxLayoutManager(requireContext());
            priceFiltersListRecyclerView.setLayoutManager(pricesLayoutManager);
            priceFiltersListRecyclerView.setAdapter(priceFiltersListAdapter);

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

    private void setupAuctionCategoriesListListener() {
        viewModel.getAuctionCategoriesList().observe(getViewLifecycleOwner(), categoriesList -> {
            fastCategoryFiltersListAdapter.submitList(categoriesList);
            categoryFiltersListAdapter.submitList(categoriesList);
        });
    }

    private void loadAuctions(String searchQuery, int limit, int offset) {
        viewModel.recoverAuctions(searchQuery, limit, offset);
    }

    private void loadAuctionCategories() {
        viewModel.recoverAuctionCategories();
    }

    private void toggleCategoryFastFilter(AuctionCategory category) {
        viewModel.toggleCategoryFilter(category);
        //TODO: clean the showed auctions list and show a new one with the filters coincidence
    }

    private void toggleCategoryFilter(AuctionCategory category) {
        viewModel.toggleCategoryFilter(category);
    }

    private void togglePriceFilter(PriceRange priceRange) {
        viewModel.togglePriceFilter(priceRange);
    }
}