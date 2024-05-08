package com.example.sewakos.Autentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sewakos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    private TextView log_in;

    private TextInputEditText etUsername, etEmail, etPassword, etRepassword;
    private RelativeLayout button_daftar;
    private RadioGroup role_radio_group;
    private RadioButton role1, role2;

    private FirebaseAuth auth;
    private DatabaseReference database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        log_in = findViewById(R.id.log_in);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRepassword = findViewById(R.id.etRepassword);

        role_radio_group = findViewById(R.id.role_radio_group);
        role1 = findViewById(R.id.role1);
        role2 = findViewById(R.id.role2);

        button_daftar = findViewById(R.id.button_daftar);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://sewakos-be714-default-rtdb.firebaseio.com/");

        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        role1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    role2.setChecked(false);
                }
            }
        });

        role2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    role1.setChecked(false);
                }
            }
        });

        button_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String repassword = etRepassword.getText().toString();
                int selectedRadioButtonId = role_radio_group.getCheckedRadioButtonId();

                if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !repassword.isEmpty()) {

                    if (password.length() > 6) {
                        if (repassword.equals(password)) {
                            database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                            String existingUsername = userSnapshot.child("username").getValue(String.class);
                                            // bandingkan dengan username yang diinputkan
                                            if (existingUsername.equals(username)) {
                                                etUsername.setError("username sudah digunakan");
                                                return;
                                            }
                                        }
                                    }

                                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(Register.this, "Daftar Berhasil", Toast.LENGTH_SHORT).show();
                                                            FirebaseUser firebaseUser = auth.getCurrentUser();


                                                            database = FirebaseDatabase.getInstance().getReference("users");
                                                            database.child(firebaseUser.getUid()).child("email").setValue(email);
                                                            database.child(firebaseUser.getUid()).child("username").setValue(username);

                                                            Log.d("RoleAndRadioButton", "Selected RadioButton ID: " + selectedRadioButtonId);

                                                            final String[] role = {""};

                                                            if (selectedRadioButtonId == R.id.role1) {
                                                                role[0] = "Pemilik Kos";
                                                            } else if (selectedRadioButtonId == R.id.role2) {
                                                                role[0] = "Pencari kos";
                                                            }
                                                            Log.d("RoleAndRadioButton", "Role value: " + role[0]);


                                                            database.child(firebaseUser.getUid()).child("role").setValue(role[0]);

                                                            startActivity(new Intent(Register.this, Login.class));
                                                        } else {
                                                            Toast.makeText(Register.this, "Verifikasi gagal di kirim", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            } else {
                                                Toast.makeText(Register.this, "Daftar gagal", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            etRepassword.setError("Password tidak sama!!!");
                        }
                    } else {
                        etPassword.setError("Password Harus Lebih dari 6 karakter!!!");
                    }

                } else {
                    Toast.makeText(Register.this, "Ada data yang masih kosong!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}