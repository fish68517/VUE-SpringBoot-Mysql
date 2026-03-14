import request from "../utils/request";

export function getAvailableCoupons(shopId) {
  return request.get(`/coupon/shop/${shopId}`);
}

export function receiveCoupon(couponId) {
  return request.post(`/coupon/${couponId}/receive`);
}

export function getUserCoupons(userId) {
  return request.get("/coupon/user", {
    params: userId ? { userId } : undefined
  });
}

export function getExchangeableCoupons() {
  return request.get("/coupon/exchange/list");
}

export function exchangeCoupon(couponId, userId) {
  return request.post(`/coupon/${couponId}/exchange`, null, {
    params: { userId }
  });
}

export function createCoupon(data) {
  return request.post("/coupon", data);
}

export function getShopCouponList(params) {
  return request.get("/coupon/list", { params });
}

export function getCouponDetail(id) {
  return request.get(`/coupon/${id}`);
}

export function updateCoupon(id, data) {
  return request.put(`/coupon/${id}`, data);
}

export function deleteCoupon(couponId) {
  return request.delete(`/coupon/${couponId}`);
}
