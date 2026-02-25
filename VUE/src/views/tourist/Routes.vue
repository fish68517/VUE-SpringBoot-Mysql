<template>
  <div class="routes-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>旅游路线推荐</span>
        </div>
      </template>
      
      <!-- 推荐方式选择 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 按天数推荐 -->
        <el-tab-pane label="按天数推荐" name="duration">
          <el-row :gutter="20" style="margin-bottom: 20px">
            <el-col :xs="24" :sm="12" :md="6">
              <el-input-number 
                v-model="filterForm.days" 
                :min="1" 
                :max="30"
                placeholder="旅游天数"
              />
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <el-button type="primary" @click="handleSearchByDuration" :loading="loading">搜索推荐</el-button>
            </el-col>
          </el-row>
        </el-tab-pane>
        
        <!-- 广州特色路线 -->
        <el-tab-pane label="广州特色路线" name="guangzhou">
          <el-row :gutter="20" style="margin-bottom: 20px">
            <el-col :xs="24" :sm="12" :md="6">
              <el-button type="primary" @click="handleGetGuangzhouSpecial" :loading="loading">获取广州特色路线</el-button>
            </el-col>
          </el-row>
        </el-tab-pane>
        
        <!-- 优先推荐 -->
        <el-tab-pane label="优先推荐" name="priority">
          <el-row :gutter="20" style="margin-bottom: 20px">
            <el-col :xs="24" :sm="12" :md="6">
              <el-input-number 
                v-model="filterForm.priorityDays" 
                :min="0" 
                :max="30"
                placeholder="旅游天数（可选）"
              />
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <el-button type="primary" @click="handleGetPriorityRecommended" :loading="loading">获取优先推荐</el-button>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>

      <!-- 路线列表 -->
      <el-row :gutter="20" v-if="routes.length > 0">
        <el-col :xs="24" :sm="12" :md="12" v-for="route in routes" :key="route.id">
          <el-card class="route-card" @click="handleViewDetail(route.id)">
            <h3>{{ route.name }}</h3>
            <div class="route-info">
              <div class="info-item">
                <span class="label">天数:</span>
                <span>{{ route.durationDays }} 天</span>
              </div>
              <div class="info-item">
                <span class="label">价格:</span>
                <span class="price">¥{{ route.totalPrice }}</span>
              </div>
              <div class="info-item">
                <span class="label">景点数:</span>
                <span>{{ route.items ? route.items.length : 0 }} 个</span>
              </div>
              <div class="info-item" v-if="route.description">
                <span class="label">描述:</span>
                <span class="description">{{ route.description }}</span>
              </div>
            </div>
            <el-button type="primary" size="small" style="width: 100%; margin-top: 10px">
              查看详情
            </el-button>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty v-if="!loading && routes.length === 0" description="暂无推荐路线" />

      <!-- 分页 -->
      <el-pagination
        v-if="routes.length > 0"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
        style="margin-top: 20px; text-align: center"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

const activeTab = ref('duration')
const loading = ref(false)
const routes = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const filterForm = ref({
  days: 3,
  priorityDays: null
})

// 按天数推荐
const handleSearchByDuration = async () => {
  if (!filterForm.value.days) {
    ElMessage.warning('请输入旅游天数')
    return
  }
  
  loading.value = true
  try {
    const response = await fetch(
      `/routes/recommend/by-duration?durationDays=${filterForm.value.days}&page=${currentPage.value - 1}&size=${pageSize.value}`
    )
    const data = await response.json()
    
    if (data.code === 0) {
      routes.value = data.data.routes
      total.value = data.data.total
      currentPage.value = data.data.currentPage + 1
      ElMessage.success('获取推荐路线成功')
    } else {
      ElMessage.error(data.message || '获取推荐路线失败')
    }
  } catch (error) {
    ElMessage.error('获取推荐路线失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 获取广州特色路线
const handleGetGuangzhouSpecial = async () => {
  loading.value = true
  try {
    const response = await fetch(
      `/routes/recommend/guangzhou-special?page=${currentPage.value - 1}&size=${pageSize.value}`
    )
    const data = await response.json()
    
    if (data.code === 0) {
      routes.value = data.data.routes
      total.value = data.data.total
      currentPage.value = data.data.currentPage + 1
      ElMessage.success('获取广州特色路线成功')
    } else {
      ElMessage.error(data.message || '获取广州特色路线失败')
    }
  } catch (error) {
    ElMessage.error('获取广州特色路线失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 获取优先推荐路线
const handleGetPriorityRecommended = async () => {
  loading.value = true
  try {
    let url = `/routes/recommend/priority?page=${currentPage.value - 1}&size=${pageSize.value}`
    if (filterForm.value.priorityDays && filterForm.value.priorityDays > 0) {
      url += `&durationDays=${filterForm.value.priorityDays}`
    }
    
    const response = await fetch(url)
    const data = await response.json()
    
    if (data.code === 0) {
      routes.value = data.data.routes
      total.value = data.data.total
      currentPage.value = data.data.currentPage + 1
      ElMessage.success('获取优先推荐路线成功')
    } else {
      ElMessage.error(data.message || '获取优先推荐路线失败')
    }
  } catch (error) {
    ElMessage.error('获取优先推荐路线失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 标签页切换
const handleTabChange = () => {
  routes.value = []
  currentPage.value = 1
}

// 分页变化
const handlePageChange = (page) => {
  currentPage.value = page
  
  if (activeTab.value === 'duration') {
    handleSearchByDuration()
  } else if (activeTab.value === 'guangzhou') {
    handleGetGuangzhouSpecial()
  } else if (activeTab.value === 'priority') {
    handleGetPriorityRecommended()
  }
}

// 查看详情
const handleViewDetail = (routeId) => {
  router.push(`/routes/${routeId}`)
}

// 页面加载时获取默认推荐
onMounted(() => {
  handleSearchByDuration()
})
</script>

<style scoped>
.routes-container {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
  font-size: 18px;
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

.route-card h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 18px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.route-info {
  margin-bottom: 15px;
}

.info-item {
  margin: 8px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.label {
  font-weight: bold;
  color: #666;
  min-width: 60px;
}

.price {
  color: #ff6b6b;
  font-weight: bold;
  font-size: 16px;
}

.description {
  color: #999;
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}
</style>
