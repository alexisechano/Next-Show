package com.example.next_show.fragments;

import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.next_show.R;
import com.example.next_show.callbacks.ImageCallback;
import com.example.next_show.callbacks.ResponseCallback;
import com.example.next_show.adapters.ShowAdapter;
import com.example.next_show.data.RecommendationClient;

import com.example.next_show.data.TraktApplication;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.example.next_show.navigators.NavigationInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;
import com.uwetrottmann.trakt5.entities.TrendingShow;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.services.Shows;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedFragment extends Fragment {
    // constants
    private static final String TAG = "FeedFragment";
    public static final int PAGES_REQUESTED = 1;
    public static final int LIMIT = 5;

    private static final String SHOW_DETAIL_URL = "https://api.themoviedb.org/3/tv/";
    private static final String ADD_API_KEY = "?api_key=";

    // view element variables
    private RecyclerView rvFeed;
    private View currView;
    private Shows showsObj;
    private User currentUser;

    protected ShowAdapter adapter;
    protected List<Show> showsList;

    // empty constructor
    public FeedFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // new application for Trakt Call, pass in the Context
        showsObj = new TraktApplication(getContext()).getNewShowsInstance();

        // get trending shows on load
        fetchTrendingShows(showsObj, new ShowCallback());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // prevent reloading view if not necessary
        if(currView == null) {
            // Inflate the layout for this fragment
            currView = inflater.inflate(R.layout.fragment_feed, container, false);

            // find Recycler View
            rvFeed = currView.findViewById(R.id.rvFeed);

            // initialize the array that will hold posts
            showsList = new ArrayList<>();

            // creates a show adapter with show list
            adapter = new ShowAdapter(getActivity(), showsList, new NavigateFeedToDetail());

            // set the adapter on the recycler view
            rvFeed.setAdapter(adapter);

            // set the layout manager on the recycler view
            rvFeed.setLayoutManager(new LinearLayoutManager(getActivity()));

            // get current user
            currentUser = (User) ParseUser.getCurrentUser();

            // determine whether to show trending or recommended
            BottomNavigationView toggleNav = (BottomNavigationView) currView.findViewById(R.id.filterMenu);

            // set selector
            toggleNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_trending:
                            // clear recycler view for new shows
                            adapter.clear();

                            // get trending shows
                            fetchTrendingShows(showsObj, new ShowCallback());

                            break;
                        case R.id.action_recommend:
                            // clear adapter to be able to make room for new recommendations
                            adapter.clear();

                            // fetch recommended shows
                            fetchRecommendedShows(showsObj);

                            break;
                    }
                    return true;
                }
            });
        }
        return currView;
    }

    private void fetchImage(String id, ImageCallback callback){
        AsyncHttpClient client = new AsyncHttpClient();
        String getUrl = SHOW_DETAIL_URL + id + ADD_API_KEY + getContext().getString(R.string.movie_api_key);

            // call URL and parse through JSON
            client.get(getUrl, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Headers headers, JSON json) {
                    callback.onSuccess(json);
                }

                @Override
                public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                    callback.onFailure(i);
                }
            });
    }

    private void fetchRecommendedShows(Shows showsObj) {
        // get user's liked shows
        List<String> savedShows = currentUser.getLikedSavedShows();

        // set up recommendation client to grab more shows
        RecommendationClient recClient = new RecommendationClient();

        // get related shows based on saved LIKED shows -> updates adapter within callback
        recClient.fetchRelatedShows(showsObj, savedShows, new ShowCallback());

        // get more recommended shows if the above doesn't retrieve any shows
        if(adapter.getItemCount() == 0) {
            // get recommended shows based on User preferences -> updates adapter within callback
            recClient.fetchGenreMatchedShows(showsObj, new GenreMatchedCallback());
        }
    }

    private void fetchTrendingShows(Shows traktShows, ResponseCallback callback) {
        Log.i(TAG, "Fetching shows now!");
        try {
            // enqueue to do asynchronous call and execute to do it synchronously
            traktShows.trending(PAGES_REQUESTED, LIMIT, Extended.FULL).enqueue(new Callback<List<TrendingShow>>() {
                @Override
                public void onResponse(Call<List<TrendingShow>> call, Response<List<TrendingShow>> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess(Show.formatTrendingShows(response.body()));
                    } else {
                        callback.onFailure(response.code());
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

    class NavigateFeedToDetail implements NavigationInterface {
        public void navigate(View v, Bundle b){
            Navigation.findNavController(v).navigate(R.id.action_feedFragment_to_showDetailFragment, b);
        }
    }

    class ShowCallback implements ResponseCallback {
        @Override
        public void onSuccess(List<Show> shows) {
            // update adapter declared in FeedFragment
            adapter.addAll(shows);

            // make API call to grab images
            for(Show s: shows){
                fetchImage(s.getId(), new DetailImageCallback());
            }
        }

        @Override
        public void onFailure(int code) {
            RecommendationClient.determineError(code);
        }
    }

    class GenreMatchedCallback implements ResponseCallback {
        @Override
        public void onSuccess(List<Show> shows) {
            // do logic to get only fave genre ones
            List<Show> genreMatchedShows = RecommendationClient.getGenreMatch(shows, currentUser);

            // no matches, let user know and don't add to adapter
            if (genreMatchedShows.isEmpty()) {
                Toast.makeText(getContext(), "No shows available right now :(", Toast.LENGTH_LONG).show();
                return;
            }

            // update adapter
            adapter.addAll(shows);

            // make API call to grab images
            for(Show s: shows){
                fetchImage(s.getId(), new DetailImageCallback());
            }
        }

        @Override
        public void onFailure(int code) {
            Toast.makeText(getContext(), "No shows available right now :(", Toast.LENGTH_LONG).show();
            RecommendationClient.determineError(code);
        }
    }

    class DetailImageCallback implements ImageCallback {
        @Override
        public void onSuccess(JsonHttpResponseHandler.JSON json) {
            Log.d(TAG, "onSuccess");

            // get json object
            JSONObject jsonObj = json.jsonObject;

            // get results and save to show object
            try {
                String path = jsonObj.getString("backdrop_path");

                // finds show in adapter
                int index = adapter.search(jsonObj.getString("id"));

                // update show in FeedFragment -> same show list as adapter!
                Show show = showsList.get(index);
                show.setImageUrl(path);

                // updates the show image at that index
                adapter.notifyItemChanged(index);

            } catch (JSONException e) {
                Log.e(TAG, "Hit JSON Exception");
            }
        }

        @Override
        public void onFailure(int i) {
            Log.e(TAG, "Error Code: " + i);
        }
    }
}
