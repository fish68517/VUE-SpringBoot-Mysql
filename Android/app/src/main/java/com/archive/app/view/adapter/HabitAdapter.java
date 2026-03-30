package com.archive.app.view.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.model.HabitTrack;
import com.archive.app.util.HabitStatusHelper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    public interface OnHabitActionListener {
        void onOpenDetail(HabitTrack habit);
        void onChangeStatus(HabitTrack habit);
        void onViewStats(HabitTrack habit);
    }

    private final List<HabitTrack> habits = new ArrayList<>();
    private OnHabitActionListener listener;

    public void setOnHabitActionListener(OnHabitActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        HabitTrack currentHabit = habits.get(position);
        holder.name.setText(currentHabit.getHabitNameText());
        holder.frequency.setText(buildFrequencyText(currentHabit.getHabitFrequencyEnum()));
        holder.reminder.setText("提醒时间: " + formatReminder(currentHabit.getHabitReminderTime()));
        holder.streak.setText("连续打卡: " + safeInt(currentHabit.getHabitStreakCount()) + " 天");
        holder.total.setText("累计完成: " + safeInt(currentHabit.getHabitTotalCount()) + " 次");
        holder.recordHint.setText("点击卡片查看每日 / 每周签到记录");
        holder.status.setText(HabitStatusHelper.getDisplayText(currentHabit.getHabitStatusEnum()));

        Drawable statusBackground = DrawableCompat.wrap(holder.status.getBackground().mutate());
        DrawableCompat.setTint(statusBackground, HabitStatusHelper.getDisplayColor(currentHabit.getHabitStatusEnum()));
        holder.status.setBackground(statusBackground);

        try {
            holder.colorView.setBackgroundColor(Color.parseColor(currentHabit.getHabitColorHex()));
        } catch (Exception exception) {
            holder.colorView.setBackgroundColor(0xFF7E57C2);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOpenDetail(currentHabit);
            }
        });

        holder.btnStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChangeStatus(currentHabit);
            }
        });

        holder.btnStats.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewStats(currentHabit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    public void setHabits(List<HabitTrack> habits) {
        this.habits.clear();
        if (habits != null) {
            this.habits.addAll(habits);
        }
        notifyDataSetChanged();
    }

    private String buildFrequencyText(String frequency) {
        if ("weekly".equalsIgnoreCase(frequency)) {
            return "频率: 每周";
        }
        return "频率: 每日";
    }

    private String formatReminder(String reminderTime) {
        if (reminderTime == null || reminderTime.trim().isEmpty()) {
            return "未设置";
        }
        return reminderTime.length() >= 5 ? reminderTime.substring(0, 5) : reminderTime;
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView frequency;
        private final TextView reminder;
        private final TextView streak;
        private final TextView total;
        private final TextView recordHint;
        private final TextView status;
        private final View colorView;
        private final MaterialButton btnStatus;
        private final MaterialButton btnStats;

        HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_view_habit_name);
            frequency = itemView.findViewById(R.id.text_view_habit_frequency);
            reminder = itemView.findViewById(R.id.text_view_habit_reminder);
            streak = itemView.findViewById(R.id.text_view_habit_streak);
            total = itemView.findViewById(R.id.text_view_habit_total);
            recordHint = itemView.findViewById(R.id.text_view_habit_record_hint);
            status = itemView.findViewById(R.id.text_view_habit_status);
            colorView = itemView.findViewById(R.id.view_habit_color);
            btnStatus = itemView.findViewById(R.id.btn_habit_status);
            btnStats = itemView.findViewById(R.id.btn_habit_stats);
        }
    }
}
