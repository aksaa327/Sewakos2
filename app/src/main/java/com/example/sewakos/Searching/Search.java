package com.example.sewakos.Searching;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.sewakos.AndroidUtil;
import com.example.sewakos.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment {

    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private List<AndroidUtil> kosList;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.search_view);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        kosList = new ArrayList<>();
        searchAdapter = new SearchAdapter(getActivity(), kosList);
        recyclerView.setAdapter(searchAdapter);
        
        loadKosData();
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdapter.filter(newText);
                return false;
            }
        });

        return view;
    }

    private void loadKosData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("List Kos");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                kosList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot kosSnapshot : userSnapshot.getChildren()) {
                        AndroidUtil kos = kosSnapshot.getValue(AndroidUtil.class);
                        if (kos != null) {
                            kosList.add(kos);
                        }
                    }
                }
                searchAdapter.setKosListFull(kosList); // Perbarui kosListFull di adapter
                searchAdapter.notifyDataSetChanged();
                Log.d("SearchFragment", "Data loaded: " + kosList.size() + " items");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SearchFragment", "Failed to load data", error.toException());
            }
        });
    }
}