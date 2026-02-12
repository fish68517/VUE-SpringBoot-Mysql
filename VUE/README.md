# 医校联动实习平台 - 前端项目

## 项目概述

这是医校联动实习平台的前端项目，使用 Vue 3 + Vite + Element Plus 构建。

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 下一代前端构建工具
- **Vue Router** - 官方路由管理器
- **Pinia** - 状态管理库
- **Axios** - HTTP 客户端
- **Element Plus** - Vue 3 UI 组件库

## 项目结构

```
src/
├── api/              # API 请求配置
│   └── axios.js      # Axios 实例和拦截器
├── layouts/          # 布局组件
│   ├── StudentLayout.vue
│   ├── TeacherLayout.vue
│   ├── SchoolAdminLayout.vue
│   ├── HospitalAdminLayout.vue
│   └── AdminLayout.vue
├── stores/           # Pinia 状态管理
│   └── userStore.js  # 用户信息存储
├── views/            # 页面组件
│   ├── Login.vue
│   ├── Register.vue
│   ├── student/      # 学生端页面
│   ├── teacher/      # 带教老师端页面
│   ├── schoolAdmin/  # 学校管理员端页面
│   ├── hospitalAdmin/# 医院管理员端页面
│   └── admin/        # 系统管理员端页面
├── router/           # 路由配置
│   └── index.js
├── App.vue           # 根组件
└── main.js           # 应用入口
```

## 安装依赖

```bash
npm install
```

## 开发

```bash
npm run dev
```

应用将在 `http://localhost:5173` 启动。

## 构建

```bash
npm run build
```

## 功能特性

### 用户认证
- 登录/注册功能
- 基于角色的路由守卫
- 用户信息持久化

### 状态管理
- 使用 Pinia 管理用户和组织信息
- 支持 localStorage 持久化

### API 集成
- Axios 实例配置
- 请求/响应拦截器
- 自动添加用户和组织信息到请求头

### 多角色支持
- 学生端
- 带教老师端
- 学校管理员端
- 医院管理员端
- 系统管理员端

## 路由说明

### 公开路由
- `/login` - 登录页面
- `/register` - 注册页面

### 受保护路由
- `/student/*` - 学生端页面（需要 STUDENT 角色）
- `/teacher/*` - 带教老师端页面（需要 TEACHER 角色）
- `/school-admin/*` - 学校管理员端页面（需要 SCHOOL_ADMIN 角色）
- `/hospital-admin/*` - 医院管理员端页面（需要 HOSPITAL_ADMIN 角色）
- `/admin/*` - 系统管理员端页面（需要 ADMIN 角色）

## 环境配置

在 `vite.config.js` 中配置 API 代理：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
  },
}
```

## 注意事项

- 所有代码注释采用简体中文
- 使用 Element Plus 组件库进行 UI 开发
- 遵循 Vue 3 Composition API 最佳实践
