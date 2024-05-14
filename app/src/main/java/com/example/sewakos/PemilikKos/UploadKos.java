package com.example.sewakos.PemilikKos;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sewakos.AndroidUtil;
import com.example.sewakos.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class UploadKos extends AppCompatActivity {

    private ImageView upload_image_kos;
    EditText upload_nama_kos, upload_deskripsi_kos, upload_catatan_alamat_kos, upload_ketersediaan_kamar
            ,upload_fasilitas_kos, upload_harga_kos;
    private RadioGroup upload_tipe_kos;
    private RadioButton putra, putri, campur;
    private Button btn_upload_kos;
    private Uri imageUri;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("List Kos");
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_kos);

        btn_upload_kos = findViewById(R.id.btn_upload_kos);
        upload_image_kos = findViewById(R.id.upload_image_kos);
        upload_nama_kos = findViewById(R.id.upload_nama_kos);
        upload_deskripsi_kos = findViewById(R.id.upload_deskripsi_kos);
        upload_catatan_alamat_kos = findViewById(R.id.upload_catatan_alamat_kos);
        upload_ketersediaan_kamar = findViewById(R.id.upload_ketersediaan_kamar);
        upload_fasilitas_kos = findViewById(R.id.upload_fasilitas_kos);
        upload_harga_kos = findViewById(R.id.upload_harga_kos);
        upload_tipe_kos = findViewById(R.id.upload_tipe_kos);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            imageUri = data.getData();
                            upload_image_kos.setImageURI(imageUri);
                        } else {
                            Toast.makeText(UploadKos.this, "no item selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        upload_image_kos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_OPEN_DOCUMENT);
                photoPicker.setType("*/*");
                photoPicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                String[] mimeTypes = {"image/*", "video/*"};
                photoPicker.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                activityResultLauncher.launch(photoPicker);
            }
        });

        btn_upload_kos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(UploadKos.this, "Please Select image", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void uploadToFirebase(Uri uri) {
        String namaKos = upload_nama_kos.getText().toString();
        String deskripsiKos = upload_deskripsi_kos.getText().toString();
        String catatanAlamatKos = upload_catatan_alamat_kos.getText().toString();
        String fasilitasKos = upload_fasilitas_kos.getText().toString();
        String ketersediaanKamar = upload_ketersediaan_kamar.getText().toString();
        String hargaKos = upload_harga_kos.getText().toString();
        Integer ketersediaanKamarInteger = Integer.parseInt(ketersediaanKamar);
        Integer hargaKosInteger = Integer.parseInt(hargaKos);
        int selectedRadioButtonId = upload_tipe_kos.getCheckedRadioButtonId();

        String folderName = "list kos";
        final StorageReference folderReference = storageReference.child(folderName);
        final StorageReference imageReference = folderReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = currentUser.getUid();

        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

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

                        AndroidUtil androidUtil = new AndroidUtil(uri.toString(), namaKos, deskripsiKos, catatanAlamatKos, fasilitasKos, tipeKos, ketersediaanKamarInteger, hargaKosInteger);

                        String key = databaseReference.child(userID).push().getKey();
                        databaseReference.child(userID).child(key).setValue(androidUtil);

                        Toast.makeText(UploadKos.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UploadKos.this, BerandaPemilikKos.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadKos.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}