package com.sharkfitness.controller;


import com.sharkfitness.entity.Company;
import com.sharkfitness.entity.JobPost;
import com.sharkfitness.repository.JobPostRepository;
import jakarta.persistence.EntityManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-posts")
@CrossOrigin
public class JobPostController {

    private final JobPostRepository repo;
    private final EntityManager em;

    public JobPostController(JobPostRepository repo, EntityManager em) {
        this.repo = repo;
        this.em = em;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody JobPost body) {
        if (body.getId() != null) body.setId(null);
        Long companyId = body.getCompany() != null ? body.getCompany().getId() : null;
        if (companyId == null) return ResponseEntity.badRequest().body("company.id is required");
        body.setCompany(em.getReference(Company.class, companyId));
        return ResponseEntity.ok(repo.save(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return repo.findById(id).<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<JobPost> list() { return repo.findAll(); }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody JobPost body) {
        return repo.findById(id).map(old -> {
            Long companyId = body.getCompany() != null ? body.getCompany().getId() : null;
            if (companyId != null) old.setCompany(em.getReference(Company.class, companyId));

            old.setTitle(body.getTitle());
            old.setCategory(body.getCategory());
            old.setSalaryAmount(body.getSalaryAmount());
            old.setSalaryUnit(body.getSalaryUnit());
            old.setSettlementType(body.getSettlementType());
            old.setWorkCity(body.getWorkCity());
            old.setWorkAddress(body.getWorkAddress());
            old.setLongitude(body.getLongitude());
            old.setLatitude(body.getLatitude());
            old.setStartTime(body.getStartTime());
            old.setEndTime(body.getEndTime());
            old.setHeadcount(body.getHeadcount());
            old.setAppliedCount(body.getAppliedCount());
            old.setContactPhone(body.getContactPhone());
            old.setCoverImg(body.getCoverImg());
            old.setAttachmentFile(body.getAttachmentFile());
            old.setDescription(body.getDescription());
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
