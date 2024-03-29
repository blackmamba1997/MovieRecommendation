package com.example.movierecommendation.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movierecommendation.NotificationService;
import com.example.movierecommendation.adapter.MovieList_Adapter;
import com.example.movierecommendation.R;
import com.example.movierecommendation.adapter.SuggestionListAdapter;
import com.example.movierecommendation.ui.activities.Filter;
import com.example.movierecommendation.ui.activities.Movie;
import com.example.movierecommendation.ui.activities.Movie_category;
import com.example.movierecommendation.ui.activities.Url;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

public class MovieActivity extends AppCompatActivity {

    LinearLayoutManager manager;
    LinearLayoutManager suggestionmanager;
    RecyclerView movie_list;
    RecyclerView suggest_list;
    MovieList_Adapter adapter;
    SuggestionListAdapter s_adapter;
    Toolbar toolbar;
    ProgressBar loading_main;
    SearchView search;
    LinearLayout searchfilterlayout;
    ImageView filterbutton;
    FirebaseFirestore db;
    AlarmManager alarmManager;
    PendingIntent pending;
    String selectedgenre = "", selectedyear = "", selectedsort = "sort_by=popularity.desc", selectedlang = "";
    Button searchbutton, cancelbutton;
    int l = 0, request_counter = 0, category_counter = 0;

    public static ArrayList<Movie_category> jsonObjects;
    public static ArrayList<Movie> suggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        System.out.println(FirebaseAuth.getInstance().getCurrentUser().getUid());
        toolbar = findViewById(R.id.tool_bar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        loading_main = findViewById(R.id.loading_main_screen);
        searchfilterlayout = findViewById(R.id.movie_activity_searchandfilter_layout);
        db = FirebaseFirestore.getInstance();

        //new notification
        checkNotification();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_logout) {

                    //cancel existing pendingintent reference using cancel()of AlarmManager instance
                    cancelIntent();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MovieActivity.this, LoginActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.action_search) {
                    if (l == 0) {

                        Toast.makeText(MovieActivity.this, "pressed the search", Toast.LENGTH_SHORT).show();
                        movie_list.setVisibility(View.GONE);
                        searchfilterlayout.setVisibility(View.VISIBLE);
                        suggest_list.setVisibility(View.VISIBLE);
                        l = 1;

                    } else {

                        Toast.makeText(MovieActivity.this, "pressed the search off", Toast.LENGTH_SHORT).show();
                        movie_list.setVisibility(View.VISIBLE);
                        searchfilterlayout.setVisibility(View.GONE);
                        suggest_list.setVisibility(View.GONE);
                        l = 0;

                    }
                    //new profile section
                } else if (id == R.id.action_settings) {
                    Toast.makeText(MovieActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MovieActivity.this, ProfileActivity.class));
                    return true;
                }
                //end of the block
                return false;
            }
        });

        jsonObjects = new ArrayList<>();
        suggestion = new ArrayList<>();

        //creating a recyclerview with vertical linear layout for the main page
        //each view in the list will hold a recyclerview with horizontal linear layout

        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        movie_list = findViewById(R.id.movie_list);
        movie_list.setLayoutManager(manager);
        adapter = new MovieList_Adapter(MovieActivity.this);
        movie_list.setAdapter(adapter);

        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Creation of  JsonoOjects request objects of three categories: popular,top rated and now playing and adding them to request queue

        JsonObjectRequest nowplaying_json = new JsonObjectRequest(Request.Method.GET, "https://api.themoviedb.org/3/movie/now_playing?api_key=4" +
                "7125e67d25d22f00aebbf4f6d08a3aa&language=en-US&page=1", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //inserting now playing json object into arraylist

                jsonObjects.add(new Movie_category(response, "Now Playing", "https://api.themoviedb.org/3/movie/now_playing?api_key=4" +
                        "7125e67d25d22f00aebbf4f6d08a3aa&language=en-US"));

                adapter.notifyDataSetChanged();

                //System.out.println(response.toString());

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

                //System.out.println(response.toString());

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

                //System.out.println(response.toString());

            }
        }, null);
        requestQueue.add(popular_json);

        //Testing out the request of data from server

        DocumentReference movie = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        movie.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String id = "";
                        String keyword = "";
                        if (task.getResult().exists()) {
                            if (task.getResult().contains("recent") && task.getResult().contains("keyword")) {
                                System.out.println(" found the KEyWORDS");
                                id = task.getResult().getString("recent");
                                try {
                                    keyword = URLEncoder.encode(task.getResult().getString("keyword"), "utf-8").replaceAll("\\+", "%20");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            JsonObjectRequest prev_movie_similar = new JsonObjectRequest("https://cinemabuna.herokuapp.com/home/test/" + id + "/" + keyword + "/", null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    System.out.println("inside the method");
                                    try {
                                        final JSONArray jsonarray = response.getJSONArray("result");

                                        final JSONArray array = new JSONArray();
                                        for (int i = 0; i < jsonarray.length(); i++) {
                                            String movie_id = jsonarray.getJSONObject(i).getString("id");
                                            JsonObjectRequest ob = new JsonObjectRequest(Url.movieurl + movie_id + "?" + Url.api_key, null, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    System.out.println("SUCCESS!!!!!");
                                                    array.put(response);
                                                    System.out.println(" array= " + array.toString());
                                                    request_counter++;
                                                    if (request_counter == jsonarray.length()) {
                                                        System.out.println("inside if");
                                                        JSONObject jsonObject = new JSONObject();
                                                        try {
                                                            jsonObject.put("results", array);
                                                            jsonObjects.add(0, new Movie_category(jsonObject, "Based on Recent Search", null));
                                                            adapter.notifyDataSetChanged();
                                                            movie_list.setVisibility(View.VISIBLE);
                                                            loading_main.setVisibility(View.GONE);
                                                        } catch (JSONException e) {
                                                            System.out.println("error in receiving");
                                                            e.printStackTrace();
                                                        }

                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    System.out.println("ERROR in receiving");
                                                    request_counter++;
                                                    if (request_counter == jsonarray.length()) {
                                                        System.out.println("inside if");
                                                        JSONObject jsonObject = new JSONObject();
                                                        try {
                                                            jsonObject.put("results", array);
                                                            jsonObjects.add(0, new Movie_category(jsonObject, "Based on Recent Search", null));
                                                            adapter.notifyDataSetChanged();
                                                            movie_list.setVisibility(View.VISIBLE);
                                                            loading_main.setVisibility(View.GONE);
                                                        } catch (JSONException e) {
                                                            System.out.println("error in receiving");
                                                            e.printStackTrace();
                                                        }

                                                    }

                                                }
                                            });
                                            requestQueue.add(ob);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    System.out.println("error = " + error);
                                    movie_list.setVisibility(View.VISIBLE);
                                    loading_main.setVisibility(View.GONE);
                                }
                            });

                            requestQueue.add(prev_movie_similar);
                        }
                    }
                });

        /*JsonObjectRequest prev_movie_similar = new JsonObjectRequest("http://192.168.43.139:8000/home/test//", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("inside the method");
                try {
                    final JSONArray jsonarray = response.getJSONArray("result");

                    final JSONArray array = new JSONArray();
                    for (int i = 0; i < jsonarray.length(); i++) {
                        String movie_id = jsonarray.getJSONObject(i).getString("id");
                        JsonObjectRequest ob = new JsonObjectRequest(Url.movieurl + movie_id + "?" + Url.api_key, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                array.put(response);
                                System.out.println(" array= " + array.toString());
                                request_counter++;
                                if (request_counter == jsonarray.length()) {
                                    System.out.println("inside if");
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("results", array);
                                        jsonObjects.add(0,new Movie_category(jsonObject, "Based on Recent Search", null));
                                        adapter.notifyDataSetChanged();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }, null);
                        requestQueue.add(ob);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error = " + error);
            }
        });
        requestQueue.add(prev_movie_similar);*/

        //end of test block

        suggest_list = findViewById(R.id.suggestion_list);
        suggestionmanager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        suggest_list.setLayoutManager(suggestionmanager);
        s_adapter = new SuggestionListAdapter();
        suggest_list.setAdapter(s_adapter);

        search = findViewById(R.id.searchbar);
        search.setIconifiedByDefault(false);
        search.setSubmitButtonEnabled(false);

        final RequestQueue suggestqueue = Volley.newRequestQueue(this);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(MovieActivity.this, "Typed "+newText, Toast.LENGTH_SHORT).show();
                suggestion.clear();
                if (newText.length() != 0) {
                    try {
                        String query = URLEncoder.encode(newText, "utf-8").replaceAll("\\+", "%20");
                        String url = "https://api.themoviedb.org/3/search/movie?api_key=47125e67d25d22f00aebbf4f6d08a3aa&query=" + query;
                        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray array = response.getJSONArray("results");
                                    for (int i = 0; i < array.length(); i++) {

                                        String movie_name = array.getJSONObject(i).getString("title");
                                        String movie_date = array.getJSONObject(i).getString("release_date");
                                        String movie_year = "";
                                        if (movie_date.length() != 0) {
                                            movie_year = movie_date.substring(0, 4);
                                        }
                                        System.out.println("movie_year = " + movie_year);
                                        int id = array.getJSONObject(i).getInt("id");
                                        suggestion.add(new Movie(movie_name, movie_year, id));
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

                    } catch (Exception e) {

                    }
                } else {
                    s_adapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        JsonObjectRequest genrereq = new JsonObjectRequest(Request.Method.GET, Url.base_url + "/genre/movie/list?" + Url.api_key, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    Filter.getGenre(response);
                    Filter.getYear();
                    Filter.getSort();
                    Filter.getLanguage();
                    for (int i = 0; i < Filter.genre.size(); i++)
                        System.out.println("MovieActivity.onResponse " + Filter.genre.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, null);
        requestQueue.add(genrereq);

        filterbutton = findViewById(R.id.filter);
        filterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(MovieActivity.this);

                View view = getLayoutInflater().inflate(R.layout.filter_dialog_layout, null);
                View view2 = getLayoutInflater().inflate(R.layout.filter_custom_title, null);
                Spinner genre_spinner = view.findViewById(R.id.genre_spinner);
                Spinner year_spinner = view.findViewById(R.id.year_spinner);
                Spinner sort_spinner = view.findViewById(R.id.sort_spinner);
                Spinner lang_spinner = view.findViewById(R.id.language_spinner);
                final Switch adult_switch = view.findViewById(R.id.adult_switch);
                builder.setView(view);
                builder.setCustomTitle(view2);
                final AlertDialog alert = builder.create();
                alert.show();

                ArrayAdapter<String> genreadapter = new ArrayAdapter<String>(MovieActivity.this, android.R.layout.simple_list_item_1, Filter.genre);
                genreadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                genre_spinner.setAdapter(genreadapter);

                ArrayAdapter<String> yearadapter = new ArrayAdapter<String>(MovieActivity.this, android.R.layout.simple_list_item_1, Filter.year);
                yearadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                year_spinner.setAdapter(yearadapter);

                ArrayAdapter<String> sortadapter = new ArrayAdapter<String>(MovieActivity.this, android.R.layout.simple_list_item_1, Filter.sort);
                sortadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sort_spinner.setAdapter(sortadapter);

                ArrayAdapter<String> langadapter = new ArrayAdapter<String>(MovieActivity.this, android.R.layout.simple_list_item_1, Filter.language);
                langadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lang_spinner.setAdapter(langadapter);

                genre_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if (position == 0) {
                            selectedgenre = "";
                        } else {
                            selectedgenre = "&with_genres=" + Filter.genreHashMap.get(Filter.genre.get(position)).gid;
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if (position == 0) {
                            selectedyear = "";
                        } else {
                            selectedyear = "&primary_release_year=" + Filter.year.get(position);
                        }

                        System.out.println("MovieActivity.onItemSelected" + selectedyear);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        selectedsort = "&sort_by=" + Filter.sortHashMap.get(Filter.sort.get(position)).key;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                searchbutton = view.findViewById(R.id.dialog_box_search);
                cancelbutton = view.findViewById(R.id.dialog_box_cancel);

                lang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if (position == 0) {
                            selectedlang = "";
                        } else {
                            selectedlang = "&with_original_language=" + Filter.langHashmap.get(Filter.language.get(position));
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                searchbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String adult;

                        if (adult_switch.isChecked()) {

                            adult = "&include_adult=true&certification_country=IN&certification=A";

                        } else {
                            adult = "&include_adult=false&certification_country=IN&certification.lte=UA";
                        }

                        String url = Url.base_url + "/discover/movie?" + Url.api_key + selectedgenre + selectedyear + selectedsort + selectedlang + adult;
                        System.out.println("MovieActivity.onClick " + url);
                        Intent intent = new Intent(MovieActivity.this, CategoryActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });

                cancelbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alert.dismiss();
                    }
                });

            }
        });


    }

    @Override
    public void onBackPressed() {

        if (l == 1) {
            movie_list.setVisibility(View.VISIBLE);
            searchfilterlayout.setVisibility(View.GONE);
            l = 0;
        } else {
            super.onBackPressed();
        }
    }

    //notification feature
    private void checkNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "channel 1 notification";
            String description = "Channel notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Channel 1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent alarmService = new Intent(this, NotificationService.class);
        pending = PendingIntent.getService(getApplicationContext(), 1997, alarmService, PendingIntent.FLAG_NO_CREATE);
        System.out.println("The result is :"+pending);
        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);

        if (pending==null) {
            System.out.println("inside this if");
            pending = PendingIntent.getService(getApplicationContext(), 1997, alarmService, 0);
            System.out.println("if result :"+pending);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_HALF_HOUR,pending);
        }

    }

    private void cancelIntent(){
        Intent alarmService = new Intent(this, NotificationService.class);
        System.out.println("the cancel result is : "+pending);
        if (pending!=null){
            System.out.println("Cancel existing pendingintent");
            alarmManager.cancel(pending);
            pending.cancel();
        }
    }
}
