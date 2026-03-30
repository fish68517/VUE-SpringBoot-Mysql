package com.archive.app.view.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.model.HabitCheckin;
import com.archive.app.model.HabitTrack;
import com.archive.app.util.CheckinStatusHelper;
import com.archive.app.view.adapter.CheckinAdapter;
import com.archive.app.viewmodel.HabitDetailViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HabitDetailActivity extends AppCompatActivity {

    private HabitDetailViewModel viewModel;
    private HabitTrack currentHabit;
    private CheckinAdapter adapter;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_detail);

        String json = getIntent().getStringExtra("habit_json");
        if (json != null) {
            currentHabit = new Gson().fromJson(json, HabitTrack.class);
        }

        initViews();
        viewModel = new ViewModelProvider(this).get(HabitDetailViewModel.class);

        viewModel.getCheckinList().observe(this, list -> adapter.setList(list));
        viewModel.getToastMessage().observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        if (currentHabit != null) {
            viewModel.fetchCheckins(currentHabit.getHabitTrackId());
        }
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_detail);
        toolbar.setTitle(currentHabit != null ? currentHabit.getHabitNameText() : "习惯详情");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycler_view_checkins);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckinAdapter();

        adapter.setOnActionClickListener(new CheckinAdapter.OnActionClickListener() {
            @Override
            public void onEdit(HabitCheckin checkin) {
                showEditDialog(checkin);
            }

            @Override
            public void onDelete(HabitCheckin checkin) {
                new AlertDialog.Builder(HabitDetailActivity.this)
                        .setTitle("确认删除")
                        .setMessage("确定要删除这条打卡记录吗？")
                        .setPositiveButton("删除", (d, w) ->
                                viewModel.deleteCheckin(checkin.getHabitCheckinId(), currentHabit.getHabitTrackId()))
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_checkin);
        fab.setOnClickListener(v -> showEditDialog(null));
    }

    private void showEditDialog(HabitCheckin existingCheckin) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_checkin_editor, null);

        TextView tvDate = view.findViewById(R.id.tv_dialog_date);
        EditText etNote = view.findViewById(R.id.et_dialog_note);
        RadioGroup rgStatus = view.findViewById(R.id.rg_dialog_status);

        final boolean isEdit = existingCheckin != null;
        final Calendar calendar = Calendar.getInstance();

        if (isEdit) {
            tvDate.setText(existingCheckin.getCheckinDate());
            etNote.setText(existingCheckin.getCheckinNoteText());
            rgStatus.check(getCheckedRadioId(existingCheckin.getCheckinStatusEnum()));
        } else {
            tvDate.setText(dateFormat.format(new Date()));
            rgStatus.check(R.id.rb_completed);
        }

        tvDate.setOnClickListener(v -> new DatePickerDialog(
                this,
                (dp, y, m, d) -> {
                    calendar.set(y, m, d);
                    tvDate.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show());

        builder.setView(view)
                .setTitle(isEdit ? "编辑打卡" : "新增打卡")
                .setPositiveButton("保存", (dialog, which) -> {
                    String date = tvDate.getText().toString();
                    String note = etNote.getText().toString();
                    String status = getStatusFromRadioId(rgStatus.getCheckedRadioButtonId());

                    HabitCheckin checkin = isEdit ? existingCheckin : new HabitCheckin();
                    checkin.setHabitTrackId(currentHabit.getHabitTrackId());
                    checkin.setCheckinDate(date);
                    checkin.setCheckinNoteText(note);
                    checkin.setCheckinStatusEnum(status);

                    if (!isEdit) {
                        checkin.setCheckinStreakCount(0);
                    }

                    if (isEdit) {
                        viewModel.updateCheckin(checkin);
                    } else {
                        viewModel.createCheckin(checkin);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private int getCheckedRadioId(String status) {
        String normalizedStatus = CheckinStatusHelper.normalize(status);
        if (CheckinStatusHelper.STATUS_COMPLETED.equals(normalizedStatus)) {
            return R.id.rb_completed;
        }
        if (CheckinStatusHelper.STATUS_INTERRUPTED.equals(normalizedStatus)) {
            return R.id.rb_interrupted;
        }
        return R.id.rb_in_progress;
    }

    private String getStatusFromRadioId(int checkedId) {
        if (checkedId == R.id.rb_completed) {
            return CheckinStatusHelper.STATUS_COMPLETED;
        }
        if (checkedId == R.id.rb_interrupted) {
            return CheckinStatusHelper.STATUS_INTERRUPTED;
        }
        return CheckinStatusHelper.STATUS_IN_PROGRESS;
    }
}
