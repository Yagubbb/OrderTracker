package com.example.arachismonitoring;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;
import android.view.MenuItem;
import android.content.Context;
import java.io.IOException;
import java.util.Properties;

public class DataActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private MaterialToolbar toolbar;
    private GoogleAccountCredential credential;
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5 minutes
    private long lastFetchTime = 0;
    private List<OrderRow> cachedData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        
        // Initialize views
        recyclerView = findViewById(R.id.sheetRecyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        toolbar = findViewById(R.id.toolbar);
        
        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Orders");
        }

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
        String role = getIntent().getStringExtra("role");
        if (account != null) {
            credential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(),
                    Collections.singleton(SheetsScopes.SPREADSHEETS)
            );
            credential.setSelectedAccount(account.getAccount());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setSubtitle(role);
            }
            fetchSheetData();
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

    private void updateUI(List<OrderRow> data) {
        // Convert OrderRow list to DeliveryOrderRow list
        List<DeliveryOrderRow> deliveryData = new ArrayList<>();
        for (OrderRow order : data) {
            DeliveryOrderRow deliveryOrder = new DeliveryOrderRow(
                order.getNo(),           // no
                order.getDate(),         // sifarisTarixi
                "",                      // sifarisAyi
                order.getName(),         // ad
                order.getSurname(),      // soyad
                order.getPhone(),        // elaqeNomresi
                order.getProduct(),      // mehsulNovu
                "",                      // packing
                order.getQty(),          // mehsulSayi
                order.getAddress(),      // unvan
                "",                      // saat
                order.getStatus(),       // sifarisStatusu
                ""                       // paymentStatus (not available in OrderRow)
            );
            deliveryData.add(deliveryOrder);
        }
        
        // Create adapter with converted data
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

                String spreadsheetId = getSpreadsheetId(this);
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
                        String qty = row.size() > 2 ? row.get(2).toString() : "";
                        String name = row.size() > 3 ? row.get(3).toString() : "";
                        String surname = row.size() > 4 ? row.get(4).toString() : "";
                        String phone = row.size() > 5 ? row.get(5).toString() : "";
                        String product = row.size() > 6 ? row.get(6).toString() : "";
                        String address = row.size() > 9 ? row.get(9).toString() : "";
                        String status = row.size() > 7 ? row.get(7).toString() : "";

                        parsedDataList.add(new OrderRow(no, date, qty, name, surname, phone, product, address, status));
                    }

                    // Update cache
                    cachedData = parsedDataList;
                    lastFetchTime = System.currentTimeMillis();
                    Log.d("DATA_DEBUG", "Parsed list size: " + parsedDataList.size());
                    if (!parsedDataList.isEmpty()) {
                        OrderRow first = parsedDataList.get(0);
                        Log.d("DATA_DEBUG", "First row: no=" + first.getNo() + ", name=" + first.getName() + ", surname=" + first.getSurname() + ", status=" + first.getStatus());
                    }

                    runOnUiThread(() -> {
                        updateUI(parsedDataList);
                        swipeRefresh.setRefreshing(false);
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    swipeRefresh.setRefreshing(false);
                });
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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