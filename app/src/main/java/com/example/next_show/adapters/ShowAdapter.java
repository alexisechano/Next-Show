package com.example.next_show.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.next_show.R;
import com.example.next_show.models.User;
import com.example.next_show.navigators.NavigationInterface;
import com.example.next_show.models.Show;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseUser;
import com.zerobranch.layout.SwipeLayout;

import java.util.List;

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ViewHolder> {
    // instance variables
    private Context context;
    private List<Show> currentShows; // currently displayed shows on recycler view
    private NavigationInterface nav;

    public ShowAdapter (Context context, List<Show> shows, NavigationInterface nav){
        this.context = context;
        this.currentShows = shows;
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
        Show show = currentShows.get(position);
        holder.bind(show);
    }

    @Override
    public int getItemCount() {
        return currentShows.size();
    }

    public void clear() {
        currentShows.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Show> list) {
        currentShows.addAll(list);
        notifyDataSetChanged();
    }

    // for filtering
    public void update(List<Show> list) {
        currentShows = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // view elements
        private TextView tvShowBody;
        private TextView tvShowTitle;
        private SwipeLayout swipeLayout;
        private CardView showCard;
        private ImageView ivDismiss;
        private ImageView ivCardImage;
        private NavigationInterface navigator;
        private String showId;

        public ViewHolder(@NonNull View itemView, NavigationInterface navigator) {
            super(itemView);

            // add onclick listener for CardView elements itself
            showCard = itemView.findViewById(R.id.showCard);
            showCard.setOnClickListener(this);

            // set up elements and swipe
            setUpView();

            // set navigator
            this.navigator = navigator;
        }

        private void setUpView() {
            // find view elements in the layout
            tvShowTitle = itemView.findViewById(R.id.tvShowTitle);
            tvShowBody = itemView.findViewById(R.id.tvShowBody);
            ivCardImage = itemView.findViewById(R.id.ivCardImage);

            // swipe layout set up
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            ivDismiss = itemView.findViewById(R.id.ivDismiss);

            // set listener for swiping
            swipeLayout.setOnActionsListener(new SwipeLayout.SwipeActionsListener() {
                @Override
                public void onOpen(int direction, boolean isContinuous) {
                    if (direction == SwipeLayout.LEFT) {
                        // was executed swipe to the left
                        Log.i("Adapter", "SWIPED!");

                        // grab current show and remove from list
                        User user = (User) ParseUser.getCurrentUser();
                        showId = removeShow(user);
                        ivDismiss.setVisibility(View.INVISIBLE);

                        // update user forbidden shows
                        user.addToForbiddenShows(showId);
                    }
                }

                @Override
                public void onClose() {
                    ivDismiss.setVisibility(View.GONE);
                }
            });
        }

        private void showSnackbar(int position, Show show, User user) {
            View.OnClickListener undoShow = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // insert show back
                    currentShows.add(position, show);
                    notifyItemInserted(position);
                    user.removeFromForbidden(show.getId());
                }
            };

            // display snackbar and allow undo
            Snackbar.make(itemView, R.string.snackbar_text, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_action, undoShow)
                    .show();
        }

        private String removeShow(User user) {
            String showId = "";
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ivDismiss.setVisibility(View.VISIBLE);
                Show show = currentShows.get(position);

                // grab show ID to save in Parse later
                showId = show.getId();

                // update adapter and give user ability to undo
                currentShows.remove(position);
                notifyItemRemoved(position);
                showSnackbar(position, show, user);
            }

            return showId;
        }

        // when the user clicks on a show, show details
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Show show = currentShows.get(position);

                // create new bundle of arguments to pass into Navigation
                Bundle bundle = new Bundle();
                bundle.putParcelable(Show.class.getSimpleName(), show);

                // navigate to next fragment
                navigator.navigate(v, bundle);
            }
        }

        public void bind(Show show) {
            // bind the show data to the view elements
            if (show.isSaved()) {
                tvShowTitle.setText(show.getTitle() + " (saved)");
            } else {
                tvShowTitle.setText(show.getTitle());
            }

            tvShowBody.setText(show.getOverview());

            Glide.with(context).load(show.getImageUrl()).into(ivCardImage);
        }
    }
}
