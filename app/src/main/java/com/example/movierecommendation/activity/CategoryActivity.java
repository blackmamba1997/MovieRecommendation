package com.example.movierecommendation.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movierecommendation.R;
import com.example.movierecommendation.adapter.GridLayoutAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {
    RecyclerView gridrecyclerview;
    RecyclerView.Adapter gridlayoutadapter;
    RecyclerView.LayoutManager layoutManager;
    RequestQueue requestQueue;
    String base_url;
    public static ArrayList<JSONObject> gridlist;
    int c=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Intent intent = getIntent();
        base_url = intent.getStringExtra("url");

        gridlist=new ArrayList<>();

        gridrecyclerview=findViewById(R.id.gridlayout);
        layoutManager=new GridLayoutManager(this,3);
        gridrecyclerview.setLayoutManager(layoutManager);
        gridlayoutadapter=new GridLayoutAdapter(this,base_url);

        gridlayoutadapter.setHasStableIds(true);

        gridrecyclerview.setAdapter(gridlayoutadapter);

        requestQueue= Volley.newRequestQueue(this);


        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(base_url+"&page="+(c++), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array=response.getJSONArray("results");
                    for (int i=0;i<array.length();i++){

                        gridlist.add(array.getJSONObject(i));
                    }
                    gridlayoutadapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },null);
        requestQueue.add(jsonObjectRequest);

        gridrecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE){
                    Toast.makeText(CategoryActivity.this,"last",Toast.LENGTH_SHORT).show();
                    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(base_url+"&page="+(c++), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray array=response.getJSONArray("results");
                                for (int i=0;i<array.length();i++){

                                    gridlist.add(array.getJSONObject(i));
                                }
                                gridlayoutadapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },null);
                    requestQueue.add(jsonObjectRequest);
                }
            }
        });
    }
}