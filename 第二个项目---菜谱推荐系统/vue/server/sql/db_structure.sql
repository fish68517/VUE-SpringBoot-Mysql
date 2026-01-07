-- 修改用户表，添加BMI和口味偏好字段
ALTER TABLE `user`
ADD COLUMN `bmi` FLOAT DEFAULT NULL COMMENT 'BMI指数',
ADD COLUMN `taste_preference` VARCHAR(100) DEFAULT NULL COMMENT '口味偏好',
ADD COLUMN `cooking_skill` ENUM('初学者','进阶','专业') DEFAULT '初学者' COMMENT '烹饪技能水平',
ADD COLUMN `dietary_restriction` VARCHAR(100) DEFAULT NULL COMMENT '饮食限制';

-- 创建评论表
CREATE TABLE IF NOT EXISTS `comment` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `recipe_id` INT NOT NULL,
  `content` TEXT NOT NULL COMMENT '评论内容',
  `rating` INT DEFAULT 5 COMMENT '评分 1-5',
  `likes` INT DEFAULT 0 COMMENT '点赞数',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `recipe_id` (`recipe_id`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 创建评论点赞表
CREATE TABLE IF NOT EXISTS `comment_like` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `comment_id` INT NOT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_comment_like` (`user_id`, `comment_id`),
  KEY `comment_id` (`comment_id`),
  CONSTRAINT `comment_like_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `comment_like_ibfk_2` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 为recipe表添加烹饪方式、菜系和分类字段（如果还不存在）
ALTER TABLE `recipe` 
ADD COLUMN IF NOT EXISTS `cooking_method` VARCHAR(50) DEFAULT NULL COMMENT '烹饪方式',
ADD COLUMN IF NOT EXISTS `cuisine_type` VARCHAR(50) DEFAULT NULL COMMENT '菜系',
ADD COLUMN IF NOT EXISTS `food_category` ENUM('主食','甜点','小吃','汤品','饮品') DEFAULT NULL COMMENT '食物分类'; 