package com.archive.app.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.archive.app.ApiService;
import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.RetrofitClient;
import com.archive.app.dto.LoginRequest;
import com.archive.app.model.Users;



import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private ApiService apiService;

    private static final String TAG = "LoginActivity";
    private CheckBox rememberPasswordCheckBox;

    // 实现保存密码登录功能可以使用SharedPreferences，这里仅展示基础登录逻辑
    private void saveLoginInfo(String username, String password) {
        // Implement saving login info logic here
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_login_username);
        etPassword = findViewById(R.id.et_login_password);
        rememberPasswordCheckBox = findViewById(R.id.cb_remember_password);

        rememberPasswordCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    saveLoginInfo(etUsername.getText().toString(), etPassword.getText().toString());
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("username");
                    editor.remove("password");
                    editor.apply();
                }
            }

        });
        btnLogin = findViewById(R.id.btn_login);
        findViewById(R.id.btn_to_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        apiService = RetrofitClient.getApiService();

        btnLogin.setOnClickListener(v -> handleLogin());

        // 获取保存的登录信息
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");
        if (!TextUtils.isEmpty(savedUsername) && !TextUtils.isEmpty(savedPassword)) {
            etUsername.setText(savedUsername);
            etPassword.setText(savedPassword);
            rememberPasswordCheckBox.setChecked(true);
        } else {
            rememberPasswordCheckBox.setChecked(false);
        }
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true); // Show loading indicator

        LoginRequest loginRequest = new LoginRequest(username, password);
        apiService.login(loginRequest).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                showLoading(false); // Hide loading
                if (response.isSuccessful() && response.body() != null) {
                    Users loggedInUser = response.body();
                    // Check if the user role is '操作员' (Operator) - assuming roleId 3 is Operator
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                    // 将用户信息保存到 SharedPreferences 或全局状态中

                    saveLoginInfo(username, password);
                    MyApplication.setCurrentUser(loggedInUser);
                    startActivity(intent);
                    finish(); // Prevent returning to Login screen

                } else {
                    // Handle unsuccessful response (e.g., 401 Unauthorized)
                    Log.e(TAG, "Login failed: " + response.code() + " - " + response.message());
                    Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                showLoading(false); // Hide loading
                Log.e(TAG, "Network error: ", t);
                Toast.makeText(LoginActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Basic loading indicator handling (replace with a proper ProgressBar if needed)
    private void showLoading(boolean isLoading) {
        btnLogin.setEnabled(!isLoading);
        btnLogin.setText(isLoading ? R.string.loading : R.string.login);
    }
}
