package com.primemedia.marvels.adapter;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.card.MaterialCardView;
import com.primemedia.marvels.MovieDetails;
import com.primemedia.marvels.R;
import com.primemedia.marvels.list.ComingSoonItem;
import com.primemedia.marvels.list.MovieList;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FreshreleaseAdapter extends RecyclerView.Adapter<FreshreleaseAdapter.MyViewHolder> {
    private Context mContext;
    private final List<ComingSoonItem> mData;

    public FreshreleaseAdapter(Context mContext, List<ComingSoonItem> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comingsoonitem, parent, false);
        return new FreshreleaseAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ComingSoonItem currentItem = mData.get(position);
        holder.setImage(currentItem);

        holder.MainItems.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MovieDetails.class);
            intent.putExtra("ID", currentItem.getID());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView banner, upcoming_logoview;
        ConstraintLayout MainItems;
        TextView genress,desc_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            genress = itemView.findViewById(R.id.genress);
            banner = itemView.findViewById(R.id.thumb_imageview);
            MainItems = itemView.findViewById(R.id.MainItems);
            upcoming_logoview = itemView.findViewById(R.id.upcoming_logoview);

            desc_view = itemView.findViewById(R.id.desc_view);

        }

        void setImage(ComingSoonItem Thumbnail_Image) {
            genress.setText(Thumbnail_Image.getGenres());

            desc_view.setText(Thumbnail_Image.getDesc());
            Glide.with(mContext)
                    .load(Thumbnail_Image.getLogoMovies())
                    .into(upcoming_logoview);
            Glide.with(mContext)
                    .load(Thumbnail_Image.getThumbnail())
                    .into(banner);

        }


    }
}
