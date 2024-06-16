package com.bidblast.usecases.consultaauctioncategories;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bidblast.R;
import com.bidblast.model.AuctionCategory;
import com.bidblast.usecases.modifycategory.ModifyAuctionCategoryFragment;

public class ConsultAuctionCategoriesFragment extends Fragment {
    private RecyclerView recyclerView;
    private AuctionCategoryAdapter adapter;
    private ConsultAuctionCategoriesViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consult_categories, container, false);

        recyclerView = view.findViewById(R.id.eq_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AuctionCategoryAdapter(getContext());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ConsultAuctionCategoriesViewModel.class);
        observeViewModel();

        viewModel.loadAuctionCategories();

        return view;
    }

    private void observeViewModel() {
        viewModel.getAuctionCategories().observe(getViewLifecycleOwner(), auctionCategories -> {
            if (auctionCategories != null) {
                adapter.submitList(auctionCategories);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorCode -> {
            if (errorCode != null) {
                Toast.makeText(getContext(), "Error loading categories", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
