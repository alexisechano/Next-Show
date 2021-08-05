package com.example.next_show.data;

import android.content.Context;
import android.util.Log;

import com.example.next_show.R;
import com.example.next_show.callbacks.ResponseCallback;
import com.example.next_show.models.Show;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.TrendingShow;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.services.Shows;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TraktApplication {
    // constants
    private static final String TAG = "TraktApplication";
    public static final int PAGES_REQUESTED = 1;
    public static final int LIMIT = 7;
    public static final int NO_CODE = 0;

    // instance var
    private Context context;

    // empty constructor
    public TraktApplication() { }

    public TraktApplication(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public Shows getNewShowsInstance(){
        // Trakt wrapper
        TraktV2 trakt = new TraktV2(context.getString(R.string.trakt_client_id));
        Shows traktShows = trakt.shows();
        Log.i(TAG, "Created new instance for Trakt");
        return traktShows;
    }

    public static void fetchTrendingShows(Shows showsObj, ResponseCallback callback) {
        Log.i(TAG, "Fetching shows now!");
        try {
            // enqueue to do asynchronous call and execute to do it synchronously
            showsObj.trending(PAGES_REQUESTED, LIMIT, Extended.FULL).enqueue(new Callback<List<TrendingShow>>() {
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
                    callback.onFailure(NO_CODE);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Call error in try/catch", e);
            callback.onFailure(NO_CODE);
        }
    }
}
