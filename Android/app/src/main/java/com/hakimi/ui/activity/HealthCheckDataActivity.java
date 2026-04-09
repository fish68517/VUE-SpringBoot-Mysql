package com.hakimi.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hakimi.R;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HealthCheckDataActivity extends AppCompatActivity {

    private static final String PREF_NAME = "health_check_data_pref";
    private static final String KEY_RECORDS = "health_check_records";

    private final Gson gson = new Gson();
    private final Type recordListType = new TypeToken<List<HealthCheckRecord>>() {
    }.getType();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    private EditText etWeight;
    private EditText etHeight;
    private EditText etSystolic;
    private EditText etDiastolic;
    private EditText etHeartRate;
    private EditText etTemperature;
    private TextView tvLatestRecord;
    private TextView tvBmiResult;
    private TextView tvHealthStatus;
    private TextView tvIndexAnalysis;
    private TextView tvRecordHistory;
    private Button btnSaveHealthCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_check_data);

        initViews();
        btnSaveHealthCheck.setOnClickListener(v -> saveRecord());
        renderLatestRecord();
    }

    private void initViews() {
        etWeight = findViewById(R.id.et_weight);
        etHeight = findViewById(R.id.et_height);
        etSystolic = findViewById(R.id.et_systolic);
        etDiastolic = findViewById(R.id.et_diastolic);
        etHeartRate = findViewById(R.id.et_heart_rate);
        etTemperature = findViewById(R.id.et_temperature);
        tvLatestRecord = findViewById(R.id.tv_latest_record);
        tvBmiResult = findViewById(R.id.tv_bmi_result);
        tvHealthStatus = findViewById(R.id.tv_health_status);
        tvIndexAnalysis = findViewById(R.id.tv_index_analysis);
        tvRecordHistory = findViewById(R.id.tv_record_history);
        btnSaveHealthCheck = findViewById(R.id.btn_save_health_check);
    }

    private void saveRecord() {
        String weightText = etWeight.getText().toString().trim();
        String heightText = etHeight.getText().toString().trim();
        String systolicText = etSystolic.getText().toString().trim();
        String diastolicText = etDiastolic.getText().toString().trim();
        String heartRateText = etHeartRate.getText().toString().trim();
        String temperatureText = etTemperature.getText().toString().trim();

        if (TextUtils.isEmpty(weightText) || TextUtils.isEmpty(heightText)
                || TextUtils.isEmpty(systolicText) || TextUtils.isEmpty(diastolicText)
                || TextUtils.isEmpty(heartRateText) || TextUtils.isEmpty(temperatureText)) {
            Toast.makeText(this, "请先填写完整的检测数据", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float weight = Float.parseFloat(weightText);
            float height = Float.parseFloat(heightText);
            int systolic = Integer.parseInt(systolicText);
            int diastolic = Integer.parseInt(diastolicText);
            int heartRate = Integer.parseInt(heartRateText);
            float temperature = Float.parseFloat(temperatureText);

            if (!isValidInput(weight, height, systolic, diastolic, heartRate, temperature)) {
                return;
            }

            HealthCheckRecord record = new HealthCheckRecord();
            record.recordTime = dateFormat.format(new Date());
            record.weight = weight;
            record.height = height;
            record.systolic = systolic;
            record.diastolic = diastolic;
            record.heartRate = heartRate;
            record.temperature = temperature;
            record.bmi = calculateBmi(weight, height);

            saveRecordToLocal(record);
            bindRecord(record);
            tvRecordHistory.setText(buildHistoryText(loadRecords()));
            Toast.makeText(this, "检测数据已保存", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入正确的数字格式", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidInput(float weight, float height, int systolic, int diastolic,
            int heartRate, float temperature) {
        if (weight <= 0 || weight > 300) {
            Toast.makeText(this, "体重请填写合理的公斤数", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (height <= 0 || height > 250) {
            Toast.makeText(this, "身高请填写合理的厘米数", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (systolic < 50 || systolic > 250 || diastolic < 30 || diastolic > 180) {
            Toast.makeText(this, "血压值超出常见范围，请检查后重试", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (heartRate < 20 || heartRate > 220) {
            Toast.makeText(this, "心率值超出常见范围，请检查后重试", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (temperature < 34f || temperature > 43f) {
            Toast.makeText(this, "体温值超出常见范围，请检查后重试", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void renderLatestRecord() {
        List<HealthCheckRecord> records = loadRecords();
        if (records.isEmpty()) {
            tvLatestRecord.setText("最近一次录入：暂无");
            tvBmiResult.setText("BMI 指数：暂无");
            tvHealthStatus.setText("健康状态：等待录入后生成");
            tvIndexAnalysis.setText("指标说明：录入体重、身高、血压、心率、体温后自动生成。");
            tvRecordHistory.setText("最近记录：暂无");
            return;
        }

        HealthCheckRecord latest = records.get(0);
        bindRecord(latest);
        fillInputWithLatest(latest);
        tvRecordHistory.setText(buildHistoryText(records));
    }

    private void fillInputWithLatest(HealthCheckRecord latest) {
        etWeight.setText(formatDecimal(latest.weight));
        etHeight.setText(formatDecimal(latest.height));
        etSystolic.setText(String.valueOf(latest.systolic));
        etDiastolic.setText(String.valueOf(latest.diastolic));
        etHeartRate.setText(String.valueOf(latest.heartRate));
        etTemperature.setText(formatDecimal(latest.temperature));
    }

    private void bindRecord(HealthCheckRecord record) {
        tvLatestRecord.setText("最近一次录入：" + record.recordTime);
        tvBmiResult.setText("BMI 指数：" + formatOneDecimal(record.bmi) + "（" + resolveBmiLabel(record.bmi) + "）");
        tvHealthStatus.setText(buildBmiAdvice(record.bmi));
        tvIndexAnalysis.setText(buildIndexAnalysis(record));
    }

    private float calculateBmi(float weight, float heightCm) {
        float heightMeter = heightCm / 100f;
        return weight / (heightMeter * heightMeter);
    }

    private String resolveBmiLabel(float bmi) {
        if (bmi < 18.5f) {
            return "偏瘦";
        }
        if (bmi < 24f) {
            return "正常";
        }
        if (bmi < 28f) {
            return "超重";
        }
        return "肥胖";
    }

    private String buildBmiAdvice(float bmi) {
        if (bmi < 18.5f) {
            return "健康状态：偏瘦，建议增加优质蛋白摄入，配合力量训练提升体能。";
        }
        if (bmi < 24f) {
            return "健康状态：正常，建议保持当前饮食结构和作息节奏。";
        }
        if (bmi < 28f) {
            return "健康状态：超重，建议减少高糖高油食物，每周保持 3 次以上有氧运动。";
        }
        return "健康状态：肥胖，建议规律控制饮食并尽快建立持续运动计划，必要时咨询医生。";
    }

    private String buildIndexAnalysis(HealthCheckRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append("指标说明：\n");
        builder.append(buildBloodPressureAdvice(record.systolic, record.diastolic)).append('\n');
        builder.append(buildHeartRateAdvice(record.heartRate)).append('\n');
        builder.append(buildTemperatureAdvice(record.temperature));
        return builder.toString();
    }

    private String buildBloodPressureAdvice(int systolic, int diastolic) {
        if (systolic < 90 || diastolic < 60) {
            return "血压：" + systolic + "/" + diastolic
                    + " mmHg，偏低。建议避免久站、及时补水，若伴头晕乏力请及时就医。";
        }
        if (systolic > 140 || diastolic > 90) {
            return "血压：" + systolic + "/" + diastolic
                    + " mmHg，偏高。建议减少高盐饮食、避免熬夜，并持续监测。";
        }
        if (systolic >= 120 || diastolic >= 80) {
            return "血压：" + systolic + "/" + diastolic
                    + " mmHg，略偏高。建议近期加强休息、减少外卖和含糖饮料。";
        }
        return "血压：" + systolic + "/" + diastolic + " mmHg，处于正常范围。";
    }

    private String buildHeartRateAdvice(int heartRate) {
        if (heartRate < 60) {
            return "心率：" + heartRate + " 次/分，偏低。若近期疲劳明显或伴随头晕，建议尽快复查。";
        }
        if (heartRate > 100) {
            return "心率：" + heartRate + " 次/分，偏高。建议先休息、减少咖啡因摄入，必要时就医。";
        }
        return "心率：" + heartRate + " 次/分，处于正常范围。";
    }

    private String buildTemperatureAdvice(float temperature) {
        if (temperature < 36.1f) {
            return "体温：" + formatOneDecimal(temperature) + "℃，偏低。注意保暖并观察是否有乏力不适。";
        }
        if (temperature > 37.3f) {
            return "体温：" + formatOneDecimal(temperature) + "℃，偏高。建议休息补水，如持续发热请及时就医。";
        }
        return "体温：" + formatOneDecimal(temperature) + "℃，处于正常范围。";
    }

    private void saveRecordToLocal(HealthCheckRecord record) {
        List<HealthCheckRecord> records = loadRecords();
        records.add(record);
        Collections.sort(records, new Comparator<HealthCheckRecord>() {
            @Override
            public int compare(HealthCheckRecord left, HealthCheckRecord right) {
                String leftTime = left == null || left.recordTime == null ? "" : left.recordTime;
                String rightTime = right == null || right.recordTime == null ? "" : right.recordTime;
                return rightTime.compareTo(leftTime);
            }
        });
        getPrefs().edit().putString(KEY_RECORDS, gson.toJson(records)).apply();
    }

    private List<HealthCheckRecord> loadRecords() {
        String json = getPrefs().getString(KEY_RECORDS, "[]");
        List<HealthCheckRecord> records = gson.fromJson(json, recordListType);
        return records == null ? new ArrayList<>() : records;
    }

    private String buildHistoryText(List<HealthCheckRecord> records) {
        if (records.isEmpty()) {
            return "最近记录：暂无";
        }

        StringBuilder builder = new StringBuilder("最近记录：\n");
        int count = Math.min(records.size(), 5);
        for (int i = 0; i < count; i++) {
            HealthCheckRecord record = records.get(i);
            builder.append(record.recordTime)
                    .append("  BMI ")
                    .append(formatOneDecimal(record.bmi))
                    .append("  血压 ")
                    .append(record.systolic)
                    .append("/")
                    .append(record.diastolic);
            if (i < count - 1) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    private String formatOneDecimal(float value) {
        return String.format(Locale.getDefault(), "%.1f", value);
    }

    private String formatDecimal(float value) {
        if (Math.abs(value - Math.round(value)) < 0.01f) {
            return String.valueOf(Math.round(value));
        }
        return formatOneDecimal(value);
    }

    private SharedPreferences getPrefs() {
        return getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static class HealthCheckRecord {
        String recordTime;
        float weight;
        float height;
        int systolic;
        int diastolic;
        int heartRate;
        float temperature;
        float bmi;
    }
}
