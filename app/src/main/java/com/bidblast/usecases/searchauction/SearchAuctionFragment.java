package com.bidblast.usecases.searchauction;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.databinding.FragmentSearchAuctionBinding;
import com.bidblast.model.Auction;
import com.bidblast.model.AuctionCategory;
import com.bidblast.model.PriceRange;
import com.bidblast.usecases.bidonauction.BidOnAuctionFragment;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class SearchAuctionFragment extends Fragment {
    private static final int TOTAL_AUCTIONS_TO_LOAD = 5;
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
        auctionsListAdapter.setOnAuctionClickListener(this::handleOpenBidOnAuctionFragment);
        binding.auctionsListRecyclerView.setAdapter(auctionsListAdapter);

        fastCategoryFiltersListAdapter = new CategoryFilterAdapter(viewModel, false);
        binding.fastCategoryFiltersListRecyclerView.setAdapter(fastCategoryFiltersListAdapter);
        fastCategoryFiltersListAdapter.setOnFilterClickListener(this::handleFastCategoryFilterClick);

        categoryFiltersListAdapter = new CategoryFilterAdapter(viewModel, true);
        categoryFiltersListAdapter.setOnFilterClickListener(this::handleCategoryFilterClick);

        priceFiltersListAdapter = new PriceFilterAdapter(viewModel);
        priceFiltersListAdapter.setOnFilterClickListener(this::handlePriceFilterClick);
        priceFiltersListAdapter.submitList(viewModel.getAllPriceRanges());

        setupFiltersButton();
        setupSearchAuctionsImageButton();
        setupAuctionsListStatusListener();
        setupAuctionsListListener();
        setupAuctionCategoriesListListener();
        setupRecyclerViewScrollListener();
        setupStillAuctionsLeftToLoadListener();
        loadAuctions();
        loadAuctionCategories();

        return rootView;
    }

    private void setupSearchAuctionsImageButton() {
        binding.searchAuctionsImageButton.setOnClickListener(v -> {
            viewModel.cleanAuctionsList();
            loadAuctions();
        });
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

            final boolean[] shouldSaveFilters = {false};
            Button showResultsButton = filtersView.findViewById(R.id.showResultsButton);
            showResultsButton.setOnClickListener(view -> {
                shouldSaveFilters[0] = true;
                filtersDialog.dismiss();
            });

            filtersDialog.setOnDismissListener(dialog -> {
                if (shouldSaveFilters[0]) {
                    viewModel.saveTemporaryFilters();
                    viewModel.cleanAuctionsList();
                    loadAuctions();
                } else {
                    viewModel.discardTemporaryFilters();
                }
            });

            filtersDialog.show();
        });
    }

    private void setupAuctionsListStatusListener() {
        viewModel.getAuctionsListRequestStatus().observe(getViewLifecycleOwner(), requestStatus -> {
            binding.emptyAuctionsMessageLinearLayout.setVisibility(View.GONE);

            if(requestStatus == RequestStatus.LOADING) {
                binding.loadingAuctionsTextView.setVisibility(View.VISIBLE);
            } else {
                binding.loadingAuctionsTextView.setVisibility(View.GONE);

                if(requestStatus == RequestStatus.DONE) {
                    binding.errorLoadingAuctionsLinearLayout.setVisibility(View.GONE);
                    binding.filtersBarLinearLayout.setVisibility(View.VISIBLE);

                    List<Auction> auctions = viewModel.getAuctionsList().getValue();
                    if(auctions != null && auctions.size() != 0) {
                        binding.auctionsListRecyclerView.setVisibility(View.VISIBLE);
                        binding.emptyAuctionsMessageLinearLayout.setVisibility(View.GONE);
                    } else {
                        binding.auctionsListRecyclerView.setVisibility(View.GONE);
                        binding.emptyAuctionsMessageLinearLayout.setVisibility(View.VISIBLE);
                        binding.allAuctionsLoadedTextView.setVisibility(View.GONE);
                    }
                } else if (requestStatus == RequestStatus.ERROR) {
                    binding.errorLoadingAuctionsLinearLayout.setVisibility(View.VISIBLE);
                    binding.auctionsListRecyclerView.setVisibility(View.GONE);
                    binding.filtersBarLinearLayout.setVisibility(View.GONE);
                    binding.allAuctionsLoadedTextView.setVisibility(View.GONE);
                }
            }
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

    private void setupStillAuctionsLeftToLoadListener() {
        viewModel.getStillAuctionsLeftToLoad().observe(getViewLifecycleOwner(), stillAuctionsLeftToLoad -> {
            if(stillAuctionsLeftToLoad) {
                binding.allAuctionsLoadedTextView.setVisibility(View.GONE);
            } else {
                binding.allAuctionsLoadedTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadAuctions() {
        String searchQuery = binding.searchbarEditText.getText().toString();

        viewModel.recoverAuctions(searchQuery, TOTAL_AUCTIONS_TO_LOAD);
    }

    private void loadAuctionCategories() {
        viewModel.recoverAuctionCategories();
    }

    private void handleFastCategoryFilterClick(AuctionCategory category) {
        viewModel.toggleCategoryFilter(category);
        viewModel.cleanAuctionsList();
        loadAuctions();
    }

    private void handleCategoryFilterClick(AuctionCategory category) {
        viewModel.toggleTemporaryCategoryFilter(category);
    }

    private void handlePriceFilterClick(PriceRange priceRange) {
        viewModel.toggleTemporaryPriceFilter(priceRange);
    }

    private void handleOpenBidOnAuctionFragment(int idAuction) {
        BidOnAuctionFragment bidOnAuctionFragment = BidOnAuctionFragment.newInstance(idAuction);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.mainViewFragmentLayout, bidOnAuctionFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setupRecyclerViewScrollListener() {
        binding.auctionsListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= TOTAL_AUCTIONS_TO_LOAD
                        && Boolean.TRUE.equals(viewModel.getStillAuctionsLeftToLoad().getValue())
                        && viewModel.getAuctionsListRequestStatus().getValue() != RequestStatus.LOADING) {
                        loadAuctions();
                    }
                }
            }
        });
    }
}