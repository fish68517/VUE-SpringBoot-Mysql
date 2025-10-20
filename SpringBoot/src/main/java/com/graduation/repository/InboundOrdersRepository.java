package com.graduation.repository;

import com.graduation.entity.InboundOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboundOrdersRepository extends JpaRepository<InboundOrders, Long> {
    // JpaRepository 已经提供了 save(), findById(), findAll(), deleteById() 等方法
    // 你还可以在这里定义自定义查询方法
    // 例如：根据状态查找订单
    // List<InboundOrders> findByStatus(String status);
}