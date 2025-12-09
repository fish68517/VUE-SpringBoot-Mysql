package com.archive.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.archive.app.ApiClient;
import com.archive.app.ApiService;
import com.archive.app.model.TaskFocus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskEditorViewModel extends ViewModel {

    private final MutableLiveData<Boolean> saveResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<Boolean> getSaveResult() { return saveResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    // 保存任务（新增或更新）
    public void saveTask(TaskFocus task, boolean isEditMode) {
        isLoading.setValue(true);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<Boolean> call;
        if (isEditMode) {
            call = apiService.updateTaskFocus(task);
        } else {
            call = apiService.createTaskFocus(task);
        }

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body()) {
                    saveResult.setValue(true);
                } else {
                    errorMessage.setValue("保存失败，请稍后重试");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("网络错误: " + t.getMessage());
            }
        });
    }
}