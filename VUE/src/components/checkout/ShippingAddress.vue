<template>
  <div class="shipping-address-container">
    <h2>收货地址</h2>

    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="收货地址" prop="address">
        <el-input
          v-model="form.address"
          type="textarea"
          :rows="4"
          placeholder="请输入详细的收货地址，例如：辽宁省沈阳市和平区中山路1号"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="收货人姓名" prop="recipientName">
        <el-input
          v-model="form.recipientName"
          placeholder="请输入收货人姓名"
          maxlength="50"
        />
      </el-form-item>

      <el-form-item label="收货人电话" prop="recipientPhone">
        <el-input
          v-model="form.recipientPhone"
          placeholder="请输入收货人电话号码"
          maxlength="20"
        />
      </el-form-item>

      <el-form-item label="邮编（可选）" prop="postalCode">
        <el-input
          v-model="form.postalCode"
          placeholder="请输入邮编"
          maxlength="10"
        />
      </el-form-item>

      <div class="form-actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleNext">下一步</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'

interface Props {
  address: string
}

interface Emits {
  (e: 'update', address: string): void
  (e: 'next'): void
}

defineProps<Props>()
const emit = defineEmits<Emits>()

const formRef = ref<FormInstance>()

const form = ref({
  address: '',
  recipientName: '',
  recipientPhone: '',
  postalCode: '',
})

const rules = {
  address: [
    { required: true, message: '请输入收货地址', trigger: 'blur' },
    { min: 10, message: '地址长度不能少于10个字符', trigger: 'blur' },
  ],
  recipientName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
    { min: 2, message: '姓名长度不能少于2个字符', trigger: 'blur' },
  ],
  recipientPhone: [
    { required: true, message: '请输入收货人电话', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入有效的手机号码',
      trigger: 'blur',
    },
  ],
}

const handleCancel = () => {
  ElMessage.info('已取消')
}

const handleNext = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    // Combine address info into a single string
    const fullAddress = `${form.value.address} | 收货人: ${form.value.recipientName} | 电话: ${form.value.recipientPhone}${form.value.postalCode ? ' | 邮编: ' + form.value.postalCode : ''}`
    emit('update', fullAddress)
    emit('next')
  } catch (error) {
    ElMessage.error('请填写完整的收货信息')
  }
}
</script>

<style scoped>
.shipping-address-container {
  max-width: 600px;
  margin: 0 auto;
}

.shipping-address-container h2 {
  color: #333;
  margin-bottom: 30px;
  font-size: 20px;
  font-weight: bold;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-top: 30px;
}

.form-actions :deep(.el-button) {
  min-width: 120px;
}

@media (max-width: 768px) {
  .shipping-address-container {
    max-width: 100%;
  }

  .shipping-address-container :deep(.el-form) {
    --el-form-label-width: 100px;
  }
}
</style>
