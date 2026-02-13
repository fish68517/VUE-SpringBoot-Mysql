package com.naxi.config;

import com.naxi.entity.*;
import com.naxi.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 数据库初始化器
 * 在应用启动时自动初始化必要的数据
 */
@Slf4j
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PatternRepository patternRepository;
    private final SystemSettingRepository systemSettingRepository;

    public DatabaseInitializer(RoleRepository roleRepository,
                               PermissionRepository permissionRepository,
                               PatternRepository patternRepository,
                               SystemSettingRepository systemSettingRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.patternRepository = patternRepository;
        this.systemSettingRepository = systemSettingRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化数据库数据...");
        
        try {
            initializeRoles();
            initializePermissions();
            initializePatterns();
            initializeSystemSettings();
            
            log.info("数据库初始化完成！");
        } catch (Exception e) {
            log.error("数据库初始化失败", e);
        }
    }

    /**
     * 初始化角色数据
     */
    private void initializeRoles() {
        log.info("初始化角色数据...");
        
        // 检查是否已存在角色
        if (roleRepository.count() > 0) {
            log.info("角色数据已存在，跳过初始化");
            return;
        }

        List<Role> roles = Arrays.asList(
            new Role(null, "SUPER_ADMIN", "超级管理员", null),
            new Role(null, "ADMIN", "管理员", null),
            new Role(null, "USER", "普通用户", null)
        );

        roleRepository.saveAll(roles);
        log.info("角色数据初始化完成，共 {} 个角色", roles.size());
    }

    /**
     * 初始化权限数据
     */
    private void initializePermissions() {
        log.info("初始化权限数据...");
        
        // 检查是否已存在权限
        if (permissionRepository.count() > 0) {
            log.info("权限数据已存在，跳过初始化");
            return;
        }

        // 获取各个角色
        Role superAdminRole = roleRepository.findByRoleName("SUPER_ADMIN").orElse(null);
        Role adminRole = roleRepository.findByRoleName("ADMIN").orElse(null);
        Role userRole = roleRepository.findByRoleName("USER").orElse(null);
        
        if (superAdminRole == null || adminRole == null || userRole == null) {
            log.warn("角色数据不完整，跳过权限初始化");
            return;
        }

        List<Permission> permissions = Arrays.asList(
            // 超级管理员权限
            new Permission(null, superAdminRole.getId(), "pattern_manage", null),
            new Permission(null, superAdminRole.getId(), "user_manage", null),
            new Permission(null, superAdminRole.getId(), "content_review", null),
            new Permission(null, superAdminRole.getId(), "system_manage", null),
            new Permission(null, superAdminRole.getId(), "role_manage", null),
            new Permission(null, superAdminRole.getId(), "audit_log_view", null),

            // 管理员权限
            new Permission(null, adminRole.getId(), "pattern_manage", null),
            new Permission(null, adminRole.getId(), "user_manage", null),
            new Permission(null, adminRole.getId(), "content_review", null),
            new Permission(null, adminRole.getId(), "system_manage", null),

            // 普通用户权限
            new Permission(null, userRole.getId(), "pattern_view", null),
            new Permission(null, userRole.getId(), "pattern_download", null),
            new Permission(null, userRole.getId(), "collection_manage", null),
            new Permission(null, userRole.getId(), "comment_publish", null),
            new Permission(null, userRole.getId(), "question_publish", null),
            new Permission(null, userRole.getId(), "work_upload", null)
        );

        permissionRepository.saveAll(permissions);
        log.info("权限数据初始化完成，共 {} 个权限", permissions.size());
    }

    /**
     * 初始化纹样数据
     */
    private void initializePatterns() {
        log.info("初始化纹样数据...");
        
        // 检查是否已存在纹样
        if (patternRepository.count() > 0) {
            log.info("纹样数据已存在，跳过初始化");
            return;
        }

        List<Pattern> patterns = Arrays.asList(
            // 七星纹
            new Pattern(null, "七星纹样1", Pattern.PatternCategory.七星纹, 
                "七星纹样示例1", "七星纹代表北斗七星，在纳西文化中象征指引和方向",
                "/images/patterns/qixing1.jpg", null, "传统服饰、建筑装饰",
                0, 0, 0, null, null),
            new Pattern(null, "七星纹样2", Pattern.PatternCategory.七星纹,
                "七星纹样示例2", "七星纹的变体形式",
                "/images/patterns/qixing2.jpg", null, "现代设计、文创产品",
                0, 0, 0, null, null),

            // 东巴衍生纹
            new Pattern(null, "东巴衍生纹样1", Pattern.PatternCategory.东巴衍生纹,
                "东巴衍生纹样示例1", "东巴文化衍生的纹样，融合了象形文字特征",
                "/images/patterns/dongba1.jpg", null, "传统服饰、现代设计",
                0, 0, 0, null, null),
            new Pattern(null, "东巴衍生纹样2", Pattern.PatternCategory.东巴衍生纹,
                "东巴衍生纹样示例2", "东巴衍生纹样的另一种形式",
                "/images/patterns/dongba2.jpg", null, "文创产品、装饰品",
                0, 0, 0, null, null),

            // 日月纹
            new Pattern(null, "日月纹样1", Pattern.PatternCategory.日月纹,
                "日月纹样示例1", "日月纹象征阴阳平衡和时间循环",
                "/images/patterns/riyue1.jpg", null, "传统服饰、建筑",
                0, 0, 0, null, null),
            new Pattern(null, "日月纹样2", Pattern.PatternCategory.日月纹,
                "日月纹样示例2", "日月纹的创意变体",
                "/images/patterns/riyue2.jpg", null, "现代艺术、设计",
                0, 0, 0, null, null),

            // 云纹水纹
            new Pattern(null, "云纹水纹样1", Pattern.PatternCategory.云纹水纹,
                "云纹水纹样示例1", "云纹水纹代表自然元素，象征流动和变化",
                "/images/patterns/yunshui1.jpg", null, "传统服饰、装饰",
                0, 0, 0, null, null),
            new Pattern(null, "云纹水纹样2", Pattern.PatternCategory.云纹水纹,
                "云纹水纹样示例2", "云纹水纹的组合形式",
                "/images/patterns/yunshui2.jpg", null, "现代设计、文创",
                0, 0, 0, null, null)
        );

        patternRepository.saveAll(patterns);
        log.info("纹样数据初始化完成，共 {} 个纹样", patterns.size());
    }

    /**
     * 初始化系统参数
     */
    private void initializeSystemSettings() {
        log.info("初始化系统参数...");
        
        // 检查是否已存在系统参数
        if (systemSettingRepository.count() > 0) {
            log.info("系统参数已存在，跳过初始化");
            return;
        }

        List<SystemSetting> settings = Arrays.asList(
            new SystemSetting(null, "carousel_interval", "5000", "轮播图切换间隔（毫秒）", null),
            new SystemSetting(null, "carousel_effect", "fade", "轮播图切换效果", null),
            new SystemSetting(null, "max_upload_size", "10485760", "最大上传文件大小（字节）", null),
            new SystemSetting(null, "items_per_page", "12", "每页显示项目数", null),
            new SystemSetting(null, "platform_name", "纳西族纹样数字化展示平台", "平台名称", null),
            new SystemSetting(null, "platform_description", "展示和传播纳西族文化的轻量级Web应用系统", "平台描述", null)
        );

        systemSettingRepository.saveAll(settings);
        log.info("系统参数初始化完成，共 {} 个参数", settings.size());
    }
}
