package server.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;

@Data
public class Recipe {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String description;
    private String image;
    private String ingredients;
    private String steps;
    private Integer cookingTime;
    private String difficulty;
    private BigDecimal price;
    private Date createdAt;
    private Date updatedAt;
    
    // 添加分类信息
    private Category category;
} 