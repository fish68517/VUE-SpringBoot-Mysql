<template>
  <div class="home-container">
    <!-- Welcome Banner -->
    <el-card class="welcome-banner" shadow="hover">
      <div class="banner-content">
        <h1>Welcome back, {{ userName }}! 👋</h1>
        <p>Ready to continue your fitness journey?</p>
      </div>
    </el-card>

    <!-- Statistics Overview -->
    <el-row :gutter="20" class="stats-section">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="Check-ins" :value="stats.checkIns">
            <template #prefix>
              <el-icon><Calendar /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="Collections" :value="stats.collections">
            <template #prefix>
              <el-icon><Star /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="My Posts" :value="stats.posts">
            <template #prefix>
              <el-icon><ChatDotRound /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="Current Streak" :value="stats.currentStreak">
            <template #prefix>
              <el-icon><TrendCharts /></el-icon>
            </template>
            <template #suffix>days</template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <!-- Quick Access Cards -->
    <div class="quick-access-section">
      <h2>Quick Access</h2>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="6" v-for="item in quickAccessItems" :key="item.title">
          <el-card shadow="hover" class="quick-access-card" @click="navigateTo(item.route)">
            <div class="quick-access-content">
              <el-icon :size="40" :color="item.color">
                <component :is="item.icon" />
              </el-icon>
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- Featured Resources -->
    <div class="featured-section">
      <div class="section-header">
        <h2>Featured Resources</h2>
        <el-button type="primary" link @click="navigateTo('/resources')">View All</el-button>
      </div>
      <el-row :gutter="20" v-loading="loadingResources">
        <el-col :xs="24" :sm="12" :md="8" v-for="resource in featuredResources" :key="resource.id">
          <el-card shadow="hover" class="resource-card" @click="navigateTo(`/resources/${resource.id}`)">
            <div class="resource-content">
              <div class="resource-icon">
                <el-icon :size="30">
                  <VideoPlay v-if="resource.contentType === 'video'" />
                  <Document v-else-if="resource.contentType === 'document'" />
                  <Reading v-else />
                </el-icon>
              </div>
              <h3>{{ resource.title }}</h3>
              <p class="resource-description">{{ truncateText(resource.description, 80) }}</p>
              <div class="resource-meta">
                <el-tag size="small" :type="getResourceTypeTag(resource.contentType)">
                  {{ resource.contentType }}
                </el-tag>
                <span class="view-count">
                  <el-icon><View /></el-icon>
                  {{ resource.viewCount }}
                </span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!loadingResources && featuredResources.length === 0" description="No resources available" />
    </div>

    <!-- Recent Community Posts -->
    <div class="community-section">
      <div class="section-header">
        <h2>Recent Community Posts</h2>
        <el-button type="primary" link @click="navigateTo('/community')">View All</el-button>
      </div>
      <el-row :gutter="20" v-loading="loadingPosts">
        <el-col :xs="24" :sm="12" :md="12" :lg="8" v-for="post in recentPosts" :key="post.id">
          <el-card shadow="hover" class="post-card" @click="navigateTo(`/community/${post.id}`)">
            <div class="post-header">
              <el-avatar :size="40" :src="post.user?.avatar || '/default-avatar.png'" />
              <div class="post-user-info">
                <span class="username">{{ post.user?.username }}</span>
                <span class="post-time">{{ formatTime(post.createdAt) }}</span>
              </div>
            </div>
            <div class="post-content">
              <p>{{ truncateText(post.content, 120) }}</p>
            </div>
            <div class="post-footer">
              <span class="post-stat">
                <el-icon><ChatDotRound /></el-icon>
                {{ post.commentCount }}
              </span>
              <span class="post-stat">
                <el-icon><Star /></el-icon>
                {{ post.likeCount }}
              </span>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!loadingPosts && recentPosts.length === 0" description="No posts yet" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/modules/auth';
import { getResources } from '@/api/resource';
import { getDynamics } from '@/api/community';
import { getCheckInStats } from '@/api/checkin';
import { getCollections } from '@/api/collection';
import { 
  Calendar, 
  Star, 
  ChatDotRound, 
  TrendCharts, 
  VideoPlay, 
  Document, 
  Reading, 
  View,
  Trophy,
  DataLine,
  Notebook,
  User
} from '@element-plus/icons-vue';

const router = useRouter();
const authStore = useAuthStore();

const featuredResources = ref([]);
const recentPosts = ref([]);
const loadingResources = ref(false);
const loadingPosts = ref(false);

const stats = ref({
  checkIns: 0,
  collections: 0,
  posts: 0,
  currentStreak: 0
});

const userName = computed(() => authStore.currentUser?.username || 'User');

const quickAccessItems = [
  {
    title: 'Resources',
    description: 'Browse fitness content',
    icon: Reading,
    route: '/resources',
    color: '#409EFF'
  },
  {
    title: 'Training Plans',
    description: 'View your plans',
    icon: Notebook,
    route: '/training-plans',
    color: '#67C23A'
  },
  {
    title: 'Community',
    description: 'Connect with others',
    icon: ChatDotRound,
    route: '/community',
    color: '#E6A23C'
  },
  {
    title: 'Check-in',
    description: 'Log your progress',
    icon: Trophy,
    route: '/checkin',
    color: '#F56C6C'
  }
];

// Fetch featured resources (top 6 by view count)
const fetchFeaturedResources = async () => {
  loadingResources.value = true;
  try {
    const data = await getResources({ page: 0, size: 6 });
    // Backend should return resources sorted by viewCount desc
    featuredResources.value = data.content || data || [];
  } catch (error) {
    console.error('Failed to load featured resources:', error);
  } finally {
    loadingResources.value = false;
  }
};

// Fetch recent community posts (latest 5)
const fetchRecentPosts = async () => {
  loadingPosts.value = true;
  try {
    const data = await getDynamics({ page: 0, size: 5 });
    recentPosts.value = data.content || data || [];
  } catch (error) {
    console.error('Failed to load recent posts:', error);
  } finally {
    loadingPosts.value = false;
  }
};

// Fetch user statistics
const fetchStats = async () => {
  try {
    // Fetch check-in stats
    const checkInData = await getCheckInStats();
    stats.value.checkIns = checkInData.totalCount || 0;
    stats.value.currentStreak = checkInData.currentStreak || 0;

    // Fetch collections count
    const collections = await getCollections();
    stats.value.collections = Array.isArray(collections) ? collections.length : 0;

    // Fetch user's posts count
    const dynamics = await getDynamics({ page: 0, size: 1000 });
    const allPosts = dynamics.content || dynamics || [];
    const userPosts = allPosts.filter(post => post.user?.id === authStore.currentUser?.id);
    stats.value.posts = userPosts.length;
  } catch (error) {
    console.error('Failed to load statistics:', error);
  }
};

// Navigate to route
const navigateTo = (route) => {
  router.push(route);
};

// Get resource type tag color
const getResourceTypeTag = (type) => {
  const tagMap = {
    video: 'success',
    article: 'primary',
    document: 'warning'
  };
  return tagMap[type] || 'info';
};

// Truncate text
const truncateText = (text, maxLength) => {
  if (!text) return '';
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
};

// Format time
const formatTime = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const now = new Date();
  const diff = now - date;
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);

  if (minutes < 60) return `${minutes}m ago`;
  if (hours < 24) return `${hours}h ago`;
  if (days < 7) return `${days}d ago`;
  return date.toLocaleDateString();
};

onMounted(() => {
  fetchFeaturedResources();
  fetchRecentPosts();
  fetchStats();
});
</script>

<style scoped>
.home-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.welcome-banner {
  margin-bottom: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.banner-content h1 {
  margin: 0 0 10px 0;
  font-size: 32px;
  font-weight: bold;
}

.banner-content p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.stats-section {
  margin-bottom: 40px;
}

.stat-card {
  text-align: center;
  cursor: default;
}

.quick-access-section {
  margin-bottom: 40px;
}

.quick-access-section h2 {
  margin-bottom: 20px;
  font-size: 24px;
  color: #303133;
}

.quick-access-card {
  cursor: pointer;
  transition: transform 0.3s;
  height: 100%;
}

.quick-access-card:hover {
  transform: translateY(-5px);
}

.quick-access-content {
  text-align: center;
  padding: 20px 10px;
}

.quick-access-content h3 {
  margin: 15px 0 10px 0;
  font-size: 18px;
  color: #303133;
}

.quick-access-content p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.featured-section,
.community-section {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.resource-card {
  cursor: pointer;
  transition: transform 0.3s;
  height: 100%;
  margin-bottom: 20px;
}

.resource-card:hover {
  transform: translateY(-5px);
}

.resource-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.resource-icon {
  color: #409EFF;
}

.resource-content h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.resource-description {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.resource-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #909399;
}

.post-card {
  cursor: pointer;
  transition: transform 0.3s;
  margin-bottom: 20px;
}

.post-card:hover {
  transform: translateY(-5px);
}

.post-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.post-user-info {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: bold;
  font-size: 14px;
  color: #303133;
}

.post-time {
  font-size: 12px;
  color: #909399;
}

.post-content p {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.post-footer {
  display: flex;
  gap: 20px;
  padding-top: 10px;
  border-top: 1px solid #EBEEF5;
}

.post-stat {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #909399;
}

@media (max-width: 768px) {
  .home-container {
    padding: 10px;
  }

  .banner-content h1 {
    font-size: 24px;
  }

  .banner-content p {
    font-size: 14px;
  }

  .section-header h2,
  .quick-access-section h2 {
    font-size: 20px;
  }
}
</style>
