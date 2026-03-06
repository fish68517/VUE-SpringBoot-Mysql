# UI 优化指南

本文档描述了在线投稿系统前端的UI优化实现，包括统一的页面风格、表单验证、加载状态和操作反馈。

## 目录

1. [全局样式系统](#全局样式系统)
2. [表单验证](#表单验证)
3. [加载状态](#加载状态)
4. [操作反馈](#操作反馈)
5. [页面布局](#页面布局)
6. [使用示例](#使用示例)

## 全局样式系统

### 颜色变量

全局样式定义了一套完整的CSS变量，用于保持整个应用的视觉一致性：

```css
:root {
  /* 主色调 */
  --primary-color: #409eff;
  --primary-light: #66b1ff;
  --primary-dark: #0a66cc;
  
  /* 状态颜色 */
  --success-color: #67c23a;
  --warning-color: #e6a23c;
  --danger-color: #f56c6c;
  --info-color: #909399;
  
  /* 中性颜色 */
  --text-primary: #303133;
  --text-secondary: #606266;
  --text-disabled: #909399;
  --border-color: #dcdfe6;
  --bg-color: #f5f7fa;
  --bg-light: #fafafa;
}
```

### 间距系统

使用统一的间距变量确保页面布局的一致性：

```css
--spacing-xs: 4px;
--spacing-sm: 8px;
--spacing-md: 12px;
--spacing-lg: 16px;
--spacing-xl: 20px;
--spacing-2xl: 24px;
--spacing-3xl: 32px;
```

### 圆角和阴影

```css
--radius-sm: 2px;
--radius-md: 4px;
--radius-lg: 8px;

--shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
--shadow-md: 0 2px 12px rgba(0, 0, 0, 0.1);
--shadow-lg: 0 4px 20px rgba(0, 0, 0, 0.15);
```

## 表单验证

### 使用 useFormValidation 组合式函数

```javascript
import { useFormValidation } from '@/composables/useFormValidation'

export default {
  setup() {
    const { validationRules, validateForm, showValidationError } = useFormValidation()
    
    return {
      validationRules,
      validateForm,
      showValidationError
    }
  }
}
```

### 预定义的验证规则

系统提供了常用的验证规则：

- `username`: 用户名验证（3-20字符，只能包含字母、数字、下划线和连字符）
- `email`: 邮箱验证
- `phone`: 手机号验证
- `password`: 密码验证（6-20字符）
- `title`: 标题验证（5-200字符）
- `abstract`: 摘要验证（50-500字符）
- `content`: 内容验证

### 表单验证示例

```vue
<template>
  <el-form ref="formRef" :model="form" :rules="rules">
    <el-form-item label="用户名" prop="username">
      <el-input v-model="form.username" placeholder="请输入用户名" />
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="form.email" placeholder="请输入邮箱" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="handleSubmit">提交</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref } from 'vue'
import { useFormValidation } from '@/composables/useFormValidation'

const formRef = ref(null)
const { validationRules, validateForm } = useFormValidation()

const form = ref({
  username: '',
  email: ''
})

const rules = {
  username: validationRules.username,
  email: validationRules.email
}

const handleSubmit = async () => {
  if (!await validateForm(formRef.value)) {
    return
  }
  // 提交表单
}
</script>
```

## 加载状态

### 使用 useLoading 组合式函数

```javascript
import { useLoading } from '@/composables/useLoading'

export default {
  setup() {
    const { 
      loading, 
      executeWithLoading, 
      showSuccess, 
      showError 
    } = useLoading()
    
    return {
      loading,
      executeWithLoading,
      showSuccess,
      showError
    }
  }
}
```

### 加载状态示例

```vue
<template>
  <el-button 
    type="primary" 
    @click="handleSave"
    :loading="loading"
  >
    {{ loading ? '保存中...' : '保存' }}
  </el-button>
</template>

<script setup>
import { useLoading } from '@/composables/useLoading'

const { loading, executeWithLoading } = useLoading()

const handleSave = async () => {
  const result = await executeWithLoading(
    () => saveData(),
    {
      loadingMessage: '保存中...',
      successMessage: '保存成功',
      errorMessage: '保存失败'
    }
  )
  
  if (result.success) {
    // 处理成功
  }
}
</script>
```

### LoadingOverlay 组件

用于显示全屏加载状态：

```vue
<template>
  <div>
    <LoadingOverlay :visible="loading" text="加载中..." />
    <!-- 页面内容 -->
  </div>
</template>

<script setup>
import { ref } from 'vue'
import LoadingOverlay from '@/components/LoadingOverlay.vue'

const loading = ref(false)
</script>
```

## 操作反馈

### 使用 messageHelper 工具

```javascript
import messageHelper from '@/utils/messageHelper'

// 显示成功消息
messageHelper.success('操作成功')

// 显示错误消息
messageHelper.error('操作失败')

// 显示警告消息
messageHelper.warning('请确认操作')

// 显示信息消息
messageHelper.info('这是一条提示信息')

// 显示确认对话框
messageHelper.confirm('确认删除', '确定要删除这条记录吗？')
  .then(() => {
    // 用户点击确定
  })
  .catch(() => {
    // 用户点击取消
  })
```

### FormFeedback 组件

用于显示表单级别的反馈信息：

```vue
<template>
  <div>
    <FormFeedback 
      :visible="showFeedback"
      type="success"
      title="成功"
      message="表单提交成功"
      @close="showFeedback = false"
    />
    <!-- 表单内容 -->
  </div>
</template>

<script setup>
import { ref } from 'vue'
import FormFeedback from '@/components/FormFeedback.vue'

const showFeedback = ref(false)
</script>
```

## 页面布局

### 使用 usePageLayout 组合式函数

```javascript
import { usePageLayout } from '@/composables/usePageLayout'

export default {
  setup() {
    const { 
      getPageContainerClass,
      getSectionClass,
      getFormContainerClass,
      getButtonGroupClass,
      getCardClass
    } = usePageLayout()
    
    return {
      getPageContainerClass,
      getSectionClass,
      getFormContainerClass,
      getButtonGroupClass,
      getCardClass
    }
  }
}
```

### 页面布局示例

```vue
<template>
  <div :class="getPageContainerClass()">
    <h1>页面标题</h1>
    
    <div :class="getSectionClass({ elevated: true })">
      <el-form :class="getFormContainerClass()">
        <!-- 表单内容 -->
      </el-form>
      
      <div :class="getButtonGroupClass({ align: 'right' })">
        <el-button>取消</el-button>
        <el-button type="primary">提交</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { usePageLayout } from '@/composables/usePageLayout'

const { 
  getPageContainerClass,
  getSectionClass,
  getFormContainerClass,
  getButtonGroupClass
} = usePageLayout()
</script>
```

### 布局工具类

系统提供了多个CSS工具类用于快速布局：

#### 网格布局

```html
<!-- 2列网格 -->
<div class="grid grid--2">
  <div>项目1</div>
  <div>项目2</div>
</div>

<!-- 3列网格 -->
<div class="grid grid--3">
  <div>项目1</div>
  <div>项目2</div>
  <div>项目3</div>
</div>
```

#### Flex 布局

```html
<!-- 行布局 -->
<div class="flex-row">
  <div>项目1</div>
  <div>项目2</div>
</div>

<!-- 列布局 -->
<div class="flex-col">
  <div>项目1</div>
  <div>项目2</div>
</div>

<!-- 居中布局 -->
<div class="flex-center">
  <div>居中内容</div>
</div>

<!-- 两端对齐 -->
<div class="flex-between">
  <div>左侧</div>
  <div>右侧</div>
</div>
```

#### 间距工具类

```html
<!-- 垂直间距 -->
<div class="space-y-lg">
  <div>项目1</div>
  <div>项目2</div>
  <div>项目3</div>
</div>

<!-- 水平间距 -->
<div class="space-x-lg">
  <div>项目1</div>
  <div>项目2</div>
  <div>项目3</div>
</div>
```

#### 文本工具类

```html
<!-- 文本对齐 -->
<div class="text-center">居中文本</div>
<div class="text-right">右对齐文本</div>

<!-- 文本颜色 -->
<div class="text-success">成功文本</div>
<div class="text-danger">错误文本</div>
<div class="text-warning">警告文本</div>
<div class="text-muted">灰色文本</div>
```

## 使用示例

### 完整的表单页面示例

```vue
<template>
  <div :class="getPageContainerClass()">
    <h1>编辑用户信息</h1>
    
    <div :class="getSectionClass({ elevated: true })">
      <el-form 
        ref="formRef"
        :model="form"
        :rules="rules"
        :class="getFormContainerClass()"
        label-width="120px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
        
        <el-form-item>
          <div :class="getButtonGroupClass({ align: 'right' })">
            <el-button @click="handleCancel">取消</el-button>
            <el-button 
              type="primary" 
              @click="handleSubmit"
              :loading="loading"
            >
              {{ loading ? '保存中...' : '保存' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useFormValidation } from '@/composables/useFormValidation'
import { useLoading } from '@/composables/useLoading'
import { usePageLayout } from '@/composables/usePageLayout'

const router = useRouter()
const formRef = ref(null)
const { validationRules, validateForm } = useFormValidation()
const { loading, executeWithLoading } = useLoading()
const { 
  getPageContainerClass,
  getSectionClass,
  getFormContainerClass,
  getButtonGroupClass
} = usePageLayout()

const form = ref({
  username: 'john_doe',
  email: '',
  phone: ''
})

const rules = {
  email: validationRules.email,
  phone: validationRules.phone
}

const handleSubmit = async () => {
  if (!await validateForm(formRef.value)) {
    return
  }

  const result = await executeWithLoading(
    () => updateUserInfo(form.value),
    {
      loadingMessage: '保存中...',
      successMessage: '用户信息更新成功',
      errorMessage: '更新失败，请稍后重试'
    }
  )

  if (result.success) {
    router.back()
  }
}

const handleCancel = () => {
  router.back()
}

const updateUserInfo = async (data) => {
  // API 调用
  return new Promise((resolve) => {
    setTimeout(() => resolve(data), 1000)
  })
}
</script>
```

## 最佳实践

1. **始终使用全局样式变量** - 不要硬编码颜色、间距等值
2. **使用组合式函数** - 利用 `useFormValidation`、`useLoading` 等提供的功能
3. **提供清晰的反馈** - 使用 `messageHelper` 为用户操作提供及时反馈
4. **保持一致的布局** - 使用 `usePageLayout` 和布局工具类
5. **处理加载状态** - 在异步操作时显示加载状态
6. **验证用户输入** - 使用预定义的验证规则
7. **响应式设计** - 使用网格和Flex布局确保移动设备兼容性

## 文件结构

```
VUE/src/
├── styles/
│   ├── global.css          # 全局样式和CSS变量
│   └── layout.css          # 页面布局样式
├── composables/
│   ├── useFormValidation.js    # 表单验证组合式函数
│   ├── useLoading.js           # 加载状态组合式函数
│   └── usePageLayout.js        # 页面布局组合式函数
├── components/
│   ├── LoadingOverlay.vue      # 全屏加载组件
│   └── FormFeedback.vue        # 表单反馈组件
└── utils/
    └── messageHelper.js        # 消息提示工具
```
