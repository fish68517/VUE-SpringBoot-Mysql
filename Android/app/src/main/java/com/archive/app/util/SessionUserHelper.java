package com.archive.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.archive.app.MyApplication;

public final class SessionUserHelper {

    private static final String LOGIN_PREFS_NAME = "login_prefs";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";

    private SessionUserHelper() {
    }

    public static long getCurrentUserId() {
        if (MyApplication.curUser != null && MyApplication.curUser.getCampusUserId() != null) {
            return MyApplication.curUser.getCampusUserId();
        }
        Context context = MyApplication.context;
        if (context == null) {
            return 1L;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(KEY_CURRENT_USER_ID, 1L);
    }
}
