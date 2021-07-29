package com.example.next_show.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ShowFilterer {
    // instance vars
    private HashMap<String, List<String>> filters;

    // genres for shows
    private static final String ACTION = "action";
    private static final String COMEDY = "comedy";
    private static final String DRAMA = "drama";
    private static final String ALL_SHOWS = "all";

    // filters
    public static final String GENRE = "genre";
    public static final String NETWORK = "network";
    public static final String YEAR = "year";

    public ShowFilterer () {

    }

    private HashMap<String, List<String>> setUpFilters() {
        HashMap<String, List<String>> filterDict = new HashMap<>();

        // populate the dictionary
        filterDict.put(GENRE, Arrays.asList(ALL_SHOWS, ACTION, COMEDY, DRAMA));
        filterDict.put(NETWORK, Arrays.asList("streaming", "cable"));
        filterDict.put(YEAR, Arrays.asList("<2010", "2011 - 2015", "2016 - 2021"));

        return filterDict;
    }

    public HashMap<String, List<String>> getFilters(){
        return filters;
    }
}
