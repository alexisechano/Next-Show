package com.example.next_show.data;

import android.util.Log;

import com.example.next_show.callbacks.ResponseCallback;
import com.example.next_show.models.Show;
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
    public static final int LOWER_LIMIT = 5;
    public static final int UPPER_LIMIT = 7;

    public static final int ERROR_NO_SAVED_SHOWS_TO_MATCH_RELATED = 11;
    public static final int TRAKT_FAILURE = 10;
    public static final int NO_CALL = -1;

    // instance vars
    private Shows showsObj;

    // empty constructor
    public RecommendationClient() { }

    public RecommendationClient(Shows showsObj) {
        this.showsObj = showsObj;
    }

    public void fetchRelatedShows(List<String> savedShows, ResponseCallback callback) {
        // check if no saved shows and immediately fail
        if (savedShows == null || savedShows.isEmpty()) {
            callback.onFailure(ERROR_NO_SAVED_SHOWS_TO_MATCH_RELATED);
            return;
        }

        // use randomly picked show ID to generate related, recommended shows
        final int randomShowIndex = new Random().nextInt(savedShows.size());
        String searchID = savedShows.get(randomShowIndex);
        Log.i(TAG, "Chosen: " + searchID);

        try {
            // enqueue to do asynchronous call and execute to do it synchronously
            showsObj.related(searchID, PAGES_REQUESTED, LOWER_LIMIT, Extended.FULL).enqueue(new Callback<List<com.uwetrottmann.trakt5.entities.Show>>() {
                @Override
                public void onResponse(Call<List<com.uwetrottmann.trakt5.entities.Show>> call, Response<List<com.uwetrottmann.trakt5.entities.Show>> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess(Show.formatShows(response.body()));
                    } else {
                        callback.onFailure(response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<com.uwetrottmann.trakt5.entities.Show>> call, Throwable t) {
                    Log.e(TAG, "OnFailure", t);
                    callback.onFailure(TRAKT_FAILURE);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Call error in try/catch", e);
            callback.onFailure(NO_CALL);
        }
    }

    // get it by genre from recommended shows call
    public void fetchGenreMatchedShows(ResponseCallback callback) {
        try {
            // enqueue to do asynchronous call and execute to do it synchronously
            showsObj.popular(PAGES_REQUESTED, UPPER_LIMIT, Extended.FULL).enqueue(new Callback<List<com.uwetrottmann.trakt5.entities.Show>>() {
                @Override
                public void onResponse(Call<List<com.uwetrottmann.trakt5.entities.Show>> call, Response<List<com.uwetrottmann.trakt5.entities.Show>> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess(Show.formatShows(response.body()));
                    } else {
                        callback.onFailure(response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<com.uwetrottmann.trakt5.entities.Show>> call, Throwable t) {
                    Log.e(TAG, "OnFailure", t);
                    callback.onFailure(TRAKT_FAILURE);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Call error in try/catch", e);
            callback.onFailure(NO_CALL);
        }
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
