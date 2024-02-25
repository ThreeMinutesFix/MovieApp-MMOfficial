package com.primemedia.marvels.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.primemedia.marvels.Constants;
import com.primemedia.marvels.R;
import com.primemedia.marvels.list.EpisodeList;
import com.primemedia.marvels.player.WebPlayer;

import java.util.List;

public class EpisodeListAdepter_player extends RecyclerView.Adapter<EpisodeListAdepter_player.MyViewHolder> {

    private final Context mContext;
    private View rootView;
    private String rootUrl;
    private String apiKey;
    private final List<EpisodeList> mData;
    private final int contentID;
    private Fragment mParentFragment;
    Context context;
    private int currentlyPlayingPosition = -1;

    public EpisodeListAdepter_player(int contentID, Context mContext, View mView, String rootUrl, String apiKey, List<EpisodeList> mData) {
        this.contentID = contentID;
        this.mContext = mContext;
        this.rootView = mView;
        this.rootUrl = rootUrl;
        this.apiKey = apiKey;
        this.mData = mData;

    }

    public void setCurrentlyPlayingPosition(int position) {
        int previousPlayingPosition = currentlyPlayingPosition;
        currentlyPlayingPosition = position;
        if (previousPlayingPosition != -1) {
            notifyItemChanged(previousPlayingPosition);
        }
        if (currentlyPlayingPosition != -1) {
            notifyItemChanged(currentlyPlayingPosition);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.episode_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setEpisode_image(mData.get(position));
        holder.setTitle(mData.get(position));
        holder.setDescription(mData.get(position));
        holder.IsDownloadable(mData.get(position));

        if (position == currentlyPlayingPosition) {
            holder.eAnimationView.setVisibility(View.VISIBLE);
        } else {
            holder.eAnimationView.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(view -> {
            if (Constants.all_series_type == 0) {
                if (mData.get(position).getType() == 1) {

                    if (mData.get(position).isPlay_Premium()) {
                        if (mData.get(position).getSource().equals("Embed")) {

                        } else {
                            Intent intent = new Intent(mContext, WebPlayer.class);
                            intent.putExtra("contentID", contentID);
                            intent.putExtra("SourceID", mData.get(position).getId());
                            intent.putExtra("name", mData.get(position).getEpisoade_Name());
                            intent.putExtra("source", mData.get(position).getSource());
                            intent.putExtra("url", mData.get(position).getUrl());

                            intent.putExtra("DrmUuid", mData.get(position).getDrmUuid());
                            intent.putExtra("DrmLicenseUri", mData.get(position).getDrmLicenseUri());

                            intent.putExtra("skip_available", mData.get(position).getSkip_available());
                            intent.putExtra("intro_start", mData.get(position).getIntro_start());
                            intent.putExtra("intro_end", mData.get(position).getIntro_end());

                            intent.putExtra("Content_Type", "WebSeries");
                            intent.putExtra("Current_List_Position", position);

                            int r_pos = position + 1;
                            if (r_pos < mData.size()) {
                                intent.putExtra("Next_Ep_Avilable", "Yes");
                            } else {
                                intent.putExtra("Next_Ep_Avilable", "No");
                            }

                            //mContext.startActivity(intent);
                            mParentFragment.startActivityForResult(intent, 1);
                        }
                    }

                } else {
                    if (mData.get(position).getSource().equals("Embed")) {

                    } else {
                        Intent intent = new Intent(mContext, WebPlayer.class);
                        intent.putExtra("contentID", contentID);
                        intent.putExtra("SourceID", mData.get(position).getId());
                        intent.putExtra("name", mData.get(position).getEpisoade_Name());
                        intent.putExtra("source", mData.get(position).getSource());
                        intent.putExtra("url", mData.get(position).getUrl());

                        intent.putExtra("DrmUuid", mData.get(position).getDrmUuid());
                        intent.putExtra("DrmLicenseUri", mData.get(position).getDrmLicenseUri());

                        intent.putExtra("skip_available", mData.get(position).getSkip_available());
                        intent.putExtra("intro_start", mData.get(position).getIntro_start());
                        intent.putExtra("intro_end", mData.get(position).getIntro_end());

                        intent.putExtra("Content_Type", "WebSeries");
                        intent.putExtra("Current_List_Position", position);

                        int r_pos = position + 1;
                        if (r_pos < mData.size()) {
                            intent.putExtra("Next_Ep_Avilable", "Yes");
                        } else {
                            intent.putExtra("Next_Ep_Avilable", "No");
                        }

                        //mContext.startActivity(intent);
                        ((WebPlayer) mContext).startActivityForResult(intent, 1);
                        ((WebPlayer) mContext).finish();
                    }
                }
            } else if (Constants.all_series_type == 1) {
                if (mData.get(position).getSource().equals("Embed")) {

                } else {
                    Intent intent = new Intent(mContext, WebPlayer.class);
                    intent.putExtra("contentID", contentID);
                    intent.putExtra("SourceID", mData.get(position).getId());
                    intent.putExtra("name", mData.get(position).getEpisoade_Name());
                    intent.putExtra("source", mData.get(position).getSource());
                    intent.putExtra("url", mData.get(position).getUrl());

                    intent.putExtra("DrmUuid", mData.get(position).getDrmUuid());
                    intent.putExtra("DrmLicenseUri", mData.get(position).getDrmLicenseUri());

                    intent.putExtra("skip_available", mData.get(position).getSkip_available());
                    intent.putExtra("intro_start", mData.get(position).getIntro_start());
                    intent.putExtra("intro_end", mData.get(position).getIntro_end());

                    intent.putExtra("Content_Type", "WebSeries");
                    intent.putExtra("Current_List_Position", position);

                    int r_pos = position + 1;
                    if (r_pos < mData.size()) {
                        intent.putExtra("Next_Ep_Avilable", "Yes");
                    } else {
                        intent.putExtra("Next_Ep_Avilable", "No");
                    }

                    //mContext.startActivity(intent);
                    ((WebPlayer) mContext).startActivityForResult(intent, 1);
                    ((WebPlayer) mContext).finish();
                }
            } else if (Constants.all_series_type == 2) {
                if (mData.get(position).isPlay_Premium()) {
                    if (mData.get(position).getSource().equals("Embed")) {

                    } else {
                        Intent intent = new Intent(mContext, WebPlayer.class);
                        intent.putExtra("contentID", contentID);
                        intent.putExtra("SourceID", mData.get(position).getId());
                        intent.putExtra("name", mData.get(position).getEpisoade_Name());
                        intent.putExtra("source", mData.get(position).getSource());
                        intent.putExtra("url", mData.get(position).getUrl());

                        intent.putExtra("DrmUuid", mData.get(position).getDrmUuid());
                        intent.putExtra("DrmLicenseUri", mData.get(position).getDrmLicenseUri());

                        intent.putExtra("skip_available", mData.get(position).getSkip_available());
                        intent.putExtra("intro_start", mData.get(position).getIntro_start());
                        intent.putExtra("intro_end", mData.get(position).getIntro_end());

                        intent.putExtra("Content_Type", "WebSeries");
                        intent.putExtra("Current_List_Position", position);

                        int r_pos = position + 1;
                        if (r_pos < mData.size()) {
                            intent.putExtra("Next_Ep_Avilable", "Yes");
                        } else {
                            intent.putExtra("Next_Ep_Avilable", "No");
                        }

                        //mContext.startActivity(intent);
                        ((WebPlayer) mContext).startActivityForResult(intent, 1);
                        ((WebPlayer) mContext).finish();
                    }
                } else {
                    Toast.makeText(mContext, "No playable Source", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView Episode_image;
        TextView Title;
        TextView Description;
        ImageView Download_btn_image;
        LottieAnimationView eAnimationView;
        CardView episode_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Episode_image = (ImageView) itemView.findViewById(R.id.Episode_image);
            Title = (TextView) itemView.findViewById(R.id.Title);
            Description = (TextView) itemView.findViewById(R.id.Description);
            Download_btn_image = (ImageView) itemView.findViewById(R.id.Download_btn_image);
            eAnimationView = itemView.findViewById(R.id.eAnimationView);
            episode_item = itemView.findViewById(R.id.episode_item);
        }

        void setEpisode_image(EpisodeList image) {
            Glide.with(context)
                    .load(image.getEpisoade_image())

                    .into(Episode_image);
        }

        void setTitle(EpisodeList title_text) {
            Title.setText(title_text.getEpisoade_Name());
        }

        void setDescription(EpisodeList description_text) {
            Description.setText(description_text.getEpisoade_description());
        }

        void IsDownloadable(EpisodeList type) {
            if (type.getDownloadable() == 1) {
                Download_btn_image.setVisibility(View.VISIBLE);
            } else {
                Download_btn_image.setVisibility(View.GONE);
            }
        }


    }
}
