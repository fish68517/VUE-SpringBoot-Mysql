/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : time_management_db

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 05/03/2026 13:54:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for achievement
-- ----------------------------
DROP TABLE IF EXISTS `achievement`;
CREATE TABLE `achievement`  (
  `achievement_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '成就徽章标识',
  `achieve_name_text` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '徽章名称文本',
  `achieve_description_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '徽章描述文本',
  `achieve_icon_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '徽章图标 URL',
  `achieve_rule_text` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '获取规则文本',
  `achieve_type_enum` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '徽章类型 (e.g., focus, habit, social)',
  `achieve_create_timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  `achieve_active_flag` tinyint(1) NULL DEFAULT 1 COMMENT '是否激活',
  PRIMARY KEY (`achievement_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '成就徽章表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for campus_user
-- ----------------------------
DROP TABLE IF EXISTS `campus_user`;
CREATE TABLE `campus_user`  (
  `campus_user_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '校园用户唯一标识',
  `campus_nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `campus_avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像 URL',
  `campus_school_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '学号',
  `campus_email_addr` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱地址',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '123456' COMMENT '用户密码',
  `campus_status_flag` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '状态 (0 - 未审核, 1 - 已审核, 2 - 禁用)',
  `campus_create_timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  `campus_level_code` int UNSIGNED NULL DEFAULT 0 COMMENT '用户等级代码',
  `campus_badge_count` int UNSIGNED NULL DEFAULT 0 COMMENT '用户徽章个数',
  `campus_last_login_ts` timestamp NULL DEFAULT NULL COMMENT '最后登录时间',
  `campus_user_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'student' COMMENT '用户类型 (student, admin)',
  PRIMARY KEY (`campus_user_id`) USING BTREE,
  UNIQUE INDEX `campus_email_addr`(`campus_email_addr` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '校园用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for focus_record
-- ----------------------------
DROP TABLE IF EXISTS `focus_record`;
CREATE TABLE `focus_record`  (
  `focus_record_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '专注记录标识',
  `campus_user_id` bigint UNSIGNED NOT NULL COMMENT '关联用户 ID',
  `task_focus_id` bigint UNSIGNED NOT NULL COMMENT '关联任务 ID',
  `focus_start_timestamp` timestamp NOT NULL COMMENT '开始时间戳',
  `focus_end_timestamp` timestamp NULL DEFAULT NULL COMMENT '结束时间戳',
  `focus_duration_seconds` int UNSIGNED NOT NULL COMMENT '专注时长 (秒)',
  `focus_type_enum` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '专注类型 (e.g., pomodoro, custom)',
  `focus_status_enum` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '专注状态 (completed, interrupted)',
  `focus_interruption_count` int UNSIGNED NULL DEFAULT 0 COMMENT '中断次数',
  `focus_pause_count` int UNSIGNED NULL DEFAULT 0 COMMENT '暂停次数',
  `focus_note_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '专注备注文本',
  `focus_app_block_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否启用应用拦截',
  PRIMARY KEY (`focus_record_id`) USING BTREE,
  INDEX `campus_user_id`(`campus_user_id` ASC) USING BTREE,
  INDEX `task_focus_id`(`task_focus_id` ASC) USING BTREE,
  CONSTRAINT `focus_record_ibfk_1` FOREIGN KEY (`campus_user_id`) REFERENCES `campus_user` (`campus_user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `focus_record_ibfk_2` FOREIGN KEY (`task_focus_id`) REFERENCES `task_focus` (`task_focus_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '专注记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for habit_checkin
-- ----------------------------
DROP TABLE IF EXISTS `habit_checkin`;
CREATE TABLE `habit_checkin`  (
  `habit_checkin_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '习惯打卡标识',
  `habit_track_id` bigint UNSIGNED NOT NULL COMMENT '关联习惯 ID',
  `checkin_date` date NOT NULL COMMENT '打卡日期',
  `checkin_timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '打卡时间戳',
  `checkin_status_enum` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'completed' COMMENT '打卡状态 (completed, skipped)',
  `checkin_note_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '打卡备注文本',
  `checkin_streak_count` int UNSIGNED NULL DEFAULT 0 COMMENT '当前连续打卡数',
  PRIMARY KEY (`habit_checkin_id`) USING BTREE,
  INDEX `habit_track_id`(`habit_track_id` ASC) USING BTREE,
  CONSTRAINT `habit_checkin_ibfk_1` FOREIGN KEY (`habit_track_id`) REFERENCES `habit_track` (`habit_track_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '习惯打卡表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for habit_track
-- ----------------------------
DROP TABLE IF EXISTS `habit_track`;
CREATE TABLE `habit_track`  (
  `habit_track_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '习惯追踪标识',
  `campus_user_id` bigint UNSIGNED NOT NULL COMMENT '关联用户 ID',
  `habit_name_text` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '习惯名称文本',
  `habit_frequency_enum` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '频率 (daily, weekly)',
  `habit_reminder_time` time NULL DEFAULT NULL COMMENT '提醒时间',
  `habit_status_enum` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'active' COMMENT '习惯状态 (active, archived)',
  `habit_create_timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  `habit_streak_count` int UNSIGNED NULL DEFAULT 0 COMMENT '连续打卡天数',
  `habit_total_count` int UNSIGNED NULL DEFAULT 0 COMMENT '总打卡次数',
  `habit_goal_count` int UNSIGNED NULL DEFAULT 0 COMMENT '目标打卡次数',
  `habit_color_hex` char(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#FFFFFF' COMMENT '习惯颜色 HEX',
  `habit_icon_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '习惯图标代码',
  PRIMARY KEY (`habit_track_id`) USING BTREE,
  INDEX `campus_user_id`(`campus_user_id` ASC) USING BTREE,
  CONSTRAINT `habit_track_ibfk_1` FOREIGN KEY (`campus_user_id`) REFERENCES `campus_user` (`campus_user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '习惯追踪表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for learn_resource
-- ----------------------------
DROP TABLE IF EXISTS `learn_resource`;
CREATE TABLE `learn_resource`  (
  `learn_resource_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '学习资源标识',
  `resource_category_id` bigint UNSIGNED NOT NULL COMMENT '资源分类 ID',
  `resource_name_text` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资源名称文本',
  `resource_url_text` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资源链接文本',
  `resource_description_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '资源描述文本',
  `resource_type_enum` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'link' COMMENT '资源类型 (link, document)',
  `resource_recommend_level` tinyint UNSIGNED NULL DEFAULT 3 COMMENT '推荐级别 (0-5)',
  `resource_create_timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  `resource_update_timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间戳',
  `resource_click_count` int UNSIGNED NULL DEFAULT 0 COMMENT '点击次数',
  PRIMARY KEY (`learn_resource_id`) USING BTREE,
  INDEX `resource_category_id`(`resource_category_id` ASC) USING BTREE,
  CONSTRAINT `learn_resource_ibfk_1` FOREIGN KEY (`resource_category_id`) REFERENCES `resource_category` (`resource_category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '学习资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notification_center
-- ----------------------------
DROP TABLE IF EXISTS `notification_center`;
CREATE TABLE `notification_center`  (
  `notification_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '通知标识',
  `campus_user_id` bigint UNSIGNED NOT NULL COMMENT '关联用户 ID',
  `notification_type_enum` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知类型 (system, friend_request, task_reminder)',
  `notification_title_text` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '通知标题文本',
  `notification_content_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '通知内容文本',
  `notification_is_read_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读标志',
  `notification_create_timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  `notification_expire_timestamp` timestamp NULL DEFAULT NULL COMMENT '过期时间戳',
  `notification_action_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '动作 URL',
  `notification_priority_code` tinyint UNSIGNED NULL DEFAULT 1 COMMENT '优先级代码 (0 - 低, 1 - 中, 2 - 高)',
  PRIMARY KEY (`notification_id`) USING BTREE,
  INDEX `campus_user_id`(`campus_user_id` ASC) USING BTREE,
  CONSTRAINT `notification_center_ibfk_1` FOREIGN KEY (`campus_user_id`) REFERENCES `campus_user` (`campus_user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知中心表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resource_category
-- ----------------------------
DROP TABLE IF EXISTS `resource_category`;
CREATE TABLE `resource_category`  (
  `resource_category_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '资源分类标识',
  `category_name_text` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称文本',
  `category_description_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类描述文本',
  `category_order_number` int UNSIGNED NULL DEFAULT 0 COMMENT '显示顺序号',
  `category_icon_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类图标代码',
  `category_create_timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  PRIMARY KEY (`resource_category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '资源分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resource_detail
-- ----------------------------
DROP TABLE IF EXISTS `resource_detail`;
CREATE TABLE `resource_detail`  (
  `detail_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '详情ID',
  `learn_resource_id` bigint UNSIGNED NOT NULL COMMENT '关联的基础资源ID',
  `content_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容类型: article(文章), video(视频), image(图片集)',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '详情标题(可选)',
  `content_text` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章正文内容',
  `media_file_names` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '媒体文件名列表(JSON格式或逗号分隔)',
  `like_count` int UNSIGNED NULL DEFAULT 0 COMMENT '点赞数',
  `collect_count` int UNSIGNED NULL DEFAULT 0 COMMENT '收藏数',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`detail_id`) USING BTREE,
  INDEX `learn_resource_id`(`learn_resource_id` ASC) USING BTREE,
  CONSTRAINT `resource_detail_ibfk_1` FOREIGN KEY (`learn_resource_id`) REFERENCES `learn_resource` (`learn_resource_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '资源详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for task_focus
-- ----------------------------
DROP TABLE IF EXISTS `task_focus`;
CREATE TABLE `task_focus`  (
  `task_focus_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '任务专注标识',
  `campus_user_id` bigint UNSIGNED NOT NULL COMMENT '关联用户 ID',
  `task_title_text` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务标题文本',
  `task_description_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '任务描述文本',
  `task_deadline_timestamp` timestamp NULL DEFAULT NULL COMMENT '截止时间戳',
  `task_status_enum` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'pending' COMMENT '任务状态 (pending, in_progress, completed, archived)',
  `task_create_timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
  `task_priority_code` tinyint UNSIGNED NULL DEFAULT 1 COMMENT '优先级代码 (0 - 低, 1 - 中, 2 - 高)',
  `task_reminder_timestamp` timestamp NULL DEFAULT NULL COMMENT '提醒时间戳',
  `task_category_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务分类代码',
  `task_focus_duration_mins` int UNSIGNED NULL DEFAULT 30 COMMENT '专注时长 (分钟)',
  `task_break_duration_mins` int UNSIGNED NULL DEFAULT 5 COMMENT '休息时长 (分钟)',
  `task_app_block_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否启用应用拦截',
  `task_actual_minutes` int UNSIGNED NULL DEFAULT 0 COMMENT '实际分钟数',
  PRIMARY KEY (`task_focus_id`) USING BTREE,
  INDEX `campus_user_id`(`campus_user_id` ASC) USING BTREE,
  CONSTRAINT `task_focus_ibfk_1` FOREIGN KEY (`campus_user_id`) REFERENCES `campus_user` (`campus_user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务专注表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_achieve_rel
-- ----------------------------
DROP TABLE IF EXISTS `user_achieve_rel`;
CREATE TABLE `user_achieve_rel`  (
  `user_achieve_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户成就关联标识',
  `campus_user_id` bigint UNSIGNED NOT NULL COMMENT '关联用户 ID',
  `achievement_id` bigint UNSIGNED NOT NULL COMMENT '关联徽章 ID',
  `achieve_timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间戳',
  `achieve_condition_text` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '达成条件描述',
  PRIMARY KEY (`user_achieve_id`) USING BTREE,
  INDEX `campus_user_id`(`campus_user_id` ASC) USING BTREE,
  INDEX `achievement_id`(`achievement_id` ASC) USING BTREE,
  CONSTRAINT `user_achieve_rel_ibfk_1` FOREIGN KEY (`campus_user_id`) REFERENCES `campus_user` (`campus_user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_achieve_rel_ibfk_2` FOREIGN KEY (`achievement_id`) REFERENCES `achievement` (`achievement_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户成就关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_resource_action
-- ----------------------------
DROP TABLE IF EXISTS `user_resource_action`;
CREATE TABLE `user_resource_action`  (
  `action_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `campus_user_id` bigint UNSIGNED NOT NULL,
  `learn_resource_id` bigint UNSIGNED NOT NULL,
  `is_liked` tinyint(1) NULL DEFAULT 0 COMMENT '是否点赞',
  `is_collected` tinyint(1) NULL DEFAULT 0 COMMENT '是否收藏',
  `action_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`action_id`) USING BTREE,
  UNIQUE INDEX `unique_user_resource`(`campus_user_id` ASC, `learn_resource_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户资源交互记录' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
