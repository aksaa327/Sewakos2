package com.example.sewakos.PemilikKos;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.sewakos.R;


public class BerandaPemilikKos extends Fragment {

    private FrameLayout btn_tambah_kos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beranda_pemilik_kos, container, false);

        btn_tambah_kos = view.findViewById(R.id.btn_tambah_kos);

        btn_tambah_kos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UploadKos.class);
                startActivity(intent);
            }
        });

        return view;
    }
}