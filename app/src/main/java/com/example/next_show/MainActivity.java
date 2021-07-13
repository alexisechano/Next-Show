package com.example.next_show;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.next_show.data.TraktClient;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.parse.ParseUser;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.TrendingShow;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.services.Shows;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Headers;
import okhttp3.Request;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    // constants
    public static final String TAG = "MainActivity";
    List<Show> shows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // test if Parse user database is working with User subclass
        getNewUserInfo();
    }


    private void getNewUserInfo() {
        User parseUser = new User(ParseUser.getCurrentUser());
    }

}