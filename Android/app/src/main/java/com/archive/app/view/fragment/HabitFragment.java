package com.archive.app.view.fragment;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.ApiClient;
import com.archive.app.ApiService;
import com.archive.app.R;
import com.archive.app.model.HabitCheckin;
import com.archive.app.model.HabitTrack;
import com.archive.app.util.CheckinStatusHelper;
import com.archive.app.util.HabitStatusHelper;
import com.archive.app.view.activity.HabitDetailActivity;
import com.archive.app.view.adapter.HabitAdapter;
import com.archive.app.viewmodel.HabitViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HabitFragment extends Fragment {

    private static final long CURRENT_USER_ID = 1L;
    private static final String[] FREQUENCY_OPTIONS = {"每日", "每周"};
    private static final String[] STATUS_OPTIONS = {"进行中", "已暂停", "已完成"};
    private static final String[] STATUS_VALUES = {
            HabitStatusHelper.STATUS_IN_PROGRESS,
            HabitStatusHelper.STATUS_PAUSED,
            HabitStatusHelper.STATUS_COMPLETED
    };
    private static final String[] COLOR_OPTIONS = {
            "#7E57C2", "#5C6BC0", "#26A69A", "#FF7043", "#AB47BC"
    };

    private final Gson gson = new Gson();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    private HabitViewModel habitViewModel;
    private ApiService apiService;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private HabitAdapter habitAdapter;
    private ExtendedFloatingActionButton fabAddHabit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_habit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);
        apiService = ApiClient.getClient().create(ApiService.class);
        recyclerView = view.findViewById(R.id.recycler_view_habits);
        progressBar = view.findViewById(R.id.progress_bar_habits);
        fabAddHabit = view.findViewById(R.id.fab_add_habit);

        setupRecyclerView();
        observeViewModel();

        fabAddHabit.setOnClickListener(v -> showCreateHabitDialog());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (habitViewModel != null) {
            habitViewModel.refreshHabits();
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        habitAdapter = new HabitAdapter();
        habitAdapter.setOnHabitActionListener(new HabitAdapter.OnHabitActionListener() {
            @Override
            public void onOpenDetail(HabitTrack habit) {
                if (getContext() == null) {
                    return;
                }
                Intent intent = new Intent(getContext(), HabitDetailActivity.class);
                intent.putExtra("habit_json", gson.toJson(habit));
                startActivity(intent);
            }

            @Override
            public void onChangeStatus(HabitTrack habit) {
                showStatusDialog(habit);
            }

            @Override
            public void onViewStats(HabitTrack habit) {
                fetchHabitCheckinsForStats(habit);
            }
        });
        recyclerView.setAdapter(habitAdapter);
    }

    private void observeViewModel() {
        habitViewModel.getUserHabits().observe(getViewLifecycleOwner(), habits -> {
            if (habits != null) {
                habitAdapter.setHabits(habits);
            }
        });

        habitViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            }
        });

        habitViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        habitViewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreateHabitDialog() {
        if (getContext() == null) {
            return;
        }

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_habit_editor, null, false);
        EditText etHabitName = dialogView.findViewById(R.id.et_habit_name);
        AutoCompleteTextView actvFrequency = dialogView.findViewById(R.id.actv_habit_frequency);
        TextView tvReminder = dialogView.findViewById(R.id.tv_habit_reminder);

        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                FREQUENCY_OPTIONS
        );
        actvFrequency.setAdapter(frequencyAdapter);
        actvFrequency.setText(FREQUENCY_OPTIONS[0], false);
        tvReminder.setText("07:00");
        tvReminder.setOnClickListener(v -> openTimePicker(tvReminder));

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("创建习惯")
                .setView(dialogView)
                .setPositiveButton("保存", (dialog, which) -> {
                    String name = etHabitName.getText() == null ? "" : etHabitName.getText().toString().trim();
                    String frequencyLabel = actvFrequency.getText() == null ? "" : actvFrequency.getText().toString().trim();
                    String reminderLabel = tvReminder.getText().toString().trim();

                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(getContext(), "请输入习惯名称", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    HabitTrack habitTrack = new HabitTrack();
                    habitTrack.setCampusUserId(CURRENT_USER_ID);
                    habitTrack.setHabitNameText(name);
                    habitTrack.setHabitFrequencyEnum("每周".equals(frequencyLabel) ? "weekly" : "daily");
                    habitTrack.setHabitReminderTime(reminderLabel + ":00");
                    habitTrack.setHabitStatusEnum(HabitStatusHelper.STATUS_IN_PROGRESS);
                    habitTrack.setHabitCreateTimestamp(isoDateTimeFormat.format(new Date()));
                    habitTrack.setHabitStreakCount(0);
                    habitTrack.setHabitTotalCount(0);
                    habitTrack.setHabitGoalCount("weekly".equals(habitTrack.getHabitFrequencyEnum()) ? 1 : 7);
                    habitTrack.setHabitColorHex(pickColor(name));
                    habitTrack.setHabitIconCode("habit");
                    habitViewModel.createHabit(habitTrack);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void openTimePicker(TextView targetView) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> targetView.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        ).show();
    }

    private void showStatusDialog(HabitTrack habit) {
        if (getContext() == null || habit == null) {
            return;
        }

        int checkedIndex = getStatusOptionIndex(habit.getHabitStatusEnum());
        final int[] selectedIndex = {checkedIndex};

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("修改习惯状态")
                .setSingleChoiceItems(STATUS_OPTIONS, checkedIndex, (dialog, which) -> selectedIndex[0] = which)
                .setPositiveButton("保存", (dialog, which) -> {
                    HabitTrack updatedHabit = copyHabit(habit);
                    updatedHabit.setHabitStatusEnum(STATUS_VALUES[selectedIndex[0]]);
                    habitViewModel.updateHabit(updatedHabit, "习惯状态已更新");
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void fetchHabitCheckinsForStats(HabitTrack habit) {
        if (habit == null || habit.getHabitTrackId() == null || getContext() == null) {
            return;
        }

        Toast.makeText(getContext(), "正在加载成果视图...", Toast.LENGTH_SHORT).show();
        apiService.getAllHabitCheckins().enqueue(new Callback<List<HabitCheckin>>() {
            @Override
            public void onResponse(Call<List<HabitCheckin>> call, Response<List<HabitCheckin>> response) {
                if (!isAdded()) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    List<HabitCheckin> filtered = new ArrayList<>();
                    for (HabitCheckin checkin : response.body()) {
                        if (habit.getHabitTrackId().equals(checkin.getHabitTrackId())) {
                            filtered.add(checkin);
                        }
                    }
                    Collections.sort(filtered, (left, right) -> safeDate(right.getCheckinDate()).compareTo(safeDate(left.getCheckinDate())));
                    showStatsDialog(habit, filtered);
                } else {
                    Toast.makeText(getContext(), "加载成果视图失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<HabitCheckin>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showStatsDialog(HabitTrack habit, List<HabitCheckin> checkins) {
        if (getContext() == null) {
            return;
        }

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_habit_stats, null, false);
        TextView tvTitle = dialogView.findViewById(R.id.tv_stats_title);
        TextView tvSubtitle = dialogView.findViewById(R.id.tv_stats_subtitle);
        TextView tvSummary = dialogView.findViewById(R.id.tv_stats_summary);
        TextView tvCompletion = dialogView.findViewById(R.id.tv_stats_completion);
        TextView tvRecordSummary = dialogView.findViewById(R.id.tv_stats_record_summary);
        GridLayout gridCalendar = dialogView.findViewById(R.id.grid_habit_calendar);

        int completedCount = countByStatus(checkins, CheckinStatusHelper.STATUS_COMPLETED);
        int inProgressCount = countByStatus(checkins, CheckinStatusHelper.STATUS_IN_PROGRESS);
        int interruptedCount = countByStatus(checkins, CheckinStatusHelper.STATUS_INTERRUPTED);
        int streakDays = habit.getHabitStreakCount() == null ? calculateCurrentStreak(checkins) : habit.getHabitStreakCount();
        int totalCount = habit.getHabitTotalCount() == null ? completedCount : habit.getHabitTotalCount();

        tvTitle.setText(habit.getHabitNameText());
        tvSubtitle.setText("频率: " + ("weekly".equals(habit.getHabitFrequencyEnum()) ? "每周" : "每日")
                + "  |  提醒时间: " + formatReminder(habit.getHabitReminderTime()));
        tvSummary.setText("连续打卡 " + streakDays + " 天，累计完成 " + totalCount + " 次");
        tvCompletion.setText(buildCompletionText(habit.getHabitFrequencyEnum(), completedCount, inProgressCount, interruptedCount, checkins));
        tvRecordSummary.setText(buildRecordSummary(checkins));
        populateCalendarGrid(gridCalendar, checkins);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("成果视图")
                .setView(dialogView)
                .setPositiveButton("关闭", null)
                .show();
    }

    private void populateCalendarGrid(GridLayout gridLayout, List<HabitCheckin> checkins) {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(7);

        String[] weekLabels = {"一", "二", "三", "四", "五", "六", "日"};
        for (String label : weekLabels) {
            gridLayout.addView(createCalendarCell(label, 0xFFEDE7F6, 0xFF4F378B, true));
        }

        Map<String, String> statusByDate = new HashMap<>();
        for (HabitCheckin checkin : checkins) {
            statusByDate.put(checkin.getCheckinDate(), CheckinStatusHelper.normalize(checkin.getCheckinStatusEnum()));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -27);
        for (int i = 0; i < 28; i++) {
            String dateKey = dateFormat.format(calendar.getTime());
            String status = statusByDate.get(dateKey);
            int backgroundColor = getCalendarBackgroundColor(status);
            int textColor = status == null ? 0xFF7B7386 : 0xFFFFFFFF;
            TextView dayCell = createCalendarCell(
                    String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)),
                    backgroundColor,
                    textColor,
                    false
            );
            dayCell.setContentDescription(dateKey + " " + (status == null ? "无记录" : CheckinStatusHelper.getDisplayText(status)));
            gridLayout.addView(dayCell);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private TextView createCalendarCell(String text, int backgroundColor, int textColor, boolean header) {
        TextView textView = new TextView(requireContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, dpToPx(header ? 8 : 10), 0, dpToPx(header ? 8 : 10));
        textView.setText(text);
        textView.setTextColor(textColor);
        textView.setTextSize(header ? 12 : 13);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(dpToPx(14));
        drawable.setColor(backgroundColor);
        textView.setBackground(drawable);
        return textView;
    }

    private int getCalendarBackgroundColor(String status) {
        if (status == null) {
            return 0xFFF1EDF5;
        }
        String normalized = CheckinStatusHelper.normalize(status);
        if (CheckinStatusHelper.STATUS_COMPLETED.equals(normalized)) {
            return 0xFF67C23A;
        }
        if (CheckinStatusHelper.STATUS_INTERRUPTED.equals(normalized)) {
            return 0xFFF56C6C;
        }
        return 0xFFE6A23C;
    }

    private int countByStatus(List<HabitCheckin> checkins, String expectedStatus) {
        int count = 0;
        for (HabitCheckin checkin : checkins) {
            if (expectedStatus.equals(CheckinStatusHelper.normalize(checkin.getCheckinStatusEnum()))) {
                count++;
            }
        }
        return count;
    }

    private int calculateCurrentStreak(List<HabitCheckin> checkins) {
        int streak = 0;
        for (HabitCheckin checkin : checkins) {
            if (CheckinStatusHelper.STATUS_COMPLETED.equals(CheckinStatusHelper.normalize(checkin.getCheckinStatusEnum()))) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    private String buildCompletionText(String frequency, int completedCount, int inProgressCount, int interruptedCount, List<HabitCheckin> checkins) {
        int recentCompleted = 0;
        int recentRecorded = 0;
        Calendar threshold = Calendar.getInstance();
        threshold.add(Calendar.DAY_OF_YEAR, "weekly".equals(frequency) ? -28 : -7);

        for (HabitCheckin checkin : checkins) {
            Date date = safeDate(checkin.getCheckinDate());
            if (!date.before(threshold.getTime())) {
                recentRecorded++;
                if (CheckinStatusHelper.STATUS_COMPLETED.equals(CheckinStatusHelper.normalize(checkin.getCheckinStatusEnum()))) {
                    recentCompleted++;
                }
            }
        }

        String periodText = "weekly".equals(frequency) ? "最近 4 周" : "最近 7 天";
        return periodText + "完成 " + recentCompleted + " 次，有记录 " + recentRecorded + " 次"
                + "，进行中 " + inProgressCount + " 次，中断 " + interruptedCount + " 次，总完成 " + completedCount + " 次";
    }

    private String buildRecordSummary(List<HabitCheckin> checkins) {
        if (checkins.isEmpty()) {
            return "暂无签到记录，点击习惯卡片可进入详情页新增每日 / 每周打卡。";
        }

        StringBuilder builder = new StringBuilder();
        int limit = Math.min(6, checkins.size());
        for (int i = 0; i < limit; i++) {
            HabitCheckin checkin = checkins.get(i);
            if (i > 0) {
                builder.append('\n');
            }
            builder.append(checkin.getCheckinDate())
                    .append("  ")
                    .append(CheckinStatusHelper.getDisplayText(checkin.getCheckinStatusEnum()));
            if (checkin.getCheckinNoteText() != null && !checkin.getCheckinNoteText().trim().isEmpty()) {
                builder.append("  ·  ").append(checkin.getCheckinNoteText().trim());
            }
        }
        return builder.toString();
    }

    private int getStatusOptionIndex(String status) {
        String normalized = HabitStatusHelper.normalize(status);
        for (int i = 0; i < STATUS_VALUES.length; i++) {
            if (STATUS_VALUES[i].equals(normalized)) {
                return i;
            }
        }
        return 0;
    }

    private HabitTrack copyHabit(HabitTrack source) {
        HabitTrack copy = new HabitTrack();
        copy.setHabitTrackId(source.getHabitTrackId());
        copy.setCampusUserId(source.getCampusUserId());
        copy.setHabitNameText(source.getHabitNameText());
        copy.setHabitFrequencyEnum(source.getHabitFrequencyEnum());
        copy.setHabitReminderTime(source.getHabitReminderTime());
        copy.setHabitStatusEnum(source.getHabitStatusEnum());
        copy.setHabitCreateTimestamp(source.getHabitCreateTimestamp());
        copy.setHabitStreakCount(source.getHabitStreakCount());
        copy.setHabitTotalCount(source.getHabitTotalCount());
        copy.setHabitGoalCount(source.getHabitGoalCount());
        copy.setHabitColorHex(source.getHabitColorHex());
        copy.setHabitIconCode(source.getHabitIconCode());
        return copy;
    }

    private String pickColor(String name) {
        int index = Math.abs(name.hashCode()) % COLOR_OPTIONS.length;
        return COLOR_OPTIONS[index];
    }

    private String formatReminder(String reminderTime) {
        if (reminderTime == null || reminderTime.trim().isEmpty()) {
            return "未设置";
        }
        return reminderTime.length() >= 5 ? reminderTime.substring(0, 5) : reminderTime;
    }

    private Date safeDate(String value) {
        if (value == null) {
            return new Date(0L);
        }
        try {
            return dateFormat.parse(value);
        } catch (ParseException exception) {
            return new Date(0L);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
