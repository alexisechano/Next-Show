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
import com.example.next_show.fragments.RatingsFragment;
import com.example.next_show.models.Show;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.Ratings;
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
                        // set feed fragment
                        FeedFragment feedFragment = FeedFragment.newInstance();
                        setFeedFragment(feedFragment);
                        break;
                    case R.id.action_rating:
                        // set rating fragment
                        RatingsFragment ratingsFragment = RatingsFragment.newInstance();
                        setRatingsFragment(ratingsFragment);
                        break;
                }
                return true;
            }
        });
    }

    // methods to toggle between fragments
    private void setFeedFragment(FeedFragment feedFragment) {
        Log.i(TAG, "Switching to FeedFragment");
        FragmentTransaction feedTransact = getSupportFragmentManager().beginTransaction();
        feedTransact.replace(R.id.fragment_container_view, feedFragment);
        feedTransact.commit();
    }

    private void setProfileFragment(ProfileFragment profileFragment) {
        Log.i(TAG, "Switching to ProfileFragment");
        FragmentTransaction profileTransact = getSupportFragmentManager().beginTransaction();
        profileTransact.replace(R.id.fragment_container_view, profileFragment);
        profileTransact.commit();
    }

    private void setRatingsFragment(RatingsFragment ratingsFragment) {
        Log.i(TAG, "Switching to RatingsFragment");
        FragmentTransaction ratingsTransact = getSupportFragmentManager().beginTransaction();
        ratingsTransact.replace(R.id.fragment_container_view, ratingsFragment);
        ratingsTransact.commit();
    }
}