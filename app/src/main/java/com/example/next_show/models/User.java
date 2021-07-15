package com.example.next_show.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {
    // ParseUser object
    private ParseUser user;

    // local array to keep track of saved shows
    private List<Show> savedShows;
    private boolean saved;

    // constants to match keys in Parse Database -> Public because used in Main
    public static final String KEY_FIRSTNAME = "firstName";
    public static final String KEY_LASTNAME = "lastName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_BIO = "bio";
    public static final String KEY_SAVED = "savedShows"; // this is an array

    // to correctly refer to User as a ParseUser
    public User(ParseUser parseUser) {
        user = parseUser;
    }

    // Parse User items
    public void setUsername(String username) {
        user.setUsername(username);
    }

    public String getUsername() {
        return user.getUsername();
    }

    public void setPassword(String pw) {
        // no getter method for security reasons
        user.setPassword(pw);
    }

    public String getFirstName() {
        return user.getString(KEY_FIRSTNAME);
    }

    public String getLastName() {
        return user.getString(KEY_LASTNAME);
    }

    public void setName(String fn, String ln) {
        user.put(KEY_FIRSTNAME, fn);
        user.put(KEY_LASTNAME, ln);
    }

    public String getEmail() {
        return user.getString(KEY_EMAIL);
    }

    public void setEmail(String email) {
        user.put(KEY_EMAIL, email);
    }

    public String getBio() {
        return user.getString(KEY_BIO);
    }

    public void setBio(String bio) {
        user.put(KEY_BIO, bio);
    }

    public void createSavedList () {
        savedShows = new ArrayList<>();
    }

    public void addToLocalSaved(Show show) {
        savedShows.add(show);
        saved = false;
    }

    public void setSaved() {
        saved = true;
    }

    public boolean hasSavedShows() {
        return saved;
    }

    public List<Show> getLocalSaved() {
       return savedShows;
    }

    // TODO: ensure that these are actual show objects being sent back
    public List<Show> getSavedShows() {
        return user.getList(KEY_SAVED);
    }

    public void addToSavedShows(List<Show> shows) {
        // batch saving
        user.addAllUnique(KEY_SAVED, shows);
        user.saveInBackground();
    }

    public ParseUser useParseUser(){
        return user;
    }
}
