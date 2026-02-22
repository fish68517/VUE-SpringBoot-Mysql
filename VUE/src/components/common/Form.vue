<template>
  <el-form
    ref="formRef"
    :model="modelValue"
    :rules="rules"
    :label-width="labelWidth"
    @submit.prevent="handleSubmit"
  >
    <el-form-item
      v-for="field in fields"
      :key="field.prop"
      :label="field.label"
      :prop="field.prop"
    >
      <el-input
        v-if="field.type === 'text' || field.type === 'email' || field.type === 'password'"
        v-model="modelValue[field.prop]"
        :type="field.type"
        :placeholder="field.placeholder"
      />
      <el-select
        v-else-if="field.type === 'select'"
        v-model="modelValue[field.prop]"
        :placeholder="field.placeholder"
      >
        <el-option
          v-for="option in field.options"
          :key="option.value"
          :label="option.label"
          :value="option.value"
        />
      </el-select>
      <el-date-picker
        v-else-if="field.type === 'date'"
        v-model="modelValue[field.prop]"
        type="date"
        :placeholder="field.placeholder"
      />
      <el-input-number
        v-else-if="field.type === 'number'"
        v-model="modelValue[field.prop]"
        :placeholder="field.placeholder"
      />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="handleSubmit">{{ submitText }}</el-button>
      <el-button @click="handleReset">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  modelValue: {
    type: Object,
    required: true,
  },
  fields: {
    type: Array,
    required: true,
  },
  rules: {
    type: Object,
    default: () => ({}),
  },
  labelWidth: {
    type: String,
    default: '120px',
  },
  submitText: {
    type: String,
    default: '提交',
  },
})

const emit = defineEmits(['submit', 'update:modelValue'])

const formRef = ref(null)

const handleSubmit = async () => {
  await formRef.value.validate()
  emit('submit')
}

const handleReset = () => {
  formRef.value.resetFields()
}
</script>
