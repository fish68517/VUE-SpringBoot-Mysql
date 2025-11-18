package com.xingluo.petshop.repository;

import com.xingluo.petshop.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户优惠券 Repository
 */
@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    
    /**
     * 查询用户的所有优惠券
     */
    List<UserCoupon> findByUserIdOrderByReceiveTimeDesc(Long userId);
    
    /**
     * 查询用户是否已领取某优惠券
     */
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
    
    /**
     * 查询用户的可用优惠券（未使用且未过期）
     */
    @Query("SELECT uc FROM UserCoupon uc JOIN FETCH uc.coupon c " +
           "WHERE uc.userId = ?1 AND uc.status = 0 " +
           "AND c.endTime >= CURRENT_TIMESTAMP")
    List<UserCoupon> findAvailableUserCoupons(Long userId);
    
    /**
     * 查询用户某个优惠券的未使用记录
     */
    Optional<UserCoupon> findByUserIdAndCouponIdAndStatus(Long userId, Long couponId, Integer status);
}
