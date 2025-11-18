package com.xingluo.petshop.dto;

import com.xingluo.petshop.entity.Coupon;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券视图对象
 */
@Data
public class CouponVO {
    
    private Long id;
    
    private Long shopId;
    
    private String shopName;
    
    private String name;
    
    private BigDecimal discountAmount;
    
    private BigDecimal minAmount;
    
    private Integer totalCount;
    
    private Integer usedCount;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    /**
     * 从实体转换为VO
     */
    public static CouponVO fromEntity(Coupon coupon) {
        CouponVO vo = new CouponVO();
        vo.setId(coupon.getId());
        vo.setShopId(coupon.getShopId());
        vo.setName(coupon.getName());
        vo.setDiscountAmount(coupon.getDiscountAmount());
        vo.setMinAmount(coupon.getMinAmount());
        vo.setTotalCount(coupon.getTotalCount());
        vo.setUsedCount(coupon.getUsedCount());
        vo.setStartTime(coupon.getStartTime());
        vo.setEndTime(coupon.getEndTime());
        vo.setStatus(coupon.getStatus());
        vo.setCreateTime(coupon.getCreateTime());
        
        if (coupon.getShop() != null) {
            vo.setShopName(coupon.getShop().getName());
        }
        
        return vo;
    }
}
