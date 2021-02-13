package com.example.queueManagementSystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        // init components
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Button btnServices = findViewById(R.id.btnServices);
        Button btnQueues = findViewById(R.id.btnQueues);
        Button btnCustomers = findViewById(R.id.btnCustomers);

        toolbar = getSupportActionBar();
        toolbar.setTitle("Home");

        // set actions for admin home menu
        // services
        btnServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminActivity.this, "SERVICES", Toast.LENGTH_SHORT).show();
                Intent adminServicesIntent = new Intent(AdminActivity.this, AdminServicesActivity.class);
                startActivity(adminServicesIntent);
                return;
            }
        });

        // queues
        btnQueues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminActivity.this, "QUEUES", Toast.LENGTH_SHORT).show();
            }
        });

        // customers
        btnCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminActivity.this, "CUSTOMERS", Toast.LENGTH_SHORT).show();
            }
        });

        // set actions for bottom menu
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        toolbar.setTitle("Home");
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
        transaction.replace(R.id.admin_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.setReorderingAllowed(true);
        transaction.commit();
    }
}