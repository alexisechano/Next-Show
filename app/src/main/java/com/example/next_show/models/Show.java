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
    public static final String KEY_GENRES = "genres"; // list
    public static final String KEY_IMAGE = "imageUrl"; // TODO: not used yet until API call is fixed

    // empty constructor
    public Show() { }

    public Show(String title, String overview, String id, String network, int year_aired, List<String> genres) {
        this.title = title;
        this.overview = overview;
        this.id = id;
        this.network = network;
        this.year_aired = year_aired;
        this.genres = genres;
        this.isSaved = false; // default is that this new Show object is not saved in Parse
    }

    // from Trending Show objects to NextShow Show objects
    public static List<Show> formatTrendingShows(List<TrendingShow> response) {
        List<Show> updatedShows = new ArrayList<>();
        for (TrendingShow trendingShow : response) {
            Show currentShow = new Show(trendingShow.show.title, trendingShow.show.overview,
                    "" + trendingShow.show.ids.tmdb, trendingShow.show.network, trendingShow.show.first_aired.getYear(), trendingShow.show.genres);
            updatedShows.add(currentShow);
        }
        return updatedShows;
    }

    // from Recommended Show objects to NextShow Show objects
    public static List<Show> formatShows(List<com.uwetrottmann.trakt5.entities.Show> repsonseShows) {
        List<Show> updatedShows = new ArrayList<>();
        for (com.uwetrottmann.trakt5.entities.Show show : repsonseShows) {
            Show currentShow = new Show(show.title, show.overview,
                    "" + show.ids.tmdb, show.network, show.first_aired.getYear(), show.genres);
            updatedShows.add(currentShow);
        }
        return updatedShows;
    }

    // from Parse Show objects to NextShow Show objects
    public static List<Show> fromParseShows(List<Show> parseShows) {
        List<Show> updatedShows = new ArrayList<>();

        // turn them into my own Show objects for use later on
        for(Show parseShow: parseShows){
            Show currentShow = new Show(parseShow.getParseTitle(), parseShow.getParseOverview(), parseShow.getParseTVID(),
                    parseShow.getParseNetwork(), parseShow.getParseYearAired(), parseShow.getParseGenres());

            // for ratings and updating them later
            currentShow.setObjectID(parseShow.getObjectId());
            currentShow.setUserLiked(parseShow.getParseUserLiked());
            currentShow.setSavedStatus(true);
            updatedShows.add(currentShow);
        }
        return updatedShows;
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
        this.imageUrl = String.format("https://image.tmdb.org/t/p/w342/%s", p);
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
        setParseImage(imageUrl); // make sure this is not null
        setParseUser(currUser);

        // null check before adding to Parse since Parse does not accept null strings
        if(liked == null){
            Log.e("Show", "No rating available");
            liked = "";
        }
        setParseUserLiked(liked);
        setParseGenres(genres);
    }
}
