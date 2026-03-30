package com.archive.app.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.ApiClient;
import com.archive.app.ApiService;
import com.archive.app.R;
import com.archive.app.model.FocusRecord;
import com.archive.app.model.TaskFocus;
import com.archive.app.util.TaskTimerStore;
import com.archive.app.view.activity.TaskEditorActivity;
import com.archive.app.view.adapter.TaskAdapter;
import com.archive.app.viewmodel.TaskViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TaskFragment extends Fragment {

    private static final long CURRENT_USER_ID = 1L;
    private static final long MILLIS_PER_MINUTE = 60_000L;

    private final Gson gson = new Gson();

    private TaskViewModel taskViewModel;
    private ApiService apiService;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TaskAdapter taskAdapter;
    private View fab;

    private AlertDialog timerDialog;
    private CountDownTimer countDownTimer;
    private TaskFocus activeTimerTask;
    private TaskTimerStore.TimerSnapshot activeTimerSnapshot;
    private TextView timerPhaseView;
    private TextView timerClockView;
    private TextView timerMetaView;
    private MaterialButton timerToggleButton;
    private boolean skipPauseOnDismiss;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        apiService = ApiClient.getClient().create(ApiService.class);

        recyclerView = view.findViewById(R.id.recycler_view_tasks);
        progressBar = view.findViewById(R.id.progress_bar_tasks);
        fab = view.findViewById(R.id.fab);

        setupRecyclerView();
        observeViewModel();

        fab.setOnClickListener(v -> startActivity(new Intent(getContext(), TaskEditorActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (taskViewModel != null) {
            taskViewModel.refreshTasks();
        }
    }

    @Override
    public void onDestroyView() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if (timerDialog != null && timerDialog.isShowing()) {
            skipPauseOnDismiss = true;
            timerDialog.dismiss();
        }
        clearTimerDialogRefs();
        super.onDestroyView();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);
        taskAdapter.setOnTaskActionListener(new TaskAdapter.OnTaskActionListener() {
            @Override
            public void onEdit(TaskFocus task) {
                if (getContext() == null || task == null) {
                    return;
                }
                Intent intent = new Intent(getContext(), TaskEditorActivity.class);
                intent.putExtra("task_json", gson.toJson(task));
                startActivity(intent);
            }

            @Override
            public void onTimer(TaskFocus task) {
                if (task == null) {
                    return;
                }
                if (task.getTaskFocusId() == null) {
                    Toast.makeText(getContext(), "请先保存任务后再开始专注计时", Toast.LENGTH_SHORT).show();
                    return;
                }
                showTimerDialog(task);
            }
        });
    }

    private void observeViewModel() {
        taskViewModel.getUserTasks().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null) {
                taskAdapter.setTasks(tasks, getActivity());
            }
        });

        taskViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            }
        });

        taskViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTimerDialog(TaskFocus task) {
        if (getContext() == null) {
            return;
        }
        stopCurrentCountDown();

        activeTimerTask = task;
        activeTimerSnapshot = loadOrCreateSnapshot(task);

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_task_timer, null, false);
        TextView timerTitleView = dialogView.findViewById(R.id.tv_timer_task_title);
        timerPhaseView = dialogView.findViewById(R.id.tv_timer_phase);
        timerClockView = dialogView.findViewById(R.id.tv_timer_clock);
        timerMetaView = dialogView.findViewById(R.id.tv_timer_meta);
        timerToggleButton = dialogView.findViewById(R.id.btn_timer_toggle);
        MaterialButton timerNextStageButton = dialogView.findViewById(R.id.btn_timer_next_stage);
        MaterialButton timerFinishButton = dialogView.findViewById(R.id.btn_timer_finish);

        timerTitleView.setText(task.getTaskTitleText() == null || task.getTaskTitleText().trim().isEmpty()
                ? "未命名任务" : task.getTaskTitleText());

        timerToggleButton.setOnClickListener(v -> {
            if (activeTimerSnapshot == null) {
                return;
            }
            if (activeTimerSnapshot.running) {
                pauseActiveTimer(true);
            } else {
                startActiveTimer();
            }
        });

        timerNextStageButton.setOnClickListener(v -> {
            if (activeTimerSnapshot == null) {
                return;
            }
            boolean resumeNextStage = activeTimerSnapshot.running;
            pauseActiveTimer(false);
            moveToNextStage(true);
            if (resumeNextStage) {
                startActiveTimer();
            } else {
                updateTimerDialogContent();
            }
        });

        timerFinishButton.setOnClickListener(v -> finishTimerSession());

        skipPauseOnDismiss = false;
        timerDialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .create();
        timerDialog.setOnDismissListener(dialog -> {
            if (!skipPauseOnDismiss) {
                pauseActiveTimer(false);
            }
            clearTimerDialogRefs();
            if (taskViewModel != null) {
                taskViewModel.refreshTasks();
            }
            skipPauseOnDismiss = false;
        });
        timerDialog.show();

        updateTimerDialogContent();
        if (activeTimerSnapshot.running) {
            startCountDown(activeTimerSnapshot.remainingMillis);
        }
    }

    private TaskTimerStore.TimerSnapshot loadOrCreateSnapshot(TaskFocus task) {
        TaskTimerStore.TimerSnapshot snapshot = TaskTimerStore.load(requireContext(), task.getTaskFocusId());
        if (snapshot != null) {
            snapshot.focusDurationMins = sanitizeFocusMinutes(snapshot.focusDurationMins);
            snapshot.breakDurationMins = sanitizeBreakMinutes(snapshot.breakDurationMins);
            if (snapshot.stageDurationMillis <= 0L) {
                snapshot.stageDurationMillis = (snapshot.breakMode
                        ? snapshot.breakDurationMins : snapshot.focusDurationMins) * MILLIS_PER_MINUTE;
            }
            if (snapshot.remainingMillis <= 0L || snapshot.remainingMillis > snapshot.stageDurationMillis) {
                snapshot.remainingMillis = snapshot.stageDurationMillis;
            }
            snapshot.taskTitle = task.getTaskTitleText();
            return snapshot;
        }

        snapshot = new TaskTimerStore.TimerSnapshot();
        snapshot.taskId = task.getTaskFocusId();
        snapshot.taskTitle = task.getTaskTitleText();
        snapshot.focusDurationMins = sanitizeFocusMinutes(task.getTaskFocusDurationMins());
        snapshot.breakDurationMins = sanitizeBreakMinutes(task.getTaskBreakDurationMins());
        snapshot.breakMode = false;
        snapshot.running = false;
        snapshot.taskStarted = false;
        snapshot.pauseCount = 0;
        snapshot.completedCycles = 0;
        snapshot.stageDurationMillis = snapshot.focusDurationMins * MILLIS_PER_MINUTE;
        snapshot.remainingMillis = snapshot.stageDurationMillis;
        snapshot.elapsedFocusMillisInRound = 0L;
        snapshot.focusSessionStartEpochMillis = 0L;
        return snapshot;
    }

    private int sanitizeFocusMinutes(Integer value) {
        return value == null || value <= 0 ? 30 : value;
    }

    private int sanitizeBreakMinutes(Integer value) {
        return value == null || value < 0 ? 5 : value;
    }

    private void startActiveTimer() {
        if (activeTimerSnapshot == null) {
            return;
        }
        activeTimerSnapshot.running = true;
        activeTimerSnapshot.taskStarted = true;
        persistActiveSnapshot();
        startCountDown(activeTimerSnapshot.remainingMillis);
        syncTaskProgress(0, true);
        updateTimerDialogContent();
    }

    private void startCountDown(long durationMillis) {
        stopCurrentCountDown();
        countDownTimer = new CountDownTimer(Math.max(durationMillis, 1000L), 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (activeTimerSnapshot == null) {
                    cancel();
                    return;
                }
                activeTimerSnapshot.remainingMillis = millisUntilFinished;
                if (!activeTimerSnapshot.breakMode) {
                    activeTimerSnapshot.elapsedFocusMillisInRound =
                            activeTimerSnapshot.stageDurationMillis - millisUntilFinished;
                }
                persistActiveSnapshot();
                updateTimerDialogContent();
            }

            @Override
            public void onFinish() {
                if (activeTimerSnapshot == null) {
                    return;
                }
                activeTimerSnapshot.remainingMillis = 0L;
                if (!activeTimerSnapshot.breakMode) {
                    activeTimerSnapshot.elapsedFocusMillisInRound = activeTimerSnapshot.stageDurationMillis;
                }
                moveToNextStage(false);
                startActiveTimer();
            }
        };
        countDownTimer.start();
    }

    private void pauseActiveTimer(boolean userTriggered) {
        if (activeTimerSnapshot == null || !activeTimerSnapshot.running) {
            return;
        }
        stopCurrentCountDown();
        activeTimerSnapshot.running = false;
        if (userTriggered) {
            activeTimerSnapshot.pauseCount += 1;
            Toast.makeText(getContext(), "已暂停，可稍后继续专注", Toast.LENGTH_SHORT).show();
        }
        persistActiveSnapshot();
        updateTimerDialogContent();
    }

    private void moveToNextStage(boolean interrupted) {
        if (activeTimerSnapshot == null) {
            return;
        }

        if (activeTimerSnapshot.breakMode) {
            activeTimerSnapshot.completedCycles += 1;
            activeTimerSnapshot.breakMode = false;
            activeTimerSnapshot.stageDurationMillis = activeTimerSnapshot.focusDurationMins * MILLIS_PER_MINUTE;
            activeTimerSnapshot.remainingMillis = activeTimerSnapshot.stageDurationMillis;
            activeTimerSnapshot.elapsedFocusMillisInRound = 0L;
            activeTimerSnapshot.running = false;
            persistActiveSnapshot();
            Toast.makeText(getContext(), "休息结束，进入下一轮专注", Toast.LENGTH_SHORT).show();
            return;
        }

        int focusSeconds = (int) (activeTimerSnapshot.elapsedFocusMillisInRound / 1000L);
        if (focusSeconds > 0) {
            saveFocusRecord(focusSeconds, interrupted ? "interrupted" : "completed");
            syncTaskProgress(focusSeconds, true);
        }

        int breakMinutes = sanitizeBreakMinutes(activeTimerSnapshot.breakDurationMins);
        if (breakMinutes <= 0) {
            activeTimerSnapshot.completedCycles += 1;
            activeTimerSnapshot.breakMode = false;
            activeTimerSnapshot.stageDurationMillis = activeTimerSnapshot.focusDurationMins * MILLIS_PER_MINUTE;
            activeTimerSnapshot.remainingMillis = activeTimerSnapshot.stageDurationMillis;
            activeTimerSnapshot.elapsedFocusMillisInRound = 0L;
            activeTimerSnapshot.running = false;
            persistActiveSnapshot();
            return;
        }

        activeTimerSnapshot.breakMode = true;
        activeTimerSnapshot.stageDurationMillis = breakMinutes * MILLIS_PER_MINUTE;
        activeTimerSnapshot.remainingMillis = activeTimerSnapshot.stageDurationMillis;
        activeTimerSnapshot.elapsedFocusMillisInRound = 0L;
        activeTimerSnapshot.running = false;
        persistActiveSnapshot();
        if (!interrupted) {
            Toast.makeText(getContext(), "专注完成，开始休息", Toast.LENGTH_SHORT).show();
        }
    }

    private void finishTimerSession() {
        if (activeTimerSnapshot == null || activeTimerTask == null || getContext() == null) {
            return;
        }

        boolean onFocusStage = !activeTimerSnapshot.breakMode;
        int focusSeconds = (int) (activeTimerSnapshot.elapsedFocusMillisInRound / 1000L);
        if (onFocusStage && focusSeconds > 0) {
            saveFocusRecord(focusSeconds, "interrupted");
            syncTaskProgress(focusSeconds, true);
        }

        stopCurrentCountDown();
        TaskTimerStore.clear(getContext(), activeTimerTask.getTaskFocusId());
        Toast.makeText(getContext(), "本次专注计时已结束", Toast.LENGTH_SHORT).show();

        activeTimerSnapshot = null;
        skipPauseOnDismiss = true;
        if (timerDialog != null) {
            timerDialog.dismiss();
        }
    }

    private void updateTimerDialogContent() {
        if (activeTimerSnapshot == null || timerPhaseView == null || timerClockView == null
                || timerMetaView == null || timerToggleButton == null) {
            return;
        }

        String phaseText = activeTimerSnapshot.breakMode ? "当前阶段: 休息" : "当前阶段: 专注";
        timerPhaseView.setText(phaseText);
        timerClockView.setText(formatMillis(activeTimerSnapshot.remainingMillis));
        timerMetaView.setText(buildTimerMetaText(activeTimerSnapshot));
        timerToggleButton.setText(activeTimerSnapshot.running ? "暂停计时" :
                (activeTimerSnapshot.taskStarted ? "继续专注" : "开始专注"));
    }

    private String buildTimerMetaText(TaskTimerStore.TimerSnapshot snapshot) {
        return "专注 " + snapshot.focusDurationMins + " 分钟 / 休息 " + snapshot.breakDurationMins
                + " 分钟  |  已暂停 " + snapshot.pauseCount + " 次  |  完成轮次 " + snapshot.completedCycles;
    }

    private String formatMillis(long millis) {
        long safeMillis = Math.max(0L, millis);
        long totalSeconds = safeMillis / 1000L;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void persistActiveSnapshot() {
        if (getContext() == null || activeTimerSnapshot == null) {
            return;
        }
        TaskTimerStore.save(getContext(), activeTimerSnapshot);
    }

    private void stopCurrentCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void clearTimerDialogRefs() {
        timerDialog = null;
        timerPhaseView = null;
        timerClockView = null;
        timerMetaView = null;
        timerToggleButton = null;
        activeTimerTask = null;
    }

    private void saveFocusRecord(int focusSeconds, String status) {
        if (activeTimerTask == null || activeTimerTask.getTaskFocusId() == null || focusSeconds <= 0) {
            return;
        }

        FocusRecord focusRecord = new FocusRecord();
        focusRecord.setCampusUserId(CURRENT_USER_ID);
        focusRecord.setTaskFocusId(activeTimerTask.getTaskFocusId());
        long endTime = System.currentTimeMillis();
        long startTime = endTime - (focusSeconds * 1000L);
        focusRecord.setFocusStartTimestamp(formatIsoTime(startTime));
        focusRecord.setFocusEndTimestamp(formatIsoTime(endTime));
        focusRecord.setFocusDurationSeconds(focusSeconds);
        focusRecord.setFocusTypeEnum("focus");
        focusRecord.setFocusStatusEnum(status);
        focusRecord.setFocusInterruptionCount("interrupted".equals(status) ? 1 : 0);
        focusRecord.setFocusPauseCount(activeTimerSnapshot != null ? activeTimerSnapshot.pauseCount : 0);
        focusRecord.setFocusNoteText(activeTimerTask.getTaskTitleText());
        focusRecord.setFocusAppBlockFlag(Boolean.TRUE.equals(activeTimerTask.getTaskAppBlockFlag()));

        apiService.createFocusRecord(focusRecord).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    private void syncTaskProgress(int focusSeconds, boolean markInProgress) {
        if (activeTimerTask == null || activeTimerTask.getTaskFocusId() == null) {
            return;
        }

        if (markInProgress && !"completed".equals(activeTimerTask.getTaskStatusEnum())) {
            activeTimerTask.setTaskStatusEnum("in_progress");
        }
        if (focusSeconds > 0) {
            int addMinutes = Math.max(1, focusSeconds / 60);
            int currentMinutes = activeTimerTask.getTaskActualMinutes() == null ? 0 : activeTimerTask.getTaskActualMinutes();
            activeTimerTask.setTaskActualMinutes(currentMinutes + addMinutes);
        }

        apiService.updateTaskFocus(activeTimerTask).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (taskViewModel != null) {
                    taskViewModel.refreshTasks();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    private String formatIsoTime(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .format(new Date(timestamp));
    }
}
