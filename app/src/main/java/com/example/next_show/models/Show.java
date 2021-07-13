package com.example.next_show.models;

import android.graphics.Movie;

import com.parse.ParseClassName;

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
    private JSONObject showObj;

    // empty constructor for Parcel
    public Show() { }

    // create constructor
    public Show(JSONObject jsonObject) throws JSONException {
        showObj = jsonObject.getJSONObject("show");
        name = showObj.getString("title");
    }

    // create a func to load array
    public static List<Show> fromJSONArray(JSONArray showJSONArray) throws JSONException {
        List<Show> shows = new ArrayList<>();
        for(int i = 0; i < showJSONArray.length(); i++){
            shows.add(new Show(showJSONArray.getJSONObject(i)));
        }
        return shows;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
