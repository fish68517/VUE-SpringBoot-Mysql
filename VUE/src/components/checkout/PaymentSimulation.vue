<template>
  <div class="payment-simulation-container">
    <h2>支付</h2>

    <div class="section">
      <h3>选择支付方式</h3>
      <el-radio-group v-model="selectedPaymentMethod" class="payment-methods">
        <el-radio value="wechat" border>
          <span class="method-label">微信支付</span>
        </el-radio>
        <el-radio value="alipay" border>
          <span class="method-label">支付宝</span>
        </el-radio>
        <el-radio value="card" border>
          <span class="method-label">银行卡</span>
        </el-radio>
      </el-radio-group>
    </div>

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

    <div class="section info-section" v-if="false">
      <el-alert
        title="模拟支付提示"
        type="info"
        description="这是一个模拟支付页面，点击下方按钮即可完成支付。支付成功后系统会自动为您创建订单。"
        :closable="false"
      />
    </div>

    <div v-if="paymentStatus" class="section">
      <el-result
        :icon="paymentStatus === 'success' ? 'success' : 'error'"
        :title="paymentStatus === 'success' ? '支付成功' : '支付失败'"
        :sub-title="paymentStatus === 'success' ? '正在为您生成真实订单，请稍候...' : '支付过程中出现错误，请重试'"
      />
    </div>

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

// 修复点 2：接收 props 变量，以便下面可以直接使用
const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const selectedPaymentMethod = ref('wechat')
const isProcessing = ref(false)
const paymentStatus = ref<'success' | 'error' | null>(null)

// 修复点 3：直接计算总和，不要返回一个函数
const totalItems = computed(() => {
  return props.orderItems.reduce((sum, item) => sum + item.quantity, 0)
})

const handleBack = () => {
  emit('back')
}

const handlePayment = async () => {
  isProcessing.value = true

  try {
    // 模拟等待 2 秒
    await new Promise((resolve) => setTimeout(resolve, 2000))

    // 模拟 100% 成功率 (方便开发测试，防止你脸黑随机到失败)
    const isSuccess = true 

    if (isSuccess) {
      paymentStatus.value = 'success'
      ElMessage.success('支付成功！正在生成订单...')

      // 再等 1.5 秒后通知 Checkout.vue 去真正调用后端接口
      setTimeout(() => {
        emit('complete')
      }, 1500)
    } else {
      paymentStatus.value = 'error'
      ElMessage.error('支付失败，请重试')

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
/* 样式保留你原来的 */
.payment-simulation-container { max-width: 600px; margin: 0 auto; }
.payment-simulation-container h2 { color: #333; margin-bottom: 30px; font-size: 20px; font-weight: bold; }
.section { margin-bottom: 30px; padding: 20px; background: #f9f9f9; border-radius: 8px; }
.section h3 { color: #333; margin: 0 0 15px 0; font-size: 16px; font-weight: bold; }
.payment-methods { display: flex; flex-direction: column; gap: 15px; }
.payment-methods :deep(.el-radio) { width: 100%; padding: 15px; border-radius: 4px; transition: all 0.3s ease; }
.payment-methods :deep(.el-radio:hover) { background: #f0f0f0; }
.payment-methods :deep(.el-radio.is-checked) { background: #e6f7ff; border-color: #409eff; }
.method-label { font-size: 14px; color: #333; font-weight: 500; }
.order-summary { padding: 15px; background: white; border-radius: 4px; }
.summary-row { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; border-bottom: 1px solid #e0e0e0; font-size: 14px; }
.summary-row.total { border-bottom: none; border-top: 2px solid #e0e0e0; padding-top: 15px; font-size: 16px; font-weight: bold; color: #e74c3c; }
.summary-row .label { color: #666; }
.summary-row .value { color: #333; font-weight: bold; }
.summary-row.total .value { color: #e74c3c; font-size: 18px; }
.info-section { background: #f0f9ff; border: 1px solid #b3d8ff; }
.action-buttons { display: flex; gap: 10px; justify-content: center; margin-top: 30px; }
.action-buttons :deep(.el-button) { min-width: 120px; }
@media (max-width: 768px) { .payment-simulation-container { max-width: 100%; } .payment-methods { gap: 10px; } .payment-methods :deep(.el-radio) { padding: 12px; } }
</style>