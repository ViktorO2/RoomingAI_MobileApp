package com.example.rooming.ui;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rooming.Activities.LoginActivity;
import com.example.rooming.Activities.WelcomeActivity;
import com.example.rooming.Data.DbHelper;
import com.example.rooming.MainActivity;
import com.example.rooming.R;
import com.example.rooming.Session.SessionManager;

import java.util.regex.Pattern;

public class MyAccount extends AppCompatActivity {
    SessionManager sessionManager;
    DbHelper dbHelper;

    TextView currentUsername;
    TextView currentEmail;
    TextView currentPassword;

    EditText editUsername;
    EditText editEmail;
    EditText editPassword;

    Button editBtn;
    Button deleteBtn;
    String regexPattern;
    int current_id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_accont);

        sessionManager = new SessionManager(getApplicationContext());
        current_id_user = sessionManager.getSession();
        dbHelper = new DbHelper(getApplicationContext(), null, null, 1);


        currentUsername = findViewById(R.id.currentName);
        currentEmail = findViewById(R.id.currentEmail);
        currentPassword = findViewById(R.id.currentPassword);
        deleteBtn = findViewById(R.id.deleteButton);
        editBtn = findViewById(R.id.editButton);
        editUsername = findViewById(R.id.editUsernameET);
        editEmail = findViewById(R.id.editEmailET);
        editPassword = findViewById(R.id.editPasswordET);
        regexPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

        String[] usernameArr = dbHelper.getUsername(String.valueOf(current_id_user));
        String[] emailArr = dbHelper.getEmail(String.valueOf(current_id_user));
        String[] passwordArr = dbHelper.getPassword(String.valueOf(current_id_user));

        String username = getUsernameAsString(usernameArr);
        String email = getEmailAsString(emailArr);
        String password = getPasswordAsString(passwordArr);


        //Show current info
        currentUsername.setText(getString(R.string.current_username) + username);
        currentEmail.setText(getString(R.string.current_email) + email);
        currentPassword.setText(getString(R.string.current_password) + password);


        //Toolbar initialization
        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        try {

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper.deleteUser(current_id_user);
                    Toast.makeText(MyAccount.this, "Account removal successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MyAccount.this, WelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String new_username = String.valueOf(editUsername.getText());
                    String new_email = editEmail.getText().toString();
                    String new_password = editPassword.getText().toString();
                    Boolean checkEmailPattern = false;

                    if (new_password.equals("") && new_username.equals("") && new_email.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (new_username.equals("")) {
                            new_username = username;
                        }
                        if (new_email.equals("")) {
                            new_email = email;
                        } else {
                            checkEmailPattern = (patternMatches(new_email, regexPattern));
                        }
                        if (new_password.equals("")) {
                            new_password = password;
                        }

                        if (checkEmailPattern) {
                            dbHelper.editUser(current_id_user, new_username, new_email, new_password);
                            Toast.makeText(MyAccount.this, "Successful edit!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MyAccount.this, LoginActivity.class));
                        } else {
                            Toast.makeText(MyAccount.this, "Input error!\\nPlease try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_back) {
            Intent intent = new Intent(MyAccount.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_logout) {
            logoutUser();
            Intent intent = new Intent(MyAccount.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(MyAccount.this, "USERS LOGOUT", Toast.LENGTH_SHORT).show();
            return true;
        }
        // Handle other menu items as needed

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        MenuItem navBack = menu.findItem(R.id.nav_back);
        MenuItem navAccount = menu.findItem(R.id.nav_account);
        navBack.setVisible(true);
        navAccount.setVisible(false);
        return true;
    }

    private void logoutUser() {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.removeSession();
        Intent intent = new Intent(MyAccount.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public static Boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    private String getUsernameAsString(String[] usernameArray) {
        StringBuilder usernameStringBuilder = new StringBuilder();
        for (String part : usernameArray) {
            usernameStringBuilder.append(part).append(" ");
        }
        return usernameStringBuilder.toString().trim();
    }

    private String getEmailAsString(String[] emailArray) {
        StringBuilder emailStringBuilder = new StringBuilder();
        for (String part : emailArray) {
            emailStringBuilder.append(part).append(" ");
        }
        return emailStringBuilder.toString().trim();
    }

    private String getPasswordAsString(String[] passwordArray) {
        StringBuilder passwordStringBuilder = new StringBuilder();
        for (String part : passwordArray) {
            passwordStringBuilder.append(part).append(" ");
        }
        return passwordStringBuilder.toString().trim();
    }
}