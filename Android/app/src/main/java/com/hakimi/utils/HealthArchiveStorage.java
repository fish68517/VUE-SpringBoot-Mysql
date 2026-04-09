package com.hakimi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hakimi.model.HealthArchiveEntry;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class HealthArchiveStorage {

    private static final String PREF_NAME = "health_archive_pref";
    private static final String KEY_ENTRIES = "entries";
    private static final String KEY_LAST_WARNING_DATE = "last_warning_date";
    private static final Gson GSON = new Gson();
    private static final Type ENTRY_LIST_TYPE = new TypeToken<List<HealthArchiveEntry>>() {
    }.getType();
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private HealthArchiveStorage() {
    }

    public static void saveOrUpdateEntry(Context context, HealthArchiveEntry entry) {
        List<HealthArchiveEntry> entries = upsertEntry(loadEntries(context), entry);
        getPrefs(context).edit().putString(KEY_ENTRIES, GSON.toJson(entries)).apply();
    }

    public static List<HealthArchiveEntry> upsertEntry(List<HealthArchiveEntry> source,
            HealthArchiveEntry entry) {
        List<HealthArchiveEntry> entries = new ArrayList<>(source);
        boolean replaced = false;
        for (int i = 0; i < entries.size(); i++) {
            if (TextUtils.equals(entries.get(i).getDate(), entry.getDate())) {
                entries.set(i, entry);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            entries.add(entry);
        }
        sortEntriesDesc(entries);
        return entries;
    }

    public static List<HealthArchiveEntry> loadEntries(Context context) {
        String json = getPrefs(context).getString(KEY_ENTRIES, "[]");
        List<HealthArchiveEntry> entries = GSON.fromJson(json, ENTRY_LIST_TYPE);
        if (entries == null) {
            entries = new ArrayList<>();
        }
        sortEntriesDesc(entries);
        return entries;
    }

    public static HealthArchiveEntry getLatestEntry(Context context) {
        List<HealthArchiveEntry> entries = loadEntries(context);
        return entries.isEmpty() ? null : entries.get(0);
    }

    public static boolean shouldShowRiskWarning(Context context) {
        HealthRiskResult riskResult = evaluateRisk(loadEntries(context));
        if (!riskResult.isTriggered()) {
            return false;
        }
        String lastWarningDate = getPrefs(context).getString(KEY_LAST_WARNING_DATE, "");
        return !TextUtils.equals(lastWarningDate, riskResult.getLatestDate());
    }

    public static void markRiskWarningShown(Context context, String date) {
        getPrefs(context).edit().putString(KEY_LAST_WARNING_DATE, date).apply();
    }

    public static HealthRiskResult evaluateRisk(List<HealthArchiveEntry> entries) {
        List<HealthArchiveEntry> sortedEntries = new ArrayList<>(entries);
        sortEntriesDesc(sortedEntries);
        if (sortedEntries.size() < 3) {
            return new HealthRiskResult(false, "", new ArrayList<>());
        }

        Set<String> hitDimensions = new LinkedHashSet<>();
        for (int i = 0; i <= sortedEntries.size() - 3; i++) {
            HealthArchiveEntry first = sortedEntries.get(i);
            HealthArchiveEntry second = sortedEntries.get(i + 1);
            HealthArchiveEntry third = sortedEntries.get(i + 2);
            if (!isPreviousDay(first.getDate(), second.getDate())
                    || !isPreviousDay(second.getDate(), third.getDate())) {
                continue;
            }

            addRiskDimension(hitDimensions, first.getMentalState(), second.getMentalState(),
                    third.getMentalState(), "精神状态");
            addRiskDimension(hitDimensions, first.getBodyPain(), second.getBodyPain(),
                    third.getBodyPain(), "身体疼痛");
            addRiskDimension(hitDimensions, first.getSleepQuality(), second.getSleepQuality(),
                    third.getSleepQuality(), "睡眠质量");
            addRiskDimension(hitDimensions, first.getDietRegularity(), second.getDietRegularity(),
                    third.getDietRegularity(), "饮食规律");
            addRiskDimension(hitDimensions, first.getStressIndex(), second.getStressIndex(),
                    third.getStressIndex(), "压力指数");

            if (!hitDimensions.isEmpty()) {
                return new HealthRiskResult(true, first.getDate(), new ArrayList<>(hitDimensions));
            }
        }
        return new HealthRiskResult(false, "", new ArrayList<>());
    }

    public static String buildPortraitSummary(HealthArchiveEntry entry) {
        if (entry == null) {
            return "暂未生成校园健康画像，请先完成一次自评。";
        }

        float average = entry.getAverageScore();
        String level;
        if (average >= 4.2f) {
            level = "活力稳定型";
        } else if (average >= 3.4f) {
            level = "整体平衡型";
        } else if (average >= 2.6f) {
            level = "作息待调整型";
        } else {
            level = "重点预警型";
        }

        String strongest = resolveDimensionName(maxDimension(entry));
        String weakest = resolveDimensionName(minDimension(entry));
        return "校园健康画像：" + level
                + "\n综合得分：" + String.format(Locale.getDefault(), "%.1f", average) + " / 5.0"
                + "\n当前优势：" + strongest
                + "\n优先改善：" + weakest
                + "\n建议：保持规律运动、按时休息，先从最低分项连续调整 3 天。";
    }

    public static String buildRecentTrend(List<HealthArchiveEntry> entries) {
        if (entries.isEmpty()) {
            return "最近记录：暂无";
        }

        StringBuilder builder = new StringBuilder();
        int count = Math.min(entries.size(), 5);
        builder.append("最近记录：\n");
        for (int i = 0; i < count; i++) {
            HealthArchiveEntry entry = entries.get(i);
            builder.append(entry.getDate())
                    .append("  平均")
                    .append(String.format(Locale.getDefault(), "%.1f", entry.getAverageScore()))
                    .append("分");
            if (i < count - 1) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    private static void addRiskDimension(Set<String> dimensions, int first, int second, int third,
            String label) {
        if (first <= 2 && second <= 2 && third <= 2) {
            dimensions.add(label);
        }
    }

    private static int maxDimension(HealthArchiveEntry entry) {
        int[] values = new int[] {
                entry.getMentalState(),
                entry.getBodyPain(),
                entry.getSleepQuality(),
                entry.getDietRegularity(),
                entry.getStressIndex()
        };
        int maxIndex = 0;
        for (int i = 1; i < values.length; i++) {
            if (values[i] > values[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private static int minDimension(HealthArchiveEntry entry) {
        int[] values = new int[] {
                entry.getMentalState(),
                entry.getBodyPain(),
                entry.getSleepQuality(),
                entry.getDietRegularity(),
                entry.getStressIndex()
        };
        int minIndex = 0;
        for (int i = 1; i < values.length; i++) {
            if (values[i] < values[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    private static String resolveDimensionName(int index) {
        switch (index) {
            case 0:
                return "精神状态";
            case 1:
                return "身体疼痛";
            case 2:
                return "睡眠质量";
            case 3:
                return "饮食规律";
            case 4:
                return "压力指数";
            default:
                return "综合状态";
        }
    }

    private static boolean isPreviousDay(String currentDate, String previousDate) {
        Date current = parseDate(currentDate);
        Date previous = parseDate(previousDate);
        if (current == null || previous == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return DATE_FORMAT.format(calendar.getTime()).equals(previousDate);
    }

    private static Date parseDate(String value) {
        try {
            return DATE_FORMAT.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static void sortEntriesDesc(List<HealthArchiveEntry> entries) {
        Collections.sort(entries, new Comparator<HealthArchiveEntry>() {
            @Override
            public int compare(HealthArchiveEntry left, HealthArchiveEntry right) {
                String leftDate = left == null || left.getDate() == null ? "" : left.getDate();
                String rightDate = right == null || right.getDate() == null ? "" : right.getDate();
                return rightDate.compareTo(leftDate);
            }
        });
    }

    public static class HealthRiskResult {
        private final boolean triggered;
        private final String latestDate;
        private final List<String> dimensions;

        public HealthRiskResult(boolean triggered, String latestDate, List<String> dimensions) {
            this.triggered = triggered;
            this.latestDate = latestDate;
            this.dimensions = dimensions;
        }

        public boolean isTriggered() {
            return triggered;
        }

        public String getLatestDate() {
            return latestDate;
        }

        public List<String> getDimensions() {
            return dimensions;
        }
    }
}
