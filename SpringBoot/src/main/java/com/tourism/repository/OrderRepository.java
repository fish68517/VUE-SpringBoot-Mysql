package com.tourism.repository;

import com.tourism.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import java.math.BigDecimal;

/**
 * 订单数据访问接口
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 根据订单号查询订单
     * @param orderNumber 订单号
     * @return 订单对象
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * 根据用户ID查询订单列表（分页）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和状态查询订单列表（分页）
     * @param userId 用户ID
     * @param status 订单状态
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<Order> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
    
    /**
     * 根据状态查询订单列表（分页）
     * @param status 订单状态
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<Order> findByStatus(String status, Pageable pageable);
    
    /**
     * 根据订单类型查询订单列表（分页）
     * @param orderType 订单类型
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<Order> findByOrderType(String orderType, Pageable pageable);
    
    /**
     * 根据用户ID查询所有订单
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * 检查订单号是否存在
     * @param orderNumber 订单号
     * @return 是否存在
     */
    boolean existsByOrderNumber(String orderNumber);
    
    /**
     * 统计指定状态的订单数量
     * @param status 订单状态
     * @return 订单数量
     */
    long countByStatus(String status);
    
    /**
     * 统计指定类型的订单数量
     * @param orderType 订单类型
     * @return 订单数量
     */
    long countByOrderType(String orderType);
    
    /**
     * 统计指定状态的订单总价
     * @param status 订单状态
     * @return 总价
     */
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = :status")
    BigDecimal sumTotalPriceByStatus(String status);
    
    /**
     * 统计所有订单的总价
     * @return 总价
     */
    @Query("SELECT SUM(o.totalPrice) FROM Order o")
    BigDecimal sumTotalPrice();
