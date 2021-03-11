package com.example.queueManagementSystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminQueuesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminQueuesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminQueuesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminServicesOverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminQueuesFragment newInstance(String param1, String param2) {
        AdminQueuesFragment fragment = new AdminQueuesFragment();
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
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.admin_queues_fragment, container, false);

        // init components
        db = new DatabaseHelper(getActivity());

        displayQueuesTable();

        return view;
    }


    //refresh table view every 1 second
    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                displayQueuesTable();
            }
        }, delay);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    public void displayQueuesTable() {
        // display counters in table
        Cursor cursor = db.getAllCounters();
        TableLayout tl = (TableLayout) view.findViewById(R.id.adminQueuesTableLayout);

        int counter = 0;

        //clear view first
        tl.removeViews(0, tl.getChildCount());


        if (cursor.moveToFirst()) {
            do {
                int counterId = cursor.getInt(0);
                String counterName = cursor.getString(1);
                int currentServingTicketNumber = cursor.getInt(2);
                int remainingInQueue = cursor.getInt(3);

                if (counter == 0) {
                    TableRow tr = genTableHeader();
                    tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }

                TableRow tr = genTableRow(counterId, counterName, currentServingTicketNumber, remainingInQueue, counter);

                // add table row to table layout
                tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                counter++;

            } while (cursor.moveToNext());
        }
    }

    public TableRow genTableRow(int col1Text, String col2Text, int col3Text, int col4Text, int counter) {
        // Create a new row to be added.
        Typeface font = AdminServicesActivity.FontCache.get("fonts/kollektif.ttf", getActivity());
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        if (counter % 2 == 0) { //odd row
            tr.setBackgroundColor(getResources().getColor(R.color.beige_200));
        } else { //even row
            tr.setBackgroundColor(getResources().getColor(R.color.semi_white));
        }

        // create column 1
        TextView col1 = new TextView(getActivity());

        col1.setTypeface(font);
        col1.setPadding(30, 10, 10, 10);
        col1.setText(String.valueOf(col1Text));
        col1.setTextColor(getResources().getColor(R.color.dark_text_2));
        col1.setTextSize(18);

        // create column 2
        TextView col2 = new TextView(getActivity());

        col2.setTypeface(font);
        col2.setPadding(30, 10, 10, 10);
        col2.setText(col2Text);

        col2.setTextColor(getResources().getColor(R.color.magenta3));
        col2.setTextSize(18);

        // create column 3
        TextView col3 = new TextView(getActivity());

        col3.setTypeface(font);
        col3.setPadding(30, 10, 10, 10);

        col3.setText("#" + String.valueOf(col3Text));
        col3.setTextColor(getResources().getColor(R.color.dark_text_2));
        col3.setTextSize(18);
//        col3.setGravity(Gravity.CENTER);

        // create column 4
        TextView col4 = new TextView(getActivity());

        col4.setTypeface(font);
        col4.setPadding(30, 10, 10, 10);

        col4.setText(String.valueOf(col4Text));
        col4.setTextColor(getResources().getColor(R.color.dark_text_2));
        col4.setTextSize(18);
        col4.setGravity(Gravity.CENTER);

        // add columns to table row
        tr.addView(col1);
        tr.addView(col2);
        tr.addView(col3);
        tr.addView(col4);
        return tr;
    }

    public TableRow genTableHeader() {
        // Create a new row to be added.
        Typeface font = AdminServicesActivity.FontCache.get("fonts/kollektif.ttf", getActivity());
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr.setBackgroundColor(getResources().getColor(R.color.magenta3));

        // create column 1
        TextView col1 = new TextView(getActivity());

        col1.setTypeface(font);
        col1.setPadding(30, 10, 10, 10);
        col1.setText("ID");
        col1.setTextColor(getResources().getColor(R.color.beige_200));
        col1.setTextSize(18);

        // create column 2
        TextView col2 = new TextView(getActivity());

        col2.setTypeface(font);
        col2.setPadding(30, 10, 10, 10);
        col2.setText("Counter");

        col2.setTextColor(getResources().getColor(R.color.beige_200));
        col2.setTextSize(18);

        // create column 3
        TextView col3 = new TextView(getActivity());

        col3.setTypeface(font);
        col3.setPadding(30, 10, 10, 10);
        col3.setText("Serving");
        col3.setTextColor(getResources().getColor(R.color.beige_200));
        col3.setTextSize(18);

        // create column 4
        TextView col4 = new TextView(getActivity());

        col4.setTypeface(font);
        col4.setPadding(30, 10, 10, 10);
        col4.setText("Remaining");
        col4.setTextColor(getResources().getColor(R.color.beige_200));
        col4.setTextSize(18);

        // add columns to table row
        tr.addView(col1);
        tr.addView(col2);
        tr.addView(col3);
        tr.addView(col4);

        return tr;
    }
}