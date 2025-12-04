-- ============================================================================
-- 沈阳音乐节管理系统 - 数据库初始化脚本
-- ============================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS shenyang_music_festival 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE shenyang_music_festival;

-- ============================================================================
-- 用户相关表
-- ============================================================================

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    phone VARCHAR(20) UNIQUE NOT NULL COMMENT '手机号',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    nickname VARCHAR(100) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像URL',
    email VARCHAR(100) COMMENT '邮箱',
    real_name VARCHAR(100) COMMENT '真实姓名',
    id_number VARCHAR(18) UNIQUE COMMENT '身份证号（用于防黄牛）',
    is_real_name_verified BOOLEAN DEFAULT FALSE COMMENT '是否完成实名认证',
    points BIGINT DEFAULT 0 COMMENT '积分余额',
    is_blocked BOOLEAN DEFAULT FALSE COMMENT '是否被封禁',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_phone (phone),
    INDEX idx_id_number (id_number),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================================
-- 音乐节信息表
-- ============================================================================

-- 音乐节表
CREATE TABLE IF NOT EXISTS festivals (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '音乐节ID',
    name VARCHAR(200) NOT NULL COMMENT '音乐节名称',
    description TEXT COMMENT '音乐节描述',
    start_date DATETIME NOT NULL COMMENT '开始时间',
    end_date DATETIME NOT NULL COMMENT '结束时间',
    location VARCHAR(500) COMMENT '举办地点',
    poster_url VARCHAR(500) COMMENT '海报URL',
    status VARCHAR(50) DEFAULT 'ongoing' COMMENT '状态：ongoing/ended',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_start_date (start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐节表';

-- 艺人表
CREATE TABLE IF NOT EXISTS artists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '艺人ID',
    name VARCHAR(200) NOT NULL COMMENT '艺人名称',
    description TEXT COMMENT '艺人描述',
    image_url VARCHAR(500) COMMENT '艺人图片URL',
    is_local_band BOOLEAN DEFAULT FALSE COMMENT '是否为沈阳本土乐队',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_is_local_band (is_local_band)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='艺人表';

-- 演出日程表
CREATE TABLE IF NOT EXISTS schedules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日程ID',
    festival_id BIGINT NOT NULL COMMENT '音乐节ID',
    artist_id BIGINT NOT NULL COMMENT '艺人ID',
    stage_name VARCHAR(100) COMMENT '舞台名称',
    start_time DATETIME NOT NULL COMMENT '演出开始时间',
    end_time DATETIME NOT NULL COMMENT '演出结束时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (festival_id) REFERENCES festivals(id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES artists(id) ON DELETE CASCADE,
    INDEX idx_festival_id (festival_id),
    INDEX idx_artist_id (artist_id),
    INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='演出日程表';

-- ============================================================================
-- 购票相关表
-- ============================================================================

-- 票务场次表
CREATE TABLE IF NOT EXISTS ticket_sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '场次ID',
    festival_id BIGINT NOT NULL COMMENT '音乐节ID',
    name VARCHAR(200) NOT NULL COMMENT '场次名称',
    start_time DATETIME NOT NULL COMMENT '场次开始时间',
    end_time DATETIME NOT NULL COMMENT '场次结束时间',
    status VARCHAR(50) DEFAULT 'available' COMMENT '状态：available/sold_out/ended',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (festival_id) REFERENCES festivals(id) ON DELETE CASCADE,
    INDEX idx_festival_id (festival_id),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='票务场次表';

-- 分区表
CREATE TABLE IF NOT EXISTS ticket_zones (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分区ID',
    session_id BIGINT NOT NULL COMMENT '场次ID',
    name VARCHAR(100) NOT NULL COMMENT '分区名称（A区/B区/站区/VIP区）',
    total_capacity INT NOT NULL COMMENT '总容纳人数',
    sold_count INT DEFAULT 0 COMMENT '已售数量',
    price DECIMAL(10, 2) NOT NULL COMMENT '票价',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (session_id) REFERENCES ticket_sessions(id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分区表';

-- 电子票表
CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '电子票ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    session_id BIGINT NOT NULL COMMENT '场次ID',
    zone_id BIGINT NOT NULL COMMENT '分区ID',
    buyer_id_number VARCHAR(18) NOT NULL COMMENT '购票人身份证号',
    buyer_name VARCHAR(100) NOT NULL COMMENT '购票人姓名',
    qr_code VARCHAR(500) COMMENT '二维码URL',
    status VARCHAR(50) DEFAULT 'valid' COMMENT '状态：valid/used/expired',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES ticket_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (zone_id) REFERENCES ticket_zones(id) ON DELETE CASCADE,
    UNIQUE KEY uk_session_id_number (session_id, buyer_id_number),
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id),
    INDEX idx_zone_id (zone_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电子票表';

-- ============================================================================
-- 商品相关表
-- ============================================================================

-- 商品分类表
CREATE TABLE IF NOT EXISTS product_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    images JSON COMMENT '商品图片URL列表（JSON数组）',
    original_price DECIMAL(10, 2) NOT NULL COMMENT '原价',
    current_price DECIMAL(10, 2) NOT NULL COMMENT '现价',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存',
    specs JSON COMMENT '规格信息（JSON对象）',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否上架',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_id) REFERENCES product_categories(id) ON DELETE CASCADE,
    INDEX idx_category_id (category_id),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- ============================================================================
-- 订单相关表
-- ============================================================================

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_type VARCHAR(50) NOT NULL COMMENT '订单类型：ticket/product',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    status VARCHAR(50) DEFAULT 'paid' COMMENT '订单状态：paid/shipped/completed/cancelled',
    shipping_address VARCHAR(500) COMMENT '收货地址',
    tracking_number VARCHAR(100) COMMENT '物流单号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_order_type (order_type),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单项表
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单项ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10, 2) NOT NULL COMMENT '单价',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';

-- ============================================================================
-- 打卡任务相关表
-- ============================================================================

-- 打卡任务表
CREATE TABLE IF NOT EXISTS checkin_tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    name VARCHAR(200) NOT NULL COMMENT '任务名称',
    description TEXT COMMENT '任务描述',
    cover_image VARCHAR(500) COMMENT '任务封面图URL',
    points INT NOT NULL COMMENT '奖励积分',
    latitude DECIMAL(10, 8) NOT NULL COMMENT '地理位置纬度',
    longitude DECIMAL(11, 8) NOT NULL COMMENT '地理位置经度',
    radius INT NOT NULL COMMENT '地理围栏半径（米）',
    start_time DATETIME NOT NULL COMMENT '任务开始时间',
    end_time DATETIME NOT NULL COMMENT '任务结束时间',
    status VARCHAR(50) DEFAULT 'ongoing' COMMENT '状态：ongoing/ended',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打卡任务表';

-- 打卡记录表
CREATE TABLE IF NOT EXISTS checkin_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '打卡记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    photo VARCHAR(500) NOT NULL COMMENT '打卡照片URL',
    latitude DECIMAL(10, 8) NOT NULL COMMENT '打卡位置纬度',
    longitude DECIMAL(11, 8) NOT NULL COMMENT '打卡位置经度',
    status VARCHAR(50) DEFAULT 'pending' COMMENT '状态：pending/approved/rejected',
    reject_reason VARCHAR(500) COMMENT '拒绝原因',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES checkin_tasks(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_task (user_id, task_id),
    INDEX idx_user_id (user_id),
    INDEX idx_task_id (task_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打卡记录表';

-- ============================================================================
-- 积分相关表
-- ============================================================================

-- 积分商城商品表
CREATE TABLE IF NOT EXISTS points_mall (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    images JSON COMMENT '商品图片URL列表（JSON数组）',
    points_required INT NOT NULL COMMENT '所需积分',
    type VARCHAR(50) NOT NULL COMMENT '商品类型：entity/virtual',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存',
    countdown_end_time DATETIME COMMENT '倒计时结束时间',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否上架',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_type (type),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分商城商品表';

-- 积分兑换订单表
CREATE TABLE IF NOT EXISTS points_exchanges (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '兑换订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    mall_item_id BIGINT NOT NULL COMMENT '商城商品ID',
    points_used INT NOT NULL COMMENT '使用积分',
    status VARCHAR(50) DEFAULT 'pending' COMMENT '状态：pending/approved/shipped/completed/rejected',
    shipping_address VARCHAR(500) COMMENT '收货地址',
    tracking_number VARCHAR(100) COMMENT '物流单号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (mall_item_id) REFERENCES points_mall(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_mall_item_id (mall_item_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分兑换订单表';

-- 积分流水表
CREATE TABLE IF NOT EXISTS points_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '流水ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    change_amount INT NOT NULL COMMENT '变动积分',
    change_type VARCHAR(50) NOT NULL COMMENT '变动类型：checkin/purchase/exchange/manual_adjust',
    related_id BIGINT COMMENT '关联的任务/订单ID',
    description VARCHAR(500) COMMENT '描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_change_type (change_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分流水表';

-- ============================================================================
-- 内容相关表
-- ============================================================================

-- 文章表
CREATE TABLE IF NOT EXISTS articles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文章ID',
    title VARCHAR(300) NOT NULL COMMENT '文章标题',
    content LONGTEXT NOT NULL COMMENT '文章内容（富文本）',
    images JSON COMMENT '文章图片URL列表（JSON数组）',
    author VARCHAR(100) COMMENT '作者',
    type VARCHAR(50) COMMENT '文章类型：news/guide/food_recommendation',
    likes INT DEFAULT 0 COMMENT '点赞数',
    views INT DEFAULT 0 COMMENT '浏览数',
    is_published BOOLEAN DEFAULT TRUE COMMENT '是否发布',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_type (type),
    INDEX idx_is_published (is_published),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 文章评论表
CREATE TABLE IF NOT EXISTS article_comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    article_id BIGINT NOT NULL COMMENT '文章ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content VARCHAR(1000) NOT NULL COMMENT '评论内容',
    likes INT DEFAULT 0 COMMENT '点赞数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_article_id (article_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章评论表';

-- 用户收藏表
CREATE TABLE IF NOT EXISTS user_collections (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    article_id BIGINT NOT NULL COMMENT '文章ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_article (user_id, article_id),
    INDEX idx_user_id (user_id),
    INDEX idx_article_id (article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- 音乐节关注表
CREATE TABLE IF NOT EXISTS festival_follows (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关注ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    festival_id BIGINT NOT NULL COMMENT '音乐节ID',
    followed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (festival_id) REFERENCES festivals(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_festival (user_id, festival_id),
    INDEX idx_user_id (user_id),
    INDEX idx_festival_id (festival_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐节关注表';

-- ============================================================================
-- 其他表
-- ============================================================================

-- 天气信息表
CREATE TABLE IF NOT EXISTS weather_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '天气ID',
    festival_id BIGINT NOT NULL COMMENT '音乐节ID',
    date DATE NOT NULL COMMENT '日期',
    temperature INT COMMENT '温度（摄氏度）',
    humidity INT COMMENT '湿度（百分比）',
    wind_speed INT COMMENT '风速（km/h）',
    weather_description VARCHAR(100) COMMENT '天气描述',
    clothing_suggestion VARCHAR(500) COMMENT '穿衣建议',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (festival_id) REFERENCES festivals(id) ON DELETE CASCADE,
    UNIQUE KEY uk_festival_date (festival_id, date),
    INDEX idx_festival_id (festival_id),
    INDEX idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='天气信息表';

-- 交通信息表
CREATE TABLE IF NOT EXISTS transport_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '交通信息ID',
    festival_id BIGINT NOT NULL COMMENT '音乐节ID',
    type VARCHAR(50) NOT NULL COMMENT '交通类型：subway/bus/parking/guide',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    description TEXT COMMENT '描述',
    details JSON COMMENT '详细信息（JSON对象）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (festival_id) REFERENCES festivals(id) ON DELETE CASCADE,
    INDEX idx_festival_id (festival_id),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交通信息表';

-- ============================================================================
-- 索引优化总结
-- ============================================================================
-- 已创建的关键索引：
-- 1. 用户表：phone, id_number, created_at
-- 2. 票务表：user_id, session_id, zone_id, status, created_at
-- 3. 分区表：session_id, name
-- 4. 商品表：category_id, is_active, created_at
-- 5. 订单表：user_id, order_type, status, created_at
-- 6. 打卡记录表：user_id, task_id, status, created_at
-- 7. 积分流水表：user_id, change_type, created_at
-- 8. 文章表：type, is_published, created_at
-- 9. 唯一约束：
--    - users.phone, users.id_number
--    - tickets.session_id + buyer_id_number (防黄牛)
--    - checkin_records.user_id + task_id (一人一任务一次)
--    - user_collections.user_id + article_id
--    - festival_follows.user_id + festival_id
--    - weather_info.festival_id + date
-- ============================================================================
