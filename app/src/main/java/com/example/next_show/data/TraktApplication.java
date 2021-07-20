package com.example.next_show.data;

import android.content.Context;
import android.util.Log;

import com.example.next_show.R;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.services.Shows;

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
}
