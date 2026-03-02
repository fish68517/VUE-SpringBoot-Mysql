# 个人健康管理系统 - 前端

这是个人健康管理系统的Vue 3前端应用，提供用户、医师和管理员三个不同的功能模块。

## 项目概述

个人健康管理系统前端是一个现代化的Web应用，支持以下核心功能：

- **用户端**: 个人信息管理、健康数据采集、趋势分析、在线咨询
- **医师端**: 患者管理、档案查看、数据审核、健康建议推送
- **管理端**: 用户权限管理、医师管理、数据统计、审计日志查询

## 技术栈

- **Vue 3** - 前端框架
- **Vue Router 4** - 路由管理
- **Axios** - HTTP 客户端
- **Element Plus** - UI 组件库
- **Pinia** - 状态管理
- **Chart.js** - 数据可视化
- **Vite** - 构建工具
- **Vitest** - 单元测试框架

## 项目结构

```
src/
├── components/                      # Vue 组件
│   ├── LoginPage.vue               # 登录页面
│   ├── RegisterPage.vue            # 注册页面
│   ├── user/                       # 用户端组件
│   │   ├── UserLayout.vue          # 用户端布局（顶部菜单）
│   │   ├── PersonalInfo.vue        # 个人信息管理
│   │   ├── HealthDataInput.vue     # 健康数据采集
│   │   ├── HealthTrend.vue         # 健康趋势分析
│   │   ├── HealthCheck.vue         # 常规健康检查
│   │   ├── GenderHealth.vue        # 性别健康监控
│   │   ├── Consultation.vue        # 在线咨询
│   │   └── HealthHistory.vue       # 健康历史查询
│   ├── doctor/                     # 医师端组件
│   │   ├── DoctorLayout.vue        # 医师端布局（侧边菜单）
│   │   ├── PatientList.vue         # 患者列表
│   │   ├── PatientRecord.vue       # 患者档案查看
│   │   ├── DataReview.vue          # 数据审核与反馈
│   │   └── HealthAdvice.vue        # 健康建议推送
│   ├── admin/                      # 管理端组件
│   │   ├── AdminLayout.vue         # 管理端布局（侧边菜单）
│   │   ├── UserManagement.vue      # 用户权限管理
│   │   ├── DoctorManagement.vue    # 医师信息管理
│   │   ├── DataStatistics.vue      # 数据统计与分析
│   │   └── AuditLog.vue            # 审计日志查询
│   └── __tests__/                  # 组件测试
├── router/                         # 路由配置
│   └── index.js                    # 路由定义和导航守卫
├── services/                       # 服务层
│   ├── api.js                      # API 调用服务
│   ├── auth.js                     # 认证服务
│   └── __tests__/                  # 服务测试
├── utils/                          # 工具函数
│   ├── formatters.js               # 数据格式化工具
│   ├── validators.js               # 数据验证工具
│   └── __tests__/                  # 工具测试
├── i18n/                           # 国际化
│   ├── index.js                    # i18n 配置
│   └── zh-CN.js                    # 中文翻译
├── assets/                         # 静态资源
├── App.vue                         # 根组件
├── main.js                         # 应用入口
├── vite.config.js                  # Vite 配置
└── vitest.config.js                # Vitest 配置
```

## 快速开始

### 前置条件

- Node.js 14.0 或更高版本
- npm 6.0 或更高版本

### 安装步骤

1. **进入项目目录**
   ```bash
   cd SpringBoot/VUE
   ```

2. **安装依赖**
   ```bash
   npm install
   ```

3. **配置环境变量**
   
   编辑 `.env` 文件，配置API基础URL：
   ```
   VITE_API_BASE_URL=http://localhost:8080/api
   VITE_APP_TITLE=个人健康管理系统
   ```

4. **启动开发服务器**
   ```bash
   npm run dev
   ```

   应用将在 `http://localhost:5173` 运行。

## 开发

### 启动开发服务器

```bash
npm run dev
```

开发服务器支持热模块替换（HMR），修改代码后会自动刷新。

### 构建生产版本

```bash
npm run build
```

生产版本将输出到 `dist` 目录。

### 预览生产构建

```bash
npm run preview
```

## 测试

### 运行所有测试

```bash
npm test
```

### 监视模式运行测试

```bash
npm run test:watch
```

### 测试覆盖率

```bash
npm test -- --coverage
```

## 环境配置

### 开发环境 (.env)

```
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_TITLE=个人健康管理系统
```

### 生产环境 (.env.production)

```
VITE_API_BASE_URL=/api
VITE_APP_TITLE=个人健康管理系统
```

## 功能模块详解

### 用户端功能

#### 1. 个人信息管理
- 查看个人信息（姓名、年龄、性别、联系方式等）
- 编辑和更新个人信息
- 表单验证和错误提示

#### 2. 健康数据采集
- 记录身高、体重、血压、心率等健康指标
- 记录饮食和运动情况
- 数据范围验证
- 时间戳自动记录

#### 3. 健康趋势分析
- 可视化健康数据趋势图表
- 支持多种图表类型（折线图、柱状图等）
- 时间范围筛选
- 健康指标选择

#### 4. 常规健康检查
- 记录定期健康检查项目
- 保存检查结果
- 历史检查记录查询

#### 5. 性别健康监控
- 记录性别相关的健康数据
- 隐私保护提示
- 敏感数据加密存储

#### 6. 在线咨询
- 提交健康咨询问题
- 查看医师回复
- 咨询历史记录
- 咨询状态跟踪

#### 7. 健康历史查询
- 查看所有历史健康记录
- 按日期、类型筛选
- 详细记录查看

### 医师端功能

#### 1. 患者列表
- 查看所有患者列表
- 患者搜索和筛选
- 快速进入患者档案

#### 2. 患者档案查看
- 查看患者个人信息
- 查看患者健康数据
- 查看患者历史记录
- 权限检查（只能查看自己的患者）

#### 3. 数据审核与反馈
- 审核患者健康数据
- 添加审核意见
- 提供专业反馈
- 反馈通知患者

#### 4. 健康建议推送
- 创建个性化健康建议
- 推送建议给患者
- 查看已发送的建议列表
- 建议更新和管理

### 管理端功能

#### 1. 用户权限管理
- 查看所有用户列表
- 修改用户权限（USER/DOCTOR/ADMIN）
- 禁用/启用用户账户
- 用户状态管理

#### 2. 医师信息管理
- 查看所有医师列表
- 编辑医师信息
- 删除医师账户
- 医师资质管理

#### 3. 数据统计与分析
- 系统数据统计（用户数、医师数、数据量等）
- 统计图表展示
- 时间范围筛选
- 数据导出功能

#### 4. 审计日志查询
- 查看系统操作日志
- 日志搜索和筛选
- 日志详情查看
- 操作追踪

## 路由配置

### 路由结构

```
/                          # 根路径 -> 重定向到登录
├── /login                 # 登录页面
├── /register              # 注册页面
├── /user                  # 用户端
│   ├── /user/profile      # 个人信息
│   ├── /user/health-data  # 健康数据采集
│   ├── /user/trends       # 健康趋势
│   ├── /user/check        # 常规检查
│   ├── /user/gender       # 性别健康
│   ├── /user/consultation # 在线咨询
│   └── /user/history      # 健康历史
├── /doctor                # 医师端
│   ├── /doctor/patients   # 患者列表
│   ├── /doctor/record     # 患者档案
│   ├── /doctor/review     # 数据审核
│   └── /doctor/advice     # 健康建议
└── /admin                 # 管理端
    ├── /admin/users       # 用户管理
    ├── /admin/doctors     # 医师管理
    ├── /admin/statistics  # 数据统计
    └── /admin/audit-logs  # 审计日志
```

### 导航守卫

- 检查用户登录状态
- 验证用户权限
- 防止未授权访问
- 自动重定向到登录页面

## API 集成

### API 服务 (src/services/api.js)

所有API调用都通过统一的API服务进行，包括：

- 请求拦截器：添加认证令牌
- 响应拦截器：处理错误和成功响应
- 错误处理：统一的错误提示

### 认证服务 (src/services/auth.js)

- 用户登录/注册
- 令牌管理
- 用户信息存储
- 登出功能

### 使用示例

```javascript
import api from '@/services/api'

// 获取用户信息
const response = await api.getUserProfile()

// 提交健康数据
const data = {
  height: 175.5,
  weight: 70.0,
  bloodPressure: '120/80'
}
const response = await api.submitHealthData(data)
```

## 国际化

系统已配置为简体中文。所有界面文本、错误消息和提示都使用中文显示。

### 添加新的翻译

编辑 `src/i18n/zh-CN.js` 文件，添加新的翻译键值对：

```javascript
export default {
  common: {
    save: '保存',
    cancel: '取消',
    delete: '删除'
  },
  user: {
    profile: '个人信息',
    healthData: '健康数据'
  }
}
```

### 在组件中使用翻译

```vue
<template>
  <button>{{ $t('common.save') }}</button>
</template>
```

## 认证机制

### 登录流程

1. 用户输入用户名和密码
2. 前端发送登录请求到后端
3. 后端验证凭证并返回用户信息
4. 前端保存用户信息和令牌到本地存储
5. 根据用户角色重定向到对应的主页面

### 令牌管理

- 登录成功后，令牌保存到 `localStorage`
- 每个API请求都会自动添加令牌到请求头
- 登出时清除令牌和用户信息

### 权限检查

- 路由导航守卫检查用户权限
- 未授权用户无法访问受保护的页面
- 权限不足时显示错误提示

## 开发指南

### 代码规范

1. **文件命名**
   - 组件文件使用PascalCase（如 `UserProfile.vue`）
   - 工具文件使用camelCase（如 `validators.js`）
   - 测试文件使用 `.test.js` 后缀

2. **组件编写**
   - 使用Vue 3 Composition API
   - 使用 `<script setup>` 语法
   - 添加适当的注释和文档

3. **样式编写**
   - 使用scoped样式避免全局污染
   - 遵循BEM命名规范
   - 使用CSS变量管理颜色和尺寸

4. **API调用**
   - 所有API调用使用 `src/services/api.js`
   - 使用async/await处理异步操作
   - 添加错误处理和加载状态

### 添加新页面的步骤

1. 在 `src/components` 目录创建新组件
2. 在 `src/router/index.js` 中添加路由
3. 在 `src/services/api.js` 中添加API调用方法
4. 在 `src/i18n/zh-CN.js` 中添加翻译
5. 编写组件测试

### 添加新API调用的步骤

1. 在 `src/services/api.js` 中添加新方法
2. 在组件中导入并使用该方法
3. 添加错误处理和加载状态
4. 编写API服务测试

## 常见问题

### Q: 如何修改API基础URL？
A: 编辑 `.env` 或 `.env.production` 文件，修改 `VITE_API_BASE_URL` 的值。

### Q: 如何添加新的UI组件？
A: 使用Element Plus提供的组件，或在 `src/components` 目录创建自定义组件。

### Q: 如何处理API错误？
A: 错误会被响应拦截器捕获，自动显示错误提示。可在组件中添加try-catch处理特定错误。

### Q: 如何调试应用？
A: 使用浏览器开发者工具（F12）进行调试。Vue DevTools浏览器扩展可帮助调试Vue组件。

### Q: 如何优化应用性能？
A: 
- 使用代码分割和懒加载
- 优化图片和资源
- 使用生产构建
- 启用gzip压缩

## 部署

详见 [部署指南](../../DEPLOYMENT.md)

## 许可证

MIT
