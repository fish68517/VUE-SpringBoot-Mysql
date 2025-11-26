# Frontend Validation Quick Reference

## Quick Start

### 1. Import the Composable
```javascript
import { useFormValidation } from '@/composables/useFormValidation'
```

### 2. Set Up Form and Rules
```javascript
const form = reactive({
  email: '',
  password: ''
})

const { validateField, getFieldError, hasFieldError, isFormValid } = useFormValidation(form, {
  email: { required: true, type: 'email' },
  password: { required: true, type: 'password', minLength: 8 }
})
```

### 3. Add to Template
```vue
<el-input
  v-model="form.email"
  @blur="validateField('email')"
/>
<div v-if="hasFieldError('email')" class="field-error">
  {{ getFieldError('email') }}
</div>
```

## Common Validation Rules

```javascript
// Required field
{ required: true }

// Email
{ required: true, type: 'email' }

// Password
{ required: true, type: 'password' }

// Username
{ required: true, type: 'username' }

// Text length
{ minLength: 3, maxLength: 50 }

// Number range
{ type: 'budget', min: 0, max: 999999 }

// URL
{ type: 'url' }

// Phone
{ type: 'phone' }

// Custom validator
{
  validator: (value) => {
    if (value !== form.password) {
      return { isValid: false, message: 'Passwords do not match' }
    }
    return { isValid: true, message: '' }
  }
}
```

## Composable Methods

| Method | Purpose | Returns |
|--------|---------|---------|
| `validateField(name)` | Validate single field | Promise<boolean> |
| `validateForm()` | Validate all fields | Promise<boolean> |
| `touchField(name)` | Mark field as touched | void |
| `touchAllFields()` | Mark all fields as touched | void |
| `resetForm()` | Reset to initial state | void |
| `setFormValues(values)` | Update form values | void |
| `getFieldError(name)` | Get error message | string |
| `hasFieldError(name)` | Check if has error | boolean |
| `isFieldValidating(name)` | Check if validating | boolean |

## Composable Properties

| Property | Type | Purpose |
|----------|------|---------|
| `formData` | object | Form values |
| `errors` | object | Error messages |
| `touched` | object | Touched state |
| `validating` | object | Validating state |
| `isFormValid` | computed | All fields valid |
| `isFormTouched` | computed | Any field touched |

## Validation Utilities

```javascript
import {
  isValidEmail,
  validatePassword,
  validateUsername,
  validateRequired,
  validateDateRange,
  validateNumberRange,
  validateTextLength,
  validateFileSize,
  validateFileType,
  validateUrl,
  validateBudget,
  validateFutureDate,
  validatePastDate,
  validateMatch,
  validateUnique,
  validatePhoneNumber,
  validatePostalCode
} from '@/utils/validationUtils'
```

## Error Display Pattern

```vue
<div class="form-field-wrapper">
  <el-input v-model="form.email" @blur="validateField('email')" />
  <div v-if="hasFieldError('email')" class="field-error">
    <el-icon><CircleCloseFilled /></el-icon>
    <span>{{ getFieldError('email') }}</span>
  </div>
</div>
```

## Form Submission

```javascript
const handleSubmit = async () => {
  if (await validateForm()) {
    // Submit form
    await submitForm()
  }
}
```

## Disable Submit Button

```vue
<el-button @click="handleSubmit" :disabled="!isFormValid">
  Submit
</el-button>
```

## Password Strength Indicator

```javascript
import { validatePassword } from '@/utils/validationUtils'

const passwordStrength = computed(() => {
  if (!form.password) return ''
  const validation = validatePassword(form.password)
  return validation.strength // 'weak', 'medium', 'strong'
})
```

## Async Validation Example

```javascript
const rules = {
  email: {
    required: true,
    validator: async (value) => {
      const response = await checkEmailUniqueness(value)
      if (!response.isUnique) {
        return { isValid: false, message: 'Email already registered' }
      }
      return { isValid: true, message: '' }
    }
  }
}
```

## Form Reset

```javascript
const handleReset = () => {
  resetForm()
}
```

## Touch All Fields (Show All Errors)

```javascript
const handleSubmitAttempt = async () => {
  touchAllFields()
  if (await validateForm()) {
    // Submit
  }
}
```

## Common Patterns

### Pattern 1: Login Form
```javascript
const { validateField, getFieldError, hasFieldError, isFormValid } = useFormValidation(
  { email: '', password: '' },
  {
    email: { required: true, type: 'email' },
    password: { required: true, minLength: 8 }
  }
)
```

### Pattern 2: Registration Form
```javascript
const { validateField, getFieldError, hasFieldError, isFormValid } = useFormValidation(
  { username: '', email: '', password: '', confirmPassword: '' },
  {
    username: { required: true, minLength: 3, maxLength: 50 },
    email: { required: true, type: 'email' },
    password: { required: true, type: 'password' },
    confirmPassword: {
      required: true,
      validator: (value) => {
        if (value !== form.password) {
          return { isValid: false, message: 'Passwords do not match' }
        }
        return { isValid: true, message: '' }
      }
    }
  }
)
```

### Pattern 3: Date Range Form
```javascript
const { validateField, getFieldError, hasFieldError, isFormValid } = useFormValidation(
  { startDate: null, endDate: null },
  {
    startDate: { required: true },
    endDate: {
      required: true,
      validator: (value) => {
        if (form.startDate && value && value < form.startDate) {
          return { isValid: false, message: 'End date must be after start date' }
        }
        return { isValid: true, message: '' }
      }
    }
  }
)
```

## Tips & Tricks

1. **Validate on blur** - Call `validateField()` on blur event
2. **Show errors only when touched** - Use `hasFieldError()` to check
3. **Disable submit until valid** - Use `:disabled="!isFormValid"`
4. **Show password strength** - Use `validatePassword()` result
5. **Handle async validation** - Use `isFieldValidating()` for loading state
6. **Reset form** - Call `resetForm()` after successful submission
7. **Custom messages** - Return custom message from validator
8. **Batch validation** - Call `validateForm()` before submission

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Errors not showing | Check `hasFieldError()` returns true |
| Validation not triggering | Ensure `validateField()` called on blur |
| Submit button not disabled | Check `isFormValid` is false |
| Async validation not working | Ensure validator returns Promise |
| Form not resetting | Call `resetForm()` after submission |

## Files Reference

- **Utilities**: `VUE/src/utils/validationUtils.js`
- **Composable**: `VUE/src/composables/useFormValidation.js`
- **Component**: `VUE/src/components/FormField.vue`
- **Guide**: `VUE/src/utils/FRONTEND_VALIDATION_GUIDE.md`
- **Summary**: `VUE/FRONTEND_VALIDATION_IMPLEMENTATION_SUMMARY.md`
