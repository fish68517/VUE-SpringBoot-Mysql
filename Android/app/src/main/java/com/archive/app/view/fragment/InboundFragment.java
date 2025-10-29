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
import com.archive.app.SessionManager;
import com.archive.app.adapter.AdapterCallback;
import com.archive.app.adapter.InboundOrderAdapter;

import com.archive.app.model.InboundOrders;
import com.archive.app.model.Inventory;
import com.archive.app.view.activity.ScanActivity;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InboundFragment extends Fragment implements AdapterCallback {

    private Button btnScanInbound;
    private RecyclerView recyclerView;
    private InboundOrderAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    private List<InboundOrders> currentOrders; // 用于存储当前列表数据


    @Override
    public void onDeleteClicked(int orderId) {
        // 在这里调用删除API
        ApiClient.getApiService().deleteInboundOrder(orderId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "订单删除成功", Toast.LENGTH_SHORT).show();
                    fetchInboundOrders(); // 刷新列表
                } else {
                    Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStatusUpdateClicked(int orderId, String newStatus) {
        // 准备请求体，可以包含操作员ID
        Map<String, Object> data = new HashMap<>();
        data.put("status", newStatus);
        // data.put("operator_id", sessionManager.getUserId()); // 如果后端需要，可以传入操作员ID

        // 调用更新API
        ApiClient.getApiService().updateInboundOrderStatus(orderId, data).enqueue(new Callback<InboundOrders>() {
            @Override
            public void onResponse(Call<InboundOrders> call, Response<InboundOrders> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "状态更新成功", Toast.LENGTH_SHORT).show();
                    fetchInboundOrders(); // 刷新列表
                } else {
                    Toast.makeText(getContext(), "更新失败", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<InboundOrders> call, Throwable t) {
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private final ActivityResultLauncher<Intent> scanActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(getContext(), "入库操作已发送", Toast.LENGTH_SHORT).show();
                    // 扫描成功后刷新列表
                    fetchInboundOrders();
                } else {
                    Toast.makeText(getContext(), R.string.scan_cancelled, Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbound, container, false);

        btnScanInbound = view.findViewById(R.id.btn_scan_inbound);
        recyclerView = view.findViewById(R.id.recycler_view_inbound);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_inbound);

        setupRecyclerView();

        btnScanInbound.setOnClickListener(v -> startScan());

        swipeRefreshLayout.setOnRefreshListener(this::fetchInboundOrders);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 首次加载数据
        fetchInboundOrders();
    }

    private void setupRecyclerView() {
        adapter = new InboundOrderAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    /**
     * 实现新的回调方法，用于加载批次详情
     */
    @Override
    public void onExpandClicked(final int orderId, final int position) {
        // 调用我们新添加的API
        ApiClient.getApiService().listInventoryByOrderId(orderId).enqueue(new Callback<List<Inventory>>() {
            @Override
            public void onResponse(Call<List<Inventory>> call, Response<List<Inventory>> response) {
                if (response.isSuccessful() && response.body() != null && currentOrders != null) {
                    // 请求成功，将返回的批次列表设置到对应的InboundOrders对象中
                    currentOrders.get(position).setBatchDetails(response.body());
                    // 通知Adapter刷新这一项，Adapter会自动显示数据并隐藏ProgressBar
                    adapter.notifyItemChanged(position);
                } else {
                    Toast.makeText(getContext(), "加载批次详情失败", Toast.LENGTH_SHORT).show();
                    // 可以在这里处理加载失败的情况，例如隐藏ProgressBar
                }
            }

            @Override
            public void onFailure(Call<List<Inventory>> call, Throwable t) {
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchInboundOrders() {
        swipeRefreshLayout.setRefreshing(true);
        // 使用您的ApiClient和ApiService
        ApiClient.getApiService().listInboundOrders().enqueue(new Callback<List<InboundOrders>>() {
            @Override
            public void onResponse(Call<List<InboundOrders>> call, Response<List<InboundOrders>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    // TODO: 为了显示详情，您需要在这里为每个Order获取其批次列表
                    // 这是一个简化版本，实际应用中可能需要更复杂的逻辑
                    currentOrders = response.body(); // 保存数据
                    adapter.setOrders(currentOrders);
                } else {
                    Toast.makeText(getContext(), "加载入库单失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<InboundOrders>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startScan() {
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, "INBOUND");
        scanActivityResultLauncher.launch(intent);
    }
}