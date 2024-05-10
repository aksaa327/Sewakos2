package com.example.sewakos.Profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sewakos.AndroidUtil;
import com.example.sewakos.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class Profile extends Fragment {

    private TextView txt_username, txt_email;
    private ImageView imageProfile;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(),selectedImageUri,imageProfile);
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(firebaseUser.getUid());

                            storageRef.putFile(selectedImageUri)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
                                            userRef.child("profileImageUrl").setValue(uri.toString());
                                        }).addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show();
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show();
                                    });

                        }
                    }
                }
        );
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txt_username = view.findViewById(R.id.username_profile);
        txt_email = view.findViewById(R.id.email_profile);
        imageProfile = view.findViewById(R.id.image_profile);

        auth = FirebaseAuth.getInstance();

        firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            showUserProfile(firebaseUser);
        }

        imageProfile.setOnClickListener((v) -> {
            ImagePicker.with(this)
                    .cropSquare()
                    .compress(512)
                    .maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        return view;
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String name = snapshot.child("username").getValue(String.class);
                    if (name != null) {
                        String email = firebaseUser.getEmail();

                        txt_username.setText(name);
                        txt_email.setText(email);

                        String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                        if (profileImageUrl != null) {
                            Glide.with(requireContext())
                                    .load(profileImageUrl)
                                    .apply(new RequestOptions().circleCrop()) // Menggunakan Glide untuk memuat gambar sebagai lingkaran
                                    .into(imageProfile);
                        }
                    } else {
                        Toast.makeText(getContext(), "Nama pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
