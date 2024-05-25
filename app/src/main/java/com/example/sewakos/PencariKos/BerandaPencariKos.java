package com.example.sewakos.PencariKos;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sewakos.AndroidUtil;
import com.example.sewakos.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class BerandaPencariKos extends Fragment {

    private RecyclerView recyclerView, recyclerViewKosTerdekat;
    private ArrayList<AndroidUtil> dataList, kosTerdekatList;
    private PencariKosAdapter adapter;
    private KosTerdekatAdapter kosTerdekatAdapter;
    private DatabaseReference databaseReference;
    private static final double MAX_DISTANCE_KM = 2.0;
    private TextView tvNoKosTerdekat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beranda_pencari_kos, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPencariKos);
        recyclerViewKosTerdekat = view.findViewById(R.id.recyclerViewKosTerdekat);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewKosTerdekat.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tvNoKosTerdekat = view.findViewById(R.id.tvNoKosTerdekat);

        dataList = new ArrayList<>();
        kosTerdekatList = new ArrayList<>();

        kosTerdekatAdapter = new KosTerdekatAdapter(kosTerdekatList, getContext());
        adapter = new PencariKosAdapter(dataList, getContext());

        recyclerViewKosTerdekat.setAdapter(kosTerdekatAdapter);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("List Kos");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                kosTerdekatList.clear();
                Location userLocation = getUserLocation();

                if (userLocation == null) {
                    Log.e("BerandaPencariKos", "User location is null");
                    return;
                }

                Log.d("BerandaPencariKos", "User location: " + userLocation.getLatitude() + ", " + userLocation.getLongitude());

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot kosSnapshot : userSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        AndroidUtil androidUtil = kosSnapshot.getValue(AndroidUtil.class);
                        if (androidUtil != null) {
                            androidUtil.setId(kosSnapshot.getKey());
                            androidUtil.setUserId(userId);

                            double lat = androidUtil.getLatitude();
                            double lon = androidUtil.getLongitude();

                            Log.d("BerandaPencariKos", "Kos: " + androidUtil.getNamaKos() + ", Lat: " + lat + ", Lon: " + lon);

                            if (lat != 0.0 && lon != 0.0) {
                                double distance = DistanceUtil.calculateDistance(userLocation.getLatitude(), userLocation.getLongitude(), lat, lon);
                                androidUtil.setDistance(distance);

                                Log.d("BerandaPencariKos", "Kos: " + androidUtil.getNamaKos() + ", Distance: " + distance);

                                // Tambahkan ke daftar kos terdekat hanya jika jaraknya kurang dari MAX_DISTANCE_KM
                                if (distance <= MAX_DISTANCE_KM) {
                                    kosTerdekatList.add(androidUtil);
                                }
                            }

                            dataList.add(androidUtil);
                        }
                    }
                }
                Collections.sort(kosTerdekatList, Comparator.comparingDouble(AndroidUtil::getDistance));

                if (kosTerdekatList.isEmpty()) {
                    tvNoKosTerdekat.setVisibility(View.VISIBLE);
                } else {
                    tvNoKosTerdekat.setVisibility(View.GONE);
                }

                kosTerdekatAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BerandaPencariKos", "Database error: " + error.getMessage());
            }
        });

        return view;
    }

    private Location getUserLocation() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return null;
        }
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
}