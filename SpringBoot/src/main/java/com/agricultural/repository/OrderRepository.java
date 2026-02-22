package com.agricultural.repository;

import com.agricultural.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问层接口
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 根据订单号查询订单
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * 根据用户ID查询订单列表
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * 根据订单状态查询订单列表
     */
    List<Order> findByStatus(Order.OrderStatus status);
    
    /**
     * 根据产品ID查询订单列表
     */
    List<Order> findByProductId(Long productId);
    
    /**
     * 根据用户ID和订单状态查询订单列表
     */
    List<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status);
    
    /**
     * 根据用户ID和时间范围查询订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.createdAt BETWEEN :startTime AND :endTime ORDER BY o.createdAt DESC")
    List<Order> findByUserIdAndTimeRange(@Param("userId") Long userId, 
                                         @Param("startTime") LocalDateTime startTime, 
                                         @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据时间范围查询订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startTime AND :endTime ORDER BY o.createdAt DESC")
    List<Order> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询待支付的订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' ORDER BY o.createdAt ASC")
    List<Order> findPendingOrders();
}
