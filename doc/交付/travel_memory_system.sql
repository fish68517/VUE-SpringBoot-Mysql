/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : travel_memory_system

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 11/12/2025 20:49:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `travel_record_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_travel_record_id`(`travel_record_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`travel_record_id`) REFERENCES `travel_records` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comments
-- ----------------------------
INSERT INTO `comments` VALUES (1, 1, 2, '樱花也太美了！求同款机位！', '2025-12-10 13:23:19', '2025-12-10 13:23:19');
INSERT INTO `comments` VALUES (2, 1, 3, '上野那张逆光樱花绝了！什么相机？', '2025-12-10 13:23:19', '2025-12-10 13:23:19');
INSERT INTO `comments` VALUES (3, 3, 1, '曼谷路边摊真的yyds！我也吃到撑', '2025-12-10 13:23:19', '2025-12-10 13:23:19');
INSERT INTO `comments` VALUES (4, 5, 6, '川西真的绝美！国庆人会很多吗？', '2025-12-10 13:23:19', '2025-12-10 13:23:19');
INSERT INTO `comments` VALUES (5, 1, 1, '太好了', '2025-12-10 15:29:10', '2025-12-10 15:29:10');
INSERT INTO `comments` VALUES (6, 2, 1, '哦酷酷酷', '2025-12-10 15:29:22', '2025-12-10 15:29:22');
INSERT INTO `comments` VALUES (7, 18, 1, '222', '2025-12-11 12:38:40', '2025-12-11 12:38:40');

-- ----------------------------
-- Table structure for itinerary_items
-- ----------------------------
DROP TABLE IF EXISTS `itinerary_items`;
CREATE TABLE `itinerary_items`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plan_id` bigint NOT NULL,
  `item_date` date NOT NULL,
  `item_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_item_date`(`item_date` ASC) USING BTREE,
  CONSTRAINT `itinerary_items_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `travel_plans` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of itinerary_items
-- ----------------------------
INSERT INTO `itinerary_items` VALUES (1, 1, '2025-03-25', 'flight', '北京→东京羽田', '红眼航班春秋航空', '日本·东京', '2025-12-10 13:23:18');
INSERT INTO `itinerary_items` VALUES (2, 1, '2025-03-26', 'accommodation', '上野胶囊酒店3晚', '省钱大法', '日本·东京上野', '2025-12-10 13:23:18');
INSERT INTO `itinerary_items` VALUES (3, 2, '2025-07-01', 'flight', '广州→曼谷', '亚航特价票', '泰国·曼谷', '2025-12-10 13:23:18');
INSERT INTO `itinerary_items` VALUES (4, 3, '2025-10-01', 'transportation', '成都租车出发', '开启川藏线', '四川·成都', '2025-12-10 13:23:18');

-- ----------------------------
-- Table structure for likes
-- ----------------------------
DROP TABLE IF EXISTS `likes`;
CREATE TABLE `likes`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `travel_record_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_like`(`travel_record_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_travel_record_id`(`travel_record_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `likes_ibfk_1` FOREIGN KEY (`travel_record_id`) REFERENCES `travel_records` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `likes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of likes
-- ----------------------------

-- ----------------------------
-- Table structure for map_footprints
-- ----------------------------
DROP TABLE IF EXISTS `map_footprints`;
CREATE TABLE `map_footprints`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `travel_record_id` bigint NOT NULL,
  `location_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `latitude` decimal(10, 8) NOT NULL,
  `longitude` decimal(11, 8) NOT NULL,
  `visit_date` date NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_travel_record_id`(`travel_record_id` ASC) USING BTREE,
  CONSTRAINT `map_footprints_ibfk_1` FOREIGN KEY (`travel_record_id`) REFERENCES `travel_records` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of map_footprints
-- ----------------------------
INSERT INTO `map_footprints` VALUES (1, 1, '上野公园', 35.71282700, 139.77459200, '2024-03-26', '2025-12-10 13:23:18');
INSERT INTO `map_footprints` VALUES (2, 1, '浅草寺', 35.71481900, 139.79666100, '2024-03-27', '2025-12-10 13:23:18');
INSERT INTO `map_footprints` VALUES (3, 3, '曼谷大皇宫', 13.75633100, 100.49153100, '2024-07-11', '2025-12-10 13:23:18');

-- ----------------------------
-- Table structure for multimedia_files
-- ----------------------------
DROP TABLE IF EXISTS `multimedia_files`;
CREATE TABLE `multimedia_files`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `travel_record_id` bigint NOT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_size` bigint NOT NULL,
  `upload_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_travel_record_id`(`travel_record_id` ASC) USING BTREE,
  CONSTRAINT `multimedia_files_ibfk_1` FOREIGN KEY (`travel_record_id`) REFERENCES `travel_records` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of multimedia_files
-- ----------------------------
INSERT INTO `multimedia_files` VALUES (1, 1, '东京樱花雨.jpg', '/uploads/records/1/cherry-blossom-tokyo.jpg', 'image/jpeg', 2897345, '2025-12-10 23:41:16');
INSERT INTO `multimedia_files` VALUES (2, 2, '浅草寺夜景.jpg', '/uploads/records/2/sensoji-night.jpg', 'image/jpeg', 3124567, '2025-12-10 23:41:16');
INSERT INTO `multimedia_files` VALUES (3, 3, '曼谷街边小吃摊.mp4', '/uploads/records/3/bangkok-street-food.mp4', 'video/mp4', 68794321, '2025-12-10 23:41:16');
INSERT INTO `multimedia_files` VALUES (4, 5, '稻城亚丁牛奶海.jpg', '/uploads/records/5/aden-milk-lake.jpg', 'image/jpeg', 4235678, '2025-12-10 23:41:16');
INSERT INTO `multimedia_files` VALUES (5, 16, '微信图片_20251205112616.jpg', '/uploads/records/1/微信图片_20251205112616.jpg', 'jpg', 257618, '2025-12-11 09:50:53');
INSERT INTO `multimedia_files` VALUES (6, 17, '微信图片_20251205112610.jpg', '/uploads/records/1/微信图片_20251205112610.jpg', 'jpg', 274957, '2025-12-11 09:54:19');
INSERT INTO `multimedia_files` VALUES (7, 18, '微信图片_2025-12-05_112323_448.jpg', '/uploads/records/1/微信图片_2025-12-05_112323_448.jpg', 'jpg', 279363, '2025-12-11 10:06:50');
INSERT INTO `multimedia_files` VALUES (8, 19, '微信图片_2025-12-05_112337_412.jpg', '/uploads/records/1/微信图片_2025-12-05_112337_412.jpg', 'jpg', 354784, '2025-12-11 12:43:11');

-- ----------------------------
-- Table structure for travel_plans
-- ----------------------------
DROP TABLE IF EXISTS `travel_plans`;
CREATE TABLE `travel_plans`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `destination` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `budget` decimal(10, 2) NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `travel_plans_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of travel_plans
-- ----------------------------
INSERT INTO `travel_plans` VALUES (1, 1, '2025日本本州15天樱花季二刷日本，住青旅+民宿超值', '日本本州', '2025-03-25', '2025-04-08', 28000.00, '二刷日本，住青旅+民宿超值二刷日本，住青旅+民宿超值二刷日本，住青旅+民宿超值二刷日本，住青旅+民宿超值', '2025-12-10 13:23:18', '2025-12-11 12:45:22');
INSERT INTO `travel_plans` VALUES (2, 2, '2025暑假东南亚四国30天毕业旅行', '泰国→老挝→越南→柬埔寨', '2025-07-01', '2025-07-31', 15000.00, '穷游版大环线，梦想照进现实', '2025-12-10 13:23:18', '2025-12-10 13:23:18');
INSERT INTO `travel_plans` VALUES (3, 3, '2025国庆川藏南线自驾进藏', '成都→林芝→拉萨→纳木错', '2025-10-01', '2025-10-15', 25000.00, '和朋友拼车进藏，圆梦318', '2025-12-10 13:23:18', '2025-12-10 13:23:18');
INSERT INTO `travel_plans` VALUES (4, 4, '2025冰岛追极光10天', '冰岛一圈', '2025-02-10', '2025-02-20', 35000.00, '攒钱一年就为了看极光', '2025-12-10 13:23:18', '2025-12-10 13:23:18');
INSERT INTO `travel_plans` VALUES (5, 5, '2025清明云南6天小环线', '昆明→大理→丽江→泸沽湖', '2025-04-03', '2025-04-09', 6000.00, '学生党拼假云南行', '2025-12-10 13:23:18', '2025-12-10 13:23:18');

-- ----------------------------
-- Table structure for travel_records
-- ----------------------------
DROP TABLE IF EXISTS `travel_records`;
CREATE TABLE `travel_records`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `destination` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `diary_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `is_public` tinyint(1) NULL DEFAULT 0,
  `view_count` int NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_public`(`is_public` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `travel_records_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of travel_records
-- ----------------------------
INSERT INTO `travel_records` VALUES (1, 1, '2024东京樱花季8天7夜', '日本·东京', '2024-03-25', '2024-04-02', '一个人在樱花雨里走了800公里', '<p>上野公园、浅草寺、东京晴空塔…樱花真的绝了！</p>', 1, 168, '2025-12-10 13:21:37', '2025-12-10 13:21:37');
INSERT INTO `travel_records` VALUES (2, 1, '京都赏枫红叶深度游', '日本·京都', '2024-11-18', '2024-11-24', '穿和服走在哲学之道，太出片了', '<p>东福寺、通天桥、清水寺红叶爆棚</p>', 1, 112, '2025-12-10 13:21:37', '2025-12-10 13:21:37');
INSERT INTO `travel_records` VALUES (3, 2, '泰国曼谷+芭提雅7天吃吃吃', '泰国·曼谷/芭提雅', '2024-07-10', '2024-07-17', '人均800块吃到扶墙走', '<p>冬阴功、芒果糯米饭、船面、烤鱿鱼…我真的会谢！</p>', 1, 256, '2025-12-10 13:21:37', '2025-12-10 13:21:37');
INSERT INTO `travel_records` VALUES (4, 2, '普吉岛圣诞跨年5天', '泰国·普吉岛', '2024-12-20', '2024-12-25', '海边看烟花太浪漫', '<p>浮潜+芭东夜市，完美跨年</p>', 0, 0, '2025-12-10 13:21:37', '2025-12-10 13:21:37');
INSERT INTO `travel_records` VALUES (5, 3, '川西小环线国庆自驾9天', '四川·稻城亚丁', '2024-10-01', '2024-10-09', '蓝天白云雪山绝了', '<p>牛奶海、五色海、洛绒牛场，氧气瓶带了仨</p>', 1, 398, '2025-12-10 13:21:37', '2025-12-10 13:21:37');
INSERT INTO `travel_records` VALUES (6, 4, '厦门+鼓浪屿3天2夜', '福建·厦门', '2024-09-12', '2024-09-15', '曾厝垵吃到撑', '<p>沙茶面、海蛎煎、花生汤、土笋冻通通拿下！</p>', 1, 189, '2025-12-10 13:21:37', '2025-12-10 13:21:37');
INSERT INTO `travel_records` VALUES (7, 5, '越南芽庄+大叻6天5夜', '越南', '2024-08-03', '2024-08-09', '3000块玩转越南南部', '<p>滴漏咖啡、法棍三明治、摩托环游大叻</p>', 1, 174, '2025-12-10 13:21:37', '2025-12-10 13:21:37');
INSERT INTO `travel_records` VALUES (8, 6, '新疆伊犁环线10天', '新疆·伊犁', '2024-06-20', '2024-06-30', '草原上的童话世界', '<p>那拉提、喀拉峻、琼库什台，野花开疯了</p>', 1, 327, '2025-12-10 13:21:37', '2025-12-10 13:21:37');
INSERT INTO `travel_records` VALUES (9, 1, '2025北京之旅', '北京', '2025-12-10', '2025-12-23', '2025北京之旅2025北京之旅2025北京之旅', '<h2>2025北京之旅2025北京之旅2025北京之旅</h2>', 1, 0, '2025-12-11 08:52:28', '2025-12-11 08:52:28');
INSERT INTO `travel_records` VALUES (10, 1, '上海毕业之旅', '上海', '2025-12-10', '2025-12-30', '上海毕业之旅', '<h1><strong>上海毕业之旅</strong></h1>', 1, 0, '2025-12-11 09:06:04', '2025-12-11 09:06:04');
INSERT INTO `travel_records` VALUES (11, 1, '广州', '广州', '2025-12-02', '2025-12-29', '广州广州广州', '<p>广州广州</p>', 1, 0, '2025-12-11 09:09:17', '2025-12-11 09:09:17');
INSERT INTO `travel_records` VALUES (12, 1, '深圳', '深圳', '2025-12-02', '2025-12-30', '深圳', '<p>深圳深圳深圳深圳</p>', 1, 0, '2025-12-11 09:10:06', '2025-12-11 09:10:06');
INSERT INTO `travel_records` VALUES (13, 1, '伦敦', '伦敦', '2025-12-03', '2025-12-17', '伦敦伦敦伦敦伦敦', '<p><strong>伦敦</strong></p>', 1, 0, '2025-12-11 09:21:32', '2025-12-11 09:21:32');
INSERT INTO `travel_records` VALUES (14, 1, '日本', '日本日本日本', '2025-12-01', '2025-12-14', '日本日本', '<p>日本日本日本</p>', 1, 0, '2025-12-11 09:46:06', '2025-12-11 09:46:06');
INSERT INTO `travel_records` VALUES (15, 1, '韩国', '韩国韩国韩国', '2025-12-03', '2025-12-23', '韩国韩国', '<h2>韩国韩国韩国</h2>', 1, 0, '2025-12-11 09:49:16', '2025-12-11 09:49:16');
INSERT INTO `travel_records` VALUES (16, 1, '美国', '美国美国美国美国', '2025-12-02', '2025-12-15', '美国美国', '<h1>美国美国美国美国</h1>', 1, 0, '2025-12-11 09:50:53', '2025-12-11 09:50:53');
INSERT INTO `travel_records` VALUES (17, 1, '欧洲', '欧洲欧洲欧洲', '2025-12-03', '2025-12-16', '欧洲欧洲', '<p>欧洲欧洲</p>', 1, 0, '2025-12-11 09:54:19', '2025-12-11 09:54:19');
INSERT INTO `travel_records` VALUES (18, 1, '非洲', '非洲', '2025-12-02', '2025-12-09', '非洲非洲', '<p>非洲非洲</p>', 1, 0, '2025-12-11 10:06:49', '2025-12-11 10:06:49');
INSERT INTO `travel_records` VALUES (19, 1, '2025毕业之旅', '美国', '2025-12-09', '2025-12-24', '2025毕业之旅2025毕业之旅2025毕业之旅2025毕业之旅2025毕业之旅', '<h1><strong>2025毕业之旅2025毕业之旅2025毕业之旅</strong></h1>', 1, 0, '2025-12-11 12:43:11', '2025-12-11 12:43:11');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, '小明爱旅行', 'xiaoming@example.com', '$2a$10$WACgr26CPjDJySaeUKOUku4tjn91qXz6vhTT88TCjYuccgT1hmE4m', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming', '背包客一枚，喜欢自驾和拍照', '2025-12-10 13:21:37', '2025-12-10 13:26:02');
INSERT INTO `users` VALUES (2, '小红的环球梦', 'xiaohong@example.com', '$2a$10$WACgr26CPjDJySaeUKOUku4tjn91qXz6vhTT88TCjYuccgT1hmE4m', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong', '省钱旅行达人，专攻东南亚', '2025-12-10 13:21:37', '2025-12-10 13:26:03');
INSERT INTO `users` VALUES (3, '摄影师阿杰', 'ajie@example.com', '$2a$10$WACgr26CPjDJySaeUKOUku4tjn91qXz6vhTT88TCjYuccgT1hmE4m', 'https://api.dicebear.com/7.x/avataaars/svg?seed=ajie', '专业风光摄影，爱拍日出日落', '2025-12-10 13:21:37', '2025-12-10 13:26:05');
INSERT INTO `users` VALUES (4, '吃货胖胖', 'pangpang@example.com', '$2a$10$WACgr26CPjDJySaeUKOUku4tjn91qXz6vhTT88TCjYuccgT1hmE4m', 'https://api.dicebear.com/7.x/avataaars/svg?seed=pangpang', '为了吃跑遍大江南北', '2025-12-10 13:21:37', '2025-12-10 13:26:07');
INSERT INTO `users` VALUES (5, '自由行小雨', 'xiaoyu@example.com', '$2a$10$WACgr26CPjDJySaeUKOUku4tjn91qXz6vhTT88TCjYuccgT1hmE4m', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoyu', '自由行攻略狂魔，已打卡30+城市', '2025-12-10 13:21:37', '2025-12-10 13:26:08');
INSERT INTO `users` VALUES (6, '川藏线老王', 'laowang@example.com', '$2a$10$WACgr26CPjDJySaeUKOUku4tjn91qXz6vhTT88TCjYuccgT1hmE4m', 'https://api.dicebear.com/7.x/avataaars/svg?seed=laowang', '骑行川藏线三次，318永远的神', '2025-12-10 13:21:37', '2025-12-10 13:26:09');
INSERT INTO `users` VALUES (7, '海岛控小美', 'xiaomei@example.com', '$2a$10$WACgr26CPjDJySaeUKOUku4tjn91qXz6vhTT88TCjYuccgT1hmE4m', 'https://api.dicebear.com/7.x/avataaars/svg?seed=xiaomei', '一年至少飞三次海岛', '2025-12-10 13:21:37', '2025-12-10 13:26:11');
INSERT INTO `users` VALUES (8, '学生党阿华', 'ahua@example.com', '$2a$10$WACgr26CPjDJySaeUKOUku4tjn91qXz6vhTT88TCjYuccgT1hmE4m', 'https://api.dicebear.com/7.x/avataaars/svg?seed=ahua', '穷游大学生，寒暑假必跑', '2025-12-10 13:21:37', '2025-12-10 13:26:13');
INSERT INTO `users` VALUES (9, 'fish123', '123@qq.com', '$2a$10$WACgr26CPjDJySaeUKOUku4tjn91qXz6vhTT88TCjYuccgT1hmE4m', NULL, NULL, '2025-12-10 05:25:36', '2025-12-10 05:25:36');

SET FOREIGN_KEY_CHECKS = 1;
