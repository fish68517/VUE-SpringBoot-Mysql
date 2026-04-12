# 旅游规划与记忆系统 - 系统设计文档

## 概述

旅游规划与记忆系统采用前后端分离的现代 Web 架构。系统使用 Vue.js 3 构建响应式前端应用，使用 SpringBoot 构建高效的后端 RESTful 服务，使用 MySQL 作为数据持久化层。系统支持用户认证、旅游记录管理、地图足迹可视化、旅游计划管理和社交互动等核心功能。

## 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                     前端应用 (Vue.js 3)                      │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Pages (Views)                                       │   │
│  │  - LoginView, DashboardView, RecordListView, etc.   │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Components                                          │   │
│  │  - UserRegister, TravelRecordEditor, MapFootprint   │   │
│  │  - MultimediaUpload, CommentSection, etc.           │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  State Management (Pinia)                            │   │
│  │  - userStore, travelStore, planStore                │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Router (Vue Router)                                 │   │
│  │  - Protected Routes, Public Routes                   │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↕ (Axios HTTP)
┌─────────────────────────────────────────────────────────────┐
│                  后端服务 (SpringBoot)                       │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Controllers                                         │   │
│  │  - AuthController, UserController, TravelController │   │
│  │  - PlanController, SocialController, FileController │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Services (业务逻辑层)                               │   │
│  │  - AuthService, UserService, TravelService          │   │
│  │  - PlanService, SocialService, FileService          │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Repositories (数据访问层)                           │   │
│  │  - UserRepository, TravelRecordRepository, etc.      │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Security (Spring Security + JWT)                    │   │
│  │  - JwtTokenProvider, SecurityConfig                 │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↕ (JDBC/JPA)
┌─────────────────────────────────────────────────────────────┐
│                    MySQL 数据库                              │
│  - users, travel_records, multimedia_files, travel_plans    │
│  - itinerary_items, map_footprints, comments, likes         │
└─────────────────────────────────────────────────────────────┘
```

### 分层架构

**前端分层：**
- **View 层**: Vue 组件和页面，负责 UI 展示和用户交互
- **State 层**: Pinia store，管理全局应用状态
- **Service 层**: API 调用服务，使用 Axios 与后端通信
- **Router 层**: Vue Router，管理路由和导航

**后端分层：**
- **Controller 层**: 处理 HTTP 请求，验证输入，调用 Service 层
- **Service 层**: 实现业务逻辑，处理数据转换和验证
- **Repository 层**: 数据访问对象，使用 JPA/MyBatis 与数据库交互
- **Entity 层**: 数据模型，映射数据库表

## 组件与接口设计

### 前端主要组件

```
App.vue
├── Header (导航栏)
├── Sidebar (侧边栏)
├── Router View (路由视图)
│   ├── LoginView
│   │   └── UserRegister (注册表单)
│   │   └── UserLogin (登录表单)
│   ├── DashboardView (仪表板)
│   │   └── StatisticsCard (统计卡片)
│   ├── RecordListView (记录列表)
│   │   └── RecordCard (记录卡片)
│   │   └── RecordFilter (筛选器)
│   ├── RecordDetailView (记录详情)
│   │   └── TravelRecordEditor (富文本编辑器)
│   │   └── MultimediaUpload (多媒体上传)
│   │   └── MapFootprint (地图足迹)
│   │   └── CommentSection (评论区)
│   ├── PlanListView (计划列表)
│   │   └── PlanCard (计划卡片)
│   ├── PlanDetailView (计划详情)
│   │   └── ItineraryEditor (行程编辑器)
│   ├── SocialFeedView (社交动态)
│   │   └── FeedCard (动态卡片)
│   └── ProfileView (个人资料)
│       └── ProfileEditor (资料编辑)
└── Footer (页脚)
```

### 后端主要接口

**认证接口：**
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/refresh` - 刷新 JWT 令牌

**用户接口：**
- `GET /api/users/{id}` - 获取用户信息
- `PUT /api/users/{id}` - 更新用户信息
- `GET /api/users/{id}/profile` - 获取用户公开资料

**旅游记录接口：**
- `POST /api/travels` - 创建旅游记录
- `GET /api/travels` - 获取用户的旅游记录列表（分页）
- `GET /api/travels/{id}` - 获取单个旅游记录详情
- `PUT /api/travels/{id}` - 更新旅游记录
- `DELETE /api/travels/{id}` - 删除旅游记录
- `GET /api/travels/public` - 获取公开旅游记录列表（社交动态）

**多媒体接口：**
- `POST /api/files/upload` - 上传多媒体文件（分片上传）
- `GET /api/files/{id}` - 获取文件信息
- `DELETE /api/files/{id}` - 删除文件

**旅游计划接口：**
- `POST /api/plans` - 创建旅游计划
- `GET /api/plans` - 获取用户的旅游计划列表
- `GET /api/plans/{id}` - 获取单个旅游计划详情
- `PUT /api/plans/{id}` - 更新旅游计划
- `DELETE /api/plans/{id}` - 删除旅游计划

**行程项接口：**
- `POST /api/plans/{planId}/items` - 添加行程项
- `PUT /api/items/{id}` - 更新行程项
- `DELETE /api/items/{id}` - 删除行程项

**地图足迹接口：**
- `POST /api/footprints` - 添加地图足迹
- `GET /api/travels/{travelId}/footprints` - 获取旅游记录的所有足迹
- `DELETE /api/footprints/{id}` - 删除足迹

**社交接口：**
- `POST /api/comments` - 添加评论
- `GET /api/travels/{travelId}/comments` - 获取记录的评论列表
- `DELETE /api/comments/{id}` - 删除评论
- `POST /api/likes` - 添加点赞
- `DELETE /api/likes/{id}` - 取消点赞

## 数据模型设计

### 数据库表结构

**users 表（用户表）**
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  avatar_url VARCHAR(255),
  bio TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_email (email),
  INDEX idx_created_at (created_at)
);
```

**travel_records 表（旅游记录表）**
```sql
CREATE TABLE travel_records (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  destination VARCHAR(200) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  description TEXT,
  diary_content LONGTEXT,
  is_public BOOLEAN DEFAULT FALSE,
  view_count INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_is_public (is_public),
  INDEX idx_created_at (created_at)
);
```

**multimedia_files 表（多媒体文件表）**
```sql
CREATE TABLE multimedia_files (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  travel_record_id BIGINT NOT NULL,
  file_name VARCHAR(255) NOT NULL,
  file_path VARCHAR(500) NOT NULL,
  file_type VARCHAR(50) NOT NULL,
  file_size BIGINT NOT NULL,
  upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (travel_record_id) REFERENCES travel_records(id) ON DELETE CASCADE,
  INDEX idx_travel_record_id (travel_record_id)
);
```

**travel_plans 表（旅游计划表）**
```sql
CREATE TABLE travel_plans (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  destination VARCHAR(200) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  budget DECIMAL(10, 2),
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_created_at (created_at)
);
```

**itinerary_items 表（行程项表）**
```sql
CREATE TABLE itinerary_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  plan_id BIGINT NOT NULL,
  item_date DATE NOT NULL,
  item_type VARCHAR(50) NOT NULL,
  title VARCHAR(200) NOT NULL,
  description TEXT,
  location VARCHAR(200),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (plan_id) REFERENCES travel_plans(id) ON DELETE CASCADE,
  INDEX idx_plan_id (plan_id),
  INDEX idx_item_date (item_date)
);
```

**map_footprints 表（地图足迹表）**
```sql
CREATE TABLE map_footprints (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  travel_record_id BIGINT NOT NULL,
  location_name VARCHAR(200) NOT NULL,
  latitude DECIMAL(10, 8) NOT NULL,
  longitude DECIMAL(11, 8) NOT NULL,
  visit_date DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (travel_record_id) REFERENCES travel_records(id) ON DELETE CASCADE,
  INDEX idx_travel_record_id (travel_record_id)
);
```

**comments 表（评论表）**
```sql
CREATE TABLE comments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  travel_record_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (travel_record_id) REFERENCES travel_records(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_travel_record_id (travel_record_id),
  INDEX idx_user_id (user_id),
  INDEX idx_created_at (created_at)
);
```

**likes 表（点赞表）**
```sql
CREATE TABLE likes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  travel_record_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (travel_record_id) REFERENCES travel_records(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  UNIQUE KEY unique_like (travel_record_id, user_id),
  INDEX idx_travel_record_id (travel_record_id),
  INDEX idx_user_id (user_id)
);
```

### 实体关系图

```
users (1) ──────────── (N) travel_records
  │                          │
  │                          ├─── (1) ──────── (N) multimedia_files
  │                          │
  │                          ├─── (1) ──────── (N) map_footprints
  │                          │
  │                          ├─── (1) ──────── (N) comments
  │                          │
  │                          └─── (1) ──────── (N) likes
  │
  ├─── (1) ──────── (N) travel_plans
  │                      │
  │                      └─── (1) ──────── (N) itinerary_items
  │
  ├─── (1) ──────── (N) comments
  │
  └─── (1) ──────── (N) likes
```

## 错误处理策略

### 前端错误处理

1. **网络错误**: 使用 Axios 拦截器捕获网络错误，显示用户友好的错误提示
2. **验证错误**: 在表单提交前进行客户端验证，显示字段级错误信息
3. **API 错误**: 根据 HTTP 状态码显示相应的错误消息
4. **全局错误处理**: 使用 Vue 的全局错误处理器捕获未处理的异常

### 后端错误处理

1. **输入验证**: 使用 Bean Validation 验证请求参数
2. **业务异常**: 定义自定义异常类，在 Service 层抛出
3. **全局异常处理**: 使用 `@ControllerAdvice` 和 `@ExceptionHandler` 统一处理异常
4. **日志记录**: 记录所有异常和错误信息用于调试

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2024-01-01T12:00:00Z"
}
```

错误响应：
```json
{
  "code": 400,
  "message": "请求参数错误",
  "errors": [
    {
      "field": "email",
      "message": "邮箱格式不正确"
    }
  ],
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## 测试策略

### 前端测试

1. **单元测试**: 使用 Vitest 测试 Vue 组件和 Pinia store
2. **集成测试**: 测试组件间的交互和 API 调用
3. **E2E 测试**: 使用 Cypress 或 Playwright 测试完整的用户流程

### 后端测试

1. **单元测试**: 使用 JUnit 5 和 Mockito 测试 Service 和 Repository 层
2. **集成测试**: 使用 Spring Boot Test 测试 Controller 和数据库交互
3. **API 测试**: 使用 REST Assured 或 Postman 测试 API 端点

### 测试覆盖范围

- 用户认证和授权
- 旅游记录的 CRUD 操作
- 多媒体文件上传和删除
- 地图足迹的添加和显示
- 社交互动（评论、点赞）
- 数据验证和错误处理
- 权限检查和数据隐私

## 安全设计

### 认证与授权

1. **JWT 令牌**: 使用 JWT 进行无状态认证，令牌有效期 24 小时
2. **密码加密**: 使用 bcrypt 算法对密码进行哈希处理
3. **权限检查**: 在 Controller 层使用 `@PreAuthorize` 注解检查用户权限
4. **CORS 配置**: 配置跨域资源共享，仅允许来自前端应用的请求

### 数据保护

1. **输入验证**: 验证所有用户输入，防止 SQL 注入和 XSS 攻击
2. **HTTPS**: 使用 HTTPS 加密传输所有敏感数据
3. **文件上传验证**: 验证上传文件的类型和大小
4. **隐私保护**: 实现权限检查，确保用户只能访问自己的数据或公开数据

## 性能优化

1. **数据库索引**: 为频繁查询的字段创建索引
2. **分页查询**: 实现分页机制，减少单次查询的数据量
3. **缓存**: 使用 Redis 缓存热点数据（如用户信息、公开记录列表）
4. **文件压缩**: 对上传的图片进行压缩，减少存储空间
5. **异步处理**: 使用异步任务处理文件上传和删除操作
6. **前端优化**: 使用虚拟滚动显示大列表，懒加载图片

## 扩展性设计

1. **推荐系统**: 预留接口用于集成推荐算法
2. **AI 规划**: 预留接口用于集成 AI 旅游规划功能
3. **第三方集成**: 支持集成高德地图、阿里云 OSS 等第三方服务
4. **消息队列**: 使用 RabbitMQ 或 Kafka 处理异步任务
5. **微服务架构**: 为未来的微服务迁移预留接口设计

## 部署架构

```
┌─────────────────────────────────────────────┐
│           Nginx (反向代理)                   │
│  - 静态文件服务                              │
│  - 请求转发                                  │
└─────────────────────────────────────────────┘
         ↓                          ↓
┌──────────────────┐      ┌──────────────────┐
│  Vue.js 应用     │      │  SpringBoot 应用 │
│  (前端)          │      │  (后端)          │
└──────────────────┘      └──────────────────┘
                                  ↓
                        ┌──────────────────┐
                        │  MySQL 数据库    │
                        └──────────────────┘
```

## 开发工具与依赖

**前端依赖：**
- Vue.js 3
- Element Plus
- Vue Router
- Pinia
- Axios
- Quill (富文本编辑器)
- AMap (高德地图)
- Vitest (测试框架)

**后端依赖：**
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- MySQL Connector
- JWT (JSON Web Token)
- Lombok
- JUnit 5
- Mockito

