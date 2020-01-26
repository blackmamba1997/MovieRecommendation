package com.example.movierecommendation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movierecommendation.R;
import com.example.movierecommendation.activity.MovieActivity;
import com.example.movierecommendation.activity.SearchableActivity;

public class SuggestionListAdapter extends RecyclerView.Adapter<SuggestionListAdapter.SuggestionListViewHolder> {

    Context context;
    @NonNull
    @Override
    public SuggestionListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context=parent.getContext();

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_tile, parent, false);
        return new SuggestionListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionListViewHolder holder, final int position) {

        holder.movie_name.setText(MovieActivity.suggestion.get(position).title);
        holder.movie_year.setText(""+MovieActivity.suggestion.get(position).year);
        holder.suggestion_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, SearchableActivity.class);
                intent.putExtra("movie_id",""+MovieActivity.suggestion.get(position).id);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return MovieActivity.suggestion.size();
    }

    static class SuggestionListViewHolder extends RecyclerView.ViewHolder {

        TextView movie_name,movie_year;
        RelativeLayout suggestion_layout;

        public SuggestionListViewHolder(@NonNull View itemView) {
            super(itemView);
            movie_name=itemView.findViewById(R.id.suggestion_movie_name);
            movie_year=itemView.findViewById(R.id.movie_year);
            suggestion_layout=itemView.findViewById(R.id.suggestion_tile);
        }
    }
}
