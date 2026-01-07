package server.mapper;

import org.apache.ibatis.annotations.*;
import server.model.Review;
import java.util.List;

@Mapper
public interface ReviewMapper {
    @Select("SELECT r.*, u.nickname as 'user.nickname', u.avatar as 'user.avatar' " +
            "FROM review r " +
            "LEFT JOIN user u ON r.user_id = u.id " +
            "WHERE r.recipe_id = #{recipeId} " +
            "ORDER BY r.created_at DESC")
    List<Review> findByRecipeId(Integer recipeId);

    @Select("SELECT * FROM review WHERE id = #{id}")
    Review findById(Integer id);

    @Insert("INSERT INTO review(user_id, recipe_id, rating, content) " +
            "VALUES(#{userId}, #{recipeId}, #{rating}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Review review);

    @Update("UPDATE review SET rating = #{rating}, content = #{content} WHERE id = #{id}")
    int update(Review review);

    @Delete("DELETE FROM review WHERE id = #{id}")
    int delete(Integer id);
} 