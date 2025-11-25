# 数据库设置总结 - Little Shark Fitness Management System

## 概述

已为小鲨鱼健身管理系统创建了完整的数据库脚本和中文模拟数据。

## 创建的文件

### 1. 数据库脚本

#### `SpringBoot/src/main/resources/schema.sql`
- **大小**: 完整的数据库架构定义
- **内容**: 10个表的创建语句
- **特点**:
  - 完整的表结构定义
  - 外键关系配置
  - 索引优化
  - 中文注释说明
  - UTF8MB4字符集支持

#### `SpringBoot/src/main/resources/data.sql`
- **大小**: 完整的中文模拟数据
- **内容**: 所有表的示例数据
- **特点**:
  - 完全中文化的数据
  - 真实的业务场景
  - 关系数据完整性
  - 时间序列数据

### 2. 初始化脚本

#### `init-database.sh` (Linux/Mac)
- 自动化数据库初始化脚本
- 交互式参数输入
- 错误处理和验证
- 彩色输出提示

#### `init-database.bat` (Windows)
- Windows批处理版本
- 相同的功能和流程
- 适配Windows环境

### 3. 文档

#### `DATABASE_SETUP.md`
- 详细的数据库设置指南
- 表结构说明
- 常用操作命令
- 备份和恢复方法
- 常见问题解答

#### `DATABASE_QUERIES.md`
- 常用SQL查询示例
- 按功能分类
- 包含统计查询
- 综合查询示例

#### `DATABASE_SUMMARY.md` (本文件)
- 数据库设置总结
- 快速参考指南

## 数据库表结构

### 10个核心表

| 表名 | 说明 | 记录数 |
|------|------|--------|
| user | 用户表 | 10 |
| fitness_resource | 健身资源表 | 10 |
| training_plan | 训练计划表 | 5 |
| dynamic | 社区动态表 | 10 |
| check_in | 打卡表 | 56 |
| diet_record | 饮食记录表 | 25 |
| comment | 评论表 | 20 |
| like_record | 点赞表 | 37 |
| coach_student | 教练-学生关系表 | 7 |
| collection | 资源收藏表 | 14 |

## 模拟数据详情

### 用户数据 (10个用户)
- **1个管理员**: admin
- **3个教练**: coach_zhang, coach_li, coach_wang
- **6个普通用户**: user_xiaoming, user_xiaohong, user_liming, user_wangfang, user_zhangjie, user_liuyan

### 健身资源 (10个资源)
- **5个视频资源**:
  - 初级健身入门教程 (1250次浏览)
  - 高效腹肌训练30分钟 (2840次浏览)
  - 瑜伽基础课程 (1560次浏览)
  - 有氧运动减脂指南 (3200次浏览)
  - 力量训练完全指南 (2100次浏览)

- **5个文章资源**:
  - 健身初学者必读：如何制定合理的训练计划
  - 营养指南：健身期间应该吃什么
  - 如何避免健身中的常见错误
  - 瑜伽的益处和如何开始
  - 运动恢复的重要性

### 训练计划 (5个计划)
- 8周增肌计划 (进行中)
- 12周减脂计划 (进行中)
- 瑜伽柔韧性提升计划 (进行中)
- 4周核心力量训练 (已完成)
- 全身综合训练计划 (进行中)

### 社区动态 (10条动态)
- 打卡分享
- 饮食分享
- 训练分享
- 健身建议
- 成果分享

### 打卡记录 (56条)
- user_xiaoming: 30天连续打卡
- user_xiaohong: 16天连续打卡
- user_liming: 9天连续打卡

### 饮食记录 (25条)
- 包含早餐、午餐、晚餐、零食
- 真实的食物名称和卡路里数据
- 近期的记录数据

### 评论和点赞
- 20条评论
- 37条点赞
- 真实的用户互动

### 教练-学生关系 (7个关系)
- coach_zhang: 3个学生
- coach_li: 2个学生
- coach_wang: 2个学生

### 资源收藏 (14条)
- 用户收藏的健身资源

## 快速开始

### 方式1: 使用初始化脚本 (推荐)

#### Linux/Mac:
```bash
chmod +x init-database.sh
./init-database.sh
```

#### Windows:
```cmd
init-database.bat
```

### 方式2: 手动导入

```bash
# 1. 创建数据库和用户
mysql -u root -p < create_user.sql

# 2. 导入架构
mysql -u shark_user -p shark_fitness < SpringBoot/src/main/resources/schema.sql

# 3. 导入数据
mysql -u shark_user -p shark_fitness < SpringBoot/src/main/resources/data.sql
```

## 配置应用

### 开发环境

编辑 `SpringBoot/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shark_fitness?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
spring.datasource.username=shark_user
spring.datasource.password=shark_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 生产环境

编辑 `SpringBoot/src/main/resources/application-prod.properties`:

```properties
spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

## 验证数据库

### 查看表

```sql
SHOW TABLES;
```

### 查看数据统计

```sql
SELECT 'user' as table_name, COUNT(*) as count FROM user
UNION ALL
SELECT 'fitness_resource', COUNT(*) FROM fitness_resource
UNION ALL
SELECT 'training_plan', COUNT(*) FROM training_plan
UNION ALL
SELECT 'dynamic', COUNT(*) FROM dynamic
UNION ALL
SELECT 'check_in', COUNT(*) FROM check_in
UNION ALL
SELECT 'diet_record', COUNT(*) FROM diet_record
UNION ALL
SELECT 'comment', COUNT(*) FROM comment
UNION ALL
SELECT 'like_record', COUNT(*) FROM like_record
UNION ALL
SELECT 'coach_student', COUNT(*) FROM coach_student
UNION ALL
SELECT 'collection', COUNT(*) FROM collection;
```

## 测试用户

### 登录凭证

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | admin | 系统管理员 |
| coach_zhang | coach123 | coach | 教练 |
| coach_li | coach123 | coach | 教练 |
| coach_wang | coach123 | coach | 教练 |
| user_xiaoming | user123 | user | 普通用户 |
| user_xiaohong | user123 | user | 普通用户 |
| user_liming | user123 | user | 普通用户 |
| user_wangfang | user123 | user | 普通用户 |
| user_zhangjie | user123 | user | 普通用户 |
| user_liuyan | user123 | user | 普通用户 |

## 常用操作

### 备份数据库

```bash
mysqldump -u shark_user -p shark_fitness > backup.sql
```

### 恢复数据库

```bash
mysql -u shark_user -p shark_fitness < backup.sql
```

### 重置数据库

```bash
# 删除所有数据但保留表结构
TRUNCATE TABLE like_record;
TRUNCATE TABLE comment;
TRUNCATE TABLE collection;
TRUNCATE TABLE coach_student;
TRUNCATE TABLE diet_record;
TRUNCATE TABLE check_in;
TRUNCATE TABLE dynamic;
TRUNCATE TABLE training_plan;
TRUNCATE TABLE fitness_resource;
TRUNCATE TABLE user;
```

### 查看数据库大小

```sql
SELECT 
    SUM(ROUND(((data_length + index_length) / 1024 / 1024), 2)) AS total_size_mb
FROM information_schema.tables
WHERE table_schema = 'shark_fitness';
```

## 文件清单

```
SpringBoot/src/main/resources/
├── schema.sql              # 数据库架构脚本
└── data.sql                # 中文模拟数据脚本

根目录/
├── init-database.sh        # Linux/Mac初始化脚本
├── init-database.bat       # Windows初始化脚本
├── DATABASE_SETUP.md       # 数据库设置指南
├── DATABASE_QUERIES.md     # SQL查询示例
└── DATABASE_SUMMARY.md     # 本文件
```

## 下一步

1. **运行初始化脚本**: 使用 `init-database.sh` 或 `init-database.bat`
2. **配置应用**: 更新 `application.properties`
3. **启动应用**: 运行 Spring Boot 应用
4. **测试登录**: 使用测试用户登录
5. **查看数据**: 使用 `DATABASE_QUERIES.md` 中的查询示例

## 支持文档

- `DATABASE_SETUP.md` - 详细的设置指南
- `DATABASE_QUERIES.md` - SQL查询示例
- `DEPLOYMENT.md` - 部署指南
- `README.md` - 项目概述

## 注意事项

1. **字符集**: 所有表都使用 UTF8MB4 字符集，支持中文和表情符号
2. **时区**: 配置为 Asia/Shanghai (中国时区)
3. **密码**: 生产环境中应更改所有默认密码
4. **备份**: 定期备份数据库
5. **索引**: 已添加常用查询的索引以提高性能

## 常见问题

### Q: 如何修改数据库密码？

A: 使用以下SQL语句：
```sql
ALTER USER 'shark_user'@'localhost' IDENTIFIED BY 'new_password';
FLUSH PRIVILEGES;
```

### Q: 如何添加新用户？

A: 使用以下SQL语句：
```sql
INSERT INTO user (username, password, role, avatar, gender, intro, created_at, updated_at) 
VALUES ('new_user', 'password123', 'user', 'avatar_url', 'male', '个人介绍', NOW(), NOW());
```

### Q: 如何导出数据？

A: 使用以下命令：
```bash
mysqldump -u shark_user -p shark_fitness > export.sql
```

### Q: 如何清空所有数据但保留表结构？

A: 使用 TRUNCATE 命令（见上面的"重置数据库"部分）

## 总结

✅ 完整的数据库架构脚本
✅ 真实的中文模拟数据
✅ 自动化初始化脚本
✅ 详细的文档和示例
✅ 生产环境就绪

数据库现已准备好用于开发和测试！

---

**创建日期**: 2024年
**版本**: 1.0
**状态**: ✅ 完成
