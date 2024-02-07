package com.primemedia.marvels.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.primemedia.marvels.Constants;
import com.primemedia.marvels.R;
import com.primemedia.marvels.adapter.MovieListAdepter;
import com.primemedia.marvels.list.MovieList;
import com.primemedia.marvels.utility.GospelUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CollectionsMovies extends Fragment {
    View view;
    RecyclerView collectionView;
    Context mContext;
    int id;
    String name;
    int shuffleContents;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_collections_movies, container, false);
        collectionView = view.findViewById(R.id.collectionview);
        Intent intent = requireActivity().getIntent();
        id = intent.getExtras().getInt("ID");
        loadMovieDetails(id);
        return view;
    }

    private void loadMovieDetails(int id) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getMovieDetails/" + id, response -> {

            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            name = jsonObject.get("name").getAsString();
            loadCollections(name);
        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr);
    }

    private void loadCollections(String movieName) {

        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getAllMovies/0", response -> {
            Log.d("AllMovies", response);
            if (!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<MovieList> collectionList = new ArrayList<>();

                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String collectionName = rootObject.get("name").getAsString();

                    if (isSamePrefix(collectionName, movieName)) {
                        Log.d("collectionName", collectionName);
                        String certificateType = rootObject.get("certificate_type").getAsString();
                        String year = "";
                        if (!rootObject.get("release_date").getAsString().equals("")) {
                            year = GospelUtil.getYearFromDate(rootObject.get("release_date").getAsString());
                        }

                        String poster = rootObject.get("poster").getAsString();
                        int type = rootObject.get("type").getAsInt();
                        int status = rootObject.get("status").getAsInt();

                        if (status == 1) {
                            collectionList.add(new MovieList(id, type, collectionName, year, poster, "", certificateType));
                        }
                    }
                }
                if (shuffleContents == 1) {
                    Collections.shuffle(collectionList);
                }
                updateUIWithRelatedMovies(collectionList);


            } else {

            }
        }, error ->
        {
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr);
    }

    private void updateUIWithRelatedMovies(List<MovieList> relatedMoviesList) {
        MovieListAdepter myAdapter = new MovieListAdepter(mContext, relatedMoviesList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
        collectionView.setLayoutManager(layoutManager);
        collectionView.setAdapter(myAdapter);
    }

    private boolean isSamePrefix(String str1, String str2) {

        int minLength = Math.min(str1.length(), str2.length());

        for (int i = 0; i < minLength; i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}