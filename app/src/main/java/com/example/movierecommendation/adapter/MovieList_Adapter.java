package com.example.movierecommendation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movierecommendation.R;
import com.example.movierecommendation.activity.CategoryActivity;
import com.example.movierecommendation.activity.MovieActivity;
import com.example.movierecommendation.ui.activities.Movie_category;

import org.json.JSONArray;

public class MovieList_Adapter extends RecyclerView.Adapter<MovieList_Adapter.CustomViewHolder> {

    Context context;

    public MovieList_Adapter(Context context) {
        this.context = context;

    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.category_list, parent, false);

        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        final Movie_category obj = MovieActivity.jsonObjects.get(position);
        holder.cat_name.setText(obj.category_type);

        if (obj.category_type.equalsIgnoreCase("Based on Recent Search")) {
            holder.cat_more.setVisibility(View.INVISIBLE);
        } else {
            holder.cat_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("url", obj.category_base_url);
                    context.startActivity(intent);

                }
            });
        }

        LinearLayoutManager child_list_layout = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.child_list.setLayoutManager(child_list_layout);

        try {
            JSONArray cat_list = obj.category_list.getJSONArray("results");
            RecyclerView.Adapter child_adapter = new ChildList_Adapter(context, cat_list);
            holder.child_list.setAdapter(child_adapter);
        } catch (Exception e) {
            JSONArray cat_list = null;
            RecyclerView.Adapter child_adapter = new ChildList_Adapter(context, cat_list);
            holder.child_list.setAdapter(child_adapter);
        }


    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {

        return MovieActivity.jsonObjects.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        RecyclerView child_list;
        TextView cat_name;
        Button cat_more;

        public CustomViewHolder(@NonNull View itemView) {

            super(itemView);
            child_list = itemView.findViewById(R.id.category_list);
            cat_more = itemView.findViewById(R.id.more);
            cat_name = itemView.findViewById(R.id.category_type);
        }
    }
}
