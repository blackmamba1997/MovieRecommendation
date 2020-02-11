package com.example.movierecommendation.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.movierecommendation.R;
import com.example.movierecommendation.activity.SearchableActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChildList_Adapter extends RecyclerView.Adapter<ChildList_Adapter.ViewHolder> {

    Context c;
    JSONArray list;
    RequestQueue requestQueue;
    ChildList_Adapter(Context c,JSONArray list){
        this.c=c;
        this.list=list;
        requestQueue= Volley.newRequestQueue(c);
    }


    @NonNull
    @Override
    public ChildList_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(c).inflate(R.layout.movie_tile,parent,false);
        return new ChildList_Adapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        try {

            final JSONObject movie=list.getJSONObject(position);
            holder.movie_name.setText(movie.getString("title"));
            holder.rating.setText(movie.get("vote_average").toString());
            String path=movie.get("poster_path").toString();

            if(path.charAt(0)=='/') {

                ImageRequest imageRequest = new ImageRequest("https://image.tmdb.org/t/p/w185" + path, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        holder.poster.setImageBitmap(response);
                    }
                }, 0, 0, null, null, null);
                requestQueue.add(imageRequest);
            }else{
                holder.poster.setImageResource(R.drawable.ic_search_black_24dp);
            }

            holder.poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        Intent intent=new Intent(c, SearchableActivity.class);
                        System.out.println("ChildList_Adapter.onClick "+movie.getInt("id"));
                        intent.putExtra("movie_id",""+movie.getInt("id"));
                        c.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.poster.setImageResource(R.drawable.ic_search_black_24dp);
    }

    @Override
    public int getItemCount() {

        return list.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;
        TextView movie_name,rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            poster=itemView.findViewById(R.id.thumbnail);
            movie_name=itemView.findViewById(R.id.title);
            rating=itemView.findViewById(R.id.rating);
        }
    }
}