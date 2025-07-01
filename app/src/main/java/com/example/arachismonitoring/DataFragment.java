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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

public class DataFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private GoogleAccountCredential credential;
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5 minutes
    private long lastFetchTime = 0;
    private List<OrderRow> cachedData = null;
    private GoogleSignInAccount account;
    private String role;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        recyclerView = view.findViewById(R.id.sheetRecyclerView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefresh.setOnRefreshListener(this::refreshData);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            account = getArguments().getParcelable("account");
            role = getArguments().getString("role");
        }
        if (account != null) {
            credential = GoogleAccountCredential.usingOAuth2(
                    requireContext().getApplicationContext(),
                    Collections.singleton(SheetsScopes.SPREADSHEETS)
            );
            credential.setSelectedAccount(account.getAccount());
            fetchSheetData();
        }
    }

    public void refreshData() {
        long currentTime = System.currentTimeMillis();
        if (cachedData != null && (currentTime - lastFetchTime) < CACHE_DURATION && !swipeRefresh.isRefreshing()) {
            updateUI(cachedData);
            return;
        }
        cachedData = null;
        lastFetchTime = 0;
        fetchSheetData();
    }

    private void updateUI(List<OrderRow> data) {
        List<DeliveryOrderRow> deliveryData = new ArrayList<>();
        OrderAdapter adapter = new OrderAdapter(deliveryData, credential);
        recyclerView.setAdapter(adapter);
        swipeRefresh.setRefreshing(false);
    }

    private void fetchSheetData() {
        swipeRefresh.setRefreshing(true);
        new Thread(() -> {
            try {
                Sheets service = new Sheets.Builder(
                        com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                        com.google.api.client.json.gson.GsonFactory.getDefaultInstance(),
                        credential
                ).setApplicationName("LocalSheetApp").build();

                String spreadsheetId = getSpreadsheetId(requireContext());
                String range = "Kuryer / Çatdırılma!A1:L";

                ValueRange response = service.spreadsheets().values()
                        .get(spreadsheetId, range)
                        .execute();

                List<List<Object>> values = response.getValues();

                if (values != null && !values.isEmpty()) {
                    List<OrderRow> parsedDataList = new ArrayList<>();
                    for (int i = 1; i < values.size(); i++) {
                        List<Object> row = values.get(i);
                        String no = row.size() > 0 ? row.get(0).toString() : "";
                        String date = row.size() > 1 ? row.get(1).toString() : "";
                        String name = row.size() > 3 ? row.get(3).toString() : "";
                        String surname = row.size() > 4 ? row.get(4).toString() : "";
                        String phone = row.size() > 5 ? row.get(5).toString() : "";
                        String product = row.size() > 6 ? row.get(6).toString() : "";
                        String address = row.size() > 9 ? row.get(9).toString() : "";
                        String qty = row.size() > 8 ? row.get(8).toString() : "";
                        String status = row.size() > 11 ? row.get(11).toString() : "";
                        android.util.Log.d("DATA_DEBUG", "Row " + i + " status=" + status);
                        parsedDataList.add(new OrderRow(no, date, qty, name, surname, phone, product, address, status));
                    }
                    cachedData = parsedDataList;
                    lastFetchTime = System.currentTimeMillis();
                    android.util.Log.d("DATA_DEBUG", "Parsed list size: " + parsedDataList.size());
                    if (!parsedDataList.isEmpty()) {
                        OrderRow first = parsedDataList.get(0);
                        android.util.Log.d("DATA_DEBUG", "First row: no=" + first.getNo() + ", name=" + first.getName() + ", surname=" + first.getSurname() + ", status=" + first.getStatus());
                    }
                    requireActivity().runOnUiThread(() -> {
                        updateUI(parsedDataList);
                        swipeRefresh.setRefreshing(false);
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "No data available", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    swipeRefresh.setRefreshing(false);
                });
            }
        }).start();
    }

    private static String getSpreadsheetId(Context context) {
        Properties properties = new Properties();
        try {
            properties.load(context.getAssets().open("local.properties"));
            return properties.getProperty("SHEET_ID");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
} 