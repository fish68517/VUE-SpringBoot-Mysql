package com.archive.app.view.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.model.TaskFocus;
import com.archive.app.util.TaskStatusHelper;
import com.archive.app.util.TaskTimerStore;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskActionListener {
        void onEdit(TaskFocus task);
        void onTimer(TaskFocus task);
    }

    private final List<TaskFocus> tasks = new ArrayList<>();
    private Context context;
    private OnTaskActionListener listener;

    public void setOnTaskActionListener(OnTaskActionListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<TaskFocus> taskList, Context context) {
        this.tasks.clear();
        if (taskList != null) {
            this.tasks.addAll(taskList);
        }
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskFocus currentTask = tasks.get(position);
        holder.title.setText(currentTask.getTaskTitleText());
        holder.description.setText(currentTask.getTaskDescriptionText());
        holder.deadline.setText(buildDeadlineText(currentTask.getTaskDeadlineTimestamp()));
        holder.mode.setText(buildModeText(currentTask));
        holder.category.setText(currentTask.getTaskCategoryCode() == null || currentTask.getTaskCategoryCode().isEmpty()
                ? "未分类" : currentTask.getTaskCategoryCode());

        holder.status.setText(TaskStatusHelper.getDisplayText(currentTask.getTaskStatusEnum()));
        GradientDrawable background = (GradientDrawable) holder.status.getBackground();
        background.setColor(TaskStatusHelper.getColor(currentTask.getTaskStatusEnum()));

        boolean hasTimerSnapshot = context != null
                && currentTask.getTaskFocusId() != null
                && TaskTimerStore.hasSnapshot(context, currentTask.getTaskFocusId());
        holder.timerButton.setText(hasTimerSnapshot ? "继续专注" : "专注计时");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(currentTask);
            }
        });

        holder.timerButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTimer(currentTask);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private String buildDeadlineText(String deadline) {
        String display = deadline != null ? deadline.replace("T", " ") : "未设置";
        return "截止: " + display;
    }

    private String buildModeText(TaskFocus task) {
        int focus = task.getTaskFocusDurationMins() == null || task.getTaskFocusDurationMins() <= 0
                ? 30 : task.getTaskFocusDurationMins();
        int rest = task.getTaskBreakDurationMins() == null || task.getTaskBreakDurationMins() < 0
                ? 5 : task.getTaskBreakDurationMins();
        return "专注 " + focus + " 分钟 / 休息 " + rest + " 分钟";
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView description;
        private final TextView deadline;
        private final TextView status;
        private final TextView mode;
        private final TextView category;
        private final TextView timerButton;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_view_task_title);
            description = itemView.findViewById(R.id.text_view_task_description);
            deadline = itemView.findViewById(R.id.text_view_task_deadline);
            status = itemView.findViewById(R.id.tag_task_status);
            mode = itemView.findViewById(R.id.text_view_task_mode);
            category = itemView.findViewById(R.id.text_view_task_category);
            timerButton = itemView.findViewById(R.id.btn_task_timer);
        }
    }
}
