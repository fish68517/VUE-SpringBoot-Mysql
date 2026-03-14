package com.hakimi.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hakimi.R;

public class VirtualFitnessActivity extends AppCompatActivity {

    private static final String PREF_NAME = "virtual_fitness_pref";
    private static final String KEY_LAST_GOAL = "last_goal";
    private static final String KEY_LAST_PLAN = "last_plan";

    private EditText etGoalInput;
    private Button btnSubmitGoal;
    private TextView tvGeneratedPlan;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_fitness);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        initViews();
        loadLastData();
        setupListeners();
    }

    private void initViews() {
        etGoalInput = findViewById(R.id.et_fitness_goal);
        btnSubmitGoal = findViewById(R.id.btn_submit_goal);
        tvGeneratedPlan = findViewById(R.id.tv_fitness_plan_result);
    }

    private void loadLastData() {
        String lastGoal = sharedPreferences.getString(KEY_LAST_GOAL, "");
        String lastPlan = sharedPreferences.getString(KEY_LAST_PLAN, "");
        etGoalInput.setText(lastGoal);
        if (!TextUtils.isEmpty(lastPlan)) {
            tvGeneratedPlan.setText(lastPlan);
        }
    }

    private void setupListeners() {
        btnSubmitGoal.setOnClickListener(v -> submitGoal());
    }

    private void submitGoal() {
        String goal = etGoalInput.getText().toString().trim();
        if (TextUtils.isEmpty(goal)) {
            Toast.makeText(this, "\u8bf7\u8f93\u5165\u5065\u8eab\u76ee\u6807", Toast.LENGTH_SHORT).show();
            return;
        }

        String generatedPlan = generatePlaceholderPlan(goal);
        tvGeneratedPlan.setText(generatedPlan);

        sharedPreferences.edit()
                .putString(KEY_LAST_GOAL, goal)
                .putString(KEY_LAST_PLAN, generatedPlan)
                .apply();

        Toast.makeText(this, "\u5df2\u751f\u6210\u5e76\u4fdd\u5b58\u8ba1\u5212", Toast.LENGTH_SHORT).show();
    }

    // Placeholder logic for AI plan generation. Replace with real model API later.
    private String generatePlaceholderPlan(String goal) {
        String lower = goal.toLowerCase();
        if (lower.contains("\u51cf\u8102") || lower.contains("\u51cf\u91cd") || lower.contains("fat")) {
            return "\u3010\u865a\u62dfAI\u51cf\u8102\u8ba1\u5212\uff087\u5929\uff09\u3011\n"
                    + "1. \u5468\u4e00/\u4e09/\u4e94\uff1a30\u5206\u949f\u6162\u8dd1 + 15\u5206\u949f\u6838\u5fc3\u8bad\u7ec3\n"
                    + "2. \u5468\u4e8c/\u56db\uff1a20\u5206\u949fHIIT + 10\u5206\u949f\u62c9\u4f38\n"
                    + "3. \u5468\u672b\uff1a60\u5206\u949f\u5feb\u8d70\u6216\u9a91\u884c\n"
                    + "4. \u996e\u98df\uff1a\u51cf\u5c11\u542b\u7cd6\u996e\u6599\uff0c\u6bcf\u5929\u6c34\u6444\u51652L";
        }
        if (lower.contains("\u589e\u808c") || lower.contains("muscle")) {
            return "\u3010\u865a\u62dfAI\u589e\u808c\u8ba1\u5212\uff087\u5929\uff09\u3011\n"
                    + "1. \u5468\u4e00\uff1a\u80f8+\u4e09\u5934\uff0c\u6bcf\u4e2a\u52a8\u4f5c4\u7ec4\n"
                    + "2. \u5468\u4e09\uff1a\u80cc+\u4e8c\u5934\uff0c\u6bcf\u4e2a\u52a8\u4f5c4\u7ec4\n"
                    + "3. \u5468\u4e94\uff1a\u817f+\u80a9\uff0c\u6bcf\u4e2a\u52a8\u4f5c4\u7ec4\n"
                    + "4. \u6bcf\u6b21\u8bad\u7ec3\u540e\u8865\u5145\u86cb\u767d\u8d28\uff0c\u4fdd\u8bc17\u5c0f\u65f6\u7761\u7720";
        }
        if (lower.contains("\u8010\u529b") || lower.contains("endurance")) {
            return "\u3010\u865a\u62dfAI\u8010\u529b\u8ba1\u5212\uff087\u5929\uff09\u3011\n"
                    + "1. \u5468\u4e00/\u4e09/\u4e94\uff1a40\u5206\u949f\u6709\u6c27\u8dd1\n"
                    + "2. \u5468\u4e8c/\u56db\uff1a\u95f4\u6b47\u8dd1 10\u7ec4\uff08\u5feb1\u5206\u949f+\u61621\u5206\u949f\uff09\n"
                    + "3. \u6bcf\u5929\u52a0\u5165 10\u5206\u949f\u547c\u5438\u8bad\u7ec3\u548c\u62c9\u4f38";
        }
        return "\u3010\u865a\u62dfAI\u901a\u7528\u8ba1\u5212\uff087\u5929\uff09\u3011\n"
                + "\u76ee\u6807\uff1a" + goal + "\n"
                + "1. \u6bcf\u5468\u8bad\u7ec34\u5929\uff0c\u6bcf\u6b2145\u5206\u949f\n"
                + "2. \u6709\u6c27\u4e0e\u529b\u91cf\u7ed3\u5408\uff0c\u5faa\u5e8f\u6e10\u8fdb\n"
                + "3. \u8fd0\u52a8\u540e\u8fdb\u884c\u62c9\u4f3810\u5206\u949f\uff0c\u6bcf\u5929\u8bb0\u5f55\u72b6\u6001";
    }
}
