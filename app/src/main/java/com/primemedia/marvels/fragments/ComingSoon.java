package com.primemedia.marvels.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.primemedia.marvels.adapter.FreshreleaseAdapter;
import com.primemedia.marvels.adapter.MovieListAdepter;
import com.primemedia.marvels.list.ComingSoonItem;
import com.primemedia.marvels.list.MovieList;
import com.primemedia.marvels.utility.ConfigManager;
import com.primemedia.marvels.utility.GospelUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComingSoon extends Fragment {
    View view;
    RecyclerView NewVideos;
    Context mContext;
    int shuffleContents;
    ImageView upcoming_logoview;
    String genres;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        NewVideos = view.findViewById(R.id.NewVideos);
        upcoming_logoview = view.findViewById(R.id.upcoming_logoview);
        try {
            JSONObject configObject = ConfigManager.loadConfig(mContext);
            shuffleContents = configObject.getInt("shuffle_contents");
        } catch (Exception e) {
            //
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr1 = new StringRequest(Request.Method.GET, Constants.url + "getRecentContentList/Movies", response -> {
            if (!response.equals("No Data Available")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<ComingSoonItem> recentlyAddedMovieList = new ArrayList<>();
                int maxMoviesToShow = 10; // Set the maximum number of movies to show

                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String certificate_type = rootObject.get("certificate_type").getAsString();
                    String logo = rootObject.get("logo_image").getAsString();
                    String year = "";
                    String description = rootObject.get("description").getAsString();
                    if (!rootObject.get("release_date").getAsString().equals("")) {
                        year = GospelUtil.getYearFromDate(rootObject.get("release_date").getAsString());
                    }
                    genres = rootObject.get("genres").getAsString();
                    String poster = rootObject.get("banner").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1)
                    {
                        recentlyAddedMovieList.add(new ComingSoonItem(id, type, name, year, poster, logo, "", "PG-13", description, genres));
                    }

                    // Limit the number of movies to fetch
                    if (recentlyAddedMovieList.size() >= maxMoviesToShow) {
                        break;
                    }
                }

                // Shuffle the list if needed
                if (shuffleContents == 1) {
                    Collections.shuffle(recentlyAddedMovieList);
                }

                FreshreleaseAdapter myAdapter = new FreshreleaseAdapter(mContext, recentlyAddedMovieList);
                NewVideos.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.VERTICAL, false));
                NewVideos.setAdapter(myAdapter);


            } else {

            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr1);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}