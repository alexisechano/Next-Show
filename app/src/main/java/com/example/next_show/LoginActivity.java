package com.example.next_show;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.next_show.models.User;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.parceler.Parcel;
import org.parceler.Parcels;

public class LoginActivity extends AppCompatActivity {
    // view element variables
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;

    // constants
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // go to main activity if already signed in
        if(ParseUser.getCurrentUser() != null){
            openMainActivity();
        }

        // set views and match to ID + on click listener
        setViewElems();
    }

    private void setViewElems() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        // set new on click for button
        btnSignup.setBackgroundColor(Color.parseColor("#d6d3d2"));
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();

                // go to signup
                openSignupActivity(username);
            }
        });

        // set new on click listener for button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Clicked");

                // get the text fields from the login layout
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });
    }

    private void loginUser(String username, String password) {
        // log in user given credentials
        Log.i(TAG, "Attempting to login user: " + username);

        // user Parse to log in
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Unable to login with Parse", e);
                    Toast.makeText(LoginActivity.this, "Error with username/password", Toast.LENGTH_SHORT).show();
                    return;
                }
                // navigate to main activity if success
                Log.i(TAG, "Success! Going to main activity");
                Toast.makeText(LoginActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
                openMainActivity();
            }
        });
    }

    private void openMainActivity() {
        // launch new intent
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void openSignupActivity(String username) {
        // launch new intent
        Intent i = new Intent(this, SignupActivity.class);

        // serialize the post using parceler, use its short name as a key
        i.putExtra("username", username);
        startActivity(i);
    }
}