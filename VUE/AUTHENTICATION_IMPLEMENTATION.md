# 用户认证流程实现文档

## 概述

本文档描述了纳西族纹样数字化展示平台的用户认证流程实现，包括登录状态管理、路由保护、登出功能和会话持久化。

## 实现组件

### 1. Pinia 状态管理 (VUE/src/store/index.js)

使用 Pinia 作为状态管理库，实现用户认证状态的集中管理。

**主要功能：**
- `user`: 存储当前登录用户信息
- `token`: 存储认证令牌
- `isLoggedIn`: 计算属性，判断用户是否已登录
- `isAdmin`: 计算属性，判断用户是否为管理员
- `setUser()`: 设置用户信息并持久化到 localStorage
- `setToken()`: 设置令牌并持久化到 localStorage
- `logout()`: 清除用户信息和令牌
- `updateUser()`: 更新用户信息

**会话持久化：**
- 应用启动时从 localStorage 恢复用户信息和令牌
- 用户登录时自动保存到 localStorage
- 用户登出时清除 localStorage 中的数据

### 2. 路由保护 (VUE/src/router/index.js)

使用 Vue Router 的 `beforeEach` 导航守卫实现路由保护。

**保护规则：**
- `requiresAuth: true`: 需要用户登录才能访问
  - 未登录用户会被重定向到登录页
  - 登录页会记录原始 URL，登录后自动跳转回原页面
- `requiresAdmin: true`: 需要管理员权限才能访问
  - 后端会验证用户权限，前端允许路由加载

**受保护的路由：**
- `/user-center`: 个人中心（需要登录）
- `/admin/*`: 所有后台管理页面（需要登录和管理员权限）

### 3. 认证服务 (VUE/src/services/auth.js)

提供认证相关的业务逻辑和 API 调用封装。

**主要方法：**
- `login(username, password)`: 用户登录
- `register(username, email, password)`: 用户注册
- `logout()`: 用户登出
- `isAuthenticated()`: 检查用户是否已认证
- `isAdmin()`: 检查用户是否为管理员
- `getCurrentUser()`: 获取当前用户信息
- `getToken()`: 获取当前令牌
- `restoreSession()`: 从 localStorage 恢复会话
- `clearSession()`: 清除会话

### 4. 认证组合式函数 (VUE/src/composables/useAuth.js)

提供响应式的认证状态和方法，方便在组件中使用。

**使用示例：**
```javascript
import { useAuth } from '@/composables/useAuth'

export default {
  setup() {
    const { isLoggedIn, isAdmin, currentUser, login, logout } = useAuth()
    
    return {
      isLoggedIn,
      isAdmin,
      currentUser,
      login,
      logout
    }
  }
}
```

### 5. API 拦截器 (VUE/src/services/api.js)

Axios 请求拦截器自动添加认证令牌到请求头。

**功能：**
- 请求拦截器：自动添加 `Authorization: Bearer {token}` 头
- 响应拦截器：处理 401 未授权错误，自动清除会话并重定向到登录页

### 6. 登录页面 (VUE/src/pages/Login.vue)

用户登录界面，包含表单验证和错误处理。

**功能：**
- 用户名和密码输入
- 表单验证
- 登录成功后存储用户信息和令牌
- 支持重定向到原始页面

### 7. 注册页面 (VUE/src/pages/Register.vue)

用户注册界面，包含表单验证和错误处理。

**功能：**
- 用户名、邮箱、密码输入
- 表单验证
- 注册成功后重定向到登录页

### 8. 头部导航 (VUE/src/components/Header.vue)

显示用户认证状态和提供登出功能。

**功能：**
- 显示登录/注册链接（未登录用户）
- 显示个人中心和后台链接（已登录用户）
- 显示管理员后台链接（管理员用户）
- 提供登出按钮

### 9. 应用初始化 (VUE/src/App.vue)

应用启动时恢复用户会话。

**功能：**
- 在应用挂载时调用 `restoreSession()` 恢复会话
- 确保页面刷新后用户仍保持登录状态

## 认证流程

### 用户登录流程

1. 用户访问登录页面 (`/login`)
2. 输入用户名和密码
3. 点击登录按钮
4. 前端调用 `userAPI.login()` 发送登录请求
5. 后端验证凭证并返回用户信息
6. 前端生成令牌并存储用户信息和令牌到 Pinia store
7. 自动保存到 localStorage
8. 重定向到原始页面或首页

### 用户登出流程

1. 用户点击登出按钮
2. 调用 `userStore.logout()` 清除用户信息和令牌
3. 清除 localStorage 中的数据
4. 重定向到登录页面

### 会话恢复流程

1. 应用启动时调用 `restoreSession()`
2. 从 localStorage 读取用户信息和令牌
3. 恢复到 Pinia store
4. 用户保持登录状态

### 路由保护流程

1. 用户尝试访问受保护的路由
2. 路由守卫检查 `meta.requiresAuth` 和 `meta.requiresAdmin`
3. 如果未登录且路由需要认证，重定向到登录页
4. 登录页记录原始 URL 作为重定向目标
5. 用户登录后自动跳转回原始页面

## 数据流

```
用户登录
  ↓
Login.vue 调用 userAPI.login()
  ↓
API 返回用户信息
  ↓
userStore.setUser() 和 setToken()
  ↓
自动保存到 localStorage
  ↓
Header.vue 更新显示
  ↓
路由守卫允许访问受保护页面
```

## 安全考虑

1. **令牌存储**：令牌存储在 localStorage 中，在生产环境应使用 HttpOnly Cookie
2. **密码加密**：前端不进行密码加密，依赖 HTTPS 传输安全
3. **令牌验证**：后端应验证令牌的有效性和过期时间
4. **CORS**：后端配置 CORS 允许前端跨域请求
5. **会话超时**：建议实现会话超时机制

## 使用示例

### 在组件中使用认证

```vue
<script setup>
import { useAuth } from '@/composables/useAuth'

const { isLoggedIn, isAdmin, currentUser, logout } = useAuth()
</script>

<template>
  <div v-if="isLoggedIn">
    <p>欢迎，{{ currentUser.username }}</p>
    <button v-if="isAdmin">管理员功能</button>
    <button @click="logout">登出</button>
  </div>
  <div v-else>
    <router-link to="/login">登录</router-link>
  </div>
</template>
```

### 在路由中使用保护

```javascript
{
  path: '/user-center',
  component: UserCenter,
  meta: { requiresAuth: true }
}
```

### 在 API 调用中使用令牌

```javascript
// API 拦截器自动添加令牌
const response = await userAPI.getUser(userId)
// 请求头自动包含: Authorization: Bearer {token}
```

## 测试清单

- [ ] 用户可以成功登录
- [ ] 登录后用户信息显示在头部
- [ ] 登出后用户信息清除
- [ ] 页面刷新后用户仍保持登录状态
- [ ] 未登录用户无法访问个人中心
- [ ] 未登录用户访问受保护页面时重定向到登录页
- [ ] 登录后自动跳转回原始页面
- [ ] 管理员可以访问后台页面
- [ ] 普通用户无法访问后台页面
- [ ] 令牌过期时自动重定向到登录页

## 相关文件

- `VUE/src/store/index.js` - Pinia 状态管理
- `VUE/src/router/index.js` - 路由配置和保护
- `VUE/src/services/auth.js` - 认证服务
- `VUE/src/composables/useAuth.js` - 认证组合式函数
- `VUE/src/services/api.js` - API 调用和拦截器
- `VUE/src/pages/Login.vue` - 登录页面
- `VUE/src/pages/Register.vue` - 注册页面
- `VUE/src/components/Header.vue` - 头部导航
- `VUE/src/App.vue` - 应用主组件
