/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : weather_agricultural_db

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 27/02/2026 18:35:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for agricultural_products
-- ----------------------------
DROP TABLE IF EXISTS `agricultural_products`;
CREATE TABLE `agricultural_products`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '产品名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '产品类别',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '产品描述',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `stock` int NULL DEFAULT NULL COMMENT '库存',
  `merchant_id` bigint NULL DEFAULT NULL COMMENT '商家ID',
  `applicable_weather` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '适用天气',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_merchant_id`(`merchant_id` ASC) USING BTREE,
  INDEX `idx_stock`(`stock` ASC) USING BTREE,
  CONSTRAINT `agricultural_products_ibfk_1` FOREIGN KEY (`merchant_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '农资产品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of agricultural_products
-- ----------------------------
INSERT INTO `agricultural_products` VALUES (1, '防雨布', '防护用品', '高质量防雨布，防水防晒，适合暴雨天气使用00', 91.99, 150, 3, '暴雨', '2026-02-26 20:01:53', '2026-02-27 10:16:19');
INSERT INTO `agricultural_products` VALUES (2, '冰雹防护网', '防护用品', '专业冰雹防护网，保护农作物免受冰雹伤害', 201.99, 79, 3, '冰雹', '2026-02-26 20:01:53', '2026-02-27 09:31:09');
INSERT INTO `agricultural_products` VALUES (3, '支架加固材料', '支架材料', '钢铁支架加固材料，适合大风天气加固使用', 45.50, 198, 4, '大风', '2026-02-26 20:01:53', '2026-02-27 08:39:42');
INSERT INTO `agricultural_products` VALUES (4, '保温被', '保温用品', '农用保温被，有效防止霜冻伤害', 129.99, 120, 3, '霜冻', '2026-02-26 20:01:53', '2026-02-26 20:01:53');
INSERT INTO `agricultural_products` VALUES (5, '灌溉管道', '灌溉设备', '高效灌溉管道系统，适合干旱地区使用', 299.99, 60, 4, '干旱', '2026-02-26 20:01:53', '2026-02-26 20:01:53');
INSERT INTO `agricultural_products` VALUES (6, '排水泵', '排水设备', '大功率排水泵，快速排除积水', 1299.99, 24, 4, '洪涝', '2026-02-26 20:01:53', '2026-02-27 09:30:30');
INSERT INTO `agricultural_products` VALUES (7, '尿素', '肥料', '尿素尿素尿素尿素尿素', 100.00, 100, 14, '晴天', '2026-02-27 08:05:59', '2026-02-27 08:05:59');
INSERT INTO `agricultural_products` VALUES (8, '粮食种子', '种子', '粮食种子粮食种子粮食种子粮食种子牛皮癣直接破门', 20.00, 9998, 14, '晴天', '2026-02-27 09:29:57', '2026-02-27 09:30:30');
INSERT INTO `agricultural_products` VALUES (10, '敌敌畏001', '农药', '强力除草剂，对人体很危险', 20.00, 1000, 17, '晴天', '2026-02-27 10:31:04', '2026-02-27 10:31:15');

-- ----------------------------
-- Table structure for crops
-- ----------------------------
DROP TABLE IF EXISTS `crops`;
CREATE TABLE `crops`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `crop_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作物名称',
  `growth_stage` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生育期',
  `region` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地区',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `planting_date` date NULL DEFAULT NULL COMMENT '种植日期',
  `expected_harvest_date` date NULL DEFAULT NULL COMMENT '预期收获日期',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_region`(`region` ASC) USING BTREE,
  CONSTRAINT `crops_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '作物表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of crops
-- ----------------------------
INSERT INTO `crops` VALUES (1, '小麦', '拔节期', '河南省郑州市', 1, '2023-10-15', '2024-06-01', '2026-02-26 20:01:53');
INSERT INTO `crops` VALUES (2, '玉米', '苗期', '山东省济南市', 2, '2024-04-01', '2024-10-15', '2026-02-26 20:01:53');
INSERT INTO `crops` VALUES (3, '水稻', '分蘖期', '江苏省南京市', 6, '2024-05-01', '2024-10-01', '2026-02-26 20:01:53');
INSERT INTO `crops` VALUES (4, '大豆', '开花期', '河南省郑州市', 1, '2024-05-15', '2024-10-30', '2026-02-26 20:01:53');
INSERT INTO `crops` VALUES (5, '棉花', '苗期', '山东省济南市', 2, '2024-04-20', '2024-11-15', '2026-02-26 20:01:53');
INSERT INTO `crops` VALUES (6, '蔬菜', '生长期', '北京市朝阳区', 1, '2024-02-01', '2024-05-01', '2026-02-26 20:01:53');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `product_id` bigint NULL DEFAULT NULL COMMENT '产品ID',
  `quantity` int NULL DEFAULT NULL COMMENT '数量',
  `total_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '总价',
  `status` enum('PENDING','PAID','SHIPPED','DELIVERED','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'PENDING' COMMENT '订单状态',
  `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付方式',
  `delivery_address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '收货地址',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_number`(`order_number` ASC) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_order_number`(`order_number` ASC) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `agricultural_products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'ORD20240220001', 1, 1, 2, 179.98, 'CANCELLED', '支付宝', '河南省郑州市中原区农业路123号', '2026-02-26 20:01:53', '2026-02-27 10:17:25');
INSERT INTO `orders` VALUES (2, 'ORD20240220002', 2, 2, 1, 199.99, 'SHIPPED', '微信支付', '山东省济南市历下区泉城路456号', '2026-02-26 20:01:53', '2026-02-26 20:01:53');
INSERT INTO `orders` VALUES (3, 'ORD20240220003', 6, 4, 3, 389.97, 'PENDING', '银行转账', '江苏省南京市鼓楼区中山路789号', '2026-02-26 20:01:53', '2026-02-26 20:01:53');
INSERT INTO `orders` VALUES (4, 'ORD20240220004', 1, 3, 5, 227.50, 'DELIVERED', '支付宝', '河南省郑州市金水区文化路321号', '2026-02-26 20:01:53', '2026-02-26 20:01:53');
INSERT INTO `orders` VALUES (5, 'ORD20240220005', 2, 5, 1, 299.99, 'PAID', '微信支付', '山东省济南市槐荫区经十路654号', '2026-02-26 20:01:53', '2026-02-26 20:01:53');
INSERT INTO `orders` VALUES (6, 'ORD20240220006', 6, 6, 1, 1299.99, 'PENDING', '银行转账', '江苏省南京市浦口区浦珠路987号', '2026-02-26 20:01:53', '2026-02-26 20:01:53');
INSERT INTO `orders` VALUES (7, 'ORD17721815822799F51F20F', 14, 3, 2, 91.00, 'CANCELLED', '在线支付(模拟)', '北京市蔬菜大棚种植基地', '2026-02-27 08:39:42', '2026-02-27 08:46:18');
INSERT INTO `orders` VALUES (8, 'ORD177218158278597417934', 14, 2, 1, 199.99, 'CANCELLED', '在线支付(模拟)', '北京市蔬菜大棚种植基地', '2026-02-27 08:39:43', '2026-02-27 10:31:22');
INSERT INTO `orders` VALUES (9, 'ORD177218462976319B6F70E', 14, 8, 2, 40.00, 'PAID', '在线支付(模拟)', '北京大学农业研究所', '2026-02-27 09:30:30', '2026-02-27 09:30:30');
INSERT INTO `orders` VALUES (10, 'ORD1772184629931DAC12A92', 14, 6, 1, 1299.99, 'CANCELLED', '在线支付(模拟)', '北京大学农业研究所', '2026-02-27 09:30:30', '2026-02-27 10:17:15');

-- ----------------------------
-- Table structure for recommendations
-- ----------------------------
DROP TABLE IF EXISTS `recommendations`;
CREATE TABLE `recommendations`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `warning_id` bigint NULL DEFAULT NULL COMMENT '预警ID',
  `product_id` bigint NULL DEFAULT NULL COMMENT '产品ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `priority` int NULL DEFAULT NULL COMMENT '优先级',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '推荐原因',
  `status` enum('pending','accepted','rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending' COMMENT '推荐状态',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_warning_id`(`warning_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `recommendations_ibfk_1` FOREIGN KEY (`warning_id`) REFERENCES `warnings` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `recommendations_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `agricultural_products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `recommendations_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '推荐表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recommendations
-- ----------------------------
INSERT INTO `recommendations` VALUES (1, 1, 1, 1, 1, '暴雨预警，建议使用防雨布保护农作物', 'pending', '2026-02-26 20:01:53');
INSERT INTO `recommendations` VALUES (2, 2, 2, 2, 1, '冰雹预警，强烈建议使用冰雹防护网', 'pending', '2026-02-26 20:01:53');
INSERT INTO `recommendations` VALUES (3, 3, 3, 1, 2, '大风预警，建议加固支架', 'pending', '2026-02-26 20:01:53');
INSERT INTO `recommendations` VALUES (4, 4, 4, 6, 1, '霜冻预警，建议使用保温被', 'pending', '2026-02-26 20:01:53');
INSERT INTO `recommendations` VALUES (5, 5, 5, 6, 2, '干旱预警，建议使用灌溉设备', 'pending', '2026-02-26 20:01:53');
INSERT INTO `recommendations` VALUES (6, 6, 6, 1, 1, '洪涝预警，建议准备排水设备', 'pending', '2026-02-26 20:01:53');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `user_type` enum('FARMER','MERCHANT','ADMIN') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户类型',
  `region` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地区',
  `status` enum('ACTIVE','INACTIVE','LOCKED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ACTIVE' COMMENT '账户状态',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_user_type`(`user_type` ASC) USING BTREE,
  INDEX `idx_region`(`region` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'farmer_001', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QIDM', 'farmer001252@example.com', '13800138008', 'FARMER', '河南省郑州市', 'ACTIVE', '2026-02-26 20:01:53', '2026-02-27 10:29:04');
INSERT INTO `users` VALUES (2, 'farmer_002', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QIDM', 'farmer002@example.com', '13800138002', 'FARMER', '山东省济南市', 'ACTIVE', '2026-02-26 20:01:53', '2026-02-26 23:47:42');
INSERT INTO `users` VALUES (3, 'farmer_0010', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QIDM', 'merchant001@example.com', '13900139001', 'FARMER', '北京市朝阳区', 'ACTIVE', '2026-02-26 20:01:53', '2026-02-27 18:30:13');
INSERT INTO `users` VALUES (4, 'farmer_0030', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QIDM', 'merchant002@example.com', '13900139002', 'FARMER', '上海市浦东新区', 'ACTIVE', '2026-02-26 20:01:53', '2026-02-27 18:30:10');
INSERT INTO `users` VALUES (5, 'admin_001', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QIDM', 'admin001@example.com', '13700137001', 'ADMIN', '北京市', 'ACTIVE', '2026-02-26 20:01:53', '2026-02-26 23:47:42');
INSERT INTO `users` VALUES (6, 'farmer_003', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy2QIDM', 'farmer003@example.com', '13800138003', 'FARMER', '江苏省南京市', 'ACTIVE', '2026-02-26 20:01:53', '2026-02-26 23:47:42');
INSERT INTO `users` VALUES (13, 'farmer001', '$2a$10$RcZvnJTXafhYkCBrVMKZCOY7Dx541nAmHoeQBlLJjxmqfGwCHK4be', '123456@qq.com', NULL, 'FARMER', '北京', 'ACTIVE', '2026-02-26 15:34:14', '2026-02-26 23:47:42');
INSERT INTO `users` VALUES (14, 'farmer003', '$2a$10$Yt8AXQUfE/1q/X1VohXQs.W1xu5BcXi8fGpAmniEASs6olPCHpWc.', '123456666@qq.com', '15071469547', 'FARMER', '北京', 'ACTIVE', '2026-02-26 15:48:05', '2026-02-27 09:30:48');
INSERT INTO `users` VALUES (15, 'farmer_0052', '$2a$10$Unf1KwB9QKthXcCHkE34K..s7FTRwxPhGhriSEqN7hGizeca0c1mK', 'merchant_001@university.edu.cn', NULL, 'FARMER', '上海', 'ACTIVE', '2026-02-26 16:06:51', '2026-02-27 18:30:08');
INSERT INTO `users` VALUES (16, 'farmer_00358', '$2a$10$mw5jaw/IuAEeplaP9R4wr.ea9R/gfUY0ndU1MmRMW2ac3pTuZ1Y/i', '12345666600@qq.com', NULL, 'FARMER', '上海', 'ACTIVE', '2026-02-26 16:08:03', '2026-02-27 18:30:04');
INSERT INTO `users` VALUES (17, 'admin100', '$2a$10$DP0lEkXFU.9I/.CxMwnakOMifky/TV7Wx1Rrbqt0nWRskWoE4TNMK', '12345657858@qq.com', NULL, 'ADMIN', '北京', 'ACTIVE', '2026-02-27 09:47:46', '2026-02-27 09:47:46');

-- ----------------------------
-- Table structure for warnings
-- ----------------------------
DROP TABLE IF EXISTS `warnings`;
CREATE TABLE `warnings`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `warning_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预警类型',
  `region` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '地区',
  `severity` enum('LOW','MEDIUM','HIGH','CRITICAL') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预警等级',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '预警描述',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '结束时间',
  `status` enum('ACTIVE','EXPIRED','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ACTIVE' COMMENT '预警状态',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_region`(`region` ASC) USING BTREE,
  INDEX `idx_warning_type`(`warning_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '预警表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of warnings
-- ----------------------------
INSERT INTO `warnings` VALUES (1, '暴雨', '河南省郑州市', 'HIGH', '未来24小时内可能出现暴雨，请做好防护准备02152', '2024-02-21 08:00:00', '2024-02-24 08:00:00', 'ACTIVE', '2026-02-26 20:01:53', '2026-02-27 09:08:47');
INSERT INTO `warnings` VALUES (2, '冰雹', '山东省济南市', 'CRITICAL', '预计明天下午可能出现冰雹，请立即采取防护措施', '2024-02-21 10:00:00', '2024-02-21 18:00:00', 'ACTIVE', '2026-02-26 20:01:53', '2026-02-26 20:01:53');
INSERT INTO `warnings` VALUES (3, '大风', '北京市朝阳区', 'MEDIUM', '明天风力较大，请加固农作物支架', '2024-02-21 12:00:00', '2024-02-22 12:00:00', 'CANCELLED', '2026-02-26 20:01:53', '2026-02-27 15:09:46');
INSERT INTO `warnings` VALUES (4, '霜冻', '江苏省南京市', 'HIGH', '后天早晨可能出现霜冻，请做好保温工作', '2024-02-22 06:00:00', '2024-02-22 12:00:00', 'ACTIVE', '2026-02-26 20:01:53', '2026-02-26 20:01:53');
INSERT INTO `warnings` VALUES (5, '干旱', '浙江省杭州市', 'MEDIUM', '近期降水较少，建议及时灌溉，保护装甲', '2024-02-20 00:00:00', '2024-03-05 00:00:00', 'ACTIVE', '2026-02-26 20:01:53', '2026-02-27 09:28:53');
INSERT INTO `warnings` VALUES (6, '洪涝', '上海市浦东新区', 'HIGH', '预计本周降水量较大，请做好排水准备', '2024-02-21 00:00:00', '2024-02-25 00:00:00', 'EXPIRED', '2026-02-26 20:01:53', '2026-02-27 15:08:47');
INSERT INTO `warnings` VALUES (9, '冰雹', '河北', 'MEDIUM', '八八八八', '2026-02-25 16:00:00', '2026-03-06 16:00:00', 'ACTIVE', '2026-02-27 10:23:25', '2026-02-27 10:23:56');
INSERT INTO `warnings` VALUES (10, '霜冻', '河南省', 'MEDIUM', '霜冻来领 注意农作物00', '2026-02-11 16:00:00', '2026-03-06 16:00:00', 'ACTIVE', '2026-02-27 10:31:58', '2026-02-27 10:32:55');

-- ----------------------------
-- Table structure for weather_data
-- ----------------------------
DROP TABLE IF EXISTS `weather_data`;
CREATE TABLE `weather_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `region` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '地区',
  `temperature` decimal(5, 2) NULL DEFAULT NULL COMMENT '温度',
  `humidity` int NULL DEFAULT NULL COMMENT '湿度',
  `precipitation` decimal(8, 2) NULL DEFAULT NULL COMMENT '降水量',
  `wind_speed` decimal(5, 2) NULL DEFAULT NULL COMMENT '风速',
  `weather_condition` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '天气状况',
  `recorded_at` timestamp NULL DEFAULT NULL COMMENT '记录时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_region`(`region` ASC) USING BTREE,
  INDEX `idx_recorded_at`(`recorded_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 61 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '气象数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of weather_data
-- ----------------------------
INSERT INTO `weather_data` VALUES (29, '北京', 6.00, 50, 0.00, 1.00, '多云', '2026-02-27 04:00:00', '2026-02-27 06:24:18');
INSERT INTO `weather_data` VALUES (30, '北京', 4.00, 50, 0.00, 1.00, '阴', '2026-02-28 04:00:00', '2026-02-27 06:24:18');
INSERT INTO `weather_data` VALUES (31, '北京', 5.00, 50, 0.00, 1.00, '多云', '2026-03-01 04:00:00', '2026-02-27 06:24:19');
INSERT INTO `weather_data` VALUES (32, '北京', 5.00, 50, 0.00, 1.00, '阴', '2026-03-02 04:00:00', '2026-02-27 06:24:19');
INSERT INTO `weather_data` VALUES (33, 'wuh', 13.00, 50, 0.00, 1.00, '阴', '2026-02-27 04:00:00', '2026-02-27 06:24:54');
INSERT INTO `weather_data` VALUES (34, 'wuh', 14.00, 50, 0.00, 1.00, '小雨', '2026-02-28 04:00:00', '2026-02-27 06:24:54');
INSERT INTO `weather_data` VALUES (35, 'wuh', 10.00, 50, 0.00, 1.00, '小雨', '2026-03-01 04:00:00', '2026-02-27 06:24:54');
INSERT INTO `weather_data` VALUES (36, 'wuh', 10.00, 50, 0.00, 1.00, '阴', '2026-03-02 04:00:00', '2026-02-27 06:24:54');
INSERT INTO `weather_data` VALUES (37, 'wuha', 13.00, 50, 0.00, 1.00, '小雨', '2026-02-27 04:00:00', '2026-02-27 06:24:54');
INSERT INTO `weather_data` VALUES (38, 'wuha', 20.00, 50, 0.00, 1.00, '小雨', '2026-02-28 04:00:00', '2026-02-27 06:24:54');
INSERT INTO `weather_data` VALUES (39, 'wuha', 14.00, 50, 0.00, 1.00, '小雨', '2026-03-01 04:00:00', '2026-02-27 06:24:54');
INSERT INTO `weather_data` VALUES (40, 'wuha', 13.00, 50, 0.00, 1.00, '阴', '2026-03-02 04:00:00', '2026-02-27 06:24:54');
INSERT INTO `weather_data` VALUES (41, '武汉', 13.00, 50, 0.00, 1.00, '阴', '2026-02-27 04:00:00', '2026-02-27 06:24:57');
INSERT INTO `weather_data` VALUES (42, '武汉', 13.00, 50, 0.00, 1.00, '小雨', '2026-02-28 04:00:00', '2026-02-27 06:24:57');
INSERT INTO `weather_data` VALUES (43, '武汉', 11.00, 50, 0.00, 1.00, '小雨', '2026-03-01 04:00:00', '2026-02-27 06:24:57');
INSERT INTO `weather_data` VALUES (44, '武汉', 10.00, 50, 0.00, 1.00, '阴', '2026-03-02 04:00:00', '2026-02-27 06:24:57');
INSERT INTO `weather_data` VALUES (45, '广州', 25.00, 50, 0.00, 1.00, '小雨-中雨', '2026-02-27 04:00:00', '2026-02-27 06:25:28');
INSERT INTO `weather_data` VALUES (46, '广州', 23.00, 50, 0.00, 1.00, '中雨-大雨', '2026-02-28 04:00:00', '2026-02-27 06:25:28');
INSERT INTO `weather_data` VALUES (47, '广州', 23.00, 50, 0.00, 1.00, '小雨', '2026-03-01 04:00:00', '2026-02-27 06:25:28');
INSERT INTO `weather_data` VALUES (48, '广州', 24.00, 50, 0.00, 1.00, '小雨-中雨', '2026-03-02 04:00:00', '2026-02-27 06:25:28');
INSERT INTO `weather_data` VALUES (49, '深圳', 26.00, 50, 0.00, 1.00, '阴', '2026-02-27 04:00:00', '2026-02-27 09:27:41');
INSERT INTO `weather_data` VALUES (50, '深圳', 24.00, 50, 0.00, 1.00, '阵雨', '2026-02-28 04:00:00', '2026-02-27 09:27:41');
INSERT INTO `weather_data` VALUES (51, '深圳', 25.00, 50, 0.00, 1.00, '小雨', '2026-03-01 04:00:00', '2026-02-27 09:27:41');
INSERT INTO `weather_data` VALUES (52, '深圳', 25.00, 50, 0.00, 1.00, '阵雨', '2026-03-02 04:00:00', '2026-02-27 09:27:41');
INSERT INTO `weather_data` VALUES (53, 'xian', 12.00, 50, 0.00, 1.00, '阴', '2026-02-27 04:00:00', '2026-02-27 09:27:48');
INSERT INTO `weather_data` VALUES (54, 'xian', 7.00, 50, 0.00, 1.00, '小雨', '2026-02-28 04:00:00', '2026-02-27 09:27:48');
INSERT INTO `weather_data` VALUES (55, 'xian', 10.00, 50, 0.00, 1.00, '小雨', '2026-03-01 04:00:00', '2026-02-27 09:27:48');
INSERT INTO `weather_data` VALUES (56, 'xian', 13.00, 50, 0.00, 1.00, '阴', '2026-03-02 04:00:00', '2026-02-27 09:27:48');
INSERT INTO `weather_data` VALUES (57, '西安', 16.00, 50, 0.00, 1.00, '晴', '2026-02-27 04:00:00', '2026-02-27 09:27:55');
INSERT INTO `weather_data` VALUES (58, '西安', 10.00, 50, 0.00, 1.00, '小雨', '2026-02-28 04:00:00', '2026-02-27 09:27:55');
INSERT INTO `weather_data` VALUES (59, '西安', 7.00, 50, 0.00, 1.00, '小雨', '2026-03-01 04:00:00', '2026-02-27 09:27:55');
INSERT INTO `weather_data` VALUES (60, '西安', 11.00, 50, 0.00, 1.00, '晴', '2026-03-02 04:00:00', '2026-02-27 09:27:55');

SET FOREIGN_KEY_CHECKS = 1;
