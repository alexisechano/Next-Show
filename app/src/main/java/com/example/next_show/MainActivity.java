package com.example.next_show;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.next_show.data.TraktClient;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.parse.ParseUser;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.TrendingShow;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.services.Shows;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Headers;
import okhttp3.Request;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    // view temp
    private Button btnLogout;

    // constants
    public static final String TAG = "MainActivity";
    List<Show> shows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // temporary logout button
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Log.i(TAG, "Logged out user");
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    // try Trakt wrapper
                    TraktV2 trakt = new TraktV2("4377c98e70a9ac3be06ecde51d34c21bf6e6944964ba1f305d0d2eab5a29b1e5");
                    Shows traktShows = trakt.shows();
                    try {
                        // Get trending shows
                        Response<List<TrendingShow>> response = traktShows.trending(1, null, Extended.FULL).execute();
                        if (response.isSuccessful()) {
                            List<TrendingShow> shows = response.body();
                            for (TrendingShow trending : shows) {
                                System.out.println("Title: " + trending.show.title);
                            }
                        } else {
                            if (response.code() == 401) {
                                // authorization required, supply a valid OAuth access token
                                Log.e(TAG, "Access token required");
                            } else {
                                // the request failed for some other reason
                                Log.e(TAG, "Look at stack trace, failed" + response.code());
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Did not get to response, failed", e);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}