<template>
  <div>
    <div class="admin-page-heading">
      <div>
        <p class="eyebrow">DASHBOARD</p>
        <h1>管理工作台</h1>
        <p>集中查看核心指标、待处理反馈，并控制演示数据模拟和统计生成。</p>
      </div>
      <div class="admin-heading-actions">
        <el-button :loading="simulationLoading" @click="simulate">运行一次状态模拟</el-button>
        <el-button type="primary" :loading="loading" @click="loadDashboard">刷新数据</el-button>
      </div>
    </div>

    <div v-loading="loading" class="admin-metric-grid">
      <article v-for="item in metrics" :key="item.key" class="admin-metric-card">
        <span>{{ item.label }}</span>
        <strong>{{ item.source === 'dashboard' ? dashboard[item.key] ?? 0 : overview[item.key] ?? 0 }}</strong>
        <small>{{ item.note }}</small>
      </article>
    </div>

    <div class="admin-dashboard-grid">
      <article class="admin-action-card">
        <p class="eyebrow">MANAGEMENT</p>
        <h2>管理模块</h2>
        <div class="admin-quick-links">
          <router-link v-for="item in moduleMenus" :key="item.key" :to="`/admin/${item.key}`">
            <span>{{ item.icon }}</span>
            <strong>{{ item.title }}</strong>
            <small>进入维护 →</small>
          </router-link>
        </div>
      </article>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminDashboard, runSimulation } from '../../api/admin'
import { moduleMenus } from './adminModules'

const loading = ref(false)
const simulationLoading = ref(false)
const overview = reactive({})
const dashboard = reactive({})
const metrics = [
  { key: 'stationCount', label: '充电站', note: '数据库有效站点' },
  { key: 'pileCount', label: '充电桩', note: '数据库有效充电桩' },
  { key: 'usingCount', label: '使用中', note: '最新状态为使用中' },
  { key: 'faultCount', label: '故障', note: '最新状态为故障' },
  { key: 'pendingFeedbackCount', label: '待处理反馈', note: '需要管理员回复', source: 'dashboard' },
  { key: 'todayUsageCount', label: '今日记录', note: '今日模拟使用记录', source: 'dashboard' }
]

const loadDashboard = async () => {
  loading.value = true
  try {
    const data = await getAdminDashboard()
    Object.assign(dashboard, data)
    Object.assign(overview, data.overview)
  } catch (error) {
    ElMessage.error(error.message || '工作台数据加载失败')
  } finally {
    loading.value = false
  }
}

const simulate = async () => {
  simulationLoading.value = true
  try {
    const data = await runSimulation()
    ElMessage.success(`已更新 ${data.updatedPileCount} 个充电桩状态，故障数 ${data.faultCount}`)
    await loadDashboard()
  } catch (error) {
    ElMessage.error(error.message || '状态模拟失败')
  } finally {
    simulationLoading.value = false
  }
}

onMounted(loadDashboard)
</script>
