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

public class OrderStatusHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private GoogleAccountCredential credential;
    private GoogleSignInAccount account;
    private List<OrderStatusHistoryRow> allRows = new ArrayList<>();
    private OrderStatusHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_status_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewOrderStatusHistory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            account = getArguments().getParcelable("account");
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

    private void fetchSheetData() {
        new Thread(() -> {
            try {
                Sheets service = new Sheets.Builder(
                        com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                        com.google.api.client.json.gson.GsonFactory.getDefaultInstance(),
                        credential
                ).setApplicationName("LocalSheetApp").build();
                String spreadsheetId = getSpreadsheetId(requireContext());
                String range = "Order Status History!A1:G";
                ValueRange response = service.spreadsheets().values()
                        .get(spreadsheetId, range)
                        .execute();
                List<List<Object>> values = response.getValues();
                List<OrderStatusHistoryRow> parsedDataList = new ArrayList<>();
                if (values != null && values.size() > 1) {
                    for (int i = 1; i < values.size(); i++) {
                        List<Object> row = values.get(i);
                        parsedDataList.add(new OrderStatusHistoryRow(
                                getValue(row, 0), // orderNumber
                                getValue(row, 1), // orderDetails
                                getValue(row, 2), // dateTime
                                getValue(row, 3), // previousStatus
                                getValue(row, 4), // newStatus
                                getValue(row, 5), // note
                                getValue(row, 6)  // user
                        ));
                    }
                }
                allRows = parsedDataList;
                requireActivity().runOnUiThread(() -> {
                    adapter = new OrderStatusHistoryAdapter(allRows);
                    recyclerView.setAdapter(adapter);
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error fetching status history: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    private String getValue(List<Object> row, int index) {
        if (row.size() > index) {
            return String.valueOf(row.get(index));
        } else {
            return "";
        }
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