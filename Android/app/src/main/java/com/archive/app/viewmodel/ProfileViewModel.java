package com.archive.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.archive.app.ApiClient;
import com.archive.app.ApiService;
import com.archive.app.model.CampusUser;
import com.archive.app.model.LearnResource;
import com.archive.app.model.ResourceCategory;
import com.archive.app.model.UserResourceAction;
import com.archive.app.util.SessionUserHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<CampusUser> userProfile = new MutableLiveData<>();
    private final MutableLiveData<List<LearnResource>> collectedResources = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Map<Long, String>> categoryMap = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<CampusUser> getUserProfile() {
        return userProfile;
    }

    public LiveData<List<LearnResource>> getCollectedResources() {
        return collectedResources;
    }

    public LiveData<Map<Long, String>> getCategoryMap() {
        return categoryMap;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public ProfileViewModel() {
        refreshAll();
    }

    public void refreshAll() {
        fetchUserProfile();
        fetchCollectedResources();
    }

    private void fetchUserProfile() {
        long currentUserId = SessionUserHelper.getCurrentUserId();
        isLoading.setValue(true);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getCampusUserById(currentUserId).enqueue(new Callback<CampusUser>() {
            @Override
            public void onResponse(Call<CampusUser> call, Response<CampusUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userProfile.setValue(response.body());
                } else {
                    errorMessage.setValue("加载个人信息失败");
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<CampusUser> call, Throwable t) {
                errorMessage.setValue("网络错误: " + t.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    private void fetchCollectedResources() {
        long currentUserId = SessionUserHelper.getCurrentUserId();
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllResourceCategories().enqueue(new Callback<List<ResourceCategory>>() {
            @Override
            public void onResponse(Call<List<ResourceCategory>> call, Response<List<ResourceCategory>> categoryResponse) {
                Map<Long, String> categories = new HashMap<>();
                if (categoryResponse.isSuccessful() && categoryResponse.body() != null) {
                    for (ResourceCategory category : categoryResponse.body()) {
                        categories.put(category.getResourceCategoryId(), category.getCategoryNameText());
                    }
                }
                categoryMap.setValue(categories);
                fetchCollectionsAndResources(apiService, currentUserId);
            }

            @Override
            public void onFailure(Call<List<ResourceCategory>> call, Throwable t) {
                fetchCollectionsAndResources(apiService, currentUserId);
            }
        });
    }

    private void fetchCollectionsAndResources(ApiService apiService, long currentUserId) {
        apiService.getAllUserResourceActions().enqueue(new Callback<List<UserResourceAction>>() {
            @Override
            public void onResponse(Call<List<UserResourceAction>> call, Response<List<UserResourceAction>> actionResponse) {
                if (!actionResponse.isSuccessful() || actionResponse.body() == null) {
                    collectedResources.setValue(new ArrayList<>());
                    errorMessage.setValue("加载收藏记录失败");
                    return;
                }

                List<Long> collectedIds = new ArrayList<>();
                for (UserResourceAction action : actionResponse.body()) {
                    if (action != null
                            && action.getCampusUserId() != null
                            && action.getCampusUserId().equals(currentUserId)
                            && action.getIsCollected()
                            && action.getLearnResourceId() != null) {
                        collectedIds.add(action.getLearnResourceId());
                    }
                }

                if (collectedIds.isEmpty()) {
                    collectedResources.setValue(new ArrayList<>());
                    return;
                }

                apiService.getAllLearnResources().enqueue(new Callback<List<LearnResource>>() {
                    @Override
                    public void onResponse(Call<List<LearnResource>> call, Response<List<LearnResource>> resourceResponse) {
                        if (resourceResponse.isSuccessful() && resourceResponse.body() != null) {
                            List<LearnResource> result = new ArrayList<>();
                            for (LearnResource resource : resourceResponse.body()) {
                                if (collectedIds.contains(resource.getLearnResourceId())) {
                                    result.add(resource);
                                }
                            }
                            collectedResources.setValue(result);
                        } else {
                            collectedResources.setValue(new ArrayList<>());
                            errorMessage.setValue("加载收藏资源失败");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<LearnResource>> call, Throwable t) {
                        collectedResources.setValue(new ArrayList<>());
                        errorMessage.setValue("网络错误: " + t.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Call<List<UserResourceAction>> call, Throwable t) {
                collectedResources.setValue(new ArrayList<>());
                errorMessage.setValue("网络错误: " + t.getMessage());
            }
        });
    }
}
