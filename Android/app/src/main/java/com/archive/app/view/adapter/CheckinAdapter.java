package com.archive.app.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.model.HabitCheckin;

import java.util.ArrayList;
import java.util.List;

public class CheckinAdapter extends RecyclerView.Adapter<CheckinAdapter.ViewHolder> {

    private List<HabitCheckin> list = new ArrayList<>();
    private OnActionClickListener listener;

    public interface OnActionClickListener {
        void onEdit(HabitCheckin checkin);
        void onDelete(HabitCheckin checkin);
    }

    public void setOnActionClickListener(OnActionClickListener listener) {
        this.listener = listener;
    }

    public void setList(List<HabitCheckin> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_checkin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HabitCheckin item = list.get(position);
        holder.date.setText(item.getCheckinDate());
        holder.note.setText(item.getCheckinNoteText() == null || item.getCheckinNoteText().isEmpty()
                ? "无备注" : item.getCheckinNoteText());

        // 状态显示
        if ("completed".equals(item.getCheckinStatusEnum())) {
            holder.status.setText("已完成");
            holder.status.setTextColor(0xFF67C23A); // Green
        } else {
            holder.status.setText("跳过");
            holder.status.setTextColor(0xFFE6A23C); // Orange
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, note, status;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_checkin_date);
            note = itemView.findViewById(R.id.tv_checkin_note);
            status = itemView.findViewById(R.id.tv_checkin_status);
            btnEdit = itemView.findViewById(R.id.btn_edit_checkin);
            btnDelete = itemView.findViewById(R.id.btn_delete_checkin);
        }
    }
}