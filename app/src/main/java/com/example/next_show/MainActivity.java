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
import com.example.next_show.fragments.ProfileFragment;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
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
        ProfileFragment profileFragment = ProfileFragment.newInstance();
        setProfileFragment(profileFragment);

        // set bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_profile:
                        // set profile fragment
                        setProfileFragment(profileFragment);
                        break;
                    case R.id.action_home:
                        FeedFragment feedFragment = FeedFragment.newInstance();
                        setFeedFragment(feedFragment);
                        break;
                    case R.id.action_rating:
                        // set rating fragment
                        break;
                }
                return true;
            }
        });


        // method to ASYNC call to API and grab trending shows
        grabTraktData();
    }

    // methods to toggle between fragments
    private void setFeedFragment(FeedFragment feedFragment) {
        FragmentTransaction feedTransact = getSupportFragmentManager().beginTransaction();
        feedTransact.replace(R.id.fragment_container_view, feedFragment);
        feedTransact.commit();
    }

    private void setProfileFragment(ProfileFragment profileFragment) {
        FragmentTransaction profileTransact = getSupportFragmentManager().beginTransaction();
        profileTransact.replace(R.id.fragment_container_view, profileFragment);
        profileTransact.commit();
    }

    private void grabTraktData() {
        // Trakt wrapper
        TraktV2 trakt = new TraktV2(getString(R.string.trakt_client_id));
        Shows traktShows = trakt.shows();

        try {
            traktShows.trending(1, null, Extended.FULL).enqueue(new Callback<List<TrendingShow>>() {
                @Override
                public void onResponse(Call<List<TrendingShow>> call, Response<List<TrendingShow>> response) {
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
                }

                @Override
                public void onFailure(Call<List<TrendingShow>> call, Throwable t) {
                    Log.e(TAG, "OnFailure", t);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "reponse error", e);
        }
    }
}