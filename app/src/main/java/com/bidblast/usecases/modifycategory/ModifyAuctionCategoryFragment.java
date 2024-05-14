package com.bidblast.usecases.modifycategory;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;

public class ModifyAuctionCategoryFragment extends Fragment {

    public ModifyAuctionCategoryFragment() {

    }

    public static ModifyAuctionCategoryFragment newInstance(String param1, String param2) {
        return new ModifyAuctionCategoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_modify_auction_category, container, false);
    }
}