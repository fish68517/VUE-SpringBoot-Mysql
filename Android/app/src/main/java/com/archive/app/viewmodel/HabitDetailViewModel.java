package com.archive.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.archive.app.ApiClient;
import com.archive.app.ApiService;
import com.archive.app.model.HabitCheckin;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HabitDetailViewModel extends ViewModel {

    private final MutableLiveData<List<HabitCheckin>> checkinList = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<List<HabitCheckin>> getCheckinList() { return checkinList; }
    public LiveData<String> getToastMessage() { return toastMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    // 获取某个习惯的所有打卡记录
    public void fetchCheckins(Long habitId) {
        isLoading.setValue(true);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        // 注意：这里调用的是获取所有记录，然后在客户端过滤。
        // 如果后端有 getByHabitId 接口会更好，但为了兼容之前的通用接口，我们这样做：
        apiService.getAllHabitCheckins().enqueue(new Callback<List<HabitCheckin>>() {
            @Override
            public void onResponse(Call<List<HabitCheckin>> call, Response<List<HabitCheckin>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HabitCheckin> all = response.body();
                    // 过滤出当前习惯的记录，并按日期倒序排列
                    List<HabitCheckin> filtered = all.stream()
                            .filter(c -> habitId.equals(c.getHabitTrackId()))
                            .sorted((a, b) -> b.getCheckinDate().compareTo(a.getCheckinDate()))
                            .collect(Collectors.toList());
                    checkinList.setValue(filtered);
                } else {
                    toastMessage.setValue("加载记录失败");
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<List<HabitCheckin>> call, Throwable t) {
                toastMessage.setValue("网络错误: " + t.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    // 新增打卡
    public void createCheckin(HabitCheckin checkin) {
        performAction(ApiClient.getClient().create(ApiService.class).createHabitCheckin(checkin), "打卡成功", checkin.getHabitTrackId());
    }

    // 更新打卡
    public void updateCheckin(HabitCheckin checkin) {
        performAction(ApiClient.getClient().create(ApiService.class).updateHabitCheckin(checkin), "更新成功", checkin.getHabitTrackId());
    }

    // 删除打卡
    public void deleteCheckin(Long checkinId, Long habitId) {
        performAction(ApiClient.getClient().create(ApiService.class).deleteHabitCheckin(checkinId), "删除成功", habitId);
    }

    // 通用请求处理
    private void performAction(Call<Boolean> call, String successMsg, Long habitIdToRefresh) {
        isLoading.setValue(true);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null && response.body()) {
                    toastMessage.setValue(successMsg);
                    fetchCheckins(habitIdToRefresh); // 刷新列表
                } else {
                    toastMessage.setValue("操作失败");
                    isLoading.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                toastMessage.setValue("网络错误");
                isLoading.setValue(false);
            }
        });
    }
}