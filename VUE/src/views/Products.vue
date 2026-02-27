<template>
  <div class="products-container">
    <Card title="农资产品">
      <el-row :gutter="20" class="filter-row">
        <el-col :xs="24" :sm="12" :md="6">
          <el-input
            v-model="filters.productName"
            placeholder="请输入产品名称"
            @input="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-select
            v-model="filters.category"
            placeholder="选择类别"
            @change="handleSearch"
          >
            <el-option label="全部" value="" />
            <el-option label="肥料" value="肥料" />
            <el-option label="农药" value="农药" />
            <el-option label="种子" value="种子" />
            <el-option label="防护用品" value="防护用品" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-input
            v-model.number="filters.minPrice"
            type="number"
            placeholder="最低价格"
            @input="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-input
            v-model.number="filters.maxPrice"
            type="number"
            placeholder="最高价格"
            @input="handleSearch"
          />
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col
          v-for="product in products"
          :key="product.id"
          :xs="24"
          :sm="12"
          :md="8"
        >
          <div class="product-card">
            <h3>{{ product.productName }}</h3>
            <p class="category">{{ product.category }}</p>
            <p class="description">{{ product.description }}</p>
            <div class="product-info">
              <span class="price">¥{{ product.price }}</span>
              <span class="stock">库存: {{ product.stock }}</span>
            </div>
            <el-button
              type="primary"
              size="small"
              @click="handleAddToCart(product)"
              :disabled="product.stock === 0"
            >
              {{ product.stock > 0 ? '加入购物车' : '缺货' }}
            </el-button>
          </div>
        </el-col>
      </el-row>

      <el-pagination
        v-if="pagination.total > 0"
        :current-page="pagination.current"
        :page-size="pagination.size"
        :page-sizes="[12, 24, 36]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-page-change="handlePageChange"
        @page-size-change="handlePageSizeChange"
      />
    </Card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { productAPI } from '@/api/product'
import Card from '@/components/common/Card.vue'

const filters = ref({
  productName: '',
  category: '',
  minPrice: null,
  maxPrice: null,
})

const products = ref([])
const pagination = ref({
  current: 1,
  size: 12,
  total: 0,
})

const handleSearch = async () => {
  try {
    const response = await productAPI.getProducts({
      productName: filters.value.productName,
      category: filters.value.category,
      minPrice: filters.value.minPrice,
      maxPrice: filters.value.maxPrice,
      page: pagination.value.current,
      size: pagination.value.size,
    })
    products.value = response || []
    pagination.value.total = response|| 0
  } catch (error) {
    ElMessage.error('获取产品列表失败')
  }
}

const handlePageChange = (page) => {
  pagination.value.current = page
  handleSearch()
}

const handlePageSizeChange = (size) => {
  pagination.value.size = size
  pagination.value.current = 1
  handleSearch()
}

const handleAddToCart = (product) => {
  ElMessage.success(`已添加 ${product.productName} 到购物车`)
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.products-container {
  padding: 20px;
}

.filter-row {
  margin-bottom: 20px;
}

.product-card {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 15px;
  background-color: #f5f7fa;
  transition: all 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.product-card h3 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
}

.product-card .category {
  margin: 5px 0;
  color: #909399;
  font-size: 12px;
}

.product-card .description {
  margin: 5px 0;
  color: #606266;
  font-size: 13px;
  flex-grow: 1;
}

.product-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 10px 0;
  padding: 10px 0;
  border-top: 1px solid #dcdfe6;
  border-bottom: 1px solid #dcdfe6;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.stock {
  font-size: 12px;
  color: #909399;
}
</style>
