
        package com.archive.app.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.archive.app.ApiClient;
import com.archive.app.ApiService;
import com.archive.app.R;
import com.archive.app.model.Departments;
import com.archive.app.model.Users;
import com.google.android.material.textfield.TextInputLayout; // 导入 TextInputLayout

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPassword, fullName;
    private TextInputLayout tilPassword; // 新增：密码外层布局用于显示错误
    private TextView tvPasswordStrength; // 新增：强度文字
    private ProgressBar pbPasswordStrength; // 新增：强度条

    private RadioGroup rgRole;
    private Button btnRegister;
    private static final String TAG = "RegisterActivity";
    private ApiService apiService = ApiClient.getApiService();

    private AutoCompleteTextView departmentDropdown;
    private List<Departments> departmentList;
    private int selectedDepartmentId = -1; // 初始化为 -1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar);

        initViews(); // 将 findViewById 抽取出来整理
        fetchDepartments();
        setupPasswordListener(); // 新增：设置密码监听

        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_register_username);
        etPassword = findViewById(R.id.et_register_password);
        // 如果 XML 中添加了 TextInputLayout，这里绑定它；如果没有，请在 XML 添加并绑定
        tilPassword = findViewById(R.id.til_password);

        // 绑定新增的强度提示控件
        tvPasswordStrength = findViewById(R.id.tv_password_strength_text);
        pbPasswordStrength = findViewById(R.id.pb_password_strength);

        rgRole = findViewById(R.id.rg_register_role);
        btnRegister = findViewById(R.id.btn_register);
        departmentDropdown = findViewById(R.id.actv_department);
        fullName = findViewById(R.id.et_register_fullname);
    }

    /**
     * 设置密码输入监听，实时反馈强度
     */
    private void setupPasswordListener() {
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();
                updatePasswordStrengthUI(password);

                // 当用户开始修改时，清除之前的错误提示
                if (tilPassword != null) {
                    tilPassword.setError(null);
                }
            }
        });
    }

    /**
     * 根据密码内容更新 UI (进度条颜色、文字)
     */
    private void updatePasswordStrengthUI(String password) {
        if (password.isEmpty()) {
            tvPasswordStrength.setText("密码强度: 未输入");
            tvPasswordStrength.setTextColor(Color.GRAY);
            pbPasswordStrength.setProgress(0);
            return;
        }

        int score = calculatePasswordScore(password);

        switch (score) {
            case 0: // 不符合基本要求
            case 1: // 弱
                tvPasswordStrength.setText("密码强度: 弱 (长度需8位且包含2种字符)");
                tvPasswordStrength.setTextColor(Color.RED);
                pbPasswordStrength.setProgress(1);
                setProgressBarColor(Color.RED);
                break;
            case 2: // 中
                tvPasswordStrength.setText("密码强度: 中");
                tvPasswordStrength.setTextColor(Color.parseColor("#FFA500")); // Orange
                pbPasswordStrength.setProgress(2);
                setProgressBarColor(Color.parseColor("#FFA500"));
                break;
            case 3: // 强
                tvPasswordStrength.setText("密码强度: 强");
                tvPasswordStrength.setTextColor(Color.parseColor("#4CAF50")); // Green
                pbPasswordStrength.setProgress(3);
                setProgressBarColor(Color.parseColor("#4CAF50"));
                break;
        }
    }

    private void setProgressBarColor(int color) {
        if (pbPasswordStrength.getProgressDrawable() != null) {
            pbPasswordStrength.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    /**
     * 计算密码分数
     * 0: 不合格
     * 1: 弱 (稍微有点内容但没达标，或者刚好达标边缘)
     * 2: 中
     * 3: 强
     */
    private int calculatePasswordScore(String password) {
        int length = password.length();
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");

        int types = (hasLetter ? 1 : 0) + (hasDigit ? 1 : 0) + (hasSpecial ? 1 : 0);

        // 基本规则：长度至少8位，且包含至少两种字符
        if (length < 8 || types < 2) {
            return 1; // 视为弱/不合格
        }

        // 如果满足基本条件：
        if (length >= 10 && types >= 3) {
            return 3; // 强
        } else if (length >= 8 && types >= 2) {
            return 2; // 中 (也是合格的)
        }

        return 1;
    }

    /**
     * 验证密码是否严格符合业务规则
     */
    private boolean isPasswordValid(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        int types = 0;
        if (password.matches(".*[a-zA-Z].*")) types++;
        if (password.matches(".*\\d.*")) types++;
        if (password.matches(".*[^a-zA-Z0-9].*")) types++; // 特殊符号

        return types >= 2;
    }

    private void handleRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fullNameStr = fullName.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // **修改点：严格校验密码规则**
        if (!isPasswordValid(password)) {
            String errorMsg = "密码长度至少8位，且必须包含至少两种不同类型的字符（如字母+数字）";
            if (tilPassword != null) {
                tilPassword.setError(errorMsg);
            } else {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
            etPassword.requestFocus();
            return;
        }

        // **校验部门**
        if (selectedDepartmentId == -1) {
            TextInputLayout tilDept = findViewById(R.id.til_department);
            if (tilDept != null) tilDept.setError("请选择一个部门");
            return;
        } else {
            TextInputLayout tilDept = findViewById(R.id.til_department);
            if (tilDept != null) tilDept.setError(null);
        }

        Users request = new Users();
        request.setUsername(username);
        request.setPasswordHash(password);
        request.setFullName(fullNameStr.isEmpty() ? username : fullNameStr); // 修复原代码覆盖逻辑
        // 注意：原代码 role 逻辑是 user->student(2?), admin->admin(?). 此处保留原样，假设 setRoleId(2) 是默认逻辑
        // 原代码逻辑有点混淆：String role变量被定义了但没完全用上，下面直接 setRoleId(2)。
        // 假设你要根据 RadioGroup 设置：
        int roleId =  2 ; // 假设 2是student, 1是admin
        request.setRoleId(roleId);

        request.setActive(false);
        request.setDepartmentId(selectedDepartmentId);

        apiService.createUser(request).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null && response.body()) {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "Register failed: " + response.code());
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, "Network error: ", t);
                Toast.makeText(RegisterActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDepartments() {
        ApiClient.getApiService().listDepartments().enqueue(new Callback<List<Departments>>() {
            @Override
            public void onResponse(Call<List<Departments>> call, Response<List<Departments>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    departmentList = response.body();
                    populateDepartmentDropdown();
                } else {
                    Toast.makeText(RegisterActivity.this, "加载部门列表失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Departments>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateDepartmentDropdown() {
        if (departmentList == null || departmentList.isEmpty()) return;

        List<String> departmentNames = departmentList.stream()
                .map(Departments::getName)
                .collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                departmentNames
        );

        departmentDropdown.setAdapter(adapter);
        departmentDropdown.setOnItemClickListener((parent, view, position, id) -> {
            Departments selectedDepartment = departmentList.get(position);
            selectedDepartmentId = selectedDepartment.getId();
            // 清除部门的错误提示
            TextInputLayout tilDept = findViewById(R.id.til_department);
            if (tilDept != null) tilDept.setError(null);
        });
    }
}
