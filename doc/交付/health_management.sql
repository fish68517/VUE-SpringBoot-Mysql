/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : health_management

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 02/03/2026 15:41:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for audit_logs
-- ----------------------------
DROP TABLE IF EXISTS `audit_logs`;
CREATE TABLE `audit_logs`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审计日志ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `action` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作类型',
  `resource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作的资源',
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间戳',
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '操作详情',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_action`(`action` ASC) USING BTREE,
  INDEX `idx_timestamp`(`timestamp` ASC) USING BTREE,
  CONSTRAINT `audit_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '审计日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of audit_logs
-- ----------------------------
INSERT INTO `audit_logs` VALUES (1, 1, '提交咨询', 'Consultation', '2026-03-02 03:19:46', '用户提交了咨询问题，问题ID: 1');
INSERT INTO `audit_logs` VALUES (2, 1, '创建健康建议', 'HealthAdvice', '2026-03-02 06:23:15', '医师为患者创建了健康建议，建议ID: 5，患者ID: 1');
INSERT INTO `audit_logs` VALUES (3, 1, '创建健康建议', 'HealthAdvice', '2026-03-02 06:36:04', '医师为患者创建了健康建议，建议ID: 6，患者ID: 1');
INSERT INTO `audit_logs` VALUES (4, 1, '创建健康建议', 'HealthAdvice', '2026-03-02 06:37:27', '医师为患者创建了健康建议，建议ID: 7，患者ID: 1');
INSERT INTO `audit_logs` VALUES (5, 1, '创建健康建议', 'HealthAdvice', '2026-03-02 06:37:43', '医师为患者创建了健康建议，建议ID: 8，患者ID: 1');
INSERT INTO `audit_logs` VALUES (6, 1, '创建健康建议', 'HealthAdvice', '2026-03-02 07:02:46', '医师为患者创建了健康建议，建议ID: 9，患者ID: 1');
INSERT INTO `audit_logs` VALUES (7, 1, '创建健康建议', 'HealthAdvice', '2026-03-02 07:03:30', '医师为患者创建了健康建议，建议ID: 10，患者ID: 1');
INSERT INTO `audit_logs` VALUES (8, 1, '提交咨询', 'Consultation', '2026-03-02 07:06:53', '用户提交了咨询问题，问题ID: 2');
INSERT INTO `audit_logs` VALUES (9, 1, '回复咨询', 'Consultation', '2026-03-02 07:34:55', '医师回复了咨询问题，咨询ID: 1');

-- ----------------------------
-- Table structure for consultations
-- ----------------------------
DROP TABLE IF EXISTS `consultations`;
CREATE TABLE `consultations`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '咨询ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `doctor_id` bigint NULL DEFAULT NULL COMMENT '医师ID',
  `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '咨询问题',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '医师回答',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'PENDING' COMMENT '咨询状态: PENDING(待回答), ANSWERED(已回答)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `answered_at` timestamp NULL DEFAULT NULL COMMENT '回答时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_doctor_id`(`doctor_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `consultations_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `consultations_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '咨询表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of consultations
-- ----------------------------
INSERT INTO `consultations` VALUES (1, 1, 1, '睡眠不好睡眠不好睡眠不好睡眠不好', '爸爸那边拿爸爸去', 'ANSWERED', '2026-03-02 03:19:46', '2026-03-02 07:34:54');
INSERT INTO `consultations` VALUES (2, 1, NULL, '头痛怎么办 开什么药', NULL, 'PENDING', '2026-03-02 07:06:53', NULL);

-- ----------------------------
-- Table structure for doctors
-- ----------------------------
DROP TABLE IF EXISTS `doctors`;
CREATE TABLE `doctors`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '医师ID',
  `user_id` bigint NOT NULL COMMENT '关联的用户ID',
  `license_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执业证号',
  `specialization` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专科',
  `hospital` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '医院',
  `verified` tinyint(1) NULL DEFAULT 0 COMMENT '是否认证',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  UNIQUE INDEX `license_number`(`license_number` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_license_number`(`license_number` ASC) USING BTREE,
  CONSTRAINT `doctors_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '医师表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doctors
-- ----------------------------
INSERT INTO `doctors` VALUES (1, 3, 'aabb852111a', '内科', '人民医院', 0, '2026-03-02 03:48:03');

-- ----------------------------
-- Table structure for health_advice
-- ----------------------------
DROP TABLE IF EXISTS `health_advice`;
CREATE TABLE `health_advice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '健康建议ID',
  `doctor_id` bigint NOT NULL COMMENT '医师ID',
  `user_id` bigint NOT NULL COMMENT '患者ID',
  `advice_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '建议内容',
  `recommendation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '推荐信息',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_doctor_id`(`doctor_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `health_advice_ibfk_1` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `health_advice_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '健康建议表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of health_advice
-- ----------------------------
INSERT INTO `health_advice` VALUES (5, 1, 1, '好好休息好好吃饭睡觉打豆豆', '好好休息好好吃饭睡觉打豆豆好好休息好好吃饭睡觉打豆豆好好休息好好吃饭睡觉打豆豆', '2026-03-02 06:23:15', '2026-03-02 06:23:15');
INSERT INTO `health_advice` VALUES (6, 1, 1, '好好休息好好吃饭睡觉打豆豆', '补充维生素', '2026-03-02 06:36:04', '2026-03-02 06:36:04');
INSERT INTO `health_advice` VALUES (7, 1, 1, 'ok 没问题', '根据数据审核提供的建议', '2026-03-02 06:37:26', '2026-03-02 06:37:26');
INSERT INTO `health_advice` VALUES (8, 1, 1, '注意吃药', '根据数据审核提供的建议', '2026-03-02 06:37:43', '2026-03-02 06:37:43');
INSERT INTO `health_advice` VALUES (9, 1, 1, '请继续保持好的状态，休息好心情好', '根据数据审核提供的建议', '2026-03-02 07:02:46', '2026-03-02 07:02:46');
INSERT INTO `health_advice` VALUES (10, 1, 1, '建议注意清淡饮食建议注意清淡饮食建议注意清淡饮食建议注意清淡饮食', '清单饮食包括：迪卡的食物等待', '2026-03-02 07:03:30', '2026-03-02 07:03:30');

-- ----------------------------
-- Table structure for health_data
-- ----------------------------
DROP TABLE IF EXISTS `health_data`;
CREATE TABLE `health_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '健康数据ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `height` decimal(5, 2) NULL DEFAULT NULL COMMENT '身高(cm)',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '体重(kg)',
  `blood_pressure` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '血压',
  `heart_rate` int NULL DEFAULT NULL COMMENT '心率(次/分钟)',
  `diet_record` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '饮食记录',
  `exercise_record` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '运动记录',
  `check_results` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '检查结果备注',
  `menstrual_cycle` int NULL DEFAULT NULL COMMENT '月经周期(天)',
  `menstrual_days` int NULL DEFAULT NULL COMMENT '月经天数(天)',
  `last_menstrual_date` date NULL DEFAULT NULL COMMENT '最后月经日期',
  `pregnancy_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '妊娠状态: NOT_PREGNANT, PREGNANT, LACTATING',
  `menstrual_symptoms` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '月经症状',
  `prostate_health` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '前列腺健康信息',
  `sexual_function` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性功能状态: NORMAL, ABNORMAL, NEED_CONSULTATION',
  `review_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'PENDING' COMMENT '审核状态: PENDING(待审核), REVIEWED(已审核)',
  `review_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '医师审核意见/反馈',
  `feedback_date` timestamp NULL DEFAULT NULL COMMENT '审核反馈时间',
  `data_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '数据类型: ROUTINE(常规), GENDER_SPECIFIC(性别相关)',
  `recorded_at` timestamp NULL DEFAULT NULL COMMENT '记录时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_recorded_at`(`recorded_at` ASC) USING BTREE,
  INDEX `idx_data_type`(`data_type` ASC) USING BTREE,
  INDEX `idx_review_status`(`review_status` ASC) USING BTREE,
  CONSTRAINT `health_data_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '健康数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of health_data
-- ----------------------------
INSERT INTO `health_data` VALUES (1, 1, 165.00, 60.50, '120/80', 75, '早餐燕麦，午餐鸡胸肉沙拉，晚餐清淡。', '慢跑3公里。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'REVIEWED', '各项指标非常健康，请继续保持良好的饮食和运动习惯！', '2026-01-16 10:00:00', 'ROUTINE', '2026-01-15 08:30:00', '2026-01-15 08:30:00');
INSERT INTO `health_data` VALUES (3, 1, 165.00, 61.20, '138/88', 88, '最近春节聚餐较多，饮食偏油腻，饮酒两次。', '无运动。', '用户自述早晨起床有轻微头晕现象。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'PENDING', NULL, NULL, 'ROUTINE', '2026-02-10 19:00:00', '2026-02-10 19:00:00');
INSERT INTO `health_data` VALUES (4, 1, 165.00, 60.80, '125/82', 78, '开始恢复清淡饮食，增加蔬菜摄入。', '瑜伽40分钟。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'REVIEWED', '血压相比前几天有所下降，很好。建议继续控制盐分摄入，观察一周。', '2026-02-16 09:30:00', 'ROUTINE', '2026-02-15 08:00:00', '2026-02-15 08:00:00');
INSERT INTO `health_data` VALUES (7, 1, 160.00, 45.00, '120/80', 200, '心率太高了', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'PENDING', NULL, NULL, 'ROUTINE', '2026-03-01 18:36:03', '2026-03-02 03:01:51');
INSERT INTO `health_data` VALUES (8, 1, 160.00, 45.00, '120/80', 300, '健康状态:健康状态很好,性功能:', '健康状态很好', NULL, NULL, NULL, NULL, '', '', '健康状态很好', '', 'PENDING', NULL, NULL, 'GENDER_SPECIFIC', '2026-03-01 19:03:28', '2026-03-02 03:04:23');
INSERT INTO `health_data` VALUES (10, 1, 165.00, 50.00, '120/86', 200, '早餐 鸡蛋和稀粥 午餐 红烧肉 晚餐 葱油面条', '跑步10lm', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'PENDING', NULL, NULL, 'ROUTINE', '2026-03-01 23:03:51', '2026-03-02 07:04:39');
INSERT INTO `health_data` VALUES (15, 1, 165.00, 50.00, '120/80', 100, NULL, NULL, '心率低 血压低', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'PENDING', NULL, NULL, 'ROUTINE', '2026-03-01 23:12:40', '2026-03-02 07:13:31');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'USER' COMMENT '角色: USER(普通用户), DOCTOR(医师), ADMIN(管理员)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ACTIVE' COMMENT '账户状态: ACTIVE(活跃), INACTIVE(禁用)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, '患者1', '12345678', '12345666600@qq.com', '张三', 29, 'MALE', '138001380000', 'USER', 'ACTIVE', '2026-03-02 00:54:22', '2026-03-02 07:03:49');
INSERT INTO `users` VALUES (2, 'admin', 'admin123', '123456666@qq.com', NULL, NULL, NULL, NULL, 'ADMIN', 'ACTIVE', '2026-03-02 03:26:07', '2026-03-02 14:42:22');
INSERT INTO `users` VALUES (3, '医生1', '12345678', '12345666688@qq.com', NULL, NULL, NULL, NULL, 'DOCTOR', 'ACTIVE', '2026-03-02 03:48:03', '2026-03-02 03:48:03');
INSERT INTO `users` VALUES (4, '医生2', '12345678', '12345666680@qq.com', NULL, NULL, NULL, NULL, 'DOCTOR', 'ACTIVE', '2026-03-02 05:18:55', '2026-03-02 14:58:38');

SET FOREIGN_KEY_CHECKS = 1;
