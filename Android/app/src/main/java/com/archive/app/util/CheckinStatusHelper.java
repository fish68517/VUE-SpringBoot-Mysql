package com.archive.app.util;

public final class CheckinStatusHelper {

    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_IN_PROGRESS = "in_progress";
    public static final String STATUS_INTERRUPTED = "interrupted";
    public static final String LEGACY_STATUS_SKIPPED = "skipped";

    private CheckinStatusHelper() {
    }

    public static String normalize(String status) {
        if (STATUS_COMPLETED.equals(status)) {
            return STATUS_COMPLETED;
        }
        if (STATUS_IN_PROGRESS.equals(status)) {
            return STATUS_IN_PROGRESS;
        }
        if (STATUS_INTERRUPTED.equals(status) || LEGACY_STATUS_SKIPPED.equals(status)) {
            return STATUS_INTERRUPTED;
        }
        return STATUS_IN_PROGRESS;
    }

    public static String getDisplayText(String status) {
        String normalized = normalize(status);
        if (STATUS_COMPLETED.equals(normalized)) {
            return "已完成";
        }
        if (STATUS_INTERRUPTED.equals(normalized)) {
            return "已中断";
        }
        return "进行中";
    }

    public static int getDisplayColor(String status) {
        String normalized = normalize(status);
        if (STATUS_COMPLETED.equals(normalized)) {
            return 0xFF67C23A;
        }
        if (STATUS_INTERRUPTED.equals(normalized)) {
            return 0xFFF56C6C;
        }
        return 0xFFE6A23C;
    }
}
