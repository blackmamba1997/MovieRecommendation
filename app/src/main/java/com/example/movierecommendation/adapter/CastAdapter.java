package com.example.movierecommendation.adapter;

import android.content.Context;
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

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    Context context;
    RequestQueue req;

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context=parent.getContext();
        req= Volley.newRequestQueue(context);

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_tile,parent,false);
        return new CastViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CastViewHolder holder, int position) {

        try {
            holder.castname.setText(SearchableActivity.cast.get(position).getString("name"));
            holder.castchar.setText(SearchableActivity.cast.get(position).getString("character"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String imagepath=SearchableActivity.cast.get(position).getString("profile_path");
            ImageRequest castposter=new ImageRequest(Url.baseimageurl+imagepath, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    holder.castpic.setImageBitmap(response);
                }
            }, 0, 0, null, null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            req.add(castposter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return SearchableActivity.cast.size();
    }

    static class CastViewHolder extends RecyclerView.ViewHolder{

        TextView castname,castchar;
        ImageView castpic;

        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            castname=itemView.findViewById(R.id.title);
            castchar=itemView.findViewById(R.id.rating);
            castpic=itemView.findViewById(R.id.thumbnail);









        }
    }
}
