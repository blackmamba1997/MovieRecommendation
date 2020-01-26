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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.movierecommendation.R;
import com.example.movierecommendation.activity.SearchableActivity;
import com.example.movierecommendation.ui.activities.Url;

import org.json.JSONException;

public class SimilarMovieAdapter extends RecyclerView.Adapter<SimilarMovieAdapter.SimilarMovieViewHolder> {

    Context context;
    RequestQueue req;
    @NonNull
    @Override
    public SimilarMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context=parent.getContext();
        req= Volley.newRequestQueue(context);
        View v= LayoutInflater.from(context).inflate(R.layout.movie_tile,parent,false);
        return new SimilarMovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimilarMovieViewHolder holder, final int position) {

        try {
            holder.title.setText(SearchableActivity.similarmovie.get(position).getString("title"));

            holder.rating.setText(""+SearchableActivity.similarmovie.get(position).get("vote_average"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String imagepath=SearchableActivity.similarmovie.get(position).getString("poster_path");
            if (imagepath.charAt(0)=='/') {
                ImageRequest poster = new ImageRequest(Url.baseimageurl + imagepath, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {

                        holder.poster.setImageBitmap(response);

                    }
                }, 0, 0, null, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                req.add(poster);

            }else{
                holder.poster.setImageResource(R.drawable.ic_search_black_24dp);
            }
        } catch (JSONException e) {

            e.printStackTrace();

        }

        holder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    Intent intent=new Intent(context,SearchableActivity.class);
                    intent.putExtra("movie_id",""+SearchableActivity.similarmovie.get(position).getInt("id"));
                    context.startActivity(intent);

                }catch(JSONException e){

                    e.printStackTrace();

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return SearchableActivity.similarmovie.size();
    }

    static class SimilarMovieViewHolder extends RecyclerView.ViewHolder{

        ImageView poster;
        TextView title,rating;

        public SimilarMovieViewHolder(@NonNull View itemView) {

            super(itemView);
            poster=itemView.findViewById(R.id.thumbnail);
            title=itemView.findViewById(R.id.title);
            rating=itemView.findViewById(R.id.rating);
        }
    }
}
