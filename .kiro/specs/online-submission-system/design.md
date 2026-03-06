# 在线投稿系统 - 系统设计文档

## 概述

在线投稿系统是一个基于Spring Boot 3.x和Vue 3的前后端分离Web应用。系统采用分层架构设计，支持四类用户角色的协作工作流程。后端提供RESTful API接口，前端通过Vue 3框架实现响应式用户界面。系统使用MySQL数据库存储所有业务数据，支持图片上传和静态资源访问。

## 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                     前端层 (Vue 3)                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  作者端      │  │  编辑端      │  │  审稿人端    │      │
│  │ (顶部导航)   │  │ (左侧边栏)   │  │ (左侧边栏)   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────────────────────────────────────────────┐   │
│  │           管理员端 (左侧边栏)                        │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Vue Router | Pinia | Axios | Element Plus          │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                          ↓ HTTP/REST
┌─────────────────────────────────────────────────────────────┐
│                  后端层 (Spring Boot 3.x)                    │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              Controller 层 (API接口)                 │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              Service 层 (业务逻辑)                   │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              Mapper 层 (数据访问)                    │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Config: CORS | 静态资源映射 | 文件上传配置         │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                          ↓ SQL
┌─────────────────────────────────────────────────────────────┐
│                   数据层 (MySQL)                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  用户表 | 稿件表 | 栏目表 | 审稿表 | 消息表 | 配置表 │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 分层设计

- **Controller层**: 处理HTTP请求，参数验证，调用Service层
- **Service层**: 实现业务逻辑，事务管理，调用Mapper层
- **Mapper层**: 数据库操作，使用MyBatis进行ORM映射
- **Entity层**: 数据库实体类，对应数据库表结构
- **DTO层**: 数据传输对象，用于前后端数据交互
- **VO层**: 视图对象，用于返回给前端的数据结构
- **Config层**: 系统配置，包括CORS、静态资源映射、文件上传等

## 组件与接口设计

### 核心模块

#### 1. 用户认证模块 (User Authentication)

**功能**: 用户注册、登录、退出登录、会话管理

**主要接口**:
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户退出登录
- `GET /api/auth/current-user` - 获取当前用户信息

**会话管理**: 使用HttpSession存储用户身份和角色信息

#### 2. 用户管理模块 (User Management)

**功能**: 用户信息管理、权限分配、用户审核

**主要接口**:
- `GET /api/users` - 查询用户列表
- `GET /api/users/{id}` - 获取用户详情
- `PUT /api/users/{id}` - 修改用户信息
- `DELETE /api/users/{id}` - 删除用户
- `GET /api/users/pending-approval` - 获取待审核用户
- `POST /api/users/{id}/approve` - 批准用户注册
- `POST /api/users/{id}/reject` - 驳回用户注册

#### 3. 稿件管理模块 (Manuscript Management)

**功能**: 稿件提交、初审、分配、进度跟踪

**主要接口**:
- `POST /api/manuscripts` - 提交稿件
- `GET /api/manuscripts` - 查询稿件列表
- `GET /api/manuscripts/{id}` - 获取稿件详情
- `PUT /api/manuscripts/{id}` - 修改稿件
- `DELETE /api/manuscripts/{id}` - 删除稿件
- `POST /api/manuscripts/{id}/review` - 提交初审意见
- `POST /api/manuscripts/{id}/assign-reviewer` - 分配审稿人
- `GET /api/manuscripts/{id}/review-history` - 获取审稿历史

#### 4. 审稿管理模块 (Review Management)

**功能**: 审稿任务分配、审稿意见提交、历史查询

**主要接口**:
- `GET /api/reviews` - 查询审稿任务列表
- `GET /api/reviews/{id}` - 获取审稿任务详情
- `POST /api/reviews/{id}/accept` - 接受审稿任务
- `POST /api/reviews/{id}/reject` - 拒绝审稿任务
- `POST /api/reviews/{id}/submit` - 提交审稿意见
- `GET /api/reviews/history` - 查询审稿历史

#### 5. 栏目管理模块 (Category Management)

**功能**: 栏目的增删改查、投稿要求设置

**主要接口**:
- `GET /api/categories` - 查询栏目列表
- `POST /api/categories` - 创建栏目
- `PUT /api/categories/{id}` - 修改栏目
- `DELETE /api/categories/{id}` - 删除栏目
- `GET /api/categories/{id}/requirements` - 获取栏目投稿要求

#### 6. 系统配置模块 (System Configuration)

**功能**: 审核流程、格式要求、通知模板配置

**主要接口**:
- `GET /api/config` - 获取系统配置
- `PUT /api/config` - 修改系统配置
- `GET /api/config/templates` - 获取通知模板
- `PUT /api/config/templates/{id}` - 修改通知模板

#### 7. 消息通知模块 (Message & Notification)

**功能**: 系统通知、消息发送、消息查询

**主要接口**:
- `GET /api/messages` - 查询消息列表
- `GET /api/messages/{id}` - 获取消息详情
- `POST /api/messages` - 发送消息
- `PUT /api/messages/{id}/read` - 标记消息为已读

#### 8. 文件上传模块 (File Upload)

**功能**: 稿件文档上传、图片上传、文件管理

**主要接口**:
- `POST /api/upload/manuscript` - 上传稿件文档
- `POST /api/upload/image` - 上传图片
- `GET /image/**` - 访问上传的图片（静态资源映射）

#### 9. 数据统计模块 (Statistics)

**功能**: 投稿量统计、通过率统计、用户活跃度统计

**主要接口**:
- `GET /api/statistics/submissions` - 获取投稿统计
- `GET /api/statistics/approval-rate` - 获取通过率统计
- `GET /api/statistics/user-activity` - 获取用户活跃度统计
- `GET /api/statistics/report` - 生成统计报表

## 数据模型设计

### 核心数据表

#### 1. 用户表 (users)
```
- id (主键)
- username (用户名，唯一)
- password (密码，明文存储)
- email (邮箱)
- phone (电话)
- role (角色: AUTHOR, EDITOR, REVIEWER, ADMIN)
- status (状态: PENDING, APPROVED, REJECTED, ACTIVE, INACTIVE)
- created_at (创建时间)
- updated_at (更新时间)
- 角色特定字段:
  - 作者: academic_achievements (学术成果)
  - 编辑: work_email (工作邮箱)
  - 审稿人: expertise_areas (专业领域), research_directions (研究方向)
```

#### 2. 稿件表 (manuscripts)
```
- id (主键)
- author_id (作者ID，外键)
- category_id (栏目ID，外键)
- title (稿件标题)
- abstract (摘要)
- content (内容)
- file_path (文件路径)
- status (状态: DRAFT, SUBMITTED, UNDER_REVIEW, REVISION_REQUIRED, ACCEPTED, REJECTED)
- submission_date (投稿日期)
- created_at (创建时间)
- updated_at (更新时间)
```

#### 3. 栏目表 (categories)
```
- id (主键)
- name (栏目名称)
- description (栏目描述)
- requirements (投稿要求)
- file_format (文件格式要求)
- max_file_size (最大文件大小)
- word_count_min (最小字数)
- word_count_max (最大字数)
- created_at (创建时间)
- updated_at (更新时间)
```

#### 4. 审稿表 (reviews)
```
- id (主键)
- manuscript_id (稿件ID，外键)
- reviewer_id (审稿人ID，外键)
- editor_id (编辑ID，外键)
- status (状态: PENDING, ACCEPTED, REJECTED, SUBMITTED)
- opinion (审稿意见)
- score (评分)
- recommendation (建议: ACCEPT, ACCEPT_WITH_REVISION, REJECT)
- submitted_date (提交日期)
- created_at (创建时间)
- updated_at (更新时间)
```

#### 5. 初审表 (initial_reviews)
```
- id (主键)
- manuscript_id (稿件ID，外键)
- editor_id (编辑ID，外键)
- status (状态: PASS, REJECT, REVISION_REQUIRED)
- opinion (初审意见)
- created_at (创建时间)
- updated_at (更新时间)
```

#### 6. 消息表 (messages)
```
- id (主键)
- sender_id (发送者ID，外键)
- recipient_id (接收者ID，外键)
- manuscript_id (稿件ID，外键，可选)
- content (消息内容)
- type (消息类型: NOTIFICATION, COMMUNICATION)
- is_read (是否已读)
- created_at (创建时间)
```

#### 7. 系统配置表 (system_config)
```
- id (主键)
- config_key (配置键)
- config_value (配置值)
- description (描述)
- created_at (创建时间)
- updated_at (更新时间)
```

#### 8. 通知模板表 (notification_templates)
```
- id (主键)
- template_name (模板名称)
- template_content (模板内容)
- template_type (模板类型: SUBMISSION, REVIEW, ACCEPTANCE, REJECTION)
- created_at (创建时间)
- updated_at (更新时间)
```

## 前端架构设计

### 项目结构

```
VUE/src/
├── components/          # 可复用组件
│   ├── common/         # 通用组件
│   ├── layout/         # 布局组件
│   └── form/           # 表单组件
├── views/              # 页面组件
│   ├── auth/           # 认证相关页面
│   ├── author/         # 作者端页面
│   ├── editor/         # 编辑端页面
│   ├── reviewer/       # 审稿人端页面
│   └── admin/          # 管理员端页面
├── router/             # 路由配置
├── stores/             # Pinia状态管理
├── services/           # API服务
├── utils/              # 工具函数
├── styles/             # 全局样式
└── App.vue             # 根组件
```

### 路由设计

- `/login` - 登录页面
- `/register` - 注册页面
- `/author/*` - 作者端页面（顶部导航布局）
- `/editor/*` - 编辑端页面（左侧边栏布局）
- `/reviewer/*` - 审稿人端页面（左侧边栏布局）
- `/admin/*` - 管理员端页面（左侧边栏布局）

### 状态管理 (Pinia)

- **userStore**: 用户信息、登录状态、角色权限
- **manuscriptStore**: 稿件列表、详情、操作状态
- **reviewStore**: 审稿任务、审稿意见
- **messageStore**: 消息列表、未读消息数
- **configStore**: 系统配置、栏目信息

### UI框架

- **Element Plus**: 提供丰富的UI组件
- **Vue Router**: 前端路由管理
- **Axios**: HTTP请求库
- **Pinia**: 状态管理库

## 后端架构设计

### 项目结构

```
SpringBoot/src/main/java/com/submission/
├── controller/         # 控制器层
├── service/            # 业务逻辑层
├── mapper/             # 数据访问层
├── entity/             # 实体类
├── dto/                # 数据传输对象
├── vo/                 # 视图对象
├── config/             # 配置类
├── exception/          # 异常处理
├── utils/              # 工具类
└── SubmissionApplication.java  # 启动类
```

### 核心配置

#### 1. CORS配置 (WebConfig)
- 允许所有来源的请求
- 支持所有HTTP方法
- 允许所有请求头

#### 2. 静态资源映射 (WebConfig)
- 映射 `/image/**` 到 `file:image/` 目录
- 支持图片文件的直接访问

#### 3. 文件上传配置 (application.yml)
- 上传目录: `image/`
- 最大文件大小: 10MB
- 支持的文件类型: pdf, doc, docx, jpg, png, gif

#### 4. 数据库配置 (application.yml)
- 使用MySQL 8.0+
- 连接池: HikariCP
- 字符编码: UTF-8

### 异常处理

- 统一异常处理器 (GlobalExceptionHandler)
- 自定义业务异常 (BusinessException)
- 返回统一的错误响应格式

### 工具类

- **FileUploadUtil**: 文件上传处理
- **DateUtil**: 日期时间处理
- **StringUtil**: 字符串处理
- **ValidationUtil**: 数据验证

## 错误处理

### 后端错误处理

- 统一的HTTP状态码使用
- 统一的错误响应格式:
  ```json
  {
    "code": 400,
    "message": "错误信息",
    "data": null
  }
  ```
- 业务异常捕获和处理
- 数据库异常处理
- 文件操作异常处理

### 前端错误处理

- HTTP请求拦截器处理错误
- 统一的错误提示
- 异常状态下的用户提示
- 网络错误重试机制

## 测试策略

### 后端测试

- **单元测试**: 测试Service层的业务逻辑
- **集成测试**: 测试Controller层的API接口
- **数据库测试**: 测试Mapper层的数据操作

### 前端测试

- **组件测试**: 测试Vue组件的功能
- **路由测试**: 测试路由导航
- **API集成测试**: 测试前后端交互

### 测试工具

- 后端: JUnit 5, Mockito, Spring Boot Test
- 前端: Vitest, Vue Test Utils

## 安全考虑

### 当前设计的安全特点

- 密码明文存储（按需求）
- 无加密传输（按需求）
- 无权限框架（按需求）
- 基于会话的用户识别

### 建议的安全改进（未来）

- 密码加密存储
- HTTPS传输
- 权限框架集成
- 审计日志记录

## 部署架构

### 开发环境

- 后端: Spring Boot 3.x 内置Tomcat
- 前端: Vue 3 开发服务器
- 数据库: MySQL 8.0+

### 生产环境

- 后端: Docker容器化部署
- 前端: 静态文件部署到CDN或Web服务器
- 数据库: 独立MySQL服务器
- 反向代理: Nginx

## 性能考虑

- 数据库查询优化（索引设计）
- 分页查询处理大数据集
- 缓存策略（可选）
- 文件上传大小限制
- 前端懒加载和代码分割

## 扩展性设计

- 模块化的代码结构
- 清晰的接口定义
- 易于添加新的角色和功能
- 支持新的栏目类型
- 可配置的审核流程
