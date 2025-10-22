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


public class OutboundFragment extends Fragment {

    private Button btnScanOutbound;

    private final ActivityResultLauncher<Intent> scanActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(getContext(), "出库操作已发送", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.scan_cancelled, Toast.LENGTH_SHORT).show();
                }
            });


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outbound, container, false);
        btnScanOutbound = view.findViewById(R.id.btn_scan_outbound);

        btnScanOutbound.setOnClickListener(v -> startScan());

        return view;
    }

    private void startScan() {
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, "OUTBOUND");
        scanActivityResultLauncher.launch(intent);
    }

    // onActivityResult is needed only if using IntentIntegrator.initiateScan() directly
    // Since we launch ScanActivity, result handling is done via the launcher callback.
}
