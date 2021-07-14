package com.example.next_show.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.next_show.R;
import com.example.next_show.models.Show;

public class ShowDetailFragment extends Fragment {
    private Show currShow;

    // view elements
    private TextView tvDetailTitle;
    private TextView tvDetailOverview;
    //private TextView tvDetailRating; // TODO: update show object to include this later (not my rating)

    // constants
    public static final String TAG = "ShowDetailFragment";

    // empty constructor
    public ShowDetailFragment(){ }

    public static ShowDetailFragment newInstance(Show show) {
        ShowDetailFragment fragment = new ShowDetailFragment();

        // generate bundle for the show
        Bundle bundle = new Bundle();
        bundle.putParcelable(Show.class.getSimpleName(), show);
        fragment.setArguments(bundle);

        return fragment;
    }

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

        // set up the view elements
        setUpView(currView);

        return currView;
    }

    private void setUpView(View currView) {
        tvDetailTitle = currView.findViewById(R.id.tvDetailTitle);
        tvDetailOverview = currView.findViewById(R.id.tvDetailOverview);

        tvDetailTitle.setText(currShow.getTitle());
        tvDetailOverview.setText(currShow.getOverview());

    }

}
