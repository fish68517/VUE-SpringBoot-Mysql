# Vue 3 前端项目结构

## 项目概述

这是"气象+农资"一体化服务平台的Vue 3前端项目。项目采用现代化的前端架构，使用Vite作为构建工具，Pinia作为状态管理库，Element Plus作为UI框架。

## 目录结构

```
VUE/
├── src/
│   ├── api/                    # API请求相关
│   │   └── client.js          # Axios API客户端配置
│   ├── components/            # Vue组件
│   │   └── common/            # 公共组件库
│   │       ├── Button.vue     # 按钮组件
│   │       ├── Card.vue       # 卡片组件
│   │       ├── Form.vue       # 表单组件
│   │       ├── Modal.vue      # 模态框组件
│   │       └── Table.vue      # 表格组件
│   ├── composables/           # 组合式函数 (可复用逻辑)
│   │   ├── useForm.js         # 表单管理
│   │   ├── useNotification.js # 通知和消息提示
│   │   └── usePagination.js   # 分页管理
│   ├── constants/             # 常量定义
│   │   └── index.js           # 应用常量
│   ├── router/                # 路由配置
│   │   └── index.js           # 路由定义和导航守卫
│   ├── stores/                # Pinia状态管理
│   │   ├── auth.js            # 认证状态
│   │   ├── weather.js         # 气象数据状态
│   │   ├── warnings.js        # 预警状态
│   │   ├── products.js        # 产品状态
│   │   ├── orders.js          # 订单状态
│   │   ├── recommendations.js # 推荐状态
│   │   └── analytics.js       # 分析数据状态
│   ├── utils/                 # 工具函数
│   │   ├── formatters.js      # 格式化工具
│   │   ├── validators.js      # 验证工具
│   │   └── storage.js         # 存储工具
│   ├── views/                 # 页面组件
│   │   ├── Home.vue           # 首页
│   │   ├── Login.vue          # 登录页
│   │   ├── Register.vue       # 注册页
│   │   ├── Weather.vue        # 气象数据页
│   │   ├── Warnings.vue       # 预警信息页
│   │   ├── Products.vue       # 农资产品页
│   │   ├── Recommendations.vue # 推荐列表页
│   │   ├── Orders.vue         # 订单管理页
│   │   ├── Analytics.vue      # 数据分析页
│   │   └── Profile.vue        # 个人中心页
│   ├── App.vue                # 根组件
│   └── main.js                # 应用入口
├── index.html                 # HTML入口文件
├── vite.config.js             # Vite配置
├── package.json               # 项目依赖
├── .eslintrc.cjs              # ESLint配置
└── .gitignore                 # Git忽略文件
```

## 核心模块说明

### 1. API客户端 (`src/api/client.js`)

- 基于Axios的HTTP客户端
- 自动添加认证令牌
- 统一的错误处理
- 响应拦截器处理401错误

### 2. 状态管理 (`src/stores/`)

使用Pinia进行状态管理，每个模块对应一个store：

- **auth.js**: 用户认证状态（token、用户信息）
- **weather.js**: 气象数据状态
- **warnings.js**: 预警信息状态
- **products.js**: 农资产品状态
- **orders.js**: 订单状态
- **recommendations.js**: 推荐信息状态
- **analytics.js**: 分析数据状态

### 3. 路由配置 (`src/router/index.js`)

- 定义所有应用路由
- 实现路由导航守卫
- 支持认证和权限检查

### 4. 公共组件 (`src/components/common/`)

可复用的UI组件：

- **Table.vue**: 数据表格，支持排序、分页、操作列
- **Form.vue**: 表单组件，支持验证和提交
- **Button.vue**: 按钮组件
- **Card.vue**: 卡片组件
- **Modal.vue**: 模态框组件

### 5. 组合式函数 (`src/composables/`)

可复用的业务逻辑：

- **useForm.js**: 表单状态管理
- **useNotification.js**: 消息提示和确认对话框
- **usePagination.js**: 分页逻辑

### 6. 工具函数 (`src/utils/`)

- **formatters.js**: 日期、货币、百分比等格式化
- **validators.js**: 邮箱、电话、密码等验证
- **storage.js**: 本地存储和会话存储

### 7. 常量 (`src/constants/`)

应用中使用的所有常量定义，包括：

- 用户类型、状态
- 预警等级、状态
- 订单状态
- 产品分类
- 作物生育期
- API响应码

## 开发指南

### 安装依赖

```bash
cd VUE
npm install
```

### 开发服务器

```bash
npm run dev
```

访问 `http://localhost:5173`

### 构建生产版本

```bash
npm run build
```

### 代码检查和格式化

```bash
npm run lint
```

## 技术栈

- **Vue 3**: 渐进式JavaScript框架
- **Vite**: 下一代前端构建工具
- **Vue Router 4**: 官方路由库
- **Pinia**: 状态管理库
- **Axios**: HTTP客户端
- **Element Plus**: Vue 3 UI组件库
- **ESLint**: 代码检查工具

## 项目特性

✅ 模块化架构
✅ 完整的状态管理
✅ 可复用的组件和函数
✅ 统一的API请求处理
✅ 路由导航守卫
✅ 本地存储管理
✅ 表单验证工具
✅ 消息提示系统

## 环境配置

### 开发环境代理

Vite配置了API代理，将 `/api` 请求转发到后端服务器：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, '/api'),
  },
}
```

确保后端服务运行在 `http://localhost:8080`

## 最佳实践

1. **组件设计**: 使用组合式API (Composition API)
2. **状态管理**: 通过stores管理全局状态
3. **API调用**: 使用stores中的异步函数
4. **错误处理**: 使用useNotification进行错误提示
5. **表单处理**: 使用useForm进行表单管理
6. **代码复用**: 优先使用composables和utils

## 常见任务

### 添加新页面

1. 在 `src/views/` 创建新的Vue文件
2. 在 `src/router/index.js` 添加路由
3. 在 `src/App.vue` 添加导航链接

### 添加新的API调用

1. 在对应的store中添加异步函数
2. 在组件中使用store的函数
3. 使用useNotification处理成功/错误消息

### 添加新的公共组件

1. 在 `src/components/common/` 创建组件
2. 在需要的地方导入使用
3. 编写相应的文档

## 故障排除

### API请求失败

- 检查后端服务是否运行
- 检查Vite代理配置
- 查看浏览器控制台的错误信息

### 路由导航不工作

- 检查路由定义是否正确
- 检查导航守卫逻辑
- 确保组件路径正确

### 状态管理问题

- 检查store是否正确导入
- 确保在main.js中使用了Pinia
- 查看浏览器DevTools中的Pinia状态

## 相关文档

- [Vue 3文档](https://vuejs.org/)
- [Vite文档](https://vitejs.dev/)
- [Vue Router文档](https://router.vuejs.org/)
- [Pinia文档](https://pinia.vuejs.org/)
- [Element Plus文档](https://element-plus.org/)
