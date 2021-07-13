package com.example.next_show;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.next_show.data.TraktClient;
import com.example.next_show.fragments.FeedFragment;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.ArrayList;
import java.util.List;

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

        // init the show list
        shows = new ArrayList<>();

        // Fragment management
        FeedFragment feedFragment = FeedFragment.newInstance();
        setFeedFragment(feedFragment);

        // set bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        setFeedFragment(feedFragment);
                        break;
                    case R.id.action_rating:
                        // set rating fragment
                        break;
                    case R.id.action_profile:
                        // set profile fragment
                        break;
                }
                return true;
            }
        });

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

        // method to call to API and gret trending shows
        //grabTraktData();
    }

    private void setFeedFragment(FeedFragment feedFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container_view, feedFragment);
        ft.commit();
    }

    private void grabTraktData() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    // Trakt wrapper
                    TraktV2 trakt = new TraktV2(getString(R.string.trakt_client_id));
                    Shows traktShows = trakt.shows();
                    try {
                        // Get trending shows
                        Response<List<TrendingShow>> response = traktShows.trending(1, null, Extended.FULL).execute();
                        if (response.isSuccessful()) {
                            List<TrendingShow> repsonseShows = response.body();
                            for (TrendingShow trending : repsonseShows) {
                                Log.i(TAG, "Title: " + trending.show.title);
                                Show currentShow = new Show(trending.show.title);
                                shows.add(currentShow);
                            }

                            // show the list
                            Log.i(TAG, "List of show titles: " + shows.toString());
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