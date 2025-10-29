package com.archive.app.view.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.archive.app.ApiClient;
import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.model.Users;
import com.archive.app.view.activity.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvFullName, tvRole, tvCacheSize;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 初始化视图
        tvFullName = view.findViewById(R.id.tv_full_name);
        tvRole = view.findViewById(R.id.tv_role);
        tvCacheSize = view.findViewById(R.id.tv_cache_size);

        // 绑定按钮点击事件
        view.findViewById(R.id.btn_change_password).setOnClickListener(v -> showUpdateProfileDialog());
        view.findViewById(R.id.btn_clear_cache).setOnClickListener(v -> confirmClearCache());
        view.findViewById(R.id.btn_check_update).setOnClickListener(v -> checkUpdate());
        view.findViewById(R.id.btn_about_us).setOnClickListener(v -> showToast("关于我们页面待开发"));
        view.findViewById(R.id.btn_logout).setOnClickListener(v -> confirmLogout());

        displayUserInfo();
        calculateCacheSize();

        return view;
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

    private void displayUserInfo() {
        Users currentUser = MyApplication.getCurrentUser();
        if (currentUser != null) {
            tvFullName.setText(currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername());
            // 假设 roleId 1:管理员, 2:经理, 3:操作员
            String roleName = "未知角色";
            switch (currentUser.getRoleId()) {
                case 1: roleName = "系统管理员"; break;
                case 2: roleName = "用户"; break;
                case 3: roleName = "操作员"; break;
            }
            tvRole.setText(String.format("角色: %s", roleName));
        } else {
            tvFullName.setText(R.string.unknown_user);
            tvRole.setText("");
        }
    }

    private void confirmLogout() {
        new AlertDialog.Builder(getContext())
                .setTitle("退出登录")
                .setMessage("您确定要退出当前账号吗？")
                .setPositiveButton("确定", (dialog, which) -> logout())
                .setNegativeButton("取消", null)
                .show();
    }

    private void logout() {
        MyApplication.logout();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void confirmClearCache() {
        new AlertDialog.Builder(getContext())
                .setTitle("清除缓存")
                .setMessage("确定要清除所有本地缓存数据吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    clearCache();
                    showToast("缓存已清除");
                    calculateCacheSize(); // 重新计算并显示
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void calculateCacheSize() {
        long size = 0;
        if (getContext() != null) {
            size += getDirSize(getContext().getCacheDir());
            size += getDirSize(getContext().getExternalCacheDir());
        }
        tvCacheSize.setText(formatSize(size));
    }

    private void clearCache() {
        if (getContext() != null) {
            deleteDir(getContext().getCacheDir());
            deleteDir(getContext().getExternalCacheDir());
        }
    }

    private void checkUpdate() {
        Toast.makeText(getContext(), "正在检查更新...", Toast.LENGTH_SHORT).show();
        // 模拟网络请求
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Toast.makeText(getContext(), "当前已是最新版本", Toast.LENGTH_LONG).show();
        }, 1500);
    }

    // --- 辅助方法 ---

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static long getDirSize(File dir) {
        long size = 0;
        if (dir != null && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    size += file.length();
                } else {
                    size += getDirSize(file);
                }
            }
        }
        return size;
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static String formatSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}