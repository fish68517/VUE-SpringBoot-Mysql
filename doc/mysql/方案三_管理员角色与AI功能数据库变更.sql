-- 方案三数据库变更脚本
-- 用途：
-- 1. 为用户表增加 role 字段，区分普通用户和管理员
-- 2. 为历史用户补齐默认角色
-- 3. 提供管理员账号初始化示例

USE travel_memory_system;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'USER' AFTER bio;

CREATE INDEX idx_users_role ON users(role);

UPDATE users
SET role = 'USER'
WHERE role IS NULL OR role = '';

-- 方案 A：把现有某个用户提升为管理员
-- 请把 1 改成你实际想提升的用户 ID
UPDATE users
SET role = 'ADMIN'
WHERE id = 1;

-- 方案 B：插入一个新的管理员账号
-- 说明：
-- 1. password_hash 使用 BCrypt
-- 2. 下方密码明文对应：Admin@123456
-- 3. 如果你不需要这个账号，可以删除这段 INSERT
INSERT INTO users (username, email, password_hash, avatar_url, bio, role, created_at, updated_at)
SELECT
    'admin',
    'admin@travelmemory.com',
    '$2a$10$B0xQxJY0fJY4xW6xN0QspuFnqvI6jV6czS4QhIwTZPIYOvTo95OfK',
    NULL,
    '系统管理员账号',
    'ADMIN',
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM users
    WHERE email = 'admin@travelmemory.com'
);
