package com.example.arachismonitoring;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class EntryFragment extends Fragment {
    private OnEntryListener listener;

    public interface OnEntryListener {
        void onCourierClicked();
        void onControllerClicked();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);
        MaterialButton btnCourier = view.findViewById(R.id.btnCourier);
        MaterialButton btnController = view.findViewById(R.id.btnController);
        btnCourier.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Courier clicked", Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onCourierClicked();
        });
        btnController.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Controller clicked", Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onControllerClicked();
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnEntryListener) {
            listener = (OnEntryListener) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
} 