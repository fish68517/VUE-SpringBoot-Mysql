<template>
  <div class="product-detail-container">
    <el-card v-if="product">
      <template #header>
        <div class="card-header">
          <el-button @click="goBack" type="info" text>← 返回</el-button>
          <span>商品详情</span>
        </div>
      </template>

      <el-row :gutter="30">
        <el-col :xs="24" :md="12">
          <div class="product-image-container">
            <el-image 
              :src="getFullImageUrl(product.imageUrl)" 
              fit="contain"
              style="width: 100%; height: 400px"
            />
          </div>
        </el-col>

        <el-col :xs="24" :md="12">
          <div class="product-info">
            <h1>{{ product.name }}</h1>
            
            <div class="info-item">
              <span class="label">分类:</span>
              <span class="value">{{ product.category || '未分类' }}</span>
            </div>

            <div class="info-item" v-if="product.isGuangzhouSpecial">
              <el-tag type="success">广州特色</el-tag>
            </div>

            <div class="price-section">
              <div class="price">¥{{ product.price }}</div>
              <div class="stock" :class="{ 'out-of-stock': product.stock === 0 }">
                库存: {{ product.stock }}
              </div>
            </div>

            <div class="info-item">
              <span class="label">商品描述:</span>
              <p class="description">{{ product.description }}</p>
            </div>

            <div class="action-buttons">
              <el-button 
                type="primary" 
                size="large"
                :disabled="product.stock === 0"
                @click="handleBuyNow"
              >
                {{ product.stock === 0 ? '库存不足' : '立即购买' }}
              </el-button>
              <!-- <el-button 
                size="large"
                @click="handleToggleFavorite"
              >
                {{ isFavorited ? '取消收藏' : '收藏' }}
              </el-button> -->
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card v-else style="margin-top: 20px">
      <el-empty description="商品不存在" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const product = ref(null)
const isFavorited = ref(false)
const loading = ref(false)

// 拼接完整的图片 URL 供前端显示
const getFullImageUrl = (url) => {
  if (!url) return ''
  // 如果已经是完整的网络图片地址（比如外链），直接返回
  if (url.startsWith('http')) return url 
  // 如果是相对路径（我们自己上传的），拼接上 Spring Boot 后端的地址
  return `http://localhost:8080/api${url}`
}

const API_BASE_URL = 'http://localhost:8080/api'

/**
 * 获取商品详情
 */
const fetchProductDetail = async () => {
  loading.value = true
  try {
    const productId = route.params.id
    const response = await fetch(`${API_BASE_URL}/products/${productId}`)
    const data = await response.json()
    
    if (data.code === '0') {
      product.value = data.data
    } else {
      ElMessage.error(data.message || '获取商品详情失败')
    }
  } catch (error) {
    ElMessage.error('获取商品详情失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

/**
 * 返回上一页
 */
const goBack = () => {
  router.back()
}

/**
 * 立即购买
 */
const handleBuyNow = () => {
  router.push(`/product/${route.params.id}/booking`)
}

/**
 * 切换收藏状态
 */
const handleToggleFavorite = () => {
  isFavorited.value = !isFavorited.value
  const message = isFavorited.value ? '已收藏' : '已取消收藏'
  ElMessage.success(message)
}

onMounted(() => {
  fetchProductDetail()
})
</script>

<style scoped>
.product-detail-container {
  padding: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: bold;
  color: #333;
}

.product-image-container {
  border-radius: 4px;
  overflow: hidden;
  background-color: #f5f5f5;
}

.product-info {
  padding: 20px;
}

.product-info h1 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 28px;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.label {
  font-weight: bold;
  color: #666;
  min-width: 80px;
}

.value {
  color: #333;
}

.description {
  margin: 0;
  color: #666;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.price-section {
  margin: 20px 0;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.price {
  font-size: 32px;
  color: #ff6b6b;
  font-weight: bold;
  margin-bottom: 10px;
}

.stock {
  font-size: 14px;
  color: #666;
}

.stock.out-of-stock {
  color: #ff6b6b;
  font-weight: bold;
}

.action-buttons {
  display: flex;
  gap: 10px;
  margin-top: 30px;
}

.action-buttons :deep(.el-button) {
  flex: 1;
}
</style>
