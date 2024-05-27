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
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.sewakos.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EditKos extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_LOCATION = 1;
    private LatLng selectedLocation;

    private ImageSlider imageSlider;
    private ImageView upload_image_kos;
    EditText edit_nama_kos, edit_deskripsi_kos, edit_catatan_alamat_kos, edit_ketersediaan_kamar, edit_fasilitas_kos, edit_harga_kos, edit_owner_phone_number;
    private RadioGroup edit_tipe_kos;
    private Button btn_update_kos;
    private List<Uri> selectedUris = new ArrayList<>();
    private List<SlideModel> slideModels = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Button btnSelectLocation;
    private TextView tvSelectedLocation;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private String kosId;
    private String userId;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedUris.clear();
                            slideModels.clear();
                            imageUrls.clear();

                            if (data.getClipData() != null) {
                                int count = data.getClipData().getItemCount();
                                for (int i = 0; i < count; i++) {
                                    Uri uri = data.getClipData().getItemAt(i).getUri();
                                    selectedUris.add(uri);
                                    slideModels.add(new SlideModel(uri.toString(), ScaleTypes.FIT));
                                }
                            } else if (data.getData() != null) {
                                Uri uri = data.getData();
                                selectedUris.add(uri);
                                slideModels.add(new SlideModel(uri.toString(), ScaleTypes.FIT));
                            }

                            imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                        } else {
                            Toast.makeText(EditKos.this, "No item selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kos);

        kosId = getIntent().getStringExtra("KOS_ID");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("List Kos").child(userId).child(kosId);
        storageReference = FirebaseStorage.getInstance().getReference();

        upload_image_kos = findViewById(R.id.upload_image_kos);
        imageSlider = findViewById(R.id.image_slider);
        btn_update_kos = findViewById(R.id.btn_update_kos);
        edit_nama_kos = findViewById(R.id.edit_nama_kos);
        edit_deskripsi_kos = findViewById(R.id.edit_deskripsi_kos);
        edit_catatan_alamat_kos = findViewById(R.id.edit_catatan_alamat_kos);
        edit_ketersediaan_kamar = findViewById(R.id.edit_ketersediaan_kamar);
        edit_fasilitas_kos = findViewById(R.id.edit_fasilitas_kos);
        edit_harga_kos = findViewById(R.id.edit_harga_kos);
        edit_tipe_kos = findViewById(R.id.edit_tipe_kos);
        edit_owner_phone_number = findViewById(R.id.edit_owner_phone_number);
        btnSelectLocation = findViewById(R.id.btn_select_location);
        tvSelectedLocation = findViewById(R.id.tv_selected_location);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating...");
        progressDialog.setCancelable(false);

        loadKosDetails();

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

        btn_update_kos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    progressDialog.show();
                    if (!selectedUris.isEmpty()) {
                        uploadImagesToFirebase();
                    } else {
                        updateKosDetails();  // Update details without changing images
                    }
                }
            }
        });

        btnSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditKos.this, SelectLocation.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_LOCATION);
            }
        });

    }

    private void uploadImagesToFirebase() {
        for (Uri fileUri : selectedUris) {
            StorageReference fileReference = storageReference.child("images/" + UUID.randomUUID().toString());
            fileReference.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
                fileReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    imageUrls.add(downloadUri.toString());
                    if (imageUrls.size() == selectedUris.size()) {
                        updateKosDetails();
                    }
                });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(EditKos.this, "Failed to upload images", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private boolean validateInputs() {
        if (edit_nama_kos.getText().toString().trim().isEmpty()) {
            edit_nama_kos.setError("Nama kos tidak boleh kosong");
            return false;
        }
        if (edit_deskripsi_kos.getText().toString().trim().isEmpty()) {
            edit_deskripsi_kos.setError("Deskripsi kos tidak boleh kosong");
            return false;
        }
        if (edit_catatan_alamat_kos.getText().toString().trim().isEmpty()) {
            edit_catatan_alamat_kos.setError("Catatan alamat kos tidak boleh kosong");
            return false;
        }
        if (edit_ketersediaan_kamar.getText().toString().trim().isEmpty()) {
            edit_ketersediaan_kamar.setError("Ketersediaan kamar kos tidak boleh kosong");
            return false;
        }
        if (edit_fasilitas_kos.getText().toString().trim().isEmpty()) {
            edit_fasilitas_kos.setError("Fasilitas kos tidak boleh kosong");
            return false;
        }
        if (edit_harga_kos.getText().toString().trim().isEmpty()) {
            edit_harga_kos.setError("Harga kos tidak boleh kosong");
            return false;
        }
        if (edit_owner_phone_number.getText().toString().trim().isEmpty()) {
            edit_owner_phone_number.setError("Nomor telepon pemilik tidak boleh kosong");
            return false;
        }
        if (selectedLocation == null) {
            Toast.makeText(this, "Lokasi belum dipilih", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edit_tipe_kos.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Tipe kos belum dipilih", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loadKosDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String namaKos = snapshot.child("namaKos").getValue(String.class);
                    String deskripsiKos = snapshot.child("deskripsiKos").getValue(String.class);
                    String catatanAlamatKos = snapshot.child("catatanAlamatKos").getValue(String.class);
                    String fasilitasKos = snapshot.child("fasilitasKos").getValue(String.class);
                    Integer ketersediaanKamarKos = snapshot.child("ketersediaanKamarKos").getValue(Integer.class);
                    String hargaKos = snapshot.child("hargaKos").getValue(String.class);
                    String tipeKos = snapshot.child("tipeKos").getValue(String.class);
                    Double latitude = snapshot.child("latitude").getValue(Double.class);
                    Double longitude = snapshot.child("longitude").getValue(Double.class);
                    String ownerPhoneNumber = snapshot.child("ownerPhoneNumber").getValue(String.class);

                    edit_nama_kos.setText(namaKos);
                    edit_deskripsi_kos.setText(deskripsiKos);
                    edit_catatan_alamat_kos.setText(catatanAlamatKos);
                    edit_fasilitas_kos.setText(fasilitasKos);
                    if (ketersediaanKamarKos != null) {
                        edit_ketersediaan_kamar.setText(String.valueOf(ketersediaanKamarKos));  // Konversi ke string untuk ditampilkan
                    }
                    edit_harga_kos.setText(hargaKos);
                    if (ownerPhoneNumber != null) {
                        edit_owner_phone_number.setText(ownerPhoneNumber); // Set nomor telepon pemilik
                    } else {
                        edit_owner_phone_number.setText(""); // Set kosong jika tidak ada nomor
                    }
                    if (latitude != null && longitude != null) {
                        selectedLocation = new LatLng(latitude, longitude);
                        tvSelectedLocation.setText("Selected Location: " + latitude + ", " + longitude);
                    }

                    if (tipeKos != null) {
                        switch (tipeKos.trim().toLowerCase()) {
                            case "putra":
                                edit_tipe_kos.check(R.id.putra);
                                break;
                            case "putri":
                                edit_tipe_kos.check(R.id.putri);
                                break;
                            case "campur":
                                edit_tipe_kos.check(R.id.campur);
                                break;
                        }
                    }

                    for (DataSnapshot imageSnapshot : snapshot.child("imageUrls").getChildren()) {
                        String imageUrl = imageSnapshot.getValue(String.class);
                        imageUrls.add(imageUrl);
                        slideModels.add(new SlideModel(imageUrl, ScaleTypes.FIT));
                    }

                    imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditKos.this, "Failed to load details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateKosDetails() {
        String namaKos = edit_nama_kos.getText().toString().trim();
        String deskripsiKos = edit_deskripsi_kos.getText().toString().trim();
        String catatanAlamatKos = edit_catatan_alamat_kos.getText().toString().trim();
        String fasilitasKos = edit_fasilitas_kos.getText().toString().trim();
        String hargaKos = edit_harga_kos.getText().toString().trim();
        String ownerPhoneNumber = edit_owner_phone_number.getText().toString().trim();

        int ketersediaanKamarKos;
        try {
            ketersediaanKamarKos = Integer.parseInt(edit_ketersediaan_kamar.getText().toString().trim());
        } catch (NumberFormatException e) {
            edit_ketersediaan_kamar.setError("Ketersediaan kamar harus berupa angka");
            return;
        }

        HashMap<String, Object> kosData = new HashMap<>();
        kosData.put("namaKos", namaKos);
        kosData.put("deskripsiKos", deskripsiKos);
        kosData.put("catatanAlamatKos", catatanAlamatKos);
        kosData.put("fasilitasKos", fasilitasKos);
        kosData.put("ketersediaanKamarKos", ketersediaanKamarKos);
        kosData.put("hargaKos", hargaKos);
        kosData.put("latitude", selectedLocation.latitude);
        kosData.put("longitude", selectedLocation.longitude);
        kosData.put("imageUrls", imageUrls);
        kosData.put("ownerPhoneNumber", ownerPhoneNumber);

        int selectedId = edit_tipe_kos.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        kosData.put("tipeKos", radioButton.getText().toString());

        Log.d("UpdateKosDetails", "Kos Data: " + kosData.toString());

        databaseReference.updateChildren(kosData).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Log.d("UpdateKosDetails", "Kos updated successfully");
                Toast.makeText(EditKos.this, "Kos updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.d("UpdateKosDetails", "Failed to update kos");
                Toast.makeText(EditKos.this, "Failed to update kos", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Log.d("UpdateKosDetails", "Failed to update kos", e);
            Toast.makeText(EditKos.this, "Failed to update kos", Toast.LENGTH_SHORT).show();
        });

    }

    /*
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_LOCATION && resultCode == RESULT_OK) {
            if (data != null) {
                selectedLocation = data.getParcelableExtra("selected_location");
                if (selectedLocation != null) {
                    String locationText = "Selected Location: " + selectedLocation.latitude + ", " + selectedLocation.longitude;
                    tvSelectedLocation.setText(locationText);
                }
            }
        }
    }
}