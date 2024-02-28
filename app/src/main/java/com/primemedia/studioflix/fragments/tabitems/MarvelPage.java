package com.primemedia.studioflix.fragments.tabitems;

import static com.primemedia.studioflix.Dashboard.bottomBar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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


public class MarvelPage extends Fragment {

    View view;

    LinearLayout Marv_layout, POne, Phase_Two_layout, phase_3_layout, phase_4_layout, phase_5_layout, phase6_layout, live_webseries_more_layoyt, spi_layout, legacyMovies_layout, aven_verse, ironverse_layout, animation_legacy;
    ImageView more_multi, phase_1, more_P2, phase_3, phase_4, phase_5, live_webseries_more;
    RecyclerView marvelVerse_recyclerview, Phase5_recyclerview, Phase6_recyclerview, IronmanVerse_recyclerview, spiderVerse_recyclerview, Movieslegacy_recyclerview, Avengers_Recyclerview,
            Phase1_recyclerview, phase2_recyclerview, phase3_recyclerview, Phase4_recyclerview, Univ_timeline_recyclerview, series_live_recyclerview, Marvel_animation_recyclerview;
    String Marvel_Cinematic_Multiverse = "Marvel_Cinematic_Multiverse";
    String Marvel_Phase_one = "Marvel_Phase_one";
    String Marvel_phase_two = "Marvel_phase_two";
    String Marvel_phase_three = "Marvel_phase_three";
    String marvel_phase_four = "marvel_phase_four";
    String Marvel_phase_five = "Marvel_phase_five";

    FragmentManager fragmentManager;

    FragmentTransaction fragmentTransaction;
    String Marvel_phase_six = "Marvel_phase_six";
    String MCU_Timelineorder = "MCU_Timelineorder";
    String SpiderVerse = "SpiderVerse";
    String live_series = "live_series";
    String legacyMovies = "legacyMovies";
    String Avengers_verse = "Avengers_verse";
    String IronmanVerse = "IronmanVerse";
    String legacyANimation = "legacy_anime";
    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setStatusBar();
        view = inflater.inflate(R.layout.fragment_marvel_page, container, false);
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(requireActivity(), WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        Marv_layout = view.findViewById(R.id.Marv_layout);
        more_multi = view.findViewById(R.id.more_multi);
        marvelVerse_recyclerview = view.findViewById(R.id.marvelVerse_recyclerview);
        POne = view.findViewById(R.id.POne);
        phase_1 = view.findViewById(R.id.phase_1);

        Phase1_recyclerview = view.findViewById(R.id.Phase1_recyclerview);
        aven_verse = view.findViewById(R.id.aven_verse);
        Phase_Two_layout = view.findViewById(R.id.Phase_Two_layout);
        Marvel_animation_recyclerview = view.findViewById(R.id.Marvel_animation_recyclerview);
        more_P2 = view.findViewById(R.id.more_P2);
        ironverse_layout = view.findViewById(R.id.ironverse_layout);
        phase2_recyclerview = view.findViewById(R.id.phase2_recyclerview);
        phase_3_layout = view.findViewById(R.id.phase_3_layout);
        phase_3 = view.findViewById(R.id.phase_3);
        phase3_recyclerview = view.findViewById(R.id.Phase3_recyclerview);
        phase_4_layout = view.findViewById(R.id.phase_4_layout);
        phase_4 = view.findViewById(R.id.phase_4);
        IronmanVerse_recyclerview = view.findViewById(R.id.IronmanVerse_recyclerview);
        Phase4_recyclerview = view.findViewById(R.id.Phase4_recyclerview);
        phase6_layout = view.findViewById(R.id.phase6_layout);
        Phase6_recyclerview = view.findViewById(R.id.Phase6_recyclerview);
        phase_5_layout = view.findViewById(R.id.phase_5_layout);
        phase_5 = view.findViewById(R.id.phase_5);
        Phase5_recyclerview = view.findViewById(R.id.Phase5_recyclerview);
        Univ_timeline_recyclerview = view.findViewById(R.id.Univ_timeline_recyclerview);
        live_webseries_more_layoyt = view.findViewById(R.id.live_webseries_more_layoyt);
        live_webseries_more = view.findViewById(R.id.live_webseries_more);
        spiderVerse_recyclerview = view.findViewById(R.id.spiderVerse_recyclerview);
        spi_layout = view.findViewById(R.id.spi_layout);
        legacyMovies_layout = view.findViewById(R.id.legacyMovies_layout);
        Movieslegacy_recyclerview = view.findViewById(R.id.Movieslegacy_recyclerview);
        Avengers_Recyclerview = view.findViewById(R.id.Avengers_Recyclerview);
        animation_legacy = view.findViewById(R.id.animation_legacy);
        series_live_recyclerview = view.findViewById(R.id.series_live_recyclerview);
        AssembleMarvelFilms();
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle back press event here
                // For example, navigate to the previous fragment
                navigateToPreviousFragment();
            }
        };

        // Add the callback to the activity's OnBackPressedDispatcher
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        return view;
    }

    private void navigateToPreviousFragment() {

        fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Home homeFragment = new Home();
        fragmentTransaction.replace(R.id.contaner, homeFragment);
        fragmentTransaction.commit();
        bottomBar.setActiveItem(0);
        bottomBar.setBadge(0);
        bottomBar.setSelected(true);
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void setStatusBar() {
        Window window = requireActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        {
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void AssembleMarvelFilms() {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr1 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + Marvel_Cinematic_Multiverse, response -> {
            if (!response.equals("No Data Avaliable")) {
                Marv_layout.setVisibility(View.VISIBLE);
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
                marvelVerse_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                marvelVerse_recyclerview.setAdapter(myadepter);

            } else {
                Marv_layout.setVisibility(View.GONE);
            }
        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr1);
        StringRequest sr2 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + Marvel_Phase_one, response -> {
            Log.d("phase", response);
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
                if (searchList.size() >= 2) {
                    Collections.shuffle(searchList.subList(0, 3));
                }
                Collections.reverse(searchList);
                SearchListAdepter myadepter = new SearchListAdepter(mContext, searchList);
                Phase1_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                Phase1_recyclerview.setAdapter(myadepter);
//                home_swoper.setRefreshing(false);
            }

        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr2);
        StringRequest sr3 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + Marvel_phase_two, response -> {

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
                if (searchList.size() >= 2) {
                    Collections.shuffle(searchList.subList(0, 2));
                }
                Collections.reverse(searchList);
                SearchListAdepter myadepter = new SearchListAdepter(mContext, searchList);
                phase2_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                phase2_recyclerview.setAdapter(myadepter);
//                home_swoper.setRefreshing(false);
            }

        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr3);
        StringRequest sr4 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + Marvel_phase_three, response -> {
            Log.d("Data_phase3", response);
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
                phase3_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                phase3_recyclerview.setAdapter(myadepter);
//                home_swoper.setRefreshing(false);
            }

        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr4);
        StringRequest sr5 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + marvel_phase_four, response -> {
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
                Phase4_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                Phase4_recyclerview.setAdapter(myadepter);

            }

        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr5);
        StringRequest sr6 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + Marvel_phase_five, response -> {
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
                Phase5_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                Phase5_recyclerview.setAdapter(myadepter);

            }

        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr6);
        StringRequest sr7 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + Marvel_phase_six, response -> {
            if (!response.equals("No Data Avaliable")) {
                phase6_layout.setVisibility(View.VISIBLE);
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
                Phase6_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                Phase6_recyclerview.setAdapter(myadepter);

            } else {
                phase6_layout.setVisibility(View.GONE);
            }

        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr7);
        StringRequest sr8 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + MCU_Timelineorder, response -> {
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
                Univ_timeline_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                Univ_timeline_recyclerview.setAdapter(myadepter);

            }

        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr8);
        StringRequest sr9 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + live_series, response -> {
            if (!response.equals("No Data Avaliable")) {
                live_webseries_more_layoyt.setVisibility(View.VISIBLE);
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
                series_live_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                series_live_recyclerview.setAdapter(myadepter);

            } else {
                live_webseries_more_layoyt.setVisibility(View.GONE);
            }
        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr9);
        StringRequest sr10 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + SpiderVerse, response -> {
            if (!response.equals("No Data Avaliable")) {
                spi_layout.setVisibility(View.VISIBLE);
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
                spiderVerse_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                spiderVerse_recyclerview.setAdapter(myadepter);

            } else {
                spi_layout.setVisibility(View.GONE);
            }
        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr10);
        StringRequest sr11 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + legacyMovies, response -> {
            if (!response.equals("No Data Avaliable")) {
                legacyMovies_layout.setVisibility(View.VISIBLE);
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
                Movieslegacy_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                Movieslegacy_recyclerview.setAdapter(myadepter);

            } else {
                legacyMovies_layout.setVisibility(View.GONE);
            }
        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr11);
        StringRequest sr12 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + Avengers_verse, response -> {
            if (!response.equals("No Data Avaliable")) {
                aven_verse.setVisibility(View.VISIBLE);
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
                Avengers_Recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                Avengers_Recyclerview.setAdapter(myadepter);

            } else {
                aven_verse.setVisibility(View.GONE);
            }
        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr12);
        StringRequest sr13 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + IronmanVerse, response -> {
            if (!response.equals("No Data Avaliable")) {
                ironverse_layout.setVisibility(View.VISIBLE);
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
                IronmanVerse_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                IronmanVerse_recyclerview.setAdapter(myadepter);

            } else {
                ironverse_layout.setVisibility(View.GONE);
            }
        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr13);
        StringRequest sr14 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + legacyANimation, response -> {
            if (!response.equals("No Data Avaliable")) {
                animation_legacy.setVisibility(View.VISIBLE);
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
                Marvel_animation_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                Marvel_animation_recyclerview.setAdapter(myadepter);

            } else {
                animation_legacy.setVisibility(View.GONE);
            }
        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr14);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

}