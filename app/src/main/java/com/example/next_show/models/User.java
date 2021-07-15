package com.example.next_show.models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {
    // ParseUser object
    private ParseUser user;

    // constants to match keys in Parse Database -> Public because used in Main
    public static final String KEY_FIRSTNAME = "firstName";
    public static final String KEY_LASTNAME = "lastName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_BIO = "bio";
    //public static final String KEY_SAVED = "savedShows"; // this is an array

    // empty constructor
    public User() { }

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

    public ParseUser useParseUser(){
        return user;
    }
}
