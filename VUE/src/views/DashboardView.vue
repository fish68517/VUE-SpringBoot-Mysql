<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>个人仪表盘</h1>
      <p class="subtitle">欢迎回来！以下是你的旅行数据统计</p>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>

    <!-- 加载失败提示 -->
    <div v-else-if="error" class="error-container">
      <el-alert :title="error" type="error" :closable="false" />
    </div>

    <!-- 数据统计卡片 -->
    <div v-else class="statistics-grid">
      <StatisticsCard
        title="旅行记录"
        :value="statistics.totalRecords"
        label="累计创建的记录数"
        class="card records"
      />
      <StatisticsCard
        title="旅行计划"
        :value="statistics.totalPlans"
        label="累计创建的计划数"
        class="card plans"
      />
      <StatisticsCard
        title="获得点赞"
        :value="statistics.totalLikesReceived"
        label="公开记录收到的点赞"
        class="card likes"
      />
      <StatisticsCard
        title="获得评论"
        :value="statistics.totalCommentsReceived"
        label="公开记录收到的评论"
        class="card comments"
      />
      <StatisticsCard
        title="地图足迹"
        :value="statistics.totalFootprints"
        label="累计添加的足迹点"
        class="card footprints"
      />
      <StatisticsCard
        title="多媒体文件"
        :value="statistics.totalMultimediaFiles"
        label="累计上传的照片/视频"
        class="card multimedia"
      />
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h2>快捷操作</h2>
      <div class="action-buttons">
        <el-button type="primary" size="large" @click="navigateTo('/records/create')">
          新建旅行记录
        </el-button>
        <el-button type="success" size="large" @click="navigateTo('/plans/create')">
          新建旅行计划
        </el-button>
        <el-button type="info" size="large" @click="navigateTo('/records')">
          查看所有记录
        </el-button>
        <el-button type="warning" size="large" @click="navigateTo('/plans')">
          查看所有计划
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import StatisticsCard from '../components/StatisticsCard.vue'
import { statisticsService } from '../services/statisticsService'

const router = useRouter()
const loading = ref(true)
const error = ref(null)

const statistics = ref({
  totalRecords: 0,
  totalPlans: 0,
  totalLikesReceived: 0,
  totalCommentsReceived: 0,
  totalFootprints: 0,
  totalMultimediaFiles: 0
})

const fetchStatistics = async () => {
  try {
    loading.value = true
    error.value = null
    const response = await statisticsService.getUserStatistics()
    if (response.data && response.data.data) {
      statistics.value = response.data.data
    }
  } catch (err) {
    error.value = err.response?.data?.message || '加载统计数据失败，请刷新重试'
    ElMessage.error(error.value)
  } finally {
    loading.value = false
  }
}

const navigateTo = (path) => {
  router.push(path)
}

onMounted(() => {
  fetchStatistics()
})
</script>

<style scoped>
.dashboard-container {
  padding: 30px 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.dashboard-header {
  margin-bottom: 36px;
  text-align: center;
}

.dashboard-header h1 {
  margin: 0 0 12px 0;
  font-size: 30px;
  font-weight: 600;
  color: #303133;
}

.subtitle {
  margin: 0;
  font-size: 16px;
  color: #909399;
}

.loading-container {
  padding: 60px 20px;
  text-align: center;
}

.statistics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 48px;
}

.quick-actions {
  background: #fff;
  border-radius: 12px;
  padding: 28px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.quick-actions h2 {
  margin: 0 0 20px 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.action-buttons .el-button {
  min-width: 180px;
 
  height: 48px;
  font-size: 16px;
}

/* 手机端适配 */
@media (max-width: 768px) {
  .dashboard-header h1 {
    font-size: 26px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }
}
</style>