package com.example.sewakos.Searching;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sewakos.AndroidUtil;
import com.example.sewakos.PencariKos.DetailKos;
import com.example.sewakos.R;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.KosViewHolder> {

    private List<AndroidUtil> kosList;
    private List<AndroidUtil> kosListFull;
    private Context context;

    public SearchAdapter(Context context,List<AndroidUtil> kosList) {
        this.context = context;
        this.kosList = new ArrayList<>(kosList);
        this.kosListFull = new ArrayList<>(kosList);
    }

    @NonNull
    @Override
    public SearchAdapter.KosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kos_item_search, parent, false);
        return new KosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull KosViewHolder holder, int position) {
        AndroidUtil currentKos = kosList.get(position);
        holder.namaKos.setText(currentKos.getNamaKos());

        if (currentKos.getImageUrls() != null && !currentKos.getImageUrls().isEmpty()) {
            Glide.with(context)
                    .load(currentKos.getImageUrls().get(0))
                    .into(holder.imageKos);
        } else {
            // Set a placeholder or default image
            holder.imageKos.setImageResource(R.drawable.ic_bed);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailKos.class);
            intent.putExtra("kosId", currentKos.getId());
            intent.putExtra("userId", currentKos.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return kosList.size();
    }

    public class KosViewHolder extends RecyclerView.ViewHolder {
        public TextView namaKos;
        public ImageView imageKos;

        public KosViewHolder(View view) {
            super(view);
            namaKos = view.findViewById(R.id.recyclerNamaKos);
            imageKos = view.findViewById(R.id.recyclerImage);
        }
    }

    public void setKosListFull(List<AndroidUtil> kosListFull) {
        this.kosListFull = new ArrayList<>(kosListFull);
    }

    public void filter(String text) {
        kosList.clear();
        if (text.isEmpty()) {
            kosList.addAll(kosListFull);
            notifyDataSetChanged();
            Log.d("SearchAdapter", "Filter empty, total items: " + kosList.size());
            return;
        }

        text = text.toLowerCase();
        for (AndroidUtil kos : kosListFull) {
            if (kos.getNamaKos().toLowerCase().contains(text)) {
                kosList.add(kos);
            }
        }
        notifyDataSetChanged();
        Log.d("SearchAdapter", "Filter applied, total items: " + kosList.size());
    }
}
