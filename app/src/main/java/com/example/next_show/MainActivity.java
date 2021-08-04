package com.example.next_show;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    // constants
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up navigation controller
        setUpNavigation();
    }

    private void setUpNavigation() {
        Log.i(TAG, "Setting up Navigation Controller and Navbar");

        // declare a new Host fragment connected to XML
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        // find attached navigation controller
        NavController navController = navHostFragment.getNavController();

        // set up bottom nav view
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        // connect the bottom view with the controller
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}