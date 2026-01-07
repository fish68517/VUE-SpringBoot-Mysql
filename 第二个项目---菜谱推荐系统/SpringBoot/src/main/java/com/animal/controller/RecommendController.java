package com.animal.controller;

import com.animal.model.Recipe;
import com.animal.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @GetMapping
    public List<Recipe> getRecommendedRecipes(@RequestParam Integer userId) {
        return recommendService.recommendRecipes(userId);
    }
}