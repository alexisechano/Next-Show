package com.example.next_show.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.next_show.R;
import com.example.next_show.adapters.ShowAdapter;
import com.example.next_show.data.RecommendationClient;
import com.example.next_show.data.TraktApplication;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;
import com.uwetrottmann.trakt5.entities.TrendingShow;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.services.Shows;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedFragment extends Fragment {
    // constants
    private static final String TAG = "FeedFragment";
    public static final int PAGES_REQUESTED = 1;
    public static final int LIMIT = 5;

    // view element variables
    private RecyclerView rvFeed;
    private View currView;
    private Shows showsObj;

    protected ShowAdapter adapter;
    protected List<Show> showsList;

    // empty constructor
    public FeedFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // new application for Trakt Call, pass in the Context
        showsObj = new TraktApplication(getContext()).getNewShowsInstance();

        // get trending shows
        fetchTraktData(showsObj);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // prevent reloading if not necessary
        if(currView == null) {
            // Inflate the layout for this fragment
            currView = inflater.inflate(R.layout.fragment_feed, container, false);

            // find Recycler View
            rvFeed = currView.findViewById(R.id.rvFeed);

            // initialize the array that will hold posts and create a ShowAdapter
            showsList = new ArrayList<>();
            adapter = new ShowAdapter(getActivity(), showsList, false);

            // set the adapter on the recycler view
            rvFeed.setAdapter(adapter);

            // set the layout manager on the recycler view
            rvFeed.setLayoutManager(new LinearLayoutManager(getActivity()));

            // determine whether to show trending or recommended
            BottomNavigationView bottomNavigationView = (BottomNavigationView) currView.findViewById(R.id.filterMenu);

            // set selector
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_trending:
                            // clear recycler view for new shows
                            adapter.clear();

                            // get trending shows
                            fetchTraktData(showsObj);

                            break;
                        case R.id.action_recommend:
                            // clear adapter to be able to make room for new recommendations
                            adapter.clear();

                            // set up recommendation client to grab more shows
                            RecommendationClient recClient = new RecommendationClient(getContext(), (User) ParseUser.getCurrentUser());

                            // get related shows based on saved LIKED shows
                            //recClient.fetchRelatedShows(showsObj, adapter);

                            // get recommended shows based on User preferences
                            recClient.fetchRecommendedShows(showsObj, adapter);

                            break;
                    }
                    return true;
                }
            });
        }
        return currView;
    }

    private void fetchTraktData(Shows traktShows) {
        try {
            // enqueue to do asynchronous call and execute to do it synchronously
            traktShows.trending(PAGES_REQUESTED, LIMIT, Extended.FULL).enqueue(new Callback<List<TrendingShow>>() {
                @Override
                public void onResponse(Call<List<TrendingShow>> call, Response<List<TrendingShow>> response) {
                    if (response.isSuccessful()) {
                        List<TrendingShow> repsonseShows = response.body();

                        // turn all of these into usable Show objects
                        List<Show> updatedShows = Show.fromTrendingShows(repsonseShows);

                        // set the adapter to update
                        adapter.addAll(updatedShows);
                    } else {
                        Toast.makeText(getContext(), "No shows available right now :(", Toast.LENGTH_LONG).show();
                        RecommendationClient.determineError(response.code());
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
