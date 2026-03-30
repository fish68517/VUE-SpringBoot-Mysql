package com.archive.app.util;

public final class TaskStatusHelper {

    private TaskStatusHelper() {
    }

    public static String getDisplayText(String status) {
        if ("completed".equals(status)) {
            return "已完成";
        }
        if ("in_progress".equals(status)) {
            return "进行中";
        }
        return "待开始";
    }

    public static int getColor(String status) {
        if ("completed".equals(status)) {
            return 0xFF67C23A;
        }
        if ("in_progress".equals(status)) {
            return 0xFF409EFF;
        }
        return 0xFFE6A23C;
    }
}
