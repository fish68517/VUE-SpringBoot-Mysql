<template>
  <div class="order-confirmation-container">
    <h2>订单确认</h2>

    <!-- Shipping Address Section -->
    <div class="section">
      <h3>收货地址</h3>
      <div class="address-info">
        <p>{{ shippingAddress }}</p>
      </div>
    </div>

    <!-- Order Items Section -->
    <div class="section">
      <h3>订单商品</h3>
      <div class="items-table">
        <!-- Header Row -->
        <div class="table-header">
          <div class="col-product">商品</div>
          <div class="col-price">单价</div>
          <div class="col-quantity">数量</div>
          <div class="col-subtotal">小计</div>
        </div>

        <!-- Item Rows -->
        <div v-for="item in cartItems" :key="item.id" class="table-row">
          <div class="col-product">
            <div class="product-info">
              <img v-if="item.image" :src="item.image" :alt="item.name" class="product-image" />
              <div class="product-details">
                <div class="product-name">{{ item.name }}</div>
                <div v-if="Object.keys(item.specs).length > 0" class="product-specs">
                  <span v-for="(value, key) in item.specs" :key="key" class="spec-tag">
                    {{ key }}: {{ value }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div class="col-price">
            <span class="price">¥{{ item.price.toFixed(2) }}</span>
          </div>

          <div class="col-quantity">
            <span>{{ item.quantity }}</span>
          </div>

          <div class="col-subtotal">
            <span class="subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Summary Section -->
    <div class="section summary-section">
      <h3>订单总计</h3>
      <div class="summary-content">
        <div class="summary-row">
          <span class="label">商品数量：</span>
          <span class="value">{{ totalItems }} 件</span>
        </div>

        <div class="summary-row">
          <span class="label">商品总价：</span>
          <span class="value">¥{{ totalAmount.toFixed(2) }}</span>
        </div>

        <div class="summary-row">
          <span class="label">运费：</span>
          <span class="value">¥0.00</span>
        </div>

        <div class="summary-row total">
          <span class="label">应付金额：</span>
          <span class="value">¥{{ totalAmount.toFixed(2) }}</span>
        </div>
      </div>
    </div>

    <!-- Action Buttons -->
    <div class="action-buttons">
      <el-button @click="handleBack">上一步</el-button>
      <el-button type="primary" @click="handleNext">确认订单</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { CartItem } from '@/stores/cart'

interface Props {
  cartItems: CartItem[]
  shippingAddress: string
  totalAmount: number
}

interface Emits {
  (e: 'back'): void
  (e: 'next'): void
}

defineProps<Props>()
const emit = defineEmits<Emits>()

const totalItems = computed(() => {
  return (props: Props) => props.cartItems.reduce((sum, item) => sum + item.quantity, 0)
})

const handleBack = () => {
  emit('back')
}

const handleNext = () => {
  emit('next')
}
</script>

<style scoped>
.order-confirmation-container {
  max-width: 900px;
  margin: 0 auto;
}

.order-confirmation-container h2 {
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

.address-info {
  padding: 15px;
  background: white;
  border-radius: 4px;
  border-left: 4px solid #409eff;
}

.address-info p {
  margin: 0;
  color: #333;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.items-table {
  background: white;
  border-radius: 4px;
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 15px;
  padding: 15px 20px;
  background: #f5f5f5;
  border-bottom: 1px solid #e0e0e0;
  font-weight: bold;
  color: #333;
  font-size: 14px;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 15px;
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
  align-items: center;
}

.table-row:last-child {
  border-bottom: none;
}

.col-product {
  display: flex;
  align-items: center;
}

.product-info {
  display: flex;
  gap: 15px;
  align-items: flex-start;
  width: 100%;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  background: #f5f5f5;
}

.product-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.product-name {
  font-weight: bold;
  color: #333;
  font-size: 14px;
}

.product-specs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.spec-tag {
  background: #f0f0f0;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
}

.col-price,
.col-quantity,
.col-subtotal {
  display: flex;
  align-items: center;
  justify-content: center;
}

.price {
  font-weight: bold;
  color: #e74c3c;
  font-size: 14px;
}

.subtotal {
  font-weight: bold;
  color: #333;
  font-size: 14px;
}

.summary-section {
  background: #f9f9f9;
}

.summary-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
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
  .order-confirmation-container {
    max-width: 100%;
  }

  .table-header,
  .table-row {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .table-header {
    display: none;
  }

  .table-row {
    padding: 15px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    margin-bottom: 15px;
  }

  .col-product,
  .col-price,
  .col-quantity,
  .col-subtotal {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .col-product::before {
    content: '商品';
    font-weight: bold;
    color: #666;
  }

  .col-price::before {
    content: '单价';
    font-weight: bold;
    color: #666;
  }

  .col-quantity::before {
    content: '数量';
    font-weight: bold;
    color: #666;
  }

  .col-subtotal::before {
    content: '小计';
    font-weight: bold;
    color: #666;
  }

  .product-image {
    width: 60px;
    height: 60px;
  }
}
</style>
