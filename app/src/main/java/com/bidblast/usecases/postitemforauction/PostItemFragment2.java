package com.bidblast.usecases.postitemforauction;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bidblast.R;
import com.bidblast.databinding.FragmentPostItemForAuction2Binding;
public class PostItemFragment2 extends Fragment {

    private PostItemForAuctionViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_item_for_auction2, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(PostItemForAuctionViewModel.class);

        Button createAuctionButton = view.findViewById(R.id.createAuctionButton);

        createAuctionButton.setOnClickListener(v -> {
            String auctionTitle = viewModel.getAuctionTitle().getValue();
            String itemDescription = viewModel.getItemDescription().getValue();
            Integer openingDays = viewModel.getOpeningDays().getValue();
            Integer itemStatus = viewModel.getItemStatus().getValue();

            // Aquí puedes mandar los datos a la base de datos
        });

        return view;
    }
}
