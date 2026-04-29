package com.animal.controller;

import com.animal.mapper.ReviewMapper;
import com.animal.mapper.UserMapper;
import com.animal.model.Review;
import com.animal.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/recipe/{recipeId}")
    public List<Review> getRecipeReviews(@PathVariable Integer recipeId) {
        return reviewMapper.findByRecipeId(recipeId);
    }

    @PostMapping
    public ResponseEntity<?> createReview(
            @RequestParam("userId") Integer userId,
            @RequestBody Review review) {
        review.setUserId(userId);
        reviewMapper.insert(review);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @RequestParam("userId") Integer userId,
            @PathVariable Integer id,
            @RequestBody Review review) {
        Review exist = reviewMapper.findById(id);
        if (exist == null) {
            return ResponseEntity.notFound().build();
        }
        if (!userId.equals(exist.getUserId())) {
            return ResponseEntity.status(403).body("无权修改该评价");
        }
        review.setId(id);
        review.setUserId(userId);
        reviewMapper.update(review);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(
            @RequestParam("userId") Integer userId,
            @PathVariable Integer id) {
        Review exist = reviewMapper.findById(id);
        if (exist == null) {
            return ResponseEntity.notFound().build();
        }

        User operator = userMapper.findById(userId);
        if (operator == null) {
            return ResponseEntity.badRequest().body("用户不存在");
        }

        boolean isAdmin = "admin".equals(operator.getRole());
        boolean isOwner = userId.equals(exist.getUserId());
        if (!isAdmin && !isOwner) {
            return ResponseEntity.status(403).body("无权删除该评价");
        }

        reviewMapper.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getReviews(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String query) {
        String keyword = query == null ? "" : query;
        int offset = (page - 1) * pageSize;
        int total = reviewMapper.count(keyword);
        List<Review> reviews = reviewMapper.findByPage(offset, pageSize, keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("data", reviews);
        return ResponseEntity.ok(result);
    }
}
