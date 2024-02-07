package com.primemedia.marvels.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.primemedia.marvels.MovieDetails;
import com.primemedia.marvels.R;
import com.primemedia.marvels.WebSeriesDetails;
import com.primemedia.marvels.list.ImageSliderItem;

import java.util.List;

public class ImageSliderAdepter extends RecyclerView.Adapter<ImageSliderAdepter.SliderViewHolder> {

    private List<ImageSliderItem> slider_items;
    private ViewPager2 viewPager2;
    boolean isfav = false;
    boolean favoriteadded;
    Context context;


    public ImageSliderAdepter(List<ImageSliderItem> slider_items, ViewPager2 viewPager2) {
        this.slider_items = slider_items;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.imageslideritem,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(slider_items.get(position));
        if (position == slider_items.size() - 2) {
            viewPager2.post(runnable);
        }
        holder.setyear(slider_items.get(position));
        holder.setImage(slider_items.get(position));
        //holder.setPoster(slider_items.get(position));
        Glide.with(context)
                .load(slider_items.get(position).getImage())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        // Handle the error case, e.g., show a placeholder or handle it in any way you want
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        // The image has been successfully loaded, now use Palette to extract colors
                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                        Palette.from(bitmap).generate(palette -> {
                            int defaultColor = ContextCompat.getColor(context, R.color.app_color);;
                            int dominantColor = palette != null ? palette.getDominantColor(defaultColor) : defaultColor;
                            int[] colors = {dominantColor, ContextCompat.getColor(context, R.color.app_color)};
                            float[] positions = {0.0f, 1.0f};

                            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
                            gradientDrawable.setColors(colors, positions);
                            holder.itemView.setBackground(gradientDrawable);
                        });


                        return false;
                    }
                })
                .into(holder.poster_view);


        holder.fav.setOnClickListener(view ->
        {
            isfav = true;
            isfav = favoriteadded;
        });

        if (position == slider_items.size() - 2) {
            viewPager2.post(runnable);
        }
        holder.play_btn_slider.setOnClickListener(v ->
        {
            if (slider_items.get(position).getContent_Type() == 0) {
                Intent intent = new Intent(context, MovieDetails.class);
                intent.putExtra("ID", slider_items.get(position).getContent_ID());
                Log.d("contentid", String.valueOf(slider_items.get(position).getContent_ID()));
                intent.putExtra("fav", favoriteadded);
                context.startActivity(intent);
            } else if (slider_items.get(position).getContent_Type() == 1) {
                Intent intent = new Intent(context, WebSeriesDetails.class);
                intent.putExtra("ID", slider_items.get(position).getContent_ID());
                context.startActivity(intent);
            } else if (slider_items.get(position).getContent_Type() == 2) {
                Intent intent = new Intent(context, WebView.class);
                intent.putExtra("URL", slider_items.get(position).getURL());
                context.startActivity(intent);

            } else if (slider_items.get(position).getContent_Type() == 3) {
                String URL = slider_items.get(position).getURL();
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
            }
        });
    }





    @Override
    public int getItemCount() {
        return Math.min(slider_items.size(), 15);
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        private ImageView logoview;
        private RoundedImageView poster_view;
        private MaterialButton play_btn_slider, fav;
        private TextView year, genres, languages;


        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            logoview = itemView.findViewById(R.id.logoview);
            poster_view = itemView.findViewById(R.id.banner_bg);
            play_btn_slider = itemView.findViewById(R.id.play_btn_slider);
            fav = itemView.findViewById(R.id.fav);
            genres = itemView.findViewById(R.id.genres);
        }

        void setImage(ImageSliderItem image_slider_item) {
            Glide.with(context)
                    .load(image_slider_item.getLogo_imgs())
                    .into(logoview);
        }

        public void setPoster(ImageSliderItem imageSliderItem) {
            Glide.with(context)
                    .load(imageSliderItem.getImage())
                    .into(poster_view);


        }

        public void setyear(ImageSliderItem imageSliderItem) {

            genres.setText(imageSliderItem.getGenre());

        }
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            slider_items.addAll(slider_items);
            notifyDataSetChanged();
        }
    };
}
