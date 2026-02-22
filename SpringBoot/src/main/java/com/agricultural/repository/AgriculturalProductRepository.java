package com.agricultural.repository;

import com.agricultural.entity.AgriculturalProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 农资产品数据访问层接口
 */
@Repository
public interface AgriculturalProductRepository extends JpaRepository<AgriculturalProduct, Long> {
    
    /**
     * 根据产品类别查询产品列表
     */
    List<AgriculturalProduct> findByCategory(String category);
    
    /**
     * 根据商家ID查询产品列表
     */
    List<AgriculturalProduct> findByMerchantId(Long merchantId);
    
    /**
     * 根据产品名称模糊查询
     */
    List<AgriculturalProduct> findByProductNameContaining(String productName);
    
    /**
     * 查询库存充足的产品（库存大于0）
     */
    @Query("SELECT p FROM AgriculturalProduct p WHERE p.stock > 0 ORDER BY p.stock DESC")
    List<AgriculturalProduct> findProductsInStock();
    
    /**
     * 根据适用天气查询产品列表
     */
    @Query("SELECT p FROM AgriculturalProduct p WHERE p.applicableWeather LIKE %:weather% AND p.stock > 0")
    List<AgriculturalProduct> findByApplicableWeather(@Param("weather") String weather);
    
    /**
     * 根据类别和商家ID查询产品列表
     */
    List<AgriculturalProduct> findByCategoryAndMerchantId(String category, Long merchantId);
}
