# 广州旅游管理系统 - 前端项目

## 项目概述

这是广州旅游管理系统的Vue 3前端项目，采用Vite作为构建工具，Element Plus作为UI框架。

## 技术栈

- Vue 3.3.4
- Vue Router 4.2.4
- Pinia 2.1.4
- Axios 1.5.0
- Element Plus 2.4.0
- Vite 4.4.9

## 项目结构

```
VUE/
├── src/
│   ├── api/              # API请求模块
│   │   ├── client.js     # Axios客户端配置
│   │   ├── user.js       # 用户API
│   │   ├── attraction.js # 景点API
│   │   ├── hotel.js      # 酒店API
│   │   ├── order.js      # 订单API
│   │   ├── product.js    # 商品API
│   │   ├── comment.js    # 留言API
│   │   ├── route.js      # 路线API
│   │   ├── announcement.js # 公告API
│   │   ├── favorite.js   # 收藏API
│   │   └── browsingHistory.js # 浏览历史API
│   ├── components/       # 可复用组件
│   │   └── Layout/       # 布局组件
│   ├── views/            # 页面组件
│   │   ├── auth/         # 认证页面
│   │   ├── tourist/      # 游客端页面
│   │   └── admin/        # 管理员端页面
│   ├── router/           # 路由配置
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── index.html            # HTML模板
├── vite.config.js        # Vite配置
├── package.json          # 项目依赖
└── README.md             # 项目说明
```

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

访问 `http://localhost:5173`

### 生产构建

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 功能模块

### 游客端功能

- 用户注册和登录
- 个人资料管理
- 景点浏览和预订
- 酒店查询和预订
- 旅游商品购买
- 订单管理
- 收藏和浏览历史
- 旅游路线推荐

### 管理员端功能

- 用户管理（启用/禁用/删除）
- 景点管理（CRUD）
- 酒店管理（CRUD）
- 订单管理和统计
- 留言审核和管理
- 公告发布和管理
- 旅游路线管理

## API配置

API请求默认指向 `http://localhost:8080/api`

在 `src/api/client.js` 中修改 `API_BASE_URL` 来改变API地址。

## 认证机制

系统使用简化的认证机制：
- 用户信息存储在 localStorage
- 用户ID和角色通过请求头传递
- 路由守卫检查用户权限

## 开发指南

### 添加新的API接口

在 `src/api/` 目录下创建新的模块文件，例如 `src/api/newModule.js`：

```javascript
import client from './client'

export const newModuleApi = {
  getList: (params) => client.get('/new-module', { params }),
  getDetail: (id) => client.get(`/new-module/${id}`),
  create: (data) => client.post('/new-module', data),
  update: (id, data) => client.put(`/new-module/${id}`, data),
  delete: (id) => client.delete(`/new-module/${id}`)
}
```

### 添加新的页面

在 `src/views/` 下创建新的Vue组件，然后在 `src/router/index.js` 中添加路由配置。

## 注意事项

- 确保后端API服务运行在 `http://localhost:8080`
- 开发时使用 `npm run dev` 启动开发服务器
- 生产环境需要配置正确的API地址
