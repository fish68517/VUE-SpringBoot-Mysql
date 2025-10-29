package com.archive.app.view.fragment;

import android.app.Activity;
import android.app.AlertDialog;
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

public class OutboundFragment extends Fragment implements OutboundLogAdapter.OnDeleteClickListener{

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
        adapter = new OutboundLogAdapter(this);
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


    /**
     * 这是实现回调接口后必须重写的方法。
     * 当用户在 Adapter 中点击删除按钮时，此方法将被调用。
     * @param log 用户点击的那个日志对象
     */
    @Override
    public void onDeleteClick(final TransactionLogs log) {
        // 最佳实践：在执行删除操作前，给用户一个确认对话框
        new AlertDialog.Builder(getContext())
                .setTitle("确认删除")
                .setMessage("您确定要删除这条出库记录吗？此操作不可恢复。")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 用户点击了“确定”，执行删除的网络请求
                    performDeleteRequest(log);
                })
                .setNegativeButton("取消", null) // 点击“取消”则什么也不做
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * 执行实际的删除网络请求
     * @param log 要删除的日志对象
     */
    private void performDeleteRequest(TransactionLogs log) {
        // 显示加载指示器，例如让 SwipeRefreshLayout 转起来
        swipeRefreshLayout.setRefreshing(true);

        ApiClient.getApiService().deleteTransactionLog(log.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    // 删除成功后，立即刷新列表以反映变化
                    fetchOutboundLogs();
                } else {
                    // 处理API返回的错误，例如权限不足等
                    Toast.makeText(getContext(), "删除失败，服务器错误", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                // 处理网络连接失败等问题
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}