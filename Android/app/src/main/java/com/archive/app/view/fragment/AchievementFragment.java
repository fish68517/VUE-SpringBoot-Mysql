package com.archive.app.view.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.view.activity.LoginActivity;
import com.archive.app.view.adapter.AchievementAdapter;
import com.archive.app.viewmodel.AchievementViewModel;

public class AchievementFragment extends Fragment {

    private AchievementViewModel achievementViewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private AchievementAdapter achievementAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achievement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        achievementViewModel = new ViewModelProvider(this).get(AchievementViewModel.class);
        recyclerView = view.findViewById(R.id.recycler_view_achievements);
        progressBar = view.findViewById(R.id.progress_bar_achievements);

        setupRecyclerView();
        observeViewModel();
        achievementViewModel.loadAchievementsForUser(resolveCurrentUserId());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (achievementViewModel != null) {
            achievementViewModel.loadAchievementsForUser(resolveCurrentUserId());
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        achievementAdapter = new AchievementAdapter();
        recyclerView.setAdapter(achievementAdapter);
    }

    private void observeViewModel() {
        achievementViewModel.getAllAchievements().observe(getViewLifecycleOwner(), achievements -> {
            if (achievements != null) {
                achievementAdapter.setAchievements(achievements);
            }
        });

        achievementViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            }
        });

        achievementViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Long resolveCurrentUserId() {
        if (MyApplication.curUser != null && MyApplication.curUser.getCampusUserId() != null) {
            return MyApplication.curUser.getCampusUserId();
        }
        Context context = getContext();
        if (context == null) {
            return -1L;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(LoginActivity.LOGIN_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(LoginActivity.KEY_CURRENT_USER_ID, -1L);
    }
}
