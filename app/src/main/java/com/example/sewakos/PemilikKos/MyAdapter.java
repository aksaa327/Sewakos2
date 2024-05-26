package com.example.sewakos.PemilikKos;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sewakos.AndroidUtil;
import com.example.sewakos.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<AndroidUtil> dataList;
    private Context context;
    private DatabaseReference databaseReference;

    public MyAdapter(ArrayList<AndroidUtil> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("List Kos");
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

        holder.btnDeleteKos.setOnClickListener(v -> {
            String userId = androidUtil.getUserId();
            String kosId = androidUtil.getId();

            new AlertDialog.Builder(context)
                    .setTitle("Hapus Kos")
                    .setMessage("Apakah kamu yakin ingin menghapus kos ini?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        databaseReference.child(userId).child(kosId).removeValue()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Kos berhasil dihapus", Toast.LENGTH_SHORT).show();
                                        new Handler().postDelayed(() -> {
                                            if (position >= 0 && position < dataList.size()) {
                                                dataList.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, dataList.size());
                                            }
                                        }, 500);
                                    } else {
                                        Toast.makeText(context, "Gagal menghapus kos", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Delete Kos", "Gagal menghapus kos", e);
                                    Toast.makeText(context, "Gagal menghapus kos", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        holder.btnEditKos.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditKos.class);
            intent.putExtra("KOS_ID", androidUtil.getId());
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
        ImageView btnDeleteKos, btnEditKos;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.recyclerImage);
            recyclerNamaKos = itemView.findViewById(R.id.recyclerNamaKos);
            btnDeleteKos = itemView.findViewById(R.id.btnDeleteKos);
            btnEditKos = itemView.findViewById(R.id.btn_edit_kos);
        }
    }
}
