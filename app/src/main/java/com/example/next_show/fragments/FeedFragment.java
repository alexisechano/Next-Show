package com.example.next_show.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.next_show.R;
import com.example.next_show.adapters.ShowAdapter;
import com.example.next_show.data.TraktApplication;
import com.example.next_show.models.Show;
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
    public static final int UNAUTHORIZED_REQUEST = 401;
    public static final int FORBIDDEN_REQUEST = 403;
    public static final int LIMIT = 10;

    // view element variables
    private RecyclerView rvFeed;

    protected ShowAdapter adapter;
    protected List<Show> showsList;

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
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
        View currView = inflater.inflate(R.layout.fragment_feed, container, false);

        // find Recycler View
        rvFeed = currView.findViewById(R.id.rvFeed);

        // initialize the array that will hold posts and create a PostsAdapter
        showsList = new ArrayList<>();
        adapter = new ShowAdapter(getActivity(), showsList);

        // set the adapter on the recycler view
        rvFeed.setAdapter(adapter);

        // set the layout manager on the recycler view
        rvFeed.setLayoutManager(new LinearLayoutManager(getActivity()));

        // new application for Trakt Call, pass in the Context
        Shows showsObj = new TraktApplication(getContext()).getNewShowsInstance();

        // method to ASYNC call to API and grab trending shows
        fetchTraktData(showsObj);

        return currView;
    }

    private void fetchTraktData(Shows traktShows) {
        try {
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
