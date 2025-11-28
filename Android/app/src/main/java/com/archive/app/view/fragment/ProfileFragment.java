package com.archive.app.view.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.archive.app.ApiClient;
import com.archive.app.ApiService;
import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.RetrofitClient;
import com.archive.app.model.TransactionLogs;
import com.archive.app.model.Users;
import com.archive.app.view.activity.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvWelcomeUser;
    private TextView tvTodayInbound;
    private TextView tvTodayOutbound;
    private TextView tvTodayCheck;
    private Button btnLogout;
    private ApiService apiService;

    private static final String TAG = "ProfileFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 1. 初始化视图控件
        tvWelcomeUser = view.findViewById(R.id.tv_full_name);
        tvTodayInbound = view.findViewById(R.id.tv_today_inbound);
        tvTodayOutbound = view.findViewById(R.id.tv_today_outbound);
        tvTodayCheck = view.findViewById(R.id.tv_today_check);
        btnLogout = view.findViewById(R.id.btn_logout);

        // 2. 获取 ApiService 实例
        apiService = RetrofitClient.getApiService();

        // 3. 设置按钮点击事件
        btnLogout.setOnClickListener(v -> logout());


        // 绑定按钮点击事件
        view.findViewById(R.id.btn_change_password).setOnClickListener(v -> showUpdateProfileDialog());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次 Fragment 可见时刷新数据
        displayUserInfo();
        fetchTodayStatistics();
    }

    /**
     * 显示当前登录用户信息
     */
    private void displayUserInfo() {
        Users currentUser = MyApplication.getCurrentUser();
        if (currentUser != null) {
            // 优先显示全名，如果没有则显示用户名
            String displayName = currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername();
            tvWelcomeUser.setText(getString(R.string.welcome_user, displayName));
        } else {
            tvWelcomeUser.setText(R.string.unknown_user);
        }
    }

    /**
     * 获取并计算今日统计数据
     */
    private void fetchTodayStatistics() {
        // 获取今日日期字符串，格式为 yyyy-MM-dd，用于匹配日志创建时间
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        apiService.getTransactionLogs().enqueue(new Callback<List<TransactionLogs>>() {
            @Override
            public void onResponse(Call<List<TransactionLogs>> call, Response<List<TransactionLogs>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TransactionLogs> logs = response.body();
                    Log.d(TAG, "Loaded " + logs.size() + " transaction logs");
                    calculateAndShowStats(logs, todayDate);
                } else {
                    Log.e(TAG, "Failed to load stats: " + response.code());
                    // 可选：显示错误提示或重置数字为0
                }
            }

            @Override
            public void onFailure(Call<List<TransactionLogs>> call, Throwable t) {
                Log.e(TAG, "Network error loading stats", t);
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 根据日志列表计算今日数据并更新 UI
     */
    private void calculateAndShowStats(List<TransactionLogs> logs, String todayDate) {
        int inboundCount = 0;
        int outboundCount = 0;
        int checkCount = 0;

        int userId = MyApplication.getCurrentUser() != null ? MyApplication.getCurrentUser().getId() : -1;
        if (userId != -1) {
            for (TransactionLogs log : logs) {
                if (log.getUserId() == userId) {
                    switch (log.getType()) {
                        case "入库":
                            inboundCount++; // 统计入库笔数
                            break;
                        case "出库":
                            outboundCount++; // 统计出库笔数
                            break;
                        case "盘点":
                            // 数据库中 '调整' 类型通常对应 '盘点' 差异处理
                            checkCount++;
                            break;
                    }
                }
            }
        }

       /* for (TransactionLogs log : logs) {
            // 确保时间字段和类型字段不为空
            if (log.getCreatedAt() != null && log.getType() != null) {
                // 检查是否是今天的记录 (通过字符串前缀匹配 yyyy-MM-dd)
                switch (log.getType()) {
                    case "入库":
                        inboundCount++; // 统计入库笔数
                        break;
                    case "出库":
                        outboundCount++; // 统计出库笔数
                        break;
                    case "调整":
                        // 数据库中 '调整' 类型通常对应 '盘点' 差异处理
                        checkCount++;
                        break;
                }
*/


              /*  if (log.getCreatedAt().startsWith(todayDate)) {
                    switch (log.getType()) {
                        case "入库":
                            inboundCount++; // 统计入库笔数
                            break;
                        case "出库":
                            outboundCount++; // 统计出库笔数
                            break;
                        case "调整":
                            // 数据库中 '调整' 类型通常对应 '盘点' 差异处理
                            checkCount++;
                            break;
                    }
                }*/
        //}
        //  }

        // 更新 UI
        tvTodayInbound.setText(String.valueOf(inboundCount));
        tvTodayOutbound.setText(String.valueOf(outboundCount));
        tvTodayCheck.setText(String.valueOf(checkCount));
    }

    /**
     * 退出登录逻辑
     */
    private void logout() {
        // MyApplication.logout(); // 清除全局用户状态

        // 跳转回登录页面，并清空 Activity 栈，防止用户按返回键回来
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }



    /**
     * 显示修改个人信息的对话框
     */
    private void showUpdateProfileDialog() {
        Users currentUser = MyApplication.getCurrentUser();
        if (currentUser == null) return;

        // 1. 加载自定义布局
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_profile, null);
        TextInputEditText etUsername = dialogView.findViewById(R.id.et_dialog_username);
        TextInputEditText etPassword = dialogView.findViewById(R.id.et_dialog_password);

        // 2. 回显当前的用户名
        etUsername.setText(currentUser.getUsername());

        // 3. 创建对话框
        new AlertDialog.Builder(getContext())
                .setTitle("修改个人信息")
                .setView(dialogView)
                .setPositiveButton("保存", null) // 先设为 null，稍后在 onShow 中覆盖，防止自动关闭
                .setNegativeButton("取消", null)
                .create();

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("修改个人信息")
                .setView(dialogView)
                .setPositiveButton("保存", null)
                .setNegativeButton("取消", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                // 4. 获取输入内容
                String newUsername = etUsername.getText().toString().trim();
                String newPassword = etPassword.getText().toString().trim();

                // 5. 简单验证
                if (TextUtils.isEmpty(newUsername)) {
                    etUsername.setError("用户名不能为空");
                    return;
                }

                // 6. 执行更新操作
                performUpdateProfile(currentUser.getId(), newUsername, newPassword, dialog);
            });
        });

        dialog.show();
    }

    /**
     * 调用 API 更新用户信息
     */
    private void performUpdateProfile(int userId, String username, String password, AlertDialog dialog) {
        // 准备请求数据
        Users updateRequest = new Users();
        updateRequest.setId(userId);
        updateRequest.setUsername(username);
        // 如果用户输入了新密码，则设置；否则留空（假设后端不更新空字段）
        if (!TextUtils.isEmpty(password)) {
            updateRequest.setPasswordHash(password);
        }

        // 调用 API
        ApiClient.getApiService().updateUser(updateRequest).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "修改成功，请重新登录", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    // 修改关键信息后，强制用户重新登录是一个好的安全实践
                    logout();
                } else {
                    Toast.makeText(getContext(), "修改成功，请重新登录", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    // 修改关键信息后，强制用户重新登录是一个好的安全实践
                    logout();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(getContext(), "修改成功，请重新登录", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                // 修改关键信息后，强制用户重新登录是一个好的安全实践
                logout();
            }
        });
    }


    private void confirmLogout() {
        new AlertDialog.Builder(getContext())
                .setTitle("退出登录")
                .setMessage("您确定要退出当前账号吗？")
                .setPositiveButton("确定", (dialog, which) -> logout())
                .setNegativeButton("取消", null)
                .show();
    }

/*    private void logout() {
        MyApplication.logout();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }*/



}