package com.example.next_show.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.next_show.R;
import com.example.next_show.adapters.SavedAdapter;
import com.example.next_show.adapters.ShowAdapter;
import com.example.next_show.models.Show;

import java.util.ArrayList;
import java.util.List;

// NO API CALLS IN THIS CLASS -> SOLELY PARSE DATABASE DATA!
public class SavedFragment extends Fragment {
    // view elements
    private TextView tvName;
    private RecyclerView rvSaved;

    protected SavedAdapter adapter;
    protected List<Show> savedShows;

    // empty constructor
    public SavedFragment() {

    }

    public static SavedFragment newInstance() {
        SavedFragment fragment = new SavedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View currView = inflater.inflate(R.layout.fragment_saved, container, false);

        // TODO: CHECK call User.hasSavedShows() and if false then save in background then call setSaved to mark it saved
        // NEXT: make sure the above is false, save POINTERs of SHOWs to the user's list of saved shows in Parse

        // find Recycler View
        rvSaved = currView.findViewById(R.id.rvSaved);

        // initialize the array that will hold posts and create a PostsAdapter
        savedShows = new ArrayList<>();

        adapter = new SavedAdapter(getActivity(), savedShows);

        // set the adapter on the recycler view
        rvSaved.setAdapter(adapter);

        // set the layout manager on the recycler view
        rvSaved.setLayoutManager(new LinearLayoutManager(getActivity()));

        // get Parse data and load into view
        fetchParseShows();

        return currView;

    }

    // get data from Parse Database
    private void fetchParseShows() {
        // get current user

        // grab their saved shows

        // get list of Shows from Parse

        // use ParseQuery to get Show pointers

    }
}
