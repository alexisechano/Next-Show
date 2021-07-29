package com.example.next_show.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ShowFilterer {
    // instance vars
    private HashMap<String, List<String>> filters;

    // genres for shows
    public static final String ACTION = "action";
    public static final String COMEDY = "comedy";
    public static final String DRAMA = "drama";
    public static final String ALL_SHOWS = "all";

    // filters
    public static final String GENRE = "genre";
    public static final String NETWORK = "network";
    public static final String YEAR = "year";

    public ShowFilterer () {

    }

    private HashMap<String, List<String>> setUpFilters() {
        HashMap<String, List<String>> filterDict = new HashMap<>();

        // initialize dictionary with keys and blank lists
        filterDict.put(GENRE, new ArrayList<>());
        filterDict.put(NETWORK, new ArrayList<>());
        filterDict.put(YEAR, new ArrayList<>());

        return filterDict;
    }

    public HashMap<String, List<String>> getFilters(){
        return filters;
    }
}
