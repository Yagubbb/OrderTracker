package com.example.arachismonitoring;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import com.google.android.material.appbar.MaterialToolbar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ImageView;
import com.example.arachismonitoring.ControllerDeliveryOrderRow;
import android.content.Context;
import java.io.IOException;
import java.util.Properties;

public class ControllerDeliveryFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private GoogleAccountCredential credential;
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5 minutes
    private long lastFetchTime = 0;
    private List<ControllerDeliveryOrderRow> cachedData = null;
    private GoogleSignInAccount account;
    private String lastSelectedStatus = "Hamısı";
    private final String[] statuses = {"Hamısı", "sifariş alındı", "çatdırıldı", "ertələndi", "ləğv olundu", "çatdırılır"};
    private List<ControllerDeliveryOrderRow> allOrders = new ArrayList<>(); // Keep all orders for filtering

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controller_delivery, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            ((androidx.appcompat.app.AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefresh.setOnRefreshListener(this::fetchSheetData);
        setHasOptionsMenu(true);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_controller_delivery, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            showStatusFilterBottomSheet();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showStatusFilterBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(getResources().getColor(R.color.background));

        // Create a horizontal layout for icon + text
        LinearLayout headerLayout = new LinearLayout(requireContext());
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setBackgroundColor(getResources().getColor(R.color.primary));
        headerLayout.setPadding(32, 32, 32, 16);
        headerLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // Add filter icon
        ImageView filterIcon = new ImageView(requireContext());
        filterIcon.setImageResource(R.drawable.ic_filter_list);
        filterIcon.setColorFilter(getResources().getColor(R.color.white));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(48, 48);
        iconParams.setMarginEnd(16);
        filterIcon.setLayoutParams(iconParams);
        headerLayout.addView(filterIcon);

        // Add text
        TextView header = new TextView(requireContext());
        header.setText("Filter");
        header.setTextColor(getResources().getColor(R.color.white));
        header.setTextSize(18);
        header.setGravity(android.view.Gravity.CENTER_VERTICAL);
        headerLayout.addView(header);

        layout.addView(headerLayout);

        ListView listView = new ListView(requireContext());
        listView.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.dropdown_menu_popup_item, statuses));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            lastSelectedStatus = statuses[position];
            filterOrdersByStatus(lastSelectedStatus);
            dialog.dismiss();
        });
        layout.addView(listView);
        dialog.setContentView(layout);
        dialog.show();
    }

    public void fetchSheetData() {
        long currentTime = System.currentTimeMillis();
        if (cachedData != null && (currentTime - lastFetchTime) < CACHE_DURATION && !swipeRefresh.isRefreshing()) {
            updateUI(cachedData);
            return;
        }
        cachedData = null;
        lastFetchTime = 0;
        swipeRefresh.setRefreshing(true);
        new Thread(() -> {
            try {
                Sheets service = new Sheets.Builder(
                        com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                        com.google.api.client.json.gson.GsonFactory.getDefaultInstance(),
                        credential
                ).setApplicationName("LocalSheetApp").build();
                String spreadsheetId = getSpreadsheetId(requireContext());
                String range = "Əsas Səhifə!A1:S";
                ValueRange response = service.spreadsheets().values()
                        .get(spreadsheetId, range)
                        .execute();
                List<List<Object>> values = response.getValues();
                List<ControllerDeliveryOrderRow> parsedDataList = new ArrayList<>();
                if (values != null && values.size() > 1) {
                    for (int i = 1; i < values.size(); i++) {
                        List<Object> row = values.get(i);
                        String orderNo = getValue(row, 0);
                        if (orderNo != null && !orderNo.trim().isEmpty() && !orderNo.trim().equalsIgnoreCase("xxx")) {
                            parsedDataList.add(new ControllerDeliveryOrderRow(
                                orderNo,  // no
                                getValue(row, 1),  // sifarisTarixi
                                getValue(row, 2),  // sifarisAyi
                                getValue(row, 3),  // sifarisIli
                                getValue(row, 4),  // ad
                                getValue(row, 5),  // soyad
                                getValue(row, 6),  // elaqeNomresi
                                getValue(row, 7),  // mehsulNovu
                                getValue(row, 8),  // packing
                                getValue(row, 9),  // mehsulSayi
                                getValue(row, 10), // satisQiymeti
                                getValue(row, 11), // mayaDeyeri
                                getValue(row, 12), // xalisGəlir
                                getValue(row, 13), // xalisGəlirFaiz
                                getValue(row, 14), // ortaSifarisDeyeri
                                getValue(row, 15), // unvan
                                getValue(row, 16), // saat
                                getValue(row, 17), // sifarisStatusu
                                getValue(row, 18)  // paymentStatus
                            ));
                        }
                    }
                }
                cachedData = parsedDataList;
                Collections.reverse(cachedData);
                lastFetchTime = System.currentTimeMillis();
                requireActivity().runOnUiThread(() -> {
                    updateUI(parsedDataList);
                    swipeRefresh.setRefreshing(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    swipeRefresh.setRefreshing(false);
                });
            }
        }).start();
    }

    private void updateUI(List<ControllerDeliveryOrderRow> data) {
        allOrders = new ArrayList<>(data); // Save all orders for filtering
        filterOrdersByStatus(lastSelectedStatus);
        swipeRefresh.setRefreshing(false);
    }

    private void filterOrdersByStatus(String status) {
        List<ControllerDeliveryOrderRow> filtered;
        if (status.equals("Hamısı")) {
            filtered = new ArrayList<>(allOrders);
        } else {
            filtered = new ArrayList<>();
            for (ControllerDeliveryOrderRow order : allOrders) {
                if (order.getSifarisStatusu() != null && order.getSifarisStatusu().equalsIgnoreCase(status)) {
                    filtered.add(order);
                }
            }
        }
        ControllerOrderAdapter adapter = new ControllerOrderAdapter(filtered, credential);
        recyclerView.setAdapter(adapter);
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