package com.example.next_show.models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ParseClassName("_User")
public class User extends ParseUser {

    // constants to match keys in Parse Database -> Public because used in Main
    public static final String KEY_FIRSTNAME = "firstName";
    public static final String KEY_LASTNAME = "lastName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_BIO = "bio";
    public static final String KEY_FAVE_GENRES = "faveGenres";

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

    public List<String> getFaveGenres() {
        return getList(KEY_FAVE_GENRES);
    }

    public void setFaveGenres(List<String> genres){
        addAllUnique(KEY_FAVE_GENRES, genres);
        saveInBackground();
    }

    public void addToFaveGenres(String g){
        add(KEY_FAVE_GENRES, g);
        saveInBackground();
    }

}
