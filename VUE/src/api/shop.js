// src/api/shop.js
import request from "../utils/request";

// 创建店铺
export function createShop(data) {
  return request.post("/shop", data);
}

// 获取店铺信息
export function getShopInfo(shopId) {
  return request.get(`/shop/${shopId}`);
}

// 更新店铺信息
export function updateShopInfo(shopId, data) {
  return request.put(`/shop/${shopId}`, data);
}

// 获取店铺商品列表
export function getShopProducts(shopId, params) {
  return request.get(`/shop/${shopId}/products`, { params });
}

// 创建商品（店家）
export function createProduct(data) {
  return request.post("/shop/product", data);
}

// 获取店铺商品列表（店家端）
export function getShopProductList(params) {
  return request.get("/shop/product/list", { params });
}

// 更新商品（店家）
export function updateProduct(productId, data) {
  return request.put(`/shop/product/${productId}`, data);
}

// 删除商品（店家）
export function deleteProduct(productId) {
  return request.delete(`/shop/product/${productId}`);
}

// 上下架商品（店家）
export function updateProductStatus(productId, data) {
  return request.put(`/shop/product/${productId}/status`, data);
}

// 更新库存（店家）
export function updateProductStock(productId, data) {
  return request.put(`/shop/product/${productId}/stock`, data);
}

// 获取店铺订单列表（店家）
export function getShopOrderList(params) {
  return request.get("/shop/order/list", { params });
}

// 获取店铺订单详情（店家）
export function getShopOrderDetail(orderId) {
  return request.get(`/shop/order/${orderId}`);
}

// 发货（店家）
export function shipOrder(orderId, data) {
  return request.put(`/shop/order/${orderId}/ship`, data);
}
