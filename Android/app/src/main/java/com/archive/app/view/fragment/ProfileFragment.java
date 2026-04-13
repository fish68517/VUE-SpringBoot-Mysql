package com.archive.app.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.model.LearnResource;
import com.archive.app.view.activity.ResourceDetailActivity;
import com.archive.app.view.adapter.ResourceAdapter;
import com.archive.app.viewmodel.ProfileViewModel;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private final Gson gson = new Gson();

    private ProfileViewModel profileViewModel;
    private ImageView avatarImageView;
    private TextView nicknameTextView;
    private TextView emailTextView;
    private TextView schoolIdTextView;
    private TextView collectCountTextView;
    private TextView emptyCollectionTextView;
    private ProgressBar progressBar;
    private View contentLayout;
    private ResourceAdapter resourceAdapter;
    private final List<LearnResource> currentCollections = new ArrayList<>();
    private Map<Long, String> currentCategoryMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        avatarImageView = view.findViewById(R.id.image_view_avatar);
        nicknameTextView = view.findViewById(R.id.text_view_nickname);
        emailTextView = view.findViewById(R.id.text_view_email);
        schoolIdTextView = view.findViewById(R.id.text_view_school_id);
        collectCountTextView = view.findViewById(R.id.text_view_collect_count);
        emptyCollectionTextView = view.findViewById(R.id.text_view_empty_collection);
        progressBar = view.findViewById(R.id.progress_bar_profile);
        contentLayout = view.findViewById(R.id.layout_content);

        RecyclerView collectRecyclerView = view.findViewById(R.id.recycler_view_my_collections);
        collectRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        collectRecyclerView.setNestedScrollingEnabled(false);
        resourceAdapter = new ResourceAdapter();
        resourceAdapter.setOnItemClickListener(resource -> {
            if (getContext() == null) {
                return;
            }
            Intent intent = new Intent(getContext(), ResourceDetailActivity.class);
            intent.putExtra("resource_json", gson.toJson(resource));
            startActivity(intent);
        });
        collectRecyclerView.setAdapter(resourceAdapter);

        observeViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (profileViewModel != null) {
            profileViewModel.refreshAll();
        }
    }

    private void observeViewModel() {
        profileViewModel.getUserProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                nicknameTextView.setText(user.getCampusNickname());
                emailTextView.setText("邮箱: " + user.getCampusEmailAddr());
                schoolIdTextView.setText("学号: " + user.getCampusSchoolId());
                Glide.with(this)
                        .load(user.getCampusAvatarUrl())
                        .placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .into(avatarImageView);
            }
        });

        profileViewModel.getCollectedResources().observe(getViewLifecycleOwner(), resources -> {
            currentCollections.clear();
            if (resources != null) {
                currentCollections.addAll(resources);
            }
            collectCountTextView.setText(String.valueOf(currentCollections.size()));
            emptyCollectionTextView.setVisibility(currentCollections.isEmpty() ? View.VISIBLE : View.GONE);
            resourceAdapter.setData(currentCollections, currentCategoryMap);
        });

        profileViewModel.getCategoryMap().observe(getViewLifecycleOwner(), map -> {
            currentCategoryMap = map == null ? new HashMap<>() : map;
            resourceAdapter.setData(currentCollections, currentCategoryMap);
        });

        profileViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                contentLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            }
        });

        profileViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
