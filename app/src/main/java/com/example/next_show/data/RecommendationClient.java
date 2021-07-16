package com.example.next_show.data;

import android.content.Context;
import android.util.Log;

import com.example.next_show.adapters.ShowAdapter;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.uwetrottmann.trakt5.entities.TrendingShow;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.services.Shows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    // empty constructor
    public RecommendationClient() {

    }

    public RecommendationClient(Context context) {
        this.context = context; }

    public void fetchRelatedShows(Shows traktShows, Show compShow, ShowAdapter adapter) {
        // use given show ID to generate related, recommended shows
        String searchID = compShow.getId();

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

        // TODO: save genres of the shows within database and the Show obj to access here

        for(Show s: shows){
            List<String> showGenres = s.getGenres();

            // if any genre(s) match the faves
            if (!Collections.disjoint(showGenres, favoriteGenres)){
                genreMatched.add(s);
            }
        }

        return genreMatched;
    }

    // get it by genre from recommended shows call
    public void fetchRecommendedShows(Shows traktShows, ShowAdapter adapter, User user) {
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
                        List<Show> genreMatchedShows = getGenreMatch(updatedShows, user);

                        // send to adapter to update
                        //adapter.addAll(genreMatchedShows);

                        // log to see if it works
                        for (Show s : genreMatchedShows){
                            Log.i(TAG, "REC SHOW: " + s.getTitle());
                        }

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
                public void onFailure(Call<List<com.uwetrottmann.trakt5.entities.Show>> call, Throwable t) {
                    Log.e(TAG, "OnFailure", t);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Call error", e);
        }
    }

}
