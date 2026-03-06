// src/api/orderItems.js
import request from "../utils/request";

// 获取订单商品明细
export function getOrderItems(orderId) {
  return request.get(`/order/${orderId}/items`);
}
