package com.primemedia.marvels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.ExoTrackSelection;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.primemedia.marvels.adapter.CastAdapterCustom;
import com.primemedia.marvels.adapter.PlayMovieItemListAdepter;
import com.primemedia.marvels.fragments.CollectionsMovies;
import com.primemedia.marvels.fragments.MovieMorelikethis;
import com.primemedia.marvels.fragments.TrailerMovies;
import com.primemedia.marvels.list.PlayMovieItemIist;
import com.primemedia.marvels.utility.GospelUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;


public class MovieDetails extends AppCompatActivity {
    Context context;
    LinearLayout more_cast_list;
    ImageView ImageLoader;
    int id;
    TextView rating_home;
   public static TabLayout tabLayout;
    int userId;
    boolean playPremium = true;
    String trailerUrl;
    View cast_addtional;
    ExoPlayer exoPlayer;
    int contentId;

    String name;
    String preferredLanguage = "en";
    String releaseDate;
    String runtime;
    String genres, MovieType;
    String poster, directors, cast, writers;
    String banner;
    private DefaultTrackSelector trackSelector;
    String movie_logo, certificate_type,subTitleurl;
    int downloadable;
    int contentType = 1;
    int type;
    int status;
    String description;
    public static ImageView moviedetails_thumb;
    public static ImageView trailerIcon;
    FrameLayout playerbuttons;
    ImageView favouriteIcon;
    ImageView downloadIcon;

    Boolean isFavourite = false;

    int adType;
    PlayerView trailer_player;
    Map<String, String> defaultRequestProperties = new HashMap<>();
    private boolean vpnStatus;
    private GospelUtil helperUtils;
    View rootView;
    String userData = null;
    String tempUserID = null;
    int TMDB_ID;

    @OptIn(markerClass = UnstableApi.class)
    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.activity_movie_details);
        rootView = findViewById(R.id.movie_details);
        context = this;
        loadConfig();
        loadData();
        loadUserSubscriptionDetails();

        if (userData != null) {
            tempUserID = String.valueOf(userId);
        } else {
            tempUserID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        Intent intent = getIntent();
        id = intent.getExtras().getInt("ID");

        if (userData != null) {
            GospelUtil.setViewLog(context, String.valueOf(userId), id, 1, Constants.apiKey);
        } else {
            GospelUtil.setViewLog(context, tempUserID, id, 1, Constants.apiKey);
        }

        loadMovieDetails(id);

        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.getPlaybackState();
            exoPlayer.play();
        }
        LinearLayout playbtn = findViewById(R.id.play_ll);
        playbtn.setOnClickListener(v ->
        {
            loadStreamLinks(id);
        });
         tabLayout = findViewById(R.id.tab_layout_movies);
        TabLayout.Tab moreLikeThisTab = tabLayout.newTab().setText("MORE LIKE THIS");
        TabLayout.Tab trailersTab = tabLayout.newTab().setText("TRAILERS & MORE");
        TabLayout.Tab Collections = tabLayout.newTab().setText("Collections");
        tabLayout.addTab(moreLikeThisTab);
        tabLayout.addTab(trailersTab);
        tabLayout.selectTab(moreLikeThisTab);
        tabLayout.addTab(Collections);
        loadFragment(new MovieMorelikethis());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1:
                        loadFragment(new TrailerMovies());

                        break;
                    case 2:
                        loadFragment(new CollectionsMovies());

                        break;
                    default:
                        loadFragment(new MovieMorelikethis());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.containers_movies_more, fragment);
        transaction.commit();
    }

    private void loadStreamLinks(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getMoviePlayLinks/" + id, response -> {
            if (!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<PlayMovieItemIist> playMovieItemList = new ArrayList<>();

                RecyclerView playMovieItemRecylerview = findViewById(R.id.Play_movie_item_Recylerview);

                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id1 = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();
                    String size = rootObject.get("size").getAsString();
                    String quality = rootObject.get("quality").getAsString();
                    int movieId = rootObject.get("movie_id").getAsInt();
                    String url = rootObject.get("url").getAsString();
                    String type = rootObject.get("type").getAsString();
                    int status = rootObject.get("status").getAsInt();
                    int skipAvailable = rootObject.get("skip_available").getAsInt();
                    String introStart = rootObject.get("intro_start").getAsString();
                    String introEnd = rootObject.get("intro_end").getAsString();
                    int link_type = rootObject.get("link_type").getAsInt();
                    String drm_uuid = rootObject.get("drm_uuid").isJsonNull() ? "" : rootObject.get("drm_uuid").getAsString();
                    String drm_license_uri = rootObject.get("drm_license_uri").isJsonNull() ? "" : rootObject.get("drm_license_uri").getAsString();

                    if (status == 1) {
                        playMovieItemList.add(new PlayMovieItemIist(id1, name, size, quality, movieId, url, type, skipAvailable, introStart, introEnd, link_type, drm_uuid, drm_license_uri));
                    }
                    PlayMovieItemListAdepter myadepter = new PlayMovieItemListAdepter(id, context, playMovieItemList, playPremium);
                    playMovieItemRecylerview.setLayoutManager(new GridLayoutManager(context, 1));
                    playMovieItemRecylerview.setAdapter(myadepter);
                }

                playMovieTab(true);
            } else {
                Snackbar snackbar = Snackbar.make(rootView, "Movie will be added soon!", Snackbar.LENGTH_SHORT);
                snackbar.setAction("Close", v -> snackbar.dismiss());
                snackbar.show();
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
        sr.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void playMovieTab(boolean show) {
        View playMovieTab = findViewById(R.id.Play_Movie_Tab);
        ViewGroup movieDetails = findViewById(R.id.movie_details);
        TextView Play_Text = findViewById(R.id.Play_Text);
        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(600);
        transition.addTarget(R.id.Play_Movie_Tab);

        TransitionManager.beginDelayedTransition(movieDetails, transition);
        playMovieTab.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void loadMovieDetails(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getMovieDetails/" + id, response -> {
            Log.d("responses_movie", response);
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            trailerUrl = jsonObject.get("youtube_trailer").getAsString();

            settrailervideo(trailerUrl);
            contentId = jsonObject.get("id").getAsInt();
            TMDB_ID = jsonObject.get("TMDB_ID").getAsInt();
            genres = jsonObject.get("genres").getAsString();
            name = jsonObject.get("name").getAsString();
            if (!jsonObject.get("release_date").getAsString().equals("")) {
                releaseDate = jsonObject.get("release_date").getAsString();
            }
            directors = jsonObject.get("director").getAsString();
            writers = jsonObject.get("writers").getAsString();
            runtime = jsonObject.get("runtime").getAsString();
            poster = jsonObject.get("poster").getAsString();
            movie_logo = jsonObject.get("logo_image").getAsString();

            banner = jsonObject.get("banner").getAsString();


            downloadable = jsonObject.get("downloadable").getAsInt();
            type = jsonObject.get("type").getAsInt();
            status = jsonObject.get("status").getAsInt();
            description = jsonObject.get("description").getAsString();
            TextView releaseDateTextView = findViewById(R.id.year_text);
            MovieType = jsonObject.get("movietype").getAsString();
            certificate_type = jsonObject.get("certificate_type").getAsString();
            Log.d("cert_type", certificate_type);
            TextView MovieType_text = findViewById(R.id.MovieType_text);
            MovieType_text.setText(name);
            TextView director = findViewById(R.id.director);
            director.setText(directors);
            cast = jsonObject.get("cast").getAsString();
            Log.d("casvoew", cast);
            TextView Castviews = findViewById(R.id.cast_view);
            Castviews.setText(cast);
            if (releaseDate != null) {
                releaseDateTextView.setText(releaseDate);
            } else {
                releaseDateTextView.setVisibility(View.GONE);
            }
            TextView runtimeTextView = findViewById(R.id.runtime_text);
            runtimeTextView.setText(runtime);
            moviedetails_thumb = findViewById(R.id.moviedetails_thumb);
            ImageView movie_logo_img = findViewById(R.id.movies_logo);
            Glide.with(MovieDetails.this).load(movie_logo).into(movie_logo_img);
            Glide.with(MovieDetails.this).load(banner).into(moviedetails_thumb);
            TextView descriptionTextView = findViewById(R.id.overview_text);
            descriptionTextView.setText(description);
            more_cast_list = findViewById(R.id.more_cast_list);
            cast_addtional = findViewById(R.id.cast_addtional);
            more_cast_list.setOnClickListener(v ->
            {
                cast_addtional.setVisibility(View.VISIBLE);
            });
            loadAdditionalDetails(certificate_type, genres, name, directors, writers);
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

    private void loadAdditionalDetails(String certificate_type, String genres, String namemovie, String directors, String writers) {
        RecyclerView cast_items = cast_addtional.findViewById(R.id.cast_items);
        TextView director = cast_addtional.findViewById(R.id.director);
        TextView writer = cast_addtional.findViewById(R.id.writer);
        TextView ratings = cast_addtional.findViewById(R.id.ratings);
        CardView close_btn = cast_addtional.findViewById(R.id.close_btn);
        RecyclerView Genres_view = cast_addtional.findViewById(R.id.Genres_view);
        LinearLayout cast_layout = cast_addtional.findViewById(R.id.cast_layout);
        rating_home = findViewById(R.id.certificate_types_Textview);
        rating_home.setText(certificate_type);
        ratings.setText(certificate_type);
        TextView titles = cast_addtional.findViewById(R.id.titles);
        titles.setText(namemovie);
        director.setText(directors);

        writer.setText(writers);
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            @SuppressLint("SetTextI18n") StringRequest sr = new StringRequest(Request.Method.POST, "https://cloud.team-dooo.com/Dooo/IMDB/index.php", response -> {
                Log.d("responses", response);
                if (!Objects.equals(response, "false")) {
                    JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                    TextView rating = findViewById(R.id.rating_home_IMDB);
                    rating.setText(jsonObject.get("rating").getAsString());
                    JsonArray jsonArray = new Gson().fromJson(response, JsonObject.class).get("cast").getAsJsonArray();
                    List<String> castNames = new ArrayList<>();

                    for (JsonElement element : jsonArray) {
                        JsonObject castObject = element.getAsJsonObject();
                        String name = castObject.get("name").isJsonNull() ? "" : castObject.get("name").getAsString();
                        Log.d("TodayNames", name);
                        castNames.add(name);
                    }
                    CastAdapterCustom castAdapter = new CastAdapterCustom(this, castNames);
                    cast_items.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.VERTICAL, false));
                    cast_items.setAdapter(castAdapter);
                } else {
                    cast_layout.setVisibility(View.GONE);
                }

            }, error -> {
                // Handle error
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("ctype", String.valueOf(1));
                    params.put("tmdbid", String.valueOf(TMDB_ID));
                    params.put("bGljZW5zZV9jb2Rl", Constants.bGljZW5zZV9jb2Rl);
                    return params;
                }
            };
            queue.add(sr);
        } catch (Exception ignored) {
            // Handle exception
        }
        String[] genresArray = genres.substring(1).split(",");
        List<String> genresList = new ArrayList<>();
        for (String genre : genresArray) {
            genresList.add(genre.trim());
            Log.d("genresarray", genre);
        }
        CastAdapterCustom castAdapter = new CastAdapterCustom(this, genresList);
        Genres_view.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.VERTICAL, false));
        Genres_view.setAdapter(castAdapter);

        close_btn.setOnClickListener(v -> {
            cast_addtional.setVisibility(View.GONE);
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    @SuppressLint("StaticFieldLeak")
    private void settrailervideo(String trailerUrl) {
        playerbuttons = findViewById(R.id.play_container);
        trailer_player = findViewById(R.id.trailer_player);
        playerbuttons.setOnClickListener(v -> {
            playerbuttons.setVisibility(View.GONE);


            new YouTubeExtractor(this) {

                @Override
                public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                    DataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()

                            .setKeepPostFor302Redirects(true).setAllowCrossProtocolRedirects(true).setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS).setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS).setDefaultRequestProperties(defaultRequestProperties);
                    DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(context, httpDataSourceFactory);


                    if (ytFiles != null) {
                        int itag = 22;
                        String downloadUrl = ytFiles.get(itag).getUrl();
                        MediaItem mediaItem = new MediaItem.Builder().setMimeType(MimeTypes.APPLICATION_MP4).setUri(downloadUrl).build();
                        MediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
                        initializePlayer(progressiveMediaSource);
                    }
                }
            }.extract(trailerUrl);

        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initializePlayer(MediaSource mediaSource) {
        ExoTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        trackSelector = new DefaultTrackSelector(MovieDetails.this, videoTrackSelectionFactory);
        trackSelector.setParameters(trackSelector.buildUponParameters().setPreferredTextLanguage(preferredLanguage));
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this);
        renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
        exoPlayer = new ExoPlayer.Builder(this, renderersFactory).setTrackSelector(trackSelector).setSeekForwardIncrementMs(10000).setSeekBackIncrementMs(10000).build();
        trailer_player.setPlayer(exoPlayer);
        trailer_player.setKeepScreenOn(true);
        exoPlayer.setMediaSource(mediaSource);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.prepare();
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                switch (playbackState) {
                    case Player.STATE_READY:
                        moviedetails_thumb.setVisibility(View.GONE);
                        trailer_player.setVisibility(View.VISIBLE);
                        playerbuttons.setVisibility(View.GONE);
                        break;
                    case Player.STATE_ENDED:
                        moviedetails_thumb.setVisibility(View.VISIBLE);
                        trailer_player.setVisibility(View.GONE);
                        playerbuttons.setVisibility(View.VISIBLE);
                        break;
                    case Player.STATE_IDLE:

                        break;
                    case Player.STATE_BUFFERING:
                }
            }
        });
    }


    private void loadUserSubscriptionDetails() {
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        if (sharedPreferences.getString("UserData", null) != null) {
            userData = sharedPreferences.getString("UserData", null);
            JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
            userId = jsonObject.get("ID").getAsInt();
        }

    }

    private void loadConfig() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String config = sharedPreferences.getString("Config", null);
        JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playMovieTab(false);
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.getPlaybackState();
            exoPlayer.play();
        }
    }

    private void pausePlayer() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.getPlaybackState();
            exoPlayer.pause();
        }
    }

    private void startPlayer() {
        exoPlayer.play();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        View playMovieTab = findViewById(R.id.Play_Movie_Tab);
        if (playMovieTab.getVisibility() == View.VISIBLE) {
            playMovieTab(false);
        } else {
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer.clearVideoSurface();
            exoPlayer = null;
        }
    }
}