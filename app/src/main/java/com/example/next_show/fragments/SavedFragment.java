package com.example.next_show.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.next_show.R;
import com.example.next_show.adapters.ShowAdapter;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.example.next_show.navigators.NavigationInterface;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

// NO API CALLS IN THIS CLASS -> SOLELY PARSE DATABASE DATA!
public class SavedFragment extends Fragment {
    // view elements
    private TextView tvName;
    private RecyclerView rvSaved;

    protected ShowAdapter adapter;
    protected List<Show> savedShows;

    // constants
    public static final String TAG = "SavedFragment";
    public static final int QUERY_LIMIT = 10;

    // empty constructor
    public SavedFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View currView = inflater.inflate(R.layout.fragment_saved, container, false);

        // find Recycler View
        rvSaved = currView.findViewById(R.id.rvSaved);

        // initialize the array that will hold posts and create a PostsAdapter
        savedShows = new ArrayList<>();

        // initialize the adapter
        adapter = new ShowAdapter(getActivity(), savedShows, new NavigateSavedToDetail(), null);

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
        User currUser = (User) ParseUser.getCurrentUser();

        // get list of Shows from Parse -> I don't really need query if I user User list!
        queryPosts(currUser);
    }

    private void queryPosts(User targetUser) {
        // specify what type of data we want to query - Show.class
        ParseQuery<Show> query = ParseQuery.getQuery(Show.class);

        // include data referred by user key
        query.include(Show.KEY_USER);

        // limit query to latest items
        query.setLimit(QUERY_LIMIT);

        // set user specific query
        query.whereEqualTo(Show.KEY_USER, targetUser);

        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");

        // start an asynchronous call for shows
        query.findInBackground(new FindCallback<Show>() {
            @Override
            public void done(List<Show> shows, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // turn these Parse Show into NextShow Show objects
                List<Show> modifiedShows = Show.fromParseShows(shows);

                // save received posts to list and notify adapter of new data
                adapter.addAll(modifiedShows);
            }
        });
    }

    class NavigateSavedToDetail implements NavigationInterface {
        public void navigate(View v, Bundle b){
            Navigation.findNavController(v).navigate(R.id.action_savedFragment_to_showDetailFragment, b);
        }
    }
}
