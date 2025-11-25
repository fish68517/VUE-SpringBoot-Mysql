# 数据库查询示例 - Little Shark Fitness Management System

本文档提供了常用的数据库查询示例，帮助您快速获取所需的数据。

## 用户相关查询

### 1. 查看所有用户

```sql
SELECT id, username, role, gender, intro, created_at 
FROM user 
ORDER BY created_at DESC;
```

### 2. 按角色查看用户

```sql
-- 查看所有教练
SELECT id, username, intro, created_at 
FROM user 
WHERE role = 'coach' 
ORDER BY created_at DESC;

-- 查看所有普通用户
SELECT id, username, intro, created_at 
FROM user 
WHERE role = 'user' 
ORDER BY created_at DESC;

-- 查看所有管理员
SELECT id, username, created_at 
FROM user 
WHERE role = 'admin';
```

### 3. 查看特定用户信息

```sql
SELECT * FROM user WHERE username = 'user_xiaoming';
```

### 4. 用户统计

```sql
-- 按角色统计用户数
SELECT role, COUNT(*) as count 
FROM user 
GROUP BY role;

-- 按性别统计用户数
SELECT gender, COUNT(*) as count 
FROM user 
WHERE gender IS NOT NULL 
GROUP BY gender;

-- 用户总数
SELECT COUNT(*) as total_users FROM user;
```

## 健身资源相关查询

### 1. 查看所有资源

```sql
SELECT id, title, content_type, creator_id, view_count, created_at 
FROM fitness_resource 
ORDER BY created_at DESC;
```

### 2. 按类型查看资源

```sql
-- 查看所有视频
SELECT id, title, view_count, created_at 
FROM fitness_resource 
WHERE content_type = 'video' 
ORDER BY view_count DESC;

-- 查看所有文章
SELECT id, title, view_count, created_at 
FROM fitness_resource 
WHERE content_type = 'article' 
ORDER BY view_count DESC;

-- 查看所有文档
SELECT id, title, view_count, created_at 
FROM fitness_resource 
WHERE content_type = 'document' 
ORDER BY view_count DESC;
```

### 3. 查看浏览量最高的资源

```sql
SELECT id, title, content_type, view_count, created_at 
FROM fitness_resource 
ORDER BY view_count DESC 
LIMIT 10;
```

### 4. 查看特定创建者的资源

```sql
SELECT r.id, r.title, r.content_type, r.view_count, u.username 
FROM fitness_resource r 
INNER JOIN user u ON r.creator_id = u.id 
WHERE u.username = 'coach_zhang' 
ORDER BY r.created_at DESC;
```

### 5. 资源统计

```sql
-- 按类型统计资源数
SELECT content_type, COUNT(*) as count 
FROM fitness_resource 
GROUP BY content_type;

-- 资源总数
SELECT COUNT(*) as total_resources FROM fitness_resource;

-- 平均浏览量
SELECT AVG(view_count) as avg_views FROM fitness_resource;
```

## 训练计划相关查询

### 1. 查看所有训练计划

```sql
SELECT tp.id, tp.name, u1.username as coach, u2.username as student, tp.status, tp.start_date, tp.end_date 
FROM training_plan tp 
INNER JOIN user u1 ON tp.coach_id = u1.id 
INNER JOIN user u2 ON tp.student_id = u2.id 
ORDER BY tp.created_at DESC;
```

### 2. 查看进行中的训练计划

```sql
SELECT tp.id, tp.name, u1.username as coach, u2.username as student, tp.start_date, tp.end_date 
FROM training_plan tp 
INNER JOIN user u1 ON tp.coach_id = u1.id 
INNER JOIN user u2 ON tp.student_id = u2.id 
WHERE tp.status = 'active' 
ORDER BY tp.start_date DESC;
```

### 3. 查看特定教练的训练计划

```sql
SELECT tp.id, tp.name, u.username as student, tp.status, tp.start_date, tp.end_date 
FROM training_plan tp 
INNER JOIN user u ON tp.student_id = u.id 
WHERE tp.coach_id = 1 
ORDER BY tp.created_at DESC;
```

### 4. 查看特定学生的训练计划

```sql
SELECT tp.id, tp.name, u.username as coach, tp.status, tp.start_date, tp.end_date 
FROM training_plan tp 
INNER JOIN user u ON tp.coach_id = u.id 
WHERE tp.student_id = 3 
ORDER BY tp.created_at DESC;
```

### 5. 训练计划统计

```sql
-- 按状态统计
SELECT status, COUNT(*) as count 
FROM training_plan 
GROUP BY status;

-- 每个教练的计划数
SELECT u.username, COUNT(*) as plan_count 
FROM training_plan tp 
INNER JOIN user u ON tp.coach_id = u.id 
GROUP BY tp.coach_id 
ORDER BY plan_count DESC;
```

## 社区动态相关查询

### 1. 查看所有动态

```sql
SELECT d.id, d.content, u.username, d.like_count, d.comment_count, d.status, d.created_at 
FROM dynamic d 
INNER JOIN user u ON d.user_id = u.id 
ORDER BY d.created_at DESC;
```

### 2. 查看已批准的动态

```sql
SELECT d.id, d.content, u.username, d.like_count, d.comment_count, d.created_at 
FROM dynamic d 
INNER JOIN user u ON d.user_id = u.id 
WHERE d.status = 'approved' 
ORDER BY d.created_at DESC;
```

### 3. 查看待审核的动态

```sql
SELECT d.id, d.content, u.username, d.created_at 
FROM dynamic d 
INNER JOIN user u ON d.user_id = u.id 
WHERE d.status = 'pending' 
ORDER BY d.created_at DESC;
```

### 4. 查看点赞最多的动态

```sql
SELECT d.id, d.content, u.username, d.like_count, d.comment_count, d.created_at 
FROM dynamic d 
INNER JOIN user u ON d.user_id = u.id 
WHERE d.status = 'approved' 
ORDER BY d.like_count DESC 
LIMIT 10;
```

### 5. 查看特定用户的动态

```sql
SELECT d.id, d.content, d.like_count, d.comment_count, d.status, d.created_at 
FROM dynamic d 
WHERE d.user_id = 3 
ORDER BY d.created_at DESC;
```

### 6. 动态统计

```sql
-- 按状态统计
SELECT status, COUNT(*) as count 
FROM dynamic 
GROUP BY status;

-- 用户动态数
SELECT u.username, COUNT(*) as dynamic_count 
FROM dynamic d 
INNER JOIN user u ON d.user_id = u.id 
GROUP BY d.user_id 
ORDER BY dynamic_count DESC;

-- 平均点赞数
SELECT AVG(like_count) as avg_likes FROM dynamic WHERE status = 'approved';
```

## 打卡相关查询

### 1. 查看用户的打卡记录

```sql
SELECT check_date, check_time 
FROM check_in 
WHERE user_id = 3 
ORDER BY check_date DESC;
```

### 2. 查看用户的连续打卡天数

```sql
SELECT 
    user_id,
    COUNT(*) as consecutive_days,
    MIN(check_date) as start_date,
    MAX(check_date) as end_date
FROM check_in
WHERE user_id = 3
GROUP BY user_id;
```

### 3. 查看最近7天的打卡情况

```sql
SELECT u.username, COUNT(*) as checkin_count 
FROM check_in c 
INNER JOIN user u ON c.user_id = u.id 
WHERE c.check_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) 
GROUP BY c.user_id 
ORDER BY checkin_count DESC;
```

### 4. 打卡统计

```sql
-- 用户打卡总数
SELECT u.username, COUNT(*) as total_checkins 
FROM check_in c 
INNER JOIN user u ON c.user_id = u.id 
GROUP BY c.user_id 
ORDER BY total_checkins DESC;

-- 今天的打卡数
SELECT COUNT(*) as today_checkins FROM check_in WHERE check_date = CURDATE();

-- 本周的打卡数
SELECT COUNT(*) as week_checkins 
FROM check_in 
WHERE check_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY);
```

## 饮食记录相关查询

### 1. 查看用户的饮食记录

```sql
SELECT meal_type, food_items, calories, meal_date 
FROM diet_record 
WHERE user_id = 3 
ORDER BY meal_date DESC;
```

### 2. 查看特定日期的饮食记录

```sql
SELECT meal_type, food_items, calories 
FROM diet_record 
WHERE user_id = 3 AND meal_date = '2024-01-15' 
ORDER BY meal_type;
```

### 3. 查看用户的每日总卡路里

```sql
SELECT 
    meal_date,
    SUM(calories) as total_calories
FROM diet_record
WHERE user_id = 3
GROUP BY meal_date
ORDER BY meal_date DESC;
```

### 4. 饮食统计

```sql
-- 用户的平均每日卡路里
SELECT 
    u.username,
    AVG(daily_calories) as avg_daily_calories
FROM (
    SELECT user_id, meal_date, SUM(calories) as daily_calories
    FROM diet_record
    GROUP BY user_id, meal_date
) t
INNER JOIN user u ON t.user_id = u.id
GROUP BY t.user_id;

-- 最受欢迎的食物
SELECT food_items, COUNT(*) as count 
FROM diet_record 
GROUP BY food_items 
ORDER BY count DESC 
LIMIT 10;
```

## 评论和点赞相关查询

### 1. 查看动态的所有评论

```sql
SELECT c.id, c.content, u.username, c.created_at 
FROM comment c 
INNER JOIN user u ON c.user_id = u.id 
WHERE c.dynamic_id = 1 
ORDER BY c.created_at DESC;
```

### 2. 查看用户的所有评论

```sql
SELECT c.id, c.content, d.content as dynamic_content, c.created_at 
FROM comment c 
INNER JOIN dynamic d ON c.dynamic_id = d.id 
WHERE c.user_id = 3 
ORDER BY c.created_at DESC;
```

### 3. 查看动态的所有点赞用户

```sql
SELECT u.id, u.username 
FROM like_record l 
INNER JOIN user u ON l.user_id = u.id 
WHERE l.dynamic_id = 1 
ORDER BY l.created_at DESC;
```

### 4. 评论和点赞统计

```sql
-- 评论最多的动态
SELECT d.id, d.content, COUNT(*) as comment_count 
FROM comment c 
INNER JOIN dynamic d ON c.dynamic_id = d.id 
GROUP BY c.dynamic_id 
ORDER BY comment_count DESC 
LIMIT 10;

-- 用户的评论总数
SELECT u.username, COUNT(*) as comment_count 
FROM comment c 
INNER JOIN user u ON c.user_id = u.id 
GROUP BY c.user_id 
ORDER BY comment_count DESC;

-- 用户的点赞总数
SELECT u.username, COUNT(*) as like_count 
FROM like_record l 
INNER JOIN user u ON l.user_id = u.id 
GROUP BY l.user_id 
ORDER BY like_count DESC;
```

## 教练-学生关系查询

### 1. 查看教练的所有学生

```sql
SELECT s.id, s.username, s.intro 
FROM user s 
INNER JOIN coach_student cs ON s.id = cs.student_id 
WHERE cs.coach_id = 1 
ORDER BY cs.created_at DESC;
```

### 2. 查看学生的所有教练

```sql
SELECT c.id, c.username, c.intro 
FROM user c 
INNER JOIN coach_student cs ON c.id = cs.coach_id 
WHERE cs.student_id = 3 
ORDER BY cs.created_at DESC;
```

### 3. 教练-学生关系统计

```sql
-- 每个教练的学生数
SELECT u.username, COUNT(*) as student_count 
FROM coach_student cs 
INNER JOIN user u ON cs.coach_id = u.id 
GROUP BY cs.coach_id 
ORDER BY student_count DESC;

-- 每个学生的教练数
SELECT u.username, COUNT(*) as coach_count 
FROM coach_student cs 
INNER JOIN user u ON cs.student_id = u.id 
GROUP BY cs.student_id 
ORDER BY coach_count DESC;
```

## 资源收藏相关查询

### 1. 查看用户的收藏

```sql
SELECT r.id, r.title, r.content_type, r.view_count, c.created_at 
FROM collection c 
INNER JOIN fitness_resource r ON c.resource_id = r.id 
WHERE c.user_id = 3 
ORDER BY c.created_at DESC;
```

### 2. 查看资源的收藏数

```sql
SELECT r.id, r.title, COUNT(*) as collection_count 
FROM collection c 
INNER JOIN fitness_resource r ON c.resource_id = r.id 
GROUP BY c.resource_id 
ORDER BY collection_count DESC;
```

### 3. 收藏统计

```sql
-- 用户的收藏总数
SELECT u.username, COUNT(*) as collection_count 
FROM collection c 
INNER JOIN user u ON c.user_id = u.id 
GROUP BY c.user_id 
ORDER BY collection_count DESC;

-- 最受欢迎的资源（按收藏数）
SELECT r.id, r.title, COUNT(*) as collection_count 
FROM collection c 
INNER JOIN fitness_resource r ON c.resource_id = r.id 
GROUP BY c.resource_id 
ORDER BY collection_count DESC 
LIMIT 10;
```

## 综合查询示例

### 1. 用户活跃度排行

```sql
SELECT 
    u.id,
    u.username,
    COUNT(DISTINCT d.id) as dynamic_count,
    COUNT(DISTINCT c.id) as comment_count,
    COUNT(DISTINCT l.id) as like_count,
    COUNT(DISTINCT ch.id) as checkin_count
FROM user u
LEFT JOIN dynamic d ON u.id = d.user_id
LEFT JOIN comment c ON u.id = c.user_id
LEFT JOIN like_record l ON u.id = l.user_id
LEFT JOIN check_in ch ON u.id = ch.user_id
WHERE u.role = 'user'
GROUP BY u.id
ORDER BY (COUNT(DISTINCT d.id) + COUNT(DISTINCT c.id) + COUNT(DISTINCT l.id)) DESC;
```

### 2. 教练效能评估

```sql
SELECT 
    u.username,
    COUNT(DISTINCT cs.student_id) as student_count,
    COUNT(DISTINCT tp.id) as plan_count,
    COUNT(DISTINCT fr.id) as resource_count
FROM user u
LEFT JOIN coach_student cs ON u.id = cs.coach_id
LEFT JOIN training_plan tp ON u.id = tp.coach_id
LEFT JOIN fitness_resource fr ON u.id = fr.creator_id
WHERE u.role = 'coach'
GROUP BY u.id
ORDER BY student_count DESC;
```

### 3. 学生进度跟踪

```sql
SELECT 
    u.username,
    COUNT(DISTINCT ch.id) as total_checkins,
    COUNT(DISTINCT CASE WHEN ch.check_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) THEN ch.id END) as week_checkins,
    COUNT(DISTINCT tp.id) as active_plans,
    COUNT(DISTINCT dr.id) as diet_records
FROM user u
LEFT JOIN check_in ch ON u.id = ch.user_id
LEFT JOIN training_plan tp ON u.id = tp.student_id AND tp.status = 'active'
LEFT JOIN diet_record dr ON u.id = dr.user_id
WHERE u.role = 'user'
GROUP BY u.id
ORDER BY total_checkins DESC;
```

---

**最后更新**: 2024年
**版本**: 1.0
