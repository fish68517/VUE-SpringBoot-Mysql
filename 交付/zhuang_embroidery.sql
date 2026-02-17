/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : zhuang_embroidery

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 17/02/2026 13:05:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动标题',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '活动描述',
  `organizer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动组织者',
  `activity_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动时间',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动地点',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity
-- ----------------------------
INSERT INTO `activity` VALUES (2, '壮族服饰发展史专题讲座', '邀请民族学专家王教授，为大家梳理壮族服饰从明清到现代的演变过程。', '文山学院', '2026-04-10 09:30-11:30', '文山学院图书馆报告厅', '2026-02-15 08:28:44', '2026-02-15 08:28:44');
INSERT INTO `activity` VALUES (3, '“非遗进校园”系列活动', '组织刺绣手艺人走进中小学课堂，让孩子们从小接触和了解家乡的传统文化。', '州教育局', '2026-05-20 全天', '文山市第一小学', '2026-02-15 08:28:44', '2026-02-15 08:28:44');

-- ----------------------------
-- Table structure for artwork
-- ----------------------------
DROP TABLE IF EXISTS `artwork`;
CREATE TABLE `artwork`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '作品描述',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类：日常生活类/节日母题类/针法风格类',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作品图片 URL',
  `creator` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创作者名称',
  `technique` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '刺绣技法',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'draft' COMMENT '状态：draft/approved/rejected/offline',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏次数',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '作品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of artwork
-- ----------------------------
INSERT INTO `artwork` VALUES (1, '百鸟朝凤图腾背带', '壮族传统婴儿背带，采用鲜艳的红、黄、绿丝线，寓意吉祥如意，保护婴孩平安。', '日常生活类', '/images/artworks/beidai.jpg', '农氏刺绣坊', '平绣', 'approved', 184, 33, '2026-02-15 08:28:43', '2026-02-16 10:46:14');
INSERT INTO `artwork` VALUES (2, '几何八角星纹壁挂', '提取壮族典型的八角星图腾，结合现代家居审美制作的装饰壁挂。', '针法风格类', '/images/artworks/bigua.jpg', '阿妹手工工作室', '挑绣', 'approved', 96, 15, '2026-02-15 08:28:43', '2026-02-16 10:47:03');
INSERT INTO `artwork` VALUES (3, '三月三节日盛装', '专为“三月三”歌圩节定制的壮族女子套裙，领口与袖口满绣花蝶纹。', '节日母题类', '/images/artworks/shengzhuang.jpg', '文山民族服饰馆', '打籽绣', 'offline', 254, 57, '2026-02-15 08:28:43', '2026-02-16 11:34:45');
INSERT INTO `artwork` VALUES (4, '现代改良马甲', '尝试将壮族水波纹与现代牛仔面料结合的设计草图与局部刺绣小样。', '针法风格类', '/images/upload/49a99da3-a38c-429a-b0ca-b806a6de95a1.jpg', '设计系张同学', '贴绣', 'offline', 12, 1, '2026-02-15 08:28:43', '2026-02-17 03:29:13');

-- ----------------------------
-- Table structure for collection
-- ----------------------------
DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `artwork_id` bigint NOT NULL COMMENT '作品 ID',
  `collected_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_user_artwork`(`user_id` ASC, `artwork_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_artwork_id`(`artwork_id` ASC) USING BTREE,
  CONSTRAINT `collection_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `collection_ibfk_2` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collection
-- ----------------------------
INSERT INTO `collection` VALUES (3, 4, 2, '2026-02-15 08:28:44');
INSERT INTO `collection` VALUES (9, 5, 3, '2026-02-16 10:35:54');
INSERT INTO `collection` VALUES (10, 5, 1, '2026-02-16 10:45:53');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `artwork_id` bigint NULL DEFAULT NULL COMMENT '作品 ID',
  `topic_id` bigint NULL DEFAULT NULL COMMENT '话题 ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父评论 ID（用于回复）',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_artwork_id`(`artwork_id` ASC) USING BTREE,
  INDEX `idx_topic_id`(`topic_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`artwork_id`) REFERENCES `artwork` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (6, '000', 5, NULL, 2, NULL, '2026-02-16 08:05:03', '2026-02-16 08:05:03');
INSERT INTO `comment` VALUES (7, '太精明了', 5, 1, NULL, NULL, '2026-02-16 08:32:37', '2026-02-16 08:32:37');
INSERT INTO `comment` VALUES (8, '00', 5, 3, NULL, NULL, '2026-02-16 08:49:17', '2026-02-16 08:49:17');
INSERT INTO `comment` VALUES (9, '你你你', 5, 1, NULL, NULL, '2026-02-16 09:14:40', '2026-02-16 09:14:40');
INSERT INTO `comment` VALUES (10, '0000', 5, NULL, 1, NULL, '2026-02-16 09:15:49', '2026-02-16 09:15:49');
INSERT INTO `comment` VALUES (11, '并不高', 5, 1, NULL, NULL, '2026-02-16 10:23:10', '2026-02-16 10:23:10');
INSERT INTO `comment` VALUES (12, '我觉得可以做成胸针或者帆布包的贴布我觉得可以做成胸针或者帆布包的贴布我觉得可以做成胸针或者帆布包的贴布我觉得可以做成胸针或者帆布包的贴布我觉得可以做成胸针或者帆布包的贴布', 5, NULL, 1, NULL, '2026-02-16 10:23:41', '2026-02-16 10:23:41');
INSERT INTO `comment` VALUES (13, '1000', 5, 1, NULL, NULL, '2026-02-16 10:24:17', '2026-02-16 10:24:17');
INSERT INTO `comment` VALUES (14, '你说是不是撒还是', 6, NULL, 1, 2, '2026-02-17 03:24:40', '2026-02-17 03:24:40');
INSERT INTO `comment` VALUES (15, '552525245', 6, NULL, 1, 2, '2026-02-17 03:25:17', '2026-02-17 03:25:17');
INSERT INTO `comment` VALUES (16, '密度函数内部公司不能是', 6, NULL, 2, 5, '2026-02-17 03:30:50', '2026-02-17 03:30:50');

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户 ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '反馈内容',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系邮箱',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending' COMMENT '状态：pending/processed',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '反馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feedback
-- ----------------------------
INSERT INTO `feedback` VALUES (2, 4, '作品详情页的图片加载有点慢，希望能优化一下服务器带宽。', 'student@gmail.com', 'processed', '2026-02-15 08:28:43', NULL);
INSERT INTO `feedback` VALUES (4, 5, '那我好感', '123456789@qq.com', 'processed', '2026-02-16 09:08:09', '2026-02-17 05:01:08');
INSERT INTO `feedback` VALUES (5, 5, '哼哼那后果', '123456789@qq.com', 'pending', '2026-02-16 10:24:27', '2026-02-17 05:02:07');

-- ----------------------------
-- Table structure for knowledge
-- ----------------------------
DROP TABLE IF EXISTS `knowledge`;
CREATE TABLE `knowledge`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '文章内容',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类：技法知识/历史文化/政策法规/常见问题',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作者',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '知识表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge
-- ----------------------------
INSERT INTO `knowledge` VALUES (1, '壮族刺绣的色彩美学与文化内涵', '壮族刺绣偏爱强烈的对比色，如黑底红花、蓝底白花，这与壮族先民的自然崇拜和生命意识息息相关...', '历史文化', '文山州文化局', 328, '2026-02-15 08:28:43', '2026-02-16 09:15:43');
INSERT INTO `knowledge` VALUES (2, '零基础入门：平绣的基本针法与图解', '平绣是壮族刺绣中最基础的技法，要求针脚齐整、线条流畅。本文将通过图文并茂的方式教你起针与收针...', '技法知识', '农师傅', 512, '2026-02-15 08:28:43', '2026-02-15 08:28:43');
INSERT INTO `knowledge` VALUES (3, '中华人民共和国非物质文化遗产法', '为了继承和弘扬中华民族优秀传统文化，促进社会主义精神文明建设，制定本法...', '政策法规', '平台管理员', 89, '2026-02-15 08:28:43', '2026-02-16 10:23:28');
INSERT INTO `knowledge` VALUES (4, '如何正确清洗和保存手工刺绣作品？', '纯手工刺绣作品切忌机洗和大力揉搓，建议使用中性洗涤剂冷水轻柔按压，并在阴凉处平铺晾干...', '常见问题', '平台运营中心', 128, '2026-02-15 08:28:43', '2026-02-15 08:28:43');

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资讯标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '资讯内容',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '资讯作者',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '资讯表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of news
-- ----------------------------
INSERT INTO `news` VALUES (1, '2026年文山州壮族刺绣文化艺术节即将开幕', '据悉，本次艺术节将于文山市广场举行，届时将展出超过500件珍贵的刺绣老物件，并有非遗传承人现场技艺展示...', '文山日报', '2026-02-15 08:28:44', '2026-02-15 08:28:44');
INSERT INTO `news` VALUES (2, '喜讯！多位壮绣非遗传承人正式入驻本平台', '为了更好地利用数字化手段保护和传承非遗，农氏等5位州级传承人已开设专属账号，将定期发布作品与教程...', '平台运营部', '2026-02-15 08:28:44', '2026-02-15 08:28:44');
INSERT INTO `news` VALUES (3, '关于开展“最美壮绣”线上摄影评选活动的通知你的000', '请广大网友拿起镜头，记录下身边的壮锦与刺绣之美。一等奖将获得价值1000元的纯手工刺绣香囊一份...', '州文化馆', '2026-02-15 08:28:44', '2026-02-17 03:30:09');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户 ID',
  `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作类型：login/view/comment/collect等',
  `target_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '目标类型：artwork/knowledge/topic等',
  `target_id` bigint NULL DEFAULT NULL COMMENT '目标 ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_action`(`action` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `operation_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES (1, 1, 'login', 'system', NULL, '2026-02-15 08:28:44');
INSERT INTO `operation_log` VALUES (3, 4, 'collect', 'artwork', 2, '2026-02-15 08:28:44');

-- ----------------------------
-- Table structure for system_settings
-- ----------------------------
DROP TABLE IF EXISTS `system_settings`;
CREATE TABLE `system_settings`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `site_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '网站名称',
  `site_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '网站描述',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'logo URL',
  `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系邮箱',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_settings
-- ----------------------------
INSERT INTO `system_settings` VALUES (1, '文山壮族刺绣网站系统涉及', '传承和展示壮族非遗刺绣文化的综合性网络平台传承和展示壮族非遗刺绣文化的综合性网络平台传承和展示壮族非遗刺绣文化的综合性网络平台传承和展示壮族非遗刺绣文化的综合性网络平台', '', 'admin@zhuang-embroidery.com', '0876-1234567891', '2026-02-17 03:32:13');
INSERT INTO `system_settings` VALUES (2, '文山壮族刺绣英文版镜像站', 'Zhuang Embroidery Cultural Heritage Display Platform', NULL, 'en_support@zhuang-embroidery.com', '+86-0876-1234568', '2026-02-15 08:28:45');
INSERT INTO `system_settings` VALUES (3, '文山壮绣内部管理测试系统', '用于平台功能测试和数据审核的内部测试环境', NULL, 'test_admin@zhuang-embroidery.com', '13800138000', '2026-02-15 08:28:45');

-- ----------------------------
-- Table structure for topic
-- ----------------------------
DROP TABLE IF EXISTS `topic`;
CREATE TABLE `topic`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '话题标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '话题描述',
  `is_pinned` tinyint(1) NULL DEFAULT 0 COMMENT '是否置顶',
  `created_by` bigint NOT NULL COMMENT '创建者 ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `created_by`(`created_by` ASC) USING BTREE,
  INDEX `idx_is_pinned`(`is_pinned` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '话题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of topic
-- ----------------------------
INSERT INTO `topic` VALUES (1, '【探讨】如何让壮族刺绣走进年轻人的日常衣柜？', '大家觉得传统的刺绣图案放在卫衣、帆布鞋或者手机壳上好看吗？欢迎分享你们的创意！', 1, 1, '2026-02-15 08:28:43', '2026-02-15 08:28:43');
INSERT INTO `topic` VALUES (2, '寻找会“马尾绣”的老手艺人', '因为毕业设计需要，想请问文山地区哪里还有老人会正宗的马尾绣技法？', 1, 4, '2026-02-15 08:28:43', '2026-02-17 00:58:58');
INSERT INTO `topic` VALUES (3, '刺绣初学者求推荐好用的绣花线', '刚入坑，不知道买棉线还是丝线好，求各位前辈推荐品牌和色号。', 1, 3, '2026-02-15 08:28:43', '2026-02-17 03:31:25');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密存储）',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像 URL',
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '个人简介',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'user' COMMENT '角色：user/admin',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admintest', 'e10adc3949ba59abbe56e057f20f883e', 'admin11@zhuang-embroidery.com', '/images/avatars/admin.png', '系统管理员，负责平台日常维护与内容审核。', 'admin', '2026-02-15 08:28:43', '2026-02-17 00:13:07');
INSERT INTO `user` VALUES (4, 'design_student', 'e10adc3949ba59abbe56e057f20f883e', 'student@gmail.com', '/images/avatars/user2.png', '服装设计专业在读，正在研究将壮锦元素融入现代服饰。', 'user', '2026-02-15 08:28:43', '2026-02-16 17:08:54');
INSERT INTO `user` VALUES (5, '文山刺绣', '$2a$10$Y7.FfEpcBpc25Lluc4lSlef2fbOEoHTZAC8zVQjlf76o5Bel4Z0pS', '123456789@qq.com', NULL, '服装设计专业在读，正在研究将壮锦元素融入现代服饰。我觉得可以做成胸针或者帆布包的贴布', 'user', '2026-02-15 02:32:56', '2026-02-16 10:24:05');
INSERT INTO `user` VALUES (6, 'admin', '$2a$10$DOANoDPlfQ6QJSkEPKUeVu5bkJuju3sovODn19h6mA4ocB0l9cuOK', 'admin@qq.com', NULL, NULL, 'admin', '2026-02-16 10:48:19', '2026-02-16 18:48:28');

-- ----------------------------
-- Table structure for view_history
-- ----------------------------
DROP TABLE IF EXISTS `view_history`;
CREATE TABLE `view_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `content_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '内容类型：artwork/knowledge',
  `content_id` bigint NOT NULL COMMENT '内容 ID',
  `viewed_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_content_type`(`content_type` ASC) USING BTREE,
  INDEX `idx_viewed_at`(`viewed_at` ASC) USING BTREE,
  CONSTRAINT `view_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '浏览历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of view_history
-- ----------------------------
INSERT INTO `view_history` VALUES (3, 4, 'artwork', 2, '2026-02-15 08:28:44');
INSERT INTO `view_history` VALUES (5, 5, 'artwork', 1, '2026-02-16 08:23:40');
INSERT INTO `view_history` VALUES (6, 5, 'artwork', 1, '2026-02-16 08:28:44');
INSERT INTO `view_history` VALUES (7, 5, 'artwork', 1, '2026-02-16 08:28:49');
INSERT INTO `view_history` VALUES (8, 5, 'artwork', 1, '2026-02-16 08:31:34');
INSERT INTO `view_history` VALUES (9, 5, 'artwork', 1, '2026-02-16 08:31:40');
INSERT INTO `view_history` VALUES (10, 5, 'artwork', 1, '2026-02-16 08:32:01');
INSERT INTO `view_history` VALUES (11, 5, 'artwork', 1, '2026-02-16 08:32:13');
INSERT INTO `view_history` VALUES (12, 5, 'artwork', 1, '2026-02-16 08:32:15');
INSERT INTO `view_history` VALUES (13, 5, 'artwork', 1, '2026-02-16 08:32:19');
INSERT INTO `view_history` VALUES (14, 5, 'artwork', 1, '2026-02-16 08:32:23');
INSERT INTO `view_history` VALUES (15, 5, 'artwork', 1, '2026-02-16 08:32:25');
INSERT INTO `view_history` VALUES (16, 5, 'artwork', 1, '2026-02-16 08:32:27');
INSERT INTO `view_history` VALUES (17, 5, 'artwork', 1, '2026-02-16 08:32:30');
INSERT INTO `view_history` VALUES (18, 5, 'artwork', 3, '2026-02-16 08:49:12');
INSERT INTO `view_history` VALUES (19, 5, 'artwork', 2, '2026-02-16 08:50:12');
INSERT INTO `view_history` VALUES (20, 5, 'artwork', 2, '2026-02-16 08:50:19');
INSERT INTO `view_history` VALUES (21, 5, 'artwork', 1, '2026-02-16 09:01:33');
INSERT INTO `view_history` VALUES (22, 5, 'artwork', 2, '2026-02-16 09:04:30');
INSERT INTO `view_history` VALUES (23, 5, 'artwork', 1, '2026-02-16 09:14:35');
INSERT INTO `view_history` VALUES (24, 5, 'artwork', 1, '2026-02-16 10:22:56');
INSERT INTO `view_history` VALUES (25, 5, 'artwork', 1, '2026-02-16 10:23:17');
INSERT INTO `view_history` VALUES (26, 5, 'artwork', 1, '2026-02-16 10:24:08');
INSERT INTO `view_history` VALUES (27, 5, 'artwork', 1, '2026-02-16 10:25:34');
INSERT INTO `view_history` VALUES (28, 5, 'artwork', 1, '2026-02-16 10:28:18');
INSERT INTO `view_history` VALUES (29, 5, 'artwork', 3, '2026-02-16 10:35:37');
INSERT INTO `view_history` VALUES (30, 5, 'artwork', 3, '2026-02-16 10:35:40');
INSERT INTO `view_history` VALUES (31, 5, 'artwork', 3, '2026-02-16 10:35:50');
INSERT INTO `view_history` VALUES (32, 5, 'artwork', 3, '2026-02-16 10:35:56');
INSERT INTO `view_history` VALUES (33, 5, 'artwork', 3, '2026-02-16 10:36:49');
INSERT INTO `view_history` VALUES (34, 5, 'artwork', 3, '2026-02-16 10:37:29');
INSERT INTO `view_history` VALUES (35, 5, 'artwork', 3, '2026-02-16 10:37:46');
INSERT INTO `view_history` VALUES (36, 5, 'artwork', 3, '2026-02-16 10:38:06');
INSERT INTO `view_history` VALUES (37, 5, 'artwork', 1, '2026-02-16 10:43:50');
INSERT INTO `view_history` VALUES (38, 5, 'artwork', 1, '2026-02-16 10:43:59');
INSERT INTO `view_history` VALUES (39, 5, 'artwork', 1, '2026-02-16 10:44:40');
INSERT INTO `view_history` VALUES (40, 5, 'artwork', 1, '2026-02-16 10:44:45');
INSERT INTO `view_history` VALUES (41, 5, 'artwork', 1, '2026-02-16 10:45:48');
INSERT INTO `view_history` VALUES (42, 5, 'artwork', 1, '2026-02-16 10:45:52');
INSERT INTO `view_history` VALUES (43, 5, 'artwork', 1, '2026-02-16 10:45:53');
INSERT INTO `view_history` VALUES (44, 5, 'artwork', 1, '2026-02-16 10:46:14');
INSERT INTO `view_history` VALUES (45, 5, 'artwork', 2, '2026-02-16 10:46:22');
INSERT INTO `view_history` VALUES (46, 5, 'artwork', 2, '2026-02-16 10:46:25');
INSERT INTO `view_history` VALUES (47, 5, 'artwork', 2, '2026-02-16 10:46:36');
INSERT INTO `view_history` VALUES (48, 5, 'artwork', 2, '2026-02-16 10:47:04');

-- ----------------------------
-- Table structure for vote
-- ----------------------------
DROP TABLE IF EXISTS `vote`;
CREATE TABLE `vote`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '投票标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '投票描述',
  `options` json NULL COMMENT '投票选项列表',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'active' COMMENT '状态：active/closed',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `end_at` datetime NULL DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '投票表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vote
-- ----------------------------
INSERT INTO `vote` VALUES (1, '你最喜欢的壮族刺绣代表性图腾是哪一个？', '我们将根据投票结果，选取最高票的图腾推出文创周边。', '[\"太阳纹\", \"青蛙纹\", \"八角星纹\", \"花蝶纹\"]', 'active', '2026-02-15 08:28:43', '2026-12-31 23:59:59');
INSERT INTO `vote` VALUES (2, '下一期公益线上培训班，你想学什么技法？', '非遗传承人农老师将根据大家的意愿开启直播教学。', '[\"平绣基础\", \"挑绣进阶\", \"贴绣应用\"]', 'active', '2026-02-15 08:28:43', '2026-06-30 23:59:59');
INSERT INTO `vote` VALUES (3, '网站首页的主题色偏好调查', '为了给大家提供更好的视觉体验，请选择您喜欢的配色方案。', '[\"传统红黑\", \"现代简约白\", \"自然植物绿\"]', 'closed', '2026-02-15 08:28:43', '2025-12-31 23:59:59');

-- ----------------------------
-- Table structure for vote_record
-- ----------------------------
DROP TABLE IF EXISTS `vote_record`;
CREATE TABLE `vote_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `vote_id` bigint NOT NULL COMMENT '投票 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `selected_option` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '选择的选项',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投票时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_vote_user`(`vote_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_vote_id`(`vote_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_selected_option`(`selected_option` ASC) USING BTREE,
  CONSTRAINT `vote_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `vote_record_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '投票记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vote_record
-- ----------------------------
INSERT INTO `vote_record` VALUES (3, 1, 4, '太阳纹', '2026-02-15 08:28:43');
INSERT INTO `vote_record` VALUES (5, 1, 5, '花蝶纹', '2026-02-16 09:22:00');
INSERT INTO `vote_record` VALUES (7, 2, 5, '挑绣进阶', '2026-02-16 10:23:53');

SET FOREIGN_KEY_CHECKS = 1;
