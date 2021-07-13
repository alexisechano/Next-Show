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

    // empty constructor
    public Show() { }

    public Show(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
