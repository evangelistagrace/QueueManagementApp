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
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.admin_insight_fragment, container, false);

        //init components
        db = new DatabaseHelper(getActivity());

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


        // display bar chart for frequent customers
        AnyChartView anyChartView2 = view.findViewById(R.id.any_chart_view2);
        APIlib.getInstance().setActiveAnyChartView(anyChartView2);

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data2 = new ArrayList<>();

        Cursor cursor2 = db.getTopCustomers();
        int count = 0;

        //limit to top 5 customers
        if (cursor2.moveToFirst()) {
            do {
                String custName = cursor2.getString(1);
                int custRequests = cursor2.getInt(2);
                data2.add(new ValueDataEntry(custName, custRequests));
                count++;
            } while (cursor2.moveToNext() && count < 5);
        }

        Column column = cartesian.column(data2);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Top 5 customers by ticket requests");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Customer");
        cartesian.yAxis(0).title("No. of requests");

        anyChartView2.setChart(cartesian);

        return view;
    }
}