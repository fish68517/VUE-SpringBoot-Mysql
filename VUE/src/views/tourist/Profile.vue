<template>
  <div class="personal-center-container">
    <!-- Personal Center Header -->
    <el-card class="header-card">
      <div class="header-content">
        <div class="user-avatar">
          <el-avatar :size="80" icon="UserFilled" />
        </div>
        <div class="user-info">
          <h2>{{ userInfo.username }}</h2>
          <p class="user-email">{{ userInfo.email || '未设置邮箱' }}</p>
          <p class="user-phone">{{ userInfo.phone || '未设置手机号' }}</p>
        </div>
        <div class="stats">
          <div class="stat-item">
            <div class="stat-number">{{ stats.orderCount }}</div>
            <div class="stat-label">订单</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ stats.favoriteCount }}</div>
            <div class="stat-label">收藏</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ stats.historyCount }}</div>
            <div class="stat-label">浏览</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Tabs for different sections -->
    <el-tabs v-model="activeTab" class="personal-tabs">
      <!-- Personal Profile Tab -->
      <el-tab-pane label="个人资料" name="profile">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>编辑个人资料</span>
            </div>
          </template>
          
          <el-form
            :model="form"
            label-width="100px"
            class="profile-form"
          >
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            
            <el-form-item label="真实姓名">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" type="email" />
            </el-form-item>
            
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSave" :loading="savingProfile">保存</el-button>
              <el-button @click="handleCancel">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- Orders Tab -->
      <el-tab-pane label="我的订单" name="orders">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>订单列表</span>
              <el-button type="primary" size="small" @click="loadOrders">刷新</el-button>
            </div>
          </template>
          
          <el-table :data="orders" stripe v-loading="loadingOrders" style="width: 100%">
            <el-table-column prop="orderNumber" label="订单号" width="180" />
            <el-table-column prop="orderType" label="订单类型" width="120">
              <template #default="{ row }">
                {{ getOrderTypeLabel(row.orderType) }}
              </template>
            </el-table-column>
            <el-table-column prop="totalPrice" label="总价" width="100" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" width="180" />
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleViewOrderDetail(row)">
                  查看详情
                </el-button>
                <el-button 
                  v-if="row.status === 'pending'" 
                  type="danger" 
                  size="small"
                  @click="handleCancelOrder(row)"
                >
                  取消
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="orderPagination.currentPage"
            v-model:page-size="orderPagination.pageSize"
            :page-sizes="[10, 20, 30, 40]"
            :total="orderPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 20px; text-align: center"
            @current-change="loadOrders"
            @size-change="loadOrders"
          />
        </el-card>
      </el-tab-pane>

      <!-- Favorites Tab -->
      <el-tab-pane label="我的收藏" name="favorites">
        <FavoritesList :userId="userInfo.id" />
      </el-tab-pane>

      <!-- Browsing History Tab -->
      <el-tab-pane label="浏览历史" name="history">
        <BrowsingHistoryList />
      </el-tab-pane>
    </el-tabs>

    <!-- Order Detail Dialog -->
    <el-dialog v-model="orderDetailVisible" title="订单详情" width="600px">
      <div v-if="selectedOrder" class="order-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单号">{{ selectedOrder.orderNumber }}</el-descriptions-item>
          <el-descriptions-item label="订单类型">{{ getOrderTypeLabel(selectedOrder.orderType) }}</el-descriptions-item>
          <el-descriptions-item label="总价">¥{{ selectedOrder.totalPrice }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedOrder.status)">{{ getStatusLabel(selectedOrder.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ selectedOrder.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ selectedOrder.updatedAt }}</el-descriptions-item>
        </el-descriptions>

        <div style="margin-top: 20px">
          <h4>订单项目</h4>
          <el-table :data="orderItems" stripe>
            <el-table-column prop="itemType" label="项目类型" width="100">
              <template #default="{ row }">
                {{ getOrderTypeLabel(row.itemType) }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="unitPrice" label="单价" width="100" />
            <el-table-column prop="subtotal" label="小计" width="100" />
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { userApi } from '../../api/user'
import BrowsingHistoryList from '../../components/BrowsingHistoryList.vue'
import FavoritesList from '../../components/FavoritesList.vue'

const API_BASE_URL = 'http://localhost:8080'

// User info
const userInfo = ref({
  id: null,
  username: '',
  email: '',
  phone: '',
  realName: ''
})

// Form data
const form = ref({
  username: '',
  realName: '',
  email: '',
  phone: ''
})

const originalForm = ref({
  username: '',
  realName: '',
  email: '',
  phone: ''
})

// Stats
const stats = ref({
  orderCount: 0,
  favoriteCount: 0,
  historyCount: 0
})

// Tab management
const activeTab = ref('profile')

// Profile
const savingProfile = ref(false)

// Orders
const orders = ref([])
const loadingOrders = ref(false)
const orderPagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// Order detail
const selectedOrder = ref(null)
const orderItems = ref([])
const orderDetailVisible = ref(false)

// Initialize user info from localStorage
onMounted(async () => {
  const user = localStorage.getItem('user')
  if (user) {
    const userInfoData = JSON.parse(user)
    userInfo.value = {
      id: userInfoData.id,
      username: userInfoData.username,
      email: userInfoData.email || '',
      phone: userInfoData.phone || '',
      realName: userInfoData.realName || ''
    }
    
    form.value = {
      username: userInfo.value.username,
      realName: userInfo.value.realName,
      email: userInfo.value.email,
      phone: userInfo.value.phone
    }
    originalForm.value = { ...form.value }
    
    // Load stats
    await loadStats()
    // Load orders
    await loadOrders()
  }
})

// Load statistics
const loadStats = async () => {
  try {
    // Load order count
    const ordersResponse = await axios.get(
      `${API_BASE_URL}/orders/user/${userInfo.value.id}`,
      { params: { page: 0, size: 1 } }
    )
    if (ordersResponse.data.code === 0) {
      stats.value.orderCount = ordersResponse.data.data.total || 0
    }

    // Load favorite count
    const favoritesResponse = await axios.get(
      `${API_BASE_URL}/favorites/user/${userInfo.value.id}`
    )
    if (favoritesResponse.data.code === 0) {
      stats.value.favoriteCount = favoritesResponse.data.data.total || 0
    }

    // Load browsing history count
    const historyResponse = await axios.get(
      `${API_BASE_URL}/browsing-history/user/${userInfo.value.id}`,
      { params: { page: 0, size: 1 } }
    )
    if (historyResponse.data.code === 0) {
      stats.value.historyCount = historyResponse.data.data.totalElements || 0
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// Profile methods
const handleSave = async () => {
  savingProfile.value = true
  try {
    const response = await userApi.updateProfile(userInfo.value.id, {
      realName: form.value.realName,
      email: form.value.email,
      phone: form.value.phone
    })
    
    if (response.code === 0) {
      ElMessage.success('保存成功')
      userInfo.value = {
        ...userInfo.value,
        realName: form.value.realName,
        email: form.value.email,
        phone: form.value.phone
      }
      const updatedUser = { ...JSON.parse(localStorage.getItem('user')), ...response.data }
      localStorage.setItem('user', JSON.stringify(updatedUser))
      originalForm.value = { ...form.value }
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    savingProfile.value = false
  }
}

const handleCancel = () => {
  form.value = { ...originalForm.value }
}

// Order methods
const getStatusType = (status) => {
  const statusMap = {
    'pending': 'warning',
    'confirmed': 'info',
    'completed': 'success',
    'cancelled': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusLabel = (status) => {
  const statusMap = {
    'pending': '待确认',
    'confirmed': '已确认',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || status
}

const getOrderTypeLabel = (type) => {
  const typeMap = {
    'attraction': '景点门票',
    'hotel': '酒店预订',
    'product': '旅游商品'
  }
  return typeMap[type] || type
}

const loadOrders = async () => {
  if (!userInfo.value.id) return

  loadingOrders.value = true
  try {
    const response = await axios.get(
      `${API_BASE_URL}/orders/user/${userInfo.value.id}`,
      {
        params: {
          page: orderPagination.value.currentPage - 1,
          size: orderPagination.value.pageSize
        }
      }
    )

    if (response.data.code === 0) {
      orders.value = response.data.data.orders
      orderPagination.value.total = response.data.data.total
    } else {
      ElMessage.error(response.data.message || '获取订单列表失败')
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败: ' + error.message)
  } finally {
    loadingOrders.value = false
  }
}

const handleViewOrderDetail = async (row) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/orders/${row.id}`)
    
    if (response.data.code === 0) {
      selectedOrder.value = response.data.data
      orderItems.value = response.data.data.items || []
      orderDetailVisible.value = true
    } else {
      ElMessage.error(response.data.message || '获取订单详情失败')
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败: ' + error.message)
  }
}

const handleCancelOrder = (row) => {
  ElMessageBox.confirm(
    '确定要取消此订单吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await axios.put(`${API_BASE_URL}/orders/${row.id}/cancel`)
      
      if (response.data.code === 0) {
        ElMessage.success('订单已取消')
        loadOrders()
      } else {
        ElMessage.error(response.data.message || '取消订单失败')
      }
    } catch (error) {
      ElMessage.error('取消订单失败: ' + error.message)
    }
  }).catch(() => {
    // User cancelled the action
  })
}
</script>

<style scoped>
.personal-center-container {
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 30px;
}

.user-avatar {
  flex-shrink: 0;
}

.user-info {
  flex: 1;
}

.user-info h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #333;
}

.user-email,
.user-phone {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.stats {
  display: flex;
  gap: 30px;
  flex-shrink: 0;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

.personal-tabs {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  color: #333;
}

.profile-form {
  max-width: 500px;
}

.order-detail {
  padding: 20px 0;
}

.order-detail h4 {
  margin-top: 20px;
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: bold;
}

@media (max-width: 1024px) {
  .personal-center-container {
    padding: 15px;
  }

  .header-content {
    gap: 20px;
  }

  .user-info h2 {
    font-size: 20px;
  }

  .stats {
    gap: 20px;
  }

  .stat-number {
    font-size: 20px;
  }

  .profile-form {
    max-width: 600px;
  }
}

@media (max-width: 768px) {
  .personal-center-container {
    padding: 12px;
  }

  .header-card {
    margin-bottom: 15px;
  }

  .header-content {
    flex-direction: column;
    gap: 15px;
    align-items: center;
    text-align: center;
  }

  .user-avatar {
    flex-shrink: 0;
  }

  .user-info {
    flex: 1;
    text-align: center;
  }

  .user-info h2 {
    font-size: 18px;
    margin: 0 0 8px 0;
  }

  .user-email,
  .user-phone {
    font-size: 13px;
    margin: 4px 0;
  }

  .stats {
    width: 100%;
    justify-content: space-around;
    gap: 15px;
  }

  .stat-number {
    font-size: 18px;
  }

  .stat-label {
    font-size: 11px;
  }

  .personal-tabs {
    margin-top: 15px;
  }

  .personal-tabs :deep(.el-tabs__header) {
    margin-bottom: 10px;
  }

  .profile-form {
    max-width: 100%;
  }

  .profile-form :deep(.el-form-item) {
    margin-bottom: 15px;
  }

  .profile-form :deep(.el-form-item__label) {
    width: 80px;
  }

  .card-header {
    font-size: 13px;
  }

  .order-detail {
    padding: 15px 0;
  }

  .order-detail h4 {
    font-size: 13px;
    margin-top: 15px;
  }
}

@media (max-width: 480px) {
  .personal-center-container {
    padding: 10px;
  }

  .header-card {
    margin-bottom: 12px;
  }

  .header-content {
    flex-direction: column;
    gap: 12px;
    align-items: center;
  }

  .user-avatar :deep(.el-avatar) {
    width: 60px;
    height: 60px;
  }

  .user-info h2 {
    font-size: 16px;
    margin: 0 0 6px 0;
  }

  .user-email,
  .user-phone {
    font-size: 12px;
    margin: 3px 0;
  }

  .stats {
    width: 100%;
    justify-content: space-around;
    gap: 10px;
  }

  .stat-item {
    text-align: center;
  }

  .stat-number {
    font-size: 16px;
  }

  .stat-label {
    font-size: 10px;
    margin-top: 3px;
  }

  .personal-tabs {
    margin-top: 12px;
  }

  .personal-tabs :deep(.el-tabs__header) {
    margin-bottom: 8px;
  }

  .personal-tabs :deep(.el-tabs__nav) {
    width: 100%;
  }

  .personal-tabs :deep(.el-tabs__item) {
    font-size: 12px;
    padding: 0 10px;
  }

  .profile-form {
    max-width: 100%;
  }

  .profile-form :deep(.el-form-item__label) {
    width: 70px;
    font-size: 12px;
  }

  .profile-form :deep(.el-form-item__content) {
    font-size: 12px;
  }

  .profile-form :deep(.el-button) {
    font-size: 12px;
    padding: 6px 12px;
  }

  .card-header {
    font-size: 12px;
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .card-header :deep(.el-button) {
    width: 100%;
    font-size: 12px;
  }

  .order-detail {
    padding: 12px 0;
  }

  .order-detail h4 {
    font-size: 12px;
    margin-top: 12px;
    margin-bottom: 8px;
  }

  .order-detail :deep(.el-table) {
    font-size: 12px;
  }

  .order-detail :deep(.el-descriptions) {
    font-size: 12px;
  }
}

@media (max-width: 360px) {
  .personal-center-container {
    padding: 8px;
  }

  .user-avatar :deep(.el-avatar) {
    width: 50px;
    height: 50px;
  }

  .user-info h2 {
    font-size: 14px;
  }

  .user-email,
  .user-phone {
    font-size: 11px;
  }

  .stat-number {
    font-size: 14px;
  }

  .stat-label {
    font-size: 9px;
  }

  .personal-tabs :deep(.el-tabs__item) {
    font-size: 11px;
    padding: 0 8px;
  }

  .profile-form :deep(.el-form-item__label) {
    width: 60px;
    font-size: 11px;
  }
}
</style>
