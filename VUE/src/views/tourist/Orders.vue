<template>
  <div class="orders-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的订单</span>
        </div>
      </template>
      
      <el-table :data="orders" stripe>
        <el-table-column prop="orderNumber" label="订单号" />
        <el-table-column prop="type" label="订单类型" />
        <el-table-column prop="totalPrice" label="总价" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)">
              查看详情
            </el-button>
            <el-button 
              v-if="row.status === '待确认'" 
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
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const orders = ref([
  { orderNumber: 'ORD001', type: '景点门票', totalPrice: 100, status: '已完成', createdAt: '2024-01-01' },
  { orderNumber: 'ORD002', type: '酒店预订', totalPrice: 500, status: '待确认', createdAt: '2024-01-02' }
])

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 2
})

const getStatusType = (status) => {
  const statusMap = {
    '待确认': 'warning',
    '已完成': 'success',
    '已取消': 'danger'
  }
  return statusMap[status] || 'info'
}

const handleViewDetail = (row) => {
  // Navigate to order detail
}

const handleCancel = (row) => {
  // Cancel order
}
</script>

<style scoped>
.orders-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
}
</style>
