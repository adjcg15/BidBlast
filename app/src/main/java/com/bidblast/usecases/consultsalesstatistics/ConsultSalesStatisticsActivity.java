package com.bidblast.usecases.consultsalesstatistics;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import com.bidblast.databinding.ActivityConsultSalesStatisticsBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ConsultSalesStatisticsActivity extends AppCompatActivity {

    private ActivityConsultSalesStatisticsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConsultSalesStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
    }
}