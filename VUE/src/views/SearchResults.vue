<template>
  <div class="search-results-container">
    <!-- Search Header -->
    <div class="search-header">
      <div class="search-info">
        <el-button :icon="ArrowLeft" circle @click="goBack" />
        <div class="search-query-display">
          <span class="search-label">搜索结果：</span>
          <span class="search-query">"{{ searchQuery }}"</span>
        </div>
      </div>
    </div>

    <!-- Type Filter Tabs -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="filter-tabs">
      <el-tab-pane label="全部" name="all">
        <span slot="label">
          全部 <el-badge v-if="totalCount > 0" :value="totalCount" class="tab-badge" />
        </span>
      </el-tab-pane>
      <el-tab-pane label="资源" name="resource">
        <span slot="label">
          资源 <el-badge v-if="resourceCount > 0" :value="resourceCount" class="tab-badge" />
        </span>
      </el-tab-pane>
      <el-tab-pane label="动态" name="post">
        <span slot="label">
          动态 <el-badge v-if="postCount > 0" :value="postCount" class="tab-badge" />
        </span>
      </el-tab-pane>
      <el-tab-pane label="教练" name="coach">
        <span slot="label">
          教练 <el-badge v-if="coachCount > 0" :value="coachCount" class="tab-badge" />
        </span>
      </el-tab-pane>
    </el-tabs>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" :size="40"><loading /></el-icon>
      <p>搜索中...</p>
    </div>

    <!-- Results Content -->
    <div v-else-if="!loading && hasResults" class="results-content">
      <!-- All Results View -->
      <div v-if="activeTab === 'all'">
        <!-- Resources Section -->
        <div v-if="resourceResults.length > 0" class="results-section">
          <div class="section-header">
            <h3>资源 ({{ resourceResults.length }})</h3>
          </div>
          <div class="resource-grid">
            <resource-card
              v-for="resource in resourceResults"
              :key="resource.id"
              :resource="resource"
            />
          </div>
        </div>

        <!-- Posts Section -->
        <div v-if="postResults.length > 0" class="results-section">
          <div class="section-header">
            <h3>动态 ({{ postResults.length }})</h3>
          </div>
          <div class="post-list">
            <post-card
              v-for="post in postResults"
              :key="post.id"
              :post="post"
              @refresh="performSearch"
            />
          </div>
        </div>

        <!-- Coaches Section -->
        <div v-if="coachResults.length > 0" class="results-section">
          <div class="section-header">
            <h3>教练 ({{ coachResults.length }})</h3>
          </div>
          <div class="coach-grid">
            <el-card
              v-for="coach in coachResults"
              :key="coach.id"
              class="coach-card"
              shadow="hover"
              @click="viewCoachProfile(coach.id)"
            >
              <div class="coach-content">
                <el-avatar :size="60" :src="coach.avatar">
                  {{ coach.username?.charAt(0).toUpperCase() }}
                </el-avatar>
                <div class="coach-info">
                  <h4 class="coach-name">{{ coach.username }}</h4>
                  <p class="coach-intro">{{ coach.intro || '暂无简介' }}</p>
                  <div class="coach-stats">
                    <el-icon><user /></el-icon>
                    <span>{{ coach.studentCount || 0 }} 学员</span>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </div>

      <!-- Resources Only View -->
      <div v-else-if="activeTab === 'resource'" class="results-section">
        <div class="section-header">
          <h3>资源结果 ({{ resourceResults.length }})</h3>
        </div>
        <div class="resource-grid">
          <resource-card
            v-for="resource in resourceResults"
            :key="resource.id"
            :resource="resource"
          />
        </div>
      </div>

      <!-- Posts Only View -->
      <div v-else-if="activeTab === 'post'" class="results-section">
        <div class="section-header">
          <h3>动态结果 ({{ postResults.length }})</h3>
        </div>
        <div class="post-list">
          <post-card
            v-for="post in postResults"
            :key="post.id"
            :post="post"
            @refresh="performSearch"
          />
        </div>
      </div>

      <!-- Coaches Only View -->
      <div v-else-if="activeTab === 'coach'" class="results-section">
        <div class="section-header">
          <h3>教练结果 ({{ coachResults.length }})</h3>
        </div>
        <div class="coach-grid">
          <el-card
            v-for="coach in coachResults"
            :key="coach.id"
            class="coach-card"
            shadow="hover"
            @click="viewCoachProfile(coach.id)"
          >
            <div class="coach-content">
              <el-avatar :size="60" :src="coach.avatar">
                {{ coach.username?.charAt(0).toUpperCase() }}
              </el-avatar>
              <div class="coach-info">
                <h4 class="coach-name">{{ coach.username }}</h4>
                <p class="coach-intro">{{ coach.intro || '暂无简介' }}</p>
                <div class="coach-stats">
                  <el-icon><user /></el-icon>
                  <span>{{ coach.studentCount || 0 }} 学员</span>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- No Results State -->
    <div v-else-if="!loading && !hasResults" class="empty-state">
      <el-empty description="未找到相关结果">
        <template #image>
          <el-icon :size="100" color="#909399"><search /></el-icon>
        </template>
        <el-button type="primary" @click="goBack">返回</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Loading, Search, User } from '@element-plus/icons-vue'
import { search } from '@/api/search'
import { showError } from '@/utils/feedback'
import ResourceCard from '@/components/resource/ResourceCard.vue'
import PostCard from '@/components/community/PostCard.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const activeTab = ref('all')
const searchQuery = ref('')
const allResults = ref([])

// Computed properties for results organized by type
const resourceResults = computed(() => {
  return allResults.value
    .filter(result => result.type === 'resource')
    .map(result => ({
      id: result.id,
      title: result.title,
      description: result.description,
      contentType: 'article', // Default, can be enhanced
      fileUrl: result.imageUrl,
      viewCount: 0,
      creatorName: ''
    }))
})

const postResults = computed(() => {
  return allResults.value
    .filter(result => result.type === 'post')
    .map(result => ({
      id: result.id,
      content: result.description,
      imageUrls: result.imageUrl || '',
      user: {
        username: result.title.replace('Post by ', '')
      },
      likeCount: 0,
      commentCount: 0,
      createdAt: new Date().toISOString()
    }))
})

const coachResults = computed(() => {
  return allResults.value
    .filter(result => result.type === 'coach')
    .map(result => ({
      id: result.id,
      username: result.title,
      intro: result.description,
      avatar: result.imageUrl,
      studentCount: 0
    }))
})

const resourceCount = computed(() => resourceResults.value.length)
const postCount = computed(() => postResults.value.length)
const coachCount = computed(() => coachResults.value.length)
const totalCount = computed(() => resourceCount.value + postCount.value + coachCount.value)

const hasResults = computed(() => {
  if (activeTab.value === 'all') {
    return totalCount.value > 0
  } else if (activeTab.value === 'resource') {
    return resourceCount.value > 0
  } else if (activeTab.value === 'post') {
    return postCount.value > 0
  } else if (activeTab.value === 'coach') {
    return coachCount.value > 0
  }
  return false
})

// Perform search
const performSearch = async () => {
  const query = route.query.q
  const type = route.query.type || 'all'

  if (!query) {
    showError('Please enter search content')
    router.back()
    return
  }

  searchQuery.value = query
  activeTab.value = type
  loading.value = true

  try {
    const results = await search(query, type)
    allResults.value = results || []
  } catch (error) {
    console.error('Search failed:', error)
    showError('Search failed, please try again')
    allResults.value = []
  } finally {
    loading.value = false
  }
}

// Handle tab change
const handleTabChange = (tabName) => {
  router.replace({
    path: '/search',
    query: {
      q: searchQuery.value,
      type: tabName
    }
  })
}

// Go back to previous page
const goBack = () => {
  router.back()
}

// View coach profile (placeholder - can be enhanced later)
const viewCoachProfile = (coachId) => {
  // TODO: Navigate to coach profile page when implemented
  router.push(`/coaches/${coachId}`)
}

// Watch for route query changes
watch(
  () => route.query,
  () => {
    if (route.path === '/search') {
      performSearch()
    }
  },
  { deep: true }
)

// Initial search on mount
onMounted(() => {
  performSearch()
})
</script>

<style scoped>
.search-results-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.search-header {
  margin-bottom: 20px;
}

.search-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-query-display {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.search-label {
  font-size: 16px;
  color: #606266;
}

.search-query {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.filter-tabs {
  margin-bottom: 24px;
}

.tab-badge {
  margin-left: 8px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #909399;
}

.loading-container p {
  margin-top: 16px;
  font-size: 14px;
}

.results-content {
  min-height: 400px;
}

.results-section {
  margin-bottom: 32px;
}

.section-header {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e4e7ed;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.coach-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.coach-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.coach-card:hover {
  transform: translateY(-4px);
}

.coach-content {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.coach-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.coach-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.coach-intro {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.coach-stats {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  padding: 40px 20px;
}

/* Responsive design */
@media (max-width: 768px) {
  .search-results-container {
    padding: 12px;
  }

  .search-query-display {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .search-query {
    font-size: 18px;
  }

  .resource-grid,
  .coach-grid {
    grid-template-columns: 1fr;
  }

  .section-header h3 {
    font-size: 16px;
  }
}

@media (max-width: 1024px) {
  .resource-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }

  .coach-grid {
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  }
}
</style>
