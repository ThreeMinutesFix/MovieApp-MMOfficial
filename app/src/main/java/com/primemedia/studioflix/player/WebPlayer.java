package com.primemedia.studioflix.player;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.primemedia.studioflix.WebSeriesDetails.tab_content_loader;
import static com.rd.utils.DensityUtils.dpToPx;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.MergingMediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.source.SingleSampleMediaSource;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.ExoTrackSelection;
import androidx.media3.exoplayer.trackselection.MappingTrackSelector;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.vkay94.dtpv.DoubleTapPlayerView;
import com.github.vkay94.dtpv.youtube.YouTubeOverlay;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.primemedia.studioflix.Constants;
import com.primemedia.studioflix.R;
import com.primemedia.studioflix.adapter.EpisodeListAdepter_player;
import com.primemedia.studioflix.dialogs.AudioTrackSelectionDialog;
import com.primemedia.studioflix.dialogs.TrackSelectionDialog;
import com.primemedia.studioflix.list.EpisodeList;
import com.primemedia.studioflix.resume_content.ResumeContentDatabase;
import com.primemedia.studioflix.utility.GospelUtil;
import com.primemedia.studioflix.utility.TinyDB;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.anilbeesetti.nextlib.media3ext.ffdecoder.NextRenderersFactory;

public class WebPlayer extends AppCompatActivity {
    DoubleTapPlayerView webPlayer;
    private MaterialCardView playbackSpeed, sourceType, Servers, resizeModeLayout;
    private Button buttonSpeed, buttonServerType;
    String preferredLanguage = "en";
    ExoPlayer exoPlayer;
    int webSeriesEpisodeitemType = 0;
    int type;
    boolean playPremium = false;

    RecyclerView released_episodes;
    List<EpisodeList> episodeList;
    EpisodeListAdepter_player myadepter;
    private final long currentPosition = 0;
    CardView animatedCardView;
    ImageView fullscreen;
    YouTubeOverlay youtube_overlay;
    private int currentlyPlayingPosition = -1;
    private boolean isExpanded = false;

    public static long current_pos_pip;
    Boolean contentLoaded = false;

    private Runnable savePositionRunnable;
    PowerManager.WakeLock wakeLock;
    int Main_ID, sourceID;
    LinearLayout Quality_settings;
    Button Play_Next_btn;
    MaterialCardView close_button;
    String Next_Ep_Avilable;
    int ct;
    DefaultTrackSelector trackSelector;
    String userData = null;
    String ContentType = null;
    MergingMediaSource nMediaSource = null;
    ResumeContentDatabase db;
    int wsType;
    String url;
    String DrmUuid = "";

    String DrmLicenseUri = "";
    Map<String, String> defaultRequestProperties = new HashMap<>();
    int resumeContentID;
    Context context = this;
    String userAgent = "";

    Button Skip_Intro_btn;
    String intro_end, intro_start;
    TinyDB tinyDB;
    long resumePosition = 0;
    int Current_List_Position = 0;
    WebView webView;
    int userId;
    String source, cpUrl, mContentType;
    int skip_available;
    TextView resizeModeTextView;
    LinearLayout settings_icon;
    View settings, quality_layout;
    MaterialCardView quality_server;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }


        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Player:No Sleep");
        wakeLock.acquire(300 * 60 * 1000L /*300 minutes*/);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_web_player);
        webPlayer = findViewById(R.id.playerView);
        youtube_overlay = findViewById(R.id.ytOverlay);

        youtube_overlay.performListener(new YouTubeOverlay.PerformListener() {
            @Override
            public void onAnimationStart() {
                youtube_overlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd() {
                youtube_overlay.setVisibility(View.GONE);
            }

            @SuppressLint("WrongConstant")
            @Nullable
            @Override
            public Boolean shouldForward(@NotNull Player player, @NotNull DoubleTapPlayerView doubleTapPlayerView, float v) {
                if (player.getPlaybackState() == PlaybackState.STATE_ERROR || player.getPlaybackState() == PlaybackState.STATE_NONE || player.getPlaybackState() == PlaybackState.STATE_STOPPED) {

                    webPlayer.cancelInDoubleTapMode();
                    return null;
                }

                if (player.getCurrentPosition() > 500 && v < webPlayer.getWidth() * 0.35)
                    return false;

                if (player.getCurrentPosition() < player.getDuration() && v > webPlayer.getWidth() * 0.65)
                    return true;

                return null;
            }
        });
        Intent intent = getIntent();
        DrmUuid = intent.getExtras().getString("DrmUuid");
        DrmLicenseUri = intent.getExtras().getString("DrmLicenseUri");

        Main_ID = intent.getExtras().getInt("contentID");
        sourceID = intent.getExtras().getInt("SourceID");
        String name = intent.getExtras().getString("name");
        TextView contentSecondName = webPlayer.findViewById(R.id.contentSecondName);
        contentSecondName.setText(name);
        source = intent.getExtras().getString("source");
        url = intent.getExtras().getString("url");
        cpUrl = url;
        Log.d("toastUr", cpUrl);
        animatedCardView = findViewById(R.id.animatedCardView);

        released_episodes = webPlayer.findViewById(R.id.released_episodes);

        Loadseasons(Main_ID);
        if (intent.getExtras().getString("Content_Type") != null) {
            mContentType = intent.getExtras().getString("Content_Type");
            Log.d("Seres", mContentType);
            if (mContentType.equals("Movie")) {
                ct = 1;
            } else if (mContentType.equals("WebSeries")) {
                ct = 2;
            }
        }
        settings = findViewById(R.id.settings);
        settings_icon = findViewById(R.id.settings_icon);
        settings_icon.setOnClickListener(v ->
        {
            settings.setVisibility(View.VISIBLE);
            webPlayer.setUseController(false);
        });
        resizeModeLayout = findViewById(R.id.resize_mode);
        resizeModeTextView = findViewById(R.id.resizeModeTextView);
        resizeModeLayout.setOnClickListener(v ->
        {
            resizeModeLayout.setCardBackgroundColor(getResources().getColor(R.color.col_bg));

            webPlayer.setUseController(false);
        });

        quality_server = findViewById(R.id.quality_server);
        quality_layout = findViewById(R.id.quality_layout);
        quality_server.setOnClickListener(v ->
        {
            quality_server.setCardBackgroundColor(getResources().getColor(R.color.col_bg));
            quality_layout.setVisibility(View.VISIBLE);
            webPlayer.setUseController(false);
        });
        close_button = findViewById(R.id.close_button);
        close_button.setOnClickListener(v ->
        {
            quality_server.setCardBackgroundColor(getResources().getColor(R.color.co_trans));
            quality_layout.setVisibility(View.GONE);
            settings.setVisibility(View.GONE);
            webPlayer.setUseController(true);
        });


        db = ResumeContentDatabase.getDbInstance(this.getApplicationContext());
        resumePosition = intent.getExtras().getLong("position");
        TextView contentFirstName = webPlayer.findViewById(R.id.contentFirstName);
        skip_available = intent.getExtras().getInt("skip_available");
        intro_start = intent.getExtras().getString("intro_start");
        intro_end = intent.getExtras().getString("intro_end");
        LinearLayout bottomSheet = webPlayer.findViewById(R.id.bottom_sheet);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(100); // Set the initial peek height
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @OptIn(markerClass = UnstableApi.class)
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setPeekHeight(300);
                    ConstraintLayout topLayout = findViewById(R.id.constraintLayout2);
                    topLayout.setVisibility(View.GONE);
                    ConstraintLayout bglayout = findViewById(R.id.bglayout);
                    bglayout.setVisibility(View.VISIBLE);
                    TabLayout tabLayout = findViewById(R.id.tab_epsiodes);
                    tabLayout.setVisibility(View.VISIBLE);
                    webPlayer.setControllerHideOnTouch(false);
                    webPlayer.setControllerShowTimeoutMs(0);
                    exoPlayer.pause();
                    TextView sheetExpander = webPlayer.findViewById(R.id.sheetexpander);
                    ViewGroup.MarginLayoutParams layoutParams2 = (ViewGroup.MarginLayoutParams) sheetExpander.getLayoutParams();
                    layoutParams2.topMargin = dpToPx(30);
                    sheetExpander.setLayoutParams(layoutParams2);
                    sheetExpander.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_keyboard_double_arrow_up_24, 0);
                    sheetExpander.setTextSize(20);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    ConstraintLayout topLayout = findViewById(R.id.constraintLayout2);
                    topLayout.setVisibility(View.VISIBLE);
                    ConstraintLayout bglayout = findViewById(R.id.bglayout);
                    bglayout.setVisibility(View.GONE);
                    TabLayout tabLayout = findViewById(R.id.tab_epsiodes);
                    tabLayout.setVisibility(View.GONE);
                    webPlayer.setControllerShowTimeoutMs(2000);
                    webPlayer.setControllerHideOnTouch(true);
                    exoPlayer.play();
                    bottomSheetBehavior.setPeekHeight(120);
                    TextView sheetExpander = webPlayer.findViewById(R.id.sheetexpander);
                    sheetExpander.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_keyboard_arrow_down_24, 0);
                    ViewGroup.MarginLayoutParams layoutParams1 = (ViewGroup.MarginLayoutParams) sheetExpander.getLayoutParams();
                    layoutParams1.topMargin = dpToPx(0);
                    sheetExpander.setLayoutParams(layoutParams1);
                    sheetExpander.setTextSize(15);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                ConstraintLayout bglayout = findViewById(R.id.bglayout);
                if (slideOffset > 0) {
                    // Show the bglayout with a fade-in animation when sliding up
                    bglayout.setVisibility(View.VISIBLE);
                    bglayout.setAlpha(slideOffset);
                } else {
                    bglayout.setVisibility(View.GONE);
                    bglayout.setAlpha(slideOffset);
                }
            }
        });
        TextView sheetExpander = webPlayer.findViewById(R.id.sheetexpander);
        // Replace bottomSheet with your bottom sheet view

        sheetExpander.setOnClickListener(v -> {
            if (isExpanded) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                // Change the icon to expand
                sheetExpander.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_keyboard_arrow_down_24, 0);
            } else {
                // Expand the bottom sheet
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                // Change the icon to collapse
                sheetExpander.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_arrow_drop_up_24, 0);
            }
            // Toggle the state flag
            isExpanded = !isExpanded;
        });
        if (intent.getExtras().getString("Content_Type") != null || intent.getExtras().getString("Current_List_Position") != null || intent.getExtras().getString("Next_Ep_Avilable") != null) {
            ContentType = intent.getExtras().getString("Content_Type");
            Current_List_Position = intent.getExtras().getInt("Current_List_Position");
            Next_Ep_Avilable = intent.getExtras().getString("Next_Ep_Avilable");

        }
        if (mContentType.equals("WebSeries")) {
            RequestQueue queue = Volley.newRequestQueue(this);
            @SuppressLint("HardwareIds") StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getWebSeriesDetails/" + Main_ID, response -> {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);

                contentFirstName.setText(jsonObject.get("name").getAsString());

                String releaseDate = "";
                if (!jsonObject.get("release_date").getAsString().equals("")) {
                    releaseDate = jsonObject.get("release_date").getAsString();
                }

                int type = jsonObject.get("type").getAsInt();
                wsType = type;

                db = ResumeContentDatabase.getDbInstance(this.getApplicationContext());
                if (db.resumeContentDao().getResumeContentid(Main_ID) == 0) {

                } else {
                    resumeContentID = db.resumeContentDao().getResumeContentid(Main_ID);
                }

                if (userData != null) {
                    GospelUtil.setWatchLog(context, String.valueOf(userId), jsonObject.get("id").getAsInt(), 2, Constants.apiKey);
                } else {
                    GospelUtil.setWatchLog(context, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), jsonObject.get("id").getAsInt(), 2, Constants.apiKey);
                }

            }, error -> {
                // Do nothing
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("x-api-key", Constants.apiKey);
                    return params;
                }
            };
            queue.add(sr);
        }

        AtomicReference<Boolean> isBackPressed = new AtomicReference<>(false);

        Skip_Intro_btn = webPlayer.findViewById(R.id.Skip_Intro_btn);
        Skip_Intro_btn.setOnClickListener(view -> exoPlayer.seekTo(Get_mil_From_Time(intro_end)));

        Play_Next_btn = webPlayer.findViewById(R.id.Play_Next_btn);
        Play_Next_btn.setOnClickListener(view -> {
            if (ContentType.equals("WebSeries")) {
                if (Next_Ep_Avilable.equals("Yes")) {
                    Play_Next_btn.setText(R.string.playing_now);
                    Intent intent1 = new Intent();
                    intent1.putExtra("Current_List_Position", Current_List_Position);
                    setResult(RESULT_OK, intent1);

                    isBackPressed.set(true);
                    releasePlayer();
                    handler.removeCallbacks(runnable);
                    finish();
                }
            }
        });


        if (resumePosition == 0) {
            if (db.resumeContentDao().getResumeContentid(Main_ID) != 0) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Resume!");
                alertDialog.setMessage("Do You Want to Resume From Where You Left?");
                alertDialog.setCancelable(false);

                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Start Over", (dialog, which) -> {
                    if (mContentType.equals("WebSeries")) {
                        dialog.dismiss();
                        Prepare_Source(source, url, DrmUuid, DrmLicenseUri);
                        db.resumeContentDao().updateSource(source, url, wsType, resumeContentID);
                    }
                });
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Resume", (dialog, which) -> {
                    if (mContentType.equals("WebSeries")) {
                        resumePosition = db.resumeContentDao().getResumeContentPosition(Main_ID);
                        Prepare_Source(db.resumeContentDao().getResumeContentSourceType(Main_ID), db.resumeContentDao().getResumeContentSourceUrl(Main_ID), "", "");
                        contentSecondName.setText(db.resumeContentDao().getResumeContentName(Main_ID));
                    }

                });
                alertDialog.show();
            } else {
                Prepare_Source(source, url, DrmUuid, DrmLicenseUri);
            }
        } else {
            Prepare_Source(source, url, DrmUuid, DrmLicenseUri);
        }


        Quality_settings = webPlayer.findViewById(R.id.quality);
        Quality_settings.setOnClickListener(v -> {
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo;
            DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
            TrackSelectionDialog trackSelectionDialog = TrackSelectionDialog.createForPlayer(exoPlayer, dismissedDialog -> {
            });
            trackSelectionDialog.show(getSupportFragmentManager(), null);
        });
        LinearLayout subtitle_audio = webPlayer.findViewById(R.id.audio_sub);
        subtitle_audio.setOnClickListener(v -> {
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo;
            DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
            AudioTrackSelectionDialog trackSelectionDialog = AudioTrackSelectionDialog.createForPlayer(exoPlayer, dismissedDialog -> {
            });
            trackSelectionDialog.show(getSupportFragmentManager(), null);
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void Prepare_Source(String source, String url, String drmUuid, String drmLicenseUri) {
        DataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory().setUserAgent(userAgent).setKeepPostFor302Redirects(true).setAllowCrossProtocolRedirects(true).setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS).setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS).setDefaultRequestProperties(defaultRequestProperties);
        DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(context, httpDataSourceFactory);
        UUID drmUUID = C.UUID_NIL;
        if (DrmUuid != null) {
            if (DrmUuid.equals("WIDEVINE")) {
                drmUUID = C.WIDEVINE_UUID;
            } else if (DrmUuid.equals("PLAYREADY")) {
                drmUUID = C.PLAYREADY_UUID;
            } else if (DrmUuid.equals("CLEARKEY")) {
                drmUUID = C.CLEARKEY_UUID;
            } else {
                drmUUID = C.UUID_NIL;
            }
        }

        if (source.equalsIgnoreCase("m3u8")) {
            if (url != null) {
                MediaItem mediaItem = new MediaItem.Builder().setMimeType(MimeTypes.APPLICATION_M3U8).setUri(url).build();
                MediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).setAllowChunklessPreparation(true).createMediaSource(mediaItem);
                initializePlayer(hlsMediaSource);
            }
        } else if (source.equalsIgnoreCase("dash")) {
            MediaItem mediaItem = new MediaItem.Builder().setMimeType(MimeTypes.APPLICATION_MPD).setUri(url).setDrmConfiguration(new MediaItem.DrmConfiguration.Builder(drmUUID).setLicenseUri(DrmLicenseUri).build()).build();
            MediaSource dashMediaSource = new DashMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
            initializePlayer(dashMediaSource);
        } else if (source.equalsIgnoreCase("mp4")) {
            MediaItem mediaItem = new MediaItem.Builder().setMimeType(MimeTypes.APPLICATION_MP4).setUri(url).build();
            MediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);

            initializePlayer(progressiveMediaSource);
        } else if (source.equalsIgnoreCase("mkv")) {
            MediaItem mediaItem = new MediaItem.Builder().setMimeType(MimeTypes.APPLICATION_MATROSKA).setUri(url).build();
            MediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
            initializePlayer(progressiveMediaSource);

        } else if (source.equals("Youtube")) {

        } else if (source.equals("Dailymotion")) {
            makeJsonObjectRequest();
        } else if (source.equals("Tubi")) {
            String[] urlParts = cpUrl.split("/");
            String movieId = urlParts[urlParts.length - 2];
            String api = "https://tubitv.com/oz/videos/" + movieId + "/content";
            RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, api, null, response -> {
                try {
                    JSONArray videoResources = response.getJSONArray("video_resources");
                    JSONObject rawManifest = (JSONObject) videoResources.get(0);
                    JSONObject manifest = rawManifest.getJSONObject("manifest");
                    String m3u8Url = manifest.getString("url");

                    MediaItem mediaItem = new MediaItem.Builder().setMimeType(MimeTypes.APPLICATION_M3U8).setUri(m3u8Url).build();
                    MediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).setAllowChunklessPreparation(true).createMediaSource(mediaItem);
                    initializePlayer(hlsMediaSource);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Gospel Stream error", Toast.LENGTH_SHORT).show();
                }
            }, Throwable::printStackTrace);

            requestQueue.add(jsonObjectRequest);
        }
    }
    private void Loadseasons(int mainid) {
        TabLayout tabLayout = findViewById(R.id.tab_epsiodes);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getSeasons/" + mainid, response -> {

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

                    tabLayout.removeAllTabs();

                    // Add tabs dynamically
                    for (String season : seasonList) {
                        TabLayout.Tab tab = tabLayout.newTab();
                        tab.setText(season);
                        tabLayout.addTab(tab);
                    }


                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {

                            loadSeasonDetails(mainid, (String) tab.getText());
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });


                    loadSeasonDetails(Main_ID, (String) Objects.requireNonNull(tabLayout.getTabAt(0)).getText());
                } else {
                    tabLayout.setVisibility(View.GONE);
                }

            } else {
                tabLayout.setVisibility(View.GONE);
            }
        }, error -> {
            //Do Nothing
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", Constants.apiKey);
                return params;
            }
        };
        queue.add(sr);

    }

    private void loadSeasonDetails(int id, String seasonName) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, Constants.url + "getSeasonDetails", response -> {
            if (!response.equals("No Data Avaliable")) {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                int seasonId = jsonObject.get("id").getAsInt();
                loadEpisodes(id, seasonId);
            }
        }, error -> {
            // Do nothing
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("WebSeriesID", String.valueOf(id));
                params.put("seasonName", seasonName); // Use the selected season name
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


    @SuppressLint("NotifyDataSetChanged")
    private void loadEpisodes(int id, int seasonId) {
        if (episodeList != null) {
            episodeList.clear();
            myadepter.notifyDataSetChanged();
        }

        RequestQueue queue = Volley.newRequestQueue(context);
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
                            episodeList.add(new EpisodeList(Sid, episoadeName, episoadeImage, episoadeDescription, currentSeasonId, downloadable, nType, source, url, skipAvailable, introStart, introEnd, true, true, drm_uuid, drm_license_uri));
                        }
                    }
                }


                myadepter = new EpisodeListAdepter_player(id, context, webView, Constants.url, Constants.apiKey, episodeList);
                if (webSeriesEpisodeitemType == 1) {
                    released_episodes.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                } else {
                    released_episodes.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                }
                released_episodes.setAdapter(myadepter);
                myadepter.setCurrentlyPlayingPosition(currentlyPlayingPosition);
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
    @OptIn(markerClass = UnstableApi.class)
    private void makeJsonObjectRequest() {
        String extractUrl = getMediaID(cpUrl);
        String url = "https://www.dailymotion.com/player/metadata/video/" + extractUrl;
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory().setUserAgent(userAgent).setKeepPostFor302Redirects(true).setAllowCrossProtocolRedirects(true).setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS).setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS).setDefaultRequestProperties(defaultRequestProperties);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                // Extract the "m3u8" URL from the JSON response
                JSONObject qualities = response.getJSONObject("qualities");
                JSONObject auto = qualities.getJSONArray("auto").getJSONObject(0);
                String dailyurl = auto.getString("url");
                MediaItem mediaItem = new MediaItem.Builder().setMimeType(MimeTypes.APPLICATION_M3U8).setUri(dailyurl).build();
                MediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).setAllowChunklessPreparation(true).createMediaSource(mediaItem);
                initializePlayer(hlsMediaSource);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
            }
        });

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    private String getMediaID(String url) {
        url = url.substring(url.lastIndexOf("/") + 1);
        return url;
    }

    @OptIn(markerClass = UnstableApi.class)


    private void finishPlayer() {
        releasePlayer();
        handler.removeCallbacks(runnable);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!isBackPressed) {
            pausePlayer();
        }
    }

    private String extractVideoId(String url) {
        // Use a regular expression to extract the video ID from the URL
        Pattern pattern = Pattern.compile("/video/(\\d+)/?");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (exoPlayer != null) {
                if (exoPlayer.isPlaying()) {
                    long apprxDuration = exoPlayer.getDuration() - 5000;
                    if (exoPlayer.getCurrentPosition() > apprxDuration) {
                        db.resumeContentDao().delete(resumeContentID);
                    } else {
                        db.resumeContentDao().update(exoPlayer.getCurrentPosition(), resumeContentID);
                    }
                }
            }

            //Skip Feature
            if (skip_available == 1) {
                if (exoPlayer != null) {
                    if (intro_start.equals("") || intro_start.equals("0") || intro_end.equals("") || intro_end.equals("0")) {
                        Skip_Intro_btn.setVisibility(View.GONE);
                    } else {
                        if (Get_mil_From_Time(intro_start) < exoPlayer.getContentPosition() && Get_mil_From_Time(intro_end) > exoPlayer.getContentPosition()) {
                            Skip_Intro_btn.setVisibility(View.VISIBLE);
                        } else {
                            Skip_Intro_btn.setVisibility(View.GONE);
                        }
                    }
                }

            } else {
                Skip_Intro_btn.setVisibility(View.GONE);
            }
            handler.postDelayed(runnable, 1000);
        }
    };

    @OptIn(markerClass = UnstableApi.class)
    void initializePlayer(MediaSource mediaSource) {

        ExoTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        trackSelector = new DefaultTrackSelector(WebPlayer.this, videoTrackSelectionFactory);
        trackSelector.setParameters(trackSelector.buildUponParameters().setPreferredTextLanguage(preferredLanguage));
        NextRenderersFactory renderersFactory = new NextRenderersFactory(this);
        renderersFactory.setExtensionRendererMode(NextRenderersFactory.EXTENSION_RENDERER_MODE_ON);
        exoPlayer = new ExoPlayer.Builder(this, renderersFactory).setTrackSelector(trackSelector).setSeekForwardIncrementMs(10000).setSeekBackIncrementMs(10000).build();
        if (youtube_overlay != null) {
            youtube_overlay.player(exoPlayer);
        }

        if (webPlayer != null) {
            webPlayer.setPlayer(exoPlayer);
            webPlayer.setKeepScreenOn(true);
        }


        //Player Speed


        // Custom Subtitle

        AtomicInteger subContentID = new AtomicInteger();
        if (resumePosition != 0 && webPlayer != null) {

            subContentID.set(Main_ID);
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest sr = new StringRequest(Request.Method.POST, Constants.url + "getsubtitle" + subContentID.get() + "/" + ct, response -> {
                if (!response.equals("No Data Avaliable")) {
                    JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                    int count = 0;
                    for (JsonElement rootElement : jsonArray) {
                        count++;

                        JsonObject rootObject = rootElement.getAsJsonObject();

                        MediaItem.Subtitle subtitle = null;
                        if (rootObject.get("mime_type").getAsString().equals("WebVTT")) {
                            subtitle = new MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.TEXT_VTT, rootObject.get("language").getAsString());
                        } else if (rootObject.get("mime_type").getAsString().equals("TTML") || rootObject.get("mime_type").getAsString().equals("SMPTE-TT")) {
                            subtitle = new MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_TTML, rootObject.get("language").getAsString());
                        } else if (rootObject.get("mime_type").getAsString().equals("SubRip")) {
                            subtitle = new MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_SUBRIP, rootObject.get("language").getAsString());
                        } else if (rootObject.get("mime_type").getAsString().equals("SubStationAlpha-SSA)") || rootObject.get("mime_type").getAsString().equals("SubStationAlpha-ASS)")) {
                            subtitle = new MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.TEXT_SSA, rootObject.get("language").getAsString());
                        } else {
                            subtitle = new MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_SUBRIP, rootObject.get("language").getAsString());
                        }

                        MediaSource textMediaSource = new SingleSampleMediaSource.Factory(new DefaultDataSource.Factory(this)).createMediaSource(subtitle, C.TIME_UNSET);

                        if (nMediaSource == null) {
                            nMediaSource = new MergingMediaSource(mediaSource, textMediaSource);
                        } else {
                            nMediaSource = new MergingMediaSource(nMediaSource, textMediaSource);
                        }

                        if (count == jsonArray.size()) {
                            if (nMediaSource != null) {
                                exoPlayer.setMediaSource(nMediaSource);
                                exoPlayer.prepare();
                                exoPlayer.setPlayWhenReady(true);

                                if (resumePosition != 0 && exoPlayer != null) {
                                    exoPlayer.seekTo(resumePosition);
                                }
                            } else {
                                exoPlayer.setMediaSource(mediaSource);
                                exoPlayer.prepare();
                                exoPlayer.setPlayWhenReady(true);

                                if (resumePosition != 0 && exoPlayer != null) {
                                    exoPlayer.seekTo(resumePosition);
                                }
                            }
                        }
                    }
                } else {
                    exoPlayer.setMediaSource(mediaSource);
                    exoPlayer.prepare();
                    exoPlayer.setPlayWhenReady(true);

                    if (resumePosition != 0 && exoPlayer != null) {
                        exoPlayer.seekTo(resumePosition);
                    }
                }
            }, error -> {
                exoPlayer.setMediaSource(mediaSource);
                exoPlayer.prepare();
                exoPlayer.setPlayWhenReady(true);

                if (resumePosition != 0 && exoPlayer != null) {
                    exoPlayer.seekTo(resumePosition);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("x-api-key", Constants.apiKey);
                    return params;
                }
            };
            queue.add(sr);

        } else {
            subContentID.set(sourceID);

            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest sr = new StringRequest(Request.Method.POST, Constants.url + "getsubtitle/" + subContentID.get() + "/" + ct, response -> {

                if (!response.equals("No Data Avaliable")) {
                    JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                    int count = 0;
                    for (JsonElement rootElement : jsonArray) {
                        count++;

                        JsonObject rootObject = rootElement.getAsJsonObject();

                        MediaItem.Subtitle subtitle = null;
                        if (rootObject.get("mime_type").getAsString().equals("WebVTT")) {
                            subtitle = new MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.TEXT_VTT, rootObject.get("language").getAsString());
                        } else if (rootObject.get("mime_type").getAsString().equals("TTML") || rootObject.get("mime_type").getAsString().equals("SMPTE-TT")) {
                            subtitle = new MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_TTML, rootObject.get("language").getAsString());
                        } else if (rootObject.get("mime_type").getAsString().equals("SubRip")) {
                            subtitle = new MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_SUBRIP, rootObject.get("language").getAsString());
                        } else if (rootObject.get("mime_type").getAsString().equals("SubStationAlpha-SSA)") || rootObject.get("mime_type").getAsString().equals("SubStationAlpha-ASS)")) {
                            subtitle = new MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.TEXT_SSA, rootObject.get("language").getAsString());
                        } else {
                            subtitle = new MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_SUBRIP, rootObject.get("language").getAsString());
                        }

                        MediaSource textMediaSource = new SingleSampleMediaSource.Factory(new DefaultDataSource.Factory(this)).createMediaSource(subtitle, C.TIME_UNSET);

                        if (nMediaSource == null) {
                            nMediaSource = new MergingMediaSource(mediaSource, textMediaSource);
                        } else {
                            nMediaSource = new MergingMediaSource(nMediaSource, textMediaSource);
                        }

                        if (exoPlayer != null) {
                            if (nMediaSource != null) {
                                exoPlayer.setMediaSource(nMediaSource);
                                exoPlayer.prepare();
                                exoPlayer.setPlayWhenReady(true);

                                if (resumePosition != 0) {
                                    exoPlayer.seekTo(resumePosition);
                                }
                            } else {
                                try {
                                    exoPlayer.setMediaSource(mediaSource);
                                    exoPlayer.prepare();
                                    exoPlayer.setPlayWhenReady(true);

                                    if (resumePosition != 0) {
                                        exoPlayer.seekTo(resumePosition);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d("Error captureed", "" + e);
                                }
                            }
                        } else {
                            // Handle the case where exoPlayer is null
                        }


                    }
                } else {
                    try {
                        exoPlayer.setMediaSource(mediaSource);
                        exoPlayer.prepare();
                        exoPlayer.setPlayWhenReady(true);
                        if (resumePosition != 0 && exoPlayer != null) {
                            exoPlayer.seekTo(resumePosition);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, error -> {
                exoPlayer.setMediaSource(mediaSource);
                exoPlayer.prepare();
                exoPlayer.setPlayWhenReady(true);

                if (resumePosition != 0 && exoPlayer != null) {
                    exoPlayer.seekTo(resumePosition);
                }
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
        exoPlayer.addListener(new Player.Listener() {

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    // Active playback.
                    contentLoaded = true;

                    db.resumeContentDao().updateDuration(exoPlayer.getDuration(), resumeContentID);

                }

                else if (playbackState == Player.STATE_ENDED) {
                    if (ContentType != null) {
                            if (Next_Ep_Avilable.equals("Yes")) {
                                Play_Next_btn.setVisibility(View.VISIBLE);
                                CountDownTimer mTimer = new CountDownTimer(5000, 100) {
                                    @SuppressLint("SetTextI18n")
                                    public void onTick(long millisUntilFinished) {
                                        Play_Next_btn.setText("Playing Next In " + millisUntilFinished / 1000 + "Sec");
                                    }

                                    @Override
                                    public void onFinish() {
                                        Play_Next_btn.setText("Playing Now");
                                        Intent intent = new Intent();
                                        intent.putExtra("Current_List_Position", Current_List_Position);
                                        setResult(RESULT_OK, intent);

                                        isBackPressed = true;
                                        releasePlayer();
                                        handler.removeCallbacks(runnable);
                                        finish();
                                    }
                                };
                                mTimer.start();
                            }


                    }

                } else {
                    // Paused by app.
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {

                Log.d("exoplayerError", "" + error);

            }
        });

    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer.clearVideoSurface();
            exoPlayer = null;
        }
    }


    private void pausePlayer() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.getPlaybackState();
            exoPlayer.pause();
        }
    }

    private long Get_mil_From_Time(String intro_end) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm:SS");
        Date parsed_date = null;
        try {
            parsed_date = format.parse(intro_end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat min = new SimpleDateFormat("mm");
        SimpleDateFormat sec = new SimpleDateFormat("SS");

        assert parsed_date != null;
        String Hour = hour.format(parsed_date);
        String Min = min.format(parsed_date);
        String Sec = sec.format(parsed_date);


        long m_hour = 0;
        long m_min = 0;
        long m_sec = 0;
        if (!Hour.equals("00")) {
            m_hour = Integer.parseInt(Hour) * 3600000L;
        }

        if (!Min.equals("00")) {
            m_min = Integer.parseInt(Min) * 60000L;
        }

        if (!Sec.equals("00")) {
            m_sec = Integer.parseInt(Sec) * 1000L;
        }

        Long F_mil = m_hour + m_min + m_sec;

        return F_mil;
    }

    Boolean isBackPressed = false;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBackPressed = true;
        releasePlayer();
        handler.removeCallbacks(runnable);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                int currentListPosition = data.getIntExtra("Current_List_Position", 0);
                int nextListPosition = currentListPosition + 1;
                EpisodeList myData = episodeList.get(nextListPosition);

                if (myData.getType() == 0) {

                    Intent intent = new Intent(this, WebPlayer.class);
                    intent.putExtra("contentID", Main_ID);
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

                    if (playPremium) {
                        Intent intent = new Intent(this, WebPlayer.class);
                        intent.putExtra("contentID", Main_ID);
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
}