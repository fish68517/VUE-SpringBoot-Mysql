package com.archive.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.archive.app.ApiClient;
import com.archive.app.ApiService;
import com.archive.app.model.Achievement;
import com.archive.app.model.HabitCheckin;
import com.archive.app.model.UserAchieveRel;
import com.archive.app.util.CheckinStatusHelper;
import com.archive.app.util.SessionUserHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HabitDetailViewModel extends ViewModel {

    private static final int AUTO_BADGE_TARGET_DAYS = 5;
    private static final String AUTO_BADGE_CONDITION_TEXT = "\u4e60\u60ef\u6253\u5361\u6ee15\u5929\u81ea\u52a8\u83b7\u5f97";

    private final MutableLiveData<List<HabitCheckin>> checkinList = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public LiveData<List<HabitCheckin>> getCheckinList() {
        return checkinList;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void fetchCheckins(Long habitId) {
        fetchCheckinsInternal(habitId, null);
    }

    public void createCheckin(HabitCheckin checkin) {
        performSaveAction(
                ApiClient.getClient().create(ApiService.class).createHabitCheckin(checkin),
                "\u6253\u5361\u6210\u529f",
                checkin.getHabitTrackId()
        );
    }

    public void updateCheckin(HabitCheckin checkin) {
        performSaveAction(
                ApiClient.getClient().create(ApiService.class).updateHabitCheckin(checkin),
                "\u66f4\u65b0\u6210\u529f",
                checkin.getHabitTrackId()
        );
    }

    public void deleteCheckin(Long checkinId, Long habitId) {
        performDeleteAction(
                ApiClient.getClient().create(ApiService.class).deleteHabitCheckin(checkinId),
                "\u5220\u9664\u6210\u529f",
                habitId
        );
    }

    private void fetchCheckinsInternal(Long habitId, Runnable onLoaded) {
        isLoading.setValue(true);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllHabitCheckins().enqueue(new Callback<List<HabitCheckin>>() {
            @Override
            public void onResponse(Call<List<HabitCheckin>> call, Response<List<HabitCheckin>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HabitCheckin> filtered = response.body().stream()
                            .filter(checkin -> habitId.equals(checkin.getHabitTrackId()))
                            .sorted((left, right) -> right.getCheckinDate().compareTo(left.getCheckinDate()))
                            .collect(Collectors.toList());
                    checkinList.setValue(filtered);
                    if (onLoaded != null) {
                        onLoaded.run();
                        return;
                    }
                } else {
                    toastMessage.setValue("\u52a0\u8f7d\u6253\u5361\u8bb0\u5f55\u5931\u8d25");
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<List<HabitCheckin>> call, Throwable t) {
                toastMessage.setValue("\u7f51\u7edc\u9519\u8bef: " + t.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    private void performSaveAction(Call<Boolean> call, String successMsg, Long habitIdToRefresh) {
        isLoading.setValue(true);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    fetchCheckinsInternal(habitIdToRefresh, () -> autoGrantBadgeIfNeeded(successMsg));
                } else {
                    toastMessage.setValue("\u64cd\u4f5c\u5931\u8d25");
                    isLoading.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                toastMessage.setValue("\u7f51\u7edc\u9519\u8bef");
                isLoading.setValue(false);
            }
        });
    }

    private void performDeleteAction(Call<Boolean> call, String successMsg, Long habitIdToRefresh) {
        isLoading.setValue(true);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    toastMessage.setValue(successMsg);
                    fetchCheckinsInternal(habitIdToRefresh, null);
                } else {
                    toastMessage.setValue("\u64cd\u4f5c\u5931\u8d25");
                    isLoading.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                toastMessage.setValue("\u7f51\u7edc\u9519\u8bef");
                isLoading.setValue(false);
            }
        });
    }

    private void autoGrantBadgeIfNeeded(String successMsg) {
        List<HabitCheckin> currentCheckins = checkinList.getValue();
        if (!hasReachedAutoBadgeTarget(currentCheckins)) {
            finishAction(successMsg);
            return;
        }

        long currentUserId = SessionUserHelper.getCurrentUserId();
        if (currentUserId <= 0L) {
            finishAction(successMsg);
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllAchievements().enqueue(new Callback<List<Achievement>>() {
            @Override
            public void onResponse(Call<List<Achievement>> call, Response<List<Achievement>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    finishAction(successMsg);
                    return;
                }

                Achievement targetAchievement = selectAutoGrantAchievement(response.body());
                if (targetAchievement == null || targetAchievement.getAchievementId() == null) {
                    finishAction(successMsg);
                    return;
                }

                ensureBadgeRelation(currentUserId, targetAchievement, successMsg, apiService);
            }

            @Override
            public void onFailure(Call<List<Achievement>> call, Throwable t) {
                finishAction(successMsg);
            }
        });
    }

    private void ensureBadgeRelation(long currentUserId, Achievement achievement, String successMsg, ApiService apiService) {
        apiService.getAllUserAchieveRels().enqueue(new Callback<List<UserAchieveRel>>() {
            @Override
            public void onResponse(Call<List<UserAchieveRel>> call, Response<List<UserAchieveRel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (UserAchieveRel relation : response.body()) {
                        if (relation != null
                                && relation.getAchievementId() != null
                                && relation.getCampusUserId() != null
                                && relation.getAchievementId().equals(achievement.getAchievementId())
                                && relation.getCampusUserId().equals(currentUserId)) {
                            finishAction(successMsg);
                            return;
                        }
                    }
                }
                createBadgeRelation(currentUserId, achievement, successMsg, apiService);
            }

            @Override
            public void onFailure(Call<List<UserAchieveRel>> call, Throwable t) {
                finishAction(successMsg);
            }
        });
    }

    private void createBadgeRelation(long currentUserId, Achievement achievement, String successMsg, ApiService apiService) {
        UserAchieveRel relation = new UserAchieveRel();
        relation.setCampusUserId(currentUserId);
        relation.setAchievementId(achievement.getAchievementId());
        relation.setAchieveTimestamp(timestampFormat.format(new Date()));
        relation.setAchieveConditionText(AUTO_BADGE_CONDITION_TEXT);

        apiService.createUserAchieveRel(relation).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    finishAction(successMsg + "\uff0c\u5df2\u81ea\u52a8\u83b7\u5f97\u6210\u5c31\u5fbd\u7ae0");
                } else {
                    finishAction(successMsg);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                finishAction(successMsg);
            }
        });
    }

    private boolean hasReachedAutoBadgeTarget(List<HabitCheckin> checkins) {
        if (checkins == null || checkins.isEmpty()) {
            return false;
        }

        int completedDays = 0;
        int maxStreak = 0;
        for (HabitCheckin checkin : checkins) {
            if (checkin == null) {
                continue;
            }
            if (CheckinStatusHelper.STATUS_COMPLETED.equals(CheckinStatusHelper.normalize(checkin.getCheckinStatusEnum()))) {
                completedDays++;
            }
            if (checkin.getCheckinStreakCount() != null) {
                maxStreak = Math.max(maxStreak, checkin.getCheckinStreakCount());
            }
        }
        return completedDays >= AUTO_BADGE_TARGET_DAYS || maxStreak >= AUTO_BADGE_TARGET_DAYS;
    }

    private Achievement selectAutoGrantAchievement(List<Achievement> achievements) {
        List<Achievement> activeAchievements = new ArrayList<>();
        for (Achievement achievement : achievements) {
            if (achievement != null
                    && achievement.getAchievementId() != null
                    && Boolean.TRUE.equals(achievement.getAchieveActiveFlag())) {
                activeAchievements.add(achievement);
            }
        }

        Achievement bestMatch = null;
        int bestScore = 0;
        for (Achievement achievement : activeAchievements) {
            int score = scoreAchievement(achievement);
            if (score > bestScore) {
                bestScore = score;
                bestMatch = achievement;
            }
        }

        if (bestMatch != null) {
            return bestMatch;
        }
        return activeAchievements.size() == 1 ? activeAchievements.get(0) : null;
    }

    private int scoreAchievement(Achievement achievement) {
        StringBuilder builder = new StringBuilder();
        appendText(builder, achievement.getAchieveNameText());
        appendText(builder, achievement.getAchieveDescriptionText());
        appendText(builder, achievement.getAchieveRuleText());
        appendText(builder, achievement.getAchieveTypeEnum());

        String text = builder.toString().toLowerCase(Locale.ROOT);
        int score = 0;
        if (text.contains("5") || text.contains("\u4e94")) {
            score += 5;
        }
        if (text.contains("\u6253\u5361") || text.contains("\u7b7e\u5230") || text.contains("\u4e60\u60ef") || text.contains("habit")) {
            score += 4;
        }
        if (text.contains("\u8fde\u7eed") || text.contains("\u575a\u6301") || text.contains("streak")) {
            score += 3;
        }
        if (text.contains("\u5929") || text.contains("day")) {
            score += 2;
        }
        return score;
    }

    private void appendText(StringBuilder builder, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(value.trim());
    }

    private void finishAction(String message) {
        toastMessage.setValue(message);
        isLoading.setValue(false);
    }
}
