package com.primemedia.marvels.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.primemedia.marvels.R;

import java.util.List;

public class CastAdapterCustom extends RecyclerView.Adapter<CastAdapterCustom.ViewHolder> {

    private Context context;
    private List<String> castNames;

    public CastAdapterCustom(Context context, List<String> castNames) {
        this.context = context;
        this.castNames = castNames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cast_item_new, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String castName = castNames.get(position);
        holder.nameTextView.setText(castName);
    }

    @Override
    public int getItemCount() {
        return castNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.names_types);
        }
    }
}

