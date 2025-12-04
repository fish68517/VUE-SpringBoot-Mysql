# 沈阳音乐节管理系统 - 前端项目

## 项目介绍

这是沈阳音乐节管理系统的前端应用，基于 Vue3 + Vite + TypeScript 构建。

## 技术栈

- **Vue3**：渐进式 JavaScript 框架
- **Vite**：下一代前端构建工具
- **TypeScript**：JavaScript 的超集
- **Vue Router**：官方路由管理库
- **Pinia**：Vue3 状态管理库
- **Element Plus**：Vue3 UI 组件库
- **Axios**：HTTP 客户端
- **ECharts**：数据可视化库

## 项目结构

```
src/
├── api/              # API 接口模块
│   ├── request.ts    # Axios 实例和拦截器
│   └── user.ts       # 用户相关 API
├── components/       # 可复用组件
│   └── Header.vue    # 头部组件
├── pages/            # 页面组件
│   ├── Home.vue      # 首页
│   ├── Login.vue     # 登录页
│   └── Register.vue  # 注册页
├── router/           # 路由配置
│   └── index.ts      # 路由定义
├── stores/           # Pinia 状态管理
│   └── user.ts       # 用户状态
├── styles/           # 全局样式
│   └── global.css    # 全局样式文件
├── utils/            # 工具函数
│   ├── date.ts       # 日期处理工具
│   ├── storage.ts    # 本地存储工具
│   └── string.ts     # 字符串处理工具
├── assets/           # 静态资源
│   └── images/       # 图片资源
├── App.vue           # 根组件
└── main.ts           # 应用入口
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

应用将在 `http://localhost:5173` 启动

### 生产构建

```bash
npm run build
```

### 预览构建结果

```bash
npm run preview
```

## 环境配置

### 开发环境 (.env)

```
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_TITLE=沈阳音乐节管理系统
```

### 生产环境 (.env.production)

```
VITE_API_BASE_URL=/api
VITE_APP_TITLE=沈阳音乐节管理系统
```

## API 代理配置

在 `vite.config.ts` 中配置了 API 代理，所有 `/api` 开头的请求都会被代理到 `http://localhost:8080`。

## 功能模块

- **用户认证**：注册、登录、登出
- **个人中心**：用户信息管理、实名认证
- **购票系统**：场次选择、分区选择、电子票生成
- **商城系统**：商品浏览、购物车、订单管理
- **打卡系统**：打卡任务、位置验证、积分获取
- **积分商城**：积分兑换、兑换记录
- **内容管理**：资讯浏览、点赞、收藏、评论
- **后台管理**：仪表盘、用户管理、订单管理等

## 开发规范

- 所有页面和组件使用 Vue3 Composition API
- 使用 TypeScript 进行类型检查
- 遵循 Element Plus 组件库的使用规范
- 使用 Pinia 进行全局状态管理
- 使用 Axios 进行 API 调用

## 注意事项

- 确保后端服务运行在 `http://localhost:8080`
- 所有用户界面均为中文
- 支持响应式设计，适配各种屏幕尺寸
