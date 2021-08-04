package com.example.next_show.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.next_show.R;
import com.example.next_show.models.Show;
import com.example.next_show.models.User;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {
    private List<HashMap<String, Object>> showsList;
    private Context context;
    private User currUser;

    // constants to use for binding
    public static final String USER_KEY = "user";
    public static final String SHOW_KEY = "show";

    public CommunityAdapter (Context context, List<HashMap<String, Object>> showsList) {
        this.context = context;
        this.showsList = showsList;
        currUser = (User) ParseUser.getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_community, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap<String, Object> showItem = showsList.get(position);
        holder.bind(showItem);
    }

    @Override
    public int getItemCount() {
        return showsList.size();
    }

    public void clear() {
        showsList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<HashMap<String, Object>> shows) {
        showsList.addAll(shows);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // view elements
        private TextView tvWatcher;
        private TextView tvShowTitle;
        private ImageView ivCardImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            tvWatcher = itemView.findViewById(R.id.tvWatcher);
            tvShowTitle = itemView.findViewById(R.id.tvShowTitle);
            ivCardImage = itemView.findViewById(R.id.ivCardImage);
        }

        @Override
        public void onClick(View v) {
            Log.i("Adapter", "Clicked on show!");
        }

        public void bind(HashMap<String, Object> showItem) {
            // grab user data
            User user = (User) showItem.get(USER_KEY);
            String username = user.getUsername();

            // mark as self
            if (username.equals(currUser.getUsername())) {
                tvWatcher.setText("@" + username + " (You)");
            } else {
                tvWatcher.setText("@" + username);
            }

            // grab show data
            Show show = (Show) showItem.get(SHOW_KEY);
            String title = show.getParseTitle();
            String imageUrl = show.getParseImage();
            tvShowTitle.setText(title);
            Glide.with(context).load(imageUrl).into(ivCardImage);
        }
    }
}
