# 样式系统文档

## 概述

文山壮族刺绣网站系统采用科技蓝配色方案，提供完整的样式系统和工具类。所有样式文件都在 `src/styles/` 目录中。

## 样式文件结构

### 1. global.css
全局样式和 CSS 变量定义

**包含内容：**
- CSS 变量定义（颜色、字体、间距、圆角、阴影）
- 基础元素重置（*, html, body, a, button, input, table 等）
- 滚动条样式
- 基础响应式设计

**主要 CSS 变量：**
```css
/* 科技蓝配色 */
--primary-color: #0066cc;        /* 主色 */
--primary-light: #3385dd;        /* 浅色 */
--primary-dark: #004499;         /* 深色 */
--secondary-color: #00ccff;      /* 次要色 */
--accent-color: #ff6600;         /* 强调色 */

/* 中性色 */
--text-primary: #333333;         /* 主文本 */
--text-secondary: #666666;       /* 次文本 */
--text-light: #999999;           /* 浅文本 */
--bg-primary: #ffffff;           /* 主背景 */
--bg-secondary: #f5f5f5;         /* 次背景 */
--bg-tertiary: #eeeeee;          /* 三级背景 */
--border-color: #dddddd;         /* 边框色 */

/* 状态色 */
--success-color: #00cc66;        /* 成功 */
--warning-color: #ffaa00;        /* 警告 */
--error-color: #ff3333;          /* 错误 */
--info-color: #0066cc;           /* 信息 */

/* 间距 */
--spacing-xs: 4px;
--spacing-sm: 8px;
--spacing-md: 16px;
--spacing-lg: 24px;
--spacing-xl: 32px;

/* 字体 */
--font-size-sm: 12px;
--font-size-base: 14px;
--font-size-lg: 16px;
--font-size-xl: 18px;
--font-size-2xl: 24px;

/* 圆角 */
--border-radius-sm: 4px;
--border-radius-md: 8px;
--border-radius-lg: 12px;

/* 阴影 */
--shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
--shadow-md: 0 4px 6px rgba(0, 0, 0, 0.1);
--shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.1);
```

### 2. layout.css
布局和页面结构样式

**包含内容：**
- 应用容器布局
- 页面标题和副标题
- 内容区域
- 列表组件
- 标签和徽章
- 提示框
- 加载状态
- 空状态
- 分隔线

**常用类名：**
```css
.app-container          /* 应用主容器 */
.main-wrapper           /* 主内容包装器 */
.main-content           /* 主内容区域 */
.page-header            /* 页面头部 */
.page-title             /* 页面标题 */
.content-section        /* 内容区域 */
.list-group             /* 列表组 */
.badge                  /* 徽章 */
.alert                  /* 提示框 */
.loading                /* 加载状态 */
.empty-state            /* 空状态 */
```

### 3. components.css
组件样式

**包含内容：**
- 导航栏
- 面包屑导航
- 标签页
- 模态框
- 下拉菜单
- 进度条
- 评分
- 卡片网格

**常用类名：**
```css
.navbar                 /* 导航栏 */
.breadcrumb             /* 面包屑 */
.tabs                   /* 标签页 */
.modal                  /* 模态框 */
.dropdown               /* 下拉菜单 */
.progress               /* 进度条 */
.rating                 /* 评分 */
.card-grid              /* 卡片网格 */
.card-item              /* 卡片项 */
```

### 4. responsive.css
响应式设计工具类

**包含内容：**
- 显示/隐藏工具类
- 宽度和高度工具类
- 溢出处理
- 文本截断
- 位置工具类
- 浮动和清除浮动
- 屏幕尺寸断点

**断点定义：**
```css
小屏幕 (max-width: 576px)
中屏幕 (576px - 768px)
大屏幕 (min-width: 769px)
```

**常用类名：**
```css
.d-none                 /* 隐藏 */
.d-block                /* 块级显示 */
.d-flex                 /* 弹性显示 */
.w-100                  /* 宽度 100% */
.w-50                   /* 宽度 50% */
.text-truncate          /* 文本截断 */
.text-clamp-2           /* 文本限制 2 行 */
.overflow-auto          /* 自动溢出 */
```

### 5. theme.css
主题和颜色工具类

**包含内容：**
- 颜色工具类
- 背景颜色
- 渐变背景
- 阴影变体
- 圆角变体
- 透明度
- 过渡效果
- 变换效果

**常用类名：**
```css
.theme-primary          /* 主色文本 */
.bg-theme-primary       /* 主色背景 */
.bg-gradient-primary    /* 主色渐变 */
.text-primary           /* 主文本色 */
.text-secondary         /* 次文本色 */
.shadow-md              /* 中等阴影 */
.rounded-lg             /* 大圆角 */
.opacity-50             /* 50% 透明度 */
.transition-all         /* 全部过渡 */
.scale-hover:hover      /* 悬停缩放 */
```

## 使用示例

### 基础布局
```html
<div class="app-container">
  <aside class="sidebar"><!-- 侧边栏 --></aside>
  <div class="main-wrapper">
    <main class="main-content">
      <div class="page-header">
        <h1 class="page-title">页面标题</h1>
      </div>
      <div class="content-section">
        <!-- 内容 -->
      </div>
    </main>
    <footer><!-- 页脚 --></footer>
  </div>
</div>
```

### 卡片组件
```html
<div class="card">
  <div class="card-header">
    <h3>卡片标题</h3>
  </div>
  <div class="card-body">
    <!-- 卡片内容 -->
  </div>
  <div class="card-footer">
    <!-- 卡片页脚 -->
  </div>
</div>
```

### 按钮
```html
<button class="btn btn-primary">主按钮</button>
<button class="btn btn-secondary">次按钮</button>
<button class="btn btn-success btn-sm">小成功按钮</button>
<button class="btn btn-danger btn-lg">大危险按钮</button>
```

### 表单
```html
<div class="form-group">
  <label class="form-label">用户名</label>
  <input type="text" class="form-control" placeholder="请输入用户名">
  <small class="form-text">输入您的用户名</small>
</div>
```

### 网格布局
```html
<div class="grid grid-3">
  <div class="card"><!-- 卡片 1 --></div>
  <div class="card"><!-- 卡片 2 --></div>
  <div class="card"><!-- 卡片 3 --></div>
</div>
```

### 响应式工具类
```html
<!-- 在小屏幕隐藏，大屏幕显示 -->
<div class="d-none-sm d-block-lg">
  仅在大屏幕显示
</div>

<!-- 响应式宽度 -->
<div class="w-100-sm w-50-md w-33-lg">
  响应式宽度
</div>

<!-- 响应式文本对齐 -->
<p class="text-center-sm text-left-lg">
  响应式文本对齐
</p>
```

### 颜色和主题
```html
<!-- 文本颜色 -->
<p class="text-primary">主文本色</p>
<p class="text-secondary">次文本色</p>
<p class="text-danger">错误色</p>

<!-- 背景颜色 -->
<div class="bg-theme-primary">主色背景</div>
<div class="bg-gradient-primary">渐变背景</div>

<!-- 徽章 -->
<span class="badge badge-primary">主徽章</span>
<span class="badge badge-success">成功徽章</span>
```

## 响应式设计

### 断点
- **小屏幕**: max-width: 576px (手机)
- **中屏幕**: 576px - 768px (平板)
- **大屏幕**: min-width: 769px (桌面)

### 响应式前缀
- `-sm`: 小屏幕
- `-md`: 中屏幕
- `-lg`: 大屏幕

### 示例
```css
/* 小屏幕隐藏 */
.d-none-sm { display: none; }

/* 中屏幕显示为块级 */
.d-block-md { display: block; }

/* 大屏幕显示为弹性 */
.d-flex-lg { display: flex; }
```

## 颜色系统

### 科技蓝主题
- **主色**: #0066cc (科技蓝)
- **浅色**: #3385dd
- **深色**: #004499
- **次要色**: #00ccff (天蓝)
- **强调色**: #ff6600 (橙色)

### 状态色
- **成功**: #00cc66 (绿色)
- **警告**: #ffaa00 (黄色)
- **错误**: #ff3333 (红色)
- **信息**: #0066cc (蓝色)

## 间距系统

```
xs: 4px
sm: 8px
md: 16px
lg: 24px
xl: 32px
```

## 字体系统

```
sm: 12px
base: 14px
lg: 16px
xl: 18px
2xl: 24px
```

## 最佳实践

1. **使用 CSS 变量**: 优先使用 CSS 变量而不是硬编码颜色值
2. **工具类优先**: 使用预定义的工具类而不是编写新的 CSS
3. **响应式设计**: 始终考虑移动设备，使用响应式工具类
4. **一致性**: 保持设计的一致性，使用统一的颜色、间距和字体
5. **可访问性**: 确保足够的颜色对比度和可读性

## 自定义

### 修改颜色
在 `global.css` 中修改 `:root` 中的 CSS 变量：

```css
:root {
  --primary-color: #0066cc;  /* 修改主色 */
  --success-color: #00cc66;  /* 修改成功色 */
}
```

### 添加新的工具类
在相应的样式文件中添加新的类名：

```css
/* 在 theme.css 中添加 */
.bg-custom {
  background-color: #custom-color;
}
```

## 浏览器兼容性

- Chrome (最新版本)
- Firefox (最新版本)
- Safari (最新版本)
- Edge (最新版本)
- IE 11 (部分功能)

## 性能优化

- 所有样式文件都已压缩
- 使用 CSS 变量减少重复代码
- 工具类设计用于最小化 CSS 输出
- 响应式设计使用移动优先方法

## 常见问题

### Q: 如何修改主色？
A: 在 `global.css` 中修改 `--primary-color` 变量。

### Q: 如何添加新的颜色？
A: 在 `:root` 中添加新的 CSS 变量，然后在 `theme.css` 中创建相应的工具类。

### Q: 如何实现暗黑模式？
A: 创建一个新的 CSS 文件，定义暗黑模式的 CSS 变量，并在需要时切换。

### Q: 如何自定义间距？
A: 在 `:root` 中修改 `--spacing-*` 变量。

## 相关资源

- [CSS 变量文档](https://developer.mozilla.org/en-US/docs/Web/CSS/--*)
- [Flexbox 布局](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Flexible_Box_Layout)
- [Grid 布局](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Grid_Layout)
- [响应式设计](https://developer.mozilla.org/en-US/docs/Learn/CSS/CSS_layout/Responsive_Design)
