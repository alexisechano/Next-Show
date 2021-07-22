package com.example.next_show.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.next_show.R;
import com.example.next_show.callbacks.ImageCallback;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

public class ShowDetailFragment extends Fragment {
    private Show currShow;

    // view elements
    private TextView tvDetailTitle;
    private TextView tvDetailOverview;
    private TextView tvYearAndNetwork;
    private TextView tvAlreadyRated;
    private Button btnSaveShow;
    private Button btnLiked;
    private Button btnDisliked;
    private ImageView ivShowPoster;

    // constants
    public static final String TAG = "ShowDetailFragment";
    public static final String LIKED = "liked";
    public static final String DISLIKED = "disliked";
    public static final String KEY_SHOW = "Show";

    private static final String SHOW_DETAIL_URL = "https://api.themoviedb.org/3/tv/";
    private static final String ADD_API_KEY = "?api_key=";
    private static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w342/%s";

    // empty constructor
    public ShowDetailFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // grab from args
        currShow = getArguments().getParcelable(Show.class.getSimpleName());
        Log.i(TAG, "TITLE: " + currShow.getTitle());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View currView = inflater.inflate(R.layout.fragment_details, container, false);

        // set up the view elements and buttons
        setUpView(currView);

        return currView;
    }

    private void setUpView(View currView) {
        // grab current User
        User parseUser = (User) ParseUser.getCurrentUser();

        tvDetailTitle = currView.findViewById(R.id.tvDetailTitle);
        tvDetailOverview = currView.findViewById(R.id.tvDetailOverview);
        tvYearAndNetwork = currView.findViewById(R.id.tvYearAndNetwork);
        btnSaveShow = currView.findViewById(R.id.btnSaveShow);
        btnLiked = currView.findViewById(R.id.btnLiked);
        btnDisliked = currView.findViewById(R.id.btnDisliked);
        ivShowPoster = currView.findViewById(R.id.ivShowPoster);

        // display show text data
        tvDetailTitle.setText(currShow.getTitle());
        tvDetailOverview.setText(currShow.getOverview());

        String yearAndNetwork = currShow.getYearAired() + " | " + currShow.getNetwork();
        tvYearAndNetwork.setText(yearAndNetwork);

        // grab image from API and load into image view
        fetchShowPoster(new DetailImageCallback());

        // check if previous fragment was the SavedFragment -> disable save feature
        if (currShow.isSaved()) {
            btnSaveShow.setVisibility(View.GONE);

            // check if user already rated it, show if user liked it or not
            if (currShow.getUserLiked().equals(LIKED) || currShow.getUserLiked().equals(DISLIKED)) {
                String status = (currShow.getUserLiked().equals(LIKED)) ? LIKED : DISLIKED;
                tvAlreadyRated = currView.findViewById(R.id.tvAlreadyRated);

                // toggle view
                btnLiked.setVisibility(View.GONE);
                btnDisliked.setVisibility(View.GONE);

                tvAlreadyRated.setVisibility(View.VISIBLE);
                tvAlreadyRated.setText("You " + status + " this show!");
            }
        }

        // mark POSITIVE rating
        btnLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRating(LIKED);
            }
        });

        // mark NEGATIVE rating
        btnDisliked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRating(DISLIKED);
            }
        });

        // save the show on button click
        btnSaveShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set parse fields like title and overview
                currShow.setParseFields(parseUser);

                currShow.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while saving", e);
                            Toast.makeText(getActivity(), "Cannot save show!", Toast.LENGTH_SHORT).show();
                        }

                        // add show ID to User list
                        User currentUser = (User) ParseUser.getCurrentUser();

                        if (currShow.getUserLiked().equals(LIKED)) {
                            currentUser.addToLikedShows(currShow.getId());
                        }

                        // if no error, let log and user know
                        Log.i(TAG, "Saved post successfully");
                        Toast.makeText(getActivity(), "Saved show!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void fetchShowPoster(ImageCallback callback) {
        // create HTTP client
        AsyncHttpClient client = new AsyncHttpClient();
        String id = currShow.getId();
        String getUrl = SHOW_DETAIL_URL + id + ADD_API_KEY + getContext().getString(R.string.movie_api_key);

        // call URL and parse through JSON
        client.get(getUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                // call image callback do manipulate json obj
                callback.onSuccess(json);
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                callback.onFailure(i);
            }
        });
    }

    private void updateRating(String rating) {
        currShow.setUserLiked(rating);
        Toast.makeText(getActivity(), "You " + rating + " this show!", Toast.LENGTH_SHORT).show();
        if (currShow.isSaved()) {
            User currentUser = (User) ParseUser.getCurrentUser();
            updateParseRating(rating, currentUser);
        }
    }

    private void updateParseRating(String r, User currentUser) {
        // grab current show's unique Parse object ID
        String objId = currShow.getObjectID();

        Log.i(TAG, "Attempting to update rating for saved show: " + objId);

        // use Parse query
        ParseQuery<ParseObject> query = ParseQuery.getQuery(KEY_SHOW);

        // Retrieve the object by id
        query.getInBackground(objId, new GetCallback<ParseObject>() {
            public void done(ParseObject show, ParseException e) {
                if (e == null) {
                    // log that retrieve in background is done
                    Log.i(TAG, "Updating show rating...");

                    // save rating
                    show.put("userRating", r);
                    show.saveInBackground();

                    // update matching user's saved show list
                    if (r.equals(LIKED)) {
                        currentUser.addToLikedShows(currShow.getId());
                    }

                    Log.i(TAG, "Saved new rating!");
                }
            }
        });
    }

    class DetailImageCallback implements ImageCallback {
        @Override
        public void onSuccess(JsonHttpResponseHandler.JSON json) {
            Log.d(TAG, "onSuccess");

            // get json object
            JSONObject jsonObj = json.jsonObject;

            // get results and save to show object
            try {
                String path = jsonObj.getString("backdrop_path");
                String imgUrl = String.format(BASE_IMAGE_URL, path);

                // display backdrop poster here
                Glide.with(getContext()).load(imgUrl).into(ivShowPoster);

            } catch (JSONException e) {
                Log.e(TAG, "Hit JSON Exception");
            }
        }

        @Override
        public void onFailure(int i) {
            Log.e(TAG, "Error Code: " + i);
        }
    }
}