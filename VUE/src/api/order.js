// src/api/order.js
import request from "../utils/request";

// 创建订单
// 创建订单
export function createOrder(data) {
  // ★ 核心修改：通过 params 将 userId 提取到 URL 查询参数中 (?userId=xxx)
  // 其余数据依然保留在 data 中作为请求体 (Body) 发送
  return request.post("/order", data, { 
    params: { userId: data.userId } 
  });
}

// 获取订单列表
export function getOrderList(userId) {
  return request.get("/order/list", { params: { userId } });
}

// 获取订单详情
export function getOrderDetail(orderId) {
  return request.get(`/order/${orderId}`);
}

// 支付订单
export function payOrder(orderId) {
  return request.put(`/order/${orderId}/pay`);
}

// 取消订单
export function cancelOrder(orderId) {
  return request.put(`/order/${orderId}/cancel`);
}

// 确认收货
export function completeOrder(orderId) {
  return request.put(`/order/${orderId}/complete`);
}

// 修改收货信息
export function updateOrderReceiver(orderId, data) {
  return request.put(`/order/${orderId}/receiver`, data);
}
