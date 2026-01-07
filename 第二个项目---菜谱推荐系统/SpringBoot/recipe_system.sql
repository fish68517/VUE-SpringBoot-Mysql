-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: localhost    Database: recipe_system
-- ------------------------------------------------------
-- Server version	8.0.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `recipe_id` int DEFAULT NULL,
  `quantity` int DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `recipe_id` (`recipe_id`),
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (6,2,3,1,'2025-03-01 08:09:31'),(7,2,2,1,'2025-03-01 08:09:43'),(8,2,6,1,'2025-03-01 08:51:16');
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `sort_order` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'川菜','四川特色菜系',0),(2,'粤菜','广东特色菜系',0),(3,'苏菜','江苏特色菜系',0);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `recipe_id` int NOT NULL,
  `content` text COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `rating` int DEFAULT '5' COMMENT '评分 1-5',
  `likes` int DEFAULT '0' COMMENT '点赞数',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `recipe_id` (`recipe_id`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment_like`
--

DROP TABLE IF EXISTS `comment_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_like` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `comment_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_comment_like` (`user_id`,`comment_id`),
  KEY `comment_id` (`comment_id`),
  CONSTRAINT `comment_like_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `comment_like_ibfk_2` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_like`
--

LOCK TABLES `comment_like` WRITE;
/*!40000 ALTER TABLE `comment_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorite` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `recipe_id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_favorite` (`user_id`,`recipe_id`),
  KEY `recipe_id` (`recipe_id`),
  CONSTRAINT `favorite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `favorite_ibfk_2` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite`
--

LOCK TABLES `favorite` WRITE;
/*!40000 ALTER TABLE `favorite` DISABLE KEYS */;
/*!40000 ALTER TABLE `favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `order_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `status` enum('待付款','已付款','已完成','已取消') COLLATE utf8mb4_general_ci DEFAULT '待付款',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (1,2,'202403150001',128.00,'已完成','2025-02-16 10:22:50'),(2,2,'202403150002',88.00,'已取消','2025-02-16 10:22:50'),(3,2,'ORDER1739779080126',256.00,'已取消','2025-02-17 07:58:00'),(4,2,'ORDER1739779088690',256.00,'已取消','2025-02-17 07:58:08'),(5,2,'ORDER1739848122881',2.57,'已付款','2025-02-18 03:08:42');
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_detail`
--

DROP TABLE IF EXISTS `order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_detail` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `recipe_id` int DEFAULT NULL,
  `quantity` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `recipe_id` (`recipe_id`),
  CONSTRAINT `order_detail_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`),
  CONSTRAINT `order_detail_ibfk_2` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_detail`
--

LOCK TABLES `order_detail` WRITE;
/*!40000 ALTER TABLE `order_detail` DISABLE KEYS */;
INSERT INTO `order_detail` VALUES (1,1,1,1,48.00),(2,1,2,1,80.00),(3,2,3,2,44.00),(4,3,1,3,0.00),(5,3,3,3,0.00),(6,3,1,2,0.00),(7,4,1,3,0.00),(8,4,3,3,0.00),(9,4,1,2,0.00),(10,5,4,3,0.00),(11,5,1,1,0.00);
/*!40000 ALTER TABLE `order_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recipe`
--

DROP TABLE IF EXISTS `recipe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recipe` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category_id` int DEFAULT NULL,
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `ingredients` text COLLATE utf8mb4_general_ci NOT NULL,
  `steps` text COLLATE utf8mb4_general_ci NOT NULL,
  `image` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `cooking_time` int DEFAULT NULL COMMENT '烹饪时间(分钟)',
  `difficulty` enum('简单','中等','困难') COLLATE utf8mb4_general_ci DEFAULT '中等',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `Taste` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '口味',
  `cooking_method` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '烹饪方式',
  `cuisine_type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜系',
  `food_category` enum('主食','甜点','小吃','汤品','饮品','菜谱') COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '食物分类',
  `Price` double DEFAULT NULL COMMENT '价格',
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `recipe_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recipe`
--

LOCK TABLES `recipe` WRITE;
/*!40000 ALTER TABLE `recipe` DISABLE KEYS */;
INSERT INTO `recipe` VALUES (1,1,'麻婆豆腐','四川传统名菜，麻辣鲜香','豆腐 300g, 猪肉末 100g, 豆瓣酱 2勺, 花椒 适量, 葱花 适量','1. 豆腐切块\n2. 肉末爆香\n3. 加入豆瓣酱炒制\n4. 加入豆腐翻炒\n5. 最后撒上花椒粉和葱花','mapodoufu.jpg',20,'简单','2025-02-16 10:22:50','麻辣','炒','川菜','菜谱',NULL),(2,1,'宫保鸡丁','川菜经典，甜辣可口','鸡胸肉 200g, 花生 50g, 干辣椒 适量, 葱姜蒜 适量','1. 鸡肉切丁\n2. 爆香花生\n3. 炒制鸡丁\n4. 加入调味料\n5. 最后加入花生','gongbaojiding.jpg',25,'中等','2025-02-16 10:22:50','麻辣','炒','川菜','菜谱',NULL),(3,2,'白切鸡','粤式经典凉菜','整鸡 1只, 姜片 适量, 葱 适量, 料酒 适量','1. 鸡洗净\n2. 放入姜葱\n3. 煮至熟透\n4. 浸入冰水\n5. 切块装盘','baiqieji.jpg',40,'中等','2025-02-16 10:22:50','清淡','炒','粤菜','小吃',NULL),(4,3,'红烧狮子头','苏菜代表作','猪肉馅 500g, 豆腐 100g, 葱姜水 适量, 生抽老抽 适量','1. 调制肉馅\n2. 搓制丸子\n3. 煎制上色\n4. 炖煮收汁\n5. 装盘即可','hognshaoshizitou.jpg',50,'困难','2025-02-16 10:22:50','咸鲜','煮','粤菜','主食',NULL),(5,1,'麻婆豆腐','四川传统名菜','豆腐 300g, 猪肉','1. 豆腐切块 2. 肉末爆香 3. 加入豆瓣酱','hognshaoshizitou.jpg',20,'简单','2025-02-16 10:22:50','麻辣','炒','湘菜','汤品',NULL),(6,1,'宫保鸡丁','川菜经典，甜辣','鸡胸肉 200g, 花生','1. 鸡肉切丁 2. 爆香花生 3. 炒制鸡丁','hognshaoshizitou.jpg',25,'中等','2025-02-16 10:22:50','麻辣','炖','湘菜','主食',NULL),(7,2,'白切鸡','粤式经典凉菜','整鸡 1只, 姜片','1. 鸡洗净 2. 放入姜葱 3. 煮至熟透 4. 浸','hognshaoshizitou.jpg',40,'中等','2025-02-16 10:22:50','清淡','炒','鲁菜','饮品',NULL),(8,3,'红烧狮子头','苏菜代表作','猪肉馅 500g, 豆','1. 调制肉馅 2. 搓制丸子 3. 煎制上色','baiqieji.jpg',50,'困难','2025-02-16 10:22:50','咸鲜','蒸','鲁菜','甜点',NULL),(9,1,'鱼香肉丝','经典川菜','猪里脊 200g, 木耳 50g, 笋 50g','1. 猪里脊切丝 2. 木耳、笋切丝 3. 调鱼香汁','baiqieji.jpg',30,'中等','2025-02-17 02:00:00','咸鲜','炒','川菜','汤品',NULL),(10,2,'清蒸鲈鱼','粤菜经典','鲈鱼 1条, 姜, 葱','1. 鲈鱼处理干净 2. 放入姜葱 3. 蒸制','mapodoufu.jpg',25,'简单','2025-02-17 02:05:00','咸鲜','煮','粤菜','饮品',NULL),(11,3,'东坡肉','杭州名菜','五花肉 500g, 冰糖, 绍兴酒','1. 五花肉切块 2. 焯水 3. 加入调料炖煮','mapodoufu.jpg',120,'困难','2025-02-17 02:10:00','清淡','炖','湘菜','菜谱',NULL),(12,1,'水煮牛肉','川菜代表','牛肉 300g, 黄豆芽 200g, 青菜 100g','1. 牛肉切片腌制 2. 黄豆芽、青菜焯水 3. 煮制','hognshaoshizitou.jpg',45,'中等','2025-02-17 02:15:00','麻辣','蒸','鲁菜','主食',NULL),(13,2,'菠萝咕咾肉','粤菜经典','猪里脊 200g, 菠萝 100g, 青椒, 红椒','1. 猪里脊切块 2. 炸制 3. 调汁','hognshaoshizitou.jpg',35,'中等','2025-02-17 02:20:00','清淡','炒','川菜','小吃',NULL),(14,3,'叫花鸡','特色菜','土鸡 1只, 荷叶, 黄泥','1. 土鸡处理干净 2. 荷叶包裹 3. 黄泥封','jiaohuaji.jpg',180,'困难','2025-02-17 02:25:00','清淡','蒸','鲁菜','饮品',NULL),(15,1,'冰糖雪梨','清热润肺，止咳化痰','雪梨 1个, 冰糖 适量, 水 适量','1. 雪梨去皮去核，切成小块 2. 放入炖盅，加入冰糖和适量水 3. 隔水炖30分钟即可','bingtangxueli.jpg',NULL,'简单','2025-03-02 01:10:49','甜','炖','家常菜','饮品',NULL),(16,1,'柠檬蜂蜜水','美白养颜，补充维生素C','柠檬 1个, 蜂蜜 适量, 温水 适量','1. 柠檬切片 2. 放入杯中，加入温水 3. 调入蜂蜜即可','ningmenfengmishui.jpg',NULL,'简单','2025-03-02 01:10:49','酸甜','冲泡','家常菜','饮品',NULL),(17,1,'红枣桂圆茶','补气养血，暖身驱寒','红枣 5颗, 桂圆 5颗, 枸杞 适量, 红糖 适量','1. 红枣去核 2. 桂圆剥壳 3. 将红枣、桂圆、枸杞放入杯中 4. 加入红糖，冲入热水即可','hongzaoguiyuancha.jpg',NULL,'简单','2025-03-02 01:10:49','甜','冲泡','家常菜','饮品',NULL),(18,1,'鲜榨橙汁','补充维生素C，增强免疫力','橙子 2个','1. 橙子洗净，对半切开 2. 用榨汁机榨汁即可','xianzhachengzhi.jpg',NULL,'简单','2025-03-02 01:10:49','酸甜','榨汁','家常菜','饮品',NULL),(19,2,'西红柿鸡蛋汤','营养丰富，简单易做','西红柿 2个, 鸡蛋 2个, 葱花 适量','1. 西红柿切块 2. 鸡蛋打散 3. 炒西红柿 4. 加入水煮沸 5. 倒入鸡蛋液 6. 撒上葱花','xihongshijidantang.jpg',NULL,'简单','2025-03-02 01:10:49','鲜','煮','家常菜','汤品',NULL),(20,2,'紫菜蛋花汤','清淡爽口，补充碘','紫菜 适量, 鸡蛋 1个, 虾皮 适量, 葱花 适量','1. 紫菜撕碎 2. 鸡蛋打散 3. 水烧开，放入紫菜和虾皮 4. 倒入鸡蛋液 5. 撒上葱花','zicaidanhua.jpg',NULL,'简单','2025-03-02 01:10:49','鲜','煮','家常菜','汤品',NULL),(21,2,'冬瓜排骨汤','清热解暑，利尿消肿','冬瓜 500g, 排骨 300g, 姜 3片, 盐 适量','1. 排骨焯水 2. 冬瓜去皮切块 3. 将排骨、冬瓜和姜片放入锅中，加水煮沸 4. 转小火炖1小时，加盐调味即可','dongguapaigutang.jpg',NULL,'中等','2025-03-02 01:10:49','鲜','炖','家常菜','汤品',NULL),(22,2,'香菇鸡汤','滋补养身，增强免疫力','鸡 1只, 香菇 10朵, 姜 3片, 盐 适量','1. 鸡洗净切块 2. 香菇泡发 3. 将鸡块、香菇和姜片放入锅中，加水煮沸 4. 转小火炖1.5小时，加盐调味即可','xianggujitang.jpg',NULL,'中等','2025-03-02 01:10:49','鲜','炖','家常菜','汤品',NULL),(23,3,'凉拌黄瓜','清脆爽口，开胃解腻','黄瓜 2根, 蒜 2瓣, 香醋 2勺, 生抽 1勺, 香油 适量, 盐 适量','1. 黄瓜拍碎切段 2. 蒜切末 3. 将黄瓜、蒜末、香醋、生抽、香油和盐拌匀即可','liangbanhuanggua.jpg',NULL,'简单','2025-03-02 01:10:49','咸鲜','拌','家常菜','小吃',NULL),(24,3,'炸薯条','香脆可口，老少皆宜','土豆 2个, 盐 适量, 番茄酱 适量','1. 土豆去皮切条 2. 用水冲洗掉淀粉 3. 放入油锅中炸至金黄 4. 捞出沥油，撒上盐，搭配番茄酱即可','zhushutiao.jpg',NULL,'简单','2025-03-02 01:10:49','咸','炸','西式','小吃',NULL),(25,3,'手抓饼','香酥可口，方便快捷','手抓饼 1张, 鸡蛋 1个, 生菜 适量, 酱料 适量','1. 手抓饼放入锅中煎至两面金黄 2. 打入鸡蛋，摊匀 3. 放入生菜，涂上酱料 4. 卷起来即可','shouzhuabing.jpg',NULL,'简单','2025-03-02 01:10:49','咸香','煎','家常菜','小吃',NULL),(26,3,'章鱼小丸子','Q弹美味，风味独特','章鱼小丸子粉 150g, 章鱼 适量, 包菜 适量, 沙拉酱 适量, 海苔 适量, 木鱼花 适量','1. 章鱼切块，包菜切碎 2. 将章鱼小丸子粉加水调成糊状 3. 放入章鱼块和包菜碎 4. 用章鱼小丸子机制作，烤至金黄 5. 淋上沙拉酱、海苔和木鱼花即可','zhangyuxiaowanzi.jpg',NULL,'中等','2025-03-02 01:10:49','咸鲜','烤','日式','小吃',NULL),(31,1,'冰糖雪梨','清热润肺，止咳化痰','雪梨 1个, 冰糖 适量, 水 适量','1. 雪梨去皮去核，切成小块 2. 放入炖盅，加入冰糖和适量水 3. 隔水炖30分钟即可','bingtangxueli.jpg',NULL,'简单','2025-03-02 01:11:26','甜','炖','家常菜','饮品',NULL),(32,1,'柠檬蜂蜜水','美白养颜，补充维生素C','柠檬 1个, 蜂蜜 适量, 温水 适量','1. 柠檬切片 2. 放入杯中，加入温水 3. 调入蜂蜜即可','ningmenfengmishui.jpg',NULL,'简单','2025-03-02 01:11:26','酸甜','冲泡','家常菜','饮品',NULL),(33,1,'红枣桂圆茶','补气养血，暖身驱寒','红枣 5颗, 桂圆 5颗, 枸杞 适量, 红糖 适量','1. 红枣去核 2. 桂圆剥壳 3. 将红枣、桂圆、枸杞放入杯中 4. 加入红糖，冲入热水即可','hongzaoguiyuancha.jpg',NULL,'简单','2025-03-02 01:11:26','甜','冲泡','家常菜','饮品',NULL),(34,1,'鲜榨橙汁','补充维生素C，增强免疫力','橙子 2个','1. 橙子洗净，对半切开 2. 用榨汁机榨汁即可','xianzhachengzhi.jpg',NULL,'简单','2025-03-02 01:11:26','酸甜','榨汁','家常菜','饮品',NULL),(35,2,'西红柿鸡蛋汤','营养丰富，简单易做','西红柿 2个, 鸡蛋 2个, 葱花 适量','1. 西红柿切块 2. 鸡蛋打散 3. 炒西红柿 4. 加入水煮沸 5. 倒入鸡蛋液 6. 撒上葱花','xihongshijidantang.jpg',NULL,'简单','2025-03-02 01:11:27','鲜','煮','家常菜','汤品',NULL),(36,2,'紫菜蛋花汤','清淡爽口，补充碘','紫菜 适量, 鸡蛋 1个, 虾皮 适量, 葱花 适量','1. 紫菜撕碎 2. 鸡蛋打散 3. 水烧开，放入紫菜和虾皮 4. 倒入鸡蛋液 5. 撒上葱花','zicaidanhua.jpg',NULL,'简单','2025-03-02 01:11:27','鲜','煮','家常菜','汤品',NULL),(37,2,'冬瓜排骨汤','清热解暑，利尿消肿','冬瓜 500g, 排骨 300g, 姜 3片, 盐 适量','1. 排骨焯水 2. 冬瓜去皮切块 3. 将排骨、冬瓜和姜片放入锅中，加水煮沸 4. 转小火炖1小时，加盐调味即可','dongguapaigutang.jpg',NULL,'中等','2025-03-02 01:11:27','鲜','炖','家常菜','汤品',NULL),(38,2,'香菇鸡汤','滋补养身，增强免疫力','鸡 1只, 香菇 10朵, 姜 3片, 盐 适量','1. 鸡洗净切块 2. 香菇泡发 3. 将鸡块、香菇和姜片放入锅中，加水煮沸 4. 转小火炖1.5小时，加盐调味即可','xianggujitang.jpg',NULL,'中等','2025-03-02 01:11:27','鲜','炖','家常菜','汤品',NULL),(39,3,'凉拌黄瓜','清脆爽口，开胃解腻','黄瓜 2根, 蒜 2瓣, 香醋 2勺, 生抽 1勺, 香油 适量, 盐 适量','1. 黄瓜拍碎切段 2. 蒜切末 3. 将黄瓜、蒜末、香醋、生抽、香油和盐拌匀即可','liangbanhuanggua.jpg',NULL,'简单','2025-03-02 01:11:27','咸鲜','拌','家常菜','小吃',NULL),(40,3,'炸薯条','香脆可口，老少皆宜','土豆 2个, 盐 适量, 番茄酱 适量','1. 土豆去皮切条 2. 用水冲洗掉淀粉 3. 放入油锅中炸至金黄 4. 捞出沥油，撒上盐，搭配番茄酱即可','zhushutiao.jpg',NULL,'简单','2025-03-02 01:11:27','咸','炸','西式','小吃',NULL),(41,3,'手抓饼','香酥可口，方便快捷','手抓饼 1张, 鸡蛋 1个, 生菜 适量, 酱料 适量','1. 手抓饼放入锅中煎至两面金黄 2. 打入鸡蛋，摊匀 3. 放入生菜，涂上酱料 4. 卷起来即可','shouzhuabing.jpg',NULL,'简单','2025-03-02 01:11:27','咸香','煎','家常菜','小吃',NULL),(42,3,'章鱼小丸子','Q弹美味，风味独特','章鱼小丸子粉 150g, 章鱼 适量, 包菜 适量, 沙拉酱 适量, 海苔 适量, 木鱼花 适量','1. 章鱼切块，包菜切碎 2. 将章鱼小丸子粉加水调成糊状 3. 放入章鱼块和包菜碎 4. 用章鱼小丸子机制作，烤至金黄 5. 淋上沙拉酱、海苔和木鱼花即可','zhangyuxiaowanzi.jpg',NULL,'中等','2025-03-02 01:11:27','咸鲜','烤','日式','小吃',NULL),(43,2,'番茄鸡蛋面','简单快捷，营养丰富','面条 100g, 西红柿 1个, 鸡蛋 2个, 葱花 适量','1. 西红柿切块 2. 鸡蛋打散 3. 炒西红柿 4. 加入水煮沸 5. 放入面条煮熟 6. 倒入鸡蛋液 7. 撒上葱花','fanqiejidanmian.jpg',NULL,'简单','2025-03-02 01:11:27','鲜','煮','家常菜','主食',NULL),(44,2,'扬州炒饭','色香味俱全，营养均衡','米饭 200g, 鸡蛋 1个, 胡萝卜 适量, 青豆 适量, 玉米 适量, 火腿 适量','1. 鸡蛋炒散 2. 胡萝卜、青豆、玉米和火腿切丁 3. 将所有材料和米饭一起炒匀即可','yangzhouchaofan.jpg',NULL,'简单','2025-03-02 01:11:27','咸香','炒','家常菜','主食',NULL),(45,2,'肉夹馍','外酥里嫩，香气扑鼻','面粉 200g, 猪肉 200g, 青椒 适量','1. 面粉加水和成面团，擀成饼 2. 猪肉炖烂，切碎 3. 青椒切碎 4. 将肉和青椒夹入饼中即可','roujiamo.jpg',NULL,'中等','2025-03-02 01:11:27','咸香','烤','陕西菜','主食',NULL),(46,2,'饺子','皮薄馅大，美味可口','面粉 200g, 猪肉 200g, 白菜 适量, 韭菜 适量','1. 面粉加水和成面团 2. 猪肉和白菜、韭菜剁碎，拌匀 3. 包成饺子 4. 煮熟即可','jiaozi.jpg',NULL,'中等','2025-03-02 01:11:27','咸鲜','煮','家常菜','主食',NULL);
/*!40000 ALTER TABLE `recipe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `recipe_id` int DEFAULT NULL,
  `rating` int NOT NULL,
  `content` text COLLATE utf8mb4_general_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `recipe_id` (`recipe_id`),
  CONSTRAINT `review_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `review_ibfk_2` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`),
  CONSTRAINT `review_chk_1` CHECK (((`rating` >= 1) and (`rating` <= 5)))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (1,2,1,5,'麻婆豆腐很正宗，麻辣鲜香，很满意！','2025-02-16 10:22:50'),(2,2,2,4,'宫保鸡丁做得不错，就是有点偏甜。','2025-02-16 10:22:50'),(3,2,3,5,'白切鸡非常嫩滑，配上姜葱酱简直绝配！','2025-02-16 10:22:50');
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `nickname` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `avatar` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `role` enum('user','admin') COLLATE utf8mb4_general_ci DEFAULT 'user',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `bmi` float DEFAULT NULL COMMENT 'BMI指数',
  `taste_preference` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '口味偏好',
  `cooking_skill` enum('初学者','进阶','专业') COLLATE utf8mb4_general_ci DEFAULT '初学者' COMMENT '烹饪技能水平',
  `dietary_restriction` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '饮食限制',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','admin','管理员',NULL,'admin','2025-02-16 10:22:50','2025-02-16 10:25:32',NULL,NULL,'初学者',NULL),(2,'张三','123456','小张000000',NULL,'user','2025-02-16 10:22:50','2025-02-17 13:36:18',NULL,NULL,'初学者',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-02 21:10:45
