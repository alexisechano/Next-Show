package com.example.next_show.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.uwetrottmann.trakt5.services.Shows;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class FeedFragment extends Fragment {
    // constants
    private static final String TAG = "FeedFragment";
    private static final String SHOW_DETAIL_URL = "https://api.themoviedb.org/3/tv/";
    private static final String ADD_API_KEY = "?api_key=";
    public static final int NOT_FOUND = -1;

    // genres for shows
    public static final String ACTION = "action";
    public static final String COMEDY = "comedy";
    public static final String DRAMA = "drama";
    public static final String ALL_SHOWS = "all";

    // view element variables
    private RecyclerView rvFeed;
    private RadioGroup rgGenre;
    private RadioButton selectedButton;

    // models and data variables
    private View currView;
    private Shows showsObj;
    private User currentUser;
    private RecommendationClient recClient;

    protected ShowAdapter adapter;
    protected List<Show> showsList; // what is being shown

    // empty constructor
    public FeedFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize the array that will hold posts
        showsList = new ArrayList<>();

        // creates a show adapter with show list
        adapter = new ShowAdapter(getActivity(), showsList, new NavigateFeedToDetail());

        // get current user
        currentUser = (User) ParseUser.getCurrentUser();

        // new application for Trakt Call, pass in the Context
        showsObj = new TraktApplication(getContext()).getNewShowsInstance();

        // set up rec client
        recClient = new RecommendationClient(showsObj);

        // get trending shows on load
        TraktApplication.fetchTrendingShows(showsObj, new ShowCallback());
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

            // set the adapter on the recycler view
            rvFeed.setAdapter(adapter);

            // set the layout manager on the recycler view
            rvFeed.setLayoutManager(new LinearLayoutManager(getActivity()));

            // find the radio button group for filtering
            rgGenre = currView.findViewById(R.id.rgGenre);

            // set up filtering by genre
            setFilterButtons();

            // determine whether to show trending or recommended
            BottomNavigationView toggleNav = (BottomNavigationView) currView.findViewById(R.id.filterMenu);

            // set selector
            toggleNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // reset everytime user switches between trending and recommended
                    RadioButton b = (RadioButton) currView.findViewById(R.id.btnAll);
                    b.setChecked(true);
                    adapter.clear();

                    switch (item.getItemId()) {
                        case R.id.action_trending:
                            // get trending shows
                            TraktApplication.fetchTrendingShows(showsObj, new ShowCallback());
                            break;
                        case R.id.action_recommend:
                            // get user's liked shows
                            List<String> savedShows = currentUser.getLikedSavedShows();

                            // fetch recommended shows
                            recClient.fetchRecommendedShows(savedShows, new ShowCallback(), new GenreMatchedCallback());
                            break;
                    }
                    return true;
                }
            });
        }

        return currView;
    }

    private void setFilterButtons() {
        rgGenre.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedButton = currView.findViewById(checkedId);
                String buttonText = selectedButton.getText().toString().toLowerCase();

                // determine which button is clicked
                switch (buttonText) {
                    case ACTION:
                        adapter.filter(ACTION);
                        rvFeed.smoothScrollToPosition(0);
                        break;
                    case COMEDY:
                        adapter.filter(COMEDY);
                        rvFeed.smoothScrollToPosition(0);
                        break;
                    case DRAMA:
                        adapter.filter(DRAMA);
                        rvFeed.smoothScrollToPosition(0);
                        break;
                    case ALL_SHOWS:
                        adapter.filter("");
                        rvFeed.smoothScrollToPosition(0);
                        break;
                }
            }

        });
    }

    public void fetchImage(String id, ImageCallback callback){
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

    // ********************** Callback and Navigation Classes ********************** //

    class NavigateFeedToDetail implements NavigationInterface {
        public void navigate(View v, Bundle b){
            Navigation.findNavController(v).navigate(R.id.action_feedFragment_to_showDetailFragment, b);
        }
    }

    public class ShowCallback implements ResponseCallback {
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

    public class GenreMatchedCallback implements ResponseCallback {
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

    public class DetailImageCallback implements ImageCallback {
        @Override
        public void onSuccess(JsonHttpResponseHandler.JSON json) {
            Log.d(TAG, "onSuccess");

            // get json object
            JSONObject jsonObj = json.jsonObject;

            // get results and save to show object
            try {
                String path = jsonObj.getString("backdrop_path");

                // finds show in list
                int index = search(jsonObj.getString("id"));

                // update show in FeedFragment
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

    // uses basic search algorithm
    public int search(String id){
        for (int index = 0; index < showsList.size(); index++){
            Show s = showsList.get(index);
            if(s.getId().equals(id)){
                return index;
            }
        }
        return NOT_FOUND;
    }
}
