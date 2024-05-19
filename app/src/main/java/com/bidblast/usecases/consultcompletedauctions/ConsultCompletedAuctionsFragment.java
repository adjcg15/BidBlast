package com.bidblast.usecases.consultcompletedauctions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;
import com.bidblast.databinding.FragmentConsultCompletedAuctionsBinding;

public class ConsultCompletedAuctionsFragment extends Fragment {
    private static final int TOTAL_AUCTIONS_TO_LOAD = 5;
    private FragmentConsultCompletedAuctionsBinding binding;
    private ConsultCompletedAuctionsViewModel viewModel;
    private CompletedAuctionDetailsAdapter completedAuctionDetailsAdapter;
    public ConsultCompletedAuctionsFragment() {

    }

    public static ConsultCompletedAuctionsFragment newInstance() {
        return new ConsultCompletedAuctionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ConsultCompletedAuctionsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConsultCompletedAuctionsBinding.inflate(inflater, container, false);
        completedAuctionDetailsAdapter = new CompletedAuctionDetailsAdapter(getContext());
        binding.completedAuctionsListRecyclerView.setAdapter(completedAuctionDetailsAdapter);
        return binding.getRoot();
    }
}