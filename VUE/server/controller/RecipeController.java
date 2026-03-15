package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.mapper.RecipeMapper;
import server.model.Recipe;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    @Autowired
    private RecipeMapper recipeMapper;

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
        result.put("items", recipes);
        
        return ResponseEntity.ok(result);
    }

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
    public ResponseEntity<?> searchRecipes(@RequestParam String keyword) {
        List<Recipe> recipes = recipeMapper.searchRecipes(keyword);
        return ResponseEntity.ok(recipes);
    }
} 