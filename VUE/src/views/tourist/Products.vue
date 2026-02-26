<template>
  <div class="products-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>旅游商品</span>
        </div>
      </template>
      
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :xs="24" :sm="12" :md="6">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="搜索商品"
            @keyup.enter="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-select 
            v-model="searchForm.category" 
            placeholder="选择分类"
            clearable
            @change="handleSearch"
          >
            <el-option label="美食" value="美食" />
            <el-option label="手工艺品" value="手工艺品" />
            <el-option label="纪念品" value="纪念品" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="product in products" :key="product.id">
          <el-card class="product-card" @click="goToDetail(product.id)">
            <div class="product-image">
              <el-image 
                :src="product.imageUrl" 
                fit="cover"
                style="width: 100%; height: 200px"
              />
            </div>
            <h3>{{ product.name }}</h3>
            <p class="description">{{ product.description }}</p>
            <div class="stock" :class="{ 'out-of-stock': product.stock === 0 }">
              库存: {{ product.stock }}
            </div>
            <div class="price">¥{{ product.price }}</div>
            <el-button type="primary" size="small" style="width: 100%">
              查看详情
            </el-button>
          </el-card>
        </el-col>
      </el-row>

      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 30, 40]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; text-align: center"
        @current-change="handlePageChange"
        @size-change="handlePageSizeChange"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const searchForm = ref({
  keyword: '',
  category: ''
})

const products = ref([])

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const loading = ref(false)

const API_BASE_URL = 'http://localhost:8080/api'

/**
 * 获取商品列表
 */
const fetchProducts = async () => {
  loading.value = true
  try {
    let url = `${API_BASE_URL}/products/list?page=${pagination.value.currentPage - 1}&size=${pagination.value.pageSize}`
    
    if (searchForm.value.keyword) {
      url = `${API_BASE_URL}/products/search?keyword=${searchForm.value.keyword}&page=${pagination.value.currentPage - 1}&size=${pagination.value.pageSize}`
    } else if (searchForm.value.category) {
      url = `${API_BASE_URL}/products/by-category?category=${searchForm.value.category}&page=${pagination.value.currentPage - 1}&size=${pagination.value.pageSize}`
    }
    
    const response = await fetch(url)
    const data = await response.json()
    
    if (data.code === '0') {
      products.value = data.data.products
      pagination.value.total = data.data.total
    } else {
      ElMessage.error(data.message || '获取商品列表失败')
    }
  } catch (error) {
    ElMessage.error('获取商品列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索商品
 */
const handleSearch = () => {
  pagination.value.currentPage = 1
  fetchProducts()
}

/**
 * 重置搜索
 */
const handleReset = () => {
  searchForm.value.keyword = ''
  searchForm.value.category = ''
  pagination.value.currentPage = 1
  fetchProducts()
}

/**
 * 页码变化
 */
const handlePageChange = () => {
  fetchProducts()
}

/**
 * 每页数量变化
 */
const handlePageSizeChange = () => {
  pagination.value.currentPage = 1
  fetchProducts()
}

/**
 * 跳转到商品详情页
 */
const goToDetail = (productId) => {
  router.push(`/product/${productId}`)
}

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.products-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
}

.product-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.product-image {
  margin-bottom: 10px;
  border-radius: 4px;
  overflow: hidden;
}

.product-card h3 {
  margin: 10px 0;
  color: #333;
  font-size: 16px;
}

.description {
  color: #666;
  font-size: 14px;
  margin: 5px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.stock {
  color: #999;
  font-size: 12px;
  margin: 5px 0;
}

.stock.out-of-stock {
  color: #ff6b6b;
  font-weight: bold;
}

.price {
  color: #ff6b6b;
  font-size: 18px;
  font-weight: bold;
  margin: 10px 0;
}

/* Tablet screens */
@media (max-width: 1024px) {
  .products-container {
    padding: 15px;
  }

  .product-card h3 {
    font-size: 15px;
  }

  .description {
    font-size: 13px;
  }

  .stock {
    font-size: 11px;
  }

  .price {
    font-size: 16px;
  }
}

/* Tablet and smaller */
@media (max-width: 768px) {
  .products-container {
    padding: 12px;
  }

  .card-header {
    font-size: 14px;
  }

  .product-card h3 {
    font-size: 14px;
    margin: 8px 0;
  }

  .description {
    font-size: 12px;
    margin: 4px 0;
  }

  .stock {
    font-size: 11px;
    margin: 4px 0;
  }

  .price {
    font-size: 15px;
    margin: 8px 0;
  }
}

/* Mobile devices */
@media (max-width: 480px) {
  .products-container {
    padding: 10px;
  }

  .card-header {
    font-size: 13px;
  }

  .product-card h3 {
    font-size: 13px;
    margin: 6px 0;
  }

  .description {
    font-size: 11px;
    margin: 3px 0;
  }

  .stock {
    font-size: 10px;
    margin: 3px 0;
  }

  .price {
    font-size: 14px;
    margin: 6px 0;
  }
}

/* Extra small devices */
@media (max-width: 360px) {
  .products-container {
    padding: 8px;
  }

  .product-card h3 {
    font-size: 12px;
  }

  .description {
    font-size: 10px;
  }

  .stock {
    font-size: 9px;
  }

  .price {
    font-size: 13px;
  }
}
</style>
