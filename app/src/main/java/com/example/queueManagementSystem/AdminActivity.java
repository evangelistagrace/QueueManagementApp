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
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        // init components
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Button btnServices = findViewById(R.id.btnServices);
        Button btnQueues = findViewById(R.id.btnQueues);
        Button btnCustomers = findViewById(R.id.btnCustomers);
        Intent currentIntent = getIntent();
        int id = currentIntent.getIntExtra("ID", -1);
        String username = currentIntent.getStringExtra("USERNAME");
        String password = currentIntent.getStringExtra("PASSWORD");
        Admin admin = new Admin(id, username, password);


        // set actions for admin home menu
        // services
        btnServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adminServicesIntent = new Intent(AdminActivity.this, AdminServicesActivity.class);
                adminServicesIntent.putExtra("adminObject", admin);
                startActivity(adminServicesIntent);
                return;
            }
        });

        // queues
        btnQueues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIntent.putExtra("adminObject", admin);
                Fragment fragment = new AdminQueuesFragment();
                loadFragment(fragment);
            }
        });

        // customers
        btnCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminActivity.this, "CUSTOMERS", Toast.LENGTH_SHORT).show();
                Intent adminServicesIntent = new Intent(AdminActivity.this, AdminCustomersActivity.class);
                adminServicesIntent.putExtra("adminObject", admin);
                startActivity(adminServicesIntent);
                return;
            }
        });

        // set actions for bottom menu
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return true;
                    case R.id.action_tickets:
                        currentIntent.putExtra("adminObject", admin);
                        fragment = new AdminInsightFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.action_profile:
                        currentIntent.putExtra("adminObject", admin);
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