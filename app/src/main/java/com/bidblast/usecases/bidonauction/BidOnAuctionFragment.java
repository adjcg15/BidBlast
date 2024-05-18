package com.bidblast.usecases.bidonauction;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;

public class BidOnAuctionFragment extends Fragment {
    private static final String ARG_ID_AUCTION = "id_auction";
    private int idAuction;

    public BidOnAuctionFragment() {

    }

    public static BidOnAuctionFragment newInstance(int idAuction) {
        BidOnAuctionFragment fragment = new BidOnAuctionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID_AUCTION, idAuction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idAuction = getArguments().getInt(ARG_ID_AUCTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bid_on_auction, container, false);
    }
}