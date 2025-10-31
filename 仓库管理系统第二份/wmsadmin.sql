/*
Navicat MySQL Data Transfer

Source Server         : 数据库快递物流仓库
Source Server Version : 80036
Source Host           : databases.ptcc9.top:3306
Source Database       : wmsadmin

Target Server Type    : MYSQL
Target Server Version : 80036
File Encoding         : 65001

Date: 2025-10-30 21:50:23
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `roles` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES ('0e12f216-408f-4fe6-bae0-70e6f8273d79', '2025-10-18 14:38:14', '2159609711@qq.com', '123456', 'ROLE_SUPER_ADMIN');
INSERT INTO `admin` VALUES ('0e12f216-408f-4fe6-bae0-70e6f8273d86', '2025-04-23 17:20:27', 'notmaker@163.com', '123456', 'ROLE_SUPER_ADMIN');
INSERT INTO `admin` VALUES ('12598638-f94f-46af-88e5-b87b40b55bf5', '2025-04-25 21:00:06', 'lop@qq.com', '123456', '');
INSERT INTO `admin` VALUES ('20b81c11-0881-4f3a-9bfe-3df6c25d5921', '2025-04-28 13:52:15', 'abc@qq.com', 'abc@qq.com', 'ROLE_SUPER_ADMIN');
INSERT INTO `admin` VALUES ('37ecf396-cf13-44a6-9715-6d14e8643fb6', '2025-04-18 11:28:21', 'admin@qq.com', 'admin@qq.com', 'ROLE_EMPLOYEE;ROLE_WAREHOUSE;ROLE_SALE;ROLE_COMMODITY;ROLE_ADMIN;ROLE_SUPER_ADMIN');
INSERT INTO `admin` VALUES ('38eb6904-785c-4e3a-a386-2dc2b9d92204', '2025-04-27 20:15:04', 'jiegod_8ck@126.com', '123456', 'ROLE_ADMIN;ROLE_SUPER_ADMIN');
INSERT INTO `admin` VALUES ('3f33b767-d6cd-4736-9a51-6c1ff27e0663', '2025-04-20 20:00:08', 'lop@qq.com', '123456', 'ROLE_SUPER_ADMIN');
INSERT INTO `admin` VALUES ('6462544b-50cc-4638-9f14-c81f63188fc2', '2025-04-29 20:25:48', 'cccccc@qq.com', '123456', 'ROLE_SUPER_ADMIN');
INSERT INTO `admin` VALUES ('9196f2e7-9c6c-4622-a7c1-5b16b80479f4', '2025-04-26 23:30:56', '1638752551@163.com', 'xiaosusu123', 'ROLE_SUPER_ADMIN');
INSERT INTO `admin` VALUES ('ab129067-4200-4860-9d4e-b8e2bca262de', '2025-04-23 21:18:48', '470372007@qq.com', '123456', 'ROLE_SUPER_ADMIN');
INSERT INTO `admin` VALUES ('b20814b6-d955-4563-9164-95415d2450f6', '2025-04-24 23:58:04', '1402014577@qq.com', '123456', 'ROLE_SUPER_ADMIN');
INSERT INTO `admin` VALUES ('b2297ef7-fe6e-41da-92a9-030ef30b6daf', '2025-04-21 13:42:38', '123@qq.com', '123@qq.com', 'ROLE_SUPER_ADMIN');
INSERT INTO `admin` VALUES ('dd5b8e1d-bcae-4063-9781-d587d205cb84', '2025-04-24 09:50:52', 'testUser@qq.com', 'testUser@qq.com', 'ROLE_COMMODITY;ROLE_SALE;ROLE_WAREHOUSE');
INSERT INTO `admin` VALUES ('e5c6ab74-3aaf-4940-82cb-035ff934fc5f', '2025-04-24 09:50:36', 'code51User@qq.com', 'code51User@qq.com', 'ROLE_SUPER_ADMIN;ROLE_COMMODITY;ROLE_EMPLOYEE;ROLE_SALE;ROLE_WAREHOUSE;ROLE_ADMIN');

-- ----------------------------
-- Table structure for alert_records
-- ----------------------------
DROP TABLE IF EXISTS `alert_records`;
CREATE TABLE `alert_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` varchar(36) COLLATE utf8mb4_bin DEFAULT NULL,
  `alert_type` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '预警类型',
  `alert_level` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '预警级别: LOW, MEDIUM, HIGH',
  `current_value` int DEFAULT NULL COMMENT '当前库存值',
  `threshold_value` int DEFAULT NULL COMMENT '阈值',
  `message` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '预警信息',
  `is_resolved` tinyint(1) DEFAULT '0' COMMENT '是否已处理',
  `resolved_time` datetime DEFAULT NULL COMMENT '处理时间',
  `resolved_by` bigint DEFAULT NULL COMMENT '处理人',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of alert_records
-- ----------------------------
INSERT INTO `alert_records` VALUES ('1', '1', 'LOW_STOCK', 'LOW', '15', '20', '商品【ID:1】库存过低！当前库存：15，安全库存：20', '0', null, null, null);
INSERT INTO `alert_records` VALUES ('2', '2', 'OVER_STOCK', 'HIGH', '200', '100', '商品【ID:2】库存积压！当前库存：200，最大库存：100', '0', null, null, null);
INSERT INTO `alert_records` VALUES ('3', '3', 'LOW_STOCK', 'LOW', '20', '30', '商品【小米15pro】库存过低！当前库存：20，安全库存：30', '0', null, null, null);
INSERT INTO `alert_records` VALUES ('4', '7', 'LOW_STOCK', 'HIGH', '0', '5', '商品【苹果16promax】库存过低！当前库存：0，安全库存：5', '0', null, null, null);
INSERT INTO `alert_records` VALUES ('5', '8', 'OVER_STOCK', 'MEDIUM', '50', '30', '商品【盒马面包】库存积压！当前库存：50，最大库存：30', '0', null, null, null);
INSERT INTO `alert_records` VALUES ('6', '2', 'LOW_STOCK', 'HIGH', '0', '10', '商品【商品ID:2】库存过低！当前库存：0，安全库存：10', '0', null, null, null);
INSERT INTO `alert_records` VALUES ('7', '4', 'LOW_STOCK', 'HIGH', '0', '10', '商品【商品ID:4】库存过低！当前库存：0，安全库存：10', '0', null, null, null);

-- ----------------------------
-- Table structure for alert_rules
-- ----------------------------
DROP TABLE IF EXISTS `alert_rules`;
CREATE TABLE `alert_rules` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` varchar(36) COLLATE utf8mb4_bin DEFAULT NULL,
  `rule_type` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '预警类型: LOW_STOCK, OVER_STOCK',
  `min_stock` int DEFAULT NULL COMMENT '最低库存阈值',
  `max_stock` int DEFAULT NULL COMMENT '最高库存阈值',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of alert_rules
-- ----------------------------
INSERT INTO `alert_rules` VALUES ('7', '1c0e25d3-db1f-4cda-93b1-81101d21dc5b', 'LOW_STOCK', '50', '200', '1', '2025-10-28 16:07:02', '2025-10-28 21:17:00');
INSERT INTO `alert_rules` VALUES ('8', '2', 'LOW_STOCK', '10', '50', '1', '2025-10-28 16:07:02', '2025-10-28 16:07:02');
INSERT INTO `alert_rules` VALUES ('9', '2757278d-fb76-496a-8bd1-7629fa6baf00', 'LOW_STOCK', '15', '100', '1', '2025-10-28 16:07:02', '2025-10-28 21:17:00');
INSERT INTO `alert_rules` VALUES ('10', '4', 'LOW_STOCK', '10', '80', '1', '2025-10-28 16:07:02', '2025-10-28 16:07:02');
INSERT INTO `alert_rules` VALUES ('11', '7', 'LOW_STOCK', '5', null, '1', '2025-10-28 16:07:02', '2025-10-28 16:07:02');
INSERT INTO `alert_rules` VALUES ('12', '8', 'OVER_STOCK', null, '30', '1', '2025-10-28 16:07:02', '2025-10-28 16:07:02');

-- ----------------------------
-- Table structure for code
-- ----------------------------
DROP TABLE IF EXISTS `code`;
CREATE TABLE `code` (
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `exp` bigint NOT NULL,
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of code
-- ----------------------------
INSERT INTO `code` VALUES ('1402014577@qq.com', '1669615938121', '276237');
INSERT INTO `code` VALUES ('2361471705@qq.com', '1760933039000', '123456');
INSERT INTO `code` VALUES ('cccccc@qq.com', '1669725702342', '858227');
INSERT INTO `code` VALUES ('notmaker@163.com', '1760944044513', '123456');

-- ----------------------------
-- Table structure for commodity
-- ----------------------------
DROP TABLE IF EXISTS `commodity`;
CREATE TABLE `commodity` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `count` int NOT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `price` double NOT NULL,
  `update_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of commodity
-- ----------------------------
INSERT INTO `commodity` VALUES ('1c0e25d3-db1f-4cda-93b1-81101d21dc5b', '120', '2025-10-18 08:10:55', '苹果手机', 'iphone17promax1TB', '13999.99', null);
INSERT INTO `commodity` VALUES ('2', '0', '2025-10-28 16:49:18', '专业摄影相机', '富士高端相机', '5999', '2025-10-28 16:49:18');
INSERT INTO `commodity` VALUES ('2757278d-fb76-496a-8bd1-7629fa6baf00', '19', '2025-10-23 23:06:16', '富士高端相机', '富士相机', '29899', null);
INSERT INTO `commodity` VALUES ('3ce4732f-61e1-4048-beb7-89dd7b3d99dc', '20', '2025-10-24 11:16:32', '小米手机', '小米15pro', '4999', null);
INSERT INTO `commodity` VALUES ('4', '15', '2025-10-28 16:49:18', '最新款小米手机', '小米16pro', '3999', '2025-10-28 16:49:18');
INSERT INTO `commodity` VALUES ('44efcfc2-89ba-4bfd-887d-674b338c5f93', '15', '2025-10-24 11:08:11', '小米手机', '小米16pro', '3999', null);
INSERT INTO `commodity` VALUES ('7', '0', '2025-10-28 16:49:18', '最新款苹果手机', '苹果16promax', '8999', '2025-10-28 16:49:18');
INSERT INTO `commodity` VALUES ('8', '50', '2025-10-28 16:49:18', '新鲜烘焙面包', '盒马面包', '15.5', '2025-10-28 16:49:18');
INSERT INTO `commodity` VALUES ('92bcd06d-3933-4425-b07b-44b22c35f2bd', '35', '2025-04-28 20:38:07', '鲜花', '鲜花', '10', null);
INSERT INTO `commodity` VALUES ('9c61fbfb-bbec-4c3f-870f-18f71da277e2', '30', '2025-10-24 11:25:11', '华为手机', '华为p80', '8999', null);
INSERT INTO `commodity` VALUES ('c25c4f8e-4185-4fc4-a6a3-578f0ff85b5e', '0', '2025-10-24 11:13:57', '苹果手机', '苹果16promax', '7999', null);
INSERT INTO `commodity` VALUES ('de73c5a7-6427-49bc-b96e-1d47a8f0e66e', '50', 'Tue Oct 28 18:34:44 CST 2025', '三星手机', '三星s25ultra', '9999', 'Tue Oct 28 18:35:06 CST 2025');
INSERT INTO `commodity` VALUES ('f17e7741-a90b-4153-92ce-540ec7bd1f5c', '50', '2025-04-28 20:37:31', '盒马面包', '盒马面包', '8.99', null);

-- ----------------------------
-- Table structure for company
-- ----------------------------
DROP TABLE IF EXISTS `company`;
CREATE TABLE `company` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of company
-- ----------------------------

-- ----------------------------
-- Table structure for distribution
-- ----------------------------
DROP TABLE IF EXISTS `distribution`;
CREATE TABLE `distribution` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `care` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `did` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `driver` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `status` int DEFAULT NULL,
  `time` datetime(6) DEFAULT NULL,
  `urgent` bit(1) NOT NULL,
  `vid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of distribution
-- ----------------------------
INSERT INTO `distribution` VALUES ('01c8fc12-11d6-4388-baf8-7de7c09070da', '111', '冰柜冷藏, 注意易碎, 防止高温, ', '7883bd7d-27e9-42e2-8977-2d41504a31b4', '张晓', '京A0010', '111', '1', '2025-04-25 09:53:06.000000', '', '751ecedf-2211-416f-9fc8-6c797ff51d16');
INSERT INTO `distribution` VALUES ('18973391-8100-4dd9-8b16-f78701b4cc40', 'xxxxxxxxxx', '注意易碎, ', '4fc77f59-a7b4-4f2b-be12-eb64d09f2bee', '小名', '京A0010', '1xxxxxxxxxx', '2', '2025-10-27 11:17:10.000000', '', '751ecedf-2211-416f-9fc8-6c797ff51d16');
INSERT INTO `distribution` VALUES ('3036e2cf-28db-4d4c-b52d-5f590540b74b', 'sad', '冰柜冷藏, ', 'ea3bba9b-cda6-438d-b196-7c81e97b5040', '王五', '京A0000', 'sad', '2', '2025-05-07 13:09:23.000000', '', 'ce33de59-9584-4161-a17e-9046399d14c6');
INSERT INTO `distribution` VALUES ('34f6fdc0-7bde-4345-93e3-57432c4e5248', 'xx省xx市xx县xx小区', '注意易碎, 冰柜冷藏, ', '7883bd7d-27e9-42e2-8977-2d41504a31b4', '张晓', '京A0010', '1888888888', '1', '2025-10-27 23:07:45.000000', '\0', '751ecedf-2211-416f-9fc8-6c797ff51d16');
INSERT INTO `distribution` VALUES ('3afcf814-ba18-4321-976c-6bd01b6a9e01', '11', '', '7883bd7d-27e9-42e2-8977-2d41504a31b4', '张晓', '京A0010', '11', '2', null, '\0', '751ecedf-2211-416f-9fc8-6c797ff51d16');
INSERT INTO `distribution` VALUES ('4728cdb2-9570-4104-af5a-caab58a33d8f', 'xxxxxxxx', '防止高温, ', '4fc77f59-a7b4-4f2b-be12-eb64d09f2bee', '小名', '京A0010', '1xxxxxxxxxx', '2', '2025-10-28 11:14:41.000000', '', '751ecedf-2211-416f-9fc8-6c797ff51d16');
INSERT INTO `distribution` VALUES ('77a6b6b9-682c-4aba-926b-6d7645895037', '2343', '注意易碎, ', 'ea3bba9b-cda6-438d-b196-7c81e97b5040', '王五', '京A0000', '234', '1', '2025-05-08 12:58:27.000000', '', 'ce33de59-9584-4161-a17e-9046399d14c6');
INSERT INTO `distribution` VALUES ('7c6c52fd-abbe-4505-a7c0-ea12f68ed6cb', 'asd345', '冰柜冷藏, ', 'ea3bba9b-cda6-438d-b196-7c81e97b5040', '王五', '京A0000', 'sd435345', '1', '2025-05-07 13:01:39.000000', '', 'ce33de59-9584-4161-a17e-9046399d14c6');
INSERT INTO `distribution` VALUES ('7ec1e684-186c-4ed7-8f91-e2a2600776bf', 'code51.cn', '冰柜冷藏, 防止高温, 注意易碎, ', '7883bd7d-27e9-42e2-8977-2d41504a31b4', '张晓', '京A0010', '15615615611', '2', '2025-05-04 09:46:48.000000', '', '751ecedf-2211-416f-9fc8-6c797ff51d16');
INSERT INTO `distribution` VALUES ('90672d13-31b0-4ed1-9ea9-48cd9039191e', 'xxxxxxxxxx', '注意易碎, ', '7883bd7d-27e9-42e2-8977-2d41504a31b4', '张晓', '京A0000', '1xxxxxxxxx', '2', '2025-10-27 11:25:49.000000', '', 'ce33de59-9584-4161-a17e-9046399d14c6');
INSERT INTO `distribution` VALUES ('924908f2-53c8-439e-a4a3-201774d7d8ee', 'xxxxxxxxxx', '注意易碎, ', '4fc77f59-a7b4-4f2b-be12-eb64d09f2bee', '小名', '京A0010', '18888888888', '1', '2025-10-28 11:04:38.000000', '', '751ecedf-2211-416f-9fc8-6c797ff51d16');
INSERT INTO `distribution` VALUES ('ae587e56-1d74-4edf-9707-ef7235eebd91', '324234', '冰柜冷藏, ', 'ea3bba9b-cda6-438d-b196-7c81e97b5040', '王五', '京A0000', '235352', '0', '2025-05-06 12:59:04.000000', '', 'ce33de59-9584-4161-a17e-9046399d14c6');
INSERT INTO `distribution` VALUES ('b6039fbb-63b9-4a19-8494-fe8fb30e7c99', 'xx省xx市xx县xxxx小区', '注意易碎, ', '7883bd7d-27e9-42e2-8977-2d41504a31b4', '张晓', '京A0010', '1888888888', '2', '2025-10-21 08:23:58.000000', '\0', '751ecedf-2211-416f-9fc8-6c797ff51d16');
INSERT INTO `distribution` VALUES ('ff9bcb29-5e7b-4462-b789-a92576711ef7', 'asd', '冰柜冷藏, ', 'ea3bba9b-cda6-438d-b196-7c81e97b5040', '王五', '京A0000', 'sd', '1', '2025-05-07 13:01:39.000000', '', 'ce33de59-9584-4161-a17e-9046399d14c6');

-- ----------------------------
-- Table structure for driver
-- ----------------------------
DROP TABLE IF EXISTS `driver`;
CREATE TABLE `driver` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `driving` bit(1) NOT NULL,
  `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `id_card` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `score` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `update_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of driver
-- ----------------------------
INSERT INTO `driver` VALUES ('4fc77f59-a7b4-4f2b-be12-eb64d09f2bee', 'xx省xx市xx县', '2025-10-23 23:33:19', '\0', '男性', '142327199602060102', 'A1 xxxxx', '小名', '1888888888', '6', null);
INSERT INTO `driver` VALUES ('7883bd7d-27e9-42e2-8977-2d41504a31b4', 'xxx', '2025-10-17 16:09:30', '\0', '男性', '123456789112345678', '苏A-12345', '张晓', '15915915911', '12', null);
INSERT INTO `driver` VALUES ('a4a88363-a438-4847-a4bf-b25e6fa5ae6e', 'xxx', '2025-04-21 13:38:53', '\0', '男性', '423423424', 'xxx', '李四', '13793242563', '12', null);
INSERT INTO `driver` VALUES ('ea3bba9b-cda6-438d-b196-7c81e97b5040', '', '2025-04-21 12:26:46', '\0', '男性', '4412242003423345534', '34', '王五', '231325345345', '12', null);

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `department` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `id_card` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `update_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES ('15b3ee2d-2ac7-431d-b96b-f6f50e87c1a1', '山西', '2025-10-17 15:43:50', '源码乐园', '男性', '142121100706627961', '王五', '1888888888', null);
INSERT INTO `employee` VALUES ('7db57373-a645-4742-a2ef-1a8103cb5ca4', 'csdn.sd', '2025-04-28 20:50:28', '源码乐园', '男性', '123435446576882324', '李四', '13823454657', null);
INSERT INTO `employee` VALUES ('f97bab9c-9635-4775-b118-558da8120c2f', 'code51.cn', '2025-04-28 20:49:09', 'A号仓库', '男性', '123456789012345678', '张三', '15615615611', null);

-- ----------------------------
-- Table structure for employee_copy
-- ----------------------------
DROP TABLE IF EXISTS `employee_copy`;
CREATE TABLE `employee_copy` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `department` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `id_card` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `update_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of employee_copy
-- ----------------------------
INSERT INTO `employee_copy` VALUES ('15b3ee2d-2ac7-431d-b96b-f6f50e87c1a1', '山西', '2025-10-17 15:43:50', '源码乐园', '男性', '142121100706627961', '王五', '1888888888', null);
INSERT INTO `employee_copy` VALUES ('7db57373-a645-4742-a2ef-1a8103cb5ca4', 'csdn.sd', '2025-04-28 20:50:28', '源码乐园', '男性', '123435446576882324', '李四', '13823454657', null);
INSERT INTO `employee_copy` VALUES ('f97bab9c-9635-4775-b118-558da8120c2f', 'code51.cn', '2025-04-28 20:49:09', 'A号仓库', '男性', '123456789012345678', '张三', '15615615611', null);

-- ----------------------------
-- Table structure for inventory
-- ----------------------------
DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `cid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `count` int DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `wid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of inventory
-- ----------------------------
INSERT INTO `inventory` VALUES ('08c1ee95-f3eb-487e-8984-8d7d29fff611', 'de73c5a7-6427-49bc-b96e-1d47a8f0e66e', '50', null, '三星s25ultra', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory` VALUES ('08d23ef9-ccc4-4269-9403-f69dd48342bd', '9c61fbfb-bbec-4c3f-870f-18f71da277e2', '30', null, '华为p80', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory` VALUES ('0ff7a831-0824-428d-b140-ab41de52d5a4', 'eda4215d-82d8-4a08-a3f3-981b19274006', '70', null, 'Apple', '9f2a2784-e182-4fdf-85e8-c3bde6d539d2');
INSERT INTO `inventory` VALUES ('101d78ad-9169-4e0a-ab3f-09ef25ccf5cb', '9794a6a5-5aef-470d-9835-3fcea8b1031e', '9', null, '鲜花', '7159a7e3-32c8-4c29-9608-039472ae47ac');
INSERT INTO `inventory` VALUES ('1eb35792-fd0e-4ff4-91a6-efac129731cc', '14864e86-c858-4027-b561-e484e70cd2fa', '50', null, '盒马面包', '7159a7e3-32c8-4c29-9608-039472ae47ac');
INSERT INTO `inventory` VALUES ('3c6f0c42-25e8-4ccb-b30a-2622d9740c87', '52fdac99-f224-469e-8af8-ed49c166bb23', '20', null, 'HUWAI MATE 30 Pro', '9f2a2784-e182-4fdf-85e8-c3bde6d539d2');
INSERT INTO `inventory` VALUES ('4a6a8f5a-f379-4e1d-b790-10e6dcf98899', '44efcfc2-89ba-4bfd-887d-674b338c5f93', '15', null, '小米16pro', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory` VALUES ('4dde7763-e5f1-41bd-a536-ac6b8a2191e1', '5b8441e5-5627-49eb-98ff-a0522253cbad', '49', null, '生鲜活鱼', '7159a7e3-32c8-4c29-9608-039472ae47ac');
INSERT INTO `inventory` VALUES ('5e23291a-03fa-49f1-9820-d92d592605af', '3ce4732f-61e1-4048-beb7-89dd7b3d99dc', '20', null, '小米15pro', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory` VALUES ('6ab58bd6-d9c4-4a24-9287-951ba209c49b', '1c0e25d3-db1f-4cda-93b1-81101d21dc5b', '106', null, 'iphone17promax1TB', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory` VALUES ('91d2e087-5446-49da-867d-e10970261a6a', 'c25c4f8e-4185-4fc4-a6a3-578f0ff85b5e', '0', null, '苹果16promax', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory` VALUES ('95513281-6733-4c07-98bd-8d3b26a340cb', '2757278d-fb76-496a-8bd1-7629fa6baf00', '13', null, '佳能Canon Eos R5 Mark II 机身', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory` VALUES ('ca7f8e97-1edf-4fc8-b9a1-783a7d053131', '5fcb392b-39aa-4381-b5ad-b1ccd8d5b74e', '20081', null, 'Mac', '9f2a2784-e182-4fdf-85e8-c3bde6d539d2');
INSERT INTO `inventory` VALUES ('d9afc86f-a782-4d00-9300-78484f72c117', '2283b0a2-5e0b-4c1e-b651-d2e3b51b87ee', '80', null, '手机支架', '9f2a2784-e182-4fdf-85e8-c3bde6d539d2');
INSERT INTO `inventory` VALUES ('e8e63bfa-be44-4cb8-abe3-0b6baf3159fa', 'b3a8f5a2-2dac-4194-b806-687b7f08e82f', '50', null, '鼠标', '9f2a2784-e182-4fdf-85e8-c3bde6d539d2');

-- ----------------------------
-- Table structure for inventory_record
-- ----------------------------
DROP TABLE IF EXISTS `inventory_record`;
CREATE TABLE `inventory_record` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `cid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `count` int DEFAULT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `type` int DEFAULT NULL,
  `wid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of inventory_record
-- ----------------------------
INSERT INTO `inventory_record` VALUES ('02a089e2-4c83-4bbe-960c-f7e9ea67d8b0', '1c0e25d3-db1f-4cda-93b1-81101d21dc5b', '50', '2025-10-27 21:49:01', '', 'iphone17promax1TB', '1', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory_record` VALUES ('0327e497-6349-4a33-816e-38f680bd48f4', 'b3a8f5a2-2dac-4194-b806-687b7f08e82f', '100', '2025-05-01 13:36:27', 'xxx', '鼠标', '1', '9f2a2784-e182-4fdf-85e8-c3bde6d539d2');
INSERT INTO `inventory_record` VALUES ('0ea21216-a91c-43ff-a785-84df4bd602dd', '9794a6a5-5aef-470d-9835-3fcea8b1031e', '10', '2025-05-04 09:49:49', '源码乐园专注提供优质源码，访问地址http://code51.cn', '鲜花', '1', '7159a7e3-32c8-4c29-9608-039472ae47ac');
INSERT INTO `inventory_record` VALUES ('0fcf2eed-a789-4316-b727-f94530f1c96c', '3ce4732f-61e1-4048-beb7-89dd7b3d99dc', '30', '2025-10-24 11:16:59', '小米手机', '小米15pro', '-1', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory_record` VALUES ('2c141734-d0d0-4f4e-bf66-ae60667f0709', 'eda4215d-82d8-4a08-a3f3-981b19274006', '20', '2025-05-05 00:01:09', 'ss', 'Apple', '1', '9f2a2784-e182-4fdf-85e8-c3bde6d539d2');
INSERT INTO `inventory_record` VALUES ('33b71330-d570-4e3c-8f1d-c66dbd13843d', '5b8441e5-5627-49eb-98ff-a0522253cbad', '1', '2025-05-04 09:43:33', '生鲜活鱼', '生鲜活鱼', '-1', '7159a7e3-32c8-4c29-9608-039472ae47ac');
INSERT INTO `inventory_record` VALUES ('40cfe0fa-aa15-42a8-b4f5-963a93d2a8a9', '9c61fbfb-bbec-4c3f-870f-18f71da277e2', '30', '2025-10-24 11:25:43', '华为手机', '华为p80', '-1', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory_record` VALUES ('4197fbfb-78a5-4cae-b825-955099da1943', '52fdac99-f224-469e-8af8-ed49c166bb23', '20', '2025-04-22 14:16:57', '', 'HUWAI MATE 30 Pro', '1', '9f2a2784-e182-4fdf-85e8-c3bde6d539d2');
INSERT INTO `inventory_record` VALUES ('51f8cee5-a238-41dc-b031-40b05cd83abf', '2283b0a2-5e0b-4c1e-b651-d2e3b51b87ee', '100', '2025-04-25 11:06:12', 'xxxxx', '手机支架', '1', '9f2a2784-e182-4fdf-85e8-c3bde6d539d2');
INSERT INTO `inventory_record` VALUES ('543b7855-91a5-4558-8f7b-dc7d27eb9766', 'de73c5a7-6427-49bc-b96e-1d47a8f0e66e', '50', '2025-10-28 18:35:06', '三星手机', '三星s25ultra', '1', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory_record` VALUES ('6d1e41a4-3f7e-440a-afe0-8c2682413f54', '2757278d-fb76-496a-8bd1-7629fa6baf00', '13', '2025-10-18 08:21:53', '佳能高端相机', '佳能Canon Eos R5 Mark II 机身', '1', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory_record` VALUES ('6ff4d4dc-326c-4fd3-a782-06b799f9931a', '5fcb392b-39aa-4381-b5ad-b1ccd8d5b74e', '21312', '2025-04-27 20:36:52', '213', 'Mac', '1', '9f2a2784-e182-4fdf-85e8-c3bde6d539d2');
INSERT INTO `inventory_record` VALUES ('7c5894f6-6941-4c33-9e69-00ae56b27acb', '9c61fbfb-bbec-4c3f-870f-18f71da277e2', '60', '2025-10-24 11:25:33', '华为手机', '华为p80', '1', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory_record` VALUES ('961d49b1-8422-4a71-9b6c-a33cf59cfd90', 'c25c4f8e-4185-4fc4-a6a3-578f0ff85b5e', '15', '2025-10-24 11:14:29', '苹果手机', '苹果16promax', '1', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory_record` VALUES ('972f09c1-258e-459d-b19f-5ea7f773b7e8', '5b8441e5-5627-49eb-98ff-a0522253cbad', '50', '2025-04-24 09:43:24', '生鲜活鱼', '生鲜活鱼', '1', '7159a7e3-32c8-4c29-9608-039472ae47ac');
INSERT INTO `inventory_record` VALUES ('a5f8d137-8cde-41dd-98eb-aa62b4193c9d', '44efcfc2-89ba-4bfd-887d-674b338c5f93', '15', '2025-10-24 11:14:18', '苹果手机', '小米16pro', '1', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory_record` VALUES ('a7d5f319-65f0-420b-b28b-a24323cde61a', 'c25c4f8e-4185-4fc4-a6a3-578f0ff85b5e', '15', '2025-10-24 11:14:37', '苹果手机', '苹果16promax', '-1', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory_record` VALUES ('b1bdf63a-3c77-45b6-8841-8d149986f709', '5fcb392b-39aa-4381-b5ad-b1ccd8d5b74e', '1231', '2025-04-30 20:37:18', '123', 'Mac', '-1', '9f2a2784-e182-4fdf-85e8-c3bde6d539d2');
INSERT INTO `inventory_record` VALUES ('b34133c3-2192-43a1-900d-fabc68ff3eed', '9794a6a5-5aef-470d-9835-3fcea8b1031e', '1', '2025-04-24 09:49:58', '源码乐园专注提供优质源码，访问地址http://code51.cn', '鲜花', '-1', '7159a7e3-32c8-4c29-9608-039472ae47ac');
INSERT INTO `inventory_record` VALUES ('b49e8ba9-30c8-4794-ab1f-c807c5dbc8c2', '2283b0a2-5e0b-4c1e-b651-d2e3b51b87ee', '20', '2025-05-01 11:08:25', 'xxxx', '手机支架', '-1', '9f2a2784-e182-4fdf-85e8-c3bde6d539d2');
INSERT INTO `inventory_record` VALUES ('d29a1d17-dcaf-4330-8d8e-cbbcc346db63', '44efcfc2-89ba-4bfd-887d-674b338c5f93', '30', '2025-10-24 11:09:27', '小米手机', '小米16pro', '-1', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory_record` VALUES ('d31b31f1-8e1f-43a4-afa7-6a8bb06a61c9', '3ce4732f-61e1-4048-beb7-89dd7b3d99dc', '50', '2025-10-24 11:16:51', '小米手机', '小米15pro', '1', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory_record` VALUES ('d7fe3633-dd8f-4ee5-9a68-8a57d64251f0', '1c0e25d3-db1f-4cda-93b1-81101d21dc5b', '26', '2025-10-18 08:21:15', '最新款苹果手机', 'iphone17promax1TB', '1', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory_record` VALUES ('dc97f770-6e79-46fe-a650-81d635e16581', '1c0e25d3-db1f-4cda-93b1-81101d21dc5b', '50', '2025-10-27 21:49:00', '', 'iphone17promax1TB', '1', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory_record` VALUES ('e3f78f05-a309-456a-a93f-83f5b72197c5', '14864e86-c858-4027-b561-e484e70cd2fa', '50', '2025-04-24 09:49:39', '源码乐园专注提供优质源码，访问地址http://code51.cn', '盒马面包', '1', '7159a7e3-32c8-4c29-9608-039472ae47ac');
INSERT INTO `inventory_record` VALUES ('e8e563aa-ba83-446e-97a4-ebb8c44aac9c', '44efcfc2-89ba-4bfd-887d-674b338c5f93', '30', '2025-10-24 11:09:21', '小米手机', '小米16pro', '1', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');
INSERT INTO `inventory_record` VALUES ('e99dd646-bbd0-42d9-a315-72d081af8544', 'b3a8f5a2-2dac-4194-b806-687b7f08e82f', '50', '2025-04-26 13:39:15', '50', '鼠标', '-1', '9f2a2784-e182-4fdf-85e8-c3bde6d539d2');
INSERT INTO `inventory_record` VALUES ('fa51e39c-f210-4bcf-928f-e1036c6bdffb', 'eda4215d-82d8-4a08-a3f3-981b19274006', '50', '2025-04-28 09:27:00', '入库苹果手机', 'Apple', '1', '9f2a2784-e182-4fdf-85e8-c3bde6d539d2');
INSERT INTO `inventory_record` VALUES ('fb740732-2502-4a50-8568-8b17d217fb9b', '1c0e25d3-db1f-4cda-93b1-81101d21dc5b', '20', '2025-10-24 09:35:01', '销售出库 - 销售单号: d16570c0-8661-4c3c-ad60-000653fbb929，客户: 小名同学', 'iphone17promax1TB', '-1', 'ba6d7573-75e4-4e11-82a8-cf89991f4659');

-- ----------------------------
-- Table structure for login_log
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `date` datetime(6) DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of login_log
-- ----------------------------
INSERT INTO `login_log` VALUES ('02ac3fa5-cc02-4803-afb7-3d2862026a3d', 'Chrome', '2025-05-25 13:12:03.616000', '470372007@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('034924b9-ad89-4a80-8278-230202e5333d', 'Chrome', '2025-04-28 20:30:46.800000', 'jiegod_8ck@126.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('0479b340-6bc5-465b-a9bd-73f5c7bd2fb3', 'Chrome', '2025-10-26 23:26:19.811000', '2159609711@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('050b8b95-f2cb-4a85-9837-3947680726a1', 'Chrome', '2025-10-18 19:40:38.297000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('07794948-9af2-462a-9fa5-869053c6c468', 'Chrome', '2025-04-28 20:37:14.340000', 'jiegod_8ck@126.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('077e0b91-6197-4f8c-b5a4-40ea56976757', 'Chrome', '2025-10-17 21:57:33.692000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('08aaaab6-72b5-4295-8f00-3939037d8ec9', 'Chrome', '2024-06-26 23:37:05.851000', '1638752551@163.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('09498a5f-ed1e-4d0e-9ff6-206bd7b5ece6', 'Chrome', '2025-10-18 16:52:08.120000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('0a2a641d-c7ce-49fc-a110-87e0695f58fd', 'Chrome', '2025-10-28 18:55:05.675000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('0b0f877a-441d-4cac-9375-592db5ec6b7b', 'Chrome', '2025-05-08 23:57:37.895000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('0bdfc3dc-8a5d-4e32-bad5-b955307c2684', 'Chrome', '2025-10-19 19:19:32.423000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('0d99c0c4-4f40-4fa7-8ca0-5195136b9a7c', 'Chrome', '2024-06-13 17:21:03.305000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('0f1838b9-b7f6-490d-87c0-d6c6e81645d0', 'Chrome', '2025-10-19 16:25:35.296000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('105a7f99-de31-4fa3-8d34-7e1b92a987dd', 'Chrome', '2025-10-17 16:07:27.297000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('1079e043-1a69-44f2-8ca2-9beeda38a1e0', 'Chrome', '2024-06-27 14:27:34.996000', '1638752551@163.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('121ae1ef-be5d-443a-b1de-6578302d9934', 'Chrome', '2022-12-06 15:05:58.830000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('122d3908-887e-4783-8998-36c6a45c1579', 'Chrome', '2025-10-18 17:59:38.984000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('140f7574-2656-4479-a5ee-9e3107248a29', 'Chrome', '2025-10-26 23:31:56.767000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('151e2f32-ae92-43b8-92e2-fa7fd78e4374', 'Chrome', '2025-05-28 10:05:17.133000', '470372007@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('15ecac40-4314-479d-9610-bbc257c85c94', 'Chrome', '2025-10-26 23:16:00.627000', '2159609411@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('16a8222f-e997-4419-8dab-5e1c2f3b0df4', 'Chrome', '2025-10-18 17:29:29.039000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('1810ddf7-f1b2-439b-a1c9-36a87b259cdd', 'Chrome', '2025-10-28 18:33:29.578000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('1938c3da-190d-495d-87e8-1101fe123973', 'Chrome', '2025-10-24 10:00:37.399000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('19a9cd96-ea5f-4541-af21-218b4089f646', 'Chrome', '2025-10-30 17:36:23.876000', 'jiegod_8ck@126.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('1a4ee61a-98e7-45b5-b3c9-3ec1d9b526dd', 'Chrome', '2025-10-24 22:32:26.000000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('1a5d7342-3a90-4d73-bd7a-d1d2955e8c6b', 'Chrome', '2025-10-19 16:38:41.460000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('1a97c0c8-47a0-4a09-bc73-ea5dbb3cafaf', 'Chrome', '2025-04-02 15:24:09.648000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('1af75d04-fca0-49a1-89d7-95aa9212a24d', 'Chrome', '2022-12-17 23:15:17.620000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('1b62cfec-329d-461c-ad73-b5fb53ad103d', 'Chrome', '2025-10-17 15:36:37.273000', '123@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('1cbffa60-559e-4706-ae97-81f1ee526f59', 'Chrome', '2024-06-26 23:31:11.632000', '1638752551@163.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('1cc5b9fc-a98b-4159-9b26-bb7978bbebbf', 'Chrome', '2025-10-17 15:36:49.897000', '123@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('1cd90c0f-8b99-40b9-9778-e667c30d9bbf', 'Chrome', '2022-12-18 11:28:31.718000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('205191b2-b077-4a36-93e0-76e5b5310997', 'Chrome', '2025-04-13 21:21:47.687000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('243934e3-5078-47ec-a58e-0410eeeabf1a', 'Chrome', '2025-10-18 18:12:46.373000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('2463ee26-76e7-43c0-8699-36101366c9db', 'Chrome', '2025-10-24 11:03:37.706000', '2159609711@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('25ce9ab2-bea4-4342-97bd-be6598a2920c', 'Chrome', '2025-10-19 23:27:49.882000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('26c7060e-32ed-4e4f-91f6-524c71677b69', 'Chrome', '2025-10-23 22:46:16.790000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('299be19d-ef75-4125-b909-26fc153e2350', 'Chrome', '2025-10-18 16:43:35.727000', 'lop@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('2aab1645-fc48-4bc5-a085-3b84a4f8adb1', 'Chrome', '2025-10-18 16:43:36.005000', 'lop@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('2b769135-a2bf-4bab-9a88-0f597968483c', 'Chrome', '2025-10-18 16:43:35.584000', 'lop@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('2cf47de1-4a1b-470f-8d60-eaa27bf6e2ba', 'Chrome', '2025-10-17 15:37:14.893000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('2d2f9d33-50ce-4f8a-88e9-1e3a50b23b4d', 'Chrome', '2025-10-18 08:42:08.350000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('2da246ec-d2de-4969-b236-bc86c47454c2', 'Chrome', '2025-10-18 16:41:06.017000', '123@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('2eebc033-9870-4303-90a7-1b23b4514cad', 'Chrome', '2024-06-13 17:24:57.791000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('2f531506-1c18-40ae-a7dc-3a0d6696e144', 'Chrome', '2025-10-19 17:10:51.539000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('2faf2285-686e-46b6-9fe9-d49cfef0a346', 'Chrome', '2025-10-19 23:15:38.178000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('31565479-8cd3-4fd1-a031-cd1ad9df9feb', 'Chrome', '2025-10-24 09:24:48.519000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('321df39a-c747-4b76-93cc-8fec5d8ead43', 'Chrome', '2025-10-19 22:19:57.355000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('32a15dd2-821a-4273-ac49-fb853b5518c0', 'Chrome', '2025-10-18 16:41:37.431000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('34058a9a-a40c-442d-ad25-fbe39ebcaa68', 'Chrome', '2025-10-19 22:50:40.574000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('3461c279-a33b-4e0a-b466-96deef0d9839', 'Chrome', '2025-10-18 17:19:21.692000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('35598255-6352-4606-8924-6daa46086313', 'Chrome', '2025-10-24 11:24:47.360000', '2159609711@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('35a131b5-4157-4e54-ae1d-cf3ba0687f30', 'Chrome', '2025-10-24 11:00:41.721000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('3676fdab-284e-4243-9c96-b0f5f0133ca4', 'Chrome', '2025-10-18 17:05:01.024000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('36b25d1b-ba49-4673-abd0-16a136930305', 'Chrome', '2022-12-14 19:56:04.265000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('376c5fb8-073d-49a3-9693-2b88d089fc31', 'Chrome', '2023-02-24 09:52:32.864000', 'code51User@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('39e7c592-aac3-4c62-b2fe-af96f3279188', 'Chrome', '2025-10-18 18:01:11.445000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('3d581a99-a62a-4049-af8b-b42a47aa3283', 'Chrome', '2025-10-18 16:41:55.144000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('3f5c4d80-fd7e-4a2a-9ea5-041c4bd380a2', 'Chrome', '2025-05-28 10:07:44.249000', 'jiegod_8ck@126.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('3f6893eb-b0d8-49ff-bdea-a4035e4fd75c', 'Chrome', '2025-10-18 16:51:32.630000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('3f9b2ecf-fac0-4f52-aa7f-0a202f09a841', 'Chrome', '2022-12-19 13:10:24.253000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('40b51d68-749e-4859-b72f-45fc635f9d0a', 'Chrome', '2022-12-13 12:00:23.181000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('40bfd947-ee9e-45c3-9d83-1bbc0db50fb6', 'Chrome', '2025-10-18 18:06:53.693000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('41fd9378-7c05-4402-bbde-d043a5f3d66f', 'Chrome', '2025-10-27 19:29:10.460000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('42ae88db-e7eb-4078-8881-bc4aa7ac4a69', 'Chrome', '2025-10-19 16:20:43.156000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('4362811c-4c29-46f6-a9f6-612b3bb7c194', 'Chrome', '2025-10-18 16:52:58.194000', '2361471705@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('4410c998-32f4-4ea5-856b-035ab53188f7', 'Chrome', '2025-10-28 18:38:47.078000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('446024f7-1c22-472c-bd13-2522093cbdab', 'Chrome', '2025-10-18 17:04:54.975000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('44dc898e-c672-4d15-95df-a3016282f13a', 'Chrome', '2025-10-18 14:58:07.303000', '2361471705@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('45126c0f-bf37-44c9-884d-9d6102eb358a', 'Chrome', '2025-10-27 18:32:34.652000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('45481ec0-2109-4693-b5c7-c8ca2bb8925a', 'Chrome', '2025-10-26 23:16:20.794000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('47d84267-32a6-4f5f-8047-aa443a3f0dc6', 'Chrome', '2024-06-27 14:30:40.338000', '1638752551@163.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('48ae43f7-153b-4d6e-bba2-8e93ed467ab0', 'Chrome', '2025-10-19 22:59:03.638000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('4c3fe589-07c2-4003-b463-dfd5f96d6722', 'Chrome', '2023-02-03 10:08:02.093000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('4d65ab97-4ea8-4842-aace-d83aba15fd05', 'Chrome', '2025-10-24 11:15:59.506000', '2159609711@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('4ee4cba9-c58d-4769-9ec0-99f23a9a728c', 'Chrome', '2025-10-18 20:06:02.819000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('4f6d9897-d2ed-47d4-bf5a-cea7cd493f5a', 'Chrome', '2025-10-18 20:28:07.774000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('4fec84eb-fc78-406e-9a31-2b99f1a500b9', 'Chrome', '2025-04-13 21:18:59.244000', '470372007@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('5119980b-d064-42be-9956-1207572fffe5', 'Chrome', '2023-02-24 09:55:46.970000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('52517f1f-6acc-4618-a94c-17d493288200', 'Chrome', '2025-10-19 21:43:37.980000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('52ce9077-5bf6-4b79-9c98-800f18fb88b8', 'Chrome', '2025-10-27 21:48:11.246000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('52d2cd7a-e83d-45a2-be06-30963f26e7ed', 'Chrome', '2025-04-04 14:33:32.462000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('5343d5ba-5d15-4f4e-9f0d-20c9968d1116', 'Chrome', '2025-10-18 17:56:46.402000', '2361471705@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('535cea43-2787-4ade-8dc1-17b2241e3c56', 'Chrome', '2025-10-23 22:45:28.147000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('54e8a455-bac5-42f8-955e-3b142797efce', 'Chrome', '2025-10-27 19:56:07.556000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('581be972-f9e5-42de-a121-4cf4e5eb578b', 'Chrome', '2025-04-02 15:26:40.322000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('59251d8d-1753-435c-bfb1-5d1a91440e2e', 'Chrome', '2025-05-09 12:59:44.664000', '470372007@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('5ae226c2-46cf-4c89-a070-4198414b8a2f', 'Chrome', '2025-10-28 13:06:27.914000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('5cb8f2bf-43cd-4398-9b91-5ffbb919e42d', 'Chrome', '2025-10-18 18:15:54.681000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('601762b5-aaae-4391-ac6e-52d920126e80', 'Chrome', '2025-10-18 17:03:47.497000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('61b7ee10-7bad-4aa7-b55e-3aaac59a5809', 'Chrome', '2025-10-18 16:43:35.894000', 'lop@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('62fa0174-1ff9-40cc-80e9-14da34f5360c', 'Chrome', '2025-10-19 16:29:30.247000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('6344c6e0-e69c-4237-80d6-c72ddba71368', 'Chrome', '2025-10-30 17:28:24.814000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('663c5947-5873-4b84-b6f2-1e8957880bb1', 'Chrome', '2024-06-27 14:31:29.459000', '1638752551@163.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('68ff4747-294c-4f18-b665-5c3ea38e7fb8', 'Chrome', '2025-10-28 14:05:54.675000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('6a172982-386a-4f04-9a24-c8f5e07bdd4d', 'Chrome', '2025-10-18 17:27:20.579000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('6a72e93f-6366-49c2-ae75-976ecb453d33', 'Chrome', '2025-10-19 22:30:05.972000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('6c08c4d4-de44-476e-a5a8-8089eee7a46c', 'Chrome', '2025-10-18 19:57:22.210000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('6cbe1050-6f2b-4b70-b761-e7997e01a9dc', 'Chrome', '2023-01-11 13:34:48.773000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('6d6cc80a-2cb9-4865-939b-5d1c5b2f9e07', 'Chrome', '2025-10-23 22:50:26.513000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('6e48623d-ba55-41ee-8bba-6abc331ebbd3', 'Chrome', '2025-10-24 11:16:04.286000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('6fb70146-8272-49c6-bc19-81242061557b', 'Chrome', '2023-02-23 11:17:18.880000', 'testUser@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('708b95f6-2fb3-491a-a3d0-c3311caa43a5', 'Chrome', '2025-05-28 10:07:28.542000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('7115039f-d44c-4f37-9308-c3c1524aa68d', 'Chrome', '2025-10-19 16:56:23.634000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('71e7c018-3e5e-4b7a-bde4-44b3877ce0e0', 'Chrome', '2025-10-28 18:04:28.236000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('721881e2-4325-4649-92c6-52e487026522', 'Chrome', '2025-10-19 16:15:28.320000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('7382f8d8-a422-4300-8fee-e530ea669467', 'Chrome', '2025-10-23 16:54:58.897000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('7445fd1b-a95d-43eb-9fb1-72cf20164a88', 'Chrome', '2024-06-27 14:23:39.285000', '1638752551@163.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('7537b2d0-f5bb-4519-8583-0495e38b1bbc', 'Chrome', '2025-10-19 16:34:37.983000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('76037af5-135a-4004-bfc3-834901879c51', 'Chrome', '2025-10-24 11:13:24.516000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('765e2493-8902-47dc-9b1c-6c14bbab5642', 'Chrome', '2025-04-05 23:45:10.426000', 'testUser@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('76a9a483-cff9-4773-b7e2-ba45c737fd76', 'Chrome', '2025-05-27 18:12:26.054000', '470372007@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('7803a14a-e261-480d-ae56-1d2584423e86', 'Chrome', '2023-02-24 09:37:02.360000', 'testUser@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('79deef74-d7f2-4ff5-9ee8-8d539bfc1c78', 'Chrome', '2025-10-24 09:40:23.731000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('7b021cd2-b600-4b4e-8217-e7d70265c07f', 'Chrome', '2025-10-19 16:59:41.888000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('7bce7319-ef6a-487d-b86e-71a5780c06e5', 'Chrome', '2025-10-30 16:11:50.102000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('7bcf4f8f-5e09-4975-b2a7-2f440f4b3e8e', 'Chrome', '2025-10-28 18:57:29.952000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('7cf7a6f6-ffa8-410d-b9c2-19dd7b65e9fd', 'Chrome', '2025-10-18 17:06:44.162000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('7ea20bc4-eced-4f41-a705-a3fa184f4bf9', 'Chrome', '2025-10-28 18:00:12.781000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('7ed7f9af-1b01-4945-ae0d-1d4e410f3d33', 'Chrome', '2025-10-21 14:39:13.608000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('7fb19f8e-3c84-416e-8087-dab0be63a0d8', 'Chrome', '2025-10-18 17:29:13.796000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('82707f71-f561-4d9b-aac5-460833fe73ce', 'Chrome', '2025-05-28 10:13:17.111000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('83ae8dfd-9547-4557-ada6-19eabf3efaf8', 'Chrome', '2025-10-19 19:42:38.211000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('85301f24-d3e7-4349-8d2b-27c3ac3ee0d2', 'Chrome', '2022-12-13 14:26:11.472000', 'abc@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('859f2b6d-416d-4688-a96b-705ad8d30341', 'Chrome', '2025-10-19 22:50:32.777000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('86196c81-6b24-42f9-9c25-7c1326fc807f', 'Chrome', '2025-10-19 22:52:29.056000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('86837f0c-8252-4890-9e46-f897362c361d', 'Chrome', '2025-10-19 17:25:57.605000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('879f7917-8182-4b98-94d3-081a9497e89b', 'Chrome', '2022-12-18 11:27:24.102000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('87eca39d-fcda-4674-b48f-5a2edd7c5c40', 'Chrome', '2025-10-18 18:14:27.730000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('8915ba3f-74dc-434b-af25-cf07dac74ded', 'Chrome', '2022-12-12 23:37:24.703000', 'admin@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('899f8c58-0d5b-46e0-a784-1c3f448719e7', 'Chrome', '2022-12-17 23:47:52.145000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('89b1a358-bbae-4a2d-adff-feba5dcdce2f', 'Chrome', '2025-10-30 17:27:04.303000', 'lop@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('8bdefa09-acdc-48f1-91f3-e5b22fd42a42', 'Chrome', '2025-10-17 20:02:31.540000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('8d46124b-548e-4f8c-8719-56cf3226c55b', 'Chrome', '2022-12-18 17:09:53.910000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('917934f4-9f8e-48aa-b59e-d174248ed7b6', 'Chrome', '2023-02-16 10:30:12.742000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('923da852-4325-4157-b428-c7729bc4c0e1', 'Chrome', '2025-10-18 15:03:54.918000', '2361471705@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('93467357-7699-42f2-957b-a0a1a4cdc13d', 'Chrome', '2022-12-06 15:34:35.701000', 'adc@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('93b213c5-e2ec-4558-af3e-12b2bc8f5217', 'Chrome', '2022-12-14 17:51:18.412000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('947b62e7-aeb1-46c3-9b0b-8e257cc72aac', 'Chrome', '2023-01-11 10:15:23.854000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('9482f3dd-7e8a-444b-ae95-596076056592', 'Chrome', '2022-12-12 23:37:31.175000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('95944d59-af29-4de6-8c81-dc80c9b26fcd', 'Chrome', '2025-10-18 18:12:35.377000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('95cfa26f-4b41-4132-93e5-ac788e9dd79c', 'Chrome', '2025-10-18 11:54:46.055000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('96943593-cd86-4823-93a3-f3bab760ee8b', 'Chrome', '2025-10-18 16:52:08.823000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('97140f13-80cc-4200-ac4e-e8e39290ca00', 'Chrome', '2023-02-24 09:37:52.029000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('97db4f45-e842-4822-9410-54fb04bfb042', 'Chrome', '2022-12-17 11:47:49.852000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('9911b8c0-47ba-4397-9274-d86faf32bbb8', 'Chrome', '2025-10-19 16:34:01.330000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('9961cc66-63ad-47c6-9c1e-c81cb08ed949', 'Chrome', '2023-02-15 20:57:44.695000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('9b9ff7e0-c6ea-406c-8b10-3f9e22db1ee6', 'Chrome', '2025-10-26 23:26:24.281000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('9c204dd9-b2dd-43b8-858f-f511e73c1508', 'Chrome', '2025-10-25 08:39:37.884000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('9c95e738-099b-4533-b155-e19704c358c3', 'Chrome', '2025-10-18 18:11:07.002000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('9cfe7454-5037-4e01-9c6d-0e33977b9242', 'Chrome', '2025-10-18 17:04:36.285000', '2361471705@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('9d827736-01fd-4d3e-b971-4554944c9356', 'Chrome', '2022-12-17 23:06:03.771000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('9d8a3d3c-735a-4bfb-a22c-bfb0a4fe9264', 'Chrome', '2025-10-18 17:18:27.526000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('9f8bab2c-eb56-45d4-af95-5ca5d1e4635b', 'Chrome', '2025-10-18 16:52:55.749000', '2361471705@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('a149603d-ac88-4b85-a860-188036776615', 'Chrome', '2023-01-11 13:43:06.172000', '123@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('a222e803-e1ba-4706-a9c4-b3abca12a8b9', 'Chrome', '2023-02-23 11:11:14.330000', '123@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('a2c600ff-cce8-482f-8bb4-25552040af00', 'Chrome', '2025-10-30 17:27:14.976000', 'lop@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('a2cc91cb-a26b-4b08-a190-81ab4b90a09b', 'Chrome', '2025-10-25 21:49:55.601000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('a333dea0-da28-4bb8-81db-9a31d9c62da7', 'Chrome', '2025-10-28 13:06:01.180000', '2179609711@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('a3b363b4-5f94-4fec-8f7c-7b4fa7e41fb9', 'Chrome', '2025-10-30 17:18:55.446000', 'testUser@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('a4fef761-163f-461d-9c8a-021e22414d74', 'Chrome', '2025-10-26 22:56:13.237000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('a603b785-99a5-466a-8d85-2520e6d86ef2', 'Chrome', '2025-04-05 23:38:18.071000', 'abc@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('a6a89c8e-c40f-4e3e-babe-cb398735770a', 'Chrome', '2025-05-09 13:06:53.981000', '470372007@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('a6fde4d3-ea66-401a-8478-6ec929a11657', 'Chrome', '2025-10-18 17:34:27.194000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('a7684b32-0a9b-4c1d-9bcd-07386cbd8a8c', 'Chrome', '2025-04-28 20:09:49.054000', '470372007@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('a82f5725-da5d-49b3-b7b5-90b544ae1296', 'Chrome', '2023-01-11 09:22:38.737000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('a87a5427-875b-4916-8aea-0c1927c5960c', 'Chrome', '2025-10-18 16:42:45.377000', 'lop@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('a9d7b4e3-f0f3-4a4e-991b-358941a26a3c', 'Chrome', '2025-10-18 15:03:48.016000', '2361471705@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('aaa79c3e-1d3a-4376-a831-3b81e7b51545', 'Chrome', '2025-10-18 16:52:07.736000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('ab226923-8a54-4338-9986-24dd4d42b54c', 'Chrome', '2025-10-18 20:23:06.818000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('ab263153-d05d-4f51-96cf-c4ff4cad1391', 'Chrome', '2025-10-19 22:43:01.242000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('ac056859-b7cc-41ec-80a1-b37e0534d008', 'Chrome', '2025-10-18 18:07:05.440000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('ad367cf2-a88f-4775-9c7f-1607df42bae5', 'Chrome', '2025-10-19 16:50:00.998000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('af79e58d-be7a-48ac-8e51-24b2fbd7e0eb', 'Chrome', '2025-10-18 17:41:23.319000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('b1ec7d00-7c27-4248-b1b8-6082167a7c7b', 'Chrome', '2025-10-30 15:45:24.134000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('b234f264-f7a9-4cfa-bf81-2d5207226d08', 'Chrome', '2025-10-19 22:13:24.944000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('b2cbf61b-f7bc-454f-8a12-83788da81081', 'Chrome', '2025-10-18 16:48:59.266000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('b30b9c6f-251b-49a1-9835-c8536f5c23bb', 'Chrome', '2022-12-12 23:36:42.313000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('b5eca1f8-f73d-4d1f-8537-e40e4b057051', 'Chrome', '2025-05-28 10:08:04.472000', '470372007@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('b75986d2-f21a-432a-8ac3-d5169ac89257', 'Chrome', '2025-10-18 17:55:56.587000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('b8ff3c17-4717-47bb-941b-0247181141ee', 'Chrome', '2025-10-24 11:13:18.521000', '2159609711@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('b965a1cb-a354-41db-8099-b217505c4dc4', 'Chrome', '2022-12-06 15:10:23.878000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('ba6420ef-4e60-4998-9c6f-4208ae2b54af', 'Chrome', '2025-10-18 19:55:51.882000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('be9cf0ab-78cb-40d9-8cb1-d5fcc5e31107', 'Chrome', '2024-06-27 14:23:30.151000', '1638752551@163.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('bf7f30c3-c591-4d86-8f30-fa5b308e6070', 'Chrome', '2025-10-18 08:09:38.568000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('c1da4a88-646f-4d87-8e15-cfc3d8f72e05', 'Chrome', '2025-10-19 21:55:44.202000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('c1f7f2e3-6355-4c91-91f2-58cacae7e463', 'Chrome', '2023-01-11 09:10:50.767000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('c303d974-e6fc-41ad-bed5-2639305fe504', 'Chrome', '2025-10-19 20:07:55.183000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('c328f8ba-7674-416c-bc68-e61a363e29fc', 'Chrome', '2025-10-20 08:15:59.241000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('c533001c-1c9d-41b3-a4cc-6468facf4298', 'Chrome', '2025-10-19 17:06:02.666000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('c56981be-71bf-4efd-b613-29fe051e0732', 'Chrome', '2025-10-18 16:56:21.402000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('c5e3b257-7e85-47de-b2c5-6feba7a7046e', 'Chrome', '2025-10-18 16:52:03.611000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('c7c5c79b-e80a-425c-a937-baff112c996e', 'Chrome', '2025-10-18 20:07:24.128000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('c7e666b4-bdac-45e8-8731-05f3274f7787', 'Chrome', '2022-12-14 17:37:48.504000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('c8ea636e-f6fb-492c-8d58-bbc1105dc5d2', 'Chrome', '2025-10-18 14:58:20.182000', '2361471705@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('ca29dc4c-cd73-4c14-b2cd-5bc6df402339', '谷歌浏览器', '2025-10-18 19:42:46.700000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('ca38e16e-ba6f-45e7-8a46-fb78abbbf9cc', 'Chrome', '2023-02-03 09:43:42.628000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('cbf4e04f-2d40-4384-87dd-7767ae103c6b', 'Chrome', '2022-12-06 15:09:47.524000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('cc34263b-7082-4e24-805f-947d289c3c61', 'Chrome', '2023-02-20 19:50:49.392000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('cc6214d2-ecb5-47d3-906d-c9753c49de0e', 'Chrome', '2023-02-24 09:40:11.920000', 'code51User@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('ccf96b7a-7fda-49d4-92da-1d830a4b4290', 'Chrome', '2025-06-10 18:38:44.484000', '470372007@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('cd118b46-eb0a-4320-9550-98d8051dbf97', 'Chrome', '2025-10-20 15:00:58.110000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('cd7fdd9b-0778-43a2-87c3-7acbd41fb164', 'Chrome', '2025-10-18 20:00:42.813000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('cdf5c5c0-0c57-498b-8fb6-fd8d3ba78e70', 'Chrome', '2025-10-18 16:41:19.917000', '123@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('ce5c023a-acc2-47dd-9fa6-09d3e1cf2b00', 'Chrome', '2025-10-19 16:39:35.402000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('ce77ed32-3d96-4ff9-9d91-cedc339bf116', 'Chrome', '2025-10-18 16:42:04.232000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('cecf1e00-2ddc-4054-b381-ab864d907838', 'Chrome', '2025-10-20 15:00:21.158000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('cef1f85a-f542-46a7-946b-d940e2e48d7b', 'Chrome', '2025-10-19 22:47:36.581000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('cf147c1d-366f-4334-ad8e-18293180573b', 'Chrome', '2025-10-19 22:31:22.956000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('d05b90d1-fe63-4958-aba2-c61de53da6a2', 'Chrome', '2025-10-18 08:04:43.432000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('d12a7277-c2bc-4b65-86de-0eb04d544163', 'Chrome', '2025-10-19 17:02:45.954000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('d55f21f4-01e6-45a5-abb9-a9d61de1f4da', 'Chrome', '2023-02-20 19:50:41.017000', 'admin@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('d59f54e9-307e-429f-b14a-3cf145af7d04', 'Chrome', '2025-10-19 08:58:37.239000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('d5e26b05-a16f-41af-85b2-86c62a26d857', 'Chrome', '2025-10-18 18:00:16.263000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('d629f3ce-1b09-44b7-9382-0c4959617b50', 'Chrome', '2023-02-24 09:51:17.260000', 'testUser@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('d87aea36-3cde-4f80-b042-ceae8a6c398f', 'Chrome', '2023-02-24 09:40:33.249000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('d97ab70d-29cf-41d6-ad9b-a999af0dbc07', 'Chrome', '2025-10-19 08:35:44.179000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('db10897f-6f28-4416-9ba3-8194f45285af', 'Chrome', '2025-10-24 09:21:13.014000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('db72edf7-bd8f-4088-96d7-3bea7895ff24', 'Chrome', '2025-10-26 23:54:42.953000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('db740aa5-2117-4a0b-8ecf-02abd9309021', '谷歌浏览器', '2025-10-18 19:43:53.756000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('dcf90538-9bea-4090-b231-99fb945aac1d', 'Chrome', '2025-10-24 11:03:41.742000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('dea012f0-04d0-45af-b6b8-82591134f0bc', 'Chrome', '2024-06-27 14:25:05.543000', '1638752551@163.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('df8b84c2-e7a7-4c16-a98a-a359ef8b1eca', 'Chrome', '2025-10-18 20:25:50.280000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('e175459e-10ad-4a9e-98a8-9cb72231b73b', 'Chrome', '2025-10-19 23:21:30.118000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('e1a5b794-2f16-4bf3-8db1-924755326af0', 'Chrome', '2022-12-17 23:33:24.419000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('e1c39162-ec7f-45ac-a525-f547b2ce7e80', 'Chrome', '2025-10-18 15:13:18.871000', '2361471705@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('e24f3617-ccae-48d8-a647-230f0f1da950', 'Chrome', '2025-10-24 09:36:07.548000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('e283f21b-8e62-4a24-b777-0340ce485f76', 'Chrome', '2024-06-27 14:29:58.104000', '1638752551@163.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('e3e6f378-3562-4ca0-a220-f1b828b582fe', 'Chrome', '2024-06-27 14:37:03.426000', '1638752551@163.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('e403d0a4-4cfd-42dd-bfe5-42128d9c4190', 'Chrome', '2025-10-30 17:37:13.715000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('e51de881-6e0c-4ba9-89e7-64fae218e212', 'Chrome', '2025-10-30 15:27:31.644000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('e5827089-583c-4b1a-b06d-ecb0ed9ac246', 'Chrome', '2025-10-19 16:45:36.407000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('e6678543-7f24-4c3f-ba66-96bf97f83243', 'Chrome', '2022-12-06 15:31:35.868000', 'adc@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('e717d589-7dde-4266-b057-b7e1d2385f6b', 'Chrome', '2025-10-26 23:52:32.644000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('e8a18096-c2d0-4434-88b4-e7dbb8987c97', 'Chrome', '2025-10-19 16:24:56.539000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('e8bc7baf-5242-441d-95f6-8087bbce9c72', 'Chrome', '2025-10-24 11:24:51.925000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('e90d361e-91b5-4595-b75e-faa6f19446fd', 'Chrome', '2024-06-13 17:19:31.716000', 'lop@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('ea676457-0dde-4e7b-8fec-e01837c12333', 'Chrome', '2023-01-11 09:20:22.825000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('eaa7939a-833a-487e-81c9-10ad128f1d93', 'Chrome', '2022-12-02 20:27:24.135000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('eb531c48-ea7b-4ef3-9517-1224c26f62e8', 'Chrome', '2022-12-18 11:37:31.447000', 'abc@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('ec24d904-a3a2-4556-a53a-b7763578c214', 'Chrome', '2025-10-19 16:38:16.099000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('ec99f789-914e-43a5-a489-330cc500f570', 'Chrome', '2025-10-25 21:59:29.183000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('f06fd7bb-a3e3-4032-959b-1fdc94b77878', 'Chrome', '2025-10-18 16:43:33.269000', 'lop@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('f2c99b73-8330-406f-a3dd-532a595b0b6d', 'Chrome', '2025-10-30 16:54:33.313000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('f32bc9de-3d76-4db9-8d71-96d6ef145dca', 'Chrome', '2022-12-12 13:54:11.044000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('f3be573d-784c-47c3-888d-9d1d57ae6dcf', 'Chrome', '2025-10-18 20:24:28.847000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('f42c92c7-601b-4125-9f67-1261cdc9af5c', 'Chrome', '2025-10-18 17:32:21.965000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('f4d833fe-c210-4160-a844-555fcd6546d9', 'Chrome', '2025-10-18 18:04:32.790000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('f574a53a-50cb-4726-a844-872dfe27b6f4', 'Chrome', '2025-10-18 20:23:36.332000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('f5fd6329-67dd-4dbf-9cf0-91021fc0347d', 'Chrome', '2025-10-18 10:18:34.279000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('f63b7014-935b-40f5-a8d5-1ce40b756c6a', 'Chrome', '2025-10-30 17:27:35.114000', 'lop@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('f6524757-c7f9-420f-8871-dcfb31e7b246', 'Chrome', '2025-10-19 16:42:55.074000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('f66bb224-49cc-46e7-b766-bd25406e2d20', 'Chrome', '2023-02-24 09:34:46.866000', 'testUser@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('f6cb2dea-9482-4986-9917-53cd32e26daa', 'Chrome', '2025-10-30 17:13:34.550000', '2159609711@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('f719c3e7-1547-43ee-8416-a9939c504f6f', 'Chrome', '2022-12-13 14:25:14.960000', 'jack@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('f7271654-203f-43db-9b91-2b8334fba5f7', 'Chrome', '2025-04-28 20:12:58.298000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('f8137ba8-9d3b-4edc-b714-2a89561fb07a', 'Chrome', '2025-10-20 10:37:36.470000', '16256226@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('f8384696-b37a-4f1b-9456-1f224427cf25', 'Chrome', '2022-12-18 10:54:04.371000', 'admin@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('f97fd7af-5a59-42ad-bbfb-b5fd065c0c1f', 'Chrome', '2022-12-18 11:28:02.712000', 'abc@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('fac5ae08-5fde-4d71-a1cf-3921678860b1', 'Chrome', '2025-10-18 15:04:17.882000', '2361471705@qq.com', '127.0.0.1', '1');
INSERT INTO `login_log` VALUES ('fc5588a9-287c-41e7-9adc-36c925d74418', 'Chrome', '2022-12-12 23:37:20.666000', 'admin@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('fd22284e-2c02-4259-97c8-a7493a9a3eac', 'Chrome', '2025-10-18 18:13:05.454000', '16256226@qq.com', '127.0.0.1', '0');
INSERT INTO `login_log` VALUES ('fe4e4ea1-41af-406e-b246-2e1e48eb045d', 'Chrome', '2023-01-11 11:04:37.010000', 'admin@qq.com', '127.0.0.1', '1');

-- ----------------------------
-- Table structure for sale
-- ----------------------------
DROP TABLE IF EXISTS `sale`;
CREATE TABLE `sale` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `commodity` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `company` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `count` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `pay` bit(1) NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of sale
-- ----------------------------
INSERT INTO `sale` VALUES ('0da2a4b7-208d-4c1d-bdd2-8966590f46c5', 'HUWAI MATE 30 Pro', '2', '500', '2025-04-23 14:23:44', 'ccc', '2342', '', '232423', '2000000');
INSERT INTO `sale` VALUES ('1307750a-78eb-4b07-8fb7-5b75bb3c6dc9', '手机支架', '中兴', '50', '2025-04-23 11:07:12', 'xxx', '234234', '', '213123', '499.5');
INSERT INTO `sale` VALUES ('16318677-a072-4924-b2cd-684216f9e549', '生鲜活鱼', '源码乐园公司', '1', '2025-04-24 09:44:41', '测试开票', '1234567869', '', '15615615611', '200');
INSERT INTO `sale` VALUES ('38214342-a785-4929-9208-94762c16a7d0', 'Apple', '小米', '20', '2025-04-24 10:15:35', 'xxx', '428354352', '', '13789253421', '800000');
INSERT INTO `sale` VALUES ('ad978658-4a02-48dc-80ce-54d77ee128db', 'iphone17promax1TB', '小名同学的公司', '20', '2025-10-23 23:22:01', '易碎物品', '6841606351480632692', '', '1888888888', '279999.8');
INSERT INTO `sale` VALUES ('b97a82bf-6aa8-47a7-a3a6-03e0df570038', '鼠标', '亚马逊', '50', '2025-04-24 13:37:50', '2341234', '342342', '', '214234', '499.5');
INSERT INTO `sale` VALUES ('bbcb2f8d-9ab4-4b6b-b0ff-1edae383c7a5', 'HUWAI MATE 30 Pro', '华为', '50', '2025-04-24 10:11:07', '234', '2134', '', '12332542342', '200000');
INSERT INTO `sale` VALUES ('d16570c0-8661-4c3c-ad60-000653fbb929', 'iphone17promax1TB', '小名同学', '20', '2025-10-24 09:35:00', '尽快', '6843641404827419739', '\0', '18444444444', '279999.8');
INSERT INTO `sale` VALUES ('f92be76b-eb85-4c4e-9fac-78e61f7ed6b3', 'iphone17promax1TB', '小名同学有限公司', '8', '2025-10-18 08:23:54', '易碎物品', '2879169741766547', '', '1888888888', '111999.92');

-- ----------------------------
-- Table structure for system_log
-- ----------------------------
DROP TABLE IF EXISTS `system_log`;
CREATE TABLE `system_log` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `busincess_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'LTD',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `module` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of system_log
-- ----------------------------
INSERT INTO `system_log` VALUES ('004bcf37-bb03-4ee2-886c-82e767cfb209', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:20:31.254000');
INSERT INTO `system_log` VALUES ('00768f97-da2c-46e2-b9cf-acd69767c7da', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 09:48:42.634000');
INSERT INTO `system_log` VALUES ('0097a66a-79dd-4988-bd28-ec98a8271736', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:24:21.798634');
INSERT INTO `system_log` VALUES ('00e04a9a-4a71-445b-9afe-7397f2bfce05', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:36:11.648000');
INSERT INTO `system_log` VALUES ('014658b8-e653-4b5f-9e49-9cfc79823829', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-19 23:29:47.893000');
INSERT INTO `system_log` VALUES ('01caea27-49cf-4d01-94ee-295897babadc', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-13 21:21:49.418742');
INSERT INTO `system_log` VALUES ('0205e3a3-d2e9-4298-8481-45863e98d285', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:42:57.829000');
INSERT INTO `system_log` VALUES ('022af003-71b1-4cce-834f-e03fbaf71182', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 20:05:32.945000');
INSERT INTO `system_log` VALUES ('022ec005-f15b-4a14-81d1-375bbad1bbe2', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 14:59:33.348000');
INSERT INTO `system_log` VALUES ('02398739-e477-4eba-83f8-227240b99296', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 16:29:31.808000');
INSERT INTO `system_log` VALUES ('02f19d7b-8776-4d31-ae3a-64967710379b', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:36:25.844651');
INSERT INTO `system_log` VALUES ('031ab444-46a8-448a-85cf-9b7033987bbc', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 17:03:01.678000');
INSERT INTO `system_log` VALUES ('0358ebfe-f036-4f78-88d7-d9f2993c8863', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-24 11:15:14.261000');
INSERT INTO `system_log` VALUES ('03a0f03d-9d2c-43c7-a78a-4590868b5727', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 15:42:12.432000');
INSERT INTO `system_log` VALUES ('04bb12d7-b38a-4b3f-9c65-a41c23b768ba', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:15:04.529000');
INSERT INTO `system_log` VALUES ('04c2ea07-217e-4742-bc5b-dfc707ab0e27', 'testUser@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2023-02-24 09:36:10.009000');
INSERT INTO `system_log` VALUES ('04ce3b48-3532-4ea3-8622-92c918afc5aa', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-20 19:50:59.325000');
INSERT INTO `system_log` VALUES ('04ed5df4-31ab-4241-bc52-071737f825b6', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-08 23:58:22.656978');
INSERT INTO `system_log` VALUES ('05011a84-94ae-4d13-8ab7-6b61f42f5e91', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 21:17:54.902178');
INSERT INTO `system_log` VALUES ('05564b21-03e6-4dc7-aad6-6d7ddb4eb5b2', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:38:08.796923');
INSERT INTO `system_log` VALUES ('05ebd740-3279-49e5-9548-5d7fd9d188e9', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:36:31.386614');
INSERT INTO `system_log` VALUES ('0657c23e-87ce-499d-a5bc-d4a42f1a62b9', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:42:05.289000');
INSERT INTO `system_log` VALUES ('06666d0b-95dc-498d-bc85-bf8e282c079f', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 23:32:50.050000');
INSERT INTO `system_log` VALUES ('06f52601-980b-4f51-97c4-e88843dbf31a', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:24:27.359000');
INSERT INTO `system_log` VALUES ('07030b17-920b-45fe-a459-69559742a66d', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-01-11 13:38:08.920000');
INSERT INTO `system_log` VALUES ('0711615f-f8b1-4d8c-b0c5-72b08a90e51b', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-01-11 11:08:04.405000');
INSERT INTO `system_log` VALUES ('07824ae3-73fd-4045-b50c-28c949d35807', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-20 19:56:46.374000');
INSERT INTO `system_log` VALUES ('07d70b7f-125f-4217-92ab-f2e96893d593', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:14:43.985543');
INSERT INTO `system_log` VALUES ('07eeb63d-58d8-4b76-b866-4e8f28c68382', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-04-28 21:28:31.176392');
INSERT INTO `system_log` VALUES ('0819a562-8c2d-4e0e-9072-0199a9b6741b', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-26 23:21:00.602000');
INSERT INTO `system_log` VALUES ('0822cab7-b2af-4a45-b220-09df5f00dfe9', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:56:08.374000');
INSERT INTO `system_log` VALUES ('083252f8-81e1-472f-b6e5-f4adce4e5e3b', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 09:11:05.517000');
INSERT INTO `system_log` VALUES ('0877caa9-0be0-4065-beb7-b9b16976a0b7', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 21:57:03.526000');
INSERT INTO `system_log` VALUES ('0883337e-669b-45a3-bdee-7d45f378029a', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:30:48.102354');
INSERT INTO `system_log` VALUES ('088be4cc-6648-4433-bd66-bd17c5a593d1', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:37:08.319000');
INSERT INTO `system_log` VALUES ('089a95dc-0c54-443a-a7b5-80e3b4f83993', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 21:49:08.981000');
INSERT INTO `system_log` VALUES ('08b90e2a-5c0f-4615-a46f-4ba979af1efe', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-23 11:11:18.980000');
INSERT INTO `system_log` VALUES ('08e9da63-f44d-4055-b810-8d218e7ca711', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-04-28 20:48:58.551738');
INSERT INTO `system_log` VALUES ('08f419ce-77c1-4209-a069-41ec2ece70a2', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-28 21:15:41.882467');
INSERT INTO `system_log` VALUES ('09e53d08-d7c3-4bd0-b839-19948d7cfe84', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-04-28 20:32:46.158906');
INSERT INTO `system_log` VALUES ('09ee1842-610c-4ead-800c-b08138eff2c6', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:29:49.204000');
INSERT INTO `system_log` VALUES ('0a06e994-622f-4d4c-98fa-59c57f6e4010', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-17 15:43:51.981000');
INSERT INTO `system_log` VALUES ('0a164b47-98b6-472a-ba9e-d458c9f14828', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:37:57.561384');
INSERT INTO `system_log` VALUES ('0a21780d-16d3-464b-bace-99b251c7e853', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-25 21:59:53.906000');
INSERT INTO `system_log` VALUES ('0a2c619f-fd0b-4e45-8292-b22ea72fdcdc', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-24 11:16:32.359000');
INSERT INTO `system_log` VALUES ('0a2cb900-7f72-4dd6-83dc-a7c6a7cb35b0', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:59:53.669000');
INSERT INTO `system_log` VALUES ('0a3e9653-fddd-46cb-b120-7189745e9023', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-16 10:30:22.900000');
INSERT INTO `system_log` VALUES ('0a6d4e9e-e5a5-4f7f-91d2-266d91286a33', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:09:40.044000');
INSERT INTO `system_log` VALUES ('0a7dc808-dbc9-4576-8010-d4234d12d6f8', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:09:28.018000');
INSERT INTO `system_log` VALUES ('0ada9a90-f4eb-4ec7-946c-9a7c944c502e', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-05-09 13:06:59.499481');
INSERT INTO `system_log` VALUES ('0adbc27e-9cc5-47eb-aafd-20b283a15670', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-28 10:08:06.895495');
INSERT INTO `system_log` VALUES ('0bbd0884-5c14-446a-924f-4da8dec0cf3c', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-23 11:11:21.947000');
INSERT INTO `system_log` VALUES ('0bc4b096-bdbd-40ec-b86a-dcea41dab3d3', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-24 09:59:33.136000');
INSERT INTO `system_log` VALUES ('0bdfe91d-62b5-408a-8cb1-04508935b322', '16256226@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-05-08 23:59:05.658201');
INSERT INTO `system_log` VALUES ('0c3ff5a0-eb53-428a-985d-54349bb04d5e', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 18:20:44.631831');
INSERT INTO `system_log` VALUES ('0c43227e-fc9e-4b46-8d98-6100e4d2d036', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 18:19:01.851589');
INSERT INTO `system_log` VALUES ('0c49d9ba-00fc-439e-a122-fc4b750154eb', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2022-12-23 19:05:11.634000');
INSERT INTO `system_log` VALUES ('0c714d07-7a44-473b-944d-648e0f5c5b6c', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:13:39.468311');
INSERT INTO `system_log` VALUES ('0c910d2c-6621-42ee-99c6-fff9b4257276', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:16:49.080191');
INSERT INTO `system_log` VALUES ('0c912cf5-e75d-4bad-9848-b06f4e9d42ab', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 20:01:41.521000');
INSERT INTO `system_log` VALUES ('0ce3f0e8-15f2-4c94-a048-145961601b35', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 10:01:59.674000');
INSERT INTO `system_log` VALUES ('0d1f0d04-fc68-48cf-955a-385452717ce6', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 10:58:59.954000');
INSERT INTO `system_log` VALUES ('0d279f20-8cfd-4c8f-b648-d319cb2b8098', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-30 17:36:28.283000');
INSERT INTO `system_log` VALUES ('0d479fff-9788-476e-8d63-768db9634208', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 09:42:21.327000');
INSERT INTO `system_log` VALUES ('0d5a284a-97e1-499a-ab11-30c384349182', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:16:06.280687');
INSERT INTO `system_log` VALUES ('0d76207c-6e13-4f39-aa8d-05fad6267abd', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-24 11:17:27.963000');
INSERT INTO `system_log` VALUES ('0d8cb9a7-40ea-45df-8442-a96455031792', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-23 23:24:34.316000');
INSERT INTO `system_log` VALUES ('0dc591c0-95cc-4e44-a43e-721e1440f054', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-17 15:37:25.009000');
INSERT INTO `system_log` VALUES ('0dc8a716-3d42-4e2a-80c1-7dc3a18964e0', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-19 23:28:03.638000');
INSERT INTO `system_log` VALUES ('0dffee7e-bc78-423d-b7e5-76114fca2eb2', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 16:56:25.166000');
INSERT INTO `system_log` VALUES ('0e127759-729d-4762-8e51-016799b91ea1', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:26:11.478000');
INSERT INTO `system_log` VALUES ('0e4b1f24-7a87-4e2a-b94d-1bc44df1cc85', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:33:03.677518');
INSERT INTO `system_log` VALUES ('0e6e972a-114c-4b9a-9115-68d70bf8304a', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 12:15:25.465000');
INSERT INTO `system_log` VALUES ('0e749bc2-ce9d-4ba4-90ae-a4535caef592', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2022-12-19 13:10:57.580000');
INSERT INTO `system_log` VALUES ('0e795302-350f-4dc7-a08a-b4aa1897cf45', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-24 09:56:16.961000');
INSERT INTO `system_log` VALUES ('0ee45fdb-a1af-4ec9-9dca-3d7da5bf7c3b', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-23 11:11:18.971000');
INSERT INTO `system_log` VALUES ('0efd9f72-ceec-4a52-acf9-2ba4cd61e236', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-30 21:31:48.400000');
INSERT INTO `system_log` VALUES ('0f952933-4b3c-47d8-a5ef-d4038b82a5f5', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 21:15:43.558877');
INSERT INTO `system_log` VALUES ('0fb13d47-8380-4ef6-8a48-66d34236d63d', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-09 13:06:55.472779');
INSERT INTO `system_log` VALUES ('0fc9c108-cc5c-44d2-88a7-f692e52de96c', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 20:25:52.892000');
INSERT INTO `system_log` VALUES ('104463bf-945d-4724-ba22-8c5b000abb3d', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:58:25.970000');
INSERT INTO `system_log` VALUES ('1059fae1-3f5b-4e2c-8b35-6442571524c3', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 15:37:16.965000');
INSERT INTO `system_log` VALUES ('10653d51-777b-4e1e-adc5-f75aa3de6ea7', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:07:25.089000');
INSERT INTO `system_log` VALUES ('10d19483-f0cc-4d49-a560-fb382e1bd77d', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 22:17:13.950000');
INSERT INTO `system_log` VALUES ('10eaf262-b9f7-437c-afa9-1f08a3854b14', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 21:57:22.826000');
INSERT INTO `system_log` VALUES ('111d3721-0509-4b05-addb-9fbacd86f9a6', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-19 22:24:00.192000');
INSERT INTO `system_log` VALUES ('114215f6-49b6-4abd-9fb7-3c11424fb3b9', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 21:50:04.777000');
INSERT INTO `system_log` VALUES ('11a1e510-11e1-406b-b377-4bc0076b538e', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:28:03.896000');
INSERT INTO `system_log` VALUES ('11a3e607-18ee-41da-bcba-2c8b419eb5d0', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 11:04:38.532000');
INSERT INTO `system_log` VALUES ('11b37d99-eb2a-483b-8101-c4f1896b1719', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-05-28 10:13:44.418323');
INSERT INTO `system_log` VALUES ('11d54f88-65a2-46fc-8858-f0689523f9ad', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-26 23:16:55.541000');
INSERT INTO `system_log` VALUES ('1203dc4a-0891-4dc0-bd98-afcf4cb9a20f', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 20:01:14.634000');
INSERT INTO `system_log` VALUES ('121c399a-7233-4607-86da-ce1d6114cadf', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:07:53.538000');
INSERT INTO `system_log` VALUES ('1258cd90-9ba2-4b8d-8549-3602027cd608', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 09:25:18.460000');
INSERT INTO `system_log` VALUES ('12ba136e-da1c-4e56-93b1-caa50b1ced20', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 09:18:41.743000');
INSERT INTO `system_log` VALUES ('12e262bc-3615-40c1-897f-7b06be4f7d01', '16256226@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-04-28 20:19:25.319792');
INSERT INTO `system_log` VALUES ('12f8f420-e25d-4e5e-95ed-a58f20b5427e', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2022-12-18 11:37:41.214000');
INSERT INTO `system_log` VALUES ('135bf499-b9f8-480d-b54d-32423f6dc269', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-04-28 20:17:07.599518');
INSERT INTO `system_log` VALUES ('1379a73e-11d8-4c97-bd2f-4141cde20117', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 10:00:41.129000');
INSERT INTO `system_log` VALUES ('137c5d5a-0c8f-45c9-9f30-98bd3d5d0215', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-04-05 23:45:29.959000');
INSERT INTO `system_log` VALUES ('13ecfc46-06bb-4a7b-a072-eed916ebcab1', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:13:56.018156');
INSERT INTO `system_log` VALUES ('1408aaaa-7749-476c-8f66-77b25d16ffa1', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:49:28.647000');
INSERT INTO `system_log` VALUES ('142395bf-7318-47ec-8a43-acca8bbe38cd', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-25 21:58:19.015000');
INSERT INTO `system_log` VALUES ('14251f25-371c-4aef-bc0f-448d556b0862', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-18 08:25:10.232000');
INSERT INTO `system_log` VALUES ('148c7411-a737-49fc-8543-a05ae0489b4a', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:38:12.278000');
INSERT INTO `system_log` VALUES ('14ab94ec-145a-40ca-a74d-e24ba39c2614', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 21:51:51.305000');
INSERT INTO `system_log` VALUES ('14f88737-b3d0-4336-abff-af171c20d021', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-28 20:50:17.308088');
INSERT INTO `system_log` VALUES ('1518cd8f-0ab0-48c9-9c6b-7b24bf0a1e0a', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:29:28.747000');
INSERT INTO `system_log` VALUES ('152128fe-ffb3-47af-bb12-59777d0f7682', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-19 23:21:47.830000');
INSERT INTO `system_log` VALUES ('152742ba-d32d-4ec3-8954-5ac342900137', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:39:02.791000');
INSERT INTO `system_log` VALUES ('1535c04e-6170-4266-8209-90d20cc367f1', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:34:43.095898');
INSERT INTO `system_log` VALUES ('15e8b262-2f3d-4c40-bf7b-12911c9d1158', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-04-13 21:25:02.147239');
INSERT INTO `system_log` VALUES ('164d9a8a-4361-40cb-a72e-7f3f09393275', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-01-11 11:07:54.695000');
INSERT INTO `system_log` VALUES ('1651e6a3-668b-4aae-89f7-f4f753f1eb7b', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-04 14:35:54.288000');
INSERT INTO `system_log` VALUES ('16853df1-38d8-4833-a1bb-3d90b154d88e', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-27 14:23:49.218000');
INSERT INTO `system_log` VALUES ('16ad7db3-f6b6-4820-a186-b67f65565264', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 20:02:33.789000');
INSERT INTO `system_log` VALUES ('16da1425-143f-424c-8638-d689ad54b228', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:01:40.630000');
INSERT INTO `system_log` VALUES ('16efd003-7bf9-4d85-b7ad-bbb289aa0152', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-19 23:21:50.345000');
INSERT INTO `system_log` VALUES ('17135dc0-cc7b-4bce-9029-8f1d3c15cbd9', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-27 14:32:38.828000');
INSERT INTO `system_log` VALUES ('17155240-f711-4ad3-beaa-caf0ed5c93d9', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:16:35.107000');
INSERT INTO `system_log` VALUES ('17503c49-df4d-47b1-97e7-3819fe387637', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-25 21:58:05.504000');
INSERT INTO `system_log` VALUES ('175b9efe-7b71-456e-8b90-314eedb1f470', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:33:04.535727');
INSERT INTO `system_log` VALUES ('1771b874-d138-44a4-a296-fd348d0c1e99', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-24 09:09:31.347000');
INSERT INTO `system_log` VALUES ('18421c38-493a-480a-95d3-1e5724a2097a', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:13:58.569000');
INSERT INTO `system_log` VALUES ('186335d0-a4fa-498d-bf9a-85b5b9e8c2d9', 'jiegod_8ck@126.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-04-28 20:37:37.612790');
INSERT INTO `system_log` VALUES ('187a6ed0-8352-4426-b87d-99e31b977714', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 11:19:07.892000');
INSERT INTO `system_log` VALUES ('189b809e-8ac0-4784-9ae2-505d26e4834a', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 09:33:41.714000');
INSERT INTO `system_log` VALUES ('189cec0a-d3ea-4c70-8c60-9151b6cf2937', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:34:42.856000');
INSERT INTO `system_log` VALUES ('18b6984b-4a3f-4cce-9cb3-f2b9dd63f862', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 09:33:19.908000');
INSERT INTO `system_log` VALUES ('18d00ca2-801f-43a4-af21-d2155fe25f86', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:44:57.766000');
INSERT INTO `system_log` VALUES ('1936d5ad-f4e8-41ef-870a-ba0168bdd852', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:52:33.923000');
INSERT INTO `system_log` VALUES ('19629465-d4b0-4972-99bb-dc7b497b2357', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:39:02.459000');
INSERT INTO `system_log` VALUES ('19925a86-5045-4541-b34c-197497985eb2', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 16:39:37.044000');
INSERT INTO `system_log` VALUES ('19998a8b-79e8-4725-878d-87f4ca22a491', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 12:56:21.871000');
INSERT INTO `system_log` VALUES ('1a1a8ca4-dd00-476d-8202-7050968b89be', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 10:15:25.052000');
INSERT INTO `system_log` VALUES ('1a801db2-06ab-47b5-bbe9-17b1a55b567c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-01-11 13:15:42.052000');
INSERT INTO `system_log` VALUES ('1abc954f-d246-4de6-b3f3-7158965f1b6f', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-06-10 18:38:51.995917');
INSERT INTO `system_log` VALUES ('1abccede-c3f2-4590-a4c6-4fb217591fe4', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2022-12-19 12:48:25.474000');
INSERT INTO `system_log` VALUES ('1b47ff7c-1fa7-425b-bfd7-ce11e068b0ce', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-05-27 18:14:08.597840');
INSERT INTO `system_log` VALUES ('1b5e1790-6d52-42f2-a9a3-3234b74b5596', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-23 11:11:24.496000');
INSERT INTO `system_log` VALUES ('1b6eff65-712b-4ec6-b252-2e73f5e531e8', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2022-12-19 12:58:39.642000');
INSERT INTO `system_log` VALUES ('1b764820-3427-4a76-b092-49bdaa2606a6', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-19 23:38:04.756000');
INSERT INTO `system_log` VALUES ('1bcfa60e-1716-411a-9218-6375bd5de168', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:37:23.022000');
INSERT INTO `system_log` VALUES ('1beee8b4-0cd5-4140-a43d-9edf2484af1e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 20:06:00.996000');
INSERT INTO `system_log` VALUES ('1bf8d432-bb64-48e7-b6d4-dd8797f56482', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-05-28 10:15:24.788595');
INSERT INTO `system_log` VALUES ('1c016759-3495-4428-b648-2f45cfffc1ba', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-04-13 21:24:54.426522');
INSERT INTO `system_log` VALUES ('1c472a75-72cf-474c-987c-09ad35e35bb6', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:20:23.990000');
INSERT INTO `system_log` VALUES ('1c4c3b10-8668-4426-9653-c1a06fa2b47d', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2022-12-18 12:15:45.300000');
INSERT INTO `system_log` VALUES ('1c65278d-2f65-432e-8221-545cb8eb1452', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:50:00.085000');
INSERT INTO `system_log` VALUES ('1c6815c7-097d-43b1-9162-896d94f1067c', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 09:35:15.075000');
INSERT INTO `system_log` VALUES ('1cfc347a-f184-44ea-8872-12172ba5787e', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:14:37.921322');
INSERT INTO `system_log` VALUES ('1d0f7faa-8baf-4137-a2da-bf700b073b67', '2159609711@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-10-23 23:04:07.831000');
INSERT INTO `system_log` VALUES ('1d1b3174-8064-4af0-a6fc-5be5acc7291e', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 15:44:54.049000');
INSERT INTO `system_log` VALUES ('1d1db78d-54d1-4d98-8f67-7bd4a3188959', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 11:19:36.060000');
INSERT INTO `system_log` VALUES ('1d3e6c4d-ffd7-4382-b215-0b8428b79587', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 11:07:45.793000');
INSERT INTO `system_log` VALUES ('1d8a8d7f-da75-4efd-b317-abe4bad6aa5a', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-18 11:50:10.518000');
INSERT INTO `system_log` VALUES ('1ebc170e-9a28-4bd1-926d-ad8753a9e3f4', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-18 08:24:45.464000');
INSERT INTO `system_log` VALUES ('1ed6652a-7d71-4153-bed9-353c16ecf826', '16256226@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-04-28 20:14:48.878339');
INSERT INTO `system_log` VALUES ('1ef78e53-4a64-4f5a-b137-de8028ed4fc9', '匿名用户', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 16:07:16.687000');
INSERT INTO `system_log` VALUES ('1f15ddbf-235e-4c4b-a7e7-d9959a95780d', '匿名用户', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 16:07:22.095000');
INSERT INTO `system_log` VALUES ('1f188cea-368b-48ea-b805-18a327c197ac', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-17 18:09:18.027000');
INSERT INTO `system_log` VALUES ('1f284865-ab65-4065-9a63-7bed746fb8a5', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:19:25.535859');
INSERT INTO `system_log` VALUES ('1f5c4ecf-b058-40fd-91a6-3cbd7968fb7a', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2024-06-26 23:42:51.820000');
INSERT INTO `system_log` VALUES ('1f94e369-e1bb-4619-a521-0563b9427288', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-05-27 21:44:27.285826');
INSERT INTO `system_log` VALUES ('1f9c1f26-7bf7-4133-9dc3-06b9242753c0', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-20 19:56:44.467000');
INSERT INTO `system_log` VALUES ('1fcf63f6-d31b-49ab-8ee6-a828ca16f972', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-04-28 21:30:01.860775');
INSERT INTO `system_log` VALUES ('20211269-fbc6-4323-9211-f72bb80a985f', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 23:24:46.899000');
INSERT INTO `system_log` VALUES ('202227ed-0895-462e-984e-705fecdcb349', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.EmployeeController.save', '员工管理', '2025-10-17 15:43:51.239000');
INSERT INTO `system_log` VALUES ('2028640b-33c7-43cd-8f86-18e17d2c11ae', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-27 14:32:07.237000');
INSERT INTO `system_log` VALUES ('206f05d8-2362-4f74-9229-6f72389a16e6', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 10:03:27.726000');
INSERT INTO `system_log` VALUES ('2087ff2a-3c06-454e-a56d-e9576e1b54cc', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:16:28.053000');
INSERT INTO `system_log` VALUES ('20a280a3-3733-4097-ab14-2017bdbc45c3', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 11:19:03.541000');
INSERT INTO `system_log` VALUES ('20be83c0-ebdf-4f8d-95d2-859a63a18777', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 23:19:26.209000');
INSERT INTO `system_log` VALUES ('20ce6170-5423-4347-a29d-8e7187c08da5', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-27 21:51:44.105000');
INSERT INTO `system_log` VALUES ('2131923c-c036-423a-b5b6-b80ada345e8a', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-18 08:19:47.326000');
INSERT INTO `system_log` VALUES ('21505de4-e9d2-44fb-b647-bf372e94222a', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-23 11:16:21.842000');
INSERT INTO `system_log` VALUES ('216463ad-0b0e-4c20-805e-c3e3562d0e88', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-18 08:19:47.302000');
INSERT INTO `system_log` VALUES ('218c5fcb-305f-4c4c-b03c-df00ac73a183', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-04 14:36:01.259000');
INSERT INTO `system_log` VALUES ('222e3c86-71b5-4dcc-8788-0e278824ae43', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:53:41.190000');
INSERT INTO `system_log` VALUES ('226b4fc8-5bda-4a56-8600-e0fdf8fa7db6', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-23 23:30:22.566000');
INSERT INTO `system_log` VALUES ('22ab6add-fb0c-4f7f-8ef3-13c72eaf14ad', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 18:12:36.178068');
INSERT INTO `system_log` VALUES ('22c649d9-dbd9-410b-ab96-ce0bc4b7a41d', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DriverController.save', '驾驶员管理', '2025-10-23 23:30:16.809000');
INSERT INTO `system_log` VALUES ('22f051ae-e9da-4487-8d32-ae579fbab261', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-03 10:08:53.122000');
INSERT INTO `system_log` VALUES ('230bbf27-928b-4c8b-8c41-5ec8100c2d82', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:18:59.066000');
INSERT INTO `system_log` VALUES ('23e1b101-ef5f-40bb-9e56-33425416d445', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:16:37.603000');
INSERT INTO `system_log` VALUES ('24281e0d-92dd-4086-96d3-efb2f54f9fdc', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-28 14:36:10.715000');
INSERT INTO `system_log` VALUES ('24978323-15d0-4346-b44d-9be40d3e4f29', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:34:03.157734');
INSERT INTO `system_log` VALUES ('25946777-5079-423e-9bad-5a39ecfc8a66', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-18 12:15:45.235000');
INSERT INTO `system_log` VALUES ('25d107d5-e955-4de3-84fd-8b75814bbb35', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-19 23:28:02.064000');
INSERT INTO `system_log` VALUES ('25dee90a-fd5b-486b-b553-511047b5b88e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 23:16:59.006000');
INSERT INTO `system_log` VALUES ('25ea7422-6609-45ce-8207-0ce14d4bd39a', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-30 16:48:34.350000');
INSERT INTO `system_log` VALUES ('25f21d57-a6de-453d-8b0d-7dd067e421bf', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 21:51:04.674000');
INSERT INTO `system_log` VALUES ('26819e0b-7ae7-4d23-bfde-eca7b34cc9d9', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-19 14:59:37.515000');
INSERT INTO `system_log` VALUES ('26a081b9-b630-4864-8ded-4e0c9e88d3aa', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2022-12-18 12:15:56.064000');
INSERT INTO `system_log` VALUES ('26b97862-727a-43e4-bffc-5b65fca7f472', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:51:23.998000');
INSERT INTO `system_log` VALUES ('26d7d852-e20c-4ee1-98bb-ec1fefa5893a', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-19 22:24:12.358000');
INSERT INTO `system_log` VALUES ('26ff1eed-a294-4cbb-87c3-8c4a0c47d4a2', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:10:25.847000');
INSERT INTO `system_log` VALUES ('274ecc18-8722-4a3f-9d31-c13188de6ad9', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 16:20:44.726000');
INSERT INTO `system_log` VALUES ('275488fb-eb1b-48e9-87d3-85435a1300c3', '匿名用户', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-30 16:06:46.840000');
INSERT INTO `system_log` VALUES ('275817ea-15f0-4875-b727-d754a4053d2d', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:35:05.878000');
INSERT INTO `system_log` VALUES ('275aab98-fc80-4258-9c8a-fcfa9f57fb2f', '2361471705@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 17:04:37.931000');
INSERT INTO `system_log` VALUES ('2760b8cc-ab8c-4beb-9625-185d3221f933', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:29:59.663000');
INSERT INTO `system_log` VALUES ('27a8725d-6ff6-4e7c-9a26-c7a745b1473c', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-26 23:52:41.489000');
INSERT INTO `system_log` VALUES ('27cd504d-9b51-4c78-86b0-d88cf7436461', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:25:20.593000');
INSERT INTO `system_log` VALUES ('28205e59-42b3-4601-a475-cb61a3c578b5', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:23:40.903000');
INSERT INTO `system_log` VALUES ('28de6a44-46a6-4aec-8f71-8e66c1808be9', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-05-27 18:20:40.935803');
INSERT INTO `system_log` VALUES ('28f258f9-b129-4cab-a300-9c806e262865', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-04-05 23:45:37.788000');
INSERT INTO `system_log` VALUES ('2928b5de-03c9-461e-a8fb-959a7ac40545', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-04-28 21:31:59.745392');
INSERT INTO `system_log` VALUES ('296e269b-f59d-4763-9b3d-2ac320ba4531', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-27 18:15:19.244084');
INSERT INTO `system_log` VALUES ('29a5ed30-dc5c-4762-8350-338564e60ff5', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:09:50.393457');
INSERT INTO `system_log` VALUES ('29b1f743-d5e9-4cab-9df3-82850d3fbe3a', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-24 11:26:29.919000');
INSERT INTO `system_log` VALUES ('29cd4013-cf09-48b9-a624-d894133170f1', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-04-04 14:34:19.741000');
INSERT INTO `system_log` VALUES ('2a0f3ad1-9b9f-4ac8-a614-0df6ce68f764', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-27 20:01:29.268000');
INSERT INTO `system_log` VALUES ('2a231073-410a-4027-8b03-11398f1df179', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:33:48.087000');
INSERT INTO `system_log` VALUES ('2a28af8e-a337-4822-9eea-147b017f8f20', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:38:06.197000');
INSERT INTO `system_log` VALUES ('2a3d8a33-3c3f-46ec-a8db-b51d014c4a66', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-23 11:12:02.912000');
INSERT INTO `system_log` VALUES ('2a43dff5-1508-49d1-99fa-dfefe1df8863', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:12:07.771000');
INSERT INTO `system_log` VALUES ('2a681414-ab2b-4f56-9c1b-9a19aa307345', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 09:33:22.460000');
INSERT INTO `system_log` VALUES ('2a77f4fd-6c00-4776-9f29-466d048e42fe', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-04-28 21:15:41.012826');
INSERT INTO `system_log` VALUES ('2a855863-3e2f-49b9-93e7-a81610622323', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2024-06-27 14:23:51.974000');
INSERT INTO `system_log` VALUES ('2ae4e735-c4ad-47e1-b9ee-1612c64d6fe0', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-02 15:26:41.960000');
INSERT INTO `system_log` VALUES ('2ae76013-4dcd-4ab8-a9a9-b711d16f9419', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 13:10:40.933000');
INSERT INTO `system_log` VALUES ('2afc457d-cbf1-420f-8c17-d36f9f56deb3', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-01-11 09:18:49.210000');
INSERT INTO `system_log` VALUES ('2b23817b-40fd-496c-8e50-3766bfacaf42', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:01:29.129000');
INSERT INTO `system_log` VALUES ('2b24e668-b1bf-4bf2-a890-e9abb953834f', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-19 11:19:40.142000');
INSERT INTO `system_log` VALUES ('2b59abf3-f3fe-4475-9a27-8145dadc3306', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 11:20:59.984000');
INSERT INTO `system_log` VALUES ('2b5a5457-a48a-49ba-908a-05865c8350b6', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:42:09.838000');
INSERT INTO `system_log` VALUES ('2b7149e8-8f90-4372-8a8a-179a750ba32f', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2024-06-27 14:26:58.404000');
INSERT INTO `system_log` VALUES ('2bbfa0a8-e342-4681-91e4-bce9f83fb621', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-24 11:01:40.630000');
INSERT INTO `system_log` VALUES ('2bdd23f5-2ec8-4ed4-93a3-5811af990c9c', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:31:29.221544');
INSERT INTO `system_log` VALUES ('2bf75a2c-7f38-4138-b797-1e62e2491616', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-05-08 23:59:51.895409');
INSERT INTO `system_log` VALUES ('2bf9e284-477c-4010-aa8d-beefeeb75604', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 20:15:24.756000');
INSERT INTO `system_log` VALUES ('2c21892d-645b-4e41-af5a-9a1d8a46e490', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 16:07:29.549000');
INSERT INTO `system_log` VALUES ('2c3ab87e-482c-4d3b-9a50-4afa13f64a6c', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 21:47:39.467000');
INSERT INTO `system_log` VALUES ('2c3cdb3d-bd74-4e38-9a6f-e5e08aa702c7', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-20 08:37:30.007000');
INSERT INTO `system_log` VALUES ('2c97f252-37b1-4434-85ff-18487dcb6eee', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:37:50.587873');
INSERT INTO `system_log` VALUES ('2cb2cc55-e265-43cf-9e6a-4189017db1c8', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-28 10:12:11.731497');
INSERT INTO `system_log` VALUES ('2cebfee1-a906-4cad-b585-c3f0f456a0cb', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2022-12-18 11:29:28.045000');
INSERT INTO `system_log` VALUES ('2d048116-5a2d-4baa-8051-74c12ce455fa', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:37:15.724201');
INSERT INTO `system_log` VALUES ('2d3b0ee0-877a-4040-8302-1faedc8ef14f', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:12:01.541000');
INSERT INTO `system_log` VALUES ('2d8d14c7-a3de-4dd2-aa63-691b5e84f765', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-05-09 00:00:56.805201');
INSERT INTO `system_log` VALUES ('2e2a7953-a419-43ef-a629-cdc1b4735399', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-27 21:48:21.960000');
INSERT INTO `system_log` VALUES ('2e5b84c0-af2c-4011-abfa-99696e573156', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-25 21:50:16.789000');
INSERT INTO `system_log` VALUES ('2e5d90b9-cb01-408d-807b-3211b0ea3996', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:21:32.977000');
INSERT INTO `system_log` VALUES ('2ee9b9e4-aaad-478d-a4d0-ead420245c52', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:35:59.209225');
INSERT INTO `system_log` VALUES ('2eef2abc-666c-4b84-a052-07016bbe993a', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-28 14:02:53.685000');
INSERT INTO `system_log` VALUES ('2f0f722a-527f-4539-b4ab-11e1f3315e15', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 14:26:03.257000');
INSERT INTO `system_log` VALUES ('2f58f63c-d261-4deb-8251-98d7ce4bff7a', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:31:07.906819');
INSERT INTO `system_log` VALUES ('2f8c7838-775c-4b8a-82a1-6a73be964abd', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.EmployeeController.save', '员工管理', '2023-01-11 11:07:38.361000');
INSERT INTO `system_log` VALUES ('2fa51b9f-8841-485d-a4a2-7bf33af92b1c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 11:08:12.464000');
INSERT INTO `system_log` VALUES ('2fa5206b-b5d0-438d-9e55-a576a1ff779b', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:48:39.637000');
INSERT INTO `system_log` VALUES ('2ff33f46-159d-47b5-b309-99f89c53ede9', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-28 14:05:32.116000');
INSERT INTO `system_log` VALUES ('306425a6-1d93-4f52-8686-2011a4cc0ac2', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-27 18:12:52.332025');
INSERT INTO `system_log` VALUES ('3068cc43-5225-432c-8fd1-6f6e839538b2', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-28 10:05:19.541975');
INSERT INTO `system_log` VALUES ('3073c755-5484-47ad-ac18-0bddc4e6e25f', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:37:20.007000');
INSERT INTO `system_log` VALUES ('309678c0-f433-45a8-b91a-277ba426ad06', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:18:21.314848');
INSERT INTO `system_log` VALUES ('30a1ae22-654b-46c3-b47a-23b09d64e90f', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-24 09:45:38.675000');
INSERT INTO `system_log` VALUES ('30a88180-fd32-466c-8959-eeaa05169ae6', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:07:54.083000');
INSERT INTO `system_log` VALUES ('3108e9c1-e170-4996-ab02-b4571c64e8cb', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-28 14:36:04.907000');
INSERT INTO `system_log` VALUES ('31b0a5e3-70c3-4281-b946-9f230ac237c3', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DriverController.save', '驾驶员管理', '2025-10-17 16:09:30.688000');
INSERT INTO `system_log` VALUES ('31b4535b-4438-427a-abbc-36c7e2f44f07', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:59:33.488000');
INSERT INTO `system_log` VALUES ('31d038fc-ce82-45c9-8aaa-b40f92882625', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 11:08:28.522000');
INSERT INTO `system_log` VALUES ('31df9a25-b184-4571-be4c-2876292ca52a', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:24:07.989000');
INSERT INTO `system_log` VALUES ('31ecd48c-7a32-4167-a683-e6bdacf785e5', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 15:36:52.551000');
INSERT INTO `system_log` VALUES ('321bd797-9f71-45d0-b81c-66a8031058c8', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2024-06-27 14:26:21.097000');
INSERT INTO `system_log` VALUES ('32485337-13bc-4405-94b2-0012d3ce067f', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:36:21.312777');
INSERT INTO `system_log` VALUES ('324e4e67-f1e3-4859-b726-9092032b5348', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-27 21:47:46.209000');
INSERT INTO `system_log` VALUES ('32817d0e-e7e8-4dac-86e2-e86776596bc1', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:25:13.141000');
INSERT INTO `system_log` VALUES ('32a5f454-c88d-4335-b60d-9e05b117ba95', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 09:12:07.771000');
INSERT INTO `system_log` VALUES ('32ba899c-4984-4d13-a504-3b4ff630de37', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-01-11 10:16:36.294000');
INSERT INTO `system_log` VALUES ('32d7e534-3a7b-45d4-b899-cbd44989f788', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:56:10.824000');
INSERT INTO `system_log` VALUES ('32e5e9ba-4350-4e73-9737-b2397ec21fd5', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-24 11:17:43.096000');
INSERT INTO `system_log` VALUES ('330f1207-1898-4332-a5d5-056ed3ae7591', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-24 11:13:57.444000');
INSERT INTO `system_log` VALUES ('3364c7aa-cf85-479e-a5e3-49079e123aca', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:27:36.591000');
INSERT INTO `system_log` VALUES ('336b69d6-8f5c-4c34-ae27-49b2906d9a05', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-23 11:12:02.537000');
INSERT INTO `system_log` VALUES ('336dcac9-1f1f-456f-9e5b-549303cd0988', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-24 11:05:05.930000');
INSERT INTO `system_log` VALUES ('33cf72b3-98aa-4975-9834-38c3b61c99a2', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:20:44.559000');
INSERT INTO `system_log` VALUES ('34b47de5-4d42-416b-a2ee-24ff902966db', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:08:28.636000');
INSERT INTO `system_log` VALUES ('35495a37-e929-4335-a1e2-7b2a4fd37510', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-23 11:11:19.448000');
INSERT INTO `system_log` VALUES ('355db0cd-0ed9-454a-a429-aaeab80e75f3', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-24 11:06:05.565000');
INSERT INTO `system_log` VALUES ('35745cc3-b5fb-4106-89fb-fabc7bae48b7', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-08 23:58:35.521935');
INSERT INTO `system_log` VALUES ('35d796f3-ca6d-43b2-93a5-7d808d8daabe', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:38:23.159844');
INSERT INTO `system_log` VALUES ('35dde209-b6d4-442f-b1b3-cc7f3e8a311c', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-27 20:02:19.248000');
INSERT INTO `system_log` VALUES ('3600f204-d671-476e-9f39-194ee6c066ab', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:14:44.828393');
INSERT INTO `system_log` VALUES ('360310bf-a982-4a29-8b2b-41ef460c175a', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:04:13.068000');
INSERT INTO `system_log` VALUES ('362f21e4-505e-4eae-ac1c-0980a5309fce', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2024-06-27 00:10:19.454000');
INSERT INTO `system_log` VALUES ('3652fd86-5024-4fb1-9977-284bc0b78eb5', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:19:23.562666');
INSERT INTO `system_log` VALUES ('388fbd00-39ce-436c-8e26-945db6a68f5b', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-27 14:26:21.146000');
INSERT INTO `system_log` VALUES ('38943e43-f9e5-4e2a-b6a8-5cfaffea1939', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 16:48:34.341000');
INSERT INTO `system_log` VALUES ('38ac61fb-2be3-4ab1-a77b-9b366e767ce3', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-05-09 00:00:36.092976');
INSERT INTO `system_log` VALUES ('38cedbb8-228f-4965-ad15-7fd5c9162781', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-01-11 09:20:28.320000');
INSERT INTO `system_log` VALUES ('38d64060-e40e-4de3-9633-af1a3fb3ee78', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-05-09 00:00:40.448123');
INSERT INTO `system_log` VALUES ('3906d078-a520-45c6-967a-4158107dd8b5', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:10:55.388000');
INSERT INTO `system_log` VALUES ('3935e2a5-2bcc-47ae-82ba-cc51ee4f1f2f', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:24:20.940665');
INSERT INTO `system_log` VALUES ('39899d68-4c6b-4067-87db-df5763a02bdf', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:28:55.674000');
INSERT INTO `system_log` VALUES ('3a08ab6a-a5bb-4a11-b97b-e013615473ca', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:36:10.836000');
INSERT INTO `system_log` VALUES ('3a0c76b8-458c-4c5c-a528-24b9d8bb783a', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 20:56:06.260000');
INSERT INTO `system_log` VALUES ('3a765348-28f3-4f91-ad99-e47bcdcb1cfb', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-24 11:17:30.577000');
INSERT INTO `system_log` VALUES ('3a8dce67-5655-4616-9257-44f1c804e3be', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-28 20:13:53.402126');
INSERT INTO `system_log` VALUES ('3ad7de03-ce1d-46cf-829f-32bf2d13b25d', '匿名用户', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-30 16:07:38.872000');
INSERT INTO `system_log` VALUES ('3af96a16-b857-4da9-9dcf-1a30d38db29f', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:35:03.818630');
INSERT INTO `system_log` VALUES ('3afae35e-3263-433e-b05e-10ab46b58f80', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-19 23:24:52.688000');
INSERT INTO `system_log` VALUES ('3b48f881-2723-4ad9-a99e-00125b2c23c5', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 18:21:04.481953');
INSERT INTO `system_log` VALUES ('3b872ebf-413f-4162-b0ed-c555b58be686', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-25 22:17:19.823000');
INSERT INTO `system_log` VALUES ('3bd8d835-6528-447b-bbad-84cfff2fa9c6', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:14:02.918000');
INSERT INTO `system_log` VALUES ('3bdfa41d-d805-4023-813c-0ccb89df5d61', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-17 16:09:12.667000');
INSERT INTO `system_log` VALUES ('3bfb5ffc-6328-4989-8a0e-609078b89af5', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:09:19.525000');
INSERT INTO `system_log` VALUES ('3c267439-efee-4eba-bf40-22fef24cd0ba', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-23 11:16:25.552000');
INSERT INTO `system_log` VALUES ('3c3c52a6-999a-4211-a8e0-d0966163de71', 'code51User@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DriverController.save', '驾驶员管理', '2023-02-24 09:46:35.683000');
INSERT INTO `system_log` VALUES ('3c3e7435-1cb7-4a85-920a-bb31ffac6f5e', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-24 09:51:31.281000');
INSERT INTO `system_log` VALUES ('3c672095-3fb4-499a-97bb-a75d7b15f60d', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-20 19:56:45.661000');
INSERT INTO `system_log` VALUES ('3c94bb7c-9747-4880-a927-08151b8807bc', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:52:06.642000');
INSERT INTO `system_log` VALUES ('3cb1de73-8374-45ca-aff1-d55b2801f8e8', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-27 14:28:23.953000');
INSERT INTO `system_log` VALUES ('3d01c743-3b7e-46b8-aca2-f25e726b3216', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-01-11 09:18:25.728000');
INSERT INTO `system_log` VALUES ('3d1e8289-9b01-4292-98d2-33cfe89ce3e5', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-28 10:07:30.935989');
INSERT INTO `system_log` VALUES ('3d348641-6af5-4b00-a2d7-1d554a11f373', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:20:29.946000');
INSERT INTO `system_log` VALUES ('3d548509-df88-4305-ba98-cf42f57f0339', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2022-12-19 13:02:16.459000');
INSERT INTO `system_log` VALUES ('3d651bca-a12b-4df1-aff5-bb3ccf5aab7f', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 11:50:18.555000');
INSERT INTO `system_log` VALUES ('3d6b0a54-8601-4281-acb0-f4401d6687ae', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:39:17.107313');
INSERT INTO `system_log` VALUES ('3dbe797b-6c60-4f5a-aa89-51614814c47a', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2022-12-19 13:01:36.018000');
INSERT INTO `system_log` VALUES ('3df5501e-fe3c-4f59-a365-eaabccb38031', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 09:42:18.604000');
INSERT INTO `system_log` VALUES ('3df58a99-d5db-405f-9cc7-f0b048844d90', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:23:31.724000');
INSERT INTO `system_log` VALUES ('3df660c7-74e3-4ab6-8deb-c73cb9383fcb', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-28 20:51:04.131309');
INSERT INTO `system_log` VALUES ('3e458605-cad4-46b0-b760-c67af93f5e26', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-20 19:58:52.210000');
INSERT INTO `system_log` VALUES ('3eb7b41d-e3f9-491b-ad34-e42797bf525f', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:48:33.313000');
INSERT INTO `system_log` VALUES ('3eef59c8-f6b8-469c-b08f-8139603ad4d6', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-28 13:06:32.998000');
INSERT INTO `system_log` VALUES ('3f01fc0f-d1db-4236-abbd-053b1a8c1c11', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-23 11:11:15.676000');
INSERT INTO `system_log` VALUES ('3f27f1ec-4ebb-4258-a224-a1243a36a201', 'admin@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2023-01-11 13:15:34.845000');
INSERT INTO `system_log` VALUES ('3f2cfe3c-4acc-4929-8109-281a035c3b3c', 'testUser@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2023-02-24 09:36:12.476000');
INSERT INTO `system_log` VALUES ('3f6c948f-5227-4020-9a46-bdc93f0d1f98', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 10:16:27.597000');
INSERT INTO `system_log` VALUES ('3f826ae9-c0d6-4cec-bf7c-ce2be5954233', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:16:25.113995');
INSERT INTO `system_log` VALUES ('3faf1012-92ac-4db4-833b-634f0226c70d', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:40:45.920000');
INSERT INTO `system_log` VALUES ('40259f31-a500-490a-95b3-c896840c3a86', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-16 10:30:20.213000');
INSERT INTO `system_log` VALUES ('40583164-1f3b-4aa5-9e9a-1027f3392ffc', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-19 23:24:55.556000');
INSERT INTO `system_log` VALUES ('405950ed-3091-43d6-bc56-2a6786da7e9c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:18:34.088000');
INSERT INTO `system_log` VALUES ('408595e3-ed1c-4d68-9486-3d6d2986e59b', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:07:02.869000');
INSERT INTO `system_log` VALUES ('40a9906d-c3d6-45c9-a025-d7a69776bc73', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:39:22.098222');
INSERT INTO `system_log` VALUES ('40bf0663-b196-4798-9b41-ba5a3bdd92ff', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:09:32.479000');
INSERT INTO `system_log` VALUES ('40daac65-0ea5-42ab-977b-997162188050', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-27 14:38:01.084000');
INSERT INTO `system_log` VALUES ('40dba9fc-6e0f-42b0-b5c4-3848a0810891', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:14:49.095983');
INSERT INTO `system_log` VALUES ('40eb7a3f-90f8-4faa-8651-fb632a1b41a9', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:32:32.395000');
INSERT INTO `system_log` VALUES ('40fc99c4-0cb4-4f56-8227-51ee453a4f7a', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-28 20:48:58.579038');
INSERT INTO `system_log` VALUES ('40fd35b5-e080-47e7-8356-9e741b3c6fea', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-26 23:22:47.262000');
INSERT INTO `system_log` VALUES ('4119190e-e9d9-41f1-a194-f7e08e9665a5', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-30 17:37:20.007000');
INSERT INTO `system_log` VALUES ('412d46e8-de45-482e-a2c7-a67f0cb32aac', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2022-12-18 11:36:37.580000');
INSERT INTO `system_log` VALUES ('412faf77-7cb5-4098-8474-ae743f4ade8c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-03 10:08:39.314000');
INSERT INTO `system_log` VALUES ('4159c10c-0544-4eab-9763-84ccebc8174e', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 22:44:07.320054');
INSERT INTO `system_log` VALUES ('418a7997-88d8-41fc-8ea6-5971612fbd81', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 20:56:37.579000');
INSERT INTO `system_log` VALUES ('418af412-c363-400e-abaa-2d0d65368638', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 10:36:23.498000');
INSERT INTO `system_log` VALUES ('41b06b7c-c217-453b-b78a-c1937cf7c412', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:33:51.052337');
INSERT INTO `system_log` VALUES ('41e38ac4-4d43-41e1-a03a-54e6b11408a7', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-18 08:21:58.192000');
INSERT INTO `system_log` VALUES ('420d03da-f550-4944-a158-aade93725df7', 'code51User@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2023-02-24 09:42:04.654000');
INSERT INTO `system_log` VALUES ('423e677d-dad4-4947-b4bf-c951ff29e860', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 09:26:06.221000');
INSERT INTO `system_log` VALUES ('427f0897-25d8-48aa-852e-f55abde89589', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-23 23:06:17.052000');
INSERT INTO `system_log` VALUES ('4283f794-b190-445a-85bb-e74be996f511', '匿名用户', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-30 16:07:22.048000');
INSERT INTO `system_log` VALUES ('4286d9ef-9a4c-4be5-b846-e6dad0699f9d', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 09:18:28.128000');
INSERT INTO `system_log` VALUES ('42d605ad-6acb-4d54-affa-0a2485de15bf', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-23 17:05:51.750000');
INSERT INTO `system_log` VALUES ('4363c955-53d8-4a3e-be75-7a78608e582a', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:15:48.962178');
INSERT INTO `system_log` VALUES ('439af84d-2caf-4af6-88ef-73b6e4da05bf', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-25 22:00:25.771000');
INSERT INTO `system_log` VALUES ('439e1c23-2abc-4b7a-857c-ed4f0305914e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-26 23:55:48.926000');
INSERT INTO `system_log` VALUES ('43a9c757-823a-49cc-9545-2971a33baa87', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 11:06:00.026000');
INSERT INTO `system_log` VALUES ('43c940de-a4cb-46ab-ab31-579085ec646e', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-04-28 20:13:51.952335');
INSERT INTO `system_log` VALUES ('443d0f7e-2cdf-498e-952a-26e16b0acc37', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-27 18:20:36.524551');
INSERT INTO `system_log` VALUES ('45b6ce7c-95b0-4e57-8916-601b404932f1', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:15:03.613747');
INSERT INTO `system_log` VALUES ('461f981b-5150-464a-8521-91e387c3c197', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:08:32.464000');
INSERT INTO `system_log` VALUES ('4668653a-9cb9-415c-99d9-1e98fdd38b24', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-26 23:55:04.288000');
INSERT INTO `system_log` VALUES ('4678390c-1556-4c86-a719-8416e57f8aae', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 18:12:43.132877');
INSERT INTO `system_log` VALUES ('46ab9780-be70-4a77-a749-c47870df27e4', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:52:36.969000');
INSERT INTO `system_log` VALUES ('46ace3ba-3eb7-4c2c-a179-53c1ba218f2b', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 09:37:55.438000');
INSERT INTO `system_log` VALUES ('472c9fe4-9314-45d9-b2cc-6e823cead51f', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-24 11:26:35.184000');
INSERT INTO `system_log` VALUES ('474566de-4af4-4f92-9f44-956d8508f85b', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-05-08 23:59:15.637125');
INSERT INTO `system_log` VALUES ('482f7d1c-b150-486a-87a2-ab80addb0774', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 09:33:23.902000');
INSERT INTO `system_log` VALUES ('48c64aff-a66f-4809-8da9-531c382eba0e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 23:22:03.121000');
INSERT INTO `system_log` VALUES ('48d387fd-5948-4558-9a54-ce4cb92c6322', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-19 11:19:31.867000');
INSERT INTO `system_log` VALUES ('48e4cafe-e45a-4dc7-bcc4-d44428cb34c8', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 14:26:01.405000');
INSERT INTO `system_log` VALUES ('49142a22-1be6-42de-b7ff-667231c92b97', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:17:06.655281');
INSERT INTO `system_log` VALUES ('491e8e68-6c4e-4f22-8278-8ec06796ab12', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-16 10:31:56.789000');
INSERT INTO `system_log` VALUES ('4990230f-7a78-4d75-bd5d-74dd255557f1', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 11:08:08.819000');
INSERT INTO `system_log` VALUES ('49a28ad0-1131-4f1d-a7b2-cf2431ac34d4', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:20:26.534000');
INSERT INTO `system_log` VALUES ('4a07e0cf-f32d-428b-a4cc-05b5b27065db', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 21:42:45.341590');
INSERT INTO `system_log` VALUES ('4a18f21f-da05-4be9-85aa-8b0e57669a3e', 'testUser@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.EmployeeController.delete', '员工管理', '2023-02-24 09:12:07.743000');
INSERT INTO `system_log` VALUES ('4a9297ca-2372-4e17-a111-b4809961586f', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 23:04:08.293000');
INSERT INTO `system_log` VALUES ('4a958c89-7537-419f-8a3c-26d5454b0134', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2022-12-19 12:59:22.957000');
INSERT INTO `system_log` VALUES ('4a9828c0-8bb4-41f1-93a4-d1058d1edeef', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 22:04:20.423000');
INSERT INTO `system_log` VALUES ('4a9cb221-9213-405a-842e-426816e3bb6f', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-25 21:59:40.818000');
INSERT INTO `system_log` VALUES ('4a9e38f6-ee21-42c3-9fc1-a13904763734', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 09:33:59.331000');
INSERT INTO `system_log` VALUES ('4ae005f3-a2f2-4ae2-8f71-8d76d74e5dee', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.VehicleController.save', '车辆管理', '2023-01-11 13:38:19.566000');
INSERT INTO `system_log` VALUES ('4af110c7-9d7d-4958-819b-d1186cd2e090', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-01-11 09:21:00.124000');
INSERT INTO `system_log` VALUES ('4af9c4e0-2a63-407b-9dd8-a520a1f9f25f', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 09:40:44.218000');
INSERT INTO `system_log` VALUES ('4b266054-8109-483d-91d3-b3e9457e89f2', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:28:32.927000');
INSERT INTO `system_log` VALUES ('4b3e167a-542a-4d4c-8962-ac45fbb2e950', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 09:18:40.423000');
INSERT INTO `system_log` VALUES ('4b8789a8-09ae-4226-ae37-e8828d2c00ce', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-27 18:15:30.010445');
INSERT INTO `system_log` VALUES ('4b9168a6-a0b1-4485-bb35-7e4522dcfbbf', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 11:54:47.591000');
INSERT INTO `system_log` VALUES ('4b97d469-01c7-4ddc-a66e-d5e8f22f3d62', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-01-11 10:16:42.295000');
INSERT INTO `system_log` VALUES ('4c2599d2-5b8d-4607-8ca6-7d69d4a7056c', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:27:45.401000');
INSERT INTO `system_log` VALUES ('4c41f964-a415-4c8c-bfcd-a0b90d50649a', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:21:10.080000');
INSERT INTO `system_log` VALUES ('4c5a201b-e749-450e-962d-05d239e9917f', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-19 14:59:35.709000');
INSERT INTO `system_log` VALUES ('4c869ad7-5dc8-4767-9c01-67ef58c01481', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-20 10:38:06.569000');
INSERT INTO `system_log` VALUES ('4ca0dd67-fa02-4495-9f7c-5fe414b0720f', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2022-12-19 12:46:47.691000');
INSERT INTO `system_log` VALUES ('4cb18d79-16ee-4ac4-b82d-2c2584ecb419', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 23:38:01.747000');
INSERT INTO `system_log` VALUES ('4cecd0d5-e9cf-4d77-97e0-dc1b70b3be6b', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:08:30.192000');
INSERT INTO `system_log` VALUES ('4cf29a07-f57d-44e4-899e-48017f2c82f1', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 10:18:36.221000');
INSERT INTO `system_log` VALUES ('4d5d95e6-d448-4102-90d8-c049af4246c4', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-20 10:38:12.924000');
INSERT INTO `system_log` VALUES ('4d7114ea-abf5-4cf3-b4e9-1d85da8c9e2a', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-04-13 21:24:36.175617');
INSERT INTO `system_log` VALUES ('4d81bcfc-f053-4dd4-80ef-c541dc40ab86', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:04:30.443000');
INSERT INTO `system_log` VALUES ('4dd0a93f-b1db-429e-aecc-00184ee8da03', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2023-01-11 13:35:52.107000');
INSERT INTO `system_log` VALUES ('4dee3907-c466-4ed6-9174-a25468ef199d', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 19:57:25.070000');
INSERT INTO `system_log` VALUES ('4e0a5fed-fee8-4b75-99ac-16639b8a0346', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 18:12:46.004000');
INSERT INTO `system_log` VALUES ('4e175b6a-d356-46e0-9fd2-f2bc84eb5aff', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:35:21.579000');
INSERT INTO `system_log` VALUES ('4e250a5a-3442-4d19-bdb8-e83809ecbce6', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-23 23:01:46.352000');
INSERT INTO `system_log` VALUES ('4e6b8c7b-b26c-47ec-b840-d7a6c395c7cc', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:31:07.020210');
INSERT INTO `system_log` VALUES ('4eb6d5b7-46d3-47e8-8ddc-474e7bc063e0', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 18:20:33.857510');
INSERT INTO `system_log` VALUES ('4ec70883-e787-42e5-8c56-d3ca309c98ca', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:15:39.853000');
INSERT INTO `system_log` VALUES ('4ec74dd2-ee42-4309-ba5d-bb8edb027050', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 23:01:47.536000');
INSERT INTO `system_log` VALUES ('4ece15eb-0ddb-4a19-b9e1-af07986bca1c', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:33:50.184911');
INSERT INTO `system_log` VALUES ('4ed4584b-55eb-456d-91df-9be5fb2caced', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-13 21:24:36.236709');
INSERT INTO `system_log` VALUES ('4f28b265-1dcb-4273-bb34-7952b08616c4', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:10:28.060000');
INSERT INTO `system_log` VALUES ('4f297bc1-8424-4d2b-9bc3-84c17500a892', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2022-12-19 14:59:35.712000');
INSERT INTO `system_log` VALUES ('4f5ffaa6-7d32-4a32-85e9-2f36b442ed9d', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-05-27 21:46:01.278893');
INSERT INTO `system_log` VALUES ('4f7766fa-39e2-4dc9-b24f-0695a644ef40', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-04-28 21:16:10.686289');
INSERT INTO `system_log` VALUES ('4f86e322-0131-4ba3-8a30-ca717473e150', '2361471705@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 17:35:32.995000');
INSERT INTO `system_log` VALUES ('4f9fc67f-f754-4016-a3bf-5c06cbc0e91b', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 16:38:17.753000');
INSERT INTO `system_log` VALUES ('4fa8acdf-3359-4f2e-98b3-6178b2bfe9a6', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-27 21:50:18.035000');
INSERT INTO `system_log` VALUES ('4fc7b4e1-51ca-467c-960d-aa006e0df18c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-01-11 09:18:30.097000');
INSERT INTO `system_log` VALUES ('50326f10-3f53-4a9b-ab72-0d50f72fd77f', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-04-28 21:17:27.966412');
INSERT INTO `system_log` VALUES ('5047c752-e010-4245-b948-569783561c09', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 12:58:26.204000');
INSERT INTO `system_log` VALUES ('509b6d47-eb41-489d-99c1-58745b767433', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-20 19:56:50.377000');
INSERT INTO `system_log` VALUES ('50c015b0-54a6-4bcb-80e9-b2dfe8fc0eaa', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-24 11:26:15.276000');
INSERT INTO `system_log` VALUES ('50d38331-31ae-4a9c-8a17-92e35e92853d', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-08 23:59:00.307395');
INSERT INTO `system_log` VALUES ('50e8705b-f65f-4103-acba-b745ce9b123c', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-27 22:42:45.838058');
INSERT INTO `system_log` VALUES ('510114bf-9977-44bc-8526-3e5e29701959', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 10:01:28.519000');
INSERT INTO `system_log` VALUES ('519b3f8b-640f-4222-badb-af223a5029ae', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-01-11 13:38:53.725000');
INSERT INTO `system_log` VALUES ('52424a22-40d9-4429-9837-be2e99de8e8e', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-20 07:59:36.470000');
INSERT INTO `system_log` VALUES ('535c9aea-dd82-4205-a871-cf5b191ec818', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-20 10:37:54.412000');
INSERT INTO `system_log` VALUES ('535fc79e-3e5b-4efe-8b98-4683f0d82f2e', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:10:52.320000');
INSERT INTO `system_log` VALUES ('5371f8b8-fcbf-4d2e-afe1-2d0bbd7cc467', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 23:19:29.808000');
INSERT INTO `system_log` VALUES ('537225e3-a228-4f5e-9588-2e05330d3e37', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 16:50:02.562000');
INSERT INTO `system_log` VALUES ('53afa028-d039-4837-bd8e-79cd64da01c5', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2024-06-27 14:39:15.040000');
INSERT INTO `system_log` VALUES ('53cd4b35-d513-4487-a262-93f93d67ef73', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:00:44.256000');
INSERT INTO `system_log` VALUES ('53d08dc7-ddd0-41e5-8a42-393f420c7b7a', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:36:28.283000');
INSERT INTO `system_log` VALUES ('53e15098-b2fb-4590-88ce-c462c9aa8430', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:16:05.984000');
INSERT INTO `system_log` VALUES ('5405f90e-f08e-4bb8-8130-0ad918a0c943', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 09:35:07.956000');
INSERT INTO `system_log` VALUES ('54399bd2-60c6-46a6-bdf5-0437ba7f1a6a', '2361471705@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 15:13:20.503000');
INSERT INTO `system_log` VALUES ('54912f66-d2a8-41ab-a29e-a82591150767', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-21 14:38:34.994000');
INSERT INTO `system_log` VALUES ('54eb1648-4485-4479-b42b-cf9218607dd8', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:34:48.073000');
INSERT INTO `system_log` VALUES ('5514e7a9-3338-428c-a73c-ca5197090caa', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-16 10:30:22.084000');
INSERT INTO `system_log` VALUES ('5515f8bf-4071-498b-8427-b95f13a4529a', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:49:30.652000');
INSERT INTO `system_log` VALUES ('553528d9-6fb6-4c3b-bef2-f693290ca73d', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-05-28 10:06:19.741893');
INSERT INTO `system_log` VALUES ('558c92ed-86e8-434d-b664-e9a40c265154', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-20 19:56:43.500000');
INSERT INTO `system_log` VALUES ('55c28808-306c-42d0-91a7-ac44c4b518ad', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:14:12.904683');
INSERT INTO `system_log` VALUES ('56047f41-b981-4270-a88f-138ea44a1301', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:55:48.044000');
INSERT INTO `system_log` VALUES ('5626af87-6f12-4fcc-aa16-fd6fb6cf6c17', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-18 08:20:31.869000');
INSERT INTO `system_log` VALUES ('5642be74-af46-421d-8590-167c612bbc12', 'jiegod_8ck@126.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-04-28 20:39:21.793342');
INSERT INTO `system_log` VALUES ('56815b5a-fb5c-408f-b316-7dae1dc6778f', '匿名用户', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-30 16:07:37.336000');
INSERT INTO `system_log` VALUES ('568eb704-1f0e-44fb-88a3-10024614f24e', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-18 08:25:03.159000');
INSERT INTO `system_log` VALUES ('56be44a7-ad6b-4c9f-bd5e-1ca515065951', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:12:47.209000');
INSERT INTO `system_log` VALUES ('5700f834-0080-4eb1-93b0-8dcb3317bf85', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2023-02-24 09:56:48.905000');
INSERT INTO `system_log` VALUES ('5750f90d-9089-42a4-9797-9ff3b9e90836', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:27:11.699000');
INSERT INTO `system_log` VALUES ('576c3036-6843-489c-9e1d-8a4ea3f69241', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:23:22.838000');
INSERT INTO `system_log` VALUES ('577f479c-3612-4c29-87f8-868f10d466a6', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 16:02:15.899000');
INSERT INTO `system_log` VALUES ('57b1d876-d4e8-4929-9592-02f4e337eb4b', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-26 23:37:07.542000');
INSERT INTO `system_log` VALUES ('57c199bc-56b2-4822-b2cb-2a986c6e3854', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:16:01.501000');
INSERT INTO `system_log` VALUES ('57cf1d65-b300-4ed4-8d7f-1bc4125b02c0', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:15:42.563256');
INSERT INTO `system_log` VALUES ('580e6df8-468e-477a-b9ca-ec4ccc8cbbe3', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-18 08:20:44.769000');
INSERT INTO `system_log` VALUES ('5814513a-32d0-4212-ba0e-8b0517eb803e', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-17 16:09:02.222000');
INSERT INTO `system_log` VALUES ('582b9027-62ed-4d5f-8475-0397587faf56', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-05-08 23:59:10.192666');
INSERT INTO `system_log` VALUES ('584d432b-008f-452e-98ec-f4cc4c4475b1', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-24 10:03:11.359000');
INSERT INTO `system_log` VALUES ('58c56d2b-c19a-422e-b24d-00d37cb88ca2', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-13 21:31:16.414296');
INSERT INTO `system_log` VALUES ('58ef9e2b-6a43-4e7d-a730-6a39c7e2778f', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-24 09:51:03.515000');
INSERT INTO `system_log` VALUES ('59636bf3-923a-40de-9658-e29c30fcc5bc', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:02:17.474000');
INSERT INTO `system_log` VALUES ('59a418df-d326-4c93-8d48-4b78e1cbf0ab', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:35:58.269580');
INSERT INTO `system_log` VALUES ('59c1a60d-d314-413f-b6c5-9d6cc572b226', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2024-06-27 14:38:48.784000');
INSERT INTO `system_log` VALUES ('59d7803d-d5c9-416f-a04e-f432cceadb9d', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:35:22.909818');
INSERT INTO `system_log` VALUES ('59db6b6f-90cc-470e-824c-83317f0a9f48', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-26 22:58:26.540000');
INSERT INTO `system_log` VALUES ('59ed5fe8-70d7-4b1a-8a29-e6c0f8e3161c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-01-11 09:23:34.586000');
INSERT INTO `system_log` VALUES ('59efd823-a978-4af3-b0f4-286525df08d8', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.EmployeeController.save', '员工管理', '2025-04-28 20:49:10.095610');
INSERT INTO `system_log` VALUES ('59f74eed-9a87-41b7-94c2-dd33b4799607', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:06:59.080000');
INSERT INTO `system_log` VALUES ('5a07a2f8-3186-4888-915e-9e95167b8f8c', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 22:42:38.823686');
INSERT INTO `system_log` VALUES ('5a373ce5-c78c-4e15-b949-c9b20811939c', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 16:34:02.932000');
INSERT INTO `system_log` VALUES ('5a7eab82-c628-496c-a091-c88a334cee4d', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-23 23:09:51.381000');
INSERT INTO `system_log` VALUES ('5a837ee6-527d-4d8d-a7c7-1fc1386a825f', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 21:15:33.582489');
INSERT INTO `system_log` VALUES ('5b0d0fc0-d1d0-4b81-bd17-4c0f9c697fea', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 17:06:03.891000');
INSERT INTO `system_log` VALUES ('5b106e27-c9e2-421d-8224-ab42e166991e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-23 23:26:15.715000');
INSERT INTO `system_log` VALUES ('5bc9defa-7cfd-4941-9368-a3439ef72738', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-04-13 21:24:40.080627');
INSERT INTO `system_log` VALUES ('5be162cd-6932-419f-a301-36573f4efa83', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:12:45.451000');
INSERT INTO `system_log` VALUES ('5bf42e1f-9549-45ed-a58a-929fd24147a6', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 17:07:14.171000');
INSERT INTO `system_log` VALUES ('5c990212-4723-4ed2-9236-e65e64250b0f', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-01-11 13:38:19.649000');
INSERT INTO `system_log` VALUES ('5c9ea31e-6011-4cba-979f-a1aa0ce0194b', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-27 21:48:20.121000');
INSERT INTO `system_log` VALUES ('5cca92dc-618d-42bc-845a-f031023ea875', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-01-11 11:08:02.124000');
INSERT INTO `system_log` VALUES ('5d1ed86b-6a65-4726-ab76-51f86620e350', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findByLikeName', '商品管理', '2022-12-18 17:03:10.144000');
INSERT INTO `system_log` VALUES ('5dd519bf-6334-4039-b74d-c3e05d52165b', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-01-11 09:22:55.919000');
INSERT INTO `system_log` VALUES ('5ddaaa2e-845e-4280-afc7-a61ad8a0b0ad', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 09:18:25.728000');
INSERT INTO `system_log` VALUES ('5dea6cd1-248f-4571-a5b6-4325b8aec309', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-23 23:22:39.277000');
INSERT INTO `system_log` VALUES ('5df75a09-94a0-471a-a1db-22e461af0028', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:01:13.326000');
INSERT INTO `system_log` VALUES ('5e10f646-15de-434b-a2c9-e057a0d8c194', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 13:36:09.637000');
INSERT INTO `system_log` VALUES ('5e2e5214-112b-45fa-bfd1-dbc29fe5232c', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:29:59.738000');
INSERT INTO `system_log` VALUES ('5e35d86b-22c0-45fc-b8b1-310c6b08f984', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-28 20:17:09.252305');
INSERT INTO `system_log` VALUES ('5e580fc6-f2ad-491a-b2c1-66c358fa747f', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-23 11:16:18.553000');
INSERT INTO `system_log` VALUES ('5e6b03d0-93f0-455a-b5da-e4b33a3f2e38', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:09:18.803000');
INSERT INTO `system_log` VALUES ('5eb7980d-ace7-49aa-a58b-803de3ede4ce', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-04 14:35:58.486000');
INSERT INTO `system_log` VALUES ('5eec705b-82fa-4aa4-9e6e-d1f854057329', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-24 11:01:23.825000');
INSERT INTO `system_log` VALUES ('5f21bf5b-8f24-4d99-b2a0-c3088843d98b', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-27 18:15:29.371406');
INSERT INTO `system_log` VALUES ('5f23d9fd-1425-41e5-98a0-fac3590617a3', 'code51User@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2023-02-24 09:47:08.842000');
INSERT INTO `system_log` VALUES ('5f347fa2-f2cf-4857-98b4-afd1f99450af', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-01-11 10:16:37.153000');
INSERT INTO `system_log` VALUES ('5fe8d44e-cb13-4007-8fca-860a9e4838a1', 'abc@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.EmployeeController.save', '员工管理', '2022-12-18 11:38:16.711000');
INSERT INTO `system_log` VALUES ('6010ec17-8a3f-4597-8819-f1a80427b2a5', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 20:24:14.407000');
INSERT INTO `system_log` VALUES ('602100e8-9a16-4d24-9134-f2d21b376855', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-20 19:56:41.891000');
INSERT INTO `system_log` VALUES ('6043a98a-6428-44ff-9039-ccd894e82638', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-24 11:18:04.657000');
INSERT INTO `system_log` VALUES ('60c8c12e-77f8-434b-80ed-26e15052686d', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-24 11:26:45.710000');
INSERT INTO `system_log` VALUES ('60dd5774-f27c-4426-95a2-daf1f0b8da74', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:37:31.644225');
INSERT INTO `system_log` VALUES ('60f76113-5a05-4fe8-93ab-d05170d38f41', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2023-01-11 09:23:25.714000');
INSERT INTO `system_log` VALUES ('610ccf87-1757-498c-bd8d-1b91db31e976', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2022-12-19 12:47:18.969000');
INSERT INTO `system_log` VALUES ('61287a93-c46f-4205-8b2b-c5ddb901c9b4', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2022-12-19 14:59:38.573000');
INSERT INTO `system_log` VALUES ('615b26ab-abdb-4d2a-9950-38ccbc6c0832', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-20 19:57:16.179000');
INSERT INTO `system_log` VALUES ('61e67807-a2e9-4e29-bb32-304629a16eb9', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-18 08:10:24.769000');
INSERT INTO `system_log` VALUES ('61ef515d-f2f2-4d27-b6f3-3059cfc296a8', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-20 07:59:28.377000');
INSERT INTO `system_log` VALUES ('62027433-5b3f-4d27-8015-7f44115115d9', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-03 10:08:39.317000');
INSERT INTO `system_log` VALUES ('62052353-d251-4af3-808e-81855ef072d4', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 12:48:03.923000');
INSERT INTO `system_log` VALUES ('6255eaf2-2118-454b-9d29-6b3525033c3e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:13:27.176000');
INSERT INTO `system_log` VALUES ('628a38b8-1f55-41e7-b191-7e6023e23491', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:32:01.832507');
INSERT INTO `system_log` VALUES ('62c6636e-5ed3-41db-90c4-f03f8e93483e', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 18:15:34.405431');
INSERT INTO `system_log` VALUES ('633b3a0f-84c6-47bf-912e-c40fe373a30c', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:25:38.580000');
INSERT INTO `system_log` VALUES ('63423c68-7ace-4c07-8aaf-407832551218', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-24 11:15:11.909000');
INSERT INTO `system_log` VALUES ('6367e2f1-1d11-460b-82ba-f49894123131', 'testUser@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2023-02-24 09:36:08.782000');
INSERT INTO `system_log` VALUES ('63a3a61c-d18e-4fc3-9b7b-9b90bd66f485', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-19 14:59:38.571000');
INSERT INTO `system_log` VALUES ('63f9e5d9-64b6-4526-b8e7-fc880c7dd69f', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:19:06.088919');
INSERT INTO `system_log` VALUES ('64363773-4c6d-414f-a8bc-987f7a917452', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:15:34.884000');
INSERT INTO `system_log` VALUES ('6436f3ba-3e87-4057-81a0-7940369dadc8', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-17 23:50:43.855000');
INSERT INTO `system_log` VALUES ('6465e428-f585-4d6e-b5d0-a3cfc2d6fbd5', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:30:06.124000');
INSERT INTO `system_log` VALUES ('64b9649c-652b-4c2f-8b2e-6c09c5270658', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-19 23:32:52.852000');
INSERT INTO `system_log` VALUES ('64f1a72f-d853-48f7-90db-aae4e0f81f1e', 'jiegod_8ck@126.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-04-28 20:31:43.669381');
INSERT INTO `system_log` VALUES ('650487e1-9ebd-4e94-8253-790e1697b0d1', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-03 09:43:44.027000');
INSERT INTO `system_log` VALUES ('6506cf08-4f24-4a78-be70-687c369cc2ff', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:08:25.678000');
INSERT INTO `system_log` VALUES ('6531f563-1f50-4b94-ac8b-bb61daf3dfb9', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 10:00:34.987000');
INSERT INTO `system_log` VALUES ('655db4e3-9b69-4340-8183-fc082c41c77d', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:37:55.771000');
INSERT INTO `system_log` VALUES ('656bb708-e7ed-4d3d-91e2-69565f22f5c1', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-18 08:19:40.693000');
INSERT INTO `system_log` VALUES ('659ab2bb-4701-43ed-b4c1-3fa5695a7a20', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-20 19:58:46.979000');
INSERT INTO `system_log` VALUES ('65a0903f-99e9-42c7-add1-b89eda191676', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-05-28 10:13:38.296137');
INSERT INTO `system_log` VALUES ('65ae6ee8-23dc-452a-ba58-fc820a2151db', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-01-11 13:15:57.298000');
INSERT INTO `system_log` VALUES ('65cf7751-6731-4785-9bb0-7cc2f510d31c', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:09:04.224000');
INSERT INTO `system_log` VALUES ('65f5f5b1-993a-482c-a4ba-ddaa46b567bb', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.EmployeeController.save', '员工管理', '2025-04-28 20:50:16.945491');
INSERT INTO `system_log` VALUES ('663cbd5b-01c6-4fb8-9e05-d37788ba0b8a', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:22:37.999000');
INSERT INTO `system_log` VALUES ('6640ccb3-3c71-4c3f-8c4d-f48105ea47c8', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:48:53.980000');
INSERT INTO `system_log` VALUES ('66615662-646d-4c09-aabe-5ba08e58b5b7', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:08:16.876000');
INSERT INTO `system_log` VALUES ('667516f8-abad-429c-97c1-1cce59552163', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:09:13.273000');
INSERT INTO `system_log` VALUES ('668b4199-a583-4bf3-8273-691180e125c8', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 22:24:03.597000');
INSERT INTO `system_log` VALUES ('66a7e469-ba7a-4121-b15c-6011b5cf0cbb', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 21:15:36.454454');
INSERT INTO `system_log` VALUES ('66dc4079-9375-4bb1-9507-d3e9aaa48f7c', '16256226@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-04-13 21:31:22.825423');
INSERT INTO `system_log` VALUES ('66dfc4d8-0fbd-45a1-b7f6-1af957879480', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 09:35:21.579000');
INSERT INTO `system_log` VALUES ('67116990-f98d-4944-9fc8-476bf8558abf', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-20 10:38:09.544000');
INSERT INTO `system_log` VALUES ('6715f020-ce91-47d0-a5b1-4b2272d0f1e0', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 22:00:56.340000');
INSERT INTO `system_log` VALUES ('67307fd5-5c71-4b1d-a939-2c4729155fd5', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-24 11:17:40.278000');
INSERT INTO `system_log` VALUES ('67382d01-9ffd-4e6c-8bce-25fd0479c47d', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:15:20.502949');
INSERT INTO `system_log` VALUES ('674d1d2d-0a7c-44a7-9dd6-02d915027afa', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-23 17:06:45.710000');
INSERT INTO `system_log` VALUES ('67a0cf15-3293-4c71-b35f-0706f9e24b2e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-26 23:55:17.615000');
INSERT INTO `system_log` VALUES ('67a4a7b1-9f84-4838-8401-ac2548c07f4c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 12:47:11.662000');
INSERT INTO `system_log` VALUES ('67cee7ad-912e-46a9-aeb1-b3d10e9096e2', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-24 11:05:17.977000');
INSERT INTO `system_log` VALUES ('67fd385d-507d-4435-9d35-da18958e4fa6', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 10:16:19.068000');
INSERT INTO `system_log` VALUES ('6818442b-6853-4a63-9b83-c5f2b50698b9', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:18:50.752771');
INSERT INTO `system_log` VALUES ('682be452-38f0-4cc4-9164-896316dca4d3', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-20 19:58:50.082000');
INSERT INTO `system_log` VALUES ('6854cf7f-b655-42d1-b978-de6bf51a9c2c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 09:07:37.027000');
INSERT INTO `system_log` VALUES ('689e5a8c-41b4-47b3-87d3-55511d4083f2', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-27 21:44:27.047672');
INSERT INTO `system_log` VALUES ('69101954-b174-43d3-8c83-5c5b95833241', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-28 10:07:46.081371');
INSERT INTO `system_log` VALUES ('696cc04e-c11c-4935-9470-81d4d80a1b12', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-05 23:45:12.050000');
INSERT INTO `system_log` VALUES ('6971f12a-a66f-4a5a-80f9-1d0d6bb35c40', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:23:50.356000');
INSERT INTO `system_log` VALUES ('698746d2-08c3-4f61-9d54-2e10f16bbbb7', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 09:09:28.024000');
INSERT INTO `system_log` VALUES ('69891b38-ae4d-40ef-b2c1-726ca832b400', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2022-12-19 13:10:27.866000');
INSERT INTO `system_log` VALUES ('699ec25e-59af-4185-b439-c84c6aed60ae', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-28 20:17:07.628287');
INSERT INTO `system_log` VALUES ('69acb2ec-17b7-4595-8e11-b31bda3886f2', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-18 08:15:03.475000');
INSERT INTO `system_log` VALUES ('69ffaa81-7d83-46d5-8ddb-815dbe16a935', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-23 11:12:00.110000');
INSERT INTO `system_log` VALUES ('6a395bf2-dd4c-4718-ae40-82d945cfd2da', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-23 17:05:55.883000');
INSERT INTO `system_log` VALUES ('6a44673e-a36a-425c-b503-c28a871778a1', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-03 10:08:40.798000');
INSERT INTO `system_log` VALUES ('6a9e57ca-2b3c-4cda-b8ef-6e4da5e9b886', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.WarehouseController.save', '仓库管理', '2025-10-18 08:20:23.802000');
INSERT INTO `system_log` VALUES ('6ab64d0a-2fc0-4a1a-86ae-bf28f5fc9cae', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:35:35.397994');
INSERT INTO `system_log` VALUES ('6aec16aa-664b-468f-aa8f-ccf9489bef1f', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-04-04 14:34:53.489000');
INSERT INTO `system_log` VALUES ('6c286a82-56cb-44e8-9eb1-674a82813c62', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-19 13:10:49.668000');
INSERT INTO `system_log` VALUES ('6c87eeb5-cae9-4eae-9dea-38cd5366905c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 10:15:26.756000');
INSERT INTO `system_log` VALUES ('6c930255-1458-45e3-8a4c-a262ec1b7138', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:11:32.902000');
INSERT INTO `system_log` VALUES ('6cc8a4e5-6a56-4b8f-9da2-e2ca4909e1f4', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-04-28 21:16:11.876279');
INSERT INTO `system_log` VALUES ('6d0b10a6-2276-4808-bc2a-ad6f0c0b0a32', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 13:37:00.467000');
INSERT INTO `system_log` VALUES ('6d365913-c78c-44a4-8ed3-5cd951c817d8', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:36:10.032000');
INSERT INTO `system_log` VALUES ('6d8ea315-9047-421c-ad9e-e977deea2594', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-17 20:15:23.015000');
INSERT INTO `system_log` VALUES ('6d9776c9-1303-4943-9767-5b287179a290', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:46:44.415000');
INSERT INTO `system_log` VALUES ('6dac5773-7bdb-44b9-b031-c11b2a40111e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-23 23:27:00.298000');
INSERT INTO `system_log` VALUES ('6dd2bc07-1a0a-4a72-a5bc-03c753357f08', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:51:27.769000');
INSERT INTO `system_log` VALUES ('6e32cf49-1b62-4701-9d6b-d31747ec361e', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:43:09.120000');
INSERT INTO `system_log` VALUES ('6e3af2ee-81e3-423c-a099-a085797ada4b', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:42:18.604000');
INSERT INTO `system_log` VALUES ('6e463fd1-d676-4f11-a545-d6be4a78ac81', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:09:42.711000');
INSERT INTO `system_log` VALUES ('6e477bdb-bb4c-46f7-94a7-541a1f3eed21', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-28 14:36:10.705000');
INSERT INTO `system_log` VALUES ('6e723e53-dad9-478d-b065-c949194dfb92', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 11:43:08.630000');
INSERT INTO `system_log` VALUES ('6e726b73-08f6-467b-a1f8-a0cf9faadeee', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:56:11.796000');
INSERT INTO `system_log` VALUES ('6e9091b1-580f-4401-ba0e-ffdf091b9d51', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-24 11:09:30.475000');
INSERT INTO `system_log` VALUES ('6eab2066-1403-438d-b4f1-5e31dd482191', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:36:17.494000');
INSERT INTO `system_log` VALUES ('6f0ffbdd-2606-4a10-b495-1dbbd230a56a', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:31:40.168315');
INSERT INTO `system_log` VALUES ('6f2877b1-271b-4ae9-8a64-6e86d7f99352', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 18:12:27.476266');
INSERT INTO `system_log` VALUES ('6f6487f3-906a-458c-9a20-d7df22110620', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 09:12:01.541000');
INSERT INTO `system_log` VALUES ('6f97c8a5-ae06-4ad9-9f0d-d70a0a3a5940', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-24 09:58:25.523000');
INSERT INTO `system_log` VALUES ('6fed4a72-ff29-4577-ba9a-e29b30ba1a91', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:22:50.646000');
INSERT INTO `system_log` VALUES ('70664bb1-1122-4187-8f42-155a7165c3d2', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-24 09:56:22.302000');
INSERT INTO `system_log` VALUES ('7094c847-e83c-475c-8a5b-c9b30787e427', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-13 21:24:25.061283');
INSERT INTO `system_log` VALUES ('709aba05-d48a-4bfb-b74c-0ee1c025d1df', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-23 11:16:39.378000');
INSERT INTO `system_log` VALUES ('70a48d12-6cb0-4eaf-b4d3-06998e7f03b6', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-04 14:35:42.536000');
INSERT INTO `system_log` VALUES ('70c71159-649e-4f30-8f8d-42a673022035', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-15 20:57:46.117000');
INSERT INTO `system_log` VALUES ('710a296b-f6d9-48a0-830c-50e3d8a87a99', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 16:02:21.020000');
INSERT INTO `system_log` VALUES ('712fc6d1-c32e-46f9-a817-b876b9ad1801', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 20:28:10.058000');
INSERT INTO `system_log` VALUES ('71407b8c-ba3d-4383-818d-967fed33d4b3', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 17:07:25.368000');
INSERT INTO `system_log` VALUES ('714be876-6a3a-458a-89e3-c0fe5ff41561', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-24 11:26:18.204000');
INSERT INTO `system_log` VALUES ('717b3416-42e5-4598-9bd1-2c7e24433ca9', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:35:23.810574');
INSERT INTO `system_log` VALUES ('71b748e4-e25d-4a90-ad92-7fa67f6cae30', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-13 21:31:23.064958');
INSERT INTO `system_log` VALUES ('71daf493-45bd-47a7-8123-cb5588f49cbd', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 22:58:44.331000');
INSERT INTO `system_log` VALUES ('71fd4223-d261-484b-8c8a-f91d20ef0ba8', '匿名用户', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-30 16:07:34.699000');
INSERT INTO `system_log` VALUES ('728e1455-bfa3-40cd-98ab-cf473758f347', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-08 23:58:12.324438');
INSERT INTO `system_log` VALUES ('731cdf8f-45f1-4776-af35-ec50390fa979', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-26 23:32:02.648000');
INSERT INTO `system_log` VALUES ('7325a00a-6cff-4463-adf3-5a92dba225f7', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-23 11:16:44.530000');
INSERT INTO `system_log` VALUES ('732e85b8-03b5-4d0d-a5c8-600f17e4a504', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:06:21.885000');
INSERT INTO `system_log` VALUES ('73804cf7-1b7d-40ef-a539-55d29b283610', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:36:06.117000');
INSERT INTO `system_log` VALUES ('7386d8d8-dbe6-46e4-9ebd-073006b6758a', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:31:48.509290');
INSERT INTO `system_log` VALUES ('73fd9c48-dc04-46c3-9d8b-ce2669b2e784', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-17 22:03:46.426000');
INSERT INTO `system_log` VALUES ('74879c5e-e32e-4d50-b0d8-3c034b25c136', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:01:25.030000');
INSERT INTO `system_log` VALUES ('74a9d649-1f7e-492f-9ac9-094049ed79ad', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 10:16:40.679000');
INSERT INTO `system_log` VALUES ('74ad2c22-ffbb-4a25-bc0f-3be4ce5abf3b', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:36:08.806000');
INSERT INTO `system_log` VALUES ('74e29360-788e-4380-8d10-ac1e5e9c5004', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:07:47.798000');
INSERT INTO `system_log` VALUES ('752f3b4d-84a1-4dcb-936f-cddce1425df2', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-05-27 18:13:53.919600');
INSERT INTO `system_log` VALUES ('76348164-8a6e-4ad0-b2a8-e1c4a70cc88e', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:22:39.874000');
INSERT INTO `system_log` VALUES ('7698b251-33b0-45c9-aef2-13968d5eedae', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-18 08:22:04.943000');
INSERT INTO `system_log` VALUES ('76d40715-5232-4846-9143-5c71f793479d', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2024-06-26 23:42:37.289000');
INSERT INTO `system_log` VALUES ('77d1ce43-d6b8-46b6-ab08-c0d4c36adda4', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 18:15:25.343933');
INSERT INTO `system_log` VALUES ('77f6fbf3-1ee6-49a6-9f53-4826d8aeb28c', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:39:00.298000');
INSERT INTO `system_log` VALUES ('78064dd8-8ac1-448a-819a-a28364793648', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:19:18.310494');
INSERT INTO `system_log` VALUES ('786e84ac-4de4-48fb-bd9b-46a21d37a0d0', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2022-12-23 19:04:25.853000');
INSERT INTO `system_log` VALUES ('788eed35-43df-4a58-8b34-9d5927aa60c0', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-27 14:28:16.419000');
INSERT INTO `system_log` VALUES ('78a8fcc8-cafe-4244-984f-1b0d0dc69602', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-23 11:15:17.168000');
INSERT INTO `system_log` VALUES ('78cb3cb2-cf67-4329-99d8-a84e9af4944d', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 16:58:35.081000');
INSERT INTO `system_log` VALUES ('78f52edd-ca6f-47ac-9bc0-9cf420db5a7c', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:23:55.808000');
INSERT INTO `system_log` VALUES ('79896e7f-37dc-41ec-b905-d1e9e20aef29', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-28 10:13:19.010294');
INSERT INTO `system_log` VALUES ('798c3c0a-c619-4b37-afa3-75352738669d', 'jiegod_8ck@126.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-04-28 20:32:01.551877');
INSERT INTO `system_log` VALUES ('79aee427-15ea-4574-a7d1-9c948a4a1b2a', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-16 10:30:22.072000');
INSERT INTO `system_log` VALUES ('79e23c65-6a31-41a5-8e63-b7386648f860', 'code51User@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2023-02-24 09:53:16.337000');
INSERT INTO `system_log` VALUES ('7a118ca1-b2e8-483b-b4a1-434581b8da05', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 10:00:29.254000');
INSERT INTO `system_log` VALUES ('7a5c765a-9046-4bc7-bcd4-b5188bfc82c7', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:48:42.633000');
INSERT INTO `system_log` VALUES ('7a7c3a25-b6f8-4343-b3af-c4d7aa51f135', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 16:55:04.873000');
INSERT INTO `system_log` VALUES ('7a9594e6-4253-4635-a283-71d7e2511275', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-15 21:05:20.969000');
INSERT INTO `system_log` VALUES ('7aead6e5-4d22-4a65-93ad-1895024f861a', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:16:07.118157');
INSERT INTO `system_log` VALUES ('7b09e3e6-3bc9-4449-9e45-1fbf7a0f907f', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 11:06:28.233000');
INSERT INTO `system_log` VALUES ('7b154f95-b1a0-4eaa-b653-ff76d5a2e32c', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-28 21:11:11.032308');
INSERT INTO `system_log` VALUES ('7b2333c8-2614-432c-a370-b2f5caad5b46', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-26 23:26:38.594000');
INSERT INTO `system_log` VALUES ('7b36499a-6520-4789-bcd9-de302747ed9b', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-05-27 21:45:45.111687');
INSERT INTO `system_log` VALUES ('7b4a4cc4-ebc2-49a7-b5dc-584e58965e21', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 23:27:55.001000');
INSERT INTO `system_log` VALUES ('7b5e31d6-767f-4282-b075-e0c4319a0953', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-20 10:37:45.272000');
INSERT INTO `system_log` VALUES ('7bfecb17-ceba-494b-9aa8-bde5d57368f1', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-26 23:41:26.235000');
INSERT INTO `system_log` VALUES ('7bffa45c-36ef-4d75-b9ca-110ca05c2f96', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.VehicleController.save', '车辆管理', '2025-10-24 11:05:17.537000');
INSERT INTO `system_log` VALUES ('7c09323f-f228-47ae-83a3-ed60543f5daa', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2022-12-19 11:21:33.793000');
INSERT INTO `system_log` VALUES ('7c386a83-a33a-4103-be85-9d3847a2ea5e', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-19 23:32:55.097000');
INSERT INTO `system_log` VALUES ('7c81db95-9416-437d-8f92-c168bc79f2e3', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-19 23:21:54.262000');
INSERT INTO `system_log` VALUES ('7cadd668-eca0-4f67-a4b5-b4d3de6ca0b9', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:35:04.713987');
INSERT INTO `system_log` VALUES ('7cd7a516-ca34-4c36-b853-cf3193b569af', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 23:21:35.500000');
INSERT INTO `system_log` VALUES ('7ce05719-7b2e-423f-84c9-4f18a55f2570', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:31:41.025319');
INSERT INTO `system_log` VALUES ('7d090768-56bd-43dd-86f6-d3471cceb119', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2022-12-19 13:02:24.215000');
INSERT INTO `system_log` VALUES ('7d5f0fd5-dab5-4069-b014-8513023622b9', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 22:23:54.285000');
INSERT INTO `system_log` VALUES ('7d677f7d-cb99-4752-9fc5-3799c32d064c', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:45:23.354000');
INSERT INTO `system_log` VALUES ('7d7821dd-a6f3-49e3-8d78-a74212914137', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-05-08 23:59:13.639546');
INSERT INTO `system_log` VALUES ('7d935ec4-d3fe-487d-9ece-6366a94afb64', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:42:47.718000');
INSERT INTO `system_log` VALUES ('7d95f4cd-a47c-4312-bfdf-417a7ddb5d7b', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-23 11:12:02.539000');
INSERT INTO `system_log` VALUES ('7dad0595-6982-4107-8fba-ef417819eea2', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-03 10:08:03.622000');
INSERT INTO `system_log` VALUES ('7dd6f561-aa7c-4a76-9764-fcc43ce6bc6e', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-05-27 18:14:01.712105');
INSERT INTO `system_log` VALUES ('7dd7b30d-d7d4-4692-866e-c4d60d189e89', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-20 10:37:43.940000');
INSERT INTO `system_log` VALUES ('7dff8528-3b1d-4c28-8d2a-60ebf9fd67e1', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:37:55.438000');
INSERT INTO `system_log` VALUES ('7e984365-6255-4725-8838-094311da79c8', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-18 11:50:53.794000');
INSERT INTO `system_log` VALUES ('7ea845eb-9015-4ced-ab45-06431f99afe2', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 23:21:14.043000');
INSERT INTO `system_log` VALUES ('7ead999f-2bb6-4777-a9f0-99644a9ea474', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:15:55.620000');
INSERT INTO `system_log` VALUES ('7ec4c605-f196-45aa-87b1-fe622e31711e', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-04 14:33:34.042000');
INSERT INTO `system_log` VALUES ('7ed3a56d-c12d-4adb-ae9e-c113d802d68f', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-19 22:24:14.596000');
INSERT INTO `system_log` VALUES ('7ed3b25d-1449-4c68-ae5a-bddd83857afc', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-20 19:58:58.630000');
INSERT INTO `system_log` VALUES ('7efca382-d6d9-4f66-97f6-366bd7e1a662', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-04-28 20:36:41.712852');
INSERT INTO `system_log` VALUES ('7f53e3aa-b072-4ed4-b7f2-eb59354d5168', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 10:15:37.560000');
INSERT INTO `system_log` VALUES ('7f97abde-407d-4d97-92ae-b27fa4bedd6e', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:32:03.472209');
INSERT INTO `system_log` VALUES ('7fb2bafc-c4d3-4934-af84-c9e1e71def88', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-19 23:27:59.986000');
INSERT INTO `system_log` VALUES ('7fc63512-5b12-4604-a47a-027a30d5ce82', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-05-09 00:00:57.946501');
INSERT INTO `system_log` VALUES ('7fcb82a4-9c9a-4d0c-a93c-58eeae357c46', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 10:54:05.831000');
INSERT INTO `system_log` VALUES ('7fcc9754-77d4-44fc-b937-9168d68e67ab', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:07:42.920000');
INSERT INTO `system_log` VALUES ('7fdf4747-be83-4646-b225-7fad669c0c5c', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 17:08:07.869000');
INSERT INTO `system_log` VALUES ('801be24a-ed8a-4f02-b667-233f7bbf8d5c', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:48:00.963000');
INSERT INTO `system_log` VALUES ('8038a64c-bbf6-48a8-a656-e2beefd53185', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-19 14:59:45.220000');
INSERT INTO `system_log` VALUES ('805ff1fb-e322-4d31-a7f0-c7d3837e0c66', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-17 22:04:18.681000');
INSERT INTO `system_log` VALUES ('812a1b43-cd9b-42d9-bd1b-971f607c4ac5', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:36:33.822905');
INSERT INTO `system_log` VALUES ('8131d283-d787-4dd2-aff1-82cbf518c239', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:06:53.592000');
INSERT INTO `system_log` VALUES ('816f92e1-6738-44aa-94a5-d270798ce928', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findByLikeName', '商品管理', '2025-10-24 11:04:20.426000');
INSERT INTO `system_log` VALUES ('818b03bd-995b-440b-a697-c40f4e8768c8', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-26 23:52:35.051000');
INSERT INTO `system_log` VALUES ('81a1544e-a64b-45f8-801d-ff66e6011b3e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:07:04.820000');
INSERT INTO `system_log` VALUES ('81c44a88-8258-4a2f-b302-32b7abae7371', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-04 14:34:07.760000');
INSERT INTO `system_log` VALUES ('82650e47-8ea5-47fb-aab8-64511e57a5e0', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-17 15:37:25.112000');
INSERT INTO `system_log` VALUES ('82e8c270-cd78-4df9-8b34-7e8b06084aeb', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-19 23:38:06.776000');
INSERT INTO `system_log` VALUES ('82eebcdf-be8b-4351-aab0-8dc697107234', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:35:36.332167');
INSERT INTO `system_log` VALUES ('83377bfe-dfb8-48de-9be3-1dbd8fc4db3c', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-24 09:48:54.298000');
INSERT INTO `system_log` VALUES ('83521493-c2ad-4f24-a292-ca4841efe8fd', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-24 11:26:44.431000');
INSERT INTO `system_log` VALUES ('83a34e34-59d4-494d-bd79-f1d28e835e99', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-17 20:14:29.255000');
INSERT INTO `system_log` VALUES ('83fdfa97-140f-4534-9a86-973215a52eee', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-16 10:30:14.195000');
INSERT INTO `system_log` VALUES ('844d3a39-4d0f-4ad0-bd3a-502a5f4bc762', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-27 20:56:42.557000');
INSERT INTO `system_log` VALUES ('849c2166-405c-4000-85db-adb6f29a69d6', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-25 22:17:10.586000');
INSERT INTO `system_log` VALUES ('84b4484b-fd4d-4c62-82e4-5efa5ffa8481', '2361471705@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 17:56:48.894000');
INSERT INTO `system_log` VALUES ('84deff15-a837-49f9-90a8-bc8380c8c239', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:38:22.223308');
INSERT INTO `system_log` VALUES ('85004903-c253-4f28-a6ca-318e9733e259', 'testUser@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2023-02-24 09:36:13.328000');
INSERT INTO `system_log` VALUES ('855513eb-a971-4373-a12e-f7eb2bb9f346', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:37:51.524847');
INSERT INTO `system_log` VALUES ('85bc9c51-4a2b-4e1a-907c-2614a9b93247', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-05-28 10:11:03.773289');
INSERT INTO `system_log` VALUES ('86b4cef4-6074-4afb-80e6-8676d8916271', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-23 19:03:04.408000');
INSERT INTO `system_log` VALUES ('87418c1b-de14-47bc-a0bf-e5f88d905bac', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:34:42.225250');
INSERT INTO `system_log` VALUES ('87ab1940-ef20-45cc-81c5-ec7bc0f7026f', '16256226@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-04-28 20:15:46.502221');
INSERT INTO `system_log` VALUES ('87b3dac3-c763-48a3-b832-beb3a511662f', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-18 08:10:55.683000');
INSERT INTO `system_log` VALUES ('87d53dc3-a291-464f-b69a-eee9e6adcfa0', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-05-08 23:59:11.471399');
INSERT INTO `system_log` VALUES ('87ebf1dc-5a32-44bb-a68d-b4bfddcd74a0', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:51:18.364000');
INSERT INTO `system_log` VALUES ('884a53f1-a4f9-4311-8943-ced69b22aecf', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 20:01:53.105000');
INSERT INTO `system_log` VALUES ('885403c9-ac90-40b4-8410-bbf83d43c4be', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:31:43.960365');
INSERT INTO `system_log` VALUES ('889a7185-b248-497b-8a49-a7345322f50b', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:16:24.273809');
INSERT INTO `system_log` VALUES ('891128f6-b41c-4615-9825-51f4d9f6bbf3', 'code51User@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2023-02-24 09:47:48.992000');
INSERT INTO `system_log` VALUES ('891e6938-973d-443a-bfda-e9d2e860b742', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-27 21:44:36.539683');
INSERT INTO `system_log` VALUES ('893be95c-57e5-4a1a-bebd-884529d29029', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-15 21:05:17.399000');
INSERT INTO `system_log` VALUES ('89503acc-f45d-4055-a883-d28c779e161d', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-23 11:11:55.905000');
INSERT INTO `system_log` VALUES ('898b8921-f458-4431-93f5-5c3547f5737e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-23 23:07:44.242000');
INSERT INTO `system_log` VALUES ('89a3ba83-31d5-45e2-8152-8b836fe0043a', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-24 11:18:44.110000');
INSERT INTO `system_log` VALUES ('89a433f2-d150-446d-993f-cc188200253a', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:13:38.620946');
INSERT INTO `system_log` VALUES ('8a0fcdca-b5ca-4e17-9f82-af0a89b12a07', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:23:42.019000');
INSERT INTO `system_log` VALUES ('8a6ecb56-14ee-4e32-a887-d42a01cc1c16', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-28 20:13:51.918953');
INSERT INTO `system_log` VALUES ('8ad0809f-e047-44e7-8c99-1df776778bc3', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 13:36:49.318000');
INSERT INTO `system_log` VALUES ('8afe90ee-a87a-40b2-9fb8-049bbd183045', 'testUser@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.EmployeeController.delete', '员工管理', '2023-02-24 09:35:21.551000');
INSERT INTO `system_log` VALUES ('8b021800-8f73-4240-aec8-f11a88c8086e', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:12:27.122000');
INSERT INTO `system_log` VALUES ('8b062c79-1617-4de3-9e4d-5310a1384024', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:04:04.622000');
INSERT INTO `system_log` VALUES ('8b23efe5-26f1-4133-8f71-61243943a52b', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findByLikeName', '商品管理', '2023-01-11 09:17:39.950000');
INSERT INTO `system_log` VALUES ('8b26224c-6845-4d5d-829f-f5563a077e93', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2024-06-27 14:23:48.716000');
INSERT INTO `system_log` VALUES ('8b718ac1-9843-458c-b10a-b0dcd9629356', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:36:01.485000');
INSERT INTO `system_log` VALUES ('8bd30a79-dfb2-4879-b5e0-889c2fd62ec1', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-08 23:58:41.690522');
INSERT INTO `system_log` VALUES ('8bf9f105-3c7b-43f6-8cee-936686aef2f4', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-19 11:43:15.416000');
INSERT INTO `system_log` VALUES ('8c6f4c00-77bd-4528-ae9e-562403a77596', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 20:14:30.997000');
INSERT INTO `system_log` VALUES ('8cb42f3a-3376-462f-8e69-e2f483f72688', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 10:57:51.084000');
INSERT INTO `system_log` VALUES ('8cb8de32-f2bd-4b13-89b7-41c948964462', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-26 23:41:58.678000');
INSERT INTO `system_log` VALUES ('8da8d1ce-1ffb-44c6-817a-5f5f40479436', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:07:28.613000');
INSERT INTO `system_log` VALUES ('8db09e9d-cc51-44a1-bb0e-8d752ea75a1c', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-23 11:16:38.962000');
INSERT INTO `system_log` VALUES ('8dd4843a-c24c-43ed-88f9-f6a9ad0f9f0b', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:22:09.189000');
INSERT INTO `system_log` VALUES ('8dd485cb-09df-4a8c-ba90-6575266d7dfb', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:56:53.934000');
INSERT INTO `system_log` VALUES ('8de5377d-8009-4efc-91c7-4d5148ed9296', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:41:47.753000');
INSERT INTO `system_log` VALUES ('8df48ec2-e8a1-4a4a-98e5-2a5f0ccb81ea', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:29:03.784000');
INSERT INTO `system_log` VALUES ('8dfdc8b5-4949-49dd-905a-d4532c61e137', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:06:56.602000');
INSERT INTO `system_log` VALUES ('8e16d7e3-1def-4202-95db-79e0d68afe77', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-24 09:56:31.316000');
INSERT INTO `system_log` VALUES ('8e8dbcf4-fe61-4a2e-af8a-b907cdc18471', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:19:06.930127');
INSERT INTO `system_log` VALUES ('8ed16e33-3a2b-4c6c-8a6e-3b3dee821016', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2024-06-26 23:43:05.237000');
INSERT INTO `system_log` VALUES ('8eea8dc5-6e55-4fc2-bb5a-6ce11a1432f3', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-26 23:55:01.320000');
INSERT INTO `system_log` VALUES ('8f222a86-71fc-4c39-acd4-0e0fdc878259', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 10:00:41.411000');
INSERT INTO `system_log` VALUES ('8f42a647-9572-494c-abe7-6906c8717939', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:34:50.316000');
INSERT INTO `system_log` VALUES ('8f9cd10d-f157-4e24-bd18-1a65c36ba4f2', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-23 11:17:22.497000');
INSERT INTO `system_log` VALUES ('8fe2ae7e-d764-45b0-81e0-1507c7e62c1e', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:16:40.527000');
INSERT INTO `system_log` VALUES ('8ff0ff1c-3eac-43c6-be5b-83685f100a09', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-27 14:23:48.709000');
INSERT INTO `system_log` VALUES ('9069d579-6f6f-410e-9c82-d74dfd739180', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 13:10:51.133000');
INSERT INTO `system_log` VALUES ('907a9417-2052-407a-adb2-c184b9d66da2', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 11:07:38.462000');
INSERT INTO `system_log` VALUES ('90bcca80-85c3-4db4-bdea-6a80a763605c', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:35:24.025000');
INSERT INTO `system_log` VALUES ('9156a955-22d7-4ba0-af7b-3d79fd3927f2', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 20:23:53.290000');
INSERT INTO `system_log` VALUES ('9194a827-5bd5-4296-88a3-752ca0b03093', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:36:12.037000');
INSERT INTO `system_log` VALUES ('91a0b720-8d25-48a4-a662-871a44993e0e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-26 23:22:17.190000');
INSERT INTO `system_log` VALUES ('91a9e96e-1e9c-4447-b112-d51d9c8577df', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:56:19.794000');
INSERT INTO `system_log` VALUES ('91b204bb-9e03-4493-9dbb-943659814711', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:35:52.417000');
INSERT INTO `system_log` VALUES ('91ea3fa3-5d44-4264-a25e-20c610384c31', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-25 21:58:31.081000');
INSERT INTO `system_log` VALUES ('92052deb-aa70-4da4-b249-a89b15652fde', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:16:40.055000');
INSERT INTO `system_log` VALUES ('920c0086-69fc-4efe-b772-38aa72c1a6fc', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:37:05.028000');
INSERT INTO `system_log` VALUES ('920f8cac-a0d2-4e1f-a913-32c6e8f3d8bb', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-01-11 13:15:14.006000');
INSERT INTO `system_log` VALUES ('92536eb0-6cb1-444d-a5dc-211daac6f7aa', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:34:58.810028');
INSERT INTO `system_log` VALUES ('9267859b-2d26-4cbf-95ca-f0afc3f1108b', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-23 11:16:30.719000');
INSERT INTO `system_log` VALUES ('926c2a02-77aa-4923-bfa0-e13e215d0016', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-24 11:01:12.161000');
INSERT INTO `system_log` VALUES ('928f7d40-4f11-4af6-ac98-d206c037ada8', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-26 23:55:15.605000');
INSERT INTO `system_log` VALUES ('929ee962-2f0a-42d5-8553-ef5dfd572730', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-05-28 10:06:21.945924');
INSERT INTO `system_log` VALUES ('92a25ba9-d9c2-4a4a-ba29-4d4c8f745763', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 12:48:13.998000');
INSERT INTO `system_log` VALUES ('92a71cff-64c5-4274-898b-bb6e659c5913', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2024-06-27 14:26:12.647000');
INSERT INTO `system_log` VALUES ('92cd14b2-9a0a-430e-b229-9278ca41ce4e', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 12:15:53.834000');
INSERT INTO `system_log` VALUES ('92cf18b9-ca9b-4697-af38-0c559ee8918b', '16256226@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-04-28 20:19:21.332721');
INSERT INTO `system_log` VALUES ('92dfa60b-1103-4645-810a-95df334547ad', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-05 23:38:19.878000');
INSERT INTO `system_log` VALUES ('92e1043a-7483-43a2-ac99-4cfd7316904e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-24 22:33:10.668000');
INSERT INTO `system_log` VALUES ('92ed1e3c-44f9-4134-be32-e29de24f6583', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-27 21:51:49.672000');
INSERT INTO `system_log` VALUES ('93077e83-1df2-40dc-bce6-d4bfdaa94c3a', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 09:12:26.295000');
INSERT INTO `system_log` VALUES ('93991ed5-ca06-464c-a1a1-427bf8d3a710', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 21:43:14.742000');
INSERT INTO `system_log` VALUES ('93d34123-9d41-4db9-9888-b2210e96a0ee', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:14:06.424000');
INSERT INTO `system_log` VALUES ('93e4b548-2871-4051-8e51-2e10cb3e8b86', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-13 21:24:24.177675');
INSERT INTO `system_log` VALUES ('9411db9c-f284-497e-b5e9-de73e53e7bce', 'testUser@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2023-02-24 09:36:10.808000');
INSERT INTO `system_log` VALUES ('94347204-1fab-43cc-9266-3d9b8f7bb4b9', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-19 23:30:51.908000');
INSERT INTO `system_log` VALUES ('9470c6ce-118f-44a7-8ddd-ee135f3569f6', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:40:34.346000');
INSERT INTO `system_log` VALUES ('948c5640-b897-4d5e-9a59-f7b3db08980d', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:42:50.952000');
INSERT INTO `system_log` VALUES ('94fab12f-022a-4ccb-8705-12d05ac5dae0', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-20 19:55:14.452000');
INSERT INTO `system_log` VALUES ('952af118-81ac-4758-8809-a515abf10a91', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:17:04.823000');
INSERT INTO `system_log` VALUES ('957f05e6-1ee8-4220-b7ad-1e964f8d9b46', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-04-28 21:17:27.025917');
INSERT INTO `system_log` VALUES ('95f77bcb-571e-4545-be5e-529697d9efcc', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:29:01.177000');
INSERT INTO `system_log` VALUES ('961d0565-8675-4803-920f-a580db031058', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 17:02:56.340000');
INSERT INTO `system_log` VALUES ('9669344d-9190-4f09-bc91-2993355c9fdd', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-26 23:31:59.434000');
INSERT INTO `system_log` VALUES ('9670d1f4-42ba-4081-a133-b86a9b038bfb', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:32:51.704259');
INSERT INTO `system_log` VALUES ('967e35b3-25b2-4203-814a-b22575808da5', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-24 10:01:06.032000');
INSERT INTO `system_log` VALUES ('96a31182-3619-4107-a3ef-6dab83e3dc4b', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-20 19:58:57.415000');
INSERT INTO `system_log` VALUES ('975d858f-3a87-4686-a15d-07d6c62682f2', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:35:39.755000');
INSERT INTO `system_log` VALUES ('97b27589-b277-408c-8cbd-f8e06a67f4fa', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-24 09:48:13.688000');
INSERT INTO `system_log` VALUES ('97e67c3f-1981-420e-86e1-9b7183ab5969', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-04 14:33:57.606000');
INSERT INTO `system_log` VALUES ('9805e01f-c84a-49f3-88f0-0d4443a56509', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-27 18:20:35.539495');
INSERT INTO `system_log` VALUES ('983bc0be-dec6-4e1d-8174-475bb1723888', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 11:05:37.280000');
INSERT INTO `system_log` VALUES ('98b80acc-83f3-4707-b597-72ba7dcb2b92', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-13 17:24:59.392000');
INSERT INTO `system_log` VALUES ('98fe6564-93ad-4cb4-a785-953dccbdcaed', '16256226@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-10-18 08:10:27.662000');
INSERT INTO `system_log` VALUES ('99a19272-3632-483e-916e-a3071f039e5a', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 11:07:17.982000');
INSERT INTO `system_log` VALUES ('99cbbec0-edb4-4bb3-adbc-38cc9c279710', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 23:06:18.148000');
INSERT INTO `system_log` VALUES ('99fbc882-8e1c-450c-b65a-75e159882697', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-23 23:33:19.917000');
INSERT INTO `system_log` VALUES ('9a193555-36ba-407a-bd3e-91d721c769cc', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-24 11:15:04.888000');
INSERT INTO `system_log` VALUES ('9a23f909-aab2-4a44-a094-d747b8068a35', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:36:16.578000');
INSERT INTO `system_log` VALUES ('9a27645b-2fbe-4a22-861b-8b42abdadf8f', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2022-12-19 13:01:51.330000');
INSERT INTO `system_log` VALUES ('9a4c07d8-0603-44fc-a9fa-0a0f42aa571a', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-20 19:51:01.697000');
INSERT INTO `system_log` VALUES ('9a6ef09a-5829-4e3e-816d-eb0da70c8dd2', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 09:27:04.690000');
INSERT INTO `system_log` VALUES ('9ae78be8-f20e-45b1-ab49-8ad1e319dfa0', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-18 08:19:55.986000');
INSERT INTO `system_log` VALUES ('9aee6ed6-7418-4fbc-aec6-b78f962a016f', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 16:34:39.556000');
INSERT INTO `system_log` VALUES ('9b53b922-4106-46e6-bf09-c918ab29fe7e', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 20:27:52.739000');
INSERT INTO `system_log` VALUES ('9b8561d7-969a-43fc-9b37-3702c9e84ff9', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-19 23:29:45.831000');
INSERT INTO `system_log` VALUES ('9b93a8a0-0632-477f-a813-9d733d1969a6', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-23 19:02:10.634000');
INSERT INTO `system_log` VALUES ('9baad216-680a-4cc3-bbee-c3caf87c6408', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:54:10.960000');
INSERT INTO `system_log` VALUES ('9bc18493-1047-4567-bfad-ae70c8c30fb3', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:20:33.767000');
INSERT INTO `system_log` VALUES ('9c30753b-d687-41e2-a2d6-7614e41e5c48', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 23:20:59.406000');
INSERT INTO `system_log` VALUES ('9c341bc3-19aa-40a4-818f-27427e1c4619', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-27 18:20:40.908693');
INSERT INTO `system_log` VALUES ('9cc61ecb-4856-4da2-8930-e87da56f9dee', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 16:42:56.649000');
INSERT INTO `system_log` VALUES ('9d09f07d-3f88-4a4a-88d4-458ba370f003', 'code51User@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2023-02-24 09:47:41.685000');
INSERT INTO `system_log` VALUES ('9da40eaa-9814-4c5a-80d6-9c2fa7c823d4', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-18 08:24:49.934000');
INSERT INTO `system_log` VALUES ('9dec9aaf-6451-4e70-a793-210e16914d12', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-08 23:59:05.909590');
INSERT INTO `system_log` VALUES ('9e10c397-9f12-4cf5-9d3f-4f8d8f206b8f', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:08:23.785000');
INSERT INTO `system_log` VALUES ('9e465116-a20b-4030-a37e-6b07a4d001f0', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:53:19.960000');
INSERT INTO `system_log` VALUES ('9e7c08b8-ff11-4abb-9994-0cd9cfb96c2b', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:34:16.759589');
INSERT INTO `system_log` VALUES ('9eb90c44-c198-42b0-9f5a-884ed78ed525', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-26 23:16:23.093000');
INSERT INTO `system_log` VALUES ('9eeb400b-5c5c-46fc-bbf3-f7e972102b27', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 15:31:40.953000');
INSERT INTO `system_log` VALUES ('9ef3a3c8-6577-4525-bbee-135dd5fbe7b9', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:25:07.094000');
INSERT INTO `system_log` VALUES ('9f0ee714-b4c8-43e1-a24c-8b5cb89486a4', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:10:56.827000');
INSERT INTO `system_log` VALUES ('9f14f3d1-3976-4e89-a667-89fc431dfe84', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 21:57:36.251000');
INSERT INTO `system_log` VALUES ('9fbe8daf-b7b8-4511-8a4f-26027a46df50', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-23 17:05:51.767000');
INSERT INTO `system_log` VALUES ('9fc7c9ee-ed93-465e-9c81-24d364b26d9d', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-24 09:51:34.386000');
INSERT INTO `system_log` VALUES ('9fdf8c09-10b8-4448-86e4-8ec3b534dd33', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:51:33.265000');
INSERT INTO `system_log` VALUES ('9fe98ad9-afb1-4c6b-94d6-a2447775dead', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:27:18.076000');
INSERT INTO `system_log` VALUES ('9ff86a83-3c1e-4842-9348-a739c52fa645', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 22:14:20.484000');
INSERT INTO `system_log` VALUES ('a0006d5c-c5ab-4777-86ec-0caa8c4db50e', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 15:42:17.766000');
INSERT INTO `system_log` VALUES ('a080b8cf-3955-4bb8-aa26-e6e14205a407', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:15:49.249000');
INSERT INTO `system_log` VALUES ('a0f561f8-987a-409b-8427-bd5a427d59a1', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-23 11:16:37.461000');
INSERT INTO `system_log` VALUES ('a1a4fc44-1067-4a49-b3a9-7f81a92ce243', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:22:31.818000');
INSERT INTO `system_log` VALUES ('a1d41741-20bb-40de-8643-744c1a68b417', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-18 11:50:30.138000');
INSERT INTO `system_log` VALUES ('a1f934fc-b5fd-440b-b832-57e789fa9544', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2022-12-19 13:10:56.237000');
INSERT INTO `system_log` VALUES ('a2275850-af80-4439-881c-bcec06338c39', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:06:05.568000');
INSERT INTO `system_log` VALUES ('a2454822-218c-467c-9adf-9bd71bf5590c', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-24 11:15:05.812000');
INSERT INTO `system_log` VALUES ('a2608a85-0092-40a1-a4b8-e3bc33d80613', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-26 23:31:13.490000');
INSERT INTO `system_log` VALUES ('a27547b8-a2e7-47bf-8337-5e1658c92295', '2361471705@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 17:32:06.702000');
INSERT INTO `system_log` VALUES ('a2812f68-d886-44b9-a1e2-40149ed4cbe3', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:15:46.717443');
INSERT INTO `system_log` VALUES ('a2989193-e3fd-49a7-9bac-75664928fd30', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-05-27 18:12:52.306789');
INSERT INTO `system_log` VALUES ('a2a80346-678e-4377-a641-4efe2e829456', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 09:14:37.816000');
INSERT INTO `system_log` VALUES ('a2d05b5b-38dd-441d-ac2b-20750d0636b0', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-23 11:16:26.629000');
INSERT INTO `system_log` VALUES ('a2d39fa7-a475-4303-ba53-8cd06f8c6128', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 10:16:42.288000');
INSERT INTO `system_log` VALUES ('a3112608-e507-4b66-b01f-a9f54ee08637', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-28 14:36:12.898000');
INSERT INTO `system_log` VALUES ('a3f54ce2-a2e2-4316-8a19-40406927c564', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:31:55.338723');
INSERT INTO `system_log` VALUES ('a466671b-2efd-4f6a-bdbc-93314193a007', 'code51User@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2023-02-24 09:41:47.118000');
INSERT INTO `system_log` VALUES ('a477b745-5f7a-45e3-b1e5-bf6314ffb4b5', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2024-06-27 14:39:07.934000');
INSERT INTO `system_log` VALUES ('a48474e2-c993-4cdc-bdfe-5a91a07e7703', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:37:53.084000');
INSERT INTO `system_log` VALUES ('a4b30a61-3b96-404f-9995-0fa10fddd7c3', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2022-12-23 19:04:07.491000');
INSERT INTO `system_log` VALUES ('a4d25292-4066-4de2-a304-8ce408680a20', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 13:10:25.646000');
INSERT INTO `system_log` VALUES ('a4d6dcf6-e8ed-4e29-be3f-d29aa6c9a02c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:24:31.526000');
INSERT INTO `system_log` VALUES ('a4db0479-39e2-4fc4-aa92-2abd1a4af4c1', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:36:31.021767');
INSERT INTO `system_log` VALUES ('a531633f-9e59-4c22-b6b6-fcaadc222b8b', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-27 21:48:20.127000');
INSERT INTO `system_log` VALUES ('a53c4f10-795e-49ba-aae4-5a604ee6116e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findByLikeName', '商品管理', '2025-10-24 11:01:34.795000');
INSERT INTO `system_log` VALUES ('a53ff22b-0573-4e97-bf10-32b929131ca3', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2024-06-27 14:26:55.139000');
INSERT INTO `system_log` VALUES ('a5791827-2672-4b7b-9ebe-3e33a23ab19d', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:03:44.504000');
INSERT INTO `system_log` VALUES ('a57e53d0-2368-4971-ad4f-04df34ba8d41', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:36:06.281847');
INSERT INTO `system_log` VALUES ('a5dd8367-2d48-4a68-8ef8-4bbfac7e8830', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-27 18:20:43.545647');
INSERT INTO `system_log` VALUES ('a5e4b3c3-35d1-4eae-8b1b-10ff295333a4', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-26 23:54:57.797000');
INSERT INTO `system_log` VALUES ('a64c0781-89eb-4b29-8980-0c1b57fe814a', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:31:18.312878');
INSERT INTO `system_log` VALUES ('a6a15587-91bc-451c-92ae-4a15c36b7953', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-18 11:36:37.569000');
INSERT INTO `system_log` VALUES ('a6c4bba7-8baa-42f9-b4da-6f985eac49ca', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-27 18:19:31.645431');
INSERT INTO `system_log` VALUES ('a6d436bb-fa87-4f9d-b466-26ae26e38140', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-25 13:12:11.977033');
INSERT INTO `system_log` VALUES ('a6e12a01-ea20-4ee4-a84f-d2e8ee90ed1d', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:32:53.325840');
INSERT INTO `system_log` VALUES ('a709d726-378e-4fce-b470-ff8ec8aae024', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-23 23:27:05.660000');
INSERT INTO `system_log` VALUES ('a73ca03d-e851-4dcc-b470-c7a1c16525b9', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:35:45.741780');
INSERT INTO `system_log` VALUES ('a77a3113-7b73-4aad-b7b3-b15683a48494', '16256226@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-10-18 08:10:04.442000');
INSERT INTO `system_log` VALUES ('a798398a-1df9-4e20-b4b4-a3a810048e7f', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 10:00:29.254000');
INSERT INTO `system_log` VALUES ('a7d6af3b-8b23-4d46-9f75-ae00c897f98e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-25 21:50:11.366000');
INSERT INTO `system_log` VALUES ('a7e92203-0b75-499c-bedd-b771f484141a', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 16:11:25.173000');
INSERT INTO `system_log` VALUES ('a82e7dad-a30b-428a-8880-2993b1981a1e', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-04-04 14:35:42.508000');
INSERT INTO `system_log` VALUES ('a841028c-03b0-48e6-aef4-38b004ce4eff', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-26 23:21:02.477000');
INSERT INTO `system_log` VALUES ('a84a686b-ee27-420c-8f99-d88de9087172', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-26 23:25:57.036000');
INSERT INTO `system_log` VALUES ('a84d1826-2e71-4bc4-a2f3-ad8cd1cdf67c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 10:02:02.854000');
INSERT INTO `system_log` VALUES ('a858e495-ed19-4219-85e0-391a32ce75ab', '470372007@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-05-28 10:13:04.665921');
INSERT INTO `system_log` VALUES ('a8732060-012b-484e-83e3-17298c47b3a1', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 21:17:21.559222');
INSERT INTO `system_log` VALUES ('a90236ae-2496-4fb1-b405-21a2997b114d', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-05-27 18:20:48.775320');
INSERT INTO `system_log` VALUES ('a90f7785-ab0f-40bf-951b-6fa9f539bc11', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 18:13:01.333950');
INSERT INTO `system_log` VALUES ('a9330bb2-0172-4367-93a1-dcfdba0c3a3f', 'testUser@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2023-02-24 09:36:11.621000');
INSERT INTO `system_log` VALUES ('a9f3f2e2-19a8-4642-9d95-b6044830f580', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-24 10:02:04.780000');
INSERT INTO `system_log` VALUES ('aa10a416-6fe0-4cc1-b384-acfbe85099e5', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:27:05.967000');
INSERT INTO `system_log` VALUES ('aa5b925c-09f7-4e6d-86a4-d31455b78072', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:48:51.405000');
INSERT INTO `system_log` VALUES ('aaec85a9-f81d-408f-99e3-3035526b8d45', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2024-06-27 14:23:53.518000');
INSERT INTO `system_log` VALUES ('ab636f15-3615-47f9-980e-22e117557a58', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 15:37:29.498000');
INSERT INTO `system_log` VALUES ('ab6c54b1-4091-4fe9-a322-317092a3c647', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-24 09:56:53.710000');
INSERT INTO `system_log` VALUES ('ab7ae3b9-acac-4e1b-bbff-310497772373', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-24 09:47:59.355000');
INSERT INTO `system_log` VALUES ('ab883039-1fcf-4a0b-ab0f-0a184080d811', 'code51User@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.WarehouseController.save', '仓库管理', '2023-02-24 09:43:05.528000');
INSERT INTO `system_log` VALUES ('abc58966-4d84-4ec8-8956-a54122c6cf0e', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-05-08 23:58:11.437045');
INSERT INTO `system_log` VALUES ('abc9a569-757f-458b-aaab-027e2abe3279', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-23 17:06:56.445000');
INSERT INTO `system_log` VALUES ('abd4825e-96ff-4b60-8352-9581e5ec8e06', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-04-28 21:16:11.157476');
INSERT INTO `system_log` VALUES ('abd7530b-f8c4-47d7-b331-b4d06557e49c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-04 14:36:05.929000');
INSERT INTO `system_log` VALUES ('ac0beca2-db4b-4d4b-acdc-9553c51274a5', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 16:24:58.116000');
INSERT INTO `system_log` VALUES ('ac3d6acd-143f-4bbc-bd99-75a52554406c', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:36:12.499000');
INSERT INTO `system_log` VALUES ('ac3f9442-66dd-4704-9da1-ab597513a779', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:08:18.562000');
INSERT INTO `system_log` VALUES ('ac619e04-18d7-45fa-aa09-dfa0a44ab20f', 'jiegod_8ck@126.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-04-28 20:37:57.254918');
INSERT INTO `system_log` VALUES ('ac695b82-ccfc-48c0-bb36-70f067cecd36', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-24 10:02:13.837000');
INSERT INTO `system_log` VALUES ('ac7b9a51-2633-4e2a-848b-39a7ffa639c4', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-20 19:50:50.836000');
INSERT INTO `system_log` VALUES ('acae46ef-21f9-4e1b-a20d-985f43836ddf', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-15 21:05:15.738000');
INSERT INTO `system_log` VALUES ('acdf0567-7137-4c20-94a4-7ec22ff93680', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 21:14:55.939147');
INSERT INTO `system_log` VALUES ('acf10e5b-e918-4459-a191-6283b1136def', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2022-12-19 13:02:18.813000');
INSERT INTO `system_log` VALUES ('ad7e5200-bbc1-4136-8cfa-080b674af52d', 'code51User@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.EmployeeController.save', '员工管理', '2023-02-24 09:42:47.686000');
INSERT INTO `system_log` VALUES ('ae063dcb-2f23-4647-8548-2ef34bec7565', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-23 11:16:44.530000');
INSERT INTO `system_log` VALUES ('ae4839e7-c0be-492b-841a-66ba33dd0059', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-18 08:21:58.168000');
INSERT INTO `system_log` VALUES ('ae48bcfe-f732-486a-b886-a304bb30a430', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:12:28.718000');
INSERT INTO `system_log` VALUES ('ae5354a5-bf35-4c15-b6b4-896dfb515c61', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2022-12-18 11:38:16.825000');
INSERT INTO `system_log` VALUES ('ae70e930-8772-4d24-8388-4a6298d58832', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:24:56.636000');
INSERT INTO `system_log` VALUES ('aea1a092-2193-4c38-8861-c03f8e4e8216', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-20 19:56:39.602000');
INSERT INTO `system_log` VALUES ('aebe72ec-aedc-42ae-965e-31ceba81c001', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-20 19:56:45.686000');
INSERT INTO `system_log` VALUES ('aed63acc-9a17-475b-aa81-4df37d73d754', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:07:49.874000');
INSERT INTO `system_log` VALUES ('aee0d10a-9804-4103-a9df-7f0de33f4be8', 'jiegod_8ck@126.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-04-28 20:36:21.004888');
INSERT INTO `system_log` VALUES ('af48572b-9740-4e0b-a1ca-c705fc7da79f', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:28:57.351000');
INSERT INTO `system_log` VALUES ('af71441c-f6fe-4274-98ff-024b9f059035', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2024-06-27 14:33:20.005000');
INSERT INTO `system_log` VALUES ('af7eafa9-8912-4b92-8fdb-9079926cf1c7', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-05-28 10:16:11.497472');
INSERT INTO `system_log` VALUES ('af8a6f18-8b9d-4f58-a013-719ec318d1aa', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-20 10:38:00.232000');
INSERT INTO `system_log` VALUES ('afc4234f-de07-4508-939a-03773d753dea', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-05-28 10:13:21.859565');
INSERT INTO `system_log` VALUES ('b019d578-4d97-4f7b-803b-6c5db50e5e90', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:09:51.805000');
INSERT INTO `system_log` VALUES ('b0351041-4dd0-4903-8f6e-87fcf64ef185', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-25 21:52:37.670000');
INSERT INTO `system_log` VALUES ('b05ed21c-0e63-4985-a4f6-fd367298cc03', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 18:20:38.161161');
INSERT INTO `system_log` VALUES ('b0b7e61e-de37-45bc-812b-9a77defc5b19', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 08:58:38.918000');
INSERT INTO `system_log` VALUES ('b0ca2f15-677d-4280-b16c-9ea21e21c122', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2024-06-27 14:32:07.284000');
INSERT INTO `system_log` VALUES ('b172e1d1-6cb5-4344-aaef-685148060a38', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-24 11:14:59.656000');
INSERT INTO `system_log` VALUES ('b1959e7e-f77c-4a44-b6a4-70eee569b40c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2022-12-19 13:10:49.669000');
INSERT INTO `system_log` VALUES ('b21cb4ac-1485-44f1-8f1f-513477be7e6e', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 10:16:13.126000');
INSERT INTO `system_log` VALUES ('b239ced4-54a3-444d-b4fc-1209cc2aa7cc', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-05-08 23:58:21.789500');
INSERT INTO `system_log` VALUES ('b286f227-436b-41fc-9f6f-c6ffb560f244', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 23:21:41.616000');
INSERT INTO `system_log` VALUES ('b2b9f0e5-2107-4c74-a759-0724c4c0395d', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 21:48:16.221000');
INSERT INTO `system_log` VALUES ('b34d792e-0ad6-47ea-886c-b248311c41eb', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-18 09:33:27.567000');
INSERT INTO `system_log` VALUES ('b363cab8-58ef-4ac7-b8b9-7a92973579ab', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-24 11:04:03.522000');
INSERT INTO `system_log` VALUES ('b3fb909b-9de4-4b1f-ae09-37f24fccbd01', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 17:05:31.123000');
INSERT INTO `system_log` VALUES ('b4095f63-6673-4242-91f6-8236717d3d13', 'code51User@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.VehicleController.save', '车辆管理', '2023-02-24 09:45:23.326000');
INSERT INTO `system_log` VALUES ('b41fb768-36e7-4d77-b28e-79b9deff2525', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-04-28 21:34:19.342300');
INSERT INTO `system_log` VALUES ('b46e810f-8800-4ffd-bb8d-9990d86d7313', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-01-11 13:36:49.334000');
INSERT INTO `system_log` VALUES ('b49e7dd8-0a55-4562-94ee-c61b4879bab0', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 13:39:01.079000');
INSERT INTO `system_log` VALUES ('b5328f79-6c73-4203-97bd-e778fdde0ded', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2023-02-24 09:56:43.517000');
INSERT INTO `system_log` VALUES ('b540ca48-adcb-45e5-aa9e-21dc94efb2c1', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 16:38:43.101000');
INSERT INTO `system_log` VALUES ('b54c88b0-8e89-4853-94f4-a0815ad6f63d', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 10:12:36.876000');
INSERT INTO `system_log` VALUES ('b57b6a7c-4fdb-44c8-ad4e-e328f79f2478', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-24 09:51:04.794000');
INSERT INTO `system_log` VALUES ('b5899d57-264f-4067-911c-c4e79ef37371', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 10:15:47.080000');
INSERT INTO `system_log` VALUES ('b5a946a4-03dc-439f-a16d-859f915b6987', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findByLikeName', '商品管理', '2023-01-11 09:18:13.365000');
INSERT INTO `system_log` VALUES ('b5b74bfe-c247-4ca0-9367-fe6c98dbef9e', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:40:13.019000');
INSERT INTO `system_log` VALUES ('b5bd82c2-75fd-4fb4-9317-a83277128065', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 21:45:55.213000');
INSERT INTO `system_log` VALUES ('b5f03c87-36d0-4004-9aef-478fbba5a260', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 12:15:59.043000');
INSERT INTO `system_log` VALUES ('b63e53aa-6097-4b7a-a1cc-d4c82e47194c', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-27 20:01:31.246000');
INSERT INTO `system_log` VALUES ('b662c241-e5db-464d-a948-e4245c6ce083', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2022-12-23 19:07:50.563000');
INSERT INTO `system_log` VALUES ('b67fcfdc-630b-4a5f-ac9c-49010ea65ddb', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:56:13.510000');
INSERT INTO `system_log` VALUES ('b693249a-a9ce-4cdc-9653-ba360eaabd78', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-01-11 09:23:13.852000');
INSERT INTO `system_log` VALUES ('b6c3b8c3-6d1a-437c-bb5a-c7bf4f89303a', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:31:47.659600');
INSERT INTO `system_log` VALUES ('b7869919-a1cb-43b3-a7fc-b2d11d24e4e8', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:24:33.010000');
INSERT INTO `system_log` VALUES ('b7a9aaa0-2e3b-45d0-a2ef-48398525b07e', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 10:16:21.420000');
INSERT INTO `system_log` VALUES ('b7d65309-7f76-4508-8dae-a79bed936e6f', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-23 23:09:48.470000');
INSERT INTO `system_log` VALUES ('b8584d43-49bc-407b-95ab-29019c8ae0be', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 17:09:55.173000');
INSERT INTO `system_log` VALUES ('b876b4d7-d393-49ba-86e1-f415e3d46919', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-17 15:42:24.751000');
INSERT INTO `system_log` VALUES ('b8b2d129-073b-44ce-95af-6c3b080161e8', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-24 09:46:35.710000');
INSERT INTO `system_log` VALUES ('b8d21655-ff61-4390-b828-cc256c289dbe', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:43:05.545000');
INSERT INTO `system_log` VALUES ('b90a9ba6-8555-4cc9-aa29-aca39a387181', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 09:36:23.319000');
INSERT INTO `system_log` VALUES ('b995dca3-af0a-491e-91f1-00dca443bdbd', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 11:19:01.204000');
INSERT INTO `system_log` VALUES ('b9a08e4c-6adf-456b-94e9-66f5b9dd4cf1', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:22:52.725000');
INSERT INTO `system_log` VALUES ('b9b402bd-9e96-4c14-b07f-ea39782fb49d', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:42:56.116000');
INSERT INTO `system_log` VALUES ('b9ef08dd-d27c-4a7e-b848-d754aa1e8f40', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-09 12:59:46.419498');
INSERT INTO `system_log` VALUES ('ba0a41f2-2c47-42f0-9f20-9a70a93d65a2', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-24 09:47:11.679000');
INSERT INTO `system_log` VALUES ('ba79559f-37f7-4637-aef4-922f1016b72d', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:56:24.352000');
INSERT INTO `system_log` VALUES ('ba8fa00e-b723-4b5c-bc01-45261acfcace', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2022-12-18 12:16:01.263000');
INSERT INTO `system_log` VALUES ('ba984358-46b0-45e5-9bdb-9b8f410347fd', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:06:10.703000');
INSERT INTO `system_log` VALUES ('bb2a078d-2d26-48ff-9de4-e315195fef31', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:04:45.152000');
INSERT INTO `system_log` VALUES ('bb2fa1a6-99de-4dff-b8d5-fcf2c7b1c680', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:22:41.846000');
INSERT INTO `system_log` VALUES ('bb3cbf8c-38c1-44a9-b06a-5c6a85bc0a7e', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-06-10 18:38:46.447117');
INSERT INTO `system_log` VALUES ('bbd8622c-26a2-455f-8ed1-5eaefff6456d', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:30:41.921000');
INSERT INTO `system_log` VALUES ('bbe158bc-6bc0-4391-b6c2-74e36a08bab6', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-01-11 09:23:32.810000');
INSERT INTO `system_log` VALUES ('bc456b54-80f1-4dc2-9df2-aa68435ab54a', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:16:15.417000');
INSERT INTO `system_log` VALUES ('bc8b0261-4186-42ad-8260-42ba3cacb5b4', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 21:51:45.715000');
INSERT INTO `system_log` VALUES ('bcd43790-9c7c-47d5-8268-4ae1470241dc', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 10:00:21.734000');
INSERT INTO `system_log` VALUES ('bcfde7dc-b215-4214-aa94-799b88bc4262', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:34:19.972962');
INSERT INTO `system_log` VALUES ('bd212f62-d867-4169-9603-b5acdb603cc7', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-25 21:53:54.364000');
INSERT INTO `system_log` VALUES ('bd29aeea-6e71-43de-a7f6-5d943d5958b2', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-02 15:24:11.651000');
INSERT INTO `system_log` VALUES ('bd38fb3c-3428-4276-86e4-c51a850c0561', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2024-06-27 14:33:30.051000');
INSERT INTO `system_log` VALUES ('bda23e6a-af04-4bea-945c-6a1ef497c319', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:45:10.072000');
INSERT INTO `system_log` VALUES ('bdb78df7-ccfb-4eb2-9996-e09b5aaf01f2', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:51:05.781000');
INSERT INTO `system_log` VALUES ('bdb7e919-95e8-498b-a85f-a3397f13e173', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DriverController.save', '驾驶员管理', '2025-10-23 23:29:20.421000');
INSERT INTO `system_log` VALUES ('be077f5b-ba15-45f4-81af-0ca1e091dd4a', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:37:37.916452');
INSERT INTO `system_log` VALUES ('be0d1dfe-d5ba-43f2-b370-b814fbd44145', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-24 09:09:32.867000');
INSERT INTO `system_log` VALUES ('be7f302c-0afa-42e2-b494-fdd121f29aa5', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findByLikeName', '商品管理', '2023-01-11 13:36:02.702000');
INSERT INTO `system_log` VALUES ('be8c0ad1-dedf-414e-ace1-3694ec06367f', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-26 23:55:53.475000');
INSERT INTO `system_log` VALUES ('beb09e3e-24f5-4cd6-b233-4ce5a8d16b85', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-26 23:57:31.141000');
INSERT INTO `system_log` VALUES ('beb6a374-6dfe-4db6-b0e8-635557a876b6', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:40:44.676000');
INSERT INTO `system_log` VALUES ('bec40a33-d70c-45d2-8e0a-171455300ef7', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-23 19:07:47.344000');
INSERT INTO `system_log` VALUES ('bed2dee2-58a8-4a5f-b40d-2f5892352123', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-05-08 23:58:34.658589');
INSERT INTO `system_log` VALUES ('bef24608-ddf3-49c9-b65c-bda1728d7dd1', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 09:44:53.055000');
INSERT INTO `system_log` VALUES ('bf301177-89b9-474f-9fa2-a2111c11bc47', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:06:51.296000');
INSERT INTO `system_log` VALUES ('bfae97c7-910f-404f-823d-007e43605791', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-01-11 13:15:23.431000');
INSERT INTO `system_log` VALUES ('c0011144-85b6-49a6-a707-fb28b89011e5', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-01-11 09:18:40.462000');
INSERT INTO `system_log` VALUES ('c01126eb-6483-40a3-b0b3-4d8afcc3b523', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:20:42.434000');
INSERT INTO `system_log` VALUES ('c01d48cf-0fe2-4e3e-bc69-7d523ec45104', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-04-28 21:17:32.670308');
INSERT INTO `system_log` VALUES ('c035450c-453d-4368-be19-9cc3ad3a30df', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-04-04 14:34:57.480000');
INSERT INTO `system_log` VALUES ('c070266d-164e-40f0-8a1b-9d80547bc939', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 09:33:50.693000');
INSERT INTO `system_log` VALUES ('c0d01c33-a180-4f71-a6d6-931b4d698137', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 23:06:58.801000');
INSERT INTO `system_log` VALUES ('c0e32b77-cbd3-441b-af76-214ba13c26a1', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 10:16:43.005000');
INSERT INTO `system_log` VALUES ('c112bf85-8571-4a3a-a03d-78d4b699ed0d', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 13:10:45.132000');
INSERT INTO `system_log` VALUES ('c14efbae-5305-4ae4-9c0a-63ee73d0972a', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:36:23.319000');
INSERT INTO `system_log` VALUES ('c1a88a1b-3ae4-4bf4-8a4f-085209a669cb', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-05-27 18:19:30.671660');
INSERT INTO `system_log` VALUES ('c24abce3-e712-4895-9e23-6556a18c1e85', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-24 11:10:06.310000');
INSERT INTO `system_log` VALUES ('c25a1b7c-ed20-40a2-8d89-a14b0de13263', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-23 11:16:43.537000');
INSERT INTO `system_log` VALUES ('c2aa0f22-9a94-453a-ad80-bf9ab45efe82', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-28 21:15:41.031026');
INSERT INTO `system_log` VALUES ('c2eaf7b2-f7c2-425f-83ae-7b17b8f95740', '匿名用户', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 16:54:41.105000');
INSERT INTO `system_log` VALUES ('c31697ad-9c76-4a13-b2a4-bf74ea21a215', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 20:11:00.258000');
INSERT INTO `system_log` VALUES ('c32d35ad-006a-4629-b961-baecf2011488', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-26 23:17:03.041000');
INSERT INTO `system_log` VALUES ('c3f6955f-0704-4c5e-89ed-dd96694771bb', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-24 11:25:18.790000');
INSERT INTO `system_log` VALUES ('c4022eb9-fadd-43fb-bb4c-2e6282d1e0b6', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-19 23:30:49.727000');
INSERT INTO `system_log` VALUES ('c439893d-461b-4444-a37f-32fb81c753a0', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-18 12:15:50.547000');
INSERT INTO `system_log` VALUES ('c469bc48-ebc1-4094-9646-9f001bcf8afd', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:36:21.525000');
INSERT INTO `system_log` VALUES ('c46e81a7-3930-4d42-aff5-5d1b255ed513', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-23 18:23:52.031000');
INSERT INTO `system_log` VALUES ('c4816f32-ec52-422e-b9d3-0f232da2bbe7', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-25 21:53:04.950000');
INSERT INTO `system_log` VALUES ('c4819c1c-d2e2-4334-8023-49d3ca11ad55', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2024-06-27 14:28:16.367000');
INSERT INTO `system_log` VALUES ('c4cf91d1-2ef5-403f-a71a-a72cf22b32c1', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-24 11:10:24.216000');
INSERT INTO `system_log` VALUES ('c4f32d37-16f0-4f0d-9c3e-a409d537ad63', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-18 08:20:24.281000');
INSERT INTO `system_log` VALUES ('c5599498-f76a-4ce6-bb31-646732f009a3', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 23:02:11.925000');
INSERT INTO `system_log` VALUES ('c574335a-c073-4236-b3a2-8452027f1c7b', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:28:34.255000');
INSERT INTO `system_log` VALUES ('c5750b02-b5c5-415a-a5e6-0c0f89e5c05c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:29:06.877000');
INSERT INTO `system_log` VALUES ('c5b491a1-c36b-4eb2-874b-2a1380fb71f7', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:14:37.077717');
INSERT INTO `system_log` VALUES ('c63d732b-7458-4218-8fa0-e3244b5b66ee', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-27 14:26:12.694000');
INSERT INTO `system_log` VALUES ('c680ec37-143f-4f80-bc39-f82ef08a553d', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2024-06-27 14:37:52.238000');
INSERT INTO `system_log` VALUES ('c7054bae-8022-4cb5-a750-d0fc6caf1fcc', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 21:50:36.472000');
INSERT INTO `system_log` VALUES ('c70deb2f-d119-453b-8202-8d52b0b44283', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:05:53.379000');
INSERT INTO `system_log` VALUES ('c7409ba4-5dc9-4fbb-a37d-ed0aca579ab8', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:15:21.346529');
INSERT INTO `system_log` VALUES ('c79a1c79-971f-4503-beee-eb5389465b81', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:43:11.483000');
INSERT INTO `system_log` VALUES ('c7f517f8-f175-4dfc-8590-d8c51d898e47', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:37:32.595268');
INSERT INTO `system_log` VALUES ('c8001387-bd07-49a3-a3cb-e5a78cb0020c', '匿名用户', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-30 16:54:39.796000');
INSERT INTO `system_log` VALUES ('c861e265-b9bd-4191-b817-51259539bcac', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-18 08:19:40.666000');
INSERT INTO `system_log` VALUES ('c909ed64-b99b-4a4e-a78a-1c2ed512cf8e', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:31:31.061000');
INSERT INTO `system_log` VALUES ('c91b8914-83d8-4652-ad29-5659c83bdc22', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 21:50:52.673000');
INSERT INTO `system_log` VALUES ('c941ea0f-f1fc-4c17-a43b-6cf87f505273', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:40:44.218000');
INSERT INTO `system_log` VALUES ('c97003da-aa84-4111-86d6-3cf08e61991c', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-23 17:07:03.037000');
INSERT INTO `system_log` VALUES ('c9734646-2f14-4b03-966a-110c5db723cd', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 18:19:32.972789');
INSERT INTO `system_log` VALUES ('c9ab600d-35e2-4190-a65c-2aaa83ce1546', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:21:25.049000');
INSERT INTO `system_log` VALUES ('c9edd424-99e6-4e24-8fe0-d8fa8f91fc3d', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:41:02.022000');
INSERT INTO `system_log` VALUES ('c9f1d5e6-c81f-481a-9357-6aed4c2b4b03', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-19 11:43:16.518000');
INSERT INTO `system_log` VALUES ('c9f8c05e-4138-4291-b751-eb8fd7ef087b', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 00:03:29.720000');
INSERT INTO `system_log` VALUES ('ca2e4cbe-ed37-4880-86a1-08b6bd590ecb', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-15 20:57:48.475000');
INSERT INTO `system_log` VALUES ('ca356a8c-adf6-41d0-bb3d-4441aae6898a', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-20 19:59:04.049000');
INSERT INTO `system_log` VALUES ('ca398d1b-6ab4-497e-9560-e6a546be3265', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:38:07.848711');
INSERT INTO `system_log` VALUES ('ca64e69f-2a72-4452-9a5d-22b9dbd73682', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-04 14:35:37.086000');
INSERT INTO `system_log` VALUES ('ca8454d0-8520-4f76-9fc6-ebe25ca57956', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:10:56.744000');
INSERT INTO `system_log` VALUES ('caa6892d-f2ce-44b3-97d3-3e4e0afdecf2', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 16:15:29.919000');
INSERT INTO `system_log` VALUES ('cb06ce17-afd8-4a9a-ae49-95b596443c3a', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2024-06-26 23:41:26.162000');
INSERT INTO `system_log` VALUES ('cb09c36e-8ad0-4ed2-b10d-9de9789c6378', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-23 11:16:25.552000');
INSERT INTO `system_log` VALUES ('cb78b622-bb2a-42c9-be0c-610388eb4309', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-05-27 18:15:29.438308');
INSERT INTO `system_log` VALUES ('cbeb5085-0a32-4a9e-85b0-a0b6537b52c2', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:36:39.565819');
INSERT INTO `system_log` VALUES ('cbefe5f3-8d9d-4d2e-aa95-8a5d7becec4a', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-04-28 21:15:57.207908');
INSERT INTO `system_log` VALUES ('cbfa66a4-2f08-4a5c-a7dd-c334a6b95d5c', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:32:40.396000');
INSERT INTO `system_log` VALUES ('cc3b5143-d5de-4bd4-9299-7c0b3ae610a1', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-20 19:58:49.246000');
INSERT INTO `system_log` VALUES ('cc7acd01-815a-4291-93c5-bd985ab22ce9', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:31:28.364441');
INSERT INTO `system_log` VALUES ('ccae1e6f-71c2-4679-81a1-99a861983a19', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-18 12:15:56.063000');
INSERT INTO `system_log` VALUES ('cceeaf4f-4d8c-4aa2-81fc-6c66aebd3f1e', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:51:03.773000');
INSERT INTO `system_log` VALUES ('cd6dde05-cf49-4c0c-9772-7adf82b737ff', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-24 11:14:57.529000');
INSERT INTO `system_log` VALUES ('cd8d5f8f-36d5-4951-8f9e-48939b9cbf1f', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 09:26:39.588000');
INSERT INTO `system_log` VALUES ('cdd1456c-34bd-4f81-b20e-cffa79bde5e4', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-25 13:12:16.509967');
INSERT INTO `system_log` VALUES ('cdec2cb4-f9a3-46c7-9e95-9e723b463511', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2022-12-18 20:23:56.306000');
INSERT INTO `system_log` VALUES ('ce2dbd6b-dc0e-4348-ac8b-2b1041d5b7ba', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-13 21:24:42.051861');
INSERT INTO `system_log` VALUES ('ce54f501-cbc4-4c9c-9e7c-e143fe208fc7', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-04-28 20:17:19.585705');
INSERT INTO `system_log` VALUES ('ce58dfc8-3037-43c9-acf1-f53a6b0e88f2', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-18 11:50:09.526000');
INSERT INTO `system_log` VALUES ('ceb14480-bbaf-4eb6-ba0e-1a214210bfea', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-04 14:35:51.382000');
INSERT INTO `system_log` VALUES ('ceeb7034-f1ef-42d2-b4c0-464298df0902', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:48:59.580000');
INSERT INTO `system_log` VALUES ('cf2e5d0c-5e42-4bd8-8b7c-f82df785bcc9', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-18 08:25:06.883000');
INSERT INTO `system_log` VALUES ('cf37152d-bc86-4741-bd34-a320ee746a22', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:48:49.509000');
INSERT INTO `system_log` VALUES ('cf807c1d-df42-4e94-852f-14479470d980', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-23 11:17:23.539000');
INSERT INTO `system_log` VALUES ('cfd9f018-ebaf-412e-b4fb-3dc9e17fff43', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2024-06-26 23:57:31.130000');
INSERT INTO `system_log` VALUES ('d00727bf-77c3-477a-a483-1e38b8d68ed4', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-03 10:08:57.067000');
INSERT INTO `system_log` VALUES ('d039b1a1-75b7-4e48-85bc-496666c10657', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:24:43.451000');
INSERT INTO `system_log` VALUES ('d06758ed-bde9-488b-bf73-88cf825f65ff', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:12:52.701000');
INSERT INTO `system_log` VALUES ('d074466a-6d17-4975-a362-72f8637dd92e', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:37:32.690000');
INSERT INTO `system_log` VALUES ('d0804a8e-c36a-4495-a40f-5ac3ab772608', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:15:04.489017');
INSERT INTO `system_log` VALUES ('d098fba7-dcc6-40bd-a8d2-d04cc74892fc', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:18:43.342000');
INSERT INTO `system_log` VALUES ('d0bccfce-b66c-4181-9093-7fb82fd93103', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 19:55:54.446000');
INSERT INTO `system_log` VALUES ('d0d52e31-4756-4a7e-a72c-eae8180f90c8', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2023-02-24 09:58:21.864000');
INSERT INTO `system_log` VALUES ('d0d8832f-c148-467a-97ce-774bce224fb4', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-23 19:01:29.620000');
INSERT INTO `system_log` VALUES ('d1485018-77e6-4d47-b707-fa3b70d05dab', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:15:33.095000');
INSERT INTO `system_log` VALUES ('d15401e9-2192-429f-9b06-9b77d58e6734', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-05-27 22:42:45.840051');
INSERT INTO `system_log` VALUES ('d2562c5e-405d-41c3-b459-9b5770585240', '16256226@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-04-28 20:19:23.305898');
INSERT INTO `system_log` VALUES ('d280225a-faab-4bb4-9057-d746484f2daf', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:22:58.540000');
INSERT INTO `system_log` VALUES ('d28f822e-1d16-42b1-906c-c9dbc894669c', 'code51User@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2023-02-24 09:47:46.956000');
INSERT INTO `system_log` VALUES ('d2c8a739-515c-4a58-8648-a9284e514924', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:36:07.218654');
INSERT INTO `system_log` VALUES ('d2ce2cf9-dbe4-4dd2-81a2-cd2303740b50', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2023-01-11 09:23:24.489000');
INSERT INTO `system_log` VALUES ('d34bea31-2a01-4fb1-88d1-1bc43c22ec92', '匿名用户', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 16:54:39.796000');
INSERT INTO `system_log` VALUES ('d3782638-774d-498e-8e2c-ee684da4f32d', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 18:53:52.396000');
INSERT INTO `system_log` VALUES ('d3d19255-baed-4057-98ad-4c0bc3e21dea', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:18:51.608634');
INSERT INTO `system_log` VALUES ('d40b1aac-b7fc-4e07-a7eb-decfccc4105e', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:43:57.106000');
INSERT INTO `system_log` VALUES ('d45ab9c5-e118-4235-bd77-e11eb51a0a1c', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-17 15:43:52.031000');
INSERT INTO `system_log` VALUES ('d4620121-ab6d-4ad0-b770-0e66462f77f5', '2159609711@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.DriverController.delete', '驾驶员管理', '2025-10-23 23:30:22.126000');
INSERT INTO `system_log` VALUES ('d5118dbd-3df9-4140-afd4-f262f1041790', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 17:03:06.371000');
INSERT INTO `system_log` VALUES ('d614c46b-ac37-41a5-bef9-6ca3b363b2d8', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-01-11 11:07:38.462000');
INSERT INTO `system_log` VALUES ('d676cf30-2180-4291-a896-00881e8f8d9b', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:27:25.369000');
INSERT INTO `system_log` VALUES ('d67cbde1-65d6-4691-8755-5a62302f7bfd', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:15:55.574000');
INSERT INTO `system_log` VALUES ('d6b8e69f-5d79-4bef-9e21-698c8d94504b', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-27 22:42:47.578980');
INSERT INTO `system_log` VALUES ('d6fee3b0-995a-4867-ba5d-ad5aab32e19b', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:29:32.826000');
INSERT INTO `system_log` VALUES ('d737aa8c-e27b-4cba-978d-2c9dd3906ce6', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 13:10:47.377000');
INSERT INTO `system_log` VALUES ('d76e6cdc-f698-455b-9437-38adeda5d818', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:08:12.822000');
INSERT INTO `system_log` VALUES ('d788bc8b-b552-429d-94cb-5d95791c02ad', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-18 11:37:41.214000');
INSERT INTO `system_log` VALUES ('d78a3cda-ab44-4307-b46a-1e7369e44aa7', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-03 10:09:01.464000');
INSERT INTO `system_log` VALUES ('d79b4fc2-c102-442c-9831-4524bc0239d6', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-24 09:40:48.023000');
INSERT INTO `system_log` VALUES ('d7c99480-fe37-4514-be69-1ed898a9377f', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2022-12-19 12:58:57.135000');
INSERT INTO `system_log` VALUES ('d813d620-ba05-44d8-bc39-8f16cb34297b', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-27 20:01:29.191000');
INSERT INTO `system_log` VALUES ('d8665c91-65be-4234-82aa-5af9c4f8ee48', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-23 19:03:05.878000');
INSERT INTO `system_log` VALUES ('d86d7c45-daea-4c3f-a959-24a94c0deea5', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-13 21:19:00.579857');
INSERT INTO `system_log` VALUES ('d8a464f5-37c0-4ee1-87af-c1f7abe82f81', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:09:14.806000');
INSERT INTO `system_log` VALUES ('d8ce5dd8-ff64-4bc6-a77b-7951a6a384e2', '匿名用户', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 16:07:25.742000');
INSERT INTO `system_log` VALUES ('d8e0818a-5330-4948-b58d-c594bffc6da7', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-23 23:29:20.875000');
INSERT INTO `system_log` VALUES ('d8ffd447-1fb2-4b25-9e7f-a16d1a9d85f5', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:18:36.364000');
INSERT INTO `system_log` VALUES ('d93e3566-a521-4e7f-a17d-09075145285a', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-20 10:37:45.272000');
INSERT INTO `system_log` VALUES ('d9750b97-8b2d-4264-b3be-5524576ff007', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 17:03:29.384000');
INSERT INTO `system_log` VALUES ('d9972fce-3b34-48c0-a70d-63e0dec5d480', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 23:30:45.076000');
INSERT INTO `system_log` VALUES ('d9c332db-9433-4092-8c0b-ef7b6031bb13', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-24 11:10:28.079000');
INSERT INTO `system_log` VALUES ('d9d147bf-b9a6-4fd0-9bbc-51d2de68ce17', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-24 11:04:11.981000');
INSERT INTO `system_log` VALUES ('d9faba2e-5c75-4c92-8c8a-764480d16f4c', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 21:50:20.235000');
INSERT INTO `system_log` VALUES ('da17f5b6-31da-43a9-8443-13f64c44e85d', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-17 15:37:26.405000');
INSERT INTO `system_log` VALUES ('da22e0a6-b508-4a7b-9462-be0a945f9395', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:09:28.610000');
INSERT INTO `system_log` VALUES ('da27dcc5-eeb7-45e4-984c-155b7b56c596', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-05-08 23:58:59.412290');
INSERT INTO `system_log` VALUES ('da7ca071-8410-4ddb-8d7c-fd5c4085a3e1', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 21:44:48.572737');
INSERT INTO `system_log` VALUES ('da88eb52-a3df-4ca8-a7e6-b5dba999739e', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 09:18:49.174000');
INSERT INTO `system_log` VALUES ('daf5238a-e48a-4a66-bcf1-c1694f68181f', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-04-28 20:17:16.740962');
INSERT INTO `system_log` VALUES ('db16c5b8-1600-47af-98c4-c413c60d971f', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:34:59.661220');
INSERT INTO `system_log` VALUES ('db56eaab-8cfe-4a52-b10c-19f74e406943', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-23 11:17:19.940000');
INSERT INTO `system_log` VALUES ('db93a080-c704-4120-bcac-4b63a22f4ac2', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:00:53.160000');
INSERT INTO `system_log` VALUES ('dbd12ca1-7351-4970-b447-c8c071e1a076', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-18 11:50:09.526000');
INSERT INTO `system_log` VALUES ('dbdb54e9-b554-4454-b4e7-1f9ad2ca042a', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-27 20:56:09.530000');
INSERT INTO `system_log` VALUES ('dc565208-c8c1-44b7-b647-8fa566d8767e', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 20:24:08.434000');
INSERT INTO `system_log` VALUES ('dc5d1ded-7a50-49a9-8d63-7febcd9989d5', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:22:24.644000');
INSERT INTO `system_log` VALUES ('dc94727d-7c09-406a-bc11-cc5ee2ee3b05', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2022-12-18 11:30:17.056000');
INSERT INTO `system_log` VALUES ('dcce1ead-d21c-4010-9059-af8b18b62ffc', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2022-12-23 19:02:10.646000');
INSERT INTO `system_log` VALUES ('dcd37ad4-ed5e-403b-8fff-8dfc30c794fa', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:26:42.109000');
INSERT INTO `system_log` VALUES ('dd38ff71-da49-49af-8ba9-0c38befda79a', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:12:59.572178');
INSERT INTO `system_log` VALUES ('ddb312be-1d75-4e08-bab1-4e700c29d331', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:12:37.527000');
INSERT INTO `system_log` VALUES ('ddda274e-58a9-4052-8e41-dc2abcde5af8', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:20:47.194000');
INSERT INTO `system_log` VALUES ('ddf23055-61fd-40c7-beff-f29fa9d44feb', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 11:43:17.815000');
INSERT INTO `system_log` VALUES ('de22491f-0b53-4526-b1ca-7c4432101176', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:13:25.351463');
INSERT INTO `system_log` VALUES ('de23f42c-fea6-4e12-bc78-b2aafe1ebfdf', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-25 13:12:05.094381');
INSERT INTO `system_log` VALUES ('de57c068-9529-4bca-b397-894ed9b9bc69', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-26 23:55:57.191000');
INSERT INTO `system_log` VALUES ('de606513-5718-45ae-b789-7cf1d9b858e6', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-27 14:26:40.067000');
INSERT INTO `system_log` VALUES ('de989105-1ab7-48cb-8fcb-0c4fc486228e', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 09:42:47.717000');
INSERT INTO `system_log` VALUES ('deae7c5d-b0ed-49b1-b1bc-78b1e7aebdd0', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:31:54.487916');
INSERT INTO `system_log` VALUES ('dec0e2fb-7945-4103-8889-f4c3701eea6f', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:14:28.827488');
INSERT INTO `system_log` VALUES ('defacdf4-b879-4e9d-ac8c-18fa9bd02529', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-23 19:02:24.992000');
INSERT INTO `system_log` VALUES ('df34e592-6af6-4709-979b-396d8bec4463', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-27 18:12:56.415734');
INSERT INTO `system_log` VALUES ('df39ea38-07fd-48d9-a95d-4259beba9ede', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-24 09:48:35.064000');
INSERT INTO `system_log` VALUES ('df6e674e-7473-4872-b0d5-6325ad569ac3', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2022-12-19 12:48:10.308000');
INSERT INTO `system_log` VALUES ('dfdb5b30-f036-42ae-b495-a927b9637e36', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DriverController.save', '驾驶员管理', '2023-01-11 13:38:53.620000');
INSERT INTO `system_log` VALUES ('e017d6f5-1758-46f6-bdcb-0a3fbf9e5980', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-26 23:41:40.322000');
INSERT INTO `system_log` VALUES ('e058699b-0955-4b03-bb98-3737d337446b', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-27 14:37:52.294000');
INSERT INTO `system_log` VALUES ('e071c5e2-53ec-423b-bd74-39e5f74a6858', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2023-02-24 09:40:50.460000');
INSERT INTO `system_log` VALUES ('e1207465-9040-435a-aeea-b3f73cf3c76e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-27 20:05:57.256000');
INSERT INTO `system_log` VALUES ('e128f208-e800-44aa-8b84-976114205e97', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:35:15.068000');
INSERT INTO `system_log` VALUES ('e18c284f-4e20-4d39-8091-4d2108a56eb7', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 11:06:02.763000');
INSERT INTO `system_log` VALUES ('e18f0c0b-6676-4659-a982-7b814ef99fa3', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 09:18:30.065000');
INSERT INTO `system_log` VALUES ('e1cc9849-72be-46b8-8635-629594680f3d', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:04:23.849000');
INSERT INTO `system_log` VALUES ('e1d742a4-52f0-47b7-a8f7-a18eec7ff8a7', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 22:14:12.907000');
INSERT INTO `system_log` VALUES ('e23325f2-6c63-421f-9b5e-8013e60ed42a', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-18 08:15:31.933000');
INSERT INTO `system_log` VALUES ('e260d698-229c-4740-82b5-03ee24c3dfba', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-04-05 23:45:32.061000');
INSERT INTO `system_log` VALUES ('e268d7a0-049e-4832-bcf1-419fe57cb73e', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2025-10-24 11:04:55.895000');
INSERT INTO `system_log` VALUES ('e28cd01e-1dbf-4f1b-8c91-03f19e9ca0ad', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-04-28 20:50:17.256643');
INSERT INTO `system_log` VALUES ('e2f89d82-205c-449d-818c-2d336c93a3f0', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:35:57.570000');
INSERT INTO `system_log` VALUES ('e2ffda29-2d91-4bd2-8759-772654531338', '16256226@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-04-28 20:16:57.852666');
INSERT INTO `system_log` VALUES ('e326a8bc-529f-4ea9-a838-f285480e5475', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:23:30.428000');
INSERT INTO `system_log` VALUES ('e3349316-db5b-4c5b-af92-f41b5e28db6b', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:43:07.470000');
INSERT INTO `system_log` VALUES ('e384cc98-22da-43ff-a380-d58e89971fca', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-27 21:50:51.103000');
INSERT INTO `system_log` VALUES ('e38ef3c4-53f7-4d96-a1d3-9df7367fcd14', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-01-11 13:15:54.278000');
INSERT INTO `system_log` VALUES ('e3a1fac6-4172-414c-883a-ab2f8c03f963', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 09:26:04.376000');
INSERT INTO `system_log` VALUES ('e3a8d71f-aa55-4275-9f53-35274a82e042', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 11:05:28.659000');
INSERT INTO `system_log` VALUES ('e3e12a20-4ca3-405d-8520-771ca80b78c0', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:39:18.061060');
INSERT INTO `system_log` VALUES ('e400dcae-245a-4a77-8a6b-8160bc3d4a69', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:16:48.204801');
INSERT INTO `system_log` VALUES ('e47df5e1-7f52-4255-87b9-4dcd9ce7d02f', '16256226@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-05-08 23:58:41.446842');
INSERT INTO `system_log` VALUES ('e48d093f-b41d-4f19-b7f8-ef9fecd39524', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-26 23:20:57.682000');
INSERT INTO `system_log` VALUES ('e50a3d2a-2049-41a8-9a36-e54fa8c31fd4', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 20:24:05.253000');
INSERT INTO `system_log` VALUES ('e513ef31-59eb-4619-bcd4-712afba06d7a', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:35:52.815000');
INSERT INTO `system_log` VALUES ('e590db59-52a0-4262-81be-564f9c64fa2e', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:14:27.986476');
INSERT INTO `system_log` VALUES ('e6190b09-fcf6-4323-a2e7-8349f8de7277', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-24 11:08:11.465000');
INSERT INTO `system_log` VALUES ('e6529259-ca7e-4d46-8d45-9c2e593eba8c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-01-11 11:07:17.996000');
INSERT INTO `system_log` VALUES ('e65be43d-330b-4483-a0cf-8c61942beec9', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 20:05:49.286000');
INSERT INTO `system_log` VALUES ('e69a031f-4631-42cf-b9e3-c703d08dd805', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2022-12-19 11:43:15.469000');
INSERT INTO `system_log` VALUES ('e6a5640d-bd85-4f30-9f91-4f7346e8f103', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 15:11:29.087000');
INSERT INTO `system_log` VALUES ('e6dacf0e-74ef-4225-8b08-ca90472b70fb', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-20 19:58:50.077000');
INSERT INTO `system_log` VALUES ('e7170a24-ac20-46ad-8976-f8fb67376e71', '匿名用户', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 16:06:58.598000');
INSERT INTO `system_log` VALUES ('e74d70e9-0616-43d8-8755-aad756afaaf7', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-23 19:02:42.269000');
INSERT INTO `system_log` VALUES ('e7698026-54d2-4aee-a002-53a4b52d5a0d', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 08:35:46.120000');
INSERT INTO `system_log` VALUES ('e770f5bd-ba7a-40c9-9c03-69bfa3dc0ccf', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:15:29.007000');
INSERT INTO `system_log` VALUES ('e7aea320-616b-4872-ad39-814c08242882', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-30 17:28:55.674000');
INSERT INTO `system_log` VALUES ('e7d5dd72-9b1a-437c-844d-e0b38bcfe7e4', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.EmployeeController.save', '员工管理', '2025-04-28 20:50:28.466621');
INSERT INTO `system_log` VALUES ('e836163c-dbfe-4ac6-813e-665f54c11897', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-26 23:55:59.096000');
INSERT INTO `system_log` VALUES ('e84dbacc-cbaf-4bb1-8dfa-7f2aa444d103', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-26 23:26:33.303000');
INSERT INTO `system_log` VALUES ('e85072dc-d9f1-4c18-bd71-87c0a2337b86', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-13 17:24:37.519000');
INSERT INTO `system_log` VALUES ('e866503d-fe5a-488c-85e2-1a91d3acde39', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 09:36:16.578000');
INSERT INTO `system_log` VALUES ('e870ae71-4786-4ade-9af3-887f358ff5ea', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-01-11 13:38:02.871000');
INSERT INTO `system_log` VALUES ('e8d9d1fb-1eaa-4e09-8869-c64545719c69', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-23 23:16:53.431000');
INSERT INTO `system_log` VALUES ('e90d3665-69b1-47b4-821a-408eda62e406', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:29:56.723000');
INSERT INTO `system_log` VALUES ('e912d897-9aac-441c-b264-c30dc478dec4', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 18:06:47.397000');
INSERT INTO `system_log` VALUES ('e92ca93b-0c85-4c95-bd87-d5c85598fb32', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-01-11 09:27:04.690000');
INSERT INTO `system_log` VALUES ('e9399567-482f-4e54-bc32-7e1dd1d89c1b', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:17:12.118019');
INSERT INTO `system_log` VALUES ('e987ac58-854f-4c53-9900-c5c57589785c', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-26 23:26:26.172000');
INSERT INTO `system_log` VALUES ('e9ac99d4-14cc-43f4-9ebf-59193a0d7787', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:49:25.521000');
INSERT INTO `system_log` VALUES ('e9d6fb2f-2e23-46db-91c2-92eaf0c4e35f', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:13:26.197483');
INSERT INTO `system_log` VALUES ('ea3b4e68-7814-4c11-9962-3cb2bbf6d03a', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-19 22:24:08.802000');
INSERT INTO `system_log` VALUES ('ea516d65-293b-4928-820b-a0ef02bff156', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 17:36:29.649000');
INSERT INTO `system_log` VALUES ('ea6f173d-a597-47c2-b000-64b85a2c07d6', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-05-27 21:45:58.196387');
INSERT INTO `system_log` VALUES ('eab53c17-02c3-4289-b754-e1ea59c34949', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DriverController.save', '驾驶员管理', '2025-10-23 23:33:19.472000');
INSERT INTO `system_log` VALUES ('eac2059a-5b0e-4a6e-aeb3-140fae9b4fc4', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-25 21:56:48.592000');
INSERT INTO `system_log` VALUES ('eb087847-b43d-4125-a638-9c101614bcc1', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-23 11:12:09.841000');
INSERT INTO `system_log` VALUES ('eb2d2dc8-4b8c-4347-aa4d-ccfb58d73c66', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-05-09 00:01:24.683451');
INSERT INTO `system_log` VALUES ('eb4ce352-7355-4e9e-977b-9223c215988f', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:42:21.327000');
INSERT INTO `system_log` VALUES ('eb4d32e7-da96-4e81-a1ed-3d7791ecc3c8', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-04 14:35:45.346000');
INSERT INTO `system_log` VALUES ('eb680871-cd10-4307-8f87-99590b3c9176', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-27 21:48:27.703000');
INSERT INTO `system_log` VALUES ('eba73a3f-38c0-4176-a316-3484dcb53704', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-05-27 18:20:35.514527');
INSERT INTO `system_log` VALUES ('eba9413c-3004-489e-b337-e2d570f5d134', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 21:10:12.346485');
INSERT INTO `system_log` VALUES ('ebcf3e8e-f04a-45cb-8436-d75b8607433d', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-04-28 20:13:59.909385');
INSERT INTO `system_log` VALUES ('ebff733c-f7dc-49fd-bf5a-52997ee2a34b', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-28 18:34:50.544000');
INSERT INTO `system_log` VALUES ('ec14625f-12c7-4534-9324-a644dcb56cdc', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-30 21:31:48.397000');
INSERT INTO `system_log` VALUES ('ec159657-1a32-4d5d-91f5-6c0f06c5c483', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-26 22:56:17.574000');
INSERT INTO `system_log` VALUES ('ec8d6435-89c0-4202-b833-f9b82f89290d', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:15:43.414738');
INSERT INTO `system_log` VALUES ('ec9a6529-1cf8-40b5-bae4-fcd76b257ab4', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-27 14:32:25.998000');
INSERT INTO `system_log` VALUES ('eca9e67d-3eb1-4f83-92ef-917b68a7fc4c', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 11:50:14.184000');
INSERT INTO `system_log` VALUES ('ecab1119-6f1a-4c2b-9242-8d8f09c03e2d', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-26 23:52:35.159000');
INSERT INTO `system_log` VALUES ('ed994aa5-7da3-4ac7-a85f-3cd8ecf0ee5a', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-01-11 09:26:39.927000');
INSERT INTO `system_log` VALUES ('edb70488-6fec-4802-9926-efea1e9463e2', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-20 07:59:33.697000');
INSERT INTO `system_log` VALUES ('edc64ec6-34a4-4795-b941-caa9c1666b83', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:09:29.459000');
INSERT INTO `system_log` VALUES ('ee2ba066-7cd2-41d0-bbac-95984f4db074', 'code51User@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.DistributionController.save', '配送管理', '2023-02-24 09:53:53.700000');
INSERT INTO `system_log` VALUES ('ee3616ee-e06f-434d-9a5c-8a0c4a8f51f4', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-19 23:38:08.266000');
INSERT INTO `system_log` VALUES ('ee41e00f-51fb-4d6d-acec-7e9c9ffa8004', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-17 15:42:24.775000');
INSERT INTO `system_log` VALUES ('ee60e1ab-56ca-43b8-b1d4-f75d11619556', 'code51User@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2023-02-24 09:49:24.889000');
INSERT INTO `system_log` VALUES ('eea867a2-2400-4e7b-a8f9-1e2f43bfd343', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-08 23:57:39.359892');
INSERT INTO `system_log` VALUES ('eef3a80b-0f23-40b2-9b83-ff3f7ac45251', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 21:17:43.463826');
INSERT INTO `system_log` VALUES ('ef0336a5-8e41-4c91-a453-eee35c132f1d', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 23:29:42.452000');
INSERT INTO `system_log` VALUES ('ef4a0963-b6d6-4c03-b715-a55792a7222b', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-27 18:19:30.698186');
INSERT INTO `system_log` VALUES ('ef9af503-7e1b-41ec-a28c-851aa04ae3b5', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2022-12-18 12:15:50.549000');
INSERT INTO `system_log` VALUES ('efe97256-1479-4975-ab47-5a31755a90ee', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findByLikeName', '商品管理', '2023-01-11 11:05:14.950000');
INSERT INTO `system_log` VALUES ('eff5086b-4d33-484c-970c-4cf35d6af833', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 16:45:37.987000');
INSERT INTO `system_log` VALUES ('f02f8f2b-9f8c-47ec-adde-3ce48f2a158d', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-04-28 20:17:18.783452');
INSERT INTO `system_log` VALUES ('f040fb54-7e88-4910-936b-3b15a8d6ccc3', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-01-11 09:11:05.506000');
INSERT INTO `system_log` VALUES ('f0e16543-2d65-4e68-bba6-7ef5e9747d23', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-24 11:10:24.941000');
INSERT INTO `system_log` VALUES ('f0e18f59-b4b6-4534-b483-115dce75c059', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 14:59:47.496000');
INSERT INTO `system_log` VALUES ('f10b0247-43c7-41ff-a9e3-557626e80936', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-27 21:43:31.352002');
INSERT INTO `system_log` VALUES ('f11d4d86-e85b-41a9-944c-075655fa750b', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:13:35.714000');
INSERT INTO `system_log` VALUES ('f133e330-0928-4a78-9bc2-034d18c10c5e', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-28 18:04:37.864000');
INSERT INTO `system_log` VALUES ('f13c9fd4-19ad-4ade-8f8a-17bf48832dad', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2025-10-18 08:25:13.784000');
INSERT INTO `system_log` VALUES ('f18f9c1f-4bff-4027-bcba-d91dd71b5818', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2025-10-24 11:05:10.121000');
INSERT INTO `system_log` VALUES ('f1b6118c-3a23-460c-9d35-620acb5697ca', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:44:53.055000');
INSERT INTO `system_log` VALUES ('f1b64160-7c16-4cd7-b2d9-51fcf49abdf4', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 09:19:19.949000');
INSERT INTO `system_log` VALUES ('f1ba1106-810e-4b33-bad2-f8253243d43d', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-18 08:21:30.718000');
INSERT INTO `system_log` VALUES ('f1c113e1-dd54-4e04-b356-cd93e6803200', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-19 11:43:10.861000');
INSERT INTO `system_log` VALUES ('f2fb52f5-8ada-405a-a0ac-352c1d40228c', 'jiegod_8ck@126.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-04-28 20:32:03.242814');
INSERT INTO `system_log` VALUES ('f34968f1-df89-4f50-869f-776a44ad576c', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 19:43:11.771000');
INSERT INTO `system_log` VALUES ('f3757651-b6c9-4ed3-8808-ec1caf1ccd7b', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-17 22:03:48.167000');
INSERT INTO `system_log` VALUES ('f3947120-4e96-4978-8ab8-6ea413d492a8', 'admin@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2023-01-11 11:05:03.463000');
INSERT INTO `system_log` VALUES ('f3a8617f-89c0-43e6-941e-2b210b42366a', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.VehicleController.save', '车辆管理', '2025-10-23 23:24:33.873000');
INSERT INTO `system_log` VALUES ('f3b2657b-832c-4d42-aee5-8905373cc106', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-10-24 11:04:58.839000');
INSERT INTO `system_log` VALUES ('f3c15b7b-e82f-4028-afe0-7f37f6bbce89', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-27 21:49:59.816000');
INSERT INTO `system_log` VALUES ('f46bee01-260c-422b-8c9e-3ca1966bcb94', '2159609711@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-10-24 11:04:23.373000');
INSERT INTO `system_log` VALUES ('f475d5af-3347-45f7-921d-6ba943368abf', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2024-06-27 14:33:38.406000');
INSERT INTO `system_log` VALUES ('f480774f-e25f-4da2-9fa0-9c53fcc98f8c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-01-11 10:16:29.688000');
INSERT INTO `system_log` VALUES ('f4f63cb7-2a5f-452a-80aa-e40377a80102', 'abc@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-18 12:16:01.263000');
INSERT INTO `system_log` VALUES ('f5915e05-2355-494a-b042-c2a41625f524', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:40:50.078000');
INSERT INTO `system_log` VALUES ('f5bd5eb7-4200-47e1-bf4c-307e8d26e658', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2024-06-13 17:21:05.041000');
INSERT INTO `system_log` VALUES ('f5c130de-f6cb-4c05-8472-40da8e099e5b', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2022-12-19 13:01:30.544000');
INSERT INTO `system_log` VALUES ('f5d5299d-2928-4cfa-b783-2c267a97d2ca', '2159609711@qq.com', '删除', '127.0.0.1', 'com.example.api.controller.CommodityController.delete', '商品管理', '2025-10-24 11:01:28.669000');
INSERT INTO `system_log` VALUES ('f6a2f850-c540-4b83-9502-cbcd06131ec9', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 11:05:04.149000');
INSERT INTO `system_log` VALUES ('f6e1ccb6-d3e1-4f95-8a96-c1d203dca05b', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-19 16:25:36.911000');
INSERT INTO `system_log` VALUES ('f6f5738c-f196-412c-a8c4-9f94d492f1b7', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:36:29.843000');
INSERT INTO `system_log` VALUES ('f734796e-210b-42eb-8f9d-65fbadc3c73e', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:16:58.072955');
INSERT INTO `system_log` VALUES ('f774940a-9e7f-4ecb-8e65-7cb9a7909e19', 'code51User@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-24 09:53:44.475000');
INSERT INTO `system_log` VALUES ('f77510af-4add-44cd-8694-5763f01d24cd', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-26 23:57:33.492000');
INSERT INTO `system_log` VALUES ('f7b98bd1-d22f-4f32-8069-3b11dd46fd16', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2024-06-27 14:26:59.264000');
INSERT INTO `system_log` VALUES ('f7d944f9-692c-4759-a857-f4672a43c65f', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 10:02:08.182000');
INSERT INTO `system_log` VALUES ('f7dc0c5c-399d-4eda-91b1-aa4f5426c06e', '2159609711@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-10-24 11:25:12.048000');
INSERT INTO `system_log` VALUES ('f7ee3450-b076-4430-9337-9bab802401bc', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-05-25 13:12:16.522116');
INSERT INTO `system_log` VALUES ('f8386bd8-8db8-49fb-9100-c988c7881fcc', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-24 09:36:13.350000');
INSERT INTO `system_log` VALUES ('f84532db-e502-49c0-a572-f0f3c76d4bdd', 'testUser@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-24 09:51:25.546000');
INSERT INTO `system_log` VALUES ('f8d03a5f-bcde-4c87-8c47-35cc64317523', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2025-05-28 10:07:11.407173');
INSERT INTO `system_log` VALUES ('f8fb4848-84b4-4307-8341-8bd8fc124834', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2022-12-19 11:21:38.466000');
INSERT INTO `system_log` VALUES ('f91eecb2-bad4-4775-bb1b-76bf9563a16c', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-05-09 13:06:59.586563');
INSERT INTO `system_log` VALUES ('f9559e2b-4dd5-46c2-adb5-18968a080cdb', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2022-12-23 19:07:50.563000');
INSERT INTO `system_log` VALUES ('f9a13c0c-5aa2-4b97-8969-0fcaab20a799', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 21:16:29.232101');
INSERT INTO `system_log` VALUES ('f9a985f9-c2b4-43ec-bdf4-78d2e3423541', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:32:48.606183');
INSERT INTO `system_log` VALUES ('f9bba91b-0deb-48ef-a25c-bee2943c2388', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-18 08:10:04.852000');
INSERT INTO `system_log` VALUES ('f9ec94e0-69be-4eb2-8e22-7ad6bcde1be8', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:19:21.598534');
INSERT INTO `system_log` VALUES ('f9fa3b8f-600c-4a48-8a78-58e50f2b984b', '470372007@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-05-28 10:12:36.337658');
INSERT INTO `system_log` VALUES ('f9fd32a4-3c1d-4ddd-a5a6-e94d04fe4543', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2023-02-24 09:55:54.607000');
INSERT INTO `system_log` VALUES ('f9feefd2-4a32-4175-bb66-19d9ad2a76b5', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2022-12-19 13:10:30.279000');
INSERT INTO `system_log` VALUES ('fa6436ce-2089-4dc7-8b3d-95b19955d2ad', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:07:33.486000');
INSERT INTO `system_log` VALUES ('fad293c9-be2c-4465-bbbf-ce6d9b5e3125', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-20 17:08:37.069000');
INSERT INTO `system_log` VALUES ('fb1f18ca-e7d7-4984-ac83-343283ad8f64', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2023-02-24 09:56:10.824000');
INSERT INTO `system_log` VALUES ('fb3b4d60-65c6-4a61-a9cb-d7556a54b845', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-10-19 22:23:58.584000');
INSERT INTO `system_log` VALUES ('fb48ab2f-5497-438c-9f74-07007e04d30e', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:36:43.653047');
INSERT INTO `system_log` VALUES ('fb7041cc-6be3-4d9d-a53d-ef1786a3cb6e', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-03 10:08:44.785000');
INSERT INTO `system_log` VALUES ('fb79e9be-a857-4d95-bb2d-68319c7164d3', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-26 22:57:46.333000');
INSERT INTO `system_log` VALUES ('fc013a7f-10e0-45e0-94e6-93648ae204c7', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.EmployeeController.findAll', '员工管理', '2025-10-19 22:23:58.563000');
INSERT INTO `system_log` VALUES ('fc6f23a4-0e2f-4267-9a90-d6a90e7b2c6c', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:34:02.307076');
INSERT INTO `system_log` VALUES ('fcc4e04b-6bc5-4333-bf23-f2445f8d0ed4', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 09:25:35.909000');
INSERT INTO `system_log` VALUES ('fd0aa192-2b0b-4e7e-8320-b4cd7d2bd9f1', '16256226@qq.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:19:17.469903');
INSERT INTO `system_log` VALUES ('fd154d82-268e-40a7-9073-3a2f220f2ba9', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:07:45.198000');
INSERT INTO `system_log` VALUES ('fd2d119d-6778-4343-9afe-8e91a02f910f', 'jiegod_8ck@126.com', '新增', '127.0.0.1', 'com.example.api.controller.CommodityController.save', '商品管理', '2025-04-28 20:34:15.906039');
INSERT INTO `system_log` VALUES ('fd3a4c5c-b289-4201-8058-8ed4852ae1c9', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DistributionController.findAll', '配送管理', '2023-02-20 19:59:00.038000');
INSERT INTO `system_log` VALUES ('fd3cf6e5-2fe8-4ac7-922c-158cc53a5d45', '1638752551@163.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2024-06-27 14:26:24.083000');
INSERT INTO `system_log` VALUES ('fd4fb9b6-687b-4cf3-92a2-a3c55ab3c539', '123@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2023-02-23 11:16:30.719000');
INSERT INTO `system_log` VALUES ('fd600f8e-d038-4ffa-b7d0-9756a7d046cb', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 11:16:33.849000');
INSERT INTO `system_log` VALUES ('fd639c7f-6e52-4ee0-9ffd-1df0dd87076c', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.DriverController.findAll', '驾驶员管理', '2022-12-19 13:10:58.648000');
INSERT INTO `system_log` VALUES ('fd64dab2-6374-4cd4-a2de-b2044e820380', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.VehicleController.findAll', '车辆管理', '2022-12-19 11:21:31.942000');
INSERT INTO `system_log` VALUES ('fd64e793-44e0-41f5-86fe-f7525e3cee8e', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-18 11:30:17.740000');
INSERT INTO `system_log` VALUES ('fdfe2804-b43d-4a7d-a48e-aa861ba5c284', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-01-11 13:39:20.473000');
INSERT INTO `system_log` VALUES ('fe2391e8-af56-48fb-894b-eb817b92f576', 'jiegod_8ck@126.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-04-28 20:31:19.183812');
INSERT INTO `system_log` VALUES ('fe3d8c18-e395-4342-86ac-06f79aace322', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2023-02-16 10:30:18.971000');
INSERT INTO `system_log` VALUES ('fe4899d1-55dc-4d8e-9745-9f598d1e21e3', 'admin@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2022-12-23 19:03:22.603000');
INSERT INTO `system_log` VALUES ('fe7deecd-96c2-41bd-981b-12a91999261c', '16256226@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.WarehouseController.findAll', '仓库管理', '2025-04-13 21:24:40.135267');
INSERT INTO `system_log` VALUES ('ff41ef38-8d2f-4f37-964a-ed57b17a580c', '2159609711@qq.com', '查询', '127.0.0.1', 'com.example.api.controller.CommodityController.findAll', '商品管理', '2025-10-24 22:32:30.755000');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `update_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('a', null, '123456', null, '123@qq.com');

-- ----------------------------
-- Table structure for vehicle
-- ----------------------------
DROP TABLE IF EXISTS `vehicle`;
CREATE TABLE `vehicle` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `driving` bit(1) NOT NULL,
  `number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of vehicle
-- ----------------------------
INSERT INTO `vehicle` VALUES ('751ecedf-2211-416f-9fc8-6c797ff51d16', '2025-04-24 09:45:23', '\0', '京A0010', '小型汽车');
INSERT INTO `vehicle` VALUES ('786fb288-4445-4784-b923-de013fa3892f', '2025-04-24 13:38:19', '\0', '京A0001', '货车');
INSERT INTO `vehicle` VALUES ('ce33de59-9584-4161-a17e-9046399d14c6', '2025-04-24 12:26:18', '\0', '京A0000', '货车');
INSERT INTO `vehicle` VALUES ('cfdafbb3-662b-4066-a39b-7ef688b7de57', '2025-10-23 23:24:33', '\0', '京A88888', '货车');
INSERT INTO `vehicle` VALUES ('d0b7fba2-ddcc-443b-9706-01e0ee5e7205', '2025-10-24 11:05:17', '\0', '京A00001', '货车');
INSERT INTO `vehicle` VALUES ('ecbaad22-2eb5-4d3b-af56-a82d0560abf2', '2025-10-19 23:11:31', '\0', '京A0000', '货车');

-- ----------------------------
-- Table structure for warehouse
-- ----------------------------
DROP TABLE IF EXISTS `warehouse`;
CREATE TABLE `warehouse` (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `principle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of warehouse
-- ----------------------------
INSERT INTO `warehouse` VALUES ('7159a7e3-32c8-4c29-9608-039472ae47ac', '2025-04-24 09:43:05', '源码乐园', '源码乐园');
INSERT INTO `warehouse` VALUES ('9f2a2784-e182-4fdf-85e8-c3bde6d539d2', '2025-04-24 14:16:46', 'A号仓库', '杰克');
INSERT INTO `warehouse` VALUES ('ba6d7573-75e4-4e11-82a8-cf89991f4659', '2025-10-18 08:20:23', 'B号仓库', '小名');
