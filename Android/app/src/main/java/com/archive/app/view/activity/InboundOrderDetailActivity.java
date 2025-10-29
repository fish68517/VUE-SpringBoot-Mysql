package com.archive.app.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.archive.app.R;
import com.archive.app.adapter.InventoryDetailAdapter;

import com.archive.app.databinding.ActivityInboundOrderDetailBinding;
import com.archive.app.model.InboundOrders;


public class InboundOrderDetailActivity extends AppCompatActivity {

    private ActivityInboundOrderDetailBinding binding;
    private InventoryDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInboundOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();

        InboundOrders order = (InboundOrders) getIntent().getSerializableExtra("inboundOrder");

        Log.d("InboundOrderDetailActivity", "getBatchDetails: " + order.getBatchDetails());
        if (order != null) {
            populateData(order);
        } else {
            Toast.makeText(this, "无法加载订单详情", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("入库单详情");
        }
    }

    private void populateData(InboundOrders order) {
        // 填充摘要信息
        binding.toolbarLayout.setTitle(order.getOrderNumber()); // 给折叠标题也设置订单号
        binding.tvOrderNumberDetail.setText(order.getOrderNumber());
        binding.tvCreatorDetail.setText(String.format("创建人ID: %d", order.getCreatedByUserId()));
        binding.tvCreatedAtDetail.setText(String.format("创建时间: %s", order.getCreatedAt()));

        // 设置状态
        binding.tvStatusDetail.setText(order.getStatus());
        switch (order.getStatus()) {
            case "已完成":
                binding.tvStatusDetail.setBackground(ContextCompat.getDrawable(this, R.drawable.status_background_completed));
                break;
            case "处理中":
                binding.tvStatusDetail.setBackground(ContextCompat.getDrawable(this, R.drawable.status_background_processing));
                break;
            case "待处理":
            default:
                binding.tvStatusDetail.setBackground(ContextCompat.getDrawable(this, R.drawable.status_background_pending));
                break;
        }

        // 填充备注
        if (order.getNotes() != null && !order.getNotes().isEmpty()) {
            binding.cardNotes.setVisibility(View.VISIBLE);
            binding.tvNotesDetail.setText(order.getNotes());
        } else {
            binding.cardNotes.setVisibility(View.GONE);
        }

        // 设置 RecyclerView
        setupRecyclerView(order);
    }

    private void setupRecyclerView(InboundOrders order) {
        adapter = new InventoryDetailAdapter();
        binding.rvBatchDetails.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBatchDetails.setAdapter(adapter);
        // 确保 batchDetails 不为 nullaa

        Log.d("InboundOrderDetailActivity", "Batch Details: " + order.getBatchDetails());
        if (order.getBatchDetails() != null) {
            adapter.setItems(order.getBatchDetails());
        }
    }

    // 处理返回按钮的点击事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}