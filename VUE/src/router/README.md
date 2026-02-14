# Vue Router 配置文档

## 概述

本项目使用 Vue Router 4 进行前端路由管理，支持前台用户界面和后台管理界面的路由切换，并通过路由守卫实现权限控制。

## 路由结构

### 前台路由 (Frontend Routes)

| 路径 | 名称 | 组件 | 认证要求 | 说明 |
|------|------|------|---------|------|
| `/` | Home | Home.vue | 否 | 首页 |
| `/artworks` | Artworks | Artworks.vue | 否 | 作品展示列表 |
| `/artworks/:id` | ArtworkDetail | ArtworkDetail.vue | 否 | 作品详情页 |
| `/knowledge` | Knowledge | Knowledge.vue | 否 | 知识科普列表 |
| `/knowledge/:id` | KnowledgeDetail | KnowledgeDetail.vue | 否 | 知识详情页 |
| `/community` | Community | Community.vue | 否 | 互动交流 |
| `/user` | UserCenter | UserCenter.vue | 是 | 用户中心 |

### 认证路由 (Auth Routes)

| 路径 | 名称 | 组件 | 认证要求 | 说明 |
|------|------|------|---------|------|
| `/login` | Login | Login.vue | 否 | 用户登录 |
| `/register` | Register | Register.vue | 否 | 用户注册 |

### 后台管理路由 (Admin Routes)

| 路径 | 名称 | 组件 | 认证要求 | 权限要求 | 说明 |
|------|------|------|---------|---------|------|
| `/admin/users` | AdminUsers | AdminUsers.vue | 是 | 管理员 | 用户管理 |
| `/admin/resources` | AdminResources | AdminResources.vue | 是 | 管理员 | 资源管理 |
| `/admin/interactions` | AdminInteractions | AdminInteractions.vue | 是 | 管理员 | 互动管理 |
| `/admin/system` | AdminSystem | AdminSystem.vue | 是 | 管理员 | 系统管理 |

## 路由元数据 (Route Meta)

每个路由都包含以下元数据：

```javascript
meta: {
  requiresAuth: boolean,      // 是否需要用户登录
  requiresAdmin: boolean,     // 是否需要管理员权限
  title: string              // 页面标题
}
```

## 路由守卫 (Route Guards)

### 全局前置守卫 (beforeEach)

在路由导航前执行，主要功能：

1. **用户认证检查**：如果路由需要认证但用户未登录，重定向到登录页
2. **权限检查**：如果路由需要管理员权限但用户不是管理员，重定向到首页
3. **登录状态检查**：如果用户已登录且访问登录/注册页，重定向到首页
4. **页面标题更新**：根据路由元数据更新页面标题
5. **用户状态恢复**：从 localStorage 恢复用户登录状态

### 全局后置钩子 (afterEach)

在路由导航完成后执行，主要功能：

1. **页面滚动**：将页面滚动到顶部

## 认证状态管理 (Auth Store)

使用 Pinia 管理全局认证状态：

```javascript
// 状态
user              // 当前登录用户信息
isLoggedIn        // 是否已登录
userRole          // 用户角色 ('user' 或 'admin')

// 计算属性
isAdmin           // 是否为管理员

// 方法
setUser()         // 设置用户信息并保存到 localStorage
logout()          // 清除用户信息
loadUserFromStorage()  // 从 localStorage 恢复用户信息
```

## 使用示例

### 在组件中使用路由

```vue
<script setup>
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 导航到其他页面
const goToArtworks = () => {
  router.push({ name: 'Artworks' })
}

// 获取当前路由参数
const artworkId = route.params.id
</script>
```

### 在组件中使用认证状态

```vue
<script setup>
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()

// 检查用户是否已登录
if (authStore.isLoggedIn) {
  console.log('用户已登录:', authStore.user.username)
}

// 检查用户是否为管理员
if (authStore.isAdmin) {
  console.log('用户是管理员')
}

// 登出用户
const logout = () => {
  authStore.logout()
}
</script>
```

### 在路由中添加新路由

```javascript
// 在 router/index.js 中添加新路由
const newRoute = {
  path: '/new-page',
  name: 'NewPage',
  component: () => import('../pages/NewPage.vue'),
  meta: {
    requiresAuth: false,
    requiresAdmin: false,
    title: '新页面'
  }
}

// 添加到对应的路由数组
frontendRoutes.push(newRoute)
```

## 权限控制流程

```
用户访问路由
    ↓
检查是否需要认证
    ├─ 需要认证 + 未登录 → 重定向到登录页
    └─ 不需要认证 → 继续
    ↓
检查是否需要管理员权限
    ├─ 需要管理员 + 不是管理员 → 重定向到首页
    └─ 不需要管理员 → 继续
    ↓
检查是否已登录且访问登录/注册页
    ├─ 已登录 + 访问登录/注册 → 重定向到首页
    └─ 其他情况 → 继续
    ↓
更新页面标题
    ↓
导航完成
```

## 最佳实践

1. **使用命名路由**：使用 `router.push({ name: 'RouteName' })` 而不是硬编码路径
2. **保护敏感路由**：为需要认证的路由设置 `requiresAuth: true`
3. **管理员路由隔离**：为后台管理路由设置 `requiresAdmin: true`
4. **页面标题管理**：为每个路由设置合适的 `title` 元数据
5. **错误处理**：在路由守卫中处理权限不足的情况
6. **用户状态持久化**：使用 localStorage 保存用户登录状态

## 常见问题

### Q: 如何在登录后重定向到之前访问的页面？

A: 路由守卫已经在登录页的查询参数中保存了重定向地址：

```javascript
// 在登录成功后
const redirect = route.query.redirect || '/'
router.push(redirect)
```

### Q: 如何添加新的后台管理页面？

A: 在 `adminRoutes` 数组中添加新路由，并确保设置 `requiresAdmin: true`

### Q: 如何检查用户是否有特定权限？

A: 使用 `useAuthStore()` 获取用户信息，然后根据 `user.role` 进行权限检查

## 相关文件

- `router/index.js` - 路由配置文件
- `router/routeUtils.js` - 路由工具函数
- `stores/authStore.js` - 认证状态管理
- `components/Sidebar.vue` - 导航栏组件
