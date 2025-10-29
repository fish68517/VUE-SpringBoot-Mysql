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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.archive.app.ApiClient;
import com.archive.app.R;
import com.archive.app.adapter.OutboundLogAdapter;

import com.archive.app.model.TransactionLogs;
import com.archive.app.view.activity.ScanActivity;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutboundFragment extends Fragment {

    private Button btnScanOutbound;
    private RecyclerView recyclerView;
    private OutboundLogAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final ActivityResultLauncher<Intent> scanActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(getContext(), "出库操作已发送", Toast.LENGTH_SHORT).show();
                    fetchOutboundLogs(); // 刷新列表
                } else {
                    Toast.makeText(getContext(), R.string.scan_cancelled, Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outbound, container, false);

        btnScanOutbound = view.findViewById(R.id.btn_scan_outbound);
        recyclerView = view.findViewById(R.id.recycler_view_outbound);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_outbound);

        setupRecyclerView();

        btnScanOutbound.setOnClickListener(v -> startScan());
        swipeRefreshLayout.setOnRefreshListener(this::fetchOutboundLogs);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchOutboundLogs(); // 首次加载
    }

    private void setupRecyclerView() {
        adapter = new OutboundLogAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void startScan() {
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, "OUTBOUND");
        scanActivityResultLauncher.launch(intent);
    }

    /**
     * 获取并显示出库日志
     * 注意：这是一个客户端筛选实现。最佳实践是让后端提供一个专门的接口来返回出库记录。
     */
    private void fetchOutboundLogs() {
        swipeRefreshLayout.setRefreshing(true);
        ApiClient.getApiService().listTransactionLogs().enqueue(new Callback<List<TransactionLogs>>() {
            @Override
            public void onResponse(Call<List<TransactionLogs>> call, Response<List<TransactionLogs>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    // 在客户端筛选出类型为 "出库" 的日志
                    List<TransactionLogs> outboundLogs = response.body().stream()
                            .filter(log -> "出库".equals(log.getType()))
                            .collect(Collectors.toList());
                    adapter.setLogs(outboundLogs);
                } else {
                    Toast.makeText(getContext(), "加载出库记录失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TransactionLogs>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}