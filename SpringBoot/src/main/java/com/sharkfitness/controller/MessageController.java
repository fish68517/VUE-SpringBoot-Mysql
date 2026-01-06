package com.sharkfitness.controller;

import com.sharkfitness.entity.Message;
import com.sharkfitness.entity.User;
import com.sharkfitness.repository.MessageRepository;
import jakarta.persistence.EntityManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin
public class MessageController {

    private final MessageRepository repo;
    private final EntityManager em;

    public MessageController(MessageRepository repo, EntityManager em) {
        this.repo = repo;
        this.em = em;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Message body) {
        if (body.getId() != null) body.setId(null);
        Long toId = body.getToUser() != null ? body.getToUser().getId() : null;
        if (toId == null) return ResponseEntity.badRequest().body("toUser.id is required");
        body.setToUser(em.getReference(User.class, toId));
        return ResponseEntity.ok(repo.save(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return repo.findById(id).<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Message> list() { return repo.findAll(); }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Message body) {
        return repo.findById(id).map(old -> {
            Long toId = body.getToUser() != null ? body.getToUser().getId() : null;
            if (toId != null) old.setToUser(em.getReference(User.class, toId));
            old.setTitle(body.getTitle());
            old.setContent(body.getContent());
            old.setReadFlag(body.getReadFlag());
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
