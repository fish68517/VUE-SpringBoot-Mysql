# 数据库设置指南 - Little Shark Fitness Management System

## 概述

本文档提供了小鲨鱼健身管理系统的数据库设置和初始化说明。

## 前置要求

- MySQL 8.0 或更高版本
- MySQL 客户端工具（mysql-cli 或 MySQL Workbench）
- 数据库管理员权限

## 快速开始

### 1. 创建数据库和用户

```bash
# 使用 MySQL 客户端连接
mysql -u root -p

# 在 MySQL 命令行中执行以下命令
CREATE DATABASE shark_fitness CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'shark_user'@'localhost' IDENTIFIED BY 'shark_password';
GRANT ALL PRIVILEGES ON shark_fitness.* TO 'shark_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 2. 导入数据库架构

```bash
# 导入数据库表结构
mysql -u shark_user -p shark_fitness < SpringBoot/src/main/resources/schema.sql

# 输入密码: shark_password
```

### 3. 导入模拟数据

```bash
# 导入中文模拟数据
mysql -u shark_user -p shark_fitness < SpringBoot/src/main/resources/data.sql

# 输入密码: shark_password
```

### 4. 验证数据库

```bash
# 连接到数据库
mysql -u shark_user -p shark_fitness

# 执行以下查询验证
SHOW TABLES;
SELECT COUNT(*) FROM user;
SELECT COUNT(*) FROM fitness_resource;
SELECT COUNT(*) FROM dynamic;
```

## 详细说明

### 数据库架构 (schema.sql)

该文件包含以下表的定义：

#### 1. user (用户表)
- **id**: 用户ID (主键)
- **username**: 用户名 (唯一)
- **password**: 密码
- **role**: 角色 (user/coach/admin)
- **avatar**: 头像URL
- **gender**: 性别
- **intro**: 个人介绍
- **created_at**: 创建时间
- **updated_at**: 更新时间

#### 2. fitness_resource (健身资源表)
- **id**: 资源ID (主键)
- **title**: 资源标题
- **description**: 资源描述
- **content_type**: 内容类型 (video/article/document)
- **file_url**: 文件URL
- **content**: 文章内容
- **creator_id**: 创建者ID (外键)
- **view_count**: 浏览次数
- **created_at**: 创建时间
- **updated_at**: 更新时间

#### 3. training_plan (训练计划表)
- **id**: 计划ID (主键)
- **name**: 计划名称
- **description**: 计划描述
- **exercises**: 训练项目 (JSON格式)
- **coach_id**: 教练ID (外键)
- **student_id**: 学生ID (外键)
- **start_date**: 开始日期
- **end_date**: 结束日期
- **status**: 状态 (active/completed/cancelled)
- **created_at**: 创建时间
- **updated_at**: 更新时间

#### 4. dynamic (社区动态表)
- **id**: 动态ID (主键)
- **content**: 内容
- **image_urls**: 图片URL列表
- **user_id**: 用户ID (外键)
- **like_count**: 点赞数
- **comment_count**: 评论数
- **status**: 状态 (pending/approved/rejected)
- **created_at**: 创建时间
- **updated_at**: 更新时间

#### 5. check_in (打卡表)
- **id**: 打卡ID (主键)
- **user_id**: 用户ID (外键)
- **check_date**: 打卡日期
- **check_time**: 打卡时间

#### 6. diet_record (饮食记录表)
- **id**: 记录ID (主键)
- **user_id**: 用户ID (外键)
- **meal_type**: 餐类型 (breakfast/lunch/dinner/snack)
- **food_items**: 食物项目
- **calories**: 卡路里
- **meal_date**: 餐食日期
- **created_at**: 创建时间
- **updated_at**: 更新时间

#### 7. comment (评论表)
- **id**: 评论ID (主键)
- **content**: 评论内容
- **user_id**: 用户ID (外键)
- **dynamic_id**: 动态ID (外键)
- **created_at**: 创建时间

#### 8. like_record (点赞记录表)
- **id**: 点赞ID (主键)
- **user_id**: 用户ID (外键)
- **dynamic_id**: 动态ID (外键)
- **created_at**: 创建时间

#### 9. coach_student (教练-学生关系表)
- **id**: 关系ID (主键)
- **coach_id**: 教练ID (外键)
- **student_id**: 学生ID (外键)
- **created_at**: 创建时间

#### 10. collection (资源收藏表)
- **id**: 收藏ID (主键)
- **user_id**: 用户ID (外键)
- **resource_id**: 资源ID (外键)
- **created_at**: 创建时间

### 模拟数据 (data.sql)

该文件包含以下中文模拟数据：

#### 用户数据
- **1个管理员**: admin (用于系统管理)
- **3个教练**: coach_zhang, coach_li, coach_wang (各有不同的专长)
- **6个普通用户**: user_xiaoming, user_xiaohong, user_liming, user_wangfang, user_zhangjie, user_liuyan

#### 健身资源
- **5个视频资源**: 包括初级教程、腹肌训练、瑜伽、有氧运动、力量训练
- **5个文章资源**: 包括训练计划、营养指南、常见错误、瑜伽益处、运动恢复

#### 训练计划
- **5个训练计划**: 包括增肌、减脂、瑜伽、核心力量、全身综合训练

#### 社区动态
- **10条社区动态**: 包括打卡分享、饮食分享、训练分享等

#### 打卡记录
- **user_xiaoming**: 30天连续打卡
- **user_xiaohong**: 16天连续打卡
- **user_liming**: 9天连续打卡

#### 饮食记录
- 为多个用户添加了近期的饮食记录，包括早餐、午餐、晚餐和零食

#### 评论和点赞
- **20条评论**: 用户对社区动态的评论
- **37条点赞**: 用户对社区动态的点赞

#### 教练-学生关系
- **7个关系**: 教练与学生的对应关系

#### 资源收藏
- **14条收藏**: 用户收藏的健身资源

## 数据库配置

### 开发环境配置

编辑 `SpringBoot/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shark_fitness?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
spring.datasource.username=shark_user
spring.datasource.password=shark_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 生产环境配置

编辑 `SpringBoot/src/main/resources/application-prod.properties`:

```properties
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:shark_fitness}?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
spring.datasource.username=${DB_USERNAME:shark_user}
spring.datasource.password=${DB_PASSWORD:shark_password}
```

## 常用数据库操作

### 查看所有表

```sql
SHOW TABLES;
```

### 查看表结构

```sql
DESCRIBE user;
DESCRIBE fitness_resource;
DESCRIBE training_plan;
```

### 查看数据统计

```sql
-- 用户统计
SELECT role, COUNT(*) as count FROM user GROUP BY role;

-- 资源统计
SELECT content_type, COUNT(*) as count FROM fitness_resource GROUP BY content_type;

-- 训练计划统计
SELECT status, COUNT(*) as count FROM training_plan GROUP BY status;

-- 社区动态统计
SELECT status, COUNT(*) as count FROM dynamic GROUP BY status;
```

### 查看用户信息

```sql
-- 查看所有用户
SELECT id, username, role, created_at FROM user;

-- 查看特定用户
SELECT * FROM user WHERE username = 'user_xiaoming';
```

### 查看教练的学生

```sql
-- 查看教练张的所有学生
SELECT s.id, s.username, s.intro 
FROM user s 
INNER JOIN coach_student cs ON s.id = cs.student_id 
WHERE cs.coach_id = 1;
```

### 查看用户的打卡记录

```sql
-- 查看用户xiaoming的打卡记录
SELECT check_date, check_time 
FROM check_in 
WHERE user_id = 3 
ORDER BY check_date DESC;
```

### 查看用户的饮食记录

```sql
-- 查看用户xiaoming的饮食记录
SELECT meal_type, food_items, calories, meal_date 
FROM diet_record 
WHERE user_id = 3 
ORDER BY meal_date DESC;
```

## 备份和恢复

### 备份数据库

```bash
# 备份整个数据库
mysqldump -u shark_user -p shark_fitness > shark_fitness_backup.sql

# 备份特定表
mysqldump -u shark_user -p shark_fitness user fitness_resource > backup_tables.sql
```

### 恢复数据库

```bash
# 恢复整个数据库
mysql -u shark_user -p shark_fitness < shark_fitness_backup.sql

# 恢复特定表
mysql -u shark_user -p shark_fitness < backup_tables.sql
```

## 数据库维护

### 检查表的完整性

```sql
CHECK TABLE user;
CHECK TABLE fitness_resource;
CHECK TABLE training_plan;
```

### 优化表

```sql
OPTIMIZE TABLE user;
OPTIMIZE TABLE fitness_resource;
OPTIMIZE TABLE training_plan;
```

### 查看表的大小

```sql
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb
FROM information_schema.tables
WHERE table_schema = 'shark_fitness'
ORDER BY size_mb DESC;
```

### 查看数据库大小

```sql
SELECT 
    SUM(ROUND(((data_length + index_length) / 1024 / 1024), 2)) AS total_size_mb
FROM information_schema.tables
WHERE table_schema = 'shark_fitness';
```

## 常见问题

### Q: 如何重置数据库？

A: 执行以下命令：
```bash
mysql -u shark_user -p shark_fitness -e "DROP DATABASE shark_fitness;"
mysql -u shark_user -p < schema.sql
mysql -u shark_user -p shark_fitness < data.sql
```

### Q: 如何添加新用户？

A: 使用以下SQL语句：
```sql
INSERT INTO user (username, password, role, avatar, gender, intro, created_at, updated_at) 
VALUES ('new_user', 'password123', 'user', 'avatar_url', 'male', '个人介绍', NOW(), NOW());
```

### Q: 如何修改用户密码？

A: 使用以下SQL语句：
```sql
UPDATE user SET password = 'new_password' WHERE username = 'user_xiaoming';
```

### Q: 如何删除用户？

A: 使用以下SQL语句：
```sql
DELETE FROM user WHERE username = 'user_xiaoming';
```

### Q: 如何查看某个用户的所有活动？

A: 使用以下SQL语句：
```sql
-- 查看用户的所有动态
SELECT * FROM dynamic WHERE user_id = 3 ORDER BY created_at DESC;

-- 查看用户的所有评论
SELECT * FROM comment WHERE user_id = 3 ORDER BY created_at DESC;

-- 查看用户的所有点赞
SELECT * FROM like_record WHERE user_id = 3 ORDER BY created_at DESC;
```

## 性能优化建议

1. **定期备份**: 每天备份一次数据库
2. **定期优化**: 每周运行一次 OPTIMIZE TABLE
3. **监控大小**: 定期检查数据库和表的大小
4. **清理日志**: 定期清理旧的日志数据
5. **索引优化**: 根据查询模式添加适当的索引

## 支持

如有问题，请参考：
- `DEPLOYMENT.md` - 部署指南
- `README.md` - 项目概述
- `API.md` - API文档

---

**最后更新**: 2024年
**版本**: 1.0
