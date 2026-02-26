<template>
  <div class="booking-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button @click="goBack" type="info" text>← 返回</el-button>
          <span>商品购买</span>
        </div>
      </template>

      <el-row :gutter="20">
        <!-- 左侧商品信息 -->
        <el-col :xs="24" :md="12">
          <div class="product-info">
            <div class="product-image">
              <el-image 
                :src="getFullImageUrl(product.imageUrl)" 
                fit="contain"
                style="width: 100%; height: 300px"
              />
            </div>
            <h3>{{ product.name }}</h3>
            <div class="info-item">
              <span class="label">分类：</span>
              <span class="value">{{ product.category || '未分类' }}</span>
            </div>
            <div class="info-item">
              <span class="label">单价：</span>
              <span class="value price">¥{{ product.price }}</span>
            </div>
            <div class="info-item">
              <span class="label">库存：</span>
              <span class="value" :class="{ 'out-of-stock': product.stock === 0 }">
                {{ product.stock }}
              </span>
            </div>
            <div class="info-item" v-if="product.isGuangzhouSpecial">
              <el-tag type="success">广州特色</el-tag>
            </div>
            <div class="info-item">
              <span class="label">描述：</span>
              <p class="description">{{ product.description }}</p>
            </div>
          </div>
        </el-col>

        <!-- 右侧购买表单 -->
        <el-col :xs="24" :md="12">
          <div class="purchase-form">
            <h3>购买信息</h3>
            <el-form :model="purchaseForm" label-width="100px">
              <!-- 购买数量 -->
              <el-form-item label="购买数量">
                <el-input-number
                  v-model="purchaseForm.quantity"
                  :min="1"
                  :max="product.stock || 1"
                  @change="calculateTotal"
                />
              </el-form-item>

              <!-- 小计 -->
              <el-form-item label="小计">
                <span class="total-price">¥{{ purchaseForm.totalPrice }}</span>
              </el-form-item>

              <!-- 提交按钮 -->
              <el-form-item>
                <el-button
                  type="primary"
                  size="large"
                  style="width: 100%"
                  @click="submitPurchase"
                  :loading="submitting"
                  :disabled="product.stock === 0"
                >
                  {{ product.stock === 0 ? '库存不足' : '确认购买' }}
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 购买成功对话框 -->
    <el-dialog v-model="showSuccessDialog" title="购买成功" width="500px">
      <div class="success-content">
        <el-icon class="success-icon">
          <SuccessFilled />
        </el-icon>
        <h3>购买成功</h3>
        <p>订单号：{{ successOrder.orderNumber }}</p>
        <p>总价格：¥{{ successOrder.totalPrice }}</p>
        <p>订单状态：{{ getStatusText(successOrder.status) }}</p>
      </div>
      <template #footer>
        <el-button type="primary" @click="goToOrders">查看订单</el-button>
        <el-button @click="goBack">返回商品详情</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { SuccessFilled } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const product = ref({})
const purchaseForm = ref({
  quantity: 1,
  totalPrice: 0
})
const submitting = ref(false)
const showSuccessDialog = ref(false)
const successOrder = ref({})

const API_BASE_URL = 'http://localhost:8080/api'

// 拼接完整的图片 URL 供前端显示
const getFullImageUrl = (url) => {
  if (!url) return ''
  // 如果已经是完整的网络图片地址（比如外链），直接返回
  if (url.startsWith('http')) return url 
  // 如果是相对路径（我们自己上传的），拼接上 Spring Boot 后端的地址
  return `http://localhost:8080/api${url}`
}

/**
 * 加载商品详情
 */
const loadProductDetail = async () => {
  try {
    const productId = route.params.id
    const response = await fetch(`${API_BASE_URL}/products/${productId}`)
    const data = await response.json()

    if (data.code === '0') {
      product.value = data.data
      calculateTotal()
    } else {
      ElMessage.error(data.message || '加载商品详情失败')
      setTimeout(() => {
        router.push('/products')
      }, 1500)
    }
  } catch (error) {
    ElMessage.error('加载商品详情失败: ' + error.message)
    setTimeout(() => {
      router.push('/products')
    }, 1500)
  }
}

/**
 * 计算总价
 */
const calculateTotal = () => {
  if (product.value.price && purchaseForm.value.quantity) {
    const total = parseFloat(product.value.price) * purchaseForm.value.quantity
    purchaseForm.value.totalPrice = total.toFixed(2)
  }
}

/**
 * 提交购买
 */
const submitPurchase = async () => {
  // 验证表单
  if (!purchaseForm.value.quantity || purchaseForm.value.quantity <= 0) {
    ElMessage.error('请输入有效的购买数量')
    return
  }

  if (purchaseForm.value.quantity > product.value.stock) {
    ElMessage.error('购买数量不能超过库存')
    return
  }

  // 获取用户信息
  const user = localStorage.getItem('user')
  if (!user) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }

  const userInfo = JSON.parse(user)

  submitting.value = true
  try {
    const response = await fetch(`${API_BASE_URL}/orders/products/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        userId: userInfo.id,
        productId: route.params.id,
        quantity: purchaseForm.value.quantity
      })
    })

    const data = await response.json()

    if (data.code === '0') {
      successOrder.value = data.data
      showSuccessDialog.value = true
      ElMessage.success('购买成功')
    } else {
      ElMessage.error(data.message || '购买失败')
    }
  } catch (error) {
    ElMessage.error('购买失败: ' + error.message)
  } finally {
    submitting.value = false
  }
}

/**
 * 获取状态文本
 */
const getStatusText = (status) => {
  const statusMap = {
    pending: '待确认',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

/**
 * 返回上一页
 */
const goBack = () => {
  router.back()
}

/**
 * 跳转到订单页面
 */
const goToOrders = () => {
  router.push('/orders')
}

// 页面加载时获取商品详情
onMounted(() => {
  loadProductDetail()
})
</script>

<style scoped>
.booking-container {
  padding: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: bold;
  color: #333;
}

.product-info {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.product-image {
  margin-bottom: 15px;
  border-radius: 4px;
  overflow: hidden;
  background-color: #fff;
}

.product-info h3 {
  margin: 15px 0;
  color: #333;
  font-size: 20px;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
  align-items: flex-start;
}

.info-item .label {
  font-weight: bold;
  color: #666;
  min-width: 80px;
}

.info-item .value {
  color: #333;
  flex: 1;
}

.info-item .price {
  color: #ff6b6b;
  font-size: 18px;
  font-weight: bold;
}

.info-item .out-of-stock {
  color: #ff6b6b;
  font-weight: bold;
}

.description {
  margin: 0;
  color: #666;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.purchase-form {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.purchase-form h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 18px;
}

.total-price {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b6b;
}

.success-content {
  text-align: center;
  padding: 20px;
}

.success-icon {
  font-size: 48px;
  color: #67c23a;
  margin-bottom: 10px;
}

.success-content h3 {
  margin: 10px 0;
  color: #333;
}

.success-content p {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
}
</style>
