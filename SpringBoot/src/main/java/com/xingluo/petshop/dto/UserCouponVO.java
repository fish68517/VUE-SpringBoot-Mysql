package com.xingluo.petshop.dto;

import com.xingluo.petshop.entity.UserCoupon;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券视图对象
 */
@Data
public class UserCouponVO {
    
    private Long id;
    
    private Long userId;
    
    private Long couponId;
    
    private String couponName;
    
    private BigDecimal discountAmount;
    
    private BigDecimal minAmount;
    
    private Long shopId;
    
    private String shopName;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer status;
    
    private LocalDateTime receiveTime;
    
    private LocalDateTime useTime;
    
    /**
     * 从实体转换为VO
     */
    public static UserCouponVO fromEntity(UserCoupon userCoupon) {
        UserCouponVO vo = new UserCouponVO();
        vo.setId(userCoupon.getId());
        vo.setUserId(userCoupon.getUserId());
        vo.setCouponId(userCoupon.getCouponId());
        vo.setStatus(userCoupon.getStatus());
        vo.setReceiveTime(userCoupon.getReceiveTime());
        vo.setUseTime(userCoupon.getUseTime());
        
        if (userCoupon.getCoupon() != null) {
            vo.setCouponName(userCoupon.getCoupon().getName());
            vo.setDiscountAmount(userCoupon.getCoupon().getDiscountAmount());
            vo.setMinAmount(userCoupon.getCoupon().getMinAmount());
            vo.setShopId(userCoupon.getCoupon().getShopId());
            vo.setStartTime(userCoupon.getCoupon().getStartTime());
            vo.setEndTime(userCoupon.getCoupon().getEndTime());
            
            if (userCoupon.getCoupon().getShop() != null) {
                vo.setShopName(userCoupon.getCoupon().getShop().getName());
            }
        }
        
        return vo;
    }
}
