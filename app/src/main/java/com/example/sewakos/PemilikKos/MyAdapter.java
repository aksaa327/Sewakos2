package com.example.sewakos.PemilikKos;

import android.content.Context;
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

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<AndroidUtil> dataList;
    private Context context;

    public MyAdapter(ArrayList<AndroidUtil> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_pemilik_kos, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AndroidUtil androidUtil = dataList.get(position);
        List<String> imageUrls = androidUtil.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            String firstImageUrl = imageUrls.get(0);
            Log.d("ImageUrl", "URL: " + firstImageUrl); // Cetak URL gambar ke logcat
            Glide.with(context).load(firstImageUrl).into(holder.recyclerImage);
        }
        holder.recyclerNamaKos.setText(dataList.get(position).getNamaKos());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView recyclerImage;
        TextView recyclerNamaKos;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerImage = itemView.findViewById(R.id.recyclerImage);
            recyclerNamaKos = itemView.findViewById(R.id.recyclerNamaKos);
        }
    }
}
