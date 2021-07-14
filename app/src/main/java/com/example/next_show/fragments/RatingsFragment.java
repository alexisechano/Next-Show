package com.example.next_show.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.next_show.R;

public class RatingsFragment extends Fragment {

    // empty constructor
    public RatingsFragment() {

    }

    public static RatingsFragment newInstance() {
        RatingsFragment fragment = new RatingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View currView = inflater.inflate(R.layout.fragment_ratings, container, false);
        return currView;

    }
}
