package com.primemedia.marvels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.primemedia.marvels.adapter.CastAdapterCustom;
import com.primemedia.marvels.fragments.CollectionsSeries;
import com.primemedia.marvels.fragments.SeriesEpsiodes;
import com.primemedia.marvels.fragments.Series_MoreLike;
import com.primemedia.marvels.fragments.Trailers_Series;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class WebSeriesDetails extends AppCompatActivity {
    Context context = this;
    ImageView playerbuttons;
    int mainId;
    ExoPlayer exoPlayer;
    PlayerView trailer_player;
   public static LinearLayout tab_content_loader;
    int userId;
    Map<String, String> defaultRequestProperties = new HashMap<>();
    private static final String KEY_PLAYBACK_POSITION = "playback_position";
    View more_cast_list, cast_addtional;
    String trailerUrl;

    int contentId;
    String name;
    String releaseDate;
    private DefaultTrackSelector trackSelector;
    String genres;
    String directors, writers, runtime, MovieType, certificate_type, cast;
    String poster;
    String banner;
    String logo;
    int downloadable;
    int type;
    LinearLayout play_ll;
    int status;
    String description;
    private long playbackPosition = C.TIME_UNSET;
    View rootView;
    ImageView playButton, movieDetailsBanner;
    String userData = null;

    String tempUserID = null;

    int webSeriesEpisodeitemType = 0;

    int TMDB_ID;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Constants.FLAG_SECURE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.activity_web_series_details);
        rootView = findViewById(R.id.series_details);
        loadConfig();
        loadData();
        Intent intent = getIntent();
        mainId = intent.getExtras().getInt("ID");
        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong(KEY_PLAYBACK_POSITION, C.TIME_UNSET);
        }
        if (userData != null) {
            tempUserID = String.valueOf(userId);
        } else {
            tempUserID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        trailer_player = findViewById(R.id.youtube_player_view);
        playButton = findViewById(R.id.playButton);
        movieDetailsBanner = findViewById(R.id.moviedetails_thumb);
        playerbuttons = findViewById(R.id.playerbuttons);
        loadWebSeriesDetails(mainId);

        TabLayout tabLayout = findViewById(R.id.containers_series_Tab);
        TabLayout.Tab episodes = tabLayout.newTab().setText("Episodes");
        TabLayout.Tab trailersTab = tabLayout.newTab().setText("TRAILERS & MORE");
        TabLayout.Tab Collections = tabLayout.newTab().setText("Collections");
        TabLayout.Tab moreLikeThisTab = tabLayout.newTab().setText("MORE LIKE THIS");


        tabLayout.addTab(episodes);
        tabLayout.addTab(trailersTab);
        tabLayout.addTab(Collections);
        tabLayout.addTab(moreLikeThisTab);
        tabLayout.selectTab(episodes);
        loadFragment(new SeriesEpsiodes());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1:
                        loadFragment(new Trailers_Series());
                        break;
                    case 2:
                        loadFragment(new CollectionsSeries());
                        break;
                    case 3:
                        loadFragment(new Series_MoreLike());
                        break;
                    default:
                        loadFragment(new SeriesEpsiodes());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        play_ll = findViewById(R.id.play_ll);
        TextView playtext = findViewById(R.id.playtext);
        playtext.setText("Play EP 1 Now");
        TextView down_item = findViewById(R.id.down_item);
        down_item.setText("Download EP 1 Now");
        tab_content_loader = findViewById(R.id.tab_content_loader);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.containers_series, fragment);
        transaction.commit();
    }


    private void loadWebSeriesDetails(int mainId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getWebSeriesDetails/" + mainId, response ->
        {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            trailerUrl = jsonObject.get("youtube_trailer").getAsString();


            playerbuttons.setOnClickListener(v ->
            {
                playerbuttons.setVisibility(View.GONE);
                movieDetailsBanner.setVisibility(View.GONE);
                trailer_player.setVisibility(View.VISIBLE);
                LoadTrailer(trailerUrl);
            });

            contentId = jsonObject.get("id").getAsInt();
            TMDB_ID = jsonObject.get("TMDB_ID").getAsInt();

            name = jsonObject.get("name").getAsString();

            if (!jsonObject.get("release_date").getAsString().equals("")) {
                releaseDate = jsonObject.get("release_date").getAsString();
            }
            directors = jsonObject.get("director").getAsString();
            writers = jsonObject.get("writers").getAsString();
//            runtime = jsonObject.get("runtime").getAsString();
            genres = jsonObject.get("genres").getAsString();
            logo = jsonObject.get("logo_image").getAsString();
            poster = jsonObject.get("poster").getAsString();
            banner = jsonObject.get("banner").getAsString();
            downloadable = jsonObject.get("downloadable").getAsInt();
            MovieType = jsonObject.get("SeriesType").getAsString();
            certificate_type = jsonObject.get("certificate_type").getAsString();
            type = jsonObject.get("type").getAsInt();
            cast = jsonObject.get("cast").getAsString();
            TextView Castviews = findViewById(R.id.cast_view);
            Castviews.setText(cast);
            status = jsonObject.get("status").getAsInt();
            description = jsonObject.get("description").getAsString();
            TextView releaseDateTextView = findViewById(R.id.year_text);
            if (releaseDate != null) {
                releaseDateTextView.setText(releaseDate);
            } else {
                releaseDateTextView.setVisibility(View.GONE);
            }


            ImageView MovieLogo = findViewById(R.id.movies_logo);
            Glide.with(WebSeriesDetails.this)
                    .load(logo)
                    .into(MovieLogo);

            Glide.with(WebSeriesDetails.this)
                    .load(banner)
                    .into(movieDetailsBanner);
//            TextView MovieType_text = findViewById(R.id.MovieType_text);
//            MovieType_text.setText(MovieType);
            TextView director = findViewById(R.id.director);
            director.setText(directors);
            TextView descriptionTextView = findViewById(R.id.overview_text);
            descriptionTextView.setText(description);
            TextView runtime_text = findViewById(R.id.runtime_text);
            runtime_text.setVisibility(View.GONE);
            more_cast_list = findViewById(R.id.more_cast_list);
            cast_addtional = findViewById(R.id.cast_addtional);
            more_cast_list.setOnClickListener(v ->
            {
                if (cast_addtional.getVisibility() == View.GONE) {
                    cast_addtional.setAlpha(0f);
                    cast_addtional.setTranslationY(cast_addtional.getHeight());
                    cast_addtional.setVisibility(View.VISIBLE);
                    cast_addtional.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(500)
                            .start();
                } else {

                    cast_addtional.animate()
                            .alpha(0f)
                            .translationY(cast_addtional.getHeight())
                            .setDuration(500)
                            .withEndAction(() -> cast_addtional.setVisibility(View.GONE))
                            .start();
                }
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

    @SuppressLint("StaticFieldLeak")
    private void LoadTrailer(String trailerUrl) {
        new YouTubeExtractor(this) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onExtractionComplete(@Nullable SparseArray<YtFile> ytFiles, @Nullable VideoMeta videoMeta) {
                if (ytFiles != null && ytFiles.size() > 0) {
                    int selectedItag = findBestQualityItag(ytFiles);
                    if (selectedItag != -1) {
                        YtFile selectedFile = ytFiles.get(selectedItag);
                        String downloadUrl = selectedFile.getUrl();

                        StartExoplayer(downloadUrl);
                    }
                }
            }
        }.extract(trailerUrl);
    }
    private int findBestQualityItag(SparseArray<YtFile> ytFiles) {
        int bestQualityItag = -1;
        int maxQuality = -1;

        for (int i = 0; i < ytFiles.size(); i++) {
            int itag = ytFiles.keyAt(i);
            YtFile ytFile = ytFiles.get(itag);
            if (ytFile.getFormat().getAudioBitrate() > 0) {
                if (ytFile.getFormat().getHeight() > maxQuality) {
                    maxQuality = ytFile.getFormat().getHeight();
                    bestQualityItag = itag;
                }
            }
        }

        return bestQualityItag;
    }

    @OptIn(markerClass = UnstableApi.class)
    private void StartExoplayer(String downloadUrl) {


        DataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setKeepPostFor302Redirects(true)
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                .setDefaultRequestProperties(defaultRequestProperties);
        DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this, httpDataSourceFactory);

        MediaItem mediaItem = new MediaItem.Builder()
                .setMimeType(MimeTypes.APPLICATION_MP4)
                .setUri(downloadUrl)
                .build();
        MediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem);

        initializePlayer(progressiveMediaSource);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initializePlayer(MediaSource progressiveMediaSource) {

        ExoTrackSelection.Factory videoTrackSelectionFactory = new
                AdaptiveTrackSelection.Factory();
        trackSelector = new
                DefaultTrackSelector(this, videoTrackSelectionFactory);
        exoPlayer = new ExoPlayer.Builder(this)

                .setTrackSelector(trackSelector)
                .setSeekForwardIncrementMs(10000)
                .setSeekBackIncrementMs(10000)
                .build();
        trailer_player.setPlayer(exoPlayer);
        trailer_player.setKeepScreenOn(true);
        exoPlayer.setMediaSource(progressiveMediaSource);
        if (playbackPosition != C.TIME_UNSET) {
            exoPlayer.seekTo(playbackPosition);
        }
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
                if (playbackState == Player.STATE_READY && playWhenReady) {
                    playbackPosition = exoPlayer.getCurrentPosition();
                } else if (playbackState == Player.STATE_ENDED) {
                    playerbuttons.setVisibility(View.VISIBLE);
                    movieDetailsBanner.setVisibility(View.VISIBLE);
                    trailer_player.setVisibility(View.GONE);
                }
            }

        });

    }

    private void loadAdditionalDetails(String certificate_type, String genres, String namemovie, String directors, String writers) {
        RecyclerView cast_items = cast_addtional.findViewById(R.id.cast_items);
        TextView director = cast_addtional.findViewById(R.id.director);
        TextView writer = cast_addtional.findViewById(R.id.writer);
        TextView ratings = cast_addtional.findViewById(R.id.ratings);
        CardView close_btn = cast_addtional.findViewById(R.id.close_btn);
        RecyclerView Genres_view = cast_addtional.findViewById(R.id.Genres_view);
        LinearLayout cast_layout = cast_addtional.findViewById(R.id.cast_layout);
        TextView rating_home = findViewById(R.id.certificate_types_Textview);
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
                    rating.setText(jsonObject.get("rating").getAsString() + "IMDB");
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
        String[] genresArray = genres.split(",");
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


    private void loadConfig() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String config = sharedPreferences.getString("Config", null);
        JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);
        webSeriesEpisodeitemType = 1;
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        if (sharedPreferences.getString("UserData", null) != null) {
            userData = sharedPreferences.getString("UserData", null);
            JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
            userId = jsonObject.get("ID").getAsInt();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the playback position
        outState.putLong(KEY_PLAYBACK_POSITION, playbackPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }


    private void releaseExoPlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isChangingConfigurations()) {
            releaseExoPlayer();
        } else {
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(false);
            }
        }
    }
}