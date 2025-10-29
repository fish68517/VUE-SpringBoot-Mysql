package com.graduation.repository; // 包名可能不同

import com.graduation.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    /**
     * 根据入库单ID查询所有相关的库存批次信息。
     *
     * Spring Data JPA 会自动解析这个方法名，并生成类似如下的 SQL:
     * "SELECT * FROM inventory WHERE inbound_order_id = ?"
     *
     * @param inboundOrderId 入库单的 ID (对应于实体类中的 inboundOrderId 字段)
     * @return 返回一个包含所有匹配的 Inventory 实体的列表
     */
    List<Inventory> findByInboundOrderId(Integer inboundOrderId);
}