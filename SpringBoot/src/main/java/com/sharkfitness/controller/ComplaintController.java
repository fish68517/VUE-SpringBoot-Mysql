package com.sharkfitness.controller;

import com.sharkfitness.entity.Complaint;
import com.sharkfitness.entity.User;
import com.sharkfitness.repository.ComplaintRepository;
import jakarta.persistence.EntityManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin
public class ComplaintController {

    private final ComplaintRepository repo;
    private final EntityManager em;

    public ComplaintController(ComplaintRepository repo, EntityManager em) {
        this.repo = repo;
        this.em = em;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Complaint body) {
        if (body.getId() != null) body.setId(null);
        Long fromId = body.getFromUser() != null ? body.getFromUser().getId() : null;
        if (fromId == null) return ResponseEntity.badRequest().body("fromUser.id is required");
        body.setFromUser(em.getReference(User.class, fromId));

        if (body.getHandlerAdmin() != null && body.getHandlerAdmin().getId() != null) {
            body.setHandlerAdmin(em.getReference(User.class, body.getHandlerAdmin().getId()));
        }
        return ResponseEntity.ok(repo.save(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return repo.findById(id).<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Complaint> list() { return repo.findAll(); }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Complaint body) {
        return repo.findById(id).map(old -> {
            Long fromId = body.getFromUser() != null ? body.getFromUser().getId() : null;
            if (fromId != null) old.setFromUser(em.getReference(User.class, fromId));

            old.setTargetType(body.getTargetType());
            old.setTargetId(body.getTargetId());
            old.setType(body.getType());
            old.setContent(body.getContent());
            old.setEvidenceImg(body.getEvidenceImg());
            old.setStatus(body.getStatus());
            old.setResult(body.getResult());
            old.setHandleTime(body.getHandleTime());

            Long adminId = body.getHandlerAdmin() != null ? body.getHandlerAdmin().getId() : null;
            if (adminId != null) old.setHandlerAdmin(em.getReference(User.class, adminId));

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
