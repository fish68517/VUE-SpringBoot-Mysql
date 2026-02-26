<template>
  <div class="admin-orders-container">
    <!-- Statistics Card -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">总订单数</div>
            <div class="stat-value">{{ statistics.totalOrders }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">总收入</div>
            <div class="stat-value">¥{{ statistics.totalRevenue }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">平均订单金额</div>
            <div class="stat-value">¥{{ statistics.averageOrderPrice }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-label">已完成订单</div>
            <div class="stat-value">{{ statistics.statusStats?.completed || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Status and Type Statistics -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :xs="24" :sm="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>订单状态分布</span>
            </div>
          </template>
          <el-table :data="statusStatsData" stripe>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                {{ getStatusLabel(row.status) }}
              </template>
            </el-table-column>
            <el-table-column prop="count" label="数量" width="100" />
            <el-table-column prop="percentage" label="占比" width="100" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>订单类型分布</span>
            </div>
          </template>
          <el-table :data="typeStatsData" stripe>
            <el-table-column prop="type" label="类型" width="100">
              <template #default="{ row }">
                {{ getOrderTypeLabel(row.type) }}
              </template>
            </el-table-column>
            <el-table-column prop="count" label="数量" width="100" />
            <el-table-column prop="percentage" label="占比" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- Orders Table -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <div class="filter-section">
            <el-select v-model="filterStatus" placeholder="按状态筛选" clearable @change="loadOrders">
              <el-option label="待确认" value="pending" />
              <el-option label="已确认" value="confirmed" />
              <el-option label="已完成" value="completed" />
              <el-option label="已取消" value="cancelled" />
            </el-select>
            <el-select v-model="filterType" placeholder="按类型筛选" clearable @change="loadOrders" style="margin-left: 10px">
              <el-option label="景点门票" value="attraction" />
              <el-option label="酒店预订" value="hotel" />
              <el-option label="旅游商品" value="product" />
            </el-select>
            <el-button type="primary" @click="loadOrders" style="margin-left: 10px">刷新</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="orders" stripe v-loading="loading">
        <el-table-column prop="orderNumber" label="订单号" width="180" />
        <el-table-column prop="userId" label="用户ID" width="80" />
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
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)">
              查看详情
            </el-button>
            <el-button type="warning" size="small" @click="handleUpdateStatus(row)">
              更新状态
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 30, 40]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; text-align: center"
        @current-change="loadOrders"
        @size-change="loadOrders"
      />
    </el-card>

    <!-- Order Detail Dialog -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="600px">
      <div v-if="selectedOrder" class="order-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单号">{{ selectedOrder.orderNumber }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ selectedOrder.userId }}</el-descriptions-item>
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
            <el-table-column prop="itemId" label="项目ID" width="80" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="unitPrice" label="单价" width="100" />
            <el-table-column prop="subtotal" label="小计" width="100" />
          </el-table>
        </div>
      </div>
    </el-dialog>

    <!-- Update Status Dialog -->
    <el-dialog v-model="updateStatusDialogVisible" title="更新订单状态" width="400px">
      <el-form v-if="selectedOrder" :model="statusForm" label-width="100px">
        <el-form-item label="订单号">
          <span>{{ selectedOrder.orderNumber }}</span>
        </el-form-item>
        <el-form-item label="当前状态">
          <span>{{ getStatusLabel(selectedOrder.status) }}</span>
        </el-form-item>
        <el-form-item label="新状态">
          <el-select v-model="statusForm.status" placeholder="选择新状态">
            <el-option label="待确认" value="pending" />
            <el-option label="已确认" value="confirmed" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="updateStatusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmUpdateStatus">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const orders = ref([])
const selectedOrder = ref(null)
const orderItems = ref([])
const loading = ref(false)
const detailDialogVisible = ref(false)
const updateStatusDialogVisible = ref(false)
const filterStatus = ref('')
const filterType = ref('')

const statistics = ref({
  totalOrders: 0,
  totalRevenue: 0,
  averageOrderPrice: 0,
  statusStats: {},
  typeStats: {}
})

const statusForm = ref({
  status: ''
})

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const statusStatsData = computed(() => {
  const stats = statistics.value.statusStats || {}
  const total = statistics.value.totalOrders || 1
  return [
    { status: 'pending', count: stats.pending || 0, percentage: ((stats.pending || 0) / total * 100).toFixed(1) + '%' },
    { status: 'confirmed', count: stats.confirmed || 0, percentage: ((stats.confirmed || 0) / total * 100).toFixed(1) + '%' },
    { status: 'completed', count: stats.completed || 0, percentage: ((stats.completed || 0) / total * 100).toFixed(1) + '%' },
    { status: 'cancelled', count: stats.cancelled || 0, percentage: ((stats.cancelled || 0) / total * 100).toFixed(1) + '%' }
  ]
})

const typeStatsData = computed(() => {
  const stats = statistics.value.typeStats || {}
  const total = statistics.value.totalOrders || 1
  return [
    { type: 'attraction', count: stats.attraction || 0, percentage: ((stats.attraction || 0) / total * 100).toFixed(1) + '%' },
    { type: 'hotel', count: stats.hotel || 0, percentage: ((stats.hotel || 0) / total * 100).toFixed(1) + '%' },
    { type: 'product', count: stats.product || 0, percentage: ((stats.product || 0) / total * 100).toFixed(1) + '%' }
  ]
})

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

const loadStatistics = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/orders/admin/statistics`)
    
    if (response.data.code === '0') {
      statistics.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '获取订单统计失败')
    }
  } catch (error) {
    ElMessage.error('获取订单统计失败: ' + error.message)
  }
}

const loadOrders = async () => {
  loading.value = true
  try {
    let url = `${API_BASE_URL}/orders/admin/all`
    
    // Use filtered endpoint if filters are applied
    if (filterStatus.value) {
      url = `${API_BASE_URL}/orders/admin/status/${filterStatus.value}`
    } else if (filterType.value) {
      url = `${API_BASE_URL}/orders/admin/type/${filterType.value}`
    }

    const response = await axios.get(url, {
      params: {
        page: pagination.value.currentPage - 1,
        size: pagination.value.pageSize
      }
    })

    if (response.data.code === '0') {
      orders.value = response.data.data.orders
      pagination.value.total = response.data.data.total
    } else {
      ElMessage.error(response.data.message || '获取订单列表失败')
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const handleViewDetail = async (row) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/orders/${row.id}`)
    
    if (response.data.code === '0') {
      selectedOrder.value = response.data.data
      orderItems.value = response.data.data.items || []
      detailDialogVisible.value = true
    } else {
      ElMessage.error(response.data.message || '获取订单详情失败')
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败: ' + error.message)
  }
}

const handleUpdateStatus = (row) => {
  selectedOrder.value = row
  statusForm.value.status = row.status
  updateStatusDialogVisible.value = true
}

const confirmUpdateStatus = async () => {
  if (!statusForm.value.status) {
    ElMessage.error('请选择新状态')
    return
  }

  try {
    const response = await axios.put(
      `${API_BASE_URL}/orders/admin/${selectedOrder.value.id}/status`,
      { status: statusForm.value.status }
    )
    
    if (response.data.code === '0') {
      ElMessage.success('订单状态已更新')
      updateStatusDialogVisible.value = false
      loadOrders()
      loadStatistics()
    } else {
      ElMessage.error(response.data.message || '更新订单状态失败')
    }
  } catch (error) {
    ElMessage.error('更新订单状态失败: ' + error.message)
  }
}

onMounted(() => {
  loadStatistics()
  loadOrders()
})
</script>

<style scoped>
.admin-orders-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  color: #333;
}

.filter-section {
  display: flex;
  gap: 10px;
}

.filter-section :deep(.el-select) {
  width: 150px;
}

.stat-card {
  height: 100%;
}

.stat-card :deep(.el-card__body) {
  padding: 20px;
}

.stat-content {
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
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
</style>
