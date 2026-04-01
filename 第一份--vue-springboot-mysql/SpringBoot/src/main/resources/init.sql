-- 纳西族纹样数字化展示平台 - 数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS naxi_pattern_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE naxi_pattern_db;

-- ============================================
-- 创建表结构
-- ============================================

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_name VARCHAR(50) UNIQUE NOT NULL COMMENT '角色名称',
  description VARCHAR(255) COMMENT '角色描述',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户表
CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
  password VARCHAR(255) NOT NULL COMMENT '密码',
  email VARCHAR(100) UNIQUE NOT NULL COMMENT '邮箱',
  nickname VARCHAR(100) COMMENT '昵称',
  avatar_url VARCHAR(255) COMMENT '头像URL',
  bio TEXT COMMENT '个人简介',
  status ENUM('ACTIVE', 'DISABLED') DEFAULT 'ACTIVE' COMMENT '用户状态',
  role_id BIGINT COMMENT '角色ID',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (role_id) REFERENCES roles(id),
  INDEX idx_username (username),
  INDEX idx_email (email),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 权限表
CREATE TABLE IF NOT EXISTS permissions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL COMMENT '角色ID',
  permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (role_id) REFERENCES roles(id),
  UNIQUE KEY unique_permission (role_id, permission_name),
  INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 纹样表
CREATE TABLE IF NOT EXISTS patterns (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL COMMENT '纹样名称',
  category ENUM('七星纹', '东巴衍生纹', '日月纹', '云纹水纹') NOT NULL COMMENT '纹样分类',
  description TEXT COMMENT '纹样描述',
  cultural_background TEXT COMMENT '文化背景',
  image_url VARCHAR(255) NOT NULL COMMENT '图片URL',
  download_url VARCHAR(255) COMMENT '下载URL',
  application_scenarios TEXT COMMENT '应用场景',
  view_count INT DEFAULT 0 COMMENT '浏览次数',
  download_count INT DEFAULT 0 COMMENT '下载次数',
  collection_count INT DEFAULT 0 COMMENT '收藏次数',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_category (category),
  INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='纹样表';

-- 收藏表
CREATE TABLE IF NOT EXISTS collections (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  pattern_id BIGINT NOT NULL COMMENT '纹样ID',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (pattern_id) REFERENCES patterns(id),
  UNIQUE KEY unique_collection (user_id, pattern_id),
  INDEX idx_user_id (user_id),
  INDEX idx_pattern_id (pattern_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 评论表
CREATE TABLE IF NOT EXISTS comments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  pattern_id BIGINT NOT NULL COMMENT '纹样ID',
  content TEXT NOT NULL COMMENT '评论内容',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (pattern_id) REFERENCES patterns(id),
  INDEX idx_user_id (user_id),
  INDEX idx_pattern_id (pattern_id),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 提问表
CREATE TABLE IF NOT EXISTS questions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  pattern_id BIGINT NOT NULL COMMENT '纹样ID',
  title VARCHAR(200) NOT NULL COMMENT '提问标题',
  content TEXT NOT NULL COMMENT '提问内容',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (pattern_id) REFERENCES patterns(id),
  INDEX idx_user_id (user_id),
  INDEX idx_pattern_id (pattern_id),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提问表';

-- 原创作品表
CREATE TABLE IF NOT EXISTS creative_works (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  title VARCHAR(200) NOT NULL COMMENT '作品标题',
  description TEXT COMMENT '作品描述',
  image_url VARCHAR(255) NOT NULL COMMENT '作品图片URL',
  status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING' COMMENT '审核状态',
  like_count INT DEFAULT 0 COMMENT '点赞数',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (user_id) REFERENCES users(id),
  INDEX idx_user_id (user_id),
  INDEX idx_status (status),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='原创作品表';

-- 操作历史表
CREATE TABLE IF NOT EXISTS operation_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
  target_type VARCHAR(50) COMMENT '目标类型',
  target_id BIGINT COMMENT '目标ID',
  details TEXT COMMENT '操作详情',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (user_id) REFERENCES users(id),
  INDEX idx_user_id (user_id),
  INDEX idx_operation_type (operation_type),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作历史表';

-- 系统日志表
CREATE TABLE IF NOT EXISTS system_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  log_level ENUM('INFO', 'WARN', 'ERROR') DEFAULT 'INFO' COMMENT '日志级别',
  message TEXT NOT NULL COMMENT '日志消息',
  details TEXT COMMENT '日志详情',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_log_level (log_level),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';

-- 系统参数表
CREATE TABLE IF NOT EXISTS system_settings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  setting_key VARCHAR(100) UNIQUE NOT NULL COMMENT '参数键',
  setting_value TEXT NOT NULL COMMENT '参数值',
  description VARCHAR(255) COMMENT '参数描述',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_setting_key (setting_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统参数表';

-- 管理员操作日志表
CREATE TABLE IF NOT EXISTS admin_audit_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  admin_id BIGINT NOT NULL COMMENT '管理员ID',
  operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
  target_type VARCHAR(50) COMMENT '目标类型',
  target_id BIGINT COMMENT '目标ID',
  details TEXT COMMENT '操作详情',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (admin_id) REFERENCES users(id),
  INDEX idx_admin_id (admin_id),
  INDEX idx_operation_type (operation_type),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员操作日志表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入角色数据
INSERT INTO roles (role_name, description) VALUES
('SUPER_ADMIN', '超级管理员'),
('ADMIN', '管理员'),
('USER', '普通用户');

-- 为超级管理员角色分配权限
INSERT INTO permissions (role_id, permission_name) VALUES
((SELECT id FROM roles WHERE role_name = 'SUPER_ADMIN'), 'pattern_manage'),
((SELECT id FROM roles WHERE role_name = 'SUPER_ADMIN'), 'user_manage'),
((SELECT id FROM roles WHERE role_name = 'SUPER_ADMIN'), 'content_review'),
((SELECT id FROM roles WHERE role_name = 'SUPER_ADMIN'), 'system_manage'),
((SELECT id FROM roles WHERE role_name = 'SUPER_ADMIN'), 'role_manage'),
((SELECT id FROM roles WHERE role_name = 'SUPER_ADMIN'), 'audit_log_view');

-- 为管理员角色分配权限
INSERT INTO permissions (role_id, permission_name) VALUES
((SELECT id FROM roles WHERE role_name = 'ADMIN'), 'pattern_manage'),
((SELECT id FROM roles WHERE role_name = 'ADMIN'), 'user_manage'),
((SELECT id FROM roles WHERE role_name = 'ADMIN'), 'content_review'),
((SELECT id FROM roles WHERE role_name = 'ADMIN'), 'system_manage');

-- 为普通用户角色分配权限
INSERT INTO permissions (role_id, permission_name) VALUES
((SELECT id FROM roles WHERE role_name = 'USER'), 'pattern_view'),
((SELECT id FROM roles WHERE role_name = 'USER'), 'pattern_download'),
((SELECT id FROM roles WHERE role_name = 'USER'), 'collection_manage'),
((SELECT id FROM roles WHERE role_name = 'USER'), 'comment_publish'),
((SELECT id FROM roles WHERE role_name = 'USER'), 'question_publish'),
((SELECT id FROM roles WHERE role_name = 'USER'), 'work_upload');

-- 插入系统参数数据
INSERT INTO system_settings (setting_key, setting_value, description) VALUES
('carousel_interval', '5000', '轮播图切换间隔（毫秒）'),
('carousel_effect', 'fade', '轮播图切换效果'),
('max_upload_size', '10485760', '最大上传文件大小（字节）'),
('items_per_page', '12', '每页显示项目数'),
('platform_name', '纳西族纹样数字化展示平台', '平台名称'),
('platform_description', '展示和传播纳西族文化的轻量级Web应用系统', '平台描述');

-- 插入初始纹样数据（四类纹样分类）
INSERT INTO patterns (name, category, description, cultural_background, image_url, application_scenarios) VALUES
('七星纹样1', '七星纹', '七星纹样示例1', '七星纹代表北斗七星，在纳西文化中象征指引和方向', '/images/patterns/qixing1.jpg', '传统服饰、建筑装饰'),
('七星纹样2', '七星纹', '七星纹样示例2', '七星纹的变体形式', '/images/patterns/qixing2.jpg', '现代设计、文创产品'),
('东巴衍生纹样1', '东巴衍生纹', '东巴衍生纹样示例1', '东巴文化衍生的纹样，融合了象形文字特征', '/images/patterns/dongba1.jpg', '传统服饰、现代设计'),
('东巴衍生纹样2', '东巴衍生纹', '东巴衍生纹样示例2', '东巴衍生纹样的另一种形式', '/images/patterns/dongba2.jpg', '文创产品、装饰品'),
('日月纹样1', '日月纹', '日月纹样示例1', '日月纹象征阴阳平衡和时间循环', '/images/patterns/riyue1.jpg', '传统服饰、建筑'),
('日月纹样2', '日月纹', '日月纹样示例2', '日月纹的创意变体', '/images/patterns/riyue2.jpg', '现代艺术、设计'),
('云纹水纹样1', '云纹水纹', '云纹水纹样示例1', '云纹水纹代表自然元素，象征流动和变化', '/images/patterns/yunshui1.jpg', '传统服饰、装饰'),
('云纹水纹样2', '云纹水纹', '云纹水纹样示例2', '云纹水纹的组合形式', '/images/patterns/yunshui2.jpg', '现代设计、文创');

-- ============================================
-- 验证表创建
-- ============================================
SELECT '数据库初始化完成！' AS 初始化状态;
SHOW TABLES;
