// src/api/coupon.js
import request from "../utils/request";

// 获取店铺可用优惠券
export function getAvailableCoupons(shopId) {
  return request.get(`/coupon/shop/${shopId}`);
}

// 领取优惠券
export function receiveCoupon(couponId) {
  return request.post(`/coupon/${couponId}/receive`);
}

// 获取用户优惠券列表
export function getUserCoupons() {
  return request.get("/coupon/user");
}

// 创建优惠券（店家）
export function createCoupon(data) {
  return request.post("/coupon", data);
}

// 获取店铺优惠券列表（店家）
export function getShopCouponList(params) {
  return request.get("/coupon/list", { params });
}

// 获取优惠券详情
export function getCouponDetail(id) {
  return request.get(`/coupon/${id}`);
}

// 更新优惠券
export function updateCoupon(id, data) {
  return request.put(`/coupon/${id}`, data);
}

// 删除优惠券
export function deleteCoupon(couponId) {
  return request.delete(`/coupon/${couponId}`);
}
