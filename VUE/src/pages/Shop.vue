<template>
  <div class="shop-container">
    <!-- Header -->
    <div class="shop-header">
      <div class="header-content">
        <h1>周边商城</h1>
        <div class="header-actions">
          <el-badge :value="cartStore.totalItems" class="cart-badge">
            <el-button type="primary" @click="handleGoToCart">
              购物车
            </el-button>
          </el-badge>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="shop-content">
      <!-- Product List View -->
      <div v-if="!selectedProduct" class="product-list-view">
        <ProductList @select-product="handleSelectProduct" />
      </div>

      <!-- Product Detail View -->
      <div v-else class="product-detail-view">
        <ProductDetail
          :product-id="selectedProduct.id"
          @back="handleBackToList"
          @added-to-cart="handleProductAdded"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { Product } from '@/api/product'
import ProductList from '@/components/ProductList.vue'
import ProductDetail from '@/components/ProductDetail.vue'

const router = useRouter()
const cartStore = useCartStore()

const selectedProduct = ref<Product | null>(null)

const handleSelectProduct = (product: Product) => {
  selectedProduct.value = product
}

const handleBackToList = () => {
  selectedProduct.value = null
}

const handleProductAdded = () => {
  // Product was added to cart, can optionally show a message or navigate
}

const handleGoToCart = () => {
  router.push('/cart')
}

onMounted(() => {
  cartStore.loadFromStorage()
})
</script>

<style scoped>
.shop-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.shop-header {
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

.shop-header h1 {
  color: #333;
  margin: 0;
  font-size: 28px;
  font-weight: bold;
}

.header-actions {
  display: flex;
  gap: 15px;
  align-items: center;
}

.cart-badge :deep(.el-badge__content) {
  background-color: #f56c6c;
}

.shop-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.product-list-view {
  animation: fadeIn 0.3s ease;
}

.product-detail-view {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .shop-header h1 {
    font-size: 20px;
  }

  .shop-content {
    padding: 20px 15px;
  }
}
</style>
