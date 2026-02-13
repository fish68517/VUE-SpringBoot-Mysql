package com.naxi.service;

import com.naxi.entity.AdminAuditLog;
import com.naxi.entity.Permission;
import com.naxi.entity.Role;
import com.naxi.entity.SystemLog;
import com.naxi.entity.SystemSetting;
import com.naxi.repository.AdminAuditLogRepository;
import com.naxi.repository.PermissionRepository;
import com.naxi.repository.RoleRepository;
import com.naxi.repository.SystemLogRepository;
import com.naxi.repository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SystemService {
    @Autowired
    private SystemSettingRepository systemSettingRepository;

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Autowired
    private AdminAuditLogRepository adminAuditLogRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * 获取系统参数
     * 根据参数key获取参数值
     */
    public SystemSetting getSystemSetting(String settingKey) {
        Optional<SystemSetting> setting = systemSettingRepository.findBySettingKey(settingKey);
        return setting.orElse(null);
    }

    /**
     * 获取所有系统参数
     */
    public List<SystemSetting> getAllSystemSettings() {
        return systemSettingRepository.findAll();
    }

    /**
     * 更新系统参数（管理员）
     * 如果参数不存在则创建新参数
     */
    public SystemSetting updateSystemSetting(String settingKey, String settingValue, String description) {
        Optional<SystemSetting> existingSetting = systemSettingRepository.findBySettingKey(settingKey);

        SystemSetting setting;
        if (existingSetting.isPresent()) {
            setting = existingSetting.get();
            setting.setSettingValue(settingValue);
            if (description != null) {
                setting.setDescription(description);
            }
        } else {
            setting = new SystemSetting();
            setting.setSettingKey(settingKey);
            setting.setSettingValue(settingValue);
            setting.setDescription(description);
        }

        setting.setUpdatedAt(LocalDateTime.now());
        return systemSettingRepository.save(setting);
    }

    /**
     * 获取系统日志（支持分页、筛选）
     * 按日志级别筛选
     */
    public Page<SystemLog> getSystemLogs(SystemLog.LogLevel logLevel, Pageable pageable) {
        if (logLevel != null) {
            return systemLogRepository.findByLogLevel(logLevel, pageable);
        }
        return systemLogRepository.findAll(pageable);
    }

    /**
     * 记录系统日志
     */
    public SystemLog recordSystemLog(SystemLog.LogLevel logLevel, String message, String details) {
        SystemLog log = new SystemLog();
        log.setLogLevel(logLevel);
        log.setMessage(message);
        log.setDetails(details);
        log.setCreatedAt(LocalDateTime.now());
        return systemLogRepository.save(log);
    }

    /**
     * 获取管理员操作日志（支持分页、筛选）
     * 可按管理员ID和操作类型筛选
     */
    public Page<AdminAuditLog> getAdminAuditLogs(Long adminId, String operationType, Pageable pageable) {
        if (adminId != null && operationType != null) {
            return adminAuditLogRepository.findByAdminIdAndOperationType(adminId, operationType, pageable);
        } else if (adminId != null) {
            return adminAuditLogRepository.findByAdminId(adminId, pageable);
        } else if (operationType != null) {
            return adminAuditLogRepository.findByOperationType(operationType, pageable);
        }
        return adminAuditLogRepository.findAll(pageable);
    }

    /**
     * 记录管理员操作日志
     */
    public AdminAuditLog recordAdminAuditLog(Long adminId, String operationType, String targetType, Long targetId, String details) {
        AdminAuditLog log = new AdminAuditLog();
        log.setAdminId(adminId);
        log.setOperationType(operationType);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetails(details);
        log.setCreatedAt(LocalDateTime.now());
        return adminAuditLogRepository.save(log);
    }

    /**
     * 创建角色（超级管理员）
     */
    public Role createRole(String roleName, String description) {
        // 检查角色是否已存在
        Optional<Role> existingRole = roleRepository.findByRoleName(roleName);
        if (existingRole.isPresent()) {
            throw new IllegalArgumentException("角色已存在");
        }

        Role role = new Role();
        role.setRoleName(roleName);
        role.setDescription(description);
        role.setCreatedAt(LocalDateTime.now());
        return roleRepository.save(role);
    }

    /**
     * 获取所有角色
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * 根据ID获取角色
     */
    public Role getRoleById(Long id) {
        Optional<Role> role = roleRepository.findById(id);
        return role.orElse(null);
    }

    /**
     * 根据角色名获取角色
     */
    public Role getRoleByName(String roleName) {
        Optional<Role> role = roleRepository.findByRoleName(roleName);
        return role.orElse(null);
    }

    /**
     * 更新角色权限（超级管理员）
     * 先删除旧权限，再添加新权限
     */
    public void updateRolePermissions(Long roleId, List<String> permissionNames) {
        // 检查角色是否存在
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }

        // 删除该角色的所有旧权限
        List<Permission> oldPermissions = permissionRepository.findByRoleId(roleId);
        permissionRepository.deleteAll(oldPermissions);

        // 添加新权限
        if (permissionNames != null && !permissionNames.isEmpty()) {
            for (String permissionName : permissionNames) {
                Permission permission = new Permission();
                permission.setRoleId(roleId);
                permission.setPermissionName(permissionName);
                permission.setCreatedAt(LocalDateTime.now());
                permissionRepository.save(permission);
            }
        }
    }

    /**
     * 获取角色的所有权限
     */
    public List<Permission> getRolePermissions(Long roleId) {
        return permissionRepository.findByRoleId(roleId);
    }

    /**
     * 为角色添加权限
     */
    public Permission addPermissionToRole(Long roleId, String permissionName) {
        // 检查角色是否存在
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }

        // 检查权限是否已存在
        Optional<Permission> existingPermission = permissionRepository.findByRoleIdAndPermissionName(roleId, permissionName);
        if (existingPermission.isPresent()) {
            throw new IllegalArgumentException("权限已存在");
        }

        Permission permission = new Permission();
        permission.setRoleId(roleId);
        permission.setPermissionName(permissionName);
        permission.setCreatedAt(LocalDateTime.now());
        return permissionRepository.save(permission);
    }

    /**
     * 从角色删除权限
     */
    public void removePermissionFromRole(Long roleId, String permissionName) {
        Optional<Permission> permission = permissionRepository.findByRoleIdAndPermissionName(roleId, permissionName);
        if (permission.isPresent()) {
            permissionRepository.delete(permission.get());
        }
    }

    /**
     * 删除角色
     */
    public void deleteRole(Long roleId) {
        // 删除该角色的所有权限
        List<Permission> permissions = permissionRepository.findByRoleId(roleId);
        permissionRepository.deleteAll(permissions);

        // 删除角色
        roleRepository.deleteById(roleId);
    }
}
