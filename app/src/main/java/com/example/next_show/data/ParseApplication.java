package com.example.next_show.data;

import android.app.Application;

import com.example.next_show.R;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    // initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        // use keys in strings.xml to initialize Parse here
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());
    }
}
