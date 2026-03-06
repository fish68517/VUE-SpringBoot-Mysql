// src/api/review.js
import request from "../utils/request";

// 创建评价
export function createReview(data) {
  return request.post("/review", data);
}

// 获取商品评价列表
export function getProductReviews(productId) {
  return request.get(`/review/product/${productId}`);
}

// 获取用户评价列表
export function getUserReviews(userId) {
  return request.get("/review/user", { params: { userId } });
}
