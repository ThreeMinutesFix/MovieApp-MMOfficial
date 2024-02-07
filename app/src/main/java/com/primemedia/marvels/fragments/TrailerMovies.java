package com.primemedia.marvels.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.Player;
import androidx.media3.common.text.Cue;
import androidx.media3.common.text.CueGroup;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.MergingMediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.source.SingleSampleMediaSource;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.ExoTrackSelection;
import androidx.media3.ui.SubtitleView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.primemedia.marvels.Constants;
import com.primemedia.marvels.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;


@UnstableApi
public class TrailerMovies extends Fragment  {
    private DefaultTrackSelector trackSelector;
    androidx.media3.ui.PlayerView PlayerView;
    private long playbackPosition = C.TIME_UNSET;
    View view;
    MergingMediaSource nMediaSource = null;
    ExoPlayer exoPlayer;
    String trailerUrl;
    Context MContext;
    String subTitleurl;
    int mainId;

    CardView cardImageview;
    ImageView ImageLoader;
    String BgImage;
    private SubtitleView subtitleView;
    Map<String, String> defaultRequestProperties = new HashMap<>();
    private static final String KEY_PLAYBACK_POSITION = "playback_position";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_trailer_movies, container, false);
        PlayerView = view.findViewById(R.id.trailerPlayer);
        Intent intent = requireActivity().getIntent();
        mainId = intent.getExtras().getInt("ID");
        subtitleView = view.findViewById(R.id.subtitleView);
        ImageLoader = view.findViewById(R.id.ImageLoader);
        cardImageview = view.findViewById(R.id.cardImageview);
        loadMoviesDetails(mainId);

        return view;
    }

    private void loadMoviesDetails(int id) {
        RequestQueue queue = Volley.newRequestQueue(MContext);
        StringRequest sr = new StringRequest(Request.Method.GET, Constants.url + "getMovieDetails/" + id, response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            trailerUrl = jsonObject.get("youtube_trailer").getAsString();
            BgImage = jsonObject.get("banner").getAsString();
            subTitleurl = jsonObject.get("suburl").getAsString();

            Glide.with(MContext).load(BgImage).into(ImageLoader);
            ImageLoader.setOnClickListener(v ->
            {
                settrailervideo(trailerUrl,subTitleurl);
                cardImageview.setVisibility(View.GONE);
                PlayerView.setVisibility(View.VISIBLE);
            });

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

    @SuppressLint("StaticFieldLeak")
    private void settrailervideo(String youtubeLink,String subTitleurl) {
        new YouTubeExtractor(MContext) {

            @Override
            protected void onExtractionComplete(@Nullable SparseArray<YtFile> ytFiles, @Nullable VideoMeta videoMeta) {
                if (ytFiles != null && ytFiles.size() > 0) {
                    for (int i = 0; i < ytFiles.size(); i++) {
                        int itag = ytFiles.keyAt(i);
                        YtFile ytFile = ytFiles.get(itag);

                    }
                    int selectedItag = findBestQualityItag(ytFiles);
                    if (selectedItag != -1) {
                        YtFile selectedFile = ytFiles.get(selectedItag);
                        String downloadUrl = selectedFile.getUrl();

                        StartExoplayer(downloadUrl,subTitleurl);
                    }
                }
            }
        }.extract(youtubeLink);
    }


    @OptIn(markerClass = UnstableApi.class)
    private void StartExoplayer(String downloadUrl,String subTitleurl) {
        DataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setKeepPostFor302Redirects(true)
                .setAllowCrossProtocolRedirects(true)

                .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                .setDefaultRequestProperties(defaultRequestProperties);
        DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(MContext, httpDataSourceFactory);
        Uri Subtitles = Uri.parse(subTitleurl);

        MediaItem.SubtitleConfiguration subtitle =
                new MediaItem.SubtitleConfiguration.Builder(Subtitles)
                        .setMimeType(MimeTypes.TEXT_VTT)
                        .setLanguage("en")
                        .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                        .build();


        MediaItem mediaItem = new MediaItem.Builder()
                .setMimeType(MimeTypes.APPLICATION_MP4)
                .setUri(downloadUrl)
                .build();
        MediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem);
        MediaSource textMediaSource = new SingleSampleMediaSource.Factory(new DefaultDataSource.Factory(requireContext())).createMediaSource(subtitle, C.TIME_UNSET);
        if(nMediaSource == null) {
            nMediaSource = new MergingMediaSource(progressiveMediaSource, textMediaSource);
        } else {
            nMediaSource = new MergingMediaSource(nMediaSource, textMediaSource);
        }

        initializePlayer(nMediaSource);
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
                if (exoPlayer != null) {
                    if (playbackState == Player.STATE_READY && playWhenReady) {
                        playbackPosition = exoPlayer.getCurrentPosition();
                    }
                }
            }

            @Override
            public void onCues(@NonNull CueGroup cueGroup) {
                Player.Listener.super.onCues(cueGroup);
                subtitleView.setCues(cueGroup.cues);
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
        MContext = context;
    }
}