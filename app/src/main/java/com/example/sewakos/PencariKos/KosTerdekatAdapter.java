package com.example.sewakos.PencariKos;

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
import com.example.sewakos.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KosTerdekatAdapter extends RecyclerView.Adapter<KosTerdekatAdapter.MyViewHolder> {

    private ArrayList<AndroidUtil> dataList;
    private Context context;

    public KosTerdekatAdapter(ArrayList<AndroidUtil> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public KosTerdekatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_kos_horizontal, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KosTerdekatAdapter.MyViewHolder holder, int position) {
        AndroidUtil androidUtil = dataList.get(position);
        List<String> imageUrls = androidUtil.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            String firstImageUrl = imageUrls.get(0);
            Glide.with(context).load(firstImageUrl).into(holder.recyclerImage);
        }
        holder.recyclerNamaKos.setText(androidUtil.getNamaKos());
        holder.recyclerAlamatKos.setText(androidUtil.getCatatanAlamatKos());
        holder.recyclerJarakKos.setText(String.format(Locale.getDefault(), "%.2f km", androidUtil.getDistance()));

        holder.itemView.setOnClickListener(v -> {
            String kosId = androidUtil.getId();
            Log.d("KosTerdekatAdapter", "Kos ID: " + kosId);
            Intent intent = new Intent(context, DetailKos.class);
            intent.putExtra("kosId", kosId);
            intent.putExtra("userId", androidUtil.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView recyclerImage;
        TextView recyclerNamaKos;
        TextView recyclerAlamatKos;
        TextView recyclerJarakKos;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.imageKos);
            recyclerNamaKos = itemView.findViewById(R.id.namaKos);
            recyclerAlamatKos = itemView.findViewById(R.id.alamatKos);
            recyclerJarakKos = itemView.findViewById(R.id.jarakKos);
        }
    }

}
