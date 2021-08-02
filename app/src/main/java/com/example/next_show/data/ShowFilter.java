package com.example.next_show.data;
import com.example.next_show.models.Show;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShowFilter {
    // instance vars
    private HashMap<String, Set<String>> filters;
    private List<Show> allShows; // contains all shows from Trakt, THIS IS NOT FILTERED

    // genres for shows
    public static final String ACTION = "action";
    public static final String COMEDY = "comedy";
    public static final String DRAMA = "drama";

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

    public ShowFilter() {
        // set up shows list and filters
        this.allShows = new ArrayList<>();
        setFilters();
    }

    public void setFilters() {
        // init a new hashmap
        filters = new HashMap<>();

        // initialize dictionary with keys and blank lists
        filters.put(GENRE, new HashSet<String>());
        filters.put(NETWORK, new HashSet<String>());
        filters.put(YEAR, new HashSet<String>());
    }

    public HashMap<String, Set<String>> getFilters(){
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

    public void addAll(List<Show> newShows) {
        allShows.addAll(newShows);
    }

    public List<Show> getAllShows() {
        return allShows;
    }

    public void clear() {
        allShows.clear();
    }

    public List<Show> filterShows(List<Show> shows) {
        // filter by genre first
        Set<String> genreQueries = filters.get(GENRE);
        List<Show> genreMatchedShows  = getGenreMatch(shows, genreQueries);

        if(genreMatchedShows.isEmpty()) {
            genreMatchedShows = shows;
        }

        // queries for other attributes
        Set<String> networkQueries = filters.get(NETWORK);
        Set<String> yearQueries = filters.get(YEAR);

        List<Show> filteredShows = new ArrayList<>();

        for (Show s: genreMatchedShows) {
            // check network and year
            if (matchedNetworkFilter(s, networkQueries) && matchedYearFilter(s, yearQueries)) {
                filteredShows.add(s);
            }
        }

        return filteredShows;
    }

    private boolean matchedYearFilter(Show current, Set<String> years) {
        if (years.isEmpty()) {
            return true;
        }

        int yearAired = current.getYearAired();
        if (yearAired >= CURRENT_YEAR && years.contains(THIS_YEAR)) {
            return true;
        } else if (yearAired <= PAST_YEAR && yearAired > PRIOR_YEAR && years.contains(PAST_FIVE)) {
            return true;
        } else if (yearAired <= PRIOR_YEAR && years.contains(PRIOR)) {
            return true;
        }
        return false;
    }

    private boolean matchedNetworkFilter(Show current, Set<String> networks) {
        if (networks.isEmpty()) {
            return true;
        }

        String showNetwork = current.getNetwork();

        // if any network match the queries
        if (networks.contains(STREAMING) && STREAMING_SERVICES.contains(showNetwork)) {
            return true;
        } else if (networks.contains(CABLE) && !(STREAMING_SERVICES.contains(showNetwork))) {
            return true;
        }

        return false;
    }

    public static List<Show> getGenreMatch(List<Show> shows, Set<String> compareGenres){
        // init return list
        List<Show> genreMatched = new ArrayList<>();

        // check if we need to even compare
        if (compareGenres.isEmpty()) {
            return genreMatched;
        }

        for (Show s: shows) {
            Set<String> showGenres = new HashSet<String>(s.getGenres());
            showGenres.addAll(s.getGenres());

            // if any genre(s) match the comparison ones or if set intersection is not empty
            showGenres.retainAll(compareGenres);
            if (!showGenres.isEmpty()) {
                genreMatched.add(s);
            }
        }

        return genreMatched;
    }
}
