package com.primemedia.marvels.adapter;

import android.annotation.SuppressLint;
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
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.primemedia.marvels.R;
import com.primemedia.marvels.fragments.tabitems.Home;
import com.primemedia.marvels.list.ContinuePlayingList;
import com.primemedia.marvels.player.MoviePlayer;
import com.primemedia.marvels.player.WebPlayer;
import com.primemedia.marvels.resume_content.ResumeContentDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContinuePlayingListAdepter extends RecyclerView.Adapter<ContinuePlayingListAdepter.MyViewHolder> {
    private Context context;
    private List<ContinuePlayingList> mData;

    public ContinuePlayingListAdepter(Context context, List<ContinuePlayingList> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.continue_playing_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setTitle(mData.get(position));
        holder.setYear(mData.get(position));
        holder.setImage(mData.get(position));


        holder.Movie_Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mData.get(position).getContent_type().equals("Movie")) {
                    Intent intent = new Intent(context, MoviePlayer.class);
                    intent.putExtra("contentID", mData.get(position).getContentID());
                    intent.putExtra("SourceID", 0);
                    intent.putExtra("Content_Type", mData.get(position).getContent_type());
                    intent.putExtra("name", mData.get(position).getName());
                    intent.putExtra("source", mData.get(position).getSourceType());
                    intent.putExtra("url", mData.get(position).getSourceUrl());

                    intent.putExtra("position", mData.get(position).getPosition());

                    intent.putExtra("skip_available", 0);
                    intent.putExtra("intro_start", 0);
                    intent.putExtra("intro_end", 0);

                    context.startActivity(intent);
                } else if (mData.get(position).getContent_type().equals("WebSeries")) {
                    Intent intent = new Intent(context, WebPlayer.class);
                    intent.putExtra("contentID", mData.get(position).getContentID());
                    intent.putExtra("SourceID", 0);
                    intent.putExtra("name", mData.get(position).getName());
                    intent.putExtra("source", mData.get(position).getSourceType());
                    intent.putExtra("url", mData.get(position).getSourceUrl());

                    intent.putExtra("position", mData.get(position).getPosition());

                    intent.putExtra("skip_available", 0);
                    intent.putExtra("intro_start", 0);
                    intent.putExtra("intro_end", 0);

                    intent.putExtra("Content_Type", "WebSeries");
                    intent.putExtra("Current_List_Position", position);

                    intent.putExtra("Next_Ep_Avilable", "No");

                    context.startActivity(intent);
                }

            }
        });

        holder.deleteItem.setOnClickListener(view -> {
            ResumeContentDatabase db = ResumeContentDatabase.getDbInstance(context);
            db.resumeContentDao().delete(mData.get(position).getId());
            mData.remove(mData.get(position));
            notifyDataSetChanged();

            if (db.resumeContentDao().getResumeContents().isEmpty()) {
                Home.resume_Layout.setVisibility(View.GONE);
            } else {
                Home.resume_Layout.setVisibility(View.VISIBLE);
            }
        });

        holder.contentProgress.setMax((int) mData.get(position).getDuration());
        holder.contentProgress.setProgress((int) mData.get(position).getPosition());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Title;
        TextView Year;
        ImageView poster;

        CardView Movie_Item;
        ImageView deleteItem;
        LinearProgressIndicator contentProgress;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            Title = (TextView) itemView.findViewById(R.id.Movie_list_Title);
            Year = (TextView) itemView.findViewById(R.id.Movie_list_Year);
            poster = (ImageView) itemView.findViewById(R.id.Movie_Item_thumbnail);

            Movie_Item = itemView.findViewById(R.id.Movie_Item);
            deleteItem = itemView.findViewById(R.id.deleteItem);
            contentProgress = itemView.findViewById(R.id.contentProgress);
        }

        void setTitle(ContinuePlayingList title_text) {
            Title.setText(title_text.getName());
        }

        void setYear(ContinuePlayingList year_text) {
            Year.setText(year_text.getYear());
        }

        void setImage(ContinuePlayingList Thumbnail_Image) {
            Glide.with(context)
                    .load(Thumbnail_Image.getPoster())
                    .placeholder(R.drawable.thumbnail_placeholder)
                    .into(poster);
        }


    }
}
