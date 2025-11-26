<template>
  <el-form-item
    :prop="prop"
    :label="label"
    :label-width="labelWidth"
    :class="{ 'has-error': hasError }"
  >
    <div class="form-field-wrapper">
      <!-- Text Input -->
      <el-input
        v-if="type === 'text' || type === 'email' || type === 'password'"
        :model-value="modelValue"
        :type="type"
        :placeholder="placeholder"
        :clearable="clearable"
        :maxlength="maxlength"
        :show-word-limit="showWordLimit"
        :show-password="type === 'password'"
        @input="handleInput"
        @blur="handleBlur"
      />

      <!-- Textarea -->
      <el-input
        v-else-if="type === 'textarea'"
        :model-value="modelValue"
        type="textarea"
        :placeholder="placeholder"
        :maxlength="maxlength"
        :show-word-limit="showWordLimit"
        :rows="rows"
        @input="handleInput"
        @blur="handleBlur"
      />

      <!-- Number Input -->
      <el-input-number
        v-else-if="type === 'number'"
        :model-value="modelValue"
        :placeholder="placeholder"
        :min="min"
        :max="max"
        :step="step"
        :precision="precision"
        style="width: 100%"
        @input="handleInput"
        @blur="handleBlur"
      />

      <!-- Date Picker -->
      <el-date-picker
        v-else-if="type === 'date'"
        :model-value="modelValue"
        type="date"
        :placeholder="placeholder"
        :disabled-date="disabledDate"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        style="width: 100%"
        @input="handleInput"
        @blur="handleBlur"
      />

      <!-- Select -->
      <el-select
        v-else-if="type === 'select'"
        :model-value="modelValue"
        :placeholder="placeholder"
        style="width: 100%"
        @input="handleInput"
        @blur="handleBlur"
      >
        <el-option
          v-for="option in options"
          :key="option.value"
          :label="option.label"
          :value="option.value"
        />
      </el-select>

      <!-- Radio Group -->
      <el-radio-group
        v-else-if="type === 'radio'"
        :model-value="modelValue"
        @input="handleInput"
        @blur="handleBlur"
      >
        <el-radio
          v-for="option in options"
          :key="option.value"
          :label="option.value"
        >
          {{ option.label }}
        </el-radio>
      </el-radio-group>

      <!-- Checkbox Group -->
      <el-checkbox-group
        v-else-if="type === 'checkbox'"
        :model-value="modelValue"
        @input="handleInput"
        @blur="handleBlur"
      >
        <el-checkbox
          v-for="option in options"
          :key="option.value"
          :label="option.value"
        >
          {{ option.label }}
        </el-checkbox>
      </el-checkbox-group>

      <!-- Error message -->
      <div v-if="hasError" class="field-error">
        <el-icon class="error-icon"><CircleCloseFilled /></el-icon>
        <span>{{ errorMessage }}</span>
      </div>

      <!-- Help text -->
      <div v-if="helpText && !hasError" class="field-help">
        {{ helpText }}
      </div>
    </div>
  </el-form-item>
</template>

<script setup>
import { computed } from 'vue'
import { CircleCloseFilled } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: [String, Number, Boolean, Array, Date],
    default: ''
  },
  type: {
    type: String,
    default: 'text',
    validator: (value) => [
      'text', 'email', 'password', 'textarea', 'number', 'date', 'select', 'radio', 'checkbox'
    ].includes(value)
  },
  prop: {
    type: String,
    required: true
  },
  label: {
    type: String,
    required: true
  },
  placeholder: {
    type: String,
    default: ''
  },
  labelWidth: {
    type: String,
    default: '120px'
  },
  clearable: {
    type: Boolean,
    default: true
  },
  maxlength: {
    type: Number,
    default: null
  },
  showWordLimit: {
    type: Boolean,
    default: false
  },
  rows: {
    type: Number,
    default: 3
  },
  min: {
    type: Number,
    default: 0
  },
  max: {
    type: Number,
    default: Infinity
  },
  step: {
    type: Number,
    default: 1
  },
  precision: {
    type: Number,
    default: 0
  },
  options: {
    type: Array,
    default: () => []
  },
  disabledDate: {
    type: Function,
    default: null
  },
  errorMessage: {
    type: String,
    default: ''
  },
  helpText: {
    type: String,
    default: ''
  },
  touched: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'blur', 'input'])

const hasError = computed(() => {
  return props.touched && !!props.errorMessage
})

const handleInput = (value) => {
  emit('update:modelValue', value)
  emit('input', value)
}

const handleBlur = () => {
  emit('blur')
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
  line-height: 1;
}

.error-icon {
  font-size: 14px;
  flex-shrink: 0;
}

.field-help {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}

:deep(.el-form-item.has-error) {
  margin-bottom: 20px;
}

:deep(.el-input__wrapper),
:deep(.el-textarea__wrapper),
:deep(.el-input-number),
:deep(.el-select),
:deep(.el-date-picker) {
  width: 100%;
}
</style>
