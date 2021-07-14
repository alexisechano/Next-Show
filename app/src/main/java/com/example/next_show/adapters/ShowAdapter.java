package com.example.next_show.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ViewHolder> {
    // instance variables
    private Context context;
    private List<Show> shows;

    public ShowAdapter (Context context, List<Show> shows){
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
        shows.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // view elements
        private TextView tvShowBody;
        private ImageView ivShowImage;
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
            ivShowImage = itemView.findViewById(R.id.ivShowImage);
        }

        // when the user clicks on a show, show details
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Show show = shows.get(position);

                // create intent for the new activity
//                Intent intent = new Intent(context, DetailActivity.class);
//
//                // serialize the show using parceler, use its short name as a key
//                intent.putExtra(show.class.getSimpleName(), Parcels.wrap(show));
//
//                // show the activity
//                context.startActivity(intent);
            }
        }

        public void bind(Show show) {
            // bind the show data to the view elements
            tvShowTitle.setText(show.getName());
//            tvUsername.setText(show.getUser().getUsername());
//            ParseFile image = show.getImage();
//            if (image != null) {
//                Glide.with(context).load(image.getUrl()).into(ivImage);
//            }
        }
    }
}
