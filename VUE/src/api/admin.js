import request from "../utils/request";

// dashboard
export function getDashboard() {
  return request.get("/admin/dashboard");
}

export function getDashboardStatistics(params) {
  return request.get("/admin/dashboard/statistics", { params });
}

export function getDashboardCategory() {
  return request.get("/admin/dashboard/category");
}

export function getDashboardProduct() {
  return request.get("/admin/dashboard/product");
}

// users
export function getUserList(params) {
  return request.get("/admin/user/list", { params });
}

export function searchUser(params) {
  return request.get("/admin/user/search", { params });
}

export function updateUserStatus(id, status) {
  return request.put(`/admin/user/${id}/status`, null, {
    params: { status }
  });
}

// shops
export function getShopList(params) {
  return request.get("/admin/shop/list", { params });
}

export function auditShop(shopId, status) {
  return request.put(`/admin/shop/${shopId}/audit`, null, {
    params: { status }
  });
}

export function updateShopStatus(shopId, status) {
  return request.put(`/admin/shop/${shopId}/status`, null, {
    params: { status }
  });
}

// product audit
export function getProductAuditList(params) {
  return request.get("/admin/product/list", { params });
}

export function auditProduct(productId, data) {
  return request.put(`/admin/product/${productId}/audit`, data);
}

// community
export function getPostList(params) {
  return request.get("/admin/post/list", { params });
}

export function deletePost(postId) {
  return request.delete(`/admin/post/${postId}`);
}

export function deleteReply(replyId) {
  return request.delete(`/admin/reply/${replyId}`);
}
