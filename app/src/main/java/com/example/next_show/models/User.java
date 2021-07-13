package com.example.next_show.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

public class User {
    // ParseUser object
    private ParseUser user;

    // constants to match keys in Parse Database -> Public because used in Main
    public static final String KEY_FIRSTNAME = "firstName";
    public static final String KEY_LASTNAME = "lastName";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_BIO = "bio";

    // to correctly refer to User as a ParseUser
    public User(ParseUser parseUser) {
        user = parseUser;
    }

}
