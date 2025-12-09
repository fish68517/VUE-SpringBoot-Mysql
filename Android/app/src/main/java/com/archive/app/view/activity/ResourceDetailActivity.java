package com.archive.app.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.ApiClient;
import com.archive.app.ApiService;
import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.model.LearnResource;
import com.archive.app.model.ResourceDetail;
import com.archive.app.model.UserResourceAction;
import com.archive.app.view.adapter.ImageListAdapter;
import com.bumptech.glide.Glide;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResourceDetailActivity extends AppCompatActivity {

    private LearnResource basicResource;
    private ResourceDetail detailData;
    private ApiService apiService;
    private Long currentUserId = MyApplication.curUser.getCampusUserId(); // 模拟当前用户ID

    // UI Components
    private TextView tvTitle, tvContent, tvLikeCount;
    private FrameLayout layoutVideo;
    private VideoView videoView;
    private ImageView ivPlayBtn;
    private RecyclerView rvImages;
    private ImageView ivLikeIcon, ivCollectIcon;
    private boolean isLiked = false;
    private boolean isCollected = false;
    private int currentLikeCount = 0;

    // 资源基础 URL
    private static final String BASE_URL = ApiClient.BASE_URL; // Android 模拟器访问本地 SpringBoot 的地址，真机请换成 IP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_detail);

        apiService = ApiClient.getClient().create(ApiService.class);

        String json = getIntent().getStringExtra("resource_json");
        if (json != null) {
            basicResource = new Gson().fromJson(json, LearnResource.class);
        }

        initViews();
        loadData();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_resource_detail);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.tv_detail_title);
        tvContent = findViewById(R.id.tv_detail_content);
        layoutVideo = findViewById(R.id.layout_video_container);
        videoView = findViewById(R.id.video_view);
        ivPlayBtn = findViewById(R.id.iv_play_btn);
        rvImages = findViewById(R.id.rv_detail_images);
        rvImages.setLayoutManager(new LinearLayoutManager(this));

        tvLikeCount = findViewById(R.id.tv_like_count);
        ivLikeIcon = findViewById(R.id.iv_like_icon);
        ivCollectIcon = findViewById(R.id.iv_collect_icon);

        MaterialButton btnOpenLink = findViewById(R.id.btn_open_link);
        btnOpenLink.setOnClickListener(v -> {
            if (basicResource != null) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(basicResource.getResourceUrlText()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "无法打开链接", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 交互按钮点击事件
        findViewById(R.id.layout_action_like).setOnClickListener(v -> toggleAction("like"));
        findViewById(R.id.layout_action_collect).setOnClickListener(v -> toggleAction("collect"));

        // 初始化显示基础信息
        if (basicResource != null) {
            getSupportActionBar().setTitle(basicResource.getResourceNameText());
            tvTitle.setText(basicResource.getResourceNameText());
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(basicResource.getResourceDescriptionText());
        }
    }

    private void loadData() {
        if (basicResource == null) return;

        // 1. 获取资源详情内容
        apiService.getResourceDetail(basicResource.getLearnResourceId()).enqueue(new Callback<ResourceDetail>() {
            @Override
            public void onResponse(Call<ResourceDetail> call, Response<ResourceDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    detailData = response.body();
                    updateUiWithDetail();
                }
            }
            @Override
            public void onFailure(Call<ResourceDetail> call, Throwable t) {
                // 获取失败则保持显示基础信息
            }
        });

        // 2. 获取当前用户对该资源的交互状态 (点赞/收藏)
        apiService.getActionStatus(currentUserId, basicResource.getLearnResourceId()).enqueue(new Callback<UserResourceAction>() {
            @Override
            public void onResponse(Call<UserResourceAction> call, Response<UserResourceAction> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResourceAction action = response.body();
                    isLiked = action.getIsLiked();
                    isCollected = action.getIsCollected();
                    updateActionIcons();
                }
            }
            @Override
            public void onFailure(Call<UserResourceAction> call, Throwable t) {}
        });
    }

    private void updateUiWithDetail() {
        // 更新标题和点赞数
        if (!TextUtils.isEmpty(detailData.getTitle())) {
            tvTitle.setText(detailData.getTitle());
        }
        currentLikeCount = detailData.getLikeCount();
        tvLikeCount.setText(String.valueOf(currentLikeCount));

        String type = detailData.getContentType(); // article, video, image

        // 根据类型渲染内容
        if ("article".equals(type)) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(detailData.getContentText());
        }
        else if ("video".equals(type)) {
            tvContent.setText(detailData.getContentText());
            layoutVideo.setVisibility(View.VISIBLE);
            String videoUrl = BASE_URL + "/upload/video/" + detailData.getMediaFileNames();

            // 简单的视频播放控制
            ivPlayBtn.setOnClickListener(v -> {
                ivPlayBtn.setVisibility(View.GONE);
                videoView.setVideoURI(Uri.parse(videoUrl));
                videoView.setMediaController(new MediaController(this));
                videoView.start();
            });
        }
        else if ("image".equals(type)) {
            tvContent.setText(detailData.getContentText());
            rvImages.setVisibility(View.VISIBLE);

            // 解析图片列表 "img1.jpg,img2.jpg"
            if (detailData.getMediaFileNames() != null) {
                String[] files = detailData.getMediaFileNames().split(",");
                List<String> imageUrls = new ArrayList<>();
                for (String f : files) {
                    if(!f.trim().isEmpty()) imageUrls.add(BASE_URL + "upload/image/" + f.trim());
                }
                // 设置简单的图片 Adapter
                rvImages.setAdapter(new ImageListAdapter(imageUrls));
            }
        }
    }

    private void toggleAction(String type) {
        apiService.toggleResourceAction(currentUserId, basicResource.getLearnResourceId(), type).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    if ("like".equals(type)) {
                        isLiked = !isLiked;
                        currentLikeCount += (isLiked ? 1 : -1);
                        tvLikeCount.setText(String.valueOf(currentLikeCount));
                    } else {
                        isCollected = !isCollected;
                        Toast.makeText(ResourceDetailActivity.this, isCollected ? "已收藏" : "取消收藏", Toast.LENGTH_SHORT).show();
                    }
                    updateActionIcons();
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(ResourceDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateActionIcons() {
        ivLikeIcon.setImageResource(isLiked ? R.drawable.ic_like : R.drawable.ic_heart_outline);
        ivCollectIcon.setImageResource(isCollected ? R.drawable.ic_star : R.drawable.ic_star_outline);

        // 简单的颜色变化
        ivLikeIcon.setColorFilter(isLiked ? 0xFFF44336 : 0xFF757575); // Red / Gray
        ivCollectIcon.setColorFilter(isCollected ? 0xFFFFC107 : 0xFF757575); // Amber / Gray
    }
}