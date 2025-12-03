<template>
  <div class="coaches-container">
    <div class="page-header">
      <h1>找教练</h1>
      <p class="subtitle">发现专业的健身教练，为您量身定制训练计划</p>
    </div>

    <!-- 搜索栏 (Search Bar) -->
    <div class="search-section">
      <el-input
        v-model="searchQuery"
        placeholder="搜索教练姓名..."
        :prefix-icon="Search"
        clearable
        class="search-input"
        @input="handleSearch"
      />
    </div>

    <!-- 加载状态 (Loading) -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated count="4" class="skeleton-grid" />
    </div>

    <!-- 教练列表 (Coach Grid) -->
    <div v-else-if="filteredCoaches.length > 0" class="coach-grid">
      <el-card
        v-for="coach in filteredCoaches"
        :key="coach.id"
        class="coach-card"
        shadow="hover"
        @click="showCoachDetails(coach)"
      >
        <div class="coach-card-body">
          <div class="coach-avatar-wrapper">
            <el-avatar :size="80" :src="coach.avatar || '/default-avatar.png'" class="coach-avatar">
              {{ coach.username?.charAt(0).toUpperCase() }}
            </el-avatar>
          </div>
          <div class="coach-info">
            <h3 class="coach-name">{{ coach.username }}</h3>
            <div class="coach-tags">
              <el-tag size="small" type="success" effect="plain">专业教练</el-tag>
              <el-tag v-if="coach.gender" size="small" type="info" effect="plain">
                {{ formatGender(coach.gender) }}
              </el-tag>
            </div>
            <p class="coach-intro">{{ truncateText(coach.intro || '暂无简介', 50) }}</p>
          </div>
          <div class="card-footer">
            <el-button type="primary" plain round size="small">查看详情</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 空状态 (Empty State) -->
    <div v-else class="empty-state">
      <el-empty description="暂无符合条件的教练" />
    </div>

    <!-- 教练详情弹窗 (Coach Detail Dialog) -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedCoach?.username"
      width="500px"
      destroy-on-close
      center
      class="coach-dialog"
    >
      <div v-if="selectedCoach" class="coach-detail">
        <div class="detail-header">
          <el-avatar :size="100" :src="selectedCoach.avatar || '/default-avatar.png'">
            {{ selectedCoach.username?.charAt(0).toUpperCase() }}
          </el-avatar>
          <h2 class="detail-name">{{ selectedCoach.username }}</h2>
          <div class="detail-tags">
            <el-tag type="warning">认证教练</el-tag>
            <el-tag v-if="selectedCoach.gender" type="info">{{ formatGender(selectedCoach.gender) }}</el-tag>
          </div>
        </div>

        <div class="detail-section">
          <h3>个人简介</h3>
          <p class="detail-intro">{{ selectedCoach.intro || '这位教练很神秘，还没有写简介。' }}</p>
        </div>

        <div class="detail-section" v-if="selectedCoach.specialties">
          <h3>擅长领域</h3>
          <div class="specialties-list">
            <el-tag 
              v-for="(tag, index) in (selectedCoach.specialties ? selectedCoach.specialties.split(',') : [])" 
              :key="index" 
              class="specialty-tag"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>

        <div class="detail-stats">
          <div class="stat-item">
            <div class="stat-value">{{ selectedCoach.studentCount || 0 }}</div>
            <div class="stat-label">学员</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ selectedCoach.contentCount || 0 }}</div>
            <div class="stat-label">内容发布</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">5.0</div>
            <div class="stat-label">评分</div>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleContact">联系教练</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
// Adding .js extension to imports to ensure correct module resolution
import { getCoaches, getCoachById } from '@/api/coach.js'
import { showSuccess, showError, showInfo } from '@/utils/feedback.js'

const coaches = ref([])
const loading = ref(false)
const searchQuery = ref('')
const dialogVisible = ref(false)
const selectedCoach = ref(null)

// 过滤教练列表
const filteredCoaches = computed(() => {
  if (!searchQuery.value) return coaches.value
  const query = searchQuery.value.toLowerCase()
  return coaches.value.filter(coach => 
    coach.username.toLowerCase().includes(query) ||
    (coach.intro && coach.intro.toLowerCase().includes(query))
  )
})

// 获取所有教练
const fetchCoaches = async () => {
  loading.value = true
  try {
    const response = await getCoaches()
    coaches.value = Array.isArray(response) ? response : (response.content || [])
  } catch (error) {
    console.error('获取教练列表失败:', error)
    showError('无法加载教练列表')
  } finally {
    loading.value = false
  }
}

// 显示教练详情
const showCoachDetails = async (coach) => {
  selectedCoach.value = coach
  dialogVisible.value = true
  
  try {
    const detail = await getCoachById(coach.id)
    selectedCoach.value = { ...coach, ...detail }
  } catch (error) {
    console.error('获取教练详情失败:', error)
  }
}

const handleSearch = () => {
  // Logic handled by computed
}

const handleContact = () => {
  showSuccess('已发送联系请求！')
  dialogVisible.value = false
}

const formatGender = (gender) => {
  const map = {
    'Male': '男',
    'Female': '女',
    'Other': '其他'
  }
  return map[gender] || '未知'
}

const truncateText = (text, maxLength) => {
  if (!text) return ''
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

onMounted(() => {
  fetchCoaches()
})
</script>

<style scoped>
.coaches-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 32px;
  color: #303133;
}

.subtitle {
  color: #909399;
  font-size: 16px;
}

.search-section {
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
}

.search-input {
  width: 100%;
  max-width: 500px;
}

.coach-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.coach-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  border-radius: 12px;
  overflow: hidden;
}

.coach-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.coach-card-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 10px;
}

.coach-avatar-wrapper {
  margin-bottom: 16px;
}

.coach-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.coach-tags {
  display: flex;
  gap: 8px;
  justify-content: center;
  margin-bottom: 12px;
}

.coach-intro {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  height: 42px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 16px;
}

.card-footer {
  margin-top: auto;
  width: 100%;
}

.loading-container {
  padding: 40px 0;
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.empty-state {
  padding: 60px 0;
}

.coach-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.detail-name {
  margin: 16px 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.detail-tags {
  display: flex;
  gap: 8px;
}

.detail-section h3 {
  font-size: 16px;
  color: #303133;
  margin: 0 0 12px 0;
  padding-left: 10px;
  border-left: 4px solid #409EFF;
}

.detail-intro {
  color: #606266;
  line-height: 1.6;
  background-color: #f8f9fa;
  padding: 12px;
  border-radius: 8px;
}

.specialties-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
  border-top: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .coaches-container {
    padding: 16px;
  }
  
  .coach-grid {
    grid-template-columns: 1fr;
  }
}
</style>