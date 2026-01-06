package com.sharkfitness.controller;

import com.sharkfitness.entity.Application;
import com.sharkfitness.entity.Review;
import com.sharkfitness.entity.User;
import com.sharkfitness.repository.ReviewRepository;
import jakarta.persistence.EntityManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin
public class ReviewController {

    private final ReviewRepository repo;
    private final EntityManager em;

    public ReviewController(ReviewRepository repo, EntityManager em) {
        this.repo = repo;
        this.em = em;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Review body) {
        if (body.getId() != null) body.setId(null);
        Long appId = body.getApplication() != null ? body.getApplication().getId() : null;
        Long fromId = body.getFromUser() != null ? body.getFromUser().getId() : null;
        Long toId = body.getToUser() != null ? body.getToUser().getId() : null;
        if (appId == null || fromId == null || toId == null) {
            return ResponseEntity.badRequest().body("application.id, fromUser.id, toUser.id are required");
        }
        body.setApplication(em.getReference(Application.class, appId));
        body.setFromUser(em.getReference(User.class, fromId));
        body.setToUser(em.getReference(User.class, toId));
        return ResponseEntity.ok(repo.save(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return repo.findById(id).<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Review> list() { return repo.findAll(); }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Review body) {
        return repo.findById(id).map(old -> {
            Long appId = body.getApplication() != null ? body.getApplication().getId() : null;
            Long fromId = body.getFromUser() != null ? body.getFromUser().getId() : null;
            Long toId = body.getToUser() != null ? body.getToUser().getId() : null;

            if (appId != null) old.setApplication(em.getReference(Application.class, appId));
            if (fromId != null) old.setFromUser(em.getReference(User.class, fromId));
            if (toId != null) old.setToUser(em.getReference(User.class, toId));

            old.setRating(body.getRating());
            old.setContent(body.getContent());
            return ResponseEntity.ok(repo.save(old));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
