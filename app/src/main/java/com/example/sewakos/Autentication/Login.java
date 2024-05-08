package com.example.sewakos.Autentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sewakos.PemilikKos.BottomNavbarPemilikKos;
import com.example.sewakos.PencariKos.BottomNavbarPencariKos;
import com.example.sewakos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private TextView daftar;

    private TextInputEditText etEmail, etPassword;
    private RelativeLayout button_masuk;

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        button_masuk = findViewById(R.id.button_masuk);
        daftar = findViewById(R.id.daftar);

        auth = FirebaseAuth.getInstance();

        button_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (auth.getCurrentUser().isEmailVerified()) {
                                final String userId = auth.getCurrentUser().getUid();
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                                String role = userSnapshot.child("role").getValue(String.class);
                                                if (role != null && userSnapshot.getKey().equals(userId)) {
                                                    if (role.equals("Pemilik Kos")) {
                                                        startActivity(new Intent(getApplicationContext(), BottomNavbarPemilikKos.class));
                                                        return;
                                                    } else if (role.equals("Pencari kos")) {
                                                        startActivity(new Intent(getApplicationContext(), BottomNavbarPencariKos.class));
                                                        return;
                                                    }
                                                }
                                            }
                                            Toast.makeText(getApplicationContext(), "Role tidak ditemukan", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getApplicationContext(), "Gagal membaca data pengguna", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Toast.makeText(getApplicationContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();

                            } else {
                                etEmail.setError("Email ini Belum Terverifikasi");
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Login Gagal", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}