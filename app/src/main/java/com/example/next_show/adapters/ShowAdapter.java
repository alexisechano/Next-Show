package com.example.next_show.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.next_show.R;
import com.example.next_show.navigators.NavigationInterface;
import com.example.next_show.models.Show;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ViewHolder> {
    // instance variables
    private Context context;
    private List<Show> shows;
    private NavigationInterface nav;

    // constant to track if adapter is being called from saved
    public static final String SAVED_FRAGMENT = "SavedFragment";

    public ShowAdapter (Context context, List<Show> shows, NavigationInterface nav){
        this.context = context;
        this.shows = shows;
        this.nav = nav;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_show, parent, false);
        return new ViewHolder(view, nav);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Show show = shows.get(position);
        holder.bind(show);
    }

    @Override
    public int getItemCount() {
        return shows.size();
    }

    public void clear() {
        shows.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Show> list) {
        List<Show> modifiedShows = list;
        if(nav.getSource().equals(SAVED_FRAGMENT)){
            modifiedShows = Show.fromParseShows(list);
        }
        shows.addAll(modifiedShows);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // view elements
        private TextView tvShowBody;
        private TextView tvShowTitle;
        private NavigationInterface navigator;

        public ViewHolder(@NonNull View itemView, NavigationInterface navigator) {
            super(itemView);
            setUpView();

            // add this as click listener
            itemView.setOnClickListener(this);

            // set navigator
            this.navigator = navigator;
        }

        private void setUpView() {
            // find them in the layout
            tvShowTitle = itemView.findViewById(R.id.tvShowTitle);
            tvShowBody = itemView.findViewById(R.id.tvShowBody);
        }

        // when the user clicks on a show, show details
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Show show = shows.get(position);

                // create new bundle of arguments to pass into Navigation
                Bundle bundle = new Bundle();
                Boolean fromSavedFragment = navigator.getSource().equals(SAVED_FRAGMENT);
                bundle.putBoolean("savedBool", fromSavedFragment);
                bundle.putParcelable("Show", show);

                // navigate to next fragment
                navigator.navigate(v, bundle);
            }
        }

        public void bind(Show show) {
            // bind the show data to the view elements
            tvShowTitle.setText(show.getTitle());
            tvShowBody.setText(show.getOverview());
        }
    }
}
