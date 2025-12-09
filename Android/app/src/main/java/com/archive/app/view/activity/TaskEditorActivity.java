package com.archive.app.view.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.archive.app.R;
import com.archive.app.model.TaskFocus;
import com.archive.app.viewmodel.TaskEditorViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TaskEditorActivity extends AppCompatActivity {

    private TaskEditorViewModel viewModel;

    // UI 组件
    private TextInputEditText etTitle, etDesc;
    private AutoCompleteTextView actvDeadline, actvCategory;
    private ChipGroup chipGroupPriority;
    private Slider sliderFocus, sliderBreak;
    private MaterialSwitch switchAppBlock;
    private ExtendedFloatingActionButton btnSave;

    // 数据
    private boolean isEditMode = false;
    private Long editingTaskId = null;
    private Calendar deadlineCalendar = Calendar.getInstance();
    private Long currentUserId = 1L; // 模拟当前用户ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_editor);

        viewModel = new ViewModelProvider(this).get(TaskEditorViewModel.class);

        initViews();
        setupPickers();
        checkIntentData(); // 检查是否是编辑模式
        observeViewModel();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etTitle = findViewById(R.id.et_task_title);
        etDesc = findViewById(R.id.et_task_desc);
        actvDeadline = findViewById(R.id.actv_deadline);
        actvCategory = findViewById(R.id.actv_category);
        chipGroupPriority = findViewById(R.id.chip_group_priority);
        sliderFocus = findViewById(R.id.slider_focus);
        sliderBreak = findViewById(R.id.slider_break);
        switchAppBlock = findViewById(R.id.is_completed_switch);
        btnSave = findViewById(R.id.btn_save);

        // 设置Slider的标签更新
        sliderFocus.addOnChangeListener((slider, value, fromUser) ->
                ((android.widget.TextView)findViewById(R.id.tv_focus_label)).setText("专注时长: " + (int)value + "分钟"));

        sliderBreak.addOnChangeListener((slider, value, fromUser) ->
                ((android.widget.TextView)findViewById(R.id.tv_break_label)).setText("休息时长: " + (int)value + "分钟"));

        // 初始化分类下拉框（防止未点击时没有数据）
        String[] categories = getResources().getStringArray(R.array.task_categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(adapter);

        btnSave.setOnClickListener(v -> saveTask());
    }

    private void setupPickers() {
        actvDeadline.setOnClickListener(v -> {
            // 1. 日期选择
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                deadlineCalendar.set(Calendar.YEAR, year);
                deadlineCalendar.set(Calendar.MONTH, month);
                deadlineCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // 2. 时间选择
                new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                    deadlineCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    deadlineCalendar.set(Calendar.MINUTE, minute);
                    updateDeadlineText();
                }, deadlineCalendar.get(Calendar.HOUR_OF_DAY), deadlineCalendar.get(Calendar.MINUTE), true).show();

            }, deadlineCalendar.get(Calendar.YEAR), deadlineCalendar.get(Calendar.MONTH), deadlineCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void updateDeadlineText() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        actvDeadline.setText(sdf.format(deadlineCalendar.getTime()));
    }

    private void checkIntentData() {
        // 如果是编辑模式，Intent中会传递 TaskFocus 的 JSON 字符串
        String taskJson = getIntent().getStringExtra("task_json");
        if (taskJson != null) {
            isEditMode = true;
            getSupportActionBar().setTitle("编辑任务");
            TaskFocus task = new Gson().fromJson(taskJson, TaskFocus.class);
            populateData(task);
        }
    }

    private void populateData(TaskFocus task) {
        editingTaskId = task.getTaskFocusId();
        etTitle.setText(task.getTaskTitleText());
        etDesc.setText(task.getTaskDescriptionText());

        // --- 修改开始 ---
        // 处理时间回显：只保留 yyyy-MM-dd HH:mm，去掉秒
        String ts = task.getTaskDeadlineTimestamp();
        if (ts != null && ts.length() >= 16) {
            // 截取前16位 (例如 "2025-10-05T22:00:00" -> "2025-10-05T22:00")
            String shortTs = ts.substring(0, 16);
            actvDeadline.setText(shortTs.replace("T", " "));
        } else {
            actvDeadline.setText("");
        }
        // --- 修改结束 ---


        actvDeadline.setText(task.getTaskDeadlineTimestamp() != null ? task.getTaskDeadlineTimestamp().replace("T", " ") : "");
        actvCategory.setText(task.getTaskCategoryCode(), false); // false防止触发过滤

        // 优先级 (0, 1, 2)
        int priorityId = R.id.chip_medium;
        if (task.getTaskPriorityCode() == 0) priorityId = R.id.chip_low;
        else if (task.getTaskPriorityCode() == 2) priorityId = R.id.chip_high;
        chipGroupPriority.check(priorityId);

        // 专注设置
        sliderFocus.setValue(task.getTaskFocusDurationMins());
        sliderBreak.setValue(task.getTaskBreakDurationMins());
        switchAppBlock.setChecked(task.getTaskAppBlockFlag());
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("请输入标题");
            return;
        }

        TaskFocus task = new TaskFocus();
        if (isEditMode) task.setTaskFocusId(editingTaskId);

        task.setCampusUserId(currentUserId);
        task.setTaskTitleText(title);
        task.setTaskDescriptionText(etDesc.getText().toString());

        // --- 修改开始 ---
        // 处理日期格式: 确保格式为 yyyy-MM-ddTHH:mm:ss
        String deadline = actvDeadline.getText().toString();
        if (!TextUtils.isEmpty(deadline)) {
            // 1. 将空格替换为 T
            String isoFormat = deadline.replace(" ", "T");

            // 2. 智能补全秒数：如果长度只有 16 (yyyy-MM-ddTHH:mm)，说明没有秒，需要补 :00
            if (isoFormat.length() == 16) {
                isoFormat += ":00";
            }
            // 如果长度已经是 19 (yyyy-MM-ddTHH:mm:ss)，则不需要处理

            task.setTaskDeadlineTimestamp(isoFormat);
        }
        // --- 修改结束 ---



        // 处理日期格式: 后端通常需要 yyyy-MM-ddTHH:mm:ss
        //String deadline = actvDeadline.getText().toString();
       /* if (!TextUtils.isEmpty(deadline)) {
            task.setTaskDeadlineTimestamp(deadline.replace(" ", "T") + ":00");
        }*/

        task.setTaskCategoryCode(actvCategory.getText().toString());

        // 获取优先级
        int checkedChipId = chipGroupPriority.getCheckedChipId();
        int priority = 1; // 默认中
        if (checkedChipId == R.id.chip_low) priority = 0;
        else if (checkedChipId == R.id.chip_high) priority = 2;
        task.setTaskPriorityCode(priority);

        task.setTaskFocusDurationMins((int) sliderFocus.getValue());
        task.setTaskBreakDurationMins((int) sliderBreak.getValue());
        task.setTaskStatusEnum(switchAppBlock.isChecked() ? "completed" : "pending");

        // 默认状态
        if (!isEditMode) task.setTaskStatusEnum("pending");

        viewModel.saveTask(task, isEditMode);
    }


    private void observeViewModel() {
        viewModel.getSaveResult().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                finish(); // 关闭页面，返回列表
            }
        });

        viewModel.getErrorMessage().observe(this, error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        );

        viewModel.getIsLoading().observe(this, isLoading ->
                btnSave.setEnabled(!isLoading)
        );
    }
}