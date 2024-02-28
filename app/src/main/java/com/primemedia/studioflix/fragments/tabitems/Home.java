package com.primemedia.studioflix.fragments.tabitems;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

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
import com.primemedia.studioflix.adapter.AllGenreListAdepter;
import com.primemedia.studioflix.adapter.ContinuePlayingListAdepter;
import com.primemedia.studioflix.adapter.ImageSliderAdepter;
import com.primemedia.studioflix.adapter.MovieListAdepter;
import com.primemedia.studioflix.adapter.SearchListAdepter;
import com.primemedia.studioflix.adapter.TrendingListAdepter;
import com.primemedia.studioflix.adapter.moviesOnlyForYouListAdepter;
import com.primemedia.studioflix.fragments.Warner;
import com.primemedia.studioflix.list.ContinuePlayingList;
import com.primemedia.studioflix.list.GenreList;
import com.primemedia.studioflix.list.ImageSliderItem;
import com.primemedia.studioflix.list.MovieList;
import com.primemedia.studioflix.list.SearchList;
import com.primemedia.studioflix.list.TrendingList;
import com.primemedia.studioflix.resume_content.ResumeContent;
import com.primemedia.studioflix.resume_content.ResumeContentDatabase;
import com.primemedia.studioflix.utility.ConfigManager;
import com.primemedia.studioflix.utility.GospelUtil;
import com.primemedia.studioflix.utility.UserManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Home extends Fragment {
    View tab_custom;
    public RecyclerView categories;
    LinearLayout close_btn;
    String imageSliderType;
    LinearLayout interested_layout;
    View moviesShimmerLayout;
    RecyclerView continue_release_recyclerview;
    List<ImageSliderItem> imageSliderItems;
    SwipeRefreshLayout home_swipper;
    View view;
    ViewPager2 HomeViewpager;
    NestedScrollView nestedScrollView;
    FragmentTransaction fragmentTransaction;
    public static LinearLayout resume_Layout;
    Bitmap bitmap;
    private final Handler sliderHandler = new Handler();
    Context mContext;
    int userID;
    int shuffleContents;
    FragmentManager fragmentManager;
    int movieImageSliderMaxVisible;
    RecyclerView drama_release_recyclerview, DC_release_recyclerview;
    RecyclerView recenly_release_recyclerview, superherorecyclerview, top_trending_release_recyclerview, home_bywm_list_Recycler_View, sceince_release_recyclerview;
    String Cat_name = "superhero";
    String Cat_name3 = "DCEU_Timeline_order";
    String Cat_name2 = "Sci-Fi";
    public static View genre_initlizer;
    String Cat_name6 = "Kids";
    TextView movies, warner, more_items, paramount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setStatusBar();
        view = inflater.inflate(R.layout.fragment_home, container, false);
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(requireActivity(), WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        requireActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        HomeViewpager = view.findViewById(R.id.home_viewpager);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        moviesShimmerLayout = view.findViewById(R.id.Movies_Shimmer_Layout);
        continue_release_recyclerview = view.findViewById(R.id.continue_release_recyclerview);
        resume_Layout = view.findViewById(R.id.resume_Layout);
        tab_custom = view.findViewById(R.id.tabcustom);
        movies = tab_custom.findViewById(R.id.movies);
        warner = tab_custom.findViewById(R.id.warner);

        paramount = tab_custom.findViewById(R.id.paramount);
        more_items = tab_custom.findViewById(R.id.more_items);
        try {
            JSONObject userObject = UserManager.loadUser(mContext);
            userID = userObject.getInt("ID");
            Log.d("usweId", "" + userID);
        } catch (Exception e) {
            Log.d("test", e.getMessage());
        }

        try {
            JSONObject configObject = ConfigManager.loadConfig(mContext);
            imageSliderType = configObject.getString("image_slider_type");
            movieImageSliderMaxVisible = configObject.getInt("movie_image_slider_max_visible");
            shuffleContents = configObject.getInt("shuffle_contents");
        } catch (Exception e) {
        }
        home_swipper = view.findViewById(R.id.home_swoper);
        loadSliderHome();
        imageSliderItems = new ArrayList<>();
        HomeViewpager.setClipToPadding(false);
        HomeViewpager.setClipChildren(false);
        HomeViewpager.setOffscreenPageLimit(3);
        HomeViewpager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        HomeViewpager.setPageTransformer((view, position) ->
        {
            if (position <= -1.0F || position >= 1.0F) {
                view.setTranslationX(view.getWidth() * position);
                view.setAlpha(0.0F);
            } else if (position == 0.0F) {
                view.setTranslationX(view.getWidth() * position);
                view.setAlpha(1.0F);
            } else {
                view.setTranslationX(view.getWidth() * -position);
                view.setAlpha(1.0F - Math.abs(position));
            }
            view.findViewById(R.id.logoview).setTranslationX(view.getWidth() * position);

        });

        HomeViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);

            }


        });

        interested_layout = view.findViewById(R.id.interested_layout);
        DC_release_recyclerview = view.findViewById(R.id.DC_release_recyclerview);
        superherorecyclerview = view.findViewById(R.id.superherorecyclerview);
        recenly_release_recyclerview = view.findViewById(R.id.recenly_release_recyclerview);
        top_trending_release_recyclerview = view.findViewById(R.id.top_trending_release_recyclerview);
        home_bywm_list_Recycler_View = view.findViewById(R.id.home_bywm_list_Recycler_View);
        drama_release_recyclerview = view.findViewById(R.id.drama_release_recyclerview);
        sceince_release_recyclerview = view.findViewById(R.id.sceince_release_recyclerview);
        LoadMoviesContent();

        home_swipper.setOnRefreshListener(() -> {
            LoadMoviesContent();
            loadSliderHome();
            loadResumeLayout();
        });
        loadResumeLayout();
        movies.setOnClickListener(v ->
        {
            fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction newTransaction = fragmentManager.beginTransaction();
            OpenMoviesFrag(newTransaction);
        });
        paramount.setOnClickListener(v ->
        {
            fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction newTransaction = fragmentManager.beginTransaction();
            ParaFragment(newTransaction);
        });
        warner.setOnClickListener(v ->
        {
            fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction newTransaction = fragmentManager.beginTransaction();
            WarnerFragment(newTransaction);
        });
        genre_initlizer = view.findViewById(R.id.genre_initlizer);

        more_items.setOnClickListener(v ->
        {
            LoadGenre();
        });
        genre_initlizer = view.findViewById(R.id.genre_initlizer);

        more_items.setOnClickListener(v ->
        {
            LoadGenre();
        });
        close_btn = genre_initlizer.findViewById(R.id.closebtn);
        close_btn.setOnClickListener(v ->
        {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(genre_initlizer, "alpha", 1f, 0f);
            fadeOut.setDuration(500); // Adjust the duration as needed
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    genre_initlizer.setVisibility(View.GONE);
                }
            });
            fadeOut.start();
        });
        return view;
    }
    private void ParaFragment(FragmentTransaction newTransaction) {
        ParaVeresal paraVersePages = new ParaVeresal();
        newTransaction.replace(R.id.contaner, paraVersePages);
        newTransaction.commit();
    }

    private void WarnerFragment(FragmentTransaction newTransaction) {
        Warner warnerPages = new Warner();
        newTransaction.replace(R.id.contaner, warnerPages);
        newTransaction.commit();
    }

    private void OpenMoviesFrag(FragmentTransaction newTransaction) {
        MarvelPage marvelPage = new MarvelPage();
        newTransaction.replace(R.id.contaner, marvelPage);
        newTransaction.commit();
    }

    private void loadResumeLayout() {
        ResumeContentDatabase db = ResumeContentDatabase.getDbInstance(mContext.getApplicationContext());
        List<ResumeContent> resumeContents = db.resumeContentDao().getResumeContents();

        if (resumeContents.isEmpty()) {
            resume_Layout.setVisibility(View.GONE);
        } else {
            resume_Layout.setVisibility(View.VISIBLE);
        }

        List<ResumeContent> mData = resumeContents;
        List<ContinuePlayingList> continuePlayingList = new ArrayList<>();

        for (int i = 0; i < mData.size(); i++) {

            int id = mData.get(i).getId();
            int contentID = mData.get(i).getContent_id();

            String contentType = mData.get(i).getContent_type();

            String name = mData.get(i).getName();

            String year = "";
            if (!mData.get(i).getYear().equals("")) {
                year = GospelUtil.getYearFromDate(mData.get(i).getYear());
            }
            String poster = mData.get(i).getPoster();
            String sourceType = mData.get(i).getSource_type();
            String sourceUrl = mData.get(i).getSource_url();
            int type = mData.get(i).getType();
            long position = mData.get(i).getPosition();
            long duration = mData.get(i).getDuration();

            continuePlayingList.add(new ContinuePlayingList(id, contentID, name, year, poster, sourceType, sourceUrl, type, contentType, position, duration));

            ContinuePlayingListAdepter myadepter = new ContinuePlayingListAdepter(mContext, continuePlayingList);
            continue_release_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
            continue_release_recyclerview.setAdapter(myadepter);

        }
    }

    @SuppressLint("HardwareIds")
    private void LoadMoviesContent() {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr1 = new StringRequest(Request.Method.GET, Constants.url + "getRecentContentList/Movies", response -> {
            if (!response.equals("No Data Available")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<MovieList> recentlyAddedMovieList = new ArrayList<>();
                int maxMoviesToShow = 6; // Set the maximum number of movies to show

                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String certificate_type = rootObject.get("certificate_type").getAsString();

                    String year = "";

                    if (!rootObject.get("release_date").getAsString().equals("")) {
                        year = GospelUtil.getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        recentlyAddedMovieList.add(new MovieList(id, type, name, year, poster, "", certificate_type));
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

                MovieListAdepter myAdapter = new MovieListAdepter(mContext, recentlyAddedMovieList);
                recenly_release_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                recenly_release_recyclerview.setAdapter(myAdapter);
                home_swipper.setRefreshing(false);

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

        StringRequest sr2 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + Cat_name, response -> {
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
                superherorecyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                superherorecyclerview.setAdapter(myadepter);
                home_swipper.setRefreshing(false);
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
        StringRequest sr3 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + Cat_name2, response -> {
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
                sceince_release_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                sceince_release_recyclerview.setAdapter(myadepter);
                home_swipper.setRefreshing(false);
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
        StringRequest sr4 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + Cat_name6, response -> {
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
                drama_release_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                drama_release_recyclerview.setAdapter(myadepter);
                home_swipper.setRefreshing(false);
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
        StringRequest sr7 = new StringRequest(Request.Method.GET, Constants.url + "getContentsReletedToGenre/" + Cat_name3, response -> {
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
                DC_release_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                DC_release_recyclerview.setAdapter(myadepter);
                home_swipper.setRefreshing(false);
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
        StringRequest sr5 = new StringRequest(Request.Method.GET, Constants.url + "getTrending", response -> {

            if (!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<TrendingList> trendingList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int content_type = rootObject.get("content_type").getAsInt();

                    trendingList.add(new TrendingList(id, type, content_type, poster));
                }
                List<TrendingList> limitedList = trendingList.subList(0, Math.min(trendingList.size(), 8));
                Collections.reverse(limitedList);
                TrendingListAdepter myadepter = new TrendingListAdepter(mContext, limitedList);
                top_trending_release_recyclerview.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                top_trending_release_recyclerview.setAdapter(myadepter);
                home_swipper.setRefreshing(false);

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
        queue.add(sr5);
        String tempUserID = null;
        if (userID != 0) {
            tempUserID = String.valueOf(userID);
        } else {
            tempUserID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        StringRequest sr6 = new StringRequest(Request.Method.GET, Constants.url + "beacauseYouWatched/Movies/" + tempUserID + "/10", response -> {
            if (!response.equals("No Data Avaliable")) {
                interested_layout.setVisibility(View.VISIBLE);
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<MovieList> movieList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if (!rootObject.get("release_date").getAsString().equals("")) {
                        year = GospelUtil.getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        movieList.add(new MovieList(id, type, name, year, poster, "", ""));
                    }
                }

                Collections.shuffle(movieList);

                moviesOnlyForYouListAdepter myadepter = new moviesOnlyForYouListAdepter(mContext, movieList);
                home_bywm_list_Recycler_View.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.HORIZONTAL, false));
                home_bywm_list_Recycler_View.setAdapter(myadepter);
                home_swipper.setRefreshing(false);
            } else {
                interested_layout.setVisibility(View.GONE);
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


    }

    private void setFragmentBackgroundColor(int dominantColor) {
        if (getView() != null) {
            getView().setBackgroundColor(dominantColor);
        }
    }

    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            HomeViewpager.setCurrentItem(HomeViewpager.getCurrentItem() + 1);
        }
    };

    private void loadSliderHome() {
        if (movieImageSliderMaxVisible > 0) {
            RequestQueue queue = Volley.newRequestQueue(mContext);
            StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getMovieImageSlider", response -> {
                Log.d("Movies", response);
                if (!response.equals("No Data Avaliable")) {
                    JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                    int i = 0;
                    int maxVisible = movieImageSliderMaxVisible;
                    for (JsonElement r : jsonArray) {
                        if (i < maxVisible) {
                            JsonObject rootObject = r.getAsJsonObject();
                            int id = rootObject.get("id").getAsInt();
                            String name = rootObject.get("name").getAsString();
                            String banner = rootObject.get("banner").getAsString();
                            String poster = rootObject.get("poster").getAsString();
                            String logo = rootObject.get("logo_image").getAsString();

                            int status = rootObject.get("status").getAsInt();

                            if (status == 1) {
                                imageSliderItems.add(new ImageSliderItem(banner, name, poster, 0, id, "", logo, "", "", ""));
                            }
                            i++;
                        }
                    }
                    HomeViewpager.setVisibility(View.VISIBLE);
                    HomeViewpager.setAdapter(new ImageSliderAdepter(imageSliderItems, HomeViewpager));
                    moviesShimmerLayout.setVisibility(View.GONE);
                } else {
                    HomeViewpager.setVisibility(View.GONE);
                    moviesShimmerLayout.setVisibility(View.VISIBLE);
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
            queue.add(sr);
            home_swipper.setRefreshing(false);
        } else {
            HomeViewpager.setVisibility(View.GONE);
        }
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
//        Dashboard dashboard = (Dashboard) requireActivity();
//        View tabcustom = dashboard.tabcustom();
//        tabcustom.setVisibility(View.VISIBLE);
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

    private void LoadGenre() {

        categories = genre_initlizer.findViewById(R.id.categories);
        List<GenreList> genreList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getGenreList", response -> {
            if (!response.equals("No Data Avaliable")) {
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(genre_initlizer, "alpha", 0f, 1f);
                fadeIn.setDuration(500);
                fadeIn.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        genre_initlizer.setVisibility(View.VISIBLE);
                    }
                });
                fadeIn.start();
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();
                    String icon = rootObject.get("icon").getAsString();
                    String description = rootObject.get("description").getAsString();
                    int featured = rootObject.get("featured").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        if (name.contains("_")) {

                            name = name.replaceAll("_", " ");
                            name = capitalizeEachWord(name);
                        } else {
                            // Capitalize the first letter of each word
                            name = capitalizeFirstLetter(name);
                        }
                        genreList.add(new GenreList(id, name, icon, description, featured, status));
                    }
                }
                AllGenreListAdepter myadepter = new AllGenreListAdepter(mContext, genreList);

                categories.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.VERTICAL, false));
                categories.setAdapter(myadepter);
            } else {

                genre_initlizer.setVisibility(View.GONE);
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
        queue.add(sr);

    }

    private String capitalizeFirstLetter(String str) {
        if (str != null && !str.isEmpty()) {
            return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
        } else {
            return "";
        }
    }

    private String capitalizeEachWord(String str) {
        StringBuilder result = new StringBuilder(str.length());
        String[] words = str.split("\\s");
        for (String word : words) {
            result.append(capitalizeFirstLetter(word)).append(" ");
        }
        return result.toString().trim();
    }
    public View genre_initlizer() {
        return genre_initlizer;
    }

    public View tabcustom() {
        return tab_custom;
    }
}