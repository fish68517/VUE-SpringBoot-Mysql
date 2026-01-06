package com.sharkfitness.controller;


import com.sharkfitness.entity.Company;
import com.sharkfitness.entity.User;
import com.sharkfitness.repository.CompanyRepository;
import jakarta.persistence.EntityManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin
public class CompanyController {

    private final CompanyRepository repo;
    private final EntityManager em;

    public CompanyController(CompanyRepository repo, EntityManager em) {
        this.repo = repo;
        this.em = em;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Company body) {
        if (body.getId() != null) body.setId(null);
        Long merchantId = body.getMerchant() != null ? body.getMerchant().getId() : null;
        if (merchantId == null) return ResponseEntity.badRequest().body("merchant.id is required");
        body.setMerchant(em.getReference(User.class, merchantId));
        return ResponseEntity.ok(repo.save(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return repo.findById(id).<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Company> list() { return repo.findAll(); }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Company body) {
        return repo.findById(id).map(old -> {
            Long merchantId = body.getMerchant() != null ? body.getMerchant().getId() : null;
            if (merchantId != null) old.setMerchant(em.getReference(User.class, merchantId));
            old.setName(body.getName());
            old.setLicenseNo(body.getLicenseNo());
            old.setLicenseImg(body.getLicenseImg());
            old.setAddress(body.getAddress());
            old.setContactName(body.getContactName());
            old.setContactPhone(body.getContactPhone());
            old.setVerifyStatus(body.getVerifyStatus());
            old.setRejectReason(body.getRejectReason());
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
