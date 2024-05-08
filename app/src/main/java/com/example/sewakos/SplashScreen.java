package com.example.sewakos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.sewakos.Autentication.Login;
import com.example.sewakos.PemilikKos.BottomNavbarPemilikKos;
import com.example.sewakos.PencariKos.BottomNavbarPencariKos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        auth = FirebaseAuth.getInstance();

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser != null) {
                    if (currentUser.isEmailVerified()) {
                        final String userId = auth.getCurrentUser().getUid();
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    DataSnapshot userSnapshot = snapshot.child(userId); // Ambil child dengan kunci UID pengguna saat ini
                                    if (userSnapshot.exists()) { // Pastikan child dengan UID pengguna saat ini ada
                                        String role = userSnapshot.child("role").getValue(String.class);
                                        if (role != null) {
                                            if (role.equals("Pemilik Kos")) {
                                                startActivity(new Intent(getApplicationContext(), BottomNavbarPemilikKos.class));
                                                finish(); // Tutup activity splash screen agar tidak kembali saat tombol kembali ditekan
                                                return;
                                            } else if (role.equals("Pencari kos")) {
                                                startActivity(new Intent(getApplicationContext(), BottomNavbarPencariKos.class));
                                                finish(); // Tutup activity splash screen agar tidak kembali saat tombol kembali ditekan
                                                return;
                                            }
                                        }
                                        Toast.makeText(getApplicationContext(), "Peran tidak valid", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "Gagal membaca data pengguna", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            }
        }, 3000L);
    }
}