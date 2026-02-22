<template>
  <div class="orders-container">
    <Card title="订单管理">
      <el-row :gutter="20" class="filter-row">
        <el-col :xs="24" :sm="12" :md="6">
          <el-input
            v-model="filters.orderNumber"
            placeholder="请输入订单号"
            @input="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-select
            v-model="filters.status"
            placeholder="选择状态"
            @change="handleSearch"
          >
            <el-option label="全部" value="" />
            <el-option label="待支付" value="pending" />
            <el-option label="已支付" value="paid" />
            <el-option label="已发货" value="shipped" />
            <el-option label="已送达" value="delivered" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-date-picker
            v-model="filters.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="handleSearch"
          />
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-col>
      </el-row>

      <Table
        :data="orders"
        :columns="columns"
        :show-pagination="true"
        :current-page="pagination.current"
        :page-size="pagination.size"
        :total="pagination.total"
        @edit="handleEdit"
        @delete="handleDelete"
        @page-change="handlePageChange"
        @page-size-change="handlePageSizeChange"
      >
        <template #actions="{ row }">
          <el-button
            v-if="row.status === 'pending'"
            type="success"
            size="small"
            @click="handlePay(row)"
          >
            支付
          </el-button>
        </template>
      </Table>
    </Card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderAPI } from '@/api/order'
import Card from '@/components/common/Card.vue'
import Table from '@/components/common/Table.vue'

const filters = ref({
  orderNumber: '',
  status: '',
  dateRange: null,
})

const orders = ref([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0,
})

const columns = [
  { prop: 'orderNumber', label: '订单号' },
  { prop: 'productName', label: '产品名称' },
  { prop: 'quantity', label: '数量' },
  { prop: 'totalPrice', label: '总价' },
  { prop: 'status', label: '状态' },
  { prop: 'createdAt', label: '创建时间' },
]

const handleSearch = async () => {
  try {
    const params = {
      orderNumber: filters.value.orderNumber,
      status: filters.value.status,
      page: pagination.value.current,
      size: pagination.value.size,
    }

    if (filters.value.dateRange && filters.value.dateRange.length === 2) {
      params.startDate = filters.value.dateRange[0]
      params.endDate = filters.value.dateRange[1]
    }

    const response = await orderAPI.getOrders(params)
    orders.value = response.data.records || []
    pagination.value.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  }
}

const handlePageChange = (page) => {
  pagination.value.current = page
  handleSearch()
}

const handlePageSizeChange = (size) => {
  pagination.value.size = size
  pagination.value.current = 1
  handleSearch()
}

const handleEdit = (row) => {
  ElMessage.info('编辑功能开发中')
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定取消该订单吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await orderAPI.updateOrderStatus(row.id, 'cancelled')
        ElMessage.success('订单已取消')
        handleSearch()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    })
    .catch(() => {})
}

const handlePay = async (row) => {
  try {
    await orderAPI.payOrder(row.id)
    ElMessage.success('支付成功')
    handleSearch()
  } catch (error) {
    ElMessage.error('支付失败')
  }
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.orders-container {
  padding: 20px;
}

.filter-row {
  margin-bottom: 20px;
}
</style>
