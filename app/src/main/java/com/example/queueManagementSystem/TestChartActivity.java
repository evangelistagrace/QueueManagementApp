package com.example.queueManagementSystem;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Pie;
import com.anychart.chart.common.dataentry.ValueDataEntry;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

public class TestChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_chart_activity);

        Pie pie = AnyChart.pie();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("John", 10000));
        data.add(new ValueDataEntry("Jake", 12000));
        data.add(new ValueDataEntry("Peter", 18000));

        pie.data(data);

        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        anyChartView.setChart(pie);
    }
}