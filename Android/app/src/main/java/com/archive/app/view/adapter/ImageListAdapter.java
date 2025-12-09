package com.archive.app.view.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImgViewHolder> {
    private List<String> urls;

    public ImageListAdapter(List<String> urls) {
        // 打印日志以确认接收到的URL列表
        System.out.println("ImageListAdapter: Received URLs: " + urls);
        this.urls = urls;

    }

    @NonNull
    @Override
    public ImgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView iv = new ImageView(parent.getContext());
        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        iv.setAdjustViewBounds(true); // 保持宽高比
        iv.setPadding(0, 0, 0, 16);   // 间距
        return new ImgViewHolder(iv);
    }

    @Override
    public void onBindViewHolder(@NonNull ImgViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(urls.get(position))
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into((ImageView) holder.itemView);
    }

    @Override
    public int getItemCount() { return urls.size(); }

    static class ImgViewHolder extends RecyclerView.ViewHolder {
        public ImgViewHolder(@NonNull android.view.View itemView) { super(itemView); }
    }
}