package com.hakimi.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hakimi.R;
import com.hakimi.model.HealthArchiveEntry;
import com.hakimi.utils.HealthArchiveStorage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HealthArchiveActivity extends AppCompatActivity {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private TextView tvTodayStatus;
    private RatingBar rbMentalState;
    private RatingBar rbBodyPain;
    private RatingBar rbSleepQuality;
    private RatingBar rbDietRegularity;
    private RatingBar rbStressIndex;
    private TextView tvPortraitSummary;
    private TextView tvRecentTrend;
    private Button btnEditTodayArchive;
    private Button btnSaveArchive;
    private boolean isEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_archive);

        initViews();
        bindLatestEntry();
        btnEditTodayArchive.setOnClickListener(v -> enableTodayEdit());
        btnSaveArchive.setOnClickListener(v -> saveTodayArchive());
        maybeShowRiskWarning();
    }

    private void initViews() {
        tvTodayStatus = findViewById(R.id.tv_today_status);
        rbMentalState = findViewById(R.id.rb_mental_state);
        rbBodyPain = findViewById(R.id.rb_body_pain);
        rbSleepQuality = findViewById(R.id.rb_sleep_quality);
        rbDietRegularity = findViewById(R.id.rb_diet_regularity);
        rbStressIndex = findViewById(R.id.rb_stress_index);
        tvPortraitSummary = findViewById(R.id.tv_portrait_summary);
        tvRecentTrend = findViewById(R.id.tv_recent_trend);
        btnEditTodayArchive = findViewById(R.id.btn_edit_today_archive);
        btnSaveArchive = findViewById(R.id.btn_save_archive);
    }

    private void bindLatestEntry() {
        HealthArchiveEntry todayEntry = findTodayEntry();
        if (todayEntry != null) {
            applyEntryToRatings(todayEntry);
            setEditMode(false);
            tvTodayStatus.setText("今日健康状态已保存，可点击“编辑今日健康状态”后修改并重新保存。");
        } else {
            setDefaultRatings();
            setEditMode(true);
            tvTodayStatus.setText("今日还未评分，请先编辑今日健康状态并保存。");
        }
        refreshSummary();
    }

    private void saveTodayArchive() {
        if (!isEditMode) {
            Toast.makeText(this, "请先点击“编辑今日健康状态”后再修改评分", Toast.LENGTH_SHORT).show();
            return;
        }

        HealthArchiveEntry entry = buildEntryFromInput();
        if (entry == null) {
            Toast.makeText(this, "请完成 5 项健康自评后再保存", Toast.LENGTH_SHORT).show();
            return;
        }

        HealthArchiveStorage.saveOrUpdateEntry(this, entry);
        Toast.makeText(this, "今日健康档案已保存", Toast.LENGTH_SHORT).show();
        tvTodayStatus.setText("今日健康状态已保存，可再次编辑并覆盖今天的评分。");
        setEditMode(false);
        refreshSummary();
        maybeShowRiskWarning();
    }

    private HealthArchiveEntry buildEntryFromInput() {
        int mentalState = readRating(rbMentalState);
        int bodyPain = readRating(rbBodyPain);
        int sleepQuality = readRating(rbSleepQuality);
        int dietRegularity = readRating(rbDietRegularity);
        int stressIndex = readRating(rbStressIndex);
        if (mentalState == 0 || bodyPain == 0 || sleepQuality == 0
                || dietRegularity == 0 || stressIndex == 0) {
            return null;
        }

        HealthArchiveEntry entry = new HealthArchiveEntry();
        entry.setDate(dateFormat.format(new Date()));
        entry.setMentalState(mentalState);
        entry.setBodyPain(bodyPain);
        entry.setSleepQuality(sleepQuality);
        entry.setDietRegularity(dietRegularity);
        entry.setStressIndex(stressIndex);
        return entry;
    }

    private int readRating(RatingBar ratingBar) {
        return Math.round(ratingBar.getRating());
    }

    private void refreshSummary() {
        List<HealthArchiveEntry> entries = HealthArchiveStorage.loadEntries(this);
        HealthArchiveEntry latest = entries.isEmpty() ? null : entries.get(0);
        tvPortraitSummary.setText(HealthArchiveStorage.buildPortraitSummary(latest));
        tvRecentTrend.setText(HealthArchiveStorage.buildRecentTrend(entries));
    }

    private void enableTodayEdit() {
        if (findTodayEntry() == null) {
            setDefaultRatings();
        }
        setEditMode(true);
        tvTodayStatus.setText("正在编辑今日健康状态，修改评分后点击“保存今日健康档案”。");
        Toast.makeText(this, "已开启今日健康状态编辑", Toast.LENGTH_SHORT).show();
    }

    private HealthArchiveEntry findTodayEntry() {
        String today = dateFormat.format(new Date());
        List<HealthArchiveEntry> entries = HealthArchiveStorage.loadEntries(this);
        for (HealthArchiveEntry entry : entries) {
            if (today.equals(entry.getDate())) {
                return entry;
            }
        }
        return null;
    }

    private void applyEntryToRatings(HealthArchiveEntry entry) {
        rbMentalState.setRating(entry.getMentalState());
        rbBodyPain.setRating(entry.getBodyPain());
        rbSleepQuality.setRating(entry.getSleepQuality());
        rbDietRegularity.setRating(entry.getDietRegularity());
        rbStressIndex.setRating(entry.getStressIndex());
    }

    private void setDefaultRatings() {
        rbMentalState.setRating(3);
        rbBodyPain.setRating(3);
        rbSleepQuality.setRating(3);
        rbDietRegularity.setRating(3);
        rbStressIndex.setRating(3);
    }

    private void setEditMode(boolean editMode) {
        isEditMode = editMode;
        setRatingEditable(rbMentalState, editMode);
        setRatingEditable(rbBodyPain, editMode);
        setRatingEditable(rbSleepQuality, editMode);
        setRatingEditable(rbDietRegularity, editMode);
        setRatingEditable(rbStressIndex, editMode);
        btnSaveArchive.setEnabled(editMode);
        btnSaveArchive.setAlpha(editMode ? 1f : 0.6f);
    }

    private void setRatingEditable(RatingBar ratingBar, boolean editable) {
        ratingBar.setIsIndicator(!editable);
        ratingBar.setEnabled(editable);
        ratingBar.setClickable(editable);
        ratingBar.setFocusable(editable);
    }

    private void maybeShowRiskWarning() {
        if (!HealthArchiveStorage.shouldShowRiskWarning(this)) {
            return;
        }
        HealthArchiveStorage.HealthRiskResult riskResult =
                HealthArchiveStorage.evaluateRisk(HealthArchiveStorage.loadEntries(this));
        if (!riskResult.isTriggered()) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("身体超负荷，请合理规划作息，必要时及时就医。\n\n连续 3 天低分项：");
        for (int i = 0; i < riskResult.getDimensions().size(); i++) {
            builder.append(riskResult.getDimensions().get(i));
            if (i < riskResult.getDimensions().size() - 1) {
                builder.append("、");
            }
        }
        builder.append("\n建议优先减少熬夜、适度运动，并关注最近三天的低分指标。");

        new AlertDialog.Builder(this)
                .setTitle("温馨提示")
                .setMessage(builder.toString())
                .setPositiveButton("知道了", null)
                .show();
        HealthArchiveStorage.markRiskWarningShown(this, riskResult.getLatestDate());
    }
}
