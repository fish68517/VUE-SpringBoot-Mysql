# 校园活动众筹与报名平台 - 设计文档

## 概述

校园活动众筹与报名平台是一个基于Spring Boot 3.x + Vue 3 + MySQL的全栈Web应用。系统采用前后端分离架构，后端提供RESTful API接口，前端通过Vue 3框架构建响应式用户界面。系统支持三类用户角色，采用蓝色配色方案，普通用户和组织者使用顶部菜单栏导航，管理员使用侧边导航栏。

## 系统架构

### 整体架构图

```
┌─────────────────────────────────────────────────────────────┐
│                     前端层 (Vue 3)                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ 普通用户界面  │  │ 组织者界面    │  │ 管理员界面    │      │
│  │ (顶部菜单栏)  │  │ (顶部菜单栏)  │  │ (侧边导航栏)  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│         │                  │                  │              │
│         └──────────────────┼──────────────────┘              │
│                            │                                 │
│                    Axios HTTP 请求                           │
└────────────────────────────┼─────────────────────────────────┘
                             │
┌────────────────────────────┼─────────────────────────────────┐
│                   API 网关层 (Spring Boot)                    │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              RESTful API 控制器                       │   │
│  │  /api/auth  /api/activity  /api/crowdfunding  ...   │   │
│  └──────────────────────────────────────────────────────┘   │
└────────────────────────────┼─────────────────────────────────┘
                             │
┌────────────────────────────┼─────────────────────────────────┐
│                   业务逻辑层 (Service)                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ 用户服务      │  │ 活动服务      │  │ 众筹服务      │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ 反馈服务      │  │ 审核服务      │  │ 统计服务      │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└────────────────────────────┼─────────────────────────────────┘
                             │
┌────────────────────────────┼─────────────────────────────────┐
│                   数据访问层 (Repository)                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ 用户仓储      │  │ 活动仓储      │  │ 众筹仓储      │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ 反馈仓储      │  │ 审核仓储      │  │ 统计仓储      │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└────────────────────────────┼─────────────────────────────────┘
                             │
┌────────────────────────────┼─────────────────────────────────┐
│                   数据库层 (MySQL)                            │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  用户表  活动表  众筹表  报名表  反馈表  审核表  ...  │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 技术栈

**后端：**
- Java 17
- Spring Boot 3.x
- Spring Data JPA (数据访问)
- Spring Security (身份认证与授权)
- MySQL 8.0 (数据库)
- Lombok (代码简化)
- Validation (数据验证)

**前端：**
- Vue 3 (框架)
- Vue Router (路由管理)
- Axios (HTTP客户端)
- Element Plus (UI组件库)
- ECharts (数据可视化)
- Pinia (状态管理)

**开发工具：**
- Maven (后端构建)
- npm/yarn (前端构建)
- Git (版本控制)

---

## 组件与接口设计

### 后端API接口

#### 认证模块 (/api/auth)

```
POST   /api/auth/register          - 用户注册
POST   /api/auth/login             - 用户登录
POST   /api/auth/logout            - 用户登出
GET    /api/auth/verify-token      - 验证令牌有效性
POST   /api/auth/refresh-token     - 刷新令牌
```

#### 用户模块 (/api/users)

```
GET    /api/users/profile          - 获取当前用户信息
PUT    /api/users/profile          - 更新用户信息
PUT    /api/users/password         - 修改密码
GET    /api/users/{id}             - 获取用户详情
GET    /api/users                  - 获取用户列表 (管理员)
PUT    /api/users/{id}/status      - 禁用/启用用户 (管理员)
```

#### 活动模块 (/api/activities)

```
GET    /api/activities             - 获取活动列表 (支持筛选、分页)
GET    /api/activities/{id}        - 获取活动详情
POST   /api/activities             - 创建活动 (组织者)
PUT    /api/activities/{id}        - 修改活动信息 (组织者)
DELETE /api/activities/{id}        - 删除活动 (组织者)
GET    /api/activities/{id}/stats  - 获取活动统计数据
```

#### 报名模块 (/api/registrations)

```
POST   /api/registrations          - 报名活动
DELETE /api/registrations/{id}     - 取消报名
GET    /api/registrations/my       - 获取我的报名列表
GET    /api/registrations          - 获取报名列表 (组织者/管理员)
GET    /api/registrations/{id}     - 获取报名详情
```

#### 众筹模块 (/api/crowdfunding)

```
GET    /api/crowdfunding/tiers/{activityId}  - 获取众筹档位列表
POST   /api/crowdfunding/support             - 支持众筹
GET    /api/crowdfunding/my                  - 获取我的众筹支持列表
GET    /api/crowdfunding/activity/{id}       - 获取活动众筹详情
GET    /api/crowdfunding/stats               - 获取众筹统计数据
```

#### 反馈模块 (/api/feedback)

```
POST   /api/feedback                - 提交反馈
GET    /api/feedback/activity/{id}  - 获取活动反馈列表
GET    /api/feedback/my             - 获取我的反馈列表
PUT    /api/feedback/{id}/reply     - 回复反馈 (组织者)
GET    /api/feedback/stats          - 获取反馈统计数据
```

#### 审核模块 (/api/audit)

```
GET    /api/audit/activities       - 获取待审核活动列表 (管理员)
PUT    /api/audit/activities/{id}  - 审核活动 (管理员)
GET    /api/audit/crowdfunding     - 获取待审核资金证明列表 (管理员)
PUT    /api/audit/crowdfunding/{id} - 审核资金证明 (管理员)
```

#### 统计模块 (/api/statistics)

```
GET    /api/statistics/dashboard   - 获取仪表板数据 (管理员)
GET    /api/statistics/activities  - 获取活动统计数据
GET    /api/statistics/crowdfunding - 获取众筹统计数据
GET    /api/statistics/feedback    - 获取反馈统计数据
GET    /api/statistics/export      - 导出报表 (管理员)
```

---

## 数据模型

### 核心实体关系图

```
┌─────────────────┐
│     用户表       │
│  (User)         │
├─────────────────┤
│ id (PK)         │
│ username        │
│ password        │
│ email           │
│ phone           │
│ role            │
│ status          │
│ created_at      │
└────────┬────────┘
         │
         ├─────────────────────────────────┐
         │                                 │
    ┌────▼──────────────┐      ┌──────────▼────────┐
    │   活动表           │      │  报名表            │
    │  (Activity)       │      │ (Registration)    │
    ├───────────────────┤      ├───────────────────┤
    │ id (PK)           │      │ id (PK)           │
    │ organizer_id (FK) │◄─────│ user_id (FK)      │
    │ title             │      │ activity_id (FK)  │
    │ description       │      │ status            │
    │ type              │      │ created_at        │
    │ start_time        │      └───────────────────┘
    │ end_time          │
    │ location          │      ┌───────────────────┐
    │ max_participants  │      │  众筹表            │
    │ registration_deadline│   │ (Crowdfunding)    │
    │ status            │      ├───────────────────┤
    │ created_at        │      │ id (PK)           │
    └────┬──────────────┘      │ activity_id (FK)  │
         │                     │ user_id (FK)      │
         │                     │ amount            │
         │                     │ tier_id           │
         │                     │ status            │
         │                     │ created_at        │
         │                     └───────────────────┘
         │
    ┌────▼──────────────┐      ┌───────────────────┐
    │  反馈表            │      │  审核表            │
    │  (Feedback)       │      │  (Audit)          │
    ├───────────────────┤      ├───────────────────┤
    │ id (PK)           │      │ id (PK)           │
    │ activity_id (FK)  │      │ activity_id (FK)  │
    │ user_id (FK)      │      │ auditor_id (FK)   │
    │ rating            │      │ status            │
    │ content           │      │ reason            │
    │ created_at        │      │ created_at        │
    └───────────────────┘      └───────────────────┘
```

### 主要数据表设计

**用户表 (users)**
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  phone VARCHAR(20),
  nickname VARCHAR(50),
  avatar_url VARCHAR(255),
  role ENUM('USER', 'ORGANIZER', 'ADMIN') DEFAULT 'USER',
  status ENUM('ACTIVE', 'DISABLED') DEFAULT 'ACTIVE',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**活动表 (activities)**
```sql
CREATE TABLE activities (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  organizer_id BIGINT NOT NULL,
  title VARCHAR(100) NOT NULL,
  description LONGTEXT,
  type VARCHAR(50),
  cover_url VARCHAR(255),
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  location VARCHAR(255),
  max_participants INT,
  registration_deadline DATETIME,
  enable_crowdfunding BOOLEAN DEFAULT FALSE,
  crowdfunding_target DECIMAL(10, 2),
  status ENUM('DRAFT', 'PENDING_AUDIT', 'APPROVED', 'REJECTED', 'ONGOING', 'ENDED', 'ARCHIVED') DEFAULT 'DRAFT',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (organizer_id) REFERENCES users(id)
);
```

**报名表 (registrations)**
```sql
CREATE TABLE registrations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  activity_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  status ENUM('REGISTERED', 'CANCELLED') DEFAULT 'REGISTERED',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (activity_id) REFERENCES activities(id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  UNIQUE KEY unique_registration (activity_id, user_id)
);
```

**众筹表 (crowdfunding_supports)**
```sql
CREATE TABLE crowdfunding_supports (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  activity_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  amount DECIMAL(10, 2) NOT NULL,
  tier_id BIGINT,
  payment_status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (activity_id) REFERENCES activities(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

**反馈表 (feedback)**
```sql
CREATE TABLE feedback (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  activity_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  rating INT CHECK (rating >= 1 AND rating <= 5),
  content LONGTEXT,
  reply_content LONGTEXT,
  reply_by BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (activity_id) REFERENCES activities(id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (reply_by) REFERENCES users(id)
);
```

---

## 前端页面结构

### 普通用户界面 (顶部菜单栏)

```
┌─────────────────────────────────────────────────────────┐
│  Logo  │ 首页 │ 我的活动 │ 我的众筹 │ 个人中心 │ 退出登录 │
└─────────────────────────────────────────────────────────┘
│                                                           │
│  ┌─────────────────────────────────────────────────────┐ │
│  │                   页面内容区域                        │ │
│  │                                                     │ │
│  │  - 首页: 活动列表、筛选、搜索                        │ │
│  │  - 活动详情: 报名、众筹、反馈、进度条               │ │
│  │  - 我的活动: 已报名活动列表                         │ │
│  │  - 我的众筹: 支持记录列表                           │ │
│  │  - 个人中心: 个人信息编辑、密码修改                 │ │
│  │                                                     │ │
│  └─────────────────────────────────────────────────────┘ │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

### 组织者界面 (顶部菜单栏)

```
┌─────────────────────────────────────────────────────────┐
│  Logo  │ 首页 │ 活动管理 │ 数据统计 │ 个人中心 │ 退出登录 │
└─────────────────────────────────────────────────────────┘
│                                                           │
│  ┌─────────────────────────────────────────────────────┐ │
│  │                   页面内容区域                        │ │
│  │                                                     │ │
│  │  - 首页: 活动列表、筛选                              │ │
│  │  - 活动管理: 创建活动、编辑活动、活动列表           │ │
│  │  - 数据统计: 统计图表、反馈汇总、资金管理           │ │
│  │  - 个人中心: 个人信息编辑、密码修改                 │ │
│  │                                                     │ │
│  └─────────────────────────────────────────────────────┘ │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

### 管理员界面 (侧边导航栏)

```
┌──────────────┬──────────────────────────────────────────┐
│              │                                          │
│  ┌────────┐  │  ┌────────────────────────────────────┐ │
│  │ 活动审核 │  │  │         页面内容区域              │ │
│  ├────────┤  │  │                                    │ │
│  │ 资金审核 │  │  │  - 活动审核: 待审核活动列表      │ │
│  ├────────┤  │  │  - 资金审核: 待审核资金证明列表   │ │
│  │ 用户管理 │  │  │  - 用户管理: 用户列表、禁用/启用 │ │
│  ├────────┤  │  │  - 数据统计: 统计图表、报表导出   │ │
│  │ 数据统计 │  │  │  - 个人中心: 个人信息编辑        │ │
│  ├────────┤  │  │                                    │ │
│  │ 个人中心 │  │  │                                    │ │
│  ├────────┤  │  │                                    │ │
│  │ 退出登录 │  │  │                                    │ │
│  └────────┘  │  └────────────────────────────────────┘ │
│              │                                          │
└──────────────┴──────────────────────────────────────────┘
```

---

## 错误处理

### 后端错误处理策略

**统一响应格式：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

**错误响应格式：**
```json
{
  "code": 400,
  "message": "请求参数错误",
  "errors": [
    {
      "field": "email",
      "message": "邮箱格式不正确"
    }
  ]
}
```

**常见错误码：**
- 200: 成功
- 400: 请求参数错误
- 401: 未授权 (需要登录)
- 403: 禁止访问 (权限不足)
- 404: 资源不存在
- 409: 冲突 (如重复报名)
- 500: 服务器内部错误

### 前端错误处理

- 表单验证错误: 在表单字段下方显示红色错误提示
- 网络请求错误: 显示Toast提示，包含错误信息
- 业务逻辑错误: 显示Modal对话框，提示用户采取行动
- 未授权错误: 自动重定向到登录页面

---

## 测试策略

### 后端测试

**单元测试：**
- Service层业务逻辑测试 (使用JUnit 5 + Mockito)
- Repository层数据访问测试 (使用H2内存数据库)
- 工具类和验证器测试

**集成测试：**
- API端点集成测试 (使用MockMvc)
- 数据库事务测试
- 认证与授权测试

### 前端测试

**单元测试：**
- Vue组件单元测试 (使用Vitest)
- 工具函数测试
- 状态管理测试

**集成测试：**
- 页面路由测试
- 用户交互流程测试
- API集成测试

---

## 安全性设计

### 认证与授权

- 使用JWT (JSON Web Token) 进行身份认证
- 密码使用BCrypt加密存储
- 基于角色的访问控制 (RBAC)
- 令牌过期时间设置为24小时

### 数据安全

- 所有API请求使用HTTPS加密传输
- 敏感数据 (密码、支付信息) 不在日志中记录
- 数据库连接使用连接池管理
- 定期备份数据库

### 防护措施

- CSRF防护: 使用Spring Security的CSRF令牌
- XSS防护: 对用户输入进行HTML转义
- SQL注入防护: 使用参数化查询
- 速率限制: 对登录、支付等敏感操作进行速率限制

---

## 性能优化

### 后端优化

- 使用数据库索引加速查询
- 实现缓存机制 (Redis) 缓存热点数据
- 使用分页查询减少数据传输
- 异步处理耗时操作 (如发送通知)

### 前端优化

- 使用路由懒加载减少初始加载时间
- 实现虚拟滚动优化长列表渲染
- 使用图片懒加载和压缩
- 启用Gzip压缩减少传输大小

---

## 部署架构

```
┌─────────────────────────────────────────────────────────┐
│                    Nginx (反向代理)                      │
│                  (处理静态资源、负载均衡)                 │
└────────────────────┬────────────────────────────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
   ┌────▼────┐  ┌────▼────┐  ┌────▼────┐
   │ Spring   │  │ Spring   │  │ Spring   │
   │ Boot 1   │  │ Boot 2   │  │ Boot 3   │
   │ (8081)   │  │ (8082)   │  │ (8083)   │
   └────┬─────┘  └────┬─────┘  └────┬─────┘
        │             │             │
        └─────────────┼─────────────┘
                      │
              ┌───────▼────────┐
              │   MySQL 8.0    │
              │   (主从复制)    │
              └────────────────┘
```

