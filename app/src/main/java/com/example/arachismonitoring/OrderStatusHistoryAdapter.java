package com.example.arachismonitoring;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Comparator;

public class OrderStatusHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    
    private List<Object> items = new ArrayList<>();

    public OrderStatusHistoryAdapter(List<OrderStatusHistoryRow> data) {
        processData(data);
    }

    private void processData(List<OrderStatusHistoryRow> data) {
        // Group by order number
        Map<String, List<OrderStatusHistoryRow>> groupedData = new LinkedHashMap<>();
        for (OrderStatusHistoryRow row : data) {
            String orderNumber = row.getOrderNumber();
            if (!groupedData.containsKey(orderNumber)) {
                groupedData.put(orderNumber, new ArrayList<>());
            }
            groupedData.get(orderNumber).add(row);
        }

        // Sort each group by date/time (newest first)
        for (List<OrderStatusHistoryRow> group : groupedData.values()) {
            Collections.sort(group, new Comparator<OrderStatusHistoryRow>() {
                @Override
                public int compare(OrderStatusHistoryRow o1, OrderStatusHistoryRow o2) {
                    return o2.getDateTime().compareTo(o1.getDateTime());
                }
            });
        }

        // Sort order numbers by the newest status in each group (newest group first)
        List<Map.Entry<String, List<OrderStatusHistoryRow>>> sortedGroups = new ArrayList<>(groupedData.entrySet());
        Collections.sort(sortedGroups, new Comparator<Map.Entry<String, List<OrderStatusHistoryRow>>>() {
            @Override
            public int compare(Map.Entry<String, List<OrderStatusHistoryRow>> e1, Map.Entry<String, List<OrderStatusHistoryRow>> e2) {
                String date1 = e1.getValue().get(0).getDateTime();
                String date2 = e2.getValue().get(0).getDateTime();
                return date2.compareTo(date1);
            }
        });

        // Create items list with headers and items
        items.clear();
        for (Map.Entry<String, List<OrderStatusHistoryRow>> entry : sortedGroups) {
            items.add(entry.getKey()); // Header
            items.addAll(entry.getValue()); // Items
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_status_history_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_status_history, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            String orderNumber = (String) items.get(position);
            headerHolder.txtOrderNumber.setText("Order #" + orderNumber);
        } else if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            OrderStatusHistoryRow row = (OrderStatusHistoryRow) items.get(position);
            itemHolder.txtOrderDetails.setText(row.getOrderDetails());
            itemHolder.txtDateTime.setText(row.getDateTime());
            itemHolder.txtPrevStatus.setText("Previous: " + row.getPreviousStatus());
            itemHolder.txtNewStatus.setText("New: " + row.getNewStatus());
            itemHolder.txtNote.setText("Note: " + row.getNote());
            itemHolder.txtUser.setText("User: " + row.getUser());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderNumber;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderNumber = itemView.findViewById(R.id.txtOrderNumber);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderDetails, txtDateTime, txtPrevStatus, txtNewStatus, txtNote, txtUser;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderDetails = itemView.findViewById(R.id.txtOrderDetails);
            txtDateTime = itemView.findViewById(R.id.txtDateTime);
            txtPrevStatus = itemView.findViewById(R.id.txtPrevStatus);
            txtNewStatus = itemView.findViewById(R.id.txtNewStatus);
            txtNote = itemView.findViewById(R.id.txtNote);
            txtUser = itemView.findViewById(R.id.txtUser);
        }
    }
} 