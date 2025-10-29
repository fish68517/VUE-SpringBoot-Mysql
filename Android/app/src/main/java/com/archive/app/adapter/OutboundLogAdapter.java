package com.archive.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.archive.app.R;
import com.archive.app.model.TransactionLogs;
import java.util.ArrayList;
import java.util.List;

public class OutboundLogAdapter extends RecyclerView.Adapter<OutboundLogAdapter.LogViewHolder> {

    private List<TransactionLogs> logs = new ArrayList<>();

    public void setLogs(List<TransactionLogs> newLogs) {
        this.logs = newLogs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_outbound_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        holder.bind(logs.get(position));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuantity, tvNotes, tvOperatorId, tvTimestamp;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuantity = itemView.findViewById(R.id.tv_quantity_change);
            tvNotes = itemView.findViewById(R.id.tv_notes);
            tvOperatorId = itemView.findViewById(R.id.tv_operator_id);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }

        void bind(TransactionLogs log) {
            tvQuantity.setText(String.valueOf(log.getQuantityChange()));
            tvNotes.setText("备注: " + log.getNotes());
            tvOperatorId.setText("操作员ID: " + log.getUserId());
            tvTimestamp.setText(log.getCreatedAt());
        }
    }
}