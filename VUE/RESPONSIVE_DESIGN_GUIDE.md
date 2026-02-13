# 响应式设计指南

## 概述

纳西族纹样数字化展示平台采用移动优先的响应式设计方法，确保在所有设备上提供最佳用户体验。

## 响应式断点

平台定义了以下响应式断点：

| 断点名称 | 屏幕宽度 | 设备类型 | 说明 |
|---------|---------|---------|------|
| 小手机 | < 480px | 小屏幕手机 | iPhone SE、小型安卓手机 |
| 手机 | 480px - 767px | 标准手机 | iPhone、标准安卓手机 |
| 平板 | 768px - 991px | 平板设备 | iPad、平板电脑 |
| 桌面 | 992px - 1199px | 小屏幕桌面 | 笔记本电脑、小屏幕显示器 |
| 大屏幕 | ≥ 1200px | 大屏幕桌面 | 台式电脑、大屏幕显示器 |

## 全局响应式工具类

### 显示/隐藏工具类

```css
.hide-mobile      /* 在手机上隐藏，在平板及以上显示 */
.show-mobile      /* 在手机上显示，在平板及以上隐藏 */
.hide-tablet      /* 在平板上隐藏，在桌面及以上显示 */
.show-tablet      /* 在平板上显示，在桌面及以上隐藏 */
.hide-desktop     /* 在桌面上隐藏，在平板及以下显示 */
.show-desktop     /* 在桌面上显示，在平板及以下隐藏 */
```

### 响应式字体大小

```css
.text-responsive-lg   /* 大标题：2rem (桌面) → 1.25rem (手机) */
.text-responsive-md   /* 中标题：1.5rem (桌面) → 1rem (手机) */
.text-responsive-sm   /* 小标题：1rem (桌面) → 0.8rem (手机) */
```

### 响应式间距

```css
.gap-responsive       /* 间距：1.5rem (桌面) → 0.75rem (手机) */
.p-responsive-lg      /* 大内边距：2rem (桌面) → 1rem (手机) */
.p-responsive-md      /* 中内边距：1.5rem (桌面) → 0.75rem (手机) */
.p-responsive-sm      /* 小内边距：1rem (桌面) → 0.5rem (手机) */
.px-responsive        /* 水平内边距：1rem (桌面) → 0.5rem (手机) */
```

### 响应式栅格

```css
.col-responsive-2     /* 2列栅格，手机时变为1列 */
.col-responsive-3     /* 3列栅格，平板时变为2列，手机时变为1列 */
.col-responsive-4     /* 4列栅格，大屏时为4列，平板时为2列，手机时为1列 */
```

### 响应式Flex

```css
.flex-responsive-row  /* 桌面时横向排列，手机时纵向排列 */
```

## 组件响应式设计

### Header（顶部导航）

**桌面版本 (≥ 992px)**
- 水平导航栏，所有菜单项并排显示
- Logo 和导航项之间有充足的间距
- 用户菜单和认证链接在右侧

**平板版本 (768px - 991px)**
- 导航项字体缩小
- 间距减少
- 菜单项可能换行

**手机版本 (< 768px)**
- 汉堡菜单（三条线）替代水平导航
- 点击汉堡菜单展开/收起导航
- 导航项纵向排列
- 全屏宽度的下拉菜单

### Footer（底部）

**桌面版本 (≥ 992px)**
- 3列布局（关于、链接、联系）
- 充足的内边距和间距

**平板版本 (768px - 991px)**
- 3列布局，间距减少

**手机版本 (< 768px)**
- 1列布局，所有内容纵向排列
- 字体和间距缩小

### Home（首页）

**轮播图**
- 桌面：400px 高度
- 平板：300px 高度
- 手机：220px 高度
- 小手机：180px 高度

**快速导航卡片**
- 桌面：4列网格
- 平板：3列网格
- 手机：1列网格

**文化标语**
- 桌面：3列网格
- 平板：2列网格
- 手机：1列网格

### PatternLibrary（纹样资源库）

**分类导航**
- 桌面：水平排列，充足间距
- 手机：水平排列，间距减少，可能换行

**搜索和筛选**
- 桌面：搜索框和筛选在同一行
- 手机：搜索框和筛选分别占满宽度

**纹样网格**
- 桌面：4列网格（250px 最小宽度）
- 平板：3列网格（200px 最小宽度）
- 手机：1列网格

### PatternDetail（纹样详情）

**布局**
- 桌面：2列布局（主内容 + 侧边栏）
- 平板：2列布局，侧边栏宽度减少
- 手机：1列布局，侧边栏在下方

**图片**
- 桌面：最大 500px 高度
- 平板：最大 400px 高度
- 手机：最大 300px 高度

## 响应式设计最佳实践

### 1. 移动优先方法

从最小屏幕开始设计，然后逐步增加功能和复杂性。

```css
/* 基础样式（手机） */
.element {
  font-size: 0.9rem;
  padding: 0.75rem;
}

/* 平板及以上 */
@media (min-width: 768px) {
  .element {
    font-size: 1rem;
    padding: 1rem;
  }
}

/* 桌面及以上 */
@media (min-width: 1200px) {
  .element {
    font-size: 1.1rem;
    padding: 1.5rem;
  }
}
```

### 2. 灵活的布局

使用 Flexbox 和 Grid 创建灵活的布局。

```css
/* 响应式网格 */
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

/* 响应式Flex */
.flex-container {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

@media (max-width: 767px) {
  .flex-container {
    flex-direction: column;
  }
}
```

### 3. 触摸友好的界面

在手机上确保按钮和链接足够大，便于点击。

```css
/* 最小触摸目标大小：44x44px */
button, a {
  min-height: 44px;
  min-width: 44px;
  padding: 0.75rem 1rem;
}
```

### 4. 图片优化

使用响应式图片和适当的尺寸。

```html
<img 
  src="image.jpg" 
  alt="描述"
  style="max-width: 100%; height: auto;"
/>
```

### 5. 字体大小

避免过小的字体，确保可读性。

```css
/* 最小字体大小：14px */
body {
  font-size: 16px;  /* 手机 */
}

@media (min-width: 768px) {
  body {
    font-size: 16px;
  }
}

@media (min-width: 1200px) {
  body {
    font-size: 16px;
  }
}
```

### 6. 视口设置

确保在 HTML 中设置正确的视口元标签。

```html
<meta name="viewport" content="width=device-width, initial-scale=1.0">
```

## 测试响应式设计

### 浏览器开发者工具

1. 打开浏览器开发者工具（F12）
2. 点击设备工具栏按钮（Ctrl+Shift+M）
3. 选择不同的设备预设或自定义尺寸

### 测试设备尺寸

- **小手机**：320px, 375px, 414px
- **标准手机**：480px, 540px, 600px
- **平板**：768px, 800px, 1024px
- **桌面**：1200px, 1366px, 1920px

### 测试场景

1. **横屏/竖屏切换**：确保在两种方向都能正常显示
2. **缩放**：测试用户缩放页面时的表现
3. **触摸交互**：在实际手机上测试触摸交互
4. **网络速度**：在慢速网络下测试加载性能
5. **浏览器兼容性**：在不同浏览器上测试

## 常见响应式问题和解决方案

### 问题1：文本过小，难以阅读

**解决方案**：
- 增加最小字体大小
- 使用相对单位（rem、em）而不是绝对单位（px）
- 在手机上增加行高

### 问题2：图片溢出容器

**解决方案**：
```css
img {
  max-width: 100%;
  height: auto;
}
```

### 问题3：按钮太小，难以点击

**解决方案**：
- 确保最小尺寸为 44x44px
- 增加内边距
- 在手机上增加按钮尺寸

### 问题4：导航菜单在手机上不可用

**解决方案**：
- 使用汉堡菜单
- 实现移动菜单的展开/收起功能
- 确保菜单项足够大

### 问题5：表单在手机上难以填写

**解决方案**：
- 增加输入框尺寸
- 使用适当的输入类型（email、tel、number）
- 在手机上使用全宽输入框

## 性能优化

### 1. 图片优化

- 使用适当的图片格式（WebP、JPEG、PNG）
- 为不同屏幕尺寸提供不同的图片
- 使用 srcset 属性

### 2. CSS 优化

- 使用 CSS 媒体查询而不是 JavaScript
- 避免过度使用媒体查询
- 合并相似的媒体查询

### 3. JavaScript 优化

- 避免在移动设备上加载不必要的脚本
- 使用事件委托减少事件监听器数量
- 延迟加载非关键脚本

## 参考资源

- [MDN - 响应式设计](https://developer.mozilla.org/zh-CN/docs/Learn/CSS/CSS_layout/Responsive_Design)
- [Google - 移动友好测试](https://search.google.com/test/mobile-friendly)
- [W3C - 媒体查询](https://www.w3.org/TR/mediaqueries-5/)
