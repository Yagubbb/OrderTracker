package com.example.arachismonitoring;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import androidx.fragment.app.FragmentActivity;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import android.graphics.Color;
import android.widget.ImageView;
import com.google.android.material.card.MaterialCardView;

public class DeliveryOrderAdapter extends RecyclerView.Adapter<DeliveryOrderAdapter.ViewHolder> {
    private List<DeliveryOrderRow> orderList;
    private GoogleAccountCredential credential;

    public DeliveryOrderAdapter(List<DeliveryOrderRow> orderList, GoogleAccountCredential credential) {
        this.orderList = orderList;
        this.credential = credential;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeliveryOrderRow order = orderList.get(position);
        holder.bind(order);
        holder.itemView.setOnClickListener(v -> {
            if (v.getContext() instanceof FragmentActivity) {
                FragmentActivity activity = (FragmentActivity) v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, OrderDetailsFragment.newInstance(order, credential))
                        .addToBackStack("order_details")
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderHeader, orderDetails;
        ImageView orderStatusIcon;
        MaterialCardView cardRoot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderHeader = itemView.findViewById(R.id.orderHeader);
            orderDetails = itemView.findViewById(R.id.orderDetails);
            orderStatusIcon = itemView.findViewById(R.id.orderStatusIcon);
            cardRoot = (MaterialCardView) itemView;

            if (orderHeader == null) android.util.Log.e("Adapter", "orderHeader is null");
            if (orderDetails == null) android.util.Log.e("Adapter", "orderDetails is null");
            if (orderStatusIcon == null) android.util.Log.e("Adapter", "orderStatusIcon is null");
            if (cardRoot == null) android.util.Log.e("Adapter", "cardRoot is null");
        }

        public void bind(DeliveryOrderRow order) {
            // Header: Nº Name Surname - PaymentStatus
            String header = "Nº " + order.getNo() + " " + order.getAd() + " " + order.getSoyad();
            if (order.getPaymentStatus() != null && !order.getPaymentStatus().isEmpty()) {
                header += " - " + order.getPaymentStatus();
            }
            orderHeader.setText(header);

            // Details: Date, Address, Product x Qty
            String details = "Date: " + order.getSifarisTarixi()
                    + "\nAddress: " + order.getUnvan()
                    + "\n" + order.getMehsulNovu() + " x" + order.getMehsulSayi();
            orderDetails.setText(details);

            // Set card background color and icon based on status
            int color = Color.parseColor("#FFFFFF"); // Default white
            int iconRes = 0;
            boolean useWhiteIcon = true;
            String status = order.getSifarisStatusu();
            if (status.equalsIgnoreCase("sifariş alındı")) {
                color = Color.parseColor("#C93E43");
                iconRes = R.drawable.accepted;
            } else if (status.equalsIgnoreCase("çatdırıldı")) {
                color = Color.parseColor("#458651");
                iconRes = R.drawable.done;
            } else if (status.equalsIgnoreCase("ertələndi")) {
                color = Color.parseColor("#255765");
                iconRes = R.drawable.pending;
            } else if (status.equalsIgnoreCase("ləğv olundu")) {
                color = Color.parseColor("#A6A885");
                iconRes = android.R.drawable.ic_menu_close_clear_cancel;
                useWhiteIcon = false;
            } else if (status.equalsIgnoreCase("çatdırılır")) {
                color = Color.parseColor("#E0D44F");
                iconRes = R.drawable.ontheway;
            }
            cardRoot.setCardBackgroundColor(color);
            if (iconRes != 0) {
                orderStatusIcon.setImageResource(iconRes);
                if (useWhiteIcon) {
                    orderStatusIcon.setColorFilter(Color.WHITE);
                } else {
                    orderStatusIcon.clearColorFilter();
                }
            } else {
                orderStatusIcon.setImageDrawable(null);
            }
        }
    }
} 