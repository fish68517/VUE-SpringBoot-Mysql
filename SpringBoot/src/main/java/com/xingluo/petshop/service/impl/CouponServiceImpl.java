package com.xingluo.petshop.service.impl;

import com.xingluo.petshop.common.exception.BusinessException;
import com.xingluo.petshop.entity.Coupon;
import com.xingluo.petshop.entity.UserCoupon;
import com.xingluo.petshop.repository.CouponRepository;
import com.xingluo.petshop.repository.UserCouponRepository;
import com.xingluo.petshop.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券服务实现类
 */
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    
    @Override
    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        // 验证优惠券信息
        if (coupon.getDiscountAmount() == null || coupon.getDiscountAmount().signum() <= 0) {
            throw new BusinessException(400, "折扣金额必须大于0");
        }
        
        if (coupon.getStartTime() == null || coupon.getEndTime() == null) {
            throw new BusinessException(400, "请设置优惠券有效期");
        }
        
        if (coupon.getEndTime().isBefore(coupon.getStartTime())) {
            throw new BusinessException(400, "结束时间不能早于开始时间");
        }
        
        return couponRepository.save(coupon);
    }
    
    @Override
    public List<Coupon> getShopCoupons(Long shopId) {
        return couponRepository.findByShopIdOrderByCreateTimeDesc(shopId);
    }
    
    @Override
    public List<Coupon> getAvailableCoupons(Long shopId) {
        return couponRepository.findAvailableCouponsByShopId(shopId, LocalDateTime.now());
    }
    
    @Override
    @Transactional
    public UserCoupon receiveCoupon(Long userId, Long couponId) {
        // 查询优惠券
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(404, "优惠券不存在"));
        
        // 检查优惠券状态
        if (coupon.getStatus() != 1) {
            throw new BusinessException(400, "优惠券已禁用");
        }
        
        // 检查有效期
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            throw new BusinessException(400, "优惠券不在有效期内");
        }
        
        // 检查是否已领取
        if (userCouponRepository.existsByUserIdAndCouponId(userId, couponId)) {
            throw new BusinessException(400, "您已领取过该优惠券");
        }
        
        // 检查库存
        if (coupon.getTotalCount() != null && coupon.getUsedCount() >= coupon.getTotalCount()) {
            throw new BusinessException(400, "优惠券已被领完");
        }
        
        // 创建用户优惠券记录
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(0); // 未使用
        userCoupon.setReceiveTime(now);
        
        return userCouponRepository.save(userCoupon);
    }
    
    @Override
    public List<UserCoupon> getUserCoupons(Long userId) {
        return userCouponRepository.findByUserIdOrderByReceiveTimeDesc(userId);
    }
    
    @Override
    @Transactional
    public void useCoupon(Long userId, Long couponId, Long orderId) {
        // 查询用户优惠券
        UserCoupon userCoupon = userCouponRepository.findByUserIdAndCouponIdAndStatus(userId, couponId, 0)
                .orElseThrow(() -> new BusinessException(400, "优惠券不可用"));
        
        // 检查优惠券是否过期
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(404, "优惠券不存在"));
        
        if (LocalDateTime.now().isAfter(coupon.getEndTime())) {
            throw new BusinessException(400, "优惠券已过期");
        }
        
        // 更新用户优惠券状态
        userCoupon.setStatus(1); // 已使用
        userCoupon.setOrderId(orderId);
        userCoupon.setUseTime(LocalDateTime.now());
        userCouponRepository.save(userCoupon);
        
        // 更新优惠券使用数量
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
    }
}
