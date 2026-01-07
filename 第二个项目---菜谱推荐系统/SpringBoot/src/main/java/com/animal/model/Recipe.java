package com.animal.model;

import lombok.Data;
import java.util.Date;

@Data
public class Recipe {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String description;
    private String ingredients;
    private String steps;
    private String image;
    private Integer cookingTime;
    private String difficulty;
    private Date createdAt;

    private String taste;
    private String cookingMethod;
    private String cuisineType;
    private String foodCategory;
}