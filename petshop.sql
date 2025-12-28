/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : petshop

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 28/12/2025 21:40:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for browse_history
-- ----------------------------
DROP TABLE IF EXISTS `browse_history`;
CREATE TABLE `browse_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `browse_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `browse_history_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '浏览历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of browse_history
-- ----------------------------
INSERT INTO `browse_history` VALUES (1, 4, 1, '2025-11-23 10:54:15');
INSERT INTO `browse_history` VALUES (2, 4, 2, '2025-11-23 10:54:15');
INSERT INTO `browse_history` VALUES (3, 4, 5, '2025-11-23 10:54:15');
INSERT INTO `browse_history` VALUES (4, 5, 3, '2025-11-23 10:54:15');
INSERT INTO `browse_history` VALUES (5, 6, 1, '2025-11-23 10:54:15');
INSERT INTO `browse_history` VALUES (6, 6, 6, '2025-11-23 10:54:15');
INSERT INTO `browse_history` VALUES (7, 7, 4, '2025-11-23 10:54:15');

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL COMMENT '数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_product`(`user_id` ASC, `product_id` ASC) USING BTREE,
  UNIQUE INDEX `UKmg7gch7630wv3d0mif6gtnsy0`(`user_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (1, 4, 1, 1, '2025-11-23 10:54:13', '2025-11-23 10:54:13');
INSERT INTO `cart` VALUES (2, 4, 3, 2, '2025-11-23 10:54:13', '2025-11-23 10:54:13');
INSERT INTO `cart` VALUES (4, 6, 2, 1, '2025-11-23 10:54:13', '2025-11-23 10:54:13');
INSERT INTO `cart` VALUES (5, 6, 4, 5, '2025-11-23 10:54:13', '2025-11-23 10:54:13');
INSERT INTO `cart` VALUES (6, 7, 6, 2, '2025-11-23 10:54:13', '2025-11-23 10:54:13');
INSERT INTO `cart` VALUES (7, 5, 1, 2, '2025-11-23 22:59:54', '2025-11-23 23:53:46');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父分类ID',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '狗狗主粮', NULL, 1, '2025-11-23 10:54:12');
INSERT INTO `category` VALUES (2, '猫咪主粮', NULL, 2, '2025-11-23 10:54:12');
INSERT INTO `category` VALUES (3, '宠物零食', NULL, 3, '2025-11-23 10:54:12');
INSERT INTO `category` VALUES (4, '全价狗粮', 1, 1, '2025-11-23 10:54:12');
INSERT INTO `category` VALUES (5, '全价猫粮', 2, 1, '2025-11-23 10:54:12');
INSERT INTO `category` VALUES (6, '猫罐头', 3, 1, '2025-11-23 10:54:12');

-- ----------------------------
-- Table structure for community_post
-- ----------------------------
DROP TABLE IF EXISTS `community_post`;
CREATE TABLE `community_post`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '图片(JSON)',
  `likes` int NULL DEFAULT 0 COMMENT '点赞数',
  `views` int NULL DEFAULT 0 COMMENT '浏览数',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态(0正常/1已删除)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `community_post_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社区帖子表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of community_post
-- ----------------------------
INSERT INTO `community_post` VALUES (1, 4, '求助！新手养猫需要准备什么？', '刚捡了一只小橘猫，大概两个月大，请问各位大神需要买什么用品？', '[\"post1.jpg\"]', 25, 301, 0, '2025-11-23 10:54:14', '2025-11-23 20:33:16');
INSERT INTO `community_post` VALUES (3, 6, '避雷！这家宠物店洗澡不干净', '大家千万别去XX路那家店，回来发现耳朵都没擦干净。', NULL, 56, 890, 0, '2025-11-23 10:54:14', '2025-11-23 10:54:14');
INSERT INTO `community_post` VALUES (4, 2, '店铺上新活动预告', '快乐宠屋下周一有大促，全场8折，欢迎选购！', '[\"promo.jpg\"]', 12, 151, 0, '2025-11-23 10:54:14', '2025-11-23 20:33:41');
INSERT INTO `community_post` VALUES (5, 7, '仓鼠越狱指南', '昨晚杰瑞又跑出来了，在沙发底下抓到的...', '[\"hamster.jpg\"]', 88, 600, 0, '2025-11-23 10:54:14', '2025-11-23 10:54:14');
INSERT INTO `community_post` VALUES (6, 5, '00', '000', '[\"http://localhost:8080/images/28ae453b9c3b47068ce87672a58d0ead.jpg\",\"http://localhost:8080/images/e7c3eb6278ff4f36aa082e0d041ef0c9.jpeg\"]', 1, 35, 0, '2025-11-23 20:25:46', '2025-11-23 21:15:40');
INSERT INTO `community_post` VALUES (7, 5, '000', '000', '[]', 0, 0, 1, '2025-11-23 23:54:26', '2025-11-23 23:57:07');

-- ----------------------------
-- Table structure for community_reply
-- ----------------------------
DROP TABLE IF EXISTS `community_reply`;
CREATE TABLE `community_reply`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `community_reply_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `community_post` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `community_reply_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社区评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of community_reply
-- ----------------------------
INSERT INTO `community_reply` VALUES (1, 1, 5, '猫砂盆、猫粮、猫砂是必须的，还有驱虫药。', '2025-11-23 10:54:14');
INSERT INTO `community_reply` VALUES (2, 1, 6, '建议先带去医院体检一下哦。', '2025-11-23 10:54:14');
INSERT INTO `community_reply` VALUES (5, 3, 5, '收到，感谢排雷。', '2025-11-23 10:54:14');
INSERT INTO `community_reply` VALUES (6, 5, 4, '哈哈哈，我家那只也天天想越狱。', '2025-11-23 10:54:14');
INSERT INTO `community_reply` VALUES (7, 6, 5, '0000', '2025-11-23 21:14:48');
INSERT INTO `community_reply` VALUES (8, 6, 5, '0', '2025-11-23 21:15:14');
INSERT INTO `community_reply` VALUES (9, 6, 5, '000', '2025-11-23 21:15:46');

-- ----------------------------
-- Table structure for coupon
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `shop_id` bigint NOT NULL COMMENT '店铺ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '优惠券名称',
  `discount_amount` decimal(10, 2) NOT NULL COMMENT '折扣金额',
  `min_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '最低消费金额',
  `total_count` int NULL DEFAULT NULL COMMENT '发放总数',
  `used_count` int NULL DEFAULT 0 COMMENT '已使用数量',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态(0禁用/1启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_shop_id`(`shop_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `coupon_ibfk_1` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of coupon
-- ----------------------------
INSERT INTO `coupon` VALUES (1, 1, '新客立减券', 10.00, 50.00, 1000, 0, NULL, NULL, 1, '2025-11-23 10:54:15');
INSERT INTO `coupon` VALUES (2, 2, '全场通用大额券', 50.00, 399.00, 200, 0, NULL, NULL, 1, '2025-11-23 10:54:15');
INSERT INTO `coupon` VALUES (3, 3, '双11狂欢券', 20.00, 199.00, 5000, 0, NULL, NULL, 1, '2025-11-23 10:54:15');
INSERT INTO `coupon` VALUES (4, 1, '狗粮专用券', 15.00, 100.00, 500, 0, NULL, NULL, 1, '2025-11-23 10:54:15');
INSERT INTO `coupon` VALUES (5, 2, '零食尝鲜券', 5.00, 29.00, 1000, 0, NULL, NULL, 1, '2025-11-23 10:54:15');
INSERT INTO `coupon` VALUES (6, 1, '新年优惠券', 100.00, 50.00, NULL, 0, '2025-11-24 15:19:00', '2025-12-23 15:19:00', 1, '2025-11-23 23:19:47');
INSERT INTO `coupon` VALUES (7, 1, '双十一优惠券', 2.00, 20.00, NULL, 0, '2025-11-24 15:32:00', '2025-12-23 15:32:00', 1, '2025-11-23 23:32:58');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品名称',
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL COMMENT '数量',
  `subtotal` decimal(10, 2) NOT NULL COMMENT '小计',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 1, '皇家小型犬成犬粮 2kg', 'https://api.example.com/products/p1.jpg', 128.00, 1, 128.00);
INSERT INTO `order_item` VALUES (2, 2, 3, '宠物冻干鸡胸肉 500g', 'https://api.example.com/products/p3.jpg', 45.00, 1, 45.00);
INSERT INTO `order_item` VALUES (3, 2, 4, '巅峰主食罐头 85g', 'https://api.example.com/products/p4.jpg', 28.00, 2, 56.00);
INSERT INTO `order_item` VALUES (4, 3, 5, '伯纳天纯鸭肉梨狗粮 15kg', 'https://api.example.com/products/p5.jpg', 450.00, 1, 450.00);
INSERT INTO `order_item` VALUES (5, 4, 2, '渴望六种鱼猫粮 1.8kg', 'https://api.example.com/products/p2.jpg', 380.00, 1, 380.00);
INSERT INTO `order_item` VALUES (6, 5, 4, '巅峰主食罐头 85g', 'https://api.example.com/products/p4.jpg', 28.00, 5, 140.00);
INSERT INTO `order_item` VALUES (7, 6, 5, '伯纳天纯鸭肉梨狗粮 15kg', 'https://api.example.com/products/p5.jpg', 450.00, 2, 900.00);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `shop_id` bigint NOT NULL COMMENT '店铺ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '总金额',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态(0待支付/1待发货/2已发货/3已完成/4已取消)',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货电话',
  `receiver_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货地址',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `ship_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_shop_id`(`shop_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'ORD20231101001', 4, 1, 128.00, 3, '柳岩', '13900000001', '深圳市南山区科技园A栋', NULL, NULL, NULL, '2025-11-23 10:54:14', '2025-11-23 10:54:14');
INSERT INTO `orders` VALUES (2, 'ORD20231102002', 4, 2, 101.00, 2, '柳岩', '13900000001', '深圳市南山区科技园A栋', NULL, NULL, NULL, '2025-11-23 10:54:14', '2025-11-23 10:54:14');
INSERT INTO `orders` VALUES (3, 'ORD20231103003', 5, 3, 450.00, 1, '张三', '13900000002', '杭州市西湖区文一西路', NULL, NULL, NULL, '2025-11-23 10:54:14', '2025-11-23 10:54:14');
INSERT INTO `orders` VALUES (4, 'ORD20231104004', 6, 1, 380.00, 0, '李四', '13900000003', '成都市武侯区锦里', NULL, NULL, NULL, '2025-11-23 10:54:14', '2025-11-23 10:54:14');
INSERT INTO `orders` VALUES (5, 'ORD20231105005', 7, 2, 140.00, 3, '王五', '13900000004', '武汉市洪山区光谷广场', NULL, NULL, NULL, '2025-11-23 10:54:14', '2025-11-23 10:54:14');
INSERT INTO `orders` VALUES (6, '202511231902520001', 5, 3, 900.00, 1, '昂纳', '1507146895', '不是VS', '八十八VS规范是v', '2025-11-23 19:25:21', NULL, '2025-11-23 19:02:53', '2025-11-23 19:25:21');

-- ----------------------------
-- Table structure for pet
-- ----------------------------
DROP TABLE IF EXISTS `pet`;
CREATE TABLE `pet`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '宠物名称',
  `species` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '种类',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `pet_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '宠物档案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pet
-- ----------------------------
INSERT INTO `pet` VALUES (1, 4, '旺财', '中华田园犬', 3, '公', 'https://api.example.com/pets/dog1.jpg', '2025-11-23 10:54:12');
INSERT INTO `pet` VALUES (2, 4, '咪咪', '英国短毛猫', 2, '母', 'https://api.example.com/pets/cat1.jpg', '2025-11-23 10:54:12');
INSERT INTO `pet` VALUES (3, 5, '可乐', '金毛', 4, '公', 'https://api.example.com/pets/dog2.jpg', '2025-11-23 10:54:12');
INSERT INTO `pet` VALUES (4, 6, '汤圆', '布偶猫', 1, '母', 'https://api.example.com/pets/cat2.jpg', '2025-11-23 10:54:12');
INSERT INTO `pet` VALUES (5, 7, '杰瑞', '仓鼠', 1, '公', 'https://api.example.com/pets/hamster.jpg', '2025-11-23 10:54:12');
INSERT INTO `pet` VALUES (6, 5, '奥斯卡', '哈士奇', 2, '公', 'https://api.example.com/pets/husky.jpg', '2025-11-23 10:54:12');
INSERT INTO `pet` VALUES (8, 5, '中华土狗', '狗', 1, '公', '', '2025-11-23 18:31:42');
INSERT INTO `pet` VALUES (9, 5, '000', '猫', 0, '公', '', '2025-11-23 18:32:22');
INSERT INTO `pet` VALUES (10, 5, '55252', '鸟', 1, '公', '', '2025-11-23 23:53:37');

-- ----------------------------
-- Table structure for post_like
-- ----------------------------
DROP TABLE IF EXISTS `post_like`;
CREATE TABLE `post_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_post_user`(`post_id` ASC, `user_id` ASC) USING BTREE,
  UNIQUE INDEX `UKpmmko3h7yonaqhy5gxvnmdeue`(`post_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `post_like_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `community_post` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `post_like_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_like
-- ----------------------------
INSERT INTO `post_like` VALUES (1, 1, 5, '2025-11-23 10:54:14');
INSERT INTO `post_like` VALUES (2, 1, 6, '2025-11-23 10:54:14');
INSERT INTO `post_like` VALUES (6, 5, 4, '2025-11-23 10:54:14');
INSERT INTO `post_like` VALUES (9, 6, 5, '2025-11-23 21:02:59');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `shop_id` bigint NOT NULL COMMENT '店铺ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主图URL',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品图片(JSON)',
  `status` int NOT NULL DEFAULT 2 COMMENT '状态(0下架/1上架/2待审核)',
  `sales` int NULL DEFAULT 0 COMMENT '销量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_shop_id`(`shop_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  CONSTRAINT `product_ibfk_1` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `product_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, 1, 4, '皇家小型犬成犬粮 2kg', '专为小型犬设计，营养均衡，亮毛护肤。', 128.00, 100, 'https://api.example.com/products/p1.jpg', '[\"p1_1.jpg\", \"p1_2.jpg\"]', 1, 50, '2025-11-23 10:54:13', '2025-11-23 10:54:13');
INSERT INTO `product` VALUES (2, 1, 5, '渴望六种鱼猫粮 1.8kg', '加拿大进口，高蛋白无谷配方。', 380.00, 50, 'https://api.example.com/products/p2.jpg', '[\"p2_1.jpg\"]', 1, 120, '2025-11-23 10:54:13', '2025-11-23 10:54:13');
INSERT INTO `product` VALUES (3, 2, 3, '宠物冻干鸡胸肉 500g', '纯肉制作，无添加，猫狗通用。', 45.00, 200, 'https://api.example.com/products/p3.jpg', '[\"p3_1.jpg\"]', 2, 300, '2025-11-23 10:54:13', '2025-11-23 22:12:02');
INSERT INTO `product` VALUES (4, 2, 6, '巅峰主食罐头 85g', '新西兰进口，98%含肉量。', 28.00, 500, 'https://api.example.com/products/p4.jpg', '[\"p4_1.jpg\"]', 1, 80, '2025-11-23 10:54:13', '2025-11-23 10:54:13');
INSERT INTO `product` VALUES (5, 3, 4, '伯纳天纯鸭肉梨狗粮 15kg', '去泪痕配方，大包装更实惠。', 450.00, 18, 'https://api.example.com/products/p5.jpg', '[\"p5_1.jpg\"]', 1, 15, '2025-11-23 10:54:13', '2025-11-23 19:02:53');
INSERT INTO `product` VALUES (6, 3, 5, '网易严选全价猫粮 2kg', '性价比之选，添加益生菌。', 88.00, 150, 'https://api.example.com/products/p6.jpg', '[\"p6_1.jpg\"]', 1, 200, '2025-11-23 10:54:13', '2025-11-23 10:54:13');
INSERT INTO `product` VALUES (7, 1, 1, '5555', '00000000000000000', 10.00, 1000, '', '[]', 2, 0, '2025-11-23 23:32:29', '2025-11-23 23:32:29');
INSERT INTO `product` VALUES (8, 1, 2, '00000', '00000', 10.00, 100, '', '[]', 2, 0, '2025-11-23 23:55:43', '2025-11-23 23:55:43');

-- ----------------------------
-- Table structure for review
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `rating` int NOT NULL COMMENT '评分(1-5)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价图片(JSON)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `review_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `review_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `review_ibfk_3` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of review
-- ----------------------------
INSERT INTO `review` VALUES (1, 4, 1, 1, 5, '狗狗很爱吃，颗粒大小刚好，物流也很快！', '[\"r1.jpg\"]', '2025-11-23 10:54:14');
INSERT INTO `review` VALUES (2, 4, 4, 2, 5, '巅峰罐头适口性无敌，就是有点贵，不过为了主子值得。', NULL, '2025-11-23 10:54:14');
INSERT INTO `review` VALUES (3, 7, 4, 5, 4, '包装有点压扁了，但不影响食用，希望改进包装。', NULL, '2025-11-23 10:54:14');
INSERT INTO `review` VALUES (4, 5, 5, 3, 5, '这大包能吃好久，日期很新鲜。', NULL, '2025-11-23 10:54:14');
INSERT INTO `review` VALUES (5, 6, 2, 4, 5, '还没拆封，看包装是正品。', NULL, '2025-11-23 10:54:14');

-- ----------------------------
-- Table structure for shop
-- ----------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '店主用户ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '店铺名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '店铺描述',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺Logo',
  `contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系方式',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态(0待审核/1正常/2禁用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `shop_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '店铺表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shop
-- ----------------------------
INSERT INTO `shop` VALUES (1, 2, '快乐宠屋旗舰店000', '专注猫狗粮，品质保证，给爱宠最好的呵护。', 'https://api.example.com/shops/logo1.jpg', '021-12345678', 1, '2025-11-23 10:54:12', '2025-11-23 23:55:56');
INSERT INTO `shop` VALUES (2, 3, '星光萌宠生活馆', '进口宠物零食、玩具、清洁用品一站式购物。', 'https://api.example.com/shops/logo2.jpg', '020-87654321', 1, '2025-11-23 10:54:12', '2025-11-23 10:54:12');
INSERT INTO `shop` VALUES (3, 3, '星落自营店', '官方自营，品质无忧，极速发货。', 'https://api.example.com/shops/logo_official.jpg', '400-8888888', 1, '2025-11-23 10:54:12', '2025-11-23 22:20:39');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'USER' COMMENT '角色(USER/SHOP/ADMIN)',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态(0禁用/1启用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '123456', '系统管理员', 'admin@petshop.com', '13800000000', 'https://api.example.com/avatars/admin.jpg', '北京市朝阳区', 'ADMIN', 1, '2025-11-23 10:54:12', '2025-11-23 10:54:12');
INSERT INTO `user` VALUES (2, 'shop_happy', '123456', '快乐宠屋店长', 'shop1@petshop.com', '13800000001', 'https://api.example.com/avatars/shop1.jpg', '上海市浦东新区', 'SHOP', 1, '2025-11-23 10:54:12', '2025-11-23 10:54:12');
INSERT INTO `user` VALUES (3, 'shop_star', '123456', '星光萌宠店长', 'shop2@petshop.com', '13800000002', 'https://api.example.com/avatars/shop2.jpg', '广州市天河区', 'SHOP', 1, '2025-11-23 10:54:12', '2025-11-23 10:54:12');
INSERT INTO `user` VALUES (4, 'user_liuyan', '123456', '柳岩', 'liuyan@test.com', '13900000001', 'https://api.example.com/avatars/user_liuyan.jpg', '深圳市南山区科技园', 'USER', 1, '2025-11-23 10:54:12', '2025-11-23 10:54:12');
INSERT INTO `user` VALUES (5, 'zhangsan', '123456', '张三', 'zs@test.com', '13900000002', 'https://api.example.com/avatars/zs.jpg', '杭州市西湖区', 'USER', 1, '2025-11-23 10:54:12', '2025-11-23 10:54:12');
INSERT INTO `user` VALUES (6, 'lisi', '123456', '李四', 'ls@test.com', '13900000003', 'https://api.example.com/avatars/ls.jpg', '成都市武侯区', 'USER', 1, '2025-11-23 10:54:12', '2025-11-23 10:54:12');
INSERT INTO `user` VALUES (7, 'wangwu', '123456', '王五', 'ww@test.com', '13900000004', 'https://api.example.com/avatars/ww.jpg', '武汉市洪山区', 'USER', 1, '2025-11-23 10:54:12', '2025-11-23 10:54:12');

-- ----------------------------
-- Table structure for user_coupon
-- ----------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `coupon_id` bigint NOT NULL COMMENT '优惠券ID',
  `order_id` bigint NULL DEFAULT NULL COMMENT '使用的订单ID',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态(0未使用/1已使用/2已过期)',
  `receive_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `use_time` datetime NULL DEFAULT NULL COMMENT '使用时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_coupon_id`(`coupon_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `user_coupon_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_coupon_ibfk_2` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `user_coupon_ibfk_3` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户优惠券表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_coupon
-- ----------------------------
INSERT INTO `user_coupon` VALUES (1, 4, 1, 1, 1, '2025-11-23 10:54:15', NULL);
INSERT INTO `user_coupon` VALUES (2, 4, 2, NULL, 0, '2025-11-23 10:54:15', NULL);
INSERT INTO `user_coupon` VALUES (3, 5, 3, 3, 1, '2025-11-23 10:54:15', NULL);
INSERT INTO `user_coupon` VALUES (4, 6, 5, NULL, 0, '2025-11-23 10:54:15', NULL);
INSERT INTO `user_coupon` VALUES (5, 7, 1, NULL, 2, '2025-11-23 10:54:15', NULL);
INSERT INTO `user_coupon` VALUES (6, 4, 3, NULL, 0, '2025-11-23 10:54:15', NULL);

SET FOREIGN_KEY_CHECKS = 1;
