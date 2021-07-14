package com.example.next_show.models;

import android.graphics.Movie;
import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.uwetrottmann.trakt5.entities.TrendingShow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Show")
public class Show extends ParseObject implements Parcelable {
    // private instance variables
    private String title;
    private String overview;

    // constants to match keys in Parse Database -> Public because used in other classes
    public static final String KEY_TITLE = "title";
    public static final String KEY_OVERVIEW = "overview";
    public static final String KEY_IMAGE = "image";

    // empty constructor
    public Show() { }

    public Show(String title, String overview) {
        this.title = title;
        this.overview = overview;
    }

    public static List<Show> fromTrendingShows(List<TrendingShow> repsonseShows) {
        List<Show> updated = new ArrayList<>();
        for (TrendingShow trending : repsonseShows) {
            Show currentShow = new Show(trending.show.title, trending.show.overview);
            updated.add(currentShow);
        }
        return updated;
    }

    // for local instance of Show
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    // for Parse instance of Show -> grab saved shows data only
    public String getParseTitle() {
        return getString(KEY_TITLE);
    }

    public void setParseTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getParseOverview() {
        return getString(KEY_OVERVIEW);
    }

    public void setParseOverview(String overview) {
        put(KEY_OVERVIEW, overview);
    }
}
