<template>
  <div class="buyer-information">
    <div class="header">
      <el-button type="text" @click="handleBack">← 返回</el-button>
      <h2>填写购票人信息</h2>
      <div></div>
    </div>

    <div class="order-summary">
      <div class="summary-item">
        <span class="label">场次：</span>
        <span class="value">{{ session?.name }}</span>
      </div>
      <div class="summary-item">
        <span class="label">分区：</span>
        <span class="value">{{ zone?.name }}</span>
      </div>
      <div class="summary-item">
        <span class="label">单价：</span>
        <span class="value">¥{{ zone?.price }}</span>
      </div>
      <div class="summary-item">
        <span class="label">购票人数：</span>
        <span class="value">{{ buyers.length }} 人</span>
      </div>
      <div class="summary-item total">
        <span class="label">总金额：</span>
        <span class="value">¥{{ totalAmount }}</span>
      </div>
    </div>

    <div class="buyers-section">
      <div class="section-header">
        <h3>购票人信息</h3>
        <span class="hint">支持一人代购最多 4 人</span>
      </div>

      <div class="buyers-list">
        <div v-for="(buyer, index) in buyers" :key="index" class="buyer-form">
          <div class="buyer-title">购票人 {{ index + 1 }}</div>

          <el-form :model="buyer" label-width="100px" @submit.prevent>
            <el-form-item label="身份证号">
              <el-input
                v-model="buyer.idNumber"
                placeholder="请输入身份证号"
                maxlength="18"
                @blur="validateIdNumber(index)"
              />
              <span v-if="idNumberErrors[index]" class="error-text">{{ idNumberErrors[index] }}</span>
            </el-form-item>

            <el-form-item label="姓名">
              <el-input
                v-model="buyer.name"
                placeholder="请输入姓名"
                maxlength="50"
                @blur="validateName(index)"
              />
              <span v-if="nameErrors[index]" class="error-text">{{ nameErrors[index] }}</span>
            </el-form-item>

            <el-form-item v-if="index > 0">
              <el-button type="danger" text @click="removeBuyer(index)">删除</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <div v-if="buyers.length < 4" class="add-buyer">
        <el-button @click="addBuyer">+ 添加购票人</el-button>
      </div>
    </div>

    <div class="actions">
      <el-button @click="handleBack">返回</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">下一步：支付</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { TicketSession, TicketZone } from '@/api/ticket'

interface Buyer {
  idNumber: string
  name: string
}

interface Props {
  zone: TicketZone | null
  session: TicketSession | null
  loading: boolean
}

interface Emits {
  (e: 'submit', buyers: Buyer[]): void
  (e: 'back'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const buyers = ref<Buyer[]>([{ idNumber: '', name: '' }])
const idNumberErrors = ref<Record<number, string>>({})
const nameErrors = ref<Record<number, string>>({})

const totalAmount = computed(() => {
  if (!props.zone) return 0
  return (props.zone.price * buyers.value.length).toFixed(2)
})

const validateIdNumber = (index: number) => {
  const idNumber = buyers.value[index].idNumber.trim()
  if (!idNumber) {
    idNumberErrors.value[index] = '请输入身份证号'
    return false
  }
  if (!/^\d{18}$/.test(idNumber)) {
    idNumberErrors.value[index] = '身份证号必须为18位数字'
    return false
  }
  delete idNumberErrors.value[index]
  return true
}

const validateName = (index: number) => {
  const name = buyers.value[index].name.trim()
  if (!name) {
    nameErrors.value[index] = '请输入姓名'
    return false
  }
  if (name.length < 2 || name.length > 50) {
    nameErrors.value[index] = '姓名长度应为2-50个字符'
    return false
  }
  delete nameErrors.value[index]
  return true
}

const validateAllBuyers = (): boolean => {
  let isValid = true
  buyers.value.forEach((_, index) => {
    if (!validateIdNumber(index)) isValid = false
    if (!validateName(index)) isValid = false
  })
  return isValid
}

const addBuyer = () => {
  if (buyers.value.length < 4) {
    buyers.value.push({ idNumber: '', name: '' })
  } else {
    ElMessage.warning('最多只能添加4个购票人')
  }
}

const removeBuyer = (index: number) => {
  if (buyers.value.length > 1) {
    buyers.value.splice(index, 1)
    delete idNumberErrors.value[index]
    delete nameErrors.value[index]
  }
}

const handleSubmit = () => {
  if (!validateAllBuyers()) {
    ElMessage.error('请填写完整的购票人信息')
    return
  }
  emit('submit', buyers.value)
}

const handleBack = () => {
  emit('back')
}
</script>

<style scoped>
.buyer-information {
  width: 100%;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header h2 {
  font-size: 24px;
  color: #333;
  margin: 0;
  font-weight: bold;
  flex: 1;
  text-align: center;
}

.header :deep(.el-button) {
  color: #409eff;
}

.order-summary {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
}

.summary-item .label {
  color: #999;
  min-width: 80px;
}

.summary-item .value {
  color: #333;
  font-weight: 500;
}

.summary-item.total {
  border-top: 1px solid #ddd;
  padding-top: 12px;
  margin-top: 12px;
  font-size: 16px;
}

.summary-item.total .value {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.buyers-section {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 18px;
  color: #333;
  margin: 0;
  font-weight: bold;
}

.hint {
  font-size: 12px;
  color: #999;
}

.buyers-list {
  display: grid;
  gap: 20px;
  margin-bottom: 20px;
}

.buyer-form {
  background: white;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 20px;
}

.buyer-title {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
}

.buyer-form :deep(.el-form-item) {
  margin-bottom: 15px;
}

.buyer-form :deep(.el-form-item:last-child) {
  margin-bottom: 0;
}

.error-text {
  display: block;
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
}

.add-buyer {
  text-align: center;
}

.add-buyer :deep(.el-button) {
  width: 100%;
}

.actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.actions :deep(.el-button) {
  min-width: 120px;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    gap: 15px;
  }

  .header h2 {
    text-align: center;
  }

  .actions {
    flex-direction: column;
  }

  .actions :deep(.el-button) {
    width: 100%;
  }
}
</style>
