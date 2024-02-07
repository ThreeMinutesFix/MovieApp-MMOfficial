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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.primemedia.marvels.MovieDetails;
import com.primemedia.marvels.R;
import com.primemedia.marvels.WebSeriesDetails;
import com.primemedia.marvels.list.SearchList;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SearchListAdepter extends RecyclerView.Adapter<SearchListAdepter.MyViewHolder> {
    private Context mContext;
    private List<SearchList> mData;
    private List<SearchList> filteredList;
    Context context;

    public SearchListAdepter(Context mContext, List<SearchList> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.filteredList = new ArrayList<>(mData);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.movie_item_v2,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setTitle(mData.get(position));
        holder.setYear(mData.get(position));
        holder.setImage(mData.get(position));



        holder.Movie_Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mData.get(position).getContent_Type() == 1) {
                    Intent intent = new Intent(mContext, MovieDetails.class);
                    intent.putExtra("ID", mData.get(position).getID());
                    mContext.startActivity(intent);
                } else if(mData.get(position).getContent_Type() == 2) {
                    Intent intent = new Intent(mContext, WebSeriesDetails.class);
                    intent.putExtra("ID", mData.get(position).getID());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Title;
        TextView Year,movie_placeholders;
        ImageView Thumbnail;

        View Premium_Tag;

        CardView Movie_Item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            movie_placeholders   = itemView.findViewById(R.id.movie_placeholders);
            Title = (TextView) itemView.findViewById(R.id.Movie_list_Title);
            Year = (TextView) itemView.findViewById(R.id.Movie_list_Year);
            Thumbnail = (ImageView) itemView.findViewById(R.id.Movie_Item_thumbnail);

            Premium_Tag = (View) itemView.findViewById(R.id.Premium_Tag);

            Movie_Item = itemView.findViewById(R.id.Movie_Item);
        }


        void setTitle(SearchList title_text) {
            Title.setText(title_text.getTitle());
        }

        void setYear(SearchList year_text) {
            Year.setText(year_text.getYear());
        }

        void setImage(SearchList Thumbnail_Image) {
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
                            movie_placeholders.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // This is called when the image is cleared or removed
                            movie_placeholders.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

}
