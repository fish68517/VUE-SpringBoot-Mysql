package com.archive.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.archive.app.ApiClient;
import com.archive.app.ApiService;
import com.archive.app.model.Achievement;
import com.archive.app.model.UserAchieveRel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AchievementViewModel extends ViewModel {

    private final MutableLiveData<List<Achievement>> earnedAchievements = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<Achievement>> getAllAchievements() {
        return earnedAchievements;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadAchievementsForUser(Long currentUserId) {
        if (currentUserId == null || currentUserId <= 0L) {
            earnedAchievements.setValue(new ArrayList<>());
            errorMessage.setValue("未获取到当前登录用户");
            return;
        }

        isLoading.setValue(true);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllUserAchieveRels().enqueue(new Callback<List<UserAchieveRel>>() {
            @Override
            public void onResponse(Call<List<UserAchieveRel>> call, Response<List<UserAchieveRel>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    errorMessage.setValue("加载用户成就关系失败");
                    earnedAchievements.setValue(new ArrayList<>());
                    isLoading.setValue(false);
                    return;
                }

                Set<Long> achievementIds = new LinkedHashSet<>();
                for (UserAchieveRel relation : response.body()) {
                    if (relation != null
                            && currentUserId.equals(relation.getCampusUserId())
                            && relation.getAchievementId() != null) {
                        achievementIds.add(relation.getAchievementId());
                    }
                }

                if (achievementIds.isEmpty()) {
                    earnedAchievements.setValue(new ArrayList<>());
                    isLoading.setValue(false);
                    return;
                }

                fetchAchievementDetails(new ArrayList<>(achievementIds), apiService);
            }

            @Override
            public void onFailure(Call<List<UserAchieveRel>> call, Throwable t) {
                errorMessage.setValue("网络错误: " + t.getMessage());
                earnedAchievements.setValue(new ArrayList<>());
                isLoading.setValue(false);
            }
        });
    }

    private void fetchAchievementDetails(List<Long> achievementIds, ApiService apiService) {
        List<Achievement> loadedAchievements = new ArrayList<>();
        final int totalCount = achievementIds.size();
        final int[] finishedCount = {0};

        for (Long achievementId : achievementIds) {
            apiService.getAchievementById(achievementId).enqueue(new Callback<Achievement>() {
                @Override
                public void onResponse(Call<Achievement> call, Response<Achievement> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Achievement achievement = response.body();
                        achievement.setEarned(true);
                        loadedAchievements.add(achievement);
                    }
                    onSingleRequestFinished();
                }

                @Override
                public void onFailure(Call<Achievement> call, Throwable t) {
                    onSingleRequestFinished();
                }

                private void onSingleRequestFinished() {
                    finishedCount[0]++;
                    if (finishedCount[0] == totalCount) {
                        loadedAchievements.sort((left, right) -> {
                            int leftIndex = achievementIds.indexOf(left.getAchievementId());
                            int rightIndex = achievementIds.indexOf(right.getAchievementId());
                            return Integer.compare(leftIndex, rightIndex);
                        });
                        earnedAchievements.setValue(loadedAchievements);
                        if (loadedAchievements.isEmpty()) {
                            errorMessage.setValue("未查询到成就详情数据");
                        }
                        isLoading.setValue(false);
                    }
                }
            });
        }
    }
}
