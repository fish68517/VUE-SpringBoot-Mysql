package com.xingluo.petshop.controller;


import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.CouponVO;
import com.xingluo.petshop.dto.CreateCouponDTO;
import com.xingluo.petshop.dto.UserCouponVO;
import com.xingluo.petshop.entity.Coupon;
import com.xingluo.petshop.entity.UserCoupon;
import com.xingluo.petshop.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 优惠券控制器
 */
@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {
    
    private final CouponService couponService;
    
    /**
     * 创建优惠券（店家）
     */
    @PostMapping
    public ApiResponse<CouponVO> createCoupon(@RequestBody CreateCouponDTO dto) {
        Coupon coupon = new Coupon();
        coupon.setShopId(dto.getShopId());
        coupon.setName(dto.getName());
        coupon.setDiscountAmount(dto.getDiscountAmount());
        coupon.setMinAmount(dto.getMinAmount());
        coupon.setTotalCount(dto.getTotalCount());
        coupon.setStartTime(dto.getStartTime());
        coupon.setEndTime(dto.getEndTime());
        
        Coupon created = couponService.createCoupon(coupon);

        return ApiResponse.ok(CouponVO.fromEntity(created));
    }
    
    /**
     * 获取店铺优惠券列表（店家）
     */
    @GetMapping("/list")
    public ApiResponse<List<CouponVO>> getShopCoupons(@RequestParam Long shopId) {
        List<Coupon> coupons = couponService.getShopCoupons(shopId);
        List<CouponVO> voList = coupons.stream()
                .map(CouponVO::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.ok(voList);
    }
    
    /**
     * 获取店铺可用优惠券（用户）
     */
    @GetMapping("/shop/{shopId}")
    public ApiResponse<List<CouponVO>> getAvailableCoupons(@PathVariable Long shopId) {
        List<Coupon> coupons = couponService.getAvailableCoupons(shopId);
        List<CouponVO> voList = coupons.stream()
                .map(CouponVO::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.ok(voList);
    }
    
    /**
     * 领取优惠券（用户）
     */
    @PostMapping("/{id}/receive")
    public ApiResponse<UserCouponVO> receiveCoupon(@PathVariable Long id, @RequestParam Long userId) {
        UserCoupon userCoupon = couponService.receiveCoupon(userId, id);
        //return Result.success(UserCouponVO.fromEntity(userCoupon));
        return ApiResponse.ok(UserCouponVO.fromEntity(userCoupon));
    }
    
    /**
     * 获取用户优惠券（用户）
     */
    @GetMapping("/user")
    public ApiResponse<List<UserCouponVO>> getUserCoupons(@RequestParam Long userId) {
        List<UserCoupon> userCoupons = couponService.getUserCoupons(userId);
        List<UserCouponVO> voList = userCoupons.stream()
                .map(UserCouponVO::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.ok(voList);
    }
}
