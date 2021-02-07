package com.example.queueManagementSystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        BottomNavigationView customerBottomNavigationView = findViewById(R.id.CustomerBottomNavigationView);

//        NavController navController = Navigation.findNavController(this, R.id.fragment);
//        NavigationUI.setupWithNavController(customerBottomNavigationView, navController);

    }
}