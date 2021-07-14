package com.example.next_show.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.next_show.R;

// NO API CALLS IN THIS CLASS -> SOLELY PARSE DATABASE DATA!
public class RatingsFragment extends Fragment {
    // view elements
    private Button btnAdd;
    private TextView tvName;
    private TextView tvSavedTitle;
    private RecyclerView rvSaved;

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

        // find Recycler View
        rvSaved = currView.findViewById(R.id.rvSaved);

        // find button and set onclick listener
        btnAdd = currView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: do something on click -> launch ratings on click
            }
        });

        return currView;

    }
}
