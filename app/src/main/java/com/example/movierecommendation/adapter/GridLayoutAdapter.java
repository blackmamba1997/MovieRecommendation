package com.example.movierecommendation.adapter;

import android.app.Application;
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

import org.json.JSONException;
import static com.example.movierecommendation.activity.CategoryActivity.gridlist;

public class GridLayoutAdapter extends RecyclerView.Adapter<GridLayoutAdapter.ViewHolder> {

    Context context;
    RequestQueue requestQueue;
    ImageRequest imageRequest;
    String _base_url;
    int pos;

    public GridLayoutAdapter(Context c,String url){
        _base_url=url;
        context=c;
        requestQueue= Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public GridLayoutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        System.out.println("GridLayoutAdapter.onCreateViewHolder");

        View v= LayoutInflater.from(context).inflate(R.layout.movie_tile,parent,false);
        return new GridLayoutAdapter.ViewHolder(v);
    }

    @Override
    public long getItemId(int position) {

        try {
            return gridlist.get(position).getInt("id");
        } catch (JSONException e) {
            return 0;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final GridLayoutAdapter.ViewHolder holder, final int position) {

        pos=holder.getAdapterPosition();

        try {

            System.out.println("onBind called " +gridlist.get(position).getString("title") );
            holder.title.setText(gridlist.get(position).getString("title"));
            if (gridlist.get(position).get("vote_average").toString().equals("0")){
                holder.rating.setText("Not rated yet");
            }else {
                holder.rating.setText(gridlist.get(position).get("vote_average").toString());
            }

            String path=gridlist.get(position).getString("poster_path");

            if(path.charAt(0)=='/') {

                System.out.println("executing with "+path);

                imageRequest = new ImageRequest("https://image.tmdb.org/t/p/w185" + path, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        holder.thumbnail.setImageBitmap(response);
                    }
                }, 0, 0, null, null, null);
                imageRequest.setTag("image");

                requestQueue.add(imageRequest);

            }else{

                holder.thumbnail.setImageResource(R.drawable.ic_search_black_24dp);
            }

            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        Intent intent=new Intent(context, SearchableActivity.class);
                        intent.putExtra("movie_id",""+gridlist.get(position).getInt("id"));
                        context.startActivity(intent);
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
        System.out.println("GridLayoutAdapter.onViewRecycled "+holder.title.getText());
        holder.thumbnail.setImageResource(R.drawable.ic_search_black_24dp);

    }


    @Override
    public int getItemCount() {

        return gridlist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView thumbnail;
        TextView title,rating;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            thumbnail=itemView.findViewById(R.id.thumbnail);
            title=itemView.findViewById(R.id.title);
            rating=itemView.findViewById(R.id.rating);

        }
    }
}
