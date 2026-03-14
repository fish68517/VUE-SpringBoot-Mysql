# 设计文档 - 管理员Web系统

## 概述

管理员Web系统是一个基于Vue 3 + Spring Boot的后台管理系统。前端采用Vue 3 + Vue Router + Pinia + Element Plus构建，后端采用Spring Boot + MySQL实现。系统提供用户管理、社区审核、健身计划管理等功能。

## 架构

### 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                     前端 (Vue 3)                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Pages (登录、用户管理、社区管理等)                    │   │
│  │  Components (表格、表单、对话框等)                    │   │
│  │  Router (路由配置)                                   │   │
│  │  Store (Pinia状态管理)                               │   │
│  │  API (Axios HTTP客户端)                              │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↕ HTTP/JSON
┌─────────────────────────────────────────────────────────────┐
│                   后端 (Spring Boot)                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Controller (API端点)                                │   │
│  │  Service (业务逻辑)                                  │   │
│  │  Mapper (数据访问)                                   │   │
│  │  Entity (数据模型)                                   │   │
│  │  Config (CORS、静态资源配置)                         │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↕ SQL
┌─────────────────────────────────────────────────────────────┐
│                    MySQL 数据库                              │
│  (用户表、帖子表、评论表、健身计划表、运动数据表等)          │
└─────────────────────────────────────────────────────────────┘
```

### 技术栈

**前端:**
- Vue 3 (框架)
- Vue Router (路由)
- Pinia (状态管理)
- Axios (HTTP客户端)
- Element Plus (UI组件库)

**后端:**
- Spring Boot (框架)
- MySQL (数据库)
- MyBatis (ORM)
- Maven (构建工具)

## 组件与接口

### 前端组件结构

```
src/
├── pages/
│   ├── Login.vue              # 登录页面
│   ├── Dashboard.vue          # 管理员主页
│   ├── UserManagement.vue     # 用户管理页面
│   ├── CommunityManagement.vue # 社区管理页面
│   ├── FitnessPlanManagement.vue # 健身计划管理页面
│   └── ExerciseDataManagement.vue # 运动数据管理页面
├── components/
│   ├── Sidebar.vue            # 左侧边栏
│   ├── Header.vue             # 顶部栏
│   ├── UserTable.vue          # 用户表格
│   ├── PostTable.vue          # 帖子表格
│   └── ...
├── router/
│   └── index.js               # 路由配置
├── store/
│   └── index.js               # Pinia状态管理
├── api/
│   ├── user.js                # 用户API
│   ├── community.js           # 社区API
│   ├── fitnessPlan.js         # 健身计划API
│   └── exerciseData.js        # 运动数据API
└── App.vue
```

### 后端API接口

#### 认证接口
- `POST /api/admin/login` - 管理员登录
- `POST /api/admin/logout` - 管理员登出

#### 用户管理接口
- `GET /api/admin/users` - 获取用户列表
- `GET /api/admin/users/{id}` - 获取用户详情
- `PUT /api/admin/users/{id}` - 编辑用户信息
- `DELETE /api/admin/users/{id}` - 删除用户

#### 社区管理接口
- `GET /api/admin/posts` - 获取帖子列表
- `GET /api/admin/posts/{id}` - 获取帖子详情
- `DELETE /api/admin/posts/{id}` - 删除帖子
- `DELETE /api/admin/comments/{id}` - 删除评论

#### 健身计划接口
- `GET /api/admin/fitness-plans` - 获取健身计划列表
- `GET /api/admin/fitness-plans/{id}` - 获取计划详情
- `PUT /api/admin/fitness-plans/{id}` - 编辑健身计划
- `DELETE /api/admin/fitness-plans/{id}` - 删除健身计划

#### 运动数据接口
- `GET /api/admin/exercise-data` - 获取运动数据列表
- `GET /api/admin/exercise-data/{id}` - 获取运动数据详情
- `DELETE /api/admin/exercise-data/{id}` - 删除运动数据

#### 图片上传接口
- `POST /api/admin/upload` - 上传图片
- `GET /image/**` - 访问图片资源

## 数据模型

### 用户表 (users)
```
id (PK)
username (VARCHAR)
password (VARCHAR) - 明文存储
email (VARCHAR)
height (DECIMAL) - 身高
weight (DECIMAL) - 体重
avatar (VARCHAR) - 头像路径
created_at (TIMESTAMP)
updated_at (TIMESTAMP)
```

### 帖子表 (posts)
```
id (PK)
user_id (FK)
content (TEXT)
image_path (VARCHAR)
likes_count (INT)
created_at (TIMESTAMP)
updated_at (TIMESTAMP)
```

### 评论表 (comments)
```
id (PK)
post_id (FK)
user_id (FK)
content (TEXT)
created_at (TIMESTAMP)
updated_at (TIMESTAMP)
```

### 健身计划表 (fitness_plans)
```
id (PK)
user_id (FK)
goal (TEXT)
plan_content (TEXT)
created_at (TIMESTAMP)
updated_at (TIMESTAMP)
```

### 运动数据表 (exercise_data)
```
id (PK)
user_id (FK)
exercise_type (VARCHAR) - 运动方式
location (VARCHAR) - 运动地点
duration (INT) - 运动时长
created_at (TIMESTAMP)
updated_at (TIMESTAMP)
```

## 错误处理

### 统一响应格式

**成功响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

**错误响应:**
```json
{
  "code": 400,
  "message": "错误信息描述",
  "data": null
}
```

### 常见错误码
- 200: 成功
- 400: 请求参数错误
- 401: 未授权/登录失败
- 403: 禁止访问
- 404: 资源不存在
- 500: 服务器错误

### 异常处理
- 全局异常处理器捕获所有异常
- 返回统一的错误响应格式
- 记录错误日志用于调试

## 测试策略

### 前端测试
- 单元测试: 测试API调用、状态管理逻辑
- 集成测试: 测试页面交互、表单提交
- E2E测试: 测试完整的用户流程

### 后端测试
- 单元测试: 测试Service层业务逻辑
- 集成测试: 测试Controller和数据库交互
- API测试: 使用Postman或类似工具测试所有接口

### 测试覆盖范围
- 用户认证流程
- CRUD操作
- 错误处理
- 权限验证
- 图片上传和访问

## 部署与配置

### Spring Boot配置
- 配置CORS允许所有来源
- 配置静态资源映射 /image/** -> image文件夹
- 配置MySQL数据库连接
- 配置日志输出为中文

### Vue 3配置
- 配置API基础URL
- 配置路由懒加载
- 配置Axios拦截器处理认证

### 文件结构
```
项目根目录/
├── SpringBoot/          # 后端项目
│   ├── src/
│   ├── pom.xml
│   └── image/           # 图片存储目录
├── VUE/                 # 前端项目
│   ├── src/
│   ├── package.json
│   └── vite.config.js
└── doc/                 # 文档
```

