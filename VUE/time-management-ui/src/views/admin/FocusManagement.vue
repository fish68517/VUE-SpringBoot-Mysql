<template>
  <div class="task-supervision-page">
    <el-row :gutter="16" class="overview-row">
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card">
          <el-statistic title="全校任务数" :value="overviewStats.totalTasks" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card">
          <el-statistic title="进行中任务" :value="overviewStats.inProgressTasks" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card">
          <el-statistic title="累计专注时长" :value="overviewStats.totalFocusMinutes" suffix="分钟" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="overview-card">
          <el-statistic title="中断 / 暂停次数" :value="overviewStats.totalInterruptions" />
        </el-card>
      </el-col>
    </el-row>

    <el-card class="task-card">
      <template #header>
        <div class="card-header">
          <span>任务监管</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索任务标题、用户昵称或任务分类"
              style="width: 300px"
              clearable
            />
          </div>
        </div>
      </template>

      <el-table :data="filteredTasks" v-loading="loading" style="width: 100%">
        <el-table-column label="发布用户" width="160">
          <template #default="scope">
            <div class="user-info">
              <el-avatar :size="24" :src="scope.row.userAvatar" style="margin-right: 8px;" />
              <span>{{ scope.row.userName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="taskTitleText" label="任务标题" min-width="220" show-overflow-tooltip />

        <el-table-column label="任务状态" width="110">
          <template #default="scope">
            <el-tag :type="getTaskStatusMeta(scope.row.taskStatusEnum).type">
              {{ getTaskStatusMeta(scope.row.taskStatusEnum).label }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="专注 / 休息配置" width="160">
          <template #default="scope">
            {{ scope.row.taskFocusDurationMins || 30 }} / {{ scope.row.taskBreakDurationMins ?? 5 }} 分钟
          </template>
        </el-table-column>

        <el-table-column label="累计专注时长" width="130" sortable prop="derivedFocusMinutes">
          <template #default="scope">
            {{ scope.row.derivedFocusMinutes }} 分钟
          </template>
        </el-table-column>

        <el-table-column label="历史记录数" width="110" sortable prop="derivedRecordCount">
          <template #default="scope">
            {{ scope.row.derivedRecordCount }}
          </template>
        </el-table-column>

        <el-table-column label="中断次数" width="100" sortable prop="derivedInterruptionCount">
          <template #default="scope">
            <span class="warn-text">{{ scope.row.derivedInterruptionCount }}</span>
          </template>
        </el-table-column>

        <el-table-column label="暂停次数" width="100" sortable prop="derivedPauseCount">
          <template #default="scope">
            {{ scope.row.derivedPauseCount }}
          </template>
        </el-table-column>

        <el-table-column label="任务关联" min-width="140">
          <template #default="scope">
            <span>{{ scope.row.taskCategoryCode || '未分类' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="openTaskDetail(scope.row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" width="1220px" destroy-on-close>
      <template #header>
        <div class="detail-header">
          <div>
            <div class="detail-title">{{ currentTask?.taskTitleText || '任务详情' }}</div>
            <div class="detail-subtitle">
              {{ currentTask?.userName || '-' }} · {{ currentTask?.taskCategoryCode || '未分类' }}
            </div>
          </div>
          <el-tag :type="getTaskStatusMeta(currentTask?.taskStatusEnum).type">
            {{ getTaskStatusMeta(currentTask?.taskStatusEnum).label }}
          </el-tag>
        </div>
      </template>

      <template v-if="currentTask">
        <el-row :gutter="16" class="detail-stats">
          <el-col :span="6">
            <el-card shadow="hover" class="detail-stat-card">
              <el-statistic title="累计专注时长" :value="currentTask.derivedFocusMinutes" suffix="分钟" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="detail-stat-card">
              <el-statistic title="历史记录数" :value="currentTask.derivedRecordCount" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="detail-stat-card">
              <el-statistic title="中断次数" :value="currentTask.derivedInterruptionCount" />
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="detail-stat-card">
              <el-statistic title="暂停次数" :value="currentTask.derivedPauseCount" />
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="16" class="detail-content">
          <el-col :span="12">
            <el-card shadow="never" class="config-card">
              <template #header>
                <div class="section-title">专注时长设置</div>
              </template>
              <div class="config-form">
                <el-form label-position="top">
                  <el-form-item label="专注时长（分钟）">
                    <el-input-number v-model="focusDurationForm" :min="1" :max="240" style="width: 100%" />
                  </el-form-item>
                  <el-form-item label="休息时长（分钟）">
                    <el-input-number v-model="breakDurationForm" :min="0" :max="120" style="width: 100%" />
                  </el-form-item>
                </el-form>
                <el-button type="primary" :loading="savingDuration" @click="handleDurationSave">
                  保存任务时长配置
                </el-button>
              </div>
            </el-card>
          </el-col>

          <el-col :span="12">
            <el-card shadow="never" class="summary-card">
              <template #header>
                <div class="section-title">专注任务统计</div>
              </template>
              <div class="summary-list">
                <div class="summary-item">
                  <span>首次开始时间</span>
                  <strong>{{ formatDateTime(taskHistorySummary.firstStart) }}</strong>
                </div>
                <div class="summary-item">
                  <span>最近结束时间</span>
                  <strong>{{ formatDateTime(taskHistorySummary.lastEnd) }}</strong>
                </div>
                <div class="summary-item">
                  <span>用户端累计计时</span>
                  <strong>{{ formatDuration(taskHistorySummary.totalSeconds) }}</strong>
                </div>
                <div class="summary-item">
                  <span>平均单次专注</span>
                  <strong>{{ formatDuration(taskHistorySummary.avgSeconds) }}</strong>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-card shadow="never" class="history-card">
          <template #header>
            <div class="section-title">专注任务历史记录</div>
          </template>

          <el-table :data="currentTaskRecords" max-height="420">
            <el-table-column label="开始时间" width="170">
              <template #default="scope">
                {{ formatDateTime(scope.row.focusStartTimestamp) }}
              </template>
            </el-table-column>
            <el-table-column label="结束时间" width="170">
              <template #default="scope">
                {{ formatDateTime(scope.row.focusEndTimestamp) }}
              </template>
            </el-table-column>
            <el-table-column label="专注时长" width="130">
              <template #default="scope">
                {{ formatDuration(scope.row.focusDurationSeconds) }}
              </template>
            </el-table-column>
            <el-table-column label="中断次数" width="100">
              <template #default="scope">
                {{ scope.row.focusInterruptionCount || 0 }}
              </template>
            </el-table-column>
            <el-table-column label="暂停次数" width="100">
              <template #default="scope">
                {{ scope.row.focusPauseCount || 0 }}
              </template>
            </el-table-column>
            <el-table-column label="专注类型" width="120">
              <template #default="scope">
                <el-tag size="small" :type="scope.row.focusTypeEnum === 'pomodoro' ? 'danger' : 'primary'">
                  {{ scope.row.focusTypeEnum === 'pomodoro' ? '番茄钟' : '自定义' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="110">
              <template #default="scope">
                <el-tag :type="getRecordStatusMeta(scope.row.focusStatusEnum).type">
                  {{ getRecordStatusMeta(scope.row.focusStatusEnum).label }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="专注备注" min-width="180" show-overflow-tooltip>
              <template #default="scope">
                {{ scope.row.focusNoteText || '-' }}
              </template>
            </el-table-column>
          </el-table>

          <div v-if="!currentTaskRecords.length" class="empty-text">暂无专注历史记录</div>
        </el-card>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../api/NetWorkApi.js'

const loading = ref(false)
const savingDuration = ref(false)
const searchKeyword = ref('')
const rawTasks = ref([])
const rawRecords = ref([])
const usersMap = ref({})
const dialogVisible = ref(false)
const currentTask = ref(null)
const currentTaskRecords = ref([])
const focusDurationForm = ref(30)
const breakDurationForm = ref(5)

const unwrapData = (response) => response?.data?.data || response?.data || []

const overviewStats = computed(() => ({
  totalTasks: rawTasks.value.length,
  inProgressTasks: rawTasks.value.filter((task) => task.taskStatusEnum === 'in_progress').length,
  totalFocusMinutes: rawTasks.value.reduce((sum, task) => sum + (task.derivedFocusMinutes || 0), 0),
  totalInterruptions: rawTasks.value.reduce(
    (sum, task) => sum + (task.derivedInterruptionCount || 0) + (task.derivedPauseCount || 0),
    0
  ),
}))

const filteredTasks = computed(() => {
  if (!searchKeyword.value) return rawTasks.value
  const keyword = searchKeyword.value.toLowerCase()
  return rawTasks.value.filter((task) =>
    String(task.userName || '').toLowerCase().includes(keyword) ||
    String(task.taskTitleText || '').toLowerCase().includes(keyword) ||
    String(task.taskCategoryCode || '').toLowerCase().includes(keyword)
  )
})

const taskHistorySummary = computed(() => {
  if (!currentTaskRecords.value.length) {
    return {
      firstStart: '',
      lastEnd: '',
      totalSeconds: 0,
      avgSeconds: 0,
    }
  }

  const sortedByStart = [...currentTaskRecords.value].sort(
    (left, right) => new Date(left.focusStartTimestamp) - new Date(right.focusStartTimestamp)
  )
  const totalSeconds = sortedByStart.reduce((sum, item) => sum + Number(item.focusDurationSeconds || 0), 0)
  return {
    firstStart: sortedByStart[0]?.focusStartTimestamp || '',
    lastEnd: sortedByStart[sortedByStart.length - 1]?.focusEndTimestamp || '',
    totalSeconds,
    avgSeconds: Math.round(totalSeconds / sortedByStart.length) || 0,
  }
})

const fetchData = async () => {
  loading.value = true
  try {
    const [userRes, taskRes, recordRes] = await Promise.all([
      api.campusUserApi.list(),
      api.taskFocusApi.list(),
      api.focusRecordApi.list(),
    ])

    const users = unwrapData(userRes)
    const tasks = unwrapData(taskRes)
    const records = unwrapData(recordRes)

    const nextUsersMap = {}
    users.forEach((user) => {
      nextUsersMap[user.campusUserId] = {
        name: user.campusNickname || user.campusEmailAddr || '未知用户',
        avatar: user.campusAvatarUrl || '',
      }
    })
    usersMap.value = nextUsersMap
    rawRecords.value = records

    rawTasks.value = tasks
      .map((task) => buildTaskRow(task, records, nextUsersMap))
      .sort((left, right) => new Date(right.taskCreateTimestamp || 0) - new Date(left.taskCreateTimestamp || 0))

    if (currentTask.value?.taskFocusId) {
      const refreshedTask = rawTasks.value.find((task) => task.taskFocusId === currentTask.value.taskFocusId)
      if (refreshedTask) {
        syncCurrentTask(refreshedTask)
      }
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('任务监管数据加载失败')
  } finally {
    loading.value = false
  }
}

const buildTaskRow = (task, records, userLookup) => {
  const taskRecords = records
    .filter((record) => record.taskFocusId === task.taskFocusId)
    .sort((left, right) => new Date(right.focusStartTimestamp || 0) - new Date(left.focusStartTimestamp || 0))

  const totalFocusSeconds = taskRecords.reduce((sum, record) => sum + Number(record.focusDurationSeconds || 0), 0)
  const totalInterruptionCount = taskRecords.reduce((sum, record) => sum + Number(record.focusInterruptionCount || 0), 0)
  const totalPauseCount = taskRecords.reduce((sum, record) => sum + Number(record.focusPauseCount || 0), 0)
  const userInfo = userLookup[task.campusUserId] || { name: '未知用户', avatar: '' }

  return {
    ...task,
    userName: userInfo.name,
    userAvatar: userInfo.avatar,
    derivedFocusMinutes: Math.round(totalFocusSeconds / 60),
    derivedRecordCount: taskRecords.length,
    derivedInterruptionCount: totalInterruptionCount,
    derivedPauseCount: totalPauseCount,
    records: taskRecords,
  }
}

const openTaskDetail = (task) => {
  syncCurrentTask(task)
  dialogVisible.value = true
}

const syncCurrentTask = (task) => {
  currentTask.value = { ...task }
  currentTaskRecords.value = [...(task.records || [])]
  focusDurationForm.value = task.taskFocusDurationMins || 30
  breakDurationForm.value = task.taskBreakDurationMins ?? 5
}

const handleDurationSave = async () => {
  if (!currentTask.value) return

  savingDuration.value = true
  try {
    await api.taskFocusApi.update({
      ...currentTask.value,
      taskFocusDurationMins: focusDurationForm.value,
      taskBreakDurationMins: breakDurationForm.value,
    })
    ElMessage.success('任务专注时长配置已更新')
    await fetchData()
  } catch (error) {
    console.error(error)
    ElMessage.error('保存任务时长配置失败')
  } finally {
    savingDuration.value = false
  }
}

const formatDateTime = (timestamp) => {
  if (!timestamp) return '-'
  return String(timestamp).replace('T', ' ').slice(0, 16)
}

const formatDuration = (seconds) => {
  const safeSeconds = Number(seconds || 0)
  const hours = Math.floor(safeSeconds / 3600)
  const minutes = Math.floor((safeSeconds % 3600) / 60)
  const remainSeconds = safeSeconds % 60
  if (hours > 0) {
    return `${hours}小时 ${minutes}分钟 ${remainSeconds}秒`
  }
  return `${minutes}分钟 ${remainSeconds}秒`
}

const getTaskStatusMeta = (status) => {
  const metaMap = {
    completed: { label: '已完成', type: 'success' },
    in_progress: { label: '进行中', type: 'primary' },
    pending: { label: '待开始', type: 'info' },
    archived: { label: '已归档', type: 'warning' },
  }
  return metaMap[status] || { label: status || '未知', type: 'info' }
}

const getRecordStatusMeta = (status) => {
  const metaMap = {
    completed: { label: '完成', type: 'success' },
    interrupted: { label: '中断', type: 'danger' },
    in_progress: { label: '进行中', type: 'warning' },
  }
  return metaMap[status] || { label: status || '未知', type: 'info' }
}

onMounted(fetchData)
</script>

<style scoped>
.overview-row,
.detail-stats,
.detail-content {
  margin-bottom: 16px;
}

.overview-card,
.detail-stat-card {
  border-radius: 16px;
}

.task-card,
.config-card,
.summary-card,
.history-card {
  border-radius: 18px;
}

.card-header,
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions,
.user-info {
  display: flex;
  align-items: center;
}

.detail-title {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
}

.detail-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
}

.config-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.summary-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  font-size: 14px;
  color: #606266;
}

.summary-item strong {
  color: #303133;
  text-align: right;
}

.warn-text {
  color: #f56c6c;
  font-weight: 600;
}

.empty-text {
  padding: 24px 0;
  text-align: center;
  color: #909399;
}
</style>
