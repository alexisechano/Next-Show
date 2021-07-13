package com.example.next_show.data;

import android.util.Log;

import com.example.next_show.models.Show;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Headers;

public class TraktClient {
    // constant callback urls
    private static final String TRENDING_SHOWS = "https://api.trakt.tv/shows/trending"; // GET
    private static final String POPULAR_SHOWS = "https://api.trakt.tv/shows/popular"; // GET
    private static final String RETRIEVE_SHOW = "https://api.trakt.tv/shows/"; // GET and + id

    public static final String TAG = "TraktClient";


}

