package com.example.next_show.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

import com.example.next_show.data.ShowFilter;
import com.example.next_show.data.TraktApplication;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.example.next_show.navigators.NavigationInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseUser;
import com.uwetrottmann.trakt5.services.Shows;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Headers;

public class FeedFragment extends Fragment {
    // constants
    private static final String TAG = "FeedFragment";
    private static final String SHOW_DETAIL_URL = "https://api.themoviedb.org/3/tv/";
    private static final String ADD_API_KEY = "?api_key=";
    private static final int NOT_FOUND = -1;

    // lists match with dialog list of checks -> necessary types for AlertDialog.Builder
    private static final String[] GENRE_FILTERS = {ShowFilter.ACTION, ShowFilter.COMEDY, ShowFilter.DRAMA};
    private boolean[] selectedGenres = {false, false, false};
    private static final String[] NETWORK_FILTERS = {ShowFilter.CABLE, ShowFilter.STREAMING};
    private boolean[] selectedNetworks = {false, false};
    private static final String[] YEAR_FILTERS = {ShowFilter.PRIOR, ShowFilter.PAST_FIVE, ShowFilter.THIS_YEAR};
    private boolean[] selectedYears = {false, false, false};

    // view element variables
    private RecyclerView rvFeed;
    private Button btnGenre;
    private Button btnNetwork;
    private Button btnYear;
    private ImageButton btnReset;

    // models and data variables
    private View currView;
    private Shows showsObj;
    private User currentUser;
    private RecommendationClient recClient;
    private ShowFilter showFilter;

    // callbacks for Trakt
    private GenreMatchedCallback genreMatchedCallback;
    private DetailImageCallback imageCallback;
    private TrendingShowCallback trendingShowCallback;
    private RelatedShowCallback relatedShowCallback;

    protected ShowAdapter adapter;
    protected List<Show> showsList; // what is being shown

    // empty constructor
    public FeedFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize the array that will hold posts
        showsList = new ArrayList<>();

        // init the filter
        showFilter = new ShowFilter();

        // creates a show adapter with show list
        adapter = new ShowAdapter(getActivity(), showsList, new NavigateFeedToDetail());

        // get current user
        currentUser = (User) ParseUser.getCurrentUser();

        // new application for Trakt Call, pass in the Context
        showsObj = new TraktApplication(getContext()).getNewShowsInstance();

        // set up rec client
        recClient = new RecommendationClient(showsObj);

        // init callbacks
        trendingShowCallback = new TrendingShowCallback();
        relatedShowCallback = new RelatedShowCallback();
        genreMatchedCallback = new GenreMatchedCallback();
        imageCallback = new DetailImageCallback();

        // get trending shows on load
        TraktApplication.fetchTrendingShows(showsObj, trendingShowCallback);
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

            // find filter buttons
            setFilterButtons();

            // determine whether to show trending or recommended
            BottomNavigationView topFeedNav = (BottomNavigationView) currView.findViewById(R.id.filterMenu);

            // set selector
            topFeedNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_trending:
                            // get trending shows and reset
                            resetPage();
                            TraktApplication.fetchTrendingShows(showsObj, trendingShowCallback);
                            break;
                        case R.id.action_recommend:
                            // get user's liked shows
                            List<String> savedShows = currentUser.getLikedSavedShows();

                            // fetch recommended shows and reset
                            resetPage();
                            recClient.fetchRelatedShows(savedShows, relatedShowCallback);
                            break;
                    }
                    return true;
                }
            });
        }

        return currView;
    }

    private void resetPage() {
        // resets filters using same set up method
        showFilter.resetFilters();

        // necessary to ensure all files are referring to same list of shows
        showsList = showFilter.getAllShows();

        // clears the screen and current list of shows everywhere
        adapter.clear();
        showFilter.clear();
        showsList.clear();

        selectedGenres = new boolean[]{false, false, false};
        selectedNetworks = new boolean[]{false, false};
        selectedYears = new boolean[]{false, false, false};
    }

    private void setFilterButtons() {
        // find them in layout
        btnGenre = currView.findViewById(R.id.btnGenre);
        btnNetwork = currView.findViewById(R.id.btnNetwork);
        btnYear = currView.findViewById(R.id.btnYear);
        btnReset = currView.findViewById(R.id.btnReset);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetShows();
            }
        });

        // set onclick listeners for dropdown menu
        btnGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(GENRE_FILTERS, selectedGenres, ShowFilter.GENRE);
            }
        });

        btnNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(NETWORK_FILTERS, selectedNetworks, ShowFilter.NETWORK);
            }
        });

        btnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(YEAR_FILTERS, selectedYears, ShowFilter.YEAR);
            }
        });
    }

    private void showDialog(String[] options, boolean[] checkedItems, String type) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle("Add filter(s) for " + type);
        dialogBuilder.setCancelable(true);

        dialogBuilder.setMultiChoiceItems(options, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // update options
                checkedItems[which] = isChecked;
                String option = options[which];

                switch (type) {
                    case ShowFilter.GENRE:
                        if (isChecked) {
                            showFilter.addToGenre(option);
                        } else {
                            showFilter.removeFromGenre(option);
                        }
                        return;
                    case ShowFilter.NETWORK:
                        if (isChecked) {
                            showFilter.addToNetwork(option);
                        } else {
                            showFilter.removeFromNetwork(option);
                        }
                        return;
                    case ShowFilter.YEAR:
                        if (isChecked) {
                            showFilter.addToYear(option);
                        } else {
                            showFilter.removeFromYear(option);
                        }
                }
            }
        });

        // create and show the dialog
        dialogBuilder.setPositiveButton("Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // verify filters
                Log.i(TAG, showFilter.getFilters().toString());

                // filter shows and send to adapter to load
                List<Show> filtered = showFilter.filterShows(new ArrayList<>(showsList));
                adapter.update(filtered);

                // check if blank
                if (adapter.getItemCount() == 0) {
                    Snackbar.make(currView, "No shows found! Showing all shows...", Snackbar.LENGTH_LONG).show();
                    resetShows();
                }
            }
        });

        dialogBuilder.setNegativeButton("Cancel", null);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    // removes filters and shows all shows
    private void resetShows() {
        adapter.clear();
        adapter.addAll(showFilter.getAllShows());
        rvFeed.smoothScrollToPosition(0);

        // resets everything
        showFilter.resetFilters();
        selectedGenres = new boolean[]{false, false, false};
        selectedNetworks = new boolean[]{false, false};
        selectedYears = new boolean[]{false, false, false};
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

    // ********************** Callback and Navigation Classes ********************** //

    class NavigateFeedToDetail implements NavigationInterface {
        public void navigate(View v, Bundle b){
            Navigation.findNavController(v).navigate(R.id.action_feedFragment_to_showDetailFragment, b);
        }
    }

    public class TrendingShowCallback implements ResponseCallback {
        @Override
        public void onSuccess(List<Show> shows) {
            // update adapter declared in FeedFragment
            adapter.addAll(shows);
            showFilter.addAll(shows);

            // make API call to grab images
            for(Show s: shows){
                fetchImage(s.getId(), imageCallback);
            }
        }

        @Override
        public void onFailure(int code) {
            RecommendationClient.determineError(code);
        }
    }

    public class RelatedShowCallback implements ResponseCallback {
        @Override
        public void onSuccess(List<Show> shows) {
            // update adapter declared in FeedFragment
            adapter.addAll(shows);
            showFilter.addAll(shows);

            // make API call to grab images
            for(Show s: shows){
                Log.i("RELATED SHOWS!", "Show: " + s.getId() + " " + s.getTitle());
                fetchImage(s.getId(), imageCallback);
            }
        }

        @Override
        public void onFailure(int code) {
            RecommendationClient.determineError(code);
            if (code == RecommendationClient.ERROR_NO_SAVED_SHOWS_TO_MATCH_RELATED) {
                recClient.fetchGenreMatchedShows(genreMatchedCallback);
            }
        }
    }

    public class GenreMatchedCallback implements ResponseCallback {
        @Override
        public void onSuccess(List<Show> shows) {
            // retrieve user fave genres
            List<String> favoriteGenres = currentUser.getFaveGenres();

            // do logic to get only fave genre ones
            List<Show> genreMatchedShows = new ArrayList<>();
            Set<String> faveGenres = new HashSet<String>(favoriteGenres);
            faveGenres.addAll(favoriteGenres);

            for (Show s: shows) {
                if (showFilter.matchedGenreFilter(s, faveGenres)) {
                    genreMatchedShows.add(s);
                }
            }

            // no matches, let user know and don't add to adapter
            if (genreMatchedShows.isEmpty()) {
                Toast.makeText(getContext(), "No shows match your genres :(", Toast.LENGTH_LONG).show();
                return;
            }

            // update adapter
            adapter.addAll(genreMatchedShows);
            showFilter.addAll(genreMatchedShows);

            // make API call to grab images
            for(Show s: genreMatchedShows){
                fetchImage(s.getId(), imageCallback);
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

                if (index == NOT_FOUND) {
                    Log.e(TAG, "Cannot find index!");
                    return;
                }

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