package com.archive.app.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.model.TaskFocus;
import com.archive.app.view.activity.TaskEditorActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TaskFocus> tasks = new ArrayList<>();
    private Context context;


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

        // 格式化日期显示
        String deadline = "截止: " + (currentTask.getTaskDeadlineTimestamp() != null ? currentTask.getTaskDeadlineTimestamp().replace("T", " ") : "无");
        holder.deadline.setText(deadline);

        holder.status.setText(currentTask.getTaskStatusEnum());

        // 根据任务状态设置标签颜色
        GradientDrawable background = (GradientDrawable) holder.status.getBackground();
        switch (currentTask.getTaskStatusEnum()) {
            case "completed":
                background.setColor(Color.parseColor("#67C23A")); // 绿色
                break;
            case "in_progress":
                background.setColor(Color.parseColor("#409EFF")); // 蓝色
                break;
            case "pending":
                background.setColor(Color.parseColor("#E6A23C")); // 黄色
                break;
            default:
                background.setColor(Color.GRAY);
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TaskEditorActivity.class);
            // 传递对象需要序列化，这里简单用Gson转Json
            intent.putExtra("task_json", new Gson().toJson(currentTask));
            // FLAG_ACTIVITY_NEW_TASK
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<TaskFocus> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView description;
        private final TextView deadline;
        private final TextView status;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_view_task_title);
            description = itemView.findViewById(R.id.text_view_task_description);
            deadline = itemView.findViewById(R.id.text_view_task_deadline);
            status = itemView.findViewById(R.id.tag_task_status);
        }
    }
}