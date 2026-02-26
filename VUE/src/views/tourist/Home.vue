<template>
  <div class="home-container">
    <!-- 欢迎卡片 -->
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="24">
        <el-card class="welcome-card">
          <h2>欢迎来到广州旅游管理系统</h2>
          <p>发现广州的美景、美食和文化特色</p>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索功能 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :sm="24" :md="24">
        <el-card class="search-card">
          <div class="search-container">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索景点、酒店、商品..."
              clearable
              @keyup.enter="handleSearch"
            >
              <template #suffix>
                <el-icon class="is-loading" v-if="searchLoading">
                  <Loading />
                </el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch" :loading="searchLoading">
              搜索
            </el-button>
          </div>
          <div v-if="searchResults.length > 0" class="search-results">
            <div class="results-title">搜索结果</div>
            <el-row :gutter="20">
              <el-col v-for="result in searchResults" :key="`${result.type}-${result.id}`" :xs="24" :sm="12" :md="8">
                <el-card class="result-card" @click="navigateToDetail(result)">
                  <div class="result-image" v-if="result.imageUrl">
                    <img :src="result.imageUrl" :alt="result.name" />
                  </div>
                  <div class="result-info">
                    <div class="result-name">{{ result.name }}</div>
                    <div class="result-type">{{ getResultTypeLabel(result.type) }}</div>
                    <div class="result-price" v-if="result.price">¥{{ result.price }}</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 功能卡片 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="feature-card" @click="$router.push('/attractions')">
          <div class="feature-icon">🏛️</div>
          <h3>景点门票</h3>
          <p>预订广州特色景点门票</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="feature-card" @click="$router.push('/hotels')">
          <div class="feature-icon">🏨</div>
          <h3>酒店预订</h3>
          <p>预订舒适的酒店住宿</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="feature-card" @click="$router.push('/products')">
          <div class="feature-icon">🛍️</div>
          <h3>旅游商品</h3>
          <p>购买广州特色旅游商品</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="feature-card" @click="$router.push('/routes')">
          <div class="feature-icon">🗺️</div>
          <h3>推荐路线</h3>
          <p>获取个性化旅游路线推荐</p>
        </el-card>
      </el-col>
    </el-row>

    <!-- 广州特色景点推荐 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :sm="24" :md="24">
        <el-card v-loading="guangzhouAttractionsLoading">
          <template #header>
            <div class="card-header">
              <span>广州特色景点推荐</span>
              <el-link type="primary" @click="$router.push('/attractions')">查看全部</el-link>
            </div>
          </template>
          <div v-if="guangzhouAttractions.length > 0" class="attractions-grid">
            <el-row :gutter="20">
              <el-col v-for="attraction in guangzhouAttractions" :key="attraction.id" :xs="24" :sm="12" :md="8">
                <el-card class="attraction-card" @click="navigateToAttractionDetail(attraction.id)">
                  <div class="attraction-image" v-if="attraction.imageUrl">
                    <img :src="attraction.imageUrl" :alt="attraction.name" />
                  </div>
                  <div class="attraction-info">
                    <div class="attraction-name">{{ attraction.name }}</div>
                    <div class="attraction-location">📍 {{ attraction.location }}</div>
                    <div class="attraction-price">¥{{ attraction.ticketPrice }}</div>
                    <div class="attraction-tags" v-if="attraction.tags && attraction.tags.length > 0">
                      <el-tag v-for="tag in attraction.tags.slice(0, 2)" :key="tag" size="small">
                        {{ tag }}
                      </el-tag>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
          <el-empty v-else description="暂无广州特色景点" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 推荐路线 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :sm="24" :md="24">
        <el-card v-loading="routesLoading">
          <template #header>
            <div class="card-header">
              <span>推荐旅游路线</span>
              <el-link type="primary" @click="$router.push('/routes')">查看全部</el-link>
            </div>
          </template>
          <div v-if="recommendedRoutes.length > 0" class="routes-list">
            <el-row :gutter="20">
              <el-col v-for="route in recommendedRoutes" :key="route.id" :xs="24" :sm="12" :md="8">
                <el-card class="route-card" @click="navigateToRouteDetail(route.id)">
                  <div class="route-header">
                    <div class="route-name">{{ route.name }}</div>
                    <div class="route-duration">{{ route.durationDays }}天</div>
                  </div>
                  <div class="route-description">{{ route.description }}</div>
                  <div class="route-price">¥{{ route.totalPrice }}</div>
                  <div class="route-items-count">包含 {{ route.items ? route.items.length : 0 }} 个景点/酒店</div>
                </el-card>
              </el-col>
            </el-row>
          </div>
          <el-empty v-else description="暂无推荐路线" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新公告 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :sm="24" :md="24">
        <el-card v-loading="announcementsLoading">
          <template #header>
            <div class="card-header">
              <span>最新公告</span>
            </div>
          </template>
          <div v-if="announcements.length > 0" class="announcements-list">
            <div v-for="announcement in announcements" :key="announcement.id" class="announcement-item">
              <div class="announcement-title">{{ announcement.title }}</div>
              <div class="announcement-content">{{ announcement.content }}</div>
              <div class="announcement-time">{{ formatDate(announcement.createdAt) }}</div>
            </div>
          </div>
          <el-empty v-else description="暂无公告" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { announcementApi } from '@/api/announcement'
import { attractionApi } from '@/api/attraction'
import { routeApi } from '@/api/route'
import { productApi } from '@/api/product'
import { hotelApi } from '@/api/hotel'

const router = useRouter()

// 公告数据
const announcements = ref([])
const announcementsLoading = ref(false)

// 广州特色景点数据
const guangzhouAttractions = ref([])
const guangzhouAttractionsLoading = ref(false)

// 推荐路线数据
const recommendedRoutes = ref([])
const routesLoading = ref(false)

// 搜索数据
const searchKeyword = ref('')
const searchResults = ref([])
const searchLoading = ref(false)

// 获取公告列表
const fetchAnnouncements = async () => {
  announcementsLoading.value = true
  try {
    const response = await announcementApi.getAnnouncements({ page: 0, size: 5 })
    
    if (response.code === '0') {
      announcements.value = response.data.announcements || []
    } else {
      ElMessage.error(response.message || '获取公告失败')
    }
  } catch (error) {
    ElMessage.error('获取公告失败: ' + error.message)
  } finally {
    announcementsLoading.value = false
  }
}

// 获取广州特色景点
const fetchGuangzhouAttractions = async () => {
  guangzhouAttractionsLoading.value = true
  try {
    const response = await attractionApi.getAttractions({ 
      page: 0, 
      size: 6,
      isGuangzhouSpecial: true 
    })
    // console.log("广州特色：" ,JSON.stringify(response));
    if (response.code === '0') {
      guangzhouAttractions.value = response.data.attractions || []
      // console.log("获取广州特色景点：" ,JSON.stringify(guangzhouAttractions.value));
    } else {
      // console.log("获取广州特色景点失败：" ,JSON.stringify(response));
      ElMessage.error(response.message || '获取广州特色景点失败')
    }
  } catch (error) {
    ElMessage.error('获取广州特色景点失败: ' + error.message)
  } finally {
  
    guangzhouAttractionsLoading.value = false
  }
}

// 获取推荐路线
const fetchRecommendedRoutes = async () => {
  routesLoading.value = true
  try {
    const response = await routeApi.getRoutes({ page: 0, size: 6 })
    
    if (response.code === '0') {
      recommendedRoutes.value = response.data.routes || []
    } else {
      ElMessage.error(response.message || '获取推荐路线失败')
    }
  } catch (error) {
    ElMessage.error('获取推荐路线失败: ' + error.message)
  } finally {
    routesLoading.value = false
  }
}

// 搜索功能
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  searchLoading.value = true
  searchResults.value = []

  try {
    // 并行搜索景点、酒店和商品
    const [attractionsRes, hotelsRes, productsRes] = await Promise.all([
      attractionApi.getAttractions({ keyword: searchKeyword.value, page: 0, size: 3 }).catch(() => ({ code: -1 })),
      hotelApi.getHotels({ keyword: searchKeyword.value, page: 0, size: 3 }).catch(() => ({ code: -1 })),
      productApi.getProducts({ keyword: searchKeyword.value, page: 0, size: 3 }).catch(() => ({ code: -1 }))
    ])

    // 处理景点搜索结果
    if (attractionsRes.code === '0' && attractionsRes.data.attractions) {
      attractionsRes.data.attractions.forEach(attraction => {
        searchResults.value.push({
          id: attraction.id,
          type: 'attraction',
          name: attraction.name,
          imageUrl: attraction.imageUrl,
          price: attraction.ticketPrice
        })
      })
    }

    // 处理酒店搜索结果
    if (hotelsRes.code === '0' && hotelsRes.data.hotels) {
      hotelsRes.data.hotels.forEach(hotel => {
        searchResults.value.push({
          id: hotel.id,
          type: 'hotel',
          name: hotel.name,
          imageUrl: hotel.imageUrl,
          price: hotel.rating
        })
      })
    }

    // 处理商品搜索结果
    if (productsRes.code === '0' && productsRes.data.products) {
      productsRes.data.products.forEach(product => {
        searchResults.value.push({
          id: product.id,
          type: 'product',
          name: product.name,
          imageUrl: product.imageUrl,
          price: product.price
        })
      })
    }

    if (searchResults.value.length === 0) {
      ElMessage.info('未找到相关结果')
    }
  } catch (error) {
    ElMessage.error('搜索失败: ' + error.message)
  } finally {
    searchLoading.value = false
  }
}

// 获取搜索结果类型标签
const getResultTypeLabel = (type) => {
  const labels = {
    'attraction': '景点',
    'hotel': '酒店',
    'product': '商品'
  }
  return labels[type] || type
}

// 导航到详情页面
const navigateToDetail = (result) => {
  if (result.type === 'attraction') {
    navigateToAttractionDetail(result.id)
  } else if (result.type === 'hotel') {
    navigateToHotelDetail(result.id)
  } else if (result.type === 'product') {
    navigateToProductDetail(result.id)
  }
}

// 导航到景点详情
const navigateToAttractionDetail = (id) => {
  router.push(`/attractions/${id}`)
}

// 导航到酒店详情
const navigateToHotelDetail = (id) => {
  router.push(`/hotels/${id}`)
}

// 导航到商品详情
const navigateToProductDetail = (id) => {
  router.push(`/products/${id}`)
}

// 导航到路线详情
const navigateToRouteDetail = (id) => {
  router.push(`/routes/${id}`)
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

// 初始化
onMounted(() => {
  fetchAnnouncements()
  fetchGuangzhouAttractions()
  fetchRecommendedRoutes()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.welcome-card h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
}

.welcome-card p {
  margin: 0;
  font-size: 16px;
}

/* 搜索卡片样式 */
.search-card {
  margin-bottom: 20px;
}

.search-container {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-container :deep(.el-input) {
  flex: 1;
}

.search-container .el-button {
  width: 100px;
}

.search-results {
  margin-top: 20px;
}

.results-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.result-card {
  cursor: pointer;
  transition: all 0.3s ease;
  height: 100%;
}

.result-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.result-image {
  width: 100%;
  height: 150px;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 10px;
}

.result-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.result-info {
  padding: 10px 0;
}

.result-name {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.result-type {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}

.result-price {
  font-size: 14px;
  color: #e74c3c;
  font-weight: bold;
}

/* 功能卡片样式 */
.feature-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.feature-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.feature-card h3 {
  margin: 10px 0;
  color: #333;
}

.feature-card p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 卡片头部样式 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  color: #333;
}

/* 广州特色景点样式 */
.attractions-grid {
  width: 100%;
}

.attraction-card {
  cursor: pointer;
  transition: all 0.3s ease;
  height: 100%;
}

.attraction-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.attraction-image {
  width: 100%;
  height: 180px;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 10px;
}

.attraction-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.attraction-info {
  padding: 10px 0;
}

.attraction-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attraction-location {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}

.attraction-price {
  font-size: 16px;
  color: #e74c3c;
  font-weight: bold;
  margin-bottom: 8px;
}

.attraction-tags {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

/* 推荐路线样式 */
.routes-list {
  width: 100%;
}

.route-card {
  cursor: pointer;
  transition: all 0.3s ease;
  height: 100%;
}

.route-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.route-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.route-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.route-duration {
  font-size: 13px;
  color: #999;
  background-color: #f5f7fa;
  padding: 4px 8px;
  border-radius: 4px;
  white-space: nowrap;
}

.route-description {
  font-size: 13px;
  color: #666;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}

.route-price {
  font-size: 16px;
  color: #e74c3c;
  font-weight: bold;
  margin-bottom: 8px;
}

.route-items-count {
  font-size: 12px;
  color: #999;
}

/* 公告列表样式 */
.announcements-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.announcement-item {
  padding: 15px;
  border-left: 4px solid #667eea;
  background-color: #f5f7fa;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.announcement-item:hover {
  background-color: #eef2f8;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.announcement-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.announcement-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.announcement-time {
  font-size: 12px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .home-container {
    padding: 15px;
  }

  .welcome-card h2 {
    font-size: 24px;
  }

  .welcome-card p {
    font-size: 14px;
  }

  .search-container {
    gap: 8px;
  }

  .search-container .el-button {
    width: 90px;
  }

  .feature-icon {
    font-size: 36px;
  }

  .feature-card h3 {
    font-size: 14px;
  }

  .feature-card p {
    font-size: 12px;
  }
}

@media (max-width: 768px) {
  .home-container {
    padding: 10px;
  }

  .welcome-card h2 {
    font-size: 20px;
  }

  .welcome-card p {
    font-size: 13px;
  }

  .search-container {
    flex-direction: column;
    gap: 10px;
  }

  .search-container :deep(.el-input) {
    width: 100%;
  }

  .search-container .el-button {
    width: 100%;
  }

  .result-card {
    margin-bottom: 10px;
  }

  .result-image {
    height: 120px;
  }

  .result-name {
    font-size: 13px;
  }

  .result-type {
    font-size: 11px;
  }

  .result-price {
    font-size: 13px;
  }

  .feature-icon {
    font-size: 32px;
  }

  .feature-card h3 {
    font-size: 13px;
    margin: 8px 0;
  }

  .feature-card p {
    font-size: 11px;
  }

  .attraction-image {
    height: 150px;
  }

  .attraction-name {
    font-size: 14px;
  }

  .attraction-location {
    font-size: 12px;
  }

  .attraction-price {
    font-size: 14px;
  }

  .route-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .route-duration {
    margin-top: 5px;
  }

  .route-name {
    font-size: 14px;
  }

  .route-description {
    font-size: 12px;
  }

  .route-price {
    font-size: 14px;
  }

  .route-items-count {
    font-size: 11px;
  }

  .announcement-item {
    padding: 12px;
  }

  .announcement-title {
    font-size: 14px;
  }

  .announcement-content {
    font-size: 12px;
  }

  .announcement-time {
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .home-container {
    padding: 8px;
  }

  .welcome-card h2 {
    font-size: 18px;
  }

  .welcome-card p {
    font-size: 12px;
  }

  .search-container {
    flex-direction: column;
    gap: 8px;
  }

  .search-container :deep(.el-input) {
    width: 100%;
  }

  .search-container .el-button {
    width: 100%;
    font-size: 12px;
  }

  .results-title {
    font-size: 14px;
  }

  .result-card {
    margin-bottom: 8px;
  }

  .result-image {
    height: 100px;
  }

  .result-info {
    padding: 8px 0;
  }

  .result-name {
    font-size: 12px;
  }

  .result-type {
    font-size: 10px;
  }

  .result-price {
    font-size: 12px;
  }

  .feature-icon {
    font-size: 28px;
    margin-bottom: 8px;
  }

  .feature-card h3 {
    font-size: 12px;
    margin: 6px 0;
  }

  .feature-card p {
    font-size: 10px;
  }

  .card-header {
    font-size: 14px;
  }

  .card-header :deep(.el-link) {
    font-size: 12px;
  }

  .attraction-image {
    height: 120px;
  }

  .attraction-name {
    font-size: 13px;
  }

  .attraction-location {
    font-size: 11px;
  }

  .attraction-price {
    font-size: 13px;
  }

  .attraction-tags {
    gap: 3px;
  }

  .attraction-tags :deep(.el-tag) {
    font-size: 10px;
  }

  .route-card {
    padding: 12px;
  }

  .route-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }

  .route-name {
    font-size: 13px;
  }

  .route-duration {
    font-size: 11px;
    padding: 3px 6px;
  }

  .route-description {
    font-size: 11px;
    margin-bottom: 8px;
  }

  .route-price {
    font-size: 13px;
  }

  .route-items-count {
    font-size: 10px;
  }

  .announcement-item {
    padding: 10px;
    border-left-width: 3px;
  }

  .announcement-title {
    font-size: 13px;
  }

  .announcement-content {
    font-size: 11px;
    -webkit-line-clamp: 1;
  }

  .announcement-time {
    font-size: 10px;
  }
}

@media (max-width: 360px) {
  .home-container {
    padding: 6px;
  }

  .welcome-card h2 {
    font-size: 16px;
  }

  .welcome-card p {
    font-size: 11px;
  }

  .feature-icon {
    font-size: 24px;
  }

  .feature-card h3 {
    font-size: 11px;
  }

  .feature-card p {
    font-size: 9px;
  }

  .result-image {
    height: 80px;
  }

  .attraction-image {
    height: 100px;
  }

  .announcement-item {
    padding: 8px;
  }

  .announcement-title {
    font-size: 12px;
  }

  .announcement-content {
    font-size: 10px;
  }
}
</style>
