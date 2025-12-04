<template>
  <div class="artist-detail-container">
    <div class="artist-detail-header">
      <el-button type="primary" @click="handleBack" class="back-button">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>

    <el-skeleton v-if="loading" :rows="5" animated />

    <div v-else-if="artist" class="artist-detail-content">
      <div class="artist-detail-image">
        <img :src="artist.imageUrl" :alt="artist.name" />
        <div v-if="artist.isLocalBand" class="local-band-badge">沈阳本土乐队</div>
      </div>

      <div class="artist-detail-info">
        <h1>{{ artist.name }}</h1>
        <p class="artist-description">{{ artist.description }}</p>

        <div class="artist-schedule" v-if="schedule.length > 0">
          <h3>演出日程</h3>
          <div class="schedule-list">
            <div v-for="item in schedule" :key="item.id" class="schedule-item">
              <div class="schedule-time">
                <span class="stage-name">{{ item.stageName }}</span>
                <span class="time">{{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="artist-actions">
          <el-button type="primary" size="large" @click="handleBuyTicket">购票</el-button>
          <el-button size="large" @click="handleFollow">关注</el-button>
        </div>
      </div>
    </div>

    <div v-else class="no-data">
      <p>艺人信息不存在</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { festivalApi } from '@/api/festival'
import { formatDate } from '@/utils/date'

interface Artist {
  id: number
  name: string
  description: string
  imageUrl: string
  isLocalBand: boolean
}

interface Schedule {
  id: number
  stageName: string
  startTime: string
  endTime: string
  artistId: number
}

const router = useRouter()
const route = useRoute()

const artist = ref<Artist | null>(null)
const schedule = ref<Schedule[]>([])
const loading = ref(true)

const formatTime = (time: string) => {
  return formatDate(time, 'HH:mm')
}

const loadArtistDetail = async () => {
  try {
    loading.value = true
    const artistId = route.params.id as string
    const response = await festivalApi.getArtistById(Number(artistId))
    artist.value = response.data
  } catch (error) {
    ElMessage.error('加载艺人信息失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleBack = () => {
  router.back()
}

const handleBuyTicket = () => {
  router.push('/tickets')
}

const handleFollow = () => {
  ElMessage.success('已关注该艺人')
}

onMounted(() => {
  loadArtistDetail()
})
</script>

<style scoped>
.artist-detail-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px;
}

.artist-detail-header {
  margin-bottom: 20px;
}

.back-button {
  margin-bottom: 20px;
}

.artist-detail-content {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  padding: 40px;
}

.artist-detail-image {
  position: relative;
}

.artist-detail-image img {
  width: 100%;
  height: auto;
  border-radius: 12px;
  object-fit: cover;
}

.local-band-badge {
  position: absolute;
  top: 20px;
  right: 20px;
  background: #ff6b6b;
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
}

.artist-detail-info h1 {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin: 0 0 15px 0;
}

.artist-description {
  font-size: 16px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 30px 0;
}

.artist-schedule {
  margin-bottom: 30px;
}

.artist-schedule h3 {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0 0 15px 0;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.schedule-item {
  background: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
  border-left: 4px solid #667eea;
}

.schedule-time {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stage-name {
  font-weight: bold;
  color: #333;
}

.time {
  color: #999;
  font-size: 14px;
}

.artist-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}

.artist-actions :deep(.el-button) {
  flex: 1;
}

.no-data {
  text-align: center;
  padding: 40px;
  color: #999;
}

@media (max-width: 768px) {
  .artist-detail-content {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }

  .artist-detail-info h1 {
    font-size: 24px;
  }

  .artist-actions {
    flex-direction: column;
  }
}
</style>
