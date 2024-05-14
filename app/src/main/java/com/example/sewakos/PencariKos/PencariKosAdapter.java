package com.example.sewakos.PencariKos;

import android.content.Context;
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

public class PencariKosAdapter extends RecyclerView.Adapter<PencariKosAdapter.MyViewHolder> {

    private ArrayList<AndroidUtil> dataList;
    private Context context;

    public PencariKosAdapter(ArrayList<AndroidUtil> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_pencari_kos, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AndroidUtil androidUtil = dataList.get(position);
        Glide.with(context).load(androidUtil.getImageURL()).into(holder.recyclerImage);
        holder.recyclerNamaKos.setText(androidUtil.getNamaKos());
        holder.recyclerKetersediaanKos.setText(String.valueOf(androidUtil.getKetersediaanKamarKos()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView recyclerImage;
        TextView recyclerNamaKos;
        TextView recyclerKetersediaanKos;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.recyclerImage);
            recyclerNamaKos = itemView.findViewById(R.id.recyclerNamaKos);
            recyclerKetersediaanKos = itemView.findViewById(R.id.recyclerKetersediaanKos);
        }

    }
}
