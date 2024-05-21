package com.example.sewakos.PemilikKos;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.example.sewakos.AndroidUtil;
import com.example.sewakos.Autentication.Register;
import com.example.sewakos.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class UploadKos extends AppCompatActivity {

    private ImageSlider imageSlider;
    private ImageView upload_image_kos, back_to_beranda;
    EditText upload_nama_kos, upload_deskripsi_kos, upload_catatan_alamat_kos, upload_ketersediaan_kamar
            ,upload_fasilitas_kos, upload_harga_kos;
    private RadioGroup upload_tipe_kos;
    private Button btn_upload_kos;
    private List<Uri> selectedUris = new ArrayList<>();
    private List<SlideModel> slideModels = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private ProgressDialog progressDialog;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("List Kos");
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // Clear previous selections
                            selectedUris.clear();
                            slideModels.clear();
                            imageUrls.clear();

                            if (data.getClipData() != null) {
                                // Handle multiple selected items
                                int count = data.getClipData().getItemCount();
                                for (int i = 0; i < count; i++) {
                                    Uri uri = data.getClipData().getItemAt(i).getUri();
                                    selectedUris.add(uri);
                                    slideModels.add(new SlideModel(uri.toString(), ScaleTypes.FIT));
                                }
                            } else if (data.getData() != null) {
                                // Handle single selected item
                                Uri uri = data.getData();
                                selectedUris.add(uri);
                                slideModels.add(new SlideModel(uri.toString(), ScaleTypes.FIT));
                            }

                            // Set images to the slider
                            imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                        } else {
                            Toast.makeText(UploadKos.this, "No item selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_kos);

        upload_image_kos = findViewById(R.id.upload_image_kos);
        imageSlider = findViewById(R.id.image_slider);
        btn_upload_kos = findViewById(R.id.btn_upload_kos);
        upload_nama_kos = findViewById(R.id.upload_nama_kos);
        upload_deskripsi_kos = findViewById(R.id.upload_deskripsi_kos);
        upload_catatan_alamat_kos = findViewById(R.id.upload_catatan_alamat_kos);
        upload_ketersediaan_kamar = findViewById(R.id.upload_ketersediaan_kamar);
        upload_fasilitas_kos = findViewById(R.id.upload_fasilitas_kos);
        upload_harga_kos = findViewById(R.id.upload_harga_kos);
        upload_tipe_kos = findViewById(R.id.upload_tipe_kos);

        back_to_beranda = findViewById(R.id.back_to_beranda);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);

        upload_image_kos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_OPEN_DOCUMENT);
                photoPicker.setType("*/*");
                photoPicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                String[] mimeTypes = {"image/*"};
                photoPicker.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                activityResultLauncher.launch(photoPicker);
            }
        });

        btn_upload_kos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate input fields
                if (validateInputs()) {
                    progressDialog.show();
                    if (!selectedUris.isEmpty()) {
                        for (Uri uri : selectedUris) {
                            uploadToFirebase();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(UploadKos.this, "Please Select image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        back_to_beranda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BerandaPemilikKos.class));
            }
        });

    }

    private boolean validateInputs() {
        String namaKos = upload_nama_kos.getText().toString().trim();
        String deskripsiKos = upload_deskripsi_kos.getText().toString().trim();
        String catatanAlamatKos = upload_catatan_alamat_kos.getText().toString().trim();
        String fasilitasKos = upload_fasilitas_kos.getText().toString().trim();
        String ketersediaanKamar = upload_ketersediaan_kamar.getText().toString().trim();
        String hargaKos = upload_harga_kos.getText().toString().trim();
        int selectedRadioButtonId = upload_tipe_kos.getCheckedRadioButtonId();

        if (namaKos.isEmpty()) {
            upload_nama_kos.setError("Nama kos tidak boleh kosong");
            return false;
        }

        if (deskripsiKos.isEmpty()) {
            upload_deskripsi_kos.setError("Deskripsi kos tidak boleh kosong");
            return false;
        }

        if (catatanAlamatKos.isEmpty()) {
            upload_catatan_alamat_kos.setError("Alamat kos tidak boleh kosong");
            return false;
        }

        if (fasilitasKos.isEmpty()) {
            upload_fasilitas_kos.setError("Fasilitas kos tidak boleh kosong");
            return false;
        }

        if (ketersediaanKamar.isEmpty()) {
            upload_ketersediaan_kamar.setError("Ketersediaan kamar tidak boleh kosong");
            return false;
        }

        if (hargaKos.isEmpty()) {
            upload_harga_kos.setError("Harga kos tidak boleh kosong");
            return false;
        }

        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Pilih tipe kos", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void uploadToFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = currentUser.getUid();

        String namaKos = upload_nama_kos.getText().toString();
        String deskripsiKos = upload_deskripsi_kos.getText().toString();
        String catatanAlamatKos = upload_catatan_alamat_kos.getText().toString();
        String fasilitasKos = upload_fasilitas_kos.getText().toString();
        String ketersediaanKamar = upload_ketersediaan_kamar.getText().toString();
        String hargaKos = upload_harga_kos.getText().toString();
        Integer ketersediaanKamarInteger = Integer.parseInt(ketersediaanKamar);
        int selectedRadioButtonId = upload_tipe_kos.getCheckedRadioButtonId();

        String tipeKos;
        if (selectedRadioButtonId == R.id.putra) {
            tipeKos = "putra";
        } else if (selectedRadioButtonId == R.id.putri) {
            tipeKos = "putri";
        } else if (selectedRadioButtonId == R.id.campur) {
            tipeKos = "campur";
        } else {
            tipeKos = "tidak_ditentukan";
        }

        String folderName = "list kos";
        final StorageReference folderReference = storageReference.child(folderName);
        final String key = databaseReference.child(userID).push().getKey();

        // Ambil informasi pengguna
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                    // Upload images to Firebase Storage
                    for (Uri uri : selectedUris) {
                        final StorageReference imageReference = folderReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
                        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageUrls.add(uri.toString());
                                        if (imageUrls.size() == selectedUris.size()) {
                                            // Buat objek AndroidUtil dengan informasi tambahan
                                            AndroidUtil androidUtil = new AndroidUtil(
                                                    key,
                                                    userID,
                                                    uri.toString(),
                                                    namaKos,
                                                    deskripsiKos,
                                                    catatanAlamatKos,
                                                    fasilitasKos,
                                                    tipeKos,
                                                    hargaKos,
                                                    username,
                                                    profileImageUrl,
                                                    ketersediaanKamarInteger,
                                                    imageUrls
                                            );
                                            databaseReference.child(userID).child(key).setValue(androidUtil);

                                            // Hide ProgressDialog when upload is complete
                                            progressDialog.dismiss();

                                            Toast.makeText(UploadKos.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(UploadKos.this, BerandaPemilikKos.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(UploadKos.this, "Failed to get URL", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UploadKos.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(UploadKos.this, "Failed to get user info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(UploadKos.this, "Failed to get user info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}