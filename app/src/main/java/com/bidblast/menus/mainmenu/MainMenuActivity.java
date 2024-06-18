package com.bidblast.menus.mainmenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.bidblast.R;
import com.bidblast.usecases.consultcompletedauctions.ConsultCompletedAuctionsFragment;
import com.bidblast.databinding.ActivityMainMenuBinding;
import com.bidblast.usecases.consultcreatedauctions.ConsultCreatedAuctionsFragment;
import com.bidblast.usecases.createauction.CreateAuctionFragment;
import com.bidblast.usecases.searchauction.SearchAuctionFragment;

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

        binding.mainMenuBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == searchMenuItemId) {
                showFragment(new SearchAuctionFragment());
            } else if (itemId == purchasesMenuItemId) {
                showFragment(new ConsultCompletedAuctionsFragment());
            } else if (itemId == salesMenuItemId) {
                showFragment(new ConsultCreatedAuctionsFragment());
            } else if (itemId == newAuctionMenuItemId) {
                showFragment(new CreateAuctionFragment());
            }

            return true;
        });
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(binding.mainViewFragmentLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}