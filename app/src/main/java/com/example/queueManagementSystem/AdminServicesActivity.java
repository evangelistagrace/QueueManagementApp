package com.example.queueManagementSystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Hashtable;

public class AdminServicesActivity extends AppCompatActivity {
    private ActionBar toolbar;
    DatabaseHelper db;
    Admin admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_services_activity);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        // init components
        db = new DatabaseHelper(this);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_services_navigation);
        TextView tvAddService = findViewById(R.id.tvAddService);
        Intent currentIntent = getIntent();
        admin = (Admin) currentIntent.getSerializableExtra("adminObject");

        toolbar = getSupportActionBar();
        toolbar.setTitle("Services");

        // set actions for admin service menu

        // service 1
//        tvService1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment fragment;
//                toolbar.setTitle("Service 1");
//                fragment = new AdminServiceSettingsFragment();
//                loadFragment(fragment);
//                return;
//            }
//        });
//
//        // service 2
//        tvService2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment fragment;
//                toolbar.setTitle("Service 2");
//                fragment = new AdminProfileFragment();
//                loadFragment(fragment);
//                return;
//            }
//        });

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

        // display services in table
        Cursor cursor = db.getServices();
        TableLayout tl = (TableLayout) findViewById(R.id.adminServicesTableLayout);
        Typeface font = FontCache.get("fonts/kollektif.ttf", this);
        int counter = 1;

        if (cursor.moveToFirst()) {
            do {
                String serviceName = cursor.getString(1);

                // Create a new row to be added.
                TableRow tr = new TableRow(this);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                if (counter % 2 == 0) { //odd row
                    tr.setBackgroundColor(getResources().getColor(R.color.semi_white));
                } else { //even row
                    tr.setBackgroundColor(getResources().getColor(R.color.beige_200));
                }

                // create column 1
                TextView col1 = new TextView(this);

                col1.setTypeface(font);
                col1.setPadding(30, 10, 10, 10);
                col1.setText(String.valueOf(counter));
                col1.setTextColor(getResources().getColor(R.color.dark_text_2));
                col1.setTextSize(18);

                // create column 2
                TextView col2 = new TextView(this);

                col2.setTypeface(font);
                col2.setPadding(30, 10, 10, 10);
                col2.setText(serviceName);
                // todo: make service name clickable
                //android:clickable="true"
                //android:focusable="true"
                //set onclick listener to open AdminServiceSettingsFragment with clicked service
                col2.setTextColor(getResources().getColor(R.color.magenta3));
                col2.setTextSize(18);

                // create column 3
                TextView col3 = new TextView(this);

                col3.setTypeface(font);
                col3.setPadding(30, 10, 10, 10);
                col3.setText("Running");
                col3.setTextColor(getResources().getColor(R.color.dark_text_2));
                col3.setTextSize(18);

                // add columns to table row
                tr.addView(col1);
                tr.addView(col2);
                tr.addView(col3);

                // add table row to table layout
                tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                counter++;

            } while (cursor.moveToNext());
        }

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
        transaction.replace(R.id.admin_services_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.setReorderingAllowed(true);
        transaction.commit();
    }

    public static class FontCache {

        private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

        public static Typeface get(String name, Context context) {
            Typeface tf = fontCache.get(name);
            if(tf == null) {
                try {
                    tf = Typeface.createFromAsset(context.getAssets(), name);
                }
                catch (Exception e) {
                    return null;
                }
                fontCache.put(name, tf);
            }
            return tf;
        }
    }
}