package com.sharkfitness.controller;

import com.sharkfitness.entity.User;
import com.sharkfitness.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody User body) {
        if (body.getId() != null) body.setId(null);
        if (repo.existsByUsername(body.getUsername())) {
            return ResponseEntity.status(409).body("username already exists");
        }
        return ResponseEntity.ok(repo.save(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return repo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<User> list() {
        return repo.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User body) {
        return repo.findById(id).map(old -> {
            old.setUsername(body.getUsername());
            old.setPasswordHash(body.getPasswordHash());
            old.setPhone(body.getPhone());
            old.setEmail(body.getEmail());
            old.setRole(body.getRole());
            old.setStatus(body.getStatus());
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
