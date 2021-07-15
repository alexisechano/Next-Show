package com.example.next_show.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.next_show.R;
import com.example.next_show.adapters.SavedAdapter;
import com.example.next_show.adapters.ShowAdapter;
import com.example.next_show.models.Show;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

// NO API CALLS IN THIS CLASS -> SOLELY PARSE DATABASE DATA!
public class SavedFragment extends Fragment {
    // view elements
    private TextView tvName;
    private RecyclerView rvSaved;

    protected SavedAdapter adapter;
    protected List<Show> savedShows;

    // constants
    public static final String TAG = "SavedFragment";

    // empty constructor
    public SavedFragment() {

    }

    public static SavedFragment newInstance() {
        SavedFragment fragment = new SavedFragment();
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
        View currView = inflater.inflate(R.layout.fragment_saved, container, false);

        // find Recycler View
        rvSaved = currView.findViewById(R.id.rvSaved);

        // initialize the array that will hold posts and create a PostsAdapter
        savedShows = new ArrayList<>();

        adapter = new SavedAdapter(getActivity(), savedShows);

        // set the adapter on the recycler view
        rvSaved.setAdapter(adapter);

        // set the layout manager on the recycler view
        rvSaved.setLayoutManager(new LinearLayoutManager(getActivity()));

        // get Parse data and load into view
        fetchParseShows();

        return currView;

    }

    // get data from Parse Database
    private void fetchParseShows() {
        // get current user
        ParseUser currUser = ParseUser.getCurrentUser();

        // get list of Shows from Parse
        queryPosts(currUser);

    }

    private void queryPosts(ParseUser targetUser) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Show> query = ParseQuery.getQuery(Show.class);

        // include data referred by user key
        query.include(Show.KEY_USER);

        // limit query to latest 20 items
        query.setLimit(10);

        // set user specific query
        query.whereEqualTo(Show.KEY_USER, targetUser);

        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");

        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Show>() {
            @Override
            public void done(List<Show> shows, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // save received posts to list and notify adapter of new data
                adapter.addAll(shows);
            }
        });
    }
}
