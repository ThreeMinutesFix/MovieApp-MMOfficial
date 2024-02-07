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
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.primemedia.marvels.adapter.ReletedMovieListAdepter;
import com.primemedia.marvels.list.MovieList;
import com.primemedia.marvels.utility.GospelUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MovieMorelikethis extends Fragment {

    View view;
    int id, TMDB_ID;
    String genres;
    RecyclerView moreLikeThis;
    Context mcontext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_movie_morelikethis, container, false);
        moreLikeThis = view.findViewById(R.id.moreLikeThis);
        Intent intent = requireActivity().getIntent();
        id = intent.getExtras().getInt("ID");
        loadMovieDetails(id);
        return view;
    }

    private void loadMovieDetails(int id) {
        RequestQueue queue = Volley.newRequestQueue(mcontext);
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getMovieDetails/" + id, response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            TMDB_ID = jsonObject.get("TMDB_ID").getAsInt();
            genres = jsonObject.get("genres").getAsString();
            getRelated(genres);

        }, error -> {
            // Do nothing
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

    private void getRelated(String genres) {
        RequestQueue queue = Volley.newRequestQueue(mcontext);
        StringRequest sr = new StringRequest(Request.Method.POST, Constants.url + "getRelatedMovies/" + id + "/10", response -> {
            Log.d("mOreLikeThis", response);
            if (!response.equals("No Data Avaliable")) {

                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<MovieList> movieList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int m_id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if (!rootObject.get("release_date").getAsString().equals("")) {
                        year = GospelUtil.getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1 && id != m_id) {
                        movieList.add(new MovieList(m_id, type, name, year, poster, "", ""));
                    }
                    Collections.shuffle(movieList);
                    ReletedMovieListAdepter myadepter = new ReletedMovieListAdepter(mcontext, movieList);
                    moreLikeThis.setLayoutManager(new GridLayoutManager(mcontext, 3, RecyclerView.VERTICAL, false));
                    moreLikeThis.setAdapter(myadepter);
                }
            } else {

            }

        }, error -> {
            // Do nothing
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("genres", String.valueOf(genres));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mcontext = context;
    }
}