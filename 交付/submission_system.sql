/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : submission_system

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 25/03/2026 10:01:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `requirements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `file_format` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `max_file_size` bigint NULL DEFAULT NULL,
  `word_count_min` int NULL DEFAULT NULL,
  `word_count_max` int NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, '学术论文', '探讨具体学术领域前沿问题，具有较高创新性和科学价值的原创性研究成果。', '1. 格式要求规范，必须包含：中英文摘要、关键词、引言、研究方法、实验结果、讨论与结论、参考文献。\n2. 查重率严格要求在 15% 以下。\n3. 公式和图表需清晰且有独立编号。', '.pdf,.doc,.docx', 10485760, 5000, 15000, '2026-03-07 08:25:03', '2026-03-07 08:25:03');
INSERT INTO `categories` VALUES (2, '科普文章', '面向大众的科学普及性文章，旨在通俗易懂地解释复杂的科学原理、现象或最新科技进展。', '1. 语言要求生动有趣、通俗易懂，尽量避免使用过于生僻的专业晦涩术语。\n2. 鼓励图文并茂，需配有高质量、无版权争议的说明性图表或照片。\n3. 科学事实必须严谨准确，不能出现常识性错误。', '.pdf,.doc,.docx,.jpg,.png', 20971520, 1500, 5000, '2026-03-07 08:25:03', '2026-03-07 08:25:03');
INSERT INTO `categories` VALUES (3, '技术报告', '针对特定工程项目、产品研发或技术难题的详细实施报告、经验总结及技术白皮书。', '1. 内容需包含：项目背景、面临的技术难点、核心解决方案（可包含少量核心伪代码）、测试数据分析及实际应用效果评估。\n2. 数据必须真实可靠，严禁造假。\n3. 涉及保密内容请提前脱敏处理。', '.pdf,.doc,.docx', 52428800, 3000, 20000, '2026-03-07 08:25:03', '2026-03-07 08:25:03');
INSERT INTO `categories` VALUES (4, '综述文章', '对某一学科或研究领域在特定时期内的研究成果进行系统性归纳、总结、客观评价和前瞻性预测的文章。', '1. 作者需具备广阔的学术视野，客观评述各方观点。\n2. 需引用大量具有代表性和时效性的权威文献（近五年文献占比不低于50%）。\n3. 不能是简单的文献堆砌，必须有作者独立的分析和对未来发展趋势的深刻展望。', '.pdf,.doc,.docx', 15728640, 6000, 25000, '2026-03-07 08:25:03', '2026-03-07 08:25:03');

-- ----------------------------
-- Table structure for initial_reviews
-- ----------------------------
DROP TABLE IF EXISTS `initial_reviews`;
CREATE TABLE `initial_reviews`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `manuscript_id` bigint NOT NULL,
  `editor_id` bigint NOT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'PASS, REJECT, REVISION_REQUIRED',
  `opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_manuscript_id`(`manuscript_id` ASC) USING BTREE,
  INDEX `idx_editor_id`(`editor_id` ASC) USING BTREE,
  CONSTRAINT `initial_reviews_ibfk_1` FOREIGN KEY (`manuscript_id`) REFERENCES `manuscripts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `initial_reviews_ibfk_2` FOREIGN KEY (`editor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of initial_reviews
-- ----------------------------
INSERT INTO `initial_reviews` VALUES (1, 7, 2, 'REJECT', '怎么瞎写啊', '2026-03-07 13:05:11', '2026-03-07 17:43:00');
INSERT INTO `initial_reviews` VALUES (2, 3, 2, 'REVISION_REQUIRED', '格式不对', '2026-03-07 13:09:46', '2026-03-07 17:36:59');
INSERT INTO `initial_reviews` VALUES (3, 4, 2, 'REVISION_REQUIRED', '请v在ixug修改', '2026-03-07 16:47:27', '2026-03-07 16:47:27');

-- ----------------------------
-- Table structure for manuscripts
-- ----------------------------
DROP TABLE IF EXISTS `manuscripts`;
CREATE TABLE `manuscripts`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `author_id` bigint NOT NULL,
  `category_id` bigint NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `abstract` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT, SUBMITTED, UNDER_REVIEW, REVISION_REQUIRED, ACCEPTED, REJECTED',
  `submission_date` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_author_id`(`author_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_submission_date`(`submission_date` ASC) USING BTREE,
  CONSTRAINT `manuscripts_ibfk_1` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `manuscripts_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of manuscripts
-- ----------------------------
INSERT INTO `manuscripts` VALUES (3, 1, 2, 'vue科普', NULL, 'vue科普vue科普vue科普', 'fb11e472-069f-40b1-a4f2-f611f49edfcb.pdf', 'REVISION_REQUIRED', '2026-03-07 08:32:53', '2026-03-07 08:32:53', '2026-03-07 17:36:59');
INSERT INTO `manuscripts` VALUES (4, 1, 2, '投稿科普', NULL, '投稿科普投稿科普投稿科普', '857fc09b-b170-4bc6-97e6-6479e8a99916.pdf', 'REVISION_REQUIRED', '2026-03-07 08:39:23', '2026-03-07 08:39:23', '2026-03-07 16:47:28');
INSERT INTO `manuscripts` VALUES (5, 1, 3, 'vue报告', NULL, 'vue报告vue报告vue报告vue报告', '638cf433-9375-4c26-8192-fe390d35bbbb.pdf', 'UNDER_REVIEW', '2026-03-07 09:12:10', '2026-03-07 09:12:09', '2026-03-07 13:09:46');
INSERT INTO `manuscripts` VALUES (7, 1, 1, 'vue原理分析', NULL, 'vue原理分析vue原理分析vue原理分析vue原理分析vue原理分析', '47a59a66-486c-41e4-9558-cefbee6ec1a3.docx', 'REJECTED', '2026-03-07 09:54:51', '2026-03-07 09:54:51', '2026-03-07 17:43:01');

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_id` bigint NOT NULL,
  `recipient_id` bigint NOT NULL,
  `manuscript_id` bigint NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'NOTIFICATION, COMMUNICATION',
  `is_read` tinyint(1) NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_recipient_id`(`recipient_id` ASC) USING BTREE,
  INDEX `idx_sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `messages_ibfk_3`(`manuscript_id` ASC) USING BTREE,
  CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`recipient_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `messages_ibfk_3` FOREIGN KEY (`manuscript_id`) REFERENCES `manuscripts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of messages
-- ----------------------------
INSERT INTO `messages` VALUES (6, 1, 1, 7, '尊敬的作者：\n\n您的稿件《vue原理分析》（编号：7）状态已更新为 SUBMITTED。\n请登录系统查看详细信息。\n\n此致\n敬礼\n\n编辑部', 'NOTIFICATION', 1, '2026-03-07 09:54:51');
INSERT INTO `messages` VALUES (9, 2, 3, 4, '尊敬的 shengao：\n\n编辑 bianji 邀请您审稿。\n稿件标题：《投稿科普》\n稿件编号：4\n\n请登录系统查看稿件详情并提交审稿意见。\n\n此致\n敬礼\n\n编辑部', 'NOTIFICATION', 0, '2026-03-07 13:45:10');
INSERT INTO `messages` VALUES (10, 2, 4, 4, '尊敬的 shengao1：\n\n编辑 bianji 邀请您审稿。\n稿件标题：《投稿科普》\n稿件编号：4\n\n请登录系统查看稿件详情并提交审稿意见。\n\n此致\n敬礼\n\n编辑部', 'NOTIFICATION', 0, '2026-03-07 13:45:59');
INSERT INTO `messages` VALUES (11, 3, 2, 4, '审稿人 shengao 已提交对稿件《投稿科普》（编号：4）的审稿意见。\n\n请登录系统查看审稿意见。', 'NOTIFICATION', 0, '2026-03-07 14:38:45');
INSERT INTO `messages` VALUES (12, 2, 3, NULL, '尽快审稿谢谢', 'COMMUNICATION', 0, '2026-03-07 15:56:22');
INSERT INTO `messages` VALUES (13, 1, 1, 4, '尊敬的 zhangsan：\n\n您的稿件《投稿科普》（编号：4）需要修改后重新提交。\n\n编辑意见：\n请v在ixug修改\n\n请登录系统查看详细信息并提交修改稿件。\n\n此致\n敬礼\n\n编辑部', 'NOTIFICATION', 0, '2026-03-07 16:47:28');
INSERT INTO `messages` VALUES (14, 1, 1, 3, '尊敬的 zhangsan：\n\n您的稿件《vue科普》（编号：3）需要修改后重新提交。\n\n编辑意见：\n格式不对\n\n请登录系统查看详细信息并提交修改稿件。\n\n此致\n敬礼\n\n编辑部', 'NOTIFICATION', 0, '2026-03-07 17:36:59');
INSERT INTO `messages` VALUES (15, 1, 1, 7, '尊敬的 zhangsan：\n\n很遗憾，您的稿件《vue原理分析》（编号：7）未通过初审。\n\n编辑意见：\n怎么瞎写啊\n\n此致\n敬礼\n\n编辑部', 'NOTIFICATION', 0, '2026-03-07 17:43:01');

-- ----------------------------
-- Table structure for notification_templates
-- ----------------------------
DROP TABLE IF EXISTS `notification_templates`;
CREATE TABLE `notification_templates`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `template_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `template_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'SUBMISSION, REVIEW, ACCEPTANCE, REJECTION',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_template_name`(`template_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification_templates
-- ----------------------------
INSERT INTO `notification_templates` VALUES (1, 'USER_APPROVED', '尊敬的 {username}：\n\n恭喜！您的 {role} 账号注册申请已被批准。\n您现在可以登录系统并开始使用相关功能。\n\n此致\n敬礼\n\n编辑部', 'SUBMISSION', '2026-03-06 20:45:07', '2026-03-06 20:45:07');
INSERT INTO `notification_templates` VALUES (2, 'USER_REJECTED', '尊敬的 {username}：\n\n很遗憾，您的 {role} 账号注册申请已被驳回。\n如有疑问，请联系系统管理员。\n\n此致\n敬礼\n\n编辑部', 'SUBMISSION', '2026-03-06 20:45:07', '2026-03-06 20:45:07');
INSERT INTO `notification_templates` VALUES (3, 'MANUSCRIPT_REJECTED', '尊敬的作者：\n\n您的稿件《{manuscriptTitle}》（编号：{manuscriptId}）已被驳回。\n请登录系统查看详细信息。\n\n此致\n敬礼\n\n编辑部', 'REJECTION', '2026-03-06 20:45:07', '2026-03-06 20:45:07');
INSERT INTO `notification_templates` VALUES (4, 'MANUSCRIPT_REVISION_REQUIRED', '尊敬的作者：\n\n您的稿件《{manuscriptTitle}》（编号：{manuscriptId}）需要修改。\n请登录系统查看编辑意见并提交修改稿件。\n\n此致\n敬礼\n\n编辑部', 'REVIEW', '2026-03-06 20:45:07', '2026-03-06 20:45:07');
INSERT INTO `notification_templates` VALUES (5, 'MANUSCRIPT_ACCEPTED', '尊敬的作者：\n\n恭喜！您的稿件《{manuscriptTitle}》（编号：{manuscriptId}）已被接受。\n我们将尽快安排您的稿件出版。\n\n此致\n敬礼\n\n编辑部', 'ACCEPTANCE', '2026-03-06 20:45:08', '2026-03-06 20:45:08');
INSERT INTO `notification_templates` VALUES (6, 'MANUSCRIPT_UNDER_REVIEW', '尊敬的作者：\n\n您的稿件《{manuscriptTitle}》（编号：{manuscriptId}）已通过初审，现已进入专家审稿阶段。\n我们将尽快完成审稿并通知您结果。\n\n此致\n敬礼\n\n编辑部', 'REVIEW', '2026-03-06 20:45:08', '2026-03-06 20:45:08');
INSERT INTO `notification_templates` VALUES (7, 'INITIAL_REVIEW_PASS', '尊敬的 {authorName}：\n\n您的稿件《{manuscriptTitle}》（编号：{manuscriptId}）已通过初审，将进入专家审稿阶段。\n\n此致\n敬礼\n\n编辑部', 'REVIEW', '2026-03-06 20:45:08', '2026-03-06 20:45:08');
INSERT INTO `notification_templates` VALUES (8, 'INITIAL_REVIEW_REJECT', '尊敬的 {authorName}：\n\n很遗憾，您的稿件《{manuscriptTitle}》（编号：{manuscriptId}）未通过初审。\n\n编辑意见：\n{opinion}\n\n此致\n敬礼\n\n编辑部', 'REJECTION', '2026-03-06 20:45:08', '2026-03-06 20:45:08');
INSERT INTO `notification_templates` VALUES (9, 'INITIAL_REVIEW_REVISION', '尊敬的 {authorName}：\n\n您的稿件《{manuscriptTitle}》（编号：{manuscriptId}）需要修改后重新提交。\n\n编辑意见：\n{opinion}\n\n请登录系统查看详细信息并提交修改稿件。\n\n此致\n敬礼\n\n编辑部', 'REVIEW', '2026-03-06 20:45:08', '2026-03-06 20:45:08');
INSERT INTO `notification_templates` VALUES (10, 'REVIEWER_ASSIGNED', '尊敬的 {reviewerName}：\n\n编辑 {editorName} 邀请您审稿。\n稿件标题：《{manuscriptTitle}》\n稿件编号：{manuscriptId}\n\n请登录系统查看稿件详情并提交审稿意见。\n\n此致\n敬礼\n\n编辑部', 'REVIEW', '2026-03-06 20:45:08', '2026-03-06 20:45:08');

-- ----------------------------
-- Table structure for reviews
-- ----------------------------
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `manuscript_id` bigint NOT NULL,
  `reviewer_id` bigint NOT NULL,
  `editor_id` bigint NOT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, ACCEPTED, REJECTED, SUBMITTED',
  `opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `score` int NULL DEFAULT NULL,
  `recommendation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'ACCEPT, ACCEPT_WITH_REVISION, REJECT',
  `submitted_date` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_manuscript_id`(`manuscript_id` ASC) USING BTREE,
  INDEX `idx_reviewer_id`(`reviewer_id` ASC) USING BTREE,
  INDEX `idx_editor_id`(`editor_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`manuscript_id`) REFERENCES `manuscripts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`reviewer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `reviews_ibfk_3` FOREIGN KEY (`editor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reviews
-- ----------------------------
INSERT INTO `reviews` VALUES (1, 4, 3, 2, 'SUBMITTED', '同意', 4, 'ACCEPT', '2026-03-07 14:38:46', '2026-03-07 13:45:10', '2026-03-07 14:38:45');
INSERT INTO `reviews` VALUES (2, 3, 3, 2, 'PENDING', NULL, NULL, NULL, NULL, '2026-03-07 13:45:58', '2026-03-07 17:09:06');

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `config_key`(`config_key` ASC) USING BTREE,
  INDEX `idx_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_config
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'AUTHOR, EDITOR, REVIEWER, ADMIN',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, APPROVED, REJECTED, ACTIVE, INACTIVE',
  `academic_achievements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'For authors',
  `work_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'For editors',
  `expertise_areas` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'For reviewers',
  `research_directions` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'For reviewers',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'zhangsan', '123456789', '12345666600@qq.com', '15071469547', 'AUTHOR', 'ACTIVE', 'ananng', NULL, NULL, NULL, '2026-03-06 20:46:17', '2026-03-07 12:49:26');
INSERT INTO `users` VALUES (2, 'bianji', '123456789', '12345666600@qq.com', '1507146975', 'EDITOR', 'ACTIVE', NULL, '12345666600@163.com', NULL, NULL, '2026-03-07 12:50:03', '2026-03-07 13:04:40');
INSERT INTO `users` VALUES (3, 'shengao', '12345678', '12345666600@qq.com', NULL, 'REVIEWER', 'ACTIVE', NULL, NULL, '计算机技术', '计算机技术计算机技术计算机技术计算机技术', '2026-03-07 13:42:31', '2026-03-07 16:55:38');
INSERT INTO `users` VALUES (4, 'shengao1', '12345678', '12345666680@qq.com', NULL, 'REVIEWER', 'ACTIVE', NULL, NULL, NULL, NULL, '2026-03-07 13:42:47', '2026-03-07 13:42:47');
INSERT INTO `users` VALUES (100, 'admin', '123456', 'admin@example.com', '13800000000', 'ADMIN', 'ACTIVE', NULL, NULL, NULL, NULL, '2026-05-01 00:00:00', '2026-05-01 00:00:00');

SET FOREIGN_KEY_CHECKS = 1;
