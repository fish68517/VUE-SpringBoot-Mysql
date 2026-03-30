package com.archive.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.archive.app.ApiClient;
import com.archive.app.ApiService;
import com.archive.app.model.HabitTrack;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HabitViewModel extends ViewModel {

    private final MutableLiveData<List<HabitTrack>> userHabits = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final Long currentUserId = 1L;

    public LiveData<List<HabitTrack>> getUserHabits() {
        return userHabits;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public HabitViewModel() {
        fetchUserHabits();
    }

    public void refreshHabits() {
        fetchUserHabits();
    }

    public void createHabit(HabitTrack habitTrack) {
        performHabitAction(
                ApiClient.getClient().create(ApiService.class).createHabitTrack(habitTrack),
                "习惯创建成功"
        );
    }

    public void updateHabit(HabitTrack habitTrack, String successMessage) {
        performHabitAction(
                ApiClient.getClient().create(ApiService.class).updateHabitTrack(habitTrack),
                successMessage
        );
    }

    private void fetchUserHabits() {
        isLoading.setValue(true);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<HabitTrack>> call = apiService.getAllHabitTracks();

        call.enqueue(new Callback<List<HabitTrack>>() {
            @Override
            public void onResponse(Call<List<HabitTrack>> call, Response<List<HabitTrack>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HabitTrack> filteredHabits = response.body().stream()
                            .filter(habit -> currentUserId.equals(habit.getCampusUserId()))
                            .collect(Collectors.toList());
                    userHabits.setValue(filteredHabits);
                } else {
                    errorMessage.setValue("加载习惯列表失败");
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<List<HabitTrack>> call, Throwable t) {
                errorMessage.setValue("网络错误: " + t.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    private void performHabitAction(Call<Boolean> call, String successMessage) {
        isLoading.setValue(true);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null && response.body()) {
                    toastMessage.setValue(successMessage);
                    fetchUserHabits();
                } else {
                    errorMessage.setValue("操作失败，请稍后重试");
                    isLoading.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                errorMessage.setValue("网络错误: " + t.getMessage());
                isLoading.setValue(false);
            }
        });
    }
}
