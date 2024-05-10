package com.example.sewakos;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtil {

    public static StorageReference getCurrentProfilePicStorageRef() {
        String currentUserId = currentUserId();
        if (currentUserId != null) {
            return FirebaseStorage.getInstance().getReference().child("profile_pic").child(currentUserId);
        } else {
            return null;
        }
    }

    private static String currentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }
}

