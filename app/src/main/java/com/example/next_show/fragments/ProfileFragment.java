package com.example.next_show.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.next_show.LoginActivity;
import com.example.next_show.R;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends Fragment {
    // constants
    public static final String TAG = "ProfileFragment";

    // view elements
    private Button btnLogout;
    private TextView tvName;
    private TextView tvUsername;
    private TextView tvBio;
    private TextView tvGenres;
    private ImageView ivProfileImage;

    // currently watching
    private CardView showCardProfile;
    private TextView tvWatching;
    private TextView tvShowTitleProfile;
    private ImageView ivCardImageProfile;

    // empty constructor
    public ProfileFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View currView = inflater.inflate(R.layout.fragment_profile, container, false);

        // set up the view elements
        setUpView(currView);

        return currView;
    }

    private void setUpView(View currView) {
        tvName = currView.findViewById(R.id.tvName);
        tvUsername = currView.findViewById(R.id.tvUsername);
        tvBio = currView.findViewById(R.id.tvBio);
        ivProfileImage = currView.findViewById(R.id.ivProfileImage);
        tvGenres = currView.findViewById(R.id.tvGenres);
        showCardProfile = currView.findViewById(R.id.showCardProfile);
        tvWatching = currView.findViewById(R.id.tvWatching);
        tvShowTitleProfile = currView.findViewById(R.id.tvShowTitleProfile);
        ivCardImageProfile = currView.findViewById(R.id.ivCardImageProfile);

        // set up logout button
        btnLogout = currView.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // load the Parse data for User
        User currUser = (User) ParseUser.getCurrentUser();

        // bind to the Profile views
        String fullName = currUser.getFirstName() + " " + currUser.getLastName();
        tvName.setText(fullName);

        String username = "@" + currUser.getUsername();
        tvUsername.setText(username);

        String bio = currUser.getBio();
        tvBio.setText(bio);

        List<String> faveGenres = currUser.getFaveGenres();
        String genreText = "";

        for (String g : faveGenres) {
            genreText += (g + ", ");
        }

        tvGenres.setText("Favorite Genres: " + genreText.substring(0, genreText.length() - 2));

        // add temp image
        Glide.with(getContext()).load(R.drawable.ic_baseline_account_box_24).into(ivProfileImage);

        // load currently watching show
        getCurrentlyWatching(currUser);
    }

    private void getCurrentlyWatching(User currUser) {
        ParseQuery<Show> query = ParseQuery.getQuery(Show.class);

        // parse object attached to user
        ParseObject pointer = currUser.getParseObject(User.KEY_CURRENTLY_WATCHING);

        // null check just in case user does not have currently watching show
        if (pointer == null) {
            return;
        }

        // get the Show object attached to pointer and load into view
        query.getInBackground(pointer.getObjectId(), new GetCallback<Show>() {
            @Override
            public void done(Show object, ParseException e) {
                if (e == null && object != null) {
                    tvWatching.setVisibility(View.VISIBLE);
                    showCardProfile.setVisibility(View.VISIBLE);

                    // load info into card view
                    Glide.with(getContext()).load(object.getParseImage()).into(ivCardImageProfile);
                    tvShowTitleProfile.setText(object.getParseTitle());
                }
            }
        });
    }

    private void logout(){
        ParseUser.logOut();
        Log.i(TAG, "Logged out user");
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
    }
}
