# 医校联动实习平台 - 设计文档

## 概述

医校联动实习平台采用前后端分离架构，使用SpringBoot作为后端框架，Vue作为前端框架，MySQL作为数据库。系统支持多学校、多医院的并行运营，通过组织隔离实现数据安全。核心业务流程包括岗位发布、申请审批、实习过程管理和数据统计。

## 架构设计

### 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                     前端层 (Vue)                              │
│  ┌──────────────┬──────────────┬──────────────┬──────────────┐
│  │  学生端      │  老师端      │  学校管理端  │  医院管理端  │
│  └──────────────┴──────────────┴──────────────┴──────────────┘
└─────────────────────────────────────────────────────────────┘
                          ↓ HTTP/REST API
┌─────────────────────────────────────────────────────────────┐
│                   后端层 (SpringBoot)                         │
│  ┌──────────────────────────────────────────────────────────┐
│  │  Controller 层 (API 接口)                                 │
│  │  - UserController, PostController, ApplicationController │
│  │  - InternshipController, NotificationController          │
│  └──────────────────────────────────────────────────────────┘
│  ┌──────────────────────────────────────────────────────────┐
│  │  Service 层 (业务逻辑)                                    │
│  │  - UserService, PostService, ApplicationService          │
│  │  - InternshipService, NotificationService                │
│  └──────────────────────────────────────────────────────────┘
│  ┌──────────────────────────────────────────────────────────┐
│  │  Repository 层 (数据访问)                                 │
│  │  - JPA Repositories for all entities                      │
│  └──────────────────────────────────────────────────────────┘
└─────────────────────────────────────────────────────────────┘
                          ↓ JDBC
┌─────────────────────────────────────────────────────────────┐
│                   数据库层 (MySQL)                            │
│  - 用户表、组织表、岗位表、申请表、实习记录表等              │
└─────────────────────────────────────────────────────────────┘
```

### 技术栈

- **后端**：SpringBoot 2.7+, Spring Data JPA, MySQL Driver
- **前端**：Vue 3, Axios, Element Plus UI
- **数据库**：MySQL 5.7+
- **其他**：Lombok, Validation, Jackson

## 数据模型

### 核心实体关系图

```
User (用户)
├── id (主键)
├── username (用户名)
├── password (密码)
├── email (邮箱)
├── role (角色: ADMIN/SCHOOL_ADMIN/HOSPITAL_ADMIN/STUDENT/TEACHER)
├── organization_id (组织ID - 外键)
├── status (状态: PENDING/APPROVED/REJECTED)
└── created_at (创建时间)

Organization (组织)
├── id (主键)
├── name (组织名称)
├── type (类型: SCHOOL/HOSPITAL)
├── code (组织代码)
└── created_at (创建时间)

Post (岗位)
├── id (主键)
├── hospital_id (医院ID - 外键)
├── title (岗位名称)
├── department (科室)
├── description (岗位描述)
├── quota (名额)
├── duration (实习周期)
├── visible_schools (面向学校范围 - JSON)
├── status (状态: DRAFT/PUBLISHED/ARCHIVED)
└── created_at (创建时间)

Application (申请)
├── id (主键)
├── student_id (学生ID - 外键)
├── post_id (岗位ID - 外键)
├── reason (申请理由)
├── school_status (学校审批状态: PENDING/APPROVED/REJECTED)
├── hospital_status (医院审批状态: PENDING/APPROVED/REJECTED)
├── school_opinion (学校审批意见)
├── hospital_opinion (医院审批意见)
└── created_at (创建时间)

Internship (实习记录)
├── id (主键)
├── application_id (申请ID - 外键)
├── student_id (学生ID - 外键)
├── teacher_id (带教老师ID - 外键)
├── post_id (岗位ID - 外键)
├── start_date (开始日期)
├── end_date (结束日期)
├── status (状态: ONGOING/COMPLETED/TERMINATED)
└── created_at (创建时间)

WeeklyReport (周记)
├── id (主键)
├── internship_id (实习记录ID - 外键)
├── week_number (周次)
├── content (周记内容)
├── teacher_comment (老师评语)
├── teacher_score (老师评分)
├── status (状态: SUBMITTED/REVIEWED/REJECTED)
└── created_at (创建时间)

Evaluation (评价)
├── id (主键)
├── internship_id (实习记录ID - 外键)
├── evaluator_id (评价人ID - 外键)
├── evaluator_type (评价人类型: TEACHER/STUDENT)
├── score (评分)
├── comment (评价内容)
└── created_at (创建时间)

Notification (通知)
├── id (主键)
├── user_id (用户ID - 外键)
├── type (通知类型: APPLICATION_RESULT/WEEKLY_REJECTED/TASK_DEADLINE)
├── content (通知内容)
├── is_read (是否已读)
└── created_at (创建时间)
```

## 组件与接口设计

### 后端API接口清单

#### 用户管理接口
- `POST /api/users/register` - 用户注册
- `POST /api/users/login` - 用户登录
- `GET /api/users/profile` - 获取个人信息
- `PUT /api/users/profile` - 更新个人信息
- `GET /api/users/organizations` - 获取用户所属组织

#### 组织管理接口（系统管理员）
- `GET /api/organizations` - 获取所有组织
- `POST /api/organizations` - 创建组织
- `PUT /api/organizations/{id}` - 更新组织信息
- `GET /api/organizations/{id}/users` - 获取组织内用户

#### 岗位管理接口
- `GET /api/posts` - 获取岗位列表（学生端：仅显示本校可见的岗位）
- `POST /api/posts` - 创建岗位（医院管理员）
- `PUT /api/posts/{id}` - 更新岗位（医院管理员）
- `PUT /api/posts/{id}/status` - 上下架岗位（医院管理员）
- `GET /api/posts/{id}` - 获取岗位详情

#### 申请管理接口
- `POST /api/applications` - 提交申请（学生）
- `GET /api/applications` - 获取申请列表
- `GET /api/applications/{id}` - 获取申请详情
- `PUT /api/applications/{id}/school-review` - 学校审批（学校管理员）
- `PUT /api/applications/{id}/hospital-review` - 医院审批（医院管理员）

#### 实习管理接口
- `GET /api/internships` - 获取实习记录列表
- `GET /api/internships/{id}` - 获取实习记录详情
- `POST /api/internships/{id}/weekly-reports` - 提交周记（学生）
- `GET /api/internships/{id}/weekly-reports` - 获取周记列表
- `PUT /api/internships/{id}/weekly-reports/{reportId}/review` - 批阅周记（老师）
- `POST /api/internships/{id}/evaluations` - 提交评价
- `GET /api/internships/{id}/evaluations` - 获取评价列表

#### 统计接口
- `GET /api/statistics/school` - 学校数据统计（学校管理员）
- `GET /api/statistics/hospital` - 医院数据统计（医院管理员）

#### 通知接口
- `GET /api/notifications` - 获取通知列表
- `PUT /api/notifications/{id}/read` - 标记通知为已读

### 前端页面结构

#### 学生端
- 登录/注册页面
- 岗位浏览页面（列表、筛选、详情）
- 我的申请页面（申请列表、申请详情、撤回）
- 实习过程页面（任务列表、周记提交、评价）
- 个人中心页面（基本信息、认证状态、通知）

#### 带教老师端
- 登录页面
- 任务管理页面（创建任务、任务列表、学生提交情况）
- 周记批阅页面（周记列表、批阅、评分）
- 评价页面（学生评价、老师评价）
- 个人中心页面

#### 学校管理员端
- 登录页面
- 学生认证管理页面（待认证列表、认证审核）
- 申请审批页面（申请列表、审批、意见）
- 学校数据看板页面（统计数据、图表展示）
- 个人中心页面

#### 医院管理员端
- 登录页面
- 岗位管理页面（岗位列表、创建/编辑岗位、上下架）
- 申请审批页面（申请列表、审批、意见）
- 带教老师管理页面（老师列表、分配学生）
- 医院数据看板页面（统计数据、图表展示）
- 个人中心页面

#### 系统管理员端
- 登录页面
- 组织管理页面（学校/医院列表、创建、编辑）
- 用户认证审核页面（待认证用户、审核通过/驳回）
- 系统配置页面（菜单配置、角色配置）

## 错误处理

### 异常处理策略

1. **业务异常**：
   - 岗位名额已满：返回 400 Bad Request
   - 申请状态不符合操作条件：返回 400 Bad Request
   - 数据不存在：返回 404 Not Found
   - 权限不足：返回 403 Forbidden

2. **系统异常**：
   - 数据库连接失败：返回 500 Internal Server Error
   - 服务器内部错误：返回 500 Internal Server Error

3. **统一响应格式**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

## 测试策略

### 单元测试
- Service 层业务逻辑测试（岗位管理、申请审批、数据统计）
- Repository 层数据访问测试
- 工具类测试

### 集成测试
- API 接口测试（使用 MockMvc）
- 数据库事务测试
- 多组织数据隔离测试

### 功能测试
- 用户认证流程测试
- 岗位申请审批流程测试
- 实习过程管理流程测试
- 数据统计准确性测试

## 安全考虑

1. **数据隔离**：
   - 所有查询操作都需要验证用户所属组织
   - 学校管理员只能看本校数据
   - 医院管理员只能看本院数据

2. **会话管理**：
   - 使用 HttpSession 管理用户会话
   - 登录后将用户信息存储在 Session 中
   - 每次请求验证 Session 有效性

3. **输入验证**：
   - 所有用户输入都需要进行验证
   - 使用 JSR-303 Bean Validation 进行参数验证
   - 防止 SQL 注入（使用 JPA 参数化查询）

## 部署考虑

1. **数据库初始化**：
   - 使用 Flyway 或 Liquibase 进行数据库版本管理
   - 初始化系统管理员账户

2. **配置管理**：
   - 使用 application.yml 管理环境配置
   - 支持开发、测试、生产环境配置切换

3. **日志管理**：
   - 使用 SLF4J + Logback 进行日志记录
   - 记录关键业务操作日志
