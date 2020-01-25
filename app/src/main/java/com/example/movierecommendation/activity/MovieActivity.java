package com.example.movierecommendation.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movierecommendation.adapter.MovieList_Adapter;
import com.example.movierecommendation.R;
import com.example.movierecommendation.adapter.SuggestionListAdapter;
import com.example.movierecommendation.ui.activities.Movie;
import com.example.movierecommendation.ui.activities.Movie_category;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class MovieActivity extends AppCompatActivity {

    LinearLayoutManager manager;
    LinearLayoutManager suggestionmanager;
    RecyclerView movie_list;
    RecyclerView suggest_list;
    MovieList_Adapter adapter;
    SuggestionListAdapter s_adapter;
    Toolbar toolbar;
    SearchView search;
    int l=0;


    public static ArrayList<Movie_category> jsonObjects;
    public static ArrayList<Movie> suggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        toolbar = findViewById(R.id.tool_bar);
        toolbar.inflateMenu(R.menu.toolbar_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_logout) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MovieActivity.this, LoginActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.action_search) {
                    if (l == 0) {

                        Toast.makeText(MovieActivity.this, "pressed the settings button", Toast.LENGTH_SHORT).show();
                        movie_list.setVisibility(View.GONE);
                        search.setVisibility(View.VISIBLE);
                        suggest_list.setVisibility(View.VISIBLE);
                        l=1;

                    }else {

                        Toast.makeText(MovieActivity.this, "pressed the settings button", Toast.LENGTH_SHORT).show();
                        movie_list.setVisibility(View.VISIBLE);
                        search.setVisibility(View.GONE);
                        suggest_list.setVisibility(View.GONE);
                        l=0;

                    }

                }
                return false;
            }
        });

        jsonObjects = new ArrayList<>();
        suggestion=new ArrayList<>();

        //creating a recyclerview with vertical linear layout for the main page
        //each view in the list will hold a recyclerview with horizontal linear layout

        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        movie_list = findViewById(R.id.movie_list);
        movie_list.setLayoutManager(manager);
        adapter = new MovieList_Adapter(MovieActivity.this);
        movie_list.setAdapter(adapter);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Creation of  JsonoOjects request objects of three categories: popular,top rated and now playing and adding them to request queue

        JsonObjectRequest nowplaying_json = new JsonObjectRequest(Request.Method.GET, "https://api.themoviedb.org/3/movie/now_playing?api_key=4" +
                "7125e67d25d22f00aebbf4f6d08a3aa&language=en-US&page=1", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //inserting now playing josn object into arraylist

                jsonObjects.add(new Movie_category(response, "Now Playing", "https://api.themoviedb.org/3/movie/now_playing?api_key=4" +
                        "7125e67d25d22f00aebbf4f6d08a3aa&language=en-US"));

                adapter.notifyDataSetChanged();

                System.out.println(response.toString());

            }
        }, null);
        requestQueue.add(nowplaying_json);

        JsonObjectRequest toprated_json = new JsonObjectRequest(Request.Method.GET, "https://api.themoviedb.org/3/movie/top_rated?api_key=4" +
                "7125e67d25d22f00aebbf4f6d08a3aa&language=en-US&page=1", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //inserting now playing json object into arraylist

                jsonObjects.add(new Movie_category(response, "Top Rated", "https://api.themoviedb.org/3/movie/top_rated?api_key=4" +
                        "7125e67d25d22f00aebbf4f6d08a3aa&language=en-US"));

                adapter.notifyDataSetChanged();

                System.out.println(response.toString());

            }
        }, null);
        requestQueue.add(toprated_json);

        JsonObjectRequest popular_json = new JsonObjectRequest(Request.Method.GET, "https://api.themoviedb.org/3/movie/popular?api_key=4" +
                "7125e67d25d22f00aebbf4f6d08a3aa&language=en-US&page=1", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //inserting now playing josn object into arraylist

                jsonObjects.add(new Movie_category(response, "Popular", "https://api.themoviedb.org/3/movie/popular?api_key=4" +
                        "7125e67d25d22f00aebbf4f6d08a3aa&language=en-US"));

                adapter.notifyDataSetChanged();

                System.out.println(response.toString());

            }
        }, null);
        requestQueue.add(popular_json);

        suggest_list=findViewById(R.id.suggestion_list);
        suggestionmanager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        suggest_list.setLayoutManager(suggestionmanager);
        s_adapter=new SuggestionListAdapter();
        suggest_list.setAdapter(s_adapter);

        search=findViewById(R.id.searchbar);
        search.setIconifiedByDefault(false);
        search.setSubmitButtonEnabled(true);

        final RequestQueue suggestqueue=Volley.newRequestQueue(this);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(MovieActivity.this, "Typed "+newText, Toast.LENGTH_SHORT).show();
                try {
                    suggestion.clear();
                    String query = URLEncoder.encode(newText, "utf-8").replaceAll("\\+", "%20");
                    String url="https://api.themoviedb.org/3/search/movie?api_key=47125e67d25d22f00aebbf4f6d08a3aa&query="+query;
                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray array=response.getJSONArray("results");
                                for(int i=0;i<array.length();i++){

                                    String movie_name=array.getJSONObject(i).getString("title");
                                    String movie_date=array.getJSONObject(i).getString("release_date");
                                    String movie_year="";
                                    if(movie_date.length()!=0){
                                        movie_year=movie_date.substring(0,4);
                                    }
                                    System.out.println("movie_year = " + movie_year);
                                    int id=array.getJSONObject(i).getInt("id");
                                    suggestion.add(new Movie(movie_name,movie_year,id));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            s_adapter.notifyDataSetChanged();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    suggestqueue.add(req);
                }catch(Exception e){


                }

                return true;
            }
        });

        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

    }

    @Override
    public void onBackPressed() {

        if(l==1){
            movie_list.setVisibility(View.VISIBLE);
            search.setVisibility(View.GONE);
            l=0;
        }else {
            super.onBackPressed();
        }
    }
}
