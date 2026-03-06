// src/api/admin.js
import request from "../utils/request";

// ===== 数据看板 =====
// 获取数据看板
export function getDashboard() {
  return request.get("/admin/dashboard");
}

// 获取时间范围统计
export function getDashboardStatistics(params) {
  return request.get("/admin/dashboard/statistics", { params });
}

// 获取分类统计
export function getDashboardCategory() {
  return request.get("/admin/dashboard/category");
}

// ===== 用户管理 =====
// 获取用户列表
export function getUserList(params) {
  return request.get("/admin/user/list", { params });
}

// 搜索用户
export function searchUser(params) {
  return request.get("/admin/user/search", { params });
}

// 启用/禁用用户
export function updateUserStatus(userId, data) {
  return request.put(`/admin/user/${userId}/status`, data);
}

// ===== 店铺管理 =====
// 获取店铺列表
export function getShopList(params) {
  return request.get("/admin/shop/list", { params });
}

// 审核店铺
export function auditShop(shopId, data) {
  return request.put(`/admin/shop/${shopId}/audit`, data);
}

// 启用/禁用店铺
export function updateShopStatus(shopId, data) {
  return request.put(`/admin/shop/${shopId}/status`, data);
}

// ===== 商品审核 =====
// 获取待审核商品列表
export function getProductAuditList(params) {
  return request.get("/admin/product/list", { params });
}

// 审核商品
export function auditProduct(productId, data) {
  return request.put(`/admin/product/${productId}/audit`, data);
}

// ===== 社区管理 =====
// 获取帖子列表
export function getPostList(params) {
  return request.get("/admin/post/list", { params });
}

// 删除帖子
export function deletePost(postId) {
  return request.delete(`/admin/post/${postId}`);
}

// 删除评论
export function deleteReply(replyId) {
  return request.delete(`/admin/reply/${replyId}`);
}
