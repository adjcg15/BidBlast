package com.bidblast.searchauction;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;

public class SearchAuctionFragment extends Fragment {
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
        return inflater.inflate(R.layout.fragment_search_auction, container, false);
    }
}