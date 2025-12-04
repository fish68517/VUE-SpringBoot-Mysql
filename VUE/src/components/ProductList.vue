<template>
  <div class="product-list-container">
    <!-- Filters Section -->
    <div class="filters-section">
      <div class="filter-group">
        <label>分类：</label>
        <el-select v-model="selectedCategoryId" placeholder="全部分类" clearable @change="handleCategoryChange">
          <el-option label="全部分类" :value="null" />
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
      </div>

      <div class="filter-group">
        <label>搜索：</label>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品名称"
          clearable
          @input="handleSearch"
        />
      </div>
    </div>

    <!-- Products Grid -->
    <div v-if="!loading" class="products-grid">
      <div v-if="filteredProducts.length === 0" class="empty-state">
        <p>暂无商品</p>
      </div>

      <div v-for="product in filteredProducts" :key="product.id" class="product-card">
        <div class="product-image">
          <img :src="product.images?.[0] || 'https://via.placeholder.com/200x200'" :alt="product.name" />
          <div v-if="product.stock === 0" class="sold-out-badge">已售罄</div>
        </div>

        <div class="product-info">
          <h3 class="product-name">{{ product.name }}</h3>
          <p class="product-description">{{ product.description }}</p>

          <div class="product-price">
            <span class="current-price">¥{{ product.currentPrice }}</span>
            <span v-if="product.originalPrice > product.currentPrice" class="original-price">
              ¥{{ product.originalPrice }}
            </span>
          </div>

          <div class="product-stock">
            <span v-if="product.stock > 0" class="in-stock">库存：{{ product.stock }}</span>
            <span v-else class="out-of-stock">已售罄</span>
          </div>

          <div class="product-actions">
            <el-button
              type="primary"
              size="small"
              :disabled="product.stock === 0"
              @click="handleViewDetail(product)"
            >
              查看详情
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-else class="loading-state">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- Pagination -->
    <div v-if="!loading && totalPages > 1" class="pagination-section">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="totalElements"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { productApi, Product, ProductCategory, PageResponse } from '@/api/product'

const emit = defineEmits<{
  selectProduct: [product: Product]
}>()

const loading = ref(false)
const categories = ref<ProductCategory[]>([])
const products = ref<Product[]>([])
const selectedCategoryId = ref<number | null>(null)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalElements = ref(0)
const totalPages = ref(0)

const filteredProducts = computed(() => {
  if (!searchKeyword.value) {
    return products.value
  }
  return products.value.filter((p) => p.name.toLowerCase().includes(searchKeyword.value.toLowerCase()))
})

const loadCategories = async () => {
  try {
    const response = await productApi.getCategories()
    const data = (response.data as unknown as ProductCategory[]) || []
    categories.value = data
  } catch (error) {
    ElMessage.error('加载分类失败')
    console.error(error)
  }
}

const loadProducts = async () => {
  try {
    loading.value = true
    const response = await productApi.getProducts(currentPage.value - 1, pageSize.value, selectedCategoryId.value || undefined)
    const pageData = (response.data as unknown as PageResponse<Product>) || {}
    products.value = pageData?.content || []
    totalElements.value = pageData?.totalElements || 0
    totalPages.value = pageData?.totalPages || 0
  } catch (error) {
    ElMessage.error('加载商品失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleCategoryChange = () => {
  currentPage.value = 1
  loadProducts()
}

const handleSearch = () => {
  // Search is done via computed property, no need to reload
}

const handlePageChange = () => {
  loadProducts()
}

const handleViewDetail = (product: Product) => {
  emit('selectProduct', product)
}

onMounted(() => {
  loadCategories()
  loadProducts()
})
</script>

<style scoped>
.product-list-container {
  width: 100%;
}

.filters-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  align-items: center;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-group label {
  font-weight: bold;
  color: #333;
  white-space: nowrap;
}

.filter-group :deep(.el-select) {
  width: 150px;
}

.filter-group :deep(.el-input) {
  width: 200px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.product-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-4px);
}

.product-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f5f5f5;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.sold-out-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-description {
  font-size: 12px;
  color: #999;
  margin: 0 0 10px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  height: 24px;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.current-price {
  font-size: 16px;
  font-weight: bold;
  color: #e74c3c;
}

.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
}

.product-stock {
  font-size: 12px;
  margin-bottom: 10px;
}

.in-stock {
  color: #67c23a;
}

.out-of-stock {
  color: #f56c6c;
}

.product-actions {
  display: flex;
  gap: 8px;
}

.product-actions :deep(.el-button) {
  flex: 1;
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.loading-state {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .filters-section {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-group {
    width: 100%;
  }

  .filter-group :deep(.el-select),
  .filter-group :deep(.el-input) {
    width: 100%;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
  }
}
</style>
