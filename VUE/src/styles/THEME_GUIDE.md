# Blue Theme Configuration Guide

## Overview

The application uses a comprehensive blue color scheme with a complete design system. The theme is automatically applied when the application starts.

## Color Palette

### Primary Colors
- **Primary**: `#1890ff` - Main blue color used for primary actions and highlights
- **Primary Light**: `#40a9ff` - Lighter shade for hover states
- **Primary Lighter**: `#69c0ff` - Even lighter shade for backgrounds
- **Primary Dark**: `#0050b3` - Darker shade for active/pressed states
- **Primary Darker**: `#003a8c` - Darkest shade for emphasis

### Semantic Colors
- **Success**: `#52c41a` - Used for positive actions and success messages
- **Warning**: `#faad14` - Used for warnings and caution messages
- **Error**: `#f5222d` - Used for errors and destructive actions
- **Info**: `#1890ff` - Used for informational messages

### Text Colors
- **Text Primary**: `#262626` - Main text color
- **Text Secondary**: `#595959` - Secondary text color
- **Text Tertiary**: `#8c8c8c` - Tertiary text color
- **Text Disabled**: `#bfbfbf` - Disabled text color

### Background Colors
- **Background Primary**: `#fafafa` - Main background color
- **Background Secondary**: `#ffffff` - Secondary background (cards, modals)
- **Background Tertiary**: `#f5f5f5` - Tertiary background (hover states)

### Border Colors
- **Border Default**: `#d9d9d9` - Standard border color
- **Border Light**: `#f0f0f0` - Light border color
- **Border Dark**: `#bfbfbf` - Dark border color

## CSS Variables

All theme values are available as CSS variables:

```css
/* Colors */
--primary-color: #1890ff;
--primary-light: #40a9ff;
--primary-lighter: #69c0ff;
--primary-dark: #0050b3;
--primary-darker: #003a8c;
--success-color: #52c41a;
--warning-color: #faad14;
--error-color: #f5222d;
--info-color: #1890ff;
--text-primary: #262626;
--text-secondary: #595959;
--text-tertiary: #8c8c8c;
--text-disabled: #bfbfbf;
--bg-primary: #fafafa;
--bg-secondary: #ffffff;
--bg-tertiary: #f5f5f5;
--border-default: #d9d9d9;
--border-light: #f0f0f0;
--border-dark: #bfbfbf;

/* Spacing */
--spacing-xs: 4px;
--spacing-sm: 8px;
--spacing-md: 12px;
--spacing-lg: 16px;
--spacing-xl: 24px;
--spacing-xxl: 32px;

/* Border Radius */
--radius-sm: 2px;
--radius-md: 4px;
--radius-lg: 8px;
--radius-xl: 12px;

/* Font Sizes */
--font-size-xs: 12px;
--font-size-sm: 13px;
--font-size-md: 14px;
--font-size-lg: 16px;
--font-size-xl: 18px;
--font-size-xxl: 20px;

/* Font Weights */
--font-weight-light: 300;
--font-weight-normal: 400;
--font-weight-medium: 500;
--font-weight-semibold: 600;
--font-weight-bold: 700;

/* Shadows */
--shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.03), ...;
--shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), ...;
--shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), ...;
--shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1), ...;

/* Transitions */
--transition-fast: 0.15s ease-in-out;
--transition-normal: 0.3s ease-in-out;
--transition-slow: 0.5s ease-in-out;
```

## Usage Examples

### Using CSS Variables in Styles

```vue
<style scoped>
.my-component {
  color: var(--text-primary);
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
}

.my-component:hover {
  background-color: var(--bg-tertiary);
  box-shadow: var(--shadow-lg);
}
</style>
```

### Using Utility Classes

```vue
<template>
  <div class="card p-lg rounded-lg shadow-md">
    <h2 class="text-lg font-semibold text-primary m-0 mb-md">Title</h2>
    <p class="text-secondary m-0">Description</p>
    <div class="flex gap-md mt-lg">
      <button class="btn-primary">Primary</button>
      <button class="btn-secondary">Secondary</button>
    </div>
  </div>
</template>
```

### Accessing Theme in JavaScript

```javascript
import { themeConfig } from '@/styles/theme'

// Access color values
const primaryColor = themeConfig.colors.primary
const spacing = themeConfig.spacing.lg
const fontSize = themeConfig.fontSize.md

// Use in dynamic styles
const style = {
  color: themeConfig.colors.primary,
  padding: themeConfig.spacing.lg,
  borderRadius: themeConfig.borderRadius.md
}
```

## Utility Classes

### Text Colors
- `.text-primary` - Primary text color
- `.text-secondary` - Secondary text color
- `.text-tertiary` - Tertiary text color
- `.text-disabled` - Disabled text color
- `.text-error` - Error text color
- `.text-success` - Success text color
- `.text-warning` - Warning text color
- `.text-info` - Info text color

### Background Colors
- `.bg-primary` - Primary background
- `.bg-secondary` - Secondary background
- `.bg-tertiary` - Tertiary background

### Borders
- `.border-default` - Default border
- `.border-light` - Light border
- `.border-dark` - Dark border

### Border Radius
- `.rounded-sm` - Small radius (2px)
- `.rounded-md` - Medium radius (4px)
- `.rounded-lg` - Large radius (8px)
- `.rounded-xl` - Extra large radius (12px)

### Shadows
- `.shadow-sm` - Small shadow
- `.shadow-md` - Medium shadow
- `.shadow-lg` - Large shadow
- `.shadow-xl` - Extra large shadow

### Spacing (Margin)
- `.m-0`, `.m-xs`, `.m-sm`, `.m-md`, `.m-lg`, `.m-xl`, `.m-xxl`

### Spacing (Padding)
- `.p-0`, `.p-xs`, `.p-sm`, `.p-md`, `.p-lg`, `.p-xl`, `.p-xxl`

### Gap (Flexbox)
- `.gap-xs`, `.gap-sm`, `.gap-md`, `.gap-lg`, `.gap-xl`, `.gap-xxl`

### Flexbox
- `.flex` - Display flex
- `.flex-center` - Center content
- `.flex-between` - Space between
- `.flex-col` - Column direction
- `.flex-wrap` - Wrap items

### Grid
- `.grid` - Display grid
- `.grid-cols-1`, `.grid-cols-2`, `.grid-cols-3`, `.grid-cols-4`

### Text Alignment
- `.text-center` - Center text
- `.text-left` - Left align text
- `.text-right` - Right align text

### Font Weight
- `.font-light` - Light weight (300)
- `.font-normal` - Normal weight (400)
- `.font-medium` - Medium weight (500)
- `.font-semibold` - Semibold weight (600)
- `.font-bold` - Bold weight (700)

### Font Size
- `.text-xs`, `.text-sm`, `.text-md`, `.text-lg`, `.text-xl`, `.text-xxl`

### Visibility
- `.hidden` - Display none
- `.visible` - Display block
- `.opacity-50`, `.opacity-75`, `.opacity-100`

## Button Styles

### Primary Button
```vue
<button class="btn-primary">Primary Action</button>
```

### Secondary Button
```vue
<button class="btn-secondary">Secondary Action</button>
```

### Success Button
```vue
<button class="btn-success">Success Action</button>
```

### Danger Button
```vue
<button class="btn-danger">Danger Action</button>
```

## Form Styling

### Form Group
```vue
<div class="form-group">
  <label class="form-label required">Email</label>
  <input type="email" placeholder="Enter email" />
  <div class="form-hint">We'll never share your email</div>
</div>
```

### Form Error
```vue
<div class="form-group">
  <label class="form-label">Username</label>
  <input type="text" />
  <div class="form-error">Username is required</div>
</div>
```

## Card Styling

```vue
<div class="card">
  <div class="card-header">
    <h3 class="card-title">Card Title</h3>
  </div>
  <div class="card-body">
    <p>Card content goes here</p>
  </div>
  <div class="card-footer">
    <button class="btn-secondary">Cancel</button>
    <button class="btn-primary">Save</button>
  </div>
</div>
```

## Alert Styling

```vue
<div class="alert alert-info">Information message</div>
<div class="alert alert-success">Success message</div>
<div class="alert alert-warning">Warning message</div>
<div class="alert alert-error">Error message</div>
```

## Badge Styling

```vue
<span class="badge badge-primary">Primary</span>
<span class="badge badge-success">Success</span>
<span class="badge badge-warning">Warning</span>
<span class="badge badge-error">Error</span>
```

## Animations

### Loading Animation
```vue
<div class="loading">⟳</div>
```

### Pulse Animation
```vue
<div class="pulse">Content</div>
```

### Fade In Animation
```vue
<div class="fade-in">Content</div>
```

### Slide In Animation
```vue
<div class="slide-in">Content</div>
```

## Element Plus Integration

The theme automatically configures Element Plus components to use the blue color scheme. All Element Plus components will inherit the theme colors:

- Buttons use primary blue color
- Inputs focus with primary blue border
- Menus highlight with primary blue
- Tables use theme colors for headers and rows
- Dialogs use theme background colors
- Messages use semantic colors

## Customization

To customize the theme, edit the `theme.js` file:

```javascript
export const themeConfig = {
  colors: {
    primary: '#1890ff', // Change primary color
    // ... other colors
  },
  spacing: {
    // ... spacing values
  },
  // ... other theme properties
}
```

Then call `applyTheme()` to apply the changes:

```javascript
import { applyTheme } from '@/styles/theme'
applyTheme()
```

## Best Practices

1. **Use CSS Variables**: Always use CSS variables instead of hardcoding colors
2. **Use Utility Classes**: Leverage utility classes for common styling patterns
3. **Consistent Spacing**: Use the spacing scale for consistent margins and padding
4. **Semantic Colors**: Use semantic colors (success, warning, error) for their intended purposes
5. **Accessibility**: Ensure sufficient color contrast for text readability
6. **Responsive Design**: Use flexbox and grid utilities for responsive layouts
7. **Transitions**: Use the transition variables for consistent animation timing
