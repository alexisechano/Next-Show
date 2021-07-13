package com.example.next_show;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.next_show.data.TraktApplication;
import com.example.next_show.fragments.FeedFragment;
import com.example.next_show.fragments.ProfileFragment;
import com.example.next_show.models.Show;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.TrendingShow;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.services.Shows;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    // constants
    public static final String TAG = "MainActivity";
    public static final int PAGES_REQUESTED = 1;
    public static final int UNAUTHORIZED_REQUEST = 401;
    public static final int FORBIDDEN_REQUEST = 403;
    public static final int LIMIT = 10;

    public static List<Show> shows;

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

        // test the API call

        // new application for Trakt Call, pass in the Context
        Shows showsObj = new TraktApplication(this).getNewShowsInstance();

        // method to ASYNC call to API and grab trending shows
        fetchTraktData(showsObj);
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

    private void fetchTraktData(Shows traktShows) {
        try {
            traktShows.trending(PAGES_REQUESTED, LIMIT, Extended.FULL).enqueue(new Callback<List<TrendingShow>>() {
                @Override
                public void onResponse(Call<List<TrendingShow>> call, Response<List<TrendingShow>> response) {
                    if (response.isSuccessful()) {
                        List<TrendingShow> repsonseShows = response.body();
                        for (TrendingShow trending : repsonseShows) {
                            Log.i(TAG, "Title: " + trending.show.title);
                            Show currentShow = new Show(trending.show.title);
                            shows.add(currentShow);
                        }

                    } else {
                        if (response.code() == UNAUTHORIZED_REQUEST) {
                            // authorization required, supply a valid OAuth access token
                            Log.e(TAG, "Access token required");
                        } else if(response.code() == FORBIDDEN_REQUEST) {
                            // invalid API key
                            Log.e(TAG, "Invalid API key or unapproved app supplied");
                        } else{
                            // the request failed for some other reason
                            Log.e(TAG, "Response code: " + response.code());
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<TrendingShow>> call, Throwable t) {
                    Log.e(TAG, "OnFailure", t);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Call error", e);
        }
    }
}