package com.archive.app.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.archive.app.ApiService;
import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.RetrofitClient;
import com.archive.app.model.BaseResponse;
import com.archive.app.model.CampusUser;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public static final String LOGIN_PREFS_NAME = "login_prefs";
    public static final String KEY_IS_REMEMBER = "is_remember";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_CURRENT_USER_ID = "current_user_id";

    private Toolbar toolbar;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private CheckBox cbRememberPassword;
    private Button btnLogin;
    private Button btnToRegister;

    private final ApiService apiService = RetrofitClient.getMainApiService();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(LOGIN_PREFS_NAME, MODE_PRIVATE);
        initViews();
        setupListeners();
        loadRememberedPassword();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_login);
        etUsername = findViewById(R.id.et_login_username);
        etPassword = findViewById(R.id.et_login_password);
        cbRememberPassword = findViewById(R.id.cb_remember_password);
        btnLogin = findViewById(R.id.btn_login);
        btnToRegister = findViewById(R.id.btn_to_register);
        setSupportActionBar(toolbar);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
        btnToRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void loadRememberedPassword() {
        boolean isRemember = sharedPreferences.getBoolean(KEY_IS_REMEMBER, false);
        if (isRemember) {
            etUsername.setText(sharedPreferences.getString(KEY_USERNAME, ""));
            etPassword.setText(sharedPreferences.getString(KEY_PASSWORD, ""));
            cbRememberPassword.setChecked(true);
        }
    }

    private void handleLogin() {
        String username = etUsername.getText() == null ? "" : etUsername.getText().toString().trim();
        String password = etPassword.getText() == null ? "" : etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        }

        CampusUser user = new CampusUser();
        user.setCampusEmailAddr(username);
        user.setPassword(password);
        apiService.login(user).enqueue(new Callback<BaseResponse<CampusUser>>() {
            @Override
            public void onResponse(Call<BaseResponse<CampusUser>> call, Response<BaseResponse<CampusUser>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(LoginActivity.this, "登录失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    return;
                }

                BaseResponse<CampusUser> wrapper = response.body();
                if (wrapper.getCode() != 200) {
                    Toast.makeText(LoginActivity.this, wrapper.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                CampusUser userFromDb = wrapper.getData();
                if (userFromDb == null) {
                    Log.e("LoginActivity", "Login succeeded but user data is null");
                    Toast.makeText(LoginActivity.this, "登录数据异常", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                handleRememberPassword(username, password);
                MyApplication.curUser = userFromDb;
                sharedPreferences.edit()
                        .putLong(KEY_CURRENT_USER_ID, userFromDb.getCampusUserId() == null ? -1L : userFromDb.getCampusUserId())
                        .apply();

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<BaseResponse<CampusUser>> call, Throwable t) {
                Log.e("LoginActivity", "Login request failed: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleRememberPassword(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (cbRememberPassword.isChecked()) {
            editor.putBoolean(KEY_IS_REMEMBER, true);
            editor.putString(KEY_USERNAME, username);
            editor.putString(KEY_PASSWORD, password);
        } else {
            editor.remove(KEY_IS_REMEMBER);
            editor.remove(KEY_USERNAME);
            editor.remove(KEY_PASSWORD);
        }
        editor.apply();
    }
}
