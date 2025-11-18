package com.xingluo.petshop.repository;

import com.xingluo.petshop.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券 Repository
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    /**
     * 查询店铺的所有优惠券
     */
    List<Coupon> findByShopIdOrderByCreateTimeDesc(Long shopId);
    
    /**
     * 查询店铺的可用优惠券（启用状态且在有效期内）
     */
    @Query("SELECT c FROM Coupon c WHERE c.shopId = ?1 AND c.status = 1 " +
           "AND c.startTime <= ?2 AND c.endTime >= ?2 " +
           "AND (c.totalCount IS NULL OR c.usedCount < c.totalCount)")
    List<Coupon> findAvailableCouponsByShopId(Long shopId, LocalDateTime now);
}
