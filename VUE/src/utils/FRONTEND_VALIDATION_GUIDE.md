# Frontend Data Validation Implementation Guide

## Overview

This guide documents the comprehensive client-side validation system implemented in the 旅行日记 frontend. The validation system provides real-time feedback, field-level error display, and a consistent user experience across all forms.

## Architecture

### Core Components

1. **validationUtils.js** - Utility functions for various validation types
2. **useFormValidation.js** - Vue composable for form state and validation management
3. **FormField.vue** - Reusable form field component with built-in validation display
4. **Form Components** - Individual form components using the validation system

### Validation Flow

```
User Input
    ↓
Field Blur/Change Event
    ↓
validateField() called
    ↓
Validation Rules Applied
    ↓
Error Message Generated
    ↓
UI Updated with Error Display
```

## Validation Utilities

### Available Validators

#### Email Validation
```javascript
import { isValidEmail } from '@/utils/validationUtils'

const result = isValidEmail('user@example.com')
// Returns: true/false
```

#### Password Validation
```javascript
import { validatePassword } from '@/utils/validationUtils'

const result = validatePassword('SecurePass123!')
// Returns: { isValid: true, strength: 'strong', message: 'Password strength: strong' }
```

#### Username Validation
```javascript
import { validateUsername } from '@/utils/validationUtils'

const result = validateUsername('john_doe')
// Returns: { isValid: true, message: 'Username is valid' }
```

#### Required Field Validation
```javascript
import { validateRequired } from '@/utils/validationUtils'

const result = validateRequired(value, 'Username')
// Returns: { isValid: false, message: 'Username is required' }
```

#### Date Range Validation
```javascript
import { validateDateRange } from '@/utils/validationUtils'

const result = validateDateRange('2024-01-01', '2024-12-31')
// Returns: { isValid: true, message: '' }
```

#### Number Range Validation
```javascript
import { validateNumberRange } from '@/utils/validationUtils'

const result = validateNumberRange(50, 0, 100)
// Returns: { isValid: true, message: '' }
```

#### Text Length Validation
```javascript
import { validateTextLength } from '@/utils/validationUtils'

const result = validateTextLength('Hello', 3, 50)
// Returns: { isValid: true, message: '' }
```

#### File Size Validation
```javascript
import { validateFileSize } from '@/utils/validationUtils'

const result = validateFileSize(file, 500) // 500MB max
// Returns: { isValid: true, message: '' }
```

#### File Type Validation
```javascript
import { validateFileType } from '@/utils/validationUtils'

const result = validateFileType(file, ['image/jpeg', 'image/png'])
// Returns: { isValid: true, message: '' }
```

#### URL Validation
```javascript
import { validateUrl } from '@/utils/validationUtils'

const result = validateUrl('https://example.com')
// Returns: { isValid: true, message: '' }
```

#### Budget Validation
```javascript
import { validateBudget } from '@/utils/validationUtils'

const result = validateBudget(1000, 0, 999999)
// Returns: { isValid: true, message: '' }
```

#### Future Date Validation
```javascript
import { validateFutureDate } from '@/utils/validationUtils'

const result = validateFutureDate('2025-12-31')
// Returns: { isValid: true, message: '' }
```

#### Past Date Validation
```javascript
import { validatePastDate } from '@/utils/validationUtils'

const result = validatePastDate('2023-01-01')
// Returns: { isValid: true, message: '' }
```

#### Match Validation (e.g., password confirmation)
```javascript
import { validateMatch } from '@/utils/validationUtils'

const result = validateMatch(password, confirmPassword, 'Passwords')
// Returns: { isValid: false, message: 'Passwords do not match' }
```

#### Unique Validation (async)
```javascript
import { validateUnique } from '@/utils/validationUtils'

const result = await validateUnique(email, async (value) => {
  const response = await checkEmailUniqueness(value)
  return response.isUnique
})
// Returns: { isValid: true, message: '' }
```

#### Phone Number Validation
```javascript
import { validatePhoneNumber } from '@/utils/validationUtils'

const result = validatePhoneNumber('+1-555-123-4567')
// Returns: { isValid: true, message: '' }
```

#### Postal Code Validation
```javascript
import { validatePostalCode } from '@/utils/validationUtils'

const result = validatePostalCode('12345')
// Returns: { isValid: true, message: '' }
```

## Form Validation Composable

### Basic Usage

```javascript
import { useFormValidation } from '@/composables/useFormValidation'

const {
  formData,
  errors,
  touched,
  validateField,
  validateForm,
  touchField,
  resetForm,
  setFormValues,
  getFieldError,
  hasFieldError,
  isFieldValidating,
  isFormValid,
  isFormTouched
} = useFormValidation(
  { email: '', password: '' }, // Initial values
  {
    email: { required: true, type: 'email' },
    password: { required: true, type: 'password', minLength: 8 }
  }
)
```

### Methods

#### validateField(fieldName)
Validates a single field and returns a Promise<boolean>

```javascript
const isValid = await validateField('email')
```

#### validateForm()
Validates all fields and returns a Promise<boolean>

```javascript
const isFormValid = await validateForm()
```

#### touchField(fieldName)
Marks a field as touched and validates it

```javascript
touchField('email')
```

#### touchAllFields()
Marks all fields as touched

```javascript
touchAllFields()
```

#### resetForm()
Resets form to initial values and clears errors

```javascript
resetForm()
```

#### setFormValues(values)
Updates form values

```javascript
setFormValues({ email: 'new@example.com' })
```

#### getFieldError(fieldName)
Returns error message for a field (only if touched)

```javascript
const error = getFieldError('email')
```

#### hasFieldError(fieldName)
Returns true if field has error and is touched

```javascript
if (hasFieldError('email')) {
  // Show error
}
```

#### isFieldValidating(fieldName)
Returns true if field is currently validating (async validators)

```javascript
if (isFieldValidating('email')) {
  // Show loading indicator
}
```

### Computed Properties

#### isFormValid
Returns true if all fields are valid

```javascript
if (isFormValid.value) {
  // Enable submit button
}
```

#### isFormTouched
Returns true if any field has been touched

```javascript
if (isFormTouched.value) {
  // Show validation feedback
}
```

## Form Field Component

### Basic Usage

```vue
<FormField
  v-model="form.email"
  type="email"
  prop="email"
  label="Email"
  placeholder="Enter your email"
  :error-message="getFieldError('email')"
  :touched="touched.email"
  @blur="touchField('email')"
/>
```

### Props

- `modelValue` - Form field value
- `type` - Input type (text, email, password, textarea, number, date, select, radio, checkbox)
- `prop` - Property name for form item
- `label` - Field label
- `placeholder` - Placeholder text
- `labelWidth` - Label width (default: 120px)
- `clearable` - Show clear button (default: true)
- `maxlength` - Maximum character length
- `showWordLimit` - Show character count (default: false)
- `rows` - Textarea rows (default: 3)
- `min` - Minimum value for number input
- `max` - Maximum value for number input
- `step` - Step value for number input
- `precision` - Decimal precision for number input
- `options` - Options for select/radio/checkbox
- `disabledDate` - Function to disable dates
- `errorMessage` - Error message to display
- `helpText` - Help text to display
- `touched` - Whether field has been touched

## Implementation Examples

### User Registration Form

```vue
<template>
  <el-form ref="formRef" :model="form" :rules="rules">
    <el-form-item label="Username" prop="username">
      <div class="form-field-wrapper">
        <el-input
          v-model="form.username"
          placeholder="Enter username"
          @blur="validateField('username')"
        />
        <div v-if="hasFieldError('username')" class="field-error">
          <el-icon><CircleCloseFilled /></el-icon>
          <span>{{ getFieldError('username') }}</span>
        </div>
      </div>
    </el-form-item>

    <el-form-item label="Email" prop="email">
      <div class="form-field-wrapper">
        <el-input
          v-model="form.email"
          type="email"
          placeholder="Enter email"
          @blur="validateField('email')"
        />
        <div v-if="hasFieldError('email')" class="field-error">
          <el-icon><CircleCloseFilled /></el-icon>
          <span>{{ getFieldError('email') }}</span>
        </div>
      </div>
    </el-form-item>

    <el-form-item label="Password" prop="password">
      <div class="form-field-wrapper">
        <el-input
          v-model="form.password"
          type="password"
          placeholder="Enter password"
          show-password
          @blur="validateField('password')"
        />
        <div v-if="hasFieldError('password')" class="field-error">
          <el-icon><CircleCloseFilled /></el-icon>
          <span>{{ getFieldError('password') }}</span>
        </div>
      </div>
    </el-form-item>

    <el-button @click="handleSubmit" :disabled="!isFormValid">
      Register
    </el-button>
  </el-form>
</template>

<script setup>
import { reactive } from 'vue'
import { CircleCloseFilled } from '@element-plus/icons-vue'
import { useFormValidation } from '@/composables/useFormValidation'

const form = reactive({
  username: '',
  email: '',
  password: ''
})

const {
  validateField,
  validateForm,
  getFieldError,
  hasFieldError,
  isFormValid
} = useFormValidation(form, {
  username: { required: true, minLength: 3, maxLength: 50 },
  email: { required: true, type: 'email' },
  password: { required: true, type: 'password' }
})

const handleSubmit = async () => {
  if (await validateForm()) {
    // Submit form
  }
}
</script>

<style scoped>
.form-field-wrapper {
  position: relative;
}

.field-error {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 6px;
  font-size: 12px;
  color: #f56c6c;
}
</style>
```

### Travel Record Form

```vue
<template>
  <el-form ref="formRef" :model="form" :rules="rules">
    <el-form-item label="Title" prop="title">
      <div class="form-field-wrapper">
        <el-input
          v-model="form.title"
          placeholder="Enter title"
          maxlength="200"
          show-word-limit
          @blur="validateField('title')"
        />
        <div v-if="hasFieldError('title')" class="field-error">
          {{ getFieldError('title') }}
        </div>
      </div>
    </el-form-item>

    <el-form-item label="Start Date" prop="startDate">
      <div class="form-field-wrapper">
        <el-date-picker
          v-model="form.startDate"
          type="date"
          placeholder="Select start date"
          style="width: 100%"
          @blur="validateField('startDate')"
        />
        <div v-if="hasFieldError('startDate')" class="field-error">
          {{ getFieldError('startDate') }}
        </div>
      </div>
    </el-form-item>

    <el-form-item label="End Date" prop="endDate">
      <div class="form-field-wrapper">
        <el-date-picker
          v-model="form.endDate"
          type="date"
          placeholder="Select end date"
          style="width: 100%"
          @blur="validateField('endDate')"
        />
        <div v-if="hasFieldError('endDate')" class="field-error">
          {{ getFieldError('endDate') }}
        </div>
      </div>
    </el-form-item>

    <el-button @click="handleSubmit" :disabled="!isFormValid">
      Save
    </el-button>
  </el-form>
</template>

<script setup>
import { reactive } from 'vue'
import { useFormValidation } from '@/composables/useFormValidation'

const form = reactive({
  title: '',
  startDate: null,
  endDate: null
})

const {
  validateField,
  validateForm,
  getFieldError,
  hasFieldError,
  isFormValid
} = useFormValidation(form, {
  title: { required: true, minLength: 1, maxLength: 200 },
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
})

const handleSubmit = async () => {
  if (await validateForm()) {
    // Submit form
  }
}
</script>
```

## Validation Rules Configuration

### Rule Structure

```javascript
{
  fieldName: {
    required: boolean,           // Field is required
    type: string,               // Validation type (email, password, username, url, budget, phone, postalCode)
    minLength: number,          // Minimum character length
    maxLength: number,          // Maximum character length
    min: number,                // Minimum numeric value
    max: number,                // Maximum numeric value
    label: string,              // Field label for error messages
    message: string,            // Custom error message
    validator: function         // Custom validator function
  }
}
```

### Custom Validators

```javascript
const rules = {
  email: {
    required: true,
    validator: async (value) => {
      // Check if email is unique
      const response = await checkEmailUniqueness(value)
      if (!response.isUnique) {
        return { isValid: false, message: 'Email already registered' }
      }
      return { isValid: true, message: '' }
    }
  }
}
```

## Error Display Patterns

### Pattern 1: Inline Error with Icon

```vue
<div v-if="hasFieldError('email')" class="field-error">
  <el-icon><CircleCloseFilled /></el-icon>
  <span>{{ getFieldError('email') }}</span>
</div>
```

### Pattern 2: Error Below Field

```vue
<div class="form-field-wrapper">
  <el-input v-model="form.email" />
  <div v-if="hasFieldError('email')" class="field-error">
    {{ getFieldError('email') }}
  </div>
</div>
```

### Pattern 3: Help Text

```vue
<div class="form-field-wrapper">
  <el-input v-model="form.password" />
  <div v-if="!hasFieldError('password')" class="field-hint">
    Password must be at least 8 characters
  </div>
  <div v-else class="field-error">
    {{ getFieldError('password') }}
  </div>
</div>
```

## Best Practices

1. **Validate on Blur** - Validate fields when user leaves the field
2. **Show Errors Only When Touched** - Don't show errors until user interacts with field
3. **Provide Clear Messages** - Use specific, actionable error messages
4. **Real-time Feedback** - Show validation status as user types
5. **Disable Submit Until Valid** - Prevent form submission with invalid data
6. **Handle Async Validation** - Show loading state during async validation
7. **Consistent Styling** - Use consistent error display across all forms
8. **Accessibility** - Include proper ARIA labels and error associations

## Testing

### Unit Test Example

```javascript
import { describe, it, expect } from 'vitest'
import { validateEmail, validatePassword } from '@/utils/validationUtils'

describe('Validation Utils', () => {
  it('should validate email format', () => {
    expect(validateEmail('user@example.com').isValid).toBe(true)
    expect(validateEmail('invalid-email').isValid).toBe(false)
  })

  it('should validate password strength', () => {
    const result = validatePassword('SecurePass123!')
    expect(result.isValid).toBe(true)
    expect(result.strength).toBe('strong')
  })
})
```

### Component Test Example

```javascript
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import UserRegister from '@/components/UserRegister.vue'

describe('UserRegister Component', () => {
  it('should show error when email is invalid', async () => {
    const wrapper = mount(UserRegister)
    const emailInput = wrapper.find('input[type="email"]')
    
    await emailInput.setValue('invalid-email')
    await emailInput.trigger('blur')
    
    expect(wrapper.find('.field-error').exists()).toBe(true)
  })

  it('should disable submit button when form is invalid', async () => {
    const wrapper = mount(UserRegister)
    const submitButton = wrapper.find('button[type="primary"]')
    
    expect(submitButton.attributes('disabled')).toBeDefined()
  })
})
```

## Troubleshooting

### Issue: Validation not triggering
**Solution**: Ensure `validateField()` is called on blur event

### Issue: Error message not displaying
**Solution**: Check that `hasFieldError()` returns true and error message is not empty

### Issue: Form submitting with invalid data
**Solution**: Ensure submit button is disabled when `isFormValid` is false

### Issue: Async validation not working
**Solution**: Ensure validator function returns a Promise and error is properly handled

## Performance Considerations

1. **Debounce Validation** - For async validators, debounce to avoid excessive API calls
2. **Lazy Validation** - Only validate fields that have been touched
3. **Memoize Validators** - Cache validation results when possible
4. **Batch Validation** - Validate multiple fields in parallel

## Future Enhancements

1. **Internationalization** - Support multiple languages for error messages
2. **Custom Error Formatting** - Allow custom error message formatting
3. **Validation Schemas** - Support Zod or Yup validation schemas
4. **Field Dependencies** - Support validation rules that depend on other fields
5. **Conditional Validation** - Show/hide fields based on validation state
