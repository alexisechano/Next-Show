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
import androidx.fragment.app.FragmentManager;

import com.example.next_show.MainActivity;
import com.example.next_show.R;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ShowDetailFragment extends Fragment {
    private Show currShow;
    private boolean alreadySaved;

    // view elements
    private TextView tvDetailTitle;
    private TextView tvDetailOverview;
    private Button btnSaveShow;
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

        // set up the view elements
        setUpView(currView);

        return currView;
    }

    private void setUpView(View currView) {
        // grab current User
        ParseUser parseUser = ParseUser.getCurrentUser();

        tvDetailTitle = currView.findViewById(R.id.tvDetailTitle);
        tvDetailOverview = currView.findViewById(R.id.tvDetailOverview);
        btnSaveShow = currView.findViewById(R.id.btnSaveShow);

        tvDetailTitle.setText(currShow.getTitle());
        tvDetailOverview.setText(currShow.getOverview());

        if(alreadySaved){
            Toast.makeText(getActivity(), "Show already saved!", Toast.LENGTH_SHORT).show();
            btnSaveShow.setVisibility(View.GONE);
            return;
        }

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
