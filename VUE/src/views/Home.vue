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
          <div class="stat-value">{{ stats.activeWarnings }}</div>
        </Card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <Card title="农资产品">
          <div class="stat-value">{{ stats.totalProducts }}</div>
        </Card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <Card title="订单总数">
          <div class="stat-value">{{ stats.totalOrders }}</div>
        </Card>
      </el-col>
    </el-row>

    <Card title="最新预警信息">
      <el-table :data="recentWarnings" stripe style="width: 100%">
        <el-table-column prop="warningType" label="预警类型" />
        <el-table-column prop="region" label="地区" />
        <el-table-column prop="severity" label="严重程度" />
        <el-table-column prop="description" label="描述" />
      </el-table>
    </Card>

    <Card title="热销农资产品">
      <el-table :data="topProducts" stripe style="width: 100%">
        <el-table-column prop="productName" label="产品名称" />
        <el-table-column prop="category" label="类别" />
        <el-table-column prop="price" label="价格" />
        <el-table-column prop="stock" label="库存" />
      </el-table>
    </Card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Card from '@/components/common/Card.vue'

const stats = ref({
  totalUsers: 0,
  activeWarnings: 0,
  totalProducts: 0,
  totalOrders: 0,
})

const recentWarnings = ref([])
const topProducts = ref([])

onMounted(() => {
  // Initialize with placeholder data
  stats.value = {
    totalUsers: 150,
    activeWarnings: 8,
    totalProducts: 45,
    totalOrders: 320,
  }

  recentWarnings.value = [
    {
      warningType: '暴雨',
      region: '华东地区',
      severity: 'high',
      description: '未来24小时内可能出现暴雨',
    },
    {
      warningType: '大风',
      region: '华北地区',
      severity: 'medium',
      description: '风力可达6-7级',
    },
  ]

  topProducts.value = [
    {
      productName: '防雨农膜',
      category: '防护用品',
      price: 89.99,
      stock: 150,
    },
    {
      productName: '复合肥',
      category: '肥料',
      price: 45.50,
      stock: 200,
    },
  ]
})
</script>

<style scoped>
.home {
  padding: 20px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  text-align: center;
  padding: 20px 0;
}
</style>
