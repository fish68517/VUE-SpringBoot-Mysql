# 纳西族纹样数字化展示平台 - 数据库设置指南

## 数据库初始化方式

本项目提供两种数据库初始化方式：

### 方式一：自动初始化（推荐）

应用启动时会自动执行数据库初始化。具体流程：

1. **启动应用**

   ```bash
   cd SpringBoot
   mvn spring-boot:run
   ```
2. **自动初始化流程**

   - 应用启动时，`DatabaseInitializer` 类会自动执行
   - 自动创建所有必要的表结构（通过 Hibernate JPA）
   - 自动初始化角色、权限、纹样和系统参数数据
   - 初始化过程中会检查数据是否已存在，避免重复插入
3. **初始化内容**

   - **角色**：超级管理员、管理员、普通用户
   - **权限**：为各角色分配相应权限
   - **纹样**：四类纹样各2个示例（共8个）
   - **系统参数**：轮播图配置、上传限制、分页设置等

### 方式二：手动执行SQL脚本

如果需要手动初始化数据库，可以执行SQL脚本：

1. **创建数据库**

   ```bash
   mysql -u root -p < SpringBoot/src/main/resources/init.sql
   ```
2. **或在MySQL客户端中执行**

   ```sql
   SOURCE SpringBoot/src/main/resources/init.sql;
   ```

## 数据库配置

数据库连接配置位于 `SpringBoot/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/naxi_pattern_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

**修改配置步骤**：

1. 根据实际环境修改 `url`、`username`、`password`
2. 确保MySQL服务器正在运行
3. 重启应用使配置生效

## 数据库表结构

### 核心表

| 表名             | 说明             | 主要字段                                  |
| ---------------- | ---------------- | ----------------------------------------- |
| users            | 用户表           | id, username, password, email, role_id    |
| roles            | 角色表           | id, role_name, description                |
| permissions      | 权限表           | id, role_id, permission_name              |
| patterns         | 纹样表           | id, name, category, image_url, view_count |
| collections      | 收藏表           | id, user_id, pattern_id                   |
| comments         | 评论表           | id, user_id, pattern_id, content          |
| questions        | 提问表           | id, user_id, pattern_id, title, content   |
| creative_works   | 原创作品表       | id, user_id, title, image_url, status     |
| operation_logs   | 操作历史表       | id, user_id, operation_type, target_type  |
| system_logs      | 系统日志表       | id, log_level, message                    |
| system_settings  | 系统参数表       | id, setting_key, setting_value            |
| admin_audit_logs | 管理员操作日志表 | id, admin_id, operation_type              |

### 索引优化

所有表都创建了以下索引以优化查询性能：

- **用户表**：username, email, status
- **纹样表**：category, name
- **收藏表**：user_id, pattern_id
- **评论表**：user_id, pattern_id, created_at
- **提问表**：user_id, pattern_id, created_at
- **原创作品表**：user_id, status, created_at
- **操作历史表**：user_id, operation_type, created_at
- **系统日志表**：log_level, created_at
- **管理员操作日志表**：admin_id, operation_type, created_at

## 初始数据

### 角色数据

| 角色名      | 说明       |
| ----------- | ---------- |
| SUPER_ADMIN | 超级管理员 |
| ADMIN       | 管理员     |
| USER        | 普通用户   |

### 权限分配

**超级管理员权限**：

- pattern_manage（纹样管理）
- user_manage（用户管理）
- content_review（内容审核）
- system_manage（系统管理）
- role_manage（角色管理）
- audit_log_view（审计日志查看）

**管理员权限**：

- pattern_manage
- user_manage
- content_review
- system_manage

**普通用户权限**：

- pattern_view（纹样查看）
- pattern_download（纹样下载）
- collection_manage（收藏管理）
- comment_publish（评论发布）
- question_publish（提问发布）
- work_upload（作品上传）

### 纹样分类

系统初始化了四类纹样，每类2个示例：

1. **七星纹**（2个）

   - 代表北斗七星，象征指引和方向
   - 应用场景：传统服饰、建筑装饰、现代设计
2. **东巴衍生纹**（2个）

   - 融合了象形文字特征
   - 应用场景：传统服饰、现代设计、文创产品
3. **日月纹**（2个）

   - 象征阴阳平衡和时间循环
   - 应用场景：传统服饰、建筑、现代艺术
4. **云纹水纹**（2个）

   - 代表自然元素，象征流动和变化
   - 应用场景：传统服饰、装饰、现代设计

### 系统参数

| 参数键               | 默认值                                  | 说明                     |
| -------------------- | --------------------------------------- | ------------------------ |
| carousel_interval    | 5000                                    | 轮播图切换间隔（毫秒）   |
| carousel_effect      | fade                                    | 轮播图切换效果           |
| max_upload_size      | 10485760                                | 最大上传文件大小（10MB） |
| items_per_page       | 12                                      | 每页显示项目数           |
| platform_name        | 纳西族纹样数字化展示平台                | 平台名称                 |
| platform_description | 展示和传播纳西族文化的轻量级Web应用系统 | 平台描述                 |

## 故障排除

### 问题1：连接被拒绝

**错误信息**：`Connection refused`

**解决方案**：

1. 确保MySQL服务器正在运行
2. 检查数据库连接配置（主机、端口、用户名、密码）
3. 确保数据库用户有创建数据库的权限

### 问题2：表已存在

**错误信息**：`Table already exists`

**解决方案**：

- 这是正常的，应用会自动跳过已存在的表
- 如需重新初始化，删除数据库后重启应用

### 问题3：权限不足

**错误信息**：`Access denied for user`

**解决方案**：

1. 确保数据库用户有足够权限
2. 使用root用户或具有CREATE、ALTER权限的用户

## 验证初始化

初始化完成后，可以通过以下方式验证：

1. **查看应用日志**

   ```
   初始化角色数据...
   角色数据初始化完成，共 3 个角色
   初始化权限数据...
   权限数据初始化完成，共 16 个权限
   初始化纹样数据...
   纹样数据初始化完成，共 8 个纹样
   初始化系统参数...
   系统参数初始化完成，共 6 个参数
   数据库初始化完成！
   ```
2. **查询数据库**

   ```sql
   SELECT COUNT(*) FROM roles;           -- 应返回 3
   SELECT COUNT(*) FROM permissions;    -- 应返回 16
   SELECT COUNT(*) FROM patterns;       -- 应返回 8
   SELECT COUNT(*) FROM system_settings; -- 应返回 6
   ```

## 后续操作

初始化完成后，可以：

1. 创建管理员用户账号
2. 上传真实的纹样素材
3. 配置系统参数
4. 开始使用平台

更多信息请参考项目的其他文档。
