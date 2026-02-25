<template>
  <div class="booking-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button @click="goBack" type="info" text>← 返回</el-button>
          <span>酒店预订</span>
        </div>
      </template>

      <el-row :gutter="20">
        <!-- 左侧酒店信息 -->
        <el-col :xs="24" :md="12">
          <div class="hotel-info">
            <h3>{{ hotel.name }}</h3>
            <div class="info-item">
              <span class="label">位置：</span>
              <span class="value">{{ hotel.location || '未提供' }}</span>
            </div>
            <div class="info-item">
              <span class="label">评分：</span>
              <el-rate v-model="hotel.rating" disabled size="small" />
              <span class="rating-value">{{ hotel.rating }}</span>
            </div>
            <div class="info-item">
              <span class="label">描述：</span>
              <p>{{ hotel.description }}</p>
            </div>
            
            <div class="rooms-section">
              <h4>房间类型</h4>
              <div v-for="room in hotel.rooms" :key="room.id" class="room-card">
                <div class="room-type">{{ room.roomType }}</div>
                <div class="room-price">¥{{ room.pricePerNight }}/晚</div>
                <div class="room-available">可用: {{ room.quantity }}间</div>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 右侧预订表单 -->
        <el-col :xs="24" :md="12">
          <div class="booking-form">
            <h3>预订信息</h3>
            <el-form :model="bookingForm" label-width="100px">
              <!-- 房间类型 -->
              <el-form-item label="房间类型">
                <el-select
                  v-model="bookingForm.roomType"
                  placeholder="选择房间类型"
                  @change="onRoomTypeChange"
                >
                  <el-option
                    v-for="room in hotel.rooms"
                    :key="room.id"
                    :label="room.roomType"
                    :value="room.roomType"
                  />
                </el-select>
              </el-form-item>

              <!-- 入住日期 -->
              <el-form-item label="入住日期">
                <el-date-picker
                  v-model="bookingForm.checkInDate"
                  type="date"
                  placeholder="选择入住日期"
                  :disabledDate="disabledDate"
                  @change="calculateTotal"
                />
              </el-form-item>

              <!-- 退房日期 -->
              <el-form-item label="退房日期">
                <el-date-picker
                  v-model="bookingForm.checkOutDate"
                  type="date"
                  placeholder="选择退房日期"
                  :disabledDate="disabledCheckOutDate"
                  @change="calculateTotal"
                />
              </el-form-item>

              <!-- 房间数量 -->
              <el-form-item label="房间数量">
                <el-input-number
                  v-model="bookingForm.quantity"
                  :min="1"
                  :max="maxRoomQuantity"
                  @change="calculateTotal"
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
        <el-button @click="goBack">返回酒店详情</el-button>
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

const hotel = ref({
  rooms: []
})
const bookingForm = ref({
  roomType: '',
  checkInDate: null,
  checkOutDate: null,
  quantity: 1,
  totalPrice: 0
})
const submitting = ref(false)
const showSuccessDialog = ref(false)
const successOrder = ref({})

const API_BASE_URL = 'http://localhost:8080'

const maxRoomQuantity = computed(() => {
  if (!bookingForm.value.roomType) return 1
  const room = hotel.value.rooms.find(r => r.roomType === bookingForm.value.roomType)
  return room ? room.quantity : 1
})

/**
 * 加载酒店详情
 */
const loadHotelDetail = async () => {
  try {
    const hotelId = route.params.id
    const response = await fetch(`${API_BASE_URL}/hotels/${hotelId}`)
    const data = await response.json()

    if (data.code === 0) {
      hotel.value = data.data
    } else {
      ElMessage.error(data.message || '加载酒店详情失败')
      setTimeout(() => {
        router.push('/hotels')
      }, 1500)
    }
  } catch (error) {
    ElMessage.error('加载酒店详情失败: ' + error.message)
    setTimeout(() => {
      router.push('/hotels')
    }, 1500)
  }
}

/**
 * 房间类型变更时的处理
 */
const onRoomTypeChange = () => {
  bookingForm.value.quantity = 1
  calculateTotal()
}

/**
 * 计算总价
 */
const calculateTotal = () => {
  if (!bookingForm.value.roomType || !bookingForm.value.checkInDate || !bookingForm.value.checkOutDate) {
    bookingForm.value.totalPrice = 0
    return
  }

  const room = hotel.value.rooms.find(r => r.roomType === bookingForm.value.roomType)
  if (!room) {
    bookingForm.value.totalPrice = 0
    return
  }

  const checkIn = new Date(bookingForm.value.checkInDate)
  const checkOut = new Date(bookingForm.value.checkOutDate)
  const nights = Math.ceil((checkOut - checkIn) / (1000 * 60 * 60 * 24))

  if (nights <= 0) {
    bookingForm.value.totalPrice = 0
    return
  }

  const total = parseFloat(room.pricePerNight) * nights * bookingForm.value.quantity
  bookingForm.value.totalPrice = total.toFixed(2)
}

/**
 * 禁用过去的日期
 */
const disabledDate = (time) => {
  return time.getTime() < new Date().setHours(0, 0, 0, 0)
}

/**
 * 禁用退房日期（不能早于入住日期）
 */
const disabledCheckOutDate = (time) => {
  if (!bookingForm.value.checkInDate) {
    return time.getTime() < new Date().setHours(0, 0, 0, 0)
  }
  return time.getTime() <= bookingForm.value.checkInDate.getTime()
}

/**
 * 提交预订
 */
const submitBooking = async () => {
  // 验证表单
  if (!bookingForm.value.roomType) {
    ElMessage.error('请选择房间类型')
    return
  }

  if (!bookingForm.value.checkInDate) {
    ElMessage.error('请选择入住日期')
    return
  }

  if (!bookingForm.value.checkOutDate) {
    ElMessage.error('请选择退房日期')
    return
  }

  if (!bookingForm.value.quantity || bookingForm.value.quantity <= 0) {
    ElMessage.error('请输入有效的房间数量')
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
    const response = await fetch(`${API_BASE_URL}/orders/hotels/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        userId: userInfo.id,
        hotelId: route.params.id,
        roomType: bookingForm.value.roomType,
        checkInDate: formatDate(bookingForm.value.checkInDate),
        checkOutDate: formatDate(bookingForm.value.checkOutDate),
        quantity: bookingForm.value.quantity
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

// 页面加载时获取酒店详情
onMounted(() => {
  loadHotelDetail()
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

.hotel-info {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.hotel-info h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 20px;
}

.hotel-info h4 {
  margin: 20px 0 15px 0;
  color: #333;
  font-size: 16px;
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

.info-item p {
  margin: 0;
  color: #333;
}

.rating-value {
  margin-left: 10px;
  font-weight: bold;
  color: #ff6b6b;
}

.rooms-section {
  margin-top: 20px;
}

.room-card {
  padding: 12px;
  margin-bottom: 10px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-type {
  font-weight: bold;
  color: #333;
  flex: 1;
}

.room-price {
  color: #ff6b6b;
  font-weight: bold;
  margin: 0 15px;
}

.room-available {
  color: #666;
  font-size: 12px;
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
