package com.bidblast.usecases.createauction;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bidblast.R;
import com.bidblast.model.AuctionState;
import com.bidblast.repositories.AuctionsRepository;
import com.bidblast.repositories.IProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;

import java.util.ArrayList;
import java.util.List;
public class CreateAuctionFragment extends Fragment {
    private CreateAuctionViewModel viewModel;
    private Spinner itemStatusSpinner;
    private AuctionState selectedState;
    private EditText auctionTitleEditText;
    private EditText itemDescriptionEditText;
    private EditText openingDaysEditText;
    private TextView auctionTitleErrorTextView;
    private TextView itemStatusErrorTextView;
    private TextView itemDescriptionErrorTextView;
    private TextView openingDaysErrorTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_item_for_auction, container, false);

        itemStatusSpinner = view.findViewById(R.id.itemStatusSpinner);
        auctionTitleEditText = view.findViewById(R.id.auctionTitleEditText);
        itemDescriptionEditText = view.findViewById(R.id.itemDesctiptionEditText);
        openingDaysEditText = view.findViewById(R.id.openingDaysEditText);
        auctionTitleErrorTextView = view.findViewById(R.id.auctionTitleErrorTextView);
        itemStatusErrorTextView = view.findViewById(R.id.itemStatusErrorTextView);
        itemDescriptionErrorTextView = view.findViewById(R.id.itemDescriptionErrorTextView);
        openingDaysErrorTextView = view.findViewById(R.id.openingDaysErrorTextView);

        viewModel = new ViewModelProvider(requireActivity()).get(CreateAuctionViewModel.class);

        loadAuctionStates();
        setFieldLimits();

        view.findViewById(R.id.nextFragmentButton).setOnClickListener(v -> {
            if (validateFields()) {
                String auctionTitle = auctionTitleEditText.getText().toString();
                String itemDescription = itemDescriptionEditText.getText().toString();
                int openingDays = Integer.parseInt(openingDaysEditText.getText().toString());

                Bundle args = new Bundle();
                args.putString("auctionTitle", auctionTitle);
                args.putString("itemDescription", itemDescription);
                args.putInt("openingDays", openingDays);
                args.putInt("itemStatus", selectedState.getId_item_condition());

                CreateAuctionFragment2 fragment2 = new CreateAuctionFragment2();
                fragment2.setArguments(args);

                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment2);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
    private void loadAuctionStates() {
        AuctionsRepository auctionsRepository = new AuctionsRepository();
        auctionsRepository.getAuctionStates(new IProcessStatusListener<List<AuctionState>>() {
            @Override
            public void onSuccess(List<AuctionState> auctionStates) {
                getActivity().runOnUiThread(() -> {
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
                            if (position == 0) {
                                textView.setTextColor(Color.GRAY);
                            } else {
                                textView.setTextColor(Color.BLACK);
                            }
                            return view;
                        }
                    };
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    itemStatusSpinner.setAdapter(adapter);
                    itemStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                selectedState = (AuctionState) parent.getItemAtPosition(position);
                            } else {
                                selectedState = null;
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            selectedState = null;
                        }
                    });
                });
            }
            @Override
            public void onError(ProcessErrorCodes errorCode) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error al cargar los estados de las subastas", Toast.LENGTH_SHORT).show());
            }
        });
    }
    private void setFieldLimits() {
        setTextLimiter(auctionTitleEditText, 40);
        setTextLimiter(itemDescriptionEditText, 255);
        setTextLimiter(openingDaysEditText,5);
    }
    private void setTextLimiter(EditText editText, int maxLength) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }
    private boolean validateFields() {
        boolean isValid = true;

        String auctionTitle = auctionTitleEditText.getText().toString();
        String itemDescription = itemDescriptionEditText.getText().toString();
        String openingDaysText = openingDaysEditText.getText().toString();

        if (auctionTitle.isEmpty()) {
            auctionTitleErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            auctionTitleErrorTextView.setVisibility(View.GONE);
        }

        if (selectedState == null) {
            itemStatusErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            itemStatusErrorTextView.setVisibility(View.GONE);
        }

        if (itemDescription.isEmpty()) {
            itemDescriptionErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            itemDescriptionErrorTextView.setVisibility(View.GONE);
        }

        if (openingDaysText.isEmpty()) {
            openingDaysErrorTextView.setVisibility(View.VISIBLE);
            openingDaysErrorTextView.setText("Ingrese los días de apertura de la subasta");
            isValid = false;
        } else {
            int openingDays = Integer.parseInt(openingDaysText);
            if (openingDays <= 0) {
                openingDaysErrorTextView.setVisibility(View.VISIBLE);
                openingDaysErrorTextView.setText("Los días de apertura deben ser mayor a cero");
                isValid = false;
            } else {
                openingDaysErrorTextView.setVisibility(View.GONE);
            }
        }
        return isValid;
    }
}
