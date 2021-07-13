package com.example.next_show.data;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.next_show.R;
import com.example.next_show.models.Show;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.TrendingShow;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.services.Shows;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TraktApplication {
    // constants
    public static final String TAG = "TraktApplication";
    private Context context;

    // empty constructor
    public TraktApplication() {

    }

    public TraktApplication(Context context) {
        this.context = context;
    }

    public Shows getNewShowsInstance(){
        // Trakt wrapper
        TraktV2 trakt = new TraktV2(context.getString(R.string.trakt_client_id));
        Shows traktShows = trakt.shows();
        Log.i(TAG, "Created new instance for Trakt");
        return traktShows;
    }

}
