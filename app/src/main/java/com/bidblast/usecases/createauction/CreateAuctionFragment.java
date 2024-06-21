package com.bidblast.usecases.createauction;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bidblast.databinding.FragmentPostItemForAuctionBinding;
import com.bidblast.menus.mainmenu.MainMenuActivity;
import com.bidblast.model.AuctionState;
import com.bidblast.repositories.AuctionsRepository;
import com.bidblast.repositories.IProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;
import com.bidblast.usecases.searchauction.SearchAuctionFragment;

import java.util.ArrayList;
import java.util.List;
public class CreateAuctionFragment extends Fragment {
    private CreateAuctionViewModel viewModel;
    private FragmentPostItemForAuctionBinding binding;
    private AuctionState selectedState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPostItemForAuctionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(requireActivity()).get(CreateAuctionViewModel.class);

        setupUI();
        setupListeners();

        return view;
    }

    private void setupUI() {
        setTextLimiter(binding.auctionTitleEditText, 40);
        setTextLimiter(binding.itemDesctiptionEditText, 255);
        setTextLimiter(binding.openingDaysEditText, 5);
        loadAuctionStates();
    }
    private void loadAuctionStates() {
        AuctionsRepository auctionsRepository = new AuctionsRepository();
        auctionsRepository.getAuctionStates(new IProcessStatusListener<List<AuctionState>>() {
            @Override
            public void onSuccess(List<AuctionState> auctionStates) {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(() -> populateSpinner(auctionStates));
                }
            }

            @Override
            public void onError(ProcessErrorCodes errorCode) {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(() -> showToast("Error al cargar los estados de las subastas"));
                }

            }
        });
    }

    private void populateSpinner(List<AuctionState> auctionStates) {
        List<AuctionState> allStates = new ArrayList<>();
        allStates.add(new AuctionState(-1, "Selecciona el estado del artículo"));
        allStates.addAll(auctionStates);

        ArrayAdapter<AuctionState> adapter = new ArrayAdapter<AuctionState>(getContext(), android.R.layout.simple_spinner_item, allStates) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.itemStatusSpinner.setAdapter(adapter);
        binding.itemStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedState = position > 0 ? (AuctionState) parent.getItemAtPosition(position) : null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedState = null;
            }
        });
    }

    private void navigateToNextFragment() {
        if (validateFields()) {
            Bundle args = new Bundle();
            args.putString("auctionTitle", binding.auctionTitleEditText.getText().toString());
            args.putString("itemDescription", binding.itemDesctiptionEditText.getText().toString());
            args.putInt("openingDays", Integer.parseInt(binding.openingDaysEditText.getText().toString()));
            args.putInt("itemStatus", selectedState.getId_item_condition());

            CreateAuctionFragment2 fragment2 = new CreateAuctionFragment2();
            fragment2.setArguments(args);

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment2);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void setTextLimiter(EditText editText, int maxLength) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (binding.auctionTitleEditText.getText().toString().isEmpty()) {
            binding.auctionTitleErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            binding.auctionTitleErrorTextView.setVisibility(View.GONE);
        }

        if (selectedState == null) {
            binding.itemStatusErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            binding.itemStatusErrorTextView.setVisibility(View.GONE);
        }

        if (binding.itemDesctiptionEditText.getText().toString().isEmpty()) {
            binding.itemDescriptionErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            binding.itemDescriptionErrorTextView.setVisibility(View.GONE);
        }

        String openingDaysText = binding.openingDaysEditText.getText().toString();
        if (openingDaysText.isEmpty()) {
            binding.openingDaysErrorTextView.setVisibility(View.VISIBLE);
            binding.openingDaysErrorTextView.setText("Ingrese los días de apertura de la subasta");
            isValid = false;
        } else {
            int openingDays = Integer.parseInt(openingDaysText);
            if (openingDays <= 0) {
                binding.openingDaysErrorTextView.setVisibility(View.VISIBLE);
                binding.openingDaysErrorTextView.setText("Los días de apertura deben ser mayor a cero");
                isValid = false;
            } else {
                binding.openingDaysErrorTextView.setVisibility(View.GONE);
            }
        }

        return isValid;
    }
    private void setupListeners() {
        binding.nextFragmentButton.setOnClickListener(v -> navigateToNextFragment());
        binding.cancelCreateAuctionButton.setOnClickListener(v -> showCancelConfirmationDialog());
    }

    private void navigateToMainMenu() {
        if (getActivity() instanceof MainMenuActivity) {
            MainMenuActivity activity = (MainMenuActivity) getActivity();
            activity.showFragment(new SearchAuctionFragment());
            activity.selectSearchAuctionMenuItem();
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmación")
                .setMessage("¿Estás seguro de que deseas cancelar la creación de la subasta?")
                .setPositiveButton("Sí", (dialog, which) -> navigateToMainMenu())
                .setNegativeButton("No", null)
                .show();
    }
}
