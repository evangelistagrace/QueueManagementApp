package com.example.queueManagementSystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminCustomersActivity extends AppCompatActivity {
    private ActionBar toolbar;
    DatabaseHelper db;
    Admin admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_customers_activity);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        // init components
        db = new DatabaseHelper(this);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_services_navigation);
        Intent currentIntent = getIntent();
        admin = (Admin) currentIntent.getSerializableExtra("adminObject");

        // display services in table
        Cursor cursor = db.getUsers();
        TableLayout tl = (TableLayout) findViewById(R.id.adminServicesTableLayout);
        Typeface font = AdminServicesActivity.FontCache.get("fonts/kollektif.ttf", this);
        int counter = 1;

        if (cursor.moveToFirst()) {
            do {
                String customerName = cursor.getString(1);
                int customerRequests = cursor.getInt(2);

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
                col2.setText(customerName);
                col2.setTextColor(getResources().getColor(R.color.magenta3));
                col2.setTextSize(18);

                // create column 3
                TextView col3 = new TextView(this);

                col3.setTypeface(font);
                col3.setPadding(30, 10, 10, 10);
                col3.setText(String.valueOf(customerRequests));
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
}