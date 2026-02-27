package com.animal.mapper;

import com.animal.model.Recipe;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RecipeMapper {
    @Select("SELECT * FROM recipe")
    List<Recipe> findAll();

    @Select("SELECT * FROM recipe WHERE id = #{id}")
    Recipe findById(Integer id);

    @Select("SELECT * FROM recipe WHERE category_id = #{categoryId}")
    List<Recipe> findByCategoryId(Integer categoryId);

    @Insert("INSERT INTO recipe(category_id, name, description, ingredients, steps, cooking_time, image, difficulty) " +
            "VALUES(#{categoryId}, #{name}, #{description}, #{ingredients}, #{steps}, #{cookingTime}, #{image}, #{difficulty})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Recipe recipe);

    @Update("UPDATE recipe SET name = #{name}, description = #{description}, ingredients = #{ingredients}, " +
            "steps = #{steps}, cooking_time = #{cookingTime}, image = #{image}, difficulty = #{difficulty} WHERE id = #{id}")
    int update(Recipe recipe);

    @Delete("DELETE FROM recipe WHERE id = #{id}")
    int delete(Integer id);

    @Select("SELECT * FROM recipe WHERE name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR description LIKE CONCAT('%', #{keyword}, '%')")
    List<Recipe> searchRecipes(@Param("keyword") String keyword);



    @Select("SELECT COUNT(*) FROM recipe " +
            "WHERE name LIKE CONCAT('%', #{query}, '%') " +
            "OR description LIKE CONCAT('%', #{query}, '%')")
    int count(@Param("query") String query);

    @Select("SELECT r.*, c.name as 'category.name' " +
            "FROM recipe r " +
            "LEFT JOIN category c ON r.category_id = c.id " +
            "WHERE r.name LIKE CONCAT('%', #{query}, '%') " +
            "OR r.description LIKE CONCAT('%', #{query}, '%') " +
            "ORDER BY r.id DESC " +
            "LIMIT #{offset}, #{pageSize}")
    List<Recipe> findByPage(
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize,
            @Param("query") String query);
} 