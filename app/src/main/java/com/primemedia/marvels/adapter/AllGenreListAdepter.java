package com.primemedia.marvels.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.primemedia.marvels.R;
import com.primemedia.marvels.fragments.CategoriesContents;
import com.primemedia.marvels.list.GenreList;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AllGenreListAdepter extends RecyclerView.Adapter<AllGenreListAdepter.MyViewHolder> {
    public static Context context;
    private List<GenreList> genreData;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public AllGenreListAdepter(Context context, List<GenreList> genreData) {
        this.context = context;
        this.genreData = genreData;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.setText(genreData.get(position));
        holder.genreItem.setOnClickListener(view -> {
            setSelectedPosition(position);
            Fragment fragment = new CategoriesContents();
            Bundle bundle = new Bundle();
            bundle.putInt("ID", genreData.get(position).getId());
            bundle.putString("Name", genreData.get(position).getName());
            bundle.putInt("SelectedPosition", position);
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contaner, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return 24;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout genreItem;

        TextView genreTextView;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            genreItem = itemView.findViewById(R.id.genreItem);
            genreTextView = itemView.findViewById(R.id.genreTextView);
        }

        public void updateTypeface(boolean isSelected) {
            genreTextView.setTypeface(null, isSelected ? Typeface.BOLD : Typeface.NORMAL);
            genreTextView.setTextColor(ContextCompat.getColor(context, isSelected ? R.color.white : R.color.Non_Selected));
        }

        void setText(GenreList text) {
            genreTextView.setText(text.getName());
        }


    }
}
