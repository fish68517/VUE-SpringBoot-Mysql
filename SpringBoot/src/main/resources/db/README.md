# 数据库初始化指南

## 概述

本目录包含沈阳音乐节管理系统的数据库初始化脚本。

## 文件说明

### schema.sql
- **用途**：创建数据库和所有表结构
- **包含内容**：
  - 数据库创建
  - 所有表的定义（用户、票务、商品、订单、打卡、积分、内容等）
  - 索引和约束配置
  - 外键关系定义

### data.sql
- **用途**：初始化示例数据
- **包含内容**：
  - 音乐节基本信息
  - 艺人和演出日程
  - 票务场次和分区
  - 商品分类和商品
  - 打卡任务
  - 积分商城商品
  - 文章和交通信息

## 数据库初始化步骤

### 方式一：使用 MySQL 命令行

```bash
# 连接到 MySQL
mysql -u root -p

# 执行 schema.sql 创建表结构
source /path/to/schema.sql

# 执行 data.sql 初始化数据
source /path/to/data.sql
```

### 方式二：使用 Spring Boot 自动初始化

Spring Boot 应用启动时会自动执行 `schema.sql` 和 `data.sql`：

1. 确保 `application.yml` 中配置了正确的数据库连接信息
2. 启动 Spring Boot 应用
3. 应用会自动创建表和初始化数据

**配置示例**（application.yml）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shenyang_music_festival?useSSL=false&serverTimezone=UTC
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
```

### 方式三：使用 MySQL Workbench 或其他 GUI 工具

1. 打开 MySQL Workbench
2. 连接到 MySQL 服务器
3. 打开 `schema.sql` 文件，执行脚本
4. 打开 `data.sql` 文件，执行脚本

## 数据库结构

### 核心表

#### 用户相关
- `users` - 用户表（包含实名认证、积分等信息）

#### 音乐节信息
- `festivals` - 音乐节表
- `artists` - 艺人表
- `schedules` - 演出日程表

#### 购票系统
- `ticket_sessions` - 票务场次表
- `ticket_zones` - 分区表
- `tickets` - 电子票表

#### 商品系统
- `product_categories` - 商品分类表
- `products` - 商品表
- `orders` - 订单表
- `order_items` - 订单项表

#### 打卡系统
- `checkin_tasks` - 打卡任务表
- `checkin_records` - 打卡记录表

#### 积分系统
- `points_mall` - 积分商城商品表
- `points_exchanges` - 积分兑换订单表
- `points_history` - 积分流水表

#### 内容系统
- `articles` - 文章表
- `article_comments` - 文章评论表
- `user_collections` - 用户收藏表
- `festival_follows` - 音乐节关注表

#### 其他
- `weather_info` - 天气信息表
- `transport_info` - 交通信息表

## 关键约束和索引

### 唯一约束（防止重复）
- `users.phone` - 手机号唯一
- `users.id_number` - 身份证号唯一（防黄牛）
- `tickets.session_id + buyer_id_number` - 同一场次同一身份证号只能购一张票
- `checkin_records.user_id + task_id` - 同一用户同一任务只能打卡一次
- `user_collections.user_id + article_id` - 同一用户同一文章只能收藏一次
- `festival_follows.user_id + festival_id` - 同一用户同一音乐节只能关注一次

### 性能索引
- `users` - phone, id_number, created_at
- `tickets` - user_id, session_id, zone_id, status, created_at
- `orders` - user_id, order_type, status, created_at
- `checkin_records` - user_id, task_id, status, created_at
- `points_history` - user_id, change_type, created_at
- `articles` - type, is_published, created_at

## 防黄牛机制

通过以下方式实现防黄牛：

1. **身份证号唯一约束**：`users.id_number` 字段设置为 UNIQUE
2. **购票限制**：`tickets` 表中 `session_id + buyer_id_number` 设置为 UNIQUE
3. **业务逻辑**：购票时检查身份证号是否已购票

## 数据库字符集

所有表都使用 `utf8mb4` 字符集和 `utf8mb4_unicode_ci` 排序规则，支持完整的 Unicode 字符（包括表情符号）。

## 备份和恢复

### 备份数据库
```bash
mysqldump -u root -p shenyang_music_festival > backup.sql
```

### 恢复数据库
```bash
mysql -u root -p shenyang_music_festival < backup.sql
```

## 常见问题

### Q: 如何重置数据库？
A: 执行以下命令：
```sql
DROP DATABASE shenyang_music_festival;
source schema.sql;
source data.sql;
```

### Q: 如何添加新表？
A: 在 `schema.sql` 中添加新的 CREATE TABLE 语句，然后重新执行脚本。

### Q: 如何修改表结构？
A: 使用 ALTER TABLE 语句修改表结构，或者删除表后重新创建。

### Q: 如何导出数据？
A: 使用 MySQL 的 SELECT INTO OUTFILE 或 mysqldump 工具。

## 性能优化建议

1. **定期维护索引**：使用 ANALYZE TABLE 和 OPTIMIZE TABLE
2. **监控慢查询**：启用 slow query log
3. **定期备份**：建立自动备份机制
4. **分区表**：对大表考虑使用分区
5. **缓存策略**：使用 Redis 缓存热点数据

## 相关文档

- [Spring Boot 配置](../README.md)
- [API 文档](../../doc/)
- [系统设计文档](../../.kiro/specs/shenyang-music-festival/design.md)
