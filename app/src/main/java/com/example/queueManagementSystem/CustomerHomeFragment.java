package com.example.queueManagementSystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.BlendMode;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerHomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment customer_home.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerHomeFragment newInstance(String param1, String param2) {
        CustomerHomeFragment fragment = new CustomerHomeFragment();
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
    private ActionBar toolbar;
    ArrayList<Service> services = new ArrayList<>();
    DatabaseHelper db;
    Customer customer;
    Intent currentIntent;
    String username;
    ConstraintLayout constraintLayout;
    TextView tvWelcome;

    @SuppressLint({"NewApi", "UseCompatTextViewDrawableApis"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.customer_home_fragment, container, false);

        db = new DatabaseHelper(getActivity());

        currentIntent = getActivity().getIntent();
        customer = (Customer) currentIntent.getSerializableExtra("customerObject");
        services = CustomerActivity.getServices();
        tvWelcome = (TextView) view.findViewById(R.id.tvWelcome);

        constraintLayout = (ConstraintLayout) view.findViewById(R.id.CustomerServiceMenuLayout);

        // set welome text
        tvWelcome.setText("Welcome back, " + customer.getUsername());

        Button prevBtn = null;
//
//        // SERVICE MENU
//        // create dynamic service menu
//        // instantiate and start counters
        for (Service service: services) {
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            // style button
            Button btn = new Button(getActivity());
            btn.setId(service.getServiceId());
            btn.setText(service.getServiceName());
            btn.setTextSize(16);
            btn.setPadding(
                    Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics())),
                    Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics())),
                    Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics())),
                    Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()))
            );
            btn.setGravity(Gravity.START);
            btn.setLayoutParams(lp); // set width to 0dp
            // set icon
            btn.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_right), null);
            btn.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.magenta3)));
            btn.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
            enableButton(btn);

            // add button to view
            constraintLayout.addView(btn);

            // add constraints
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.connect(btn.getId(),ConstraintSet.RIGHT,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT,0);
            constraintSet.connect(btn.getId(),ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.LEFT,0);

            if (prevBtn == null) { // n first button yet
                constraintSet.connect(btn.getId(),ConstraintSet.TOP,ConstraintSet.PARENT_ID,ConstraintSet.TOP,0);
            } else {
                constraintSet.connect(btn.getId(),ConstraintSet.TOP,prevBtn.getId(),ConstraintSet.BOTTOM, 40);
            }

            // add constraints to layout
            constraintSet.applyTo(constraintLayout);

            prevBtn = (Button) btn;

            // disable button and and return early if service has been stopped/disabled
            if (service.isRunning() == 0) {
                disableButton(btn);
                continue;
            }

            // dynamically attach click listener
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // need to overwrite to pass service object and customer object to service queue options fragment
                    Fragment fragment;
                    Ticket requestedTicket = customer.sendTicketRequest(customer, service.getServiceId()); //
                    QueueManager queueManager = null;

                    if (requestedTicket != null) {
                        for (Counter counter : service.getCounters()) {
                            for (Ticket ticket : counter.getQueueManager().getTickets()) { // there can only be one queuemanager handling a customer's ticket for a given service
                                if (ticket.getTicketNumber() == requestedTicket.getTicketNumber()) {
                                    queueManager = counter.getQueueManager();
                                }
                            }
                        }
                        currentIntent.putExtra("ticketObject", requestedTicket);
                        currentIntent.putExtra("customerObject", customer);
                        currentIntent.putExtra("queueManagerObject", queueManager);

                        fragment = new CustomerTicketFragment();
                        loadFragment(fragment);

                        Toast.makeText(getActivity(), "Successfully requested for a ticket", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Error in requesting ticket", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
            });


            // disable ticket request btn for a service if there's already an active ticket for the service
            for (Ticket ticket: customer.getTickets()) {
                if (ticket.getService().getServiceId() == btn.getId()) {
                   disableButton(btn);
                }
            }

            //start counter
            startCounters(service);
            service.setCountersStarted(true);
        }

        return view;
    }

    public void startCounters(Service service) {
        if (service.getCountersStarted()) {
            return; //return early if counters for the service had already started
        }
        ArrayList<Counter> counters;
        // get services from db
        Cursor cursor = db.getCounters(service.getServiceId());

        // instantiate counters
        if (cursor.moveToFirst()) {
            do {
                service.addCounter(new Counter(cursor.getInt(0), cursor.getString(1), true, service));

            } while (cursor.moveToNext());
        }

        counters = service.getCounters();

        // start counters
        for (Counter counter: counters) {
            if (counter.open()) {
                counter.getQueueManager().run();
            }
        }
    }

    void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.customer_fragment_container, fragment, Integer.toString(getFragmentCount()));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected int getFragmentCount() {
        return getActivity().getSupportFragmentManager().getBackStackEntryCount();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void enableButton(Button btn) {
        btn.setEnabled(true);
        btn.setBackground(getResources().getDrawable(R.drawable.semi_opaque_button));
        btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        btn.setTextColor(getResources().getColor(R.color.magenta3));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void disableButton(Button btn) {
        btn.setBackgroundTintList(null);
        btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey_400)));
        btn.setTextColor(getResources().getColor(R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btn.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        }
        btn.setEnabled(false);
        btn.setOnClickListener(null);
    }
}