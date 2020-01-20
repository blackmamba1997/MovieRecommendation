package com.example.movierecommendation.ui.activities;

import org.json.JSONObject;

public class Movie_category {

    public JSONObject category_list;
    public String category_type;
    public String category_base_url;

    public Movie_category(JSONObject category_list,String category_type,String url){

        this.category_list=category_list;
        this.category_type=category_type;
        category_base_url=url;

    }
}
