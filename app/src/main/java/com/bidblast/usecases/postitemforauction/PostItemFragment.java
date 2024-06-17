package com.bidblast.usecases.postitemforauction;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.bidblast.R;
import com.bidblast.databinding.FragmentPostItemForAuctionBinding;

public class PostItemFragment extends Fragment {
    private PostItemForAuctionViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_item_for_auction, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(PostItemForAuctionViewModel.class);

        EditText auctionTitleEditText = view.findViewById(R.id.auctionTitleEditText);
        Spinner itemStatusSpinner = view.findViewById(R.id.itemStatusSpinner);
        EditText itemDescriptionEditText = view.findViewById(R.id.itemDesctiptionEditText);
        EditText openingDaysEditText = view.findViewById(R.id.openingDaysEditText);
        Button nextFragmentButton = view.findViewById(R.id.nextFragmentButton);

        auctionTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setAuctionTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        itemDescriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setItemDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        openingDaysEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    viewModel.setOpeningDays(Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        nextFragmentButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PostItemFragment2())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
