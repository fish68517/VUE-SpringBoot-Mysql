package com.animal.controller;

import com.animal.mapper.RecipeMapper;
import com.animal.model.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    @Autowired
    private RecipeMapper recipeMapper;

 /*   @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeMapper.findAll();
    }*/

    @GetMapping("/{id}")
    public Recipe getRecipeById(@PathVariable Integer id) {
        return recipeMapper.findById(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<Recipe> getRecipesByCategoryId(@PathVariable Integer categoryId) {
        return recipeMapper.findByCategoryId(categoryId);
    }

    @PostMapping
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        recipeMapper.insert(recipe);
        return recipe;
    }

    @PutMapping("/{id}")
    public Recipe updateRecipe(@PathVariable Integer id, @RequestBody Recipe recipe) {
        recipe.setId(id);
        recipeMapper.update(recipe);
        return recipe;
    }

    @DeleteMapping("/{id}")
    public void deleteRecipe(@PathVariable Integer id) {
        recipeMapper.delete(id);
    }


    @GetMapping("/search")
    public List<Recipe> searchRecipes(@RequestParam String keyword) {
        return recipeMapper.searchRecipes(keyword);
    }

    @GetMapping
    public ResponseEntity<?> getRecipes(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String query) {
        int offset = (page - 1) * pageSize;

        int total = recipeMapper.count(query);

        List<Recipe> recipes = recipeMapper.findByPage(offset, pageSize, query);

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("data", recipes);

        return ResponseEntity.ok(result);
    }

} 