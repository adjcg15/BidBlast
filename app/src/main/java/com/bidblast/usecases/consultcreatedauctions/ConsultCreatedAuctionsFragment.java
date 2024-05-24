package com.bidblast.usecases.consultcreatedauctions;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;

public class ConsultCreatedAuctionsFragment extends Fragment {

    private ConsultCreatedAuctionsViewModel mViewModel;

    public static ConsultCreatedAuctionsFragment newInstance() {
        return new ConsultCreatedAuctionsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consult_created_auctions, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = new ViewModelProvider(this).get(ConsultCreatedAuctionsViewModel.class);
        // TODO: Use the ViewModel
    }

}