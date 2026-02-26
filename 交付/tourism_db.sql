/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : tourism_db

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 26/02/2026 11:24:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告内容',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcements
-- ----------------------------
INSERT INTO `announcements` VALUES (1, '新春佳节来广州旅游把', '新春佳节来广州旅游把新春佳节来广州旅游把新春佳节来广州旅游把新春佳节来广州旅游把新春佳节来广州旅游把', 1, '2026-02-26 01:20:40', '2026-02-26 01:20:40');
INSERT INTO `announcements` VALUES (2, '春节来广州', '春节来广州春节来广州春节来广州春节来广州春节来广州', 1, '2026-02-26 03:13:15', '2026-02-26 03:13:15');

-- ----------------------------
-- Table structure for attraction_tags
-- ----------------------------
DROP TABLE IF EXISTS `attraction_tags`;
CREATE TABLE `attraction_tags`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `attraction_id` bigint NOT NULL COMMENT '景点ID',
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_attraction_id`(`attraction_id` ASC) USING BTREE,
  INDEX `idx_tag_name`(`tag_name` ASC) USING BTREE,
  CONSTRAINT `attraction_tags_ibfk_1` FOREIGN KEY (`attraction_id`) REFERENCES `attractions` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '景点标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attraction_tags
-- ----------------------------
INSERT INTO `attraction_tags` VALUES (1, 1, '文化', '2026-02-25 21:42:29');
INSERT INTO `attraction_tags` VALUES (2, 1, '历史', '2026-02-25 21:42:29');
INSERT INTO `attraction_tags` VALUES (3, 2, '美食', '2026-02-25 21:42:29');
INSERT INTO `attraction_tags` VALUES (4, 2, '夜景', '2026-02-25 21:42:29');
INSERT INTO `attraction_tags` VALUES (5, 3, '现代', '2026-02-25 21:42:29');
INSERT INTO `attraction_tags` VALUES (6, 3, '建筑', '2026-02-25 21:42:29');
INSERT INTO `attraction_tags` VALUES (7, 4, '公园', '2026-02-25 21:42:29');
INSERT INTO `attraction_tags` VALUES (8, 4, '休闲', '2026-02-25 21:42:29');
INSERT INTO `attraction_tags` VALUES (9, 5, '历史', '2026-02-25 21:42:29');
INSERT INTO `attraction_tags` VALUES (10, 5, '文化', '2026-02-25 21:42:29');
INSERT INTO `attraction_tags` VALUES (11, 6, '自然', '2026-02-25 21:42:29');
INSERT INTO `attraction_tags` VALUES (12, 6, '生态', '2026-02-25 21:42:29');
INSERT INTO `attraction_tags` VALUES (13, 7, '自然', '2026-02-25 21:42:29');
INSERT INTO `attraction_tags` VALUES (14, 7, '休闲', '2026-02-25 21:42:29');
INSERT INTO `attraction_tags` VALUES (15, 8, '动物', '2026-02-25 21:42:29');
INSERT INTO `attraction_tags` VALUES (16, 8, '家庭', '2026-02-25 21:42:29');

-- ----------------------------
-- Table structure for attractions
-- ----------------------------
DROP TABLE IF EXISTS `attractions`;
CREATE TABLE `attractions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '景点名称',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '景点描述',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '位置',
  `ticket_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '门票价格',
  `opening_hours` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '营业时间',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '景点图片',
  `is_guangzhou_special` tinyint(1) NULL DEFAULT 0 COMMENT '是否为广州特色',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_guangzhou_special`(`is_guangzhou_special` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '景点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attractions
-- ----------------------------
INSERT INTO `attractions` VALUES (1, '陈家祠00', '陈家祠是广东现存规模最大、保存最完整的中国传统民居建筑群，是岭南建筑艺术的典范。', '广州市荔湾区中山七路', 10.00, '09:00-17:00', '/images/011198f3-3175-462e-a9b6-6a0363f8e34f.jpg', 1, '2026-02-25 21:42:29', '2026-02-26 03:10:00');
INSERT INTO `attractions` VALUES (2, '珠江夜游', '乘坐游船欣赏珠江两岸的夜景，感受广州的现代魅力。', '广州市越秀区珠江', 80.00, '19:00-22:00', '/images/ae5a235a-ead8-4311-9663-454170136007.jpg', 1, '2026-02-25 21:42:29', '2026-02-26 02:04:30');
INSERT INTO `attractions` VALUES (3, '广州塔', '广州塔是中国第一高塔，可俯瞰整个广州城市风景。', '广州市海珠区阅江西路', 150.00, '09:00-23:00', '/images/4d156f43-d6bc-4c7e-a9cc-0a7456407fa6.jpg', 1, '2026-02-25 21:42:29', '2026-02-26 02:04:46');
INSERT INTO `attractions` VALUES (4, '越秀公园', '越秀公园是广州最大的城市公园，有五羊雕像等著名景观。', '广州市越秀区解放北路', 5.00, '06:00-21:00', '/images/d7f710df-f6bc-4e7e-91eb-ffd2beda3aba.jpg', 1, '2026-02-25 21:42:29', '2026-02-26 02:04:54');
INSERT INTO `attractions` VALUES (5, '黄埔古港', '黄埔古港是广州对外贸易的重要港口，见证了广州的商业历史。', '广州市黄埔区黄埔东路', 20.00, '09:00-17:00', '/images/245a9e40-35b8-4ee3-b66f-3d1e8bf0a969.jpg', 1, '2026-02-25 21:42:29', '2026-02-26 02:05:02');
INSERT INTO `attractions` VALUES (6, '南沙湿地公园', '南沙湿地公园是广州最大的湿地公园，有丰富的野生动物。', '广州市南沙区万顷沙镇', 30.00, '08:00-18:00', '/images/b945df6a-07b3-414c-a854-fdf7bb3f67ab.jpg', 0, '2026-02-25 21:42:29', '2026-02-26 02:05:08');
INSERT INTO `attractions` VALUES (7, '白云山', '白云山是广州的城市绿肺，有多条登山步道和观景台。', '广州市白云区白云山', 5.00, '06:00-21:00', '/images/bc29ac54-263b-4e9e-92ed-3a0404937330.jpg', 1, '2026-02-25 21:42:29', '2026-02-26 02:05:16');
INSERT INTO `attractions` VALUES (8, '长隆野生动物园', '长隆野生动物园是中国最大的野生动物园，有众多珍稀动物。', '广州市番禺区大石镇', 250.00, '09:30-18:00', '/images/92aa5b69-1992-4e41-8725-45f7ac14dd08.jpg', 0, '2026-02-25 21:42:29', '2026-02-26 02:05:32');
INSERT INTO `attractions` VALUES (9, '番禺大夫山', '番禺大夫山番禺大夫山番禺大夫山番禺大夫山', '番禺区', 50.00, '09：00-18：00', '/images/3bf99cd2-6f93-4f3f-8c37-41feef1e8b77.jpg', 1, '2026-02-26 02:06:15', '2026-02-26 02:06:15');
INSERT INTO `attractions` VALUES (10, '广州塔00', '广州塔广州塔广州塔广州塔广州塔广州塔广州塔', '广州白云区', 50.00, '09：00-18：00', '/images/260a37f2-1d53-4360-831b-af3462822158.jpg', 1, '2026-02-26 03:10:34', '2026-02-26 03:10:34');

-- ----------------------------
-- Table structure for browsing_history
-- ----------------------------
DROP TABLE IF EXISTS `browsing_history`;
CREATE TABLE `browsing_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标类型: attraction, hotel, product',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `browsing_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '浏览历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of browsing_history
-- ----------------------------

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标类型: attraction, hotel',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '留言内容',
  `rating` int NULL DEFAULT NULL COMMENT '评分: 1-5',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '状态: pending, approved, rejected',
  `is_pinned` tinyint(1) NULL DEFAULT 0 COMMENT '是否置顶',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_target_type_id`(`target_type` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_is_pinned`(`is_pinned` ASC) USING BTREE,
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '留言表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comments
-- ----------------------------
INSERT INTO `comments` VALUES (1, 1, 'hotel', 1, '这个酒店太好了', 5, 'approved', 0, '2026-02-26 02:37:44', '2026-02-26 03:13:00');

-- ----------------------------
-- Table structure for favorites
-- ----------------------------
DROP TABLE IF EXISTS `favorites`;
CREATE TABLE `favorites`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标类型: attraction, hotel, product',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_favorite`(`user_id` ASC, `target_type` ASC, `target_id` ASC) USING BTREE,
  UNIQUE INDEX `idx_unique_favorite`(`user_id` ASC, `target_type` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_target_type_id`(`target_type` ASC, `target_id` ASC) USING BTREE,
  CONSTRAINT `favorites_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorites
-- ----------------------------

-- ----------------------------
-- Table structure for hotel_rooms
-- ----------------------------
DROP TABLE IF EXISTS `hotel_rooms`;
CREATE TABLE `hotel_rooms`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `hotel_id` bigint NOT NULL COMMENT '酒店ID',
  `room_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房间类型',
  `price_per_night` decimal(10, 2) NOT NULL COMMENT '每晚价格',
  `quantity` int NOT NULL COMMENT '房间数量',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_hotel_id`(`hotel_id` ASC) USING BTREE,
  CONSTRAINT `hotel_rooms_ibfk_1` FOREIGN KEY (`hotel_id`) REFERENCES `hotels` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '酒店房间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hotel_rooms
-- ----------------------------
INSERT INTO `hotel_rooms` VALUES (1, 1, '豪华套房', 1500.00, 10, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (2, 1, '标准间', 800.00, 30, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (3, 1, '商务间', 600.00, 20, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (4, 2, '豪华套房', 1200.00, 8, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (5, 2, '标准间', 700.00, 25, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (6, 2, '经济间', 500.00, 15, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (7, 3, '标准间', 500.00, 40, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (8, 3, '商务间', 400.00, 30, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (9, 3, '经济间', 300.00, 20, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (10, 4, '家庭房', 800.00, 15, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (11, 4, '标准间', 600.00, 35, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (12, 4, '经济间', 400.00, 25, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (13, 5, '标准间', 400.00, 30, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (14, 5, '经济间', 250.00, 40, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (15, 6, '商务间', 450.00, 25, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (16, 6, '标准间', 350.00, 35, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (17, 6, '经济间', 250.00, 30, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotel_rooms` VALUES (18, 7, '单人间', 5.00, 1, '2026-02-26 01:19:25', '2026-02-26 01:19:25');
INSERT INTO `hotel_rooms` VALUES (19, 1, '家庭套房', 10000.00, 1, '2026-02-26 03:11:28', '2026-02-26 03:11:28');

-- ----------------------------
-- Table structure for hotels
-- ----------------------------
DROP TABLE IF EXISTS `hotels`;
CREATE TABLE `hotels`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '酒店名称',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '位置',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '酒店描述',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '酒店图片',
  `rating` decimal(3, 1) NULL DEFAULT NULL COMMENT '评分',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_location`(`location` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '酒店表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hotels
-- ----------------------------
INSERT INTO `hotels` VALUES (1, '广州白天鹅宾馆', '广州市越秀区环市东路00', '五星级豪华酒店，位于珠江河畔，拥有优美的江景和完善的设施。', '/images/9f7c84f2-5e2d-4083-9671-35e2a71e8859.jpg', 5.0, '2026-02-25 21:42:29', '2026-02-26 02:13:09');
INSERT INTO `hotels` VALUES (2, '广州香格里拉大酒店', '广州市越秀区中山一路', '五星级国际酒店，提供高端服务和舒适的住宿环境。', '/images/e3e0ea91-d71e-43d1-b380-ed23163ec93e.jpg', 4.8, '2026-02-25 21:42:29', '2026-02-26 03:12:08');
INSERT INTO `hotels` VALUES (3, '广州花园酒店', '广州市越秀区中山一路', '四星级商务酒店，地理位置优越，靠近商业中心。', 'https://via.placeholder.com/300x200?text=花园酒店', 4.5, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotels` VALUES (4, '广州长隆酒店', '广州市番禺区大石镇', '四星级主题酒店，靠近长隆野生动物园，适合家庭旅游。', 'https://via.placeholder.com/300x200?text=长隆酒店', 4.6, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotels` VALUES (5, '广州南沙假日酒店', '广州市南沙区万顷沙镇', '三星级度假酒店，环境优美，靠近南沙湿地公园。', 'https://via.placeholder.com/300x200?text=南沙假日', 4.2, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotels` VALUES (6, '广州天河城市酒店', '广州市天河区天河路', '四星级商务酒店，靠近购物中心和商业区。', 'https://via.placeholder.com/300x200?text=天河城市', 4.4, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `hotels` VALUES (7, '广州大酒店', '番禺区', '这个酒店不行', 'jiudian.jpg', 1.0, '2026-02-26 01:19:11', '2026-02-26 01:19:11');
INSERT INTO `hotels` VALUES (8, '广州大酒店22', '白云区', '广州大酒店广州大酒店广州大酒店广州大酒店', '/images/fa313d7a-e672-43a6-a968-c89405d90958.jpg', 5.0, '2026-02-26 02:27:35', '2026-02-26 02:27:35');
INSERT INTO `hotels` VALUES (9, '广州豪华酒店', '海珠区', '广州豪华酒店广州豪华酒店广州豪华酒店广州豪华酒店', '/images/bd62712f-9c4b-464c-8a7f-f2c1c7d99df4.jpg', 5.0, '2026-02-26 03:12:39', '2026-02-26 03:12:39');

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目类型: attraction, hotel, product',
  `item_id` bigint NOT NULL COMMENT '项目ID',
  `quantity` int NOT NULL COMMENT '数量',
  `unit_price` decimal(10, 2) NOT NULL COMMENT '单价',
  `subtotal` decimal(10, 2) NOT NULL COMMENT '小计',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_items
-- ----------------------------
INSERT INTO `order_items` VALUES (1, 1, 'attraction', 1, 2, 10.00, 20.00, '2026-02-26 01:39:15');
INSERT INTO `order_items` VALUES (2, 2, 'product', 1, 1, 128.00, 128.00, '2026-02-26 03:00:27');
INSERT INTO `order_items` VALUES (3, 3, 'attraction', 1, 4, 10.00, 40.00, '2026-02-26 03:14:19');
INSERT INTO `order_items` VALUES (4, 4, 'hotel', 2, 1, 700.00, 700.00, '2026-02-26 03:14:40');
INSERT INTO `order_items` VALUES (5, 5, 'product', 1, 2, 129.00, 258.00, '2026-02-26 03:14:52');
INSERT INTO `order_items` VALUES (6, 6, 'product', 1, 1, 129.00, 129.00, '2026-02-26 03:22:14');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_price` decimal(10, 2) NOT NULL COMMENT '总价格',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '状态: pending, confirmed, completed, cancelled',
  `order_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单类型: attraction, hotel, product',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_number`(`order_number` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_order_number`(`order_number` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'ORD202602260939142C3313D5', 1, 20.00, 'confirmed', 'attraction', '2026-02-26 01:39:15', '2026-02-26 02:07:54');
INSERT INTO `orders` VALUES (2, 'ORD20260226110027EFD413EE', 1, 128.00, 'confirmed', 'product', '2026-02-26 03:00:27', '2026-02-26 03:12:52');
INSERT INTO `orders` VALUES (3, 'ORD20260226111419BEFB9332', 1, 40.00, 'pending', 'attraction', '2026-02-26 03:14:19', '2026-02-26 03:14:19');
INSERT INTO `orders` VALUES (4, 'ORD20260226111439205F6E1D', 1, 700.00, 'pending', 'hotel', '2026-02-26 03:14:40', '2026-02-26 03:14:40');
INSERT INTO `orders` VALUES (5, 'ORD20260226111452ABA4C7D6', 1, 258.00, 'pending', 'product', '2026-02-26 03:14:52', '2026-02-26 03:14:52');
INSERT INTO `orders` VALUES (6, 'ORD2026022611221340F34CC6', 1, 129.00, 'pending', 'product', '2026-02-26 03:22:14', '2026-02-26 03:22:14');

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '商品描述',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `stock` int NOT NULL COMMENT '库存',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品图片',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类',
  `is_guangzhou_special` tinyint(1) NULL DEFAULT 0 COMMENT '是否为广州特色',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_guangzhou_special`(`is_guangzhou_special` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '旅游商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, '广州特色茶叶', '正宗广州普洱茶，选用云南优质茶叶，经过传统工艺精心制作，香气浓郁，回甘悠长。', 129.00, 47, '/images/969bbad4-fae1-4db9-b145-513d6b1007e0.png', '美食', 1, '2026-02-25 21:42:29', '2026-02-26 03:22:14');
INSERT INTO `products` VALUES (2, '陶瓷工艺品', '手工制作的陶瓷工艺品，采用传统广东陶瓷工艺，每件都是独一无二的艺术品。', 89.00, 30, 'https://via.placeholder.com/300x200?text=陶瓷', '手工艺品', 1, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `products` VALUES (3, '广州特色糕点', '传统广州糕点，包括蛋挞、老婆饼等，采用传统配方，口感酥脆，甜而不腻。', 45.00, 100, 'https://via.placeholder.com/300x200?text=糕点', '美食', 1, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `products` VALUES (4, '丝绸纪念品', '精美的丝绸纪念品，采用广东特色丝绸面料，设计精美，是旅游纪念的最佳选择。', 68.00, 40, 'https://via.placeholder.com/300x200?text=丝绸', '纪念品', 1, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `products` VALUES (5, '广州特色咸鱼', '传统腌制咸鱼，选用新鲜鱼类，采用传统腌制工艺，咸香适中，是家庭必备的美食。', 35.00, 60, 'https://via.placeholder.com/300x200?text=咸鱼', '美食', 1, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `products` VALUES (6, '手工扇子', '精美的手工扇子，采用竹子和丝绸制作，设计精美，是广州传统工艺的代表。', 55.00, 25, 'https://via.placeholder.com/300x200?text=扇子', '手工艺品', 1, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `products` VALUES (7, '广州特色酱料', '传统广州酱料，包括豆酱、虾酱等，采用传统配方，风味独特，是烹饪的好帮手。', 28.00, 80, 'https://via.placeholder.com/300x200?text=酱料', '美食', 1, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `products` VALUES (8, '木雕工艺品', '精美的木雕工艺品，采用传统木雕工艺，雕工精细，是收藏和送礼的佳品。', 120.00, 15, 'https://via.placeholder.com/300x200?text=木雕', '手工艺品', 0, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `products` VALUES (9, '广州特色蜜饯', '传统蜜饯，采用新鲜水果和蜂蜜制作，甜蜜可口，是旅游必买的特产。', 32.00, 70, 'https://via.placeholder.com/300x200?text=蜜饯', '美食', 1, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `products` VALUES (10, '景泰蓝工艺品', '精美的景泰蓝工艺品，采用传统景泰蓝工艺，色彩艳丽，是艺术品的典范。', 150.00, 10, 'https://via.placeholder.com/300x200?text=景泰蓝', '手工艺品', 0, '2026-02-25 21:42:29', '2026-02-25 21:42:29');
INSERT INTO `products` VALUES (11, '广州早茶套餐包', '广州早茶套餐包广州早茶套餐包广州早茶套餐包广州早茶套餐包', 100.00, 40, '/images/be8475aa-2a7b-4c96-898f-69867a8048f5.jpg', '美食', 1, '2026-02-26 03:09:16', '2026-02-26 03:09:16');

-- ----------------------------
-- Table structure for route_items
-- ----------------------------
DROP TABLE IF EXISTS `route_items`;
CREATE TABLE `route_items`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `route_id` bigint NOT NULL COMMENT '路线ID',
  `day_number` int NOT NULL COMMENT '第几天',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目类型: attraction, hotel',
  `item_id` bigint NOT NULL COMMENT '项目ID',
  `sequence` int NOT NULL COMMENT '顺序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_route_id`(`route_id` ASC) USING BTREE,
  INDEX `idx_route_day`(`route_id` ASC, `day_number` ASC) USING BTREE,
  CONSTRAINT `route_items_ibfk_1` FOREIGN KEY (`route_id`) REFERENCES `routes` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '路线项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of route_items
-- ----------------------------
INSERT INTO `route_items` VALUES (1, 1, 1, 'attraction', 1, 0, '2026-02-26 01:21:23');
INSERT INTO `route_items` VALUES (2, 1, 2, 'attraction', 2, 1, '2026-02-26 01:21:35');
INSERT INTO `route_items` VALUES (3, 2, 1, 'hotel', 1, 0, '2026-02-26 03:13:56');

-- ----------------------------
-- Table structure for routes
-- ----------------------------
DROP TABLE IF EXISTS `routes`;
CREATE TABLE `routes`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '路线名称',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '路线描述',
  `duration_days` int NOT NULL COMMENT '天数',
  `total_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '总价格',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '旅游路线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of routes
-- ----------------------------
INSERT INTO `routes` VALUES (1, '番禺都珠江新城', '番禺都珠江新城番禺都珠江新城番禺都珠江新城番禺都珠江新城', 3, 1000.00, '2026-02-26 01:21:05', '2026-02-26 01:21:05');
INSERT INTO `routes` VALUES (2, '番禺到广州塔', '春节来广州春节来广州春节来广州', 1, 100.00, '2026-02-26 03:13:41', '2026-02-26 03:13:41');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'tourist' COMMENT '角色: tourist 或 admin',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '状态: active 或 disabled',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'guangzhou', '123456', '123456@qq.com', '15071469547', NULL, 'tourist', 'active', '2026-02-25 15:26:27', '2026-02-25 15:26:27');
INSERT INTO `users` VALUES (2, 'admin', 'admin123', 'admin@qq.com', '15071469546', NULL, 'admin', 'active', '2026-02-26 09:16:33', '2026-02-26 09:17:00');

SET FOREIGN_KEY_CHECKS = 1;
