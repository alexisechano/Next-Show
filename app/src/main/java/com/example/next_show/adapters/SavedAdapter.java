package com.example.next_show.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.next_show.R;
import com.example.next_show.fragments.ShowDetailFragment;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.parse.Parse;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.ViewHolder> {
    // instance variables
    private Context context;
    private List<Show> shows;

    // constant boolean to check if already in saved fragment
    private static final boolean FROM_SAVED_FRAGMENT = true;

    public SavedAdapter (Context context, List<Show> shows){
        this.context = context;
        this.shows = shows;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_show, parent, false);
        return new ViewHolder(view);
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
        List<Show> modifiedShows = new ArrayList<>();

        // turn them into my own show objects
        for(Show pre: list){
            Show currentShow = new Show(pre.getParseTitle(), pre.getParseOverview(), pre.getParseTVID(),
                    pre.getParseNetwork(), pre.getParseYearAired());
            modifiedShows.add(currentShow);
        }

        shows.addAll(modifiedShows);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // view elements
        private TextView tvShowBody;
        private TextView tvShowTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setUpView();

            // add this as click listener
            itemView.setOnClickListener(this);
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

                // make new instance of the fragment
                ShowDetailFragment fragment = ShowDetailFragment.newInstance(show, FROM_SAVED_FRAGMENT);

                // launch the fragment and commit
                AppCompatActivity currActivity = (AppCompatActivity) context;
                FragmentTransaction detailTransact = currActivity.getSupportFragmentManager().beginTransaction();
                detailTransact.replace(R.id.fragment_container_view, fragment);
                detailTransact.commit();

                // TODO: navigate back to original feed without reloading all of that information again
            }
        }

        public void bind(Show show) {
            // bind the show data to the view elements
            tvShowTitle.setText(show.getTitle());
            tvShowBody.setText(show.getOverview());
        }
    }
}

