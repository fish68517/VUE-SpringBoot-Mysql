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
INSERT INTO `category` VALUES (1,'宸濊彍','鍥涘窛鐗硅壊鑿滅郴',0),(2,'绮よ彍','骞夸笢鐗硅壊鑿滅郴',0),(3,'鑻忚彍','姹熻嫃鐗硅壊鑿滅郴',0);
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
  `content` text COLLATE utf8mb4_general_ci NOT NULL COMMENT '璇勮鍐呭',
  `rating` int DEFAULT '5' COMMENT '璇勫垎 1-5',
  `likes` int DEFAULT '0' COMMENT '鐐硅禐鏁?,
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
  `pickup_code` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `status` enum('寰呬粯娆?,'宸蹭粯娆?,'宸插畬鎴?,'宸插彇娑?) COLLATE utf8mb4_general_ci DEFAULT '寰呬粯娆?,
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
INSERT INTO `order` VALUES (1,2,'202403150001',128.00,'宸插畬鎴?,'2025-02-16 10:22:50'),(2,2,'202403150002',88.00,'宸插彇娑?,'2025-02-16 10:22:50'),(3,2,'ORDER1739779080126',256.00,'宸插彇娑?,'2025-02-17 07:58:00'),(4,2,'ORDER1739779088690',256.00,'宸插彇娑?,'2025-02-17 07:58:08'),(5,2,'ORDER1739848122881',2.57,'宸蹭粯娆?,'2025-02-18 03:08:42');
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
  `merchant_id` int DEFAULT NULL,
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `ingredients` text COLLATE utf8mb4_general_ci NOT NULL,
  `steps` text COLLATE utf8mb4_general_ci NOT NULL,
  `image` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `cooking_time` int DEFAULT NULL COMMENT '鐑归オ鏃堕棿(鍒嗛挓)',
  `difficulty` enum('绠€鍗?,'涓瓑','鍥伴毦') COLLATE utf8mb4_general_ci DEFAULT '涓瓑',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `Taste` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '鍙ｅ懗',
  `cooking_method` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '鐑归オ鏂瑰紡',
  `cuisine_type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '鑿滅郴',
  `food_category` enum('涓婚','鐢滅偣','灏忓悆','姹ゅ搧','楗搧','鑿滆氨') COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '椋熺墿鍒嗙被',
  `Price` double DEFAULT NULL COMMENT '浠锋牸',
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  KEY `merchant_id` (`merchant_id`),
  CONSTRAINT `recipe_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `recipe_ibfk_2` FOREIGN KEY (`merchant_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recipe`
--

LOCK TABLES `recipe` WRITE;
/*!40000 ALTER TABLE `recipe` DISABLE KEYS */;
INSERT INTO `recipe` VALUES (1,1,'楹诲﹩璞嗚厫','鍥涘窛浼犵粺鍚嶈彍锛岄夯杈ｉ矞棣?,'璞嗚厫 300g, 鐚倝鏈?100g, 璞嗙摚閰?2鍕? 鑺辨 閫傞噺, 钁辫姳 閫傞噺','1. 璞嗚厫鍒囧潡\n2. 鑲夋湯鐖嗛\n3. 鍔犲叆璞嗙摚閰辩倰鍒禱n4. 鍔犲叆璞嗚厫缈荤倰\n5. 鏈€鍚庢拻涓婅姳妞掔矇鍜岃懕鑺?,'mapodoufu.jpg',20,'绠€鍗?,'2025-02-16 10:22:50','楹昏荆','鐐?,'宸濊彍','鑿滆氨',NULL),(2,1,'瀹繚楦′竵','宸濊彍缁忓吀锛岀敎杈ｅ彲鍙?,'楦¤兏鑲?200g, 鑺辩敓 50g, 骞茶荆妞?閫傞噺, 钁卞钂?閫傞噺','1. 楦¤倝鍒囦竵\n2. 鐖嗛鑺辩敓\n3. 鐐掑埗楦′竵\n4. 鍔犲叆璋冨懗鏂橽n5. 鏈€鍚庡姞鍏ヨ姳鐢?,'gongbaojiding.jpg',25,'涓瓑','2025-02-16 10:22:50','楹昏荆','鐐?,'宸濊彍','鑿滆氨',NULL),(3,2,'鐧藉垏楦?,'绮ゅ紡缁忓吀鍑夎彍','鏁撮浮 1鍙? 濮滅墖 閫傞噺, 钁?閫傞噺, 鏂欓厭 閫傞噺','1. 楦℃礂鍑€\n2. 鏀惧叆濮滆懕\n3. 鐓嚦鐔熼€廫n4. 娴稿叆鍐版按\n5. 鍒囧潡瑁呯洏','baiqieji.jpg',40,'涓瓑','2025-02-16 10:22:50','娓呮贰','鐐?,'绮よ彍','灏忓悆',NULL),(4,3,'绾㈢儳鐙瓙澶?,'鑻忚彍浠ｈ〃浣?,'鐚倝棣?500g, 璞嗚厫 100g, 钁卞姘?閫傞噺, 鐢熸娊鑰佹娊 閫傞噺','1. 璋冨埗鑲夐\n2. 鎼撳埗涓稿瓙\n3. 鐓庡埗涓婅壊\n4. 鐐栫叜鏀舵眮\n5. 瑁呯洏鍗冲彲','hognshaoshizitou.jpg',50,'鍥伴毦','2025-02-16 10:22:50','鍜搁矞','鐓?,'绮よ彍','涓婚',NULL),(5,1,'楹诲﹩璞嗚厫','鍥涘窛浼犵粺鍚嶈彍','璞嗚厫 300g, 鐚倝','1. 璞嗚厫鍒囧潡 2. 鑲夋湯鐖嗛 3. 鍔犲叆璞嗙摚閰?,'hognshaoshizitou.jpg',20,'绠€鍗?,'2025-02-16 10:22:50','楹昏荆','鐐?,'婀樿彍','姹ゅ搧',NULL),(6,1,'瀹繚楦′竵','宸濊彍缁忓吀锛岀敎杈?,'楦¤兏鑲?200g, 鑺辩敓','1. 楦¤倝鍒囦竵 2. 鐖嗛鑺辩敓 3. 鐐掑埗楦′竵','hognshaoshizitou.jpg',25,'涓瓑','2025-02-16 10:22:50','楹昏荆','鐐?,'婀樿彍','涓婚',NULL),(7,2,'鐧藉垏楦?,'绮ゅ紡缁忓吀鍑夎彍','鏁撮浮 1鍙? 濮滅墖','1. 楦℃礂鍑€ 2. 鏀惧叆濮滆懕 3. 鐓嚦鐔熼€?4. 娴?,'hognshaoshizitou.jpg',40,'涓瓑','2025-02-16 10:22:50','娓呮贰','鐐?,'椴佽彍','楗搧',NULL),(8,3,'绾㈢儳鐙瓙澶?,'鑻忚彍浠ｈ〃浣?,'鐚倝棣?500g, 璞?,'1. 璋冨埗鑲夐 2. 鎼撳埗涓稿瓙 3. 鐓庡埗涓婅壊','baiqieji.jpg',50,'鍥伴毦','2025-02-16 10:22:50','鍜搁矞','钂?,'椴佽彍','鐢滅偣',NULL),(9,1,'楸奸鑲変笣','缁忓吀宸濊彍','鐚噷鑴?200g, 鏈ㄨ€?50g, 绗?50g','1. 鐚噷鑴婂垏涓?2. 鏈ㄨ€炽€佺瑡鍒囦笣 3. 璋冮奔棣欐眮','baiqieji.jpg',30,'涓瓑','2025-02-17 02:00:00','鍜搁矞','鐐?,'宸濊彍','姹ゅ搧',NULL),(10,2,'娓呰捀椴堥奔','绮よ彍缁忓吀','椴堥奔 1鏉? 濮? 钁?,'1. 椴堥奔澶勭悊骞插噣 2. 鏀惧叆濮滆懕 3. 钂稿埗','mapodoufu.jpg',25,'绠€鍗?,'2025-02-17 02:05:00','鍜搁矞','鐓?,'绮よ彍','楗搧',NULL),(11,3,'涓滃潯鑲?,'鏉窞鍚嶈彍','浜旇姳鑲?500g, 鍐扮硸, 缁嶅叴閰?,'1. 浜旇姳鑲夊垏鍧?2. 鐒按 3. 鍔犲叆璋冩枡鐐栫叜','mapodoufu.jpg',120,'鍥伴毦','2025-02-17 02:10:00','娓呮贰','鐐?,'婀樿彍','鑿滆氨',NULL),(12,1,'姘寸叜鐗涜倝','宸濊彍浠ｈ〃','鐗涜倝 300g, 榛勮眴鑺?200g, 闈掕彍 100g','1. 鐗涜倝鍒囩墖鑵屽埗 2. 榛勮眴鑺姐€侀潚鑿滅劘姘?3. 鐓埗','hognshaoshizitou.jpg',45,'涓瓑','2025-02-17 02:15:00','楹昏荆','钂?,'椴佽彍','涓婚',NULL),(13,2,'鑿犺悵鍜曞捑鑲?,'绮よ彍缁忓吀','鐚噷鑴?200g, 鑿犺悵 100g, 闈掓, 绾㈡','1. 鐚噷鑴婂垏鍧?2. 鐐稿埗 3. 璋冩眮','hognshaoshizitou.jpg',35,'涓瓑','2025-02-17 02:20:00','娓呮贰','鐐?,'宸濊彍','灏忓悆',NULL),(14,3,'鍙姳楦?,'鐗硅壊鑿?,'鍦熼浮 1鍙? 鑽峰彾, 榛勬偿','1. 鍦熼浮澶勭悊骞插噣 2. 鑽峰彾鍖呰９ 3. 榛勬偿灏?,'jiaohuaji.jpg',180,'鍥伴毦','2025-02-17 02:25:00','娓呮贰','钂?,'椴佽彍','楗搧',NULL),(15,1,'鍐扮硸闆ⅷ','娓呯儹娑﹁偤锛屾鍜冲寲鐥?,'闆ⅷ 1涓? 鍐扮硸 閫傞噺, 姘?閫傞噺','1. 闆ⅷ鍘荤毊鍘绘牳锛屽垏鎴愬皬鍧?2. 鏀惧叆鐐栫泤锛屽姞鍏ュ啺绯栧拰閫傞噺姘?3. 闅旀按鐐?0鍒嗛挓鍗冲彲','bingtangxueli.jpg',NULL,'绠€鍗?,'2025-03-02 01:10:49','鐢?,'鐐?,'瀹跺父鑿?,'楗搧',NULL),(16,1,'鏌犳铚傝湝姘?,'缇庣櫧鍏婚锛岃ˉ鍏呯淮鐢熺礌C','鏌犳 1涓? 铚傝湝 閫傞噺, 娓╂按 閫傞噺','1. 鏌犳鍒囩墖 2. 鏀惧叆鏉腑锛屽姞鍏ユ俯姘?3. 璋冨叆铚傝湝鍗冲彲','ningmenfengmishui.jpg',NULL,'绠€鍗?,'2025-03-02 01:10:49','閰哥敎','鍐叉场','瀹跺父鑿?,'楗搧',NULL),(17,1,'绾㈡灒妗傚渾鑼?,'琛ユ皵鍏昏锛屾殩韬┍瀵?,'绾㈡灒 5棰? 妗傚渾 5棰? 鏋告潪 閫傞噺, 绾㈢硸 閫傞噺','1. 绾㈡灒鍘绘牳 2. 妗傚渾鍓ュ３ 3. 灏嗙孩鏋ｃ€佹鍦嗐€佹灨鏉炴斁鍏ユ澂涓?4. 鍔犲叆绾㈢硸锛屽啿鍏ョ儹姘村嵆鍙?,'hongzaoguiyuancha.jpg',NULL,'绠€鍗?,'2025-03-02 01:10:49','鐢?,'鍐叉场','瀹跺父鑿?,'楗搧',NULL),(18,1,'椴滄Θ姗欐眮','琛ュ厖缁寸敓绱燙锛屽寮哄厤鐤姏','姗欏瓙 2涓?,'1. 姗欏瓙娲楀噣锛屽鍗婂垏寮€ 2. 鐢ㄦΘ姹佹満姒ㄦ眮鍗冲彲','xianzhachengzhi.jpg',NULL,'绠€鍗?,'2025-03-02 01:10:49','閰哥敎','姒ㄦ眮','瀹跺父鑿?,'楗搧',NULL),(19,2,'瑗跨孩鏌块浮铔嬫堡','钀ュ吇涓板瘜锛岀畝鍗曟槗鍋?,'瑗跨孩鏌?2涓? 楦¤泲 2涓? 钁辫姳 閫傞噺','1. 瑗跨孩鏌垮垏鍧?2. 楦¤泲鎵撴暎 3. 鐐掕タ绾㈡熆 4. 鍔犲叆姘寸叜娌?5. 鍊掑叆楦¤泲娑?6. 鎾掍笂钁辫姳','xihongshijidantang.jpg',NULL,'绠€鍗?,'2025-03-02 01:10:49','椴?,'鐓?,'瀹跺父鑿?,'姹ゅ搧',NULL),(20,2,'绱彍铔嬭姳姹?,'娓呮贰鐖藉彛锛岃ˉ鍏呯','绱彍 閫傞噺, 楦¤泲 1涓? 铏剧毊 閫傞噺, 钁辫姳 閫傞噺','1. 绱彍鎾曠 2. 楦¤泲鎵撴暎 3. 姘寸儳寮€锛屾斁鍏ョ传鑿滃拰铏剧毊 4. 鍊掑叆楦¤泲娑?5. 鎾掍笂钁辫姳','zicaidanhua.jpg',NULL,'绠€鍗?,'2025-03-02 01:10:49','椴?,'鐓?,'瀹跺父鑿?,'姹ゅ搧',NULL),(21,2,'鍐摐鎺掗姹?,'娓呯儹瑙ｆ殤锛屽埄灏挎秷鑲?,'鍐摐 500g, 鎺掗 300g, 濮?3鐗? 鐩?閫傞噺','1. 鎺掗鐒按 2. 鍐摐鍘荤毊鍒囧潡 3. 灏嗘帓楠ㄣ€佸啲鐡滃拰濮滅墖鏀惧叆閿呬腑锛屽姞姘寸叜娌?4. 杞皬鐏倴1灏忔椂锛屽姞鐩愯皟鍛冲嵆鍙?,'dongguapaigutang.jpg',NULL,'涓瓑','2025-03-02 01:10:49','椴?,'鐐?,'瀹跺父鑿?,'姹ゅ搧',NULL),(22,2,'棣欒弴楦℃堡','婊嬭ˉ鍏昏韩锛屽寮哄厤鐤姏','楦?1鍙? 棣欒弴 10鏈? 濮?3鐗? 鐩?閫傞噺','1. 楦℃礂鍑€鍒囧潡 2. 棣欒弴娉″彂 3. 灏嗛浮鍧椼€侀鑿囧拰濮滅墖鏀惧叆閿呬腑锛屽姞姘寸叜娌?4. 杞皬鐏倴1.5灏忔椂锛屽姞鐩愯皟鍛冲嵆鍙?,'xianggujitang.jpg',NULL,'涓瓑','2025-03-02 01:10:49','椴?,'鐐?,'瀹跺父鑿?,'姹ゅ搧',NULL),(23,3,'鍑夋媽榛勭摐','娓呰剢鐖藉彛锛屽紑鑳冭В鑵?,'榛勭摐 2鏍? 钂?2鐡? 棣欓唻 2鍕? 鐢熸娊 1鍕? 棣欐补 閫傞噺, 鐩?閫傞噺','1. 榛勭摐鎷嶇鍒囨 2. 钂滃垏鏈?3. 灏嗛粍鐡溿€佽挏鏈€侀閱嬨€佺敓鎶姐€侀娌瑰拰鐩愭媽鍖€鍗冲彲','liangbanhuanggua.jpg',NULL,'绠€鍗?,'2025-03-02 01:10:49','鍜搁矞','鎷?,'瀹跺父鑿?,'灏忓悆',NULL),(24,3,'鐐歌柉鏉?,'棣欒剢鍙彛锛岃€佸皯鐨嗗疁','鍦熻眴 2涓? 鐩?閫傞噺, 鐣寗閰?閫傞噺','1. 鍦熻眴鍘荤毊鍒囨潯 2. 鐢ㄦ按鍐叉礂鎺夋穩绮?3. 鏀惧叆娌归攨涓偢鑷抽噾榛?4. 鎹炲嚭娌ユ补锛屾拻涓婄洂锛屾惌閰嶇暘鑼勯叡鍗冲彲','zhushutiao.jpg',NULL,'绠€鍗?,'2025-03-02 01:10:49','鍜?,'鐐?,'瑗垮紡','灏忓悆',NULL),(25,3,'鎵嬫姄楗?,'棣欓叆鍙彛锛屾柟渚垮揩鎹?,'鎵嬫姄楗?1寮? 楦¤泲 1涓? 鐢熻彍 閫傞噺, 閰辨枡 閫傞噺','1. 鎵嬫姄楗兼斁鍏ラ攨涓厧鑷充袱闈㈤噾榛?2. 鎵撳叆楦¤泲锛屾憡鍖€ 3. 鏀惧叆鐢熻彍锛屾秱涓婇叡鏂?4. 鍗疯捣鏉ュ嵆鍙?,'shouzhuabing.jpg',NULL,'绠€鍗?,'2025-03-02 01:10:49','鍜搁','鐓?,'瀹跺父鑿?,'灏忓悆',NULL),(26,3,'绔犻奔灏忎父瀛?,'Q寮圭編鍛筹紝椋庡懗鐙壒','绔犻奔灏忎父瀛愮矇 150g, 绔犻奔 閫傞噺, 鍖呰彍 閫傞噺, 娌欐媺閰?閫傞噺, 娴疯嫈 閫傞噺, 鏈ㄩ奔鑺?閫傞噺','1. 绔犻奔鍒囧潡锛屽寘鑿滃垏纰?2. 灏嗙珷楸煎皬涓稿瓙绮夊姞姘磋皟鎴愮硦鐘?3. 鏀惧叆绔犻奔鍧楀拰鍖呰彍纰?4. 鐢ㄧ珷楸煎皬涓稿瓙鏈哄埗浣滐紝鐑よ嚦閲戦粍 5. 娣嬩笂娌欐媺閰便€佹捣鑻斿拰鏈ㄩ奔鑺卞嵆鍙?,'zhangyuxiaowanzi.jpg',NULL,'涓瓑','2025-03-02 01:10:49','鍜搁矞','鐑?,'鏃ュ紡','灏忓悆',NULL),(31,1,'鍐扮硸闆ⅷ','娓呯儹娑﹁偤锛屾鍜冲寲鐥?,'闆ⅷ 1涓? 鍐扮硸 閫傞噺, 姘?閫傞噺','1. 闆ⅷ鍘荤毊鍘绘牳锛屽垏鎴愬皬鍧?2. 鏀惧叆鐐栫泤锛屽姞鍏ュ啺绯栧拰閫傞噺姘?3. 闅旀按鐐?0鍒嗛挓鍗冲彲','bingtangxueli.jpg',NULL,'绠€鍗?,'2025-03-02 01:11:26','鐢?,'鐐?,'瀹跺父鑿?,'楗搧',NULL),(32,1,'鏌犳铚傝湝姘?,'缇庣櫧鍏婚锛岃ˉ鍏呯淮鐢熺礌C','鏌犳 1涓? 铚傝湝 閫傞噺, 娓╂按 閫傞噺','1. 鏌犳鍒囩墖 2. 鏀惧叆鏉腑锛屽姞鍏ユ俯姘?3. 璋冨叆铚傝湝鍗冲彲','ningmenfengmishui.jpg',NULL,'绠€鍗?,'2025-03-02 01:11:26','閰哥敎','鍐叉场','瀹跺父鑿?,'楗搧',NULL),(33,1,'绾㈡灒妗傚渾鑼?,'琛ユ皵鍏昏锛屾殩韬┍瀵?,'绾㈡灒 5棰? 妗傚渾 5棰? 鏋告潪 閫傞噺, 绾㈢硸 閫傞噺','1. 绾㈡灒鍘绘牳 2. 妗傚渾鍓ュ３ 3. 灏嗙孩鏋ｃ€佹鍦嗐€佹灨鏉炴斁鍏ユ澂涓?4. 鍔犲叆绾㈢硸锛屽啿鍏ョ儹姘村嵆鍙?,'hongzaoguiyuancha.jpg',NULL,'绠€鍗?,'2025-03-02 01:11:26','鐢?,'鍐叉场','瀹跺父鑿?,'楗搧',NULL),(34,1,'椴滄Θ姗欐眮','琛ュ厖缁寸敓绱燙锛屽寮哄厤鐤姏','姗欏瓙 2涓?,'1. 姗欏瓙娲楀噣锛屽鍗婂垏寮€ 2. 鐢ㄦΘ姹佹満姒ㄦ眮鍗冲彲','xianzhachengzhi.jpg',NULL,'绠€鍗?,'2025-03-02 01:11:26','閰哥敎','姒ㄦ眮','瀹跺父鑿?,'楗搧',NULL),(35,2,'瑗跨孩鏌块浮铔嬫堡','钀ュ吇涓板瘜锛岀畝鍗曟槗鍋?,'瑗跨孩鏌?2涓? 楦¤泲 2涓? 钁辫姳 閫傞噺','1. 瑗跨孩鏌垮垏鍧?2. 楦¤泲鎵撴暎 3. 鐐掕タ绾㈡熆 4. 鍔犲叆姘寸叜娌?5. 鍊掑叆楦¤泲娑?6. 鎾掍笂钁辫姳','xihongshijidantang.jpg',NULL,'绠€鍗?,'2025-03-02 01:11:27','椴?,'鐓?,'瀹跺父鑿?,'姹ゅ搧',NULL),(36,2,'绱彍铔嬭姳姹?,'娓呮贰鐖藉彛锛岃ˉ鍏呯','绱彍 閫傞噺, 楦¤泲 1涓? 铏剧毊 閫傞噺, 钁辫姳 閫傞噺','1. 绱彍鎾曠 2. 楦¤泲鎵撴暎 3. 姘寸儳寮€锛屾斁鍏ョ传鑿滃拰铏剧毊 4. 鍊掑叆楦¤泲娑?5. 鎾掍笂钁辫姳','zicaidanhua.jpg',NULL,'绠€鍗?,'2025-03-02 01:11:27','椴?,'鐓?,'瀹跺父鑿?,'姹ゅ搧',NULL),(37,2,'鍐摐鎺掗姹?,'娓呯儹瑙ｆ殤锛屽埄灏挎秷鑲?,'鍐摐 500g, 鎺掗 300g, 濮?3鐗? 鐩?閫傞噺','1. 鎺掗鐒按 2. 鍐摐鍘荤毊鍒囧潡 3. 灏嗘帓楠ㄣ€佸啲鐡滃拰濮滅墖鏀惧叆閿呬腑锛屽姞姘寸叜娌?4. 杞皬鐏倴1灏忔椂锛屽姞鐩愯皟鍛冲嵆鍙?,'dongguapaigutang.jpg',NULL,'涓瓑','2025-03-02 01:11:27','椴?,'鐐?,'瀹跺父鑿?,'姹ゅ搧',NULL),(38,2,'棣欒弴楦℃堡','婊嬭ˉ鍏昏韩锛屽寮哄厤鐤姏','楦?1鍙? 棣欒弴 10鏈? 濮?3鐗? 鐩?閫傞噺','1. 楦℃礂鍑€鍒囧潡 2. 棣欒弴娉″彂 3. 灏嗛浮鍧椼€侀鑿囧拰濮滅墖鏀惧叆閿呬腑锛屽姞姘寸叜娌?4. 杞皬鐏倴1.5灏忔椂锛屽姞鐩愯皟鍛冲嵆鍙?,'xianggujitang.jpg',NULL,'涓瓑','2025-03-02 01:11:27','椴?,'鐐?,'瀹跺父鑿?,'姹ゅ搧',NULL),(39,3,'鍑夋媽榛勭摐','娓呰剢鐖藉彛锛屽紑鑳冭В鑵?,'榛勭摐 2鏍? 钂?2鐡? 棣欓唻 2鍕? 鐢熸娊 1鍕? 棣欐补 閫傞噺, 鐩?閫傞噺','1. 榛勭摐鎷嶇鍒囨 2. 钂滃垏鏈?3. 灏嗛粍鐡溿€佽挏鏈€侀閱嬨€佺敓鎶姐€侀娌瑰拰鐩愭媽鍖€鍗冲彲','liangbanhuanggua.jpg',NULL,'绠€鍗?,'2025-03-02 01:11:27','鍜搁矞','鎷?,'瀹跺父鑿?,'灏忓悆',NULL),(40,3,'鐐歌柉鏉?,'棣欒剢鍙彛锛岃€佸皯鐨嗗疁','鍦熻眴 2涓? 鐩?閫傞噺, 鐣寗閰?閫傞噺','1. 鍦熻眴鍘荤毊鍒囨潯 2. 鐢ㄦ按鍐叉礂鎺夋穩绮?3. 鏀惧叆娌归攨涓偢鑷抽噾榛?4. 鎹炲嚭娌ユ补锛屾拻涓婄洂锛屾惌閰嶇暘鑼勯叡鍗冲彲','zhushutiao.jpg',NULL,'绠€鍗?,'2025-03-02 01:11:27','鍜?,'鐐?,'瑗垮紡','灏忓悆',NULL),(41,3,'鎵嬫姄楗?,'棣欓叆鍙彛锛屾柟渚垮揩鎹?,'鎵嬫姄楗?1寮? 楦¤泲 1涓? 鐢熻彍 閫傞噺, 閰辨枡 閫傞噺','1. 鎵嬫姄楗兼斁鍏ラ攨涓厧鑷充袱闈㈤噾榛?2. 鎵撳叆楦¤泲锛屾憡鍖€ 3. 鏀惧叆鐢熻彍锛屾秱涓婇叡鏂?4. 鍗疯捣鏉ュ嵆鍙?,'shouzhuabing.jpg',NULL,'绠€鍗?,'2025-03-02 01:11:27','鍜搁','鐓?,'瀹跺父鑿?,'灏忓悆',NULL),(42,3,'绔犻奔灏忎父瀛?,'Q寮圭編鍛筹紝椋庡懗鐙壒','绔犻奔灏忎父瀛愮矇 150g, 绔犻奔 閫傞噺, 鍖呰彍 閫傞噺, 娌欐媺閰?閫傞噺, 娴疯嫈 閫傞噺, 鏈ㄩ奔鑺?閫傞噺','1. 绔犻奔鍒囧潡锛屽寘鑿滃垏纰?2. 灏嗙珷楸煎皬涓稿瓙绮夊姞姘磋皟鎴愮硦鐘?3. 鏀惧叆绔犻奔鍧楀拰鍖呰彍纰?4. 鐢ㄧ珷楸煎皬涓稿瓙鏈哄埗浣滐紝鐑よ嚦閲戦粍 5. 娣嬩笂娌欐媺閰便€佹捣鑻斿拰鏈ㄩ奔鑺卞嵆鍙?,'zhangyuxiaowanzi.jpg',NULL,'涓瓑','2025-03-02 01:11:27','鍜搁矞','鐑?,'鏃ュ紡','灏忓悆',NULL),(43,2,'鐣寗楦¤泲闈?,'绠€鍗曞揩鎹凤紝钀ュ吇涓板瘜','闈㈡潯 100g, 瑗跨孩鏌?1涓? 楦¤泲 2涓? 钁辫姳 閫傞噺','1. 瑗跨孩鏌垮垏鍧?2. 楦¤泲鎵撴暎 3. 鐐掕タ绾㈡熆 4. 鍔犲叆姘寸叜娌?5. 鏀惧叆闈㈡潯鐓啛 6. 鍊掑叆楦¤泲娑?7. 鎾掍笂钁辫姳','fanqiejidanmian.jpg',NULL,'绠€鍗?,'2025-03-02 01:11:27','椴?,'鐓?,'瀹跺父鑿?,'涓婚',NULL),(44,2,'鎵窞鐐掗キ','鑹查鍛充勘鍏紝钀ュ吇鍧囪　','绫抽キ 200g, 楦¤泲 1涓? 鑳¤悵鍗?閫傞噺, 闈掕眴 閫傞噺, 鐜夌背 閫傞噺, 鐏吙 閫傞噺','1. 楦¤泲鐐掓暎 2. 鑳¤悵鍗溿€侀潚璞嗐€佺帀绫冲拰鐏吙鍒囦竵 3. 灏嗘墍鏈夋潗鏂欏拰绫抽キ涓€璧风倰鍖€鍗冲彲','yangzhouchaofan.jpg',NULL,'绠€鍗?,'2025-03-02 01:11:27','鍜搁','鐐?,'瀹跺父鑿?,'涓婚',NULL),(45,2,'鑲夊す棣?,'澶栭叆閲屽锛岄姘旀墤榧?,'闈㈢矇 200g, 鐚倝 200g, 闈掓 閫傞噺','1. 闈㈢矇鍔犳按鍜屾垚闈㈠洟锛屾搥鎴愰ゼ 2. 鐚倝鐐栫儌锛屽垏纰?3. 闈掓鍒囩 4. 灏嗚倝鍜岄潚妞掑す鍏ラゼ涓嵆鍙?,'roujiamo.jpg',NULL,'涓瓑','2025-03-02 01:11:27','鍜搁','鐑?,'闄曡タ鑿?,'涓婚',NULL),(46,2,'楗哄瓙','鐨杽棣呭ぇ锛岀編鍛冲彲鍙?,'闈㈢矇 200g, 鐚倝 200g, 鐧借彍 閫傞噺, 闊彍 閫傞噺','1. 闈㈢矇鍔犳按鍜屾垚闈㈠洟 2. 鐚倝鍜岀櫧鑿溿€侀煭鑿滃墎纰庯紝鎷屽寑 3. 鍖呮垚楗哄瓙 4. 鐓啛鍗冲彲','jiaozi.jpg',NULL,'涓瓑','2025-03-02 01:11:27','鍜搁矞','鐓?,'瀹跺父鑿?,'涓婚',NULL);
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
INSERT INTO `review` VALUES (1,2,1,5,'楹诲﹩璞嗚厫寰堟瀹楋紝楹昏荆椴滈锛屽緢婊℃剰锛?,'2025-02-16 10:22:50'),(2,2,2,4,'瀹繚楦′竵鍋氬緱涓嶉敊锛屽氨鏄湁鐐瑰亸鐢溿€?,'2025-02-16 10:22:50'),(3,2,3,5,'鐧藉垏楦￠潪甯稿婊戯紝閰嶄笂濮滆懕閰辩畝鐩寸粷閰嶏紒','2025-02-16 10:22:50');
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
  `role` enum('user','merchant','admin') COLLATE utf8mb4_general_ci DEFAULT 'user',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `bmi` float DEFAULT NULL COMMENT 'BMI鎸囨暟',
  `taste_preference` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '鍙ｅ懗鍋忓ソ',
  `cooking_skill` enum('鍒濆鑰?,'杩涢樁','涓撲笟') COLLATE utf8mb4_general_ci DEFAULT '鍒濆鑰? COMMENT '鐑归オ鎶€鑳芥按骞?,
  `dietary_restriction` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '楗闄愬埗',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','admin','绠＄悊鍛?,NULL,'admin','2025-02-16 10:22:50','2025-02-16 10:25:32',NULL,NULL,'鍒濆鑰?,NULL),(2,'寮犱笁','123456','灏忓紶000000',NULL,'user','2025-02-16 10:22:50','2025-02-17 13:36:18',NULL,NULL,'鍒濆鑰?,NULL);
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

