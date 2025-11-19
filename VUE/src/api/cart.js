// src/api/cart.js
import request from "../utils/request";

// 添加商品到购物车
export function addToCart(data) {
  return request.post("/cart", data);
}

// 获取购物车列表
export function getCartList(userId) {
  return request.get("/cart/list", { params: { userId } });
}

// 更新购物车商品数量
export function updateCartQuantity(cartId, quantity) {
  return request.put(`/cart/${cartId}`, { quantity });
}

// 删除购物车商品
export function deleteCartItem(cartId) {
  return request.delete(`/cart/${cartId}`);
}

// 清空购物车
export function clearCart(userId) {
  return request.delete("/cart/clear", { params: { userId } });
}
