package com.archive.app.view.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.model.Achievement;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder> {

    private final List<Achievement> achievements = new ArrayList<>();

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_achievement, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Achievement currentAchievement = achievements.get(position);
        holder.name.setText(currentAchievement.getAchieveNameText());
        holder.description.setText(currentAchievement.getAchieveDescriptionText());

        String iconUrl = currentAchievement.getAchieveIconUrl();
        if (TextUtils.isEmpty(iconUrl)) {
            holder.icon.setImageResource(R.drawable.ic_default);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(iconUrl)
                    .placeholder(R.drawable.ic_default)
                    .error(R.drawable.ic_default)
                    .into(holder.icon);
        }

        holder.itemView.setAlpha(currentAchievement.isEarned() ? 1.0f : 0.5f);
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements.clear();
        if (achievements != null) {
            this.achievements.addAll(achievements);
        }
        notifyDataSetChanged();
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView name;
        private final TextView description;

        AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.image_view_ach_icon);
            name = itemView.findViewById(R.id.text_view_ach_name);
            description = itemView.findViewById(R.id.text_view_ach_description);
        }
    }
}
