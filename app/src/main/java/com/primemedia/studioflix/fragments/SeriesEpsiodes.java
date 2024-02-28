package com.primemedia.studioflix.fragments;

import static android.app.Activity.RESULT_OK;

import static com.primemedia.studioflix.WebSeriesDetails.tab_content_loader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.primemedia.studioflix.Constants;
import com.primemedia.studioflix.R;
import com.primemedia.studioflix.adapter.EpisodeListAdepter;
import com.primemedia.studioflix.list.EpisodeList;
import com.primemedia.studioflix.player.WebPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SeriesEpsiodes extends Fragment {
    MaterialSpinner seasonSpinner;
    View EpView;
    int mainId;
    Context mContext;
    boolean playPremium = true;
    List<EpisodeList> episodeList;
    EpisodeListAdepter myadepter;
    View rootView;
    int webSeriesEpisodeitemType = 0;
    int type;
    RecyclerView episodeListRecyclerView;
    String banner;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        EpView = inflater.inflate(R.layout.fragment_series_epsiodes, container, false);
        rootView = EpView.findViewById(R.id.Epview);
        Intent intent = requireActivity().getIntent();
        mainId = intent.getExtras().getInt("ID");
        loadSeasons(mainId);
        seasonSpinner = EpView.findViewById(R.id.seasonSpinner);
        episodeListRecyclerView = EpView.findViewById(R.id.episode_list_RecyclerView);
        return EpView;
    }


    private void loadSeasons(int mainId) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getSeasons/" + mainId, response -> {
            if (!response.equals("No Data Avaliable")) {

                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);

                List<String> seasonList = new ArrayList<>();

                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();

                    String sessionName = rootObject.get("Session_Name").getAsString();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        seasonList.add(sessionName);
                    }
                }
                if (!seasonList.isEmpty()) {
                    seasonSpinner.setVisibility(View.VISIBLE);

                    seasonSpinner.setItems(seasonList);

                    seasonSpinner.setSelectedIndex(0);

                    loadSeasonDetails(mainId, (String) seasonSpinner.getText());

                    seasonSpinner.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> loadSeasonDetails(mainId, item));
                } else {
                    seasonSpinner.setVisibility(View.GONE);
                }

            } else {
                seasonSpinner.setVisibility(View.GONE);
            }
        }, error -> {
            //Do Nothing
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

    private void loadSeasonDetails(int id, String item) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.POST, Constants.url + "getSeasonDetails", response -> {
            if (!response.equals("No Data Avaliable")) {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                int seasonId = jsonObject.get("id").getAsInt();
                loadEpisoades(id, seasonId);
            }
        }, error -> {
            // Do nothing
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("WebSeriesID", String.valueOf(id));
                params.put("seasonName", item);
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

    private void loadEpisoades(int id, int seasonId) {
        if (episodeList != null) {
            episodeList.clear();
            myadepter.notifyDataSetChanged();
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);
        @SuppressLint("NotifyDataSetChanged") StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getEpisodes/" + seasonId, response -> {
            if (!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                episodeList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();

                    int Sid = rootObject.get("id").getAsInt();
                    String episoadeName = rootObject.get("Episoade_Name").getAsString();
                    String episoadeImage = rootObject.get("episoade_image").getAsString();
                    String episoadeDescription = rootObject.get("episoade_description").getAsString();
                    int currentSeasonId = rootObject.get("season_id").getAsInt();
                    int downloadable = rootObject.get("downloadable").getAsInt();
                    int status = rootObject.get("status").getAsInt();
                    int episodeType = rootObject.get("type").getAsInt();
                    String source = rootObject.get("source").getAsString();
                    String url = rootObject.get("url").getAsString();
                    int skipAvailable = rootObject.get("skip_available").getAsInt();
                    String introStart = rootObject.get("intro_start").getAsString();
                    String introEnd = rootObject.get("intro_end").getAsString();
                    String drm_uuid = rootObject.get("drm_uuid").isJsonNull() ? "" : rootObject.get("drm_uuid").getAsString();
                    String drm_license_uri = rootObject.get("drm_license_uri").isJsonNull() ? "" : rootObject.get("drm_license_uri").getAsString();

                    int nType = 0;
                    if (type == 0) {
                        nType = episodeType;
                    } else if (type == 1) {
                        nType = 0;
                    } else if (type == 2) {
                        nType = 1;
                    }

                    if (status == 1) {
                        if (!episoadeImage.equals("")) {
                            episodeList.add(new EpisodeList(Sid, episoadeName, episoadeImage, episoadeDescription, currentSeasonId, downloadable, nType, source, url, skipAvailable, introStart, introEnd, true, true, drm_uuid, drm_license_uri));
                        } else {
                            episodeList.add(new EpisodeList(Sid, episoadeName, banner, episoadeDescription, currentSeasonId, downloadable, nType, source, url, skipAvailable, introStart, introEnd, true, true, drm_uuid, drm_license_uri));
                        }
                    }
                }


                myadepter = new EpisodeListAdepter(id, mContext, rootView, Constants.url, Constants.apiKey, episodeList,this);
                if (webSeriesEpisodeitemType == 1) {
                    episodeListRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.VERTICAL, false));
                } else {
//                    episodeListRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 1));
                    episodeListRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.VERTICAL, false));
                }

                episodeListRecyclerView.setAdapter(myadepter);
                tab_content_loader.setVisibility(View.GONE);
            } else {
                if (episodeList != null) {
                    episodeList.clear();
                    myadepter.notifyDataSetChanged();
                }
            }
        }, error -> {
            //Do Nothing
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                int currentListPosition = data.getIntExtra("Current_List_Position", 0);
                Log.d("listcurrent",""+currentListPosition);
                int nextListPosition = currentListPosition+1;
            EpisodeList myData = episodeList.get(nextListPosition);

                if(myData.getType() == 0) {

                    Intent intent = new Intent(mContext, WebPlayer.class);
                    intent.putExtra("contentID", mainId);
                    intent.putExtra("SourceID", myData.getId());
                    intent.putExtra("name", myData.getEpisoade_Name());

                    intent.putExtra("source", myData.getSource());
                    intent.putExtra("url", myData.getUrl());

                    intent.putExtra("skip_available", myData.getSkip_available());
                    intent.putExtra("intro_start", myData.getIntro_start());
                    intent.putExtra("intro_end", myData.getIntro_end());

                    intent.putExtra("Content_Type", "WebSeries");
                    intent.putExtra("Current_List_Position", nextListPosition);

                    int rPos = nextListPosition + 1;
                    if (rPos < episodeList.size()) {
                        intent.putExtra("Next_Ep_Avilable", "Yes");
                    } else {
                        intent.putExtra("Next_Ep_Avilable", "No");
                    }

                    startActivityForResult(intent, 1);
                } else {
                    if(playPremium) {
                        Intent intent = new Intent(mContext, WebPlayer.class);
                        intent.putExtra("contentID", mainId);
                        intent.putExtra("SourceID", myData.getId());
                        intent.putExtra("name", myData.getEpisoade_Name());

                        intent.putExtra("source", myData.getSource());
                        intent.putExtra("url", myData.getUrl());

                        intent.putExtra("skip_available", myData.getSkip_available());
                        intent.putExtra("intro_start", myData.getIntro_start());
                        intent.putExtra("intro_end", myData.getIntro_end());

                        intent.putExtra("Content_Type", "WebSeries");
                        intent.putExtra("Current_List_Position", nextListPosition);

                        int rPos = nextListPosition + 1;
                        if (rPos < episodeList.size()) {
                            intent.putExtra("Next_Ep_Avilable", "Yes");
                        } else {
                            intent.putExtra("Next_Ep_Avilable", "No");
                        }

                        startActivityForResult(intent, 1);
                    } else {
                           }
                }
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

}