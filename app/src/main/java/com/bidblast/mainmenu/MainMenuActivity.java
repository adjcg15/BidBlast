package com.bidblast.mainmenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.bidblast.R;
import com.bidblast.databinding.ActivityMainMenuBinding;
import com.bidblast.searchauction.SearchAuctionFragment;

public class MainMenuActivity extends AppCompatActivity {
    ActivityMainMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showFragment(new SearchAuctionFragment());
        setupMenuNavigation();
    }

    private void setupMenuNavigation() {
        int searchMenuItemId = R.id.searchMenuItem;
        int purchasesMenuItemId = R.id.purchasesMenuItem;
        int salesMenuItemId = R.id.salesMenuItem;
        int newAuctionMenuItemId = R.id.newAuctionMenuItem;

        binding.mainMenuBottomNavigationView.setOnItemReselectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == searchMenuItemId) {
                showFragment(new SearchAuctionFragment());
            } else if (itemId == purchasesMenuItemId) {
                //TODO: show purchases fragment
            } else if (itemId == salesMenuItemId) {
                //TODO: show sales fragmet
            } else if (itemId == newAuctionMenuItemId) {
                //TODO: show create auction fragment
            }
        });
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(binding.mainViewFragmentLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}