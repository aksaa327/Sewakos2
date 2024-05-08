package com.example.sewakos.PemilikKos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.sewakos.Communication.Pesan;
import com.example.sewakos.Profile.Profile;
import com.example.sewakos.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavbarPemilikKos extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private BerandaPemilikKos berandaPemilikKos = new BerandaPemilikKos();
    private Pesan pesan = new Pesan();
    private Profile profile = new Profile();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navbar_pemilik_kos);

        bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navBeranda);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.navBeranda) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, berandaPemilikKos).commit();
            return true;
        } else if (menuItem.getItemId() == R.id.navPesan) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, pesan).commit();
            return true;
        } else if (menuItem.getItemId() == R.id.navProfile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, profile).commit();
            return true;
        }

        return false;
    }
}