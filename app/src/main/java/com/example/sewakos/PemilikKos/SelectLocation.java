package com.example.sewakos.PemilikKos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sewakos.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SelectLocation extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedLocation;
    private Button btnConfirmLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        btnConfirmLocation = findViewById(R.id.btn_confirm_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("MapError", "Error initializing map fragment");
        }

        btnConfirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLocation != null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selected_location", selectedLocation);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(SelectLocation.this, "No location selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Cek izin lokasi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Set listener untuk mendapatkan lokasi yang dipilih
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                selectedLocation = latLng;
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });

        Log.d("MapReady", "Map is ready and location enabled");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    Log.d("PermissionsResult", "Location permission granted and location enabled");
                }
            } else {
                Log.d("PermissionsResult", "Location permission denied");
            }
        }
    }
}
