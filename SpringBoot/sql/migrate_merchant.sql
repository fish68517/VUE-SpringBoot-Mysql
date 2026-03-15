-- 1) 扩展角色枚举，支持 merchant
ALTER TABLE `user`
MODIFY COLUMN `role` ENUM('user','merchant','admin') DEFAULT 'user';

-- 2) 菜品归属商家
ALTER TABLE `recipe`
ADD COLUMN IF NOT EXISTS `merchant_id` INT NULL;

ALTER TABLE `recipe`
ADD INDEX IF NOT EXISTS `idx_recipe_merchant_id` (`merchant_id`);

-- 3) 外键（如果已存在会报重复，可先 DROP 后 ADD）
ALTER TABLE `recipe`
ADD CONSTRAINT `fk_recipe_merchant`
FOREIGN KEY (`merchant_id`) REFERENCES `user` (`id`)
ON DELETE SET NULL ON UPDATE CASCADE;

-- 4) 订单取餐码
ALTER TABLE `order`
ADD COLUMN IF NOT EXISTS `pickup_code` VARCHAR(20) NULL;
