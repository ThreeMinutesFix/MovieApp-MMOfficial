package com.primemedia.studioflix.player;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.ui.PlayerView;

import com.primemedia.studioflix.R;

public class TrailerPlayer extends AppCompatActivity {
    PlayerView playerView;
    int ContentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer_player);
        playerView = findViewById(R.id.playerView);


    }
}