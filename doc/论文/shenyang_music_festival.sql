/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : shenyang_music_festival

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 26/02/2026 14:22:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article_comments
-- ----------------------------
DROP TABLE IF EXISTS `article_comments`;
CREATE TABLE `article_comments`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `likes` int NULL DEFAULT 0 COMMENT '点赞数',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_article_id`(`article_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `article_comments_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `article_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article_comments
-- ----------------------------

-- ----------------------------
-- Table structure for articles
-- ----------------------------
DROP TABLE IF EXISTS `articles`;
CREATE TABLE `articles`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章内容（富文本）',
  `images` json NULL COMMENT '文章图片URL列表（JSON数组）',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作者',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文章类型：news/guide/food_recommendation',
  `likes` int NULL DEFAULT 0 COMMENT '点赞数',
  `views` int NULL DEFAULT 0 COMMENT '浏览数',
  `is_published` tinyint(1) NULL DEFAULT 1 COMMENT '是否发布',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_is_published`(`is_published` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of articles
-- ----------------------------
INSERT INTO `articles` VALUES (1, '2024沈阳音乐节完全攻略', '本文为您详细介绍沈阳音乐节的各项信息，包括交通、住宿、美食等', NULL, '管理员', 'guide', 0, 0, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `articles` VALUES (2, '沈阳本土乐队推荐', '介绍沈阳本地优秀乐队，感受本土音乐文化', NULL, '音乐编辑', 'news', 0, 0, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `articles` VALUES (3, '沈阳特色美食推荐', '音乐节期间必吃的沈阳特色美食', NULL, '美食编辑', 'food_recommendation', 0, 0, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `articles` VALUES (4, '五月天演唱会亮点预告', '五月天在沈阳音乐节的精彩表演预告', NULL, '娱乐编辑', 'news', 0, 0, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `articles` VALUES (5, '音乐节防疫须知', '参加音乐节需要了解的防疫信息', NULL, '管理员', 'guide', 0, 0, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `articles` VALUES (6, '2024沈阳音乐节完全攻略', '本文为您详细介绍沈阳音乐节的各项信息，包括交通、住宿、美食等', NULL, '管理员', 'guide', 0, 0, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `articles` VALUES (7, '沈阳本土乐队推荐', '介绍沈阳本地优秀乐队，感受本土音乐文化', NULL, '音乐编辑', 'news', 0, 0, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `articles` VALUES (8, '沈阳特色美食推荐', '音乐节期间必吃的沈阳特色美食', NULL, '美食编辑', 'food_recommendation', 0, 0, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `articles` VALUES (9, '五月天演唱会亮点预告', '五月天在沈阳音乐节的精彩表演预告', NULL, '娱乐编辑', 'news', 0, 0, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `articles` VALUES (10, '音乐节防疫须知', '参加音乐节需要了解的防疫信息', NULL, '管理员', 'guide', 0, 0, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');

-- ----------------------------
-- Table structure for artists
-- ----------------------------
DROP TABLE IF EXISTS `artists`;
CREATE TABLE `artists`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '艺人ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '艺人名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '艺人描述',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '艺人图片URL',
  `is_local_band` tinyint(1) NULL DEFAULT 0 COMMENT '是否为沈阳本土乐队',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_is_local_band`(`is_local_band` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '艺人表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of artists
-- ----------------------------
INSERT INTO `artists` VALUES (1, '五月天', '台湾摇滚乐队，中文摇滚的代表', NULL, 0, '2025-12-04 19:02:58', '2025-12-04 19:02:58');
INSERT INTO `artists` VALUES (2, 'Coldplay', '英国摇滚乐队，全球知名', NULL, 0, '2025-12-04 19:02:58', '2025-12-04 19:02:58');
INSERT INTO `artists` VALUES (3, '二手玫瑰', '中国摇滚乐队，沈阳本土乐队', NULL, 1, '2025-12-04 19:02:58', '2025-12-04 19:02:58');
INSERT INTO `artists` VALUES (4, '谢天笑', '中国摇滚歌手，沈阳本土艺人', NULL, 1, '2025-12-04 19:02:58', '2025-12-04 19:02:58');
INSERT INTO `artists` VALUES (5, 'Mayday', '台湾摇滚乐队', NULL, 0, '2025-12-04 19:02:58', '2025-12-04 19:02:58');
INSERT INTO `artists` VALUES (6, 'The Neighbourhood', '美国独立摇滚乐队', NULL, 0, '2025-12-04 19:02:58', '2025-12-04 19:02:58');
INSERT INTO `artists` VALUES (7, '五月天', '台湾摇滚乐队，中文摇滚的代表', NULL, 0, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `artists` VALUES (8, 'Coldplay', '英国摇滚乐队，全球知名', NULL, 0, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `artists` VALUES (9, '二手玫瑰', '中国摇滚乐队，沈阳本土乐队', NULL, 1, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `artists` VALUES (10, '谢天笑', '中国摇滚歌手，沈阳本土艺人', NULL, 1, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `artists` VALUES (11, 'Mayday', '台湾摇滚乐队', NULL, 0, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `artists` VALUES (12, 'The Neighbourhood', '美国独立摇滚乐队', NULL, 0, '2025-12-04 19:56:08', '2025-12-04 19:56:08');

-- ----------------------------
-- Table structure for checkin_records
-- ----------------------------
DROP TABLE IF EXISTS `checkin_records`;
CREATE TABLE `checkin_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '打卡记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `photo` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '打卡照片URL',
  `latitude` decimal(10, 8) NOT NULL COMMENT '打卡位置纬度',
  `longitude` decimal(11, 8) NOT NULL COMMENT '打卡位置经度',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending' COMMENT '状态：pending/approved/rejected',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_task`(`user_id` ASC, `task_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_task_id`(`task_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `checkin_records_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `checkin_records_ibfk_2` FOREIGN KEY (`task_id`) REFERENCES `checkin_tasks` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '打卡记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of checkin_records
-- ----------------------------

-- ----------------------------
-- Table structure for checkin_tasks
-- ----------------------------
DROP TABLE IF EXISTS `checkin_tasks`;
CREATE TABLE `checkin_tasks`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '任务描述',
  `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '任务封面图URL',
  `points` int NOT NULL COMMENT '奖励积分',
  `latitude` decimal(10, 8) NOT NULL COMMENT '地理位置纬度',
  `longitude` decimal(11, 8) NOT NULL COMMENT '地理位置经度',
  `radius` int NOT NULL COMMENT '地理围栏半径（米）',
  `start_time` datetime NOT NULL COMMENT '任务开始时间',
  `end_time` datetime NOT NULL COMMENT '任务结束时间',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ongoing' COMMENT '状态：ongoing/ended',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  INDEX `idx_end_time`(`end_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '打卡任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of checkin_tasks
-- ----------------------------
INSERT INTO `checkin_tasks` VALUES (1, '盛京皇宫打卡', '在沈阳盛京皇宫完成打卡，感受满族文化', NULL, 50, 41.80440000, 123.43280000, 500, '2024-12-15 00:00:00', '2024-12-17 23:59:59', 'ongoing', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `checkin_tasks` VALUES (2, '中街美食街打卡', '在沈阳中街美食街品尝地方美食并打卡', NULL, 40, 41.80500000, 123.43500000, 300, '2024-12-15 00:00:00', '2024-12-17 23:59:59', 'ongoing', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `checkin_tasks` VALUES (3, '奥体中心打卡', '在音乐节举办地奥体中心打卡', NULL, 100, 41.79500000, 123.42000000, 1000, '2024-12-15 00:00:00', '2024-12-17 23:59:59', 'ongoing', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `checkin_tasks` VALUES (4, '沈阳故宫打卡', '在沈阳故宫完成打卡，了解沈阳历史', NULL, 50, 41.81000000, 123.44000000, 400, '2024-12-15 00:00:00', '2024-12-17 23:59:59', 'ongoing', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `checkin_tasks` VALUES (5, '北陵公园打卡', '在北陵公园完成打卡，享受自然风光', NULL, 30, 41.82000000, 123.45000000, 600, '2024-12-15 00:00:00', '2024-12-17 23:59:59', 'ongoing', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `checkin_tasks` VALUES (6, '盛京皇宫打卡', '在沈阳盛京皇宫完成打卡，感受满族文化', NULL, 50, 41.80440000, 123.43280000, 500, '2024-12-15 00:00:00', '2024-12-17 23:59:59', 'ongoing', '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `checkin_tasks` VALUES (7, '中街美食街打卡', '在沈阳中街美食街品尝地方美食并打卡', NULL, 40, 41.80500000, 123.43500000, 300, '2024-12-15 00:00:00', '2024-12-17 23:59:59', 'ongoing', '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `checkin_tasks` VALUES (8, '奥体中心打卡', '在音乐节举办地奥体中心打卡', NULL, 100, 41.79500000, 123.42000000, 1000, '2024-12-15 00:00:00', '2024-12-17 23:59:59', 'ongoing', '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `checkin_tasks` VALUES (9, '沈阳故宫打卡', '在沈阳故宫完成打卡，了解沈阳历史', NULL, 50, 41.81000000, 123.44000000, 400, '2024-12-15 00:00:00', '2024-12-17 23:59:59', 'ongoing', '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `checkin_tasks` VALUES (10, '北陵公园打卡', '在北陵公园完成打卡，享受自然风光', NULL, 30, 41.82000000, 123.45000000, 600, '2024-12-15 00:00:00', '2024-12-17 23:59:59', 'ongoing', '2025-12-04 19:56:09', '2025-12-04 19:56:09');

-- ----------------------------
-- Table structure for festival_follows
-- ----------------------------
DROP TABLE IF EXISTS `festival_follows`;
CREATE TABLE `festival_follows`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关注ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `festival_id` bigint NOT NULL COMMENT '音乐节ID',
  `followed_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_festival`(`user_id` ASC, `festival_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_festival_id`(`festival_id` ASC) USING BTREE,
  CONSTRAINT `festival_follows_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `festival_follows_ibfk_2` FOREIGN KEY (`festival_id`) REFERENCES `festivals` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '音乐节关注表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of festival_follows
-- ----------------------------

-- ----------------------------
-- Table structure for festivals
-- ----------------------------
DROP TABLE IF EXISTS `festivals`;
CREATE TABLE `festivals`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '音乐节ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '音乐节名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '音乐节描述',
  `start_date` datetime NOT NULL COMMENT '开始时间',
  `end_date` datetime NOT NULL COMMENT '结束时间',
  `location` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '举办地点',
  `poster_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '海报URL',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ongoing' COMMENT '状态：ongoing/ended',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_start_date`(`start_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '音乐节表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of festivals
-- ----------------------------
INSERT INTO `festivals` VALUES (1, '2024沈阳音乐节', '沈阳首届大型音乐节，汇聚国内外知名艺人，融合沈阳本地文化元素', '2024-12-15 18:00:00', '2024-12-17 23:00:00', '沈阳奥体中心', NULL, 'ongoing', '2025-12-04 19:02:58', '2025-12-04 19:02:58');
INSERT INTO `festivals` VALUES (2, '2024沈阳音乐节', '沈阳首届大型音乐节，汇聚国内外知名艺人，融合沈阳本地文化元素', '2024-12-15 18:00:00', '2024-12-17 23:00:00', '沈阳奥体中心', NULL, 'ongoing', '2025-12-04 19:56:07', '2025-12-04 19:56:07');

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL COMMENT '数量',
  `unit_price` decimal(10, 2) NOT NULL COMMENT '单价',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_items
-- ----------------------------

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单类型：ticket/product',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'paid' COMMENT '订单状态：paid/shipped/completed/cancelled',
  `shipping_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收货地址',
  `tracking_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物流单号',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_order_type`(`order_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------

-- ----------------------------
-- Table structure for points_exchanges
-- ----------------------------
DROP TABLE IF EXISTS `points_exchanges`;
CREATE TABLE `points_exchanges`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '兑换订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `mall_item_id` bigint NOT NULL COMMENT '商城商品ID',
  `points_used` int NOT NULL COMMENT '使用积分',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending' COMMENT '状态：pending/approved/shipped/completed/rejected',
  `shipping_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收货地址',
  `tracking_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物流单号',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_mall_item_id`(`mall_item_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `points_exchanges_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `points_exchanges_ibfk_2` FOREIGN KEY (`mall_item_id`) REFERENCES `points_mall` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '积分兑换订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_exchanges
-- ----------------------------

-- ----------------------------
-- Table structure for points_history
-- ----------------------------
DROP TABLE IF EXISTS `points_history`;
CREATE TABLE `points_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `change_amount` int NOT NULL COMMENT '变动积分',
  `change_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '变动类型：checkin/purchase/exchange/manual_adjust',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联的任务/订单ID',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_change_type`(`change_type` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `points_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '积分流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_history
-- ----------------------------

-- ----------------------------
-- Table structure for points_mall
-- ----------------------------
DROP TABLE IF EXISTS `points_mall`;
CREATE TABLE `points_mall`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '商品描述',
  `images` json NULL COMMENT '商品图片URL列表（JSON数组）',
  `points_required` int NOT NULL COMMENT '所需积分',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品类型：entity/virtual',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存',
  `countdown_end_time` datetime NULL DEFAULT NULL COMMENT '倒计时结束时间',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否上架',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_is_active`(`is_active` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '积分商城商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_mall
-- ----------------------------
INSERT INTO `points_mall` VALUES (1, '音乐节限定T恤', '积分兑换专属T恤', NULL, 500, 'entity', 100, NULL, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `points_mall` VALUES (2, 'VIP演唱会门票折扣券', '享受VIP票价9折优惠', NULL, 1000, 'virtual', 50, NULL, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `points_mall` VALUES (3, '沈阳美食代金券', '沈阳特色美食代金券100元', NULL, 800, 'virtual', 200, NULL, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `points_mall` VALUES (4, '音乐节周边礼盒', '精美周边礼盒套装', NULL, 1200, 'entity', 50, NULL, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `points_mall` VALUES (5, '演唱会现场优先入场券', '优先入场权益', NULL, 600, 'virtual', 100, NULL, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `points_mall` VALUES (6, '音乐节限定T恤', '积分兑换专属T恤', NULL, 500, 'entity', 100, NULL, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `points_mall` VALUES (7, 'VIP演唱会门票折扣券', '享受VIP票价9折优惠', NULL, 1000, 'virtual', 50, NULL, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `points_mall` VALUES (8, '沈阳美食代金券', '沈阳特色美食代金券100元', NULL, 800, 'virtual', 200, NULL, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `points_mall` VALUES (9, '音乐节周边礼盒', '精美周边礼盒套装', NULL, 1200, 'entity', 50, NULL, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `points_mall` VALUES (10, '演唱会现场优先入场券', '优先入场权益', NULL, 600, 'virtual', 100, NULL, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');

-- ----------------------------
-- Table structure for product_categories
-- ----------------------------
DROP TABLE IF EXISTS `product_categories`;
CREATE TABLE `product_categories`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '分类描述',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_categories
-- ----------------------------
INSERT INTO `product_categories` VALUES (1, 'T恤', '音乐节主题T恤', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `product_categories` VALUES (2, '卫衣', '音乐节主题卫衣', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `product_categories` VALUES (3, '帽子', '音乐节主题帽子', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `product_categories` VALUES (4, '徽章', '音乐节纪念徽章', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `product_categories` VALUES (5, '周边', '其他周边商品', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `product_categories` VALUES (6, 'T恤', '音乐节主题T恤', '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `product_categories` VALUES (7, '卫衣', '音乐节主题卫衣', '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `product_categories` VALUES (8, '帽子', '音乐节主题帽子', '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `product_categories` VALUES (9, '徽章', '音乐节纪念徽章', '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `product_categories` VALUES (10, '周边', '其他周边商品', '2025-12-04 19:56:08', '2025-12-04 19:56:08');

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '商品描述',
  `images` json NULL COMMENT '商品图片URL列表（JSON数组）',
  `original_price` decimal(10, 2) NOT NULL COMMENT '原价',
  `current_price` decimal(10, 2) NOT NULL COMMENT '现价',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存',
  `specs` json NULL COMMENT '规格信息（JSON对象）',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否上架',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_is_active`(`is_active` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `product_categories` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, 1, '2024沈阳音乐节纪念T恤', '高质量纯棉T恤，印有音乐节LOGO', NULL, 99.00, 79.00, 500, NULL, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `products` VALUES (2, 1, '五月天主题T恤', '五月天演唱会纪念T恤', NULL, 129.00, 99.00, 300, NULL, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `products` VALUES (3, 2, '音乐节限定卫衣', '舒适保暖的音乐节主题卫衣', NULL, 199.00, 159.00, 200, NULL, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `products` VALUES (4, 3, '沈阳音乐节棒球帽', '黑色棒球帽，刺绣LOGO', NULL, 79.00, 59.00, 400, NULL, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `products` VALUES (5, 4, '音乐节纪念徽章套装', '5枚精美徽章套装', NULL, 49.00, 39.00, 600, NULL, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `products` VALUES (6, 5, '音乐节手机壳', '防摔手机壳，多种机型', NULL, 59.00, 49.00, 350, NULL, 1, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `products` VALUES (7, 1, '2024沈阳音乐节纪念T恤', '高质量纯棉T恤，印有音乐节LOGO', NULL, 99.00, 79.00, 500, NULL, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `products` VALUES (8, 1, '五月天主题T恤', '五月天演唱会纪念T恤', NULL, 129.00, 99.00, 300, NULL, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `products` VALUES (9, 2, '音乐节限定卫衣', '舒适保暖的音乐节主题卫衣', NULL, 199.00, 159.00, 200, NULL, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `products` VALUES (10, 3, '沈阳音乐节棒球帽', '黑色棒球帽，刺绣LOGO', NULL, 79.00, 59.00, 400, NULL, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `products` VALUES (11, 4, '音乐节纪念徽章套装', '5枚精美徽章套装', NULL, 49.00, 39.00, 600, NULL, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');
INSERT INTO `products` VALUES (12, 5, '音乐节手机壳', '防摔手机壳，多种机型', NULL, 59.00, 49.00, 350, NULL, 1, '2025-12-04 19:56:09', '2025-12-04 19:56:09');

-- ----------------------------
-- Table structure for schedules
-- ----------------------------
DROP TABLE IF EXISTS `schedules`;
CREATE TABLE `schedules`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日程ID',
  `festival_id` bigint NOT NULL COMMENT '音乐节ID',
  `artist_id` bigint NOT NULL COMMENT '艺人ID',
  `stage_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '舞台名称',
  `start_time` datetime NOT NULL COMMENT '演出开始时间',
  `end_time` datetime NOT NULL COMMENT '演出结束时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_festival_id`(`festival_id` ASC) USING BTREE,
  INDEX `idx_artist_id`(`artist_id` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  CONSTRAINT `schedules_ibfk_1` FOREIGN KEY (`festival_id`) REFERENCES `festivals` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `schedules_ibfk_2` FOREIGN KEY (`artist_id`) REFERENCES `artists` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '演出日程表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of schedules
-- ----------------------------
INSERT INTO `schedules` VALUES (1, 1, 1, '主舞台', '2024-12-15 19:00:00', '2024-12-15 20:30:00', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `schedules` VALUES (2, 1, 2, '主舞台', '2024-12-15 21:00:00', '2024-12-15 22:30:00', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `schedules` VALUES (3, 1, 3, '副舞台', '2024-12-15 19:30:00', '2024-12-15 20:30:00', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `schedules` VALUES (4, 1, 4, '副舞台', '2024-12-15 21:00:00', '2024-12-15 22:00:00', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `schedules` VALUES (5, 1, 5, '主舞台', '2024-12-16 19:00:00', '2024-12-16 20:30:00', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `schedules` VALUES (6, 1, 6, '主舞台', '2024-12-16 21:00:00', '2024-12-16 22:30:00', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `schedules` VALUES (7, 1, 1, '主舞台', '2024-12-15 19:00:00', '2024-12-15 20:30:00', '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `schedules` VALUES (8, 1, 2, '主舞台', '2024-12-15 21:00:00', '2024-12-15 22:30:00', '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `schedules` VALUES (9, 1, 3, '副舞台', '2024-12-15 19:30:00', '2024-12-15 20:30:00', '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `schedules` VALUES (10, 1, 4, '副舞台', '2024-12-15 21:00:00', '2024-12-15 22:00:00', '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `schedules` VALUES (11, 1, 5, '主舞台', '2024-12-16 19:00:00', '2024-12-16 20:30:00', '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `schedules` VALUES (12, 1, 6, '主舞台', '2024-12-16 21:00:00', '2024-12-16 22:30:00', '2025-12-04 19:56:08', '2025-12-04 19:56:08');

-- ----------------------------
-- Table structure for ticket_sessions
-- ----------------------------
DROP TABLE IF EXISTS `ticket_sessions`;
CREATE TABLE `ticket_sessions`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '场次ID',
  `festival_id` bigint NOT NULL COMMENT '音乐节ID',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场次名称',
  `start_time` datetime NOT NULL COMMENT '场次开始时间',
  `end_time` datetime NOT NULL COMMENT '场次结束时间',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'available' COMMENT '状态：available/sold_out/ended',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_festival_id`(`festival_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  CONSTRAINT `ticket_sessions_ibfk_1` FOREIGN KEY (`festival_id`) REFERENCES `festivals` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '票务场次表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ticket_sessions
-- ----------------------------
INSERT INTO `ticket_sessions` VALUES (1, 1, '12月15日场次', '2024-12-15 18:00:00', '2024-12-15 23:00:00', 'available', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `ticket_sessions` VALUES (2, 1, '12月16日场次', '2024-12-16 18:00:00', '2024-12-16 23:00:00', 'available', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `ticket_sessions` VALUES (3, 1, '12月17日场次', '2024-12-17 18:00:00', '2024-12-17 23:00:00', 'available', '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `ticket_sessions` VALUES (4, 1, '12月15日场次', '2024-12-15 18:00:00', '2024-12-15 23:00:00', 'available', '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `ticket_sessions` VALUES (5, 1, '12月16日场次', '2024-12-16 18:00:00', '2024-12-16 23:00:00', 'available', '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `ticket_sessions` VALUES (6, 1, '12月17日场次', '2024-12-17 18:00:00', '2024-12-17 23:00:00', 'available', '2025-12-04 19:56:08', '2025-12-04 19:56:08');

-- ----------------------------
-- Table structure for ticket_zones
-- ----------------------------
DROP TABLE IF EXISTS `ticket_zones`;
CREATE TABLE `ticket_zones`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分区ID',
  `session_id` bigint NOT NULL COMMENT '场次ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分区名称（A区/B区/站区/VIP区）',
  `total_capacity` int NOT NULL COMMENT '总容纳人数',
  `sold_count` int NULL DEFAULT 0 COMMENT '已售数量',
  `price` decimal(10, 2) NOT NULL COMMENT '票价',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  CONSTRAINT `ticket_zones_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `ticket_sessions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '分区表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ticket_zones
-- ----------------------------
INSERT INTO `ticket_zones` VALUES (1, 1, 'A区', 1000, 0, 380.00, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `ticket_zones` VALUES (2, 1, 'B区', 1500, 0, 280.00, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `ticket_zones` VALUES (3, 1, '站区', 2000, 0, 180.00, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `ticket_zones` VALUES (4, 1, 'VIP区', 500, 0, 680.00, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `ticket_zones` VALUES (5, 2, 'A区', 1000, 0, 380.00, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `ticket_zones` VALUES (6, 2, 'B区', 1500, 0, 280.00, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `ticket_zones` VALUES (7, 2, '站区', 2000, 0, 180.00, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `ticket_zones` VALUES (8, 2, 'VIP区', 500, 0, 680.00, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `ticket_zones` VALUES (9, 3, 'A区', 1000, 0, 380.00, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `ticket_zones` VALUES (10, 3, 'B区', 1500, 0, 280.00, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `ticket_zones` VALUES (11, 3, '站区', 2000, 0, 180.00, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `ticket_zones` VALUES (12, 3, 'VIP区', 500, 0, 680.00, '2025-12-04 19:02:59', '2025-12-04 19:02:59');
INSERT INTO `ticket_zones` VALUES (13, 1, 'A区', 1000, 0, 380.00, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `ticket_zones` VALUES (14, 1, 'B区', 1500, 0, 280.00, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `ticket_zones` VALUES (15, 1, '站区', 2000, 0, 180.00, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `ticket_zones` VALUES (16, 1, 'VIP区', 500, 0, 680.00, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `ticket_zones` VALUES (17, 2, 'A区', 1000, 0, 380.00, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `ticket_zones` VALUES (18, 2, 'B区', 1500, 0, 280.00, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `ticket_zones` VALUES (19, 2, '站区', 2000, 0, 180.00, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `ticket_zones` VALUES (20, 2, 'VIP区', 500, 0, 680.00, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `ticket_zones` VALUES (21, 3, 'A区', 1000, 0, 380.00, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `ticket_zones` VALUES (22, 3, 'B区', 1500, 0, 280.00, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `ticket_zones` VALUES (23, 3, '站区', 2000, 0, 180.00, '2025-12-04 19:56:08', '2025-12-04 19:56:08');
INSERT INTO `ticket_zones` VALUES (24, 3, 'VIP区', 500, 0, 680.00, '2025-12-04 19:56:08', '2025-12-04 19:56:08');

-- ----------------------------
-- Table structure for tickets
-- ----------------------------
DROP TABLE IF EXISTS `tickets`;
CREATE TABLE `tickets`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '电子票ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `session_id` bigint NOT NULL COMMENT '场次ID',
  `zone_id` bigint NOT NULL COMMENT '分区ID',
  `buyer_id_number` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '购票人身份证号',
  `buyer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '购票人姓名',
  `qr_code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '二维码URL',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'valid' COMMENT '状态：valid/used/expired',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_session_id_number`(`session_id` ASC, `buyer_id_number` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_zone_id`(`zone_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `tickets_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `tickets_ibfk_2` FOREIGN KEY (`session_id`) REFERENCES `ticket_sessions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `tickets_ibfk_3` FOREIGN KEY (`zone_id`) REFERENCES `ticket_zones` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '电子票表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tickets
-- ----------------------------

-- ----------------------------
-- Table structure for transport_info
-- ----------------------------
DROP TABLE IF EXISTS `transport_info`;
CREATE TABLE `transport_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '交通信息ID',
  `festival_id` bigint NOT NULL COMMENT '音乐节ID',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '交通类型：subway/bus/parking/guide',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '描述',
  `details` json NULL COMMENT '详细信息（JSON对象）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_festival_id`(`festival_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  CONSTRAINT `transport_info_ibfk_1` FOREIGN KEY (`festival_id`) REFERENCES `festivals` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '交通信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of transport_info
-- ----------------------------
INSERT INTO `transport_info` VALUES (1, 1, 'subway', '地铁路线', '推荐乘坐地铁1号线、2号线到达奥体中心', NULL, '2025-12-04 19:03:00', '2025-12-04 19:03:00');
INSERT INTO `transport_info` VALUES (2, 1, 'bus', '公交路线', '多条公交线路可到达奥体中心', NULL, '2025-12-04 19:03:00', '2025-12-04 19:03:00');
INSERT INTO `transport_info` VALUES (3, 1, 'parking', '停车攻略', '奥体中心周边有多个停车场，建议提前预订', NULL, '2025-12-04 19:03:00', '2025-12-04 19:03:00');
INSERT INTO `transport_info` VALUES (4, 1, 'guide', '入场须知', '请携带有效身份证件，配合安检检查', NULL, '2025-12-04 19:03:00', '2025-12-04 19:03:00');

-- ----------------------------
-- Table structure for user_collections
-- ----------------------------
DROP TABLE IF EXISTS `user_collections`;
CREATE TABLE `user_collections`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_article`(`user_id` ASC, `article_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_article_id`(`article_id` ASC) USING BTREE,
  CONSTRAINT `user_collections_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_collections_ibfk_2` FOREIGN KEY (`article_id`) REFERENCES `articles` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_collections
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密存储）',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `id_number` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号（用于防黄牛）',
  `is_real_name_verified` tinyint(1) NULL DEFAULT 0 COMMENT '是否完成实名认证',
  `points` bigint NULL DEFAULT 0 COMMENT '积分余额',
  `is_blocked` tinyint(1) NULL DEFAULT 0 COMMENT '是否被封禁',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'USER' COMMENT '用户角色',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `id_number`(`id_number` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_id_number`(`id_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, '15071469547', '$2a$10$gJc4..x9Ax2.OJivl//aFO4lbchnEY8T2Q00iwIZtIKfVEU5SDtA2', NULL, NULL, NULL, NULL, NULL, 0, 0, 0, '2025-12-04 12:20:35', '2025-12-04 12:20:35', 'USER');
INSERT INTO `users` VALUES (2, '17665231453', '$2a$10$fTwk4xtqrhYJBmtNGZwToO6O8dM5kC1zDGLxO/tZKTch8ipnZWORq', NULL, NULL, NULL, NULL, NULL, 0, 0, 0, '2025-12-04 12:22:39', '2025-12-04 20:25:18', 'admin');

-- ----------------------------
-- Table structure for weather_info
-- ----------------------------
DROP TABLE IF EXISTS `weather_info`;
CREATE TABLE `weather_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '天气ID',
  `festival_id` bigint NOT NULL COMMENT '音乐节ID',
  `date` date NOT NULL COMMENT '日期',
  `temperature` int NULL DEFAULT NULL COMMENT '温度（摄氏度）',
  `humidity` int NULL DEFAULT NULL COMMENT '湿度（百分比）',
  `wind_speed` int NULL DEFAULT NULL COMMENT '风速（km/h）',
  `weather_description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '天气描述',
  `clothing_suggestion` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '穿衣建议',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_festival_date`(`festival_id` ASC, `date` ASC) USING BTREE,
  INDEX `idx_festival_id`(`festival_id` ASC) USING BTREE,
  INDEX `idx_date`(`date` ASC) USING BTREE,
  CONSTRAINT `weather_info_ibfk_1` FOREIGN KEY (`festival_id`) REFERENCES `festivals` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '天气信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of weather_info
-- ----------------------------
INSERT INTO `weather_info` VALUES (1, 1, '2024-12-15', 5, 60, 15, '晴天', '建议穿着厚重外套、羽绒服，戴帽子和手套', '2025-12-04 19:03:00', '2025-12-04 19:03:00');
INSERT INTO `weather_info` VALUES (2, 1, '2024-12-16', 3, 65, 18, '多云', '建议穿着厚重外套、羽绒服，戴帽子和手套', '2025-12-04 19:03:00', '2025-12-04 19:03:00');
INSERT INTO `weather_info` VALUES (3, 1, '2024-12-17', 2, 70, 20, '晴天', '建议穿着厚重外套、羽绒服，戴帽子和手套', '2025-12-04 19:03:00', '2025-12-04 19:03:00');

SET FOREIGN_KEY_CHECKS = 1;
