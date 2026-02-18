/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : naxi_pattern_db

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 18/02/2026 09:47:
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for 
-- ----------------------------
DROP TABLE IF EXISTS `admin_audit_logs`;
CREATE TABLE `admin_audit_logs`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_id` bigint NOT NULL COMMENT '管理员ID',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '目标类型',
  `target_id` bigint NULL DEFAULT NULL COMMENT '目标ID',
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '操作详情',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_admin_id`(`admin_id` ASC) USING BTREE,
  INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `admin_audit_logs_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_audit_logs
-- ----------------------------
INSERT INTO `admin_audit_logs` VALUES (1, 1, 'DELETE', 'Comment', 6, '删除评论，用户ID: 6，纹样ID: 2', '2026-02-17 08:41:11');
INSERT INTO `admin_audit_logs` VALUES (2, 1, 'DELETE', 'Comment', 8, '删除评论，用户ID: 6，纹样ID: 1', '2026-02-17 09:29:46');
INSERT INTO `admin_audit_logs` VALUES (3, 1, 'DELETE', 'Question', 3, '删除提问: 这是那里生成的，用户ID: 6', '2026-02-17 10:30:09');
INSERT INTO `admin_audit_logs` VALUES (4, 1, 'UPDATE', 'Pattern', 1, '更新纹样: 披星戴月纹000', '2026-02-17 11:05:05');
INSERT INTO `admin_audit_logs` VALUES (5, 1, 'CREATE', 'Pattern', 6, '创建纹样: 00', '2026-02-17 11:33:05');
INSERT INTO `admin_audit_logs` VALUES (6, 1, 'CREATE', 'Pattern', 7, '创建纹样: 纳西族传统纹样', '2026-02-17 14:40:34');
INSERT INTO `admin_audit_logs` VALUES (7, 1, 'UPDATE', 'Pattern', 1, '更新纹样: 披星戴月纹000222', '2026-02-17 14:42:03');
INSERT INTO `admin_audit_logs` VALUES (8, 1, 'DELETE', 'Pattern', 6, '删除纹样: 00', '2026-02-17 14:42:47');
INSERT INTO `admin_audit_logs` VALUES (9, 1, 'DELETE', 'Pattern', 1, '删除纹样: 披星戴月纹000222', '2026-02-17 15:00:28');
INSERT INTO `admin_audit_logs` VALUES (10, 1, 'UPDATE', 'Pattern', 2, '更新纹样: 东巴神路图', '2026-02-17 15:00:41');
INSERT INTO `admin_audit_logs` VALUES (11, 1, 'UPDATE', 'User', 4, '更新用户状态为: disabled', '2026-02-17 15:02:51');
INSERT INTO `admin_audit_logs` VALUES (12, 1, 'UPDATE', 'User', 4, '更新用户状态为: ACTIVE', '2026-02-17 15:03:01');
INSERT INTO `admin_audit_logs` VALUES (13, 1, 'REVIEW', 'CreativeWork', 3, '审核作品: 民族抱枕，状态: approved', '2026-02-18 00:34:55');
INSERT INTO `admin_audit_logs` VALUES (14, 1, 'REVIEW', 'CreativeWork', 6, '审核作品: 000，状态: approved', '2026-02-18 00:35:25');
INSERT INTO `admin_audit_logs` VALUES (15, 1, 'REVIEW', 'CreativeWork', 7, '审核作品: 558，状态: rejected', '2026-02-18 00:35:32');
INSERT INTO `admin_audit_logs` VALUES (16, 1, 'UPDATE', 'SystemSetting', NULL, '更新系统参数，共更新 8 个参数', '2026-02-18 00:54:23');
INSERT INTO `admin_audit_logs` VALUES (17, 1, 'CREATE', 'Role', 4, '创建角色: 纳西族张三', '2026-02-18 00:55:26');
INSERT INTO `admin_audit_logs` VALUES (18, 1, 'REVIEW', 'CreativeWork', 8, '审核作品: 这是纳西族传统纹样服饰，状态: approved', '2026-02-18 01:04:36');
INSERT INTO `admin_audit_logs` VALUES (19, 1, 'CREATE', 'Pattern', 8, '创建纹样: 传统服饰--纳西族', '2026-02-18 01:38:23');
INSERT INTO `admin_audit_logs` VALUES (20, 1, 'UPDATE', 'Pattern', 8, '更新纹样: 传统服饰--纳西族', '2026-02-18 01:38:34');
INSERT INTO `admin_audit_logs` VALUES (21, 1, 'DELETE', 'Pattern', 8, '删除纹样: 传统服饰--纳西族', '2026-02-18 01:38:39');
INSERT INTO `admin_audit_logs` VALUES (22, 1, 'UPDATE', 'User', 4, '更新用户状态为: disabled', '2026-02-18 01:39:04');
INSERT INTO `admin_audit_logs` VALUES (23, 1, 'UPDATE', 'User', 4, '更新用户状态为: ACTIVE', '2026-02-18 01:39:19');

-- ----------------------------
-- Table structure for collections
-- ----------------------------
DROP TABLE IF EXISTS `collections`;
CREATE TABLE `collections`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `pattern_id` bigint NOT NULL COMMENT '纹样ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_collection`(`user_id` ASC, `pattern_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_pattern_id`(`pattern_id` ASC) USING BTREE,
  CONSTRAINT `collections_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_collections_pattern` FOREIGN KEY (`pattern_id`) REFERENCES `patterns` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collections
-- ----------------------------
INSERT INTO `collections` VALUES (2, 4, 4, '2026-02-17 14:38:05');
INSERT INTO `collections` VALUES (3, 5, 5, '2026-02-17 14:38:05');
INSERT INTO `collections` VALUES (4, 6, 5, '2026-02-17 14:38:05');

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `pattern_id` bigint NOT NULL COMMENT '纹样ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_pattern_id`(`pattern_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_comments_pattern` FOREIGN KEY (`pattern_id`) REFERENCES `patterns` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comments
-- ----------------------------
INSERT INTO `comments` VALUES (2, 4, 4, '动感十足', '2026-02-17 14:38:05', '2026-02-17 14:38:05');
INSERT INTO `comments` VALUES (3, 5, 5, '文化底蕴深厚', '2026-02-17 14:38:05', '2026-02-17 14:38:05');
INSERT INTO `comments` VALUES (4, 1, 2, '已收录', '2026-02-17 14:38:05', '2026-02-17 14:38:05');
INSERT INTO `comments` VALUES (5, 2, 3, '精美', '2026-02-17 14:38:05', '2026-02-17 14:38:05');
INSERT INTO `comments` VALUES (7, 6, 2, '还是女生', '2026-02-17 08:41:18', '2026-02-17 08:41:18');

-- ----------------------------
-- Table structure for creative_works
-- ----------------------------
DROP TABLE IF EXISTS `creative_works`;
CREATE TABLE `creative_works`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作品标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '作品描述',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作品图片URL',
  `status` enum('PENDING','APPROVED','REJECTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'PENDING' COMMENT '审核状态',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `creative_works_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '原创作品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of creative_works
-- ----------------------------
INSERT INTO `creative_works` VALUES (1, 3, '现代卫衣', '结合七星纹', '/images/w1.jpg', 'APPROVED', 88, '2026-02-17 14:38:05', '2026-02-17 15:25:43');
INSERT INTO `creative_works` VALUES (2, 4, '东巴滑板', '黑金风格', '/images/w2.jpg', 'APPROVED', 125, '2026-02-17 14:38:05', '2026-02-17 15:25:48');
INSERT INTO `creative_works` VALUES (3, 5, '民族抱枕', '软装周边', '/images/w3.jpg', 'APPROVED', 0, '2026-02-17 14:38:05', '2026-02-18 00:34:55');
INSERT INTO `creative_works` VALUES (4, 1, '官方画册', '纹样集锦', '/images/w4.jpg', 'PENDING', 999, '2026-02-17 14:38:05', '2026-02-18 09:39:53');
INSERT INTO `creative_works` VALUES (5, 2, '研究报告', '文化溯源', '/images/w5.jpg', 'APPROVED', 50, '2026-02-17 14:38:05', '2026-02-17 15:26:01');
INSERT INTO `creative_works` VALUES (6, 6, '000', '00000', '/images/1771316552214_knowledge-science.jpg', 'APPROVED', 0, '2026-02-17 08:25:14', '2026-02-18 00:35:25');
INSERT INTO `creative_works` VALUES (7, 6, '558', '282', '/images/1771316727364_embroidery-culture.jpg', 'REJECTED', 0, '2026-02-17 08:25:31', '2026-02-18 00:35:32');
INSERT INTO `creative_works` VALUES (8, 6, '这是纳西族传统纹样服饰', '这是纳西族传统纹样服饰这是纳西族传统纹样服饰这是纳西族传统纹样服饰这是纳西族传统纹样服饰这是纳西族传统纹样服饰', '/images/1771324250344_4119f700974059646bc1e8668dfb24e6.jpg', 'APPROVED', 0, '2026-02-17 10:31:10', '2026-02-18 01:04:36');

-- ----------------------------
-- Table structure for operation_logs
-- ----------------------------
DROP TABLE IF EXISTS `operation_logs`;
CREATE TABLE `operation_logs`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '目标类型',
  `target_id` bigint NULL DEFAULT NULL COMMENT '目标ID',
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '操作详情',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `operation_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_logs
-- ----------------------------
INSERT INTO `operation_logs` VALUES (1, 1, 'LOGIN', 'USER', 1, '管理员登录', '2026-02-17 14:38:05');
INSERT INTO `operation_logs` VALUES (2, 3, 'COLLECT', 'PATTERN', 1, '用户收藏纹样', '2026-02-17 14:38:05');
INSERT INTO `operation_logs` VALUES (3, 4, 'DOWNLOAD', 'PATTERN', 4, '用户下载文件', '2026-02-17 14:38:05');
INSERT INTO `operation_logs` VALUES (4, 2, 'AUDIT', 'WORK', 1, '审批原创作品', '2026-02-17 14:38:05');
INSERT INTO `operation_logs` VALUES (5, 5, 'PUBLISH', 'WORK', 2, '发布新作品', '2026-02-17 14:38:05');

-- ----------------------------
-- Table structure for patterns
-- ----------------------------
DROP TABLE IF EXISTS `patterns`;
CREATE TABLE `patterns`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '纹样名称',
  `category` enum('七星纹','东巴衍生纹','日月纹','云纹水纹') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '纹样分类',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '纹样描述',
  `cultural_background` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '文化背景',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `download_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '下载URL',
  `application_scenarios` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '应用场景',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `download_count` int NULL DEFAULT 0 COMMENT '下载次数',
  `collection_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '纹样表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of patterns
-- ----------------------------
INSERT INTO `patterns` VALUES (2, '东巴神路图', '东巴衍生纹', '叙事性图案', '东巴教文化', '/images/p2.jpg', NULL, '现代设计00', 104, 0, 12, '2026-02-17 14:38:05', '2026-02-17 15:00:41');
INSERT INTO `patterns` VALUES (3, '玉龙双月纹', '日月纹', '对称月亮图案', '天文崇拜', '/images/p3.jpg', NULL, '文创产品', 66, 0, 20, '2026-02-17 14:38:05', '2026-02-17 17:34:47');
INSERT INTO `patterns` VALUES (4, '金沙江流纹', '云纹水纹', '波浪几何化', '自然生命力', '/images/p4.jpg', NULL, '其他', 94, 0, 31, '2026-02-17 14:38:05', '2026-02-17 17:34:54');
INSERT INTO `patterns` VALUES (5, '东巴象形文字', '东巴衍生纹', '文字装饰化', '活着的文字', '/images/p5.jpg', NULL, '其他', 221, 0, 88, '2026-02-17 14:38:05', '2026-02-17 17:34:57');
INSERT INTO `patterns` VALUES (7, '纳西族传统纹样', '七星纹', '纳西族传统纹样纳西族传统纹样纳西族传统纹样', '纳西族传统纹样纳西族传统纹样纳西族传统纹样', '/images/1771339229545_p4.jpg', '', '传统服饰', 0, 0, 0, '2026-02-17 14:40:34', '2026-02-17 14:40:34');

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_permission`(`role_id` ASC, `permission_name` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `permissions_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permissions
-- ----------------------------

-- ----------------------------
-- Table structure for questions
-- ----------------------------
DROP TABLE IF EXISTS `questions`;
CREATE TABLE `questions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `pattern_id` bigint NOT NULL COMMENT '纹样ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提问标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提问内容',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_pattern_id`(`pattern_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `fk_questions_pattern` FOREIGN KEY (`pattern_id`) REFERENCES `patterns` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `questions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '提问表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of questions
-- ----------------------------
INSERT INTO `questions` VALUES (4, 6, 5, '000', '0000', '2026-02-17 10:47:53', '2026-02-17 10:47:53');

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_name`(`role_name` ASC) USING BTREE,
  INDEX `idx_role_name`(`role_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES (1, 'SUPER_ADMIN', '超级管理员', '2026-02-17 14:38:05');
INSERT INTO `roles` VALUES (2, 'ADMIN', '管理员', '2026-02-17 14:38:05');
INSERT INTO `roles` VALUES (3, 'USER', '普通用户', '2026-02-17 14:38:05');
INSERT INTO `roles` VALUES (4, '纳西族张三', '纳西族张三是用户', '2026-02-18 00:55:26');

-- ----------------------------
-- Table structure for system_logs
-- ----------------------------
DROP TABLE IF EXISTS `system_logs`;
CREATE TABLE `system_logs`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `log_level` enum('INFO','WARN','ERROR') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'INFO' COMMENT '日志级别',
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日志消息',
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '日志详情',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_log_level`(`log_level` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_logs
-- ----------------------------

-- ----------------------------
-- Table structure for system_settings
-- ----------------------------
DROP TABLE IF EXISTS `system_settings`;
CREATE TABLE `system_settings`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `setting_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数键',
  `setting_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数值',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '参数描述',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `setting_key`(`setting_key` ASC) USING BTREE,
  INDEX `idx_setting_key`(`setting_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统参数表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_settings
-- ----------------------------
INSERT INTO `system_settings` VALUES (1, 'platform_name', '纳西族纹样平台', '平台名称', '2026-02-18 00:54:22');
INSERT INTO `system_settings` VALUES (2, 'max_upload', '100MB', '限制', '2026-02-17 22:23:26');
INSERT INTO `system_settings` VALUES (3, 'page_size', '12', '分页', '2026-02-17 14:38:05');
INSERT INTO `system_settings` VALUES (4, 'theme', 'blue', '主题', '2026-02-17 14:38:05');
INSERT INTO `system_settings` VALUES (5, 'api_version', 'v1.0', '版本', '2026-02-17 14:38:05');
INSERT INTO `system_settings` VALUES (6, 'carousel_interval', '5000', '轮播图显示时间（毫秒）', '2026-02-18 00:54:21');
INSERT INTO `system_settings` VALUES (7, 'carousel_effect', 'fade', '轮播图切换效果', '2026-02-18 00:54:22');
INSERT INTO `system_settings` VALUES (8, 'carousel_auto_play', 'true', '轮播图自动播放', '2026-02-18 00:54:22');
INSERT INTO `system_settings` VALUES (9, 'carousel_show_indicators', 'true', '轮播图显示指示器', '2026-02-18 00:54:22');
INSERT INTO `system_settings` VALUES (10, 'platform_description', '000', '平台描述', '2026-02-18 00:54:23');
INSERT INTO `system_settings` VALUES (11, 'items_per_page', '10', '每页显示条数', '2026-02-18 00:54:23');
INSERT INTO `system_settings` VALUES (12, 'max_upload_size', '50', '最大上传文件大小（MB）', '2026-02-18 00:54:23');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '个人简介',
  `status` enum('ACTIVE','DISABLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ACTIVE' COMMENT '用户状态',
  `role_id` bigint NULL DEFAULT NULL COMMENT '角色ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  INDEX `role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin_naxi', 'password123', 'admin@naxi.com', '纳西管理员', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Admin', '系统维护者', 'ACTIVE', 1, '2026-02-17 14:38:05', '2026-02-17 14:38:05');
INSERT INTO `users` VALUES (2, 'li_jing', 'password123', 'lijing@naxi.com', '李静', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Li', '非遗研究员', 'ACTIVE', 2, '2026-02-17 14:38:05', '2026-02-17 14:38:05');
INSERT INTO `users` VALUES (3, 'naxi_xiaowei', 'password123', 'xiaowei@naxi.com', '纳西小薇', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Wei', '纹样爱好者', 'ACTIVE', 3, '2026-02-17 14:38:05', '2026-02-17 14:38:05');
INSERT INTO `users` VALUES (4, 'ah_gang', 'password123', 'ahgang@naxi.com', '阿刚', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Gang', '摄影师', 'ACTIVE', 3, '2026-02-17 14:38:05', '2026-02-18 01:39:19');
INSERT INTO `users` VALUES (5, 'mumu_design', 'password123', 'mumu@naxi.com', '木木', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Mumu', '插画师', 'ACTIVE', 3, '2026-02-17 14:38:05', '2026-02-17 14:38:05');
INSERT INTO `users` VALUES (6, '纳西族', '$2a$10$.joCSqa7pRICXb4.Ih4TweVxJiV14lqFvOTpH/4k/J.cQMWmqCNL6', '123456789@qq.com', '半死不040', NULL, '巴萨巴萨v 按时', 'ACTIVE', 3, '2026-02-17 06:39:12', '2026-02-17 10:31:34');
INSERT INTO `users` VALUES (7, 'adminTest', '$2a$10$aTR5xNVNgBrPzY9sAFOkoOnhfrJDW4RUm5N5ovDZE4c5FDuXx5FAS', '1234567890@qq.com', NULL, NULL, NULL, 'ACTIVE', 1, '2026-02-17 10:32:42', '2026-02-17 10:32:42');

SET FOREIGN_KEY_CHECKS = 1;
