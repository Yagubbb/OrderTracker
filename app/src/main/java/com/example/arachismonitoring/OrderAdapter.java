package com.example.arachismonitoring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import java.util.List;
import android.graphics.Color;
import android.widget.ImageView;
import com.google.android.material.card.MaterialCardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<DeliveryOrderRow> orderList;
    private Context context;
    private GoogleAccountCredential credential;

    public OrderAdapter(List<DeliveryOrderRow> orderList, GoogleAccountCredential credential) {
        this.orderList = orderList;
        this.credential = credential;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeliveryOrderRow deliveryOrder = orderList.get(position);
        holder.orderNo.setText(deliveryOrder.getNo());
        holder.orderDate.setText(deliveryOrder.getSifarisTarixi());
        holder.orderMonth.setText(deliveryOrder.getSifarisAyi());
        holder.orderName.setText(deliveryOrder.getAd());
        holder.orderSurname.setText(deliveryOrder.getSoyad());
        holder.orderPhone.setText(deliveryOrder.getElaqeNomresi());
        holder.orderProduct.setText(deliveryOrder.getMehsulNovu());
        holder.orderPacking.setText(deliveryOrder.getPacking());
        holder.orderQuantity.setText(deliveryOrder.getMehsulSayi());
        holder.orderAddress.setText(deliveryOrder.getUnvan());
        holder.orderTime.setText(deliveryOrder.getSaat());
        holder.orderStatus.setText(deliveryOrder.getSifarisStatusu());

        holder.itemView.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, OrderDetailsFragment.newInstance(deliveryOrder, credential))
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
        TextView orderNo, orderDate, orderMonth, orderName, orderSurname, orderPhone,
                orderProduct, orderPacking, orderQuantity, orderAddress, orderTime, orderStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNo = itemView.findViewById(R.id.orderNo);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderMonth = itemView.findViewById(R.id.orderMonth);
            orderName = itemView.findViewById(R.id.orderName);
            orderSurname = itemView.findViewById(R.id.orderSurname);
            orderPhone = itemView.findViewById(R.id.orderPhone);
            orderProduct = itemView.findViewById(R.id.orderProduct);
            orderPacking = itemView.findViewById(R.id.orderPacking);
            orderQuantity = itemView.findViewById(R.id.orderQuantity);
            orderAddress = itemView.findViewById(R.id.orderAddress);
            orderTime = itemView.findViewById(R.id.orderTime);
            orderStatus = itemView.findViewById(R.id.orderStatus);
        }
    }
}
