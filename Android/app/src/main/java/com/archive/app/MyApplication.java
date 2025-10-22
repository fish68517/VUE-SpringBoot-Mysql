package com.archive.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.archive.app.model.Users;


/**
 * Application class to hold global state like current user.
 */
public class MyApplication extends Application {

    private static Users currentUser;
    private static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("warehouse_prefs", Context.MODE_PRIVATE);
        loadUserFromPrefs();
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
