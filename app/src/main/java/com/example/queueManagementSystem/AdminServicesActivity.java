package com.example.queueManagementSystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminServicesActivity extends AppCompatActivity {
    private ActionBar toolbar;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_services_activity);

        // init components
        db = new DatabaseHelper(this);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_services_navigation);
        TextView tvService1 = findViewById(R.id.tvService1);
        TextView tvService2 = findViewById(R.id.tvService2);
        TextView tvAddService = findViewById(R.id.tvAddService);
        TextView tvDisplayServices = findViewById(R.id.tvDisplayServices);

        toolbar = getSupportActionBar();
        toolbar.setTitle("Services");

        // set actions for admin service menu

        // service 1
        tvService1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment;
                toolbar.setTitle("Service 1");
                fragment = new AdminServiceSettingsFragment();
                loadFragment(fragment);
                return;
            }
        });

        // service 2
        tvService2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment;
                toolbar.setTitle("Service 2");
                fragment = new AdminProfileFragment();
                loadFragment(fragment);
                return;
            }
        });

        tvAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment;
                toolbar.setTitle("Add Service");
                fragment = new AdminAddServiceFragment();
                loadFragment(fragment);
                return;
            }
        });

        // test display services
        String serviceNames = "";
        Cursor cursor = db.getServices();

        if (cursor.moveToFirst()) {
            do {
                serviceNames += cursor.getString(0) + "\n";

            } while (cursor.moveToNext());
        }

        tvDisplayServices.setText(serviceNames);



        // set actions for bottom menu
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        finish();
                        return true;
                    case R.id.action_tickets:
                        toolbar.setTitle("Insight");
                        fragment = new AdminInsightFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.action_profile:
                        toolbar.setTitle("Profile");
                        fragment = new AdminProfileFragment();
                        loadFragment(fragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.admin_services_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.setReorderingAllowed(true);
        transaction.commit();
    }
}