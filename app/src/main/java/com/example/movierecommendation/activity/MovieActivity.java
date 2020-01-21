package com.example.movierecommendation.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movierecommendation.adapter.MovieList_Adapter;
import com.example.movierecommendation.R;
import com.example.movierecommendation.ui.activities.Movie_category;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.ArrayList;

public class MovieActivity extends AppCompatActivity {

    LinearLayoutManager manager;
    RecyclerView movie_list;
    MovieList_Adapter adapter;
    Toolbar toolbar;

    public static ArrayList<Movie_category> jsonObjects;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        toolbar = findViewById(R.id.tool_bar);
        toolbar.inflateMenu(R.menu.toolbar_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id=item.getItemId();
                if (id==R.id.action_logout){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MovieActivity.this,LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

        jsonObjects=new ArrayList<>();

        //creating a recyclerview with vertical linear layout for the main page
        //each view in the list will hold a recyclerview with horizontal linear layout

        manager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        movie_list=findViewById(R.id.movie_list);
        movie_list.setLayoutManager(manager);
        adapter=new MovieList_Adapter(MovieActivity.this) ;
        movie_list.setAdapter(adapter);

        RequestQueue requestQueue=Volley.newRequestQueue(this);

        //Creation of  JsonoOjects request objects of three categories: popular,top rated and now playing and adding them to request queue

        JsonObjectRequest nowplaying_json=new JsonObjectRequest(Request.Method.GET,"https://api.themoviedb.org/3/movie/now_playing?api_key=4" +
                "7125e67d25d22f00aebbf4f6d08a3aa&language=en-US&page=1",null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //inserting now playing josn object into arraylist

                jsonObjects.add(new Movie_category(response,"Now Playing","https://api.themoviedb.org/3/movie/now_playing?api_key=4" +
                "7125e67d25d22f00aebbf4f6d08a3aa&language=en-US"));

                adapter.notifyDataSetChanged();

                System.out.println(response.toString());

            }
        },null);
        requestQueue.add(nowplaying_json);

        JsonObjectRequest toprated_json=new JsonObjectRequest(Request.Method.GET,"https://api.themoviedb.org/3/movie/top_rated?api_key=4" +
                "7125e67d25d22f00aebbf4f6d08a3aa&language=en-US&page=1",null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //inserting now playing json object into arraylist

                jsonObjects.add(new Movie_category(response,"Top Rated","https://api.themoviedb.org/3/movie/top_rated?api_key=4" +
                        "7125e67d25d22f00aebbf4f6d08a3aa&language=en-US"));

                adapter.notifyDataSetChanged();

                System.out.println(response.toString());

            }
        },null);
        requestQueue.add(toprated_json);

        JsonObjectRequest popular_json=new JsonObjectRequest(Request.Method.GET,"https://api.themoviedb.org/3/movie/popular?api_key=4" +
                "7125e67d25d22f00aebbf4f6d08a3aa&language=en-US&page=1",null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //inserting now playing josn object into arraylist

                jsonObjects.add(new Movie_category(response,"Popular","https://api.themoviedb.org/3/movie/popular?api_key=4" +
                        "7125e67d25d22f00aebbf4f6d08a3aa&language=en-US"));

                adapter.notifyDataSetChanged();

                System.out.println(response.toString());

            }
        },null);
        requestQueue.add(popular_json);

    }

}
