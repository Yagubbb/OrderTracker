package com.example.arachismonitoring;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.graphics.Color;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import android.content.Context;
import android.content.res.Resources;
import java.io.IOException;
import java.util.Properties;

public class OrderDetailsFragment extends Fragment {
    private DeliveryOrderRow order;
    private static final String COLOR_RED = "#C93E43";
    private static final String COLOR_GREEN = "#458651";
    private static final String COLOR_BLUE = "#255765";
    private static final String COLOR_GRAY = "#A6A885";
    private static final String COLOR_YELLOW = "#E0D44F";
    private GoogleAccountCredential credential;
    private static final String RANGE = "Kuryer / Çatdırılma!A1:L";
    private String sheetName;

    public static OrderDetailsFragment newInstance(DeliveryOrderRow order, GoogleAccountCredential credential) {
        OrderDetailsFragment fragment = new OrderDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("order", order);
        fragment.setArguments(args);
        fragment.credential = credential;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = (DeliveryOrderRow) getArguments().getSerializable("order");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_details, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        TextView tvOrderHeader = view.findViewById(R.id.tvOrderHeader);
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvAddress = view.findViewById(R.id.tvAddress);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        TextView tvProduct = view.findViewById(R.id.tvProduct);
        TextView tvQuantity = view.findViewById(R.id.tvQuantity);
        TextView tvPacking = view.findViewById(R.id.tvPacking);
        TextView tvTime = view.findViewById(R.id.tvTime);
        TextView tvStatus = view.findViewById(R.id.tvStatus);
        ImageView ivStatusIcon = view.findViewById(R.id.ivStatusIcon);
        LinearLayout buttonContainer = view.findViewById(R.id.buttonContainer);

        // Clear any existing buttons to prevent duplication
        buttonContainer.removeAllViews();

        // Set order header (Nº Name Surname)
        String header = "Nº " + order.getNo() + " " + order.getAd() + " " + order.getSoyad();
        tvOrderHeader.setText(header);

        // Set date
        tvDate.setText("Date: " + order.getSifarisTarixi());

        // Set address
        tvAddress.setText("Address: " + order.getUnvan());

        // Set phone
        tvPhone.setText("Phone: " + order.getElaqeNomresi());

        // Set product
        tvProduct.setText("Product: " + order.getMehsulNovu());

        // Set quantity
        tvQuantity.setText("Quantity: " + order.getMehsulSayi());

        // Set packing
        tvPacking.setText("Packing: " + order.getPacking());

        // Set time
        tvTime.setText("Time: " + order.getSaat());

        // Set status text and icon based on status
        String status = order.getSifarisStatusu();
        tvStatus.setText(getEnglishStatus(status));
        
        int iconRes = 0;
        if (status.equalsIgnoreCase("sifariş alındı")) {
            iconRes = R.drawable.accepted;
            addButton(buttonContainer, "Deliver", COLOR_GREEN, "çatdırılır");
        } else if (status.equalsIgnoreCase("çatdırıldı")) {
            iconRes = R.drawable.done;
            addButton(buttonContainer, "Edit", COLOR_RED, "sifariş alındı");
        } else if (status.equalsIgnoreCase("ertələndi")) {
            iconRes = R.drawable.pending;
            addButton(buttonContainer, "Deliver", COLOR_GREEN, "çatdırılır");
            addButton(buttonContainer, "Edit", COLOR_RED, "sifariş alındı");
        } else if (status.equalsIgnoreCase("ləğv olundu")) {
            iconRes = android.R.drawable.ic_menu_close_clear_cancel;
            addButton(buttonContainer, "Edit", COLOR_RED, "sifariş alındı");
        } else if (status.equalsIgnoreCase("çatdırılır")) {
            iconRes = R.drawable.ontheway;
            addButton(buttonContainer, "Delivered", COLOR_GREEN, "çatdırıldı");
            addButton(buttonContainer, "Cancelled", COLOR_GRAY, "ləğv olundu");
            addButton(buttonContainer, "Postponed", COLOR_BLUE, "ertələndi");
        }

        if (iconRes != 0) {
            ivStatusIcon.setImageResource(iconRes);
            ivStatusIcon.setColorFilter(Color.GRAY);
        }

        // Add payment status display
        TextView tvPaymentStatus = view.findViewById(R.id.tvPaymentStatus);
        if (tvPaymentStatus != null && order != null && order.getPaymentStatus() != null && !order.getPaymentStatus().isEmpty()) {
            tvPaymentStatus.setText("Payment: " + order.getPaymentStatus());
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        TextView tvOrderHeader = view.findViewById(R.id.tvOrderHeader);
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvAddress = view.findViewById(R.id.tvAddress);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        TextView tvProduct = view.findViewById(R.id.tvProduct);
        TextView tvQuantity = view.findViewById(R.id.tvQuantity);
        TextView tvPacking = view.findViewById(R.id.tvPacking);
        TextView tvTime = view.findViewById(R.id.tvTime);
        TextView tvStatus = view.findViewById(R.id.tvStatus);
        ImageView ivStatusIcon = view.findViewById(R.id.ivStatusIcon);
        LinearLayout buttonContainer = view.findViewById(R.id.buttonContainer);

        // Clear any existing buttons to prevent duplication
        buttonContainer.removeAllViews();

        // Set order header (Nº Name Surname)
        String header = "Nº " + order.getNo() + " " + order.getAd() + " " + order.getSoyad();
        tvOrderHeader.setText(header);

        // Set date
        tvDate.setText("Date: " + order.getSifarisTarixi());

        // Set address
        tvAddress.setText("Address: " + order.getUnvan());

        // Set phone
        tvPhone.setText("Phone: " + order.getElaqeNomresi());

        // Set product
        tvProduct.setText("Product: " + order.getMehsulNovu());

        // Set quantity
        tvQuantity.setText("Quantity: " + order.getMehsulSayi());

        // Set packing
        tvPacking.setText("Packing: " + order.getPacking());

        // Set time
        tvTime.setText("Time: " + order.getSaat());

        // Set status text and icon based on status
        String status = order.getSifarisStatusu();
        tvStatus.setText(getEnglishStatus(status));
        
        int iconRes = 0;
        if (status.equalsIgnoreCase("sifariş alındı")) {
            iconRes = R.drawable.accepted;
            addButton(buttonContainer, "Deliver", COLOR_GREEN, "çatdırılır");
        } else if (status.equalsIgnoreCase("çatdırıldı")) {
            iconRes = R.drawable.done;
            addButton(buttonContainer, "Edit", COLOR_RED, "sifariş alındı");
        } else if (status.equalsIgnoreCase("ertələndi")) {
            iconRes = R.drawable.pending;
            addButton(buttonContainer, "Deliver", COLOR_GREEN, "çatdırılır");
            addButton(buttonContainer, "Edit", COLOR_RED, "sifariş alındı");
        } else if (status.equalsIgnoreCase("ləğv olundu")) {
            iconRes = android.R.drawable.ic_menu_close_clear_cancel;
            addButton(buttonContainer, "Edit", COLOR_RED, "sifariş alındı");
        } else if (status.equalsIgnoreCase("çatdırılır")) {
            iconRes = R.drawable.ontheway;
            addButton(buttonContainer, "Delivered", COLOR_GREEN, "çatdırıldı");
            addButton(buttonContainer, "Cancelled", COLOR_GRAY, "ləğv olundu");
            addButton(buttonContainer, "Postponed", COLOR_BLUE, "ertələndi");
        }

        if (iconRes != 0) {
            ivStatusIcon.setImageResource(iconRes);
            ivStatusIcon.setColorFilter(Color.GRAY);
        }
    }

    private void addButton(LinearLayout container, String text, String color, String newStatus) {
        MaterialButton button = new MaterialButton(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMarginEnd(8); // Add margin between buttons
        button.setLayoutParams(params);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(Color.parseColor(color));
        button.setCornerRadius(8);
        button.setElevation(4);
        
        button.setOnClickListener(v -> showConfirmationDialog(text, newStatus));
        
        container.addView(button);
    }

    private void showConfirmationDialog(String action, String newStatus) {
        String message = "";
        final boolean showNoteArea;

        switch (action) {
            case "Deliver":
                message = "Start delivery";
                showNoteArea = false;
                break;
            case "Cancelled":
                message = "Cancel the order";
                showNoteArea = true;
                break;
            case "Edit":
                showStatusSelectionDialog();
                return;
            case "Delivered":
                message = "Order delivered";
                showNoteArea = true;
                break;
            case "Postponed":
                message = "Order postponed";
                showNoteArea = true;
                break;
            default:
                showNoteArea = true;
                break;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirmation, null);
        final EditText noteInput = dialogView.findViewById(R.id.noteInput);
        TextView messageText = dialogView.findViewById(R.id.messageText);
        noteInput.setVisibility(showNoteArea ? View.VISIBLE : View.GONE);
        messageText.setText(message);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                .setView(dialogView)
                .setPositiveButton("Yes", (dialog, which) -> {
                    String note = showNoteArea ? noteInput.getText().toString() : "";
                    updateOrderStatus(newStatus, note);
                })
                .setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            
            // Set button text colors
            positiveButton.setTextColor(getResources().getColor(R.color.primary));
            negativeButton.setTextColor(getResources().getColor(R.color.text_secondary));
            
            // Set button background
            positiveButton.setBackgroundResource(R.drawable.button_background);
            negativeButton.setBackgroundResource(R.drawable.button_background);
            
            // Set button padding
            int padding = getResources().getDimensionPixelSize(R.dimen.button_padding);
            positiveButton.setPadding(padding, padding, padding, padding);
            negativeButton.setPadding(padding, padding, padding, padding);
        });
        
        dialog.show();
    }

    private void showStatusSelectionDialog() {
        String[] statuses;
        String currentStatus = order.getSifarisStatusu();

        if (currentStatus.equalsIgnoreCase("çatdırıldı")) {
            statuses = new String[]{"çatdırılır", "ertələndi", "ləğv olundu"};
        } else if (currentStatus.equalsIgnoreCase("ləğv olundu")) {
            statuses = new String[]{"çatdırılır", "ertələndi", "ləğv olundu", "sifariş alındı"};
        } else if (currentStatus.equalsIgnoreCase("ertələndi")) {
            statuses = new String[]{"çatdırılır", "ertələndi", "ləğv olundu", "sifariş alındı"};
        } else {
            return;
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change Status")
                .setItems(statuses, (dialog, which) -> {
                    String selectedStatus = statuses[which];
                    showConfirmationDialog("Status dəyişikliyi", selectedStatus);
                })
                .show();
    }

    private void updateOrderStatus(String newStatus, String note) {
        new Thread(() -> {
            try {
                Sheets service = new Sheets.Builder(
                        com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                        com.google.api.client.json.gson.GsonFactory.getDefaultInstance(),
                        credential
                ).setApplicationName("LocalSheetApp").build();

                // First, get the current data to find the row
                ValueRange response = service.spreadsheets().values()
                        .get(getSpreadsheetId(requireContext()), RANGE)
                        .execute();

                List<List<Object>> values = response.getValues();
                if (values != null && values.size() > 1) {
                    // Find the row with matching order number
                    int rowIndex = -1;
                    String previousStatus = "";
                    for (int i = 1; i < values.size(); i++) {
                        List<Object> row = values.get(i);
                        if (row.size() > 0 && row.get(0).toString().equals(order.getNo())) {
                            rowIndex = i + 1; // +1 because sheets is 1-indexed
                            previousStatus = row.size() > 11 ? row.get(11).toString() : "";
                            break;
                        }
                    }

                    if (rowIndex != -1) {
                        // Update the status in column L (index 11)
                        List<List<Object>> updateValues = new ArrayList<>();
                        List<Object> row = new ArrayList<>();
                        row.add(newStatus);
                        updateValues.add(row);

                        ValueRange body = new ValueRange()
                                .setValues(updateValues);

                        String updateRange = "Kuryer / Çatdırılma!L" + rowIndex;
                        service.spreadsheets().values()
                                .update(getSpreadsheetId(requireContext()), updateRange, body)
                                .setValueInputOption("RAW")
                                .execute();

                        // Create order details string
                        String orderDetails = order.getAd() + " " + order.getSoyad() + " " + 
                                           order.getMehsulNovu() + " " + order.getMehsulSayi();

                        // Save status change to history sheet
                        List<List<Object>> historyRow = new ArrayList<>();
                        List<Object> historyData = new ArrayList<>();
                        historyData.add(order.getNo()); // Order Number
                        historyData.add(orderDetails); // Order Details
                        historyData.add(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())); // Timestamp
                        historyData.add(previousStatus); // Previous Status
                        historyData.add(newStatus); // New Status
                        historyData.add(note); // Comment/Note
                        historyData.add(credential.getSelectedAccountName()); // Updated By
                        historyRow.add(historyData);

                        ValueRange historyBody = new ValueRange()
                                .setValues(historyRow);

                        // Append to the history sheet
                        service.spreadsheets().values()
                            .append(getSpreadsheetId(requireContext()), "Order Status History!A:G", historyBody)
                            .setValueInputOption("RAW")
                            .execute();

                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Status updated successfully", Toast.LENGTH_SHORT).show();
                            
                            // Update the order status in memory
                            order.setSifarisStatusu(newStatus);
                            
                            // Find the previous fragment (list fragment)
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            Fragment previousFragment = fragmentManager.getFragments().get(0);
                            
                            // Go back to the list
                            requireActivity().onBackPressed();
                            
                            // If we're in a list fragment, refresh it
                            if (previousFragment instanceof DataFragment) {
                                ((DataFragment) previousFragment).refreshData();
                            } else if (previousFragment instanceof DeliveryFragment) {
                                ((DeliveryFragment) previousFragment).fetchSheetData();
                            } else if (previousFragment instanceof CourierFragment) {
                                ((CourierFragment) previousFragment).fetchSheetData();
                            } else if (previousFragment instanceof ControllerDeliveryFragment) {
                                ((ControllerDeliveryFragment) previousFragment).fetchSheetData();
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Error updating status: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private String getEnglishStatus(String azStatus) {
        if (azStatus == null) return "";
        switch (azStatus.toLowerCase()) {
            case "çatdırıldı": return "Delivered";
            case "çatdırılır": return "Delivering";
            case "ləğv olundu": return "Cancelled";
            case "ertələndi": return "Postponed";
            case "sifariş alındı": return "Order Received";
            default: return azStatus;
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