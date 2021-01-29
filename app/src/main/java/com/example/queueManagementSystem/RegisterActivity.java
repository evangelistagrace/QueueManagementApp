package com.example.queueManagementSystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    EditText et_cust_username, et_cust_email, et_cust_password;
    Button btn_cust_register;
    TextView tv_cust_login;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);
        et_cust_username = findViewById(R.id.et_cust_username);
        et_cust_email = findViewById(R.id.et_cust_email);
        et_cust_password = findViewById(R.id.et_cust_password);
        btn_cust_register = findViewById(R.id.btn_cust_register);
        tv_cust_login = findViewById(R.id.tv_cust_login);

        tv_cust_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(LoginActivity.this, "Login btn clicked!", Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        btn_cust_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_cust_email.getText().toString().trim();
                String username = et_cust_username.getText().toString().trim();
                String password = et_cust_password.getText().toString().trim();

                Boolean res = db.checkUser(username, password);

                // check if account already exist
                if (res == false) {
                    long val = db.addUser(email, username, password);
                    if (val > 0) {
                        Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                        Intent moveToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(moveToLogin);
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Account already exists, please login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}