<template>
  <div class="orders-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的订单</span>
          <el-button type="primary" @click="loadOrders">刷新</el-button>
        </div>
      </template>
      
      <el-table :data="orders" stripe v-loading="loading">
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
            <el-button type="primary" size="small" @click="handleViewDetail(row)">
              查看详情
            </el-button>
            <el-button 
              v-if="row.status === 'pending'" 
              type="danger" 
              size="small"
              @click="handleCancel(row)"
            >
              取消
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

const API_BASE_URL = 'http://localhost:8080'

const orders = ref([])
const selectedOrder = ref(null)
const orderItems = ref([])
const loading = ref(false)
const detailDialogVisible = ref(false)

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// Get user ID from localStorage
const userId = localStorage.getItem('userId')

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
  if (!userId) {
    ElMessage.error('请先登录')
    return
  }

  loading.value = true
  try {
    const response = await axios.get(
      `${API_BASE_URL}/orders/user/${userId}`,
      {
        params: {
          page: pagination.value.currentPage - 1,
          size: pagination.value.pageSize
        }
      }
    )

    if (response.data.code === 0) {
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
    
    if (response.data.code === 0) {
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

const handleCancel = (row) => {
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

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  color: #333;
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

/* Tablet screens */
@media (max-width: 1024px) {
  .orders-container {
    padding: 15px;
  }

  .card-header {
    font-size: 14px;
  }

  .order-detail h4 {
    font-size: 13px;
  }
}

/* Tablet and smaller */
@media (max-width: 768px) {
  .orders-container {
    padding: 12px;
  }

  .card-header {
    font-size: 13px;
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .card-header :deep(.el-button) {
    width: 100%;
    font-size: 12px;
  }

  .order-detail {
    padding: 15px 0;
  }

  .order-detail h4 {
    font-size: 12px;
    margin-top: 15px;
    margin-bottom: 8px;
  }

  .order-detail :deep(.el-table) {
    font-size: 12px;
  }

  .order-detail :deep(.el-descriptions) {
    font-size: 12px;
  }

  .order-detail :deep(.el-descriptions-item__label) {
    width: 80px;
  }
}

/* Mobile devices */
@media (max-width: 480px) {
  .orders-container {
    padding: 10px;
  }

  .card-header {
    font-size: 12px;
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .card-header :deep(.el-button) {
    width: 100%;
    font-size: 11px;
    padding: 6px 12px;
  }

  .order-detail {
    padding: 12px 0;
  }

  .order-detail h4 {
    font-size: 11px;
    margin-top: 12px;
    margin-bottom: 6px;
  }

  .order-detail :deep(.el-table) {
    font-size: 11px;
  }

  .order-detail :deep(.el-table th) {
    padding: 8px 0;
  }

  .order-detail :deep(.el-table td) {
    padding: 8px 0;
  }

  .order-detail :deep(.el-descriptions) {
    font-size: 11px;
  }

  .order-detail :deep(.el-descriptions-item__label) {
    width: 70px;
  }

  .order-detail :deep(.el-descriptions-item__content) {
    word-break: break-all;
  }
}

/* Extra small devices */
@media (max-width: 360px) {
  .orders-container {
    padding: 8px;
  }

  .card-header {
    font-size: 11px;
  }

  .order-detail h4 {
    font-size: 10px;
  }

  .order-detail :deep(.el-table) {
    font-size: 10px;
  }

  .order-detail :deep(.el-descriptions) {
    font-size: 10px;
  }

  .order-detail :deep(.el-descriptions-item__label) {
    width: 60px;
  }
}
</style>
