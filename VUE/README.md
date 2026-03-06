# 在线投稿系统 - 前端项目

## 项目介绍

这是在线投稿系统的Vue 3前端项目，采用现代化的前端技术栈。

## 技术栈

- **Vue 3**: 渐进式JavaScript框架
- **Vue Router**: 前端路由管理
- **Pinia**: 状态管理库
- **Axios**: HTTP客户端
- **Element Plus**: UI组件库
- **Vite**: 构建工具

## 项目结构

```
src/
├── components/          # 可复用组件
├── views/              # 页面组件
│   ├── auth/           # 认证相关页面
│   ├── author/         # 作者端页面
│   ├── editor/         # 编辑端页面
│   ├── reviewer/       # 审稿人端页面
│   ├── admin/          # 管理员端页面
│   └── layouts/        # 布局组件
├── router/             # 路由配置
├── stores/             # Pinia状态管理
├── services/           # API服务
├── utils/              # 工具函数
├── styles/             # 全局样式
├── App.vue             # 根组件
└── main.js             # 入口文件
```

## 安装依赖

```bash
npm install
```

## 开发

```bash
npm run dev
```

开发服务器将在 `http://localhost:5173` 启动

## 构建

```bash
npm run build
```

## 预览

```bash
npm run preview
```

## 环境配置

- `.env.development`: 开发环境配置
- `.env.production`: 生产环境配置

## 功能模块

### 认证模块
- 用户登录
- 用户注册
- 用户退出

### 作者端
- 个人信息管理
- 稿件提交
- 稿件进度查看
- 消息中心

### 编辑端
- 个人信息管理
- 稿件初审
- 审稿人分配
- 审稿进度跟踪

### 审稿人端
- 个人信息管理
- 审稿任务管理
- 历史记录查询

### 管理员端
- 个人信息管理
- 用户管理
- 栏目管理
- 系统配置
- 数据统计

## 路由配置

- `/login` - 登录页面
- `/register` - 注册页面
- `/author/*` - 作者端页面
- `/editor/*` - 编辑端页面
- `/reviewer/*` - 审稿人端页面
- `/admin/*` - 管理员端页面

## API服务

所有API请求通过 `src/services/api.js` 中的Axios实例进行，支持请求/响应拦截。

## 状态管理

使用Pinia进行状态管理，主要stores包括：
- `userStore`: 用户信息和登录状态
- `manuscriptStore`: 稿件相关数据
- `reviewStore`: 审稿相关数据
- `messageStore`: 消息相关数据
- `configStore`: 系统配置数据
