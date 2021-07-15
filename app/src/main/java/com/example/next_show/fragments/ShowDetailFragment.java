package com.example.next_show.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import androidx.fragment.app.FragmentManager;

import com.example.next_show.MainActivity;
import com.example.next_show.R;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ShowDetailFragment extends Fragment {
    private Show currShow;
    private boolean alreadySaved;

    // view elements
    private TextView tvDetailTitle;
    private TextView tvDetailOverview;
    private TextView tvYearAndNetwork;
    private TextView tvAlreadyRated;
    private Button btnSaveShow;
    private Button btnLiked;
    private Button btnDisliked;

    private RelativeLayout rlRatingBar;

    //private TextView tvDetailRating; // TODO: update show object to include this later (not my rating)

    // constants
    public static final String TAG = "ShowDetailFragment";

    // empty constructor
    public ShowDetailFragment(){ }

    public static ShowDetailFragment newInstance(Show show, boolean alreadySaved) {
        ShowDetailFragment fragment = new ShowDetailFragment();

        // generate bundle for the show
        Bundle bundle = new Bundle();
        bundle.putParcelable(Show.class.getSimpleName(), show);
        bundle.putBoolean("savedBool", alreadySaved);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // grab from args
        currShow = getArguments().getParcelable(Show.class.getSimpleName());
        Log.i(TAG, "TITLE: " + currShow.getTitle());

        this.alreadySaved = getArguments().getBoolean("savedBool");
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
        ParseUser parseUser = ParseUser.getCurrentUser();

        tvDetailTitle = currView.findViewById(R.id.tvDetailTitle);
        tvDetailOverview = currView.findViewById(R.id.tvDetailOverview);
        tvYearAndNetwork = currView.findViewById(R.id.tvYearAndNetwork);
        btnSaveShow = currView.findViewById(R.id.btnSaveShow);
        btnLiked = currView.findViewById(R.id.btnLiked);
        btnDisliked = currView.findViewById(R.id.btnDisliked);
        rlRatingBar = currView.findViewById(R.id.ratingsBar);

        // change color to denote difference
        btnDisliked.setBackgroundColor(Color.parseColor("#d6d3d2"));

        // display show text data
        tvDetailTitle.setText(currShow.getTitle());
        tvDetailOverview.setText(currShow.getOverview());

        String yearAndNetwork = currShow.getYearAired() + " | " + currShow.getNetwork();
        tvYearAndNetwork.setText(yearAndNetwork);

        // check if previous fragment was the SavedFragment -> disable save feature
        if(alreadySaved){
            btnSaveShow.setVisibility(View.GONE);

            // check if user already rated it
            if (currShow.getUserLiked() != null) {
                String status = (currShow.getUserLiked().equals("liked")) ? "liked" : "disliked";
                tvAlreadyRated = currView.findViewById(R.id.tvAlreadyRated);

                // toggle view
                btnLiked.setVisibility(View.GONE);
                btnDisliked.setVisibility(View.GONE);

                tvAlreadyRated.setVisibility(View.VISIBLE);
                tvAlreadyRated.setText("You " + status + " this show!");
            }
        }

        // set up the buttons for onclicks and saving
        // mark POSITIVE rating
        btnLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currShow.setUserLiked("liked");
                Toast.makeText(getActivity(), "You liked this show!", Toast.LENGTH_LONG).show();
                if(alreadySaved){
                    // TODO: update Parse data with new rating
                }
            }
        });

        // mark NEGATIVE rating
        btnDisliked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currShow.setUserLiked("disliked");
                Toast.makeText(getActivity(), "You disliked this show!", Toast.LENGTH_LONG).show();
                if(alreadySaved){
                    // TODO: update Parse data with new rating
                }
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
                        if(e != null){
                            Log.e(TAG, "Error while saving", e);
                            Toast.makeText(getActivity(), "Cannot save show!", Toast.LENGTH_SHORT).show();
                        }

                        // if no error, let log and user know
                        Log.i(TAG, "Saved post successfully");
                        Toast.makeText(getActivity(), "Saved show!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

}
