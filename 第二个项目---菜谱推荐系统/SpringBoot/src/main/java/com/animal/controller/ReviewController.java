package com.animal.controller;

import com.animal.mapper.ReviewMapper;
import com.animal.model.Recipe;
import com.animal.model.Review;
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

    @GetMapping("/recipe/{recipeId}")
    public List<Review> getRecipeReviews(@PathVariable Integer recipeId) {
        return reviewMapper.findByRecipeId(recipeId);
    }

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        reviewMapper.insert(review);
        return review;
    }

    @PutMapping("/{id}")
    public Review updateReview(@PathVariable Integer id, @RequestBody Review review) {
        review.setId(id);
        reviewMapper.update(review);
        return review;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Integer id) {
        reviewMapper.delete(id);
    }


    @GetMapping
    public ResponseEntity<?> getReviews(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String query) {
        int offset = (page - 1) * pageSize;

        int total = reviewMapper.count(query);

        List<Review> recipes = reviewMapper.findByPage(offset, pageSize, query);

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("data", recipes);

        return ResponseEntity.ok(result);
    }
} 