# 校园活动众筹与报名平台 - 前端项目

## 项目概述

这是校园活动众筹与报名平台的前端项目，使用 Vue 3 + Vite 构建。

## 技术栈

- **Vue 3**: 渐进式JavaScript框架
- **Vite**: 下一代前端构建工具
- **Vue Router**: 官方路由管理库
- **Axios**: HTTP客户端库
- **Element Plus**: Vue 3 UI组件库
- **ECharts**: 数据可视化库
- **Pinia**: 状态管理库

## 项目结构

```
src/
├── components/      # 可复用组件
├── pages/          # 页面组件
├── router/         # 路由配置
├── store/          # Pinia状态管理
├── styles/         # 全局样式
├── utils/          # 工具函数
├── assets/         # 静态资源
├── App.vue         # 根组件
└── main.js         # 入口文件
```

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发服务器

```bash
npm run dev
```

开发服务器将在 `http://localhost:5173` 启动。

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

## 配置说明

### Vite配置

- 开发服务器端口: 5173
- API代理: `/api` 代理到 `http://localhost:8080`
- 构建输出目录: `dist`

### 蓝色主题配置

全局CSS变量定义在 `src/styles/main.css` 中，包括：
- 主色调: `#1890ff`
- 辅助色、背景色等

## API集成

Axios已配置请求/响应拦截器，自动处理：
- 请求头中添加JWT令牌
- 错误响应处理
- 401未授权自动重定向到登录页

## 开发指南

### 创建新页面

1. 在 `src/pages/` 中创建 `.vue` 文件
2. 在 `src/router/index.js` 中添加路由配置

### 创建新组件

1. 在 `src/components/` 中创建 `.vue` 文件
2. 在需要的页面中导入使用

### 使用状态管理

```javascript
import { useAuthStore } from '@/store'

const authStore = useAuthStore()
```

## 许可证

MIT
