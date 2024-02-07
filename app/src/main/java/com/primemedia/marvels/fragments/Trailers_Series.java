package com.primemedia.marvels.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.primemedia.marvels.Constants;
import com.primemedia.marvels.R;

import java.util.HashMap;
import java.util.Map;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;


public class Trailers_Series extends Fragment {
    private DefaultTrackSelector trackSelector;
    PlayerView PlayerView;
    private long playbackPosition = C.TIME_UNSET;
    View view;
    ExoPlayer exoPlayer;
    String trailerUrl;
    int mainId;
    Context MContext;
    Map<String, String> defaultRequestProperties = new HashMap<>();
    private static final String KEY_PLAYBACK_POSITION = "playback_position";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_trailers__series, container, false);
        PlayerView = view.findViewById(R.id.PlayerView);
        Intent intent = requireActivity().getIntent();
        mainId = intent.getExtras().getInt("ID");

        loadWebSeriesDetails(mainId);



        return view;
    }

    private void loadWebSeriesDetails(int mainId) {
        RequestQueue queue = Volley.newRequestQueue(MContext);
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getWebSeriesDetails/" + mainId, response ->
        {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            trailerUrl = jsonObject.get("youtube_trailer").getAsString();
            extractAndPlay(trailerUrl);
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
    private void extractAndPlay(String youtubeLink) {
        new YouTubeExtractor(MContext) {

            @Override
            protected void onExtractionComplete(@Nullable SparseArray<YtFile> ytFiles, @Nullable VideoMeta videoMeta) {
                if (ytFiles != null && ytFiles.size() > 0) {
                    for (int i = 0; i < ytFiles.size(); i++) {
                        int itag = ytFiles.keyAt(i);
                        YtFile ytFile = ytFiles.get(itag);
//                        Log.d("MyApp", "itag: " + itag + ", format: " + ytFile.getFormat());
                    }
                    int selectedItag = findBestQualityItag(ytFiles);
                    if (selectedItag != -1) {
                        YtFile selectedFile = ytFiles.get(selectedItag);
                        String downloadUrl = selectedFile.getUrl();

                        StartExoplayer(downloadUrl);
                    }
                }
            }
        }.extract(youtubeLink);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the playback position
        outState.putLong(KEY_PLAYBACK_POSITION, playbackPosition);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void StartExoplayer(String downloadUrl) {


        DataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setKeepPostFor302Redirects(true)
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                .setDefaultRequestProperties(defaultRequestProperties);
        DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(MContext, httpDataSourceFactory);

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
                DefaultTrackSelector(MContext, videoTrackSelectionFactory);
        exoPlayer = new ExoPlayer.Builder(MContext)

                .setTrackSelector(trackSelector)
                .setSeekForwardIncrementMs(10000)
                .setSeekBackIncrementMs(10000)
                .build();
        PlayerView.setPlayer(exoPlayer);
        PlayerView.setKeepScreenOn(true);
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
               if(exoPlayer!= null)
               {
                   if (playbackState == Player.STATE_READY && playWhenReady) {
                       playbackPosition = exoPlayer.getCurrentPosition();
                   }
               }
            }
        });

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
        MContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!requireActivity().isChangingConfigurations()) {
            releaseExoPlayer();
        } else {
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(false);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Restore the playback position
        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong(KEY_PLAYBACK_POSITION, C.TIME_UNSET);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!requireActivity().isChangingConfigurations()) {
            releaseExoPlayer();
        }
    }

    private void releaseExoPlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

}