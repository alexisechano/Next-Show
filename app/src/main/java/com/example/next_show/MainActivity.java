package com.example.next_show;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.next_show.fragments.FeedFragment;
import com.example.next_show.fragments.ProfileFragment;
import com.example.next_show.fragments.SavedFragment;
import com.example.next_show.models.Show;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

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
        FeedFragment feedFragment = FeedFragment.newInstance();
        setFeedFragment(feedFragment);

        // set bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_profile:
                        // set profile fragment
                        ProfileFragment profileFragment = ProfileFragment.newInstance();
                        setProfileFragment(profileFragment);
                        break;
                    case R.id.action_home:
                        // set feed fragment
                        setFeedFragment(feedFragment);
                        break;
                    case R.id.action_rating:
                        // set rating fragment
                        SavedFragment savedFragment = SavedFragment.newInstance();
                        SavedFragment(savedFragment);
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

    private void SavedFragment(SavedFragment savedFragment) {
        Log.i(TAG, "Switching to SavedFragment");
        FragmentTransaction savedTransact = getSupportFragmentManager().beginTransaction();
        savedTransact.replace(R.id.fragment_container_view, savedFragment);
        savedTransact.commit();
    }
}