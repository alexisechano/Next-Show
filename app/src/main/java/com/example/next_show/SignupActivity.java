package com.example.next_show;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.next_show.models.User;
import com.parse.ParseException;
import com.parse.SignUpCallback;

import java.util.Arrays;
import java.util.List;

public class SignupActivity extends AppCompatActivity {
    // view elements
    private EditText etUsername;
    private EditText etPassword;
    private EditText etEmail;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etBio;
    private EditText etGenres;
    private Button btnSignup;

    // constants
    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        String username = getIntent().getStringExtra("username");

        // username and password persist if not empty
        etUsername = findViewById(R.id.etUsername);

        if (username != null) {
            etUsername.setText(username);
        }

        // set views and match to ID + create new user
        setUser(username);
    }

    private void setUser(String username) {
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etBio = findViewById(R.id.etBio);
        etGenres = findViewById(R.id.etGenres);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.isEmpty()) {
                    String username = etUsername.getText().toString();
                }

                String password = etPassword.getText().toString();
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String email = etEmail.getText().toString();
                String bio = etBio.getText().toString();

                // process the genre inputs
                String preGenres = etGenres.getText().toString().replaceAll("\\s+","");
                String[] arrayGenres = preGenres.split(",");
                List<String> genreList = Arrays.asList(arrayGenres);

                // Create the new User
                User user = new User();

                // set core properties
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.setName(firstName, lastName);
                user.setBio(bio);

                // add all of the genres to list
                user.setFaveGenres(genreList);

                // Invoke signUpInBackground
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            openMainActivity();
                        } else {
                            Log.e(TAG, "Unable to sign up with Parse", e);
                            Toast.makeText(SignupActivity.this, "Error with sign up", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        });

    }

    private void openMainActivity() {
        // launch new intent
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}