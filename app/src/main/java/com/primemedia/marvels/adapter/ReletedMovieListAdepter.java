package com.primemedia.marvels.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.primemedia.marvels.MovieDetails;
import com.primemedia.marvels.R;
import com.primemedia.marvels.list.MovieList;

import java.util.List;

public class ReletedMovieListAdepter extends RecyclerView.Adapter<ReletedMovieListAdepter.MyViewHolder> {

    private Context mContext;
    private List<MovieList> mData;

    Context context;

    public ReletedMovieListAdepter(Context mContext, List<MovieList> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item_v2, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setTitle(mData.get(position));
        holder.setYear(mData.get(position));
        holder.setImage(mData.get(position));
        holder.Movie_Item.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MovieDetails.class);
            intent.putExtra("ID", mData.get(position).getID());
            mContext.startActivity(intent);
            ((MovieDetails) mContext).finish();
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView Title;
        TextView Year;
        ImageView Thumbnail;

        CardView Movie_Item;

        CardView Show_All;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Title = (TextView) itemView.findViewById(R.id.Movie_list_Title);
            Year = (TextView) itemView.findViewById(R.id.Movie_list_Year);
            Thumbnail = (ImageView) itemView.findViewById(R.id.Movie_Item_thumbnail);


            Movie_Item = itemView.findViewById(R.id.Movie_Item);


        }


        void setTitle(MovieList title_text) {
            Title.setText(title_text.getTitle());
        }

        void setYear(MovieList year_text) {
            Year.setText(year_text.getYear());
        }

        void setImage(MovieList Thumbnail_Image) {
            Glide.with(context)
                    .load(Thumbnail_Image.getThumbnail())
                    .placeholder(R.drawable.thumbnail_placeholder)
                    .into(Thumbnail);
        }
    }
}
