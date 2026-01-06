package com.sharkfitness.controller;

import com.sharkfitness.entity.Application;
import com.sharkfitness.entity.JobPost;
import com.sharkfitness.entity.User;
import com.sharkfitness.repository.ApplicationRepository;
import jakarta.persistence.EntityManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin
public class ApplicationController {

    private final ApplicationRepository repo;
    private final EntityManager em;

    public ApplicationController(ApplicationRepository repo, EntityManager em) {
        this.repo = repo;
        this.em = em;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Application body) {
        if (body.getId() != null) body.setId(null);

        Long jobId = body.getJobPost() != null ? body.getJobPost().getId() : null;
        Long userId = body.getJobseeker() != null ? body.getJobseeker().getId() : null;
        if (jobId == null || userId == null) return ResponseEntity.badRequest().body("jobPost.id and jobseeker.id are required");

        if (repo.existsByJobPost_IdAndJobseeker_Id(jobId, userId)) {
            return ResponseEntity.status(409).body("duplicate application");
        }

        body.setJobPost(em.getReference(JobPost.class, jobId));
        body.setJobseeker(em.getReference(User.class, userId));
        return ResponseEntity.ok(repo.save(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return repo.findById(id).<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Application> list() { return repo.findAll(); }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Application body) {
        return repo.findById(id).map(old -> {
            Long jobId = body.getJobPost() != null ? body.getJobPost().getId() : null;
            Long userId = body.getJobseeker() != null ? body.getJobseeker().getId() : null;
            if (jobId != null) old.setJobPost(em.getReference(JobPost.class, jobId));
            if (userId != null) old.setJobseeker(em.getReference(User.class, userId));

            old.setStatus(body.getStatus());
            old.setApplyTime(body.getApplyTime());
            old.setMerchantNote(body.getMerchantNote());
            old.setCancelReason(body.getCancelReason());
            old.setCheckinTime(body.getCheckinTime());
            old.setFinishTime(body.getFinishTime());
            old.setSettleTime(body.getSettleTime());

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
