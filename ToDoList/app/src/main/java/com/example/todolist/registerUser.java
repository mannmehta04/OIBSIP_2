package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class registerUser extends AppCompatActivity {

    Button register, login;
    EditText fname, lname, email, pass, confPass;

    String fnameStr, lnameStr, emailStr, passStr, confPassStr;
    Intent loginPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        loginPage = new Intent(registerUser.this, MainActivity.class);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        confPass = findViewById(R.id.confirmPass);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(loginPage);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fnameStr = fname.getText().toString();
                lnameStr = lname.getText().toString();
                emailStr = email.getText().toString();
                passStr = pass.getText().toString();
                confPassStr = confPass.getText().toString();
                int maxLength = 8;

                if (fnameStr.isEmpty() && lnameStr.isEmpty()){
                    Toast.makeText(registerUser.this, "Please Enter Valid Name.", Toast.LENGTH_LONG).show();
                }
                else if (!emailStr.contains("@") && !emailStr.contains(".")){
                    Toast.makeText(registerUser.this, "Please Enter Valid Email.", Toast.LENGTH_LONG).show();
                }
                else if (passStr.length()!=maxLength){
                    Toast.makeText(registerUser.this, "Please Enter at least 8 characters for Password.", Toast.LENGTH_LONG).show();
                }
                else if (fnameStr!="" && lnameStr!="" && emailStr.contains("@") && emailStr.contains(".") && passStr.length()==maxLength){
                    if(passStr.equals(confPassStr)){
                        registerUser();
                    }
                    else {
                        Toast.makeText(registerUser.this, "Confirmation password does not match", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void registerUser(){
        dbInfo db = new dbInfo(this);
        boolean dataInserted = false;

        try {
            if (!dataInserted){
                db.addData(fnameStr, lnameStr, emailStr, passStr);
                dataInserted = true;
            }
        }
        catch (Exception e){
            Toast.makeText(this, "Error while completing Registration.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        if (dataInserted){
            Toast.makeText(this, "Congratulations, "+ fnameStr + "! You are now registered. Login to Start making Lists.", Toast.LENGTH_LONG).show();

            startActivity(loginPage);
            finish();
        }
    }
}