package com.example.queueManagementSystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
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
    Button btnServiceLoans, btnServiceAccounts, btnServicePayments;
    DatabaseHelper db;
    Customer customer;
    Intent currentIntent;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_activity);

        // add code after this line
        //init components
        db = new DatabaseHelper(this);
        btnServiceLoans = findViewById(R.id.btnServiceLoans);
        btnServiceAccounts = findViewById(R.id.btnServiceAccounts);
        toolbar = getSupportActionBar();
        currentIntent = getIntent();

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

        // instantiate and start counters
        for (Service service: services) {
            startCounters(service);
        }

        // SERVICE MENU
       btnServiceLoans.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               customer.sendTicketRequest(customer, 1); //assume customer had clicked on a service with service id 1
               Toast.makeText(CustomerActivity.this, "new ticket requested by " + customer.getUsername(), Toast.LENGTH_SHORT).show();
           }
       });


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