package com.bidblast.menus.moderatormenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.bidblast.R;
import com.bidblast.databinding.ActivityModeratorMenuBinding;
import com.bidblast.usecases.consultcompletedauctions.ConsultCompletedAuctionsFragment;
import com.bidblast.usecases.consultcreatedauctions.ConsultCreatedAuctionsFragment;
import com.bidblast.usecases.registerandmodifycategory.AuctionCategoryFormFragment;
import com.bidblast.usecases.searchauction.SearchAuctionFragment;

public class ModeratorMenuActivity extends AppCompatActivity {
    ActivityModeratorMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModeratorMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showFragment(new ConsultAuctionCategoriesFragment());
        setupMenuNavigation();
    }

    private void setupMenuNavigation() {
        int categoriesMenuItemId = R.id.categoriesMenuItem;
        int auctionsMenuItemId = R.id.auctionsMenuItem;
        int statisticsMenuItemId = R.id.statisticsMenuItem;

        binding.mainMenuBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == categoriesMenuItemId) {
                showFragment(new ConsultAuctionCategoriesFragment());
            } else if (itemId == auctionsMenuItemId) {
                //TODO: show auctions fragment
            } else if (itemId == statisticsMenuItemId) {
                //TODO: show statistics fragment
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