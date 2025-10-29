package com.graduation.repository; // 包名可能不同

import com.graduation.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    /**
     * 根据批次号精确查找库存记录。
     * 因为 batch_code 是唯一的，所以我们期望最多只返回一条记录。
     * 使用 Optional<Inventory> 是处理“可能为空”结果的最佳实践。
     *
     * @param batchCode 批次唯一编码
     * @return 包含查询结果的 Optional 对象
     */
    Optional<Inventory> findByBatchCode(String batchCode);


    // 在 InventoryRepository.java 中
    List<Inventory> findByProductId(Integer productId);
}