package com.animal.mapper;

import com.animal.model.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

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

    @Insert("INSERT INTO `order`(user_id, order_no, pickup_code, remark, total_amount, status) " +
            "VALUES(#{userId}, #{orderNo}, #{pickupCode}, #{remark}, #{totalAmount}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Update("UPDATE `order` SET status = #{status} WHERE id = #{id}")
    int updateStatus(Order order);

    @Select("<script>" +
            "SELECT COUNT(*) FROM `order` " +
            "<where>" +
            "<if test='query != null and query != \"\"'>" +
            "(order_no LIKE CONCAT('%', #{query}, '%') OR IFNULL(remark, '') LIKE CONCAT('%', #{query}, '%'))" +
            "</if>" +
            "</where>" +
            "</script>")
    int countAll(@Param("query") String query);

    @Select("<script>" +
            "SELECT * FROM `order` " +
            "<where>" +
            "<if test='query != null and query != \"\"'>" +
            "(order_no LIKE CONCAT('%', #{query}, '%') OR IFNULL(remark, '') LIKE CONCAT('%', #{query}, '%'))" +
            "</if>" +
            "</where>" +
            "ORDER BY created_at DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Order> findAllByPage(
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize,
            @Param("query") String query);

    @Select("<script>" +
            "SELECT COUNT(*) FROM `order` " +
            "<where>" +
            "user_id = #{userId} " +
            "<if test='query != null and query != \"\"'>" +
            "AND (order_no LIKE CONCAT('%', #{query}, '%') OR IFNULL(remark, '') LIKE CONCAT('%', #{query}, '%'))" +
            "</if>" +
            "</where>" +
            "</script>")
    int countByUser(@Param("userId") Integer userId, @Param("query") String query);

    @Select("<script>" +
            "SELECT * FROM `order` " +
            "<where>" +
            "user_id = #{userId} " +
            "<if test='query != null and query != \"\"'>" +
            "AND (order_no LIKE CONCAT('%', #{query}, '%') OR IFNULL(remark, '') LIKE CONCAT('%', #{query}, '%'))" +
            "</if>" +
            "</where>" +
            "ORDER BY created_at DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Order> findByUserByPage(
            @Param("userId") Integer userId,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize,
            @Param("query") String query);

    @Select("<script>" +
            "SELECT COUNT(*) FROM `order` o " +
            "<where>" +
            "EXISTS (" +
            "SELECT 1 FROM order_detail od JOIN recipe r ON r.id = od.recipe_id " +
            "WHERE od.order_id = o.id AND r.merchant_id = #{merchantId}" +
            ") " +
            "<if test='query != null and query != \"\"'>" +
            "AND (o.order_no LIKE CONCAT('%', #{query}, '%') OR IFNULL(o.remark, '') LIKE CONCAT('%', #{query}, '%'))" +
            "</if>" +
            "</where>" +
            "</script>")
    int countByMerchant(@Param("merchantId") Integer merchantId, @Param("query") String query);

    @Select("<script>" +
            "SELECT o.* FROM `order` o " +
            "<where>" +
            "EXISTS (" +
            "SELECT 1 FROM order_detail od JOIN recipe r ON r.id = od.recipe_id " +
            "WHERE od.order_id = o.id AND r.merchant_id = #{merchantId}" +
            ") " +
            "<if test='query != null and query != \"\"'>" +
            "AND (o.order_no LIKE CONCAT('%', #{query}, '%') OR IFNULL(o.remark, '') LIKE CONCAT('%', #{query}, '%'))" +
            "</if>" +
            "</where>" +
            "ORDER BY o.created_at DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Order> findByMerchantByPage(
            @Param("merchantId") Integer merchantId,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize,
            @Param("query") String query);

    @Select("SELECT COALESCE(r.window_id, r.category_id) AS windowId, COUNT(DISTINCT o.id) AS waitingCount " +
            "FROM `order` o " +
            "JOIN order_detail od ON od.order_id = o.id " +
            "JOIN recipe r ON r.id = od.recipe_id " +
            "WHERE COALESCE(r.window_id, r.category_id) IS NOT NULL " +
            "AND o.status IN ('\u5df2\u4ed8\u6b3e', '\u5df2\u5b8c\u6210') " +
            "GROUP BY COALESCE(r.window_id, r.category_id)")
    List<Map<String, Object>> countWaitingByWindow();
}
