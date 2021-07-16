package com.example.next_show.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.next_show.adapters.ShowAdapter;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.uwetrottmann.trakt5.entities.TrendingShow;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.services.Shows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendationClient {
    // constants
    public static final String TAG = "RecommendationClient";
    public static final int PAGES_REQUESTED = 1;
    public static final int UNAUTHORIZED_REQUEST = 401;
    public static final int FORBIDDEN_REQUEST = 403;
    public static final int LIMIT = 5;

    // instance vars
    private Context context;
    private User currentUser;

    // empty constructor
    public RecommendationClient() {

    }

    public RecommendationClient(Context context, User currentUser) {
        this.context = context;
        this.currentUser = currentUser;
    }

    // TODO: will need to use User's liked saved shows for this one unless -> random choice?
    public void fetchRelatedShows(Shows traktShows, ShowAdapter adapter) {
        // log statement
        Log.i(TAG, "Fetching related shows from Trakt!");

        // use given show ID to generate related, recommended shows -> randomize it!
        List<String> savedShows = currentUser.getLikedSavedShows();
        String searchID = savedShows.get(new Random().nextInt(savedShows.size()));;

        // from trakt wrapper -> call to get related shows and send to adapter
        try {
            // enqueue to do asynchronous call and execute to do it synchronously
            traktShows.related(searchID, PAGES_REQUESTED, LIMIT, Extended.FULL).enqueue(new Callback<List<com.uwetrottmann.trakt5.entities.Show>>() {
                @Override
                public void onResponse(Call<List<com.uwetrottmann.trakt5.entities.Show>> call, Response<List<com.uwetrottmann.trakt5.entities.Show>> response) {
                    if (response.isSuccessful()) {
                        List<com.uwetrottmann.trakt5.entities.Show> repsonseShows = response.body();

                        // turn all of these into usable Show objects
                        List<Show> updatedShows = Show.fromRecShows(repsonseShows);

                        // set the adapter to update
                        adapter.addAll(updatedShows);
                    } else {
                        Toast.makeText(context, "No shows available right now :(", Toast.LENGTH_LONG).show();
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
                public void onFailure(Call<List<com.uwetrottmann.trakt5.entities.Show>> call, Throwable t) {
                    Log.e(TAG, "OnFailure", t);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Call error", e);
        }
    }

    public List<Show> getGenreMatch(List<Show> shows, User user){
        // get proper user
        List<String> favoriteGenres = user.getFaveGenres();

        // init return list
        List<Show> genreMatched = new ArrayList<>();

        for(Show s: shows){
            List<String> showGenres = s.getGenres();

            // if any genre(s) match the favorite ones
            if (!Collections.disjoint(showGenres, favoriteGenres)){
                genreMatched.add(s);
            }
        }

        return genreMatched;
    }

    // get it by genre from recommended shows call
    public void fetchRecommendedShows(Shows traktShows, ShowAdapter adapter) {
        // from trakt wrapper -> call to get related shows and send to adapter
        try {
            // enqueue to do asynchronous call and execute to do it synchronously
            traktShows.popular(PAGES_REQUESTED, LIMIT, Extended.FULL).enqueue(new Callback<List<com.uwetrottmann.trakt5.entities.Show>>() {
                @Override
                public void onResponse(Call<List<com.uwetrottmann.trakt5.entities.Show>> call, Response<List<com.uwetrottmann.trakt5.entities.Show>> response) {
                    if (response.isSuccessful()) {
                        List<com.uwetrottmann.trakt5.entities.Show> repsonseShows = response.body();

                        // turn all of these into usable Show objects
                        List<Show> updatedShows = Show.fromRecShows(repsonseShows);

                        // do logic to get only fave genre ones
                        List<Show> genreMatchedShows = getGenreMatch(updatedShows, currentUser);

                        if(genreMatchedShows.isEmpty()){
                            Toast.makeText(context, "No shows available right now :(", Toast.LENGTH_LONG).show();
                            Log.i(TAG, "No shows match this user's preferences");
                            return;
                        }

                        // send to adapter to update
                        adapter.addAll(genreMatchedShows);

                        // log to see if it works
                        for (Show s : genreMatchedShows){
                            Log.i(TAG, "REC SHOW: " + s.getTitle());
                        }

                    } else {
                        Toast.makeText(context, "No shows available right now :(", Toast.LENGTH_LONG).show();
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
                public void onFailure(Call<List<com.uwetrottmann.trakt5.entities.Show>> call, Throwable t) {
                    Log.e(TAG, "OnFailure", t);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Call error", e);
        }
    }

}
