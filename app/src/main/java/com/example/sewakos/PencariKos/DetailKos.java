package com.example.sewakos.PencariKos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sewakos.AndroidUtil;
import com.example.sewakos.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailKos extends AppCompatActivity {

    private ImageView detailImage;
    private TextView detailNamaKos, detailCatatanAlamatKos, detailDeskripsiKos, detailFasilitasKos, detailHargaKos, detailKetersediaanKamarKos, detailTipeKos;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kos);

        detailImage = findViewById(R.id.image_detail_kos);
        detailNamaKos = findViewById(R.id.nama_kos_detail_kos);
        detailCatatanAlamatKos = findViewById(R.id.catatanAlamatKos);
        detailDeskripsiKos = findViewById(R.id.deskripsiKos);
        detailFasilitasKos = findViewById(R.id.fasilitasKos);
        detailHargaKos = findViewById(R.id.hargaKos);
        detailKetersediaanKamarKos = findViewById(R.id.ketersediaanKamarKos);
        detailTipeKos = findViewById(R.id.tipeKos);

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
                        Log.d("DetailKosActivity", "Kos Details: " + kos.getNamaKos()); // Tambahkan log ini
                        Glide.with(DetailKos.this).load(kos.getImageURL()).into(detailImage);
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