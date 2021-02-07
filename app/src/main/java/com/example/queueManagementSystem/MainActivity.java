package com.example.queueManagementSystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fragment fragment;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = getSupportActionBar();
        toolbar.setTitle("Home");

        fragment = new CustomerHome();
        loadFragment(fragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        toolbar.setTitle("Home");
                        fragment = new CustomerHome();
                        loadFragment(fragment);
                        return true;
                    case R.id.action_tickets:
                        toolbar.setTitle("Tickets");
                        fragment = new CustomerTicket();
                        loadFragment(fragment);
//                        Toast.makeText(MainActivity.this, "Tickets", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.action_profile:
                        toolbar.setTitle("Profile");
                        fragment = new CustomerProfile();
                        loadFragment(fragment);
//                        Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}