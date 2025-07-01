package com.example.arachismonitoring;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.google.api.client.util.ExponentialBackOff;
import java.io.IOException;
import java.util.Properties;

public class DeliveryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private GoogleAccountCredential credential;
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5 minutes
    private long lastFetchTime = 0;
    private List<DeliveryOrderRow> cachedData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        
        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup SwipeRefresh
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefresh.setOnRefreshListener(this::refreshData);

        // Get account from intent
        GoogleSignInAccount account = getIntent().getParcelableExtra("account");
        if (account != null) {
            credential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(),
                    Collections.singleton(SheetsScopes.SPREADSHEETS)
            );
            credential.setSelectedAccount(account.getAccount());
            fetchSheetData();
        } else {
            Toast.makeText(this, "No Google account found!", Toast.LENGTH_LONG).show();
        }
    }

    private void refreshData() {
        long currentTime = System.currentTimeMillis();
        if (cachedData != null && (currentTime - lastFetchTime) < CACHE_DURATION && !swipeRefresh.isRefreshing()) {
            // Use cached data if it's still fresh and not manually refreshing
            updateUI(cachedData);
            return;
        }
        // Force refresh by clearing cache
        cachedData = null;
        lastFetchTime = 0;
        fetchSheetData();
    }

    private void updateUI(List<DeliveryOrderRow> data) {
        DeliveryOrderAdapter adapter = new DeliveryOrderAdapter(data, credential);
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

                String spreadsheetId = getSpreadsheetId(this);
                String range = "Kuryer / Çatdırılma!A1:M";

                ValueRange response = service.spreadsheets().values()
                        .get(spreadsheetId, range)
                        .execute();

                List<List<Object>> values = response.getValues();
                List<DeliveryOrderRow> parsedDataList = new ArrayList<>();
                if (values != null && values.size() > 1) {
                    for (int i = 1; i < values.size(); i++) {
                        List<Object> row = values.get(i);
                        parsedDataList.add(new DeliveryOrderRow(
                                getValue(row, 0), // Nº
                                getValue(row, 1), // Sifariş tarixi
                                getValue(row, 2), // Sifariş Ayı
                                getValue(row, 3), // Ad
                                getValue(row, 4), // Soyad
                                getValue(row, 5), // Əlaqə Nömrəsi
                                getValue(row, 6), // Məhsul Növü
                                getValue(row, 7), // Packing
                                getValue(row, 8), // Məhsul Sayı
                                getValue(row, 9), // Ünvan
                                getValue(row, 10), // Saat
                                getValue(row, 11),  // Sifarişin Statusu
                                getValue(row, 12)   // Ödəniş
                        ));
                    }
                }

                // Update cache
                cachedData = parsedDataList;
                lastFetchTime = System.currentTimeMillis();

                runOnUiThread(() -> {
                    updateUI(parsedDataList);
                    swipeRefresh.setRefreshing(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    swipeRefresh.setRefreshing(false);
                });
            }
        }).start();
    }

    private String getValue(List<Object> row, int index) {
        if (row.size() > index && row.get(index) != null && !row.get(index).toString().trim().isEmpty()) {
            return row.get(index).toString();
        } else {
            return "xxx";
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