<template>
  <div class="product-detail-container">
    <div class="detail-header">
      <el-button type="primary" text icon="ArrowLeft" @click="handleBack">返回</el-button>
    </div>

    <div v-if="product" class="detail-content">
      <!-- Image Gallery -->
      <div class="image-section">
        <div class="main-image">
          <img :src="currentImage" :alt="product.name" />
        </div>
        <div v-if="product.images && product.images.length > 1" class="thumbnail-gallery">
          <div
            v-for="(image, index) in product.images"
            :key="index"
            :class="['thumbnail', { active: currentImage === image }]"
            @click="currentImage = image"
          >
            <img :src="image" :alt="`${product.name} ${index + 1}`" />
          </div>
        </div>
      </div>

      <!-- Product Info -->
      <div class="info-section">
        <h1 class="product-title">{{ product.name }}</h1>

        <div class="price-section">
          <span class="current-price">¥{{ product.currentPrice }}</span>
          <span v-if="product.originalPrice > product.currentPrice" class="original-price">
            ¥{{ product.originalPrice }}
          </span>
          <span v-if="product.originalPrice > product.currentPrice" class="discount">
            {{ Math.round(((product.originalPrice - product.currentPrice) / product.originalPrice) * 100) }}% OFF
          </span>
        </div>

        <div class="stock-section">
          <span v-if="product.stock > 0" class="in-stock">库存充足（{{ product.stock }}件）</span>
          <span v-else class="out-of-stock">已售罄</span>
        </div>

        <div class="description-section">
          <h3>商品描述</h3>
          <p>{{ product.description }}</p>
        </div>

        <!-- Specs Selection -->
        <div v-if="product.specs && Object.keys(product.specs).length > 0" class="specs-section">
          <h3>规格选择</h3>
          <div v-for="(values, specName) in product.specs" :key="specName" class="spec-group">
            <label>{{ specName }}：</label>
            <div class="spec-options">
              <el-button
                v-for="value in values"
                :key="value"
                :type="selectedSpecs[specName] === value ? 'primary' : 'default'"
                size="small"
                @click="selectedSpecs[specName] = value"
              >
                {{ value }}
              </el-button>
            </div>
          </div>
        </div>

        <!-- Quantity Selection -->
        <div class="quantity-section">
          <label>数量：</label>
          <el-input-number
            v-model="quantity"
            :min="1"
            :max="product.stock"
            size="large"
          />
        </div>

        <!-- Action Buttons -->
        <div class="action-buttons">
          <el-button
            type="primary"
            size="large"
            :disabled="product.stock === 0"
            @click="handleAddToCart"
          >
            加入购物车
          </el-button>
        </div>
      </div>
    </div>

    <div v-else class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Product, productApi } from '@/api/product'
import { useCartStore } from '@/stores/cart'

const props = defineProps<{
  productId: number
}>()

const emit = defineEmits<{
  back: []
  addedToCart: [product: Product]
}>()

const cartStore = useCartStore()
const product = ref<Product | null>(null)
const currentImage = ref('')
const quantity = ref(1)
const selectedSpecs = ref<Record<string, string>>({})

const loadProduct = async () => {
  try {
    const response = await productApi.getProductById(props.productId)
    const data = (response.data as unknown as Product) || null
    product.value = data
    if (product.value && product.value.images && product.value.images.length > 0) {
      currentImage.value = product.value.images[0]
    }
    // Initialize selected specs with first option
    if (product.value && product.value.specs) {
      Object.entries(product.value.specs).forEach(([key, values]) => {
        if (Array.isArray(values) && values.length > 0) {
          selectedSpecs.value[key] = values[0]
        }
      })
    }
  } catch (error) {
    ElMessage.error('加载商品详情失败')
    console.error(error)
  }
}

const handleAddToCart = () => {
  if (!product.value) return

  // Validate specs selection
  if (product.value.specs && Object.keys(product.value.specs).length > 0) {
    const allSpecsSelected = Object.keys(product.value.specs).every((key) => selectedSpecs.value[key])
    if (!allSpecsSelected) {
      ElMessage.warning('请选择所有规格')
      return
    }
  }

  // Add to cart
  for (let i = 0; i < quantity.value; i++) {
    cartStore.addItem(product.value, selectedSpecs.value)
  }

  ElMessage.success(`已添加 ${quantity.value} 件商品到购物车`)
  emit('addedToCart', product.value)
  quantity.value = 1
}

const handleBack = () => {
  emit('back')
}

onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.product-detail-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.detail-header {
  margin-bottom: 20px;
}

.detail-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
}

.image-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.main-image {
  width: 100%;
  aspect-ratio: 1;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-gallery {
  display: flex;
  gap: 10px;
  overflow-x: auto;
}

.thumbnail {
  width: 80px;
  height: 80px;
  border: 2px solid transparent;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail.active {
  border-color: #409eff;
}

.thumbnail:hover {
  border-color: #409eff;
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.price-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.current-price {
  font-size: 32px;
  font-weight: bold;
  color: #e74c3c;
}

.original-price {
  font-size: 18px;
  color: #999;
  text-decoration: line-through;
}

.discount {
  background: #e74c3c;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.stock-section {
  font-size: 14px;
}

.in-stock {
  color: #67c23a;
  font-weight: bold;
}

.out-of-stock {
  color: #f56c6c;
  font-weight: bold;
}

.description-section {
  padding: 15px;
  background: #f9f9f9;
  border-radius: 8px;
}

.description-section h3 {
  margin: 0 0 10px 0;
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.description-section p {
  margin: 0;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.specs-section {
  padding: 15px;
  background: #f9f9f9;
  border-radius: 8px;
}

.specs-section h3 {
  margin: 0 0 15px 0;
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.spec-group {
  margin-bottom: 15px;
}

.spec-group label {
  display: block;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
  font-size: 14px;
}

.spec-options {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.quantity-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.quantity-section label {
  font-weight: bold;
  color: #333;
  white-space: nowrap;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.action-buttons :deep(.el-button) {
  flex: 1;
}

.loading-state {
  padding: 40px 20px;
}

@media (max-width: 768px) {
  .detail-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .product-title {
    font-size: 20px;
  }

  .current-price {
    font-size: 24px;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>
