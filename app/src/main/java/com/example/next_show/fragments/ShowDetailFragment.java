package com.example.next_show.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.next_show.MainActivity;
import com.example.next_show.R;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ShowDetailFragment extends Fragment {
    private Show currShow;

    // view elements
    private TextView tvDetailTitle;
    private TextView tvDetailOverview;
    private TextView tvYearAndNetwork;
    private TextView tvRatingTitle;
    private Button btnSaveShow;
    private Button btnLiked;
    private Button btnDisliked;
    private Button btnYes;
    private ChipGroup genreChipGroup;
    private ImageView ivShowPoster;

    // constants
    public static final String TAG = "ShowDetailFragment";
    public static final String LIKED = "liked";
    public static final String DISLIKED = "disliked";
    public static final String KEY_SHOW = "Show";
    public static final String PARSE_RATING_KEY = "userRating";
    public static final int MAX_CHIPS = 3;

    // empty constructor
    public ShowDetailFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // grab parceled show object from args
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
        genreChipGroup = currView.findViewById(R.id.genreChipGroup);
        btnYes = currView.findViewById(R.id.btnYes);

        // display show text data
        tvDetailTitle.setText(currShow.getTitle());
        tvDetailOverview.setText(currShow.getOverview());

        String yearAndNetwork = currShow.getYearAired() + " | " + currShow.getNetwork();
        tvYearAndNetwork.setText(yearAndNetwork);

        // put into imageview -> if not loaded yet, this will be blank but loads properly if clicked again
        Glide.with(getContext()).load(currShow.getImageUrl()).into(ivShowPoster);

        // go through show genres and add chips
        List<String> showGenres = currShow.getGenres();
        int genreChipCount = 1;
        for (String g: showGenres) {
            // only put 3 genres max (formatting constraint)
            if (genreChipCount <= MAX_CHIPS) {
                addChip(g);
                genreChipCount++;
            } else {
               break;
            }
        }

        // check if previous fragment was the SavedFragment -> disable save feature
        if (currShow.isSaved()) {
            btnSaveShow.setVisibility(View.GONE);

            // local view vars to toggle visible or gone
            tvRatingTitle = currView.findViewById(R.id.tvRatingTitle);
            TextView tvCurrentWatch = currView.findViewById(R.id.tvCurrentWatch);
            RelativeLayout currentWatchBar = currView.findViewById(R.id.currentWatchBar);

            // retrieve user liked status
            String likedStatus = currShow.getUserLiked();

            // check if user already rated it, show if user liked it or not
            if (likedStatus != null && (likedStatus.equals(LIKED) || likedStatus.equals(DISLIKED))) {
                String status = (currShow.getUserLiked().equals(LIKED)) ? LIKED : DISLIKED;

                // toggle view, remove liked and disliked if rated already
                btnLiked.setVisibility(View.GONE);
                btnDisliked.setVisibility(View.GONE);

                tvRatingTitle.setVisibility(View.VISIBLE);
                tvRatingTitle.setText("You " + status + " this show!");
            } else {
                btnLiked.setVisibility(View.GONE);
                btnDisliked.setVisibility(View.GONE);

                tvRatingTitle.setText("Already saved this show, click Saved tab!");
                tvRatingTitle.setTypeface(tvRatingTitle.getTypeface(), Typeface.BOLD);
            }

            // make currently watching option available
            currentWatchBar.setVisibility(View.VISIBLE);
            tvCurrentWatch.setVisibility(View.VISIBLE);
            btnYes.setVisibility(View.VISIBLE);

            // set as currently watching for user
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateCurrentlyWatching(parseUser);
                }
            });
        }

        // mark POSITIVE rating
        btnLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRating(LIKED, parseUser);
            }
        });

        // mark NEGATIVE rating
        btnDisliked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRating(DISLIKED, parseUser);
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
                            MainActivity.showToast("Cannot save show!", getActivity());
                        }

                        // add show ID to User list
                        User currentUser = (User) ParseUser.getCurrentUser();

                        if (currShow.getUserLiked().equals(LIKED)) {
                            currentUser.addToLikedShows(currShow.getSlug());
                        }

                        // add to general list of saved shows
                        currentUser.addToSavedShows(currShow.getSlug());

                        // if no error, let log and user know
                        Log.i(TAG, "Saved post successfully");
                        MainActivity.showToast("Saved show!", getActivity());
                    }
                });
            }
        });
    }

    private void addChip(String g) {
        Chip currentGenre = new Chip(getContext());
        currentGenre.setText(g);
        currentGenre.setChipBackgroundColorResource(R.color.theme_color);
        currentGenre.setTextColor(getResources().getColor(R.color.white));
        currentGenre.setGravity(Gravity.CENTER_HORIZONTAL);

        // add to chip group
        genreChipGroup.addView(currentGenre);
    }

    private void updateRating(String rating, User currentUser) {
        currShow.setUserLiked(rating);
        MainActivity.showToast("You " + rating + " this show!", getActivity());
        if (currShow.isSaved()) {
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
                    show.put(PARSE_RATING_KEY, r);
                    show.saveInBackground();

                    // update matching user's saved show list
                    if (r.equals(LIKED)) {
                        currentUser.addToLikedShows(currShow.getSlug());
                    }

                    Log.i(TAG, "Saved new rating!");
                    MainActivity.showToast("Updated the rating!", getActivity());
                }
            }
        });
    }

    private void updateCurrentlyWatching(User currentUser) {
        // grab current show's unique Parse object ID
        String objId = currShow.getObjectID();

        Log.i(TAG, "Attempting set this saved show: " + objId);

        // use Parse query
        ParseQuery<Show> query = ParseQuery.getQuery(Show.class);

        // Retrieve the object by id
        query.getInBackground(objId, new GetCallback<Show>() {
            public void done(Show show, ParseException e) {
                if (e == null) {
                    currentUser.setCurrentlyWatching(show);

                    Log.i(TAG, "Saved new show as currently watching!");
                    MainActivity.showToast("Set as currently watching!", getActivity());
                }
            }
        });
    }
}