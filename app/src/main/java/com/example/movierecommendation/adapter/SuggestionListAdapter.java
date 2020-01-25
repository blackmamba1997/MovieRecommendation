package com.example.movierecommendation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movierecommendation.R;
import com.example.movierecommendation.activity.MovieActivity;

public class SuggestionListAdapter extends RecyclerView.Adapter<SuggestionListAdapter.SuggestionListViewHolder> {

    @NonNull
    @Override
    public SuggestionListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_tile, parent, false);
        return new SuggestionListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionListViewHolder holder, int position) {

        holder.movie_name.setText(MovieActivity.suggestion.get(position).title);
        holder.movie_year.setText(""+MovieActivity.suggestion.get(position).year);

    }

    @Override
    public int getItemCount() {
        return MovieActivity.suggestion.size();
    }

    static class SuggestionListViewHolder extends RecyclerView.ViewHolder {

        TextView movie_name,movie_year;

        public SuggestionListViewHolder(@NonNull View itemView) {
            super(itemView);
            movie_name=itemView.findViewById(R.id.suggestion_movie_name);
            movie_year=itemView.findViewById(R.id.movie_year);
        }
    }
}
