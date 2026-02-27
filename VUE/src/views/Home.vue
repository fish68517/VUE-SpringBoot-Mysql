<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <Card title="用户总数">
          <div class="stat-value">{{ stats.totalUsers }}</div>
        </Card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <Card title="活跃预警">
          <div class="stat-value highlight-red">{{ stats.activeWarnings }}</div>
        </Card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <Card title="农资产品">
          <div class="stat-value">{{ stats.totalProducts }}</div>
        </Card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <Card title="订单总数">
          <div class="stat-value highlight-blue">{{ stats.totalOrders }}</div>
        </Card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <Card title="最新预警信息">
          <el-table :data="recentWarnings" stripe style="width: 100%" v-loading="loading">
            <el-table-column prop="warningType" label="预警类型" width="120">
              <template #default="{ row }">
                <el-tag type="danger">{{ row.warningType }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="region" label="地区" width="150" />
            <el-table-column prop="severity" label="严重等级" width="100" />
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="createdAt" label="发布时间" width="180" />
          </el-table>
        </Card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <Card title="热门农资产品">
          <el-table :data="topProducts" stripe style="width: 100%" v-loading="loading">
            <el-table-column prop="productName" label="产品名称" />
            <el-table-column prop="category" label="类别" width="150">
              <template #default="{ row }">
                <el-tag>{{ row.category }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="价格" width="120">
              <template #default="{ row }">
                <span style="color: #f56c6c; font-weight: bold;">¥{{ row.price }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="stock" label="当前库存" width="120" />
          </el-table>
        </Card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Card from '@/components/common/Card.vue'
import apiClient from '@/api/client'
import { ElMessage } from 'element-plus'

const loading = ref(false)

const stats = ref({
  totalUsers: 0,
  activeWarnings: 0,
  totalProducts: 0,
  totalOrders: 0,
})

const recentWarnings = ref([])
const topProducts = ref([])

// 抓取真实的看板数据
const fetchDashboardData = async () => {
  loading.value = true
  try {
    const response = await apiClient.get('/home/dashboard')
    // 兼容部分拦截器将真实数据包裹在 data 属性里的情况
    const resData = response.data || response
    
    if (resData) {
      stats.value = resData.stats || stats.value
      recentWarnings.value = resData.recentWarnings || []
      topProducts.value = resData.topProducts || []
    }
  } catch (error) {
    console.error('获取首页数据失败:', error)
    ElMessage.error('无法加载首页统计数据，请检查网络或后端服务')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<style scoped>
.home {
  padding: 20px;
}
.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #303133;
  text-align: center;
  padding: 20px 0;
}
/* 给关键数字增加一点颜色强调 */
.highlight-red { color: #f56c6c; }
.highlight-blue { color: #409eff; }

.mt-20 {
  margin-top: 20px;
}
</style>