package com.example.sewakos.PencariKos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.sewakos.AndroidUtil;
import com.example.sewakos.PemilikKos.BerandaPemilikKos;
import com.example.sewakos.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailKos extends AppCompatActivity {

    private ImageSlider imageSlider;
    private ImageView btn_back_to_beranda;
    private TextView detailNamaKos, detailCatatanAlamatKos, detailDeskripsiKos, detailFasilitasKos, detailHargaKos, detailKetersediaanKamarKos, detailTipeKos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kos);

        imageSlider = findViewById(R.id.image_slider);
        detailNamaKos = findViewById(R.id.nama_kos_detail_kos);
        detailCatatanAlamatKos = findViewById(R.id.catatanAlamatKos);
        detailDeskripsiKos = findViewById(R.id.deskripsiKos);
        detailFasilitasKos = findViewById(R.id.fasilitasKos);
        detailHargaKos = findViewById(R.id.hargaKos);
        detailKetersediaanKamarKos = findViewById(R.id.ketersediaanKamarKos);
        detailTipeKos = findViewById(R.id.tipeKos);

        btn_back_to_beranda = findViewById(R.id.btn_back_to_beranda);

        btn_back_to_beranda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BerandaPencariKos.class));
            }
        });

        //imageSlider.setImageList(imageList);

        String kosId = getIntent().getStringExtra("kosId");
        String userId = getIntent().getStringExtra("userId");
        Log.d("DetailKosActivity", "Received Kos ID: " + kosId + ", User ID: " + userId);
        if (kosId != null && userId != null) {
            loadKosDetails(userId, kosId);
        } else {
            Log.d("DetailKosActivity", "Kos ID is null");
        }
    }

    private void loadKosDetails(String userId, String kosId) {
        Log.d("DetailKosActivity", "Loading Kos Details");
        DatabaseReference kosRef = FirebaseDatabase.getInstance().getReference("List Kos").child(userId).child(kosId);
        kosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    AndroidUtil kos = snapshot.getValue(AndroidUtil.class);
                    if (kos != null) {
                        Log.d("DetailKosActivity", "Kos Details: " + kos.getNamaKos());

                        List<SlideModel> slideModels = new ArrayList<>();
                        List<String> imageUrls = kos.getImageUrls(); // Mengambil daftar URL gambar
                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            for (String imageUrl : imageUrls) {
                                slideModels.add(new SlideModel(imageUrl, ScaleTypes.CENTER_CROP));
                            }
                            imageSlider.setImageList(slideModels);
                        } else {
                            Log.d("DetailKosActivity", "Image URLs are null or empty");
                        }

                        detailNamaKos.setText(kos.getNamaKos());
                        detailCatatanAlamatKos.setText(kos.getCatatanAlamatKos());
                        detailDeskripsiKos.setText(kos.getDeskripsiKos());
                        detailFasilitasKos.setText(kos.getFasilitasKos());
                        detailHargaKos.setText(String.valueOf(kos.getHargaKos()));
                        detailKetersediaanKamarKos.setText(String.valueOf(kos.getKetersediaanKamarKos()));
                        detailTipeKos.setText(kos.getTipeKos());
                    } else {
                        Log.d("DetailKosActivity", "Kos is null");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DetailKosActivity", "Failed to load data: " + error.getMessage());
                Toast.makeText(DetailKos.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}