package com.example.next_show.data;

import android.util.Log;

import com.example.next_show.models.Show;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ShowFilter {
    // instance vars
    private HashMap<String, List<String>> filters;
    private List<Show> allShows; // contains all shows from Trakt, THIS IS NOT FILTERED
    private List<Show> showsList;

    // genres for shows
    public static final String ACTION = "action";
    public static final String COMEDY = "comedy";
    public static final String DRAMA = "drama";
    public static final String ALL_SHOWS = "all";

    // filters for network
    public static final String CABLE = "cable";
    public static final String STREAMING = "streaming";
    public static final List<String> STREAMING_SERVICES =
            Arrays.asList("Netflix", "Hulu", "Disney+", "HBOMax", "Peacock", "Amazon", "Apple TV+");

    // filters for year
    public static final String THIS_YEAR = "2021";
    public static final String PAST_FIVE = "2016 - 2020";
    public static final String PRIOR = "< 2015";

    public static final int CURRENT_YEAR = 2021;
    public static final int PAST_YEAR = 2020;
    public static final int PRIOR_YEAR = 2015;

    // filters
    public static final String GENRE = "genre";
    public static final String NETWORK = "network";
    public static final String YEAR = "year"; // year aired!

    public ShowFilter(List<Show> showsList) {
        // when new Filterer is init, set it up
        this.showsList = showsList;
        setUpFilters();
    }

    public void setUpFilters() {
        filters = new HashMap<>();

        // initialize dictionary with keys and blank lists
        filters.put(GENRE, new ArrayList<>());
        filters.put(NETWORK, new ArrayList<>());
        filters.put(YEAR, new ArrayList<>());

        // clear shows
        showsList.clear();
    }

    public HashMap<String, List<String>> getFilters(){
        return filters;
    }

    public void addToGenre(String g) {
        filters.get(GENRE).add(g);
    }

    public void addToNetwork(String n) {
        filters.get(NETWORK).add(n);
    }

    public void addToYear(String y) {
        filters.get(YEAR).add(y);
    }

    public void removeFromGenre(String g) {
        filters.get(GENRE).remove(g);
    }

    public void removeFromNetwork(String n) {
        filters.get(NETWORK).remove(n);
    }

    public void removeFromYear(String y) {
        filters.get(YEAR).remove(y);
    }

    public void updateShowsList(List<Show> newShows) {
        showsList.addAll(newShows);
    }

    public void setShowsList(List<Show> newShows) {
        allShows = newShows;
    }

    public List<Show> getAllShows() {
        return allShows;
    }

    public List<Show> filterShows(List<Show> shows) {
        List<Show> filteredShows = shows;

        // filter by network (if appplicable)
        List<String> networkQueries = filters.get(NETWORK);
        filteredShows = filterNetwork(filteredShows, networkQueries);

        // filter by genre (if appplicable)
        List<String> genreQueries = filters.get(GENRE);
        filteredShows = filterGenre(filteredShows, genreQueries);

        // filter by year (if appplicable)
        List<String> yearQueries = filters.get(YEAR);
        filteredShows = filterYear(filteredShows, yearQueries);

        return filteredShows;
    }

    private List<Show> filterYear(List<Show> current, List<String> queries) {
        if (queries.isEmpty()) {
            return current;
        }

        List<Show> filterByYear = new ArrayList<>();
        for (Show s : current) {
            int yearAired = s.getYearAired();
            if (yearAired >= CURRENT_YEAR && queries.contains(THIS_YEAR)) {
                filterByYear.add(s);
            } else if (yearAired <= PAST_YEAR && yearAired > PRIOR_YEAR && queries.contains(PAST_FIVE)) {
                filterByYear.add(s);
            } else if (yearAired <= PRIOR_YEAR && queries.contains(PRIOR)) {
                filterByYear.add(s);
            }
        }
        return filterByYear;
    }

    private List<Show> filterNetwork(List<Show> current, List<String> queries) {
        if (queries.isEmpty()) {
            return current;
        }

        List<Show> filterByNetwork = new ArrayList<>();
        for (Show s: current) {
            String showNetwork = s.getNetwork();

            // if any network match the queries
            if (queries.contains(STREAMING) && STREAMING_SERVICES.contains(showNetwork)) {
                filterByNetwork.add(s);
            } else if (queries.contains(NETWORK) && !STREAMING_SERVICES.contains(showNetwork)) {
                filterByNetwork.add(s);
            }
        }
        return filterByNetwork;

    }

    public List<Show> filterGenre(List<Show> current, List<String> queries) {
        if (queries.isEmpty()) {
            return current;
        }

        // reuse code from recClient to determine intersection of genres
        List<Show> filterByGenre = RecommendationClient.getGenreMatch(current, queries);
        return filterByGenre;
    }
}
