<template>
  <div class="booking-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button @click="goBack" type="info" text>← 返回</el-button>
          <span>景点门票预订</span>
        </div>
      </template>

      <el-row :gutter="20">
        <!-- 左侧景点信息 -->
        <el-col :xs="24" :md="12">
          <div class="attraction-info">
            <h3>{{ attraction.name }}</h3>
            <div class="info-item">
              <span class="label">位置：</span>
              <span class="value">{{ attraction.location || '未提供' }}</span>
            </div>
            <div class="info-item">
              <span class="label">门票价格：</span>
              <span class="value price">¥{{ attraction.ticketPrice }}</span>
            </div>
            <div class="info-item">
              <span class="label">营业时间：</span>
              <span class="value">{{ attraction.openingHours || '未提供' }}</span>
            </div>
            <div class="info-item" v-if="attraction.isGuangzhouSpecial">
              <el-tag type="success">广州特色景点</el-tag>
            </div>
          </div>
        </el-col>

        <!-- 右侧预订表单 -->
        <el-col :xs="24" :md="12">
          <div class="booking-form">
            <h3>预订信息</h3>
            <el-form :model="bookingForm" label-width="100px">
              <!-- 购票数量 -->
              <el-form-item label="购票数量">
                <el-input-number
                  v-model="bookingForm.quantity"
                  :min="1"
                  :max="10"
                  @change="calculateTotal"
                />
              </el-form-item>

              <!-- 访问日期 -->
              <el-form-item label="访问日期">
                <el-date-picker
                  v-model="bookingForm.visitDate"
                  type="date"
                  placeholder="选择访问日期"
                  :disabledDate="disabledDate"
                />
              </el-form-item>

              <!-- 小计 -->
              <el-form-item label="小计">
                <span class="total-price">¥{{ bookingForm.totalPrice }}</span>
              </el-form-item>

              <!-- 提交按钮 -->
              <el-form-item>
                <el-button
                  type="primary"
                  size="large"
                  style="width: 100%"
                  @click="submitBooking"
                  :loading="submitting"
                >
                  确认预订
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 预订成功对话框 -->
    <el-dialog v-model="showSuccessDialog" title="预订成功" width="500px">
      <div class="success-content">
        <el-icon class="success-icon">
          <SuccessFilled />
        </el-icon>
        <h3>预订成功</h3>
        <p>订单号：{{ successOrder.orderNumber }}</p>
        <p>总价格：¥{{ successOrder.totalPrice }}</p>
        <p>订单状态：{{ getStatusText(successOrder.status) }}</p>
      </div>
      <template #footer>
        <el-button type="primary" @click="goToOrders">查看订单</el-button>
        <el-button @click="goBack">返回景点详情</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { SuccessFilled } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const attraction = ref({})
const bookingForm = ref({
  quantity: 1,
  visitDate: null,
  totalPrice: 0
})
const submitting = ref(false)
const showSuccessDialog = ref(false)
const successOrder = ref({})

const API_BASE_URL = 'http://localhost:8080'

/**
 * 加载景点详情
 */
const loadAttractionDetail = async () => {
  try {
    const attractionId = route.params.id
    const response = await fetch(`${API_BASE_URL}/attractions/${attractionId}`)
    const data = await response.json()

    if (data.code === 0) {
      attraction.value = data.data
      calculateTotal()
    } else {
      ElMessage.error(data.message || '加载景点详情失败')
      setTimeout(() => {
        router.push('/attractions')
      }, 1500)
    }
  } catch (error) {
    ElMessage.error('加载景点详情失败: ' + error.message)
    setTimeout(() => {
      router.push('/attractions')
    }, 1500)
  }
}

/**
 * 计算总价
 */
const calculateTotal = () => {
  if (attraction.value.ticketPrice && bookingForm.value.quantity) {
    const total = parseFloat(attraction.value.ticketPrice) * bookingForm.value.quantity
    bookingForm.value.totalPrice = total.toFixed(2)
  }
}

/**
 * 禁用过去的日期
 */
const disabledDate = (time) => {
  return time.getTime() < new Date().setHours(0, 0, 0, 0)
}

/**
 * 提交预订
 */
const submitBooking = async () => {
  // 验证表单
  if (!bookingForm.value.quantity || bookingForm.value.quantity <= 0) {
    ElMessage.error('请输入有效的购票数量')
    return
  }

  if (!bookingForm.value.visitDate) {
    ElMessage.error('请选择访问日期')
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
    const response = await fetch(`${API_BASE_URL}/orders/attractions/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        userId: userInfo.id,
        attractionId: route.params.id,
        quantity: bookingForm.value.quantity,
        visitDate: formatDate(bookingForm.value.visitDate)
      })
    })

    const data = await response.json()

    if (data.code === 0) {
      successOrder.value = data.data
      showSuccessDialog.value = true
      ElMessage.success('预订成功')
    } else {
      ElMessage.error(data.message || '预订失败')
    }
  } catch (error) {
    ElMessage.error('预订失败: ' + error.message)
  } finally {
    submitting.value = false
  }
}

/**
 * 格式化日期
 */
const formatDate = (date) => {
  if (!date) return null
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
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

// 页面加载时获取景点详情
onMounted(() => {
  loadAttractionDetail()
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

.attraction-info {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.attraction-info h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 20px;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
  align-items: center;
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

.booking-form {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.booking-form h3 {
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
