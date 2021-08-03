package com.example.next_show.models;

import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.uwetrottmann.trakt5.entities.TrendingShow;
import java.util.ArrayList;
import java.util.List;

@ParseClassName("Show")
public class Show extends ParseObject implements Parcelable {
    // private instance variables
    private String title;
    private String overview;
    private String id;
    private String objectId;
    private String network;
    private String liked;
    private String imageUrl;
    private String slug;
    private int year_aired;
    private boolean isSaved;
    private List<String> genres;

    // constants to match keys in Parse Database -> Public because used in other classes
    public static final String KEY_TITLE = "title";
    public static final String KEY_OVERVIEW = "overview";
    public static final String KEY_ID = "tmdb";
    public static final String KEY_NETWORK = "network";
    public static final String KEY_YEAR_AIRED = "yearAired";
    public static final String KEY_USER = "user";
    public static final String KEY_RATING = "userRating";
    public static final String KEY_SLUG = "slug";
    public static final String KEY_GENRES = "genres";
    public static final String KEY_IMAGE = "imageUrl";

    // empty constructor
    public Show() { }

    public Show(String title, String overview, String id, String network, int year_aired,
                List<String> genres, String slug) {
        this.title = title;
        this.overview = overview;
        this.id = id;
        this.network = network;
        this.year_aired = year_aired;
        this.genres = genres;
        this.slug = slug;
        this.isSaved = false; // default is that this new Show object is not saved in Parse
    }

    // from Trending Show objects to NextShow Show objects
    public static List<Show> formatTrendingShows(List<TrendingShow> response) {
        List<Show> updatedShows = new ArrayList<>();
        for (TrendingShow trendingShow : response) {
            // grab showID to check if in forbidden shows
            String showID = String.valueOf(trendingShow.show.ids.tmdb);
            String showSlug = String.valueOf(trendingShow.show.ids.slug);

            if (!isForbiddenShow(showID)) {
                Show currentShow = new Show(trendingShow.show.title, trendingShow.show.overview,
                        showID, trendingShow.show.network, trendingShow.show.first_aired.getYear(),
                        trendingShow.show.genres, showSlug);

                // for labelling on feedfragment
                if (isSaved(showSlug)) {
                    currentShow.setSavedStatus(true);
                }

                updatedShows.add(currentShow);
            }
        }
        return updatedShows;
    }

    // from Recommended Show objects to NextShow Show objects
    public static List<Show> formatShows(List<com.uwetrottmann.trakt5.entities.Show> repsonseShows) {
        List<Show> updatedShows = new ArrayList<>();
        for (com.uwetrottmann.trakt5.entities.Show show : repsonseShows) {
            // grab showID to check if in forbidden shows
            String showID = String.valueOf(show.ids.tmdb);
            String showSlug = String.valueOf(show.ids.slug);

            if (!isForbiddenShow(showID)) {
                Show currentShow = new Show(show.title, show.overview, showID, show.network,
                        show.first_aired.getYear(), show.genres, showSlug);

                // for labelling on feedfragment
                if (isSaved(showSlug)) {
                    currentShow.setSavedStatus(true);
                }

                updatedShows.add(currentShow);
            }
        }
        return updatedShows;
    }

    // from Parse Show objects to NextShow Show objects
    public static List<Show> fromParseShows(List<Show> parseShows) {
        List<Show> updatedShows = new ArrayList<>();

        // turn them into my own Show objects for use later on
        for (Show parseShow: parseShows) {
            // grab showID to check if in forbidden shows
            String showID = parseShow.getParseTVID();

            if (!isForbiddenShow(showID)) {
                Show currentShow = new Show(parseShow.getParseTitle(), parseShow.getParseOverview(), showID,
                        parseShow.getParseNetwork(), parseShow.getParseYearAired(), parseShow.getParseGenres(), parseShow.getParseSlug());

                // for ratings and updating them later
                currentShow.setObjectID(parseShow.getObjectId());
                currentShow.setUserLiked(parseShow.getParseUserLiked());
                currentShow.setSavedStatus(true);
                currentShow.setImageUrl(parseShow.getParseImage());

                // add to updated list
                updatedShows.add(currentShow);
            }
        }
        return updatedShows;
    }

    private static boolean isForbiddenShow(String showID) {
        User user = (User) ParseUser.getCurrentUser();
        List<String> forbiddenShows = user.getForbiddenShows();

        // check if user has forbidden shows
        if (forbiddenShows == null || forbiddenShows.isEmpty()) {
            return false;
        }

        return forbiddenShows.contains(showID);
    }

    private static boolean isSaved(String slug) {
        User user = (User) ParseUser.getCurrentUser();
        List<String> savedShows = user.getAllSavedShows();

        // check if user has saved shows -> trivially true
        if (savedShows == null || savedShows.isEmpty()) {
            return true;
        }

        return savedShows.contains(slug);
    }

    // for local instance of Show -> no setter for these, only constructor can set
    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getId() {
        return id;
    }

    public String getNetwork() {
        return network;
    }

    public int getYearAired() { return year_aired; }

    public String getUserLiked() { return liked; }

    public void setUserLiked(String liked) { this.liked = liked; }

    public String getSlug() { return slug; }

    public List<String> getGenres() {
        return genres;
    }

    public String getObjectID() { return objectId; }

    public void setObjectID(String i) {
        this.objectId = i;
    }

    public boolean isSaved() { return isSaved; }

    public void setSavedStatus(Boolean b) {
        this.isSaved = b;
    }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String p) {
        if (p.contains("https")) {
            this.imageUrl = p;
        } else {
            this.imageUrl = String.format("https://image.tmdb.org/t/p/w342/%s", p);
        }
    }

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

    public List<String> getParseGenres() {
        return getList(KEY_GENRES);
    }

    public void setParseGenres(List<String> genres) {
        addAllUnique(KEY_GENRES, genres);
        saveInBackground();
    }

    public String getParseSlug() {
        return getString(KEY_SLUG);
    }

    public void setParseSlug(String s) {
        put(KEY_SLUG, s);
    }

    public String getParseImage() {
        return getString(KEY_IMAGE);
    }

    public void setParseImage(String img) {
        put(KEY_IMAGE, img);
    }

    public ParseUser getParseUser() {
        return getParseUser(KEY_USER);
    }

    public void setParseUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setParseFields(ParseUser currUser) {
        // before saving, set as Parse attributes
        setParseTitle(title);
        setParseOverview(overview);
        setParseTVID(id);
        setParseNetwork(network);
        setParseYearAired(year_aired);
        setParseSlug(slug);
        setParseImage(getImageUrl());
        setParseUser(currUser);

        // null check before adding to Parse since Parse does not accept null strings
        if (liked == null) {
            Log.e("Show", "No rating available");
            liked = "";
        }
        setParseUserLiked(liked);
        setParseGenres(genres);
    }

}
