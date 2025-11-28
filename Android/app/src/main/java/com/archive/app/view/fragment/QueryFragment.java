package com.archive.app.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter; // 新增导入
import android.widget.AutoCompleteTextView; // 新增导入
import android.widget.Button;
// import android.widget.EditText; // 删除或注释掉
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.archive.app.ApiService;
import com.archive.app.R;
import com.archive.app.RetrofitClient;
import com.archive.app.dto.ScanResponse;
import com.archive.app.model.InboundOrders;
import com.archive.app.model.Inventory;
import com.archive.app.model.Products;
import com.archive.app.view.activity.ProductDetailActivity;
import com.archive.app.view.activity.ScanActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QueryFragment extends Fragment {

    // 1. 修改控件类型为 AutoCompleteTextView
    private AutoCompleteTextView etQueryInput;
    private Button btnScanQuery, btnSearch;
    private TextView tvProductName, tvProductSku, tvBatchCode, tvQuantity, tvNoResults;
    private LinearLayout layoutResults;
    private ProgressBar progressBar;
    private ApiService apiService;

    private static final String TAG = "QueryFragment";

    // 用于自动补全的 Adapter
    private ArrayAdapter<String> mAutoAdapter;
    private List<String> mAutoDataList = new ArrayList<>();

    // ... ScanLauncher 代码保持不变 ...
    private final ActivityResultLauncher<Intent> scanQueryResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String batchCode = result.getData().getStringExtra(ScanActivity.EXTRA_SCAN_TYPE);
                    if (batchCode != null) {
                        etQueryInput.setText(batchCode);
                        etQueryInput.dismissDropDown(); // 扫码填充后关闭下拉提示
                        searchByBatchCode(batchCode);
                    }
                } else {
                    Toast.makeText(getContext(), R.string.scan_cancelled, Toast.LENGTH_SHORT).show();
                }
            });

    private List<Inventory> mListInventory = new ArrayList<>();
    private List<Products> mListProducts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query, container, false);

        // 2. 初始化 View (注意强转或者类型匹配)
        etQueryInput = view.findViewById(R.id.et_query_input);
        btnScanQuery = view.findViewById(R.id.btn_scan_query);
        btnSearch = view.findViewById(R.id.btn_search);
        tvProductName = view.findViewById(R.id.tv_result_product_name);
        tvProductSku = view.findViewById(R.id.tv_result_sku);
        tvBatchCode = view.findViewById(R.id.tv_result_batch_code);
        tvQuantity = view.findViewById(R.id.tv_result_quantity);
        tvNoResults = view.findViewById(R.id.tv_no_results);
        layoutResults = view.findViewById(R.id.layout_results);
        progressBar = view.findViewById(R.id.progress_bar_query);

        apiService = RetrofitClient.getApiService();

        btnScanQuery.setOnClickListener(v -> startScan());
        btnSearch.setOnClickListener(v -> handleSearch());

        // 3. 初始化自动补全的 Adapter
        // simple_dropdown_item_1line 是 Android 自带的下拉项样式
        mAutoAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, mAutoDataList);
        etQueryInput.setAdapter(mAutoAdapter);
        // 设置从第1个字符开始匹配
        etQueryInput.setThreshold(1);

        // 4. 处理用户点击下拉项的事件
        etQueryInput.setOnItemClickListener((parent, view1, position, id) -> {
            // 获取用户选中的文本
            String selectedText = (String) parent.getItemAtPosition(position);
            etQueryInput.setText(selectedText);
            // 可选：选中后自动触发搜索
            // handleSearch();
        });

        initInventory();
        initProducts();

        return view;
    }

    /**
     * 核心方法：更新自动补全的数据源
     * 将 Inventory 的 batchCode 和 Products 的 sku 合并
     */
    private void updateAutoCompleteData() {
        if (getContext() == null) return;

        // 使用 Set 去重，防止重复提示
        Set<String> uniqueData = new HashSet<>();

        // 添加 SKU
        if (mListProducts != null) {
            for (Products p : mListProducts) {
                if (!TextUtils.isEmpty(p.getSku())) {
                    uniqueData.add(p.getSku());
                }
            }
        }

        // 添加 BatchCode
        if (mListInventory != null) {
            for (Inventory inv : mListInventory) {
                if (!TextUtils.isEmpty(inv.getBatchCode())) {
                    uniqueData.add(inv.getBatchCode());
                }
            }
        }

        // 更新列表
        mAutoDataList.clear();
        mAutoDataList.addAll(uniqueData);

        // 通知 Adapter 数据变了
        // 注意：ArrayAdapter 有时需要重新通过 new 创建或者使用 clear/addAll 配合 notify
        // 最稳妥的方式是重新设置 Adapter 或者使用 addAll (如果 API level 允许)
        // 这里使用最通用的重置方式：
        mAutoAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, mAutoDataList);
        etQueryInput.setAdapter(mAutoAdapter);
        mAutoAdapter.notifyDataSetChanged();
    }

    private void initProducts() {
        apiService.listProducts().enqueue(new Callback<List<Products>>() {
            @Override
            public void onResponse(Call<List<Products>> call, Response<List<Products>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    mListProducts.clear();
                    mListProducts = response.body();
                    // 数据回来后，更新提示列表
                    updateAutoCompleteData();
                } else {
                    showNoResults(); // 注意：这里可能不需要 showNoResults，因为这是初始化数据
                }
            }
            @Override
            public void onFailure(Call<List<Products>> call, Throwable t) {
                // 忽略错误或记录日志，不影响主流程
                Log.e(TAG, "Init products error: ", t);
            }
        });
    }

    private void initInventory() {
        apiService.listInventory().enqueue(new Callback<List<Inventory>>() {
            @Override
            public void onResponse(Call<List<Inventory>> call, Response<List<Inventory>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    mListInventory.clear();
                    mListInventory = response.body();
                    // 数据回来后，更新提示列表
                    updateAutoCompleteData();
                } else {
                    // showNoResults();
                }
            }

            @Override
            public void onFailure(Call<List<Inventory>> call, Throwable t) {
                Log.e(TAG, "Init inventory error: ", t);
            }
        });
    }

    // ... startScan, handleSearch, searchByBatchCode, searchBySku, etc. 保持不变 ...
    // ... 下面的代码不需要修改，除了 etQueryInput 的引用类型变了，但 getText() 方法是通用的 ...

    private void startScan() {
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, "QUERY");
        scanQueryResultLauncher.launch(intent);
    }

    private void handleSearch() {
        String query = etQueryInput.getText().toString().trim();
        // ... 保持原有逻辑 ...
        if (TextUtils.isEmpty(query)) {
            Toast.makeText(getContext(), "请输入批次号或SKU", Toast.LENGTH_SHORT).show();
            return;
        }
        // ...
        if (query.toUpperCase().startsWith("BAT-")) {
            searchByBatchCode(query);
        } else {
            searchBySku(query);
        }
    }

    // ... 其他方法保持不变 ...

    private void searchByBatchCode(String batchCode) {
        // ... 略 ...
        showLoading(true);
        clearResults();
        apiService.getInventoryByBatchCode(batchCode).enqueue(new Callback<ScanResponse>() {
            @Override
            public void onResponse(Call<ScanResponse> call, Response<ScanResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().getInventory() != null) {
                    displayResults(response.body());
                } else {
                    showNoResults();
                }
            }
            // ...
            @Override
            public void onFailure(Call<ScanResponse> call, Throwable t) {
                showLoading(false);
                showNoResults();
            }
        });
    }

    // ... searchBySku 等方法 ...

    private void searchBySku(String sku) {
        // ... 略 ...
        showLoading(true);
        clearResults();
        apiService.getInventoryByProductSku(sku).enqueue(new Callback<List<Inventory>>() {
            @Override
            public void onResponse(Call<List<Inventory>> call, Response<List<Inventory>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    fetchProductAndDisplay(response.body().get(0));
                } else {
                    showNoResults();
                }
            }
            @Override
            public void onFailure(Call<List<Inventory>> call, Throwable t) {
                showLoading(false);
                showNoResults();
            }
        });
    }

    // ... fetchProductAndDisplay, displayResults, clearResults, showNoResults, showLoading 等方法保持不变 ...
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSearch.setEnabled(!isLoading);
        btnScanQuery.setEnabled(!isLoading);
    }
    private void clearResults() {
        layoutResults.setVisibility(View.GONE);
        tvNoResults.setVisibility(View.GONE);
    }
    private void showNoResults() {
        layoutResults.setVisibility(View.GONE);
        tvNoResults.setVisibility(View.VISIBLE);
    }
    private void fetchProductAndDisplay(Inventory inventory) {
        // ... 略 ...
        apiService.getProductById(inventory.getProductId()).enqueue(new Callback<Products>() {
            @Override
            public void onResponse(Call<Products> call, Response<Products> response) {
                // ...
                if(response.isSuccessful() && response.body() != null) {
                    ScanResponse combinedData = new ScanResponse();
                    combinedData.setInventory(inventory);
                    combinedData.setProduct(response.body());
                    displayResults(combinedData);
                } else {
                    showNoResults();
                }
            }
            @Override
            public void onFailure(Call<Products> call, Throwable t) {
                // ...
            }
        });
    }
    private void displayResults(ScanResponse data) {
        Inventory inventory = data.getInventory();
        Products product = data.getProduct();

        tvProductName.setText(product != null ? product.getName() : "N/A");
        tvProductSku.setText(product != null ? product.getSku() : "N/A");
        tvBatchCode.setText(inventory.getBatchCode());
        tvQuantity.setText(String.valueOf(inventory.getQuantity()));

        layoutResults.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.GONE);
        layoutResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
     /*           apiService.getInboundOrderById(inventory.getInboundOrderId()).enqueue(new Callback<InboundOrders>() {
                    @Override
                    public void onResponse(Call<InboundOrders> call, Response<InboundOrders> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            InboundOrders inboundOrder = response.body();
                            Log.d(TAG, "Clicked inventory belongs to inbound order: " + inboundOrder.getOrderNumber());
                            In
                        }
                    }

                    @Override
                    public void onFailure(Call<InboundOrders> call, Throwable t) {

                    }
                });*/

                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("INVENTORY", inventory);

                startActivity(intent);
            }
        });
    }
}
