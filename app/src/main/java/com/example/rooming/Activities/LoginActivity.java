package com.example.rooming.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rooming.Data.DbHelper;
import com.example.rooming.Entity.Users;
import com.example.rooming.MainActivity;
import com.example.rooming.R;
import com.example.rooming.Session.SessionManager;

public class LoginActivity extends AppCompatActivity {

    EditText usernameET;
    EditText passwordET;
    DbHelper dbHelper;

    Button loginB;
    TextView registerBt;
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        usernameET = findViewById(R.id.usernameEditText);
        passwordET = findViewById(R.id.passwordEditText);
        loginB=findViewById(R.id.loginBtn);
        registerBt = findViewById(R.id.registerBt);
        dbHelper=new DbHelper(this,null,null,1);
        session = new SessionManager(getApplicationContext());


        registerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        try {
        //Login listener
        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String username = usernameET.getText().toString();
                String password = passwordET.getText().toString();

                Users user = new Users(username, password);
                String id = dbHelper.getUserId(username, password);


                if (username.equals("") || password.equals("")){
                    Toast.makeText(LoginActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                } else{
                    Boolean checkLoginParametersExist = dbHelper.checkLoginParametersExist(user);
                    if (checkLoginParametersExist) {
                        Toast.makeText(LoginActivity.this,"Successful Login!",Toast.LENGTH_SHORT).show();
                        session.saveSession(Integer.parseInt(id));
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                }


                }
        });
}catch (Exception e){
    e.printStackTrace();
    }

}
}

