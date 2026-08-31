# 数字法治底座前端架构设计方案

## 1. 项目目标

基于《数字法治底座总体设计》，规划一套面向管理端的前端架构。

前端当前阶段采用：

- Vue 3
- TypeScript
- Vite
- Element Plus
- Vue Router
- Pinia
- Axios
- vite-plugin-mock
- MockJS
- ECharts
- SCSS

当前阶段不接真实后端，不依赖 MySQL、Redis、Nacos 等基础设施，所有接口和业务数据均通过 Mock 模拟。

设计目标是：

> 当前使用 Mock 完成前端开发和演示，后续接入真实 Spring Cloud Gateway 时，尽量不修改页面层和业务逻辑层。

---

## 2. 前端总体架构

```text
                    数字法治底座前端
                  Vue 3 + TypeScript
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
     页面层 Views       组件层 Components    状态层 Store
        │                  │                  │
        └──────────────────┼──────────────────┘
                           │
                     业务 Service 层
                           │
                    Axios Request 层
                           │
              ┌────────────┴────────────┐
              │                         │
         当前开发阶段                  后期联调
              │                         │
              ▼                         ▼
        Mock API 数据             Gateway API
        本地 JSON/TS            /api/web/v1/**
```

核心原则：

```text
页面
 ↓
Service
 ↓
Request
 ↓
Mock API
```

后期接真实后端：

```text
页面
 ↓
Service
 ↓
Request
 ↓
Spring Cloud Gateway
```

因此页面层不直接依赖 Mock 数据。

---

## 3. 技术栈

| 层级 | 技术 |
|---|---|
| 前端框架 | Vue 3 |
| 开发语言 | TypeScript |
| 构建工具 | Vite |
| UI 框架 | Element Plus |
| 路由 | Vue Router |
| 状态管理 | Pinia |
| HTTP | Axios |
| Mock | vite-plugin-mock + MockJS |
| CSS | SCSS |
| 图标 | Element Plus Icons |
| 图表 | ECharts |
| 日期处理 | Day.js |
| 表单校验 | Element Plus Form + TypeScript |
| 代码规范 | ESLint + Prettier |

不建议继续引入 Vuex、jQuery 等旧技术。

---

## 4. 前端业务模块规划

### 4.1 登录认证

包含：

- 登录
- 退出登录
- 用户信息
- Mock Token
- 登录状态保存
- 模拟权限加载

---

### 4.2 首页 Dashboard

包含：

- 用户统计
- 事件统计
- 待办统计
- 消息统计
- 事件趋势
- 系统运行状态
- 最新事件
- 我的待办

---

### 4.3 系统管理 System

包含：

- 用户管理
- 角色管理
- 菜单管理
- 部门管理
- 权限管理
- 数据权限

---

### 4.4 接入管理

包含：

- 三方接入方管理
- 应用管理
- API 授权
- IP 白名单
- 免认证接口

---

### 4.5 事件中心 Event

包含：

- 事件列表
- 事件详情
- 新建事件
- 我的待办
- 流程记录

---

### 4.6 消息中心 Message

包含：

- 消息列表
- 通知模板
- 站内消息
- 发送记录

---

### 4.7 定时任务 Job

包含：

- 任务管理
- 执行记录
- 任务状态

---

### 4.8 操作日志

包含：

- 登录日志
- 操作日志
- 异常日志

---

### 4.9 个人中心

包含：

- 基本资料
- 修改密码

---

## 5. 推荐项目目录

```text
digital-law-web/
│
├── public/
│
├── mock/
│   ├── auth.ts
│   ├── system/
│   │   ├── user.ts
│   │   ├── role.ts
│   │   ├── menu.ts
│   │   └── dept.ts
│   ├── event.ts
│   ├── message.ts
│   ├── job.ts
│   ├── log.ts
│   └── dashboard.ts
│
├── src/
│   ├── api/
│   │   ├── auth.ts
│   │   ├── system/
│   │   │   ├── user.ts
│   │   │   ├── role.ts
│   │   │   ├── menu.ts
│   │   │   └── dept.ts
│   │   ├── event.ts
│   │   ├── message.ts
│   │   ├── job.ts
│   │   └── dashboard.ts
│   │
│   ├── assets/
│   │   ├── images/
│   │   ├── icons/
│   │   └── styles/
│   │
│   ├── components/
│   │   ├── SearchForm/
│   │   ├── Pagination/
│   │   ├── StatusTag/
│   │   ├── DictSelect/
│   │   ├── FileUpload/
│   │   └── PageHeader/
│   │
│   ├── layout/
│   │   ├── index.vue
│   │   ├── Sidebar.vue
│   │   ├── Header.vue
│   │   ├── TagsView.vue
│   │   └── AppMain.vue
│   │
│   ├── router/
│   │   ├── index.ts
│   │   ├── routes.ts
│   │   └── guard.ts
│   │
│   ├── stores/
│   │   ├── user.ts
│   │   ├── permission.ts
│   │   ├── app.ts
│   │   └── tabs.ts
│   │
│   ├── types/
│   │   ├── api.ts
│   │   ├── auth.ts
│   │   ├── system.ts
│   │   ├── event.ts
│   │   └── common.ts
│   │
│   ├── utils/
│   │   ├── request.ts
│   │   ├── auth.ts
│   │   ├── storage.ts
│   │   ├── permission.ts
│   │   └── validate.ts
│   │
│   ├── views/
│   │   ├── login/
│   │   ├── dashboard/
│   │   ├── system/
│   │   │   ├── user/
│   │   │   ├── role/
│   │   │   ├── menu/
│   │   │   └── dept/
│   │   ├── access/
│   │   ├── event/
│   │   ├── message/
│   │   ├── job/
│   │   ├── log/
│   │   └── profile/
│   │
│   ├── App.vue
│   └── main.ts
│
├── .env.development
├── .env.production
├── package.json
├── tsconfig.json
└── vite.config.ts
```

---

## 6. API 与 Mock 解耦设计

不建议在页面中直接写死模拟数据，例如：

```vue
<script setup lang="ts">
const users = [
  { id: 1, name: '张三' },
  { id: 2, name: '李四' }
]
</script>
```

推荐使用：

```text
UserPage.vue
     │
     ▼
api/system/user.ts
     │
     ▼
utils/request.ts
     │
     ▼
/api/web/v1/system/user/page
     │
     ▼
Mock
```

后期联调：

```text
UserPage.vue
     │
     ▼
api/system/user.ts
     │
     ▼
utils/request.ts
     │
     ▼
/api/web/v1/system/user/page
     │
     ▼
Spring Cloud Gateway
```

这样可以实现：

> Mock 数据与真实后端无感切换。

---

## 7. API 路径规范

根据后端总体架构，Web 管理端统一使用：

```text
/api/web/v1
```

H5 / 移动端统一使用：

```text
/api/h5/v1
```

Open API 使用：

```text
/api/open/v1
```

前端当前即使只使用 Mock，也应严格按照正式 API 路径开发。

用户管理示例：

```text
GET  /api/web/v1/system/user/page
GET  /api/web/v1/system/user/{id}
POST /api/web/v1/system/user
POST /api/web/v1/system/user/update
POST /api/web/v1/system/user/delete
POST /api/web/v1/system/user/changeStatus
```

---

## 8. Mock 返回结构

Mock 数据需要模拟真实后端统一响应结构。

推荐：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 10001,
        "username": "admin",
        "realName": "系统管理员",
        "deptName": "系统管理部",
        "status": 1,
        "createTime": "2026-08-26 10:20:00"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

不要直接返回数组。

---

## 9. TypeScript 数据模型

### 9.1 用户模型

```ts
export interface User {
  id: number
  username: string
  realName: string
  deptId: number
  deptName: string
  status: number
  createTime: string
}
```

### 9.2 分页模型

```ts
export interface PageResult<T> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
}
```

### 9.3 统一响应

```ts
export interface ResponseResult<T> {
  code: number
  message: string
  data: T
}
```

目标关系：

```text
后端 DTO
      ↕
前端 TypeScript Interface
      ↕
Mock 数据
```

三者字段结构尽量保持一致。

---

## 10. Mock 登录设计

当前阶段登录流程：

```text
登录页
 │
 │ admin / 123456
 ▼
POST /api/web/v1/auth/login
 │
 ▼
Mock auth.ts
 │
 ├── 判断用户名密码
 │
 └── 返回 fake-token
 │
 ▼
Pinia userStore
 │
 ▼
localStorage
 │
 ▼
Dashboard
```

示例返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "mock-jwt-token-admin",
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "管理员"
    }
  }
}
```

Axios 请求头模拟：

```text
Authorization: Bearer mock-jwt-token-admin
```

---

## 11. Mock 权限设计

权限模型建议：

```text
用户
 ↓
角色
 ↓
菜单
 ↓
Permission Code
```

权限编码示例：

```ts
[
  'system:user:list',
  'system:user:add',
  'system:user:update',
  'system:user:delete'
]
```

页面示例：

```vue
<el-button
  v-if="hasPermission('system:user:add')"
>
  新增用户
</el-button>
```

最终流程：

```text
Mock 用户
  ↓
Mock 角色
  ↓
Mock 权限
  ↓
动态菜单
  ↓
按钮权限
```

---


## 14. 前端核心心智模型

```text
                 ┌────────────────────┐
                 │      Vue 页面       │
                 │ Views              │
                 └─────────┬──────────┘
                           │
                           ▼
                 ┌────────────────────┐
                 │    公共业务组件     │
                 │ Components         │
                 └─────────┬──────────┘
                           │
                  ┌────────┴────────┐
                  ▼                 ▼
          ┌──────────────┐   ┌──────────────┐
          │ Pinia Store  │   │ API Service  │
          └──────────────┘   └──────┬───────┘
                                    │
                                    ▼
                             ┌──────────────┐
                             │ Axios Request│
                             └──────┬───────┘
                                    │
                       ┌────────────┴───────────┐
                       │                        │
                    当前阶段                  正式阶段
                       │                        │
                       ▼                        ▼
                ┌──────────────┐       ┌──────────────┐
                │ Mock Server  │       │ API Gateway  │
                │ 本地模拟数据  │       │ Spring Cloud │
                └──────────────┘       └──────────────┘
```

核心思想：

> 现在虽然只开发 Vue 前端，但应按照“真实后端已经存在”的方式设计接口和数据结构。

---

## 15. 第一阶段开发范围

建议第一版实现：

| 一级模块 | 第一阶段 |
|---|---|
| 登录 | 完成 |
| Dashboard | 完成 |
| 用户管理 | 完成 |
| 角色管理 | 完成 |
| 菜单管理 | 完成 |
| 部门管理 | 完成 |
| 三方接入管理 | 完成 |
| API 授权 | 完成 |
| IP 白名单 | 完成 |
| 免认证接口 | 完成 |
| 事件中心 | 完成 |
| 消息中心 | 完成 |
| 定时任务 | 完成 |
| 操作日志 | 完成 |
| 文件上传 | Mock |
| 权限系统 | Mock |
| JWT | Mock |
| Java 后端 | 暂不接入 |
| MySQL | 暂不接入 |
| Redis | 暂不接入 |
| Nacos | 暂不接入 |

---

## 16. 最终工程技术定义

```text
Vue 3
+ TypeScript
+ Vite
+ Element Plus
+ Vue Router
+ Pinia
+ Axios
+ vite-plugin-mock
+ MockJS
+ ECharts
```

整体调用链：

```text
页面层
  ↓
业务组件层
  ↓
Pinia / API Service
  ↓
Axios
  ↓
Mock API
```

后期替换为：

```text
页面层
  ↓
业务组件层
  ↓
Pinia / API Service
  ↓
Axios
  ↓
Spring Cloud Gateway
```

这样可以保证：

1. 当前阶段快速完成前端开发和产品演示。
2. 不依赖后端开发进度。
3. Mock API 与正式 API 保持一致。
4. 后续联调时主要修改环境配置和请求地址。
5. Vue 页面、TypeScript 数据模型、业务 Service、Pinia 状态管理可以继续复用。
6. 前端模块边界与数字法治底座后端模块保持一致。
