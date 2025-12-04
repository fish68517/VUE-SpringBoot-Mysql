<template>
  <div class="ticket-purchase-container">
    <div class="purchase-header">
      <h1>购票</h1>
      <div class="progress-indicator">
        <div :class="['step', { active: currentStep === 1, completed: currentStep > 1 }]">
          <span>1</span>
          <p>选择场次</p>
        </div>
        <div class="step-line" :class="{ completed: currentStep > 1 }"></div>
        <div :class="['step', { active: currentStep === 2, completed: currentStep > 2 }]">
          <span>2</span>
          <p>选择分区</p>
        </div>
        <div class="step-line" :class="{ completed: currentStep > 2 }"></div>
        <div :class="['step', { active: currentStep === 3, completed: currentStep > 3 }]">
          <span>3</span>
          <p>填写信息</p>
        </div>
        <div class="step-line" :class="{ completed: currentStep > 3 }"></div>
        <div :class="['step', { active: currentStep === 4 }]">
          <span>4</span>
          <p>支付</p>
        </div>
      </div>
    </div>

    <div class="purchase-content">
      <!-- Step 1: Session Selection -->
      <div v-if="currentStep === 1" class="step-content">
        <SessionSelection
          :sessions="sessions"
          :loading="loading"
          @select="handleSelectSession"
        />
      </div>

      <!-- Step 2: Zone Selection -->
      <div v-if="currentStep === 2" class="step-content">
        <ZoneSelection
          :zones="zones"
          :selected-session="selectedSession"
          :loading="loading"
          @select="handleSelectZone"
          @back="handleBack"
        />
      </div>

      <!-- Step 3: Buyer Information -->
      <div v-if="currentStep === 3" class="step-content">
        <BuyerInformation
          :zone="selectedZone"
          :session="selectedSession"
          :loading="loading"
          @submit="handleSubmitBuyers"
          @back="handleBack"
        />
      </div>

      <!-- Step 4: Payment -->
      <div v-if="currentStep === 4" class="step-content">
        <PaymentSimulation
          :order="orderResponse"
          :loading="loading"
          @confirm="handlePaymentConfirm"
          @back="handleBack"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ticketApi, TicketSession, TicketZone, TicketOrderRequest, TicketOrderResponse } from '@/api/ticket'
import SessionSelection from '@/components/ticket/SessionSelection.vue'
import ZoneSelection from '@/components/ticket/ZoneSelection.vue'
import BuyerInformation from '@/components/ticket/BuyerInformation.vue'
import PaymentSimulation from '@/components/ticket/PaymentSimulation.vue'

const router = useRouter()

const currentStep = ref(1)
const loading = ref(false)
const sessions = ref<TicketSession[]>([])
const zones = ref<TicketZone[]>([])
const selectedSession = ref<TicketSession | null>(null)
const selectedZone = ref<TicketZone | null>(null)
const orderResponse = ref<TicketOrderResponse | null>(null)

const loadSessions = async () => {
  try {
    loading.value = true
    const response = await ticketApi.getSessions()
    sessions.value = response.data
  } catch (error) {
    ElMessage.error('加载场次失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadZones = async (sessionId: number) => {
  try {
    loading.value = true
    const response = await ticketApi.getZones()
    zones.value = response.data.filter((z) => z.sessionId === sessionId)
  } catch (error) {
    ElMessage.error('加载分区失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSelectSession = (session: TicketSession) => {
  selectedSession.value = session
  loadZones(session.id)
  currentStep.value = 2
}

const handleSelectZone = (zone: TicketZone) => {
  selectedZone.value = zone
  currentStep.value = 3
}

const handleSubmitBuyers = async (buyers: Array<{ idNumber: string; name: string }>) => {
  if (!selectedSession.value || !selectedZone.value) {
    ElMessage.error('请选择场次和分区')
    return
  }

  try {
    loading.value = true
    const request: TicketOrderRequest = {
      sessionId: selectedSession.value.id,
      zoneId: selectedZone.value.id,
      buyers,
    }
    const response = await ticketApi.createOrder(request)
    orderResponse.value = response.data
    currentStep.value = 4
  } catch (error: any) {
    const errorMsg = error.response?.data?.message || '创建订单失败'
    ElMessage.error(errorMsg)
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handlePaymentConfirm = () => {
  ElMessage.success('支付成功！')
  router.push('/my-tickets')
}

const handleBack = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

onMounted(() => {
  loadSessions()
})
</script>

<style scoped>
.ticket-purchase-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.purchase-header {
  background: white;
  padding: 30px 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.purchase-header h1 {
  text-align: center;
  color: #333;
  margin: 0 0 30px 0;
  font-size: 28px;
}

.progress-indicator {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  position: relative;
}

.step span {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f0f0f0;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  transition: all 0.3s ease;
}

.step.active span {
  background: #409eff;
  color: white;
}

.step.completed span {
  background: #67c23a;
  color: white;
}

.step p {
  font-size: 12px;
  color: #999;
  margin: 0;
  white-space: nowrap;
}

.step.active p {
  color: #409eff;
  font-weight: bold;
}

.step.completed p {
  color: #67c23a;
}

.step-line {
  width: 40px;
  height: 2px;
  background: #f0f0f0;
  transition: all 0.3s ease;
}

.step-line.completed {
  background: #67c23a;
}

.purchase-content {
  max-width: 1000px;
  margin: 30px auto;
  padding: 0 20px;
}

.step-content {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-height: 400px;
}

@media (max-width: 768px) {
  .progress-indicator {
    gap: 10px;
  }

  .step-line {
    width: 20px;
  }

  .step span {
    width: 32px;
    height: 32px;
    font-size: 12px;
  }

  .step p {
    font-size: 10px;
  }

  .step-content {
    padding: 20px;
  }
}
</style>
