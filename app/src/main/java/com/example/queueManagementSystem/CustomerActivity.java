package com.example.queueManagementSystem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class CustomerActivity extends AppCompatActivity {
    private ActionBar toolbar;
    protected static ArrayList<Service> services = new ArrayList<>();
    DatabaseHelper db;
    Customer customer;
    Intent currentIntent;
    String username;
    ConstraintLayout constraintLayout;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_activity);

        // add code after this line
        //init components
        db = new DatabaseHelper(this);
        toolbar = getSupportActionBar();
        currentIntent = getIntent();
        constraintLayout = (ConstraintLayout) findViewById(R.id.CustomerServiceMenuLayout);

//        toolbar.setTitle("Home");

        //instantiate customer
        username = currentIntent.getStringExtra("USERNAME");
        customer = new Customer(username);

        Toast.makeText(CustomerActivity.this, "Welcome " + customer.getUsername(), Toast.LENGTH_SHORT).show();


        // instantiate services
        Cursor cursor = db.getServices();

        if (cursor.moveToFirst()) {
            do {
                services.add(new Service(cursor.getInt(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }

        Button prevBtn = null;

        // SERVICE MENU
        // create dynamic service menu
        // instantiate and start counters
        for (Service service: services) {
            Button btn = new Button(this);
            btn.setId(service.getServiceId());
            btn.setText(service.getServiceName());
            btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.salmon_500)));
            btn.setTextSize(16);
            btn.setTextColor(getResources().getColor(R.color.white));
            btn.setWidth(700);
            btn.setPadding(0, 10, 0, 10);

            // style button
            // add button to view
            // add constraints
            // add constraints to layout
            constraintLayout.addView(btn);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.connect(btn.getId(),ConstraintSet.RIGHT,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT,0);
            constraintSet.connect(btn.getId(),ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.LEFT,0);

            if (prevBtn == null) { // n first button yet
                constraintSet.connect(btn.getId(),ConstraintSet.TOP,ConstraintSet.PARENT_ID,ConstraintSet.TOP,0);
            } else {
                constraintSet.connect(btn.getId(),ConstraintSet.TOP,prevBtn.getId(),ConstraintSet.BOTTOM, 20);
            }

            constraintSet.applyTo(constraintLayout);


            prevBtn = (Button) btn;

            // dynamically attach click listener
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // need to overwrite to pass service object and customer object to service queue options fragment
                    Fragment fragment;
                    Ticket requestedTicket = customer.sendTicketRequest(customer, service.getServiceId());
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

                        Toast.makeText(CustomerActivity.this, "Successfully requested for a ticket", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CustomerActivity.this, "Error in requesting ticket", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
            });

            //start counter
            startCounters(service);
        }


        // BOTTOM NAVIGATION
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
//                        toolbar.setTitle("Home");
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return true;
                    case R.id.action_tickets:
//                        toolbar.setTitle("Tickets");
                        // load customer object into intent before loading fragment
                        currentIntent.putExtra("customerObject", customer);
                        fragment = new CustomerTicketFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.action_profile:
//                        toolbar.setTitle("Profile");
                        fragment = new CustomerProfileFragment();
                        loadFragment(fragment);
                        return true;
                }
                return false;
            }
        });
    }

    public static ArrayList<Service> getServices() {
        return services;
    }

    public void startCounters(Service service) {
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

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.customer_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}