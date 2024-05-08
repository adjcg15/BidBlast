package com.bidblast.consultcompletedauctions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;

public class ConsultCompletedAuctionsFragment extends Fragment {
    public ConsultCompletedAuctionsFragment() {

    }

    public static ConsultCompletedAuctionsFragment newInstance(String param1, String param2) {
        return new ConsultCompletedAuctionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consult_completed_auctions, container, false);
    }
}