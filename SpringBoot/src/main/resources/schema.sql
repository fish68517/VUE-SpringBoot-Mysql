-- Little Shark Fitness Management System Database Schema
-- 小鲨鱼健身管理系统数据库架构

-- Create database
CREATE DATABASE IF NOT EXISTS shark_fitness CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shark_fitness;

-- User table - 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    role VARCHAR(20) NOT NULL COMMENT '角色: user(普通用户), coach(教练), admin(管理员)',
    avatar VARCHAR(255) COMMENT '头像URL',
    gender VARCHAR(10) COMMENT '性别: male(男), female(女)',
    intro VARCHAR(500) COMMENT '个人介绍',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- Fitness Resource table - 健身资源表
CREATE TABLE IF NOT EXISTS fitness_resource (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '资源ID',
    title VARCHAR(200) NOT NULL COMMENT '资源标题',
    description VARCHAR(1000) COMMENT '资源描述',
    content_type VARCHAR(50) NOT NULL COMMENT '内容类型: video(视频), article(文章), document(文档)',
    file_url VARCHAR(500) COMMENT '文件URL',
    content LONGTEXT COMMENT '文章内容',
    creator_id BIGINT COMMENT '创建者ID',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (creator_id) REFERENCES user(id) ON DELETE SET NULL,
    INDEX idx_content_type (content_type),
    INDEX idx_creator_id (creator_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健身资源表';

-- Training Plan table - 训练计划表
CREATE TABLE IF NOT EXISTS training_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID',
    name VARCHAR(200) NOT NULL COMMENT '计划名称',
    description VARCHAR(1000) COMMENT '计划描述',
    exercises LONGTEXT COMMENT '训练项目(JSON格式)',
    coach_id BIGINT NOT NULL COMMENT '教练ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态: active(进行中), completed(已完成), cancelled(已取消)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (coach_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_coach_id (coach_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='训练计划表';

-- Dynamic (Community Posts) table - 动态(社区帖子)表
CREATE TABLE IF NOT EXISTS dynamic (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '动态ID',
    content LONGTEXT NOT NULL COMMENT '内容',
    image_urls VARCHAR(1000) COMMENT '图片URL列表(逗号分隔)',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    status VARCHAR(20) DEFAULT 'approved' COMMENT '状态: pending(待审核), approved(已批准), rejected(已拒绝)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区动态表';

-- Check-in table - 打卡表
CREATE TABLE IF NOT EXISTS check_in (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '打卡ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    check_date DATE NOT NULL COMMENT '打卡日期',
    check_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '打卡时间',
    UNIQUE KEY unique_user_date (user_id, check_date),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_check_date (check_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户打卡表';

-- Diet Record table - 饮食记录表
CREATE TABLE IF NOT EXISTS diet_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    meal_type VARCHAR(20) COMMENT '餐类型: breakfast(早餐), lunch(午餐), dinner(晚餐), snack(零食)',
    food_items VARCHAR(500) COMMENT '食物项目',
    calories INT COMMENT '卡路里',
    meal_date DATE COMMENT '餐食日期',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_meal_date (meal_date),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='饮食记录表';

-- Comment table - 评论表
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    content LONGTEXT NOT NULL COMMENT '评论内容',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    dynamic_id BIGINT NOT NULL COMMENT '动态ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (dynamic_id) REFERENCES dynamic(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_dynamic_id (dynamic_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- Like Record table - 点赞记录表
CREATE TABLE IF NOT EXISTS like_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    dynamic_id BIGINT NOT NULL COMMENT '动态ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY unique_user_dynamic (user_id, dynamic_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (dynamic_id) REFERENCES dynamic(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_dynamic_id (dynamic_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞记录表';

-- Coach-Student Relationship table - 教练-学生关系表
CREATE TABLE IF NOT EXISTS coach_student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
    coach_id BIGINT NOT NULL COMMENT '教练ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY unique_coach_student (coach_id, student_id),
    FOREIGN KEY (coach_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_coach_id (coach_id),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教练-学生关系表';

-- Collection (Saved Resources) table - 收藏表
CREATE TABLE IF NOT EXISTS collection (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY unique_user_resource (user_id, resource_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (resource_id) REFERENCES fitness_resource(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_resource_id (resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源收藏表';

-- Create indexes for better query performance
CREATE INDEX idx_user_created_at ON user(created_at);
CREATE INDEX idx_resource_view_count ON fitness_resource(view_count);
CREATE INDEX idx_dynamic_like_count ON dynamic(like_count);
CREATE INDEX idx_checkin_user_date ON check_in(user_id, check_date);

-- Display completion message
SELECT '✓ Database schema created successfully!' AS status;
