package com.primemedia.studioflix.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.primemedia.studioflix.R;
import com.primemedia.studioflix.list.PlayMovieItemIist;
import com.primemedia.studioflix.player.EmbedStream;
import com.primemedia.studioflix.player.MoviePlayer;

import java.util.List;

public class PlayMovieItemListAdepter extends RecyclerView.Adapter<PlayMovieItemListAdepter.MyViewHolder> {
    private final Context mContext;
    private final List<PlayMovieItemIist> mData;
    private final boolean playPremium;
    private final int contentID;

    Context context;

    public PlayMovieItemListAdepter(int contentID, Context mContext, List<PlayMovieItemIist> mData, boolean playPremium) {
        this.contentID = contentID;
        this.mContext = mContext;
        this.mData = mData;
        this.playPremium = playPremium;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.movie_play_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setMovie_link_name(mData.get(position));
        holder.setmovie_link_quality(mData.get(position));
        holder.setmovie_link_size(mData.get(position));
        holder.setPremiumTag(mData.get(position));
        if(mData.size() == 1) {
            playMovie(position);
        }
        holder.movie_link_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playMovie(position);
            }
        });
    }
    private void playMovie(int position){
        if(mData.get(position).getLink_type() == 1) { //Premium
            if(playPremium) {
                if (mData.get(position).getType().equals("Embed")) {
                    Intent intent = new Intent(mContext, EmbedStream.class);
                    intent.putExtra("url", mData.get(position).getUrl());
                    intent.putExtra("name", mData.get(position).getName());
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, MoviePlayer.class);
                    intent.putExtra("contentID", contentID);
                    intent.putExtra("SourceID", mData.get(position).getId());
                    intent.putExtra("Content_Type", "Movie");
                    intent.putExtra("name", mData.get(position).getName());
                    intent.putExtra("source", mData.get(position).getType());
                    intent.putExtra("url", mData.get(position).getUrl());
                    intent.putExtra("DrmUuid", mData.get(position).getDrmUuid());
                    intent.putExtra("DrmLicenseUri", mData.get(position).getDrmLicenseUri());
                    intent.putExtra("skip_available", mData.get(position).getSkip_available());
                    intent.putExtra("intro_start", mData.get(position).getIntro_start());
                    intent.putExtra("intro_end", mData.get(position).getIntro_end());
                    mContext.startActivity(intent);
                }
            } else {
                Toast.makeText(mContext, "Onnum illa", Toast.LENGTH_SHORT).show();
            }

        } else {
            if (mData.get(position).getType().equals("Embed")) {
                Toast.makeText(mContext, "Onnum illa", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(mContext, MoviePlayer.class);
                intent.putExtra("contentID", contentID);
                intent.putExtra("SourceID", mData.get(position).getId());
                intent.putExtra("Content_Type", "Movie");
                intent.putExtra("name", mData.get(position).getName());
                intent.putExtra("source", mData.get(position).getType());
                intent.putExtra("url", mData.get(position).getUrl());

                intent.putExtra("DrmUuid", mData.get(position).getDrmUuid());
                intent.putExtra("DrmLicenseUri", mData.get(position).getDrmLicenseUri());

                intent.putExtra("skip_available", mData.get(position).getSkip_available());
                intent.putExtra("intro_start", mData.get(position).getIntro_start());
                intent.putExtra("intro_end", mData.get(position).getIntro_end());

                mContext.startActivity(intent);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView movie_link_card;
        TextView movie_link_name;
        TextView movie_link_quality;
        TextView movie_link_size;
        LinearLayout Premium_Tag;
        ConstraintLayout movie_play_item_bg;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            movie_link_card = itemView.findViewById(R.id.movie_link_card);
            movie_link_name = itemView.findViewById(R.id.movie_link_name);
            movie_link_quality = itemView.findViewById(R.id.movie_link_quality);
            movie_link_size = itemView.findViewById(R.id.movie_link_size);
            Premium_Tag = itemView.findViewById(R.id.Premium_Tag);
            movie_play_item_bg = itemView.findViewById(R.id.movie_play_item_bg);
        }

        void setMovie_link_name(PlayMovieItemIist movie_link_title) {
            if(movie_link_title.getName().equals("")) {
                movie_link_name.setVisibility(View.INVISIBLE);
            } else {
                movie_link_name.setVisibility(View.VISIBLE);
                movie_link_name.setText(movie_link_title.getName());
            }
        }
        void setmovie_link_quality(PlayMovieItemIist movie_link_quality_data) {
            if(movie_link_quality_data.getQuality().equals("")) {
                movie_link_quality.setVisibility(View.GONE);
            } else {
                movie_link_quality.setVisibility(View.VISIBLE);
                movie_link_quality.setText(movie_link_quality_data.getQuality());
            }
        }
        void setmovie_link_size(PlayMovieItemIist movie_link_size_data) {
            if(movie_link_size_data.getSize().equals("")) {
                movie_link_size.setVisibility(View.GONE);
            } else {
                movie_link_size.setVisibility(View.VISIBLE);
                movie_link_size.setText(String.format(", %s", movie_link_size_data.getSize()));
            }
        }

        void setPremiumTag(PlayMovieItemIist data) {
            if(data.getLink_type() == 1) {
                Premium_Tag.setVisibility(View.VISIBLE);
            } else {
                Premium_Tag.setVisibility(View.GONE);
            }
        }
    }
}
