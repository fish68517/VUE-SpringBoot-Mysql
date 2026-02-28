package com.health.service;

import com.health.entity.AuditLog;
import com.health.entity.Doctor;
import com.health.entity.HealthData;
import com.health.entity.User;
import com.health.repository.AuditLogRepository;
import com.health.repository.DoctorRepository;
import com.health.repository.HealthDataRepository;
import com.health.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 管理员服务类
 * 处理系统管理员的用户权限管理、医师管理、数据统计和审计日志查询
 */
@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private HealthDataRepository healthDataRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * 获取所有用户
     * 返回系统中所有用户的列表
     *
     * @return 所有用户列表
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 修改用户权限
     * 更新用户的角色权限
     *
     * @param userId 用户ID
     * @param newRole 新的角色（USER、DOCTOR、ADMIN）
     * @return 更新后的用户对象
     * @throws IllegalArgumentException 如果用户不存在或角色无效
     */
    public User updateUserRole(Long userId, String newRole) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 验证角色是否有效
        if (!isValidRole(newRole)) {
            throw new IllegalArgumentException("无效的角色类型");
        }

        User user = userOptional.get();
        user.setRole(newRole);
        return userRepository.save(user);
    }

    /**
     * 禁用用户账户
     * 将用户账户状态设置为INACTIVE，阻止用户登录
     *
     * @param userId 用户ID
     * @return 更新后的用户对象
     * @throws IllegalArgumentException 如果用户不存在
     */
    public User disableUserAccount(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        User user = userOptional.get();
        user.setStatus("INACTIVE");
        return userRepository.save(user);
    }

    /**
     * 启用用户账户
     * 将用户账户状态设置为ACTIVE，允许用户登录
     *
     * @param userId 用户ID
     * @return 更新后的用户对象
     * @throws IllegalArgumentException 如果用户不存在
     */
    public User enableUserAccount(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (!userOptional.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        User user = userOptional.get();
        user.setStatus("ACTIVE");
        return userRepository.save(user);
    }

    /**
     * 获取所有医师
     * 返回系统中所有医师的列表
     *
     * @return 所有医师列表
     */
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * 编辑医师信息
     * 更新医师的专科、医院等信息
     *
     * @param doctorId 医师ID
     * @param specialization 专科
     * @param hospital 医院
     * @return 更新后的医师对象
     * @throws IllegalArgumentException 如果医师不存在
     */
    public Doctor updateDoctorInfo(Long doctorId, String specialization, String hospital) {
        Optional<Doctor> doctorOptional = doctorRepository.findById(doctorId);

        if (!doctorOptional.isPresent()) {
            throw new IllegalArgumentException("医师不存在");
        }

        Doctor doctor = doctorOptional.get();

        if (specialization != null && !specialization.isEmpty()) {
            doctor.setSpecialization(specialization);
        }

        if (hospital != null && !hospital.isEmpty()) {
            doctor.setHospital(hospital);
        }

        return doctorRepository.save(doctor);
    }

    /**
     * 删除医师账户
     * 从系统中删除医师及其关联的用户账户
     *
     * @param doctorId 医师ID
     * @throws IllegalArgumentException 如果医师不存在
     */
    public void deleteDoctorAccount(Long doctorId) {
        Optional<Doctor> doctorOptional = doctorRepository.findById(doctorId);

        if (!doctorOptional.isPresent()) {
            throw new IllegalArgumentException("医师不存在");
        }

        Doctor doctor = doctorOptional.get();
        Long userId = doctor.getUserId();

        // 删除医师记录
        doctorRepository.deleteById(doctorId);

        // 删除关联的用户账户
        if (userId != null) {
            userRepository.deleteById(userId);
        }
    }

    /**
     * 获取健康数据统计
     * 返回系统中健康数据的统计信息
     *
     * @return 包含统计信息的Map对象
     */
    public Map<String, Object> getHealthDataStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 获取所有健康数据
        List<HealthData> allHealthData = healthDataRepository.findAll();

        // 统计总数
        statistics.put("总健康数据条数", allHealthData.size());

        // 按数据类型统计
        long routineCount = allHealthData.stream()
                .filter(h -> "ROUTINE".equals(h.getDataType()))
                .count();
        long genderSpecificCount = allHealthData.stream()
                .filter(h -> "GENDER_SPECIFIC".equals(h.getDataType()))
                .count();

        statistics.put("常规检查数据条数", routineCount);
        statistics.put("性别健康数据条数", genderSpecificCount);

        // 统计用户数
        List<User> allUsers = userRepository.findAll();
        statistics.put("总用户数", allUsers.size());

        // 按角色统计用户
        long userCount = allUsers.stream()
                .filter(u -> "USER".equals(u.getRole()))
                .count();
        long doctorCount = allUsers.stream()
                .filter(u -> "DOCTOR".equals(u.getRole()))
                .count();
        long adminCount = allUsers.stream()
                .filter(u -> "ADMIN".equals(u.getRole()))
                .count();

        statistics.put("普通用户数", userCount);
        statistics.put("医师数", doctorCount);
        statistics.put("管理员数", adminCount);

        // 统计活跃用户
        long activeUserCount = allUsers.stream()
                .filter(u -> "ACTIVE".equals(u.getStatus()))
                .count();
        long inactiveUserCount = allUsers.stream()
                .filter(u -> "INACTIVE".equals(u.getStatus()))
                .count();

        statistics.put("活跃用户数", activeUserCount);
        statistics.put("禁用用户数", inactiveUserCount);

        return statistics;
    }

    /**
     * 获取审计日志
     * 返回系统中所有审计日志
     *
     * @return 审计日志列表
     */
    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findAll();
    }

    /**
     * 按时间范围获取审计日志
     * 返回指定时间范围内的审计日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    public List<AuditLog> getAuditLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return auditLogRepository.findByTimestampBetween(startTime, endTime);
    }

    /**
     * 按用户ID获取审计日志
     * 返回指定用户的所有审计日志
     *
     * @param userId 用户ID
     * @return 审计日志列表
     */
    public List<AuditLog> getAuditLogsByUserId(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }

    /**
     * 按操作类型获取审计日志
     * 返回指定操作类型的所有审计日志
     *
     * @param action 操作类型
     * @return 审计日志列表
     */
    public List<AuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findByAction(action);
    }

    /**
     * 验证角色是否有效
     * 检查角色是否为USER、DOCTOR或ADMIN
     *
     * @param role 角色
     * @return 角色是否有效
     */
    private boolean isValidRole(String role) {
        return role != null && (role.equals("USER") || role.equals("DOCTOR") || role.equals("ADMIN"));
    }
}
