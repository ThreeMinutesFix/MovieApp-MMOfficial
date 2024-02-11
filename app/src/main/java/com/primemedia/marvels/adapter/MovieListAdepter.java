package com.primemedia.marvels.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.card.MaterialCardView;
import com.primemedia.marvels.MovieDetails;
import com.primemedia.marvels.R;
import com.primemedia.marvels.list.MovieList;
import com.primemedia.marvels.utility.Utils;


import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovieListAdepter extends RecyclerView.Adapter<MovieListAdepter.MyViewHolder> {

    private final Context mContext;
    private List<MovieList> mData;

    Context context;

    public MovieListAdepter(Context mContext, List<MovieList> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_releases, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MovieList currentItem = mData.get(position);
        holder.setImage(currentItem);
        holder.Movie_Item.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MovieDetails.class);
            intent.putExtra("ID", currentItem.getID());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView Thumbnail;
        MaterialCardView Movie_Item;
        TextView Title, rating;
        TextView movie_placeholders;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            movie_placeholders = itemView.findViewById(R.id.movie_placeholders);
            Thumbnail = itemView.findViewById(R.id.Movie_Item_thumbnail);

            Movie_Item = itemView.findViewById(R.id.Movie_Item);

        }

        void setImage(MovieList Thumbnail_Image) {
            movie_placeholders.setText(Thumbnail_Image.getTitle());
            Glide.with(context)
                    .load(Thumbnail_Image.getThumbnail())
                    .into(Thumbnail);

            Glide.with(context)
                    .asBitmap()
                    .load(Thumbnail_Image.getThumbnail())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Image is loaded, so hide the TextView
                            movie_placeholders.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // This is called when the image is cleared or removed
                            movie_placeholders.setVisibility(View.VISIBLE);
                        }
                    });
        }

        void setTitle(MovieList title_text) {
            Title.setText(title_text.getTitle());

        }
    }


}
