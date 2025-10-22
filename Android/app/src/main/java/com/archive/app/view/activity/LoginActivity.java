package com.archive.app.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_login_username);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);

        apiService = RetrofitClient.getApiService();

        btnLogin.setOnClickListener(v -> handleLogin());
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
                    if (loggedInUser.getRoleId() == 3) {
                        MyApplication.setCurrentUser(loggedInUser); // Store user globally
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Prevent returning to Login screen
                    } else {
                        Toast.makeText(LoginActivity.this, "登录失败：非操作员禁止登录App", Toast.LENGTH_LONG).show();
                    }

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
