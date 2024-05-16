package com.bidblast.usecases.consultsalesstatistics;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.bidblast.R;
import com.bidblast.usecases.modifycategory.ModifyAuctionCategoryViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.bidblast.databinding.FragmentConsultSalesStatisticsBinding;

public class ConsultSalesStatisticsFragment extends Fragment {

    private FragmentConsultSalesStatisticsBinding binding;
    private ConsultSalesStatisticsViewModel viewModel;
    public ConsultSalesStatisticsFragment() {
    }
    public static ConsultSalesStatisticsFragment newInstance() {
        return new ConsultSalesStatisticsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ConsultSalesStatisticsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConsultSalesStatisticsBinding.inflate(inflater, container, false);
        setupFirstDateEditText();
        setupSecondDateEditText();
        // Referencia al BarChart desde View Binding
        BarChart barChart = binding.barChart;

        // Crear un conjunto de datos de barras
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, 50)); // (X, Y)
        entries.add(new BarEntry(2, 70));
        entries.add(new BarEntry(3, 90));
        entries.add(new BarEntry(4, 110));
        entries.add(new BarEntry(5, 130));

        // Crear un conjunto de datos a partir de las entradas
        BarDataSet dataSet = new BarDataSet(entries, "Productos");

        // Personalización del conjunto de datos
        dataSet.setColor(Color.BLUE);

        // Crear una instancia de BarData y establecer el conjunto de datos
        BarData barData = new BarData(dataSet);

        // Configurar el BarChart
        // Calcular el ancho total necesario para mostrar todas las barras
        float anchoBarra = 2f; // Ancho de una barra en píxeles
        int totalBarras = 300; // Número total de barras
        float anchoTotal = anchoBarra * totalBarras; // Ancho total necesario para mostrar todas las barras

        ViewGroup.LayoutParams params = barChart.getLayoutParams();
        params.height = (int) anchoTotal;
        params.width =  MATCH_PARENT;// Convertir el ancho total a un entero

        barChart.setLayoutParams(params);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setText("Precio de productos");
        barChart.getDescription().setTextSize(16f);
        barChart.getDescription().setTextColor(Color.BLACK);
        barChart.animateY(2000);


        ArrayList<String> productos = new ArrayList<>();
        productos.add("Producto A");
        productos.add("Producto B");
        productos.add("Producto C");

        ArrayList<Float> precios = new ArrayList<>();
        precios.add(100f); // Precio del Producto A
        precios.add(50f); // Precio del Producto B
        precios.add(200f); // Precio del Producto C
        List<PieEntry> pieEntries = new ArrayList<>();

        for (int i = 0; i < productos.size(); i++) {
            pieEntries.add(new PieEntry(precios.get(i), productos.get(i)));
        }
        PieDataSet pieDtaSet = new PieDataSet(pieEntries, "Productos");
        pieDtaSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDtaSet.setValueTextSize(12f);

        PieData pieData = new PieData(pieDtaSet);
        PieChart pieChart = binding.pieChart;
        pieChart.setData(pieData);
        pieChart.invalidate();
        return binding.getRoot();
    }

    private void setupFirstDateEditText(){
        binding.firstDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerToFirstDate();
            }
        });
    }

    private void showDatePickerToFirstDate(){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        binding.firstDateEditText.setText(selectedDate);
                    }
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
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        binding.secondDateEditText.setText(selectedDate);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
}