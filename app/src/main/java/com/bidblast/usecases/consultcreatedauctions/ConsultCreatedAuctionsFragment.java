package com.bidblast.usecases.consultcreatedauctions;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.databinding.FragmentConsultCompletedAuctionsBinding;
import com.bidblast.databinding.FragmentConsultCreatedAuctionsBinding;
import com.bidblast.model.Auction;
import com.bidblast.usecases.bidonauction.BidOnAuctionFragment;
import com.bidblast.usecases.consultoffersonauction.OffersOnAuctionFragment;
import com.bidblast.usecases.consultsalesstatistics.ConsultSalesStatisticsFragment;

import java.util.List;

public class ConsultCreatedAuctionsFragment extends Fragment {

    private static final int TOTAL_AUCTIONS_TO_LOAD = 5;
    private FragmentConsultCreatedAuctionsBinding binding;
    private ConsultCreatedAuctionsViewModel viewModel;
    private CreatedAuctionDetailsAdapter createdAuctionDetailsAdapter;
    private String searchQuery;
    public ConsultCreatedAuctionsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConsultCreatedAuctionsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel = new ViewModelProvider(this).get(ConsultCreatedAuctionsViewModel.class);
        createdAuctionDetailsAdapter = new CreatedAuctionDetailsAdapter(getContext());
        createdAuctionDetailsAdapter.setOnAuctionClickListener(this::handleOpenOffersOnAuctionFragment);
        binding.createdAuctionsRecyclerView.setAdapter(createdAuctionDetailsAdapter);

        setupCreatedAuctionsListStatusListener();
        setupCreatedAuctionsListListener();
        setupRecyclerViewScrollListener();
        setupStillCreatedAuctionsLeftToLoadListener();
        setupSearchCreatedAuctionsImageButton();
        viewModel.cleanAuctionsList();
        loadCreatedAuctions();
        setupConsultSalesStatisticsButton();
    }

    private void setupConsultSalesStatisticsButton() {
        binding.consultSalesStatisticsButton.setOnClickListener(v -> {
            ConsultSalesStatisticsFragment consultSalesStatisticsFragment = ConsultSalesStatisticsFragment.newInstance();
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.mainViewFragmentLayout, consultSalesStatisticsFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    private void setupSearchCreatedAuctionsImageButton() {
        binding.searchAuctionsImageButton.setOnClickListener(v -> {
            viewModel.cleanAuctionsList();
            loadCreatedAuctions();
        });
    }

    private void loadCreatedAuctions() {
        searchQuery = binding.searchBarEditText.getText().toString();

        viewModel.recoverAuctions(searchQuery, TOTAL_AUCTIONS_TO_LOAD);
    }

    private void handleOpenOffersOnAuctionFragment(int idAuction) {
        OffersOnAuctionFragment offersOnAuctionFragment = OffersOnAuctionFragment.newInstance(idAuction);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.mainViewFragmentLayout, offersOnAuctionFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setupCreatedAuctionsListStatusListener() {
        viewModel.getAuctionsListRequestStatus().observe(getViewLifecycleOwner(), requestStatus -> {
            binding.emptyCreatedAuctionsMessageLinearLayout.setVisibility(View.GONE);

            if(requestStatus == RequestStatus.LOADING) {
                binding.loadingAuctionsTextView.setVisibility(View.VISIBLE);
            } else {
                binding.loadingAuctionsTextView.setVisibility(View.GONE);

                if(requestStatus == RequestStatus.DONE) {
                    binding.errorLoadingCreatedAuctionsLinearLayout.setVisibility(View.GONE);

                    List<Auction> auctions = viewModel.getAuctionsList().getValue();
                    if(auctions != null && auctions.size() != 0) {
                        binding.createdAuctionsRecyclerView.setVisibility(View.VISIBLE);
                        binding.emptyCreatedAuctionsMessageLinearLayout.setVisibility(View.GONE);
                    } else {
                        if (searchQuery == null || searchQuery.isEmpty()) {
                            binding.emptyCompletedAuctionsTitleTextView.setText(
                                    getString(
                                            R.string.consultcompletedauctions_empty_completed_auctions_title
                                    )
                            );
                            binding.emptyCompletedAuctionsTextTextView.setText(
                                    getString(
                                            R.string.consultcompletedauctions_empty_completed_auctions_text
                                    )
                            );
                        } else {
                            binding.emptyCompletedAuctionsTitleTextView.setText(
                                    getString(
                                            R.string.consultcompletedauctions_empty_completed_auctions_by_search_title
                                    )
                            );
                            binding.emptyCompletedAuctionsTextTextView.setText(
                                    getString(
                                            R.string.consultcompletedauctions_empty_completed_auctions_by_search_text
                                    )
                            );
                        }
                        binding.createdAuctionsRecyclerView.setVisibility(View.GONE);
                        binding.emptyCreatedAuctionsMessageLinearLayout.setVisibility(View.VISIBLE);
                        binding.allAuctionsLoadedTextView.setVisibility(View.GONE);
                    }
                } else if (requestStatus == RequestStatus.ERROR) {
                    binding.errorLoadingCreatedAuctionsLinearLayout.setVisibility(View.VISIBLE);
                    binding.consultSalesStatisticsButton.setVisibility(View.GONE);
                    binding.createdAuctionsRecyclerView.setVisibility(View.GONE);
                    binding.allAuctionsLoadedTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupStillCreatedAuctionsLeftToLoadListener() {
        viewModel.getStillAuctionsLeftToLoad().observe(getViewLifecycleOwner(), stillAuctionsLeftToLoad -> {
            if(stillAuctionsLeftToLoad) {
                binding.allAuctionsLoadedTextView.setVisibility(View.GONE);
            } else {
                binding.allAuctionsLoadedTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupCreatedAuctionsListListener() {
        viewModel.getAuctionsList().observe(getViewLifecycleOwner(), auctionsList -> {
            createdAuctionDetailsAdapter.submitList(auctionsList);
        });
    }

    private void setupRecyclerViewScrollListener() {
        binding.createdAuctionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        loadCreatedAuctions();
                    }
                }
            }
        });
    }
}