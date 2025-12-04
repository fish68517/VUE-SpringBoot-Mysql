<template>
  <div class="checkout-container">
    <!-- Header -->
    <div class="checkout-header">
      <div class="header-content">
        <el-button type="primary" text icon="ArrowLeft" @click="handleBack">返回购物车</el-button>
        <h1>订单结算</h1>
        <div style="width: 100px"></div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="checkout-content">
      <!-- Steps -->
      <el-steps :active="currentStep" finish-status="success" align-center class="checkout-steps">
        <el-step title="收货地址" />
        <el-step title="订单确认" />
        <el-step title="支付" />
      </el-steps>

      <!-- Step Content -->
      <div class="step-content">
        <!-- Step 1: Shipping Address -->
        <ShippingAddress
          v-if="currentStep === 0"
          :address="shippingAddress"
          @update="handleAddressUpdate"
          @next="handleAddressNext"
        />

        <!-- Step 2: Order Confirmation -->
        <OrderConfirmation
          v-if="currentStep === 1"
          :cart-items="cartItems"
          :shipping-address="shippingAddress"
          :total-amount="totalAmount"
          @back="handleConfirmationBack"
          @next="handleConfirmationNext"
        />

        <!-- Step 3: Payment -->
        <PaymentSimulation
          v-if="currentStep === 2"
          :order-amount="totalAmount"
          :order-items="cartItems"
          @back="handlePaymentBack"
          @complete="handlePaymentComplete"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { orderApi } from '@/api/order'
import ShippingAddress from '@/components/checkout/ShippingAddress.vue'
import OrderConfirmation from '@/components/checkout/OrderConfirmation.vue'
import PaymentSimulation from '@/components/checkout/PaymentSimulation.vue'

const router = useRouter()
const cartStore = useCartStore()

const currentStep = ref(0)
const shippingAddress = ref('')

const cartItems = computed(() => cartStore.items)
const totalAmount = computed(() => cartStore.totalPrice)

const handleBack = () => {
  ElMessageBox.confirm('确定要返回购物车吗？当前进度将丢失', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      router.push('/cart')
    })
    .catch(() => {
      // User cancelled
    })
}

const handleAddressUpdate = (address: string) => {
  shippingAddress.value = address
}

const handleAddressNext = () => {
  if (!shippingAddress.value.trim()) {
    ElMessage.warning('请填写收货地址')
    return
  }
  currentStep.value = 1
}

const handleConfirmationBack = () => {
  currentStep.value = 0
}

const handleConfirmationNext = () => {
  currentStep.value = 2
}

const handlePaymentBack = () => {
  currentStep.value = 1
}

const handlePaymentComplete = async () => {
  try {
    // Create order
    const request = {
      cartItemIds: cartItems.value.map((item) => item.id),
      shippingAddress: shippingAddress.value,
    }

    const response = await orderApi.createProductOrder(request)

    if (response && response.data) {
      ElMessage.success('订单创建成功')
      cartStore.clearCart()
      // Navigate to orders page after successful order
      setTimeout(() => {
        router.push('/orders')
      }, 1500)
    } else {
      ElMessage.error('订单创建失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '订单创建失败')
  }
}

onMounted(() => {
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    router.push('/cart')
  }
})
</script>

<style scoped>
.checkout-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.checkout-header {
  background: white;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.checkout-header h1 {
  color: #333;
  margin: 0;
  font-size: 28px;
  font-weight: bold;
}

.checkout-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.checkout-steps {
  margin-bottom: 40px;
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.step-content {
  background: white;
  border-radius: 8px;
  padding: 40px;
  min-height: 500px;
}

@media (max-width: 768px) {
  .checkout-header h1 {
    font-size: 20px;
  }

  .checkout-content {
    padding: 20px 15px;
  }

  .step-content {
    padding: 20px;
    min-height: auto;
  }
}
</style>
