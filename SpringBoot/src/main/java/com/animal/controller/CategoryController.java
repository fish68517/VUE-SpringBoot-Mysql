package com.animal.controller;

import com.animal.mapper.CategoryMapper;
import com.animal.mapper.UserMapper;
import com.animal.model.Category;
import com.animal.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryMapper.findAll();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Integer id) {
        return categoryMapper.findById(id);
    }

    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestParam("userId") Integer userId,
            @RequestBody Category category) {
        if (!canManageCategories(userId)) {
            return ResponseEntity.status(403).body("无权限操作分类");
        }
        categoryMapper.insert(category);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @RequestParam("userId") Integer userId,
            @PathVariable Integer id,
            @RequestBody Category category) {
        if (!canManageCategories(userId)) {
            return ResponseEntity.status(403).body("无权限操作分类");
        }
        category.setId(id);
        categoryMapper.update(category);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
            @RequestParam("userId") Integer userId,
            @PathVariable Integer id) {
        if (!canManageCategories(userId)) {
            return ResponseEntity.status(403).body("无权限操作分类");
        }
        categoryMapper.delete(id);
        return ResponseEntity.ok().build();
    }

    private boolean canManageCategories(Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null || user.getRole() == null) {
            return false;
        }
        return "admin".equals(user.getRole()) || "merchant".equals(user.getRole());
    }
}
