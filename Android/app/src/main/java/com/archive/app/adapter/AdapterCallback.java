package com.archive.app.adapter;

public interface AdapterCallback {
    void onDeleteClicked(int orderId);
    void onStatusUpdateClicked(int orderId, String newStatus);

    /**
     * 当用户点击展开一个列表项时调用
     * @param orderId 需要加载详情的订单ID
     * @param position 在Adapter中的位置，用于后续刷新
     */
    void onExpandClicked(int orderId, int position);
}