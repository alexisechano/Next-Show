package com.example.next_show.models;

import android.graphics.Movie;
import android.os.Parcelable;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
import com.example.next_show.R;
import com.parse.ParseClassName;
import com.parse.ParseObject;
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
    private String imageUrl;
    private String id;

    // constants to match keys in Parse Database -> Public because used in other classes
    public static final String KEY_TITLE = "title";
    public static final String KEY_OVERVIEW = "overview";
    public static final String KEY_IMAGE = "image";

    // url constant
    private static final String SHOW_DETAIL_URL = "https://api.themoviedb.org/3/tv/";
    private static final String ADD_API_KEY = "?api_key=";

    // empty constructor
    public Show() { }

    public Show(String title, String overview, String id) {
        this.title = title;
        this.overview = overview;
        this.id = id;
    }

    public static List<Show> fromTrendingShows(List<TrendingShow> repsonseShows) {
        List<Show> updated = new ArrayList<>();
        for (TrendingShow trending : repsonseShows) {
            Show currentShow = new Show(trending.show.title, trending.show.overview, "" + trending.show.ids.tmdb);
            updated.add(currentShow);
        }

        // for each show, add image url
        for(Show s: updated){
            Show.updateShowDetails(s);
            System.out.println(s.title + " URL: " + s.imageUrl);
        }

        return updated;
    }

    // might be a temp place to put this -> not sure if it is good to call async from here
    public static void updateShowDetails(Show currShow) {

        // url to grab the details
        String apiKey = "11e01d0f4b98d95d68797db14128bb0a";
        String id = currShow.getId();
        String url = SHOW_DETAIL_URL + id + ADD_API_KEY + apiKey;

        // create HTTP client
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null /* no params */, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // get json object
                JSONObject jsonObj = json.jsonObject;
                try {
                    String imgUrl = jsonObj.getString("poster_path");
                    currShow.setImageUrl(imgUrl);
                } catch (JSONException e) {
                    Log.e("ShowJson", "Failed to grab poster path", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String errorResponse, Throwable t) {
                Log.e("ShowObject", "Error Code: " + statusCode, t);
            }
        });
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

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = String.format("https://image.tmdb.org/t/p/w342/%s", imageUrl);
    }

    // for Parse instance of Show -> grab saved shows data only
    // TODO: Add image file for Parse
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
