package com.graduation.repository;

import com.graduation.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Integer> {

    /**
     * 根据 SKU 查找产品信息。
     * SKU 也是唯一的。
     *
     * @param sku 产品的 SKU
     * @return 包含查询结果的 Optional 对象
     */
    Optional<Products> findBySku(String sku);
}