package com.archive.app;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "AppSession";
    private static final String KEY_USER_ID = "user_id";
    private static final int PRIVATE_MODE = 0;

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context _context;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * 在用户成功登录后调用此方法
     * @param userId 要保存的用户ID
     */
    public void saveUserId(int userId) {
        editor.putInt(KEY_USER_ID, userId);
        editor.commit();
    }

    /**
     * 获取已保存的用户ID
     * @return 返回用户ID，如果未找到则返回 -1
     */
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1); // -1 表示未登录或无效用户
    }

    /**
     * 检查用户是否已登录
     * @return 如果userId存在则为true
     */
    public boolean isLoggedIn() {
        return getUserId() != -1;
    }

    /**
     * 清除会话信息 (用于退出登录)
     */
    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}