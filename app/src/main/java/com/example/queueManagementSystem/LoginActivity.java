package com.example.queueManagementSystem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        db = new DatabaseHelper(this);
        et_cust_username = findViewById(R.id.et_cust_username);
        et_cust_password = findViewById(R.id.et_cust_password);
        btn_cust_login = findViewById(R.id.btn_cust_login);
        tv_cust_register = findViewById(R.id.tv_cust_register);

        // create admin if there isn't one since login page is the first to initiate
        Boolean res = db.checkUser("admin", "123");

        if (res == false) {
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

                Boolean isAdmin = db.checkAdmin(username, password);
                Boolean res = db.checkUser(username, password);

                if (isAdmin) {
                    Toast.makeText(LoginActivity.this, "IS ADMIN", Toast.LENGTH_SHORT).show();
                    Intent adminIntent = new Intent(LoginActivity.this, AdminActivity.class);
                    startActivity(adminIntent);
                    return;
                }

                if (res == true) {
                    Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                    Intent customerIntent = new Intent(LoginActivity.this, CustomerActivity.class);
                    startActivity(customerIntent);
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}