# 前端通用组件使用指南

本文档介绍文山壮族刺绣网站系统中的通用组件及其使用方法。

## 1. 导航栏组件 (Sidebar)

**位置**: `VUE/src/components/Sidebar.vue`

**功能**:
- 显示左侧导航菜单
- 支持前台和后台菜单切换（基于用户角色）
- 显示用户登录状态和认证链接
- 响应式设计

**使用方法**:
```vue
<template>
  <Sidebar />
</template>

<script setup>
import { Sidebar } from '@/components'
</script>
```

**特性**:
- 自动根据当前路由高亮菜单项
- 管理员用户自动显示后台管理菜单
- 集成登出功能
- 科技蓝配色方案

---

## 2. 页脚组件 (Footer)

**位置**: `VUE/src/components/Footer.vue`

**功能**:
- 显示网站底部信息
- 展示快速链接
- 显示联系信息
- 社交媒体链接

**使用方法**:
```vue
<template>
  <Footer />
</template>

<script setup>
import { Footer } from '@/components'
</script>
```

**特性**:
- 自动从系统设置加载联系信息
- 响应式网格布局
- 科技蓝配色方案
- 包含版权信息

---

## 3. 分页组件 (Pagination)

**位置**: `VUE/src/components/Pagination.vue`

**功能**:
- 提供分页导航
- 显示当前页码和总页数
- 支持快速跳转到首页/末页

**使用方法**:
```vue
<template>
  <div>
    <div class="content">
      <!-- 列表内容 -->
    </div>
    <Pagination
      :current-page="currentPage"
      :total-pages="totalPages"
      :total-items="totalItems"
      @update:current-page="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Pagination } from '@/components'

const currentPage = ref(1)
const totalPages = ref(10)
const totalItems = ref(100)

const handlePageChange = (newPage) => {
  currentPage.value = newPage
  // 加载新页面数据
}
</script>
```

**属性**:
- `currentPage` (Number): 当前页码，默认为 1
- `totalPages` (Number): 总页数，默认为 1
- `totalItems` (Number): 总项目数，默认为 0
- `maxVisiblePages` (Number): 最多显示的页码数，默认为 5

**事件**:
- `update:currentPage`: 页码改变时触发

---

## 4. 加载动画组件 (Loading)

**位置**: `VUE/src/components/Loading.vue`

**功能**:
- 显示加载中的动画
- 支持全屏和局部加载
- 可自定义加载提示文本

**使用方法**:
```vue
<template>
  <div>
    <Loading :is-loading="isLoading" message="加载中..." />
    
    <!-- 或全屏加载 -->
    <Loading :is-loading="isLoading" :fullscreen="true" message="处理中..." />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Loading } from '@/components'

const isLoading = ref(false)

const fetchData = async () => {
  isLoading.value = true
  try {
    // 获取数据
  } finally {
    isLoading.value = false
  }
}
</script>
```

**属性**:
- `isLoading` (Boolean): 是否显示加载动画，默认为 false
- `message` (String): 加载提示文本，默认为 "加载中..."
- `fullscreen` (Boolean): 是否全屏显示，默认为 false

---

## 5. 消息提示组件 (Toast)

**位置**: `VUE/src/components/Toast.vue`

**功能**:
- 显示临时消息提示
- 支持多种消息类型（成功、错误、警告、信息）
- 自动消失或手动关闭
- 支持自定义位置

**使用方法**:

### 在 App.vue 中注册（已完成）:
```vue
<template>
  <Toast ref="toastRef" />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Toast } from '@/components'
import { useToast } from '@/utils/useToast'

const toastRef = ref(null)
const { setToastInstance } = useToast()

onMounted(() => {
  if (toastRef.value) {
    setToastInstance(toastRef.value)
  }
})
</script>
```

### 在其他组件中使用:
```vue
<template>
  <button @click="showSuccess">显示成功消息</button>
  <button @click="showError">显示错误消息</button>
</template>

<script setup>
import { useToast } from '@/utils/useToast'

const { success, error, warning, info } = useToast()

const showSuccess = () => {
  success('操作成功！', {
    title: '成功',
    duration: 3000
  })
}

const showError = () => {
  error('操作失败，请重试', {
    title: '错误',
    duration: 5000
  })
}

const showWarning = () => {
  warning('请注意此操作', {
    title: '警告'
  })
}

const showInfo = () => {
  info('这是一条信息提示', {
    title: '信息'
  })
}
</script>
```

**方法**:
- `success(message, options)`: 显示成功消息
- `error(message, options)`: 显示错误消息
- `warning(message, options)`: 显示警告消息
- `info(message, options)`: 显示信息消息

**选项**:
- `title` (String): 消息标题（可选）
- `duration` (Number): 自动消失时间（毫秒），0 表示不自动消失，默认 3000
- `icon` (String): 自定义图标（可选）

**位置属性**:
- `top-left`: 左上角
- `top-right`: 右上角（默认）
- `bottom-left`: 左下角
- `bottom-right`: 右下角
- `top-center`: 顶部中央
- `bottom-center`: 底部中央

---

## 6. 组件导入

所有组件都可以从 `@/components` 导入：

```javascript
import { 
  Sidebar, 
  Footer, 
  Pagination, 
  Loading, 
  Toast 
} from '@/components'
```

---

## 7. 样式定制

所有组件都使用全局 CSS 变量，可在 `VUE/src/styles/global.css` 中修改：

```css
:root {
  /* 科技蓝配色 */
  --primary-color: #0066cc;
  --primary-light: #3385dd;
  --primary-dark: #004499;
  
  /* 其他变量... */
}
```

---

## 8. 响应式设计

所有组件都支持响应式设计，在移动设备上会自动调整布局和字体大小。

---

## 9. 无障碍性

所有组件都遵循基本的无障碍设计原则：
- 按钮和链接都有适当的 hover 状态
- 使用语义化的 HTML 元素
- 支持键盘导航

---

## 10. 常见用法示例

### 列表页面完整示例

```vue
<template>
  <div class="page">
    <h1>作品列表</h1>
    
    <Loading :is-loading="isLoading" message="加载作品中..." />
    
    <div v-if="!isLoading" class="content">
      <div class="list">
        <div v-for="item in items" :key="item.id" class="item">
          {{ item.title }}
        </div>
      </div>
      
      <Pagination
        :current-page="currentPage"
        :total-pages="totalPages"
        :total-items="totalItems"
        @update:current-page="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Loading, Pagination } from '@/components'
import { useToast } from '@/utils/useToast'
import { ArtworkService } from '@/services'

const isLoading = ref(false)
const currentPage = ref(1)
const totalPages = ref(1)
const totalItems = ref(0)
const items = ref([])
const { success, error } = useToast()

const fetchData = async () => {
  isLoading.value = true
  try {
    const response = await ArtworkService.getArtworks({
      page: currentPage.value,
      pageSize: 10
    })
    items.value = response.data.content
    totalPages.value = response.data.totalPages
    totalItems.value = response.data.totalElements
  } catch (err) {
    error('加载失败，请重试')
  } finally {
    isLoading.value = false
  }
}

const handlePageChange = (newPage) => {
  currentPage.value = newPage
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>
```

