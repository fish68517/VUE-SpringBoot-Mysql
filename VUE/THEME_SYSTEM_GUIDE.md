# Theme System Guide

## Overview

The Naxi Pattern Platform features a comprehensive theme system that supports light mode, dark mode, and automatic theme switching based on system preferences. The theme system is built with CSS custom properties (variables) for easy customization and maintenance.

## Features

- **Light Mode**: Clean, bright interface optimized for daytime use
- **Dark Mode**: Eye-friendly dark interface for low-light environments
- **Auto Mode**: Automatically switches based on system preferences
- **Persistent Storage**: Theme preference is saved to localStorage
- **Real-time Switching**: Smooth transitions between themes
- **Element Plus Integration**: Full customization of Element Plus components
- **CSS Variables**: Comprehensive set of CSS variables for consistent styling

## File Structure

```
src/
├── styles/
│   ├── theme.css                 # CSS variables and theme definitions
│   ├── element-plus-theme.css    # Element Plus component customization
│   └── global.css                # Global styles using CSS variables
├── utils/
│   └── theme.js                  # Theme management utilities
├── composables/
│   └── useTheme.js               # Vue composable for theme management
└── components/
    └── ThemeSwitcher.vue         # Theme switcher UI component
```

## CSS Variables

### Color Variables

#### Primary Colors (Naxi Cultural Theme)
- `--color-primary`: #d4a574 (Main brand color)
- `--color-primary-light`: #e8c9a8
- `--color-primary-lighter`: #f5e6d3
- `--color-primary-dark`: #a67c52
- `--color-primary-darker`: #7a5a3a

#### Secondary Colors
- `--color-secondary`: #8b6f47
- `--color-secondary-light`: #a88a5f
- `--color-secondary-dark`: #6b5535

#### Accent Colors
- `--color-accent`: #c41e3a
- `--color-accent-light`: #e85d75
- `--color-accent-dark`: #8b1528

#### Status Colors
- `--color-success`: #67c23a
- `--color-warning`: #e6a23c
- `--color-error`: #f56c6c
- `--color-info`: #409eff

#### Neutral Colors
- `--color-white`: #ffffff
- `--color-black`: #000000
- `--color-gray-50` to `--color-gray-900`: Full grayscale

#### Background Colors
- `--color-bg-primary`: Main background
- `--color-bg-secondary`: Secondary background
- `--color-bg-tertiary`: Tertiary background

#### Text Colors
- `--color-text-primary`: Primary text
- `--color-text-secondary`: Secondary text
- `--color-text-tertiary`: Tertiary text
- `--color-text-disabled`: Disabled text
- `--color-text-inverse`: Inverse text

#### Border Colors
- `--color-border`: Standard border
- `--color-border-light`: Light border
- `--color-border-dark`: Dark border

### Typography Variables

#### Font Families
- `--font-family-base`: System font stack
- `--font-family-mono`: Monospace font
- `--font-family-serif`: Serif font

#### Font Sizes
- `--font-size-xs`: 0.75rem
- `--font-size-sm`: 0.875rem
- `--font-size-base`: 1rem
- `--font-size-lg`: 1.125rem
- `--font-size-xl`: 1.25rem
- `--font-size-2xl`: 1.5rem
- `--font-size-3xl`: 1.875rem
- `--font-size-4xl`: 2.25rem

#### Font Weights
- `--font-weight-light`: 300
- `--font-weight-normal`: 400
- `--font-weight-medium`: 500
- `--font-weight-semibold`: 600
- `--font-weight-bold`: 700

#### Line Heights
- `--line-height-tight`: 1.25
- `--line-height-normal`: 1.5
- `--line-height-relaxed`: 1.75
- `--line-height-loose`: 2

### Spacing Variables

- `--spacing-0` to `--spacing-20`: Consistent spacing scale
- Example: `--spacing-4`: 1rem

### Border Radius Variables

- `--radius-none`: 0
- `--radius-sm`: 0.25rem
- `--radius-md`: 0.375rem
- `--radius-lg`: 0.5rem
- `--radius-xl`: 0.75rem
- `--radius-2xl`: 1rem
- `--radius-full`: 9999px

### Shadow Variables

- `--shadow-sm`: Small shadow
- `--shadow-md`: Medium shadow
- `--shadow-lg`: Large shadow
- `--shadow-xl`: Extra large shadow

### Transition Variables

- `--transition-fast`: 150ms ease-in-out
- `--transition-base`: 250ms ease-in-out
- `--transition-slow`: 350ms ease-in-out

## Usage

### Using CSS Variables in Styles

```css
/* In your component styles */
.my-component {
  background-color: var(--color-bg-primary);
  color: var(--color-text-primary);
  padding: var(--spacing-4);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-base);
}
```

### Using Theme Utilities in JavaScript

```javascript
import {
  getCurrentTheme,
  setTheme,
  toggleTheme,
  isDarkTheme,
  getCSSVariable,
  setCSSVariable,
  THEMES
} from '@/utils/theme'

// Get current theme
const theme = getCurrentTheme() // 'light' or 'dark'

// Set theme
setTheme(THEMES.DARK)
setTheme(THEMES.LIGHT)
setTheme(THEMES.AUTO)

// Toggle between light and dark
toggleTheme()

// Check if dark theme
if (isDarkTheme()) {
  console.log('Dark mode is active')
}

// Get CSS variable value
const primaryColor = getCSSVariable('color-primary')

// Set CSS variable value
setCSSVariable('color-primary', '#ff0000')
```

### Using Theme Composable in Vue Components

```vue
<template>
  <div>
    <p>Current theme: {{ currentTheme }}</p>
    <p>Theme preference: {{ themePreference }}</p>
    <p v-if="isDark">Dark mode is active</p>
    <p v-if="isLight">Light mode is active</p>
    
    <button @click="changeTheme(THEMES.LIGHT)">Light</button>
    <button @click="changeTheme(THEMES.DARK)">Dark</button>
    <button @click="changeTheme(THEMES.AUTO)">Auto</button>
    <button @click="toggle">Toggle</button>
  </div>
</template>

<script setup>
import { useTheme } from '@/composables/useTheme'

const {
  currentTheme,
  themePreference,
  isDark,
  isLight,
  isAuto,
  changeTheme,
  toggle,
  THEMES
} = useTheme()
</script>
```

### Using Theme Switcher Component

```vue
<template>
  <header>
    <h1>My App</h1>
    <ThemeSwitcher />
  </header>
</template>

<script setup>
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'
</script>
```

## Dark Mode Implementation

The theme system automatically detects system dark mode preference using the `prefers-color-scheme` media query. When a user selects "Auto" mode, the theme will:

1. Check the system preference on app load
2. Listen for system preference changes
3. Update the theme accordingly

### System Preference Detection

```javascript
// Check if system prefers dark mode
const isDarkPreferred = window.matchMedia('(prefers-color-scheme: dark)').matches

// Listen for system preference changes
const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
mediaQuery.addEventListener('change', (e) => {
  if (e.matches) {
    console.log('System switched to dark mode')
  } else {
    console.log('System switched to light mode')
  }
})
```

## Customizing the Theme

### Modifying Color Scheme

Edit `src/styles/theme.css` to change the color variables:

```css
:root {
  --color-primary: #your-color;
  --color-primary-light: #your-light-color;
  /* ... other colors ... */
}
```

### Adding New CSS Variables

Add new variables to `src/styles/theme.css`:

```css
:root {
  --color-custom: #value;
  --spacing-custom: 1.5rem;
  /* ... */
}
```

Then use them in your styles:

```css
.my-element {
  color: var(--color-custom);
  padding: var(--spacing-custom);
}
```

### Customizing Element Plus Components

Edit `src/styles/element-plus-theme.css` to customize Element Plus components:

```css
.el-button--primary {
  background-color: var(--color-primary);
  border-color: var(--color-primary);
}

.el-button--primary:hover {
  background-color: var(--color-primary-light);
  border-color: var(--color-primary-light);
}
```

## Theme Persistence

The theme preference is automatically saved to localStorage under the key `naxi-platform-theme`. This ensures the user's theme choice persists across sessions.

### Storage Format

```javascript
// Stored value
localStorage.getItem('naxi-platform-theme') // 'light', 'dark', or 'auto'
```

### Clearing Theme Preference

```javascript
localStorage.removeItem('naxi-platform-theme')
```

## Theme Change Events

The theme system dispatches custom events when the theme changes:

```javascript
window.addEventListener('themechange', (event) => {
  console.log('Theme changed to:', event.detail.theme) // 'light' or 'dark'
  console.log('Theme preference:', event.detail.preference) // 'light', 'dark', or 'auto'
})
```

## Browser Support

The theme system uses modern CSS features and requires:

- CSS Custom Properties (CSS Variables)
- `prefers-color-scheme` media query
- localStorage API

Supported browsers:
- Chrome 49+
- Firefox 31+
- Safari 9.1+
- Edge 15+
- Opera 36+

## Best Practices

1. **Use CSS Variables**: Always use CSS variables instead of hardcoded colors
2. **Consistent Spacing**: Use the spacing scale for consistent layouts
3. **Semantic Colors**: Use semantic color names (primary, success, error) instead of color names
4. **Transitions**: Use the transition variables for consistent animations
5. **Accessibility**: Ensure sufficient color contrast in both light and dark modes
6. **Testing**: Test your components in both light and dark modes

## Troubleshooting

### Theme Not Persisting

Check if localStorage is enabled and not full:

```javascript
try {
  localStorage.setItem('test', 'test')
  localStorage.removeItem('test')
} catch (e) {
  console.error('localStorage is not available')
}
```

### CSS Variables Not Working

Ensure the theme CSS files are imported in the correct order in `main.js`:

```javascript
import './styles/theme.css'
import './styles/element-plus-theme.css'
import './styles/global.css'
```

### Dark Mode Not Detecting System Preference

Check if the browser supports `prefers-color-scheme`:

```javascript
const supported = window.matchMedia('(prefers-color-scheme: dark)').media !== 'not all'
console.log('prefers-color-scheme supported:', supported)
```

## Future Enhancements

- Custom theme builder UI
- Multiple theme presets
- Per-component theme overrides
- Theme animation preferences
- Accessibility contrast modes
