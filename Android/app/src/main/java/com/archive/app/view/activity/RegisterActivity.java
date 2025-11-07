package com.archive.app.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.archive.app.ApiClient;
import com.archive.app.ApiService;

import com.archive.app.R;


import com.archive.app.model.Departments;
import com.archive.app.model.Users;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private RadioGroup rgRole;
    private Button btnRegister;
    private static final String TAG = "RegisterActivity";
    private ApiService apiService = ApiClient.getApiService();
    private EditText fullName;


    private AutoCompleteTextView departmentDropdown;
    private List<Departments> departmentList;
    private int selectedDepartmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar);

        etUsername = findViewById(R.id.et_register_username);
        etPassword = findViewById(R.id.et_register_password);
        rgRole = findViewById(R.id.rg_register_role);
        btnRegister = findViewById(R.id.btn_register);
        departmentDropdown = findViewById(R.id.actv_department);

        fullName = findViewById(R.id.et_register_fullname);


        fetchDepartments();
        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String fullNameq = fullName.getText().toString().trim();
            String role = rgRole.getCheckedRadioButtonId() == R.id.rb_register_user ? "student" : "admin";

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "账号和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            Users request = new Users();
            request.setUsername(username);
            request.setPasswordHash(password);
            request.setFullName(fullNameq);
            request.setRoleId(2);
            request.setActive(false);
            request.setFullName(username);
            // **7. 验证用户是否已选择部门**
            if (selectedDepartmentId == -1) {
                // 在 TextInputLayout 上显示错误提示
                ((com.google.android.material.textfield.TextInputLayout) findViewById(R.id.til_department))
                        .setError("请选择一个部门");
                return;
            } else {
                // 清除错误提示
                ((com.google.android.material.textfield.TextInputLayout) findViewById(R.id.til_department))
                        .setError(null);
            }
            request.setDepartmentId(selectedDepartmentId);

            apiService.createUser(request).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Boolean loggedInUser = response.body();
                        if (loggedInUser) {
                            // Check if the user role is '操作员' (Operator) - assuming roleId 3 is Operator
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish(); //
                        } else {
                            // Handle unsuccessful login (e.g., 401 Unauthorized)
                            Log.e(TAG, "Login failed: " + response.code() + " - " + response.message());
                            Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        // Handle unsuccessful response (e.g., 401 Unauthorized)
                        Log.e(TAG, "Login failed: " + response.code() + " - " + response.message());
                        Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {

                    Log.e(TAG, "Network error: ", t);
                    Toast.makeText(RegisterActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    /**
     * 使用 Retrofit 从服务器异步获取部门列表
     */
    private void fetchDepartments() {
        ApiClient.getApiService().listDepartments().enqueue(new Callback<List<Departments>>() {
            @Override
            public void onResponse(Call<List<Departments>> call, Response<List<Departments>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 2. 获取成功，保存列表并填充到下拉框
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

    /**
     * 将获取到的部门名称填充到 AutoCompleteTextView 中
     */
    private void populateDepartmentDropdown() {
        if (departmentList == null || departmentList.isEmpty()) {
            return;
        }

        Log.d(TAG, "Department List: " + departmentList);
        // 3. 提取所有部门的名称用于显示
        List<String> departmentNames = departmentList.stream()
                .map(Departments::getName)
                .collect(Collectors.toList());

        // 4. 创建一个简单的 ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                departmentNames
        );

        // 5. 将 Adapter 设置给下拉框
        departmentDropdown.setAdapter(adapter);

        // 6. 监听用户的选择事件，以获取选中项的 ID
        departmentDropdown.setOnItemClickListener((parent, view, position, id) -> {
            // position 是用户在下拉列表中点击的位置
            // 我们用这个 position 从原始的 departmentList 中找到对应的对象
            Departments selectedDepartment = departmentList.get(position);
            // 保存选中的部门ID
            selectedDepartmentId = selectedDepartment.getId();
            Toast.makeText(this, "已选择: " + selectedDepartment.getName(), Toast.LENGTH_SHORT).show();
        });
    }
}