# Vue 前端项目设置指南

## 项目结构

```
VUE/
├── src/
│   ├── components/          # 可复用组件
│   │   ├── Header.vue       # 顶部导航栏
│   │   ├── Footer.vue       # 底部信息栏
│   │   ├── Loading.vue      # 加载动画
│   │   ├── PatternCard.vue  # 纹样卡片
│   │   ├── CommentList.vue  # 评论列表
│   │   ├── QuestionList.vue # 提问列表
│   │   └── AdminMenu.vue    # 后台菜单
│   ├── pages/               # 页面组件
│   ├── router/              # 路由配置
│   ├── services/            # API 服务
│   ├── store/               # Pinia 状态管理
│   ├── styles/              # 全局样式
│   ├── utils/               # 工具函数
│   ├── App.vue              # 根组件
│   └── main.js              # 入口文件
├── .env                     # 环境变量
├── .env.example             # 环境变量示例
├── vite.config.js           # Vite 配置
└── package.json             # 项目依赖
```

## 环境配置

### 1. 安装依赖

```bash
cd VUE
npm install
```

### 2. 配置环境变量

复制 `.env.example` 为 `.env`：

```bash
cp .env.example .env
```

编辑 `.env` 文件，配置 API 地址：

```
VITE_API_URL=http://localhost:8080/api
```

## 开发

### 启动开发服务器

```bash
npm run dev
```

开发服务器将在 `http://localhost:5173` 启动。

### 代码检查和修复

```bash
npm run lint
```

## 构建

### 生产构建

```bash
npm run build
```

构建输出将在 `dist/` 目录中。

### 预览构建结果

```bash
npm run preview
```

## 技术栈

- **Vue 3**: 渐进式 JavaScript 框架
- **Vue Router 4**: 官方路由库
- **Pinia**: 状态管理库
- **Axios**: HTTP 客户端
- **Element Plus**: UI 组件库
- **Vite**: 前端构建工具

## 项目特性

### 路由管理

- 使用 Vue Router 4 进行路由管理
- 支持路由懒加载
- 实现路由守卫，保护需要认证的页面

### 状态管理

使用 Pinia 进行状态管理，包括：

- **useUserStore**: 用户状态（用户信息、登录状态、token）
- **useGlobalStore**: 全局状态（加载状态、消息提示）

### API 服务

在 `src/services/api.js` 中封装所有 HTTP 请求：

- 请求拦截器：自动添加 Authorization header
- 响应拦截器：处理 401 错误，自动跳转到登录页

### 工具函数

在 `src/utils/` 目录中提供常用工具函数：

- **helpers.js**: 日期格式化、验证、文本处理等
- **storage.js**: localStorage 管理
- **errorHandler.js**: 错误处理和消息提示

### 全局样式

在 `src/styles/global.css` 中定义全局样式和工具类。

## 常见任务

### 添加新页面

1. 在 `src/pages/` 中创建新的 `.vue` 文件
2. 在 `src/router/index.js` 中添加路由配置
3. 在导航菜单中添加链接

### 添加新组件

1. 在 `src/components/` 中创建新的 `.vue` 文件
2. 在需要的页面中导入并使用

### 调用 API

```javascript
import { patternAPI } from '@/services/api'

// 获取纹样列表
const patterns = await patternAPI.getPatterns({ page: 1, limit: 10 })
```

### 使用状态管理

```javascript
import { useUserStore } from '@/store'

const userStore = useUserStore()

// 获取用户信息
console.log(userStore.user)

// 设置用户信息
userStore.setUser(userData)

// 登出
userStore.logout()
```

## 浏览器支持

- Chrome (最新版本)
- Firefox (最新版本)
- Safari (最新版本)
- Edge (最新版本)

## 许可证

MIT
