package com.primemedia.studioflix.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.primemedia.studioflix.Constants;
import com.primemedia.studioflix.R;
import com.primemedia.studioflix.adapter.SearchListAdepter;
import com.primemedia.studioflix.list.SearchList;
import com.primemedia.studioflix.utility.GospelUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Warner extends Fragment {
    View view;
    String New_DCU = "New_DCU";
    String DCEU_Timeline_order = "DCEU_Timeline_order";
    String batman_movies = "Batman";
    String superman_movies = "Superman";
    RecyclerView dc_movies, dc_movies_extended,superman_movies_recyclerview,batman_movies_recyclerview;
    Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_warners, container, false);
        dc_movies = view.findViewById(R.id.dc_movies);
        dc_movies_extended = view.findViewById(R.id.dc_movies_extended);
        superman_movies_recyclerview = view.findViewById(R.id.superman_movies);
        batman_movies_recyclerview = view.findViewById(R.id.batman_movies);


        loadMovies();

        return view;
    }

    private void loadMovies()
    {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr1 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + New_DCU, response -> {
            if (!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<SearchList> searchList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String contentName = rootObject.get("name").getAsString();

                    String year = "";
                    if (!rootObject.get("release_date").getAsString().equals("")) {
                        year = GospelUtil.getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();
                    int contentType = rootObject.get("content_type").getAsInt();

                    if (status == 1) {
                        searchList.add(new SearchList(id, type, contentName, year, poster, contentType));
                    }
                }
                Collections.reverse(searchList);
                SearchListAdepter myadepter = new SearchListAdepter(mContext, searchList);
                dc_movies.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                dc_movies.setAdapter(myadepter);

            } else {

            }
        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr1);
        StringRequest sr2 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + DCEU_Timeline_order, response -> {
            if (!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<SearchList> searchList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String contentName = rootObject.get("name").getAsString();

                    String year = "";
                    if (!rootObject.get("release_date").getAsString().equals("")) {
                        year = GospelUtil.getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();
                    int contentType = rootObject.get("content_type").getAsInt();

                    if (status == 1) {
                        searchList.add(new SearchList(id, type, contentName, year, poster, contentType));
                    }
                }
                Collections.reverse(searchList);
                SearchListAdepter myadepter = new SearchListAdepter(mContext, searchList);
                dc_movies_extended.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                dc_movies_extended.setAdapter(myadepter);

            } else {

            }
        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr2);
        StringRequest sr3 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + batman_movies, response -> {
            if (!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<SearchList> searchList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String contentName = rootObject.get("name").getAsString();

                    String year = "";
                    if (!rootObject.get("release_date").getAsString().equals("")) {
                        year = GospelUtil.getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();
                    int contentType = rootObject.get("content_type").getAsInt();

                    if (status == 1) {
                        searchList.add(new SearchList(id, type, contentName, year, poster, contentType));
                    }
                }
                Collections.reverse(searchList);
                SearchListAdepter myadepter = new SearchListAdepter(mContext, searchList);
                batman_movies_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                batman_movies_recyclerview.setAdapter(myadepter);

            } else {

            }
        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr3);
        StringRequest sr4 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + superman_movies, response -> {
            if (!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<SearchList> searchList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String contentName = rootObject.get("name").getAsString();

                    String year = "";
                    if (!rootObject.get("release_date").getAsString().equals("")) {
                        year = GospelUtil.getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();
                    int contentType = rootObject.get("content_type").getAsInt();

                    if (status == 1) {
                        searchList.add(new SearchList(id, type, contentName, year, poster, contentType));
                    }
                }
                Collections.reverse(searchList);
                SearchListAdepter myadepter = new SearchListAdepter(mContext, searchList);
                superman_movies_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                superman_movies_recyclerview.setAdapter(myadepter);

            } else {

            }
        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr4);
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

}