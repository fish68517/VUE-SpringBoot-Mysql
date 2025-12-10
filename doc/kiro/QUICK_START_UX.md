# Quick Start: UX Improvements

## 5-Minute Setup Guide

### 1. Import Notification Utilities
```javascript
import { showSuccess, showError } from '@/utils/notificationUtils'

// Use in your component
showSuccess('Operation successful!')
showError('Something went wrong')
```

### 2. Add Loading Spinner
```vue
<template>
  <LoadingSpinner v-if="isLoading" message="Loading..." />
</template>

<script setup>
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import { useLoading } from '@/composables/useLoading'

const { isLoading, withLoading } = useLoading()
</script>
```

### 3. Add Skeleton Loader
```vue
<template>
  <SkeletonLoader v-if="loading" :count="5" type="card" />
  <div v-else>Your content here</div>
</template>

<script setup>
import SkeletonLoader from '@/components/SkeletonLoader.vue'
</script>
```

### 4. Add Form Validation
```vue
<template>
  <el-form :model="formData" :rules="rules">
    <el-form-item label="Email" prop="email">
      <el-input v-model="formData.email" />
    </el-form-item>
  </el-form>
</template>

<script setup>
import { useFormValidation } from '@/composables/useFormValidation'

const { formData, validateForm } = useFormValidation(
  { email: '' },
  { email: { required: true, type: 'email' } }
)
</script>
```

### 5. Add Lazy Loading Images
```vue
<template>
  <img v-lazy="imageUrl" alt="Travel photo" />
</template>
```

### 6. Add Virtual Scrolling
```vue
<template>
  <VirtualList :items="records" :itemHeight="100" :containerHeight="600">
    <template #default="{ item }">
      <div class="record">{{ item.title }}</div>
    </template>
  </VirtualList>
</template>

<script setup>
import VirtualList from '@/components/VirtualList.vue'
</script>
```

## Common Patterns

### Pattern 1: Async Operation with Loading
```javascript
const { isLoading, withLoading } = useLoading()

const fetchData = async () => {
  await withLoading(async () => {
    const data = await api.get('/data')
    // Process data
  }, 'Loading data...')
}
```

### Pattern 2: Form Submission with Validation
```javascript
const { formData, validateForm } = useFormValidation(initialData, rules)

const handleSubmit = async () => {
  if (!validateForm()) return
  
  try {
    await api.post('/submit', formData)
    showSuccess('Submitted successfully!')
  } catch (error) {
    showError(error.message)
  }
}
```

### Pattern 3: List with Pagination
```javascript
const { currentPage, pageSize, total, setTotal } = usePagination(10)

const fetchRecords = async () => {
  const response = await api.get('/records', {
    params: { page: currentPage.value - 1, size: pageSize.value }
  })
  setTotal(response.data.totalElements)
}
```

### Pattern 4: Async Data Fetching
```javascript
const { data, isLoading, error, execute } = useAsync(
  () => api.get('/data'),
  { immediate: true, showErrorNotification: true }
)
```

## File Locations

### Utilities
- Notifications: `src/utils/notificationUtils.js`
- Validation: `src/utils/validationUtils.js`

### Components
- LoadingSpinner: `src/components/LoadingSpinner.vue`
- SkeletonLoader: `src/components/SkeletonLoader.vue`
- VirtualList: `src/components/VirtualList.vue`

### Composables
- Form Validation: `src/composables/useFormValidation.js`
- Loading: `src/composables/useLoading.js`
- Async: `src/composables/useAsync.js`
- Pagination: `src/composables/usePagination.js`

### Styles
- UX Improvements: `src/styles/ux-improvements.css`

## Tips & Tricks

1. **Always show loading state** when fetching data
2. **Use skeleton screens** for better perceived performance
3. **Validate on blur** for better form UX
4. **Use lazy loading** for images below the fold
5. **Use virtual scrolling** for lists with 100+ items
6. **Show success notifications** for important operations
7. **Show error notifications** for failed operations

## Troubleshooting

### Images not loading?
- Check image URL is correct
- Verify CORS headers
- Check browser console for errors

### Form validation not working?
- Ensure rules are defined
- Check field names match form data
- Verify blur event is triggered

### Virtual scrolling not smooth?
- Ensure itemHeight matches actual height
- Check containerHeight is set
- Verify items array is not empty

## Full Documentation

See `UX_IMPROVEMENTS_GUIDE.md` for comprehensive documentation.
