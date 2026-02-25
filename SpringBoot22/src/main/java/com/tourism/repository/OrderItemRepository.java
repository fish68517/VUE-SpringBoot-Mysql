package com.tourism.repository;

import com.tourism.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 订单项目数据访问接口
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * 根据订单ID查询订单项目列表
     * @param orderId 订单ID
     * @return 订单项目列表
     */
    List<OrderItem> findByOrderId(Long orderId);
    
    /**
     * 根据订单ID和项目类型查询订单项目列表
     * @param orderId 订单ID
     * @param itemType 项目类型
     * @return 订单项目列表
     */
    List<OrderItem> findByOrderIdAndItemType(Long orderId, String itemType);
    
    /**
     * 根据项目类型和项目ID查询订单项目列表
     * @param itemType 项目类型
     * @param itemId 项目ID
     * @return 订单项目列表
     */
    List<OrderItem> findByItemTypeAndItemId(String itemType, Long itemId);
    
    /**
     * 删除指定订单的所有项目
     * @param orderId 订单ID
     */
    void deleteByOrderId(Long orderId);
}
