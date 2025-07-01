package com.example.arachismonitoring;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.example.arachismonitoring.ControllerDeliveryOrderRow;

public class ControllerOrderAdapter extends RecyclerView.Adapter<ControllerOrderAdapter.ViewHolder> {
    private List<ControllerDeliveryOrderRow> orderList;
    private GoogleAccountCredential credential;

    public ControllerOrderAdapter(List<ControllerDeliveryOrderRow> orderList, GoogleAccountCredential credential) {
        this.orderList = orderList;
        this.credential = credential;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_controller_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ControllerDeliveryOrderRow order = orderList.get(position);
        holder.bind(order);
        holder.itemView.setOnClickListener(v -> {
            if (v.getContext() instanceof FragmentActivity) {
                FragmentActivity activity = (FragmentActivity) v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, ControllerOrderDetailsFragment.newInstance(order, credential))
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
            orderStatusIcon = itemView.findViewById(R.id.orderStatusIcon);
            orderDetails = itemView.findViewById(R.id.orderDetails);
            cardRoot = (MaterialCardView) itemView;
        }

        public void bind(ControllerDeliveryOrderRow order) {
            // Header: Order No + Name + Surname
            orderHeader.setText(order.getNo() + ". " + order.getAd() + " " + order.getSoyad());
            // Details: Date, Address, Product x Qty
            String details = "Tarix: " + order.getSifarisTarixi() +
                    "\nÜnvan: " + order.getUnvan() +
                    "\nMəhsul: " + order.getMehsulNovu() + " x" + order.getMehsulSayi();
            orderDetails.setText(details);
            // Status icon and color
            String status = order.getSifarisStatusu();
            int iconRes = 0;
            int color = android.graphics.Color.WHITE;
            if (status.equalsIgnoreCase("sifariş alındı")) {
                iconRes = R.drawable.accepted;
                color = android.graphics.Color.parseColor("#C93E43");
            } else if (status.equalsIgnoreCase("çatdırıldı")) {
                iconRes = R.drawable.done;
                color = android.graphics.Color.parseColor("#458651");
            } else if (status.equalsIgnoreCase("ertələndi")) {
                iconRes = R.drawable.pending;
                color = android.graphics.Color.parseColor("#255765");
            } else if (status.equalsIgnoreCase("ləğv olundu")) {
                iconRes = android.R.drawable.ic_menu_close_clear_cancel;
                color = android.graphics.Color.parseColor("#A6A885");
            } else if (status.equalsIgnoreCase("çatdırılır")) {
                iconRes = R.drawable.ontheway;
                color = android.graphics.Color.parseColor("#E0D44F");
            }
            cardRoot.setCardBackgroundColor(color);
            if (iconRes != 0) {
                orderStatusIcon.setImageResource(iconRes);
                orderStatusIcon.setColorFilter(android.graphics.Color.WHITE);
            } else {
                orderStatusIcon.setImageDrawable(null);
            }
        }
    }
} 