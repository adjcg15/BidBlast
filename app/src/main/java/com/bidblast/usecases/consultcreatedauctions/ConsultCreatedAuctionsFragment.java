package com.bidblast.usecases.consultcreatedauctions;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.databinding.FragmentConsultCompletedAuctionsBinding;
import com.bidblast.model.Auction;
import com.bidblast.usecases.consultcompletedauctions.CompletedAuctionDetailsAdapter;
import com.bidblast.usecases.consultcompletedauctions.ConsultCompletedAuctionsFragment;
import com.bidblast.usecases.consultcompletedauctions.ConsultCompletedAuctionsViewModel;

import java.util.List;

public class ConsultCreatedAuctionsFragment extends Fragment {

    private static final int TOTAL_AUCTIONS_TO_LOAD = 5;
    private FragmentConsultCompletedAuctionsBinding binding;
    private ConsultCreatedAuctionsViewModel viewModel;
    private CreatedAuctionDetailsAdapter createdAuctionDetailsAdapter;
    private String searchQuery;
    public ConsultCreatedAuctionsFragment() {

    }

    public static ConsultCompletedAuctionsFragment newInstance() {
        return new ConsultCompletedAuctionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ConsultCreatedAuctionsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConsultCompletedAuctionsBinding.inflate(inflater, container, false);
        createdAuctionDetailsAdapter = new CreatedAuctionDetailsAdapter(getContext());
        binding.completedAuctionsListRecyclerView.setAdapter(createdAuctionDetailsAdapter);

        setupCompletedAuctionsListStatusListener();
        setupCompletedAuctionsListListener();
        setupRecyclerViewScrollListener();
        setupStillCompletedAuctionsLeftToLoadListener();
        setupSearchCompletedAuctionsImageButton();
        loadCompletedAuctions();
        return binding.getRoot();
    }

    private void setupSearchCompletedAuctionsImageButton() {
        binding.searchAuctionsImageButton.setOnClickListener(v -> {
            viewModel.cleanAuctionsList();
            loadCompletedAuctions();
        });
    }

    private void loadCompletedAuctions() {
        searchQuery = binding.searchBarEditText.getText().toString();

        viewModel.recoverAuctions(searchQuery, TOTAL_AUCTIONS_TO_LOAD);
    }

    private void setupCompletedAuctionsListStatusListener() {
        viewModel.getAuctionsListRequestStatus().observe(getViewLifecycleOwner(), requestStatus -> {
            binding.emptyCompletedAuctionsMessageLinearLayout.setVisibility(View.GONE);

            if(requestStatus == RequestStatus.LOADING) {
                binding.loadingAuctionsTextView.setVisibility(View.VISIBLE);
            } else {
                binding.loadingAuctionsTextView.setVisibility(View.GONE);

                if(requestStatus == RequestStatus.DONE) {
                    binding.errorLoadingCompletedAuctionsLinearLayout.setVisibility(View.GONE);

                    List<Auction> auctions = viewModel.getAuctionsList().getValue();
                    if(auctions != null && auctions.size() != 0) {
                        binding.completedAuctionsListRecyclerView.setVisibility(View.VISIBLE);
                        binding.emptyCompletedAuctionsMessageLinearLayout.setVisibility(View.GONE);
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
                        binding.completedAuctionsListRecyclerView.setVisibility(View.GONE);
                        binding.emptyCompletedAuctionsMessageLinearLayout.setVisibility(View.VISIBLE);
                        binding.allAuctionsLoadedTextView.setVisibility(View.GONE);
                    }
                } else if (requestStatus == RequestStatus.ERROR) {
                    binding.errorLoadingCompletedAuctionsLinearLayout.setVisibility(View.VISIBLE);
                    binding.completedAuctionsListRecyclerView.setVisibility(View.GONE);
                    binding.allAuctionsLoadedTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupStillCompletedAuctionsLeftToLoadListener() {
        viewModel.getStillAuctionsLeftToLoad().observe(getViewLifecycleOwner(), stillAuctionsLeftToLoad -> {
            if(stillAuctionsLeftToLoad) {
                binding.allAuctionsLoadedTextView.setVisibility(View.GONE);
            } else {
                binding.allAuctionsLoadedTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupCompletedAuctionsListListener() {
        viewModel.getAuctionsList().observe(getViewLifecycleOwner(), auctionsList -> {
            createdAuctionDetailsAdapter.submitList(auctionsList);
        });
    }

    private void setupRecyclerViewScrollListener() {
        binding.completedAuctionsListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        loadCompletedAuctions();
                    }
                }
            }
        });
    }
}