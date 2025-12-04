<template>
  <div class="home-container">
    <div class="home-header">
      <h1>沈阳音乐节</h1>
      <div class="user-info">
        <span>欢迎，{{ userStore.userInfo?.phone }}</span>
        <el-button type="primary" size="small" @click="handleGoToTicket">购票</el-button>
        <el-button type="primary" size="small" @click="handleGoToShop">商城</el-button>
        <el-button type="primary" size="small" @click="handleGoToOrders">我的订单</el-button>
        <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
      </div>
    </div>

    <div class="home-content">
      <!-- Carousel Section -->
      <Carousel :items="carouselItems" />

      <!-- Countdown Section -->
      <Countdown :target-date="festivalStartDate" />

      <!-- Artist List Section -->
      <ArtistList :artists="artists" @select-artist="handleSelectArtist" />

      <!-- Festival Info Section -->
      <div v-if="festivalInfo" class="festival-info-section">
        <div class="section-title">
          <h2>音乐节介绍</h2>
        </div>
        <div class="festival-info-card">
          <p>{{ festivalInfo.description }}</p>
          <div class="festival-details">
            <div class="detail-item">
              <span class="label">时间：</span>
              <span class="value">{{ formatDate(festivalInfo.startDate) }} - {{ formatDate(festivalInfo.endDate) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">地点：</span>
              <span class="value">{{ festivalInfo.location }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { festivalApi } from '@/api/festival'
import { formatDate } from '@/utils/date'
import Carousel from '@/components/Carousel.vue'
import Countdown from '@/components/Countdown.vue'
import ArtistList from '@/components/ArtistList.vue'

interface CarouselItem {
  image: string
  title: string
  description: string
}

interface Artist {
  id: number
  name: string
  description: string
  imageUrl: string
  isLocalBand: boolean
}

interface FestivalInfo {
  id: number
  name: string
  description: string
  startDate: string
  endDate: string
  location: string
  posterUrl: string
  status: string
}

const router = useRouter()
const userStore = useUserStore()

const carouselItems = ref<CarouselItem[]>([
  {
    image: 'https://via.placeholder.com/1200x400?text=沈阳音乐节',
    title: '沈阳音乐节',
    description: '汇聚国内外顶级艺人，打造沉浸式音乐体验',
  },
  {
    image: 'https://via.placeholder.com/1200x400?text=音乐盛宴',
    title: '音乐盛宴',
    description: '融合沈阳本地文化，展现满族风情',
  },
  {
    image: 'https://via.placeholder.com/1200x400?text=打卡盛京',
    title: '打卡盛京',
    description: '完成打卡任务，赢取积分奖励',
  },
])

const artists = ref<Artist[]>([])
const festivalInfo = ref<FestivalInfo | null>(null)
const festivalStartDate = ref<string>('')

const loadFestivalData = async () => {
  try {
    // Load festival info
    const festivalResponse = await festivalApi.getFestivalInfo()
    festivalInfo.value = festivalResponse.data
    festivalStartDate.value = festivalResponse.data.startDate

    // Load artists
    const artistsResponse = await festivalApi.getArtists()
    artists.value = artistsResponse.data
  } catch (error) {
    ElMessage.error('加载数据失败')
    console.error(error)
  }
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}

const handleSelectArtist = (artist: Artist) => {
  router.push(`/artist/${artist.id}`)
}

const handleGoToTicket = () => {
  router.push('/ticket-purchase')
}

const handleGoToShop = () => {
  router.push('/shop')
}

const handleGoToOrders = () => {
  router.push('/orders')
}

onMounted(() => {
  loadFestivalData()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.home-header {
  background: white;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.home-header h1 {
  color: #333;
  margin: 0;
  font-size: 24px;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info span {
  color: #666;
  font-size: 14px;
}

.home-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.festival-info-section {
  margin-top: 40px;
}

.section-title {
  text-align: center;
  margin-bottom: 30px;
}

.section-title h2 {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin: 0 0 10px 0;
}

.festival-info-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.festival-info-card p {
  font-size: 16px;
  color: #666;
  line-height: 1.8;
  margin: 0 0 20px 0;
}

.festival-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.detail-item .label {
  font-weight: bold;
  color: #333;
  min-width: 60px;
}

.detail-item .value {
  color: #666;
}

@media (max-width: 768px) {
  .home-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .user-info {
    width: 100%;
    justify-content: space-between;
  }

  .home-content {
    padding: 20px 15px;
  }

  .festival-info-card {
    padding: 20px;
  }

  .festival-details {
    grid-template-columns: 1fr;
  }
}
</style>
