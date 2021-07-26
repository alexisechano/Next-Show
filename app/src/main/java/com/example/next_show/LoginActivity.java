package com.example.next_show;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.next_show.models.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.File;

public class LoginActivity extends AppCompatActivity {
    // view element variables
    private EditText etUsername;
    private EditText etPassword;
    private ImageView ivLogo;
    private Button btnLogin;
    private Button btnSignup;

    // constants
    private static final String TAG = "LoginActivity";
    private static final String LOGO_URL = "https://i.imgur.com/yBGsdF4.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // go to main activity if already signed in
        if(User.getCurrentUser() != null){
            openMainActivity();
        }

        // set views and match to ID + on click listener
        setViewElems();
    }

    private void setViewElems() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        ivLogo = findViewById(R.id.ivLogo);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        // add logo image
        Glide.with(this).load(LOGO_URL).centerInside().into(ivLogo);

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
                    Toast.makeText(LoginActivity.this, "Invalid username/password", Toast.LENGTH_SHORT).show();
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