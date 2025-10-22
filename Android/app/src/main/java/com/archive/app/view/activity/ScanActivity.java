package com.archive.app.view.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.archive.app.ApiService;
import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.RetrofitClient;
import com.archive.app.dto.ScanRequest;
import com.archive.app.dto.ScanResponse;
import com.archive.app.model.Inventory;
import com.archive.app.model.Products;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends AppCompatActivity {

    public static final String EXTRA_SCAN_TYPE = "scan_type"; // "INBOUND", "OUTBOUND", "QUERY"
    public static final String RESULT_BATCH_CODE = "batch_code";

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ApiService apiService;
    private String scanType;

    private static final String TAG = "ScanActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        apiService = RetrofitClient.getApiService();
        scanType = getIntent().getStringExtra(EXTRA_SCAN_TYPE);
        if (scanType == null) {
            scanType = "QUERY"; // Default if not specified
        }

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        // Do not call decode() here, let CaptureManager handle it
        // capture.decode(); // REMOVE THIS LINE
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Add this line
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    // CaptureManager will call this method internally via Activity Result API
    // Need to override onActivityResult IF CaptureManager doesn't handle it
    // Or just let CaptureManager handle it. Let's assume CaptureManager handles it.
    // However, the standard ZXing IntentIntegrator method requires onActivityResult.
    // If using CaptureManager directly without IntentIntegrator, handle result differently.

    // Let's revert to using IntentIntegrator for simpler result handling,
    // which requires overriding onActivityResult.

    // Remove CaptureManager related code and use IntentIntegrator in Fragments.
    // THIS ACTIVITY BECOMES JUST A HOST FOR THE SCANNER VIEW (if using DecoratedBarcodeView directly)
    // OR it might not be needed if using IntentIntegrator.launch().

    // --- REVISED APPROACH: Using IntentIntegrator from Fragments ---
    // ScanActivity is NOT NEEDED if using IntentIntegrator().initiateScan()
    // The result will be delivered back to the calling Fragment's onActivityResult.

    // --- ALTERNATIVE: If Keeping ScanActivity as Scanner Host ---
    // If you keep this ScanActivity, you need a way to return the result.
    // Let's assume CaptureManager sends the result back via onActivityResult
    // (though modern practice prefers ActivityResultLauncher).

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, R.string.scan_cancelled, Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_CANCELED);
                finish();
            } else {
                String batchCode = result.getContents();
                Log.d(TAG, "Scanned Batch Code: " + batchCode);
                handleScanResult(batchCode);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleScanResult(String batchCode) {
        // Immediately fetch inventory details from backend
        fetchInventoryDetails(batchCode);
    }

    private void fetchInventoryDetails(String batchCode) {
        showLoadingDialog(true);
        apiService.getInventoryByBatchCode(batchCode).enqueue(new Callback<ScanResponse>() {
            @Override
            public void onResponse(Call<ScanResponse> call, Response<ScanResponse> response) {
                showLoadingDialog(false);
                if (response.isSuccessful() && response.body() != null && response.body().getInventory() != null && response.body().getProduct() != null) {
                    ScanResponse scanData = response.body();
                    showConfirmationDialog(scanData);
                } else {
                    Log.e(TAG, "Failed to fetch inventory details: " + response.code());
                    Toast.makeText(ScanActivity.this, R.string.invalid_batch_code, Toast.LENGTH_SHORT).show();
                    // Optionally restart scan or finish
                    restartScan(); // Example: restart scan on invalid code
                    // finishWithError();
                }
            }

            @Override
            public void onFailure(Call<ScanResponse> call, Throwable t) {
                showLoadingDialog(false);
                Log.e(TAG, "Network error fetching details: ", t);
                Toast.makeText(ScanActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                restartScan(); // Example: restart scan on network error
                // finishWithError();
            }
        });
    }

    private void showConfirmationDialog(ScanResponse scanData) {
        Inventory inventory = scanData.getInventory();
        Products product = scanData.getProduct();
        String batchCode = inventory.getBatchCode();
        Integer currentUserId = MyApplication.getCurrentUserId();

        if (currentUserId == null) {
            Toast.makeText(this, "用户未登录，无法操作", Toast.LENGTH_SHORT).show();
            finishWithError();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.scan_result_title);

        // Build message string
        StringBuilder message = new StringBuilder();
        message.append(getString(R.string.product_name_label)).append(" ").append(product.getName()).append("\n");
        message.append(getString(R.string.product_sku_label)).append(" ").append(product.getSku()).append("\n");
        message.append(getString(R.string.batch_code_label)).append(" ").append(batchCode).append("\n");
        message.append(getString(R.string.current_quantity_label)).append(" ").append(inventory.getQuantity());

        builder.setMessage(message.toString());

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
            restartScan(); // Allow scanning again after cancel
            // finishWithError(); // Or just finish
        });

        if ("INBOUND".equals(scanType)) {
            builder.setPositiveButton(R.string.confirm_inbound, (dialog, which) -> {
                performApiCall(apiService.scanInbound(new ScanRequest(batchCode, currentUserId)), R.string.inbound_success, R.string.inbound_failed);
            });
        } else if ("OUTBOUND".equals(scanType)) {
            // Check stock before showing the confirm button
            if (inventory.getQuantity() <= 0) {
                builder.setMessage(message.toString() + "\n\n库存不足，无法出库！");
                // No positive button if stock is zero
                builder.setPositiveButton(null, null); // Hide positive button
            } else {
                builder.setPositiveButton(R.string.confirm_outbound, (dialog, which) -> {
                    performApiCall(apiService.scanOutbound(new ScanRequest(batchCode, currentUserId)), R.string.outbound_success, R.string.outbound_failed);
                });
            }
        } else { // QUERY type
            builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
                finishWithSuccess(batchCode); // Just return the batch code for query
            });
        }

        builder.setCancelable(false); // Prevent dismissing by tapping outside
        builder.show();
    }


    private void performApiCall(Call<Void> apiCall, int successMsgResId, int errorMsgResId) {
        showLoadingDialog(true);
        apiCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showLoadingDialog(false);
                if (response.isSuccessful()) {
                    Toast.makeText(ScanActivity.this, successMsgResId, Toast.LENGTH_SHORT).show();
                    finishWithSuccess(null); // Indicate success, maybe pass batch code back if needed
                } else {
                    Log.e(TAG, "API call failed: " + response.code() + " - " + response.message());
                    Toast.makeText(ScanActivity.this, errorMsgResId, Toast.LENGTH_SHORT).show();
                    restartScan(); // Allow retry
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showLoadingDialog(false);
                Log.e(TAG, "Network error during operation: ", t);
                Toast.makeText(ScanActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                restartScan(); // Allow retry
            }
        });
    }

    private void finishWithSuccess(String batchCode) {
        Intent resultIntent = new Intent();
        if (batchCode != null) {
            resultIntent.putExtra(RESULT_BATCH_CODE, batchCode);
        }
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void finishWithError() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    // Method to restart the scan (if using CaptureManager directly)
    private void restartScan() {
        // If using CaptureManager:
        if (capture != null) {
            // Reset state and start decoding again
            barcodeScannerView.resume(); // Ensure view is resumed
            // capture.decode(); // This might re-trigger if needed, or handle in CaptureManager callbacks
            Toast.makeText(this, "请重新扫描", Toast.LENGTH_SHORT).show();
        } else {
            // If using IntentIntegrator launched from Fragment, finishing here allows Fragment to relaunch
            finishWithError(); // Or just finish to return control
        }
    }


    // Simple loading indicator management (replace with ProgressBar if needed)
    private AlertDialog loadingDialog;
    private void showLoadingDialog(boolean show) {
        if (show) {
            if (loadingDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(R.layout.dialog_loading); // Create a simple layout with a ProgressBar
                builder.setCancelable(false);
                loadingDialog = builder.create();
            }
            loadingDialog.show();
        } else {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }
}
