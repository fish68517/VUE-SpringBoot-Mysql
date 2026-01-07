package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import server.mapper.ReviewMapper;
import server.model.Review;
import java.util.List;

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
} 