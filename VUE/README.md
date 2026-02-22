# 气象+农资一体化服务平台 - 前端项目

## 项目概述

这是一个基于 Vue 3 的前端项目，为"气象+农资一体化服务平台"提供用户界面。项目采用现代化的前端技术栈，包括 Vue 3、Vue Router、Pinia 状态管理和 Element Plus UI 组件库。

## 技术栈

- **Vue 3**: 渐进式 JavaScript 框架
- **Vue Router 4**: 官方路由管理器
- **Pinia**: 轻量级状态管理库
- **Axios**: HTTP 客户端库
- **Element Plus**: Vue 3 UI 组件库
- **Vite**: 下一代前端构建工具
- **ESLint**: 代码质量检查工具

## 项目结构

```
VUE/
├── src/
│   ├── api/                 # API 请求模块
│   │   ├── client.js        # Axios 实例配置
│   │   ├── user.js          # 用户相关 API
│   │   ├── weather.js       # 气象数据 API
│   │   ├── warning.js       # 预警信息 API
│   │   ├── product.js       # 农资产品 API
│   │   ├── recommendation.js # 推荐引擎 API
│   │   ├── order.js         # 订单管理 API
│   │   └── analytics.js     # 数据分析 API
│   ├── components/          # 可复用组件
│   │   └── common/          # 通用组件
│   │       ├── Button.vue   # 按钮组件
│   │       ├── Form.vue     # 表单组件
│   │       ├── Table.vue    # 表格组件
│   │       ├── Card.vue     # 卡片组件
│   │       └── Modal.vue    # 模态框组件
│   ├── stores/              # Pinia 状态管理
│   │   └── auth.js          # 认证状态
│   ├── views/               # 页面组件
│   │   ├── Home.vue         # 首页
│   │   ├── Login.vue        # 登录页
│   │   ├── Register.vue     # 注册页
│   │   ├── Weather.vue      # 气象数据页
│   │   ├── Warnings.vue     # 预警信息页
│   │   ├── Products.vue     # 农资产品页
│   │   ├── Recommendations.vue # 推荐列表页
│   │   ├── Orders.vue       # 订单管理页
│   │   ├── Analytics.vue    # 数据分析页
│   │   └── Profile.vue      # 个人中心页
│   ├── router/              # 路由配置
│   │   └── index.js         # 路由定义
│   ├── App.vue              # 根组件
│   └── main.js              # 应用入口
├── index.html               # HTML 模板
├── vite.config.js           # Vite 配置
├── package.json             # 项目依赖
├── .eslintrc.cjs            # ESLint 配置
└── .gitignore               # Git 忽略文件
```

## 安装和运行

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

项目将在 `http://localhost:5173` 启动

### 生产构建

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

### 代码检查

```bash
npm run lint
```

## 主要功能

### 1. 用户管理
- 用户注册和登录
- 个人信息管理
- 密码修改
- 用户退出

### 2. 气象数据
- 查看当前气象数据
- 查看气象预报
- 查看历史气象数据
- 按地区和时间范围查询

### 3. 预警管理
- 查看预警信息列表
- 按地区和严重程度筛选
- 发布新预警（管理员）
- 更新和删除预警

### 4. 农资产品
- 浏览农资产品
- 按类别和价格范围筛选
- 查看产品详情
- 加入购物车

### 5. 智能推荐
- 查看基于预警的农资推荐
- 接受或拒绝推荐
- 查看推荐原因和优先级

### 6. 订单管理
- 创建订单
- 查看订单列表
- 订单支付
- 订单状态跟踪

### 7. 数据分析
- 查看关键业务指标
- 产品销售排行
- 数据导出功能

## API 集成

项目通过 Axios 与后端 API 进行通信。所有 API 请求都经过统一的客户端配置，包括：

- 自动添加认证令牌
- 统一的错误处理
- 请求/响应拦截

### API 基础 URL

默认 API 基础 URL 为 `/api`，通过 Vite 代理配置转发到 `http://localhost:8080`

## 状态管理

使用 Pinia 进行状态管理，主要包括：

- **auth store**: 管理用户认证状态和用户信息

## 路由配置

应用包含以下主要路由：

- `/` - 首页
- `/login` - 登录页
- `/register` - 注册页
- `/weather` - 气象数据页
- `/warnings` - 预警信息页
- `/products` - 农资产品页
- `/recommendations` - 推荐列表页
- `/orders` - 订单管理页
- `/analytics` - 数据分析页（需要管理员权限）
- `/profile` - 个人中心页

## 认证和授权

- 使用 JWT 令牌进行身份验证
- 某些路由需要用户登录（`requiresAuth: true`）
- 某些路由需要管理员权限（`requiresAdmin: true`）
- 自动重定向未认证用户到登录页

## 开发指南

### 添加新的 API 模块

1. 在 `src/api/` 目录下创建新文件
2. 导入 `apiClient`
3. 定义 API 方法

示例：
```javascript
import apiClient from './client'

export const newAPI = {
  getList(params) {
    return apiClient.get('/endpoint', { params })
  },
  
  create(data) {
    return apiClient.post('/endpoint', data)
  },
}
```

### 添加新的页面

1. 在 `src/views/` 目录下创建新的 `.vue` 文件
2. 在 `src/router/index.js` 中添加路由配置
3. 在 `App.vue` 中添加导航链接

### 添加新的组件

1. 在 `src/components/common/` 目录下创建新的 `.vue` 文件
2. 在需要的地方导入和使用

## 浏览器支持

- Chrome（最新版本）
- Firefox（最新版本）
- Safari（最新版本）
- Edge（最新版本）

## 许可证

MIT
