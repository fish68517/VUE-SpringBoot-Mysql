package com.animal.mapper;

import com.animal.model.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {
    @Select("SELECT * FROM `order` ORDER BY created_at DESC")
    List<Order> findAllOrders();

    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Order> findByUserId(Integer userId);

    @Select("SELECT DISTINCT o.* FROM `order` o " +
            "JOIN order_detail od ON od.order_id = o.id " +
            "JOIN recipe r ON r.id = od.recipe_id " +
            "WHERE r.merchant_id = #{merchantId} " +
            "ORDER BY o.created_at DESC")
    List<Order> findByMerchantId(@Param("merchantId") Integer merchantId);

    @Select("SELECT * FROM `order` WHERE id = #{id}")
    Order findById(Integer id);

    @Insert("INSERT INTO `order`(user_id, order_no, pickup_code, total_amount, status) " +
            "VALUES(#{userId}, #{orderNo}, #{pickupCode}, #{totalAmount}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Update("UPDATE `order` SET status = #{status} WHERE id = #{id}")
    int updateStatus(Order order);
} 
