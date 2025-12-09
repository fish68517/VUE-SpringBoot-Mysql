package com.archive.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.archive.app.ApiClient;
import com.archive.app.ApiService;
import com.archive.app.model.LearnResource;
import com.archive.app.model.ResourceCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResourceViewModel extends ViewModel {

    // 我们可以创建一个包装类或者直接修改 LearnResource 模型添加 categoryName 字段
    // 这里为了简单，我们直接在 Adapter 里处理，或者传回一个包含 Map 的数据
    private final MutableLiveData<List<LearnResource>> resources = new MutableLiveData<>();
    private final MutableLiveData<Map<Long, String>> categoryMap = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<LearnResource>> getResources() { return resources; }
    public LiveData<Map<Long, String>> getCategoryMap() { return categoryMap; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public ResourceViewModel() {
        fetchData();
    }

    public void fetchData() {
        isLoading.setValue(true);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // 1. 获取所有资源分类
        apiService.getAllResourceCategories().enqueue(new Callback<List<ResourceCategory>>() {
            @Override
            public void onResponse(Call<List<ResourceCategory>> call, Response<List<ResourceCategory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<Long, String> map = new HashMap<>();
                    for (ResourceCategory cat : response.body()) {
                        map.put(cat.getResourceCategoryId(), cat.getCategoryNameText());
                    }
                    categoryMap.setValue(map);

                    // 分类获取成功后，再获取资源（或者并行获取，这里串行简单点）
                    fetchResources(apiService);
                } else {
                    isLoading.setValue(false);
                    errorMessage.setValue("加载分类失败");
                }
            }

            @Override
            public void onFailure(Call<List<ResourceCategory>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("网络错误: " + t.getMessage());
            }
        });
    }

    private void fetchResources(ApiService apiService) {
        apiService.getAllLearnResources().enqueue(new Callback<List<LearnResource>>() {
            @Override
            public void onResponse(Call<List<LearnResource>> call, Response<List<LearnResource>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    resources.setValue(response.body());
                } else {
                    errorMessage.setValue("加载资源失败");
                }
            }

            @Override
            public void onFailure(Call<List<LearnResource>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("网络错误: " + t.getMessage());
            }
        });
    }
}