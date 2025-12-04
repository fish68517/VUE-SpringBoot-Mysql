<template>
  <div class="payment-simulation">
    <div class="header">
      <el-button type="text" @click="handleBack">← 返回</el-button>
      <h2>支付</h2>
      <div></div>
    </div>

    <div class="payment-container">
      <!-- Order Summary -->
      <div class="order-summary">
        <h3>订单信息</h3>
        <div class="summary-content">
          <div class="summary-item">
            <span class="label">订单号：</span>
            <span class="value">{{ order?.orderId }}</span>
          </div>
          <div class="summary-item">
            <span class="label">票数：</span>
            <span class="value">{{ order?.ticketCount }} 张</span>
          </div>
          <div class="summary-item">
            <span class="label">订单状态：</span>
            <span class="value">{{ order?.status }}</span>
          </div>
          <div class="summary-item total">
            <span class="label">应付金额：</span>
            <span class="value">¥{{ order?.totalAmount }}</span>
          </div>
        </div>
      </div>

      <!-- Payment Method Selection -->
      <div class="payment-method">
        <h3>选择支付方式</h3>
        <div class="method-options">
          <div
            :class="['method-option', { selected: selectedMethod === 'wechat' }]"
            @click="selectedMethod = 'wechat'"
          >
            <div class="method-icon">💳</div>
            <div class="method-name">微信支付</div>
          </div>
          <div
            :class="['method-option', { selected: selectedMethod === 'alipay' }]"
            @click="selectedMethod = 'alipay'"
          >
            <div class="method-icon">💳</div>
            <div class="method-name">支付宝</div>
          </div>
          <div
            :class="['method-option', { selected: selectedMethod === 'card' }]"
            @click="selectedMethod = 'card'"
          >
            <div class="method-icon">🏦</div>
            <div class="method-name">银行卡</div>
          </div>
        </div>
      </div>

      <!-- Payment Confirmation -->
      <div class="payment-confirmation">
        <el-checkbox v-model="agreeTerms">
          我已阅读并同意
          <span class="link">《购票协议》</span>
          和
          <span class="link">《隐私政策》</span>
        </el-checkbox>
      </div>

      <!-- Action Buttons -->
      <div class="actions">
        <el-button @click="handleBack">返回</el-button>
        <el-button
          type="primary"
          :loading="loading"
          :disabled="!agreeTerms"
          @click="handleConfirmPayment"
        >
          确认支付 ¥{{ order?.totalAmount }}
        </el-button>
      </div>
    </div>

    <!-- Payment Success Modal -->
    <el-dialog v-model="showSuccessDialog" title="支付成功" width="400px" :close-on-click-modal="false">
      <div class="success-content">
        <div class="success-icon">✓</div>
        <h3>支付成功</h3>
        <p>您的电子票已生成，请前往"我的电子票"查看</p>
        <div class="ticket-info">
          <div class="info-item">
            <span class="label">订单号：</span>
            <span class="value">{{ order?.orderId }}</span>
          </div>
          <div class="info-item">
            <span class="label">票数：</span>
            <span class="value">{{ order?.ticketCount }} 张</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="handleConfirmSuccess">完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { TicketOrderResponse } from '@/api/ticket'

interface Props {
  order: TicketOrderResponse | null
  loading: boolean
}

interface Emits {
  (e: 'confirm'): void
  (e: 'back'): void
}

defineProps<Props>()
const emit = defineEmits<Emits>()

const selectedMethod = ref('wechat')
const agreeTerms = ref(false)
const showSuccessDialog = ref(false)

const handleConfirmPayment = async () => {
  if (!agreeTerms.value) {
    ElMessage.warning('请同意购票协议和隐私政策')
    return
  }

  // Simulate payment processing
  showSuccessDialog.value = true
}

const handleConfirmSuccess = () => {
  showSuccessDialog.value = false
  emit('confirm')
}

const handleBack = () => {
  emit('back')
}
</script>

<style scoped>
.payment-simulation {
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

.payment-container {
  max-width: 600px;
  margin: 0 auto;
}

.order-summary {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
}

.order-summary h3 {
  font-size: 16px;
  color: #333;
  margin: 0 0 15px 0;
  font-weight: bold;
}

.summary-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.summary-item .label {
  color: #999;
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

.payment-method {
  margin-bottom: 30px;
}

.payment-method h3 {
  font-size: 16px;
  color: #333;
  margin: 0 0 15px 0;
  font-weight: bold;
}

.method-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.method-option {
  border: 2px solid #f0f0f0;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.method-option:hover {
  border-color: #409eff;
}

.method-option.selected {
  border-color: #409eff;
  background: #f0f9ff;
}

.method-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

.method-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.payment-confirmation {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 30px;
}

.payment-confirmation :deep(.el-checkbox) {
  font-size: 14px;
}

.link {
  color: #409eff;
  cursor: pointer;
  text-decoration: underline;
}

.actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
}

.actions :deep(.el-button) {
  min-width: 120px;
}

.success-content {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  font-size: 48px;
  color: #67c23a;
  margin-bottom: 15px;
}

.success-content h3 {
  font-size: 20px;
  color: #333;
  margin: 0 0 10px 0;
  font-weight: bold;
}

.success-content p {
  color: #999;
  font-size: 14px;
  margin: 0 0 20px 0;
}

.ticket-info {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 15px;
  text-align: left;
}

.info-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  margin-bottom: 8px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-item .label {
  color: #999;
}

.info-item .value {
  color: #333;
  font-weight: 500;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    gap: 15px;
  }

  .header h2 {
    text-align: center;
  }

  .method-options {
    grid-template-columns: 1fr;
  }

  .actions {
    flex-direction: column;
  }

  .actions :deep(.el-button) {
    width: 100%;
  }
}
</style>
