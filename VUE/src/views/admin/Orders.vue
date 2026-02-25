<template>
  <div class="admin-orders-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
        </div>
      </template>
      
      <el-table :data="orders" stripe>
        <el-table-column prop="orderNumber" label="订单号" />
        <el-table-column prop="userId" label="用户ID" />
        <el-table-column prop="type" label="订单类型" />
        <el-table-column prop="totalPrice" label="总价" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small">查看详情</el-button>
            <el-button type="warning" size="small">更新状态</el-button>
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

const orders = ref([
  { orderNumber: 'ORD001', userId: 1, type: '景点门票', totalPrice: 100, status: '已完成' },
  { orderNumber: 'ORD002', userId: 2, type: '酒店预订', totalPrice: 500, status: '待确认' }
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
</script>

<style scoped>
.admin-orders-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
}
</style>
