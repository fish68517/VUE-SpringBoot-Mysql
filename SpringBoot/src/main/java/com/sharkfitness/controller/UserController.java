package com.sharkfitness.controller;

import com.sharkfitness.entity.User;
import com.sharkfitness.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.dao.DataIntegrityViolationException; // 记得导入这个包
import org.springframework.http.HttpStatus;
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


 /*   @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        // 1. 检查是否存在
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            // 2. 尝试删除
            repo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // 3. 捕获外键冲突异常 (即：该数据正在被其他表引用)
            // 返回 400 Bad Request 或者 409 Conflict，并带上错误提示
            return ResponseEntity.ok("该用户已被关联（例如有申请记录等其他数据），无法直接删除！");
            *//*return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("该用户已被关联（例如有申请记录等其他数据），无法直接删除！");*//*
        } catch (Exception e) {
            // 4. 处理其他可能的异常
        *//*    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("删除失败：" + e.getMessage());*//*
            return ResponseEntity.ok("该用户已被关联（例如有申请记录等其他数据），无法直接删除！");
        }

        return ResponseEntity.ok().build();
    }*/
}
