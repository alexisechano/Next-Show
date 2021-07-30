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

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.next_show.LoginActivity;
import com.example.next_show.R;
import com.example.next_show.models.User;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.io.File;
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
        tvBio.setText("Bio: " + bio);

        List<String> faveGenres = currUser.getFaveGenres();
        String genreText = "";

        for (String g : faveGenres) {
            genreText += (g + ", ");
        }

        tvGenres.setText("Favorite Genres: " + genreText.substring(0, genreText.length() - 2));

        // add temp image
        Glide.with(getContext()).load(R.drawable.ic_baseline_account_box_24).into(ivProfileImage);

    }

    private void logout(){
        ParseUser.logOut();
        Log.i(TAG, "Logged out user");
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
    }
}
