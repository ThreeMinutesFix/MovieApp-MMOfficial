package com.primemedia.studioflix.player;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.primemedia.studioflix.fragments.tabitems.Home.setWindowFlag;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.Tracks;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
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
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.primemedia.studioflix.Constants;
import com.primemedia.studioflix.R;
import com.primemedia.studioflix.dialogs.AudioTrackSelectionDialog;
import com.primemedia.studioflix.dialogs.TrackSelectionDialog;
import com.primemedia.studioflix.resume_content.ResumeContent;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import io.github.anilbeesetti.nextlib.media3ext.ffdecoder.NextRenderersFactory;

public class MoviePlayer extends AppCompatActivity {

    DoubleTapPlayerView playerView;
    String DrmUuid = "";

    String DrmLicenseUri = "";
    CardView animatedCardView;
    YouTubeOverlay youtube_overlay;
    private String dailyurl;
    View mDecorView;
    ImageView imageView;
    private TextView positionView;
    ResumeContentDatabase db;
    private ScaleGestureDetector scaleGestureDetector;
    int contentId;
    String preferredLanguage = "en";
    TextView contentFirstName;
    String genres;
    private PictureInPictureParams.Builder pictureInPictureParamsBuilder;

    private Runnable savePositionRunnable;

    public static ExoPlayer simpleExoPlayer;

    ImageView cast;
    boolean caster_available = false;
    private ConstraintLayout sliderContent;
    private boolean isAnimationRunning = false;
    Handler handler = new Handler();

    LinearLayout Quality_settings, related_layout;
    private DefaultTrackSelector trackSelector;
    int skip_available;
    Context context = this;
    NumberPicker numberPicker;
    String intro_start;
    String intro_end;
    MaterialButton Skip_Intro_btn;
    private boolean vpnStatus;
    private GospelUtil helperUtils;
    Boolean contentLoaded = false;
    String tubiUrl;
    int resumeContentID;
    long resumePosition = 0;
    String userData = null;
    String mContentType = "";
    int sourceID;
    int ct;
    LinearLayout textContainer, audiodelay;
    String Movie_name;
    public static long landpos;
    private Handler handler_position;
    private static final int BRIGHTNESS_STEP = 10;
    private static final int VOLUME_STEP = 1;
    MergingMediaSource nMediaSource = null;
    PowerManager.WakeLock wakeLock;
    int maxBrightness;
    String source;
    int contentID;
    String cpUrl = "";
    Map<String, String> defaultRequestProperties = new HashMap<>();
    String userAgent = "";
    private Bitmap thumbnailBitmap;
    TinyDB tinyDB;


    @SuppressLint("MissingInflatedId")
    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDecorView = getWindow().getDecorView();
        hideSystemUI();
        getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Constants.FLAG_SECURE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        if (Constants.FLAG_SECURE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Player:No Sleep");
        wakeLock.acquire(300 * 60 * 1000L /*300 minutes*/);
        maxBrightness = getMaxBrightness(this, 1000);
        setContentView(R.layout.activity_movieplayers);
        playerView = findViewById(R.id.playerView);

        youtube_overlay = findViewById(R.id.ytOverlay);
        sliderContent = findViewById(R.id.slider_content_movies);
        animatedCardView = findViewById(R.id.animatedCardView);
        Skip_Intro_btn = findViewById(R.id.Skip_Intro_btn);
        handler1.post(runnable);
        textContainer = findViewById(R.id.textContainer);

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
                if (player.getPlaybackState() == PlaybackState.STATE_ERROR ||
                        player.getPlaybackState() == PlaybackState.STATE_NONE ||
                        player.getPlaybackState() == PlaybackState.STATE_STOPPED) {

                    playerView.cancelInDoubleTapMode();
                    return null;
                }

                if (player.getCurrentPosition() > 500 && v < playerView.getWidth() * 0.35)
                    return false;

                if (player.getCurrentPosition() < player.getDuration() && v > playerView.getWidth() * 0.65)
                    return true;

                return null;
            }
        });
        contentFirstName = findViewById(R.id.contentFirstName);
        Intent intent = getIntent();
        tinyDB = new TinyDB(this);
        userAgent = Util.getUserAgent(this, "KAIOS");
        sourceID = intent.getExtras().getInt("SourceID");
        Movie_name = intent.getExtras().getString("name");
        contentID = intent.getExtras().getInt("contentID");
        source = intent.getExtras().getString("source");
        String url = intent.getExtras().getString("url");

        DrmUuid = intent.getExtras().getString("DrmUuid");
        DrmLicenseUri = intent.getExtras().getString("DrmLicenseUri");
        cpUrl = url;
        //ResumePlayback
        db = ResumeContentDatabase.getDbInstance(this.getApplicationContext());
        resumePosition = intent.getExtras().getLong("position");
        TextView contentSecondName = findViewById(R.id.contentSecondName);
        contentSecondName.setText(Movie_name);
        if (intent.getExtras().getString("Content_Type") != null) {
            mContentType = intent.getExtras().getString("Content_Type");

            if (mContentType.equals("Movie")) {
                ct = 1;
            }

        }

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getMovieDetails/" + contentID, response -> {
            Log.d("movienameResponse", response);
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            contentFirstName.setText(jsonObject.get("name").getAsString());
            TextView rating_context = findViewById(R.id.rating_context);
            rating_context.setText(jsonObject.get("context_cert").getAsString());
            Log.d("ratings", jsonObject.get("context_cert").getAsString());
            int type = jsonObject.get("type").getAsInt();
            String releaseDate = "";
            if (!jsonObject.get("release_date").getAsString().equals("")) {
                releaseDate = jsonObject.get("release_date").getAsString();
            }
            db = ResumeContentDatabase.getDbInstance(this.getApplicationContext());
            if (db.resumeContentDao().getResumeContentid(contentID) == 0) {
                db.resumeContentDao().insert(new ResumeContent(0, contentID, source, url, jsonObject.get("poster").getAsString(), Movie_name, releaseDate, 0, 0, intent.getExtras().getString("Content_Type"), type));
                resumeContentID = db.resumeContentDao().getResumeContentid(contentID);
            } else {
                resumeContentID = db.resumeContentDao().getResumeContentid(contentID);
                db.resumeContentDao().updateSource(source, url, type, resumeContentID);
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


        skip_available = intent.getExtras().getInt("skip_available");
        intro_start = intent.getExtras().getString("intro_start");
        intro_end = intent.getExtras().getString("intro_end");

        if (resumePosition == 0) {
            if (db.resumeContentDao().getResumeContentid(contentID) != 0) {
                resumePosition = db.resumeContentDao().getResumeContentPosition(contentID);
                Log.d("Myposition", " " + resumePosition);
                Prepare_Source(source, url, DrmUuid, DrmLicenseUri);
            } else {
                Prepare_Source(source, url, DrmUuid, DrmLicenseUri);
            }
        } else {
            Prepare_Source(source, url, DrmUuid, DrmLicenseUri);
        }

        startAnimation();

        LinearLayout quality = playerView.findViewById(R.id.quality);
        quality.setOnClickListener(v ->

        {
            if (contentLoaded) {
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo;
                DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment existingDialog = fragmentManager.findFragmentByTag("trackSelectionDialog");
                if (existingDialog == null) {

                    TrackSelectionDialog trackSelectionDialog = TrackSelectionDialog.createForPlayer(simpleExoPlayer, dismissedDialog -> {
                    });

                    trackSelectionDialog.show(fragmentManager, "trackSelectionDialog");
                }
            } else {
                if (Quality_settings != null) {
                    Quality_settings.setClickable(false);
                }

            }
        });
        LinearLayout Audio = playerView.findViewById(R.id.audio_sub);
        Audio.setOnClickListener(v ->
        {
            AudioTrackSelectionDialog trackSelectionDialog = AudioTrackSelectionDialog.createForPlayer(simpleExoPlayer, dismissedDialog -> {
            });
            trackSelectionDialog.show(getSupportFragmentManager(), null);
        });
    }

    private void startAnimation() {
        if (!isAnimationRunning) {
            isAnimationRunning = true;
            new Handler().postDelayed(() -> {
                textContainer.setVisibility(View.GONE);
                animatedCardView.setVisibility(View.VISIBLE);
                float distanceToSlideTop = -100;
                ObjectAnimator animatorTop = ObjectAnimator.ofFloat(animatedCardView, "translationY", distanceToSlideTop, 0f);
                animatorTop.setDuration(1000);
                animatorTop.start();
                new Handler().postDelayed(() -> {
                    sliderContent.setVisibility(View.VISIBLE);
                    textContainer.setVisibility(View.VISIBLE);
                    float distanceToSlideLeft = -30;
                    ObjectAnimator animatorLeft = ObjectAnimator.ofFloat(textContainer, "translationX", distanceToSlideLeft, 0f);
                    animatorLeft.setDuration(1000);
                    animatorLeft.start();
                }, 1000);

                new Handler().postDelayed(() -> {
                    // Make the ConstraintLayout invisible again
                    sliderContent.setVisibility(View.GONE);
                    animatedCardView.setVisibility(View.GONE);
                    isAnimationRunning = false;
                }, 10000); // Hide after 5 seconds

            }, 500);
        }
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

            MediaItem mediaItem = new MediaItem.Builder().setMimeType(MimeTypes.APPLICATION_M3U8).setUri(url).build();
            MediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).setAllowChunklessPreparation(true).createMediaSource(mediaItem);
            initializePlayer(hlsMediaSource);

        } else if (source.equalsIgnoreCase("dash")) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .setUri(url)
                    .setDrmConfiguration(new MediaItem.DrmConfiguration.Builder(drmUUID).
                            setLicenseUri(DrmLicenseUri).build())
                    .build();
            MediaSource dashMediaSource = new DashMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
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
            new YouTubeExtractor(context) {

                @Override
                protected void onExtractionComplete(@androidx.annotation.Nullable SparseArray<YtFile> ytFiles, @androidx.annotation.Nullable VideoMeta videoMeta) {
                    if (ytFiles != null && ytFiles.size() > 0) {
                        for (int i = 0; i < ytFiles.size(); i++) {
                            int itag = ytFiles.keyAt(i);
                            YtFile ytFile = ytFiles.get(itag);

                        }
                        int selectedItag = findBestQualityItag(ytFiles);
                        if (selectedItag != -1) {
                            YtFile selectedFile = ytFiles.get(selectedItag);
                            String downloadUrl = selectedFile.getUrl();
                            Log.d("UtPlayer", downloadUrl);
                            StartExoplayer(downloadUrl);
                        }
                    }
                }
            }.extract(cpUrl);
        } else if (source.equals("Dailymotion")) {
            makeJsonObjectRequest();
        } else if (source.equals("Tubi")) {
            String[] urlParts = cpUrl.split("/");
            String movieId = urlParts[urlParts.length - 2];
            String api = "https://tubitv.com/oz/videos/" + movieId + "/content";
            RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, api, null,
                    response -> {
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
                    },
                    Throwable::printStackTrace);

            requestQueue.add(jsonObjectRequest);
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void StartExoplayer(String downloadUrl) {
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory().setUserAgent(userAgent).setKeepPostFor302Redirects(true).setAllowCrossProtocolRedirects(true).setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS).setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS).setDefaultRequestProperties(defaultRequestProperties);
        MediaItem mediaItem = new MediaItem.Builder()
                .setMimeType(MimeTypes.APPLICATION_MP4)
                .setUri(downloadUrl)
                .build();
        MediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem);
        initializePlayer(progressiveMediaSource);
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
                dailyurl = auto.getString("url");
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
    private void initializePlayer(MediaSource mediaSource) {
        ExoTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        trackSelector = new DefaultTrackSelector(MoviePlayer.this, videoTrackSelectionFactory);
        trackSelector.setParameters(trackSelector.buildUponParameters()
                .setPreferredAudioLanguage("ta")
                .setPreferredTextLanguage("en")
                .setPreferredTextLanguage(preferredLanguage));

        NextRenderersFactory renderersFactory = new NextRenderersFactory(this);
        renderersFactory.setExtensionRendererMode(NextRenderersFactory.EXTENSION_RENDERER_MODE_ON);
        simpleExoPlayer = new ExoPlayer.Builder(this, renderersFactory)
                .setTrackSelector(trackSelector)
                .setSeekForwardIncrementMs(10000)
                .setSeekBackIncrementMs(10000)
                .build();
        youtube_overlay.player(simpleExoPlayer);
        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);
        simpleExoPlayer.seekTo(landpos);
        handler = new Handler();
        savePositionRunnable = new Runnable() {
            @Override
            public void run() {
                landpos = simpleExoPlayer.getCurrentPosition();
                handler_position.postDelayed(this, 1000);
            }
        };

        simpleExoPlayer.addListener(new Player.Listener() {
//            Runnable updateDurationRunnable;

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState == Player.STATE_READY) {
                    // Active playback.
                    contentLoaded = true;
                    db.resumeContentDao().updateDuration(simpleExoPlayer.getDuration(), resumeContentID);

                    Log.d("CurrentPosition", "" + resumePosition);
                    long currentPosition = simpleExoPlayer.getCurrentPosition();
                    if (currentPosition >= 0 && currentPosition <= 10000) {
                        if (!isAnimationRunning || simpleExoPlayer != null) {
                            startAnimation();
                        }
                    } else {
                        sliderContent.setVisibility(View.GONE);
                        animatedCardView.setVisibility(View.GONE);
                        isAnimationRunning = false;
                    }
                }
            }

            @Override
            public void onTracksChanged(Tracks tracks) {
                Player.Listener.super.onTracksChanged(tracks);


            }
        });
        AtomicInteger subContentID = new AtomicInteger();
        if (resumePosition != 0 && simpleExoPlayer != null) {
            if (ct == 1) {
                RequestQueue queue0 = Volley.newRequestQueue(context);
                StringRequest sr0 = new StringRequest(Request.Method.POST, Constants.url + "getcontentidfromurl/" + contentID + "/" + ct, response0 -> {
                    JsonObject jsonObject = new Gson().fromJson(response0, JsonObject.class);
                    int subCiD = jsonObject.get("id").getAsInt();
                    subContentID.set(subCiD);

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
                                if (count == jsonArray.size()) {
                                    if (nMediaSource != null) {
                                        simpleExoPlayer.setMediaSource(nMediaSource);
                                        simpleExoPlayer.prepare();
                                        simpleExoPlayer.setPlayWhenReady(true);

                                        if (resumePosition != 0 && simpleExoPlayer != null) {
                                            simpleExoPlayer.seekTo(resumePosition);
                                        }
                                    } else {
                                        simpleExoPlayer.setMediaSource(mediaSource);
                                        simpleExoPlayer.prepare();
                                        simpleExoPlayer.setPlayWhenReady(true);

                                        if (resumePosition != 0 && simpleExoPlayer != null) {
                                            simpleExoPlayer.seekTo(resumePosition);
                                        }
                                    }
                                }
                            }
                        } else {
                            simpleExoPlayer.setMediaSource(mediaSource);
                            simpleExoPlayer.prepare();
                            simpleExoPlayer.setPlayWhenReady(true);

                            if (resumePosition != 0 && simpleExoPlayer != null) {
                                simpleExoPlayer.seekTo(resumePosition);
                            }
                        }
                    }, error -> {
                        simpleExoPlayer.setMediaSource(mediaSource);
                        simpleExoPlayer.prepare();
                        simpleExoPlayer.setPlayWhenReady(true);

                        if (resumePosition != 0 && simpleExoPlayer != null) {
                            simpleExoPlayer.seekTo(resumePosition);
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
                }, error -> {
                    //Do Nothing
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("url", cpUrl);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put("x-api-key", Constants.apiKey);
                        return params;
                    }
                };
                queue0.add(sr0);
            } else {
                subContentID.set(contentID);
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
                                    simpleExoPlayer.setMediaSource(nMediaSource);
                                    simpleExoPlayer.prepare();
                                    simpleExoPlayer.setPlayWhenReady(true);
                                    if (resumePosition != 0 && simpleExoPlayer != null) {
                                        simpleExoPlayer.seekTo(resumePosition);
                                    }
                                } else {
                                    simpleExoPlayer.setMediaSource(mediaSource);
                                    simpleExoPlayer.prepare();
                                    simpleExoPlayer.setPlayWhenReady(true);

                                    if (resumePosition != 0 && simpleExoPlayer != null) {
                                        simpleExoPlayer.seekTo(resumePosition);
                                    }
                                }
                            }
                        }
                    } else {
                        simpleExoPlayer.setMediaSource(mediaSource);
                        simpleExoPlayer.prepare();
                        simpleExoPlayer.setPlayWhenReady(true);

                        if (resumePosition != 0 && simpleExoPlayer != null) {
                            simpleExoPlayer.seekTo(resumePosition);
                        }
                    }
                }, error -> {
                    simpleExoPlayer.setMediaSource(mediaSource);
                    simpleExoPlayer.prepare();
                    simpleExoPlayer.setPlayWhenReady(true);

                    if (resumePosition != 0 && simpleExoPlayer != null) {
                        simpleExoPlayer.seekTo(resumePosition);
                    }
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
                        if (count == jsonArray.size()) {
                            if (nMediaSource != null) {
                                simpleExoPlayer.setMediaSource(nMediaSource);
                            } else {
                                simpleExoPlayer.setMediaSource(mediaSource);
                            }
                            simpleExoPlayer.prepare();
                            simpleExoPlayer.setPlayWhenReady(true);
                            if (resumePosition != 0 && simpleExoPlayer != null) {
                                simpleExoPlayer.seekTo(resumePosition);
                            }
                        }
                    }
                } else {
                    simpleExoPlayer.setMediaSource(mediaSource);
                    simpleExoPlayer.prepare();
                    simpleExoPlayer.setPlayWhenReady(true);
                    if (resumePosition != 0 && simpleExoPlayer != null) {
                        simpleExoPlayer.seekTo(resumePosition);
                    }
                }
            }, error -> {
                simpleExoPlayer.setMediaSource(mediaSource);
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(true);
                if (resumePosition != 0 && simpleExoPlayer != null) {
                    simpleExoPlayer.seekTo(resumePosition);
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
    }


    private void hideSystemUI() {
        mDecorView.setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

    private int getMaxBrightness(MoviePlayer movieDetails, int i) {
        return 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer.clearVideoSurface();
            simpleExoPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        if (thumbnailBitmap != null) {
            thumbnailBitmap.recycle();
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private Handler handler1 = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (simpleExoPlayer != null) {
                if (simpleExoPlayer.isPlaying()) {
                    long apprxDuration = simpleExoPlayer.getDuration() - 5000;
                    if (simpleExoPlayer.getCurrentPosition() > apprxDuration) {
                        db.resumeContentDao().delete(resumeContentID);
                    } else {
                        db.resumeContentDao().update(simpleExoPlayer.getCurrentPosition(), resumeContentID);
                    }
                }
            }

            //Skip Feature
            if (skip_available == 1) {
                if (simpleExoPlayer != null) {
                    if (intro_start.equals("") || intro_start.equals("0") || intro_start.equals(null) || intro_end.equals("") || intro_end.equals("0") || intro_end.equals(null)) {
                        Skip_Intro_btn.setVisibility(View.GONE);
                    } else {
                        if (Get_mil_From_Time(intro_start) < simpleExoPlayer.getContentPosition() && Get_mil_From_Time(intro_end) > simpleExoPlayer.getContentPosition()) {
                            Skip_Intro_btn.setVisibility(View.VISIBLE);
                        } else {
                            Skip_Intro_btn.setVisibility(View.GONE);
                        }
                    }
                }

            } else {
                Skip_Intro_btn.setVisibility(View.GONE);
            }


            ////


            // Repeat every 1 seconds
            handler1.postDelayed(runnable, 1000);
        }
    };

    long Get_mil_From_Time(String Time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:SS");
        Date parsed_date = null;
        try {
            parsed_date = format.parse(Time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat min = new SimpleDateFormat("mm");
        SimpleDateFormat sec = new SimpleDateFormat("SS");

        String Hour = hour.format(parsed_date);
        String Min = min.format(parsed_date);
        String Sec = sec.format(parsed_date);


        long m_hour = 0;
        long m_min = 0;
        long m_sec = 0;
        if (!Hour.equals("00")) {
            m_hour = Integer.parseInt(Hour) * 3600000;
        }

        if (!Min.equals("00")) {
            m_min = Integer.parseInt(Min) * 60000;
        }

        if (!Sec.equals("00")) {
            m_sec = Integer.parseInt(Sec) * 1000;
        }

        Long F_mil = m_hour + m_min + m_sec;

        return F_mil;
    }


    private void adjustAudioDelay(ExoPlayer exoPlayer, long delayMillis) {
        exoPlayer.setPlaybackParameters(new PlaybackParameters(delayMillis, 1.0f));
    }

    @OptIn(markerClass = UnstableApi.class)
    private int findRendererIndexForType(MappingTrackSelector.MappedTrackInfo mappedTrackInfo, int trackType) {
        for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
            if (mappedTrackInfo.getRendererType(i) == trackType) {
                return i;
            }
        }
        return -1; // If the renderer is not found, return -1
    }


}