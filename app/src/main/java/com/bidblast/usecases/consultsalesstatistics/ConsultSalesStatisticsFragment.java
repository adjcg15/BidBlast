package com.bidblast.usecases.consultsalesstatistics;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.lib.CurrencyToolkit;
import com.bidblast.lib.DateToolkit;
import com.bidblast.model.Auction;
import com.bidblast.repositories.ProcessErrorCodes;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.bidblast.databinding.FragmentConsultSalesStatisticsBinding;
import com.google.android.material.snackbar.Snackbar;

public class ConsultSalesStatisticsFragment extends Fragment {

    private FragmentConsultSalesStatisticsBinding binding;
    private ConsultSalesStatisticsViewModel viewModel;
    private List<Auction> salesAuctionsList;
    private float profitsEarned = 0;
    private final List<String> categories = new ArrayList<>();
    private final List<Integer> categoriesCount = new ArrayList<>();
    private final List<Date> salesDates = new ArrayList<>();
    private  final List<Integer> salesDatesCount = new ArrayList<>();
    private  final List<Float> salesDatesAmounts = new ArrayList<>();
    private String startDate;
    private String endDate;
    public ConsultSalesStatisticsFragment() {
    }
    public static ConsultSalesStatisticsFragment newInstance() {
        return new ConsultSalesStatisticsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ConsultSalesStatisticsViewModel.class);
        getSalesAuctionsList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConsultSalesStatisticsBinding.inflate(inflater, container, false);
        setupFirstDateEditText();
        setupSecondDateEditText();
        setupSalesAuctionsListStatusListener();
        setupFirstDateListener();
        setupSecondDateListener();
        setupDiscardModifyCategoryButton();
        return binding.getRoot();
    }

    private void setupDiscardModifyCategoryButton() {
        binding.goBackImageButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });
    }

    private void setupFirstDateEditText(){
        binding.firstDateEditText.setOnClickListener(v -> showDatePickerToFirstDate());
    }

    private void showDatePickerToFirstDate(){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    String selectedDate = dayOfMonth1 + "/" + (monthOfYear + 1) + "/" + year1;
                    binding.firstDateEditText.setText(selectedDate);
                    LocalDate date = LocalDate.of(year1, monthOfYear + 1, dayOfMonth1);
                    startDate = DateToolkit.parseISO8601FromLocalDate(date);
                }, year, month, dayOfMonth);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setupSecondDateEditText(){
        binding.secondDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerToSecondDate();
            }
        });
    }

    private void showDatePickerToSecondDate(){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        String firstDateStr = binding.firstDateEditText.getText().toString();
        if (!TextUtils.isEmpty(firstDateStr)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date firstDate = sdf.parse(firstDateStr);
                calendar.setTime(firstDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    String selectedDate = dayOfMonth1 + "/" + (monthOfYear + 1) + "/" + year1;
                    binding.secondDateEditText.setText(selectedDate);
                    LocalDate date = LocalDate.of(year1, monthOfYear + 1, dayOfMonth1 + 1);
                    endDate = DateToolkit.parseISO8601FromLocalDate(date);
                }, year, month, dayOfMonth);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setupFirstDateListener() {
        binding.firstDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                profitsEarned = 0;
                categories.clear();
                categoriesCount.clear();
                salesAuctionsList.clear();
                salesDates.clear();
                salesDatesAmounts.clear();
                salesDatesCount.clear();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (startDate != null && endDate != null) {
                        getSalesAuctionsList();
                    }
                }, 1);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupSecondDateListener() {
        binding.secondDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                profitsEarned = 0;
                categories.clear();
                categoriesCount.clear();
                salesAuctionsList.clear();
                salesDates.clear();
                salesDatesAmounts.clear();
                salesDatesCount.clear();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (startDate != null && endDate != null) {
                        getSalesAuctionsList();
                    }
                }, 1);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void getSalesAuctionsList() {
        if(viewModel.getSalesAuctionsListRequestStatus().getValue() != RequestStatus.LOADING) {
            viewModel.recoverSalesAuctions(startDate, endDate);
        }
    }

    private void setupSalesAuctionsListStatusListener() {
        viewModel.getSalesAuctionsListRequestStatus().observe(getViewLifecycleOwner(), requestStatus -> {
            if (requestStatus == RequestStatus.DONE) {
                salesAuctionsList = viewModel.getSalesAuctionsList().getValue();
                if (salesAuctionsList.size() != 0) {
                    collapseSalesAuctionsImageError();
                    collapseEmptySalesAuctionsImage();
                    calculateAndShowSalesStatistics();
                    calculateAndShowCategoryStatistics();
                    calculateAndShowFeaturedDay();
                } else {
                    if (startDate == null && endDate == null) {
                        collapseDatesRangeSection();
                    } else {
                        changeEmptySalesAuctionsImageStringValues();
                    }
                    collapseStatisticsSections();
                    showEmptySalesAuctionsImage();
                }
            }

            if (requestStatus == RequestStatus.ERROR) {
                ProcessErrorCodes errorCode = viewModel.getSalesAuctionsListErrorCode().getValue();

                if(errorCode != null) {
                    showSalesAuctionsImageError();
                }
            }
        });
    }

    private void collapseStatisticsSections() {
        binding.earnedProfitsLinerLayout.setVisibility(View.GONE);
        binding.salesCategoriesLinerLayout.setVisibility(View.GONE);
        binding.bestDateLinerLayout.setVisibility(View.GONE);
    }

    private void calculateAndShowSalesStatistics() {
        binding.earnedProfitsLinerLayout.setVisibility(View.VISIBLE);
        BarChart barChart = binding.barChart;
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < salesAuctionsList.size(); i++) {
            Auction auction = salesAuctionsList.get(i);
            profitsEarned += auction.getLastOffer().getAmount();
            entries.add(new BarEntry(i, auction.getLastOffer().getAmount()));
            labels.add(trimString(auction.getTitle(), 12));
        }

        binding.profitsEarnedTextView.setText(CurrencyToolkit.parseToMXN(profitsEarned));
        BarDataSet dataSet = new BarDataSet(entries, "Subastas vendidas");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(dataSet);

        float barWidth = 2f;
        int bars = salesAuctionsList.size() * 80;
        float barHeight = barWidth * bars;

        ViewGroup.LayoutParams params = barChart.getLayoutParams();
        params.height = (int) barHeight;
        params.width =  MATCH_PARENT;

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int position = (int) e.getX();
                Auction selectedAuction = salesAuctionsList.get(position);
                Snackbar.make(binding.getRoot(), selectedAuction.getTitle(), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {}
        });

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.setLayoutParams(params);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setText("Precio de productos");
        barChart.getDescription().setTextSize(16f);
        barChart.getDescription().setTextColor(Color.BLACK);
        barChart.animateY(2000);
        barChart.invalidate();
    }

    private void calculateAndShowCategoryStatistics() {
        binding.salesCategoriesLinerLayout.setVisibility(View.VISIBLE);
        for (int i = 0; i < salesAuctionsList.size(); i++) {
            Auction auction = salesAuctionsList.get(i);
            if (!categories.contains(auction.getCategory().getTitle())) {
                categories.add(auction.getCategory().getTitle());
            }
        }
        for (int i = 0; i < categories.size(); i++) {
            int count = 0;
            String category = categories.get(i);
            for (int j = 0; j < salesAuctionsList.size(); j++) {
                Auction auction = salesAuctionsList.get(j);
                if (category.equals(auction.getCategory().getTitle())) {
                    count += 1;
                }
            }
            categoriesCount.add(count);
        }

        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            pieEntries.add(new PieEntry(categoriesCount.get(i), categories.get(i)));
        }

        PieDataSet pieDtaSet = new PieDataSet(pieEntries, "");
        pieDtaSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDtaSet.setValueTextSize(12f);

        PieData pieData = new PieData(pieDtaSet);

        PieChart pieChart = binding.pieChart;

        Description description = new Description();
        description.setText("");

        pieChart.setDescription(description);
        pieChart.setData(pieData);

        pieChart.invalidate();
    }

    private void calculateAndShowFeaturedDay() {
        binding.bestDateLinerLayout.setVisibility(View.VISIBLE);
        Date bestDate = new Date();
        int totalAuctions = 0;
        float totalAmount = 0;
        for (int i = 0; i < salesAuctionsList.size(); i++) {
            Auction auction = salesAuctionsList.get(i);
            if (!salesDates.contains(auction.getUpdatedDate())) {
                salesDates.add(auction.getUpdatedDate());
            }
        }
        for (int i = 0; i < salesDates.size(); i++) {
            int count = 0;
            float amount = 0;
            LocalDate saleDate = salesDates.get(i).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            for (int j = 0; j < salesAuctionsList.size(); j++) {
                Auction auction = salesAuctionsList.get(j);
                LocalDate updatedDate = auction.getUpdatedDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                if (saleDate.equals(updatedDate)) {
                    count += 1;
                    amount += auction.getLastOffer().getAmount();
                }
            }
            salesDatesCount.add(count);
            salesDatesAmounts.add(amount);
        }

        float amountMax = 0;
        for (int i = 0; i < salesDates.size(); i++) {
            Date saleSate = salesDates.get(i);
            int auctions = salesDatesCount.get(i);
            float amount = salesDatesAmounts.get(i);
            if (amount > amountMax) {
                bestDate = saleSate;
                totalAuctions = auctions;
                totalAmount = amount;
            }
        }

        binding.bestDateTextView.setText(DateToolkit.parseToFullDate(bestDate));
        String featuredDayMessage = getString(R.string.consultsalesstatistics_first_featured_day_message) +
                " " + totalAuctions + " " + getString(R.string.consultsalesstatistics_second_featured_day_message);
        binding.totalAuctionsTextView.setText(featuredDayMessage);
        binding.totalAmountTextView.setText(CurrencyToolkit.parseToMXN(totalAmount));
    }

    public static String trimString(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        } else {
            return text.substring(0, maxLength);
        }
    }

    private void collapseDatesRangeSection() {
        binding.datesTitleTextView.setVisibility(View.GONE);
        binding.datesRangeLinerLayout.setVisibility(View.GONE);
    }

    private void changeEmptySalesAuctionsImageStringValues() {
        String emptySalesAuctionsTitle = getString(R.string.consultsalesstatistics_empty_sales_auctions_dates_title);
        String emptySalesAuctionsText = getString(R.string.consultsalesstatistics_empty_sales_auctions_dates_text);
        binding.emptySalesAuctionsTitle.setText(emptySalesAuctionsTitle);
        binding.emptySalesAuctionsText.setText(emptySalesAuctionsText);
    }

    private void showSalesAuctionsImageError() {
        collapseDatesRangeSection();
        collapseStatisticsSections();
        binding.errorLoadingSalesAuctionsLinearLayout.setVisibility(View.VISIBLE);
    }

    private void collapseSalesAuctionsImageError() {
        binding.errorLoadingSalesAuctionsLinearLayout.setVisibility(View.GONE);
    }

    private void showEmptySalesAuctionsImage() {
        collapseStatisticsSections();
        binding.emptySalesAuctionsMessageLinearLayout.setVisibility(View.VISIBLE);
    }

    private void collapseEmptySalesAuctionsImage() {
        binding.emptySalesAuctionsMessageLinearLayout.setVisibility(View.GONE);
    }
}