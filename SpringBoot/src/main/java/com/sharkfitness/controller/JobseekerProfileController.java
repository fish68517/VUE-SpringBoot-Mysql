package com.sharkfitness.controller;

import com.sharkfitness.entity.JobseekerProfile;
import com.sharkfitness.entity.User;
import com.sharkfitness.repository.JobseekerProfileRepository;
import jakarta.persistence.EntityManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobseeker-profiles")
@CrossOrigin
public class JobseekerProfileController {

    private final JobseekerProfileRepository repo;
    private final EntityManager em;

    public JobseekerProfileController(JobseekerProfileRepository repo, EntityManager em) {
        this.repo = repo;
        this.em = em;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody JobseekerProfile body) {
        if (body.getId() != null) body.setId(null);
        // 只要 body.user.id 有值即可
        Long uid = body.getUser() != null ? body.getUser().getId() : null;
        if (uid == null) return ResponseEntity.badRequest().body("user.id is required");
        body.setUser(em.getReference(User.class, uid));
        return ResponseEntity.ok(repo.save(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return repo.findById(id).<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<JobseekerProfile> list() { return repo.findAll(); }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody JobseekerProfile body) {
        return repo.findById(id).map(old -> {
            Long uid = body.getUser() != null ? body.getUser().getId() : null;
            if (uid != null) old.setUser(em.getReference(User.class, uid));
            old.setRealName(body.getRealName());
            old.setGender(body.getGender());
            old.setAge(body.getAge());
            old.setCity(body.getCity());
            old.setSkills(body.getSkills());
            old.setAvailableTimeDesc(body.getAvailableTimeDesc());
            old.setResumeFile(body.getResumeFile());
            old.setIdCardFront(body.getIdCardFront());
            old.setIdCardBack(body.getIdCardBack());
            old.setVerifyStatus(body.getVerifyStatus());
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
