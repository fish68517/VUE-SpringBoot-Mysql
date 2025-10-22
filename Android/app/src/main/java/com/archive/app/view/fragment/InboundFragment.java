package com.archive.app.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.archive.app.R;
import com.archive.app.view.activity.ScanActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;



public class InboundFragment extends Fragment {

    private Button btnScanInbound;

    // New way to handle Activity results
    private final ActivityResultLauncher<Intent> scanActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Handle successful scan confirmation from ScanActivity if needed
                    Toast.makeText(getContext(), "入库操作已发送", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle cancellation or error from ScanActivity
                    Toast.makeText(getContext(), R.string.scan_cancelled, Toast.LENGTH_SHORT).show();
                }
            });


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbound, container, false);
        btnScanInbound = view.findViewById(R.id.btn_scan_inbound);

        btnScanInbound.setOnClickListener(v -> startScan());

        return view;
    }

    private void startScan() {
        // Option 1: Using ZXing IntentIntegrator directly
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this); // Use forSupportFragment inside a Fragment
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt(getString(R.string.scan_prompt));
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(true);
        // integrator.setCaptureActivity(ScanActivity.class); // Use your custom ScanActivity layout
        // integrator.initiateScan(); // This will deliver result via onActivityResult in Fragment

        // Option 2: Launching your custom ScanActivity (Recommended with the revised ScanActivity)
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, "INBOUND");
        scanActivityResultLauncher.launch(intent); // Use the launcher

    }

    // Handle result IF using IntentIntegrator.initiateScan()
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getContext(), R.string.scan_cancelled, Toast.LENGTH_LONG).show();
            } else {
                String batchCode = result.getContents();
                // IMPORTANT: With IntentIntegrator, you'd show confirmation dialog HERE
                // Need to call API to get details first, then show dialog, then call inbound API
                Toast.makeText(getContext(), "Scanned: " + batchCode + " - Processing...", Toast.LENGTH_LONG).show();
                // Simplified: Assume ScanActivity handles confirmation and API calls
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
