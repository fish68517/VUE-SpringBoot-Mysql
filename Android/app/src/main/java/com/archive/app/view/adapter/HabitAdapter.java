package com.archive.app.view.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.model.HabitTrack;

import java.util.ArrayList;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private List<HabitTrack> habits = new ArrayList<>();
    private OnItemClickListener listener; // 点击监听器接口

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    // 定义接口
    public interface OnItemClickListener {
        void onItemClick(HabitTrack habit);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        HabitTrack currentHabit = habits.get(position);
        holder.name.setText(currentHabit.getHabitNameText());
        holder.streak.setText("已连续打卡：" + currentHabit.getHabitStreakCount() + " 天");
        holder.total.setText("总计打卡：" + currentHabit.getHabitTotalCount() + " 次");

        try {
            holder.colorView.setBackgroundColor(Color.parseColor(currentHabit.getHabitColorHex()));
        } catch (Exception e) {
            holder.colorView.setBackgroundColor(Color.GRAY);
        }

        // 绑定点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentHabit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    public void setHabits(List<HabitTrack> habits) {
        this.habits = habits;
        notifyDataSetChanged();
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView streak;
        private final TextView total;
        private final View colorView;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_view_habit_name);
            streak = itemView.findViewById(R.id.text_view_habit_streak);
            total = itemView.findViewById(R.id.text_view_habit_total);
            colorView = itemView.findViewById(R.id.view_habit_color);
        }
    }
}