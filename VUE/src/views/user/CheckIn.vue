<template>
  <div class="check-in-container">
    <el-card class="check-in-card">
      <template #header>
        <div class="card-header">
          <h2>Daily Check-In</h2>
        </div>
      </template>

      <!-- Check-in Button Section -->
      <div class="check-in-action">
        <el-button
          type="primary"
          size="large"
          :disabled="hasCheckedInToday"
          :loading="checkingIn"
          @click="handleCheckIn"
          class="check-in-button"
        >
          <el-icon v-if="!hasCheckedInToday && !checkingIn" class="button-icon">
            <Calendar />
          </el-icon>
          <span v-if="hasCheckedInToday">✓ Checked In Today</span>
          <span v-else>Check In Now</span>
        </el-button>

        <div v-if="hasCheckedInToday" class="success-message">
          <el-icon color="#67c23a"><SuccessFilled /></el-icon>
          <span>Great job! You've already checked in today!</span>
        </div>
      </div>

      <!-- Statistics Section -->
      <div class="stats-section">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="8">
            <div class="stat-card">
              <div class="stat-icon fire">
                <el-icon><Sunny /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ stats.currentStreak }}</div>
                <div class="stat-label">Current Streak</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="stat-card">
              <div class="stat-icon total">
                <el-icon><Calendar /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ stats.totalCount }}</div>
                <div class="stat-label">Total Check-Ins</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="stat-card">
              <div class="stat-icon record">
                <el-icon><Trophy /></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ stats.longestStreak }}</div>
                <div class="stat-label">Longest Streak</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- Calendar Section -->
    <el-card class="calendar-card">
      <template #header>
        <h3>Check-In Calendar</h3>
      </template>
      <el-calendar v-model="calendarDate">
        <template #date-cell="{ data }">
          <div class="calendar-day" :class="{ 'checked-in': isCheckedInDate(data.day) }">
            <span class="day-number">{{ data.day.split('-').slice(-1)[0] }}</span>
            <el-icon v-if="isCheckedInDate(data.day)" class="check-icon" color="#67c23a">
              <SuccessFilled />
            </el-icon>
          </div>
        </template>
      </el-calendar>
    </el-card>

    <!-- Success Animation -->
    <transition name="celebration">
      <div v-if="showCelebration" class="celebration-overlay">
        <div class="celebration-content">
          <el-icon class="celebration-icon" color="#67c23a"><SuccessFilled /></el-icon>
          <h2>Check-In Successful!</h2>
          <p v-if="stats.currentStreak > 1">🔥 {{ stats.currentStreak }} days streak!</p>
          <p v-else>Keep it up! Start your streak!</p>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Calendar, SuccessFilled, Sunny, Trophy } from '@element-plus/icons-vue'
import { performCheckIn, getCheckInHistory, getCheckInStats } from '@/api/checkin'
import { useUserStore } from '@/store/modules/user'
import { showSuccess, showError, showInfo } from '@/utils/feedback'

const userStore = useUserStore()

const checkingIn = ref(false)
const showCelebration = ref(false)
const calendarDate = ref(new Date())
const checkInHistory = ref([])
const stats = ref({
  totalCount: 0,
  currentStreak: 0,
  longestStreak: 0
})

const hasCheckedInToday = computed(() => {
  const today = new Date().toISOString().split('T')[0]
  return checkInHistory.value.some(record => {
    const recordDate = new Date(record.checkDate).toISOString().split('T')[0]
    return recordDate === today
  })
})

const isCheckedInDate = (dateString) => {
  return checkInHistory.value.some(record => {
    const recordDate = new Date(record.checkDate).toISOString().split('T')[0]
    return recordDate === dateString
  })
}

const handleCheckIn = async () => {
  if (hasCheckedInToday.value) {
    showInfo('You have already checked in today!')
    return
  }

  checkingIn.value = true
  try {
    await performCheckIn()
    
    // Show celebration animation
    showCelebration.value = true
    setTimeout(() => {
      showCelebration.value = false
    }, 3000)

    // Refresh data
    await Promise.all([
      fetchCheckInHistory(),
      fetchStats()
    ])

    showSuccess('Check-in successful! Keep up the great work!')
  } catch (error) {
    // Error already handled by request interceptor
    console.error('Check-in failed:', error)
  } finally {
    checkingIn.value = false
  }
}

const fetchCheckInHistory = async () => {
  try {
    const response = await getCheckInHistory({ page: 0, size: 365 })
    checkInHistory.value = response.content || response || []
  } catch (error) {
    console.error('Failed to fetch check-in history:', error)
  }
}

const fetchStats = async () => {
  try {
    const response = await getCheckInStats()
    stats.value = response
    userStore.checkInStats = response
  } catch (error) {
    console.error('Failed to fetch stats:', error)
  }
}

onMounted(async () => {
  await Promise.all([
    fetchCheckInHistory(),
    fetchStats()
  ])
})
</script>

<style scoped>
.check-in-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.check-in-card {
  margin-bottom: 20px;
}

.card-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.check-in-action {
  text-align: center;
  padding: 40px 20px;
}

.check-in-button {
  width: 200px;
  height: 60px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 30px;
  transition: all 0.3s ease;
}

.check-in-button:not(:disabled):hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.check-in-button:disabled {
  background-color: #67c23a;
  border-color: #67c23a;
  color: white;
  cursor: not-allowed;
}

.button-icon {
  margin-right: 8px;
  font-size: 20px;
}

.success-message {
  margin-top: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #67c23a;
  font-size: 16px;
  font-weight: 500;
}

.stats-section {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #ebeef5;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-radius: 12px;
  margin-bottom: 15px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-right: 15px;
}

.stat-icon.fire {
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
  color: #ff6b6b;
}

.stat-icon.total {
  background: linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%);
  color: #409eff;
}

.stat-icon.record {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  color: #e6a23c;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.calendar-card {
  margin-top: 20px;
}

.calendar-card h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.calendar-day {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 5px;
}

.calendar-day.checked-in {
  background-color: #f0f9ff;
  border-radius: 4px;
}

.day-number {
  font-size: 14px;
  color: #606266;
}

.check-icon {
  font-size: 16px;
  margin-top: 2px;
}

.celebration-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.celebration-content {
  background: white;
  padding: 60px 80px;
  border-radius: 20px;
  text-align: center;
  animation: bounce 0.6s ease;
}

.celebration-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.celebration-content h2 {
  font-size: 32px;
  color: #303133;
  margin: 0 0 15px 0;
}

.celebration-content p {
  font-size: 20px;
  color: #606266;
  margin: 0;
}

.celebration-enter-active,
.celebration-leave-active {
  transition: opacity 0.3s ease;
}

.celebration-enter-from,
.celebration-leave-to {
  opacity: 0;
}

@keyframes bounce {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

@media (max-width: 768px) {
  .check-in-container {
    padding: 10px;
  }

  .check-in-button {
    width: 100%;
    max-width: 300px;
  }

  .stat-card {
    padding: 15px;
  }

  .stat-icon {
    width: 50px;
    height: 50px;
    font-size: 24px;
  }

  .stat-value {
    font-size: 24px;
  }

  .celebration-content {
    padding: 40px 30px;
    margin: 20px;
  }

  .celebration-icon {
    font-size: 60px;
  }

  .celebration-content h2 {
    font-size: 24px;
  }

  .celebration-content p {
    font-size: 16px;
  }
}
</style>
