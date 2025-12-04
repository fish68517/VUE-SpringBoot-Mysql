# 数据库配置与初始化指南

## 概述

本文档说明如何为沈阳音乐节管理系统配置和初始化 MySQL 数据库。

## 前置要求

- MySQL 8.0 或更高版本
- MySQL 客户端工具（mysql 命令行或 MySQL Workbench）
- Spring Boot 应用已配置

## 快速开始

### 步骤 1：创建数据库

使用 MySQL 命令行连接到 MySQL 服务器：

```bash
mysql -u root -p
```

输入密码后，执行以下命令创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS shenyang_music_festival 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

### 步骤 2：执行数据库脚本

#### 方式 A：使用 MySQL 命令行

```bash
# 连接到 MySQL
mysql -u root -p shenyang_music_festival

# 执行 schema.sql 创建表结构
source src/main/resources/db/schema.sql

# 执行 data.sql 初始化数据
source src/main/resources/db/data.sql
```

#### 方式 B：使用 Spring Boot 自动初始化

1. 确保 `application.yml` 中的数据库连接配置正确
2. 启动 Spring Boot 应用
3. 应用会自动执行 `schema.sql` 和 `data.sql`

```bash
mvn spring-boot:run
```

#### 方式 C：使用 MySQL Workbench

1. 打开 MySQL Workbench
2. 连接到 MySQL 服务器
3. 创建新的 SQL 标签页
4. 打开 `src/main/resources/db/schema.sql` 文件
5. 执行脚本（Ctrl+Shift+Enter）
6. 打开 `src/main/resources/db/data.sql` 文件
7. 执行脚本

### 步骤 3：验证数据库

连接到数据库并验证表是否创建成功：

```sql
USE shenyang_music_festival;
SHOW TABLES;
```

应该看到以下表：
- users
- festivals
- artists
- schedules
- ticket_sessions
- ticket_zones
- tickets
- product_categories
- products
- orders
- order_items
- checkin_tasks
- checkin_records
- points_mall
- points_exchanges
- points_history
- articles
- article_comments
- user_collections
- festival_follows
- weather_info
- transport_info

## 数据库配置

### application.yml 配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shenyang_music_festival?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=utf8mb4
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  sql:
    init:
      mode: always
      data-locations: classpath:db/data.sql
      continue-on-error: false
  
  jpa:
    hibernate:
      ddl-auto: update
```

### 配置说明

| 配置项 | 说明 |
|--------|------|
| `url` | MySQL 连接 URL，包含数据库名称 |
| `username` | MySQL 用户名 |
| `password` | MySQL 密码 |
| `driver-class-name` | MySQL JDBC 驱动类 |
| `sql.init.mode` | SQL 初始化模式（always/never/embedded） |
| `sql.init.data-locations` | 数据初始化脚本位置 |
| `jpa.hibernate.ddl-auto` | Hibernate DDL 自动生成策略（update/create/create-drop/validate） |

## 数据库架构

### 表关系图

```
users (用户)
├── tickets (电子票)
│   ├── ticket_sessions (场次)
│   └── ticket_zones (分区)
├── orders (订单)
│   └── order_items (订单项)
│       └── products (商品)
├── checkin_records (打卡记录)
│   └── checkin_tasks (打卡任务)
├── points_exchanges (积分兑换)
│   └── points_mall (积分商城)
├── points_history (积分流水)
├── article_comments (文章评论)
│   └── articles (文章)
├── user_collections (用户收藏)
│   └── articles (文章)
└── festival_follows (音乐节关注)
    └── festivals (音乐节)
```

## 关键特性

### 防黄牛机制

通过以下方式实现防黄牛：

1. **身份证号唯一约束**
   ```sql
   UNIQUE KEY uk_id_number (id_number)
   ```

2. **购票限制**
   ```sql
   UNIQUE KEY uk_session_id_number (session_id, buyer_id_number)
   ```

3. **业务逻辑**：购票时检查身份证号是否已购票

### 性能优化

#### 索引设计

| 表名 | 索引字段 | 用途 |
|------|---------|------|
| users | phone, id_number, created_at | 快速查询用户 |
| tickets | user_id, session_id, zone_id, status, created_at | 快速查询电子票 |
| orders | user_id, order_type, status, created_at | 快速查询订单 |
| checkin_records | user_id, task_id, status, created_at | 快速查询打卡记录 |
| points_history | user_id, change_type, created_at | 快速查询积分流水 |
| articles | type, is_published, created_at | 快速查询文章 |

#### 字符集优化

所有表使用 `utf8mb4` 字符集，支持完整的 Unicode 字符：

```sql
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
```

## 常见问题

### Q: 如何重置数据库？

A: 执行以下命令：

```sql
DROP DATABASE shenyang_music_festival;
CREATE DATABASE shenyang_music_festival CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shenyang_music_festival;
source src/main/resources/db/schema.sql;
source src/main/resources/db/data.sql;
```

### Q: 如何修改数据库连接信息？

A: 修改 `application.yml` 中的以下配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://your-host:3306/your-database
    username: your-username
    password: your-password
```

### Q: 如何禁用自动初始化？

A: 修改 `application.yml`：

```yaml
spring:
  sql:
    init:
      mode: never
```

### Q: 如何查看 SQL 执行日志？

A: 修改 `application.yml`：

```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Q: 如何备份数据库？

A: 使用 mysqldump 命令：

```bash
mysqldump -u root -p shenyang_music_festival > backup.sql
```

### Q: 如何恢复数据库？

A: 使用 mysql 命令：

```bash
mysql -u root -p shenyang_music_festival < backup.sql
```

## 数据库维护

### 定期检查

```sql
-- 检查表大小
SELECT table_name, ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb
FROM information_schema.tables
WHERE table_schema = 'shenyang_music_festival'
ORDER BY size_mb DESC;

-- 检查索引使用情况
SELECT object_schema, object_name, count_read, count_write, count_delete, count_update
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE object_schema = 'shenyang_music_festival'
ORDER BY count_read DESC;
```

### 优化表

```sql
-- 优化所有表
OPTIMIZE TABLE users, festivals, tickets, orders, products;

-- 分析表统计信息
ANALYZE TABLE users, festivals, tickets, orders, products;
```

## 生产环境建议

1. **使用强密码**：修改 MySQL root 用户密码
2. **创建专用用户**：为应用创建专用数据库用户
3. **启用备份**：设置定期自动备份
4. **监控性能**：启用慢查询日志
5. **定期维护**：定期优化表和索引
6. **安全加固**：限制数据库访问 IP

### 创建专用用户示例

```sql
-- 创建用户
CREATE USER 'musicfestival'@'localhost' IDENTIFIED BY 'strong_password';

-- 授予权限
GRANT ALL PRIVILEGES ON shenyang_music_festival.* TO 'musicfestival'@'localhost';

-- 刷新权限
FLUSH PRIVILEGES;
```

## 相关文件

- `src/main/resources/db/schema.sql` - 数据库表结构定义
- `src/main/resources/db/data.sql` - 初始化数据
- `src/main/resources/db/README.md` - 数据库详细说明
- `src/main/resources/application.yml` - Spring Boot 配置

## 支持

如有问题，请参考：
- [MySQL 官方文档](https://dev.mysql.com/doc/)
- [Spring Boot 数据库配置](https://spring.io/guides/gs/accessing-data-mysql/)
- [Hibernate 文档](https://hibernate.org/orm/documentation/)
