package com.naxi.repository;

import com.naxi.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByRoleId(Long roleId);

    Optional<Permission> findByRoleIdAndPermissionName(Long roleId, String permissionName);
}
