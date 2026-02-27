<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>欢迎使用自习室预约系统</span>
          </template>
          <div class="welcome-content">
            <p>您好，{{ userStore.user?.nickname || userStore.user?.username }}！</p>
            <p>当前时间：{{ currentTime }}</p>
            <el-button type="primary" size="large" @click="router.push('/reserve')">
              立即预约座位
            </el-button>
          </div>
        </el-card>

        <el-card style="margin-top: 20px">
          <template #header>
            <span>今日预约</span>
          </template>
          <el-table :data="todayReservations" v-loading="loading">
            <el-table-column prop="seatNo" label="座位" width="100">
              <template #default="{ row }">
                {{ row.area }}-{{ row.seatNo }}
              </template>
            </el-table-column>
            <el-table-column prop="startTime" label="时间段">
              <template #default="{ row }">
                {{ row.startTime }} - {{ row.endTime }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button v-if="row.status === 1" type="success" size="small" @click="handleCheckIn(row)">
                  签到
                </el-button>
                <el-button v-if="row.status === 2" type="warning" size="small" @click="handleCheckOut(row)">
                  签退
                </el-button>
                <el-button v-if="row.status === 1" type="danger" size="small" @click="handleCancel(row)">
                  取消
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <template #header>
            <span>学习统计</span>
          </template>
          <div class="study-stats">
            <div class="stat-item">
              <div class="stat-value">{{ formatTime(userStore.user?.monthlyStudyTime || 0) }}</div>
              <div class="stat-label">本月学习时长</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ formatTime(userStore.user?.totalStudyTime || 0) }}</div>
              <div class="stat-label">累计学习时长</div>
            </div>
          </div>
        </el-card>

        <el-card style="margin-top: 20px">
          <template #header>
            <span>本月奖章</span>
          </template>
          <div class="badge-container">
            <div class="badge" :class="badgeClass">
              <span class="badge-icon">{{ badgeIcon }}</span>
              <span class="badge-name">{{ badgeName }}</span>
            </div>
            <p class="badge-tip">{{ badgeTip }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { getMyReservations, checkIn, checkOut, cancelReservation } from '../api/reservation'
import { getUserInfo } from '../api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const todayReservations = ref([])
const currentTime = ref('')

const formatTime = (minutes) => {
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return `${hours}小时${mins}分钟`
}

const statusType = (status) => {
  const types = { 0: 'info', 1: 'primary', 2: 'success', 3: 'info' }
  return types[status]
}

const statusText = (status) => {
  const texts = { 0: '已取消', 1: '已预约', 2: '使用中', 3: '已完成' }
  return texts[status]
}

const monthlyHours = computed(() => (userStore.user?.monthlyStudyTime || 0) / 60)

const badgeClass = computed(() => {
  if (monthlyHours.value >= 100) return 'diamond'
  if (monthlyHours.value >= 60) return 'gold'
  if (monthlyHours.value >= 30) return 'silver'
  if (monthlyHours.value >= 10) return 'bronze'
  return 'none'
})

const badgeIcon = computed(() => {
  if (monthlyHours.value >= 100) return '💎'
  if (monthlyHours.value >= 60) return '🥇'
  if (monthlyHours.value >= 30) return '🥈'
  if (monthlyHours.value >= 10) return '🥉'
  return '🎯'
})

const badgeName = computed(() => {
  if (monthlyHours.value >= 100) return '钻石学霸'
  if (monthlyHours.value >= 60) return '金牌学员'
  if (monthlyHours.value >= 30) return '银牌学员'
  if (monthlyHours.value >= 10) return '铜牌学员'
  return '努力中'
})

const badgeTip = computed(() => {
  if (monthlyHours.value >= 100) return '太棒了！你是本月的学习之星！'
  if (monthlyHours.value >= 60) return '优秀！距离钻石章还需' + (100 - monthlyHours.value).toFixed(0) + '小时'
  if (monthlyHours.value >= 30) return '很好！距离金章还需' + (60 - monthlyHours.value).toFixed(0) + '小时'
  if (monthlyHours.value >= 10) return '加油！距离银章还需' + (30 - monthlyHours.value).toFixed(0) + '小时'
  return '距离铜章还需' + (10 - monthlyHours.value).toFixed(0) + '小时'
})

const loadTodayReservations = async () => {
  loading.value = true
  try {
    const res = await getMyReservations()
    const today = new Date().toISOString().split('T')[0]
    todayReservations.value = res.data.filter(r => r.date === today && r.status !== 0)
  } finally {
    loading.value = false
  }
}

const refreshUserInfo = async () => {
  const res = await getUserInfo()
  userStore.setUser(res.data)
}

const handleCheckIn = async (row) => {
  await checkIn(row.id)
  ElMessage.success('签到成功')
  loadTodayReservations()
}

const handleCheckOut = async (row) => {
  const res = await checkOut(row.id)
  ElMessage.success(res.message)
  loadTodayReservations()
  refreshUserInfo()
}

const handleCancel = async (row) => {
  await ElMessageBox.confirm('确定要取消这个预约吗？', '提示', { type: 'warning' })
  await cancelReservation(row.id)
  ElMessage.success('已取消')
  loadTodayReservations()
}

const updateTime = () => {
  currentTime.value = new Date().toLocaleString('zh-CN')
}

onMounted(() => {
  loadTodayReservations()
  updateTime()
  setInterval(updateTime, 1000)
})
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}

.welcome-content {
  text-align: center;
  padding: 30px;
}

.welcome-content p {
  margin-bottom: 20px;
  font-size: 16px;
  color: #606266;
}

.study-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  color: #909399;
  margin-top: 8px;
}

.badge-container {
  text-align: center;
  padding: 20px;
}

.badge {
  display: inline-block;
  padding: 20px 30px;
  border-radius: 10px;
  margin-bottom: 15px;
}

.badge-icon {
  font-size: 48px;
  display: block;
}

.badge-name {
  font-size: 18px;
  font-weight: bold;
  display: block;
  margin-top: 10px;
}

.badge.diamond { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
.badge.gold { background: linear-gradient(135deg, #f5af19 0%, #f12711 100%); color: white; }
.badge.silver { background: linear-gradient(135deg, #bdc3c7 0%, #2c3e50 100%); color: white; }
.badge.bronze { background: linear-gradient(135deg, #c9a227 0%, #8b6914 100%); color: white; }
.badge.none { background: #f5f7fa; color: #909399; }

.badge-tip {
  color: #909399;
  font-size: 14px;
}
</style>
