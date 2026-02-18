/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : medical_internship

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 19/02/2026 07:22:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for application
-- ----------------------------
DROP TABLE IF EXISTS `application`;
CREATE TABLE `application`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '申请理由',
  `school_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学校审批状态',
  `hospital_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '医院审批状态',
  `school_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学校意见',
  `hospital_opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '医院意见',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_app_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_app_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of application
-- ----------------------------
INSERT INTO `application` VALUES (1, 4, 1, '我对心内科非常感兴趣，希望获得实习机会。', 'APPROVED', 'APPROVED', '同意', '同意录用', '2026-02-18 11:09:07', '2026-02-18 11:09:07');
INSERT INTO `application` VALUES (2, 5, 2, '在校期间成绩优异，外科操作考核满分。', 'APPROVED', 'APPROVED', '同意推荐', '同意', '2026-02-18 11:09:07', '2026-02-18 11:30:48');
INSERT INTO `application` VALUES (3, 8, 1, '我i很想这个岗位', 'APPROVED', 'APPROVED', '同意 改学术扎实', '欧克 ', '2026-02-18 07:02:59', '2026-02-18 07:50:18');
INSERT INTO `application` VALUES (4, 4, 4, '不不不', 'APPROVED', 'APPROVED', '通过', '同意录用', '2026-02-18 07:57:21', '2026-02-18 11:32:28');
INSERT INTO `application` VALUES (5, 4, 2, '我想申请这个岗位', 'PENDING', 'PENDING', NULL, NULL, '2026-02-18 11:26:59', '2026-02-18 11:26:59');
INSERT INTO `application` VALUES (6, 7, 1, '出来扎到多多指教', 'APPROVED', 'APPROVED', '通过', '欧克 ', '2026-02-18 11:42:48', '2026-02-18 11:42:48');

-- ----------------------------
-- Table structure for evaluation
-- ----------------------------
DROP TABLE IF EXISTS `evaluation`;
CREATE TABLE `evaluation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `internship_id` bigint NOT NULL,
  `evaluator_id` bigint NOT NULL,
  `evaluator_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评价人类型: TEACHER, STUDENT',
  `score` int NOT NULL,
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_internship_id`(`internship_id` ASC) USING BTREE,
  INDEX `idx_evaluator_id`(`evaluator_id` ASC) USING BTREE,
  CONSTRAINT `fk_eval_int` FOREIGN KEY (`internship_id`) REFERENCES `internship` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_eval_user` FOREIGN KEY (`evaluator_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluation
-- ----------------------------
INSERT INTO `evaluation` VALUES (1, 1, 6, 'TEACHER', 4, '该生基础知识扎实，临床操作规范。', '2026-02-18 11:09:11');
INSERT INTO `evaluation` VALUES (2, 1, 4, 'STUDENT', 4, '陈老师教学非常耐心，学到了很多实用的临床技巧。', '2026-02-18 11:09:11');
INSERT INTO `evaluation` VALUES (3, 1, 6, 'TEACHER', 4, '学术勤奋好学继续努力', '2026-02-18 09:22:25');
INSERT INTO `evaluation` VALUES (4, 2, 6, 'TEACHER', 4, '学术勤奋好学继续努力学术勤奋好学继续努力学术勤奋好学继续努力', '2026-02-18 09:22:42');
INSERT INTO `evaluation` VALUES (5, 2, 6, 'TEACHER', 4, '很好的师兄', '2026-02-18 11:29:32');
INSERT INTO `evaluation` VALUES (6, 1, 4, 'TEACHER', 4, '老师很好', '2026-02-18 15:42:27');
INSERT INTO `evaluation` VALUES (7, 1, 4, 'HOSPITAL', 5, '医院也很好', '2026-02-18 15:42:44');

-- ----------------------------
-- Table structure for internship
-- ----------------------------
DROP TABLE IF EXISTS `internship`;
CREATE TABLE `internship`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `application_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `teacher_id` bigint NOT NULL,
  `post_id` bigint NOT NULL,
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态: ONGOING, COMPLETED, TERMINATED',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_application_id`(`application_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE,
  INDEX `fk_int_post`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_int_app` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_int_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_int_stu` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_int_tea` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '实习记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of internship
-- ----------------------------
INSERT INTO `internship` VALUES (1, 1, 4, 6, 1, '2024-03-01', '2024-08-28', 'ONGOING', '2026-02-18 11:09:08', '2026-02-18 11:09:08');
INSERT INTO `internship` VALUES (2, 3, 8, 6, 1, '2026-02-18', '2026-08-17', 'ONGOING', '2026-02-18 07:50:18', '2026-02-18 07:50:18');
INSERT INTO `internship` VALUES (3, 2, 5, 6, 2, '2026-02-18', '2026-05-19', 'ONGOING', '2026-02-18 11:30:48', '2026-02-18 11:30:48');
INSERT INTO `internship` VALUES (4, 4, 4, 6, 4, '2026-02-04', '2026-07-17', 'ONGOING', '2026-02-18 16:32:52', '2026-02-18 16:32:52');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型: APPLICATION_RESULT, WEEKLY_REJECTED, TASK_DEADLINE',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_read` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_notif_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, 4, 'APPLICATION_RESULT', '您的岗位申请已通过审批。审批意见：同意录用', 1, '2026-02-18 11:09:11');
INSERT INTO `notification` VALUES (2, 6, 'TASK_DEADLINE', '任务 \"完成心电图判读练习\" 即将截止，请及时完成。', 1, '2026-02-18 11:09:11');
INSERT INTO `notification` VALUES (3, 8, 'APPLICATION_RESULT', '您的岗位申请已通过审批。审批意见：同意 改学术扎实', 0, '2026-02-18 07:28:27');
INSERT INTO `notification` VALUES (4, 8, 'APPLICATION_RESULT', '您的岗位申请已通过审批。审批意见：欧克 ', 0, '2026-02-18 07:50:18');
INSERT INTO `notification` VALUES (5, 1, 'SYSTEM_NOTICE', '请大家开始实习', 1, '2026-02-18 17:35:39');
INSERT INTO `notification` VALUES (6, 1, 'SYSTEM_NOTICE', '系统通知', 1, '2026-02-18 11:21:16');
INSERT INTO `notification` VALUES (7, 5, 'APPLICATION_RESULT', '您的岗位申请已通过审批。审批意见：同意', 0, '2026-02-18 11:30:48');
INSERT INTO `notification` VALUES (8, 4, 'APPLICATION_RESULT', '您的岗位申请已通过审批。审批意见：通过', 0, '2026-02-18 11:31:51');
INSERT INTO `notification` VALUES (9, 4, 'APPLICATION_RESULT', '您的岗位申请已被驳回。驳回原因：专业不符', 0, '2026-02-18 11:32:28');
INSERT INTO `notification` VALUES (10, 1, 'SYSTEM_NOTICE', '全体通知', 1, '2026-02-18 11:33:08');

-- ----------------------------
-- Table structure for organization
-- ----------------------------
DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '组织名称',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '组织类型: SCHOOL(学校) 或 HOSPITAL(医院)',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '组织代码',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '组织表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of organization
-- ----------------------------
INSERT INTO `organization` VALUES (1, '广州医科大学', 'SCHOOL', 'SCH_GZMU_001', '2026-02-18 11:09:04');
INSERT INTO `organization` VALUES (2, '中山大学医学院', 'SCHOOL', 'SCH_SYSU_002', '2026-02-18 11:09:04');
INSERT INTO `organization` VALUES (3, '广东省人民医院', 'HOSPITAL', 'HOS_GDPH_001', '2026-02-18 11:09:04');
INSERT INTO `organization` VALUES (4, '广州市第一人民医院', 'HOSPITAL', 'HOS_GZFH_002', '2026-02-18 11:09:04');

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `hospital_id` bigint NOT NULL COMMENT '医院ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位名称',
  `department` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '科室',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `quota` int NOT NULL COMMENT '名额',
  `duration` int NOT NULL COMMENT '周期(天)',
  `visible_schools` json NULL COMMENT '可见学校ID列表 JSON 格式',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态: DRAFT, PUBLISHED, ARCHIVED',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_hospital_id`(`hospital_id` ASC) USING BTREE,
  CONSTRAINT `fk_post_hospital` FOREIGN KEY (`hospital_id`) REFERENCES `organization` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '岗位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES (1, 3, '心内科实习生', '心血管内科', '参与病房查房、病历书写及基础临床操作。', 5, 180, '[1, 2]', 'PUBLISHED', '2026-02-18 11:09:06', '2026-02-18 11:09:06');
INSERT INTO `post` VALUES (2, 3, '普外科实习生', '普外科', '参与手术跟台及外科换药。', 3, 90, '[1]', 'PUBLISHED', '2026-02-18 11:09:06', '2026-02-18 11:09:06');
INSERT INTO `post` VALUES (3, 4, '儿科见习生', '儿科', '协助儿科医生处理日常问诊。', 2, 30, '[2]', 'DRAFT', '2026-02-18 11:09:06', '2026-02-18 11:09:06');
INSERT INTO `post` VALUES (4, 3, 'ct医生', 'ct1', 'ct医生ct医生ct医生ct医生ct医生ct医生', 2, 8, NULL, 'PUBLISHED', '2026-02-18 07:55:59', '2026-02-18 07:59:40');
INSERT INTO `post` VALUES (5, 3, '外科医生', '外科', '外科医生外科医生外科医生外科医生', 3, 8, NULL, 'PUBLISHED', '2026-02-18 11:30:27', '2026-02-18 11:30:33');

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `internship_id` bigint NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `deadline` datetime NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_internship_id`(`internship_id` ASC) USING BTREE,
  CONSTRAINT `fk_task_int` FOREIGN KEY (`internship_id`) REFERENCES `internship` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task
-- ----------------------------
INSERT INTO `task` VALUES (1, 1, '整理本周心内科查房记录', '请将本周三主治医师的查房记录整理成电子版并提交。', '2026-02-25 11:09:09', '2026-02-18 11:09:09', '2026-02-18 11:09:09');
INSERT INTO `task` VALUES (2, 1, '完成心电图判读练习', '判读科室指定的10份心电图，并给出初步诊断。', '2026-02-21 11:09:09', '2026-02-18 11:09:09', '2026-02-18 11:09:09');
INSERT INTO `task` VALUES (3, 2, '好好学习', '好好学习好好学习好好学习好好学习', '2026-06-18 16:00:00', '2026-02-18 09:11:36', '2026-02-18 09:11:36');
INSERT INTO `task` VALUES (4, 2, '请积极完成其他的周报', '请积极完成其他的周报', '2026-02-27 16:00:00', '2026-02-18 11:28:46', '2026-02-18 11:28:46');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色: ADMIN, SCHOOL_ADMIN, HOSPITAL_ADMIN, STUDENT, TEACHER',
  `organization_id` bigint NOT NULL COMMENT '所属组织ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态: PENDING, APPROVED, REJECTED',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_org_id`(`organization_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_org` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin_system', 'password123', 'admin@sys.com', 'ADMIN', 1, 'APPROVED', '2026-02-18 11:09:05', '2026-02-18 11:09:05');
INSERT INTO `user` VALUES (2, 'school_admin_1', 'password123', 'sa1@school.com', 'SCHOOL_ADMIN', 1, 'APPROVED', '2026-02-18 11:09:05', '2026-02-18 11:09:05');
INSERT INTO `user` VALUES (3, 'hospital_admin_1', 'password123', 'ha1@hospital.com', 'HOSPITAL_ADMIN', 3, 'APPROVED', '2026-02-18 11:09:05', '2026-02-18 11:09:05');
INSERT INTO `user` VALUES (4, 'student_li', 'password123', 'li@student.com', 'STUDENT', 3, 'APPROVED', '2026-02-18 11:09:05', '2026-02-18 11:09:05');
INSERT INTO `user` VALUES (5, 'student_wang', 'password123', 'wang@student.com', 'STUDENT', 1, 'APPROVED', '2026-02-18 11:09:05', '2026-02-18 08:21:17');
INSERT INTO `user` VALUES (6, 'teacher_zhang', 'password123', 'zhang@hospital.com', 'TEACHER', 3, 'APPROVED', '2026-02-18 11:09:05', '2026-02-18 11:09:05');
INSERT INTO `user` VALUES (7, '张同学', '123456', '123@qq.com', 'STUDENT', 1, 'PENDING', '2026-02-18 03:29:55', '2026-02-18 03:29:55');
INSERT INTO `user` VALUES (8, '李同学', '123456', '123hh@qq.com', 'STUDENT', 1, 'REJECTED', '2026-02-18 07:00:06', '2026-02-18 08:21:45');

-- ----------------------------
-- Table structure for weekly_report
-- ----------------------------
DROP TABLE IF EXISTS `weekly_report`;
CREATE TABLE `weekly_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `internship_id` bigint NOT NULL,
  `week_number` int NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `teacher_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `teacher_score` int NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态: SUBMITTED, REVIEWED, REJECTED',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_internship_id`(`internship_id` ASC) USING BTREE,
  CONSTRAINT `fk_wr_int` FOREIGN KEY (`internship_id`) REFERENCES `internship` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '周记表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of weekly_report
-- ----------------------------
INSERT INTO `weekly_report` VALUES (1, 1, 1, '第一周主要是熟悉了科室的环境和排班流程，观摩了张老师的手术。', '融入较快，继续保持。', 5, 'REVIEWED', '2026-02-18 11:09:09', '2026-02-18 11:09:09');
INSERT INTO `weekly_report` VALUES (2, 1, 2, '本周尝试独立询问病史，发现部分专业词汇在医患沟通时还需要转化。', NULL, 5, 'REVIEWED', '2026-02-18 11:09:09', '2026-02-18 09:14:46');
INSERT INTO `weekly_report` VALUES (3, 1, 3, '今天很开心', '很好，再接再厉', 4, 'REVIEWED', '2026-02-18 08:20:22', '2026-02-18 09:18:06');

SET FOREIGN_KEY_CHECKS = 1;
