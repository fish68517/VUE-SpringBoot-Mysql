# 前端导航和菜单系统

## 概述

本系统实现了一个完整的前端导航和菜单系统，支持游客端和管理员端的菜单切换。系统采用组件化设计，包含顶部导航栏和侧边栏菜单两个主要部分。

## 组件结构

### 1. TopNavigation 组件 (VUE/src/components/TopNavigation.vue)

**功能**：
- 显示应用标题和品牌
- 未登录用户显示顶部导航菜单（景点、酒店、商品、路线）
- 显示登录/注册按钮
- 已登录用户显示用户下拉菜单

**Props**：
- `isLoggedIn` (Boolean): 用户是否已登录
- `username` (String): 用户名
- `userRole` (String): 用户角色 ('tourist' 或 'admin')

**Events**：
- `logout`: 用户点击退出登录时触发

**特性**：
- 响应式设计，支持移动端
- 点击标题可返回首页
- 用户下拉菜单包含个人资料、管理员面板（仅管理员）、退出登录

### 2. SidebarMenu 组件 (VUE/src/components/SidebarMenu.vue)

**功能**：
- 显示侧边栏菜单
- 根据用户角色显示不同的菜单项
- 显示用户角色标识（游客/管理员）

**Props**：
- `userRole` (String): 用户角色 ('tourist' 或 'admin')

**游客菜单项**：
- 首页
- 景点
- 酒店
- 商品
- 推荐路线
- 我的订单
- 我的收藏

**管理员菜单项**：
- 用户管理
- 景点管理
- 酒店管理
- 订单管理
- 留言管理
- 公告管理
- 路线管理

**特性**：
- 自动高亮当前活跃菜单项
- 支持路由导航
- 响应式设计
- 角色标识清晰

### 3. App.vue 主应用组件

**功能**：
- 整合 TopNavigation 和 SidebarMenu 组件
- 管理用户登录状态
- 控制侧边栏显示/隐藏
- 处理用户登出

**状态管理**：
- `isLoggedIn`: 用户登录状态
- `userInfo`: 用户信息（用户名、角色）

**计算属性**：
- `showSidebar`: 根据登录状态和当前路由决定是否显示侧边栏

## 菜单切换逻辑

### 游客端菜单
当用户以游客身份登录时，侧边栏显示游客菜单，包括：
- 首页、景点、酒店、商品、推荐路线等浏览功能
- 我的订单、我的收藏等个人功能

### 管理员端菜单
当用户以管理员身份登录时，侧边栏显示管理员菜单，包括：
- 用户管理、景点管理、酒店管理等数据管理功能
- 订单管理、留言管理、公告管理等业务管理功能

### 菜单切换触发条件
1. 用户登录时：根据 localStorage 中存储的用户角色加载相应菜单
2. 用户切换账户时：菜单自动更新
3. 用户登出时：隐藏侧边栏，显示游客导航菜单

## 样式设计

### 顶部导航栏
- 背景色：蓝色渐变 (#409eff - #66b1ff)
- 高度：60px
- 包含阴影效果
- 响应式设计

### 侧边栏菜单
- 宽度：220px（桌面）、180px（平板）、150px（手机）
- 背景色：浅灰色 (#f5f7fa)
- 活跃菜单项：蓝色背景 (#409eff)
- 悬停效果：浅蓝色背景 (#e0e9ff)

### 角色标识
- 游客：蓝色标识 (#409eff)
- 管理员：红色标识 (#f56c6c)

## 响应式设计

系统支持三个断点的响应式设计：

### 桌面版 (> 768px)
- 完整的顶部导航菜单
- 220px 宽的侧边栏
- 完整的菜单文本和图标

### 平板版 (768px - 480px)
- 简化的顶部导航菜单
- 180px 宽的侧边栏
- 较小的字体

### 手机版 (< 480px)
- 隐藏顶部导航菜单
- 150px 宽的侧边栏
- 最小化的菜单项

## 图标使用

系统使用 Element Plus 提供的图标库：
- `HomeFilled`: 首页
- `Location`: 景点/位置
- `Building`: 酒店
- `ShoppingBag`: 商品
- `ShoppingCart`: 订单
- `MapLocation`: 路线
- `StarFilled`: 收藏
- `User`: 用户
- `UserFilled`: 用户（填充）
- `Setting`: 设置/管理员面板
- `Bell`: 公告
- `ChatDotSquare`: 留言
- `Logout`: 退出登录
- `ArrowDown`: 下拉箭头

## 使用示例

### 在 App.vue 中使用

```vue
<template>
  <div id="app" class="app-container">
    <el-container>
      <!-- 顶部导航栏 -->
      <top-navigation 
        :is-logged-in="isLoggedIn"
        :username="userInfo.username"
        :user-role="userInfo.role"
        @logout="logout"
      />

      <el-container class="main-container">
        <!-- 侧边栏菜单 -->
        <sidebar-menu 
          v-if="showSidebar"
          :user-role="userInfo.role"
        />

        <!-- 主内容区 -->
        <el-main class="app-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>
```

## 用户登录流程

1. 用户访问登录页面
2. 输入用户名和密码
3. 登录成功后，用户信息存储到 localStorage
4. App.vue 检测到登录状态变化
5. 根据用户角色显示相应的菜单
6. 侧边栏自动显示

## 用户登出流程

1. 用户点击用户下拉菜单中的"退出登录"
2. TopNavigation 组件触发 logout 事件
3. App.vue 处理 logout 事件
4. 清除 localStorage 中的用户信息
5. 隐藏侧边栏
6. 重定向到登录页面

## 路由配置

系统支持以下路由：

### 游客路由
- `/` - 首页
- `/attractions` - 景点列表
- `/hotels` - 酒店列表
- `/products` - 商品列表
- `/routes` - 推荐路线
- `/orders` - 我的订单
- `/favorites` - 我的收藏
- `/profile` - 个人资料

### 管理员路由
- `/admin/users` - 用户管理
- `/admin/attractions` - 景点管理
- `/admin/hotels` - 酒店管理
- `/admin/orders` - 订单管理
- `/admin/comments` - 留言管理
- `/admin/announcements` - 公告管理
- `/admin/routes` - 路线管理

### 认证路由
- `/login` - 登录页面
- `/register` - 注册页面

## 权限控制

系统在路由层面实现权限控制：

1. **requiresAuth**: 标记需要登录的路由
2. **role**: 标记需要特定角色的路由

路由守卫 (router.beforeEach) 在导航前检查：
- 用户是否已登录
- 用户是否有访问该路由的权限

## 最佳实践

1. **组件化**：将导航和菜单分离为独立组件，便于维护和复用
2. **响应式设计**：支持多种设备尺寸
3. **清晰的角色区分**：通过不同的菜单和标识区分游客和管理员
4. **用户体验**：提供清晰的导航和菜单反馈
5. **可访问性**：使用语义化的 HTML 和 ARIA 属性

## 扩展建议

1. **菜单权限细分**：可以根据更细粒度的权限显示/隐藏菜单项
2. **菜单搜索**：为管理员菜单添加搜索功能
3. **菜单收起/展开**：添加侧边栏收起功能以节省空间
4. **面包屑导航**：在主内容区添加面包屑导航
5. **菜单快捷键**：为常用菜单项添加快捷键支持
