package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import server.model.Recipe;
import server.service.RecommendService;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {
    
    @Autowired
    private RecommendService recommendService;
    
    @GetMapping
    public List<Recipe> getRecommendedRecipes(@RequestAttribute Integer userId) {
        return recommendService.recommendRecipes(userId);
    }
} 