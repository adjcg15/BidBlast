package com.bidblast.usecases.modifycategory;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;
import com.bidblast.databinding.FragmentModifyAuctionCategoryBinding;
import com.bidblast.usecases.login.LoginViewModel;

public class ModifyAuctionCategoryFragment extends Fragment {

    private FragmentModifyAuctionCategoryBinding binding;
    private ModifyAuctionCategoryViewModel viewModel;

    private static final String ID_KEY = "id";
    private static final String TITLE_KEY = "title";
    private static final String DESCRIPTION_KEY = "description";
    private static final String KEYWORDS_KEY = "keywords";

    private int id;
    private String title;
    private String description;
    private String keywords;

    public ModifyAuctionCategoryFragment() {}

    public static ModifyAuctionCategoryFragment newInstance(int id, String title, String description, String keywords) {
        ModifyAuctionCategoryFragment fragment = new ModifyAuctionCategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ID_KEY, id);
        args.putString(TITLE_KEY, title);
        args.putString(DESCRIPTION_KEY, description);
        args.putString(KEYWORDS_KEY, keywords);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ID_KEY);
            title = getArguments().getString(TITLE_KEY);
            description = getArguments().getString(DESCRIPTION_KEY);
            keywords = getArguments().getString(KEYWORDS_KEY);
            // Aquí podrías hacer cualquier cosa que necesites con los datos, como pasárselos al ViewModel
            // viewModel.setData(id, title, description, keywords);
        }

        viewModel = new ViewModelProvider(this).get(ModifyAuctionCategoryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentModifyAuctionCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}

