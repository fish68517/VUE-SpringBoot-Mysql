package com.archive.app.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.model.LearnResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ResourceViewHolder> {

    private List<LearnResource> resources = new ArrayList<>();
    private Map<Long, String> categoryMap = new HashMap<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(LearnResource resource);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<LearnResource> resources, Map<Long, String> categoryMap) {
        this.resources = resources;
        if (categoryMap != null) {
            this.categoryMap = categoryMap;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_resource, parent, false);
        return new ResourceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResourceViewHolder holder, int position) {
        LearnResource item = resources.get(position);

        holder.title.setText(item.getResourceNameText());
        holder.desc.setText(item.getResourceDescriptionText());

        // 显示分类名称
        String catName = categoryMap.get(item.getResourceCategoryId());
        holder.category.setText(catName != null ? catName : "默认分类");

        // 显示推荐指数 (0-5)
        holder.ratingBar.setRating(item.getResourceRecommendLevel() != null ? item.getResourceRecommendLevel() : 3);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

    static class ResourceViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, category;
        RatingBar ratingBar;

        public ResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_resource_title);
            desc = itemView.findViewById(R.id.tv_resource_desc);
            category = itemView.findViewById(R.id.tv_resource_category);
            ratingBar = itemView.findViewById(R.id.rating_resource);
        }
    }
}