package com.example.next_show.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;
import java.util.Arrays;
import java.util.List;

@ParseClassName("_User")
public class User extends ParseUser {

    // constants to match keys in Parse Database -> Public because used in Main
    public static final String KEY_FIRSTNAME = "firstName";
    public static final String KEY_LASTNAME = "lastName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_BIO = "bio";
    public static final String KEY_CURRENTLY_WATCHING = "currentlyWatching"; // holds POINTER to show
    public static final String KEY_FAVE_GENRES = "faveGenres";
    public static final String KEY_FORBIDDEN = "forbiddenShows"; // holds TVIDs for DELETED SHOWS
    public static final String KEY_LIKED_SAVED_SHOWS = "likedShows"; // holds SLUGS for LIKED savedShows
    public static final String KEY_ALL_SAVED_SHOWS = "savedShows"; // holds SLUGS for all savedShows

    // empty constructor
    public User() { }

    // Parse User items
    public String getFirstName() {
        return getString(KEY_FIRSTNAME);
    }

    public String getLastName() {
        return getString(KEY_LASTNAME);
    }

    public void setName(String fn, String ln) {
        put(KEY_FIRSTNAME, fn);
        put(KEY_LASTNAME, ln);
    }

    public String getEmail() {
        return getString(KEY_EMAIL);
    }

    public void setEmail(String email) {
        put(KEY_EMAIL, email);
    }

    public String getBio() {
        return getString(KEY_BIO);
    }

    public void setBio(String bio) {
        put(KEY_BIO, bio);
    }

    public void setCurrentlyWatching(Show s) {
        put(KEY_CURRENTLY_WATCHING, s);
        saveInBackground();
    }

    public List<String> getFaveGenres() {
        return getList(KEY_FAVE_GENRES);
    }

    public void setFaveGenres(List<String> genres){
        addAllUnique(KEY_FAVE_GENRES, genres);
        saveInBackground();
    }

    public List<String> getLikedSavedShows() {
        return getList(KEY_LIKED_SAVED_SHOWS);
    }

    public void addToLikedShows(String s){
        add(KEY_LIKED_SAVED_SHOWS, s);
        saveInBackground();
    }

    public List<String> getAllSavedShows() {
        return getList(KEY_ALL_SAVED_SHOWS);
    }

    public void addToSavedShows(String s){
        add(KEY_ALL_SAVED_SHOWS, s);
        saveInBackground();
    }

    public List<String> getForbiddenShows() {
        return getList(KEY_FORBIDDEN);
    }

    public void addToForbiddenShows(String s){
        add(KEY_FORBIDDEN, s);
        saveInBackground();
    }

    public void removeFromForbidden(String s){
        removeAll(KEY_FORBIDDEN, Arrays.asList(s));
        saveInBackground();
    }
}
