package com.example.movierecommendation.ui.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Filter {

    public static List<String> genre;
    public static List<String> sort;
    public static List<String> year;
    public static List<String> language;
    public static HashMap<String,Genre> genreHashMap;
    public static HashMap<String,Sort> sortHashMap;
    public static HashMap<String,String> langHashmap;

    public static void getGenre(JSONObject response) throws JSONException {

        genre=new ArrayList<>();
        genreHashMap=new HashMap<>();
        genre.add("All");
        genreHashMap.put("All",new Genre("All",-1,true));
        JSONArray array=response.getJSONArray("genres");
        for(int i=0;i<array.length();i++){

            JSONObject name=array.getJSONObject(i);
            if(!name.getString("name").equals("Western") && !name.getString("name").equals("TV Movie") && !name.getString("name").equals("Documentary")){

                genre.add(name.getString("name"));
                genreHashMap.put(name.getString("name"),new Genre(name.getString("name"),name.getInt("id"),false));
            }
        }
    }

    public static void getYear() {


        year=new ArrayList<>();
        year.add("All");
        for(int i=2019;i>=1970;i--){
            year.add(""+i);
        }
    }

    public static void getSort() {

        sort=new ArrayList<>();
        sortHashMap=new HashMap<>();

        sort.add("Popularity Descending");
        sortHashMap.put("Popularity Descending",new Sort("Popularity Descending","popularity.desc"));
        sort.add("Popularity Ascending");
        sortHashMap.put("Popularity Ascending",(new Sort("Popularity Ascending","popularity.asc")));
        sort.add("Release Date Descending");
        sortHashMap.put("Release Date Descending",new Sort("Release Date Descending","release_date.desc"));
        sort.add("Release Date Ascending");
        sortHashMap.put("Release Date Ascending",new Sort("Release Date Ascending","release_date.asc"));
        sort.add("Rating Descending");
        sortHashMap.put("Rating Descending",new Sort("Rating Descending","vote_average.desc"));
        sort.add("Rating Ascending");
        sortHashMap.put("Rating Ascending",new Sort("Rating Ascending","vote_average.asc"));

    }

    public static void getLanguage(){

        language=new ArrayList<>();
        langHashmap=new HashMap<>();

        language.add("All");
        language.add("English");
        langHashmap.put("English","en");
        language.add("Hindi");
        langHashmap.put("Hindi","hi");
        language.add("Bengali");
        langHashmap.put("Bengali","bn");

    }

    public static class Genre{
        public String gname;
        public int gid;
        public boolean selected;

        Genre(String gn,int gi,boolean s){

            gname=gn;
            gid=gi;
            selected=s;
        }
    }
    public static class Sort{
        public String name,key;
        Sort(String n,String k){

            name=n;
            key=k;

        }
    }

}
