package com.example.next_show.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.next_show.R;
import com.example.next_show.adapters.CommunityAdapter;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommunityFragment extends Fragment {
    // variables
    private View currView;
    private CommunityAdapter adapter;
    private List<HashMap<String, Object>> showsList;
    private AVLoadingIndicatorView loadingBar;
    private RecyclerView rvCommunity;

    // constants
    public static final String TAG = "CommunityFragment";
    public static final String FETCH_COMMUNITY_FUNC = "fetchCommunity";

    // empty constructor
    public CommunityFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        currView = inflater.inflate(R.layout.fragment_community, container, false);

        // put loading bar on screen
        loadingBar = currView.findViewById(R.id.loadingBar);
        loadingBar.show();

        // init showsList
        showsList = new ArrayList<>();

        // init the adapter
        adapter = new CommunityAdapter(getActivity(), showsList);

        // init recycler view
        rvCommunity = currView.findViewById(R.id.rvCommunity);
        rvCommunity.setVisibility(View.GONE);

        // set to adapter
        rvCommunity.setAdapter(adapter);

        // set the layout manager on the recycler view
        rvCommunity.setLayoutManager(new LinearLayoutManager(getActivity()));

        // call custom Parse query (defined in JS code)
        fetchCommunity();

        return currView;
    }

    private void fetchCommunity() {
        Log.i(TAG, "Grabbing parse information now!");

        // params necessary for call
        HashMap<String, Object> emptyParams = new HashMap<String, Object>();
        ParseCloud.callFunctionInBackground(FETCH_COMMUNITY_FUNC, emptyParams, new FunctionCallback<List<HashMap<String, Object>>>() {
            @Override
            public void done(List<HashMap<String, Object>> results, ParseException e) {
                if (e == null) {
                    // grab array of results
                    Log.i(TAG, "Results: " + results.toString());

                    // send to adapter to update RV
                    adapter.addAll(results);

                    // hide loading bar and show RV
                    rvCommunity.setVisibility(View.VISIBLE);
                    loadingBar.hide();
                    loadingBar.setVisibility(View.GONE);
                } else {
                    Log.e(TAG, "Could not retrieve results!", e);
                }
            }
        });
    }
}
