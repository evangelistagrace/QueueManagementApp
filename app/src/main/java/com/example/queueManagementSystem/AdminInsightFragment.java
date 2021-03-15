package com.example.queueManagementSystem;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.core.cartesian.series.Column;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.MarkerType;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminInsightFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminInsightFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminInsightFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminInsightFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminInsightFragment newInstance(String param1, String param2) {
        AdminInsightFragment fragment = new AdminInsightFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View view;
    DatabaseHelper db;
    private static DecimalFormat df2 = new DecimalFormat("#.##");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.admin_insight_fragment, container, false);

        //init components
        db = new DatabaseHelper(getActivity());


        // FIRST CHART
        // display pie chart for services and the amount of requests received
        AnyChartView anyChartView = (AnyChartView) view.findViewById(R.id.any_chart_view);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));

        Pie pie = AnyChart.pie();
        pie.title("Percentage of ticket requests by service");

        List<DataEntry> data = new ArrayList<>();
        Cursor cursor = db.getServices();

        if (cursor.moveToFirst()) {
            do {
                data.add(new ValueDataEntry(cursor.getString(1), cursor.getInt(3)));
            } while (cursor.moveToNext());
        }

        pie.data(data);
        pie.animation(true);
        anyChartView.setChart(pie);



        // SEOND CHART
        // display average waiating time of customers
        AnyChartView anyChartView2 = view.findViewById(R.id.any_chart_view2);
        APIlib.getInstance().setActiveAnyChartView(anyChartView2);
        anyChartView2.setProgressBar(view.findViewById(R.id.progress_bar2));

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.title("Average Waiting Time");

        cartesian.yAxis(0).title("Avg. Waiting Time (minutes)");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();
        ArrayList<Double> dataSet = AdminActivity.getAvgServingTimeArr();

        for (int i=0; i< dataSet.size(); i++) {
            String val = df2.format(dataSet.get(i));
            seriesData.add(new CustomDataEntry(String.valueOf(i), Double.parseDouble(val), null, null));
        }

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");


        Line series1 = cartesian.line(series1Mapping);
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(false);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView2.setChart(cartesian);


        //THIRD CHART
        // display bar chart for frequent customers
        AnyChartView anyChartView3 = view.findViewById(R.id.any_chart_view3);
        APIlib.getInstance().setActiveAnyChartView(anyChartView3);

        Cartesian cartesian2 = AnyChart.column();

        List<DataEntry> data3 = new ArrayList<>();

        Cursor cursor3 = db.getTopCustomers();
        int count2 = 0;

        //limit to top 5 customers
        if (cursor3.moveToFirst()) {
            do {
                String custName = cursor3.getString(1);
                int custRequests = cursor3.getInt(2);
                data3.add(new ValueDataEntry(custName, custRequests));
                count2++;
            } while (cursor3.moveToNext() && count2 < 5);
        }

        Column column2 = cartesian2.column(data3);

        column2.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        cartesian2.animation(true);
        cartesian2.title("Top 5 customers by ticket requests");

        cartesian2.yScale().minimum(0d);

        cartesian2.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

        cartesian2.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian2.interactivity().hoverMode(HoverMode.BY_X);

        cartesian2.xAxis(0).title("Customer");
        cartesian2.yAxis(0).title("No. of requests");

        anyChartView3.setChart(cartesian2);

        return view;
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value, Number value2, Number value3) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
        }

    }
}