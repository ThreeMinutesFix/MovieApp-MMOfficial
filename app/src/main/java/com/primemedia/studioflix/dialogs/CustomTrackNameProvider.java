package com.primemedia.studioflix.dialogs;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.media3.common.Format;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.DefaultTrackNameProvider;
import androidx.media3.ui.TrackNameProvider;

@UnstableApi
public class CustomTrackNameProvider implements TrackNameProvider {
    private Resources resources;
    private DefaultTrackNameProvider defaultTrackNameProvider;

    @OptIn(markerClass = UnstableApi.class)
    public CustomTrackNameProvider(Resources resources) {
        this.resources = resources;
        this.defaultTrackNameProvider = new DefaultTrackNameProvider(resources);

    }

    @NonNull
    @OptIn(markerClass = UnstableApi.class)
    @Override
    public String getTrackName(Format format) {

        if (format.height != Format.NO_VALUE) {
            int height = format.height;
            if (height >= 815 && height < 1080) {
                return "HD Up to 720p";
            } else if (height >= 1080 && height < 2160) {
                return "UHD Up to 2160";
            } else if (height >= 480 && height < 815) {
                return "Data Saver";
            }
        }
        return format.height + "p";

    }


}
