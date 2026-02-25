-- 创建数据库
CREATE DATABASE IF NOT EXISTS tourism_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tourism_db;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    real_name VARCHAR(100) COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL DEFAULT 'tourist' COMMENT '角色: tourist 或 admin',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active 或 disabled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 景点表
CREATE TABLE IF NOT EXISTS attractions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '景点名称',
    description LONGTEXT COMMENT '景点描述',
    location VARCHAR(200) COMMENT '位置',
    ticket_price DECIMAL(10, 2) COMMENT '门票价格',
    opening_hours VARCHAR(100) COMMENT '营业时间',
    image_url VARCHAR(500) COMMENT '景点图片',
    is_guangzhou_special BOOLEAN DEFAULT FALSE COMMENT '是否为广州特色',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_guangzhou_special (is_guangzhou_special)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点表';

-- 景点标签表
CREATE TABLE IF NOT EXISTS attraction_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    attraction_id BIGINT NOT NULL COMMENT '景点ID',
    tag_name VARCHAR(50) NOT NULL COMMENT '标签名称',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (attraction_id) REFERENCES attractions(id) ON DELETE CASCADE,
    INDEX idx_attraction_id (attraction_id),
    INDEX idx_tag_name (tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点标签表';

-- 酒店表
CREATE TABLE IF NOT EXISTS hotels (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '酒店名称',
    location VARCHAR(200) COMMENT '位置',
    description LONGTEXT COMMENT '酒店描述',
    image_url VARCHAR(500) COMMENT '酒店图片',
    rating DECIMAL(3, 1) COMMENT '评分',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_location (location)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店表';

-- 酒店房间表
CREATE TABLE IF NOT EXISTS hotel_rooms (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hotel_id BIGINT NOT NULL COMMENT '酒店ID',
    room_type VARCHAR(50) NOT NULL COMMENT '房间类型',
    price_per_night DECIMAL(10, 2) NOT NULL COMMENT '每晚价格',
    quantity INT NOT NULL COMMENT '房间数量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE,
    INDEX idx_hotel_id (hotel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店房间表';

-- 旅游商品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    description LONGTEXT COMMENT '商品描述',
    price DECIMAL(10, 2) NOT NULL COMMENT '价格',
    stock INT NOT NULL COMMENT '库存',
    image_url VARCHAR(500) COMMENT '商品图片',
    category VARCHAR(50) COMMENT '分类',
    is_guangzhou_special BOOLEAN DEFAULT FALSE COMMENT '是否为广州特色',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_category (category),
    INDEX idx_guangzhou_special (is_guangzhou_special)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='旅游商品表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_price DECIMAL(10, 2) NOT NULL COMMENT '总价格',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending, confirmed, completed, cancelled',
    order_type VARCHAR(20) NOT NULL COMMENT '订单类型: attraction, hotel, product',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_order_number (order_number),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单项目表
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    item_type VARCHAR(20) NOT NULL COMMENT '项目类型: attraction, hotel, product',
    item_id BIGINT NOT NULL COMMENT '项目ID',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10, 2) NOT NULL COMMENT '单价',
    subtotal DECIMAL(10, 2) NOT NULL COMMENT '小计',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项目表';

-- 旅游路线表
CREATE TABLE IF NOT EXISTS routes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '路线名称',
    description LONGTEXT COMMENT '路线描述',
    duration_days INT NOT NULL COMMENT '天数',
    total_price DECIMAL(10, 2) COMMENT '总价格',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='旅游路线表';

-- 路线项目表
CREATE TABLE IF NOT EXISTS route_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    route_id BIGINT NOT NULL COMMENT '路线ID',
    day_number INT NOT NULL COMMENT '第几天',
    item_type VARCHAR(20) NOT NULL COMMENT '项目类型: attraction, hotel',
    item_id BIGINT NOT NULL COMMENT '项目ID',
    sequence INT NOT NULL COMMENT '顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE,
    INDEX idx_route_id (route_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路线项目表';

-- 留言表
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型: attraction, hotel',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    content LONGTEXT NOT NULL COMMENT '留言内容',
    rating INT COMMENT '评分: 1-5',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态: pending, approved, rejected',
    is_pinned BOOLEAN DEFAULT FALSE COMMENT '是否置顶',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_target_type_id (target_type, target_id),
    INDEX idx_status (status),
    INDEX idx_is_pinned (is_pinned)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='留言表';

-- 公告表
CREATE TABLE IF NOT EXISTS announcements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content LONGTEXT NOT NULL COMMENT '公告内容',
    created_by BIGINT COMMENT '创建者ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- 收藏表
CREATE TABLE IF NOT EXISTS favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型: attraction, hotel, product',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_target_type_id (target_type, target_id),
    UNIQUE KEY unique_favorite (user_id, target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 浏览历史表
CREATE TABLE IF NOT EXISTS browsing_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型: attraction, hotel, product',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浏览历史表';

-- 插入示例景点数据
INSERT INTO attractions (name, description, location, ticket_price, opening_hours, image_url, is_guangzhou_special) VALUES
('陈家祠', '陈家祠是广东现存规模最大、保存最完整的中国传统民居建筑群，是岭南建筑艺术的典范。', '广州市荔湾区中山七路', 10.00, '09:00-17:00', 'https://via.placeholder.com/300x200?text=陈家祠', true),
('珠江夜游', '乘坐游船欣赏珠江两岸的夜景，感受广州的现代魅力。', '广州市越秀区珠江', 80.00, '19:00-22:00', 'https://via.placeholder.com/300x200?text=珠江夜游', true),
('广州塔', '广州塔是中国第一高塔，可俯瞰整个广州城市风景。', '广州市海珠区阅江西路', 150.00, '09:00-23:00', 'https://via.placeholder.com/300x200?text=广州塔', true),
('越秀公园', '越秀公园是广州最大的城市公园，有五羊雕像等著名景观。', '广州市越秀区解放北路', 5.00, '06:00-21:00', 'https://via.placeholder.com/300x200?text=越秀公园', true),
('黄埔古港', '黄埔古港是广州对外贸易的重要港口，见证了广州的商业历史。', '广州市黄埔区黄埔东路', 20.00, '09:00-17:00', 'https://via.placeholder.com/300x200?text=黄埔古港', true),
('南沙湿地公园', '南沙湿地公园是广州最大的湿地公园，有丰富的野生动物。', '广州市南沙区万顷沙镇', 30.00, '08:00-18:00', 'https://via.placeholder.com/300x200?text=南沙湿地', false),
('白云山', '白云山是广州的城市绿肺，有多条登山步道和观景台。', '广州市白云区白云山', 5.00, '06:00-21:00', 'https://via.placeholder.com/300x200?text=白云山', false),
('长隆野生动物园', '长隆野生动物园是中国最大的野生动物园，有众多珍稀动物。', '广州市番禺区大石镇', 250.00, '09:30-18:00', 'https://via.placeholder.com/300x200?text=长隆野生动物园', false);

-- 插入示例标签数据
INSERT INTO attraction_tags (attraction_id, tag_name) VALUES
(1, '文化'),
(1, '历史'),
(2, '美食'),
(2, '夜景'),
(3, '现代'),
(3, '建筑'),
(4, '公园'),
(4, '休闲'),
(5, '历史'),
(5, '文化'),
(6, '自然'),
(6, '生态'),
(7, '自然'),
(7, '休闲'),
(8, '动物'),
(8, '家庭');

-- 插入示例酒店数据
INSERT INTO hotels (name, location, description, image_url, rating) VALUES
('广州白天鹅宾馆', '广州市越秀区环市东路', '五星级豪华酒店，位于珠江河畔，拥有优美的江景和完善的设施。', 'https://via.placeholder.com/300x200?text=白天鹅宾馆', 5.0),
('广州香格里拉大酒店', '广州市越秀区中山一路', '五星级国际酒店，提供高端服务和舒适的住宿环境。', 'https://via.placeholder.com/300x200?text=香格里拉', 4.8),
('广州花园酒店', '广州市越秀区中山一路', '四星级商务酒店，地理位置优越，靠近商业中心。', 'https://via.placeholder.com/300x200?text=花园酒店', 4.5),
('广州长隆酒店', '广州市番禺区大石镇', '四星级主题酒店，靠近长隆野生动物园，适合家庭旅游。', 'https://via.placeholder.com/300x200?text=长隆酒店', 4.6),
('广州南沙假日酒店', '广州市南沙区万顷沙镇', '三星级度假酒店，环境优美，靠近南沙湿地公园。', 'https://via.placeholder.com/300x200?text=南沙假日', 4.2),
('广州天河城市酒店', '广州市天河区天河路', '四星级商务酒店，靠近购物中心和商业区。', 'https://via.placeholder.com/300x200?text=天河城市', 4.4);

-- 插入示例酒店房间数据
INSERT INTO hotel_rooms (hotel_id, room_type, price_per_night, quantity) VALUES
(1, '豪华套房', 1500.00, 10),
(1, '标准间', 800.00, 30),
(1, '商务间', 600.00, 20),
(2, '豪华套房', 1200.00, 8),
(2, '标准间', 700.00, 25),
(2, '经济间', 500.00, 15),
(3, '标准间', 500.00, 40),
(3, '商务间', 400.00, 30),
(3, '经济间', 300.00, 20),
(4, '家庭房', 800.00, 15),
(4, '标准间', 600.00, 35),
(4, '经济间', 400.00, 25),
(5, '标准间', 400.00, 30),
(5, '经济间', 250.00, 40),
(6, '商务间', 450.00, 25),
(6, '标准间', 350.00, 35),
(6, '经济间', 250.00, 30);
