package com.example.movierecommendation.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movierecommendation.R;
import com.example.movierecommendation.adapter.CastAdapter;
import com.example.movierecommendation.adapter.SimilarMovieAdapter;
import com.example.movierecommendation.ui.activities.Movie;
import com.example.movierecommendation.ui.activities.Url;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchableActivity extends YouTubeBaseActivity {

    ImageView poster;
    TextView title, rating, director, runtime, overview;
    RecyclerView cast_list, similarmovie_list;
    LinearLayoutManager cast_manager, similarmovie_manager;
    CastAdapter castAdapter;
    SimilarMovieAdapter similarMovieAdapter;
    public static ArrayList<JSONObject> cast;
    public static ArrayList<JSONObject> similarmovie;
    ArrayList<String> youtubevideo;
    RequestQueue requestQueue;
    YouTubePlayerView youTubePlayerView;
    ProgressBar progressBar;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        Intent intent = getIntent();

        poster = findViewById(R.id.movie_detail_poster);
        title = findViewById(R.id.movie_detail_title);
        title.setSelected(true);
        rating = findViewById(R.id.movie_detail_rating);
        director = findViewById(R.id.movie_detail_director);
        runtime = findViewById(R.id.movie_detail_runtime);
        overview = findViewById(R.id.movie_detail_overview);
        cast_list = findViewById(R.id.movie_detail_cast);
        similarmovie_list = findViewById(R.id.movie_detail_similar_movies);
        youTubePlayerView=findViewById(R.id.youtube_video_player);
        progressBar=findViewById(R.id.searchable_activity_progressbar);
        layout=findViewById(R.id.searchable_activity_linear_layout);

        cast=new ArrayList<>();
        similarmovie=new ArrayList<>();
        youtubevideo=new ArrayList<>();

        cast_manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        similarmovie_manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        cast_list.setLayoutManager(cast_manager);
        castAdapter = new CastAdapter();
        cast_list.setAdapter(castAdapter);
        similarmovie_list.setLayoutManager(similarmovie_manager);
        similarMovieAdapter = new SimilarMovieAdapter();
        similarmovie_list.setAdapter(similarMovieAdapter);
        String movie_id = intent.getStringExtra("movie_id");

        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest movie_details = new JsonObjectRequest(Request.Method.GET, Url.movieurl+movie_id+"?"+Url.api_key+"&append_to_response=similar_movies%2Ccredits%2Cvideos", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);

                try {

                    System.out.println("SearchableActivity.onResponse: "+response);
                    insertposter(response.getString("poster_path"));

                    title.setText(response.getString("title"));
                    runtime.setText(""+response.getInt("runtime"));
                    overview.setText(response.getString("overview"));
                    JSONArray castJsonarray = response.getJSONObject("credits").getJSONArray("cast");

                    System.out.println("SearchableActivity.onResponse "+castJsonarray);
                    JSONArray similarmovieJsonarray=response.getJSONObject("similar_movies").getJSONArray("results");

                    int k=10;
                    if(castJsonarray.length()<10){
                        k=castJsonarray.length();
                    }

                    for (int i = 0; i<k; i++) {
                        cast.add(castJsonarray.getJSONObject(i));
                    }

                    castAdapter.notifyDataSetChanged();

                    for (int i = 0; i<similarmovieJsonarray.length(); i++) {
                        similarmovie.add(similarmovieJsonarray.getJSONObject(i));
                    }

                    similarMovieAdapter.notifyDataSetChanged();

                    JSONArray ytvideos=response.getJSONObject("videos").getJSONArray("results");

                    for (int i=0;i<ytvideos.length();i++){
                        youtubevideo.add(ytvideos.getJSONObject(i).getString("key"));
                    }

                    youTubePlayerView.initialize(Url.google_api_key, new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                            youTubePlayer.cueVideos(youtubevideo);

                            youTubePlayer.setPlaylistEventListener(new YouTubePlayer.PlaylistEventListener() {
                                @Override
                                public void onPrevious() {

                                    Toast.makeText(SearchableActivity.this, "prev vid", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNext() {

                                    Toast.makeText(SearchableActivity.this, "next vid", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onPlaylistEnded() {

                                }
                            });

                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(movie_details);

    }
    private void insertposter(String path){

        ImageRequest posterrequest=new ImageRequest(Url.baseimageurl+path, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {

                poster.setImageBitmap(response);

            }
        }, 0, 0, null, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(posterrequest);
    }


}
