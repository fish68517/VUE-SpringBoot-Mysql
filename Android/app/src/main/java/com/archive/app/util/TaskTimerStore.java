package com.archive.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public final class TaskTimerStore {

    private static final String PREFS_NAME = "task_timer_state";
    private static final String KEY_PREFIX = "task_";
    private static final Gson GSON = new Gson();

    private TaskTimerStore() {
    }

    public static void save(Context context, TimerSnapshot snapshot) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_PREFIX + snapshot.taskId, GSON.toJson(snapshot)).apply();
    }

    public static TimerSnapshot load(Context context, long taskId) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String raw = preferences.getString(KEY_PREFIX + taskId, null);
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        try {
            return GSON.fromJson(raw, TimerSnapshot.class);
        } catch (Exception exception) {
            clear(context, taskId);
            return null;
        }
    }

    public static void clear(Context context, long taskId) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(KEY_PREFIX + taskId).apply();
    }

    public static boolean hasSnapshot(Context context, long taskId) {
        return load(context, taskId) != null;
    }

    public static class TimerSnapshot {
        public long taskId;
        public String taskTitle;
        public int focusDurationMins;
        public int breakDurationMins;
        public long remainingMillis;
        public long stageDurationMillis;
        public boolean breakMode;
        public boolean running;
        public boolean taskStarted;
        public int pauseCount;
        public int completedCycles;
        public long elapsedFocusMillisInRound;
        public long focusSessionStartEpochMillis;
    }
}
