<template>
  <div class="payment-simulation-container">
    <h2>支付</h2>

    <!-- Payment Method Selection -->
    <div class="section">
      <h3>选择支付方式</h3>
      <el-radio-group v-model="selectedPaymentMethod" class="payment-methods">
        <el-radio label="wechat" border>
          <span class="method-label">微信支付</span>
        </el-radio>
        <el-radio label="alipay" border>
          <span class="method-label">支付宝</span>
        </el-radio>
        <el-radio label="card" border>
          <span class="method-label">银行卡</span>
        </el-radio>
      </el-radio-group>
    </div>

    <!-- Order Summary -->
    <div class="section">
      <h3>订单信息</h3>
      <div class="order-summary">
        <div class="summary-row">
          <span class="label">商品数量：</span>
          <span class="value">{{ totalItems }} 件</span>
        </div>
        <div class="summary-row">
          <span class="label">商品总价：</span>
          <span class="value">¥{{ orderAmount.toFixed(2) }}</span>
        </div>
        <div class="summary-row">
          <span class="label">运费：</span>
          <span class="value">¥0.00</span>
        </div>
        <div class="summary-row total">
          <span class="label">应付金额：</span>
          <span class="value">¥{{ orderAmount.toFixed(2) }}</span>
        </div>
      </div>
    </div>

    <!-- Payment Simulation Info -->
    <div class="section info-section">
      <el-alert
        title="模拟支付提示"
        type="info"
        description="这是一个模拟支付页面，点击下方按钮即可完成支付。实际应用中会跳转到真实的支付网关。"
        :closable="false"
      />
    </div>

    <!-- Payment Status -->
    <div v-if="paymentStatus" class="section">
      <el-result
        :icon="paymentStatus === 'success' ? 'success' : 'error'"
        :title="paymentStatus === 'success' ? '支付成功' : '支付失败'"
        :sub-title="paymentStatus === 'success' ? '订单已创建，请等待跳转...' : '支付过程中出现错误，请重试'"
      />
    </div>

    <!-- Action Buttons -->
    <div v-if="!paymentStatus" class="action-buttons">
      <el-button @click="handleBack" :disabled="isProcessing">上一步</el-button>
      <el-button
        type="primary"
        @click="handlePayment"
        :loading="isProcessing"
        :disabled="isProcessing"
      >
        {{ isProcessing ? '处理中...' : '完成支付' }}
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { CartItem } from '@/stores/cart'

interface Props {
  orderAmount: number
  orderItems: CartItem[]
}

interface Emits {
  (e: 'back'): void
  (e: 'complete'): void
}

defineProps<Props>()
const emit = defineEmits<Emits>()

const selectedPaymentMethod = ref('wechat')
const isProcessing = ref(false)
const paymentStatus = ref<'success' | 'error' | null>(null)

const totalItems = computed(() => {
  return (props: Props) => props.orderItems.reduce((sum, item) => sum + item.quantity, 0)
})

const handleBack = () => {
  emit('back')
}

const handlePayment = async () => {
  isProcessing.value = true

  try {
    // Simulate payment processing
    await new Promise((resolve) => setTimeout(resolve, 2000))

    // Simulate successful payment (90% success rate for demo)
    const isSuccess = Math.random() > 0.1

    if (isSuccess) {
      paymentStatus.value = 'success'
      ElMessage.success('支付成功')

      // Wait 2 seconds then emit complete event
      setTimeout(() => {
        emit('complete')
      }, 2000)
    } else {
      paymentStatus.value = 'error'
      ElMessage.error('支付失败，请重试')

      // Reset after 3 seconds
      setTimeout(() => {
        paymentStatus.value = null
        isProcessing.value = false
      }, 3000)
    }
  } catch (error) {
    paymentStatus.value = 'error'
    ElMessage.error('支付过程中出现错误')

    setTimeout(() => {
      paymentStatus.value = null
      isProcessing.value = false
    }, 3000)
  }
}
</script>

<style scoped>
.payment-simulation-container {
  max-width: 600px;
  margin: 0 auto;
}

.payment-simulation-container h2 {
  color: #333;
  margin-bottom: 30px;
  font-size: 20px;
  font-weight: bold;
}

.section {
  margin-bottom: 30px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
}

.section h3 {
  color: #333;
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: bold;
}

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.payment-methods :deep(.el-radio) {
  width: 100%;
  padding: 15px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.payment-methods :deep(.el-radio:hover) {
  background: #f0f0f0;
}

.payment-methods :deep(.el-radio.is-checked) {
  background: #e6f7ff;
  border-color: #409eff;
}

.method-label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.order-summary {
  padding: 15px;
  background: white;
  border-radius: 4px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #e0e0e0;
  font-size: 14px;
}

.summary-row.total {
  border-bottom: none;
  border-top: 2px solid #e0e0e0;
  padding-top: 15px;
  font-size: 16px;
  font-weight: bold;
  color: #e74c3c;
}

.summary-row .label {
  color: #666;
}

.summary-row .value {
  color: #333;
  font-weight: bold;
}

.summary-row.total .value {
  color: #e74c3c;
  font-size: 18px;
}

.info-section {
  background: #f0f9ff;
  border: 1px solid #b3d8ff;
}

.action-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-top: 30px;
}

.action-buttons :deep(.el-button) {
  min-width: 120px;
}

@media (max-width: 768px) {
  .payment-simulation-container {
    max-width: 100%;
  }

  .payment-methods {
    gap: 10px;
  }

  .payment-methods :deep(.el-radio) {
    padding: 12px;
  }
}
</style>
