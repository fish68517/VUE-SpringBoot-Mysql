package com.archive.app.util;

public final class HabitStatusHelper {

    public static final String STATUS_IN_PROGRESS = "in_progress";
    public static final String STATUS_PAUSED = "paused";
    public static final String STATUS_COMPLETED = "completed";

    private HabitStatusHelper() {
    }

    public static String normalize(String status) {
        if (STATUS_COMPLETED.equals(status)) {
            return STATUS_COMPLETED;
        }
        if (STATUS_PAUSED.equals(status)) {
            return STATUS_PAUSED;
        }
        return STATUS_IN_PROGRESS;
    }

    public static String getDisplayText(String status) {
        String normalized = normalize(status);
        if (STATUS_COMPLETED.equals(normalized)) {
            return "已完成";
        }
        if (STATUS_PAUSED.equals(normalized)) {
            return "已暂停";
        }
        return "进行中";
    }

    public static int getDisplayColor(String status) {
        String normalized = normalize(status);
        if (STATUS_COMPLETED.equals(normalized)) {
            return 0xFF67C23A;
        }
        if (STATUS_PAUSED.equals(normalized)) {
            return 0xFFF56C6C;
        }
        return 0xFF409EFF;
    }
}
