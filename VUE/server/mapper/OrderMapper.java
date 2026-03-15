package server.mapper;

import org.apache.ibatis.annotations.*;
import server.model.Order;
import java.util.List;

@Mapper
public interface OrderMapper {
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Order> findByUserId(Integer userId);

    @Select("SELECT * FROM `order` WHERE id = #{id}")
    Order findById(Integer id);

    @Insert("INSERT INTO `order`(user_id, order_no, total_amount, status) " +
            "VALUES(#{userId}, #{orderNo}, #{totalAmount}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Update("UPDATE `order` SET status = #{status} WHERE id = #{id}")
    int updateStatus(Order order);
} 