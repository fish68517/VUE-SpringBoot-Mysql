package com.archive.app.view.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.archive.app.ApiClient;
import com.archive.app.MyApplication;
import com.archive.app.databinding.ActivityScanBinding;
import com.archive.app.dto.ScanRequest;
import com.archive.app.dto.ScanResponse;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends AppCompatActivity {

    public static final String EXTRA_SCAN_TYPE = "EXTRA_SCAN_TYPE"; // "INBOUND" or "OUTBOUND"
    // (可选) 用于方案一模拟扫描的 key
    public static final String EXTRA_SIMULATED_SCAN_RESULT = "EXTRA_SIMULATED_SCAN_RESULT";

    private ActivityScanBinding binding;
    private CodeScanner mCodeScanner;
    private String scanType;


    // 1. 创建一个 ActivityResultLauncher 用于请求权限
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // 权限被授予，现在可以安全地启动扫描器了
                    Toast.makeText(this, "相机权限已授予", Toast.LENGTH_SHORT).show();
                    setupScanner();
                    mCodeScanner.startPreview(); // 别忘了在这里也启动预览
                } else {
                    // 权限被拒绝，告知用户并关闭页面
                    Toast.makeText(this, "需要相机权限才能进行扫描", Toast.LENGTH_LONG).show();
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        scanType = getIntent().getStringExtra(EXTRA_SCAN_TYPE);
        if (scanType == null) {
            Toast.makeText(this, "扫描类型错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        // 2. 检查权限并启动扫描
        checkCameraPermissionAndStartScanner();

        // 检查是否有模拟扫描结果传入（用于方案一）
        if (getIntent().hasExtra(EXTRA_SIMULATED_SCAN_RESULT)) {
            String simulatedResult = getIntent().getStringExtra(EXTRA_SIMULATED_SCAN_RESULT);
            // 直接处理结果，不启动相机
            handleScanResult(simulatedResult);
        } else {
            // 正常启动相机扫描
            setupScanner();
        }
    }

    /**
     * 检查相机权限，如果已授予则启动扫描器，否则发起请求
     */
    private void checkCameraPermissionAndStartScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // 权限已经存在，直接设置并启动扫描器
            setupScanner();
        } else {
            // 权限不存在，发起请求
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void setupScanner() {
        if (mCodeScanner == null) {
            CodeScannerView scannerView = binding.scannerView;
            mCodeScanner = new CodeScanner(this, scannerView);
            mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
                mCodeScanner.stopPreview();
                handleScanResult(result.getText());
            }));
            scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
        }
    }

    /**
     * 处理扫描到的二维码内容 (batchCode)
     */
    private void handleScanResult(String batchCode) {
        // 1. 调用 API 获取批次详情，以确认物品信息
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiClient.getApiService().getInventoryByBatchCode(batchCode).enqueue(new Callback<ScanResponse>() {
            @Override
            public void onResponse(Call<ScanResponse> call, Response<ScanResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    // 2. 获取成功，弹出确认对话框
                    showConfirmationDialog(response.body());
                } else {
                    // 批次号无效或网络错误
                    showErrorDialog("无效的批次号", "未在系统中找到该批次号，请确认二维码是否正确。");
                }
            }

            @Override
            public void onFailure(Call<ScanResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                showErrorDialog("网络错误", "无法连接到服务器，请检查网络连接。");
            }
        });
    }

    /**
     * 显示一个包含物品信息的确认对话框
     */
    private void showConfirmationDialog(ScanResponse data) {
        String title = "INBOUND".equals(scanType) ? "确认入库" : "确认出库";
        String message = String.format(
                "请确认物品信息：\n\n产品名称: %s\n批次号: %s\n当前库存: %d",
                data.getProduct().getName(),
                data.getInventory().getBatchCode(),
                data.getInventory().getQuantity()
        );

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确认", (dialog, which) -> {
                    // 用户点击确认，根据扫描类型调用相应的接口
                    if ("INBOUND".equals(scanType)) {
                        performInbound(data.getInventory().getBatchCode());
                    } else {
                        performOutbound(data.getInventory().getBatchCode());
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    // 用户取消，关闭当前 Activity
                    finish();
                })
                .setCancelable(false) // 不允许通过点击外部来关闭
                .show();
    }

    /**
     * 调用入库接口
     */
    private void performInbound(String batchCode) {
        binding.progressBar.setVisibility(View.VISIBLE);
        ScanRequest request = new ScanRequest(batchCode, MyApplication.getCurrentUser().getId());
        ApiClient.getApiService().scanInbound(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ScanActivity.this, "入库成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // 设置成功结果
                    finish();
                } else {
                    handleApiError("入库失败");
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handleApiError("入库失败，网络错误");
            }
        });
    }

    /**
     * 调用出库接口
     */
    private void performOutbound(String batchCode) {
        binding.progressBar.setVisibility(View.VISIBLE);
        ScanRequest request = new ScanRequest(batchCode, MyApplication.getCurrentUser().getId());
        ApiClient.getApiService().scanOutbound(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ScanActivity.this, "出库成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // 设置成功结果
                    finish();
                } else {
                    handleApiError("出库失败");
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handleApiError("出库失败，网络错误");
            }
        });
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void handleApiError(String defaultMessage) {
        binding.progressBar.setVisibility(View.GONE);
        Toast.makeText(this, defaultMessage, Toast.LENGTH_LONG).show();
        // 也可以选择不关闭，让用户重试
        // mCodeScanner.startPreview();
        finish(); // 当前逻辑是失败后直接关闭
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCodeScanner != null && !getIntent().hasExtra(EXTRA_SIMULATED_SCAN_RESULT)) {
            mCodeScanner.startPreview();
        }
    }

    @Override
    protected void onPause() {
        if (mCodeScanner != null) {
            mCodeScanner.releaseResources();
        }
        super.onPause();
    }
}