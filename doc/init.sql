-- 创建数据库
CREATE DATABASE IF NOT EXISTS zhuang_embroidery CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE zhuang_embroidery;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像 URL',
    bio TEXT COMMENT '个人简介',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色：user/admin',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 作品表
CREATE TABLE IF NOT EXISTS artwork (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '作品名称',
    description TEXT COMMENT '作品描述',
    category VARCHAR(50) COMMENT '分类：日常生活类/节日母题类/针法风格类',
    image_url VARCHAR(255) COMMENT '作品图片 URL',
    creator VARCHAR(100) COMMENT '创作者名称',
    technique VARCHAR(100) COMMENT '刺绣技法',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft/approved/rejected/offline',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    collect_count INT DEFAULT 0 COMMENT '收藏次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作品表';

-- 知识表
CREATE TABLE IF NOT EXISTS knowledge (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '文章标题',
    content LONGTEXT COMMENT '文章内容',
    category VARCHAR(50) COMMENT '分类：技法知识/历史文化/政策法规/常见问题',
    author VARCHAR(100) COMMENT '作者',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识表';

-- 评论表
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL COMMENT '评论内容',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    artwork_id BIGINT COMMENT '作品 ID',
    topic_id BIGINT COMMENT '话题 ID',
    parent_id BIGINT COMMENT '父评论 ID（用于回复）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (artwork_id) REFERENCES artwork(id),
    INDEX idx_artwork_id (artwork_id),
    INDEX idx_topic_id (topic_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 话题表
CREATE TABLE IF NOT EXISTS topic (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '话题标题',
    description TEXT COMMENT '话题描述',
    is_pinned BOOLEAN DEFAULT FALSE COMMENT '是否置顶',
    created_by BIGINT NOT NULL COMMENT '创建者 ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (created_by) REFERENCES user(id),
    INDEX idx_is_pinned (is_pinned),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='话题表';

-- 投票表
CREATE TABLE IF NOT EXISTS vote (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '投票标题',
    description TEXT COMMENT '投票描述',
    options JSON COMMENT '投票选项列表',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active/closed',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    end_at DATETIME COMMENT '结束时间',
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投票表';

-- 投票记录表
CREATE TABLE IF NOT EXISTS vote_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    vote_id BIGINT NOT NULL COMMENT '投票 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    selected_option VARCHAR(255) COMMENT '选择的选项',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '投票时间',
    FOREIGN KEY (vote_id) REFERENCES vote(id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    INDEX idx_vote_id (vote_id),
    INDEX idx_user_id (user_id),
    UNIQUE KEY unique_vote_user (vote_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投票记录表';

-- 反馈表
CREATE TABLE IF NOT EXISTS feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '用户 ID',
    content TEXT NOT NULL COMMENT '反馈内容',
    email VARCHAR(100) COMMENT '联系邮箱',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending/processed',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈表';

-- 浏览历史表
CREATE TABLE IF NOT EXISTS view_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    content_type VARCHAR(50) COMMENT '内容类型：artwork/knowledge',
    content_id BIGINT NOT NULL COMMENT '内容 ID',
    viewed_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    INDEX idx_user_id (user_id),
    INDEX idx_content_type (content_type),
    INDEX idx_viewed_at (viewed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浏览历史表';

-- 收藏表
CREATE TABLE IF NOT EXISTS collection (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    artwork_id BIGINT NOT NULL COMMENT '作品 ID',
    collected_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (artwork_id) REFERENCES artwork(id),
    INDEX idx_user_id (user_id),
    INDEX idx_artwork_id (artwork_id),
    UNIQUE KEY unique_user_artwork (user_id, artwork_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '用户 ID',
    action VARCHAR(50) COMMENT '操作类型：login/view/comment/collect等',
    target_type VARCHAR(50) COMMENT '目标类型：artwork/knowledge/topic等',
    target_id BIGINT COMMENT '目标 ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 系统设置表
CREATE TABLE IF NOT EXISTS system_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    site_name VARCHAR(255) COMMENT '网站名称',
    site_description TEXT COMMENT '网站描述',
    logo VARCHAR(255) COMMENT 'logo URL',
    contact_email VARCHAR(100) COMMENT '联系邮箱',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统设置表';

-- 资讯表
CREATE TABLE IF NOT EXISTS news (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '资讯标题',
    content LONGTEXT COMMENT '资讯内容',
    author VARCHAR(100) COMMENT '资讯作者',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资讯表';

-- 活动表
CREATE TABLE IF NOT EXISTS activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '活动标题',
    description LONGTEXT COMMENT '活动描述',
    organizer VARCHAR(100) COMMENT '活动组织者',
    activity_time VARCHAR(255) COMMENT '活动时间',
    location VARCHAR(255) COMMENT '活动地点',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- 插入默认系统设置
INSERT INTO system_settings (site_name, site_description, contact_email, contact_phone) 
VALUES ('文山壮族刺绣网站系统', '传承和展示壮族非遗刺绣文化的综合性网络平台', 'admin@zhuang-embroidery.com', '0876-1234567');
