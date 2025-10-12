-- 推荐使用 InnoDB 存储引擎，因为它支持事务和外键约束。
-- SET default_storage_engine=InnoDB;

-- 创建并切换到数据库（根据需要修改数据库名）
CREATE DATABASE IF NOT EXISTS `warehouse_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `warehouse_db`;

-- =============================================
-- 1. 部门表
-- 用于管理组织架构 [cite: 25]。
-- 对应 Web 端的 "部门管理" 功能 [cite: 35]。
-- =============================================
CREATE TABLE `departments` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL COMMENT '部门名称',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_name` (`name`)
) COMMENT='部门信息表';

-- =============================================
-- 2. 角色表
-- 定义用户角色及其权限。
-- 系统预设了系统管理员、仓库管理员、操作员等角色 [cite: 12]。
-- =============================================
CREATE TABLE `roles` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称 (例如: 系统管理员, 仓库管理员)',
  UNIQUE KEY `uk_role_name` (`role_name`)
) COMMENT='用户角色表';

-- =============================================
-- 3. 用户表
-- 存储用户账户信息。
-- 支持用户登录、修改密码和用户管理功能 [cite: 23, 25]。
-- =============================================
CREATE TABLE `users` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL COMMENT '登录用户名',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '加密后的密码',
  `full_name` VARCHAR(100) COMMENT '用户全名',
  `role_id` INT UNSIGNED NOT NULL COMMENT '关联角色表的外键',
  `department_id` INT UNSIGNED COMMENT '关联部门表的外键',
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '标记用户账户是否启用',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_department_id` (`department_id`),
  CONSTRAINT `fk_users_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_users_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL
) COMMENT='系统用户账户表';

-- =============================================
-- 4. 产品表 (商品表)
-- 存储仓库中所有货品的基础信息。
-- 是库存查询、出入库管理的基础。
-- =============================================
CREATE TABLE `products` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `sku` VARCHAR(100) NOT NULL COMMENT 'SKU (Stock Keeping Unit), 产品的唯一标识符',
  `name` VARCHAR(200) NOT NULL COMMENT '产品名称',
  `description` TEXT COMMENT '产品详细描述',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_sku` (`sku`),
  KEY `idx_name` (`name`)
) COMMENT='产品主数据表';

-- =============================================
-- 5. 入库单表
-- 管理入库任务，通常由Excel批量导入创建 [cite: 25]。
-- 这是一个入库批次的头信息。
-- =============================================
CREATE TABLE `inbound_orders` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `order_number` VARCHAR(50) NOT NULL COMMENT '入库单的唯一编号',
  `created_by_user_id` INT UNSIGNED NOT NULL COMMENT '创建该订单的用户 (仓库管理员) [cite: 15]',
  `status` ENUM('待处理', '处理中', '已完成') NOT NULL DEFAULT '待处理' COMMENT '入库单状态',
  `notes` TEXT COMMENT '额外备注',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_order_number` (`order_number`),
  KEY `idx_created_by_user_id` (`created_by_user_id`),
  CONSTRAINT `fk_inbound_orders_user` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) COMMENT='入库单 (任务) 表';

-- =============================================
-- 6. 库存表
-- 系统的核心表，代表了实际的库存。每条记录都是一个特定批次的产品。
-- 二维码将唯一标识此表中的一条记录，用于所有扫码操作 [cite: 5]。
-- =============================================
CREATE TABLE `inventory` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `product_id` INT UNSIGNED NOT NULL COMMENT '关联产品表的外键',
  `batch_code` VARCHAR(100) NOT NULL COMMENT '批次唯一编码, 用于生成二维码',
  `inbound_order_id` INT UNSIGNED COMMENT '此批次所属的入库单',
  `quantity` INT NOT NULL DEFAULT 0 COMMENT '该批次的当前库存数量',
  `received_at` DATETIME COMMENT '批次被实际扫码入库的时间',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_batch_code` (`batch_code`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_inbound_order_id` (`inbound_order_id`),
  CONSTRAINT `fk_inventory_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_inventory_inbound_order` FOREIGN KEY (`inbound_order_id`) REFERENCES `inbound_orders` (`id`) ON DELETE SET NULL
) COMMENT='库存及批次表';

-- =============================================
-- 7. 异动日志表
-- 记录每一次库存变动（入库、出库、调整），用于审计和报表。
-- App的扫码操作会填充此表，为数据统计提供依据 [cite: 25]。
-- =============================================
CREATE TABLE `transaction_logs` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `inventory_id` INT UNSIGNED NOT NULL COMMENT '关联的库存批次ID',
  `user_id` INT UNSIGNED NOT NULL COMMENT '执行此操作的用户 (操作员) [cite: 18]',
  `type` ENUM('入库', '出库', '调整') NOT NULL COMMENT '异动类型',
  `quantity_change` INT NOT NULL COMMENT '库存变化数量 (+表示入库, -表示出库)',
  `quantity_after_transaction` INT NOT NULL COMMENT '本次异动后, 批次的库存数量',
  `notes` VARCHAR(255) COMMENT '可选备注, 例如: 盘点调整原因',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `idx_inventory_id` (`inventory_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_logs_inventory` FOREIGN KEY (`inventory_id`) REFERENCES `inventory` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_logs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) COMMENT='库存异动日志表';

-- =============================================
-- 模拟数据插入脚本
-- =============================================

-- 禁用外键检查，以便我们可以按任意顺序插入数据，最后再启用它。
SET FOREIGN_KEY_CHECKS=0;

-- 清空现有数据 (可选)
TRUNCATE TABLE `transaction_logs`;
TRUNCATE TABLE `inventory`;
TRUNCATE TABLE `inbound_orders`;
TRUNCATE TABLE `users`;
TRUNCATE TABLE `products`;
TRUNCATE TABLE `roles`;
TRUNCATE TABLE `departments`;


-- 1. 插入部门表 (`departments`) 数据
-- 对应 Web 端的 "部门管理" 功能。
-- 用于管理组织架构。
INSERT INTO `departments` (`id`, `name`) VALUES
(1, '行政部'),
(2, '仓储部'),
(3, '技术支持部'),
(4, '采购部');

-- 2. 插入角色表 (`roles`) 数据
-- 系统预设了系统管理员、仓库管理员、操作员等角色。
INSERT INTO `roles` (`id`, `role_name`) VALUES
(1, '系统管理员'),
(2, '仓库管理员'),
(3, '操作员');

-- 3. 插入用户表 (`users`) 数据
-- 支持用户登录、修改密码和用户管理功能。
-- 密码字段 `password_hash` 在这里使用了明文，实际项目中应存储加密后的哈希值。
INSERT INTO `users` (`id`, `username`, `password_hash`, `full_name`, `role_id`, `department_id`, `is_active`) VALUES
(1, 'admin', '123456', '张管理', 1, 1, TRUE),
(2, 'manager_wh', '123456', '李经理', 2, 2, TRUE),
(3, 'operator_a', '123456', '王操作员', 3, 2, TRUE),
(4, 'operator_b', '123456', '赵操作员', 3, 2, TRUE),
(5, 'disabled_user', '123456', '孙禁用', 3, 2, FALSE);

-- 4. 插入产品表 (`products`) 数据
INSERT INTO `products` (`id`, `sku`, `name`, `description`) VALUES
(101, 'SKU-A001', '智能降噪蓝牙耳机', '支持主动降噪，提供沉浸式音频体验，24小时超长待机。'),
(102, 'SKU-B002', '高速电吹风', '负离子护发，11万转高速马达，快速干发不伤发。'),
(103, 'SKU-C003', '便携式电动牙刷', '声波震动技术，多种清洁模式，IPX7级防水。'),
(104, 'SKU-D004', '家用智能监控摄像头', '1080P高清画质，支持夜视和双向语音通话，AI人形侦测。');

-- 5. 插入入库单表 (`inbound_orders`) 数据
-- 管理入库任务，通常由Excel批量导入创建。
INSERT INTO `inbound_orders` (`id`, `order_number`, `created_by_user_id`, `status`, `notes`) VALUES
(201, 'IN-20251011-001', 2, '已完成', '首批产品入库'),
(202, 'IN-20251012-001', 2, '处理中', '供应商补货'),
(203, 'IN-20251012-002', 2, '待处理', '新品待入库');

-- 6. 插入库存表 (`inventory`) 数据
-- 二维码将唯一标识此表中的一条记录。
-- `batch_code` 是用于生成二维码的唯一编码。
INSERT INTO `inventory` (`id`, `product_id`, `batch_code`, `inbound_order_id`, `quantity`, `received_at`) VALUES
(1001, 101, 'BAT-20251011-001', 201, 200, '2025-10-11 10:30:00'),
(1002, 102, 'BAT-20251011-002', 201, 150, '2025-10-11 11:00:00'),
(1003, 103, 'BAT-20251011-003', 201, 300, '2025-10-11 11:45:00'),
(1004, 101, 'BAT-20251012-001', 202, 90, '2025-10-12 09:00:00'), -- 这个批次在后续日志中有出库记录
(1005, 104, 'BAT-20251012-002', 202, 120, '2025-10-12 09:30:00');

-- 7. 插入异动日志表 (`transaction_logs`) 数据
-- App的扫码操作会填充此表。
-- 记录了库存的每一次变动。
INSERT INTO `transaction_logs` (`id`, `inventory_id`, `user_id`, `type`, `quantity_change`, `quantity_after_transaction`, `notes`) VALUES
-- 对应入库单 IN-20251011-001 的入库操作记录
(1, 1001, 3, '入库', 200, 200, '初始入库'),
(2, 1002, 3, '入库', 150, 150, '初始入库'),
(3, 1003, 4, '入库', 300, 300, '初始入库'),

-- 对应入库单 IN-20251012-001 的入库操作记录
(4, 1004, 3, '入库', 100, 100, '初始入库'), -- 初始入了100个
(5, 1005, 4, '入库', 120, 120, '初始入库'),

-- 模拟一次出库操作
(6, 1004, 4, '出库', -10, 90, '销售订单 SO-20251012-A05'), -- 从批次1004中出库10个，剩余90

-- 模拟一次库存调整操作
(7, 1002, 2, '调整', -1, 149, '盘点时发现一个包装破损'); -- 盘点调整，批次1002库存减1

-- 启用外键检查，确保数据完整性。
SET FOREIGN_KEY_CHECKS=1;

-- 在 `transaction_logs` 表插入数据后，需要手动更新 `inventory` 表中对应批次的当前数量 `quantity`
-- 以确保数据的一致性。
UPDATE `inventory` SET `quantity` = 90 WHERE `id` = 1004;
UPDATE `inventory` SET `quantity` = 149 WHERE `id` = 1002;