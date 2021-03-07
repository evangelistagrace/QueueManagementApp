package com.example.queueManagementSystem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
    private static final String CHANNEL_ID = "QMS";
    private ActionBar toolbar;
    protected static ArrayList<Service> services = new ArrayList<>();
    DatabaseHelper db;
    Customer customer;
    Intent currentIntent;
    String username, password;
    int id;
    ConstraintLayout constraintLayout;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_activity);

        // add code after this line

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        //init components
        // instantiate objects
        db = new DatabaseHelper(this);
        currentIntent = getIntent();

        //instantiate customer
        username = currentIntent.getStringExtra("USERNAME");
        password = currentIntent.getStringExtra("PASSWORD");
        id = currentIntent.getIntExtra("ID", -1);

        customer = new Customer(id, username, password);

        createNotificationChannel();

        // instantiate services
        Cursor cursor = db.getServices();

        if (cursor.moveToFirst()) {
            do {
                services.add(new Service(cursor.getInt(0), cursor.getString(1), cursor.getInt(2)));
            } while (cursor.moveToNext());
        }

        // init layout
        currentIntent.putExtra("customerObject", customer);
        Fragment homeFragment = new CustomerHomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.customer_fragment_container, homeFragment);
        transaction.commit();

        // BOTTOM NAVIGATION
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
//                        toolbar.setTitle("Home");
//                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        Fragment homeFragment = new CustomerHomeFragment();
                        loadFragment(homeFragment);
                        return true;
                    case R.id.action_tickets:
//                        toolbar.setTitle("Tickets");
                        // load customer object into intent before loading fragment
                        currentIntent.putExtra("customerObject", customer);
                        fragment = new CustomerTicketsFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.action_profile:
//                        toolbar.setTitle("Profile");
                        currentIntent.putExtra("customerObject", customer);
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
        transaction.add(R.id.customer_fragment_container, fragment, Integer.toString(getFragmentCount()));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected int getFragmentCount() {
        return getSupportFragmentManager().getBackStackEntryCount();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}