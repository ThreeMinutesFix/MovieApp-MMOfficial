package com.primemedia.marvels.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.primemedia.marvels.MovieDetails;
import com.primemedia.marvels.R;
import com.primemedia.marvels.WebSeriesDetails;
import com.primemedia.marvels.list.TrendingList;

import java.util.List;

public class TrendingListAdepter extends RecyclerView.Adapter<TrendingListAdepter.MyViewHolder> {
    private Context mContext;
    private List<TrendingList> mData;

    public TrendingListAdepter(Context mContext, List<TrendingList> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setImage(mData.get(position));

        holder.setNo(position);

        holder.Trending_Item_contenar.setOnClickListener(view -> {
            if(mData.get(position).getContentType()==1) {
                Intent intent = new Intent(mContext, MovieDetails.class);
                intent.putExtra("ID", mData.get(position).getID());
                mContext.startActivity(intent);
            } else if(mData.get(position).getContentType()==2) {
                Intent intent = new Intent(mContext, WebSeriesDetails.class);
                intent.putExtra("ID", mData.get(position).getID());
                mContext.startActivity(intent);
            }

        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView Trending_Item_thumbnail;
        View Premium_Tag;
        LinearLayout Trending_Item_contenar;
        ImageView no_image, no_10_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Trending_Item_thumbnail = itemView.findViewById(R.id.Trending_Item_thumbnail);
            Premium_Tag = itemView.findViewById(R.id.Premium_Tag);
            Trending_Item_contenar = itemView.findViewById(R.id.Trending_Item_contenar);
            no_image = itemView.findViewById(R.id.no_image);
            no_10_image = itemView.findViewById(R.id.no_10_image);
        }

        void setImage(TrendingList Thumbnail_Image) {

                Glide.with(mContext)
                        .load(Thumbnail_Image.getThumbnail())

                        .into(Trending_Item_thumbnail);

        }



        void setNo(int position) {
            if(position==0) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_1));
            } else if(position==1) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_2));
            } else if(position==2) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_3));
            } else if(position==3) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_4));
            } else if(position==4) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_5));
            } else if(position==5) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_6));
            } else if(position==6) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_7));
            } else if(position==7) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_8));
            } else if(position==8) {
                no_image.setVisibility(View.VISIBLE);
                no_10_image.setVisibility(View.GONE);
                no_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_9));
            } else if(position==9) {
                no_image.setVisibility(View.GONE);
                no_10_image.setVisibility(View.VISIBLE);
                no_10_image.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_10));
            }

        }
    }
}
