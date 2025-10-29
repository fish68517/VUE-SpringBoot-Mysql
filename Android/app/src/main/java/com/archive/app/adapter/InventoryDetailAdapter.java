package com.archive.app.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.model.Inventory;

import java.util.ArrayList;
import java.util.List;

public class InventoryDetailAdapter extends RecyclerView.Adapter<InventoryDetailAdapter.ViewHolder> {

    private List<Inventory> items = new ArrayList<>();

    public void setItems(List<Inventory> items) {
        Log.d("InventoryDetailAdapter", "setItems called with " + (items != null ? items.size() : 0) + " items.");
        this.items = (items != null) ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_inventory_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvBatchCode;
        private final TextView tvProductSku;
        private final TextView tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBatchCode = itemView.findViewById(R.id.tv_batch_code);
            tvProductSku = itemView.findViewById(R.id.tv_product_sku);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
        }

        void bind(Inventory inventory) {
            tvBatchCode.setText(inventory.getBatchCode());
            // 假设 productId 就是 SKU，实际中可能需要再查表获取 SKU 字符串
            tvProductSku.setText("产品ID: " + inventory.getProductId());
            tvQuantity.setText(String.valueOf(inventory.getQuantity()));
        }
    }
}