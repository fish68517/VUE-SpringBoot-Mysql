package com.archive.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.archive.app.R;
import com.archive.app.model.InboundOrders;
import com.archive.app.model.Inventory; // 假设入库单模型包含批次列表

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InboundOrderAdapter extends RecyclerView.Adapter<InboundOrderAdapter.InboundOrderViewHolder> {

    private List<InboundOrders> orders = new ArrayList<>();
    private final Context context;
    // 用于跟踪哪些项是展开的
    private final Set<Integer> expandedPositions = new HashSet<>();

    private AdapterCallback callback; // 新增回调接口

    public InboundOrderAdapter(Context context, AdapterCallback callback) { // 修改构造函数
        this.context = context;
        this.callback = callback;
    }


    public InboundOrderAdapter(Context context) {
        this.context = context;
    }

    public void setOrders(List<InboundOrders> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InboundOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_inbound_order, parent, false);
        return new InboundOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InboundOrderViewHolder holder, int position) {
        InboundOrders order = orders.get(position);
        holder.bind(order, position);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class InboundOrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOrderNumber, tvStatus, tvCreatorName, tvCreatedAt;
        private final LinearLayout llDetailsContainer, llBatchList;

        private  Button btnDelete, btnSetProcessing, btnSetCompleted;

        private final ProgressBar progressBarDetails;

        public InboundOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tv_order_number);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvCreatorName = itemView.findViewById(R.id.tv_creator_name);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
            llDetailsContainer = itemView.findViewById(R.id.ll_details_container);
            llBatchList = itemView.findViewById(R.id.ll_batch_list);

            progressBarDetails = itemView.findViewById(R.id.progress_bar_details);

            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnSetProcessing = itemView.findViewById(R.id.btn_set_processing);
            btnSetCompleted = itemView.findViewById(R.id.btn_set_completed);
        }

        void bind(final InboundOrders order, final int position) {
            tvOrderNumber.setText(order.getOrderNumber());
            tvCreatorName.setText("创建人ID: " + order.getCreatedByUserId()); // 实际应用中会查询用户名
            tvCreatedAt.setText(order.getCreatedAt());

            // 按钮点击事件
            btnDelete.setOnClickListener(v -> {
                if (callback != null) {
                    callback.onDeleteClicked(order.getId());
                }
            });
            btnSetProcessing.setOnClickListener(v -> {
                if (callback != null) {
                    callback.onStatusUpdateClicked(order.getId(), "处理中");
                }
            });
            btnSetCompleted.setOnClickListener(v -> {
                if (callback != null) {
                    callback.onStatusUpdateClicked(order.getId(), "已完成");
                }
            });

            // 设置状态和背景
            tvStatus.setText(order.getStatus());
            switch (order.getStatus()) {
                case "已完成":
                    tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.status_background_completed));
                    break;
                case "处理中":
                    tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.status_background_processing));
                    break;
                case "待处理":
                default:
                    tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.status_background_pending));
                    break;
            }

            // 处理展开/折叠状态
            final boolean isExpanded = expandedPositions.contains(position);
            llDetailsContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> {
                if (isExpanded) {
                    // 如果已经是展开状态，则折叠
                    expandedPositions.remove(position);
                    notifyItemChanged(position);
                } else {
                    // 如果是折叠状态，则展开
                    expandedPositions.add(position);
                    // 检查是否已经加载过数据
                    if (order.getBatchDetails() == null) {
                        // 未加载过，通过回调通知Fragment去加载
                        if (callback != null) {
                            callback.onExpandClicked(order.getId(), position);
                        }
                    }
                    notifyItemChanged(position); // 立即刷新以显示加载中状态
                }
            });

            // 根据状态填充详情区域
            if (isExpanded) {
                if (order.getBatchDetails() != null) {
                    // 数据已加载，显示批次列表，隐藏加载动画
                    progressBarDetails.setVisibility(View.GONE);
                    llBatchList.setVisibility(View.VISIBLE);
                    populateBatchDetails(order.getBatchDetails());
                } else {
                    // 数据未加载，显示加载动画，隐藏批次列表
                    progressBarDetails.setVisibility(View.VISIBLE);
                    llBatchList.setVisibility(View.GONE);
                    llBatchList.removeAllViews(); // 清空旧数据
                }
            }

            Log.d("InboundOrderAdapter", "Binding order: " + order);
            Log.d("InboundOrderAdapter", "Binding isExpanded: " + isExpanded);
            // 如果是展开状态，则填充批次详情
            if (isExpanded) {
                populateBatchDetails(order.getBatchDetails()); // 假设InboundOrders有getBatchDetails()方法
            }
        }

        private void populateBatchDetails(List<Inventory> batchDetails) {
            llBatchList.removeAllViews(); // 清除旧视图
            if (batchDetails == null || batchDetails.isEmpty()) {
                TextView noData = new TextView(context);
                noData.setText("无批次详情");
                llBatchList.addView(noData);
                return;
            }

            for (Inventory item : batchDetails) {
                // 为每一条批次信息创建一个新的行布局
                LinearLayout row = new LinearLayout(context);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(0, 8, 0, 8);

                TextView batchCode = new TextView(context);
                batchCode.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));
                batchCode.setText(item.getBatchCode());

                TextView sku = new TextView(context);
                sku.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                sku.setText("SKU" + item.getProductId()); // 实际应查询SKU

                TextView quantity = new TextView(context);
                quantity.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                quantity.setText(String.valueOf(item.getQuantity()));
                quantity.setGravity(android.view.Gravity.END);

                row.addView(batchCode);
                row.addView(sku);
                row.addView(quantity);

                llBatchList.addView(row);
            }
        }
    }
}