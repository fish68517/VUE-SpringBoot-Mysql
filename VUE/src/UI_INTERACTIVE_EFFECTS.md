# UI Interactive Effects Implementation Guide

## Overview

This document describes the UI interactive effects implemented in the campus activity crowdfunding platform. These effects enhance user experience through visual feedback, smooth animations, and clear error handling.

## 1. Button Hover and Click Effects

### Features
- **Hover Effect**: Buttons lift up with shadow and opacity change
- **Click Effect**: Ripple animation on button click
- **Active State**: Scale down effect on click
- **Disabled State**: Reduced opacity and no interaction

### CSS Classes
```css
.btn-primary    /* Primary blue button */
.btn-secondary  /* Secondary white button */
.btn-success    /* Green success button */
.btn-danger     /* Red danger button */
```

### Usage
```html
<button class="btn-primary">Click me</button>
<button class="btn-secondary">Secondary</button>
<button class="btn-success">Success</button>
<button class="btn-danger">Delete</button>
```

### Effects
- Hover: `translateY(-2px)` with shadow
- Click: Ripple animation with `scale(0.98)`
- Disabled: `opacity: 0.6` with no cursor

---

## 2. Form Validation Error Styling

### Features
- **Error State**: Red border and light red background
- **Success State**: Green border and light green background
- **Error Messages**: Animated slide-in with warning icon
- **Real-time Validation**: Clear errors on input

### CSS Classes
```css
.form-group.has-error    /* Error state */
.form-group.has-success  /* Success state */
.error-message           /* Error message styling */
.success-message         /* Success message styling */
```

### Usage
```vue
<div :class="['form-group', { 'has-error': errors.email }]">
  <label class="form-label required">Email</label>
  <input 
    v-model="form.email"
    @blur="validateEmail"
    @input="clearError('email')"
  />
  <span v-if="errors.email" class="error-message">
    {{ errors.email }}
  </span>
</div>
```

### Validation Rules
The `formValidation.js` utility provides common validation rules:
- `required`: Field must not be empty
- `email`: Valid email format
- `phone`: Valid phone number (Chinese format)
- `studentId`: Valid student ID
- `password`: Minimum 6 characters
- `passwordStrength`: Strong password requirements
- `minLength(n)`: Minimum length
- `maxLength(n)`: Maximum length
- `match(value)`: Match another field value

### Example
```javascript
import { validateField, validateForm } from '@/utils/formValidation'

const validationSchema = {
  email: [validationRules.required, validationRules.email],
  password: [validationRules.required, validationRules.passwordStrength]
}

const errors = validateForm(formData, validationSchema)
```

---

## 3. Loading Animations

### Features
- **Spinner**: Rotating circle animation
- **Dots**: Pulsing dots animation
- **Overlay**: Full-screen loading with backdrop
- **Text**: Optional loading message

### Components

#### LoadingSpinner Component
```vue
<LoadingSpinner ref="loadingRef" />
```

#### Global API
```javascript
// Show loading
window.$loading.show('Loading...')

// Hide loading
window.$loading.hide()
```

#### Utility Functions
```javascript
import { showLoading, hideLoading, withLoading } from '@/utils/loading'

// Show loading
showLoading('Processing...')

// Hide loading
hideLoading()

// Execute with loading
await withLoading(async () => {
  // Your async operation
}, 'Loading...')
```

### CSS Classes
```css
.loading-spinner      /* Spinner animation */
.loading-dots         /* Dots animation */
.loading-overlay      /* Full-screen overlay */
.loading-overlay-content  /* Overlay content */
```

### Animations
- `spin`: 360° rotation (0.8s)
- `pulse`: Opacity pulse (1.4s)
- `bounce`: Vertical bounce (1s)

---

## 4. Toast Notifications

### Features
- **Multiple Types**: Success, error, warning, info
- **Auto-dismiss**: Configurable duration (default 3s)
- **Progress Bar**: Visual countdown
- **Stacking**: Multiple toasts stack vertically
- **Smooth Animations**: Slide-in and fade-out

### Components
```vue
<Toast ref="toastRef" />
```

### Global API
```javascript
// Success notification
window.$toast.success('Operation successful')

// Error notification
window.$toast.error('An error occurred')

// Warning notification
window.$toast.warning('Please be careful')

// Info notification
window.$toast.info('Information message')

// Clear all
window.$toast.clear()
```

### Utility Functions
```javascript
import { showSuccess, showError, showWarning, showInfo } from '@/utils/errorHandler'

showSuccess('Saved successfully')
showError('Failed to save')
showWarning('This action cannot be undone')
showInfo('Please note this information')
```

### CSS Classes
```css
.toast                /* Toast container */
.toast-success        /* Success toast */
.toast-error          /* Error toast */
.toast-warning        /* Warning toast */
.toast-info           /* Info toast */
.toast-icon           /* Icon styling */
.toast-content        /* Content text */
.toast-close          /* Close button */
.toast-progress       /* Progress bar */
```

### Styling
- Success: Green (#52c41a)
- Error: Red (#f5222d)
- Warning: Orange (#faad14)
- Info: Blue (#1890ff)

---

## 5. Input Focus Effects

### Features
- **Focus State**: Blue border with light blue shadow
- **Error Focus**: Red border with light red shadow
- **Smooth Transition**: 0.3s ease transition
- **Placeholder**: Tertiary text color

### CSS
```css
input:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.form-group.has-error input:focus {
  border-color: var(--error-color);
  box-shadow: 0 0 0 2px rgba(245, 34, 45, 0.2);
}
```

---

## 6. Card Hover Effects

### Features
- **Hover Shadow**: Elevation effect on hover
- **Smooth Transition**: 0.3s ease transition
- **Border**: Light border for definition

### CSS Classes
```css
.card              /* Card styling */
.card:hover        /* Hover effect */
.card-header       /* Header section */
.card-body         /* Body section */
.card-footer       /* Footer section */
```

---

## 7. Animation Utilities

### Available Animations
```css
@keyframes spin       /* 360° rotation */
@keyframes pulse      /* Opacity pulse */
@keyframes fadeIn     /* Fade in */
@keyframes slideIn    /* Slide in from top */
@keyframes bounce     /* Vertical bounce */
@keyframes shimmer    /* Shimmer effect */
```

### CSS Classes
```css
.loading      /* Spin animation */
.pulse        /* Pulse animation */
.fade-in      /* Fade in animation */
.slide-in     /* Slide in animation */
.bounce       /* Bounce animation */
```

---

## 8. Implementation Examples

### Example 1: Form with Validation
```vue
<template>
  <form @submit.prevent="handleSubmit">
    <div :class="['form-group', { 'has-error': errors.email }]">
      <label class="form-label required">Email</label>
      <input 
        v-model="form.email"
        @blur="validateEmail"
        @input="clearError('email')"
      />
      <span v-if="errors.email" class="error-message">
        {{ errors.email }}
      </span>
    </div>
    <button type="submit" class="btn-primary" :disabled="isLoading">
      {{ isLoading ? 'Submitting...' : 'Submit' }}
    </button>
  </form>
</template>

<script setup>
import { ref } from 'vue'
import { showSuccess, showError } from '@/utils/errorHandler'
import { validationRules, validateField } from '@/utils/formValidation'

const form = ref({ email: '' })
const errors = ref({})
const isLoading = ref(false)

const validateEmail = () => {
  const error = validateField(form.value.email, [
    validationRules.required,
    validationRules.email
  ])
  errors.value.email = error === true ? '' : error
}

const clearError = (field) => {
  delete errors.value[field]
}

const handleSubmit = async () => {
  validateEmail()
  if (errors.value.email) return

  isLoading.value = true
  try {
    // Your API call
    showSuccess('Submitted successfully')
  } catch (error) {
    showError('Failed to submit')
  } finally {
    isLoading.value = false
  }
}
</script>
```

### Example 2: Loading with Async Operation
```javascript
import { withLoading } from '@/utils/loading'
import { showSuccess, showError } from '@/utils/errorHandler'

const handleFetch = async () => {
  try {
    const data = await withLoading(async () => {
      const response = await http.get('/api/data')
      return response.data
    }, 'Loading data...')
    
    showSuccess('Data loaded successfully')
  } catch (error) {
    showError('Failed to load data')
  }
}
```

### Example 3: Toast Notifications
```javascript
// In any component
import { showSuccess, showError, showWarning } from '@/utils/errorHandler'

// After successful operation
showSuccess('Changes saved')

// On error
showError('Failed to save changes')

// Warning
showWarning('This action cannot be undone')
```

---

## 9. Accessibility Considerations

- **Color Contrast**: All text meets WCAG AA standards
- **Focus Indicators**: Clear focus states for keyboard navigation
- **Error Messages**: Associated with form fields
- **Loading States**: Announced to screen readers
- **Animations**: Respect `prefers-reduced-motion` preference

---

## 10. Browser Compatibility

- Chrome/Edge: Full support
- Firefox: Full support
- Safari: Full support
- IE11: Partial support (no CSS variables)

---

## 11. Performance Tips

1. **Debounce Validation**: Use debounce for real-time validation
2. **Lazy Load**: Load components on demand
3. **CSS Animations**: Use GPU-accelerated properties (transform, opacity)
4. **Minimize Reflows**: Batch DOM updates

---

## 12. Customization

### Theme Colors
Edit `VUE/src/styles/main.css` CSS variables:
```css
:root {
  --primary-color: #1890ff;
  --success-color: #52c41a;
  --error-color: #f5222d;
  --warning-color: #faad14;
}
```

### Animation Duration
```css
:root {
  --transition-fast: 0.15s ease-in-out;
  --transition-normal: 0.3s ease-in-out;
  --transition-slow: 0.5s ease-in-out;
}
```

### Toast Duration
```javascript
window.$toast.success('Message', 5000) // 5 seconds
```

---

## Summary

The UI interactive effects implementation provides:
- ✅ Button hover and click feedback
- ✅ Form validation error styling
- ✅ Loading animations and overlays
- ✅ Toast notifications
- ✅ Smooth transitions and animations
- ✅ Accessibility support
- ✅ Customizable theme colors
- ✅ Reusable utility functions
