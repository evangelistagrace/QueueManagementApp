package com.example.queueManagementSystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {
    private ActionBar toolbar;
    private BroadcastReceiver minuteUpdateReceiver;
    ArrayList<Integer>
            arrServingTime0, arrServingTime1, arrServingTime2,
            arrServingTime3, arrServingTime4, arrServingTime5,
            arrServingTime6, arrServingTime7, arrServingTime8;
    static ArrayList<Double> avgServingTimeArr;

    ArrayList<Integer> currArr, prevArr = null;

    int timerCounter = 0;

    DatabaseHelper db;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1500;

    int servingTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        // init components
        db = new DatabaseHelper(AdminActivity.this);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Button btnServices = findViewById(R.id.btnServices);
        Button btnQueues = findViewById(R.id.btnQueues);
        Button btnCustomers = findViewById(R.id.btnCustomers);
        Intent currentIntent = getIntent();
        int id = currentIntent.getIntExtra("ID", -1);
        String username = currentIntent.getStringExtra("USERNAME");
        String password = currentIntent.getStringExtra("PASSWORD");
        Admin admin = new Admin(id, username, password);


        //init serving time arrays
        arrServingTime0 = new ArrayList<>();
        arrServingTime1 = new ArrayList<>();
        arrServingTime2 = new ArrayList<>();
        arrServingTime3 = new ArrayList<>();
        arrServingTime4 = new ArrayList<>();
        arrServingTime5 = new ArrayList<>();
        arrServingTime6 = new ArrayList<>();
        arrServingTime7 = new ArrayList<>();
        arrServingTime8 = new ArrayList<>();
        avgServingTimeArr = new ArrayList<>();

        // start minute timer
        startMinuteUpdater();

//        // save current ticket serving time into array
//        // run this every second
//        handler.postDelayed(runnable = new Runnable() {
//            public void run() {
//                handler.postDelayed(runnable, delay);
//                Cursor cursor = db.getServingTime();
//
//                if (cursor.moveToFirst()) {
//                    servingTime = cursor.getInt(0);
//                }
//                cursor.close();
//                Toast.makeText(AdminActivity.this, "current ticket serving time: " + servingTime, Toast.LENGTH_SHORT).show();
//                currArr.add(servingTime);
//            }
//        }, delay);


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

    public static ArrayList<Double> getAvgServingTimeArr() {
        return avgServingTimeArr;
    }


    public void startMinuteUpdater() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        minuteUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Double avgServingTime = 0.0;

//                Toast.makeText(AdminActivity.this, "ran after a minute", Toast.LENGTH_SHORT).show();
                System.out.println("ran after a minute");
                // check for an existing previous serving time array
                    // if exist, calculate avg serving time in this array and save into global avg serving time array
                // create one serving time array every minute
                // keep saving current ticket serving time into the newly created array

                switch (timerCounter) {
                    case 0:
                        currArr = arrServingTime0;
                        prevArr = null;
                        break;
                    case 1:
                        currArr = arrServingTime1;
                        prevArr = arrServingTime0;
                        break;
                    case 2:
                        currArr = arrServingTime2;
                        prevArr = arrServingTime1;
                        break;
                    case 3:
                        currArr = arrServingTime3;
                        prevArr = arrServingTime2;
                        break;
                    case 4:
                        currArr = arrServingTime4;
                        prevArr = arrServingTime3;
                        break;
                    case 8:
                        currArr = arrServingTime8;
                        prevArr = arrServingTime7;
                        break;
                    default:
                        break;
                }

                // check for existing prev serving time arr
                if (prevArr != null && prevArr.size() > 0) {
                    if (avgServingTimeArr.size() < 10) {

                        Double totalServingTime = 0.0d;
                        // calculate avg serving time and insert into avgServingTime array
                        for (int j=0; j< prevArr.size(); j++) {
                            totalServingTime += prevArr.get(j);
                        }

                        avgServingTime = (Double) (totalServingTime/prevArr.size());

                        avgServingTimeArr.add(avgServingTime);
                        System.out.println("avg serving time arr: " + avgServingTimeArr);
                    } else { // arrays have reached full capacity, reset
//                        System.out.println("avg serving time arr: " + avgServingTimeArr);
                        //reset serving time and avgservingtime arrays
                        arrServingTime0 = new ArrayList<>();
                        arrServingTime1 = new ArrayList<>();
                        arrServingTime2 = new ArrayList<>();
                        arrServingTime3 = new ArrayList<>();
                        arrServingTime4 = new ArrayList<>();
                        arrServingTime5 = new ArrayList<>();
                        arrServingTime6 = new ArrayList<>();
                        arrServingTime7 = new ArrayList<>();
                        arrServingTime8 = new ArrayList<>();
                        avgServingTimeArr = new ArrayList<>();
                    }
                }

                // save current ticket serving time into array
                // run this every second
                handler.postDelayed(runnable = new Runnable() {
                    public void run() {
                        handler.postDelayed(runnable, delay);
                        Cursor cursor = db.getServingTime();

                        if (cursor.moveToFirst()) {
                            servingTime = cursor.getInt(0);
                        }
                        cursor.close();
                        Toast.makeText(AdminActivity.this, "current ticket serving time: " + servingTime, Toast.LENGTH_SHORT).show();
                        currArr.add(servingTime);
                    }
                }, delay);

                timerCounter++;
            }
        };
        registerReceiver(minuteUpdateReceiver, intentFilter);
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