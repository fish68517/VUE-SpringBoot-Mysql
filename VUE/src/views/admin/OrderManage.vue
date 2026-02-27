<template>
  <div class="admin-page-container">
    <el-card shadow="never" class="box-card">
      <template #header>
        <div class="card-header">
          <span>所有订单管理</span>
        </div>
      </template>

      <el-form :inline="true" :model="filters" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="filters.orderNumber" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="选择状态" clearable style="width: 150px">
            <el-option label="待处理" value="PENDING" />
            <el-option label="已支付" value="PAID" />
            <el-option label="已发货" value="SHIPPED" />
            <el-option label="已送达" value="DELIVERED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" icon="Search">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="orders" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="orderNumber" label="订单编号" width="220" />
        <el-table-column prop="userId" label="用户ID" width="80" align="center" />
        <el-table-column prop="productId" label="产品ID" width="80" align="center" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="totalPrice" label="总金额" width="100" align="center">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ row.totalPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="120" align="center" />
        <el-table-column prop="createdAt" label="下单时间" width="160" />
        <el-table-column prop="status" label="当前状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ formatStatus(row.status) }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="快捷操作" min-width="250" fixed="right" align="center">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'PAID'" 
              size="small" 
              type="success" 
              @click="changeStatus(row, 'SHIPPED')">
              发货
            </el-button>
            <el-button 
              v-if="row.status === 'SHIPPED'" 
              size="small" 
              type="primary" 
              @click="changeStatus(row, 'DELIVERED')">
              确认送达
            </el-button>
            <el-button 
              v-if="row.status !== 'CANCELLED' && row.status !== 'DELIVERED'" 
              size="small" 
              type="danger" 
              @click="changeStatus(row, 'CANCELLED')">
              强制取消
            </el-button>
            <span v-if="row.status === 'CANCELLED' || row.status === 'DELIVERED'" style="color: #909399; font-size: 12px;">
              无可用操作
            </span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handlePageSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import apiClient from '@/api/client'
import { orderAPI } from '@/api/order'

const loading = ref(false)
const orders = ref([])
const filters = ref({ orderNumber: '', status: '' })
const pagination = ref({ current: 1, size: 10, total: 0 })

// 辅助格式化
const formatStatus = (status) => {
  const map = { 'PENDING': '待付款', 'PAID': '已支付', 'SHIPPED': '已发货', 'DELIVERED': '已送达', 'CANCELLED': '已取消' }
  return map[status] || status
}
const getStatusType = (status) => {
  const map = { 'PENDING': 'info', 'PAID': 'warning', 'SHIPPED': 'primary', 'DELIVERED': 'success', 'CANCELLED': 'danger' }
  return map[status] || 'info'
}

// 获取全站订单数据
const handleSearch = async () => {
  loading.value = true
  try {
    // 管理员获取所有订单 (如果后端没有单独写，通过底层的 GET /orders 获取所有)
    const res = await apiClient.get('/orders')
    let allData = res.data || res || []

    // 前端条件筛选
    if (filters.value.orderNumber) {
      allData = allData.filter(o => o.orderNumber.includes(filters.value.orderNumber))
    }
    if (filters.value.status) {
      allData = allData.filter(o => o.status === filters.value.status)
    }

    pagination.value.total = allData.length
    const startIdx = (pagination.value.current - 1) * pagination.value.size
    const endIdx = startIdx + pagination.value.size
    orders.value = allData.slice(startIdx, endIdx)

  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 修改状态核心方法
const changeStatus = (row, newStatus) => {
  let actionName = newStatus === 'SHIPPED' ? '发货' : newStatus === 'DELIVERED' ? '确认送达' : '取消订单'
  let type = newStatus === 'CANCELLED' ? 'error' : 'warning'
  
  ElMessageBox.confirm(`确定要执行【${actionName}】操作吗?`, '订单状态变更', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: type,
  }).then(async () => {
    try {
      // 复用 order.js 中写好的 updateOrderStatus 接口
      await orderAPI.updateOrderStatus(row.id, newStatus)
      ElMessage.success(`${actionName} 操作成功`)
      handleSearch()
    } catch (error) {
      ElMessage.error(`${actionName} 操作失败`)
    }
  }).catch(() => {})
}

const handlePageChange = (page) => { pagination.value.current = page; handleSearch() }
const handlePageSizeChange = (size) => { pagination.value.size = size; pagination.value.current = 1; handleSearch() }

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.admin-page-container { padding: 0; }
.card-header { font-size: 16px; font-weight: bold; }
.search-form { margin-bottom: 20px; }
.pagination-wrapper { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>