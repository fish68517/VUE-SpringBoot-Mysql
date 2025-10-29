package com.archive.app.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.archive.app.MyApplication;
import com.archive.app.R;
import com.archive.app.SessionManager;
import com.archive.app.adapter.AdapterCallback;
import com.archive.app.adapter.InboundOrderAdapter;

import com.archive.app.model.InboundOrders;
import com.archive.app.model.Inventory;
import com.archive.app.view.activity.ScanActivity;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InboundFragment extends Fragment implements AdapterCallback {

    private Button btnScanInbound;
    private RecyclerView recyclerView;
    private InboundOrderAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    // 用于管理 RxJava 的订阅，防止内存泄漏
    private final CompositeDisposable disposables = new CompositeDisposable();


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
        ApiClient.getApiService().updateInboundOrderStatus(orderId, data).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "状态更新成功", Toast.LENGTH_SHORT).show();
                    fetchInboundOrders(); // 刷新列表
                } else {
                    Toast.makeText(getContext(), "更新失败", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
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
        adapter = new InboundOrderAdapter(getContext(),this);
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

    /**
     * [{"id":201,"orderNumber":"IN-20251011-001","createdByUserId":2,"status":"已完成","notes":"首批产品入库","createdAt":"2025-10-12T10:28:39","updatedAt":"2025-10-12T10:28:39"},{"id":202,"orderNumber":"IN-20251012-001","createdByUserId":6,"status":"已完成","notes":"供应商补货","createdAt":"2025-10-12T10:28:39","updatedAt":"2025-10-29T14:19:12"},{"id":203,"orderNumber":"IN-20251012-002","createdByUserId":2,"status":"处理中","notes":"新品待入库","createdAt":"2025-10-12T10:28:39","updatedAt":"2025-10-12T10:28:39"},{"id":204,"orderNumber":"IN-1760946090076","createdByUserId":6,"status":"处理中","notes":"补充摄像头牙刷","createdAt":"2025-10-20T15:42:04","updatedAt":"2025-10-29T14:19:09"},{"id":206,"orderNumber":"IN-1760946206949","createdByUserId":6,"status":"待处理","notes":"补充摄像头牙刷","createdAt":"2025-10-20T15:43:46","updatedAt":"2025-10-29T14:19:05"}]
     */
    /**
     * 使用 RxJava 获取入库单及其详情
     */
    private void fetchInboundOrders() {
        swipeRefreshLayout.setRefreshing(true);

        // 1. 获取所有入库单列表
        disposables.add(ApiClient.getApiService().listInboundOrders()
                // 2. 将整个 List<InboundOrders> 转换为一个发射单个 InboundOrders 的流 (Observable)
                .flatMapObservable(Observable::fromIterable)
                // 3. 过滤出只属于当前用户的订单
                .filter(order -> order.getCreatedByUserId() == MyApplication.getCurrentUser().getId())
                // 4. 为每一个符合条件的订单，异步请求其批次详情
                .flatMap(order -> ApiClient.getApiService().getInventoryByInboundId(order.getId())
                        // 将获取到的批次详情 (List<Inventory>) 设置回订单对象
                        .map(batchDetails -> {
                            order.setBatchDetails(batchDetails);
                            return order; // 返回更新后的订单对象
                        })
                        .toObservable() // 将 Single 转换回 Observable 以继续流
                )
                // 5. 将处理过的所有订单对象重新收集回一个 List
                .toList()
                // 6. 指定网络请求在 IO 线程执行
                .subscribeOn(Schedulers.io())
                // 7. 指定结果在主线程（UI线程）处理
                .observeOn(AndroidSchedulers.mainThread())
                // 8. 订阅并处理最终结果
                .subscribe(
                        // onSuccess: 成功获取到完整的列表
                        ordersWithDetails -> {
                            swipeRefreshLayout.setRefreshing(false);
                            currentOrders = ordersWithDetails; // 保存数据
                            adapter.setOrders(currentOrders);
                            Log.d("InboundFragment", "成功加载并组合了 " + currentOrders.size() + " 条订单数据");
                        },
                        // onError: 任何一步发生错误都会在这里捕获
                        throwable -> {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getContext(), "加载失败: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("InboundFragment", "加载入库单失败", throwable);
                        }
                )
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 在 Fragment 销毁时，取消所有正在进行的网络请求，防止内存泄漏
        disposables.clear();
    }


    private void startScan() {
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, "INBOUND");
        scanActivityResultLauncher.launch(intent);
    }
}