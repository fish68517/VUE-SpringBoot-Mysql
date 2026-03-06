// src/api/product.js
import request from "../utils/request";

// 获取商品列表（分页）
export function getProductList(params) {
  return request.get("/product/list", { params });
}

// 获取商品详情
export function getProductDetail(id) {
  return request.get(`/product/${id}`);
}

// 搜索商品
export function searchProducts(params) {
  return request.get("/product/search", { params });
}

// 按分类获取商品
export function getProductsByCategory(categoryId, params) {
  return request.get(`/product/category/${categoryId}`, { params });
}

// 获取推荐商品
export function getRecommendedProducts(params) {
  return request.get("/product/recommend", { params });
}

// 获取所有分类
export function getCategories() {
  return request.get("/category/list");
}
