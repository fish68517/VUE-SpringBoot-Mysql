<template>
  <div class="analytics-container">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <Card title="订单统计">
          <div class="stat-value">{{ stats.totalOrders }}</div>
          <p class="stat-label">总订单数</p>
        </Card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <Card title="预警统计">
          <div class="stat-value">{{ stats.totalWarnings }}</div>
          <p class="stat-label">总预警数</p>
        </Card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <Card title="用户统计">
          <div class="stat-value">{{ stats.totalUsers }}</div>
          <p class="stat-label">总用户数</p>
        </Card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <Card title="销售额">
          <div class="stat-value">¥{{ stats.totalSales }}</div>
          <p class="stat-label">总销售额</p>
        </Card>
      </el-col>
    </el-row>

    <Card title="产品销售排行">
      <el-table :data="topProducts" stripe style="width: 100%">
        <el-table-column prop="productName" label="产品名称" />
        <el-table-column prop="category" label="类别" />
        <el-table-column prop="salesCount" label="销售数量" />
        <el-table-column prop="totalSales" label="销售额" />
      </el-table>
    </Card>

    <Card title="数据导出">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <el-button type="primary" @click="handleExport('orders')">
            导出订单数据
          </el-button>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-button type="primary" @click="handleExport('warnings')">
            导出预警数据
          </el-button>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-button type="primary" @click="handleExport('users')">
            导出用户数据
          </el-button>
        </el-col>
      </el-row>
    </Card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { analyticsAPI } from '@/api/analytics'
import Card from '@/components/common/Card.vue'

const stats = ref({
  totalOrders: 0,
  totalWarnings: 0,
  totalUsers: 0,
  totalSales: 0,
})

const topProducts = ref([])

const handleExport = async (type) => {
  try {
    const response = await analyticsAPI.exportData({ type })
    const url = window.URL.createObjectURL(new Blob([response]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${type}_export.csv`)
    document.body.appendChild(link)
    link.click()
    link.parentNode.removeChild(link)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

onMounted(async () => {
  try {
    const [orderStats, warningStats, userStats, products] = await Promise.all([
      analyticsAPI.getOrderStats(),
      analyticsAPI.getWarningStats(),
      analyticsAPI.getUserStats(),
      analyticsAPI.getTopProducts(),
    ])

    stats.value = {
      totalOrders: orderStats.data?.total || 0,
      totalWarnings: warningStats.data?.total || 0,
      totalUsers: userStats.data?.total || 0,
      totalSales: orderStats.data?.totalSales || 0,
    }

    topProducts.value = products.data || []
  } catch (error) {
    ElMessage.error('获取数据分析失败')
  }
})
</script>

<style scoped>
.analytics-container {
  padding: 20px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  text-align: center;
  padding: 20px 0;
}

.stat-label {
  text-align: center;
  color: #606266;
  margin: 0;
}
</style>
