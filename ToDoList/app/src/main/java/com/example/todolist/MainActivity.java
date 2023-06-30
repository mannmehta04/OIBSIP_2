package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button login, register;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent registerPage = new Intent(MainActivity.this, registerUser.class);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(registerPage);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login(){
        String emailStr = email.getText().toString();
        String passStr = password.getText().toString();

        dbInfo db = new dbInfo(this);
        Context appContext = getApplicationContext();

        if(emailStr.isEmpty() || passStr.isEmpty()){
            Toast.makeText(this, "Please fill out all the details!", Toast.LENGTH_SHORT).show();
        } else{
            db.searchUser(emailStr, passStr, appContext);
        }
    }
}