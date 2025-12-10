<template>
  <div class="social-feed-container">
    <div class="feed-header">
      <h1>旅行社区</h1>
      <p class="feed-subtitle">发现全球旅友的精彩故事与足迹</p>
    </div>

    <!-- 加载中骨架屏 -->
    <SkeletonLoader v-if="loading" :count="6" type="feed" />

    <!-- 空状态（超温馨） -->
    <el-empty
      v-else-if="records.length === 0"
      description="暂时还没有人分享公开的旅行记忆～"
      style="padding: 100px 0"
    >
      <template #image>
        <el-icon :size="100" color="#e6e9f0"><Picture /></el-icon>
      </template>
      <p style="color:#909399; margin:margin-top: 20px; font-size: 16px">
        成为第一个分享的人吧！
      </p>
      <el-button type="primary" size="large" @click="$router.push('/records/create')">
        去发布我的旅行记忆
      </el-button>
    </el-empty>

    <!-- 社区动态流 -->
    <div v-else class="feed-list">
      <FeedCard
        v-for="record in records"
        :key="record.id"
        :record="record"
        class="feed-item"
      />
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[6, 12, 24]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper →"
        background
        @current-change="handlePageChange"
        @size-change="handlePageSizeChange"
      />
    </div>

    <!-- 错误提示 -->
    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      closable
      @close="errorMessage = ''"
      style="margin-top: 30px"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Picture } from '@element-plus/icons-vue'
import FeedCard from '../components/FeedCard.vue'
import SkeletonLoader from '../components/SkeletonLoader.vue'
import { travelService } from '../services/travelService'
import { showError } from '../utils/notificationUtils'

const router = useRouter()

const records = ref([])
const loading = ref(true)
const errorMessage = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

onMounted(() => {
  fetchPublicRecords()
})

const fetchPublicRecords = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    const response = await travelService.getPublicTravelRecords(
      currentPage.value - 1,
      pageSize.value
    )

    if (response?.data?.content) {
      records.value = response.data.content
      total.value = response.data.totalElements || 0
    } else {
      records.value = []
      total.value = 0
    }
  } catch (err) {
    errorMessage.value = '加载社区动态失败，请刷新重试'
    showError('加载失败啦～')
  } finally {
    loading.value = false
  }
}

const handlePageChange = () => fetchPublicRecords()
const handlePageSizeChange = () => {
  currentPage.value = 1
  fetchPublicRecords()
}
</script>

<style scoped>
.social-feed-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 20px;
}

.feed-header {
  text-align: center;
  margin-bottom: 48px;
}

.feed-header h1 {
  margin: 0 0 12px 0;
  font-size: 34px;
  font-weight: 700;
  color: #303133;
  letter-spacing: 1.2px;
}

.feed-subtitle {
  margin: 0;
  font-size: 17px;
  color: #909399;
  font-weight: 400;
}

.feed-list {
  margin-bottom: 50px;
}

.feed-item {
  margin-bottom: 32px;
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 60px;
}

/* 手机端优化 */
@media (max-width: 768px) {
  .social-feed-container {
    padding: 20px 12px;
  }

  .feed-header h1 {
    font-size: 28px;
  }

  .feed-subtitle {
    font-size: 15px;
  }

  .feed-item {
    margin-bottom: 24px;
  }
}
</style>