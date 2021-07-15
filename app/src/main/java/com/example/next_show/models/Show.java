package com.example.next_show.models;

import android.graphics.Movie;
import android.os.Parcelable;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
import com.example.next_show.R;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.uwetrottmann.trakt5.entities.TrendingShow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

@ParseClassName("Show")
public class Show extends ParseObject implements Parcelable {
    // private instance variables
    private String title;
    private String overview;
    private String id;
    private String network;
    private String liked;
    private int year_aired;

    // constants to match keys in Parse Database -> Public because used in other classes
    public static final String KEY_TITLE = "title";
    public static final String KEY_OVERVIEW = "overview";
    public static final String KEY_ID = "tmdb";
    public static final String KEY_NETWORK = "network";
    public static final String KEY_YEAR_AIRED = "yearAired";
    public static final String KEY_USER = "user";
    public static final String KEY_RATING = "userRating";
    public static final String KEY_IMAGE = "image"; // not used yet until API call is fixed

    // empty constructor
    public Show() { }

    public Show(String title, String overview, String id, String network, int year_aired) {
        this.title = title;
        this.overview = overview;
        this.id = id;
        this.network = network;
        this.year_aired = year_aired;
    }

    // from Trending Show objects to NextShow Show objects
    public static List<Show> fromTrendingShows(List<TrendingShow> repsonseShows) {
        List<Show> updated = new ArrayList<>();
        for (TrendingShow trending : repsonseShows) {
            Show currentShow = new Show(trending.show.title, trending.show.overview,
                    "" + trending.show.ids.tmdb, trending.show.network, trending.show.first_aired.getYear());

            updated.add(currentShow);
        }
        return updated;
    }

    // from Parse Show objects to NextShow Show objects
    public static List<Show> fromParseShows(List<Show> parseShows) {
        List<Show> updated = new ArrayList<>();

        // turn them into my own Show objects for use later on
        for(Show pre: parseShows){
            Show currentShow = new Show(pre.getParseTitle(), pre.getParseOverview(), pre.getParseTVID(),
                    pre.getParseNetwork(), pre.getParseYearAired());

            currentShow.setUserLiked(pre.getParseUserLiked());
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

    // no setter for these, only constructor can set

    public String getId() {
        return id;
    }

    public String getNetwork() {
        return network;
    }

    public int getYearAired() { return year_aired; }

    public String getUserLiked() { return liked; }

    public void setUserLiked(String liked) { this.liked = liked; }


    // **** for Parse instance of Show -> grab saved shows data only ****
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

    public String getParseTVID() {
        return getString(KEY_ID);
    }

    public void setParseTVID(String id) {
        put(KEY_ID, id);
    }

    public String getParseNetwork() {
        return getString(KEY_NETWORK);
    }

    public void setParseNetwork(String network) {
        put(KEY_NETWORK, network);
    }

    public int getParseYearAired() {
        return getInt(KEY_YEAR_AIRED);
    }

    public void setParseYearAired(int year) {
        put(KEY_YEAR_AIRED, year);
    }

    public String getParseUserLiked() {
        return getString(KEY_RATING);
    }

    public void setParseUserLiked(String liked) {
        put(KEY_RATING, liked);
    }

    public ParseUser getParseUser() {
        return getParseUser(KEY_USER);
    }

    public void setParseUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setParseFields(ParseUser currUser) {
        // before saving, save these as Parse attributes
        setParseTitle(title);
        setParseOverview(overview);
        setParseTVID(id);
        setParseNetwork(network);
        setParseYearAired(year_aired);
        setParseUser(currUser);
        setParseUserLiked(liked);
    }

    // TODO: Add image file for Parse
}
