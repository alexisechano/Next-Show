package com.example.next_show.data;

import android.util.Log;

import com.example.next_show.callbacks.ResponseCallback;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
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

    // empty constructor
    public RecommendationClient() { }

    public void fetchRelatedShows(Shows traktShows, List<String> savedShows, ResponseCallback callback) {
        // use randomly picked show ID to generate related, recommended shows
        final int randomShowIndex = new Random().nextInt(savedShows.size());
        String searchID = savedShows.get(randomShowIndex);

        try {
            // enqueue to do asynchronous call and execute to do it synchronously
            traktShows.related(searchID, PAGES_REQUESTED, LIMIT, Extended.FULL).enqueue(new Callback<List<com.uwetrottmann.trakt5.entities.Show>>() {
                @Override
                public void onResponse(Call<List<com.uwetrottmann.trakt5.entities.Show>> call, Response<List<com.uwetrottmann.trakt5.entities.Show>> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure(response.code());
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

    // get it by genre from recommended shows call
    public void fetchGenreMatchedShows(Shows traktShows, ResponseCallback callback) {
        try {
            // enqueue to do asynchronous call and execute to do it synchronously
            traktShows.popular(PAGES_REQUESTED, LIMIT, Extended.FULL).enqueue(new Callback<List<com.uwetrottmann.trakt5.entities.Show>>() {
                @Override
                public void onResponse(Call<List<com.uwetrottmann.trakt5.entities.Show>> call, Response<List<com.uwetrottmann.trakt5.entities.Show>> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure(response.code());
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

    public static List<Show> getGenreMatch(List<Show> shows, User user){
        // get proper user genres
        List<String> favoriteGenres = user.getFaveGenres();

        // init return list
        List<Show> genreMatched = new ArrayList<>();

        for (Show s: shows) {
            List<String> showGenres = s.getGenres();

            // if any genre(s) match the favorite ones
            if (!Collections.disjoint(showGenres, favoriteGenres)) {
                genreMatched.add(s);
            }
        }

        return genreMatched;
    }

    public static void determineError(int code) {
        switch (code) {
            case UNAUTHORIZED_REQUEST:
                // authorization required, supply a valid OAuth access token
                Log.e(TAG, "Access token required");
            case FORBIDDEN_REQUEST:
                // invalid API key
                Log.e(TAG, "Invalid API key or unapproved app supplied");
            default:
                // the request failed for some other reason
                Log.e(TAG, "Response code: " + code);
        }
    }
}
