package com.example.sewakos.PencariKos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.sewakos.AndroidUtil;
import com.example.sewakos.PemilikKos.BerandaPemilikKos;
import com.example.sewakos.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailKos extends AppCompatActivity {

    private ImageSlider imageSlider;
    private ImageView profileImageView;
    private TextView username, detailNamaKos, detailCatatanAlamatKos, detailDeskripsiKos, detailFasilitasKos, detailHargaKos, detailKetersediaanKamarKos, detailTipeKos;

    private Button btn_hubungi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kos);

        profileImageView = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        imageSlider = findViewById(R.id.image_slider);
        detailNamaKos = findViewById(R.id.nama_kos_detail_kos);
        detailCatatanAlamatKos = findViewById(R.id.catatanAlamatKos);
        detailDeskripsiKos = findViewById(R.id.deskripsiKos);
        detailFasilitasKos = findViewById(R.id.fasilitasKos);
        detailHargaKos = findViewById(R.id.hargaKos);
        detailKetersediaanKamarKos = findViewById(R.id.ketersediaanKamarKos);
        detailTipeKos = findViewById(R.id.tipeKos);
        btn_hubungi = findViewById(R.id.btn_hubungi);

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
                        username.setText(kos.getUsername());

                        String profileImageUrl = kos.getProfileImageUrl();
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(DetailKos.this)
                                    .load(profileImageUrl)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(profileImageView);
                        }

                        initializeMap(kos.getLatitude(), kos.getLongitude(), kos.getNamaKos());

                        String ownerPhoneNumber = kos.getOwnerPhoneNumber(); // Ambil nomor telepon pemilik
                        btn_hubungi.setVisibility(View.VISIBLE); // Tampilkan tombol
                        btn_hubungi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ownerPhoneNumber != null && !ownerPhoneNumber.isEmpty()) {
                                    openWhatsApp(ownerPhoneNumber);
                                } else {
                                    Toast.makeText(DetailKos.this, "Nomor telepon pemilik tidak tersedia", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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

    private void openWhatsApp(String ownerPhoneNumber) {
        if (ownerPhoneNumber.startsWith("0")) {
            ownerPhoneNumber = "+62" + ownerPhoneNumber.substring(1);
        }

        String initialMessage = "Halo, saya tertarik dengan kos Anda. Bisakah saya mendapatkan informasi lebih lanjut?";
        String encodedMessage = Uri.encode(initialMessage);
        String whatsappUrl = "https://wa.me/" + ownerPhoneNumber + "?text=" + encodedMessage;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(whatsappUrl));
        startActivity(intent);
    }

    private void initializeMap(double latitude, double longitude, String namaKos) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detail_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    LatLng location = new LatLng(latitude, longitude);
                    googleMap.addMarker(new MarkerOptions().position(location).title(namaKos));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                }
            });
        }
    }
}