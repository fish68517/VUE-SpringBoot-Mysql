// src/api/order.js
import request from "../utils/request";

// 创建订单
export function createOrder(data) {
  return request.post("/order", data);
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
