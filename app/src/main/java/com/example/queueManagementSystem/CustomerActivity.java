package com.example.queueManagementSystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_activity);

        // add code after this line
        btnServiceLoans = findViewById(R.id.btnServiceLoans);
        btnServiceAccounts = findViewById(R.id.btnServiceAccounts);

        toolbar = getSupportActionBar();
//        toolbar.setTitle("Home");

        // start background processes
        services.add(new Service(1, "Service 1"));
        services.add(new Service(2, "Service 2"));
        for (Service service: services) {
            service.startCounters();
        }

       btnServiceLoans.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Customer customer = new Customer("grace");
               customer.sendTicketRequest(customer, 1); //assume customer had clicked on a service with service id 1
               Toast.makeText(CustomerActivity.this, "new ticket requested!", Toast.LENGTH_SHORT).show();
           }
       });

//        Customer customer = new Customer("grace");
//        Customer customer2 = new Customer("anna");
//        customer.sendTicketRequest(customer, 1); //assume customer had clicked on a service with service id 1
//        customer2.sendTicketRequest(customer2, 1);


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

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.customer_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}