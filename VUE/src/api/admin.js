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


// 修改后（作为 Query 参数发送）：
// 假设你的 api 文件叫 src/api/admin.js 或者 user.js

// export function updateUserStatus(id, params) {
//   // 注意：axios.put 的标准传参顺序是 (url, data, config)
//   // 因为后端使用 @RequestParam 接收，所以请求体 data 我们传 null
//   // 第三项 config 里面放 params，这样 axios 会自动把它拼接到 URL 后面变成 ?status=1
//   return request.put(`/admin/user/${id}/status`, { params });
// }

// ===== 店铺管理 =====
// 获取店铺列表
// api.js 中的定义
export function updateUserStatus(id, status) { // 这里接收到的是数字 0
  // 手动把 status 包装进 params 对象中
  return request.put(`/admin/user/${id}/status`, null, { 
    params: { status: status } // 或者简写为 { params: { status } }
  }); 
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
