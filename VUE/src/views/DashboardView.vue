<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>Dashboard</h1>
      <p class="subtitle">Welcome back! Here's your travel statistics.</p>
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else-if="error" class="error-container">
      <el-alert :title="error" type="error" :closable="false" />
    </div>

    <div v-else class="statistics-grid">
      <StatisticsCard
        title="Travel Records"
        :value="statistics.totalRecords"
        label="Total records created"
        class="card records"
      />
      <StatisticsCard
        title="Travel Plans"
        :value="statistics.totalPlans"
        label="Total plans created"
        class="card plans"
      />
      <StatisticsCard
        title="Likes Received"
        :value="statistics.totalLikesReceived"
        label="Likes on public records"
        class="card likes"
      />
      <StatisticsCard
        title="Comments Received"
        :value="statistics.totalCommentsReceived"
        label="Comments on public records"
        class="card comments"
      />
      <StatisticsCard
        title="Map Footprints"
        :value="statistics.totalFootprints"
        label="Total footprints added"
        class="card footprints"
      />
      <StatisticsCard
        title="Multimedia Files"
        :value="statistics.totalMultimediaFiles"
        label="Total files uploaded"
        class="card multimedia"
      />
    </div>

    <div class="quick-actions">
      <h2>Quick Actions</h2>
      <div class="action-buttons">
        <el-button type="primary" @click="navigateTo('/records/create')">
          <i class="el-icon-plus"></i> Create Record
        </el-button>
        <el-button type="success" @click="navigateTo('/plans/create')">
          <i class="el-icon-plus"></i> Create Plan
        </el-button>
        <el-button type="info" @click="navigateTo('/records')">
          <i class="el-icon-document"></i> View Records
        </el-button>
        <el-button type="warning" @click="navigateTo('/plans')">
          <i class="el-icon-document"></i> View Plans
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
    error.value = err.response?.data?.message || 'Failed to load statistics'
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
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.dashboard-header {
  margin-bottom: 32px;
}

.dashboard-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: #333;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.loading-container {
  padding: 20px;
}

.error-container {
  margin-bottom: 20px;
}

.statistics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.quick-actions {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.quick-actions h2 {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.action-buttons :deep(.el-button) {
  flex: 1;
  min-width: 150px;
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }

  .dashboard-header h1 {
    font-size: 24px;
  }

  .statistics-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons :deep(.el-button) {
    width: 100%;
  }
}
</style>
