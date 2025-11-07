package com.archive.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.archive.app.model.Departments;
import com.archive.app.model.Users;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Application class to hold global state like current user.
 */
public class MyApplication extends Application {

    private static Users currentUser;
    private static SharedPreferences sharedPreferences;
    public static List<Departments> departments;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("warehouse_prefs", Context.MODE_PRIVATE);
        loadUserFromPrefs();
        ApiClient.getApiService().listDepartments().enqueue(new Callback<List<Departments>>() {

            @Override
            public void onResponse(Call<List<Departments>> call, Response<List<Departments>> response) {
                if (response.isSuccessful()){
                    departments = response.body();
                    Log.e("departments: ", response.body().toString());

                }
            }

            @Override
            public void onFailure(Call<List<Departments>> call, Throwable t) {

            }
        });
    }

    public static Users getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(Users user) {
        currentUser = user;
        saveUserToPrefs(user);
    }

    public static void logout() {
        currentUser = null;
        clearUserPrefs();
    }

    public static Integer getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }

    private static void saveUserToPrefs(Users user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (user != null) {
            editor.putInt("user_id", user.getId());
            editor.putString("user_username", user.getUsername());
            editor.putString("user_fullname", user.getFullName());
            // Add other relevant fields if needed (e.g., roleId)
            editor.putInt("user_role_id", user.getRoleId());
        } else {
            editor.clear();
        }
        editor.apply();
    }

    private void loadUserFromPrefs() {
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId != -1) {
            currentUser = new Users();
            currentUser.setId(userId);
            currentUser.setUsername(sharedPreferences.getString("user_username", null));
            currentUser.setFullName(sharedPreferences.getString("user_fullname", null));
            currentUser.setRoleId(sharedPreferences.getInt("user_role_id", -1)); // Load roleId
        } else {
            currentUser = null;
        }
    }

    private static void clearUserPrefs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
