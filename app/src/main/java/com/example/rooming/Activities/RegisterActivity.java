package com.example.rooming.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rooming.Data.DbHelper;
import com.example.rooming.Entity.Users;
import com.example.rooming.R;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText usernameET;
    EditText emailET;
    EditText passwordET;
    EditText rePasswordET;

    DbHelper dbHelper;
    Button registerBtnET;
    TextView loginBtET;
    String regexPattern;

    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameET = findViewById(R.id.username);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        rePasswordET = findViewById(R.id.repassword);
        dbHelper = new DbHelper(this, null, null, 1);
        registerBtnET = findViewById(R.id.registerbtn);
        loginBtET=findViewById(R.id.loginBt);
        regexPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";


        loginBtET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        registerBtnET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = usernameET.getText().toString();
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                String rePassword = rePasswordET.getText().toString();
                Boolean emailValidation = patternMatches(email, regexPattern);
                Users user = new Users(username, email, password);


                if (username.equals("") || email.equals("") || password.equals("") || rePassword.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                } else {
                    if (!password.equals(rePassword)) {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    } else {
                        Boolean checkEmailExist = dbHelper.checkEmailRegistration(user);
                        if (checkEmailExist == false) {
                            if (emailValidation == false) {
                                Toast.makeText(RegisterActivity.this, "Invalid Email or already use! \n Please try again!", Toast.LENGTH_SHORT).show();
                            } else {
                                Boolean insert = dbHelper.Registration(user);
                                if (insert == true) {
                                    Toast.makeText(RegisterActivity.this, "Successful registration!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                                } else {
                                    Toast.makeText(RegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
            }
        });


    }
}
