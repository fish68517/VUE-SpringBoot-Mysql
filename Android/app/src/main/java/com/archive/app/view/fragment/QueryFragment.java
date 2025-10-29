package com.archive.app.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.archive.app.model.Inventory;
import com.archive.app.model.Products;
import com.archive.app.view.activity.ScanActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QueryFragment extends Fragment {

    private EditText etQueryInput;
    private Button btnScanQuery, btnSearch;
    private TextView tvProductName, tvProductSku, tvBatchCode, tvQuantity, tvNoResults;
    private LinearLayout layoutResults;
    private ProgressBar progressBar;
    private ApiService apiService;

    private static final String TAG = "QueryFragment";

    private final ActivityResultLauncher<Intent> scanQueryResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String batchCode = result.getData().getStringExtra(ScanActivity.EXTRA_SCAN_TYPE);
                    if (batchCode != null) {
                        etQueryInput.setText(batchCode);
                        searchByBatchCode(batchCode); // Trigger search after scan
                    }
                } else {
                    Toast.makeText(getContext(), R.string.scan_cancelled, Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query, container, false);

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

        return view;
    }

    private void startScan() {
        Intent intent = new Intent(getActivity(), ScanActivity.class);
        intent.putExtra(ScanActivity.EXTRA_SCAN_TYPE, "QUERY"); // Set scan type
        scanQueryResultLauncher.launch(intent);
    }

    private void handleSearch() {
        String query = etQueryInput.getText().toString().trim();
        if (TextUtils.isEmpty(query)) {
            Toast.makeText(getContext(), "请输入批次号或SKU", Toast.LENGTH_SHORT).show();
            return;
        }
        // Basic check: Assume it's a batch code if it starts with "BAT-" (adjust as needed)
        if (query.toUpperCase().startsWith("BAT-")) {
            searchByBatchCode(query);
        } else {
            // Otherwise, assume it's an SKU - this endpoint needs to exist on backend
            // searchBySku(query);

            searchBySku(query); // For now, try batch code search anyway
        }
    }

    private void searchByBatchCode(String batchCode) {
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

            @Override
            public void onFailure(Call<ScanResponse> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Search error: ", t);
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                showNoResults();
            }
        });
    }

    // --- Add searchBySku method if backend supports it ---
    private void searchBySku(String sku) {
        showLoading(true);
        clearResults();
        apiService.getInventoryByProductSku(sku).enqueue(new Callback<List<Inventory>>() {
            @Override
            public void onResponse(Call<List<Inventory>> call, Response<List<Inventory>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // How to display multiple results? For now, just show the first one.
                    // Need to fetch Product info separately for the first item
                     fetchProductAndDisplay(response.body().get(0));
                } else {
                    showNoResults();
                }
            }
             @Override
            public void onFailure(Call<List<Inventory>> call, Throwable t) {
                 showLoading(false);
                 Log.e(TAG, "Search by SKU error: ", t);
                 Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                 showNoResults();
            }
        });
    }

    private void fetchProductAndDisplay(Inventory inventory) {
        showLoading(true); // Show loading while fetching product
        apiService.getProductById(inventory.getProductId()).enqueue(new Callback<Products>() {
             @Override
             public void onResponse(Call<Products> call, Response<Products> response) {
                  showLoading(false);
                  if(response.isSuccessful() && response.body() != null) {
                       ScanResponse combinedData = new ScanResponse();
                       combinedData.setInventory(inventory);
                       combinedData.setProduct(response.body());
                       displayResults(combinedData);
                  } else {
                       // Handle case where product info couldn't be fetched
                        Toast.makeText(getContext(), "无法获取产品详情", Toast.LENGTH_SHORT).show();
                       showNoResults(); // Or display partial info
                  }
             }

              @Override
             public void onFailure(Call<Products> call, Throwable t) {
                   showLoading(false);
                   Log.e(TAG, "Fetch product error: ", t);
                   Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                   showNoResults();
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
    }

    private void clearResults() {
        layoutResults.setVisibility(View.GONE);
        tvNoResults.setVisibility(View.GONE);
    }

    private void showNoResults() {
        layoutResults.setVisibility(View.GONE);
        tvNoResults.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSearch.setEnabled(!isLoading);
        btnScanQuery.setEnabled(!isLoading);
    }
}
