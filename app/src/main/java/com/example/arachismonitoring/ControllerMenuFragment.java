package com.example.arachismonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ControllerMenuFragment extends Fragment {
    private GoogleSignInAccount account;
    private OnControllerMenuListener listener;

    public interface OnControllerMenuListener {
        void onDeliverySelected(GoogleSignInAccount account);
        void onDataSelected(GoogleSignInAccount account);
        void onOrderStatusHistorySelected(GoogleSignInAccount account);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controller_menu, container, false);
        Button btnDelivery = view.findViewById(R.id.btnDelivery);
        Button btnData = view.findViewById(R.id.btnData);
        Button btnOrderStatusHistory = view.findViewById(R.id.btnOrderStatusHistory);
        btnDelivery.setOnClickListener(v -> {
            if (listener != null) listener.onDeliverySelected(account);
        });
        btnData.setOnClickListener(v -> {
            if (listener != null) listener.onDataSelected(account);
        });
        btnOrderStatusHistory.setOnClickListener(v -> {
            if (listener != null) listener.onOrderStatusHistorySelected(account);
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            account = getArguments().getParcelable("account");
        }
        if (getActivity() instanceof OnControllerMenuListener) {
            listener = (OnControllerMenuListener) getActivity();
        }
    }
} 