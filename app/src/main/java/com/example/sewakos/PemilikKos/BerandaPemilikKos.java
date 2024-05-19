package com.example.sewakos.PemilikKos;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.sewakos.AndroidUtil;
import com.example.sewakos.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BerandaPemilikKos extends Fragment {

    private FrameLayout btn_tambah_kos;
    private RecyclerView recyclerView;
    private ArrayList<AndroidUtil> dataList;
    private MyAdapter adapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    //final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("List Kos");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beranda_pemilik_kos, container, false);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userID = currentUser.getUid();

            btn_tambah_kos = view.findViewById(R.id.btn_tambah_kos);
            recyclerView = view.findViewById(R.id.recyclerView);

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            dataList = new ArrayList<>();
            adapter = new MyAdapter(dataList, getContext());
            recyclerView.setAdapter(adapter);

            databaseReference = FirebaseDatabase.getInstance().getReference("List Kos").child(userID);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dataList.clear();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        AndroidUtil androidUtil = dataSnapshot.getValue(AndroidUtil.class);
                        if (androidUtil != null) {
                            androidUtil.setId(dataSnapshot.getKey()); // Set the key as ID
                            androidUtil.setUserId(userID); // Set user ID
                            dataList.add(androidUtil);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseData", "Failed to read data", error.toException());
                }
            });

            btn_tambah_kos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UploadKos.class);
                    startActivity(intent);
                }
            });
        } else {

        }

        return view;
    }
}