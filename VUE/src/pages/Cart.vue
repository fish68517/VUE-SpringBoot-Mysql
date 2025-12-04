<template>
  <div class="cart-container">
    <!-- Header -->
    <div class="cart-header">
      <div class="header-content">
        <el-button type="primary" text icon="ArrowLeft" @click="handleBackToShop">返回商城</el-button>
        <h1>购物车</h1>
        <div style="width: 100px"></div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="cart-content">
      <!-- Empty State -->
      <div v-if="cartStore.items.length === 0" class="empty-state">
        <el-empty description="购物车为空" />
        <el-button type="primary" @click="handleBackToShop" style="margin-top: 20px">
          继续购物
        </el-button>
      </div>

      <!-- Cart Items -->
      <div v-else class="cart-items-section">
        <!-- Items Table -->
        <div class="items-table">
          <!-- Header Row -->
          <div class="table-header">
            <div class="col-product">商品</div>
            <div class="col-price">单价</div>
            <div class="col-quantity">数量</div>
            <div class="col-subtotal">小计</div>
            <div class="col-action">操作</div>
          </div>

          <!-- Item Rows -->
          <div v-for="item in cartStore.items" :key="item.id" class="table-row">
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
              <el-input-number
                :model-value="item.quantity"
                :min="1"
                size="small"
                @change="(val: number | string) => handleUpdateQuantity(item.id, typeof val === 'number' ? val : parseInt(val))"
              />
            </div>

            <div class="col-subtotal">
              <span class="subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>

            <div class="col-action">
              <el-button type="danger" text size="small" @click="handleRemoveItem(item.id)">
                删除
              </el-button>
            </div>
          </div>
        </div>

        <!-- Summary Section -->
        <div class="summary-section">
          <div class="summary-content">
            <div class="summary-row">
              <span class="label">商品数量：</span>
              <span class="value">{{ cartStore.totalItems }} 件</span>
            </div>

            <div class="summary-row">
              <span class="label">商品总价：</span>
              <span class="value">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
            </div>

            <div class="summary-row total">
              <span class="label">应付金额：</span>
              <span class="value">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
            </div>

            <div class="action-buttons">
              <el-button @click="handleBackToShop">继续购物</el-button>
              <el-button type="primary" @click="handleCheckout">
                去结算
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const cartStore = useCartStore()

const handleBackToShop = () => {
  router.push('/shop')
}

const handleUpdateQuantity = (itemId: string, quantity: number) => {
  if (quantity <= 0) {
    ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(() => {
        cartStore.removeItem(itemId)
        ElMessage.success('商品已删除')
      })
      .catch(() => {
        // User cancelled
      })
  } else {
    cartStore.updateQuantity(itemId, quantity)
    ElMessage.success('数量已更新')
  }
}

const handleRemoveItem = (itemId: string) => {
  ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      cartStore.removeItem(itemId)
      ElMessage.success('商品已删除')
    })
    .catch(() => {
      // User cancelled
    })
}

const handleCheckout = () => {
  if (cartStore.items.length === 0) {
    ElMessage.warning('购物车为空，请先添加商品')
    return
  }

  router.push('/checkout')
}
</script>

<style scoped>
.cart-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.cart-header {
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

.cart-header h1 {
  color: #333;
  margin: 0;
  font-size: 28px;
  font-weight: bold;
}

.cart-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.empty-state {
  background: white;
  border-radius: 8px;
  padding: 60px 20px;
  text-align: center;
}

.cart-items-section {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 20px;
}

.items-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 0.8fr;
  gap: 15px;
  padding: 15px 20px;
  background: #f9f9f9;
  border-bottom: 1px solid #e0e0e0;
  font-weight: bold;
  color: #333;
  font-size: 14px;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 0.8fr;
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
.col-subtotal,
.col-action {
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
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  height: fit-content;
  position: sticky;
  top: 100px;
}

.summary-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
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
  flex-direction: column;
  gap: 10px;
  margin-top: 10px;
}

.action-buttons :deep(.el-button) {
  width: 100%;
}

@media (max-width: 1024px) {
  .cart-items-section {
    grid-template-columns: 1fr;
  }

  .summary-section {
    position: static;
  }

  .table-header,
  .table-row {
    grid-template-columns: 1.5fr 1fr 1fr 1fr 0.8fr;
  }
}

@media (max-width: 768px) {
  .cart-header h1 {
    font-size: 20px;
  }

  .cart-content {
    padding: 20px 15px;
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
  .col-subtotal,
  .col-action {
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

  .col-action::before {
    content: '操作';
    font-weight: bold;
    color: #666;
  }

  .product-image {
    width: 60px;
    height: 60px;
  }

  .summary-section {
    margin-top: 20px;
  }
}
</style>
