# 文山壮族刺绣网站系统 - Vue 前端

这是文山壮族刺绣网站系统的前端应用，使用 Vue 3 + Vite 构建。

## 项目结构

```
src/
├── components/          # 可复用组件
├── pages/              # 页面组件
│   ├── admin/          # 后台管理页面
│   └── ...             # 前台页面
├── services/           # API 服务层
├── utils/              # 工具函数
├── styles/             # 全局样式
├── router/             # 路由配置
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

应用将在 `http://localhost:5173` 启动。

## 构建

```bash
npm run build
```

## 配置

### 环境变量

在 `.env` 文件中配置 API 基础 URL：

```
VITE_API_URL=http://localhost:8080/api
```

## 技术栈

- Vue 3
- Vue Router 4
- Axios
- Pinia (状态管理)
- Vite (构建工具)

## 功能模块

### 前台
- 首页
- 作品展示
- 知识科普
- 互动交流
- 用户中心

### 后台管理
- 用户管理
- 资源管理
- 互动管理
- 系统管理
