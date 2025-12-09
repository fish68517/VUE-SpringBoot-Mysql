/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : warehouse_db

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 09/12/2025 12:14:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for departments
-- ----------------------------
DROP TABLE IF EXISTS `departments`;
CREATE TABLE `departments`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '部门名称',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of departments
-- ----------------------------
INSERT INTO `departments` VALUES (1, '行政部', '2025-10-12 10:28:39', '2025-10-12 10:28:39');
INSERT INTO `departments` VALUES (2, '仓储部', '2025-10-12 10:28:39', '2025-10-12 10:28:39');
INSERT INTO `departments` VALUES (3, '技术支持部', '2025-10-12 10:28:39', '2025-10-12 10:28:39');
INSERT INTO `departments` VALUES (4, '采购部', '2025-10-12 10:28:39', '2025-10-12 10:28:39');
INSERT INTO `departments` VALUES (5, '制造部', '2025-10-12 10:28:39', '2025-10-12 10:28:39');
INSERT INTO `departments` VALUES (8, '00', '2025-10-19 10:58:18', '2025-10-19 10:58:18');

-- ----------------------------
-- Table structure for inbound_orders
-- ----------------------------
DROP TABLE IF EXISTS `inbound_orders`;
CREATE TABLE `inbound_orders`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `order_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '入库单的唯一编号',
  `created_by_user_id` int UNSIGNED NOT NULL COMMENT '创建该订单的用户 (仓库管理员) [cite: 15]',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '额外备注',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_number`(`order_number` ASC) USING BTREE,
  INDEX `idx_created_by_user_id`(`created_by_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_inbound_orders_user` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 220 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '入库单 (任务) 表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inbound_orders
-- ----------------------------
INSERT INTO `inbound_orders` VALUES (201, 'IN-20251011-001', 2, '已完成', '首批产品入库', '2025-10-12 10:28:39', '2025-10-12 10:28:39');
INSERT INTO `inbound_orders` VALUES (202, 'IN-20251012-001', 6, '已完成', '供应商补货', '2025-10-12 10:28:39', '2025-10-29 14:19:12');
INSERT INTO `inbound_orders` VALUES (203, 'IN-20251012-002', 2, '处理中', '新品待入库', '2025-10-12 10:28:39', '2025-10-12 10:28:39');
INSERT INTO `inbound_orders` VALUES (204, 'IN-1760946090076', 6, '已完成', '补充摄像头牙刷', '2025-10-20 15:42:04', '2025-10-29 17:38:13');
INSERT INTO `inbound_orders` VALUES (207, 'IN-1762525967115', 1, '待处理', '00', '2025-11-07 22:32:54', '2025-11-07 22:32:54');
INSERT INTO `inbound_orders` VALUES (209, 'IN-1762526027002', 1, '待处理', '00', '2025-11-07 22:33:51', '2025-11-07 22:33:51');
INSERT INTO `inbound_orders` VALUES (210, 'IN-1762526048805', 1, '待处理', '000', '2025-11-07 22:34:15', '2025-11-07 22:34:15');
INSERT INTO `inbound_orders` VALUES (211, 'IN-1762526148211', 1, '待处理', '00', '2025-11-07 22:35:54', '2025-11-07 22:35:54');
INSERT INTO `inbound_orders` VALUES (214, 'IN-17625706', 1, '待处理', '000', '2025-11-08 10:56:46', '2025-11-08 10:56:46');
INSERT INTO `inbound_orders` VALUES (215, 'IN-1762570', 1, '待处理', '00', '2025-11-08 11:00:27', '2025-11-08 11:00:27');
INSERT INTO `inbound_orders` VALUES (216, 'IN-176265330585', 1, '待处理', '00', '2025-11-09 09:55:12', '2025-11-09 09:55:12');
INSERT INTO `inbound_orders` VALUES (217, 'IN-1765248764094', 1, '待处理', '查询货品', '2025-12-09 10:53:10', '2025-12-09 10:53:10');
INSERT INTO `inbound_orders` VALUES (218, 'IN-1765249654733', 1, '待处理', '清走损坏的火区', '2025-12-09 11:07:57', '2025-12-09 11:07:57');
INSERT INTO `inbound_orders` VALUES (219, 'IN-1765249734518', 1, '待处理', '那囊给 噶vv 宝宝如果日本', '2025-12-09 11:09:04', '2025-12-09 11:09:04');

-- ----------------------------
-- Table structure for inventory
-- ----------------------------
DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `product_id` int UNSIGNED NOT NULL COMMENT '关联产品表的外键',
  `batch_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '批次唯一编码, 用于生成二维码',
  `inbound_order_id` int UNSIGNED NULL DEFAULT NULL COMMENT '此批次所属的入库单',
  `quantity` int NOT NULL DEFAULT 0 COMMENT '该批次的当前库存数量',
  `received_at` datetime NULL DEFAULT NULL COMMENT '批次被实际扫码入库的时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_batch_code`(`batch_code` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_inbound_order_id`(`inbound_order_id` ASC) USING BTREE,
  CONSTRAINT `fk_inventory_inbound_order` FOREIGN KEY (`inbound_order_id`) REFERENCES `inbound_orders` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_inventory_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1010 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '库存及批次表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inventory
-- ----------------------------
INSERT INTO `inventory` VALUES (1001, 101, 'BAT-20251011-001', 201, 162, '2025-10-11 10:30:00', '2025-10-12 10:28:40', '2025-10-12 10:28:40');
INSERT INTO `inventory` VALUES (1002, 102, 'BAT-20251011-002', 201, 129, '2025-10-11 11:00:00', '2025-10-12 10:28:40', '2025-10-12 10:28:40');
INSERT INTO `inventory` VALUES (1003, 103, 'BAT-20251011-003', 201, 300, '2025-10-11 11:45:00', '2025-10-12 10:28:40', '2025-10-12 10:28:40');
INSERT INTO `inventory` VALUES (1004, 101, 'BAT-20251012-001', 202, 90, '2025-10-12 09:00:00', '2025-10-12 10:28:40', '2025-10-12 10:28:40');
INSERT INTO `inventory` VALUES (1005, 104, 'BAT-20251012-002', 202, 120, '2025-10-12 09:30:00', '2025-10-12 10:28:40', '2025-10-12 10:28:40');
INSERT INTO `inventory` VALUES (1006, 101, 'BAT-215-101-827233-iby9', 215, 1, NULL, '2025-11-08 11:00:27', '2025-11-08 11:00:27');
INSERT INTO `inventory` VALUES (1007, 101, 'BAT-216-101-312573-tzyt', 216, 1, NULL, '2025-11-09 09:55:12', '2025-11-09 09:55:12');
INSERT INTO `inventory` VALUES (1008, 106, 'BAT-217-106-790188-ya6a', 217, 12, NULL, '2025-12-09 10:53:10', '2025-12-09 10:53:10');
INSERT INTO `inventory` VALUES (1009, 106, 'BAT-219-106-745486-1mfx', 219, 3, NULL, '2025-12-09 11:09:05', '2025-12-09 11:09:05');

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `sku` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'SKU (Stock Keeping Unit), 产品的唯一标识符',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '产品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '产品详细描述',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` int NULL DEFAULT NULL COMMENT '创建人创建者ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sku`(`sku` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 107 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '产品主数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (101, 'SKU-A001', '智能降噪蓝牙耳机', '支持主动降噪，提供沉浸式音频体验，24小时超长待机。', '2025-10-12 10:28:39', '2025-12-09 10:50:28', 3);
INSERT INTO `products` VALUES (102, 'SKU-B002', '高速电吹风戴森', '负离子护发，11万转高速马达，快速干发不伤发。', '2025-10-12 10:28:39', '2025-12-09 10:50:29', 1);
INSERT INTO `products` VALUES (103, 'SKU-C003', '便携式电动牙刷', '声波震动技术，多种清洁模式，IPX7级防水。', '2025-10-12 10:28:39', '2025-12-09 10:50:31', 3);
INSERT INTO `products` VALUES (104, 'SKU-D004', '家用智能监控摄像头', '1080P高清画质，支持夜视和双向语音通话，AI人形侦测。', '2025-10-12 10:28:39', '2025-12-09 10:50:33', 4);
INSERT INTO `products` VALUES (106, 'SKU-A0031', '电脑笔记本', '电脑笔记本电脑笔记本电脑笔记本电脑笔记本电脑笔记本', '2025-12-09 02:45:09', '2025-12-09 10:50:16', 3);

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称 (例如: 系统管理员, 仓库管理员)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_name`(`role_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES (2, '仓库管理员');
INSERT INTO `roles` VALUES (3, '操作员');
INSERT INTO `roles` VALUES (1, '系统管理员');

-- ----------------------------
-- Table structure for system_logs
-- ----------------------------
DROP TABLE IF EXISTS `system_logs`;
CREATE TABLE `system_logs`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `operator_id` int UNSIGNED NULL DEFAULT NULL COMMENT '操作者ID',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作者用户名',
  `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '动作类型 (例如: DELETE_USER, UPDATE_ROLE)',
  `target_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作对象ID',
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '详细描述或JSON数据',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作者IP',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统关键操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_logs
-- ----------------------------
INSERT INTO `system_logs` VALUES (1, 1, 'admin', 'LOGIN', NULL, '用户登录成功', '127.0.0.1', '2025-12-09 11:29:22');
INSERT INTO `system_logs` VALUES (2, 1, 'admin', 'UPDATE_USER', '1', '更新用户 [admin]: 全名变更: 张管理000111->张管理0001112222; 密码变更: ******->******; ', '127.0.0.1', '2025-12-09 11:56:45');
INSERT INTO `system_logs` VALUES (3, 9, 'admin', 'DELETE_USER', '9', '删除了用户: admin12345 (张管理23)', '127.0.0.1', '2025-12-09 11:57:48');

-- ----------------------------
-- Table structure for transaction_logs
-- ----------------------------
DROP TABLE IF EXISTS `transaction_logs`;
CREATE TABLE `transaction_logs`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `inventory_id` int UNSIGNED NOT NULL COMMENT '关联的库存批次ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '执行此操作的用户 (操作员) [cite: 18]',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `quantity_change` int NOT NULL COMMENT '库存变化数量 (+表示入库, -表示出库)',
  `quantity_after_transaction` int NOT NULL COMMENT '本次异动后, 批次的库存数量',
  `notes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '可选备注, 例如: 盘点调整原因',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_inventory_id`(`inventory_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_logs_inventory` FOREIGN KEY (`inventory_id`) REFERENCES `inventory` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_logs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '库存异动日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of transaction_logs
-- ----------------------------
INSERT INTO `transaction_logs` VALUES (1, 1001, 3, '入库', 200, 200, '初始入库', '2025-10-12 10:28:40');
INSERT INTO `transaction_logs` VALUES (2, 1002, 3, '入库', 150, 150, '初始入库', '2025-10-12 10:28:40');
INSERT INTO `transaction_logs` VALUES (3, 1003, 4, '入库', 300, 300, '初始入库', '2025-10-12 10:28:40');
INSERT INTO `transaction_logs` VALUES (4, 1004, 3, '入库', 100, 100, '初始入库', '2025-10-12 10:28:40');
INSERT INTO `transaction_logs` VALUES (5, 1005, 4, '入库', 120, 120, '初始入库', '2025-10-12 10:28:40');
INSERT INTO `transaction_logs` VALUES (7, 1002, 3, '盘点', -1, 149, '盘点时发现一个包装破损', '2025-10-12 10:28:40');
INSERT INTO `transaction_logs` VALUES (8, 1001, 6, '入库', 1, 201, '扫码入库', '2025-10-29 18:58:55');
INSERT INTO `transaction_logs` VALUES (9, 1001, 6, '入库', 1, 202, '扫码入库', '2025-10-29 18:59:33');
INSERT INTO `transaction_logs` VALUES (11, 1001, 6, '入库', 1, 202, '扫码入库', '2025-11-07 22:13:44');
INSERT INTO `transaction_logs` VALUES (12, 1001, 3, '出库', -10, 192, '领用', '2025-11-07 23:05:17');
INSERT INTO `transaction_logs` VALUES (13, 1001, 3, '出库', -10, 182, '你你你', '2025-11-07 23:06:02');
INSERT INTO `transaction_logs` VALUES (14, 1002, 1, '出库', -20, 129, '11', '2025-11-07 23:06:25');
INSERT INTO `transaction_logs` VALUES (15, 1001, 1, '出库', -20, 162, '风格恢复', '2025-11-07 23:16:42');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录用户名',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '加密后的密码',
  `full_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户全名',
  `role_id` int UNSIGNED NOT NULL COMMENT '关联角色表的外键',
  `department_id` int UNSIGNED NULL DEFAULT NULL COMMENT '关联部门表的外键',
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '标记用户账户是否启用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_department_id`(`department_id` ASC) USING BTREE,
  CONSTRAINT `fk_users_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_users_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户账户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '1234567890', '张管理0001112222', 1, 2, 1, '2025-10-12 10:28:39', '2025-12-09 11:56:45');
INSERT INTO `users` VALUES (2, 'manager_wh', '123456', '李经理', 2, 2, 1, '2025-10-12 10:28:39', '2025-10-12 10:28:39');
INSERT INTO `users` VALUES (3, 'operator_a', '123456', '王操作员', 3, 2, 1, '2025-10-12 10:28:39', '2025-12-09 10:26:51');
INSERT INTO `users` VALUES (4, 'operator_b', '123456', '赵操作员', 3, 2, 0, '2025-10-12 10:28:39', '2025-12-09 11:02:43');
INSERT INTO `users` VALUES (5, 'disabled_user', '123456', '孙禁用', 3, 2, 0, '2025-10-12 10:28:39', '2025-10-12 10:28:39');
INSERT INTO `users` VALUES (6, '111', '123456', '1', 2, NULL, 1, '2025-10-29 13:42:36', '2025-11-07 21:46:26');
INSERT INTO `users` VALUES (7, '11', '11', '11', 2, 1, 0, '2025-11-07 21:42:25', '2025-11-07 21:42:25');
INSERT INTO `users` VALUES (8, '12', 'a12345678', '123', 2, 1, 1, '2025-11-28 20:37:01', '2025-11-28 20:37:46');

SET FOREIGN_KEY_CHECKS = 1;
