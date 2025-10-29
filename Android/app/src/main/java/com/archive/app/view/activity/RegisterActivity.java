package com.archive.app.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.archive.app.ApiClient;
import com.archive.app.ApiService;

import com.archive.app.R;



import com.archive.app.model.Users;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private RadioGroup rgRole;
    private Button btnRegister;
    private static final String TAG = "RegisterActivity";
    private ApiService apiService = ApiClient.getApiService();

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


        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String role = rgRole.getCheckedRadioButtonId() == R.id.rb_register_user ? "student" : "admin";

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "账号和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            Users request = new Users();
            request.setUsername(username);
            request.setPasswordHash(password);
            request.setRoleId(2);
            request.setActive(true);
            request.setFullName(username);
            request.setDepartmentId(1);
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
}