package com.example.next_show.models;

import android.graphics.Movie;
import android.util.Log;

import com.parse.ParseClassName;
import com.uwetrottmann.trakt5.entities.TrendingShow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Show")
public class Show {
    // private instance variables
    private String name;
    private String overview;

    // empty constructor
    public Show() { }

    public Show(String name, String overview) {
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
