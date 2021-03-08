package com.example.pathways;


import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    EditText _emailText;
    EditText _passwordText;
    Button _submitButton;
    Button _registerButton;

    private ProgressBar _progressBar;
    private FirebaseAuth auth;

    /**
     * If user has already signed in, navigate directly to initial page without the user needing to log back in
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("EMAIL", auth.getCurrentUser().getEmail());
            startActivity(intent);
            ActivityCompat.finishAffinity(LoginActivity.this);
        }
    }

    /**
     * Initializes UI components of log in page
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _emailText = (EditText) findViewById(R.id.email);
        _passwordText = (EditText) findViewById(R.id.password);
        _submitButton = findViewById(R.id.submit_button);
        _registerButton = findViewById(R.id.register_button);

        _progressBar = new ProgressBar(this);
        auth = FirebaseAuth.getInstance();

        getSupportActionBar().hide();


        _submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = _emailText.getText().toString();
                String password = _passwordText.getText().toString();
                login(email, password);
            }
        });
        _registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = _emailText.getText().toString();
                String password = _passwordText.getText().toString();
                register(email, password);
            }
        });

    }

    /**
     * When user enters email and password, create firebase user
     * @param email - User's email
     * @param password - User's password (>6 characters)
     */
    public void register(String email, String password) {
        if (!validate()) {
            return;
        }
        _progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Registration successful. You may now login.",
                                    Toast.LENGTH_LONG).show();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure" + task.getException().getMessage());
                            Toast.makeText(LoginActivity.this, "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }
                        _progressBar.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * Check if user exists with given email and password and then log in to app if so
     * @param email - User's email
     * @param password - User's password (>6 characters)
     */
    public void login(String email, String password) {
        if (!validate()) {
            return;
        }
        _progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("EMAIL", email);
                            startActivity(intent);
                            ActivityCompat.finishAffinity(LoginActivity.this);
                        } else {
                            if (task.getException() != null) {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage() + " " + "Please try again.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to login." + " " + "Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        _progressBar.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * Make sure email and password fields are populated
     * @return True if email and password fields are both populated, False otherwise
     */
    public boolean validate() {
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        //validate email
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Required", Toast.LENGTH_SHORT).show();
            return false;
        }
        //validate password
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
//https://firebase.google.com/docs/auth/android/password-auth#create_a_password-based_account resource
