package com.archive.app.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.archive.app.ApiClient; // 假设你有 ApiClient
import com.archive.app.ApiService;
import com.archive.app.R;
import com.archive.app.model.InboundOrders;
import com.archive.app.model.Inventory;
import com.archive.app.model.Products;
import com.archive.app.model.Users; // 需要 Users 模型

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailActivity";
    private Inventory inventory;
    private ApiService apiService = ApiClient.getApiService(); // 确保获取单例

    // UI 控件
    private TextView tvProductName, tvProductSku, tvProductDesc;
    private TextView tvBatchCode, tvQuantity, tvReceivedAt;
    private TextView tvInboundOrder, tvOperatorName, tvOrderStatus, tvOrderNotes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // 1. 获取传递过来的对象
        inventory = (Inventory) getIntent().getSerializableExtra("INVENTORY");

        if (inventory == null) {
            Toast.makeText(this, "数据错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        // 先显示 inventory 自身持有的数据（无需网络请求的部分）
        populateInventoryBasicData();

        // 开始网络请求
        initData();
    }

    private void initViews() {
        // 产品卡片
        tvProductName = findViewById(R.id.tv_product_name);
        tvProductSku = findViewById(R.id.tv_product_sku);
        tvProductDesc = findViewById(R.id.tv_product_desc);

        // 库存卡片
        tvBatchCode = findViewById(R.id.tv_batch_code);
        tvQuantity = findViewById(R.id.tv_quantity);
        tvReceivedAt = findViewById(R.id.tv_received_at);

        // 订单卡片
        tvInboundOrder = findViewById(R.id.tv_inbound_order);
        tvOperatorName = findViewById(R.id.tv_operator_name);
        tvOrderStatus = findViewById(R.id.tv_order_status);
        tvOrderNotes = findViewById(R.id.tv_order_notes);
    }

    private void populateInventoryBasicData() {
        tvBatchCode.setText(inventory.getBatchCode());
        tvQuantity.setText(String.valueOf(inventory.getQuantity()));
        // 如果 inventory 中有时间字段，这里直接显示，或者等后端返回格式化时间
        tvReceivedAt.setText(inventory.getReceivedAt() != null ? inventory.getReceivedAt() : "未知");
    }

    private void initData() {
        int productId = inventory.getProductId();
        int inboundOrderId = inventory.getInboundOrderId();

        // --- 1. 获取产品详情 ---
        apiService.getProductById(productId).enqueue(new Callback<Products>() {
            @Override
            public void onResponse(Call<Products> call, Response<Products> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Products product = response.body();
                    tvProductName.setText(product.getName());
                    tvProductSku.setText(product.getSku());
                    tvProductDesc.setText(product.getDescription());
                }
            }
            @Override
            public void onFailure(Call<Products> call, Throwable t) {
                Log.e(TAG, "Get Product Error", t);
            }
        });

        // --- 2. 获取入库单详情 (为了知道是哪个单子，以及谁创建的) ---
        apiService.getInboundOrderById(inboundOrderId).enqueue(new Callback<InboundOrders>() {
            @Override
            public void onResponse(Call<InboundOrders> call, Response<InboundOrders> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InboundOrders order = response.body();

                    tvInboundOrder.setText(order.getOrderNumber());
                    tvOrderStatus.setText(order.getStatus());
                    tvOrderNotes.setText("备注: " + (order.getNotes() != null ? order.getNotes() : "无"));

                    // **关键步骤：获取操作员/入库人姓名**
                    // InboundOrders 中包含 createdByUserId
                    fetchOperatorName(order.getCreatedByUserId());
                }
            }
            @Override
            public void onFailure(Call<InboundOrders> call, Throwable t) {
                Log.e(TAG, "Get Inbound Order Error", t);
                tvInboundOrder.setText("获取失败");
            }
        });
    }

    /**
     * 根据 User ID 获取用户全名，显示为“入库人”
     */
    private void fetchOperatorName(int userId) {
        apiService.getUserById(userId).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Users user = response.body();
                    // 优先显示全名，没有则显示用户名
                    String displayName = user.getFullName() != null ? user.getFullName() : user.getUsername();
                    tvOperatorName.setText(displayName);
                } else {
                    tvOperatorName.setText("未知用户 (ID:" + userId + ")");
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                tvOperatorName.setText("加载失败");
            }
        });
    }
}