package com.example.movierecommendation.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
    YouTubePlayer ytplayer;
    ProgressBar progressBar;
    LinearLayout layout;
    CardView videocard,castcard,similarmoviecard;
    RatingBar ratingbar;
    FirebaseFirestore db;
    String movie_id;
    boolean isfullscreen=false;

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
        progressBar=findViewById(R.id.searchable_activity_progressbar);
        layout=findViewById(R.id.searchable_activity_linear_layout);
        videocard=findViewById(R.id.video_cardView);
        castcard=findViewById(R.id.cast_cardview);
        similarmoviecard=findViewById(R.id.similarmovie_cardview);
        ratingbar=findViewById(R.id.movie_detail_user_rating);
        db=FirebaseFirestore.getInstance();
        youTubePlayerView=findViewById(R.id.youtube_video_player);

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
        movie_id = intent.getStringExtra("movie_id");

        findMovieRating(movie_id);

        requestQueue = Volley.newRequestQueue(this);



        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if(fromUser && rating>=0.5f) {

                    Toast.makeText(SearchableActivity.this, "the rating is " + rating, Toast.LENGTH_SHORT).show();
                    DocumentReference doc = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    HashMap<String, String> map = new HashMap<>();
                    map.put("rating", "" + rating);
                    doc.collection("movies").document(movie_id).set(map);

                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        JsonObjectRequest movie_details = new JsonObjectRequest(Request.Method.GET, Url.movieurl+movie_id+"?"+Url.api_key+"&append_to_response=similar_movies%2Ccredits%2Cvideos", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);

                try {

                    System.out.println("SearchableActivity.onResponse: "+response);
                    insertposter(response.getString("poster_path"));

                    title.setText(response.getString("title"));

                    JSONArray crewjsonarray=response.getJSONObject("credits").getJSONArray("crew");

                    for(int i=0;i<crewjsonarray.length();i++){

                        if(crewjsonarray.getJSONObject(i).getString("job").equals("Director")){

                            director.setText("Director: " +crewjsonarray.getJSONObject(i).getString("name"));
                            break;
                        }

                    }

                    if(response.getInt("runtime")!=0) {
                        runtime.setText("" + response.getInt("runtime")+" mins");
                    }
                    else{
                        runtime.setText("No data available");
                    }

                    if (response.getString("vote_average").equals("0")){

                        rating.setText("Not rated yet");
                    }else{
                        rating.setText(response.getString("vote_average"));
                    }
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

                    if(cast.size()!=0) {

                        castcard.setVisibility(View.VISIBLE);
                        castAdapter.notifyDataSetChanged();
                    }

                    for (int i = 0; i<similarmovieJsonarray.length(); i++) {
                        similarmovie.add(similarmovieJsonarray.getJSONObject(i));
                    }

                    if(similarmovie.size()!=0) {

                        similarmoviecard.setVisibility(View.VISIBLE);
                        similarMovieAdapter.notifyDataSetChanged();

                    }

                    JSONArray ytvideos=response.getJSONObject("videos").getJSONArray("results");

                    for (int i=0;i<ytvideos.length();i++){
                        youtubevideo.add(ytvideos.getJSONObject(i).getString("key"));
                    }

                    if(youtubevideo.size()!=0) {
                        videocard.setVisibility(View.VISIBLE);

                        youTubePlayerView.initialize(Url.google_api_key, new YouTubePlayer.OnInitializedListener() {
                            @Override
                            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                                System.out.println("SearchableActivity.onInitializationSuccess entered");

                                ytplayer=youTubePlayer;

                                ytplayer.cueVideos(youtubevideo);


                                ytplayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                                    @Override
                                    public void onFullscreen(boolean b) {
                                        isfullscreen=b;
                                    }
                                });

                            }

                            @Override
                            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                                System.out.println("SearchableActivity.onInitializationFailure");

                            }
                        });
                    }

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

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onRestart() {
        super.onRestart();

        cast.clear();
        similarmovie.clear();
        youtubevideo.clear();
        if(ytplayer!=null) {
          ytplayer.release();
        }

    }

    @Override
    public void onBackPressed() {
        if(isfullscreen){

            isfullscreen=false;
            ytplayer.setFullscreen(isfullscreen);

        }else {
            super.onBackPressed();
        }
    }

    private void insertposter(String path){

        if(path.charAt(0)=='/') {

            ImageRequest posterrequest = new ImageRequest(Url.baseimageurl + path, new Response.Listener<Bitmap>() {
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
        }else{
            poster.setImageResource(R.drawable.ic_search_black_24dp);
        }
    }

    private void findMovieRating(String movie_id){

        DocumentReference movie=db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("movies").document(movie_id);

        movie.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.getResult().exists()){

                            DocumentSnapshot data=task.getResult();
                            float rating=Float.valueOf(data.getString("rating"));
                            ratingbar.setRating(rating);
                        }
                    }
                });

    }

}
