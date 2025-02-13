package com.example.queueManagementSystem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText et_cust_username, et_cust_password;
    Button btn_cust_login;
    TextView tv_cust_register;
    DatabaseHelper db;
    int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        db = new DatabaseHelper(this);
        et_cust_username = findViewById(R.id.et_cust_username);
        et_cust_password = findViewById(R.id.et_cust_password);
        btn_cust_login = findViewById(R.id.btn_cust_login);
        tv_cust_register = findViewById(R.id.tv_cust_register);

        // create admin if there isn't one since login page is the first to initiate
        int adminId = db.checkUser("admin", "123");

        if (adminId < 0) { // no admin record found, so add admin
            db.addAdmin("admin@mail.com", "admin", "123");
        }

        tv_cust_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(LoginActivity.this, "Login btn clicked!", Toast.LENGTH_SHORT).show();
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        btn_cust_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_cust_username.getText().toString().trim();
                String password = et_cust_password.getText().toString().trim();

                int adminId = db.checkAdmin(username, password);
                int userId = db.checkUser(username, password);

                if (adminId > -1) {
                    Intent adminIntent = new Intent(LoginActivity.this, AdminActivity.class);
                    adminIntent.putExtra("USERNAME", username);
                    adminIntent.putExtra("PASSWORD", password);
                    adminIntent.putExtra("ID", userId);
                    startActivity(adminIntent);
                    finish();
                    return;
                }

                if (userId > -1) {
                    Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                    Intent customerIntent = new Intent(LoginActivity.this, CustomerActivity.class);
                    customerIntent.putExtra("USERNAME", username);
                    customerIntent.putExtra("PASSWORD", password);
                    customerIntent.putExtra("ID", userId);
                    startActivity(customerIntent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}