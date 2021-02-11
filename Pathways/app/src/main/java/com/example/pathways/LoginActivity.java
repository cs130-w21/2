package com.example.pathways;
//package com.google.firebase.quickstart.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity{

    EditText _emailText;
    EditText _passwordText;
    Button _submitButton;
    Button _registerButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _emailText = (EditText) findViewById(R.id.email);
        _passwordText = (EditText) findViewById(R.id.password);
        _submitButton = findViewById(R.id.submit_button);
        _registerButton = findViewById(R.id.register_button);
        _submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = _emailText.getText().toString();
                String password = _passwordText.getText().toString();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("EMAIL", email);
                startActivity(intent);
            }
        });
        _registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = _emailText.getText().toString();
                String password = _passwordText.getText().toString();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("EMAIL", email);
                startActivity(intent);
            }
        });
    }


}
