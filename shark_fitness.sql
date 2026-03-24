/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : shark_fitness

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 16/03/2026 11:11:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for check_in
-- ----------------------------
DROP TABLE IF EXISTS `check_in`;
CREATE TABLE `check_in`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '打卡ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `check_date` date NOT NULL COMMENT '打卡日期',
  `check_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '打卡时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_user_date`(`user_id` ASC, `check_date` ASC) USING BTREE,
  UNIQUE INDEX `UK44bu08d5kalkps607l66jlck4`(`user_id` ASC, `check_date` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_check_date`(`check_date` ASC) USING BTREE,
  INDEX `idx_checkin_user_date`(`user_id` ASC, `check_date` ASC) USING BTREE,
  CONSTRAINT `check_in_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户打卡表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of check_in
-- ----------------------------
INSERT INTO `check_in` VALUES (1, 3, '2025-10-24', '2025-10-24 09:48:46');
INSERT INTO `check_in` VALUES (2, 3, '2025-10-25', '2025-10-25 09:48:46');
INSERT INTO `check_in` VALUES (3, 3, '2025-10-26', '2025-10-26 09:48:46');
INSERT INTO `check_in` VALUES (4, 3, '2025-10-27', '2025-10-27 09:48:46');
INSERT INTO `check_in` VALUES (5, 3, '2025-10-28', '2025-10-28 09:48:46');
INSERT INTO `check_in` VALUES (6, 3, '2025-10-29', '2025-10-29 09:48:46');
INSERT INTO `check_in` VALUES (7, 3, '2025-10-30', '2025-10-30 09:48:46');
INSERT INTO `check_in` VALUES (8, 3, '2025-10-31', '2025-10-31 09:48:46');
INSERT INTO `check_in` VALUES (9, 3, '2025-11-01', '2025-11-01 09:48:46');
INSERT INTO `check_in` VALUES (10, 3, '2025-11-02', '2025-11-02 09:48:46');
INSERT INTO `check_in` VALUES (11, 3, '2025-11-03', '2025-11-03 09:48:46');
INSERT INTO `check_in` VALUES (12, 3, '2025-11-04', '2025-11-04 09:48:46');
INSERT INTO `check_in` VALUES (13, 3, '2025-11-05', '2025-11-05 09:48:46');
INSERT INTO `check_in` VALUES (14, 3, '2025-11-06', '2025-11-06 09:48:46');
INSERT INTO `check_in` VALUES (15, 3, '2025-11-07', '2025-11-07 09:48:46');
INSERT INTO `check_in` VALUES (16, 3, '2025-11-08', '2025-11-08 09:48:46');
INSERT INTO `check_in` VALUES (17, 3, '2025-11-09', '2025-11-09 09:48:46');
INSERT INTO `check_in` VALUES (18, 3, '2025-11-10', '2025-11-10 09:48:46');
INSERT INTO `check_in` VALUES (19, 3, '2025-11-11', '2025-11-11 09:48:46');
INSERT INTO `check_in` VALUES (20, 3, '2025-11-12', '2025-11-12 09:48:46');
INSERT INTO `check_in` VALUES (21, 3, '2025-11-13', '2025-11-13 09:48:46');
INSERT INTO `check_in` VALUES (22, 3, '2025-11-14', '2025-11-14 09:48:46');
INSERT INTO `check_in` VALUES (23, 3, '2025-11-15', '2025-11-15 09:48:46');
INSERT INTO `check_in` VALUES (24, 3, '2025-11-16', '2025-11-16 09:48:46');
INSERT INTO `check_in` VALUES (25, 3, '2025-11-17', '2025-11-17 09:48:46');
INSERT INTO `check_in` VALUES (26, 3, '2025-11-18', '2025-11-18 09:48:46');
INSERT INTO `check_in` VALUES (27, 3, '2025-11-19', '2025-11-19 09:48:46');
INSERT INTO `check_in` VALUES (28, 3, '2025-11-20', '2025-11-20 09:48:46');
INSERT INTO `check_in` VALUES (29, 3, '2025-11-21', '2025-11-21 09:48:46');
INSERT INTO `check_in` VALUES (30, 3, '2025-11-22', '2025-11-22 09:48:46');
INSERT INTO `check_in` VALUES (31, 3, '2025-11-23', '2025-11-23 09:48:46');
INSERT INTO `check_in` VALUES (32, 4, '2025-11-08', '2025-11-08 09:48:46');
INSERT INTO `check_in` VALUES (33, 4, '2025-11-09', '2025-11-09 09:48:46');
INSERT INTO `check_in` VALUES (34, 4, '2025-11-10', '2025-11-10 09:48:46');
INSERT INTO `check_in` VALUES (35, 4, '2025-11-11', '2025-11-11 09:48:46');
INSERT INTO `check_in` VALUES (36, 4, '2025-11-12', '2025-11-12 09:48:46');
INSERT INTO `check_in` VALUES (37, 4, '2025-11-13', '2025-11-13 09:48:46');
INSERT INTO `check_in` VALUES (38, 4, '2025-11-14', '2025-11-14 09:48:46');
INSERT INTO `check_in` VALUES (39, 4, '2025-11-15', '2025-11-15 09:48:46');
INSERT INTO `check_in` VALUES (40, 4, '2025-11-16', '2025-11-16 09:48:46');
INSERT INTO `check_in` VALUES (41, 4, '2025-11-17', '2025-11-17 09:48:46');
INSERT INTO `check_in` VALUES (42, 4, '2025-11-18', '2025-11-18 09:48:46');
INSERT INTO `check_in` VALUES (43, 4, '2025-11-19', '2025-11-19 09:48:46');
INSERT INTO `check_in` VALUES (44, 4, '2025-11-20', '2025-11-20 09:48:46');
INSERT INTO `check_in` VALUES (45, 4, '2025-11-21', '2025-11-21 09:48:46');
INSERT INTO `check_in` VALUES (46, 4, '2025-11-22', '2025-11-22 09:48:46');
INSERT INTO `check_in` VALUES (47, 4, '2025-11-23', '2025-11-23 09:48:46');
INSERT INTO `check_in` VALUES (48, 5, '2025-11-15', '2025-11-15 09:48:46');
INSERT INTO `check_in` VALUES (49, 5, '2025-11-16', '2025-11-16 09:48:46');
INSERT INTO `check_in` VALUES (50, 5, '2025-11-17', '2025-11-17 09:48:46');
INSERT INTO `check_in` VALUES (51, 5, '2025-11-18', '2025-11-18 09:48:46');
INSERT INTO `check_in` VALUES (52, 5, '2025-11-19', '2025-11-19 09:48:46');
INSERT INTO `check_in` VALUES (53, 5, '2025-11-20', '2025-11-20 09:48:46');
INSERT INTO `check_in` VALUES (54, 5, '2025-11-21', '2025-11-21 09:48:46');
INSERT INTO `check_in` VALUES (55, 5, '2025-11-22', '2025-11-22 09:48:46');
INSERT INTO `check_in` VALUES (56, 5, '2025-11-23', '2025-11-23 09:48:46');
INSERT INTO `check_in` VALUES (57, 10, '2025-11-25', '2025-11-25 20:02:14');
INSERT INTO `check_in` VALUES (58, 10, '2025-12-03', '2025-12-03 15:18:34');
INSERT INTO `check_in` VALUES (59, 5, '2026-03-15', '2026-03-15 12:02:34');

-- ----------------------------
-- Table structure for coach_certification
-- ----------------------------
DROP TABLE IF EXISTS `coach_certification`;
CREATE TABLE `coach_certification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID(关联user表)',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '证书名称(如: ACE-CPT)',
  `issuing_authority` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '颁发机构',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '证书图片路径',
  `issue_date` date NULL DEFAULT NULL COMMENT '颁发日期',
  `expiry_date` date NULL DEFAULT NULL COMMENT '有效期至',
  `status` int NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  CONSTRAINT `fk_cert_coach` FOREIGN KEY (`coach_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教练资格认证表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coach_certification
-- ----------------------------
INSERT INTO `coach_certification` VALUES (2, 2, '中国健身教练证书', '健身协会', '/uploads/2/zhengshu.jpeg', '2025-12-24', '2025-12-30', 1, '2025-12-08 21:33:09', '2026-03-08 22:59:14');
INSERT INTO `coach_certification` VALUES (4, 3, '中国健身教练证书2级', '中国健身协会证书', '/uploads/2/zhengshu.jpeg', '2026-01-02', '2026-01-10', 2, '2025-12-08 21:52:06', '2026-03-08 22:59:21');
INSERT INTO `coach_certification` VALUES (5, 4, '中国健身教练证书3级', '中国健身协会证书', '/uploads/2/zhengshu.jpeg', '2026-03-28', '2026-05-01', 0, '2026-03-08 22:31:49', '2026-03-08 22:41:41');

-- ----------------------------
-- Table structure for coach_student
-- ----------------------------
DROP TABLE IF EXISTS `coach_student`;
CREATE TABLE `coach_student`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `expire_at` datetime NULL DEFAULT NULL COMMENT '会员有效期截止时间',
  `status` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_coach_student`(`coach_id` ASC, `student_id` ASC) USING BTREE,
  UNIQUE INDEX `UK4xk9pxfnbbtpi8cbkink1urv1`(`coach_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `coach_student_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `coach_student_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教练-学生关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coach_student
-- ----------------------------
INSERT INTO `coach_student` VALUES (1, 3, 5, '2025-11-23 09:48:46', '2026-01-08 04:41:30', 1);
INSERT INTO `coach_student` VALUES (3, 4, 6, '2025-11-23 09:48:46', '2025-12-09 11:11:42', 1);
INSERT INTO `coach_student` VALUES (5, 2, 5, '2025-11-23 09:48:46', '2027-03-15 02:55:34', 1);
INSERT INTO `coach_student` VALUES (6, 3, 4, '2025-11-23 09:48:46', '2025-12-27 11:11:48', 1);
INSERT INTO `coach_student` VALUES (7, 3, 6, '2025-11-23 09:48:46', '2026-01-02 11:11:51', 1);
INSERT INTO `coach_student` VALUES (8, 2, 6, '2025-12-03 12:57:21', '2025-12-21 11:11:55', 1);
INSERT INTO `coach_student` VALUES (9, 2, 8, '2025-12-03 15:20:39', '2026-01-04 11:11:58', 1);
INSERT INTO `coach_student` VALUES (10, 4, 5, '2025-12-08 12:46:18', '2025-11-06 20:47:19', 1);
INSERT INTO `coach_student` VALUES (11, 2, 10, '2025-12-08 21:36:17', '2026-04-08 05:36:17', 1);
INSERT INTO `coach_student` VALUES (12, 3, 10, '2025-12-08 21:54:07', '2026-12-08 13:55:16', 1);

-- ----------------------------
-- Table structure for collection
-- ----------------------------
DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `resource_id` bigint NOT NULL COMMENT '资源ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_user_resource`(`user_id` ASC, `resource_id` ASC) USING BTREE,
  UNIQUE INDEX `UKtathe9nbgr52oty1koeoy3wyj`(`user_id` ASC, `resource_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_resource_id`(`resource_id` ASC) USING BTREE,
  CONSTRAINT `collection_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `collection_ibfk_2` FOREIGN KEY (`resource_id`) REFERENCES `fitness_resource` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '资源收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collection
-- ----------------------------
INSERT INTO `collection` VALUES (1, 3, 1, '2025-11-13 09:48:46');
INSERT INTO `collection` VALUES (2, 3, 2, '2025-11-15 09:48:46');
INSERT INTO `collection` VALUES (3, 3, 5, '2025-11-18 09:48:46');
INSERT INTO `collection` VALUES (4, 4, 2, '2025-11-16 09:48:46');
INSERT INTO `collection` VALUES (5, 4, 4, '2025-11-20 09:48:46');
INSERT INTO `collection` VALUES (6, 5, 1, '2025-11-17 09:48:46');
INSERT INTO `collection` VALUES (8, 5, 5, '2025-11-21 09:48:46');
INSERT INTO `collection` VALUES (9, 6, 2, '2025-11-14 09:48:46');
INSERT INTO `collection` VALUES (10, 6, 4, '2025-11-22 09:48:46');
INSERT INTO `collection` VALUES (12, 2, 5, '2025-11-20 09:48:46');
INSERT INTO `collection` VALUES (13, 7, 1, '2025-11-18 09:48:46');
INSERT INTO `collection` VALUES (14, 7, 4, '2025-11-21 09:48:46');
INSERT INTO `collection` VALUES (16, 5, 10, '2025-12-02 21:51:48');
INSERT INTO `collection` VALUES (17, 5, 7, '2025-12-02 22:43:00');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `dynamic_id` bigint NOT NULL COMMENT '动态ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_dynamic_id`(`dynamic_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`dynamic_id`) REFERENCES `dynamic` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, '太棒了！坚持就是胜利！', 4, 1, '2025-11-21 09:48:46');
INSERT INTO `comment` VALUES (2, '我也要开始打卡了，向你学习！', 5, 1, '2025-11-21 09:48:46');
INSERT INTO `comment` VALUES (3, '这个早餐看起来很健康，我也要试试！', 3, 2, '2025-11-22 09:48:46');
INSERT INTO `comment` VALUES (4, '营养搭配得很好，赞！', 6, 2, '2025-11-22 09:48:46');
INSERT INTO `comment` VALUES (5, '瑜伽真的很舒服，我也喜欢！', 3, 3, '2025-11-20 09:48:46');
INSERT INTO `comment` VALUES (6, '请问在哪里可以学瑜伽呢？', 5, 3, '2025-11-20 09:48:46');
INSERT INTO `comment` VALUES (7, '新器械效果怎么样？', 4, 4, '2025-11-19 09:48:46');
INSERT INTO `comment` VALUES (8, '我也想试试新器械！', 2, 4, '2025-11-19 09:48:46');
INSERT INTO `comment` VALUES (9, '太厉害了！减脂5kg，我也要加油！', 3, 5, '2025-11-18 09:48:46');
INSERT INTO `comment` VALUES (10, '分享一下你的减脂秘诀吧！', 6, 5, '2025-11-18 09:48:46');
INSERT INTO `comment` VALUES (11, '教练张真的很专业！', 4, 6, '2025-11-17 09:48:46');
INSERT INTO `comment` VALUES (12, '我也想找一个好教练！', 5, 6, '2025-11-17 09:48:46');
INSERT INTO `comment` VALUES (13, '这句话说得太好了！', 2, 7, '2025-11-16 09:48:46');
INSERT INTO `comment` VALUES (14, '健身改变人生！', 3, 7, '2025-11-16 09:48:46');
INSERT INTO `comment` VALUES (15, '我试过这个动作，真的很有效！', 4, 8, '2025-11-15 09:48:46');
INSERT INTO `comment` VALUES (16, '坚持做这个动作，效果真的不错！', 5, 8, '2025-11-15 09:48:46');
INSERT INTO `comment` VALUES (17, '团体课程真的很有动力！', 3, 9, '2025-11-14 09:48:46');
INSERT INTO `comment` VALUES (18, '我也想参加团体课程！', 6, 9, '2025-11-14 09:48:46');
INSERT INTO `comment` VALUES (19, '这个建议很实用，谢谢分享！', 4, 10, '2025-11-13 09:48:46');
INSERT INTO `comment` VALUES (20, '热身真的很重要，我之前就因为没热身受过伤！', 5, 10, '2025-11-13 09:48:46');
INSERT INTO `comment` VALUES (25, '那你说', 10, 1, '2025-12-03 15:18:57');
INSERT INTO `comment` VALUES (26, '000', 10, 15, '2026-03-15 14:17:14');
INSERT INTO `comment` VALUES (27, '00', 10, 1, '2026-03-15 14:25:16');
INSERT INTO `comment` VALUES (28, '00', 10, 15, '2026-03-15 14:25:28');

-- ----------------------------
-- Table structure for diet_record
-- ----------------------------
DROP TABLE IF EXISTS `diet_record`;
CREATE TABLE `diet_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `meal_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '餐类型: breakfast(早餐), lunch(午餐), dinner(晚餐), snack(零食)',
  `food_items` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '食物项目',
  `calories` int NULL DEFAULT NULL COMMENT '卡路里',
  `meal_date` date NULL DEFAULT NULL COMMENT '餐食日期',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_meal_date`(`meal_date` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `diet_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '饮食记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of diet_record
-- ----------------------------
INSERT INTO `diet_record` VALUES (1, 3, 'breakfast', '鸡蛋2个、燕麦粥、牛奶', 350, '2025-11-21', '2025-11-21 09:48:46', '2025-11-21 09:48:46');
INSERT INTO `diet_record` VALUES (2, 3, 'lunch', '鸡胸肉200g、糙米饭、青菜', 550, '2025-11-21', '2025-11-21 09:48:46', '2025-11-21 09:48:46');
INSERT INTO `diet_record` VALUES (3, 3, 'dinner', '鱼肉150g、红薯、沙拉', 480, '2025-11-21', '2025-11-21 09:48:46', '2025-11-21 09:48:46');
INSERT INTO `diet_record` VALUES (4, 3, 'snack', '蛋白粉、香蕉', 200, '2025-11-21', '2025-11-21 09:48:46', '2025-11-21 09:48:46');
INSERT INTO `diet_record` VALUES (5, 3, 'breakfast', '全麦面包、鸡蛋、牛奶', 380, '2025-11-22', '2025-11-22 09:48:46', '2025-11-22 09:48:46');
INSERT INTO `diet_record` VALUES (6, 3, 'lunch', '牛肉200g、米饭、蔬菜', 620, '2025-11-22', '2025-11-22 09:48:46', '2025-11-22 09:48:46');
INSERT INTO `diet_record` VALUES (7, 3, 'dinner', '鸡胸肉150g、甘薯、绿菜', 450, '2025-11-22', '2025-11-22 09:48:46', '2025-11-22 09:48:46');
INSERT INTO `diet_record` VALUES (8, 3, 'snack', '酸奶、坚果', 180, '2025-11-22', '2025-11-22 09:48:46', '2025-11-22 09:48:46');
INSERT INTO `diet_record` VALUES (9, 3, 'breakfast', '燕麦、蓝莓、牛奶', 320, '2025-11-23', '2025-11-23 09:48:46', '2025-11-23 09:48:46');
INSERT INTO `diet_record` VALUES (10, 3, 'lunch', '鸡胸肉、糙米、西兰花', 580, '2025-11-23', '2025-11-23 09:48:46', '2025-11-23 09:48:46');
INSERT INTO `diet_record` VALUES (11, 4, 'breakfast', '鸡蛋、面包、果汁', 400, '2025-11-22', '2025-11-22 09:48:46', '2025-11-22 09:48:46');
INSERT INTO `diet_record` VALUES (12, 4, 'lunch', '鱼肉、米饭、蔬菜', 520, '2025-11-22', '2025-11-22 09:48:46', '2025-11-22 09:48:46');
INSERT INTO `diet_record` VALUES (13, 4, 'dinner', '鸡肉、红薯、沙拉', 480, '2025-11-22', '2025-11-22 09:48:46', '2025-11-22 09:48:46');
INSERT INTO `diet_record` VALUES (14, 4, 'breakfast', '酸奶、谷物、水果', 350, '2025-11-23', '2025-11-23 09:48:46', '2025-11-23 09:48:46');
INSERT INTO `diet_record` VALUES (15, 4, 'lunch', '牛肉、糙米、青菜', 600, '2025-11-23', '2025-11-23 09:48:46', '2025-11-23 09:48:46');
INSERT INTO `diet_record` VALUES (16, 5, 'breakfast', '鸡蛋、全麦面包', 320, '2025-11-22', '2025-11-22 09:48:46', '2025-11-22 09:48:46');
INSERT INTO `diet_record` VALUES (17, 5, 'lunch', '鸡胸肉、米饭、蔬菜', 550, '2025-11-22', '2025-11-22 09:48:46', '2025-11-22 09:48:46');
INSERT INTO `diet_record` VALUES (18, 5, 'dinner', '鱼肉、红薯、沙拉', 450, '2025-11-22', '2025-11-22 09:48:46', '2025-11-22 09:48:46');
INSERT INTO `diet_record` VALUES (19, 5, 'breakfast', '燕麦、香蕉、牛奶', 380, '2025-11-23', '2025-11-23 09:48:46', '2025-11-23 09:48:46');
INSERT INTO `diet_record` VALUES (20, 5, 'lunch', '鸡肉、糙米、西兰花', 580, '2025-11-23', '2025-11-23 09:48:46', '2025-11-23 09:48:46');
INSERT INTO `diet_record` VALUES (21, 6, 'breakfast', '鸡蛋、面包、牛奶', 400, '2025-11-22', '2025-11-22 09:48:46', '2025-11-22 09:48:46');
INSERT INTO `diet_record` VALUES (22, 6, 'lunch', '牛肉、米饭、蔬菜', 620, '2025-11-22', '2025-11-22 09:48:46', '2025-11-22 09:48:46');
INSERT INTO `diet_record` VALUES (23, 6, 'dinner', '鱼肉、红薯、沙拉', 480, '2025-11-22', '2025-11-22 09:48:46', '2025-11-22 09:48:46');
INSERT INTO `diet_record` VALUES (24, 6, 'breakfast', '全麦面包、鸡蛋、果汁', 420, '2025-11-23', '2025-11-23 09:48:46', '2025-11-23 09:48:46');
INSERT INTO `diet_record` VALUES (25, 6, 'lunch', '鸡胸肉、糙米、青菜', 560, '2025-11-23', '2025-11-23 09:48:46', '2025-11-23 09:48:46');
INSERT INTO `diet_record` VALUES (26, 5, 'breakfast', '鸡蛋 小米粥', 10, '2025-12-08', '2025-12-08 09:26:18', '2025-12-08 09:26:18');
INSERT INTO `diet_record` VALUES (27, 5, 'dinner', '红烧肉 牛奶 汤', 100, '2025-12-08', '2025-12-08 09:26:35', '2025-12-08 09:26:50');
INSERT INTO `diet_record` VALUES (29, 10, 'breakfast', '222', 10, '2025-12-08', '2025-12-08 21:53:02', '2025-12-08 21:53:02');

-- ----------------------------
-- Table structure for dynamic
-- ----------------------------
DROP TABLE IF EXISTS `dynamic`;
CREATE TABLE `dynamic`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '动态ID',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `image_urls` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片URL列表(逗号分隔)',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'approved' COMMENT '状态: pending(待审核), approved(已批准), rejected(已拒绝)',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_dynamic_like_count`(`like_count` ASC) USING BTREE,
  CONSTRAINT `dynamic_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社区动态表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dynamic
-- ----------------------------
INSERT INTO `dynamic` VALUES (1, '今天完成了我的第100次打卡！坚持健身真的改变了我的生活，感谢小鲨鱼健身平台的陪伴！💪', 'https://api.example.com/images/checkin_100.jpg', 3, 46, 10, 'approved', '2025-11-21 09:48:46', '2026-03-15 14:37:19');
INSERT INTO `dynamic` VALUES (2, '分享一下我的早餐：鸡蛋、燕麦和蓝莓。健身从饮食开始！', 'https://api.example.com/images/breakfast.jpg', 4, 33, 5, 'approved', '2025-11-22 09:48:46', '2026-03-15 14:17:28');
INSERT INTO `dynamic` VALUES (3, '刚完成了一个小时的瑜伽课程，感觉身心都得到了放松。瑜伽真的很棒！🧘‍♀️', 'https://api.example.com/images/yoga_session.jpg', 2, 28, 4, 'approved', '2025-11-20 09:48:46', '2025-11-20 09:48:46');
INSERT INTO `dynamic` VALUES (4, '健身房新增了几台器械，今天试了试新的腿部训练机，效果不错！', 'https://api.example.com/images/gym_equipment.jpg', 5, 18, 3, 'approved', '2025-11-19 09:48:46', '2025-11-19 09:48:46');
INSERT INTO `dynamic` VALUES (5, '坚持了3个月的减脂计划，终于看到效果了！从80kg减到75kg，继续加油！', 'https://api.example.com/images/weight_loss.jpg', 4, 56, 12, 'approved', '2025-11-18 09:48:46', '2025-12-03 14:56:26');
INSERT INTO `dynamic` VALUES (6, '今天的训练计划完成！感谢教练张的专业指导，每次都能学到新东西。', 'https://api.example.com/images/training_session.jpg', 6, 22, 6, 'approved', '2025-11-17 09:48:46', '2025-12-03 14:56:28');
INSERT INTO `dynamic` VALUES (7, '健身不仅改变了我的身体，更改变了我的心态。每天都充满了正能量！', 'https://api.example.com/images/motivation.jpg', 7, 89, 15, 'approved', '2025-11-16 09:48:46', '2025-11-16 09:48:46');
INSERT INTO `dynamic` VALUES (8, '分享一个简单有效的腹肌训练动作：卷腹。每天坚持20个，4周见效！', 'https://api.example.com/images/abs_exercise.jpg', 3, 67, 11, 'approved', '2025-11-15 09:48:46', '2025-11-15 09:48:46');
INSERT INTO `dynamic` VALUES (9, '今天参加了健身房的团体课程，和大家一起运动真的很有动力！', 'https://api.example.com/images/group_class.jpg', 5, 34, 7, 'rejected', '2025-11-14 09:48:46', '2025-12-03 15:11:47');
INSERT INTO `dynamic` VALUES (10, '健身小贴士：运动前一定要充分热身，这样可以减少受伤的风险。', 'https://api.example.com/images/warmup_tips.jpg', 1, 41, 9, 'approved', '2025-11-13 09:48:46', '2025-11-13 09:48:46');
INSERT INTO `dynamic` VALUES (15, '252525', '/images/d95ee37a-f854-4827-be77-d42f8d90fa99.jpg,/images/8e71115e-320e-4433-aab7-58c48e7d3c75.jpeg', 3, 1, 2, 'pending', '2025-12-03 15:18:24', '2026-03-15 14:25:28');

-- ----------------------------
-- Table structure for fitness_resource
-- ----------------------------
DROP TABLE IF EXISTS `fitness_resource`;
CREATE TABLE `fitness_resource`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源标题',
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '资源描述',
  `content_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容类型: video(视频), article(文章), document(文档)',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件URL',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '文章内容',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_content_type`(`content_type` ASC) USING BTREE,
  INDEX `idx_creator_id`(`creator_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_resource_view_count`(`view_count` ASC) USING BTREE,
  CONSTRAINT `fitness_resource_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '健身资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fitness_resource
-- ----------------------------
INSERT INTO `fitness_resource` VALUES (1, '初级健身入门教程000', '适合健身初学者的基础训练教程，包括热身、基本动作和放松', 'video', 'https://api.example.com/videos/beginner_tutorial.mp4', '', 1, 1265, '2025-11-23 09:48:46', '2025-12-03 14:35:50');
INSERT INTO `fitness_resource` VALUES (2, '高效腹肌训练30分钟', '针对腹肌的高效训练课程，每天30分钟，坚持4周见效', 'video', 'https://api.example.com/videos/abs_training.mp4', NULL, 1, 2853, '2025-11-23 09:48:46', '2025-12-02 22:43:07');
INSERT INTO `fitness_resource` VALUES (4, '有氧运动减脂指南', '科学的有氧运动方法，帮助您有效减脂，提高心肺功能', 'video', 'https://api.example.com/videos/cardio_guide.mp4', NULL, 3, 3201, '2025-11-23 09:48:46', '2026-03-15 12:18:42');
INSERT INTO `fitness_resource` VALUES (5, '力量训练完全指南', '从零开始学习力量训练，包括各种器械的正确使用方法', 'video', 'https://api.example.com/videos/strength_guide.mp4', NULL, 1, 2102, '2025-11-23 09:48:46', '2025-12-03 15:17:47');
INSERT INTO `fitness_resource` VALUES (6, '健身初学者必读：如何制定合理的训练计划', '本文详细介绍了如何根据自己的目标和身体状况制定合理的训练计划，包括训练频率、强度和恢复时间的安排。', 'article', NULL, '健身初学者常常不知道如何开始。首先，你需要明确你的目标：是增肌、减脂还是提高体能？其次，根据你的目标选择合适的训练方式。最后，制定一个循序渐进的计划，并坚持执行。记住，健身是一个长期的过程，不要急功近利。', 1, 892, '2025-11-23 09:48:46', '2025-11-25 21:18:39');
INSERT INTO `fitness_resource` VALUES (7, '营养指南：健身期间应该吃什么', '详细介绍了健身期间的营养需求，包括蛋白质、碳水化合物和脂肪的合理比例，以及推荐的食物清单。', 'article', NULL, '健身效果的50%来自训练，另外50%来自营养。蛋白质是肌肉的基础，建议每天摄入1.6-2.2克/公斤体重。碳水化合物提供能量，脂肪帮助激素分泌。选择全谷物、瘦肉、鸡蛋、豆类和新鲜蔬菜。', 2, 1242, '2025-11-23 09:48:46', '2025-12-02 22:42:58');
INSERT INTO `fitness_resource` VALUES (8, '如何避免健身中的常见错误', '列举了健身过程中常见的错误做法，并提供了改正建议，帮助您更安全有效地进行训练。', 'article', NULL, '常见错误包括：1. 过度训练导致过度疲劳；2. 忽视热身和放松；3. 动作不标准导致受伤；4. 只做有氧运动忽视力量训练；5. 不注意恢复和睡眠。避免这些错误，您的健身效果会大大提升。', 3, 760, '2025-11-23 09:48:46', '2025-11-25 20:20:14');
INSERT INTO `fitness_resource` VALUES (9, '瑜伽的益处和如何开始', '介绍了瑜伽对身心健康的益处，以及初学者如何安全地开始瑜伽练习。', 'article', NULL, '瑜伽不仅能增强身体柔韧性和力量，还能改善心理健康，减少压力和焦虑。初学者应该从基础课程开始，学习正确的呼吸技巧和姿势。每周练习3-4次，坚持8周就能看到明显效果。', 2, 1102, '2025-11-23 09:48:46', '2025-12-03 15:17:43');
INSERT INTO `fitness_resource` VALUES (10, '运动恢复的重要性', '详细说明了运动后恢复的重要性，包括睡眠、营养和主动恢复的方法。', 'article', NULL, '恢复和训练同样重要。充足的睡眠（7-9小时）对肌肉生长至关重要。营养恢复包括训练后30分钟内摄入蛋白质和碳水化合物。主动恢复包括轻度有氧运动、拉伸和按摩。', 1, 965, '2025-11-23 09:48:46', '2026-03-15 12:17:17');
INSERT INTO `fitness_resource` VALUES (13, '如何跑步', '如何跑步如何跑步如何跑步如何跑步如何跑步', 'article', '', '如何跑步如何跑步如何跑步如何跑步如何跑步如何跑步', 2, 8, '2025-12-03 15:22:00', '2026-03-15 12:18:34');

-- ----------------------------
-- Table structure for like_record
-- ----------------------------
DROP TABLE IF EXISTS `like_record`;
CREATE TABLE `like_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `dynamic_id` bigint NOT NULL COMMENT '动态ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_user_dynamic`(`user_id` ASC, `dynamic_id` ASC) USING BTREE,
  UNIQUE INDEX `UK62lagx4idbguex4c5m14nmrap`(`user_id` ASC, `dynamic_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_dynamic_id`(`dynamic_id` ASC) USING BTREE,
  CONSTRAINT `like_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `like_record_ibfk_2` FOREIGN KEY (`dynamic_id`) REFERENCES `dynamic` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '点赞记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of like_record
-- ----------------------------
INSERT INTO `like_record` VALUES (1, 4, 1, '2025-11-21 09:48:46');
INSERT INTO `like_record` VALUES (2, 5, 1, '2025-11-21 09:48:46');
INSERT INTO `like_record` VALUES (3, 6, 1, '2025-11-21 09:48:46');
INSERT INTO `like_record` VALUES (4, 2, 1, '2025-11-21 09:48:46');
INSERT INTO `like_record` VALUES (5, 3, 2, '2025-11-22 09:48:46');
INSERT INTO `like_record` VALUES (6, 5, 2, '2025-11-22 09:48:46');
INSERT INTO `like_record` VALUES (7, 6, 2, '2025-11-22 09:48:46');
INSERT INTO `like_record` VALUES (8, 3, 3, '2025-11-20 09:48:46');
INSERT INTO `like_record` VALUES (9, 4, 3, '2025-11-20 09:48:46');
INSERT INTO `like_record` VALUES (10, 5, 3, '2025-11-20 09:48:46');
INSERT INTO `like_record` VALUES (11, 6, 3, '2025-11-20 09:48:46');
INSERT INTO `like_record` VALUES (12, 3, 4, '2025-11-19 09:48:46');
INSERT INTO `like_record` VALUES (13, 4, 4, '2025-11-19 09:48:46');
INSERT INTO `like_record` VALUES (14, 2, 4, '2025-11-19 09:48:46');
INSERT INTO `like_record` VALUES (15, 3, 5, '2025-11-18 09:48:46');
INSERT INTO `like_record` VALUES (16, 5, 5, '2025-11-18 09:48:46');
INSERT INTO `like_record` VALUES (17, 6, 5, '2025-11-18 09:48:46');
INSERT INTO `like_record` VALUES (18, 2, 5, '2025-11-18 09:48:46');
INSERT INTO `like_record` VALUES (19, 3, 6, '2025-11-17 09:48:46');
INSERT INTO `like_record` VALUES (20, 4, 6, '2025-11-17 09:48:46');
INSERT INTO `like_record` VALUES (21, 5, 6, '2025-11-17 09:48:46');
INSERT INTO `like_record` VALUES (22, 3, 7, '2025-11-16 09:48:46');
INSERT INTO `like_record` VALUES (23, 4, 7, '2025-11-16 09:48:46');
INSERT INTO `like_record` VALUES (24, 5, 7, '2025-11-16 09:48:46');
INSERT INTO `like_record` VALUES (25, 6, 7, '2025-11-16 09:48:46');
INSERT INTO `like_record` VALUES (26, 2, 7, '2025-11-16 09:48:46');
INSERT INTO `like_record` VALUES (27, 3, 8, '2025-11-15 09:48:46');
INSERT INTO `like_record` VALUES (28, 4, 8, '2025-11-15 09:48:46');
INSERT INTO `like_record` VALUES (29, 5, 8, '2025-11-15 09:48:46');
INSERT INTO `like_record` VALUES (30, 6, 8, '2025-11-15 09:48:46');
INSERT INTO `like_record` VALUES (31, 3, 9, '2025-11-14 09:48:46');
INSERT INTO `like_record` VALUES (32, 4, 9, '2025-11-14 09:48:46');
INSERT INTO `like_record` VALUES (33, 5, 9, '2025-11-14 09:48:46');
INSERT INTO `like_record` VALUES (34, 3, 10, '2025-11-13 09:48:46');
INSERT INTO `like_record` VALUES (35, 4, 10, '2025-11-13 09:48:46');
INSERT INTO `like_record` VALUES (36, 5, 10, '2025-11-13 09:48:46');
INSERT INTO `like_record` VALUES (37, 6, 10, '2025-11-13 09:48:46');
INSERT INTO `like_record` VALUES (39, 10, 15, '2026-03-15 14:16:41');
INSERT INTO `like_record` VALUES (40, 10, 2, '2026-03-15 14:17:28');
INSERT INTO `like_record` VALUES (41, 10, 1, '2026-03-15 14:37:19');

-- ----------------------------
-- Table structure for subscription_order
-- ----------------------------
DROP TABLE IF EXISTS `subscription_order`;
CREATE TABLE `subscription_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型: new(新办), renew(续约)',
  `days` int NOT NULL COMMENT '购买时长(天数)',
  `amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '支付金额',
  `start_date` date NOT NULL COMMENT '生效日期',
  `end_date` date NOT NULL COMMENT '截止日期',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coach_student`(`coach_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `sub_order_student_fk`(`student_id` ASC) USING BTREE,
  CONSTRAINT `sub_order_coach_fk` FOREIGN KEY (`coach_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `sub_order_student_fk` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '会员续约/购买记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of subscription_order
-- ----------------------------

-- ----------------------------
-- Table structure for training_feedback
-- ----------------------------
DROP TABLE IF EXISTS `training_feedback`;
CREATE TABLE `training_feedback`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `plan_id` bigint NOT NULL COMMENT '关联的训练计划ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `coach_id` bigint NULL DEFAULT NULL COMMENT '教练ID',
  `feedback_date` date NULL DEFAULT NULL COMMENT '反馈针对的训练日期(如果是针对某一天的训练)',
  `rating` int NULL DEFAULT NULL,
  `feeling` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '体感: easy(轻松), normal(适中), hard(困难), exhausted(力竭)',
  `content` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `coach_reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '教练回复内容',
  `reply_at` date NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '反馈提交时间',
  `document_urls` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `image_urls` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `video_urls` tinytext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `feedback_plan_fk` FOREIGN KEY (`plan_id`) REFERENCES `training_plan` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `feedback_student_fk` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练反馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of training_feedback
-- ----------------------------
INSERT INTO `training_feedback` VALUES (1, 4, 5, NULL, '2025-12-08', 5, NULL, '00000', NULL, NULL, '2025-12-08 16:42:13', NULL, NULL, NULL);
INSERT INTO `training_feedback` VALUES (2, 5, 10, NULL, '2025-12-08', 5, NULL, '教练太好了', NULL, NULL, '2025-12-08 21:37:34', NULL, NULL, NULL);
INSERT INTO `training_feedback` VALUES (3, 9, 10, NULL, '2025-12-08', 5, NULL, '教练不错', NULL, NULL, '2025-12-08 21:37:43', NULL, NULL, NULL);
INSERT INTO `training_feedback` VALUES (4, 9, 10, NULL, '2025-12-10', 5, NULL, '0000', NULL, NULL, '2025-12-08 21:53:23', NULL, NULL, NULL);
INSERT INTO `training_feedback` VALUES (5, 9, 10, NULL, '2025-12-10', 5, NULL, '88888', NULL, NULL, '2025-12-08 21:53:47', NULL, NULL, NULL);
INSERT INTO `training_feedback` VALUES (7, 1, 10, 3, '2026-01-01', 4, NULL, '你就回家', '哈哈哈哈', '2026-01-01', '2026-01-01 21:55:13', NULL, NULL, NULL);
INSERT INTO `training_feedback` VALUES (8, 1, 10, 3, '2026-01-02', 2, NULL, '刚刚好是不是', '2525252', '2026-01-01', '2026-01-01 22:58:10', NULL, NULL, NULL);
INSERT INTO `training_feedback` VALUES (10, 1, 5, 2, '2026-03-15', 5, 'normal', '10000', '知道了', '2026-03-15', '2026-03-15 11:39:21', '/documents/93535098-dfe8-40ee-9ab7-1f66186980a2.docx', '/images/31aacf0b-2cd8-4f67-b7cb-2fbb29742da7.jpeg', '/static/videos/edbddb59-1f7a-4d95-8e91-acfdf54ca89a.mp4');

ALTER TABLE `training_feedback`
  ADD COLUMN IF NOT EXISTS `coach_reply_image_urls` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '教练回复图片';
ALTER TABLE `training_feedback`
  ADD COLUMN IF NOT EXISTS `coach_reply_video_urls` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '教练回复视频';
ALTER TABLE `training_feedback`
  ADD COLUMN IF NOT EXISTS `coach_reply_document_urls` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '教练回复文档';

-- ----------------------------
-- Table structure for training_plan
-- ----------------------------
DROP TABLE IF EXISTS `training_plan`;
CREATE TABLE `training_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '计划名称',
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '计划描述',
  `exercises` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '训练项目(JSON格式)',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'active' COMMENT '状态: active(进行中), completed(已完成), cancelled(已取消)',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coach_id`(`coach_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `training_plan_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `training_plan_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '训练计划表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of training_plan
-- ----------------------------
INSERT INTO `training_plan` VALUES (1, '8周增肌计划', '针对初学者的增肌训练计划，通过循序渐进的力量训练帮助您增加肌肉质量', '[{\"day\":\"周一\",\"exercises\":[{\"name\":\"卧推\",\"sets\":4,\"reps\":8,\"weight\":\"60kg\"},{\"name\":\"杠铃深蹲\",\"sets\":4,\"reps\":8,\"weight\":\"80kg\"}]},{\"day\":\"周三\",\"exercises\":[{\"name\":\"硬拉\",\"sets\":3,\"reps\":5,\"weight\":\"100kg\"},{\"name\":\"杠铃划船\",\"sets\":4,\"reps\":8,\"weight\":\"70kg\"}]},{\"day\":\"周五\",\"exercises\":[{\"name\":\"肩上推举\",\"sets\":3,\"reps\":8,\"weight\":\"40kg\"},{\"name\":\"卧推\",\"sets\":3,\"reps\":10,\"weight\":\"55kg\"}]}]', 2, 7, '2024-01-15', '2024-03-15', 'active', '2025-11-23 09:48:46', '2025-12-08 19:48:15');
INSERT INTO `training_plan` VALUES (2, '12周减脂计划', '科学的减脂训练计划，结合有氧运动和力量训练，帮助您安全有效地减脂', '[{\"day\":\"周一\",\"exercises\":[{\"name\":\"跑步\",\"duration\":\"30分钟\",\"intensity\":\"中等\"},{\"name\":\"腹肌训练\",\"sets\":3,\"reps\":15}]},{\"day\":\"周三\",\"exercises\":[{\"name\":\"动感单车\",\"duration\":\"45分钟\",\"intensity\":\"高\"},{\"name\":\"全身力量训练\",\"duration\":\"30分钟\"}]},{\"day\":\"周五\",\"exercises\":[{\"name\":\"椭圆机\",\"duration\":\"30分钟\",\"intensity\":\"中等\"},{\"name\":\"核心训练\",\"sets\":3,\"reps\":20}]}]', 3, 8, '2024-01-20', '2024-04-20', 'active', '2025-11-23 09:48:46', '2025-12-08 19:48:25');
INSERT INTO `training_plan` VALUES (3, '瑜伽柔韧性提升计划', '为期6周的瑜伽课程，帮助您改善身体柔韧性和平衡能力', '[{\"day\":\"周一\",\"exercises\":[{\"name\":\"基础瑜伽\",\"duration\":\"60分钟\",\"type\":\"hatha\"}]},{\"day\":\"周三\",\"exercises\":[{\"name\":\"流瑜伽\",\"duration\":\"60分钟\",\"type\":\"vinyasa\"}]},{\"day\":\"周五\",\"exercises\":[{\"name\":\"阴瑜伽\",\"duration\":\"60分钟\",\"type\":\"yin\"}]}]', 2, 6, '2024-01-10', '2024-02-21', 'active', '2025-11-23 09:48:46', '2025-12-08 19:48:00');
INSERT INTO `training_plan` VALUES (4, '4周核心力量训练', '专注于核心肌群的训练计划，提高身体稳定性和运动表现', '[{\"day\":\"周一\",\"exercises\":[{\"name\":\"平板支撑\",\"duration\":\"3分钟\",\"sets\":3},{\"name\":\"卷腹\",\"sets\":3,\"reps\":20}]},{\"day\":\"周三\",\"exercises\":[{\"name\":\"死虫式\",\"sets\":3,\"reps\":15},{\"name\":\"鸟狗式\",\"sets\":3,\"reps\":12}]},{\"day\":\"周五\",\"exercises\":[{\"name\":\"俄罗斯转体\",\"sets\":3,\"reps\":20},{\"name\":\"山羊挺身\",\"sets\":3,\"reps\":15}]}]', 3, 5, '2024-02-01', '2024-02-29', 'completed', '2025-11-23 09:48:46', '2025-12-08 15:38:57');
INSERT INTO `training_plan` VALUES (5, '全身综合训练计划', '适合有一定基础的健身爱好者，全面提升身体各部位的力量和耐力', '[{\"day\":\"周一\",\"exercises\":[{\"name\":\"卧推\",\"sets\":4,\"reps\":8},{\"name\":\"杠铃深蹲\",\"sets\":4,\"reps\":8}]},{\"day\":\"周三\",\"exercises\":[{\"name\":\"硬拉\",\"sets\":3,\"reps\":5},{\"name\":\"引体向上\",\"sets\":3,\"reps\":8}]},{\"day\":\"周五\",\"exercises\":[{\"name\":\"肩上推举\",\"sets\":3,\"reps\":8},{\"name\":\"杠铃划船\",\"sets\":4,\"reps\":8}]}]', 1, 10, '2024-01-25', '2024-04-25', 'completed', '2025-11-23 09:48:46', '2025-12-03 15:19:56');
INSERT INTO `training_plan` VALUES (6, '每天30分支跑步', '每天30分支跑步每天30分支跑步每天30分支跑步每天30分支跑步', '[{\"name\":\"跑步\",\"sets\":1,\"reps\":10,\"duration\":\"30\",\"notes\":\"每天30分支跑步\"},{\"name\":\"拉伸\",\"sets\":1,\"reps\":2,\"duration\":\"10\",\"notes\":\"每天30分支跑步拉伸\"}]', 2, 5, '2025-12-02', '2025-12-10', 'active', '2025-12-03 13:29:52', '2025-12-08 19:41:03');
INSERT INTO `training_plan` VALUES (7, '减脂跑步', '减脂跑步减脂跑步减脂跑步减脂跑步减脂跑步减脂跑步', '[{\"name\":\"跑步\",\"sets\":10,\"reps\":10,\"duration\":\"30\",\"notes\":\"跑步跑步跑步跑步跑步\"},{\"name\":\"跑步后拉伸\",\"sets\":1,\"reps\":1,\"duration\":\"15\",\"notes\":\"跑步后拉伸跑步后拉伸跑步后拉伸跑步后拉伸\"}]', 2, 5, '2025-12-01', '2025-12-17', 'completed', '2025-12-03 15:21:39', '2025-12-08 19:41:15');
INSERT INTO `training_plan` VALUES (8, '跑步跑步', '跑步跑步跑步跑步', '[{\"name\":\"深蹲\",\"sets\":1,\"reps\":1,\"duration\":\"10\",\"notes\":\"\"}]', 2, 5, '2025-12-01', '2025-12-01', 'active', '2025-12-08 15:23:05', '2025-12-08 15:23:05');
INSERT INTO `training_plan` VALUES (9, '瑜伽瑜伽', '瑜伽瑜伽瑜伽瑜伽瑜伽', '[{\"name\":\"深蹲\",\"sets\":2,\"reps\":2,\"duration\":\"10\",\"notes\":\"深蹲深蹲深蹲深蹲深蹲\"},{\"name\":\"休息\",\"sets\":1,\"reps\":1,\"duration\":\"30\",\"notes\":\"深蹲深蹲深蹲深蹲深蹲深蹲深蹲深蹲\"}]', 2, 10, '2025-12-18', '2026-01-01', 'active', '2025-12-08 21:37:14', '2025-12-08 21:37:14');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色: user(普通用户), coach(教练), admin(管理员)',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别: male(男), female(女)',
  `intro` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '个人介绍',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_user_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'admin123', 'admin', '', 'male', '系统管理员', '2025-11-23 09:48:45', '2025-12-08 20:07:11');
INSERT INTO `user` VALUES (2, 'coach_zhang', 'coach123', 'coach', '', 'Female', '资深健身教练，擅长力量训练和体型塑造000', '2025-11-23 09:48:46', '2025-12-08 20:07:14');
INSERT INTO `user` VALUES (3, 'coach_li', 'coach123', 'coach', '', 'female', '瑜伽和普拉提专家，帮助您改善身体柔韧性', '2025-11-23 09:48:46', '2025-12-08 20:07:16');
INSERT INTO `user` VALUES (4, 'coach_wang', 'coach123', 'coach', '', 'male', '有氧运动和减脂专家，5年教学经验', '2025-11-23 09:48:46', '2025-12-08 20:07:18');
INSERT INTO `user` VALUES (5, 'user_xiaoming', 'user123', 'user', '', 'male', '健身爱好者，坚持运动改变生活', '2025-11-23 09:48:46', '2025-12-08 20:07:19');
INSERT INTO `user` VALUES (6, 'user_xiaohong', 'user123', 'user', '', 'female', '瑜伽爱好者，追求身心健康', '2025-11-23 09:48:46', '2025-12-08 20:07:21');
INSERT INTO `user` VALUES (7, 'user_liming', 'user123', 'user', '', 'male', '健身新手，正在学习正确的训练方法', '2025-11-23 09:48:46', '2025-12-08 20:07:23');
INSERT INTO `user` VALUES (8, 'user_wangfang', 'user123', 'user', '', 'female', '减脂目标用户，已坚持3个月', '2025-11-23 09:48:46', '2025-12-08 20:07:24');
INSERT INTO `user` VALUES (9, 'user_zhangjie', 'user123', 'user', '', 'male', '力量训练爱好者，目标是增肌', '2025-11-23 09:48:46', '2025-12-08 20:07:26');
INSERT INTO `user` VALUES (10, 'user_liuyan', 'user123', 'user', '', 'female', '健身达人，分享健身心得和经验000', '2025-11-23 09:48:46', '2025-12-08 20:07:28');

SET FOREIGN_KEY_CHECKS = 1;
