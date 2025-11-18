package com.xingluo.petshop.service;

import com.xingluo.petshop.entity.Coupon;
import com.xingluo.petshop.entity.UserCoupon;

import java.util.List;

/**
 * 优惠券服务接口
 */
public interface CouponService {
    
    /**
     * 创建优惠券
     */
    Coupon createCoupon(Coupon coupon);
    
    /**
     * 查询店铺优惠券列表
     */
    List<Coupon> getShopCoupons(Long shopId);
    
    /**
     * 查询店铺可用优惠券（用户端）
     */
    List<Coupon> getAvailableCoupons(Long shopId);
    
    /**
     * 用户领取优惠券
     */
    UserCoupon receiveCoupon(Long userId, Long couponId);
    
    /**
     * 查询用户优惠券列表
     */
    List<UserCoupon> getUserCoupons(Long userId);
    
    /**
     * 使用优惠券（订单创建时调用）
     */
    void useCoupon(Long userId, Long couponId, Long orderId);
}
