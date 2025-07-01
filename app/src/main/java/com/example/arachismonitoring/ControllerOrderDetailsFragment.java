package com.example.arachismonitoring;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ControllerOrderDetailsFragment extends Fragment {
    private ControllerDeliveryOrderRow order;
    private GoogleAccountCredential credential;

    public static ControllerOrderDetailsFragment newInstance(ControllerDeliveryOrderRow order, GoogleAccountCredential credential) {
        ControllerOrderDetailsFragment fragment = new ControllerOrderDetailsFragment();
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
            order = (ControllerDeliveryOrderRow) getArguments().getSerializable("order");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controller_order_details, container, false);

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
        TextView tvPaymentStatus = view.findViewById(R.id.tvPaymentStatus);
        TextView tvOrderMonth = view.findViewById(R.id.tvOrderMonth);
        TextView tvOrderYear = view.findViewById(R.id.tvOrderYear);
        TextView tvSalesPrice = view.findViewById(R.id.tvSalesPrice);
        TextView tvProductCost = view.findViewById(R.id.tvProductCost);
        TextView tvNetProfit = view.findViewById(R.id.tvNetProfit);
        TextView tvNetProfitPercent = view.findViewById(R.id.tvNetProfitPercent);
        TextView tvAvgOrderValue = view.findViewById(R.id.tvAvgOrderValue);

        // Do NOT add any status change buttons for controller
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
        // Set payment status
        if (tvPaymentStatus != null && order.getPaymentStatus() != null && !order.getPaymentStatus().isEmpty()) {
            tvPaymentStatus.setText("Payment: " + order.getPaymentStatus());
        }
        // Set extra fields
        tvOrderMonth.setText("Order month: " + order.getSifarisAyi());
        tvOrderYear.setText("Order year: " + order.getSifarisIli());
        tvSalesPrice.setText("Sales Price: " + order.getSatisQiymeti());
        tvProductCost.setText("Product Cost: " + order.getMayaDeyeri());
        tvNetProfit.setText("Net Profit: " + order.getXalisGəlir());
        tvNetProfitPercent.setText("Net Profit (%): " + order.getXalisGəlirFaiz());
        tvAvgOrderValue.setText("Average Order Value: " + order.getOrtaSifarisDeyeri());

        // Set status text and icon based on status
        String status = order.getSifarisStatusu();
        tvStatus.setText(getEnglishStatus(status));
        int iconRes = 0;
        final String COLOR_RED = "#C93E43";
        final String COLOR_GREEN = "#458651";
        final String COLOR_BLUE = "#255765";
        final String COLOR_GRAY = "#A6A885";
        final String COLOR_YELLOW = "#E0D44F";
        if (status != null) {
            if (status.equalsIgnoreCase("sifariş alındı")) {
                iconRes = R.drawable.accepted;
            } else if (status.equalsIgnoreCase("çatdırıldı")) {
                iconRes = R.drawable.done;
            } else if (status.equalsIgnoreCase("ertələndi")) {
                iconRes = R.drawable.pending;
            } else if (status.equalsIgnoreCase("ləğv olundu")) {
                iconRes = android.R.drawable.ic_menu_close_clear_cancel;
            } else if (status.equalsIgnoreCase("çatdırılır")) {
                iconRes = R.drawable.ontheway;
            }
        }
        if (iconRes != 0) {
            ivStatusIcon.setImageResource(iconRes);
            ivStatusIcon.setColorFilter(android.graphics.Color.GRAY);
        }
        return view;
    }

    private void addButton(LinearLayout container, String text, String color, String newStatus) {
        com.google.android.material.button.MaterialButton button = new com.google.android.material.button.MaterialButton(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMarginEnd(8); // Add margin between buttons
        button.setLayoutParams(params);
        button.setText(text);
        button.setTextColor(android.graphics.Color.WHITE);
        button.setBackgroundColor(android.graphics.Color.parseColor(color));
        button.setCornerRadius(8);
        button.setElevation(4);
        button.setOnClickListener(v -> showConfirmationDialog(text, newStatus));
        container.addView(button);
    }

    private void showConfirmationDialog(String action, String newStatus) {
        String message = "";
        final boolean showNoteArea;

        switch (action) {
            case "Çatdır":
                message = "Çatdırılmaya başla";
                showNoteArea = false;
                break;
            case "Ləğv olundu":
                message = "Sifariş ləğv edilsin";
                showNoteArea = true;
                break;
            case "Düzəliş et":
                // Optionally, you can implement status selection dialog for controller as well
                showNoteArea = false;
                break;
            case "Çatdırıldı":
                message = "Sifariş çatdırıldı";
                showNoteArea = true;
                break;
            case "Ertələndi":
                message = "Sifariş ertələndi";
                showNoteArea = true;
                break;
            default:
                showNoteArea = true;
                break;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirmation, null);
        final EditText noteInput = dialogView.findViewById(R.id.noteInput);
        TextView messageText = dialogView.findViewById(R.id.messageText);
        noteInput.setVisibility(showNoteArea ? View.VISIBLE : View.GONE);
        messageText.setText(message);

        com.google.android.material.dialog.MaterialAlertDialogBuilder builder = new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                .setView(dialogView)
                .setPositiveButton("Bəli", (dialog, which) -> {
                    String note = showNoteArea ? noteInput.getText().toString() : "";
                    // Optionally, implement updateOrderStatus logic for controller
                })
                .setNegativeButton("Xeyr", null);

        builder.show();
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
} 